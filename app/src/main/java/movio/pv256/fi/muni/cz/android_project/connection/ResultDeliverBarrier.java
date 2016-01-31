package movio.pv256.fi.muni.cz.android_project.connection;

import java.util.List;

import movio.pv256.fi.muni.cz.android_project.App;
import movio.pv256.fi.muni.cz.android_project.R;
import movio.pv256.fi.muni.cz.android_project.model.Movie;
import movio.pv256.fi.muni.cz.android_project.utils.HeaderArrayList;

/**
 * Created by Michal on 30. 1. 2016.
 */
public class ResultDeliverBarrier {

    private DataProvider.DataLoaded mListener;

    private HeaderArrayList<Movie> mList1;
    private HeaderArrayList<Movie> mList2;
    private HeaderArrayList<Movie> mList3;

    private boolean mList1Loaded = false;
    private boolean mList2Loaded = false;
    private boolean mList3Loaded = false;

    public ResultDeliverBarrier(DataProvider.DataLoaded listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener == null");
        }
        mListener = listener;
        mList1 = new HeaderArrayList<>(7);
        mList2 = new HeaderArrayList<>(7);
        mList3 = new HeaderArrayList<>(7);
        mList1.add(0, new Movie(App.get().getString(R.string.category1), true));
        mList2.add(0, new Movie(App.get().getString(R.string.category2), true));
        mList3.add(0, new Movie(App.get().getString(R.string.category3), true));
    }

    public void addList1(List<Movie> list) {
        mList1Loaded = true;
        if (list != null && list.size() > 6) {
            mList1.addAll(list.subList(0, 6));
        } else if (list != null) {
            mList1.addAll(list);
        }
        deliverIfComplete();
    }

    public void addList2(List<Movie> list) {
        mList2Loaded = true;
        if (list != null && list.size() > 6) {
            mList2.addAll(list.subList(0, 6));
        } else if (list != null) {
            mList2.addAll(list);
        }
        deliverIfComplete();
    }

    public void addList3(List<Movie> list) {
        mList3Loaded = true;
        if (list != null && list.size() > 6) {
            mList3.addAll(list.subList(0, 6));
        } else if (list != null) {
            mList3.addAll(list);
        }
        deliverIfComplete();
    }

    private void deliverIfComplete() {
        if (!(mList1Loaded && mList2Loaded && mList3Loaded)) {
            return;
        }

        HeaderArrayList<Movie> list = new HeaderArrayList<>(21);
        list.setHeaderEvery(7);
        if (mList1 != null) {
            list.addAll(mList1);
            mList1.clear();
        }
        if (mList2 != null) {
            list.addAll(mList2);
            mList2.clear();
        }
        if (mList3 != null) {
            list.addAll(mList3);
            mList3.clear();
        }
        mListener.onDataLoaded(list);
    }
}
