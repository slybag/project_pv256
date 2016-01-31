package movio.pv256.fi.muni.cz.android_project.service;

import android.util.Log;

import java.util.ArrayList;

import movio.pv256.fi.muni.cz.android_project.model.Cast;

/**
 * Created by Michal on 30. 1. 2016.
 */
public class UpdateCastEvent {

    private static final String TAG = ".UpdateCastEvent";

    private ArrayList<Cast> data;

    public UpdateCastEvent(ArrayList<Cast> data) {
        Log.d(TAG, "UpdateCastEvent ");
        this.data = data;
    }

    public ArrayList<Cast> getData() {
        Log.d(TAG, "getData ");
        return data;
    }
}
