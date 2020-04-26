package cn.synway.app.ui.weex;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.taobao.weex.WXSDKManager;
import com.taobao.weex.adapter.IWXImgLoaderAdapter;
import com.taobao.weex.common.WXImageStrategy;
import com.taobao.weex.dom.WXImageQuality;

/**
 * 图片下载功能
 */
public class WeexImageAdapter implements IWXImgLoaderAdapter {

    @Override
    public void setImage(final String url, final ImageView view,
                         WXImageQuality quality, final WXImageStrategy strategy) {

        WXSDKManager.getInstance().postOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (view == null || view.getLayoutParams() == null) {
                    return;
                }
                if (TextUtils.isEmpty(url)) {
                    view.setImageBitmap(null);
                    return;
                }
                String temp = url;
                // base64 start
                if (url.startsWith("data")) {
                    int index = url.indexOf(":");
                    String tempUrl = url.substring(index + 1,url.length());
                    Bitmap bitmap = stringtoBitmap(tempUrl);
                    view.setImageBitmap(bitmap);
                    return;
                }
                // base64 end
                if (url.startsWith("//")) {
                    temp = "http:" + url;
                }
                if (temp.startsWith("/images/")) {
                    //过滤掉所有相对位置
                    temp = temp.replace("../", "");
                    temp = temp.replace("./", "");
                    //替换asset目录的配置
                    temp = temp.replace("/images/", "file:///android_asset/weex/images/");
                    Log.d("WeexImageAdapter", "url:" + temp);
                }
                if (view.getLayoutParams().width <= 0 || view.getLayoutParams().height <= 0) {
                    return;
                }

                if (!TextUtils.isEmpty(strategy.placeHolder)) {
                    Glide.with(view.getContext()).load(Uri.parse(strategy.placeHolder)).into(view);
                }
                Glide.with(view.getContext()).load(temp).into(view);

            }
        }, 0);
    }
    public Bitmap stringtoBitmap(String string){
        //将字符串转换成Bitmap类型
        Bitmap bitmap=null;
        try {
            byte[]bitmapArray;
            bitmapArray= Base64.decode(string, Base64.DEFAULT);
            bitmap= BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

}
