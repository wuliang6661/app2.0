package cn.synway.synmonitor.event;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.synway.synmonitor.bean.UserBO;
import cn.synway.synmonitor.config.Config;
import cn.synway.synmonitor.config.Policy;
import cn.synway.synmonitor.event.xgupload.XGUploadManager;
import cn.synway.synmonitor.logutil.LogUtil;
import cn.synway.synmonitor.utils.UrlUtils;


/***
 *                                         ,s555SB@@&
 *                                      :9H####@@@@@Xi
 *                                     1@@@@@@@@@@@@@@8
 *                                   ,8@@@@@@@@@B@@@@@@8
 *                                  :B@@@@X3hi8Bs;B@@@@@Ah,
 *             ,8i                  r@@@B:     1S ,M@@@@@@#8;
 *            1AB35.i:               X@@8 .   SGhr ,A@@@@@@@@S
 *            1@h31MX8                18Hhh3i .i3r ,A@@@@@@@@@5
 *            ;@&i,58r5                 rGSS:     :B@@@@@@@@@@A
 *             1#i  . 9i                 hX.  .: .5@@@@@@@@@@@1
 *              sG1,  ,G53s.              9#Xi;hS5 3B@@@@@@@B1
 *               .h8h.,A@@@MXSs,           #@H1:    3ssSSX@1
 *               s ,@@@@@@@@@@@@Xhi,       r#@@X1s9M8    .GA981
 *               ,. rS8H#@@@@@@@@@@#HG51;.  .h31i;9@r    .8@@@@BS;i;
 *                .19AXXXAB@@@@@@@@@@@@@@#MHXG893hrX#XGGXM@@@@@@@@@@MS
 *                s@@MM@@@hsX#@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@&,
 *              :GB@#3G@@Brs ,1GM@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@B,
 *            .hM@@@#@@#MX 51  r;iSGAM@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@8
 *          :3B@@@@@@@@@@@&9@h :Gs   .;sSXH@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@:
 *      s&HA#@@@@@@@@@@@@@@M89A;.8S.       ,r3@@@@@@@@@@@@@@@@@@@@@@@@@@@r
 *   ,13B@@@@@@@@@@@@@@@@@@@5 5B3 ;.         ;@@@@@@@@@@@@@@@@@@@@@@@@@@@i
 *  5#@@#&@@@@@@@@@@@@@@@@@@9  .39:          ;@@@@@@@@@@@@@@@@@@@@@@@@@@@;
 *  9@@@X:MM@@@@@@@@@@@@@@@#;    ;31.         H@@@@@@@@@@@@@@@@@@@@@@@@@@:
 *   SH#@B9.rM@@@@@@@@@@@@@B       :.         3@@@@@@@@@@@@@@@@@@@@@@@@@@5
 *     ,:.   9@@@@@@@@@@@#HB5                 .M@@@@@@@@@@@@@@@@@@@@@@@@@B
 *           ,ssirhSM@&1;i19911i,.             s@@@@@@@@@@@@@@@@@@@@@@@@@@S
 *              ,,,rHAri1h1rh&@#353Sh:          8@@@@@@@@@@@@@@@@@@@@@@@@@#:
 *            .A3hH@#5S553&@@#h   i:i9S          #@@@@@@@@@@@@@@@@@@@@@@@@@A.
 *
 *
 *    又看源码，看你妹妹呀！
 */

public class SynCountly {

    private CollectionInfoManager mCollectionInfoManager;

    /**
     * 调度线程池
     */
    private ScheduledExecutorService sessionExecutorService;

    /**
     * 锁对象
     */
    private XGUploadManager mUploadManager;


    private SynCountly() {

    }

    public static SynCountly getInstance() {
        return SynCountlyHolder.instance;
    }


    private static class SynCountlyHolder {
        private static SynCountly instance = new SynCountly();
    }


    /**
     * 初始化监控入口
     *
     * @param serverURL 上传数据路径
     * @param appKey    app授权码
     * @param context   静态持有者:Config, 使用者：AppInfo DeviceBO DeviceInfo DiskCacheUtil
     */
    public SynCountly init(Context context, String serverURL, String appKey) {

        if (mCollectionInfoManager != null && !TextUtils.isEmpty(Config.SERVER_URL) && !TextUtils.isEmpty(Config.APP_KEY)) {
            throw new IllegalStateException("SynCountly只能初始化一次！,请先调用destory");
        }
        if (!UrlUtils.isValidURL(serverURL)) {
            LogUtil.e("SynCountly", "init , URL无效！");
            throw new IllegalStateException("URL无效");
        }

        if (serverURL.charAt(serverURL.length() - 1) == '/') {
            serverURL = serverURL.substring(0, serverURL.length() - 1);
        }
        Config.SERVER_URL = serverURL;

        if (TextUtils.isEmpty(appKey)) {
            LogUtil.e("SynCountly", "init , appKey无效！");
            throw new IllegalStateException("appKey无效");
        }
        if (context == null) {
            LogUtil.e("SynCountly", "init , Context不能为空！");
            throw new IllegalStateException("Context不能为空");
        }
        Config.APP_KEY = appKey;
        Config.context = context.getApplicationContext();

        if (mCollectionInfoManager == null) {
            mCollectionInfoManager = CollectionInfoManager.getInstance();
        }

        if (mUploadManager == null) {
            mUploadManager = XGUploadManager.getInstance();
        }

        mUploadManager.setServerUrl(serverURL);
        mUploadManager.setAppKey(appKey);
        LogUtil.d("monitor", serverURL + "     init Success!!");

        return this;
    }

    /**
     * 控制日志开关
     *
     * @param isOpen
     */
    public SynCountly setLogUtilSwitch(boolean isOpen) {
        LogUtil.setLogUtilSwitch(isOpen);
        return this;
    }


    /**
     * 设置上传所需用户信息(可选)
     *
     * @param userId    用户ID
     * @param name      用户名称
     * @param orginName 用户机构
     */
    public SynCountly setUser(String userId, String name, String orginId, String orginName, Object describe) {
        UserBO userBO = new UserBO();
        userBO.setUserId(userId);
        userBO.setUserName(name);
        userBO.setOrginId(orginId);
        userBO.setOrginName(orginName);
        userBO.setDescripe(describe);
        Config.userBO = userBO;
        return this;
    }

    /**
     * 设置上传所需用户信息(可选)
     */
    public SynCountly setUser(@Nullable UserBO userBO) {
        Config.userBO = userBO;
        return this;
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    public @Nullable
    UserBO getUser() {
        return Config.userBO;
    }

    /**
     * 设置数据上报策略
     *
     * @param policy 上报策略   （现在只实现了 UPLOAD_POLICY_INTERVA）
     * @param time   上传间隔时间
     */
    public SynCountly setUpdatePolicy(Policy policy, int time) {
        if (policy != Policy.UPLOAD_POLICY_INTERVA) {
            throw new IllegalStateException("该策略暂未实现！");
        }
        if (time == 0) {
            throw new IllegalStateException("间隔时间不可为0！");
        }
        Config.UPDATE_TIME = time;
        return this;
    }


    /**
     * 启动数据上报
     */
    public void create() {
        if (checkVal()) {
            beginSession();
        }
        else {
            LogUtil.e("SynCountly", "init 初始化失败");
        }
    }

    /**
     * 检查初始化
     *
     * @return
     */
    private boolean checkVal() {
        if (mUploadManager == null || mCollectionInfoManager == null) {
            return false;
        }
        if (!UrlUtils.isValidURL(Config.SERVER_URL)) {
            return false;
        }
        if (TextUtils.isEmpty(Config.APP_KEY)) {
            return false;
        }
        if (Config.context == null) {
            return false;
        }
        return true;
    }


    /**
     * 启动时间线程，根据时间开始上报
     */
    private void beginSession() {
        if (mCollectionInfoManager == null) {
            LogUtil.e("SynCountly", "beginSession , SynCountly not init");
            return;
        }
        if (sessionExecutorService == null) {
            sessionExecutorService = Executors.newSingleThreadScheduledExecutor();
            sessionExecutorService.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    updateSession();
                }
            }, 0, Config.UPDATE_TIME, TimeUnit.MINUTES);
        }
    }


    /**
     * 数据上报
     */
    private void updateSession() {

        if (mUploadManager == null) {
            LogUtil.e("SynCountly", "updateSession , SynCountly not init");
            return;
        }

        if (mUploadManager != null) {
            try {
                mUploadManager.updateFile();
            }
            catch (Exception e) {
                LogUtil.e("SynCountly", "updateSession ,请检查URL和APPKey");
            }
        }
    }


    public void gatherPageStart(String name) {
        if (mUploadManager == null) {
            LogUtil.e("SynCountly", "gatherPageStart,SynCountly not init");
            return;
        }
        mCollectionInfoManager.collectionPageStart(name);
    }

    public void gatherPageEnd(String name, String id, String describle,int storageType) {
        if (mUploadManager == null) {
            LogUtil.e("SynCountly", "gatherPageEnd,SynCountly not init");
            return;
        }
        mCollectionInfoManager.collectionPageEnd(name,id, describle,storageType);
    }

    public void gatherApplicationStart() {
        gatherPageStart("app");
    }

    public void gatherApplicationEnd(String describe) {
        gatherPageEnd("app",null, describe,0);
        mCollectionInfoManager.saveNotUpData();
    }

    /**
     * 销毁，清理资源
     */
    public void destory() {
        if (mUploadManager == null) {
            LogUtil.e("SynCountly", "clear , SynCountly not init");
            return;
        }
        //mCollectionInfoManager
        mCollectionInfoManager.saveNotUpData();
        mCollectionInfoManager = null;

        //Config
        Config.APP_KEY = "";
        Config.SERVER_URL = "";
        Config.context = null;

        //Uploadmanager
        mUploadManager.setAppKey("");
        mUploadManager.setServerUrl("");
        mUploadManager = null;


        //sessionExecutorService
        sessionExecutorService.shutdown();
        try {
            if (!sessionExecutorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                sessionExecutorService.shutdownNow();
                sessionExecutorService = null;
            }
        }
        catch (InterruptedException e) {
            sessionExecutorService.shutdownNow();
            sessionExecutorService = null;
        }

    }

}
