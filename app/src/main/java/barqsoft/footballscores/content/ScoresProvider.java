package barqsoft.footballscores.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;

import barqsoft.footballscores.utils.LogUtils;

/**
 *
 * Created by yehya khaled on 2/25/2015.
 */
public class ScoresProvider extends ContentProvider {
    private Context mContext;

    private static final int MATCHES = 100;
    private static final int MATCHES_WITH_LEAGUE = 101;
    private static final int MATCHES_WITH_ID = 102;
    private static final int MATCHES_WITH_DATE = 103;

    private static final String SCORES_BY_LEAGUE = DatabaseContract.Scores.LEAGUE_ID + " = ?";
    private static final String SCORES_BY_DATE = DatabaseContract.Scores.DATE + " LIKE ?";
    private static final String SCORES_BY_ID = DatabaseContract.Scores.MATCH_ID + " = ?";

    private static SQLiteDatabase db;
    private final UriMatcher mUriMatcher = buildUriMatcher();

    @Override
    public boolean onCreate() {
        mContext = getContext();
        ScoresDbHelper databaseHelper = new ScoresDbHelper(getContext());

        try {
            db = databaseHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            LogUtils.LOGE("***> db", "Unable to open database: " + e);
        }
        return db != null;
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DatabaseContract.BASE_CONTENT_URI.toString();

        matcher.addURI(authority, null, MATCHES);
        matcher.addURI(authority, DatabaseContract.Scores.LEAGUE_ID, MATCHES_WITH_LEAGUE);
        matcher.addURI(authority, DatabaseContract.Scores.MATCH_ID, MATCHES_WITH_ID);
        matcher.addURI(authority, DatabaseContract.Scores.DATE, MATCHES_WITH_DATE);

        return matcher;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match = mUriMatcher.match(uri);

        switch (match) {
            case MATCHES:
                return DatabaseContract.Scores.CONTENT_TYPE;
            case MATCHES_WITH_LEAGUE:
                return DatabaseContract.Scores.CONTENT_TYPE;
            case MATCHES_WITH_ID:
                return DatabaseContract.Scores.CONTENT_ITEM_TYPE;
            case MATCHES_WITH_DATE:
                return DatabaseContract.Scores.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri :" + uri);
        }
    }

    private int getMatchUri(Uri uri) {
        String link = uri.toString();

        if (link.contentEquals(DatabaseContract.BASE_CONTENT_URI.toString())) {
            return MATCHES;
        } else if (link.contentEquals(DatabaseContract.Scores.buildScoreWithDate().toString())) {
            return MATCHES_WITH_DATE;
        } else if (link.contentEquals(DatabaseContract.Scores.buildScoreWithId().toString())) {
            return MATCHES_WITH_ID;
        } else if (link.contentEquals(DatabaseContract.Scores.buildScoreWithLeague().toString())) {
            return MATCHES_WITH_LEAGUE;
        }

        return -1;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        switch (getMatchUri(uri)) {
            case MATCHES:
                cursor = db.query(DatabaseContract.SCORES_TABLE, projection, null, null, null, null, sortOrder);
                break;
            case MATCHES_WITH_DATE:
                cursor = db.query(DatabaseContract.SCORES_TABLE, projection, SCORES_BY_DATE, selectionArgs, null, null, sortOrder);
                break;
            case MATCHES_WITH_ID:
                cursor = db.query(DatabaseContract.SCORES_TABLE, projection, SCORES_BY_ID, selectionArgs, null, null, sortOrder);
                break;
            case MATCHES_WITH_LEAGUE:
                cursor = db.query(DatabaseContract.SCORES_TABLE, projection, SCORES_BY_LEAGUE, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri" + uri);
        }

        cursor.setNotificationUri(mContext.getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        switch (getMatchUri(uri)) {
            case MATCHES:
                db.beginTransaction();
                int count = 0;
                try {
                    for (ContentValues value : values) {
                        if (value != null) {
                            long id = db.insertWithOnConflict(DatabaseContract.SCORES_TABLE, null, value, SQLiteDatabase.CONFLICT_REPLACE);
                            if (id != -1) {
                                count++;
                            }
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                mContext.getContentResolver().notifyChange(uri, null);

                return count;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }
}
