package barqsoft.footballscores.service;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import barqsoft.footballscores.R;
import barqsoft.footballscores.ScoresWidgetProvider;
import barqsoft.footballscores.content.ProviderUtils;
import barqsoft.footballscores.object.Score;
import barqsoft.footballscores.utils.Constants;
import barqsoft.footballscores.utils.LogUtils;

// DONE: Refactor code to follow Java naming conventions
/**
 *
 * Created by yehya khaled on 3/2/2015.
 */
public class ScoresService extends IntentService {
    private static final String LOG_TAG = ScoresService.class.getSimpleName();

    private Context mContext;
    private ProviderUtils mProvider;

    public ScoresService() {
        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mContext = this;
        mProvider = ProviderUtils.Factory.get(mContext);

        // Retrieve next two days worth of scores
        getData("n2");
        // HACK: Due to time zone differences, retrieve the past 3 days
        // Retrieve the previous three days worth of scores
        getData("p3");

        handleBroadcast();
    }

    private void getData(String timeFrame) {
        // Base URL
        final String BASE_URL = "http://api.football-data.org/alpha/fixtures";
        // timeFrame parameter to determine days
        final String QUERY_TIME_FRAME = "timeFrame";

        Uri uriFetch = Uri.parse(BASE_URL)
                    .buildUpon()
                    .appendQueryParameter(QUERY_TIME_FRAME, timeFrame)
                    .build();

        LogUtils.LOGI(LOG_TAG, "The url we are looking at is: " + uriFetch.toString());

        HttpURLConnection httpURLConnection = null;
        BufferedReader reader = null;
        String jsonData = null;

        // Open connection
        try {
            URL fetch = new URL(uriFetch.toString());
            httpURLConnection = (HttpURLConnection) fetch.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.addRequestProperty("X-Auth-Token", getString(R.string.api_key));
            httpURLConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = httpURLConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line);
                buffer.append("\n");
            }
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            jsonData = buffer.toString();
        } catch (Exception e) {
            LogUtils.LOGE(LOG_TAG, "Exception here" + e.getMessage());
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LogUtils.LOGE(LOG_TAG, "Error Closing Stream");
                }
            }
        }

        try {
            if (jsonData != null) {
                //This bit is to check if the data contains any matches. If not, we call processJson on the dummy data
                JSONArray matches = new JSONObject(jsonData).getJSONArray("fixtures");
                if (matches.length() == 0) {
                    //if there is no data, call the function on dummy data
                    //this is expected behavior during the off season.
                    processJSONdata(getString(R.string.dummy_data), false);
                    return;
                }


                processJSONdata(jsonData, true);
            } else {
                // Could not Connect
                LogUtils.LOGE(LOG_TAG, "Could not connect to server.");
            }
        } catch (Exception e) {
            LogUtils.LOGE(LOG_TAG, e.getMessage());
        }
    }

//    private String getTeamCrestUrl(String teamUrl) {
//        final String CREST_URL = "crestUrl";
//
//        HttpURLConnection httpURLConnection = null;
//        BufferedReader reader = null;
//        String jsonData = null;
//
//        try {
//            URL fetch = new URL(teamUrl);
//            httpURLConnection = (HttpURLConnection) fetch.openConnection();
//            httpURLConnection.setRequestMethod("GET");
//            httpURLConnection.addRequestProperty("X-Auth-Token", getString(R.string.api_key));
//            httpURLConnection.connect();
//
//            // Read the input stream into a String
//            InputStream inputStream = httpURLConnection.getInputStream();
//            StringBuilder buffer = new StringBuilder();
//            if (inputStream == null) {
//                return "";
//            }
//            reader = new BufferedReader(new InputStreamReader(inputStream));
//
//            String line;
//            while ((line = reader.readLine()) != null) {
//                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
//                // But it does make debugging a *lot* easier if you print out the completed
//                // buffer for debugging.
//                buffer.append(line);
//                buffer.append("\n");
//            }
//            if (buffer.length() == 0) {
//                return "";
//            }
//            jsonData = buffer.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//            LogUtils.LOGE(LOG_TAG, e.getMessage());
//        } finally {
//            if (httpURLConnection != null) {
//                httpURLConnection.disconnect();
//            }
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e) {
//                    LogUtils.LOGE(LOG_TAG, "Error Closing Stream");
//                }
//            }
//        }
//
//        try {
//            if (jsonData != null) {
//                JSONObject team = new JSONObject(jsonData);
//                return team.getString(CREST_URL);
//            } else {
//                // Could not Connect
//                LogUtils.LOGE(LOG_TAG, "Could not connect to server.");
//            }
//        } catch (Exception e) {
//            LogUtils.LOGE(LOG_TAG, e.getMessage());
//        }
//
//        return "";
//    }

    /**
     * Send the broadcast intent to notify the calling activity that the service has completed
     */
    private void handleBroadcast() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(Constants.RECEIVER_SCORES_NOTIFICATION);
        mContext.sendBroadcast(broadcastIntent);

        // Update the widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(mContext, ScoresWidgetProvider.class));
        if (appWidgetIds.length > 0) {
            new ScoresWidgetProvider().onUpdate(mContext, appWidgetManager, appWidgetIds);
        }
    }

    /**
     * Process the JSON data from the service call
     *
     * @param jsonData
     * @param isReal
     */
    private void processJSONdata(String jsonData, boolean isReal) {
//        final int MAX_CREST_COUNT = 5;
        Locale locale = mContext.getResources().getConfiguration().locale;

        final String MATCH_LINK = "http://api.football-data.org/alpha/fixtures/";
        final String SEASON_LINK = "http://api.football-data.org/alpha/soccerseasons/";
        final String AWAY_GOALS = "goalsAwayTeam";
        final String AWAY_TEAM_NAME = "awayTeamName";
        final String AWAY_TEAM_LINK = "awayTeam";
        final String FIXTURES = "fixtures";
        final String HOME_GOALS = "goalsHomeTeam";
        final String HOME_TEAM_LINK = "homeTeam";
        final String HOME_TEAM_NAME = "homeTeamName";
        final String LINKS = "_links";
        final String MATCH_DATE = "date";
        final String MATCH_DAY = "matchday";
        final String RESULT = "result";
        final String SELF = "self";
        final String SOCCER_SEASON = "soccerseason";

        // Match data
        String awayGoals;
        String awayTeamLink;
        String awayTeamName;
        String date;
        String homeGoals;
        String homeTeamLink;
        String homeTeamName;
        String league;
        String matchDay;
        String matchId;
        String time;

        try {
            int j = 0;
//            int crestCount = 0;
            JSONArray matches = new JSONObject(jsonData).getJSONArray(FIXTURES);

            ContentValues[] contentValues = new ContentValues[matches.length()];

            for (int i = 0; i < matches.length(); i++) {
                JSONObject matchData = matches.getJSONObject(i);

                league = matchData
                        .getJSONObject(LINKS)
                        .getJSONObject(SOCCER_SEASON)
                        .getString("href");

                league = league.replace(SEASON_LINK, "");

                // TODO: Consider adding a setting to allow the user to select the leagues they want to retrieve
                // This if statement controls which leagues we're interested in the data from.
                // add leagues here in order to have them be added to the DB.
                // If you are finding no data in the app, check that this contains all the leagues.
                // If it doesn't, that can cause an empty DB, bypassing the dummy data routine.
                if (league.equals(Integer.toString(Constants.League.PREMIER_LEAGUE)) ||
                        league.equals(Integer.toString(Constants.League.SERIE_A)) ||
                        league.equals(Integer.toString(Constants.League.BUNDESLIGA1)) ||
                        league.equals(Integer.toString(Constants.League.BUNDESLIGA2)) ||
                        league.equals(Integer.toString(Constants.League.PRIMERA_DIVISION)) ||
                        league.equals(Integer.toString(Constants.League.LIGUE1)) ||
                        league.equals(Integer.toString(Constants.League.LIGUE2)) ||
                        league.equals(Integer.toString(Constants.League.SEGUNDA_DIVISION)) ||
                        league.equals(Integer.toString(Constants.League.PRIMEIRA_LIGA)) ||
                        league.equals(Integer.toString(Constants.League.BUNDESLIGA3)) ||
                        league.equals(Integer.toString(Constants.League.EREDIVISIE))) {

                    matchId = matchData.getJSONObject(LINKS).getJSONObject(SELF).getString("href");
                    matchId = matchId.replace(MATCH_LINK, "");

                    // Get the team link to use to retrieve the crest
                    homeTeamLink = matchData
                            .getJSONObject(LINKS)
                            .getJSONObject(HOME_TEAM_LINK)
                            .getString("href");

                    awayTeamLink = matchData
                            .getJSONObject(LINKS)
                            .getJSONObject(AWAY_TEAM_LINK)
                            .getString("href");

                    // Determine if the score entry already exists - need to retrieve the existing crest image info
                    // TODO: Determine how to retrieve the crest from the team info without hitting the max on the service calls
                    // TODO: Some of the image URLs are SVG - without additional processing, there is no out-of-the-box approach to handle loading
                    // SVG graphics into an ImageView resource
                    // HACK: Only retrieve the first 5 crests during a load, otherwise we'll reach the limit of the service calls
//                    Score score = mProvider.getScore(matchId);
//
//                    String homeCrestUrl = "";
//                    String awayCrestUrl = "";
//
//                    if (score == null) {
//                        if (crestCount <= 3) {
//                            homeCrestUrl = getTeamCrestUrl(homeTeamLink);
//                            awayCrestUrl = getTeamCrestUrl(awayTeamLink);
//                            crestCount++;
//                        }
//                    } else {
//                        if (TextUtils.isEmpty(score.getHomeImageUrl()) && crestCount <= MAX_CREST_COUNT) {
//                            homeCrestUrl = getTeamCrestUrl(homeTeamLink);
//                            crestCount++;
//                        } else {
//                            homeCrestUrl = score.getHomeImageUrl();
//                        }
//
//                        if (TextUtils.isEmpty(score.getAwayImageUrl()) && crestCount <= MAX_CREST_COUNT) {
//                            awayCrestUrl = getTeamCrestUrl(awayTeamLink);
//                            crestCount++;
//                        } else {
//                            awayCrestUrl = score.getAwayImageUrl();
//                        }
//                    }

                    if (!isReal) {
                        // This if statement changes the match ID of the dummy data so that it all goes into the database
                        matchId = matchId + Integer.toString(i);
                    }

                    date = matchData.getString(MATCH_DATE);
                    time = date.substring(date.indexOf("T") + 1, date.indexOf("Z"));
                    date = date.substring(0, date.indexOf("T"));

                    SimpleDateFormat matchDate = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss", locale);
                    matchDate.setTimeZone(TimeZone.getTimeZone("UTC"));
                    try {
                        Date parsedDate = matchDate.parse(date + time);

                        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd:HH:mm", locale);
                        dateTimeFormat.setTimeZone(TimeZone.getDefault());
                        date = dateTimeFormat.format(parsedDate);

                        time = date.substring(date.indexOf(":") + 1);
                        date = date.substring(0, date.indexOf(":"));

                        if (!isReal) {
                            // This if statement changes the dummy data's date to match our current date range.
                            Date dummyDate = new Date(System.currentTimeMillis() + ((i - 2) * Constants.ONE_DAY));
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", locale);
                            date = dateFormat.format(dummyDate);
                        }
                    } catch (Exception e) {
                        LogUtils.LOGE(LOG_TAG, e.getMessage());
                    }

                    homeTeamName = matchData.getString(HOME_TEAM_NAME);
                    awayTeamName = matchData.getString(AWAY_TEAM_NAME);
                    homeGoals = matchData.getJSONObject(RESULT).getString(HOME_GOALS);
                    awayGoals = matchData.getJSONObject(RESULT).getString(AWAY_GOALS);
                    matchDay = matchData.getString(MATCH_DAY);

                    Score s = new Score();
                    s.setAwayGoals(Integer.parseInt(awayGoals));
//                    s.setAwayImageUrl(awayCrestUrl);
                    s.setAwayLink(awayTeamLink);
                    s.setAwayName(awayTeamName);
                    s.setDate(date);
                    s.setHomeGoals(Integer.parseInt(homeGoals));
//                    s.setHomeImageUrl(homeCrestUrl);
                    s.setHomeLink(homeTeamLink);
                    s.setHomeName(homeTeamName);
                    s.setLeagueId(Integer.parseInt(league));
                    s.setMatchDay(Integer.parseInt(matchDay));
                    s.setMatchId(Integer.parseInt(matchId));
                    s.setTime(time);

                    contentValues[j] = mProvider.createContentValues(s);
                    j++;
                }
            }

            mProvider.bulkInsertScore(contentValues);
        } catch (JSONException e) {
            LogUtils.LOGE(LOG_TAG, e.getMessage());
        }
    }
}

