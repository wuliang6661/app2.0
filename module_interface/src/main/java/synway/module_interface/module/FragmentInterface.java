package synway.module_interface.module;

import android.content.Context;

import java.util.ArrayList;

import synway.module_interface.invoke.HandlerActIntent;
import synway.module_interface.invoke.HandlerIntelligence;
import synway.module_interface.invoke.HandlerLCTCount;
import synway.module_interface.invoke.HandlerRTGIS;
import synway.module_interface.invoke.HandlerSelectContact;
import synway.module_interface.invoke.HandlerUserInfo;
import synway.module_interface.invoke.HandlerVideoPlayer;
import synway.module_interface.searchinterface.SearchInterface;

/**
 * Created by zjw on 2016/11/18.
 *
 * 这个类由Application装载,继承这个接口的类,它就会被系统进程类保持引用
 *
 */

public abstract class FragmentInterface {

    public Context context;


    /** 当应用程序建立 */
    public void onAppCreat(Context context) {
        this.context = context;
    }

    /** 设置程序初始化从服务器同步消息的类的路径*/
    public ArrayList<String> setInitClassPath(){return null;}

    /**当用户注销或被顶时*/
    public void onLoginOut(){}


    /** 当收到服务器相关推送,推送解析工厂注册的type不允许重合,这会导致另一个工厂收不到推送" */
    /** 设置推送的路径列表，返回为null则不注册推送*/
    public  ArrayList<String> setPushPath(){return null;}

    /** 设置需要在主模块升级的数据库，返回为null则不升级*/
    public  ArrayList<String> setDBTablePath(){return null;}

    /** 设置通知消息点击后跳转的Activity的路径*/
    public  String setActivityPath(){return null;}


    /**设置插入最近联系人的一列，点击之后所跳转的Activity 不插入最近联系人则返回null**/
    public  LastContactObj setInsertLastContactActivity(){return null;}

    /**设置插入最近联系人的一体化通知的一栏，点击之后所跳转的Activity 不插入最近联系人则返回null**/
    public  LastContactObj setInsertOneNoticeActivity(){return null;}

    //主模块提供的接口操作方法只允许在主线程中使用，不允许在推送线程中使用
    //提供的接口的方法如下
    // moduleHandle、handlerLCTCount、handlerUserinfo、handlerSelectContact、handlerVideoPlayer、handlerRTGIS
    /** 模块操作接口 的一些参数 */
    public ModuleHandle moduleHandle = null;

    /**操作最近联系人未读消息提示*/
    public HandlerLCTCount handlerLCTCount = null;

    /**跳转用户信息界面*/
    public HandlerUserInfo handlerUserinfo = null;
    /**跳转用户用户筛选界面*/
    public HandlerSelectContact handlerSelectContact = null;
    /**跳转视频播放界面*/
    public HandlerVideoPlayer handlerVideoPlayer = null;
    /**跳转RTGISAct界面*/
    public HandlerRTGIS handlerRTGIS = null;

    /**跳转情报编辑模块*/
    public HandlerIntelligence handlerIntelligence = null;

    /**
     * 跳转登录界面
     */
    public HandlerActIntent handlerActIntent = null;

    /**必须每次new */
    public abstract HomeFragment newFragment();

    /**设置实现搜索接口的类的列表,无搜索则返回null*/
    public ArrayList<SearchInterface> newSearch(){ return null;}

    //获取LocationInterface实现的路径
    public String[] getLoacationInterfacepath(){return null;}

}
