package cn.synway.app.ui.main.publicappcenter;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.synway.app.api.HttpResultSubscriber;
import cn.synway.app.api.HttpServerImpl;
import cn.synway.app.bean.NetAPPBO;
import cn.synway.app.mvp.BasePresenterImpl;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class PublicAppCenterPresenter extends BasePresenterImpl<PublicAppCenterContract.View> implements PublicAppCenterContract.Presenter {

    public void getData() {
        HttpServerImpl.getNewAPP().subscribe(new HttpResultSubscriber<List<NetAPPBO>>() {
            @Override
            public void onSuccess(List<NetAPPBO> list) {
                if (mView != null) {
                    List<NetAPPBO> data = conversionData(list);
                    mView.setNetAPPData(data);
                }
            }

            @Override
            public void onFiled(String message) {
                if(mView!=null){
                    mView.onFaild();
                }
                ToastUtils.showShort(message);
            }
        });
    }


    /**
     * 整理数据
     *
     * @param list
     * @return
     */
    private List<NetAPPBO> conversionData(List<NetAPPBO> list) {
        ArrayList<NetAPPBO> data = new ArrayList<>();

        HashMap<String, NetAPPBO> headData = new HashMap<>();

        for (int i = 0; i < list.size(); i++) {
            NetAPPBO netAPPBO = list.get(i);
            String title = StringUtils.isEmpty(netAPPBO.getBelongGroupName()) ? "其他" : netAPPBO.getBelongGroupName();

            if (!headData.containsKey(title)) {
                NetAPPBO appDao = new NetAPPBO(title, true);
                data.add(appDao);
                data.add(netAPPBO);
                headData.put(title, appDao);
            }
            else {
                NetAPPBO appDao = headData.get(title);
                int index = data.indexOf(appDao);
                data.add(++index, netAPPBO);
            }
        }

        return data;
    }


}
