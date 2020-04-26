package cn.synway.app.ui.downmap;


import android.Manifest.permission;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ToastUtils;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import butterknife.BindView;
import cn.synway.app.R;
import cn.synway.app.bean.MyAreaDO;
import cn.synway.app.mvp.MVPBaseActivity;
import cn.synway.app.widget.lgrecycleadapter.OnRVItemClickListener;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class DownMapActivity extends MVPBaseActivity<DownMapContract.View, DownMapPresenter> implements DownMapContract.View {


    @BindView(R.id.title_bar)
    LinearLayout mToolbar;
    @BindView(R.id.dowm_map_rv)
    RecyclerView mDowmMapRv;

    private boolean hasPerMission;

    @Override
    protected int getLayout() {
        return R.layout.act_down_map;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public RecyclerView getDowmMapRv() {
        return mDowmMapRv;
    }

    private void init() {
        goBack();
        setTitleText("离线地图");
        requestPermissions();
        
        mDowmMapRv.setLayoutManager(new LinearLayoutManager(this));
        mDowmMapRv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        showProgress(null);
        mPresenter.getMapInfo();
    }

    private void requestPermissions() {
        Disposable subscribe = new RxPermissions(this)
                .requestEachCombined(permission.WRITE_EXTERNAL_STORAGE, permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) {
                        if (permission.granted) {
                            //全部同意后调用
                            DownMapActivity.this.hasPerMission = true;
                        }
                        else if (permission.shouldShowRequestPermissionRationale) {
                            //只要有一个选择：禁止，但没有选择“以后不再询问”，以后申请权限，会继续弹出提示
                            DownMapActivity.this.hasPerMission = false;
                        }
                        else {
                            //只要有一个选择：禁止，但选择“以后不再询问”，以后申请权限，不会继续弹出提示
                            DownMapActivity.this.hasPerMission = false;
                        }
                    }
                });

    }

    @Override
    public void getData(List<MyAreaDO> list) {
        stopProgress();
        Adapter adapter = mDowmMapRv.getAdapter();
        if (adapter == null) {
            mDowmMapRv.setAdapter(new DownMapAdapter(this, list, new OnRVItemClickListener<MyAreaDO>() {
                @Override
                public void ItemClick(android.view.View view, int position, MyAreaDO entity) {
                    downMap(entity);
                }
            }));
        }
        else {
            ((DownMapAdapter) adapter).changeData(list);
        }
    }


    /**
     * 请求下载地址
     *
     * @param entity
     */
    private void downMap(MyAreaDO entity) {
        if (DownMapActivity.this.hasPerMission) {
            mPresenter.downMap(entity);
        }
        else {
            ToastUtils.showShort("无权限");
        }

    }


    /**
     * 更新下载进度
     * item下载状态
     *
     * @param entity
     */
    @Override
    public void updateItemState(MyAreaDO entity) {
        ((DownMapAdapter) mDowmMapRv.getAdapter()).notifyItemByEntity(entity);
    }

    @Override
    public void onRequestError(String msg) {
        stopProgress();
    }
}
