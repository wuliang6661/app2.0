package synway.common.watermaker;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/6/513:38
 * desc   :
 * version: 1.0
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.StringUtils;

import synway.common.R;


/**
 * 描述：水印工具
 */
public class WaterMarkUtil {

    public static String mWaterMarkDesc = "";

    private static WaterMarkUtil waterMarkUtil;

    public static WaterMarkUtil getInstance() {
        if (waterMarkUtil == null) {
            waterMarkUtil = new WaterMarkUtil();
        }
        return waterMarkUtil;
    }


    /**
     * 显示水印布局
     *
     * @param activity
     */
    public void showWatermarkView(Activity activity) {
        if (StringUtils.isEmpty(mWaterMarkDesc)) {
            return;
        }
        final ViewGroup rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        View framView = LayoutInflater.from(activity).inflate(R.layout.watermark, null);
        //可对水印布局进行初始化操作
        rootView.addView(framView);
    }


}
