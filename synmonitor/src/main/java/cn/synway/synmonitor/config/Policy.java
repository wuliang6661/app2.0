package cn.synway.synmonitor.config;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/7/1713:41
 * desc   :  上报策略类，涉及多久时间上报一次
 * version: 1.0
 */
public enum Policy {

    /**
     * 实时发送
     */
    UPLOAD_POLICY_REALTIME,
    /**
     * 只在wifi下
     */
    UPLOAD_POLICY_WIFI_ONLY,
    /**
     * 批量上报 达到一定次数
     */
    UPLOAD_POLICY_BATCH,
    /**
     * 时间间隔
     */
    UPLOAD_POLICY_INTERVA,
    /**
     * 开发者debug模式 调用就可以发送
     */
    UPLOAD_POLICY_DEVELOPMENT,
    /**
     * 每次启动 发送上次产生的数据
     */
    UPLOAD_POLICY_WHILE_INITIALIZE


}
