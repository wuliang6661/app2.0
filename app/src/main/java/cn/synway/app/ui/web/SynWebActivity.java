package cn.synway.app.ui.web;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.synway.app.R;
import cn.synway.app.base.SynBaseActivity;
import cn.synway.app.ui.web.web_interface.SynOSCInterface;
import cn.synway.app.ui.web.web_interface.X5WebAppInterface;
import cn.synway.synmonitor.logutil.LogUtil;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/7/1915:57
 * desc   :  加载webview的Activity
 * version: 1.0
 */
public class SynWebActivity extends SynBaseActivity {

    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.web_view)
    WebView webView;
    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.web_layout)
    FrameLayout webLayout;


    private String url;
    private String urlParam;
    private String faceImgPath;

    private X5WebChromeClient chromeClient;

    @Override
    protected int getLayout() {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.act_synweb;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        initToorBar();
        initWebView();
        initView();
        webView.loadUrl(url);
    }


    /**
     * 初始化布局
     */
    private void initView() {
        Intent intent = getIntent();
        url = intent.getStringExtra(SynWebBuilder.EXTRA_URL);
        Log.d("dym------------------->", "SynWebActivity url= " + url);
        String name = intent.getStringExtra(SynWebBuilder.EXTRA_NAME);
        int isShowTitle = intent.getIntExtra(SynWebBuilder.EXTRA_IS_SHOW_TITLE, 1);
        urlParam = intent.getStringExtra(SynWebBuilder.EXTRA_URL_PARAM);
        faceImgPath = intent.getStringExtra(SynWebBuilder.EXTRA_FACE);
        String urlEnd = intent.getStringExtra(SynWebBuilder.EXTRA_URL_END);
        if (!TextUtils.isEmpty(urlEnd)) {
            if (url.contains("?")) {
                url += "&" + urlEnd;
            } else {
                url += "?" + urlEnd;
            }
        }
        titleText.setText(name);
        if (isShowTitle == 1) {
            titleBar.setVisibility(View.VISIBLE);
            immersionBar.titleBar(R.id.title_bar_layout).statusBarDarkFont(true).keyboardEnable(true).statusBarColor(R.color.title_bg).init();   //解决虚拟按键与状态栏沉浸冲突
        } else {
            titleBar.setVisibility(View.GONE);
            // 侵入式会导致网页和状态栏冲突
            immersionBar.titleBar(R.id.title_bar_layout).statusBarDarkFont(true).keyboardEnable(true).statusBarColor(R.color.title_bg).init();   //解决虚拟按键与状态栏沉浸冲突
        }
    }


    /**
     * 初始化webView
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        WebSettings settings = webView.getSettings();
        //支持获取手势焦点
        webView.requestFocusFromTouch();
//        //支持Js
        settings.setJavaScriptEnabled(true);
//        //支持插件
        settings.setPluginState(WebSettings.PluginState.ON);
//        //设置适应屏幕
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        //支持缩放
        settings.setSupportZoom(false);
        //隐藏原生的缩放控件
        settings.setDisplayZoomControls(false);
        //支持内容重新布局
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setSupportMultipleWindows(true);
        settings.supportMultipleWindows();
//        //设置可访问文件
        settings.setAllowFileAccess(true);
//        //当webView调用requestFocus时为webview设置节点
        settings.setNeedInitialFocus(true);
//        settings.setTextZoom(100);
//        settings.setDomStorageEnabled(true);//DOM Storage
        //webView's Setting的初始化
        // 如果访问的页面中有Javascript，则webview必须设置支持Javascript。
        webView.getSettings().setJavaScriptEnabled(true);
        // 允许js弹出窗口r
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        // 设置是否使用最后一次缓存
        //优先使用缓存
        // webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 不使用缓存
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        // 触摸焦点起作用,如果不设置，则在点击网页文本输入框时，不能弹出软键盘及不响应其他的一些事件。
        webView.requestFocus();
        //设置支持自动加载图片
        if (Build.VERSION.SDK_INT >= 19) {
            settings.setLoadsImagesAutomatically(true);
        } else {
            settings.setLoadsImagesAutomatically(false);
        }
        //设置编码格式
        settings.setDefaultTextEncodingName("UTF-8");
//        settings.setUserAgentString("Mozilla/5.0 (Linux; U; Android 5.1.1; zh-cn;) AppleWebKit/537.36 (KHTML, like Gecko)Version/4.0 Chrome/37.0.0.0 MQQBrowser/6.3 Mobile Safari/537.36");
//        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //下面方法去掉滑动条
//        IX5WebViewExtension ix5 = webView.getX5WebViewExtension();
//        if (null != ix5) {
//            ix5.setScrollBarFadingEnabled(false);
//        }

        chromeClient = new X5WebChromeClient(this);
        chromeClient.setData(webLayout, webView);
        webView.setWebChromeClient(chromeClient);
        webView.setWebViewClient(new X5WebClient(this));
        webView.addJavascriptInterface(new SynOSCInterface(this), "synosc");
        webView.addJavascriptInterface(new X5WebAppInterface(this), "Android");
    }


    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }


    /**
     * 设置顶部布局事件监听
     */
    @OnClick({R.id.back, R.id.closeview, R.id.fresh})
    public void titleClick(View view) {
        switch (view.getId()) {
            case R.id.back:     //返回
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }
                break;
            case R.id.closeview:  //关闭页面
                finish();
                break;
            case R.id.fresh:   //刷新当前页面
                webView.reload();
                break;
        }
    }


    @Override
    protected void onDestroy() {
        try {
            if (webView != null) {
//                webView.stopLoading();
//                webView.removeAllViewsInLayout();
//                webView.removeAllViews();
//                webView.setWebViewClient(null);
//                CookieSyncManager.createInstance(this).stopSync();
                webView.destroy();
                webView = null;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            super.onDestroy();
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == X5WebChromeClient.REQUEST_CODE) {
            // 经过上边(1)、(2)两个赋值操作，此处即可根据其值是否为空来决定采用哪种处理方法
            if (chromeClient.mUploadMessage != null) {
                chromeClient.chooseBelow(resultCode, data);
            } else if (chromeClient.mUploadCallbackAboveL != null) {
                chromeClient.chooseAbove(resultCode, data);
            } else {
                Toast.makeText(this, "发生错误", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == X5WebChromeClient.VIDEO_REQUEST) {
            if (null == chromeClient.mUploadMessage && null == chromeClient.mUploadCallbackAboveL)
                return;

            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (chromeClient.mUploadCallbackAboveL != null) {
                if (resultCode == RESULT_OK) {
                    chromeClient.mUploadCallbackAboveL.onReceiveValue(new Uri[]{result});
                    chromeClient.mUploadCallbackAboveL = null;
                } else {
                    chromeClient.mUploadCallbackAboveL.onReceiveValue(new Uri[]{});
                    chromeClient.mUploadCallbackAboveL = null;
                }

            } else if (chromeClient.mUploadMessage != null) {
                if (resultCode == RESULT_OK) {
                    chromeClient.mUploadMessage.onReceiveValue(result);
                    chromeClient.mUploadMessage = null;
                } else {
                    chromeClient.mUploadMessage.onReceiveValue(Uri.EMPTY);
                    chromeClient.mUploadMessage = null;
                }

            }
        }

    }


    public String getFaceImg() {
        Bitmap bit = BitmapFactory.decodeFile(faceImgPath);
        if (bit == null) return "";
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, 40, bos);//参数100表示不压缩
        byte[] bytes = bos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);

    }

    public String getUrlParam() {
        return urlParam;
    }


    /**
     * 如果网页还有上一层，则返回上一层网页
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                /** 回退键 事件处理 优先级:视频播放全屏-网页回退-关闭页面 */
                if (chromeClient.customView != null) {
                    chromeClient.onHideCustomView();
                } else if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }


    /**
     * 定位：权限判断
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getLocation() {
        //检查定位权限
        ArrayList<String> permissions = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        //判断
        if (permissions.size() == 0) {//有权限，直接获取定位
            getLocationLL();
        } else {//没有权限，获取定位权限
            requestPermissions(permissions.toArray(new String[permissions.size()]), 2);

            LogUtil.d("*************", "没有定位权限");
        }
    }

    /**
     * 定位：获取经纬度
     */
    private void getLocationLL() {
        LogUtil.d("*************", "获取定位权限1 - 开始");
        Location location = getLastKnownLocation();
        if (location != null) {
            //传递经纬度给网页
            String result = "{code: '0',type:'2',data: {longitude: '" + location.getLongitude() + "',latitude: '" + location.getLatitude() + "'}}";
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl("javascript:callback(" + result + ");");
                }
            });

            //日志
            String locationStr = "维度：" + location.getLatitude() + "\n"
                    + "经度：" + location.getLongitude();
            LogUtil.d("*************", "经纬度：" + locationStr);
        } else {
            Toast.makeText(this, "位置信息获取失败", Toast.LENGTH_SHORT).show();
            LogUtil.d("*************", "获取定位权限7 - " + "位置获取失败");
        }
    }

    /**
     * 定位：得到位置对象
     *
     * @return
     */
    private Location getLastKnownLocation() {
        //获取地理位置管理器
        LocationManager mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            @SuppressLint("MissingPermission") Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    /**
     * 定位：权限监听
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 2://定位
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LogUtil.d("*************", "同意定位权限");
                    getLocationLL();
                } else {
                    Toast.makeText(this, "未同意获取定位权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
}
