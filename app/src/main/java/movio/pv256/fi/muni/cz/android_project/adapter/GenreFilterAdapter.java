package movio.pv256.fi.muni.cz.android_project.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import movio.pv256.fi.muni.cz.android_project.R;
import movio.pv256.fi.muni.cz.android_project.model.Genre;

/**
 * Created by Michal on 30. 1. 2016.
 */
public class GenreFilterAdapter extends ArrayAdapter<Genre> {

    public interface OnSelectionChanged{
        void allSelected(boolean isAllSelected);
    }

    private static final String TAG = ".GenreFilterAdapter";

    private LayoutInflater mLayoutInflater;

    private SharedPreferences mSharedPreferences;

    private OnSelectionChanged mListener;

    public GenreFilterAdapter(Context context, List<Genre> objects, OnSelectionChanged listener) {
        super(context, R.layout.menu_checkable_item, objects);
        mLayoutInflater = LayoutInflater.from(context);
        mSharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        mListener = listener;
        checkSelectionAndNotifyParent();
    }

    public String getCheckedIds() {
        String genreIdString = "";
        String separator = "";
        int len = getCount();
        for (int i = 0; i < len; i++) {
            Genre genre = getItem(i);
            if (genre.isChecked) {
                genreIdString += separator;
                genreIdString += genre.id;
                separator = "|";
            }
        }
        return genreIdString;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.menu_checkable_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Genre genre = getItem(position);

        if (genre != null) {
            viewHolder.name.setText(genre.name);
            viewHolder.checkBox.setChecked(
                    genre.isChecked = mSharedPreferences.getBoolean(String.valueOf(genre.id), true));
            viewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChecked = !viewHolder.checkBox.isChecked();
                    viewHolder.checkBox.setChecked(isChecked);
                    mSharedPreferences.edit().putBoolean(String.valueOf(genre.id), isChecked).apply();
                    genre.isChecked = isChecked;
                    checkSelectionAndNotifyParent();
                }
            });
        }

        return convertView;
    }

    private void checkSelectionAndNotifyParent() {
        boolean result = true;
        for (int i = 0, size = getCount(); i < size; i++) {
            result &= getItem(i).isChecked;
        }
        if (mListener != null){
            mListener.allSelected(result);
        }
    }

    static class ViewHolder {
        CheckBox checkBox;
        TextView name;
        View root;

        public ViewHolder(View root) {
            this.root = root;
            name = (TextView) root.findViewById(R.id.name);
            checkBox = (CheckBox) root.findViewById(R.id.checkbox);
        }
    }

    public void selectAll() {
        bulkSelection(true);
    }

    public void deselectAll(){
        bulkSelection(false);
    }

    private void bulkSelection(boolean isSelected){
        for (int i = 0, size = getCount(); i < size; i++) {
            Genre genre = getItem(i);
            mSharedPreferences.edit().putBoolean(String.valueOf(genre.id), isSelected).apply();
            genre.isChecked = true;
        }
        notifyDataSetInvalidated();
    }
}
