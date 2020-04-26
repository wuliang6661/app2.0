package cn.synway.app.ui.search;

import java.util.List;

import cn.synway.app.bean.OrganDao;
import cn.synway.app.db.table.UserEntry;
import cn.synway.app.mvp.BasePresenter;
import cn.synway.app.mvp.BaseView;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class SearchContract {
    interface View extends BaseView {
        void getData(List<UserEntry> list, List<OrganDao> olist, String key);
    }

    interface Presenter extends BasePresenter<View> {
        void getSearchVal(String val);
    }
}
