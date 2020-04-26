package synway.module_publicaccount.public_chat.file_upload_for_camera.album;

import android.content.Intent;
import android.os.Parcel;
import android.view.View;

import java.io.File;

import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.PictureClick;
import synway.cameraNew.editimage.EditImageActivity;
import synway.cameraNew.utils.FileUtils;


public class PictureEditClick extends PictureClick {
    @Override
    public void onClick(View v) {
        Intent it = new Intent(context, EditImageActivity.class);
        it.putExtra(EditImageActivity.FILE_PATH, oriPath);
        File outputFile = FileUtils.getEmptyFile("tuya" + System.currentTimeMillis() + ".jpg");
        it.putExtra(EditImageActivity.EXTRA_OUTPUT, outputFile.getAbsolutePath());
        context.startActivityForResult(it, PhotoPickerActivity.PLUS_PICTURE);
        super.onClick(v);
    }

    public static final Creator<PictureEditClick> CREATOR = new Creator<PictureEditClick>() {

        @Override
        public PictureEditClick createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            PictureEditClick p = new PictureEditClick();
            return p;
        }

        @Override
        public PictureEditClick[] newArray(int size) {
            // TODO Auto-generated method stub
            return new PictureEditClick[size];
        }
    };
}
