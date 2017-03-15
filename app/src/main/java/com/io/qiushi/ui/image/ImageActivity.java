package com.io.qiushi.ui.image;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.io.qiushi.R;
import com.io.qiushi.bean.Image;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageActivity extends AppCompatActivity implements ImageContract.View, SwipeRefreshLayout
        .OnRefreshListener {
    private static final String TAG = "ImageActivity";

    private Context mContext;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout srlRefresh;
    @BindView(R.id.rv_data)
    RecyclerView rvData;
    @BindView(R.id.pb_loading)
    ProgressBar pbLoading;

    ImageContract.Presenter mPresenter;
    private int page = 1;

    ImageAdapter adapter;

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
        rvData.setLayoutManager(new GridLayoutManager(mContext, 2));
    }

    private void initData() {
        adapter = new ImageAdapter(mContext, null);
        rvData.setAdapter(adapter);
        mPresenter.getData(page);
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
    public void noNetwork() {
        Toast.makeText(mContext, "无网络", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setData(List<Image> list) {
        Log.e(TAG, ">>" + list.size());
        adapter = new ImageAdapter(mContext, list);
        rvData.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {
        mPresenter.getData(page);
    }
}
