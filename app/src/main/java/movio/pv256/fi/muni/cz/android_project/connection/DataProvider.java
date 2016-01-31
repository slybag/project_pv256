package movio.pv256.fi.muni.cz.android_project.connection;

import android.net.Uri;
import android.util.Log;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import movio.pv256.fi.muni.cz.android_project.connection.moshiLib.MoshiConverterFactory;
import movio.pv256.fi.muni.cz.android_project.model.Cast;
import movio.pv256.fi.muni.cz.android_project.model.GenresWrapper;
import movio.pv256.fi.muni.cz.android_project.model.Movie;
import movio.pv256.fi.muni.cz.android_project.model.MovieCredits;
import movio.pv256.fi.muni.cz.android_project.model.MovieWrapper;
import movio.pv256.fi.muni.cz.android_project.utils.DateUtils;
import movio.pv256.fi.muni.cz.android_project.utils.HeaderArrayList;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Michal on 30. 1. 2016.
 */
public class DataProvider {

    public interface DataLoaded {
        void onDataLoaded(HeaderArrayList<Movie> data);
    }

    public interface CastLoaded {
        void onCastLoaded(ArrayList<Cast> data);
    }

    public static final String BASE_URL = "https://api.themoviedb.org";

    public static final String API_KEY = "4676b2ad373f69b4c152cb41314548ff";

    public static final int CAT_1 = 0;
    public static final int CAT_2 = 1;
    public static final int CAT_3 = 2;

    private static DataProvider mInstance;

    public static DataProvider get() {
        if (mInstance == null) {
            mInstance = new DataProvider();
        }
        return mInstance;
    }

    private TheMovieDatabaseService mService;

    private List<Call<MovieWrapper>> mDataRequest;

    private Call<MovieCredits> mCastRequest;

    private DataProvider() {

        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(new Interceptor() {

            private static final String TAG = ".DataProvider";

            @Override
            public com.squareup.okhttp.Response intercept(Interceptor.Chain chain) throws IOException {

                Log.d(TAG, "intercept() returned: " + chain.request().url());

                return chain.proceed(chain.request());
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();

        mService = retrofit.create(TheMovieDatabaseService.class);

        mDataRequest = new ArrayList<>(3);
    }

    public void cancelLoading() {
        if (!mDataRequest.isEmpty()) {
            for (int i = 0, size = mDataRequest.size(); i < size; i++) {
                mDataRequest.get(i).cancel();
            }
            mDataRequest.clear();
        }
    }

    private static final String TAG = ".DataProvider";

    public void loadData(String genreIdString, DataLoaded listener) {
        cancelLoading();

        Log.d(TAG, "loadData() called with: " + "genreIdString = [" + genreIdString + "], listener = ["
                + listener + "]");

        final ResultDeliverBarrier barrier = new ResultDeliverBarrier(listener);

        long milis_now = System.currentTimeMillis();
        long milis_7days = milis_now + DateUtils.getMilis(DateUtils.Type.DAY, 7);
        long milis_31days = milis_now + DateUtils.getMilis(DateUtils.Type.DAY, 31);
        long milis_365days = milis_now + DateUtils.getMilis(DateUtils.Type.DAY, 365);

        mDataRequest.add(CAT_1, mService.getMovies(
                API_KEY,
                "popularity.desc",
                Uri.encode(genreIdString),
                DateUtils.format(DateUtils.DEFAULT_DAY, milis_now),
                DateUtils.format(DateUtils.DEFAULT_DAY, milis_7days),
                Locale.getDefault().getLanguage()
        ));

        mDataRequest.get(CAT_1).enqueue(
                new Callback<MovieWrapper>() {
                    @Override
                    public void onResponse(Response<MovieWrapper> response,
                                           Retrofit retrofit) {
                        barrier.addList1(response.body().movies);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        barrier.addList1(null);
                    }
                });

        mDataRequest.add(CAT_2, mService.getMovies(
                API_KEY,
                "popularity.desc",
                Uri.encode(genreIdString),
                DateUtils.format(DateUtils.DEFAULT_DAY, milis_7days),
                DateUtils.format(DateUtils.DEFAULT_DAY, milis_31days),
                Locale.getDefault().getLanguage()
        ));

        mDataRequest.get(CAT_2).enqueue(
                new Callback<MovieWrapper>() {
                    @Override
                    public void onResponse(Response<MovieWrapper> response,
                                           Retrofit retrofit) {
                        barrier.addList2(response.body().movies);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        barrier.addList2(null);
                    }
                });

        mDataRequest.add(CAT_3, mService.getMovies(
                API_KEY,
                "popularity.desc",
                Uri.encode(genreIdString),
                DateUtils.format(DateUtils.DEFAULT_DAY, milis_31days),
                DateUtils.format(DateUtils.DEFAULT_DAY, milis_365days),
                Locale.getDefault().getLanguage()
        ));

        mDataRequest.get(CAT_3).enqueue(
                new Callback<MovieWrapper>() {
                    @Override
                    public void onResponse(Response<MovieWrapper> response,
                                           Retrofit retrofit) {
                        barrier.addList3(response.body().movies);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        barrier.addList3(null);
                    }
                });
    }

    public Movie loadMovie(long movie_id) throws IOException {
        return mService.getMovie(movie_id, API_KEY, Locale.getDefault().getLanguage()).execute().body();
    }

    public void loadGenres(Callback<GenresWrapper> callback) {
        mService.getGenres(API_KEY, Locale.getDefault().getLanguage()).enqueue(callback);
    }

    public void loadCast(long movie_id, final CastLoaded listener) {
        if (mCastRequest != null) {
            mCastRequest.cancel();
        }
        mCastRequest = mService.getCast(movie_id, API_KEY);
        mCastRequest.enqueue(new Callback<MovieCredits>() {
            @Override
            public void onResponse(Response<MovieCredits> response, Retrofit retrofit) {
                if (response.body().cast.size() > 10) {
                    listener.onCastLoaded(new ArrayList<>(response.body().cast.subList(0, 10)));
                } else {
                    listener.onCastLoaded(new ArrayList<>(response.body().cast));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                listener.onCastLoaded(null);
            }
        });
    }

    private interface TheMovieDatabaseService {

        @GET("/3/genre/movie/list")
        Call<GenresWrapper> getGenres(
                @Query("api_key") String api_key,
                @Query("language") String lang
        );

        @GET("/3/movie/{id}")
        Call<Movie> getMovie(
                @Path("id") long movie_id,
                @Query("api_key") String api_key,
                @Query("language") String lang
        );

        @GET("/3/discover/movie")
        Call<MovieWrapper> getMovies(
                @Query("api_key") String api_key,
                @Query("sort_by") String sort,
                @Query(value = "with_genres", encoded = true) String genres,
                @Query("primary_release_date.gte") String dateFrom,
                @Query("primary_release_date.lte") String dateTo,
                @Query("language") String lang
        );

        @GET("/3/movie/{id}/credits")
        Call<MovieCredits> getCast(
                @Path("id") long movie_id,
                @Query("api_key") String api_key
        );
    }
}
