package cn.synway.app.ui.web;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;


import java.io.File;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by wuliang on 2017/4/11.
 * <p>
 * 如果说WebViewClient是帮助WebView处理各种通知、请求事件的“内政大臣”的话，
 * 那么WebChromeClient就是辅助WebView处理Javascript的对话框，网站图标，网站title，加载进度等偏外部事件的“外交大臣”。
 */

public class X5WebChromeClient extends WebChromeClient {


    public ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> mUploadCallbackAboveL;
    public final static int REQUEST_CODE = 1234;
    private Activity activity;
    private Uri imageUri;


    public X5WebChromeClient(Activity context) {
        activity = context;
    }


    /**
     * 当页面加载的进度发生改变时回调，用来告知主程序当前页面的加载进度。
     *
     * @param view
     * @param newProgress
     */
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
    }


    /**
     * 用来接收web页面的icon，我们可以在这里将该页面的icon设置到Toolbar。
     *
     * @param view
     * @param icon
     */
    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        super.onReceivedIcon(view, icon);
    }

    /**
     * 用来接收web页面的title，我们可以在这里将页面的title设置到Toolbar。
     *
     * @param view
     * @param title
     */
    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
    }

    //以下两个方法是为了支持web页面进入全屏模式而存在的（比如播放视频），
    //如果不实现这两个方法，该web上的内容便不能进入全屏模式。


    /**
     * 该方法在当前页面进入全屏模式时回调，主程序必须提供一个包含当前web内容（视频 or Something）的自定义的View。
     *
     * @param view
     * @param callback
     */
//    @Override
//    public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback callback) {
//        super.onShowCustomView(view, callback);
//    }

    /**
     * 该方法在当前页面退出全屏模式时回调，主程序应在这时隐藏之前show出来的View。
     */
    @Override
    public void onHideCustomView() {
        super.onHideCustomView();
    }

    /**
     * 当我们的Web页面包含视频时，我们可以在HTML里为它设置一个预览图，WebView会在绘制页面时根据它的宽高为它布局。
     * 而当我们处于弱网状态下时，我们没有比较快的获取该图片，
     * 那WebView绘制页面时的gitWidth()方法就会报出空指针异常~ 于是app就crash了。。
     * 这时我们就需要重写该方法，在我们尚未获取web页面上的video预览图时，给予它一个本地的图片，避免空指针的发生。
     *
     * @return
     */
    @Override
    public Bitmap getDefaultVideoPoster() {
        return super.getDefaultVideoPoster();
    }

    /**
     * 重写该方法可以在视频loading时给予一个自定义的View，可以是加载圆环 or something。
     *
     * @return
     */
    @Override
    public View getVideoLoadingProgressView() {
        return super.getVideoLoadingProgressView();
    }

    /**
     * 处理Javascript中的Alert对话框。
     *
     * @return
     */
    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        return super.onJsAlert(view, url, message, result);
    }

    /**
     * 处理Javascript中的Prompt对话框。
     *
     * @return
     */
    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        return super.onJsPrompt(view, url, message, defaultValue, result);
    }

    /**
     * 处理Javascript中的Confirm对话框
     *
     * @return
     */
    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        return super.onJsConfirm(view, url, message, result);
    }

    /**
     * 该方法在用户进行了web上某个需要上传文件的操作时回调。我们应该在这里打开一个文件选择器，
     * 如果要取消这个请求我们可以调用filePathCallback.onReceiveValue(null)并返回true。
     *
     * @param webView
     * @param filePathCallback
     * @param fileChooserParams
     * @return
     */
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        mUploadCallbackAboveL = filePathCallback;
        takePhoto();
        return true;

    }


    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        takePhoto();
    }


    public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
        openFileChooser(uploadMsg);
    }


    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        openFileChooser(uploadMsg);
    }


    /**
     * 调用相机
     */
    private void takePhoto() {
        // 指定拍照存储位置的方式调起相机
        String filePath = Environment.getExternalStorageDirectory() + File.separator
                + Environment.DIRECTORY_PICTURES + File.separator;
        String fileName = "IMG_" + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
        imageUri = Uri.fromFile(new File(filePath + fileName));
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        Intent Photo = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent chooserIntent = Intent.createChooser(Photo, "Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});
        activity.startActivityForResult(chooserIntent, REQUEST_CODE);

    }

    /**
     * Android API < 21(Android 5.0)版本的回调处理
     *
     * @param resultCode 选取文件或拍照的返回码
     * @param data       选取文件或拍照的返回结果
     */
    public void chooseBelow(int resultCode, Intent data) {
        Log.e("WangJ", "返回调用方法--chooseBelow");

        if (Activity.RESULT_OK == resultCode) {
            updatePhotos();

            if (data != null) {
                // 这里是针对文件路径处理
                Uri uri = data.getData();
                if (uri != null) {
                    Log.e("WangJ", "系统返回URI：" + uri.toString());
                    mUploadMessage.onReceiveValue(uri);
                } else {
                    mUploadMessage.onReceiveValue(null);
                }
            } else {
                // 以指定图像存储路径的方式调起相机，成功后返回data为空
                Log.e("WangJ", "自定义结果：" + imageUri.toString());
                mUploadMessage.onReceiveValue(imageUri);
            }
        } else {
            mUploadMessage.onReceiveValue(null);
        }
        mUploadMessage = null;
    }

    /**
     * Android API >= 21(Android 5.0) 版本的回调处理
     *
     * @param resultCode 选取文件或拍照的返回码
     * @param data       选取文件或拍照的返回结果
     */
    public void chooseAbove(int resultCode, Intent data) {
        Log.e("WangJ", "返回调用方法--chooseAbove");

        if (Activity.RESULT_OK == resultCode) {
            updatePhotos();

            if (data != null) {
                // 这里是针对从文件中选图片的处理
                Uri[] results;
                Uri uriData = data.getData();
                if (uriData != null) {
                    results = new Uri[]{uriData};
                    for (Uri uri : results) {
                        Log.e("WangJ", "系统返回URI：" + uri.toString());
                    }
                    mUploadCallbackAboveL.onReceiveValue(results);
                } else {
                    mUploadCallbackAboveL.onReceiveValue(null);
                }
            } else {
                Log.e("WangJ", "自定义结果：" + imageUri.toString());
                mUploadCallbackAboveL.onReceiveValue(new Uri[]{imageUri});
            }
        } else {
            mUploadCallbackAboveL.onReceiveValue(null);
        }
        mUploadCallbackAboveL = null;
    }

    private void updatePhotos() {
        // 该广播即使多发（即选取照片成功时也发送）也没有关系，只是唤醒系统刷新媒体文件
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(imageUri);
        activity.sendBroadcast(intent);
    }
}

