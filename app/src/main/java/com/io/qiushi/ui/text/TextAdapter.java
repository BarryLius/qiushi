package com.io.qiushi.ui.text;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.io.qiushi.R;
import com.io.qiushi.bean.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mhl on 2017/3/29.
 */

public class TextAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_CONTENT = 0;
    private static final int TYPE_FOOTER = 1;
    private boolean showLoadMore = false;

    private Context mContext;
    private List<Text> list;

    public TextAdapter(Context context, List<Text> list) {
        mContext = context;
        this.list = list;
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_CONTENT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_text, parent, false);
            return new ViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_loading, parent, false);
            return new LoadingMoreHolder(view);
        }
        return null;
    }

    public void startLoading() {
        if (showLoadMore) {
            return;
        }
        showLoadMore = true;
        notifyItemInserted(getLoadingItemPosition());
    }

    public void finishLoading() {
        if (!showLoadMore) {
            return;
        }
        notifyItemRemoved(getLoadingItemPosition() + 1);
        showLoadMore = false;
    }

    public int getLoadingItemPosition() {
        return showLoadMore ? getItemCount() - 1 : RecyclerView.NO_POSITION;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_CONTENT:
                bindViewHolders((ViewHolder) holder, position);
                break;
            case TYPE_FOOTER:
                bindLoadingViewHolder((LoadingMoreHolder) holder, position);
                break;
        }
    }

    private void bindViewHolders(RecyclerView.ViewHolder holder, int position) {
        ViewHolder holders = (ViewHolder) holder;
        holders.tvTitle.setText(list.get(position).getTitle());
        holders.tvTime.setText(list.get(position).getTime());
        holders.tvContent.setText(list.get(position).getContent());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void bindLoadingViewHolder(RecyclerView.ViewHolder holder, int position) {
        LoadingMoreHolder holders = (LoadingMoreHolder) holder;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_content)
        TextView tvContent;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class LoadingMoreHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.pb_loading)
        ProgressBar progress;

        public LoadingMoreHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
