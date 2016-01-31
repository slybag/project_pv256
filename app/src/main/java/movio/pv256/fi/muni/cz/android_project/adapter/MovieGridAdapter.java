package movio.pv256.fi.muni.cz.android_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tonicartos.superslim.GridSLM;
import com.tonicartos.superslim.LayoutManager;

import movio.pv256.fi.muni.cz.android_project.R;
import movio.pv256.fi.muni.cz.android_project.model.Movie;
import movio.pv256.fi.muni.cz.android_project.utils.HeaderArrayList;

/**
 * Created by Michal on 30. 1. 2016.
 */
public class MovieGridAdapter extends RecyclerView.Adapter<MovieGridAdapter.Holder> {

    public static final int VIEW_TYPE_HEADER = 0;
    public static final int VIEW_TYPE_CONTENT = 1;

    private HeaderArrayList<Movie> mItems;
    private int grid_column_width;
    private int grid_column_count;

    private int width = 0;
    private int height = 0;

    private int mGroupCount;

    public MovieGridAdapter(Context ctx, HeaderArrayList<Movie> data, int column_width, int column_count) {
        mItems = data;
        grid_column_width = column_width;
        grid_column_count = column_count;
        width = ctx.getResources().getDisplayMetrics().widthPixels / 3;
        height = Math.round(width * 1.41f);
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).isHeader ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        Holder holder;
        if (viewType == VIEW_TYPE_HEADER) {
            view = inflater.inflate(R.layout.view_movie_header, parent, false);
            holder = new HeaderVH(view);
        } else {
            view = inflater.inflate(R.layout.view_movie_item, parent, false);
            holder = new ContentVH(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        Movie item = mItems.get(position);
        holder.bindItem(item);

        GridSLM.LayoutParams lp = GridSLM.LayoutParams.from(holder.itemView.getLayoutParams());
        if (item.isHeader) {
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.isHeader = true;
            lp.headerDisplay = LayoutManager.LayoutParams.HEADER_INLINE;
        } else {
            lp.isHeader = false;
        }
        lp.setSlm(GridSLM.ID);
        lp.setColumnWidth(grid_column_width);
        lp.setFirstPosition(position - (position % mItems.getHeaderEvery()));
        lp.setNumColumns(grid_column_count);
        holder.itemView.setLayoutParams(lp);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public abstract class Holder extends RecyclerView.ViewHolder {
        public Holder(View itemView) {
            super(itemView);
        }

        public abstract void bindItem(Movie item);
    }

    public class ContentVH extends Holder {

        ImageView mImageView;

        public ContentVH(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.movie_image);
        }

        @Override
        public void bindItem(Movie item) {
            Glide.with(mImageView.getContext())
                    .load("http://image.tmdb.org/t/p/w500" + item.coverPath)
                    .error(R.drawable.im_no_poster)
                    .placeholder(R.drawable.im_placeholder_poster)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .into(mImageView);
        }
    }

    public class HeaderVH extends Holder {

        TextView mTextView;

        public HeaderVH(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.section_name);
        }

        @Override
        public void bindItem(Movie item) {
            mTextView.setText(item.title);
        }
    }


}
