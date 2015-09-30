package barqsoft.footballscores.utils;

import android.content.Context;
import android.net.Uri;

import barqsoft.footballscores.R;

// DONE: Update to use string resources instead of hard-coded texts
/**
 *
 * Created by yehya khaled on 3/3/2015.
 */
public class ScoreUtils {
    /**
     * Retrieve the league name based on the ID
     *
     * @param context
     * @param leagueId
     * @param includeHtml
     * @return
     */
    public static String getLeague(Context context, int leagueId, boolean includeHtml) {
        String league = includeHtml ?
                Constants.HTML_FONT_OPEN.replace("[color]", "color=\"" + Constants.HTML_FONT_COLOR_GRAY + "\"") +
                context.getString(R.string.content_league) + ": " + Constants.HTML_FONT_CLOSE : "";

        switch (leagueId) {
            case Constants.League.BUNDESLIGA1:
                return league + context.getString(R.string.content_league_bundesliga1);
            case Constants.League.BUNDESLIGA2:
                return league + context.getString(R.string.content_league_bundesliga2);
            case Constants.League.LIGUE1:
                return league + context.getString(R.string.content_league_ligue1);
            case Constants.League.LIGUE2:
                return league + context.getString(R.string.content_league_ligue2);
            case Constants.League.PREMIER_LEAGUE:
                return league + context.getString(R.string.content_league_premier);
            case Constants.League.PRIMERA_DIVISION:
                return league + context.getString(R.string.content_league_primera);
            case Constants.League.SEGUNDA_DIVISION:
                return league + context.getString(R.string.content_league_segunda_division);
            case Constants.League.SERIE_A:
                return league + context.getString(R.string.content_league_seria_a);
            case Constants.League.PRIMEIRA_LIGA:
                return league + context.getString(R.string.content_league_primeira_liga);
            case Constants.League.BUNDESLIGA3:
                return league + context.getString(R.string.content_league_bundesliga3);
            case Constants.League.EREDIVISIE:
                return league + context.getString(R.string.content_league_eredivisie);
            case Constants.League.CHAMPIONS_LEAGUE:
                return league + context.getString(R.string.content_league_champions);
            default:
                return league + context.getString(R.string.content_league_unknown);
        }
    }

    /**
     * Retrieve the match day based on the int value
     *
     * @param context
     * @param matchDay
     * @param leagueId
     * @return
     */
    public static String getMatchDay(Context context, int matchDay, int leagueId) {
        if (leagueId == Constants.League.CHAMPIONS_LEAGUE) {
            if (matchDay <= 6) {
                return context.getString(R.string.content_match_day_group);
            } else if (matchDay == 7 || matchDay == 8) {
                return context.getString(R.string.content_match_day_first_round);
            } else if (matchDay == 9 || matchDay == 10) {
                return context.getString(R.string.content_match_day_quarterfinal);
            } else if (matchDay == 11 || matchDay == 12) {
                return context.getString(R.string.content_match_day_semifinal);
            } else {
                return context.getString(R.string.content_match_day_final);
            }
        } else {
            return Constants.HTML_FONT_OPEN.replace("[color]", "color=\"" + Constants.HTML_FONT_COLOR_GRAY + "\"") +
                    context.getString(R.string.content_match_day) + ": " + Constants.HTML_FONT_CLOSE + matchDay;
        }
    }

    /**
     * Format the match time
     *
     * @param context
     * @param time
     * @return
     */
    public static String getMatchTime(Context context, String time) {
        return Constants.HTML_FONT_OPEN.replace("[color]", "color=\"" + Constants.HTML_FONT_COLOR_GRAY + "\"") +
                context.getString(R.string.content_match_time) + ": " + Constants.HTML_FONT_CLOSE + time;
    }

    /**
     * Retrieve the team crest based on the id
     *
     * @param context
     * @param teamId
     * @return
     */
    // TODO: This should retrieve image assets from the service, however they are in SVG format and not easily accessible for the app. In addition,
    // connection constraints on hitting the service complicate retrieving the image urls.
    public static Uri getTeamCrestByTeam(Context context, String teamId) {
        try {
            return Uri.parse("android.resource://" + context.getPackageName() +
                    "/drawable/team_" + teamId);
        } catch (Exception ex) {
            return Uri.parse("android.resource://" + context.getPackageName() +
                    "/drawable/no_icon");
        }
    }
}
