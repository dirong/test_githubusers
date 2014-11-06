package com.example.dirong.githubusers.model;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.dirong.githubusers.network.User;

/**
 * Created by dirong on 11/6/14.
 */
public class PhotoViewData implements Parcelable {

    private User user;

    private Rect viewRect;

    public PhotoViewData(User user, Rect viewRect) {
        this.user = user;
        this.viewRect = viewRect;
    }

    public User getUser() {
        return user;
    }

    public Rect getViewRect() {
        return viewRect;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.user, 0);
        dest.writeParcelable(this.viewRect, 0);
    }

    private PhotoViewData(Parcel in) {
        this.user = in.readParcelable(User.class.getClassLoader());
        this.viewRect = in.readParcelable(Rect.class.getClassLoader());
    }

    public static final Creator<PhotoViewData> CREATOR = new Creator<PhotoViewData>() {
        public PhotoViewData createFromParcel(Parcel source) {
            return new PhotoViewData(source);
        }

        public PhotoViewData[] newArray(int size) {
            return new PhotoViewData[size];
        }
    };
}
