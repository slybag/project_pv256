package movio.pv256.fi.muni.cz.android_project.utils;

import java.util.ArrayList;

/**
 * Created by Michal on 30. 1. 2016.
 */
public class HeaderArrayList<T> extends ArrayList<T> {

    private int header_repeater = 7;

    public HeaderArrayList(){
        super();
    }

    public HeaderArrayList(int size){
        super(size);
    }

    public void setHeaderEvery(int count){
        header_repeater = count;
    }

    public int getHeaderEvery(){
        return header_repeater;
    }

}
