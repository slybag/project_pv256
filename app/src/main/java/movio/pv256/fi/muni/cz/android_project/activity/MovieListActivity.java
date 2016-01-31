package movio.pv256.fi.muni.cz.android_project.activity;

import android.app.LoaderManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import movio.pv256.fi.muni.cz.android_project.R;
import movio.pv256.fi.muni.cz.android_project.adapter.GenreFilterAdapter;
import movio.pv256.fi.muni.cz.android_project.connection.DataProvider;
import movio.pv256.fi.muni.cz.android_project.connection.NetworkStateChangedReceiver;
import movio.pv256.fi.muni.cz.android_project.database.Contract;
import movio.pv256.fi.muni.cz.android_project.database.Manager;
import movio.pv256.fi.muni.cz.android_project.fragment.MovieDetailFragment;
import movio.pv256.fi.muni.cz.android_project.fragment.MovieListFragment;
import movio.pv256.fi.muni.cz.android_project.model.Genre;
import movio.pv256.fi.muni.cz.android_project.model.GenresWrapper;
import movio.pv256.fi.muni.cz.android_project.model.Movie;
import movio.pv256.fi.muni.cz.android_project.service.UpdateCastEvent;
import movio.pv256.fi.muni.cz.android_project.service.UpdateListEvent;
import movio.pv256.fi.muni.cz.android_project.service.UpdateService;
import movio.pv256.fi.muni.cz.android_project.sync.SyncAdapter;
import movio.pv256.fi.muni.cz.android_project.sync.SyncDoneEvent;
import movio.pv256.fi.muni.cz.android_project.utils.HeaderArrayList;
import movio.pv256.fi.muni.cz.android_project.utils.RecyclerItemClickListener;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Michal on 30. 1. 2016.
 */
public class MovieListActivity
        extends BaseActivity
        implements
        RecyclerItemClickListener.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        NetworkStateChangedReceiver.NetworkStateChanged,
        GenreFilterAdapter.OnSelectionChanged,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ".MovieListActivity";

    private MovieListFragment mListFragment;
    private MovieDetailFragment mDetailFragment;

    private GenreFilterAdapter mGenresAdapter;
    private CheckBox mGenresCheckboxAll;

    private SharedPreferences mPreferences;
    private boolean savedData;

    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener =
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (mGenresAdapter != null) {
                        if (isChecked) {
                            mGenresAdapter.selectAll();
                        } else {
                            mGenresAdapter.deselectAll();
                        }
                    }
                }
            };

    private HeaderArrayList<Movie> mDataDiscover;
    private HeaderArrayList<Movie> mDataSaved;

    @SuppressWarnings("unused")
    public void onEvent(final UpdateListEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (event.getData() == null) {
                    showNotification();
                }
                mDataDiscover = event.getData();
                setData();
            }
        });
    }

    @SuppressWarnings("unused")
    public void onEvent(final UpdateCastEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (event.getData() == null) {
                    showNotification();
                }
                if (mDetailFragment != null) {
                    mDetailFragment.setCast(event.getData());
                }
            }
        });
    }

    @SuppressWarnings("unused")
    public void onEvent(final SyncDoneEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getLoaderManager().restartLoader(0, null, MovieListActivity.this);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreferences = getSharedPreferences(TAG, MODE_PRIVATE);

        getLoaderManager().initLoader(1, null, this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle =
                new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open,
                        R.string.drawer_close) {

                    private String prevSelection = "";

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                        super.onDrawerClosed(drawerView);
                        //Test if selection has changed before load.
                        if (!prevSelection.equalsIgnoreCase(mGenresAdapter.getCheckedIds())) {
                            if (mListFragment != null) {
                                mListFragment.showLoading();
                            }
                            requestList(mGenresAdapter.getCheckedIds());
                        }
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        super.onDrawerOpened(drawerView);
                        prevSelection = mGenresAdapter.getCheckedIds();
                    }
                };

        mGenresCheckboxAll = (CheckBox) findViewById(R.id.checkbox_all);
        mGenresCheckboxAll.setOnCheckedChangeListener(mOnCheckedChangeListener);

        final ListView lv = (ListView) findViewById(R.id.listview);
        mGenresAdapter = new GenreFilterAdapter(this, new HeaderArrayList<Genre>(), this);
        lv.setAdapter(mGenresAdapter);

        DataProvider.get().loadGenres(new Callback<GenresWrapper>() {
            @Override
            public void onResponse(Response<GenresWrapper> response, Retrofit retrofit) {
                Log.d(TAG, "genres = [" + response.body() + "]");
                mGenresAdapter.clear();
                mGenresAdapter.addAll(response.body().genres);
                mGenresAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "onFailure ", t);
            }
        });

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

        mListFragment =
                (MovieListFragment) getFragmentManager().findFragmentById(R.id.list_frag);
        mDetailFragment =
                (MovieDetailFragment) getFragmentManager().findFragmentById(R.id.details_frag);

        if (mListFragment != null) {
            mListFragment.showLoading();
            mListFragment.addListOnClickListener(this, this);
            mListFragment.setOnRefreshListener(this);
        }

        requestList(mPreferences.getString("genres_selection", ""));
        if (!mConnected) {
            Toast.makeText(this, "No internet connection, data could be inaccurate.", Toast.LENGTH_LONG)
                    .show();
        }

        SyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        savedData = mPreferences.getBoolean("savedData", false);
        final SwitchCompat sc =
                ((SwitchCompat) menu.getItem(0).getActionView().findViewById(R.id.switch_button));
        sc.setChecked(savedData);
        sc.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mPreferences.edit().putBoolean("savedData", savedData = isChecked).apply();
                        if (!isChecked) {
                            requestList(mGenresAdapter.getCheckedIds());
                        } else {
                            getLoaderManager().restartLoader(1, null, MovieListActivity.this);
                        }
                    }
                });
        return true;
    }

    @Override
    public void allSelected(boolean isAllSelected) {
        mGenresCheckboxAll.setOnCheckedChangeListener(null);
        mGenresCheckboxAll.setChecked(isAllSelected);
        mGenresCheckboxAll.setOnCheckedChangeListener(mOnCheckedChangeListener);
    }

    public void setData() {
        Log.d(TAG, "setData() called with: " + "");
        if (mListFragment != null) {
            mListFragment.setData(getCurrentData());
        }
    }

    @Override
    public void onRefresh() {
        if (mConnected) {
            //noinspection ConstantConditions
            requestList(mGenresAdapter.getCheckedIds());
        } else {
            showNotification();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "onItemClick() called with: " + "view = [" + view + "], position = ["
                + position + "]");
        if (mDetailFragment == null || !mDetailFragment.isFragmentUIActive()) {
            Intent intent =
                    new Intent(MovieListActivity.this, MovieDetailActivity.class);
            intent.putExtra("movie", getCurrentData().get(position));
            startActivity(intent);
        } else {
            mDetailFragment.setMovie(getCurrentData().get(position));
        }
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Log.d(TAG,
                "onItemLongClick() called with: " + "view = [" + view + "], position = ["
                        + position + "]");
        Toast.makeText(MovieListActivity.this, getCurrentData().get(position).title, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called with: " + "");
        cancelLoading();
        mPreferences.edit().putString("genres_selection", mGenresAdapter.getCheckedIds()).apply();
    }

    public void requestList(String genres) {
        if (savedData) {
            SyncAdapter.syncImmediately(this);
        } else {
            Intent intent = new Intent(Intent.ACTION_SYNC, null, this, UpdateService.class);
            intent.putExtra("action", "list");
            intent.putExtra("genres", genres);
            startService(intent);
        }
    }

    @Override
    public void requestCast(long movie_id) {
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, UpdateService.class);
        intent.putExtra("action", "detail");
        intent.putExtra("id", movie_id);
        startService(intent);
    }

    public void cancelLoading() {
        DataProvider.get().cancelLoading();
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Contract.MovieEntry.CONTENT_URI,
                Manager.MOVIE_COLS,
                null,
                null,
                Contract.MovieEntry.COLUMN_RELEASE_DATE + " ASC ");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            HeaderArrayList<Movie> list = new HeaderArrayList<>();
            list.add(new Movie(getString(R.string.saved), true));
            list.setHeaderEvery(Integer.MAX_VALUE);
            while (data.moveToNext()) {
                list.add(Manager.getMovieFromCursor(data));
            }
            data.close();
            mDataSaved = list;
            setData();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private HeaderArrayList<Movie> getCurrentData() {
        return savedData ? mDataSaved : mDataDiscover;
    }
}
