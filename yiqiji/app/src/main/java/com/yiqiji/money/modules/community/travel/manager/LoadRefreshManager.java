package com.yiqiji.money.modules.community.travel.manager;

import android.support.v4.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;

/**
 * Created by huangweishui on 2017/10/16.
 */

public class LoadRefreshManager {


    public static void refreshing(BaseQuickAdapter baseQuickAdapter) {
        if (baseQuickAdapter != null) {
            baseQuickAdapter.setEnableLoadMore(false);
        }
    }

    public static void loading(SwipeRefreshLayout swipeRefreshLayout) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void compelet(SwipeRefreshLayout swipeRefreshLayout,BaseQuickAdapter baseQuickAdapter) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }
        if (baseQuickAdapter != null) {
            baseQuickAdapter.setEnableLoadMore(true);
        }
    }
}
