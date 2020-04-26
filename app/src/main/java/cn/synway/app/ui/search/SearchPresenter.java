package cn.synway.app.ui.search;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.List;

import cn.synway.app.api.HttpResultSubscriber;
import cn.synway.app.api.HttpServerImpl;
import cn.synway.app.bean.OrganDao;
import cn.synway.app.db.table.UserEntry;
import cn.synway.app.mvp.BasePresenterImpl;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class SearchPresenter extends BasePresenterImpl<SearchContract.View> implements SearchContract.Presenter {


    public void getSearchVal(String val) {
        LogUtils.e(val);

        HttpServerImpl.getUserInfoByName(val).subscribe(new HttpResultSubscriber<List<UserEntry>>() {
            @Override
            public void onSuccess(List<UserEntry> list) {
                if (mView != null) {
                    getOrgan(list,val);
                }
            }

            @Override
            public void onFiled(String message) {
                ToastUtils.showShort(message);
            }
        });
    }

    private void getOrgan(List<UserEntry> list, String val){
        HttpServerImpl.getOrganInfoByName(val).subscribe(new HttpResultSubscriber<List<OrganDao>>() {
            @Override
            public void onSuccess(List<OrganDao> organDaos) {
                if (mView != null) {

                     mView.getData(list,organDaos, val);
                }
            }

            @Override
            public void onFiled(String message) {
                ToastUtils.showShort(message);
            }
        });
    }
}
