package synway.common.newCamera;

import android.content.Context;

import synway.common.camerainterface.CameraInterface;

/**
 * Created by zjw on 2017/7/24.
 */

public class CameraState extends CameraInterface {
    private static CameraState cameraState = null;

    public static CameraState instance() {
        return cameraState;
    }

    @Override
    public void onAppCreat(Context context) {
        super.onAppCreat(context);
        cameraState = this;
    }
}
