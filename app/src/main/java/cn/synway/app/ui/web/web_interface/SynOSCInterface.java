package cn.synway.app.ui.web.web_interface;

import android.webkit.JavascriptInterface;

import cn.synway.app.ui.web.SynWebActivity;


/**
 * Created by leo on 2019/2/19.
 */

public class SynOSCInterface {

    private SynWebActivity paWebViewAct;

    public SynOSCInterface(SynWebActivity paWebViewAct) {
        this.paWebViewAct = paWebViewAct;
    }


    /**
     * 获取base64 格式的人脸图片
     */
    @JavascriptInterface
    public String getFaceImg() {
        return paWebViewAct.getFaceImg();
    }

    /***
     * 传递一个长字符串
     */
    @JavascriptInterface
    public String getUrlParam() {
        return paWebViewAct.getUrlParam();
    }

    @JavascriptInterface
    public void finishAct() {
        paWebViewAct.finish();
    }




    @JavascriptInterface
    public void showMap(String lon, String lat, String info) {

    }

}
