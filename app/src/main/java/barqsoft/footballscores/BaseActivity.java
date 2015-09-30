package barqsoft.footballscores;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.WindowManager;

import barqsoft.footballscores.content.ProviderUtils;

/**
 * Base activity for Football Scores
 *
 * Created by kyleparker on 9/15/2015.
 */
public class BaseActivity extends AppCompatActivity {
    static AppCompatActivity mActivity;
    static ProviderUtils mProvider;
    private Toolbar mActionBarToolbar;

    boolean mShouldBeFloatingWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = this;
        mProvider = ProviderUtils.Factory.get(mActivity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // HACK: Resolve issue with MaterialDialog problem
        // https://github.com/afollestad/material-dialogs/issues/279
        mActivity = this;
    }

    /**
     * Retrieve the base toolbar for the activity.
     *
     * @return
     */
    Toolbar getActionBarToolbar() {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
            if (mActionBarToolbar != null) {
                setSupportActionBar(mActionBarToolbar);
            }
        }

        return mActionBarToolbar;
    }

    /**
     * Floating window is enabled per the styles.xml in the sw600dp folder (floating window for tablets)
     */
    void setupFloatingWindow(int width, int height) {
        // configure this Activity as a floating window, dimming the background
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = getResources().getDimensionPixelSize(width);
        params.height = getResources().getDimensionPixelSize(height);
        params.alpha = 1;
        params.dimAmount = 0.80f;
        params.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        getWindow().setAttributes(params);
    }

    /**
     * Determine based on the style, whether the window should be floating or full screen
     * @return
     */
    boolean shouldBeFloatingWindow() {
        Resources.Theme theme = getTheme();
        TypedValue floatingWindowFlag = new TypedValue();
        return !((theme == null) || !theme.resolveAttribute(R.attr.isFloatingWindow, floatingWindowFlag, true)) && (floatingWindowFlag.data != 0);
    }
}
