package com.yiqiji.money.modules.community.travel.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.TypedValue;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yiqiji.frame.core.facade.SimpleMsg;
import com.yiqiji.frame.core.facade.ViewCallBack;
import com.yiqiji.money.R;
import com.yiqiji.money.modules.common.activity.BaseActivity;
import com.yiqiji.money.modules.common.widget.BaseRecylerview;
import com.yiqiji.money.modules.community.travel.adapter.HotDestinationsAdapter;
import com.yiqiji.money.modules.community.travel.manager.DestinationGuideDataModleManager;
import com.yiqiji.money.modules.community.travel.manager.LoadRefreshManager;
import com.yiqiji.money.modules.community.travel.manager.TravelDataManager;
import com.yiqiji.money.modules.community.travel.model.HotDestinationsInfo;
import com.yiqiji.money.modules.community.travel.model.HotPlace;
import com.yiqiji.money.modules.community.travel.view.HotDestinationsHeadView;
import com.yiqiji.money.modules.community.view.RefreshLoadMoreView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ${huangweishui} on 2017/8/2.
 * address huang.weishui@71dai.com
 */
public class HotDestinationsActivity extends BaseActivity {
    @BindView(R.id.refreshLoadMoreView)
    RefreshLoadMoreView refreshLoadMoreView;//
    @BindView(R.id.srl_main)
    SwipeRefreshLayout srl_main;//
    @BindView(R.id.list_hot)
    BaseRecylerview listHot;//
    private Unbinder unbinder;
    private HotDestinationsAdapter hotDestinationsAdapter;
    private HotDestinationsHeadView hotDestinationsHeadView;
    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_destinations_layout);
        unbinder = ButterKnife.bind(this);
        getIntentInfo();
        initAdapter();
        initDtata(true);
    }

    private void getIntentInfo() {
        city = getIntent().getStringExtra("city");
    }

    private void initAdapter() {
        hotDestinationsAdapter = new HotDestinationsAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        listHot.setLayoutManager(linearLayoutManager);
        hotDestinationsHeadView = new HotDestinationsHeadView(this);
        hotDestinationsAdapter.addHeaderView(hotDestinationsHeadView);
        refreshLoadMoreView.setAdapter(hotDestinationsAdapter);
        refreshLoadMoreView.setCanRefresh(false);
        refreshLoadMoreView.setCanLoadMore(false);
        refreshLoadMoreView.setLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                initDtata(false);
            }
        });
        refreshLoadMoreView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initDtata(true);
            }
        });

//        listHot.setAdapter(hotDestinationsAdapter);
//
//        hotDestinationsAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
//            @Override
//            public void onLoadMoreRequested() {
//                loading();
//                initDtata(false);
//            }
//        }, listHot);
//        hotDestinationsAdapter.disableLoadMoreIfNotFullPage();//默认第一次加载会进入回调，这里设置第一次不需要回调
//        // 这句话是为了，第一次进入页面的时候显示加载进度条
//        srl_main.setProgressViewOffset(false, 0, (int) TypedValue
//                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
//                        .getDisplayMetrics()));
//        srl_main.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                refreshing();
//                initDtata(true);
//            }
//        });
    }

    private void initDtata(final boolean isRefresh) {
        TravelDataManager.getDestinationData(city, new ViewCallBack<HotDestinationsInfo.DataBean>() {
            @Override
            public void onStart() {
                super.onStart();
                showLoadingDialog();
            }

            @Override
            public void onSuccess(HotDestinationsInfo.DataBean dataBean) throws Exception {
                super.onSuccess(dataBean);
                HotPlace hotPlace = dataBean.getPlace();
                hotDestinationsHeadView.setHotDestinationsHeadImageView(hotPlace.getImg());
                hotDestinationsHeadView.setCity(hotPlace.getTitle());
                hotDestinationsHeadView.setDestinationGuideView(dataBean.getColumn(), hotPlace.getTitle());
                if (isRefresh) {
                    hotDestinationsAdapter.setDataList(DestinationGuideDataModleManager.getHotDestinationsMultiitem(dataBean.getHotbook()));
                } else {
                    hotDestinationsAdapter.addData(DestinationGuideDataModleManager.getHotDestinationsMultiitem(dataBean.getHotbook()));
                }

            }

            @Override
            public void onFailed(SimpleMsg simleMsg) {
                super.onFailed(simleMsg);
                showToast(simleMsg);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissDialog();
//                compelet();
                refreshLoadMoreView.compelet();
            }
        });

    }

    public void refreshing() {
        if (hotDestinationsAdapter != null) {
            hotDestinationsAdapter.setEnableLoadMore(false);
        }
    }

    public void loading() {
        if (srl_main != null) {
            srl_main.setRefreshing(true);
        }
    }

    public void compelet() {
        if (srl_main != null) {
            srl_main.setRefreshing(false);
        }
        if (hotDestinationsAdapter != null) {
            hotDestinationsAdapter.setEnableLoadMore(true);
            hotDestinationsAdapter.loadMoreComplete();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
