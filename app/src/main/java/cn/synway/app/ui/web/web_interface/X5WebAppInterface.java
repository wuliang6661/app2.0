package cn.synway.app.ui.web.web_interface;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import cn.synway.app.db.dbmanager.UserIml;
import cn.synway.app.db.table.UserEntry;
import cn.synway.app.ui.selectpersonforshare.SelectPersonForShareActivity;
import cn.synway.app.ui.web.SynWebBuilder;
import cn.synway.app.ui.weex.entity.WxMapData;
import cn.synway.app.utils.NetUtils;


/**
 * Created by wuliang on 2017/4/11.
 * <p>
 * 此处存放所有Html页面js调用的方法
 */

public class X5WebAppInterface {

    Activity mContext;

    public static String cardId = "";   //保存在本地的身份证号，供全息取值
    public static String mobile = "";    //全息需要取的参数

    public X5WebAppInterface(Activity context) {
        mContext = context;
    }

    /**
     * 此方法为例
     *
     * @param message
     */
    @JavascriptInterface
    public void showToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }


    @JavascriptInterface
    public void goToPersonMessage(String person) {
        Log.e("wuliang", person);
        Intent intent = new Intent("com.hm_leave.personMessage");
        Bundle bundle = new Bundle();
        bundle.putString("strPerson", person);
        bundle.putBoolean("isJson", true);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }


    @JavascriptInterface
    public void goToWaringCheck(String person) {
        Log.e("wuliang", person);
        Intent intent = new Intent("com.hm_leave.personMessage");
        Bundle bundle = new Bundle();
        bundle.putString("strPerson", person);
        bundle.putBoolean("isJson", true);
        intent.putExtras(bundle);
        intent.putExtra("isCheck", true);
        mContext.startActivity(intent);
    }


//    @JavascriptInterface
//    public void goWeexByIdCard(String card, String message) {
//        String id = getPublicAccountData("全息档案");
//        if (!StringUtils.isEmpty(id)) {
//            String url = getPublicMes(id);
//            if (!StringUtils.isEmpty(url)) {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                Uri uri = Uri.parse(url.replace("search", "index"));
//                intent.setData(uri);
//                intent.putExtra("Title", "全息档案");
//                intent.addCategory(Intent.CATEGORY_DEFAULT);
//                intent.addCategory("com.SynwayOsc.android.intent.category.WEEX");
//                Map<String, Object> params = new HashMap<>();
//                params.put("SourceUrl", urlParam(id));
//                params.put("UserId", Sps_RWLoginUser.readUser(mContext).ID);
//                params.put("PaId", "8e5fbc6d-4456-4ac0-b3a9-1b086ff64666 ");
//                params.put("selectData", card);
//                params.put("modeid", message);
//                X5WebAppInterface.cardId = card;
//                X5WebAppInterface.mobile = message;
//                WxMapData map = new WxMapData();
//                map.setWxMapData(params);
//                intent.putExtra("DATA", map);
//                mContext.startActivity(intent);
//            }
//        } else {
//            ToastUtils.showShort("当前用户无全息档案权限！");
//        }
//    }


    /**
     * 打开一个新的weex 页面
     */
    @JavascriptInterface
    public void goWeex(String url, String title, String card, String message) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(url);
        intent.setData(uri);
        if (StringUtils.isEmpty(title)) {
            intent.putExtra("IsShowTitle", 0);
        } else {
            intent.putExtra("Title", title);
            intent.putExtra("IsShowTitle", 1);
        }
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addCategory("com.SynwayOsc.android.intent.category.WEEX");
        Map<String, Object> params = new HashMap<>();
//        params.put("SourceUrl", urlParam(id));
        params.put("UserId", UserIml.getUser().getUserID());
        params.put("PaId", "8e5fbc6d-4456-4ac0-b3a9-1b086ff64666 ");
        params.put("selectData", card);
        params.put("modeid", message);
        X5WebAppInterface.cardId = card;
        X5WebAppInterface.mobile = message;
        WxMapData map = new WxMapData();
        map.setWxMapData(params);
        intent.putExtra("DATA", map);
        mContext.startActivity(intent);
    }


    /**
     * 打开一个新的html 页面
     */
    @JavascriptInterface
    public void goHtml(String url, boolean isShowTitle) {
        SynWebBuilder.builder()
                .setUrl(url)
                .setIsShowTitle(isShowTitle ? 1 : 0)
                .start(mContext);
    }


    @JavascriptInterface
    public String getLoginCode() {
        UserEntry entry = UserIml.getUser();
        return entry.getCode();
    }


    @JavascriptInterface
    public String getUserRole() {
        LogUtils.e(UserIml.getUser().getRoles());
        return UserIml.getUser().getRoles();
    }

    /**
     * 复制文字到剪贴板
     */
    @JavascriptInterface
    public void copyText(String text) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(text);
        ToastUtils.showShort("已复制到剪贴板！");
    }

    /**
     * 获取所有用户信息
     */
    @JavascriptInterface
    public String getUserInfo() {
        return new Gson().toJson(UserIml.getUser());
    }


    /**
     * 分享网址
     *
     * @param title    分享的标题
     * @param message  分享的内容
     * @param imageUrl 分享的图片地址
     * @param shareUrl 分享的网址
     */
    @JavascriptInterface
    public void shareUrl(String title, String message, String imageUrl, String shareUrl) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("message", message);
        bundle.putString("imageUrl", imageUrl);
        bundle.putString("shareUrl", shareUrl);
        Intent intent = new Intent(mContext, SelectPersonForShareActivity.class);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }


    /**
     * 获取本机IP地址
     */
    @JavascriptInterface
    public String getDevicesIP() {
        return NetUtils.getIpAddress(mContext);
    }

}
