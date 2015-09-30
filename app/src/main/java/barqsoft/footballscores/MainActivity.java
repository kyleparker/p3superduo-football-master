package barqsoft.footballscores;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import barqsoft.footballscores.service.ScoresService;
import barqsoft.footballscores.utils.Constants;
import barqsoft.footballscores.utils.DialogUtils;
import barqsoft.footballscores.utils.LogUtils;
import barqsoft.footballscores.utils.UIUtils;
import barqsoft.footballscores.utils.ZoomOutSlideTransformer;

// DONE: Football Scores can be displayed in a widget.
// DONE: Football Scores app has content descriptions for all buttons, images, and text.
// DONE: Football Scores app supports layout mirroring.
// DONE: Football Scores also support a collection widget.
// DONE: Strings are all included in the strings.xml file and untranslatable strings have a translatable tag marked to false.
// DONE: Extra error cases are found, accounted for, and called out in code comments.
// DONE: Get API key for the scores
// DONE: Review usability - "took me a while to figure out"
// DONE: Accessibility - "trouble navigating and understanding the app without perfect vision"
// DONE: Update to Material Design layout
// DONE: Use tab layout, Cardview and RecyclerView
// DONE: Handle offline notifications
// DONE: Show loading spinner when retrieving data
// DONE: Show empty view
// DONE: Refresh the scores when the app launches
// DONE: Update access based on need - some items are marked public when they are not needed beyond the scope of the class
// DONE: Update Log calls to utilize a LogUtils helper class - only log during DEBUG mode
// DONE: Enable the detail view for the match
// TODO: To truly productionize the app, the database needs a complete overhaul - needs to be relational and not just a dump of scores
// DONE: Review device rotation and the savedInstanceState of the fragments
public class MainActivity extends BaseActivity {
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private Handler mHandler;

    private int mCurrentPagerView = 2;
    private boolean mAboutDialogShown = false;

    private Snackbar mSnackbar;
    private Toolbar mToolbar;
    private MaterialDialog mAboutDialog;
    private MaterialDialog mProgressDialog = null;
    private Locale mLocale;
    private ViewPager mViewPager;

    private BroadcastReceiver mNotificationReceiver;
    private ScoresPagerAdapter mPagerAdapter;

    // Navigation drawer
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    // A Runnable that we should execute when the navigation drawer finishes its closing animation
    private Runnable mDeferredOnDrawerClosedRunnable;
    private static final int NAVDRAWER_LAUNCH_DELAY = 250;
    private int mNormalStatusBarColor;
    private boolean mActionBarShown = true;

    private CoordinatorLayout mCoordinatorLayout;

    private static final TypeEvaluator<?> ARGB_EVALUATOR = new ArgbEvaluator();
    private ObjectAnimator mStatusBarColorAnimator;

//    private PagerFragment my_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocale = getResources().getConfiguration().locale;

        mHandler = new Handler();
        mNormalStatusBarColor = getResources().getColor(R.color.theme_primary_dark);

        if (savedInstanceState != null) {
            LogUtils.LOGI(LOG_TAG, "fragment: " + String.valueOf(savedInstanceState.getInt(Constants.Extra.CURRENT_PAGER_VIEW)));
            mCurrentPagerView = savedInstanceState.getInt(Constants.Extra.CURRENT_PAGER_VIEW);
            mAboutDialogShown = savedInstanceState.getBoolean(Constants.Extra.ABOUT_DIALOG);

            if (mAboutDialogShown) {
                showAboutDialog();
            }
        }

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        setupToolbar();
        registerReceiver();
        setupViewPager();

        if (savedInstanceState == null) {
            loadScores();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                loadScores();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        if (mAboutDialog != null && mAboutDialog.isShowing()) {
            mAboutDialog.dismiss();
        }

        if (mNotificationReceiver != null) {
            getApplicationContext().unregisterReceiver(mNotificationReceiver);
        }

        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(Constants.Extra.CURRENT_PAGER_VIEW, mViewPager != null ? mViewPager.getCurrentItem() : mCurrentPagerView);
        outState.putBoolean(Constants.Extra.ABOUT_DIALOG, mAboutDialogShown);
        super.onSaveInstanceState(outState);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupNavDrawer();

        mDrawerToggle.syncState();

        if (UIUtils.isHoneycombMR1()) {
            View mainContent = findViewById(R.id.main_content);
            if (mainContent != null) {
                mainContent.setAlpha(0);
                mainContent.animate().alpha(1).setDuration(NAVDRAWER_LAUNCH_DELAY);
            }
        }
    }

    /**
     * Handle the show/hide option for the toolbar.
     *
     * @param shown
     */
    private void onActionBarAutoShowOrHide(boolean shown) {
        if (mStatusBarColorAnimator != null) {
            mStatusBarColorAnimator.cancel();
        }

        mStatusBarColorAnimator = ObjectAnimator.ofInt(mDrawerLayout, "statusBarBackgroundColor",
                shown ? Color.BLACK : mNormalStatusBarColor,
                shown ? mNormalStatusBarColor : Color.BLACK).setDuration(NAVDRAWER_LAUNCH_DELAY);

        if (mDrawerLayout != null) {
            mStatusBarColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ViewCompat.postInvalidateOnAnimation(mDrawerLayout);
                }
            });
        }

        mStatusBarColorAnimator.setEvaluator(ARGB_EVALUATOR);
        mStatusBarColorAnimator.start();
    }

    private void onNavDrawerStateChanged(boolean isOpen) {
        if (isOpen) {
            autoShowOrHideActionBar(true);
        }
    }

    private void autoShowOrHideActionBar(boolean show) {
        if (show == mActionBarShown) {
            return;
        }

        mActionBarShown = show;
        onActionBarAutoShowOrHide(show);
    }

    /**
     * Return whether the navdrawer is open.
     *
     * @return
     */
    private boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    private void loadScores() {
        if (mSnackbar != null) {
            mSnackbar.dismiss();
        }

        if (UIUtils.isOnline(mActivity)) {
            // Show loading spinner, will be dismissed by the broadcast receiver
            mProgressDialog = DialogUtils.createSpinnerProgressDialog(mActivity, DialogUtils.DEFAULT_TITLE_ID,
                    R.string.dialog_loading_scores, false);
            mProgressDialog.show();

            mActivity.startService(new Intent(mActivity, ScoresService.class));
        } else {
            mSnackbar = Snackbar.make(mCoordinatorLayout, R.string.content_device_offline, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.action_settings, mSnackbarOnClickListener);
            mSnackbar.show();
        }
    }

    /**
     * Register the receivers for the activity
     */
    private void registerReceiver() {
        // Register the notification receiver to update the badge on the action bar when a new notification comes in
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.RECEIVER_SCORES_NOTIFICATION);
        mNotificationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }

//                setupViewPager();
                mPagerAdapter.notifyDataSetChanged();
            }
        };

        getApplicationContext().registerReceiver(mNotificationReceiver, intentFilter);
    }

    /**
     * Sets up the navigation drawer as appropriate.
     */
    private void setupNavDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout == null) {
            return;
        }

        mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.theme_primary_dark));
        mDrawerToggle = new ActionBarDrawerToggle(mActivity, mDrawerLayout, R.string.navdrawer_open, R.string.navdrawer_close);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                // run deferred action, if we have one
                if (mDeferredOnDrawerClosedRunnable != null) {
                    mDeferredOnDrawerClosedRunnable.run();
                    mDeferredOnDrawerClosedRunnable = null;
                }
                onNavDrawerStateChanged(false);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                onNavDrawerStateChanged(true);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                onNavDrawerStateChanged(isNavDrawerOpen());
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }
        });

        if (mToolbar != null) {
            mToolbar.setNavigationIcon(R.drawable.ic_action_drawer);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }

        NavigationView navigation = (NavigationView) findViewById(R.id.navigation_view);
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem menuItem) {
                mDrawerLayout.closeDrawer(GravityCompat.START);

                // launch the target Activity after a short delay, to allow the close animation to play
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switch (menuItem.getItemId()) {
                            case R.id.menu_settings:
                                startActivity(new Intent(mActivity, SettingsActivity.class));
                                break;
                            case R.id.menu_about:
                                // DONE: About dialog
                                showAboutDialog();
                                break;
                        }
                    }
                }, NAVDRAWER_LAUNCH_DELAY);

                return false;
            }
        });
    }

    private void setupToolbar() {
        mToolbar = getActionBarToolbar();
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.post(new Runnable() {
                @Override
                public void run() {
                    mToolbar.setTitle(mActivity.getString(R.string.app_name));
                }
            });
        }
    }

    // NOTE: Sliding between the tabs is not as fluid as I would prefer, but having difficulty in getting the TabLayout and ViewPager to play nicely
    // with RTL support.
    private void setupViewPager() {
        FragmentManager fm = getSupportFragmentManager();

        mViewPager = (ViewPager) findViewById(R.id.pager);

        if (mViewPager != null) {
            mPagerAdapter = new ScoresPagerAdapter(fm);
            mViewPager.setAdapter(mPagerAdapter);
            mViewPager.setCurrentItem(mCurrentPagerView, true);
            mViewPager.setPageTransformer(true, new ZoomOutSlideTransformer());
            mViewPager.setOffscreenPageLimit(2);

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);
            tabLayout.setSmoothScrollingEnabled(true);
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    mCurrentPagerView = tab.getPosition();
                    mViewPager.setCurrentItem(tab.getPosition(), true);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    mCurrentPagerView = tab.getPosition();
                }
            });
            TabLayout.Tab tab = tabLayout.getTabAt(mCurrentPagerView);
            if (tab != null) {
                tab.select();
            }
        }
    }

    private void showAboutDialog() {
        mAboutDialogShown = true;

        mAboutDialog = new MaterialDialog.Builder(mActivity)
                .title(R.string.dialog_about)
                .content(R.string.dialog_about_text)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        mAboutDialogShown = false;
                    }
                })
                .positiveText(R.string.dialog_close)
                .build();

        mAboutDialog.show();
    }

    /**
     * Handle the click listener for the snackbar "undo" action
     */
    private final View.OnClickListener mSnackbarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivity.startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
        }
    };

    /**
     * PagerAdapter for the football scores
     * <p/>
     * Show the scores for 5 days:
     * (LTR) D-2, D-1, D (Today), D+1, D+2
     * (RTL) D+2, D+1, D (Today), D-1, D-2
     *
     * NOTE: The TabLayout and ViewPager do not swipe as smoothly as desired, this is due to a bug with the ViewPager
     */
    private class ScoresPagerAdapter extends FragmentPagerAdapter {
        private final ScoresFragment[] mViewFragments = new ScoresFragment[Constants.NUM_PAGES];

        public ScoresPagerAdapter(FragmentManager fm) {
            super(fm);
            for (int i = 0; i < Constants.NUM_PAGES; i++) {
                Date date = new Date(System.currentTimeMillis() + ((i - Constants.TODAY_POSITION) * Constants.ONE_DAY));
                // DONE: Get user's current local to format the date/time properly
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", mLocale);
                LogUtils.LOGE("***> date", dateFormat.format(date) + "");
                mViewFragments[i] = new ScoresFragment();
                mViewFragments[i].setFragmentDate(dateFormat.format(date));
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getDayName(System.currentTimeMillis() + ((position - Constants.TODAY_POSITION) * Constants.ONE_DAY));
        }

        @Override
        public Fragment getItem(int position) {
            return mViewFragments[position];
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return Constants.NUM_PAGES;
        }

        private String getDayName(long dateInMillis) {
            // If the date is today, return the localized version of "Today" instead of the actual day name.
            Time t = new Time();
            t.setToNow();
            int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
            int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
            if (julianDay == currentJulianDay) {
                return mActivity.getString(R.string.content_today);
            } else if (julianDay == currentJulianDay + 1) {
                return mActivity.getString(R.string.content_tomorrow);
            } else if (julianDay == currentJulianDay - 1) {
                return mActivity.getString(R.string.content_yesterday);
            } else {
                Time time = new Time();
                time.setToNow();
                // Otherwise, the format is just the day of the week (e.g "Wednesday")
                SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", mLocale);
                return dayFormat.format(dateInMillis);
            }
        }
    }
}
