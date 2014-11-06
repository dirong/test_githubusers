package com.example.dirong.githubusers.network;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.dirong.githubusers.utils.URLUtils;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dirong on 11/6/14.
 */
public class User implements Parcelable {

    private static final String SIZE_PHOTO_PARAMETER = "size";
    public static final int SMALL_AVATAR_SIZE = 100;
    public static final int NORMAL_AVATAR_SIZE = 400;

    Long id;

    String login;

    @SerializedName("avatar_url")
    String avatar;

    @SerializedName("html_url")
    String url;

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAvatarWithSize(int size) {
        return URLUtils.addParameter(avatar, SIZE_PHOTO_PARAMETER, String.valueOf(size));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.login);
        dest.writeString(this.avatar);
        dest.writeString(this.url);
    }

    private User(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.login = in.readString();
        this.avatar = in.readString();
        this.url = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
