package barqsoft.footballscores.object;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Score object
 *
 * Created by kyleparker on 9/22/2015.
 */
public class Score implements Parcelable, Comparable<Score> {
    private int awayGoals;
    private String awayImageUrl;
    private String awayLink;
    private String awayName;
    private String date;
    private long id;
    private int homeGoals;
    private String homeImageUrl;
    private String homeLink;
    private String homeName;
    private int leagueId;
    private int matchDay;
    private int matchId;
    private String time;

    public int getAwayGoals() {
        return awayGoals;
    }

    public void setAwayGoals(int awayGoals) {
        this.awayGoals = awayGoals;
    }

    public String getAwayImageUrl() {
        return awayImageUrl;
    }

    public void setAwayImageUrl(String awayImageUrl) {
        this.awayImageUrl = awayImageUrl;
    }

    public String getAwayLink() {
        return awayLink;
    }

    public void setAwayLink(String awayLink) {
        this.awayLink = awayLink;
    }

    public String getAwayName() {
        return awayName;
    }

    public void setAwayName(String awayName) {
        this.awayName = awayName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getHomeGoals() {
        return homeGoals;
    }

    public void setHomeGoals(int homeGoals) {
        this.homeGoals = homeGoals;
    }

    public String getHomeImageUrl() {
        return homeImageUrl;
    }

    public void setHomeImageUrl(String homeImageUrl) {
        this.homeImageUrl = homeImageUrl;
    }

    public String getHomeLink() {
        return homeLink;
    }

    public void setHomeLink(String homeLink) {
        this.homeLink = homeLink;
    }

    public String getHomeName() {
        return homeName;
    }

    public void setHomeName(String homeName) {
        this.homeName = homeName;
    }

    public int getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(int leagueId) {
        this.leagueId = leagueId;
    }

    public int getMatchDay() {
        return matchDay;
    }

    public void setMatchDay(int matchDay) {
        this.matchDay = matchDay;
    }

    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Score() { }

    private Score(Parcel in) {
        awayGoals = in.readInt();
        awayImageUrl = in.readString();
        awayLink = in.readString();
        awayName = in.readString();
        date = in.readString();
        id = in.readLong();
        homeGoals = in.readInt();
        homeImageUrl = in.readString();
        homeLink = in.readString();
        homeName = in.readString();
        leagueId = in.readInt();
        matchDay = in.readInt();
        matchId = in.readInt();
        time = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(awayGoals);
        dest.writeString(awayImageUrl);
        dest.writeString(awayLink);
        dest.writeString(awayName);
        dest.writeString(date);
        dest.writeLong(id);
        dest.writeInt(homeGoals);
        dest.writeString(homeImageUrl);
        dest.writeString(homeLink);
        dest.writeString(homeName);
        dest.writeInt(leagueId);
        dest.writeInt(matchDay);
        dest.writeInt(matchId);
        dest.writeString(time);
    }

    public static final Creator<Score> CREATOR = new Creator<Score>() {
        @Override
        public Score createFromParcel(Parcel in) {
            return new Score(in);
        }

        @Override
        public Score[] newArray(int size) {
            return new Score[size];
        }
    };


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + Integer.toString(awayGoals).hashCode();
        result = prime * result + ((awayImageUrl == null) ? 0 : awayImageUrl.hashCode());
        result = prime * result + ((awayLink == null) ? 0 : awayLink.hashCode());
        result = prime * result + ((awayName == null) ? 0 : awayName.hashCode());
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + Long.toString(id).hashCode();
        result = prime * result + Integer.toString(homeGoals).hashCode();
        result = prime * result + ((homeImageUrl == null) ? 0 : homeImageUrl.hashCode());
        result = prime * result + ((homeLink == null) ? 0 : homeLink.hashCode());
        result = prime * result + ((homeName == null) ? 0 : homeName.hashCode());
        result = prime * result + Integer.toString(leagueId).hashCode();
        result = prime * result + Integer.toString(matchDay).hashCode();
        result = prime * result + Integer.toString(matchId).hashCode();
        result = prime * result + ((time == null) ? 0 : time.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }

        if (obj == null) { return false; }

        if (getClass() != obj.getClass()) { return false; }

        Score other = (Score) obj;

        if (awayGoals != other.awayGoals) { return false; }

        if (awayImageUrl == null) {
            if (other.awayImageUrl != null) { return false; }
        } else if (!awayImageUrl.equals(other.awayImageUrl)) { return false; }

        if (awayLink == null) {
            if (other.awayLink != null) { return false; }
        } else if (!awayLink.equals(other.awayLink)) { return false; }

        if (awayName == null) {
            if (other.awayName != null) { return false; }
        } else if (!awayName.equals(other.awayName)) { return false; }

        if (date == null) {
            if (other.date != null) { return false; }
        } else if (!date.equals(other.date)) { return false; }

        if (id != other.id) { return false; }

        if (homeGoals != other.homeGoals) { return false; }

        if (homeImageUrl == null) {
            if (other.homeImageUrl != null) { return false; }
        } else if (!homeImageUrl.equals(other.homeImageUrl)) { return false; }

        if (homeLink == null) {
            if (other.homeLink != null) { return false; }
        } else if (!homeLink.equals(other.homeLink)) { return false; }

        if (homeName == null) {
            if (other.homeName != null) { return false; }
        } else if (!homeName.equals(other.homeName)) { return false; }

        if (leagueId != other.leagueId) { return false; }

        if (matchDay != other.matchDay) { return false; }

        if (matchId != other.matchId) { return false; }

        if (time == null) {
            if (other.time != null) { return false; }
        } else if (!time.equals(other.time)) { return false; }


        return true;
    }

    public int compareTo(@NonNull Score another) {
        String compareId = Long.toString(id);
        String anotherId = Long.toBinaryString(another.id);

        return anotherId.compareTo(compareId);
    }
}
