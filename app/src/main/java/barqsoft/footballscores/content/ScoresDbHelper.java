package barqsoft.footballscores.content;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *
 * Created by yehya khaled on 2/25/2015.
 */
class ScoresDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "scores.db";
    private static final int DATABASE_VERSION = 3;

    public ScoresDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseContract.Scores.SQL_CREATE_SCORE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Remove old values when upgrading.
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.SCORES_TABLE);

        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + DatabaseContract.SCORES_TABLE + " ADD " + DatabaseContract.Scores.AWAY_IMAGE + " TEXT");
            db.execSQL("ALTER TABLE " + DatabaseContract.SCORES_TABLE + " ADD " + DatabaseContract.Scores.AWAY_LINK + " TEXT");
            db.execSQL("ALTER TABLE " + DatabaseContract.SCORES_TABLE + " ADD " + DatabaseContract.Scores.HOME_IMAGE + " TEXT");
            db.execSQL("ALTER TABLE " + DatabaseContract.SCORES_TABLE + " ADD " + DatabaseContract.Scores.HOME_LINK + " TEXT");
        }
    }
}
