package app.psiteportal.com.model;

/**
 * Created by fmpdroid on 2/29/2016.
 */
public class Announcement {

    private String pid;
    private String title;
    private String details;
    private String date;
    private String time;
    private String venue;
    private String poster;

    public Announcement(){
    }

    public Announcement(String pid, String title, String details, String date, String time, String venue, String poster) {
        this.pid = pid;
        this.title = title;
        this.details = details;
        this.date = date;
        this.time = time;
        this.venue = venue;
        this.poster = poster;
    }


    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }
}
