package synway.module_interface.invoke;

/**
 * Created by zjw on 2016/11/24.
 */

public abstract class HandlerTipCount {

    //设置自己Fragment下按钮图标的消息未读数，消息数<0,不显示
    public abstract void setUnReadNum(int count);
    //读取自己Fragment下按钮图标的消息未读数
    public abstract  int getUnReadNum();


}
