package cn.synway.app.ui.main.publicappcenter;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.synway.app.R;
import cn.synway.app.bean.NetAPPBO;
import cn.synway.app.bean.event.KeepEvent;
import cn.synway.app.bean.event.MainMenuEvent;
import cn.synway.app.bean.event.MessageRefreshEvent;
import cn.synway.app.bean.event.UpdateEvent;
import cn.synway.app.config.Config;
import cn.synway.app.mvp.MVPBaseFragment;
import cn.synway.app.ui.search.SearchActivity;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class PublicAppCenterFragment extends MVPBaseFragment<PublicAppCenterContract.View, PublicAppCenterPresenter> implements PublicAppCenterContract.View {
    @BindView(R.id.menu)
    ImageView mMenu;
    @BindView(R.id.title_text)
    TextView mTitleText;
    @BindView(R.id.search)
    ImageView mSearch;
    @BindView(R.id.app_center_rv)
    RecyclerView mAppCenterRv;
    @BindView(R.id.refresh)
    SwipeRefreshLayout mRefresh;
    @BindView(R.id.today_point)
    TextView todayPoint;
    @BindView(R.id.keep_hint)
    TextView keepHint;
    private Unbinder unbinder;
    private AppCenterGridAdapter gridAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fra_public_app_center, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTitleText.setText(StringUtils.isEmpty(Config.AppCenterName) ? "应用中心" : Config.AppCenterName);
        initView();

    }

    /**
     * PublicAccountItemInterface 事件处理者
     */
    private void initView() {
        gridAdapter = new AppCenterGridAdapter(getActivity(), new PublicAccountItemInterface());
        mAppCenterRv.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);

        gridLayoutManager.setSpanSizeLookup(new SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int viewType = gridAdapter.getItemViewType(position);
                switch (viewType) {
                    case 1://标题
                        return 4;
                    default:
                        return 1;
                }
            }
        });

        mAppCenterRv.setLayoutManager(gridLayoutManager);
        mAppCenterRv.setAdapter(this.gridAdapter);

        mRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getData();
            }
        });
        mSearch.setVisibility(View.GONE);
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();

    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.getData();
    }

    /**
     * 获取到数据
     *
     * @param list
     */
    @Override
    public void setNetAPPData(List<NetAPPBO> list) {
        gridAdapter.changeData(list);
        mRefresh.setRefreshing(false);
    }

    /**
     * 获取数据失败
     */
    @Override
    public void onFaild() {
        mRefresh.setRefreshing(false);
    }

    /**
     * 接收到消息通知,刷新
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageRefreshEvent event) {
        mPresenter.getData();
    }


    /**
     * 是否有新版本更新
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateEvent(UpdateEvent event) {
        todayPoint.setVisibility(View.VISIBLE);
    }


    /**
     * 长连接是否断开
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void connectEvent(KeepEvent event) {
        keepHint.setVisibility(View.GONE);
//        if (event.isContect) {
//            keepHint.setVisibility(View.GONE);
//        } else {
//            keepHint.setVisibility(View.VISIBLE);
//        }
    }

    /**
     * 菜单
     */
    @OnClick({R.id.menu, R.id.search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.menu:
                EventBus.getDefault().post(new MainMenuEvent());
                break;
            case R.id.search:
                gotoActivity(SearchActivity.class, false);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }


}
