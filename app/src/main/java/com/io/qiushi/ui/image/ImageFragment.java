package com.io.qiushi.ui.image;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class ImageFragment extends Fragment implements ImageContract.View, SwipeRefreshLayout.OnRefreshListener,
        LoadMoreRecyclerView.OnLoadMoreListener, OnItemClicklistener {
    private View rootView;
    private final int RV_COLUMN = 2;

    private Context mContext;
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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public ImageFragment() {
        // Required empty public constructor
    }

    public static ImageFragment newInstance(String param1, String param2) {
        ImageFragment fragment = new ImageFragment();
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
        rootView = inflater.inflate(R.layout.fragment_image, container, false);
        mContext = getContext();
        ButterKnife.bind(this, rootView);
        mPresenter = new ImagePresenter(mContext, this);
        initView();
        initData();
        return rootView;
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
        Intent intent = new Intent(getActivity(), ImageDetailsActivity.class);
        intent.putExtra("url", tempList.get(position).getSrc());
        startActivity(intent);
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void noMoreData() {

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
