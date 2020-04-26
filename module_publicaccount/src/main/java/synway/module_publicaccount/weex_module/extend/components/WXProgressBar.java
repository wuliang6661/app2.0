package synway.module_publicaccount.weex_module.extend.components;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ProgressBar;

import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.ui.action.BasicComponentData;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXComponentProp;
import com.taobao.weex.ui.component.WXVContainer;

import org.json.JSONObject;

//import com.taobao.weex.dom.WXDomObject;

/**
 * @author XuKaiyin
 * @class com.sys.weexdemo.weex_module.extend.components
 * @name weex 加载转动圈
 * @describe 可用于页面未显示时
 * @time 2018/12/6 15:25
 */
public class WXProgressBar extends WXComponent<ProgressBar> {
    private ProgressBar mProgressBar;

        public WXProgressBar(WXSDKInstance instance, WXVContainer parent, BasicComponentData basicComponentData) {
        super(instance, parent, basicComponentData);
    }
//    public WXProgressBar(WXSDKInstance instance, WXDomObject dom, WXVContainer parent) {
//        super(instance, dom, parent);
//    }

    @Override
    protected ProgressBar initComponentHostView(@NonNull Context context) {
        mProgressBar = new ProgressBar(context);
        return mProgressBar;
    }

    @WXComponentProp(name = "progressBarColor")
    public void setColor(String color) {
        if (mProgressBar != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mProgressBar.setIndeterminateTintList(ColorStateList.valueOf(Color.parseColor(color)));
                mProgressBar.setIndeterminateTintMode(PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    @JSMethod
    public void setVisible() {
        if (mProgressBar != null && mProgressBar.getVisibility() == View.GONE) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @JSMethod
    public void setGone() {
        if (mProgressBar != null && mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @WXComponentProp(name = "center")
    public void setCenter(String center) {
        try {
            JSONObject jsonStr = new JSONObject(center);
            double latitude = jsonStr.getDouble("latitude");
            double longitude = jsonStr.getDouble("longitude");
            if (!jsonStr.has("zoomLevel")) {

            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
