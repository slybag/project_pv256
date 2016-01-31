package movio.pv256.fi.muni.cz.android_project.service;

import android.util.Log;

import movio.pv256.fi.muni.cz.android_project.model.Movie;
import movio.pv256.fi.muni.cz.android_project.utils.HeaderArrayList;

/**
 * Created by Michal on 30. 1. 2016.
 */
public class UpdateListEvent {

    private static final String TAG = ".UpdateListEvent";

    private HeaderArrayList<Movie> data;

    public UpdateListEvent(HeaderArrayList<Movie> data) {
        Log.d(TAG, "UpdateListEvent ");
        this.data = data;
    }

    public HeaderArrayList<Movie> getData() {
        Log.d(TAG, "getData ");
        return data;
    }
}
