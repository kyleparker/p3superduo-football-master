package barqsoft.footballscores;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;

import barqsoft.footballscores.content.ScoresWidgetService;
import barqsoft.footballscores.utils.LogUtils;

/**
 *
 * Created by kyleparker on 9/28/2015.
 */
public class ScoresWidgetProvider extends AppWidgetProvider {
    private static final String REFRESH_ACTION = "barqsoft.footballscores.REFRESH";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        LogUtils.LOGE("***> provider", "onReceive " + intent.getAction());
        if (intent.getAction().equals(REFRESH_ACTION)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, ScoresWidgetProvider.class));
            for (int appWidgetId : appWidgetIds) {
                RemoteViews remoteViews = buildLayout(context, appWidgetId, true);
                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            }

            final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            final ComponentName cn = new ComponentName(context, ScoresWidgetProvider.class);
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.scores_list);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Update each of the widgets with the remote adapter
        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = buildLayout(context, appWidgetId, false);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
        LogUtils.LOGE("***> provider", "onUpdate");

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private RemoteViews buildLayout(Context context, int appWidgetId, boolean refereshLayout) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_scores);

        // Specify the service to provide data for the collection widget.  Note that we need to
        // embed the appWidgetId via the data otherwise it will be ignored.
        final Intent intent = new Intent(context, ScoresWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        remoteViews.setRemoteAdapter(appWidgetId, R.id.scores_list, intent);

        // Set the empty view to be displayed if the collection is empty.  It must be a sibling
        // view of the collection view.
        remoteViews.setEmptyView(R.id.scores_list, R.id.empty_view);

        // Handle the refresh action
        Intent updateIntent = new Intent(context, ScoresWidgetProvider.class);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        updateIntent.setAction(REFRESH_ACTION);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(context, 0, updateIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.action_refresh, updatePendingIntent);

        // Open the app when tapping on the title
        Intent appIntent = new Intent(context, MainActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.title, appPendingIntent);

        if (refereshLayout) {
            remoteViews.setViewVisibility(R.id.action_refresh, View.GONE);
            remoteViews.setViewVisibility(R.id.loading_container, View.VISIBLE);
        } else {
            remoteViews.setViewVisibility(R.id.action_refresh, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.loading_container, View.GONE);
        }

        return remoteViews;
    }
}
