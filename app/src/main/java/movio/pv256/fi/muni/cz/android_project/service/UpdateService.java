package movio.pv256.fi.muni.cz.android_project.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import movio.pv256.fi.muni.cz.android_project.connection.DataProvider;
import movio.pv256.fi.muni.cz.android_project.connection.NetworkStateChangedReceiver;
import movio.pv256.fi.muni.cz.android_project.model.Cast;
import movio.pv256.fi.muni.cz.android_project.model.Movie;
import movio.pv256.fi.muni.cz.android_project.utils.HeaderArrayList;

/**
 * Created by Michal on 30. 1. 2016.
 */
public class UpdateService extends IntentService {

    private static final String TAG = ".UpdateService";

    private final IBinder mBinder = new UpdateServiceBinder();

    private final EventBus mBus = EventBus.getDefault();

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind ");
        return mBinder;
    }

    public class UpdateServiceBinder extends Binder {
        public UpdateService getService() {
            Log.d(TAG, "getService ");
            return UpdateService.this;
        }
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        Log.d(TAG, "onHandleIntent ");
        switch (workIntent.getStringExtra("action")){
            case "list":
                String dataString = workIntent.getStringExtra("genres");
                DataProvider.get().loadData(dataString, new DataProvider.DataLoaded() {
                    @Override
                    public void onDataLoaded(HeaderArrayList<Movie> data) {
                        mBus.removeAllStickyEvents();
                        if (NetworkStateChangedReceiver.isNetworkAvailable(UpdateService.this)) {
                            mBus.postSticky(new UpdateListEvent(data));
                        } else {
                            mBus.postSticky(new UpdateListEvent(null));
                        }
                    }
                });
                break;
            case "detail":
                Long id = workIntent.getLongExtra("id", 0);
                DataProvider.get().loadCast(id, new DataProvider.CastLoaded() {
                    @Override
                    public void onCastLoaded(ArrayList<Cast> data) {
                        mBus.removeAllStickyEvents();
                        if (NetworkStateChangedReceiver.isNetworkAvailable(UpdateService.this)) {
                            mBus.postSticky(new UpdateCastEvent(data));
                        } else {
                            mBus.postSticky(new UpdateCastEvent(null));
                        }
                    }
                });
                break;
        }

    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public UpdateService() {
        super(TAG);
        Log.d(TAG, "UpdateService ");
    }
}
