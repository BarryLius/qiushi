package com.io.qiushi.ui.image;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.io.qiushi.R;
import com.io.qiushi.bean.Image;
import com.io.qiushi.util.GifBadge;

import java.util.List;

/**
 * Created by mhl on 2017/3/15.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private Context mContext;
    private List<Image> list;
    GifBadge gifBadge;
    GifDrawable gif = null;

    public ImageAdapter(Context context, List<Image> list) {
        mContext = context;
        this.list = list;
        gifBadge = new GifBadge(mContext);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_image, parent, false);
        ViewHolder mViewHolder = new ViewHolder(view);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        int[] array = new int[]{
                R.color.accent,
                R.color.primary_text
        };

        if (position % 3 == 0) {
            holder.mImageView.setBackground(mContext.getDrawable(array[0]));
        } else {
            holder.mImageView.setBackground(mContext.getDrawable(R.color.icons));
        }
        Glide.with(mContext)
                .load(list.get(position).getSrc())
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean
                            isFirstResource) {
                        return false;
                    }

                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable>
                            target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if (resource instanceof GifDrawable) {
                            holder.mImageView.setForeground(gifBadge);
                            holder.mImageView.setForegroundGravity(Gravity.RIGHT | Gravity.BOTTOM);
                        }

                        return false;
                    }
                })
                .override(800, 600)
                .crossFade()
//                .into(holder.mImageView);
                .into(new GlideDrawableImageViewTarget(holder.mImageView) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable>
                            animation) {
                        super.onResourceReady(resource, animation);
                        resource.stop();
                    }
                });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.image);

            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mImageView.getDrawable() instanceof GifDrawable) {
                        gif = (GifDrawable) mImageView.getDrawable();
                        if (gif.isRunning()) {
                            gif.stop();
                        } else {
                            gif.start();
                        }
                    }
                }
            });

//            mImageView.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    if (mImageView.getDrawable() instanceof GifDrawable) {
//                        gif = (GifDrawable) mImageView.getDrawable();
//                        switch (event.getAction()) {
//                            case MotionEvent.ACTION_DOWN:
//                                gif.start();
//                                break;
//                            case MotionEvent.ACTION_UP:
//                            case MotionEvent.ACTION_CANCEL:
//                                gif.stop();
//                                break;
//                        }
//                    }
//                    return false;
//                }
//            });
        }
    }
}
