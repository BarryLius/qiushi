package com.io.qiushi.ui.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.io.qiushi.R;
import com.io.qiushi.ui.commom.adapter.OnItemClicklistener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mhl on 2017/3/29.
 */

public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<String> list;
    OnItemClicklistener mOnItemClicklistener;

    public MainAdapter(Context context, List<String> list) {
        mContext = context;
        this.list = list;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_main_right, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        //set text,click
        ViewHolder holders = (ViewHolder) holder;
        holders.tvText.setText(list.get(position));

        holders.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClicklistener != null) {
                    mOnItemClicklistener.OnItemClick(position, v);
                }
            }
        });
    }

    public void setOnItemClicklistener(OnItemClicklistener mOnItemClicklistener) {
        this.mOnItemClicklistener = mOnItemClicklistener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_ico)
        ImageView ivIco;
        @BindView(R.id.tv_text)
        TextView tvText;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
