package com.io.qiushi.ui.imagedetails;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.io.qiushi.R;
import com.io.qiushi.util.image.ProgressModelLoader;
import com.io.qiushi.widget.RingProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ImageDetailsActivity extends AppCompatActivity {
    @BindView(R.id.image) ImageView imageView;
    @BindView(R.id.rpb_loading) RingProgressBar rpbLoading;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);

        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        url = getIntent().getStringExtra("url");
        if (url == null || url.equals("")) {
            return;
        }

        rpbLoading.setVisibility(View.VISIBLE);
        Glide.with(this)
                .using(new ProgressModelLoader(new ProgressHandler(rpbLoading)))
                .load(url)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        rpbLoading.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        rpbLoading.setVisibility(View.GONE);
                        return false;
                    }
                })
                .crossFade()
                .into(new GlideDrawableImageViewTarget(imageView) {
                    @Override
                    public void onStart() {
                        super.onStart();
                        Log.e("start", "");
                    }

                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        imageView.setImageDrawable(resource);
                    }

                });
    }

    private class ProgressHandler extends Handler {

        private RingProgressBar mProgressImageView;

        public ProgressHandler(RingProgressBar progressImageView) {
            super(Looper.getMainLooper());
            mProgressImageView = progressImageView;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (this != null && !ImageDetailsActivity.this.isFinishing()) {
                        int percent = msg.arg1 * 100 / msg.arg2;
                        mProgressImageView.setProgress(percent);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
