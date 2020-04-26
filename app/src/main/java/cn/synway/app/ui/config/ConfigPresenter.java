package cn.synway.app.ui.config;

import cn.synway.app.api.HttpResultSubscriber;
import cn.synway.app.api.HttpServerImpl;
import cn.synway.app.bean.AppConfigBO;
import cn.synway.app.db.table.ConfigEntry;
import cn.synway.app.mvp.BasePresenterImpl;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class ConfigPresenter extends BasePresenterImpl<ConfigContract.View> implements ConfigContract.Presenter {

    public void checkConfig(ConfigEntry configEntry) {
        HttpServerImpl.checkConfig(configEntry.serverIp, configEntry.serverPort).subscribe(new HttpResultSubscriber<AppConfigBO>() {
            @Override
            public void onSuccess(AppConfigBO s) {
                if (mView != null) {
                    mView.checkSoress(configEntry);
                }
            }

            @Override
            public void onFiled(String message) {
                if (mView != null) {
                    mView.onRequestError(message);
                }
            }
        });
    }
}
