package com.example.dirong.githubusers.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.example.dirong.githubusers.R;
import com.example.dirong.githubusers.model.PhotoViewData;
import com.example.dirong.githubusers.network.User;
import com.example.dirong.githubusers.ui.SimpleAnimatorListener;
import com.example.dirong.githubusers.ui.widget.PhotoViewWrapper;
import com.example.dirong.githubusers.ui.widget.imagetouchview.ImageViewTouch;
import com.example.dirong.githubusers.ui.widget.imagetouchview.ImageViewTouchBase;
import com.nineoldandroids.animation.Animator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by dirong on 11/6/14.
 */
public class PhotoViewActivity extends Activity {

    public static final String PHOTO_VIEW_DATA = "photo_view_data";

    @InjectView(R.id.image_wrapper)
    PhotoViewWrapper imageWrapper;
    @InjectView(R.id.photo)
    ImageViewTouch image;
    @InjectView(R.id.progress_bar)
    View progress;

    private PhotoViewData photoViewData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_view_layout);
        ButterKnife.inject(this);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        restoreData(savedInstanceState);
        imageWrapper.init(photoViewData.getViewRect(), null);
        imageWrapper.setShowAnimatorListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                progress.setVisibility(View.VISIBLE);
                Picasso.with(PhotoViewActivity.this)
                        .load(photoViewData.getUser().getAvatarWithSize(User.NORMAL_AVATAR_SIZE))
                        .noFade()
                        .noPlaceholder()
                        .into(image, new Callback() {
                            @Override
                            public void onSuccess() {
                                progress.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                progress.setVisibility(View.GONE);
                            }
                        });
            }
        });
        if (savedInstanceState != null) {
            imageWrapper.setNeedZoom(false);
        }
        image.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        Picasso.with(PhotoViewActivity.this).load(photoViewData.getUser().getAvatarWithSize(User.SMALL_AVATAR_SIZE)).into(image);
    }

    private void restoreData(Bundle state) {
        if (state == null) {
            state = getIntent().getExtras();
        }
        photoViewData = state.getParcelable(PHOTO_VIEW_DATA);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(PHOTO_VIEW_DATA, photoViewData);
    }
}
