package com.io.qiushi.ui.image;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

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
 * Created by liuwei on 17/3/15.
 */

public class ImageAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_CONTENT = 1;
    private static final int TYPE_FOOTER = 2;
    private Context mContext;
    private List<Image> list;
    private boolean showLoadMore = false;
    GifBadge gifBadge;
    GifDrawable gif = null;

    public ImageAdapter2(Context context, List<Image> list) {
        mContext = context;
        this.list = list;
        gifBadge = new GifBadge(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_CONTENT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_image, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_loading, parent, false);
            LoadingMoreHolder loadingMoreHolder = new LoadingMoreHolder(view);
            return loadingMoreHolder;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size() + (showLoadMore ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        if (list.size() > 0 && list.size() > position) {
            return TYPE_CONTENT;
        }
        return TYPE_FOOTER;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_CONTENT:
                bindViewHolders((ViewHolder) holder, position);
                break;
            case TYPE_FOOTER:
                bindLoadingHolder((LoadingMoreHolder) holder, position);
                break;
        }
    }

    public void startLoading() {
        if (showLoadMore)
            return;
        showLoadMore = true;
        notifyItemInserted(getLoadingItemPosition());
    }

    public void finishLoading() {
        if (!showLoadMore)
            return;
        notifyItemRemoved(getLoadingItemPosition() + 1);
        showLoadMore = false;
    }

    public int getLoadingItemPosition() {
        return showLoadMore ? getItemCount() - 1 : RecyclerView.NO_POSITION;
    }

    private void bindViewHolders(final ViewHolder holder, final int position) {
        int[] array = new int[]{
                R.color.black,
                R.color.divider
        };

        if (position % 3 == 0) {
            holder.mImageView.setBackground(mContext.getDrawable(array[0]));
        } else {
            holder.mImageView.setBackground(mContext.getDrawable(array[1]));
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
                .placeholder(new ColorDrawable(array[0]))
                .crossFade()
                .into(new GlideDrawableImageViewTarget(holder.mImageView) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable>
                            animation) {
                        super.onResourceReady(resource, animation);
//                        resource.stop();
                    }
                });

    }

    private void bindLoadingHolder(LoadingMoreHolder holder, int position) {
        holder.progress.setVisibility(View.VISIBLE);
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
        }
    }


    class LoadingMoreHolder extends RecyclerView.ViewHolder {
        ProgressBar progress;

        LoadingMoreHolder(View itemView) {
            super(itemView);
            progress = (ProgressBar) itemView.findViewById(R.id.pb_loading);
        }
    }
}
