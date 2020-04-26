package cn.synway.app.ui.main.personlist;

import java.util.List;

import cn.synway.app.bean.PersonInPsListBO.OrganAndUserInfoBO;
import cn.synway.app.bean.PersonInPsListBO.TagList;
import cn.synway.app.mvp.BasePresenter;
import cn.synway.app.mvp.BaseView;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class PersonListContract {
    interface View extends BaseView {
        void getPeoleList(List<OrganAndUserInfoBO> list, List<TagList> organTags);

        void onFaild();
    }

    interface Presenter extends BasePresenter<View> {
        void getPeopleList(String fatherId, boolean fresh);

        void getGroupPerson(String id);

        Integer getCharPosition(String letter);
    }
}
