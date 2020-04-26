package synway.module_interface.chatinterface;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import synway.module_interface.module.ModuleHandle;

/**
 * 聊天界面接口.
 * Created by 钱园超 on 2016/12/23.
 */

public abstract class ChatInterface {

    /**
     * 打开Activity时,通过intent传入的参数:目标ID
     */
    public static final String EXTRA_TARGET_ID = "TARGET_ID";
    /**
     * 打开Activity时,通过intent传入的参数:目标类型
     */
    public static final String EXTRA_TARGET_TYPE = "TARGET_TYPE";

    /**
     * 打开Activity时,通过intent传入的参数:用户id列表
     */
    public static final String EXTRA_TARGET_ID_LIST = "TARGET_ID_LIST";

    /**
     * 聊天菜单接口
     */
    public abstract ChatMenuEntity setChatMenuEntity();


    // 文件名称
    private static final String FILE_NAME = "FILE_NOTIFY_AGAIN";
    // 当前 聊天的targetID
    private static final String KEY_CURRENT_CHAT_ID = "KEY_CURRENTID";

    /** 将目标ID的消息设置为不显示通知 */
    public static final void setUnNitif(Context context, String targetID) {
        SharedPreferences spf = context.getSharedPreferences(FILE_NAME,
                Context.MODE_MULTI_PROCESS);
        spf.edit().putString(KEY_CURRENT_CHAT_ID, targetID).commit();
    }

    public static final void clear(Context context) {
        context.getSharedPreferences(FILE_NAME, Context.MODE_MULTI_PROCESS)
                .edit().clear().commit();
    }

    /** 模块操作接口 的一些参数 */
    public ModuleHandle moduleHandle = null;
    /** 获取数据库操作 */
    public SQLiteOpenHelper getSQLiteHelp;

    /** 设置需要在主模块升级的数据库，返回为null则没有需要升级的表*/
    public  ArrayList<String> setDBTablePath(){return null;}


    public Context context;
    /** 当应用程序建立,该方法在application中执行， */
    public void onAppCreat(Context context) {
        this.context = context;
    }
    /** 主homeAct初始化时 */
    public void onHomeStart(Context context){}

    /**主模块跳转子模块某个Actvity,例如rtvoice模块的p2p 呼叫界面*/
    public String setModuleActivity(){return null;}

}
