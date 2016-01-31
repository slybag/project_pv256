package movio.pv256.fi.muni.cz.android_project.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import de.greenrobot.event.EventBus;
import movio.pv256.fi.muni.cz.android_project.connection.NetworkStateChangedReceiver;
import movio.pv256.fi.muni.cz.android_project.service.UpdateService;

/**
 * Created by Michal on 30. 1. 2016.
 */
public abstract class BaseActivity extends AppCompatActivity
        implements NetworkStateChangedReceiver.NetworkStateChanged {

    private static final String TAG = ".BaseActivity";

    private UpdateService mService;

    private final EventBus mBus = EventBus.getDefault();

    private NetworkStateChangedReceiver mNetworkStateChangedReceiver =
            new NetworkStateChangedReceiver(this);

    protected boolean mConnected = false;


    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder binder) {
            mService = ((UpdateService.UpdateServiceBinder) binder).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBus.register(this);
        mConnected = NetworkStateChangedReceiver.isNetworkAvailable(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBus.unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called with: " + "");
        registerReceiver(mNetworkStateChangedReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called with: " + "");
        unregisterReceiver(mNetworkStateChangedReceiver);
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(this, UpdateService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        unbindService(mConnection);
    }

    @Override
    public void isConnected(boolean isConnected) {
        mConnected = isConnected;
    }

    public abstract void requestCast(long movie_id);
}

