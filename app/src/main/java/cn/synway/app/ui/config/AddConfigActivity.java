package cn.synway.app.ui.config;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;

import com.blankj.utilcode.util.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.synway.app.R;
import cn.synway.app.api.HttpResultSubscriber;
import cn.synway.app.api.HttpServerImpl;
import cn.synway.app.base.SynBaseActivity;
import cn.synway.app.bean.AppConfigBO;
import cn.synway.app.db.dbmanager.ConfigIml;
import cn.synway.app.db.table.ConfigEntry;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/7/210:38
 * desc   :  新增服务器
 * version: 1.0
 */
public class AddConfigActivity extends SynBaseActivity {


    @BindView(R.id.edit_name)
    EditText editName;
    @BindView(R.id.edit_ip)
    EditText editIp;
    @BindView(R.id.edit_port)
    EditText editPort;

    @Override
    protected int getLayout() {
        return R.layout.act_config_settings;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        goBack();
        setTitleText("新增服务器");
    }


    @OnClick(R.id.btn)
    public void click() {
        String name = editName.getText().toString().trim();
        String ip = editIp.getText().toString().trim();
        String port = editPort.getText().toString().trim();
        if (StringUtils.isEmpty(name)) {
            showToast("请输入配置名称！");
            return;
        }
        if (StringUtils.isEmpty(ip)) {
            showToast("请输入服务器IP！");
            return;
        }
        if (StringUtils.isEmpty(port)) {
            showToast("请输入服务器端口！");
            return;
        }
        showProgress("");
        checkConfig(name, ip, port);
    }


    /**
     * 检测IP端口是否可用
     */
    public void checkConfig(String name, String ip, String port) {
        HttpServerImpl.checkConfig(ip, port).subscribe(new HttpResultSubscriber<AppConfigBO>() {
            @Override
            public void onSuccess(AppConfigBO s) {
                stopProgress();
                addSourss(name, ip, port);
            }

            @Override
            public void onFiled(String message) {
                stopProgress();
                showToast("该服务器无法连接，请检测服务器是否可用！");
            }
        });
    }


    /**
     * 新增服务器成功
     */
    private void addSourss(String name, String ip, String port) {
        ConfigEntry configEntry = new ConfigEntry(name, ip, port, true);
        List<ConfigEntry> configs = ConfigIml.queryAllConfig();
        for (ConfigEntry item : configs) {
            item.isSelector = false;
        }
        configs.add(configEntry);
        ConfigIml.clearConfig();
        ConfigIml.addConfigs(configs);
        HttpServerImpl.service = null;
        finish();
    }
}
