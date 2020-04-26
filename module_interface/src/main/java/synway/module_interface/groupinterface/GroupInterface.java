package synway.module_interface.groupinterface;

import android.content.Context;

import java.util.ArrayList;

import synway.module_interface.module.ModuleHandle;
import synway.module_interface.searchinterface.SearchInterface;

/**
 * 群信息界面的接口
 * 暂时只有群情报模块使用
 */

public abstract class GroupInterface {

    public Context context;
    /**
     * 获取数据库操作
     */
//    public SQLiteOpenHelper sqLiteOpenHelper = null;
    /** 模块操作接口 的一些参数 */
    public ModuleHandle moduleHandle = null;

    /** 设置推送的路径列表，返回为null则不注册推送*/
    public  ArrayList<String> setPushPath(){return null;}

    /**
     * 设置需要在主模块升级的数据库，返回为null则不升级
     */
    public ArrayList<String> setDBTablePath() {
        return null;
    }

    /**
     * 群信息界面添加的对象
     */
    public abstract GroupItem setGroupItem();

    /**
     * 图文消息跳转情报详情路径
     */
    public abstract  Class<?> setActivity();

    /** 当应用程序建立 */
    public void  onAppCreat(Context context) {
        this.context = context;
    }

    /**跳转群选择界面*/
    public HandlerSelectGroup handlerSelectGroup = null;

    /**跳转位置标注界面*/
    public HandlerMyLocation handlerMyLocation = null;

    /**跳转位置显示界面*/
    public HandlerShowLocation handlerShowLocation = null;

    /**跳转消息多选界面*/
    public HandlerPickMsg handlerPickMsg = null;

    /**打开公众号模块提供的浏览器*/
    public HandlerPAWeb handlerPAWeb = null;

    /**设置实现搜索接口的类的列表,无搜索则返回null*/
    public ArrayList<SearchInterface> newSearch(){ return null;}

    /**当用户注销或被顶时*/
    public void onLoginOut(){}
}
