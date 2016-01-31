package movio.pv256.fi.muni.cz.android_project.model;

import com.squareup.moshi.Json;

/**
 * Created by Michal on 30. 1. 2016.
 */
public class Genre {

    @Json(name = "id")
    public long id;

    @Json(name = "name")
    public String name;

    public boolean isChecked;

    @Override
    public String toString() {
        return name;
    }
}
