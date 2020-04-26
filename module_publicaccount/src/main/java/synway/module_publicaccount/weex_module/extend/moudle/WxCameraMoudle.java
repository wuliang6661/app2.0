package synway.module_publicaccount.weex_module.extend.moudle;

import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.common.WXModule;

import synway.module_publicaccount.weex_module.interfaces.OpenCameraInterface;

/**
 * Created by huangxi on 2018/10/11.
 * 说明：
 */

public class WxCameraMoudle extends WXModule {
    /**
     * 打开系统摄像图接口
     */
    @JSMethod(uiThread = true)
    public void openCamera() {
        OpenCameraInterface openCameraInterface= ((OpenCameraInterface) mWXSDKInstance.getContext());
        openCameraInterface.openCamera();
    }

}
