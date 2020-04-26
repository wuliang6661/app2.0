package synway.module_publicaccount;

import android.content.Context;

import java.util.ArrayList;

import synway.module_interface.AppConfig;
import synway.module_interface.module.FragmentInterface;
import synway.module_interface.module.HomeFragment;
import synway.module_interface.module.LastContactObj;
import synway.module_interface.searchinterface.SearchInterface;
import synway.module_publicaccount.public_favorite.Search.SearchMenuImpl;
import synway.module_publicaccount.public_favorite.Search.SearchPublicAccountImpl;
import synway.module_publicaccount.public_list_new.PublicAccountFragment;

/**
 * 完成接口模块的接口
 * Created by zjw on 2016/11/24.
 */

public class Main extends FragmentInterface {
    // 手机资源调用接口文档：1.0.6
    public static final String InterfaceVersion = "1.0.6";
    //weex的SDK版本
    public static final String WeexSdkVersion = "weex_sdk:0.19.0.7";
    //模块版本
    public static final String AppModuleVersion = "1.0.0";
    /**
     * 公众号收到新推送
     **/
    private String pushPath = "synway.module_publicaccount.push.OnPublicNewMsg";
    /**
     * 公众号收到给Weex页面推送
     **/
    private String pushPath3 = "synway.module_publicaccount.push.OnPublicWeexMsg";
    /**
     * 公众号更新的推送
     **/
    private String pushPath4 = "synway.module_publicaccount.push.onPublicUpdate";

    private String pushPath5 = "synway.module_publicaccount.push.OnPublicMsgDelete";
    /**
     * 哈密项目预警消息的推送
     **/
    private String pushPath6 = "synway.module_publicaccount.push.OnPublicHmMsg";

    /**
     * 服务端修改菜单后的推送
     **/
//    private String pushPath2="synway.module_publicaccount.push.OnPublicChangeMenuMsg";
    private String dbPath1 = "synway.module_publicaccount.db.table_util.Table_PublicAccount";
    private String dbPath2 = "synway.module_publicaccount.db.table_util.Table_PublicAccountFocusedList";
    private String dbPath3 = "synway.module_publicaccount.db.table_util.Table_PublicAccountRecord";
    private String dbPath4 = "synway.module_publicaccount.db.table_util.Table_PublicMenu";
    private String dbPath5 = "synway.module_publicaccount.db.table_util.Table_PublicAccount_Gis";
    private String dbPath6 = "synway.module_publicaccount.db.table_util.Table_PublicConfigMsg";
    private String dbPath7 = "synway.module_publicaccount.db.table_util.Table_PublicAccount_Notice";
    private String dbPath8 = "synway.module_publicaccount.db.table_util.Table_PublicAccount_LastMsg";

    public HomeFragment homeFragment;

    @Override
    public ArrayList<String> setPushPath() {
        ArrayList<String> pushList = new ArrayList<>();
        pushList.add(pushPath);
//        pushList.add(pushPath2);
        pushList.add(pushPath3);
        pushList.add(pushPath4);
        pushList.add(pushPath5);
        pushList.add(pushPath6);
        return pushList;
    }

    @Override
    public ArrayList<String> setDBTablePath() {
        ArrayList<String> dbList = new ArrayList<>();
        dbList.add(dbPath1);
        dbList.add(dbPath2);
        dbList.add(dbPath3);
        dbList.add(dbPath4);
        dbList.add(dbPath5);
        dbList.add(dbPath6);
        dbList.add(dbPath7);
        dbList.add(dbPath8);
        return dbList;
    }

    private static Main _main = null;

    public static Main instance() {
        return _main;
    }

    public Main() {
        _main = this;
    }

    @Override
    public void onAppCreat(Context context) {
        super.onAppCreat(context);
        _main = this;
    }

    @Override
    public LastContactObj setInsertLastContactActivity() {
        LastContactObj lastContactObj = new LastContactObj();
        lastContactObj.activityPath = "synway.module_publicaccount.public_chat.PublicAccountChatActNormal";
        lastContactObj.type = 2;
        return lastContactObj;
    }

    @Override
    public LastContactObj setInsertOneNoticeActivity() {
        LastContactObj lastContactObj = new LastContactObj();
        lastContactObj.activityPath = "synway.module_publicaccount.notice.PublicNoticeActivity";
        lastContactObj.type = 5;
        return lastContactObj;
    }

    @Override
    public ArrayList<String> setInitClassPath() {
        ArrayList<String> initList = new ArrayList<>();
        initList.add("synway.module_publicaccount.init.InitPublicaccountMsg");
        return initList;
    }

    @Override
    public String setActivityPath() {
        return "synway.module_publicaccount.public_chat.PublicAccountChatActNormal";

    }

    @Override
    public HomeFragment newFragment() {
        // return new PublicFavoriteFragment();
        return new PublicAccountFragment();
    }

    @Override
    public ArrayList<SearchInterface> newSearch() {
        if (AppConfig.IS_USE_PUBLIC_SEARCH) {
            ArrayList<SearchInterface> searchInterfaces = new ArrayList<SearchInterface>();
            SearchInterface searchInterface = new SearchPublicAccountImpl();
            SearchInterface searchInterfacemenu = new SearchMenuImpl();
            searchInterfaces.add(searchInterface);
            searchInterfaces.add(searchInterfacemenu);
            return searchInterfaces;
        }
        return null;

    }

    @Override
    public String[] getLoacationInterfacepath() {
        //2019-4-2 注释：经沟通后，友方位置是特定功能，常规版本去除掉.
        //将好友位置 改为 外线终端，敌方位置 改为 布控对象 ，设备位置 改为 跟踪设备
        //备注：这个是属于早期预警公众号的功能,和新的应用平台无关.

        if (AppConfig.IS_HX_VERSION) {
            return new String[] { "synway.module_publicaccount.MLoacationenemy",
                "synway.module_publicaccount.MLoacationequip" };
        } else {
            return null;
        }


        // return new String[]{"synway.module_publicaccount.MLoacationenemy","synway.module_publicaccount.MLoacationfriend",
        // "synway.module_publicaccount.MLoacationequip"};
//        return  null;
    }
}
