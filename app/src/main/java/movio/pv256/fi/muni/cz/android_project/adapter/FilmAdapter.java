package movio.pv256.fi.muni.cz.android_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import movio.pv256.fi.muni.cz.android_project.R;
import movio.pv256.fi.muni.cz.android_project.model.Film;

/**
 * Created by Michal on 18. 10. 2015.
 */
public class FilmAdapter extends BaseAdapter {

    private List<Film> mData;
    private Context mContext;

    public FilmAdapter(List<Film> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mData.get(position).getId();
    }

    private static class ViewHolder {
        TextView view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.film_row, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.view = (TextView) convertView.findViewById(R.id.text1);
            convertView.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.view.setText(mData.get(position).getTitle());
        return convertView;
    }

}
