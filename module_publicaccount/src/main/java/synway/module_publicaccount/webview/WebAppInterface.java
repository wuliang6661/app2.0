package synway.module_publicaccount.webview;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.HashMap;
import java.util.Map;

import synway.module_interface.config.userConfig.LoginUser;
import synway.module_interface.config.userConfig.Sps_RWLoginUser;
import synway.module_publicaccount.Main;
import synway.module_publicaccount.db.table_util.Table_PublicAccount;
import synway.module_publicaccount.db.table_util.Table_PublicConfigMsg;
import synway.module_publicaccount.public_chat.PAWebView;
import synway.module_publicaccount.weex_module.beans.WxMapData;

/**
 * Created by wuliang on 2017/4/11.
 * <p>
 * 此处存放所有Html页面js调用的方法
 */

public class WebAppInterface {

    Activity mContext;

    public static String cardId = "";   //保存在本地的身份证号，供全息取值
    public static String mobile = "";    //全息需要取的参数

    public WebAppInterface(Activity context) {
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


    @JavascriptInterface
    public void goWeexByIdCard(String card, String message) {
        String id = getPublicAccountData("全息档案");
        if (!StringUtils.isEmpty(id)) {
            String url = getPublicMes(id);
            if (!StringUtils.isEmpty(url)) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(url.replace("search", "index"));
                intent.setData(uri);
                intent.putExtra("Title", "全息档案");
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.addCategory("com.SynwayOsc.android.intent.category.WEEX");
                Map<String, Object> params = new HashMap<>();
                params.put("SourceUrl", urlParam(id));
                params.put("UserId", Sps_RWLoginUser.readUser(mContext).ID);
                params.put("PaId", "8e5fbc6d-4456-4ac0-b3a9-1b086ff64666 ");
                params.put("selectData", card);
                params.put("modeid", message);
                WebAppInterface.cardId = card;
                WebAppInterface.mobile = message;
                WxMapData map = new WxMapData();
                map.setWxMapData(params);
                intent.putExtra("DATA", map);
                mContext.startActivity(intent);
            }
        } else {
            ToastUtils.showShort("当前用户无全息档案权限！");
        }
    }

    /**
     * 打开一个新的html 页面
     */
    @JavascriptInterface
    public void goHtml(String url, boolean isShowTitle) {
        PAWebView.builder()
                .setUrl(url)
                .setIsShowTitle(isShowTitle ? 1 : 0)
                .start(mContext);
    }


    @JavascriptInterface
    public String getLoginCode() {
        LoginUser user = Sps_RWLoginUser.readUser(mContext);
        return user.LoginCode;
    }


    @JavascriptInterface
    public void copyText(String text) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(text);
        ToastUtils.showShort("已复制到剪贴板！");
    }


    /**
     * 判断公众号是否被授权
     *
     * @param publicName 公众号名
     * @return 是否被授权
     */
    public String getPublicAccountData(String publicName) {
        String sql = "select * " + " from " + Table_PublicAccount._TABLE_NAME + " where " +
                Table_PublicAccount.FC_NAME + "='" + publicName + "'";
        Cursor cursor = Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase().rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            String pubId = cursor.getString(cursor.getColumnIndex(Table_PublicAccount.FC_ID));
            cursor.close();
            return pubId;
        }
        cursor.close();
        return null;
    }


    /**
     * 获取公众号信息
     */
    public String getPublicMes(String publicId) {
        String sql = "select * from " + Table_PublicConfigMsg._TABLE_NAME + " where " + Table_PublicConfigMsg.PAC_ID + " = '" + publicId + "'";
        Cursor cursor = Main.instance().moduleHandle.getSQLiteHelp().getReadableDatabase().rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            int publicUrlType = cursor.getInt(cursor.getColumnIndex(Table_PublicConfigMsg.PAM_PublicUrlType));
            String publicUrl = cursor.getString(cursor.getColumnIndex(Table_PublicConfigMsg.PAM_PublicUrl));
            cursor.close();
            return publicUrl;
        }
        cursor.close();
        return null;
    }


    public String urlParam(String publicId) {
        String sql = "select * from " + Table_PublicConfigMsg._TABLE_NAME + " where " + Table_PublicConfigMsg.PAC_ID + " = '" + publicId + "'";
        Cursor cursor = Main.instance().moduleHandle.getSQLiteHelp().getReadableDatabase().rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            String urlParams = cursor.getString(cursor.getColumnIndex(Table_PublicConfigMsg.PAC_SourceUrl));
            cursor.close();
            return urlParams;
        }
        cursor.close();
        return null;
    }

}
