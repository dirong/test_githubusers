package com.example.dirong.githubusers.network;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by dirong on 11/6/14.
 */
public interface GitHub {

    @GET("/users?since=0")
    public void getUsers(Callback<ArrayList<User>> callback);


}
