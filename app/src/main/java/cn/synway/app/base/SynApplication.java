package cn.synway.app.base;

import android.util.Log;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.taobao.weex.InitConfig;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.common.WXException;
import com.tencent.smtt.sdk.QbSdk;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cn.synway.app.BuildConfig;
import cn.synway.app.config.Config;
import cn.synway.app.db.DataManager;
import cn.synway.app.db.table.MyObjectBox;
import cn.synway.app.ui.weex.WeexImageAdapter;
import cn.synway.app.ui.weex.moudle.WxBaseModule;
import im.NettyChatApp;
import io.objectbox.BoxStore;
import io.objectbox.android.AndroidObjectBrowser;


/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/6/1715:37
 * desc   :
 * version: 1.0
 */
public class SynApplication extends NettyChatApp {


    public static SPUtils spUtils;

    public static String token;

    @Override
    public void onCreate() {
        super.onCreate();
        CustomActivityOnCrash.install(this);
//        CustomActivityOnCrash.setErrorActivityClass(CustomErrorActivity.class);
        /***初始化工具类*/
        Utils.init(this);
        spUtils = SPUtils.getInstance(Config.SP_NAME);
        initObjectBox();
        //初始化weex
        initWeex();
        registerActivityLifecycleCallbacks(new AppLifecycleHandler());
        //x5内核初始化接口，初始化腾讯X5内核
        QbSdk.initX5Environment(getApplicationContext(), cb);
        Config.getDeviceIP();
    }


    /**
     * 初始化weex
     */
    private void initWeex() {
        //weex初始化
        InitConfig config = new InitConfig.Builder()
                .setImgAdapter(new WeexImageAdapter())
                //网络库接口
                // .setHttpAdapter(new InterceptWXHttpAdapter())
                .build();
        WXSDKEngine.initialize(this, config);
        try {
            WXSDKEngine.registerModule("wxBaseModule", WxBaseModule.class);
        }
        catch (WXException e) {
            e.printStackTrace();
        }

    }

    /**
     * 初始化数据库
     */
    private BoxStore boxStore;

    private void initObjectBox() {
        //第一次没运行之前，MyObjectBox默认会有报错提示，可以忽略。创建实体类， make之后报错就会不提示
        boxStore = MyObjectBox.builder().androidContext(this).build();
        if (BuildConfig.DEBUG) {//开启浏览器访问ObjectBox
            boolean started = new AndroidObjectBrowser(boxStore).start(this);
            Log.i("ObjectBrowser", "Started: " + started);
        }
        DataManager.getInstance().init(this);//数据库统一操作管理类初始化
    }

    public BoxStore getBoxStore() {
        return boxStore;
    }


    /**
     * 搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
     */
    QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

        @Override
        public void onViewInitFinished(boolean arg0) {
            //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
            Log.e("APPAplication", " onViewInitFinished is " + arg0);
        }

        @Override
        public void onCoreInitFinished() {
            Log.e("APPAplication", " onCoreInitFinished");
        }
    };


}
