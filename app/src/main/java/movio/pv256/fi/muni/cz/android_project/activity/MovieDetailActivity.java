package movio.pv256.fi.muni.cz.android_project.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import movio.pv256.fi.muni.cz.android_project.R;
import movio.pv256.fi.muni.cz.android_project.fragment.MovieDetailFragment;
import movio.pv256.fi.muni.cz.android_project.model.Movie;
import movio.pv256.fi.muni.cz.android_project.service.UpdateCastEvent;
import movio.pv256.fi.muni.cz.android_project.service.UpdateService;

/**
 * Created by Michal on 30. 1. 2016.
 */
public class MovieDetailActivity extends BaseActivity {

    private static final String TAG = ".MovieDetailActivity";

    private MovieDetailFragment mMovieDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowHomeEnabled(true);
        }

        mMovieDetailFragment =
                (MovieDetailFragment) getFragmentManager().findFragmentById(R.id.details_frag);

        mMovieDetailFragment.setMovie((Movie) getIntent().getExtras().getParcelable("movie"));
    }

    @SuppressWarnings("unused")
    public void onEvent(final UpdateCastEvent event) {
        if (event.getData() == null) {
            showNotification();
        }
        if (mMovieDetailFragment != null) {
            mMovieDetailFragment.setCast(event.getData());
        }
    }

    @Override
    public void requestCast(long movie_id) {
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, UpdateService.class);
        intent.putExtra("action", "detail");
        intent.putExtra("id", movie_id);
        startService(intent);
    }

    public void showNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.no_connection));
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }
}
