package movio.pv256.fi.muni.cz.android_project.model;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by Michal on 30. 1. 2016.
 */
public class GenresWrapper {

    @Json(name = "genres")
    public List<Genre> genres;

}
