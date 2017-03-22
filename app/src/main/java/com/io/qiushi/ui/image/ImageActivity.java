package com.io.qiushi.ui.image;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.io.qiushi.R;
import com.io.qiushi.bean.Image;
import com.io.qiushi.ui.commom.adapter.OnItemClicklistener;
import com.io.qiushi.ui.commom.adapter.SlideInItemAnimator;
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
        adapter.setOnItemClicklistener(this);
        gridLayoutManager = new GridLayoutManager(mContext, RV_COLUMN);
        rvData.setLayoutManager(gridLayoutManager);
        rvData.setOnLoadMoreListener(this);
        rvData.setItemAnimator(new SlideInItemAnimator());
        rvData.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (gridLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                    toolbar.setTranslationZ(0f);
                } else {
                    toolbar.setTranslationZ(10f);
                }
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
    }

    //adapter item click
    @Override
    public void OnItemClick(int position, View view) {

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

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == adapter.getLoadingItemPosition()) {
                    return RV_COLUMN;
                }
                return 1;
            }
        });

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
