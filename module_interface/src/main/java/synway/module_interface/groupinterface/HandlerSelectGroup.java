package synway.module_interface.groupinterface;

import android.app.Activity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zjw on 2016/12/2.
 */

public abstract class HandlerSelectGroup {

    /**
     * @param selectMap 默认选择的对象 String 为目标id Integer 为目标类型
     * @filterList 过滤的id列表
     * @isGroupOnly 是否只选群
     * @isSupportRemote 是否支持异地通讯
     */
    public abstract  void goContactSelectAct(int REQUEST_CODE, Activity act, HashMap<String,Integer> selectMap, ArrayList<String> filterList, boolean isGroupOnly,boolean isSupportRemote);

    /**
     * 返回的结果 已选中人员ID
     */
    public static final String EXTRA_RESULT_USERID_LIST = "USERID_LIST";
    /**
     * 返回的结果 已选中人员TYPE
     */
    public static final String EXTRA_RESULT_USERTYPE_LIST = "USERTYPE_LIST";

}
