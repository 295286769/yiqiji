package com.yiqiji.money.modules.community.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yiqiji.money.R;
import com.yiqiji.money.modules.common.widget.BaseRecylerview;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by huangweishui on 2017/10/16.
 */

public class RefreshLoadMoreView extends LinearLayout {
    private Context context;
    SwipeRefreshLayout srl_main;//
    BaseRecylerview listHot;//
    private BaseQuickAdapter baseQuickAdapter;
    private boolean canRefresh = true;//是否开启刷新
    private boolean canLoadMore = true;//是否开启上拉加载

    public RefreshLoadMoreView(Context context) {
        this(context, null);
    }

    public RefreshLoadMoreView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshLoadMoreView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.refresh_load_more_view, this, true);
        srl_main = (SwipeRefreshLayout) view.findViewById(R.id.srl_main);
        listHot = (BaseRecylerview) view.findViewById(R.id.list_hot);
        initRefresh();
        initRecylerView();

    }

    /**
     * 初始化SwipeRefreshLayout
     */
    private void initRefresh() {
        srl_main.setEnabled(canRefresh);
        // 这句话是为了，第一次进入页面的时候显示加载进度条
        srl_main.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));

    }

    /**
     * 初始化recylerview
     */
    private void initRecylerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        listHot.setLayoutManager(linearLayoutManager);
    }

    /**
     * RecylerView 设置Manager
     *
     * @param layoutManager
     */
    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        if (listHot != null) {
            listHot.setLayoutManager(layoutManager);
        }
    }

    /**
     * 下拉刷新
     *
     * @param refreshListener
     */
    public void setRefreshListener(final SwipeRefreshLayout.OnRefreshListener refreshListener) {
        if (srl_main != null) {
            srl_main.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    refreshing();
                    refreshListener.onRefresh();
                }
            });
        }
    }

    /**
     * 上拉加载
     *
     * @param loadMoreListener
     */
    public void setLoadMoreListener(final BaseQuickAdapter.RequestLoadMoreListener loadMoreListener) {
        if (baseQuickAdapter != null && listHot != null) {
            baseQuickAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
                @Override
                public void onLoadMoreRequested() {
                    loading();
                    loadMoreListener.onLoadMoreRequested();
                }
            }, listHot);
            baseQuickAdapter.disableLoadMoreIfNotFullPage();//默认第一次加载会进入回调，这里设置第一次不需要回调
        }

    }

    public void setCanRefresh(boolean canRefresh) {
        this.canRefresh = canRefresh;
        if (srl_main != null) {
            srl_main.setEnabled(canRefresh);
        }

    }

    public void setCanLoadMore(boolean canLoadMore) {
        this.canLoadMore = canLoadMore;
        if (baseQuickAdapter != null) {
            baseQuickAdapter.setEnableLoadMore(canLoadMore);
        }

    }

    public void setAdapter(BaseQuickAdapter adapter) {
        if (baseQuickAdapter == null) {
            baseQuickAdapter = adapter;
        }
        if (listHot != null) {
            listHot.setAdapter(adapter);
        }


    }

    /**
     * 刷新中
     */
    public void refreshing() {
        if (baseQuickAdapter != null && canRefresh) {
            baseQuickAdapter.setEnableLoadMore(false);
        }

    }

    /**
     * 加载中
     */
    public void loading() {
        if (srl_main != null && canLoadMore) {
            srl_main.setEnabled(false);
        }
    }

    /**
     * 刷新或加载完成
     */
    public void compelet() {
        if (srl_main != null && canRefresh) {
            srl_main.setEnabled(true);
            srl_main.setRefreshing(false);
        }
        if (baseQuickAdapter != null && canLoadMore) {
            baseQuickAdapter.setEnableLoadMore(true);
            baseQuickAdapter.loadMoreComplete();
        }
    }

    /**
     * 数据全部加载完毕
     */
    public void setLoadMoreEnd() {
        if (baseQuickAdapter != null) {
            baseQuickAdapter.loadMoreEnd();
        }
    }

    /**
     * 数据加载失败
     */
    public void setLoadMoreFail() {
        if (baseQuickAdapter != null) {
            baseQuickAdapter.loadMoreFail();
        }
    }
}
