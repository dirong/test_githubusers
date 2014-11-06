package com.example.dirong.githubusers.ui.widget;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.example.dirong.githubusers.ui.SimpleAnimatorListener;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

public class PhotoViewWrapper extends FrameLayout {

    private View photoView;
    private Animator currentAnimator;
    private Rect startBounds;
    private Rect finalBounds;
    private float startScale;
    private static final long END_FADE_OUT_DURATION = 40;
    public static final long SHOW_IMAGE_DURATION = 200;
    private OnKeyListener onKeyListener;
    private boolean needZoom = true;
    private SimpleAnimatorListener showAnimatorListener;
    private SimpleAnimatorListener hideAnimatorListener;

    public PhotoViewWrapper(Context context, View view) {
        super(context);
        addView(view);
    }

    public PhotoViewWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PhotoViewWrapper(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(Rect startBounds, OnKeyListener onKeyListener) {
        this.startBounds = startBounds;
        this.onKeyListener = onKeyListener;
        if (getChildCount() == 0) {
            throw new IllegalAccessError("Child count must be 1");
        }
        post(new Runnable() {
            @Override
            public void run() {
                photoView = getChildAt(0);
                setupBackListener();
                ViewHelper.setAlpha(PhotoViewWrapper.this, 0);
                showGallery();
            }
        });
    }

    public void setNeedZoom(boolean needZoom) {
        this.needZoom = needZoom;
    }

    private void setupBackListener() {
        setFocusableInTouchMode(true);
        requestFocus();
        setOnKeyListener(new View.OnKeyListener() {
            public boolean keyCodePressed;

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (onKeyListener != null) {
                    onKeyListener.onKey(v, keyCode, event);
                }
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (keyCodePressed) {
                        return false;
                    }
                    keyCodePressed = true;
                    hideGallery();
                }
                return false;
            }
        });
    }

    private void showGallery() {
        ViewHelper.setAlpha(this, 1);
        if (needZoomImage()) {
            zoomInImage();
        } else {
            fadeInImage();
        }
    }

    private void fadeInImage() {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(photoView, "alpha", 0, 1);
        alpha.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (showAnimatorListener != null) {
                    showAnimatorListener.onAnimationEnd(animation);
                }
            }
        });
        alpha.setDuration(SHOW_IMAGE_DURATION).start();

    }

    private void zoomInImage() {
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

        finalBounds = new Rect();
        final Point globalOffset = new Point();

        getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        ViewHelper.setPivotX(photoView, 0f);
        ViewHelper.setPivotY(photoView, 0f);

        animateZoomIn();
    }

    private void animateZoomIn() {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(photoView, "translationX", startBounds.left, finalBounds.left),
                ObjectAnimator.ofFloat(photoView, "translationY", startBounds.top, finalBounds.top),
                ObjectAnimator.ofFloat(photoView, "scaleX", startScale, 1f),
                ObjectAnimator.ofFloat(photoView, "scaleY", startScale, 1f));
        set.setDuration(SHOW_IMAGE_DURATION);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                finishAnimation();
                if (showAnimatorListener != null) {
                    showAnimatorListener.onAnimationEnd(animation);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                finishAnimation();
            }
        });
        set.start();
        currentAnimator = set;
    }

    private void finishAnimation() {
        currentAnimator = null;
    }

    private void hideGallery() {
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

        if (needZoomImage()) {
            currentAnimator = zoomOutImage();
        } else {
            currentAnimator = fadeOutImage();
        }

        if (hideAnimatorListener != null) {
            hideAnimatorListener.onAnimationStart(null);
            currentAnimator.setStartDelay(100);
        }
        currentAnimator.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (hideAnimatorListener != null) {
                    hideAnimatorListener.onAnimationEnd(animation);
                }
            }
        });
        currentAnimator.start();
    }

    private boolean needZoomImage() {
        if (!needZoom) {
            return false;
        }
        double maxWidth = photoView.getWidth() * 1.2;
        double minWidth = photoView.getWidth() * 0.8;
        if (startBounds == null) {
            return false;
        }
        int rectWidth = startBounds.width();
        boolean fullScreenImage = rectWidth < maxWidth && rectWidth > minWidth;
        return !fullScreenImage;
    }

    private Animator fadeOutImage() {
        AnimatorSet set = new AnimatorSet();
        set.setDuration(SHOW_IMAGE_DURATION);
        set.playTogether(
                ObjectAnimator.ofFloat(photoView, "alpha", 1, 0));
        return set;
    }

    private Animator zoomOutImage() {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(photoView, "translationX", startBounds.left),
                ObjectAnimator.ofFloat(photoView, "translationY", startBounds.top),
                ObjectAnimator.ofFloat(photoView, "scaleX", startScale),
                ObjectAnimator.ofFloat(photoView, "scaleY", startScale),
                getEndFadeout(photoView));
        set.setDuration(SHOW_IMAGE_DURATION);
        set.setInterpolator(new DecelerateInterpolator());
        return set;
    }

    private ObjectAnimator getEndFadeout(View view) {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1, 0);
        fadeOut.setDuration(END_FADE_OUT_DURATION);
        fadeOut.setStartDelay(SHOW_IMAGE_DURATION - END_FADE_OUT_DURATION);
        return fadeOut;
    }

    public void setShowAnimatorListener(SimpleAnimatorListener showAnimatorListner) {
        this.showAnimatorListener = showAnimatorListner;
    }

    public void setHideAnimatorListener(SimpleAnimatorListener hideAnimatorListner) {
        this.hideAnimatorListener = hideAnimatorListner;
    }
}
