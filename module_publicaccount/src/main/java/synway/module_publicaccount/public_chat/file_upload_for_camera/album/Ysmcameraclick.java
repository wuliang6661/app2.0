package synway.module_publicaccount.public_chat.file_upload_for_camera.album;

import android.content.Intent;
import android.os.Parcel;
import android.util.Log;
import android.view.View;

import me.iwf.photopicker.CameraClick;
import me.iwf.photopicker.PhotoPickerActivity;
import synway.module_interface.config.BaseUtil;
import synway.module_publicaccount.public_chat.WebviewFn.camera.PublicAccountCameraNew;


public class Ysmcameraclick extends CameraClick  {

    private int seed;

    public Ysmcameraclick() {
    }

    public Ysmcameraclick(int seed) {
        this.seed = seed;
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        String cameraBigPath = BaseUtil.ChatFileUtil.getChatPicPath(userid,
                targetid);
        String cameraSmallPath = BaseUtil.ChatFileUtil.getChatPicSmallPath(userid,
                targetid);
        Intent openCamera = new Intent();
        openCamera.setClass(context, PublicAccountCameraNew.class);
        openCamera.putExtra(PublicAccountCameraNew.EXTRA_BIGTHU_FOLDER_KEY, cameraBigPath);
        openCamera.putExtra(PublicAccountCameraNew.EXTRA_IS_BACK, 0);
        openCamera.putExtra(PublicAccountCameraNew.EXTRA_SMALLTHU_FOLDER_KEY, cameraSmallPath);
        openCamera.putExtra(PublicAccountCameraNew.EXTRA_IS_ADD_DESCRIPTION, false);
        openCamera.putExtra(PublicAccountCameraNew.EXTRA_IS_SHOW_IMAGE_CONTINUE, false);
        openCamera.putExtra(PublicAccountCameraNew.EXTRA_IS_SHOW_IMAGE_EDIT, false);
        openCamera.putExtra(PublicAccountCameraNew.EXTRA_IS_FROM_PACKER, true);
        openCamera.putExtra(PublicAccountCameraNew.EXTRA_SEED, seed);
        context.startActivityForResult(openCamera, PhotoPickerActivity.PLUS_CAMERA);

    }



    public static final Creator<Ysmcameraclick> CREATOR = new Creator<Ysmcameraclick>() {

        @Override
        public Ysmcameraclick createFromParcel(Parcel source) {
            Ysmcameraclick p = new Ysmcameraclick(source);
            return p;
        }

        @Override
        public Ysmcameraclick[] newArray(int size) {
            return new Ysmcameraclick[size];
        }
    };
    @Override
    public void writeToParcel(Parcel out, int flags)
    {
        out.writeInt(seed);
    }
    public Ysmcameraclick(Parcel in)
    {
        seed = in.readInt();
    }
}
