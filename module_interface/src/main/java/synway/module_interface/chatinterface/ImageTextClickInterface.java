package synway.module_interface.chatinterface;

import android.app.Activity;

/**
 * Created by zjw on 2017/5/22.
 * 图文消息点击接口
 */

public abstract class ImageTextClickInterface {
    /**
     * 图文消息类型 暂时只有type=1 情报功能使用
     */
    public  int type;

    /**
     * 设置图文消息类型
     */
    public abstract void setType();

    /**
     * 图文消息点击后
     * 传递的参数包括 context 情报tag 目标id
     */
    public abstract void doAfterClick(Activity context, String tag, String targetID);

}
