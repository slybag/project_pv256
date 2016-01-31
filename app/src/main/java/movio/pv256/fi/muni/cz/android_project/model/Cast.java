package movio.pv256.fi.muni.cz.android_project.model;

import com.squareup.moshi.Json;

/**
 * Created by Michal on 30. 1. 2016.
 */
public class Cast {

    @Json(name="cast_id")
    public long cast_id;

    @Json(name="character")
    public String character;

    @Json(name="id")
    public long id;

    @Json(name="name")
    public String name;

    @Json(name="order")
    public long order;

    @Json(name="profile_path")
    public String image;

}
