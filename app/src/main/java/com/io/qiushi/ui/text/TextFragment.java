package com.io.qiushi.ui.text;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.io.qiushi.R;
import com.io.qiushi.bean.Text;
import com.io.qiushi.ui.commom.adapter.SlideInItemAnimator;
import com.io.qiushi.widget.LoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TextFragment extends Fragment implements TextContract.View, SwipeRefreshLayout.OnRefreshListener,
        LoadMoreRecyclerView.OnLoadMoreListener {
    private View rootView;
    private Context mContext;
    @BindView(R.id.rv_data)
    LoadMoreRecyclerView rvData;
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout srlRefresh;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private int page = 1;
    private List<Text> tempList = new ArrayList<>();

    TextContract.Presenter mPresenter;
    TextAdapter adapter;

    public TextFragment() {
    }

    public static TextFragment newInstance(String param1, String param2) {
        TextFragment fragment = new TextFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_text, container, false);
        ButterKnife.bind(this, rootView);
        mContext = getContext();
        mPresenter = new TextPresenter(mContext, this);

        initView();
        initData();
        return rootView;
    }

    private void initView() {
        rvData.setLayoutManager(new LinearLayoutManager(mContext));
        rvData.setItemAnimator(new SlideInItemAnimator());
        srlRefresh.setOnRefreshListener(this);
        rvData.setOnLoadMoreListener(this);
        adapter = new TextAdapter(mContext, null);
        rvData.setAdapter(adapter);
    }

    private void initData() {
        mPresenter.getData(page);
    }

    @Override
    public void onRefresh() {
        rvData.setFinishLoading();
        tempList = new ArrayList<>();
        adapter = new TextAdapter(mContext, null);
        page = 1;
        mPresenter.getData(page);
    }

    @Override
    public void onLoadMore() {
        adapter.startLoading();
        page++;
        mPresenter.getData(page);
    }

    @Override
    public void setData(List<Text> list) {
        tempList.addAll(list);
        if (adapter.getItemCount() <= 0) {
            adapter = new TextAdapter(mContext, tempList);
            rvData.setAdapter(adapter);
        } else {
            adapter.notifyItemRangeInserted(adapter.getItemCount(), list.size());
            rvData.setFinishLoading();
        }

    }

    @Override
    public void setPresenter(TextContract.Presenter presenter) {
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

    }

    @Override
    public void serverError() {
        srlRefresh.setRefreshing(false);
        adapter.finishLoading();
        rvData.setFinishLoading();
    }

    @Override
    public void noMoreData() {
        srlRefresh.setRefreshing(false);
        adapter.finishLoading();
        rvData.setFinishLoading();
    }
}
