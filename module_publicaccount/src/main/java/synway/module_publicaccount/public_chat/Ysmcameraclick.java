package synway.module_publicaccount.public_chat;

import android.content.Intent;
import android.os.Parcel;
import android.view.View;

import me.iwf.photopicker.CameraClick;
import synway.common.newCamera.NewCamera;
import synway.module_interface.config.BaseUtil;


public class Ysmcameraclick extends CameraClick {

    @Override
    public void onClick(View v) {
        super.onClick(v);
        String cameraBigPath = BaseUtil.ChatFileUtil.getChatPicPath(userid,
                targetid);
        String cameraSmallPath = BaseUtil.ChatFileUtil.getChatPicSmallPath(userid,
                targetid);
        Intent openCamera = new Intent();
//        openCamera.setClass(context, Camera.class);
//        openCamera.putExtra(Camera.EXTRA_BIGTHU_FOLDER_KEY, cameraBigPath);
//        openCamera.putExtra(Camera.EXTRA_IS_BACK, 0);
//        openCamera.putExtra(Camera.EXTRA_SMALLTHU_FOLDER_KEY, cameraSmallPath);
        openCamera.setClass(context, NewCamera.class);
        openCamera.putExtra(NewCamera.EXTRA_BIGTHU_FOLDER_KEY, cameraBigPath);
        openCamera.putExtra(NewCamera.EXTRA_IS_BACK, 0);
        openCamera.putExtra(NewCamera.EXTRA_SMALLTHU_FOLDER_KEY, cameraSmallPath);
        context.startActivityForResult(openCamera,502);

    }

    public static final Creator<Ysmcameraclick> CREATOR = new Creator() {

        @Override
        public Ysmcameraclick createFromParcel(Parcel source) {
            Ysmcameraclick p = new Ysmcameraclick();
            return p;
        }

        @Override
        public Ysmcameraclick[] newArray(int size) {
            return new Ysmcameraclick[size];
        }
    };
}
