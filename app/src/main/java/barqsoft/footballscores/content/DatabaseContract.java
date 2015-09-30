package barqsoft.footballscores.content;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

// DONE: Refactor to following Java naming conventions
/**
 *
 * Created by yehya khaled on 2/25/2015.
 */
class DatabaseContract {
    private static final String CONTENT_AUTHORITY = "barqsoft.footballscores";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String SCORES_TABLE = "scores";
    private static final String PATH = "scores";

    public static final class Scores implements BaseColumns {
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;

        public static final String AWAY = "away";
        public static final String AWAY_GOALS = "away_goals";
        public static final String AWAY_IMAGE = "away_image";
        public static final String AWAY_LINK = "away_link";
        public static final String DATE = "date";
        public static final String HOME = "home";
        public static final String HOME_GOALS = "home_goals";
        public static final String HOME_IMAGE = "home_image";
        public static final String HOME_LINK = "home_link";
        public static final String LEAGUE_ID = "league";
        public static final String MATCH_ID = "match_id";
        public static final String MATCH_DAY = "match_day";
        public static final String TIME = "time";

        public static final String SQL_CREATE_SCORE_TABLE = "CREATE TABLE " + SCORES_TABLE + " ("
                + _ID + " INTEGER PRIMARY KEY,"
                + DATE + " TEXT NOT NULL,"
                // FIX: TIME is not formatted as an int - 06:00 is text
                + TIME + " TEXT NOT NULL,"
                + HOME + " TEXT NOT NULL,"
                + HOME_IMAGE + " TEXT,"
                + HOME_LINK + " TEXT,"
                + AWAY + " TEXT NOT NULL,"
                + AWAY_IMAGE + " TEXT,"
                + AWAY_LINK + " TEXT,"
                + LEAGUE_ID + " INTEGER NOT NULL,"
                + HOME_GOALS + " INTEGER NOT NULL,"
                + AWAY_GOALS + " INTEGER NOT NULL,"
                + MATCH_ID + " INTEGER NOT NULL,"
                + MATCH_DAY + " INTEGER NOT NULL,"
                + " UNIQUE (" + MATCH_ID + ") ON CONFLICT REPLACE"
                + " );";

        public static Uri buildScoreWithLeague() {
            return BASE_CONTENT_URI.buildUpon().appendPath(LEAGUE_ID).build();
        }

        public static Uri buildScoreWithId() {
            return BASE_CONTENT_URI.buildUpon().appendPath(MATCH_ID).build();
        }

        public static Uri buildScoreWithDate() {
            return BASE_CONTENT_URI.buildUpon().appendPath(DATE).build();
        }
    }
}
