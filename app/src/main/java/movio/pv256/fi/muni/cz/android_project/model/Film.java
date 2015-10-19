package movio.pv256.fi.muni.cz.android_project.model;

/**
 * Created by Michal on 18. 10. 2015.
 */
public class Film {

    private long id;
    private long releaseDate;
    private String coverPath;
    private String title;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(long releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
