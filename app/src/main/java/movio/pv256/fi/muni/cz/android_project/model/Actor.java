package movio.pv256.fi.muni.cz.android_project.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Michal on 30. 1. 2016.
 */
public class Actor implements Parcelable {

    public String mImage;
    public String mName;

    public Actor(String image, String name) {
        mImage = image;
        mName = name;
    }

    public Actor(Parcel in) {
        mImage = in.readString();
        mName = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mImage);
        dest.writeString(mName);
    }

    public static final Creator<Actor> CREATOR =
            new Creator<Actor>() {
                public Actor createFromParcel(Parcel in) {
                    return new Actor(in);
                }

                public Actor[] newArray(int size) {
                    return new Actor[size];
                }
            };

}
