package movio.pv256.fi.muni.cz.android_project.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import movio.pv256.fi.muni.cz.android_project.R;
import movio.pv256.fi.muni.cz.android_project.activity.BaseActivity;
import movio.pv256.fi.muni.cz.android_project.database.Manager;
import movio.pv256.fi.muni.cz.android_project.model.Cast;
import movio.pv256.fi.muni.cz.android_project.model.Movie;
import movio.pv256.fi.muni.cz.android_project.utils.CircleTransform;
import movio.pv256.fi.muni.cz.android_project.utils.DateUtils;

/**
 * Created by Michal on 30. 1. 2016.
 */
public class MovieDetailFragment extends Fragment {

    private static final String TAG = ".MovieDetailFragment";

    private ImageView mTitleImage;
    private ImageView mTitleBackgroundImage;

    private TextView mTitleTranslated;
    private TextView mTitleOriginal;
    private TextView mYear;
    private TextView mOverView;

    private View mContentView;
    private View mEmptyView;
    private LinearLayout mCastContainer;

    private LayoutInflater mLayoutInflater;

    private FloatingActionButton mSaveFab;

    private Manager mManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mLayoutInflater = inflater;
        View root = mLayoutInflater.inflate(R.layout.fragment_detail, container);

        mContentView = root.findViewById(R.id.content_view);
        mEmptyView = root.findViewById(R.id.empty);
        mContentView.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.VISIBLE);

        mTitleImage = (ImageView) root.findViewById(R.id.movie_image);
        mTitleBackgroundImage = (ImageView) root.findViewById(R.id.movie_background_image);

        mTitleTranslated = (TextView) root.findViewById(R.id.movie_title);
        mTitleOriginal = (TextView) root.findViewById(R.id.movie_title_original);
        mYear = (TextView) root.findViewById(R.id.movie_year);
        mOverView = (TextView) root.findViewById(R.id.movie_overview);

        mSaveFab = (FloatingActionButton) root.findViewById(R.id.fab);

        mCastContainer = (LinearLayout) root.findViewById(R.id.cast_container);

        mManager = new Manager(getActivity());

        return root;
    }

    public void setMovie(final Movie movie) {
        Log.d(TAG, "setMovie() called with: " + "movie = [" + movie + "]");
        if (movie == null) {
            mContentView.setVisibility(View.INVISIBLE);
            mEmptyView.setVisibility(View.VISIBLE);
            return;
        }

        ((BaseActivity) getActivity()).requestCast(movie.id);

        Glide.with(mTitleBackgroundImage.getContext())
                .load("http://image.tmdb.org/t/p/w1280" + movie.backdropPath)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .error(R.drawable.im_no_back)
                .placeholder(R.drawable.im_placeholder_back)
                .centerCrop()
                .into(mTitleBackgroundImage);

        Glide.with(mTitleImage.getContext())
                .load("http://image.tmdb.org/t/p/w500" + movie.coverPath)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .error(R.drawable.im_no_poster)
                .placeholder(R.drawable.im_placeholder_poster)
                .into(mTitleImage);

        mTitleTranslated.setText(movie.title);
        mTitleOriginal.setText(movie.originalTitle);
        mOverView.setText(movie.overview);
        mYear.setText(DateUtils
                .format("dd. MM. yyyy", DateUtils.parseDate(DateUtils.DEFAULT_DAY, movie.releaseDate)));

        mCastContainer.removeAllViews();

        mEmptyView.setVisibility(View.INVISIBLE);
        mContentView.setVisibility(View.VISIBLE);

        if (mManager.contains(movie.id)) {
            mSaveFab.setImageResource(R.drawable.ic_remove);
        } else {
            mSaveFab.setImageResource(R.drawable.ic_add);
        }

        mSaveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mManager.contains(movie.id)) {
                    mManager.delete(movie);
                    mSaveFab.setImageResource(R.drawable.ic_add);
                } else {
                    mManager.add(movie);
                    mSaveFab.setImageResource(R.drawable.ic_remove);
                }
            }
        });
    }

    public void setCast(ArrayList<Cast> data) {

        mCastContainer.removeAllViews();

        final Context context = getActivity();
        for (int i = 0, actorsSize = data.size(); i < actorsSize; i++) {
            final Cast person = data.get(i);
            View item = mLayoutInflater.inflate(R.layout.view_cast_item, mCastContainer, false);
            final TextView name = (TextView) item.findViewById(R.id.actor_name);

            final ImageView icon = (ImageView) item.findViewById(R.id.actor_image);
            Glide.with(icon.getContext())
                    .load("http://image.tmdb.org/t/p/w185" + person.image)
                    .transform(new CircleTransform(context))
                    .error(R.drawable.im_no_back)
                    .placeholder(R.drawable.im_placeholder_poster)
                    .into(icon);
            name.setText(person.name);
            name.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(name.getContext(), person.character, Toast.LENGTH_SHORT).show();
                    return false;
                }
            });

            mCastContainer.addView(item);
        }
    }

    public boolean isFragmentUIActive() {
        return isAdded() && !isDetached() && !isRemoving();
    }
}
