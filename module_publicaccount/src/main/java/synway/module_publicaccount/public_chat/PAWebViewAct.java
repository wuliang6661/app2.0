package synway.module_publicaccount.public_chat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;

import me.iwf.photopicker.PhotoPicker;
import qyc.library.control.dialog_progress.DialogProgress;
import synway.common.watermaker.WaterMarkUtil;
import synway.module_interface.config.netConfig.NetConfig;
import synway.module_interface.config.netConfig.Sps_NetConfig;
import synway.module_publicaccount.R;
import synway.module_publicaccount.fingerprint.SyncValidator;
import synway.module_publicaccount.public_chat.WebviewFn.CameraAndPicureFn;
import synway.module_publicaccount.public_chat.WebviewFn.SynOSCFn;
import synway.module_publicaccount.public_chat.WebviewFn.UploadSingleFileFn;
import synway.module_publicaccount.public_chat.WebviewFn.VideoUploadFn;
import synway.module_publicaccount.public_chat.WebviewFn.newVideoVoice.VoicePlayerView;
import synway.module_publicaccount.public_chat.file_upload_for_camera.PicUploadManager;
import synway.module_publicaccount.public_chat.file_upload_for_camera.VideoUploadManager;
import synway.module_publicaccount.qrcode.QRCode;
import synway.module_publicaccount.until.ClearTipUtils;
import synway.module_publicaccount.until.PicUtil;
import synway.module_publicaccount.webview.WebAppInterface;
import synway.module_publicaccount.webview.WebClient;
import synway.module_publicaccount.webview.WebViewChromeClient;

public class PAWebViewAct extends Activity {

    private PicUploadManager mPicUploadManager;
    private VideoUploadManager mVideoUploadManager;
    private NetConfig netConfig;

    private WebView webView = null;
    private WebTitleView titleView = null;
    private VoicePlayerView voicePlayerView;

    private Dialog dialog;


    private CameraAndPicureFn caneraAndPicureFn;
    private VideoUploadFn videoUploadFn;
    private SynOSCFn synOSCFn;
    private UploadSingleFileFn uploadSingleFileFn;
    private QRCode qrCode;

    private WebViewChromeClient chromeClient;
    private WebClient webViewClient;

    private String url;
    private String name;
    private int isShowTitle;
    private String urlParam;
    private String faceImgPath;
    private boolean isFinish;
    private boolean isAll;
    private String publicId;
    private String urlEnd;
    private String userId;
    private ImageView back, forward, close;

    private View.OnClickListener onVoiceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v.equals(back)) {
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadUrl("javascript:previousRecordFn()");
                    }
                });
            }
            else if (v.equals(forward)) {
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadUrl("javascript:nextRecordFn()");
                    }
                });
            }
            else if (v.equals(close)) {
                voicePlayerView.destory();
                voicePlayerView.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.model_public_account_chat_webview_act);
        netConfig = Sps_NetConfig.getNetConfigFromSpf(this);
        if (!TextUtils.isEmpty(netConfig.proxyIP) && !TextUtils.isEmpty(netConfig.proxyPort)) {
            //            Properties prop = System.getProperties();
            // 设置http访问要使用的代理服务器的地址
            System.setProperty("http.proxyHost", netConfig.proxyIP);
            // 设置http访问要使用的代理服务器的端口titlebar_block
            System.setProperty("http.proxyPort", netConfig.proxyPort);
            // 设置不需要通过代理服务器访问的主机，可以使用*通配符，多个地址用|分隔
            System.setProperty("http.nonProxyHosts", netConfig.nonProxyHosts);
        }

        dealIntent();
        mPicUploadManager = new PicUploadManager(PAWebViewAct.this);
        mPicUploadManager.initAsyUI();
        mPicUploadManager.setManagerListen(managerSynListen);
        mVideoUploadManager = new VideoUploadManager(PAWebViewAct.this);
        mVideoUploadManager.initAsyUI();
        mVideoUploadManager.setManagerListen(mVideoManagerListen);

        initViews();

        initWebView();

    }

    private void dealIntent() {
        Intent intent = getIntent();

        url = intent.getStringExtra(PAWebView.EXTRA_URL);
        Log.d("dym------------------->", "PAWebViewAct url= " + url);
        name = intent.getStringExtra(PAWebView.EXTRA_NAME);
        isShowTitle = intent.getIntExtra(PAWebView.EXTRA_IS_SHOW_TITLE, 1);
        urlParam = intent.getStringExtra(PAWebView.EXTRA_URL_PARAM);

        faceImgPath = intent.getStringExtra(PAWebView.EXTRA_FACE);
        isFinish = intent.getBooleanExtra(PAWebView.EXTRA_IS_FINISH, false);
        isAll = intent.getBooleanExtra(PAWebView.EXTRA_IS_ALL, false);
        publicId = intent.getStringExtra(PAWebView.EXTRA_PUBLIC_ID);
        urlEnd = intent.getStringExtra(PAWebView.EXTRA_URL_END);
        userId = intent.getStringExtra(PAWebView.EXTRA_USER_ID);
        if (!TextUtils.isEmpty(publicId)) {
            ClearTipUtils.clearTip(this, publicId);
        }
        if (!TextUtils.isEmpty(urlEnd)) {
            if (url.contains("?")) {
                url += "&" + urlEnd;
            }
            else {
                url += "?" + urlEnd;
            }
        }
    }


    private void initViews() {

        webView = findViewById(R.id.webView1);
        voicePlayerView = findViewById(R.id.newpublicrtVoiceView);
        back = voicePlayerView.findViewById(R.id.back);
        back.setOnClickListener(onVoiceClickListener);
        forward = voicePlayerView.findViewById(R.id.forward);
        forward.setOnClickListener(onVoiceClickListener);
        close = voicePlayerView.findViewById(R.id.close);
        close.setOnClickListener(onVoiceClickListener);

        titleView = new WebTitleView(this, webView, voicePlayerView, webTitleInterface);
        titleView.setTitle(name);
        if (isShowTitle == 1) {
            titleView.setVisible(View.VISIBLE);
        }
        else {
            titleView.setVisible(View.GONE);
        }
        voicePlayerView.setWebviewListener(webView);
        dialog = DialogProgress.get(this, "通知", "数据加载中...");
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        //初始化js的接口
        caneraAndPicureFn = new CameraAndPicureFn(this, this, mPicUploadManager, userId);
        videoUploadFn = new VideoUploadFn(this, this, mVideoUploadManager, userId);
        uploadSingleFileFn = new UploadSingleFileFn(this);
        synOSCFn = new SynOSCFn(this, titleView, voicePlayerView);
        qrCode = new QRCode(webView, this);

        //webView's Setting的初始化
        // 如果访问的页面中有Javascript，则webview必须设置支持Javascript。
        webView.getSettings().setJavaScriptEnabled(true);
        // 允许js弹出窗口r
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // 设置是否使用最后一次缓存
        //优先使用缓存
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 不使用缓存
        //webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setDomStorageEnabled(true);
        // 触摸焦点起作用,如果不设置，则在点击网页文本输入框时，不能弹出软键盘及不响应其他的一些事件。
        webView.requestFocus();
        chromeClient = new WebViewChromeClient(this);
        webView.setWebChromeClient(chromeClient);
        webViewClient = new WebClient();
        webView.setWebViewClient(webViewClient);
        webViewClient.setWebClientInterface(webClientInterface);

        webView.addJavascriptInterface(synOSCFn, "synosc");
        webView.addJavascriptInterface(new SyncValidator(this), "fi");// 提供指纹认证接口
        webView.addJavascriptInterface(qrCode, "qi");        // 集成二维码扫描
        webView.addJavascriptInterface(caneraAndPicureFn, "camera");   //提供相册相机接口
        webView.addJavascriptInterface(videoUploadFn, "videosupload");   //提供视频上传接口
        webView.addJavascriptInterface(uploadSingleFileFn, "synoscfile");
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");
        webView.loadUrl(url);
    }


    public String getFaceImg() {
        return PicUtil.Bitmap2StrByBase64(faceImgPath);
    }

    public String getUrlParam() {
        return urlParam;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();// 返回上一页面
//                webView.reload();
                voicePlayerView.destory();
                voicePlayerView.setVisibility(View.GONE);
                return true;
            }
            else {
                activiyCloseCheck();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        webView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        webView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {

        //退出界面做清除缓存的工作
        //同其他组了解之后，其中cookie存的是用户信息，localStorage存的是配置参数比如网络ip，端口之类的
        //其中localStorage的缓存引起的问题，实际上代码逻辑上出现问题，当后台参数更新后，需要将新的参数
        //存入localStorage，不然的话读取到localStorage里面旧的数据就会出现错误。
        //关于cookie存取的都是用户之类的信息，需要在切换用户的时候进行清理，当同一个用户的时候就没必要
        //进行清理。所以这里做出的处理是当在注销的时候，进行webview的缓存清理。
        // clearCookie();
        // clearLocalStorage();
        webView.destroy();
        super.onDestroy();
        if (mPicUploadManager != null) {
            mPicUploadManager.destroy();
            mPicUploadManager = null;
        }
        if (mVideoUploadManager != null) {
            mVideoUploadManager.destroy();
            mVideoUploadManager = null;
        }
        if (voicePlayerView != null) {
            voicePlayerView.destory();
        }
        if (qrCode != null) {
            qrCode.destroy();
        }
        if (uploadSingleFileFn != null) {
            uploadSingleFileFn.destroy();
        }
        if (!TextUtils.isEmpty(publicId)) {
            ClearTipUtils.clearTip(this, publicId);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WebViewChromeClient.REQUEST_CODE) {
            // 经过上边(1)、(2)两个赋值操作，此处即可根据其值是否为空来决定采用哪种处理方法
            if (chromeClient.mUploadMessage != null) {
                chromeClient.chooseBelow(resultCode, data);
            }
            else if (chromeClient.mUploadCallbackAboveL != null) {
                chromeClient.chooseAbove(resultCode, data);
            }
            else {
                Toast.makeText(this, "发生错误", Toast.LENGTH_SHORT).show();
            }
        }
        switch (requestCode) {
            case VideoUploadManager.VIDEO_RECPRD://拍摄视频完毕
            case VideoUploadManager.LOCAL_VIDEO_REQUEST_CODE://本地视频获取完毕
            case VideoUploadManager.LOCAL_VIDEO_COMPRESS://压缩完
                if (mVideoUploadManager != null) {
                    mVideoUploadManager.onActivityResult(requestCode, resultCode, data);
                }
                break;
            case PhotoPicker.REQUEST_CODE://图片选择返回
            case PicUploadManager.ONLY_CAMERA://纯相机返回
                if (mPicUploadManager != null) {
                    mPicUploadManager.onActivityResult(requestCode, resultCode, data);
                }
                break;
            case QRCode.REQUEST_CODE:
                if (qrCode != null) {
                    qrCode.onActivityResult(requestCode, resultCode, data);
                }
                break;
            case UploadSingleFileFn.RESQUEST_CODE:
                if (uploadSingleFileFn != null) {
                    uploadSingleFileFn.onActivityResult(requestCode, resultCode, data);
                }
                break;
            default:
        }
    }

    private PicUploadManager.ManagerListen managerSynListen = new PicUploadManager.ManagerListen() {
        @Override
        public void updateBack(final String re) {
            if (webView == null) return;
            webView.post(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl("javascript:getImgsUploadBack('" + re + "')");
                }
            });
        }
    };
    private VideoUploadManager.ManagerListen mVideoManagerListen = new VideoUploadManager.ManagerListen() {
        @Override
        public void updateBack(final String re) {
            if (webView == null) return;
            webView.post(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl("javascript:getVideosUploadBack('" + re + "')");
                }
            });
        }
    };

    WebTitleView.WebTitleInterface webTitleInterface = new WebTitleView.WebTitleInterface() {
        @Override
        public void closeActivity() {
            //检测是否正在上传
            activiyCloseCheck();
        }
    };

    public void activiyCloseCheck() {
        boolean isUploading = false;
        if (mVideoUploadManager != null) {
            isUploading = mVideoUploadManager.isStart();
        }
        if (!isUploading && mPicUploadManager != null) {
            isUploading = mPicUploadManager.isStart();
        }
        if (isUploading) {
            showCancelUploadDialog();
        }
        else {
            closeActiviy();
        }
    }

    private WebClient.WebClientInterface webClientInterface = new WebClient.WebClientInterface() {
        @Override
        public void onPageStarted() {
            if (dialog != null) {
                dialog.show();
            }
        }


        @Override
        public void onPageFinished() {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }


        @Override
        public void onReceivedError() {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    };


    private void closeActiviy() {
        PAWebViewAct.this.finish();
    }

    private MaterialDialog cancelUploadDialog = null;

    private void showCancelUploadDialog() {
        if (cancelUploadDialog == null) {
            cancelUploadDialog = new MaterialDialog(this);
            cancelUploadDialog.isTitleShow(false)
                    .content(
                            "是否停止上传并退出？")
                    .btnText("否", "是")
                    .show();
            cancelUploadDialog.setOnBtnClickL(
                    new OnBtnClickL() {//left btn click listener
                        @Override
                        public void onBtnClick() {
                            cancelUploadDialog.dismiss();
                        }
                    },
                    new OnBtnClickL() {//right btn click listener
                        @Override
                        public void onBtnClick() {
                            cancelUploadDialog.dismiss();
                            closeActiviy();
                        }
                    }
            );
            cancelUploadDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    cancelUploadDialog.dismiss();
                }
            });
        }
        else {
            cancelUploadDialog.show();
        }
    }
}