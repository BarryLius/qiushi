package com.io.qiushi.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;


/**
 * Created by mhl on 2017/3/17.
 */

public class LoadMoreRecyclerView extends RecyclerView {
    /**
     * whether loading
     */
    private boolean isLoading = false;

    LayoutManager layout;

    OnLoadMoreListener mOnLoadMoreListener;

    public LoadMoreRecyclerView(Context context) {
        super(context);
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
    }

    /**
     * loading success
     */
    public void setFinishLoading() {
        isLoading = false;
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        this.layout = layout;
    }


    @Override
    public LinearLayoutManager getLayoutManager() {
        if (layout instanceof GridLayoutManager) {
            return (GridLayoutManager) layout;
        } else if (layout instanceof LinearLayoutManager) {
            return (LinearLayoutManager) layout;
        }
        return null;
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);

        if (dy < 0 || isLoading)
            return;
        if (getLayoutManager() != null && getLayoutManager() instanceof GridLayoutManager) {
            GridLayoutManager mGridLayoutManager = (GridLayoutManager) layout;

            int visibleItemCount = this.getChildCount();
            int totalItemCount = mGridLayoutManager.getItemCount();
            int firstVisibleItem = mGridLayoutManager.findFirstVisibleItemPosition();

            if ((totalItemCount - visibleItemCount) <= (firstVisibleItem)) {
                if (!isLoading) {
                    isLoading = true;
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                }
            }
        } else if (getLayoutManager() != null && getLayoutManager() instanceof LinearLayoutManager) {
            //TODO LinearLayout load more
        }
    }

    public void setOnLoadMoreListener(@NonNull OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        public void onLoadMore();
    }
}
