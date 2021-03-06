package com.io.qiushi.ui.image;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.io.qiushi.R;
import com.io.qiushi.bean.Image;
import com.io.qiushi.ui.commom.adapter.OnItemClicklistener;
import com.io.qiushi.ui.commom.adapter.SlideInItemAnimator;
import com.io.qiushi.ui.imagedetails.ImageDetailsActivity;
import com.io.qiushi.util.NetUtils;
import com.io.qiushi.widget.LoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageActivity extends AppCompatActivity implements ImageContract.View, SwipeRefreshLayout
        .OnRefreshListener, LoadMoreRecyclerView.OnLoadMoreListener, OnItemClicklistener {
    private static final String TAG = "ImageActivity";
    private final int RV_COLUMN = 2;

    private Context mContext;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout srlRefresh;
    @BindView(R.id.rv_data)
    LoadMoreRecyclerView rvData;
    @BindView(R.id.pb_loading)
    ProgressBar pbLoading;

    ImageContract.Presenter mPresenter;
    private int page = 1;

    ImageAdapter2 adapter;
    GridLayoutManager gridLayoutManager;
    StaggeredGridLayoutManager mStaggeredGridLayoutManager;

    private List<Image> tempList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        mContext = this;
        ButterKnife.bind(this);
        mPresenter = new ImagePresenter(mContext, this);
        initView();
        initData();
    }

    private void initView() {
        srlRefresh.setOnRefreshListener(this);
        gridLayoutManager = new GridLayoutManager(mContext, RV_COLUMN);
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvData.setLayoutManager(mStaggeredGridLayoutManager);
        rvData.setOnLoadMoreListener(this);
        rvData.setItemAnimator(new SlideInItemAnimator());

        //preven recyclerview scroll top happen animation
//        mStaggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        rvData.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                mStaggeredGridLayoutManager.invalidateSpanAssignments();
            }
        });
    }

    private void initData() {
        adapter = new ImageAdapter2(mContext, null);
        rvData.setAdapter(adapter);
        mPresenter.getData(page);
    }

    @Override
    public void setData(List<Image> list) {
        Log.e(TAG, "size>>" + list.size());
        tempList.addAll(list);
        if (adapter.getItemCount() <= 0) {
            adapter = new ImageAdapter2(mContext, tempList);
            rvData.setAdapter(adapter);
        } else {
            adapter.notifyItemRangeInserted(adapter.getItemCount(), list.size());
            rvData.setFinishLoading();
        }
        adapter.setOnItemClicklistener(this);
    }

    @Override
    public void OnItemClick(int position, View view) {
        if (tempList == null || tempList.isEmpty()) {
            return;
        }
        Intent intent = new Intent(ImageActivity.this, ImageDetailsActivity.class);
        intent.putExtra("url", tempList.get(position).getSrc());
        startActivity(intent);
    }

    @Override
    public void noMoreData() {

    }

    @Override
    public void onRefresh() {
        rvData.setFinishLoading();
        tempList = new ArrayList<>();
        adapter = new ImageAdapter2(mContext, null);
        page = 1;
        mPresenter.getData(page);
    }

    @Override
    public void onLoadMore() {
        if (!NetUtils.isNetworkAvailable(mContext)) {
            networkError();
            return;
        }
        adapter.startLoading();

        // set loading more layout is single layout
//        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                if (position == adapter.getLoadingItemPosition()) {
//                    return RV_COLUMN;
//                }
//                return 1;
//            }
//        });

        page++;
        mPresenter.getData(page);
        Log.e(TAG, "page= " + page);
    }

    @Override
    public void setPresenter(ImageContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setLoading() {
        srlRefresh.setRefreshing(true);
    }

    @Override
    public void setLoaded() {
        srlRefresh.setRefreshing(false);
    }

    @Override
    public void networkError() {
        Toast.makeText(mContext, "无网络", Toast.LENGTH_SHORT).show();
        srlRefresh.setRefreshing(false);
        adapter.finishLoading();
        rvData.setFinishLoading();
    }

    @Override
    public void serverError() {
        srlRefresh.setRefreshing(false);
        adapter.finishLoading();
        rvData.setFinishLoading();
    }

}
