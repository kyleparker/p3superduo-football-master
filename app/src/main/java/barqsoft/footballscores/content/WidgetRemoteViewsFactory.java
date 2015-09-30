package barqsoft.footballscores.content;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import barqsoft.footballscores.R;
import barqsoft.footballscores.ScoresWidgetProvider;
import barqsoft.footballscores.object.Score;
import barqsoft.footballscores.utils.LogUtils;
import barqsoft.footballscores.utils.ScoreUtils;

/**
 *
 * Created by kyleparker on 9/28/2015.
 */
class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private final Context mContext;
    private final ProviderUtils mProvider;

    private List<Score> mScoreList;

    public WidgetRemoteViewsFactory(Context context, Intent intent) {
        LogUtils.LOGE("***> factory", "WidgetRemoteViewsFactory");
        mContext = context;
        mProvider = ProviderUtils.Factory.get(mContext);

//        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    public void onCreate() {
        LogUtils.LOGE("***> factory", "onCreate");
        // Since we reload the list in onDataSetChanged() which gets called immediately after onCreate(), we do nothing here.
    }

    @Override
    public void onDataSetChanged() {
        LogUtils.LOGE("***> factory", "onDataSetChanged");
        Locale locale = mContext.getResources().getConfiguration().locale;

        // Retrieve today's scores from the database
        Date date = new Date(System.currentTimeMillis());
        // DONE: Get user's current local to format the date/time properly
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", locale);

        mScoreList = mProvider.getScoreList(dateFormat.format(date));

        // Update the widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(mContext, ScoresWidgetProvider.class));
        if (appWidgetIds.length > 0) {
            new ScoresWidgetProvider().onUpdate(mContext, appWidgetManager, appWidgetIds);
        }
    }

    @Override
    public void onDestroy() {
        LogUtils.LOGE("***> factory", "onDestroy");
    }

    public RemoteViews getViewAt(int position) {
        LogUtils.LOGE("***> factory", "getViewAt");
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item_score);

        rv.setTextViewText(R.id.home_name, mScoreList.get(position).getHomeName());
        rv.setTextColor(R.id.home_name, mContext.getResources().getColor(R.color.text_normal));

        rv.setTextViewText(R.id.away_name, mScoreList.get(position).getAwayName());
        rv.setTextColor(R.id.away_name, mContext.getResources().getColor(R.color.text_normal));

        if (mScoreList.get(position).getHomeGoals() > -1 && mScoreList.get(position).getAwayGoals() > -1) {
            rv.setTextViewText(R.id.home_score, String.valueOf(mScoreList.get(position).getHomeGoals()));
            rv.setTextColor(R.id.away_score, mContext.getResources().getColor(R.color.text_normal));

            rv.setTextViewText(R.id.away_score, String.valueOf(mScoreList.get(position).getAwayGoals()));
            rv.setTextColor(R.id.away_score, mContext.getResources().getColor(R.color.text_normal));

            if (mScoreList.get(position).getHomeGoals() > mScoreList.get(position).getAwayGoals()) {
                rv.setTextColor(R.id.home_name, mContext.getResources().getColor(R.color.theme_accent));
                rv.setTextColor(R.id.home_score, mContext.getResources().getColor(R.color.theme_accent));
            } else {
                rv.setTextColor(R.id.away_name, mContext.getResources().getColor(R.color.theme_accent));
                rv.setTextColor(R.id.away_score, mContext.getResources().getColor(R.color.theme_accent));
            }
        } else {
            rv.setTextViewText(R.id.home_score, mContext.getString(R.string.content_empty_score));
            rv.setTextViewText(R.id.away_score, mContext.getString(R.string.content_empty_score));
        }

        String homeId = mScoreList.get(position).getHomeLink().replace("http://api.football-data.org/alpha/teams/", "");
        String awayId = mScoreList.get(position).getAwayLink().replace("http://api.football-data.org/alpha/teams/", "");

        rv.setImageViewUri(R.id.home_crest, ScoreUtils.getTeamCrestByTeam(mContext, homeId));
        rv.setImageViewUri(R.id.away_crest, ScoreUtils.getTeamCrestByTeam(mContext, awayId));

        return rv;
    }

    public int getCount() {
        return mScoreList.size();
    }

    public RemoteViews getLoadingView() {
        // We aren't going to return a default loading view in this sample
        return null;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }
}
