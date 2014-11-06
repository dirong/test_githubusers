package com.example.dirong.githubusers;

import android.app.Application;

import com.example.dirong.githubusers.network.GitHub;

import retrofit.RestAdapter;

/**
 * Created by dirong on 11/6/14.
 */
public class GithubUsersApplication extends Application {

    private static final String API_URL = "https://api.github.com";

    private GitHub gitHub;

    public GitHub getGitHub() {
        if (gitHub == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(API_URL)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .build();
            gitHub = restAdapter.create(GitHub.class);
        }
        return gitHub;
    }
}
