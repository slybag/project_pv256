package movio.pv256.fi.muni.cz.android_project.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.tonicartos.superslim.LayoutManager;

import movio.pv256.fi.muni.cz.android_project.R;
import movio.pv256.fi.muni.cz.android_project.adapter.MovieGridAdapter;
import movio.pv256.fi.muni.cz.android_project.model.Movie;
import movio.pv256.fi.muni.cz.android_project.utils.GridItemDecorator;
import movio.pv256.fi.muni.cz.android_project.utils.HeaderArrayList;
import movio.pv256.fi.muni.cz.android_project.utils.RecyclerItemClickListener;

import static android.view.View.VISIBLE;

/**
 * Created by Michal on 30. 1. 2016.
 */
public class MovieListFragment extends Fragment {

    private static final String TAG = ".MovieListFragment";

    public static final int COLUMN_COUNT = 3;

    protected RecyclerView mRecyclerView;
    protected ViewStub mEmptyView;
    protected ViewStub mNoConnView;
    protected View mLoadingView;
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container);

        mEmptyView = (ViewStub) root.findViewById(R.id.empty);
        mNoConnView = (ViewStub) root.findViewById(R.id.no_connection);
        mLoadingView = root.findViewById(R.id.loading);

        mRecyclerView = (RecyclerView) root.findViewById(R.id.movies_grid_view);
        mRecyclerView.setLayoutManager(new LayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(
                new GridItemDecorator(getResources().getDimensionPixelSize(R.dimen.grid_spacing),
                        COLUMN_COUNT));

        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.gplus_color_1, R.color.gplus_color_2,
                R.color.gplus_color_3, R.color.gplus_color_4
        );

        return root;
    }

    public void setData(HeaderArrayList<Movie> data) {
        mSwipeRefreshLayout.setRefreshing(false);
        setLoading(false);
        setNoConnectionViewVisible(data == null);
        setEmptyViewVisible(data != null && data.isEmpty());
        setRecyclerViewVisible(data != null && !data.isEmpty());

        if (data != null) {
            DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
            mRecyclerView.setAdapter(
                    new MovieGridAdapter(getActivity(), data, dm.widthPixels / COLUMN_COUNT, COLUMN_COUNT));
        }
    }

    public void addListOnClickListener(Context ctx,
                                       RecyclerItemClickListener.OnItemClickListener listener) {
        if (mRecyclerView != null) {
            mRecyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(ctx, mRecyclerView, listener));
        }
    }

    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setOnRefreshListener(listener);
        }
    }

    public void showLoading() {
        if (mRecyclerView.getVisibility() == VISIBLE && mRecyclerView.getAdapter().getItemCount() > 0) {
            mSwipeRefreshLayout.setRefreshing(true);
        } else {
            setLoading(true);
        }
    }

    private void setLoading(boolean isVisible) {
        mLoadingView.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    private void setEmptyViewVisible(boolean isVisible) {
        mEmptyView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private void setNoConnectionViewVisible(boolean isVisible) {
        mNoConnView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private void setRecyclerViewVisible(boolean isVisible) {
        mRecyclerView.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

}
