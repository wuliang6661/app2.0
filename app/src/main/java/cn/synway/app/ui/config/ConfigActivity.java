package cn.synway.app.ui.config;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.synway.app.R;
import cn.synway.app.api.HttpServerImpl;
import cn.synway.app.config.Config;
import cn.synway.app.db.dbmanager.ConfigIml;
import cn.synway.app.db.table.ConfigEntry;
import cn.synway.app.mvp.MVPBaseActivity;
import cn.synway.app.widget.AlertDialog;
import cn.synway.app.widget.lgrecycleadapter.LGRecycleViewAdapter;
import cn.synway.app.widget.lgrecycleadapter.LGViewHolder;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class ConfigActivity extends MVPBaseActivity<ConfigContract.View, ConfigPresenter>
        implements ConfigContract.View {

    @BindView(R.id.recycle_view)
    RecyclerView recycleView;

    List<ConfigEntry> configs;


    @Override
    protected int getLayout() {
        return R.layout.act_config_list;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        goBack();
        setTitleText("服务器列表");

        initView();
    }


    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recycleView.setLayoutManager(manager);
    }


    /**
     * 查询数据库数据
     */
    private void getData() {
        configs = ConfigIml.queryAllConfig();
        if (configs.isEmpty()) {
            ConfigEntry configEntry = new ConfigEntry("默认服务器", Config.SERVER_IP,
                    Config.SERVER_PORT + "", true);
            ConfigIml.addConfig(configEntry);
            configs = ConfigIml.queryAllConfig();
        }
        setAdapter();
    }

    /**
     * 设置服务器列表适配器
     */
    private void setAdapter() {
        LGRecycleViewAdapter<ConfigEntry> adapter = new LGRecycleViewAdapter<ConfigEntry>(configs) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_server;
            }

            @Override
            public void convert(LGViewHolder holder, ConfigEntry configEntry, int position) {
                holder.setText(R.id.server_name, configEntry.configName);
                if (configEntry.isSelector) {
                    holder.getView(R.id.select_img).setVisibility(View.VISIBLE);
                } else {
                    holder.getView(R.id.select_img).setVisibility(View.GONE);
                }
            }
        };
        adapter.setOnItemClickListener(R.id.item_layout, (view, position) ->
                new AlertDialog(ConfigActivity.this).builder().setGone().setMsg("是否切换到该服务器？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", v -> {
                            showProgress("");
                            mPresenter.checkConfig(configs.get(position));
                        }).show());
        recycleView.setAdapter(adapter);
    }


    @OnClick(R.id.btn_add_server)
    public void addServer() {
        gotoActivity(AddConfigActivity.class, false);
    }

    @Override
    public void onRequestError(String msg) {
        stopProgress();
        showToast("该服务器无法连接，请检测服务器是否可用！");
    }

    @Override
    public void checkSoress(ConfigEntry configEntry) {
        showToast("同步配置成功！");
        stopProgress();
        for (ConfigEntry item : configs) {
            item.isSelector = item.configName.equals(configEntry.configName);
        }
        ConfigIml.clearConfig();
        ConfigIml.addConfigs(configs);
        HttpServerImpl.service = null;
        getData();
    }
}
