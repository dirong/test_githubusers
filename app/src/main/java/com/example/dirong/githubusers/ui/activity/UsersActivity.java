package com.example.dirong.githubusers.ui.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dirong.githubusers.GithubUsersApplication;
import com.example.dirong.githubusers.R;
import com.example.dirong.githubusers.model.PhotoViewData;
import com.example.dirong.githubusers.network.GitHub;
import com.example.dirong.githubusers.ui.adapter.RecyclerViewAdapter;
import com.example.dirong.githubusers.ui.adapter.UsersAdapter;
import com.example.dirong.githubusers.network.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class UsersActivity extends ActionBarActivity {

    private static final String EXTRA_USERS = "users";

    @InjectView(R.id.recycler_view)
    RecyclerView recyclerView;
    @InjectView(R.id.progress_bar)
    View progress;
    @InjectView(R.id.swipe_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private GitHub gitHub;
    private UsersAdapter adapter;
    private ArrayList<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        ButterKnife.inject(this);
        setupRecyclerView();
        gitHub = ((GithubUsersApplication) getApplication()).getGitHub();
        if (savedInstanceState != null) {
            users = savedInstanceState.getParcelableArrayList(EXTRA_USERS);
            adapter.setData(users);
        }
        if (adapter.isEmpty()) {
            showProgressLoading(true);
        } else {
            swipeRefreshLayout.setEnabled(true);
        }
        loadUsers();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_USERS, users);
    }

    private void setupRecyclerView() {
        LinearLayoutManager layout = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layout);
        DefaultItemAnimator animator = new DefaultItemAnimator();
        recyclerView.setItemAnimator(animator);
        adapter = new UsersAdapter(this);
        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                Rect rect = new Rect();
                UsersAdapter.ViewHolder holder = new UsersAdapter.ViewHolder(view);
                holder.photo.getGlobalVisibleRect(rect);
                PhotoViewData photoViewData = new PhotoViewData(users.get(position), rect);
                Intent intent = new Intent(UsersActivity.this, PhotoViewActivity.class);
                intent.putExtra(PhotoViewActivity.PHOTO_VIEW_DATA, photoViewData);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadUsers();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void showProgressLoading(boolean show) {
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void loadUsers() {
        gitHub.getUsers(new Callback<ArrayList<User>>() {

            @Override
            public void success(ArrayList<User> data, Response response) {
                if (!isFinishing() && adapter != null) {
                    users = data;
                    adapter.setData(users);
                    showProgressLoading(false);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                showProgressLoading(false);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        swipeRefreshLayout.setRefreshing(false);
    }

}
