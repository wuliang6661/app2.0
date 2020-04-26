package cn.synway.app.push.data;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/8/269:43
 * desc   : 收到服务端推送的设备管理推送应用层监听
 * version: 1.0
 */
public class ServerNotificationBO {


    /**
     * 1 : 账号注销，设备禁用 ，账号自动退出登录
     * <p>
     * 2 ：应用中心数据修改，通知刷新应用中心
     */
    public String type;

}
