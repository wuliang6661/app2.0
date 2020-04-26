package synway.module_interface.groupinterface;

/**
 * Created by zjw on 2017/5/17.
 */

public class GroupItem implements Comparable<GroupItem>{
    /**
     * 按钮文本
     */
    public String buttonText;
    /**
     * 图标
     */
    public int icon;
    /**
     * 点击后跳转的类的路径
     */
    public Class<?> activity;
    /**
     * Intent 传递groupID的key
     */
    public static String INTENT_KEY_GROUPID = "IntentKeyGroupID";
    /**
     * Intent 传递command的key
     */
    public static String INTENT_KEY_COMMANDID = "IntentKeyCommandID";

    /**
     * 图标排列顺序，数字越大排越前面，默认是0，负数值排在默认0后面，越小越后面
     */
    public int index =0;

    @Override
    public int compareTo(GroupItem o) {
        return  o.index-this.index ;//从大到小排
    }
}
