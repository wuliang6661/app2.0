package cn.synway.app.utils;

import android.content.Context;

import com.blankj.utilcode.util.LogUtils;

import cn.synway.synmonitor.event.SynCountly;

public class SynCountlyFactory {
    /**
     * android应用数据收集
     *
     * @param context
     * @return 登录后，才收集应用启动
     */
    public static SynCountly initSynCountly(Context context, String gartherUrl, String appKey) {
        LogUtils.e("initSynCountly");
        try {
            SynCountly synCountly = SynCountly.getInstance().init(context.getApplicationContext(), gartherUrl, appKey);
            return synCountly;
        }
        catch (Exception e) {
            LogUtils.e(e.getLocalizedMessage());
            return SynCountly.getInstance();
        }
    }


    /**
     * 清理用户信息,并收集应用结束使用
     */
    public static void clearSynCountlyUserData() {
        LogUtils.e("clearSynCountlyUserData");
        SynCountly.getInstance().gatherApplicationEnd("应用退出或后台");
        SynCountly.getInstance().setUser(null);
    }

    /**
     * android应用数据收集
     * <p>
     * 关闭数据收集，清理资源
     */
    public static void destorySynCountly() {
        LogUtils.e("destorySynCountly");
        SynCountly.getInstance().destory();
    }

}
