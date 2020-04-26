package cn.synway.app.base;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.freddy.im.IMSConfig;

import java.util.HashMap;
import java.util.Map;

import cn.synway.app.config.Config;
import cn.synway.app.ui.main.MainActivity;
import cn.synway.app.ui.web.SynWebBuilder;
import cn.synway.synmonitor.event.SynCountly;
import im.im.IMSClientBootstrap;
import synway.common.watermaker.WaterMarkUtil;


/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/7/1613:25
 * desc   :  监测程序中所有界面的生命周期
 * version: 1.0
 */
public class AppLifecycleHandler implements Application.ActivityLifecycleCallbacks {


    //存储已经显示水印的界面，避免重复显示，导致水印重叠
    private Map<String, Activity> waterMaps;

    // 判断app 是在前台还是后台的
    private int refCount = 0;

    AppLifecycleHandler() {
        waterMaps = new HashMap<>();
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        gatherStart(activity);
        refCount++;
        Config.AppInBack = false;
        //设置推送服务的长连接进入前台，心跳时间变短
        IMSClientBootstrap.getInstance().updateAppStatus(IMSConfig.APP_STATUS_FOREGROUND);
    }


    @Override
    public void onActivityResumed(Activity activity) {
        if (activity instanceof MainActivity) {
            return;
        }
        //如果水印开启，则显示水印
        if (Config.isOpenWaterMaker) {
            if (waterMaps.containsKey(activity.getLocalClassName())) {
                return;
            }
            WaterMarkUtil.getInstance().showWatermarkView(activity);
            waterMaps.put(activity.getLocalClassName(), activity);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        refCount--;
        gatherStopped(activity);
        if (refCount == 0) {
            Config.AppInBack = true;
            //设置推送服务的长连接进入后台，心跳时间变长
            IMSClientBootstrap.getInstance().updateAppStatus(IMSConfig.APP_STATUS_BACKGROUND);
        }

    }


    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        waterMaps.remove(activity.getLocalClassName());
    }

    /**
     * 页面启动收集
     *
     * @param activity
     */
    private void gatherStart(Activity activity) {
        if (refCount == 0 && SynCountly.getInstance().getUser() != null) {
            SynCountly.getInstance().gatherApplicationStart();
        }

        String className = activity.getLocalClassName();
        Intent intent = activity.getIntent();
        if (intent != null
                && !StringUtils.isTrimEmpty(intent.getStringExtra(SynWebBuilder.EXTRA_ID))
                && "cn.synway.app.ui.web.SynWebActivity".equals(className)) {
            String name = activity.getIntent().getStringExtra(SynWebBuilder.EXTRA_NAME);
            LogUtils.e(className, name);
            SynCountly.getInstance().gatherPageStart(name);
        } else if (intent != null
                && !StringUtils.isTrimEmpty(intent.getStringExtra("id"))
                && "synway.module_publicaccount.weex_module.WXPageActivity".equals(className)) {
            String name = activity.getIntent().getStringExtra("Title");
            LogUtils.e(className, name);
            SynCountly.getInstance().gatherPageStart(name);
        }
    }

    /**
     * 页面关闭收集
     *
     * @param activity
     */
    private void gatherStopped(Activity activity) {
        if (refCount == 0 && SynCountly.getInstance().getUser() != null) {
            SynCountly.getInstance().gatherApplicationEnd("应用退出或后台");
        }
        String className = activity.getLocalClassName();
        Intent intent = activity.getIntent();
        if ("cn.synway.app.ui.web.SynWebActivity".equals(className)
                && intent != null
                && !StringUtils.isTrimEmpty(intent.getStringExtra(SynWebBuilder.EXTRA_ID))) {
            String id = intent.getStringExtra(SynWebBuilder.EXTRA_ID);
            String name = activity.getIntent().getStringExtra(SynWebBuilder.EXTRA_NAME);
            LogUtils.e(className, name);
            SynCountly.getInstance().gatherPageEnd(name, id, "HTML页面", 1);
        } else if ("synway.module_publicaccount.weex_module.WXPageActivity".equals(className)
                && intent != null
                && !StringUtils.isTrimEmpty(intent.getStringExtra("id"))) {
            String id = intent.getStringExtra("id");
            String name = activity.getIntent().getStringExtra("Title");
            LogUtils.e(className, name);
            SynCountly.getInstance().gatherPageEnd(name, id, "WEEX页面", 1);
        }
    }
}
