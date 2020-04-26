package synway.common.camerainterface;

import android.content.Context;

/**
 * Created by zjw on 2017/7/21.
 */

public abstract class CameraInterface {
    private Context context;

    public void onAppCreat(Context context) {
        this.context = context;
    }

    public CameraStateHandler cameraStateHandler;
}
