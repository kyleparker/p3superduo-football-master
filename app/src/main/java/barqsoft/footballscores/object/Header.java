package barqsoft.footballscores.object;

/**
 * Simple object for headers in RecyclerViews
 *
 * Created by kyleparker on 7/10/2015.
 */
public class Header {
    private String firstLine;
    private String secondLine;

    public String getFirstLine() {
        return firstLine;
    }
    public void setFirstLine(String value) {
        this.firstLine = value == null ? "" : value.trim();
    }

    public String getSecondLine() {
        return secondLine;
    }
    public void setSecondLine(String value) {
        this.secondLine = value == null ? "" : value.trim();
    }
}
