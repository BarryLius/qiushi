package com.io.qiushi.ui.image;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.io.qiushi.R;
import com.io.qiushi.bean.Image;
import com.io.qiushi.ui.commom.adapter.OnItemClicklistener;
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
    private final ColorDrawable[] loadingBackground;
    GifBadge gifBadge;
    GifDrawable gif = null;
    OnItemClicklistener mOnItemClicklistener;

    public ImageAdapter2(Context context, List<Image> list) {
        mContext = context;
        this.list = list;
        gifBadge = new GifBadge(mContext);

        int[] colors = mContext.getResources().getIntArray(R.array.colors);
        loadingBackground = new ColorDrawable[colors.length];
        for (int i = 0; i < colors.length; i++) {
            loadingBackground[i] = new ColorDrawable(colors[i]);
        }
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

    /**
     * add loading
     */
    public void startLoading() {
        //prevent duplication insret loading view
        if (showLoadMore) {
            return;
        }
        showLoadMore = true;
        notifyItemInserted(getLoadingItemPosition());
    }

    /**
     * remove loading
     */
    public void finishLoading() {
        //prevent duplication remove loading view
        if (!showLoadMore) {
            return;
        }
        notifyItemRemoved(getLoadingItemPosition() + 1);
        showLoadMore = false;
    }

    public int getLoadingItemPosition() {
        return showLoadMore ? getItemCount() - 1 : RecyclerView.NO_POSITION;
    }

    private void bindViewHolders(final ViewHolder holder, final int position) {
        holder.title.setText("");
        holder.mImageView.setBackground(loadingBackground[position % loadingBackground.length]);

        Glide.with(mContext)
                .load(list.get(position).getSrc())
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean
                            isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable>
                            target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if (resource instanceof GifDrawable) {
                        } else {
                        }
                        return false;
                    }
                })
//                .override(800, 600)
                .placeholder(loadingBackground[position % loadingBackground.length])
                .error(R.mipmap.ic_launcher)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new GlideDrawableImageViewTarget(holder.mImageView) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable>
                            animation) {
                        super.onResourceReady(resource, animation);
//                        resource.stop();
                        ViewGroup.LayoutParams para = holder.mImageView.getLayoutParams();
                        para.height = resource.getIntrinsicWidth();
                        para.width = WindowManager.LayoutParams.MATCH_PARENT;
                        holder.mImageView.setLayoutParams(para);
                        holder.mImageView.setImageDrawable(resource);

                        holder.title.setText(list.get(position).getTitle() == null ? "" : list.get(position).getTitle
                                ());
                    }
                });

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClicklistener != null) {
                    mOnItemClicklistener.OnItemClick(position, v);
                }
            }
        });

    }

    private void bindLoadingHolder(LoadingMoreHolder holder, int position) {
        //set loading more layout is single line
        StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) holder.itemView
                .getLayoutParams();
        p.setFullSpan(true);
    }

    public void setOnItemClicklistener(OnItemClicklistener mOnItemClicklistener) {
        this.mOnItemClicklistener = mOnItemClicklistener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
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
