package barqsoft.footballscores;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;

import com.afollestad.materialdialogs.MaterialDialog;

// TODO: Add a setting to select favorite team - scores will appear in notification area
/**
 * SettingsActivity
 *
 * Created by kyleparker on 9/22/2015.
 */
public class SettingsActivity extends BaseActivity {
    private final static String SETTINGS_NAME = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mShouldBeFloatingWindow = shouldBeFloatingWindow();
        if (mShouldBeFloatingWindow) {
            setupFloatingWindow(R.dimen.floating_window_width, R.dimen.floating_window_height);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setupToolbar();

        // HACK: cannot get FitsSystemWindow to work on LinearLayout
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.theme_primary_dark));
        }

        getFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsPreferenceFragment()).commit();
    }

    /**
     * Setup the toolbar for the activity
     */
    private void setupToolbar() {
        final Toolbar toolbar = getActionBarToolbar();
        toolbar.setBackgroundColor(mActivity.getResources().getColor(R.color.theme_primary));
        toolbar.setNavigationIcon(mShouldBeFloatingWindow ? R.drawable.ic_action_close : R.drawable.ic_action_up);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.finish();
            }
        });
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                toolbar.setTitle(mActivity.getString(R.string.title_settings));
            }
        });
    }

    public static class SettingsPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            PreferenceManager preferenceManager = getPreferenceManager();
            preferenceManager.setSharedPreferencesName(SETTINGS_NAME);
            preferenceManager.setSharedPreferencesMode(Context.MODE_PRIVATE);

            addPreferencesFromResource(R.xml.preferences);

            Preference selectLeague = findPreference(getString(R.string.settings_league_key));
            selectLeague.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showSelectLeagueDialog();
                    return true;
                }
            });
        }

        private void showSelectLeagueDialog() {
            // TODO: Retrieve the list of available leagues using http://api.football-data.org/alpha/soccerseasons
            // Save selected leagues as a setting and check when retrieving the data from the score service
            MaterialDialog dialog = new MaterialDialog.Builder(mActivity)
                    .title(mActivity.getString(R.string.dialog_select_leagues))
                    .content(mActivity.getString(R.string.dialog_select_leagues_message))
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                        }
                    })
                    .positiveText(R.string.dialog_ok)
                    .negativeText(R.string.dialog_cancel)
                    .build();

            dialog.show();
        }
    }
}
