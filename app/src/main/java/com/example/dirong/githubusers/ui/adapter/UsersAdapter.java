package com.example.dirong.githubusers.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dirong.githubusers.R;
import com.example.dirong.githubusers.network.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by dirong on 11/6/14.
 */
public class UsersAdapter extends RecyclerViewAdapter<UsersAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final Context context;
    private List<User> users = new ArrayList<User>();

    public UsersAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(inflater.inflate(R.layout.user_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = users.get(position);
        holder.name.setText(user.getLogin());
        holder.topShadow.setVisibility(position == 0 ? VISIBLE : GONE);
        holder.url.setText(user.getUrl());
        Picasso.with(context)
                .load(user.getAvatarWithSize(User.SMALL_AVATAR_SIZE))
                .placeholder(R.drawable.avatar_placeholder)
                .into(holder.photo);
    }

    public void setData(List<User> data) {
        users = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.photo)
        public ImageView photo;
        @InjectView(R.id.name)
        public TextView name;
        @InjectView(R.id.shadow)
        public View topShadow;
        @InjectView(R.id.url)
        public TextView url;

        public ViewHolder(View item) {
            super(item);
            ButterKnife.inject(this, item);
        }
    }

}
