package synway.common.camerainterface;

/**
 * Created by zjw on 2017/7/21.
 */

public interface  CameraStateHandler {

    void setCameraState(int state);

    int getCameraState();

    void setServiceTargetId(String id);

    String getServiceTargetId();

}
