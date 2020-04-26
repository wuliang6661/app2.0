package synway.module_publicaccount.weex_module.extend.moudle;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import java.util.HashMap;
import java.util.Map;

import synway.module_interface.config.userConfig.Sps_RWLoginUser;
import synway.module_publicaccount.public_chat.PAWebView;
import synway.module_publicaccount.webview.WebAppInterface;
import synway.module_publicaccount.weex_module.beans.WxMapData;
import synway.module_publicaccount.weex_module.extend.interfaces.PageOperaInterface;

/**
 * Created by huangxi on 2018/9/15.
 * 说明：
 */

public class WxBaseModule extends WXModule {

    @JSMethod(uiThread = true)
    public void printLog(String msg) {
        Toast.makeText(mWXSDKInstance.getContext(), msg, Toast.LENGTH_LONG).show();
    }

    // 原uiThread设置为false, 导致vue按顺序调用jump和finish方法后，先执行了finish方法
    @JSMethod(uiThread = true)
    public void finishActivity() {
//        Intent intent = new Intent();
//        String backtype = "backkey";
//        intent.putExtra("backtype", backtype);
//        ((Activity) mWXSDKInstance.getContext()).setResult(3, intent);
        ((PageOperaInterface) (mWXSDKInstance.getContext())).closeActivity();
//        ((Activity) mWXSDKInstance.getContext()).finish();
    }

    @JSMethod(uiThread = true)
    public void getUserInfo(JSCallback callback) {
        String userInfo = Sps_RWLoginUser.readUser(mWXSDKInstance.getContext()).userInfoJson;
        if (userInfo == null) {
            userInfo = "";
        }
        callback.invoke(userInfo);
    }


    /**
     * weex 调用跳转进入Html的方法
     */
    @JSMethod(uiThread = true)
    public void goHtml(String url, boolean isShowTitle) {
        PAWebView.builder()
                .setUrl(url)
                .setIsShowTitle(isShowTitle ? 1 : 0)
                .start((Activity) mWXSDKInstance.getContext());
    }


    /**
     * weex页面回调取参数
     */
    @JSMethod(uiThread = true)
    public void getDataByCardId(JSCallback callback) {
        Map<String, String> datas = new HashMap<>();
        datas.put("selectData", WebAppInterface.cardId);
        datas.put("modeid", WebAppInterface.mobile);
        callback.invoke(datas);
    }


    /**
     * 主线程执行页面跳转函数
     *
     * @param urerId      用户Id
     * @param paId        公众号Id
     * @param url         跳转地址
     * @param title       标题名
     * @param wxSourceUrl 备注信息（图片地址，服务器地址）
     * @param data        传递值
     */
    @JSMethod(uiThread = true)
    public void jumpActivity(String urerId, String paId, String url, String title, String wxSourceUrl, Map<String, Object> data) {
        if (!TextUtils.isEmpty(url)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            StringBuilder builder = new StringBuilder();
            builder.append(url);
            Uri uri = Uri.parse(builder.toString());
            if (TextUtils.equals("http", uri.getScheme()) || TextUtils.equals("https", uri.getScheme())) {
                intent.setData(uri);
                intent.putExtra("Title", title);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.addCategory("com.SynwayOsc.android.intent.category.WEEX");
                Map<String, Object> params = new HashMap<>();
                params.put("SourceUrl", wxSourceUrl);
                params.put("UserId", urerId);
                params.put("PaId", paId);
                params.put("TransferData", data);
                WxMapData map = new WxMapData();
                map.setWxMapData(params);
                intent.putExtra("DATA", map);
                Activity activity = (Activity) mWXSDKInstance.getContext();
                activity.startActivity(intent);
            } else {
                Toast.makeText(mWXSDKInstance.getContext(), "跳转地址格式错误,请检查格式", Toast.LENGTH_LONG).show();
            }

        }
    }

    /**
     * 主线程页面跳转函数
     *
     * @param urerId      用户Id
     * @param paId        公众号Id
     * @param url         跳转地址
     * @param isShowTitle 是否显示标题栏
     * @param title       标题名称
     * @param wxSourceUrl 备注信息（图片地址，服务器地址）
     * @param data        传递值
     */
    @JSMethod(uiThread = true)
    public void jumpNewActivity(String urerId, String paId, String url, int isShowTitle, String title, String wxSourceUrl, Map<String, Object> data) {
        if (!TextUtils.isEmpty(url)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            StringBuilder builder = new StringBuilder();
            builder.append(url);
            Uri uri = Uri.parse(builder.toString());
            if (TextUtils.equals("http", uri.getScheme()) || TextUtils.equals("https", uri.getScheme())) {
                intent.setData(uri);
                intent.putExtra("Title", title);
                intent.putExtra("IsShowTitle", isShowTitle);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.addCategory("com.SynwayOsc.android.intent.category.WEEX");
                Map<String, Object> params = new HashMap<>();
                params.put("SourceUrl", wxSourceUrl);
                params.put("UserId", urerId);
                params.put("PaId", paId);
                params.put("TransferData", data);
                WxMapData map = new WxMapData();
                map.setWxMapData(params);
                intent.putExtra("DATA", map);
                Activity activity = (Activity) mWXSDKInstance.getContext();
                activity.startActivity(intent);
            } else {
                Toast.makeText(mWXSDKInstance.getContext(), "跳转地址格式错误,请检查格式", Toast.LENGTH_LONG).show();
            }

        }
    }

}
