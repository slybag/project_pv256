package movio.pv256.fi.muni.cz.android_project.database;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

import movio.pv256.fi.muni.cz.android_project.model.Movie;
import  movio.pv256.fi.muni.cz.android_project.database.Contract.MovieEntry;

/**
 * Created by Michal on 30. 1. 2016.
 */
public class Manager {

    private static final String TAG = ".Manager";

    private static final String WHERE_ID = MovieEntry._ID + " = ?";

    public static final String[] MOVIE_COLS = {
            MovieEntry._ID,
            MovieEntry.COLUMN_TITLE,
            MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieEntry.COLUMN_OVERVIEW,
            MovieEntry.COLUMN_COVER_PATH,
            MovieEntry.COLUMN_BACKDROP_PATH,
            MovieEntry.COLUMN_RELEASE_DATE
    };

    private Context mContext;

    public Manager(Context context) {
        mContext = context.getApplicationContext();
    }

    public boolean contains(long id) {
        Cursor cursor = mContext.getContentResolver()
                .query(MovieEntry.CONTENT_URI, new String[]{MovieEntry._ID}, WHERE_ID,
                        new String[]{String.valueOf(id)}, null);

        if (cursor == null) {
            return false;
        }

        boolean result = cursor.getCount() == 1;
        cursor.close();
        return result;
    }

    public Movie get(long id) {

        Cursor cursor = mContext.getContentResolver().query(MovieEntry.CONTENT_URI,
                MOVIE_COLS, WHERE_ID, new String[]{String.valueOf(id)}, null);

        if (cursor == null) {
            return null;
        }

        if (cursor.getCount() == 0){
            cursor.close();
            return  null;
        }

        cursor.moveToFirst();
        Movie movie = getMovieFromCursor(cursor);
        cursor.close();
        return movie;
    }

    public ArrayList<Movie> getAll() {

        ArrayList<Movie> list = new ArrayList<>();

        Cursor cursor =
                mContext.getContentResolver().query(MovieEntry.CONTENT_URI, MOVIE_COLS, null, null, null);

        if (cursor == null) {
            return list;
        }

        while (cursor.moveToNext()) {
            list.add(getMovieFromCursor(cursor));
        }
        cursor.close();

        return list;
    }

    public long add(Movie movie) {
        Log.d(TAG, "add() called with: " + "movie = [" + movie + "]");
        return ContentUris.parseId(mContext.getContentResolver()
                .insert(MovieEntry.CONTENT_URI, getContentValuesFromMovie(movie)));
    }

    public void set(Movie movie) {
        mContext.getContentResolver()
                .update(MovieEntry.CONTENT_URI, getContentValuesFromMovie(movie), WHERE_ID,
                        new String[]{String.valueOf(movie.id)});
    }

    public void delete(Movie movie) {
        delete(movie.id);
    }

    public void delete(long id) {
        mContext.getContentResolver().delete(MovieEntry.CONTENT_URI, WHERE_ID,
                new String[]{String.valueOf(id)});
    }

    public static Movie getMovieFromCursor(Cursor openedCursor) {
        Movie movie = new Movie();
        movie.id = openedCursor.getLong(0);
        movie.title = openedCursor.getString(1);
        movie.originalTitle = openedCursor.getString(2);
        movie.overview = openedCursor.getString(3);
        movie.coverPath = openedCursor.getString(4);
        movie.backdropPath = openedCursor.getString(5);
        movie.releaseDate = openedCursor.getString(6);
        movie.isHeader = false;
        return movie;
    }

    public static ContentValues getContentValuesFromMovie(Movie movie) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieEntry._ID, movie.id);
        contentValues.put(MovieEntry.COLUMN_TITLE, movie.title);
        contentValues.put(MovieEntry.COLUMN_ORIGINAL_TITLE, movie.originalTitle);
        contentValues.put(MovieEntry.COLUMN_OVERVIEW, movie.overview);
        contentValues.put(MovieEntry.COLUMN_COVER_PATH, movie.coverPath);
        contentValues.put(MovieEntry.COLUMN_BACKDROP_PATH, movie.backdropPath);
        contentValues.put(MovieEntry.COLUMN_RELEASE_DATE, movie.releaseDate);
        return contentValues;
    }
}
