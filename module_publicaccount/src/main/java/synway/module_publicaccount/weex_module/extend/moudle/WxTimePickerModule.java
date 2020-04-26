package synway.module_publicaccount.weex_module.extend.moudle;

import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.common.WXModule;

import synway.module_publicaccount.weex_module.beans.TimePickerBean;
import synway.module_publicaccount.weex_module.extend.interfaces.OpenTimePickerInterface;

/**
 * Created by ztc on 2018/10/11.
 * 说明：时间选择器
 */

public class WxTimePickerModule extends WXModule {

    @JSMethod(uiThread = true)
    public void openTimePickerDialog(TimePickerBean timePickerBean) {
        ((OpenTimePickerInterface) mWXSDKInstance.getContext()).openTimePickerDialog(timePickerBean);

    }
}
