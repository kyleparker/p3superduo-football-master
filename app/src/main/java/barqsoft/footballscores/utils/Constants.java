package barqsoft.footballscores.utils;

/**
 *
 * Created by kyleparker on 9/21/2015.
 */
public class Constants {
    private static final String PACKAGE = "barqsoft.footballscores";

    public static final int TODAY_POSITION = 2;
    public static final int NUM_PAGES = 5;
    public static final long ONE_DAY = 86400000;

    public static final String HTML_FONT_OPEN = "<font [color]>";
    public static final String HTML_FONT_CLOSE = "</font>";
    public static final String HTML_FONT_COLOR_GRAY = "#807f7b";

    public static final String RECEIVER_SCORES_NOTIFICATION = PACKAGE + ".NOTIFICATION";

    public class Extra {
        private static final String EXTRA = PACKAGE + ".EXTRA.";

        public static final String ABOUT_DIALOG = "ABOUT_DIALOG";
        public static final String CURRENT_PAGER_VIEW = EXTRA + "CURRENT_PAGER_VIEW";
        public static final String MATCH_DATE = "MATCH_DATE";
    }

    public class League {
        // This set of league codes is for the 2015/2016 season.
        // NOTE: In fall of 2016, they will need to be updated. Feel free to use the codes
        public static final int BUNDESLIGA1 = 394;
        public static final int BUNDESLIGA2 = 395;
        public static final int LIGUE1 = 396;
        public static final int LIGUE2 = 397;
        public static final int PREMIER_LEAGUE = 398;
        public static final int PRIMERA_DIVISION = 399;
        public static final int SEGUNDA_DIVISION = 400;
        public static final int SERIE_A = 401;
        public static final int PRIMEIRA_LIGA = 402;
        public static final int BUNDESLIGA3 = 403;
        public static final int EREDIVISIE = 404;
        public static final int CHAMPIONS_LEAGUE = 405;
    }
}
