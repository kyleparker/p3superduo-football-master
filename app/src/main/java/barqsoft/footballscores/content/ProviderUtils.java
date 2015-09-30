package barqsoft.footballscores.content;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import barqsoft.footballscores.object.Score;

/**
 *
 * Created by kyleparker on 9/22/2015.
 */
public class ProviderUtils {
    private final ContentResolver contentResolver;

    private ProviderUtils(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public int bulkInsertScore(ContentValues[] contentValues) {
        return contentResolver.bulkInsert(DatabaseContract.BASE_CONTENT_URI, contentValues);
    }

//    public Score getScore(String matchId) {
//        if (TextUtils.isEmpty(matchId)) {
//            return null;
//        }
//
//        String[] selectionArgs = new String[] { matchId };
//        Cursor cursor = contentResolver.query(DatabaseContract.Scores.buildScoreWithId(), null, null, selectionArgs, null);
//        if (cursor != null) {
//            try {
//                if (cursor.moveToFirst()) {
//                    return createScore(cursor);
//                }
//            } catch (RuntimeException e) {
//                e.printStackTrace();
//            } finally {
//                cursor.close();
//            }
//        }
//        return null;
//    }

    public List<Score> getScoreList(String date) {
        ArrayList<Score> list = new ArrayList<>();

        String[] selectionArgs = new String[] { date };
        Cursor cursor = contentResolver.query(DatabaseContract.Scores.buildScoreWithDate(), null, null, selectionArgs,
                DatabaseContract.Scores.LEAGUE_ID);

        if (cursor != null) {
            list.ensureCapacity(cursor.getCount());

            if (cursor.moveToFirst()) {
                do {
                    list.add(createScore(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return list;
    }

    /**
     *
     * @param cursor
     * @return
     */
    private Score createScore(Cursor cursor) {
        int idxId = cursor.getColumnIndex(DatabaseContract.Scores._ID);
        int idxMatchId = cursor.getColumnIndex(DatabaseContract.Scores.MATCH_ID);
        int idxAway = cursor.getColumnIndex(DatabaseContract.Scores.AWAY);
        int idxAwayGoals = cursor.getColumnIndex(DatabaseContract.Scores.AWAY_GOALS);
        int idxAwayImage = cursor.getColumnIndex(DatabaseContract.Scores.AWAY_IMAGE);
        int idxAwayLink = cursor.getColumnIndex(DatabaseContract.Scores.AWAY_LINK);
        int idxDate = cursor.getColumnIndex(DatabaseContract.Scores.DATE);
        int idxHome = cursor.getColumnIndex(DatabaseContract.Scores.HOME);
        int idxHomeGoals = cursor.getColumnIndex(DatabaseContract.Scores.HOME_GOALS);
        int idxHomeImage = cursor.getColumnIndex(DatabaseContract.Scores.HOME_IMAGE);
        int idxHomeLink = cursor.getColumnIndex(DatabaseContract.Scores.HOME_LINK);
        int idxLeagueId = cursor.getColumnIndex(DatabaseContract.Scores.LEAGUE_ID);
        int idxMatchDay = cursor.getColumnIndex(DatabaseContract.Scores.MATCH_DAY);
        int idxTime = cursor.getColumnIndex(DatabaseContract.Scores.TIME);

        Score item = new Score();

        if (idxId > -1) {
            item.setId(cursor.getLong(idxId));
        }
        if (idxMatchId > -1) {
            item.setMatchId(cursor.getInt(idxMatchId));
        }
        if (idxAway > -1) {
            item.setAwayName(cursor.getString(idxAway));
        }
        if (idxAwayGoals > -1) {
            item.setAwayGoals(cursor.getInt(idxAwayGoals));
        }
        if (idxAwayImage > -1) {
            item.setAwayImageUrl(cursor.getString(idxAwayImage));
        }
        if (idxAwayLink > -1) {
            item.setAwayLink(cursor.getString(idxAwayLink));
        }
        if (idxDate > -1) {
            item.setDate(cursor.getString(idxDate));
        }
        if (idxHome > -1) {
            item.setHomeName(cursor.getString(idxHome));
        }
        if (idxHomeGoals > -1) {
            item.setHomeGoals(cursor.getInt(idxHomeGoals));
        }
        if (idxHomeImage > -1) {
            item.setHomeImageUrl(cursor.getString(idxHomeImage));
        }
        if (idxHomeLink > -1) {
            item.setHomeLink(cursor.getString(idxHomeLink));
        }
        if (idxLeagueId > -1) {
            item.setLeagueId(cursor.getInt(idxLeagueId));
        }
        if (idxMatchDay > -1) {
            item.setMatchDay(cursor.getInt(idxMatchDay));
        }
        if (idxTime > -1) {
            item.setTime(cursor.getString(idxTime));
        }

        return item;
    }

    public ContentValues createContentValues(Score obj) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseContract.Scores.MATCH_ID, obj.getMatchId());
        contentValues.put(DatabaseContract.Scores.AWAY, obj.getAwayName());
        contentValues.put(DatabaseContract.Scores.AWAY_GOALS, obj.getAwayGoals());
        contentValues.put(DatabaseContract.Scores.AWAY_IMAGE, obj.getAwayImageUrl());
        contentValues.put(DatabaseContract.Scores.AWAY_LINK, obj.getAwayLink());
        contentValues.put(DatabaseContract.Scores.DATE, obj.getDate());
        contentValues.put(DatabaseContract.Scores.HOME, obj.getHomeName());
        contentValues.put(DatabaseContract.Scores.HOME_GOALS, obj.getHomeGoals());
        contentValues.put(DatabaseContract.Scores.HOME_IMAGE, obj.getHomeImageUrl());
        contentValues.put(DatabaseContract.Scores.HOME_LINK, obj.getHomeLink());
        contentValues.put(DatabaseContract.Scores.LEAGUE_ID, obj.getLeagueId());
        contentValues.put(DatabaseContract.Scores.MATCH_DAY, obj.getMatchDay());
        contentValues.put(DatabaseContract.Scores.TIME, obj.getTime());

        return contentValues;
    }

    /**
     * A factory which can produce instances of {@link ProviderUtils}
     */
    public static class Factory {
        private static final Factory instance = new Factory();

        /**
         * Creates and returns an instance of {@link ProviderUtils} which uses the given context to access its data.
         */
        public static ProviderUtils get(Context context) {
            return instance.newForContext(context);
        }

        /**
         * Creates an instance of {@link ProviderUtils}.
         */
        ProviderUtils newForContext(Context context) {
            return new ProviderUtils(context.getContentResolver());
        }
    }
}
