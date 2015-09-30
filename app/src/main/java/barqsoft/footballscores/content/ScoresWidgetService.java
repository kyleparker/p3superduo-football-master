package barqsoft.footballscores.content;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 *
 * Created by kyleparker on 9/28/2015.
 */
public class ScoresWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}
