package cn.synway.app.ui.web;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.KeyEvent;

import com.blankj.utilcode.util.LogUtils;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import qyc.library.control.dialog_progress.DialogProgress;

/**
 * Created by wuliang on 2017/4/11.
 * <p>
 * 从名字上不难理解，这个类就像WebView的委托人一样，
 * 是帮助WebView处理各种通知和请求事件的，我们可以称他为WebView的“内政大臣”。
 */

public class X5WebClient extends WebViewClient {

    private Activity activity;

    /**
     * 滚动的菊花
     */
    private Dialog progressDialog = null;

    X5WebClient(Activity activity) {
        this.activity = activity;
    }


    protected void showProgress() {
        progressDialog = DialogProgress.get(activity, "加载中....",
                "message");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    protected void stopProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * 该方法在加载页面资源时会回调，每一个资源（比如图片）的加载都会调用一次。
     *
     * @param view
     * @param url
     */
    @Override
    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);
    }

    /**
     * 该方法在WebView开始加载页面且仅在Main frame loading（即整页加载）时回调，
     * 一次Main frame的加载只会回调该方法一次。我们可以在这个方法里设定开启一个加载的动画，告诉用户程序在等待网络的响应。
     *
     * @param view
     * @param url
     * @param favicon
     */
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        showProgress();
    }


    /**
     * 该方法只在WebView完成一个页面加载时调用一次（同样也只在Main frame loading时调用），
     * 我们可以可以在此时关闭加载动画，进行其他操作。
     *
     * @param view
     * @param url
     */
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        stopProgress();
    }

    /***
     * 该方法在web页面加载错误时回调，这些错误通常都是由无法与服务器正常连接引起的，最常见的就是网络问题。
     * 这个方法有两个地方需要注意：
     * 1.这个方法只在与服务器无法正常连接时调用，类似于服务器返回错误码的那种错误（即HTTP ERROR），
     * 该方法是不会回调的，因为你已经和服务器正常连接上了（全怪官方文档(︶^︶)）；
     * 2.这个方法是新版本的onReceivedError()方法，
     * 从API23开始引进，与旧方法onReceivedError(WebView view,int errorCode,String description,String failingUrl)
     * 不同的是，新方法在页面局部加载发生错误时也会被调用（比如页面里两个子Tab或者一张图片）。
     * 这就意味着该方法的调用频率可能会更加频繁，所以我们应该在该方法里执行尽量少的操作。
     *
     * @param view
     * @param request
     * @param error
     */
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        stopProgress();
    }


    /**
     * 上一个方法提到onReceivedError并不会在服务器返回错误码时被回调，
     * 那么当我们需要捕捉HTTP ERROR并进行相应操作时应该怎么办呢？API23便引入了该方法。
     * 当服务器返回一个HTTP ERROR并且它的status code>=400时，该方法便会回调。
     * 这个方法的作用域并不局限于Main Frame，任何资源的加载引发HTTP ERROR都会引起该方法的回调，
     * 所以我们也应该在该方法里执行尽量少的操作，只进行非常必要的错误处理等。
     *
     * @param view
     * @param request
     * @param errorResponse
     */
    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
    }

    /**
     * 当WebView加载某个资源引发SSL错误时会回调该方法，这时WebView要么执行handler.cancel()取消加载，
     * 要么执行handler.proceed()方法继续加载（默认为cancel）
     * 。需要注意的是，这个决定可能会被保留并在将来再次遇到SSL错误时执行同样的操作。
     *
     * @param view
     * @param handler
     * @param error
     */
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        super.onReceivedSslError(view, handler, error);
    }

    /**
     * 当WebView需要请求某个数据时，
     * 这个方法可以拦截该请求来告知app并且允许app本身返回一个数据来替代我们原本要加载的数据。
     * 比如你对web的某个js做了本地缓存，希望在加载该js时不再去请求服务器而是可以直接读取本地缓存的js，
     * 这个方法就可以帮助你完成这个需求。你可以写一些逻辑检测这个request，并返回相应的数据，
     * 你返回的数据就会被WebView使用，如果你返回null，WebView会继续向服务器请求。
     *
     * @param view
     * @param request
     * @return
     */
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        return super.shouldInterceptRequest(view, request);
    }


    /**
     * 哈~ 终于到了这个方法，在最开始的基础演示时我们用到了这个方法。
     * 从实践中我们知道，当我们没有给WebView提供WebViewClient时，
     * WebView如果要加载一个url会向ActivityManager寻求一个适合的处理者来加载该url（比如系统自带的浏览器），
     * 这通常是我们不想看到的。于是我们需要给WebView提供一个WebViewClient，
     * 并重写该方法返回true来告知WebView url的加载就在app中进行。这时便可以实现在app内访问网页。
     *
     * @param view
     * @param request
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        String url = request.getUrl().toString();
        LogUtils.i(url);
        if (url.startsWith("https") || url.startsWith("http")) {
            view.loadUrl(url);
        }
        return true;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        LogUtils.i(url);
        if (url.startsWith("https") || url.startsWith("http")) {
            view.loadUrl(url);
        }
        return true;
    }


    /**
     * 当WebView得页面Scale值发生改变时回调。
     *
     * @param view
     * @param oldScale
     * @param newScale
     */
    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        super.onScaleChanged(view, oldScale, newScale);
    }

    /**
     * 默认值为false，重写此方法并return true可以让我们在WebView内处理按键事件。
     *
     * @param view
     * @param event
     * @return
     */
    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        return super.shouldOverrideKeyEvent(view, event);
    }

}
