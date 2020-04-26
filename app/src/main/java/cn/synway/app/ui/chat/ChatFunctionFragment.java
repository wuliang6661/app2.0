package cn.synway.app.ui.chat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import cn.synway.app.R;
import cn.synway.app.db.table.MessageEntry;
import cn.synway.app.utils.JxdUtils;
import cn.synway.app.widget.imutil.Constants;
import cn.synway.app.widget.imutil.PhotoUtils;
import synway.common.base.BaseFragment;


/**
 * 发送文件的菜单处理类
 */
public class ChatFunctionFragment extends BaseFragment {


    private static final String TAG = "ChatFunctionFragment";
    private View rootView;
    private static final int CODE_TAKE_PHOTO = 0x111;
    private static final int CODE_CROP_PHOTO = 0xa2;
    private static final int REQUEST_CODE_PICK_IMAGE = 0xa3;
    private static final int REQUEST_CODE_PICK_FILE = 0xa4;
    private static final int REQUEST_CODE_SNAP = 0xa5;

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 0xa6;   //拍照权限同意
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE_CODE = 0xa7;  //相册权限
    private static final int MY_PERMISSIONS_REQUEST_CAMERACODE = 0xa8;

    private static final int MY_PERMISSIONS_REQUEST_SNAP = 0xa9;

    private File fileUri = new File(Environment.getExternalStorageDirectory().getPath() + "/photo.jpg");
    private Uri imageUri;
    private Uri cropImageUri;
    TextView tvCapture, tvAlbum, tvContact, tvCloud, tvFile, tvSnap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_chat_function, container, false);
            findViewByIds(rootView);
            setItemClick();
        }
        return rootView;
    }

    /**
     * 初始化布局
     */
    private void findViewByIds(View rootView) {
        tvCapture = rootView.findViewById(R.id.chat_function_capture);
        tvAlbum = rootView.findViewById(R.id.chat_function_album);
        tvContact = rootView.findViewById(R.id.chat_function_contact);
        tvCloud = rootView.findViewById(R.id.chat_function_cloud);
        tvFile = rootView.findViewById(R.id.chat_function_file);
        tvSnap = rootView.findViewById(R.id.chat_function_snap);
    }

    /**
     * 权限处理
     */
    private void autoObtainCameraPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
                Toast.makeText(getActivity(), "您已拒绝过一次", Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CAMERACODE);
        } else {//有权限直接调用系统相机拍照
            imageUri = Uri.fromFile(fileUri);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                imageUri = FileProvider.getUriForFile(getActivity(), Constants.AUTHORITY, fileUri);//通过FileProvider创建一个content类型的Uri
            PhotoUtils.takePicture(this, imageUri, CODE_TAKE_PHOTO);
        }
    }

    /***
     * 设置点击事件
     */
    public void setItemClick() {
        tvCapture.setOnClickListener(v -> autoObtainCameraPermission());
        tvAlbum.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_STORAGE_CODE);

            } else {
                choosePhoto();
            }
        });
        tvFile.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_STORAGE_CODE);
            } else {
                chooseFile();
            }
        });
        tvSnap.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_SNAP);

            } else {
                snapChat();
            }
        });

        tvCloud.setOnClickListener(v -> {

        });


        tvContact.setOnClickListener(v -> {
        });
    }


    /**
     * 打开文件选择器
     */
    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE_PICK_FILE);
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        imageUri = Uri.fromFile(fileUri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(getActivity(), Constants.AUTHORITY, fileUri);
            PhotoUtils.takePicture(this, imageUri, CODE_TAKE_PHOTO);
        }
    }

    /**
     * 从相册选取图片
     */
    private void choosePhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    /**
     * 阅后即焚
     */
    private void snapChat() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, REQUEST_CODE_SNAP);
    }


    public void onActivityResult(int req, int res, Intent data) {
        switch (req) {
            case CODE_TAKE_PHOTO:    //拍照
                if (res == Activity.RESULT_OK) {
                    MessageEntry messageEntry = new MessageEntry();
                    messageEntry.setFilepath(fileUri.getAbsolutePath());
                    messageEntry.setFileType(Constants.CHAT_FILE_TYPE_IMAGE);
                    EventBus.getDefault().post(messageEntry);
                }
                break;
            case CODE_CROP_PHOTO:    //裁剪
                if (res == Activity.RESULT_OK) {
                    try {
                        MessageEntry messageEntry = new MessageEntry();
                        messageEntry.setFilepath(cropImageUri.getPath());
                        messageEntry.setFileType(Constants.CHAT_FILE_TYPE_IMAGE);
                        EventBus.getDefault().post(messageEntry);
                    } catch (Exception e) {
                    }
                } else {
                    Log.d(Constants.TAG, "失败");
                }

                break;
            case REQUEST_CODE_PICK_IMAGE:    //相册
                if (res == Activity.RESULT_OK) {
                    try {
                        Uri uri = data.getData();
                        MessageEntry messageEntry = new MessageEntry();
                        messageEntry.setFilepath(getImageRealPathFromURI(uri));
                        messageEntry.setFileType(Constants.CHAT_FILE_TYPE_IMAGE);
                        EventBus.getDefault().post(messageEntry);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(Constants.TAG, e.getMessage());
                    }
                } else {
                    Log.d(Constants.TAG, "失败");
                }
                break;
            case REQUEST_CODE_SNAP:        //阅后即焚
                if (res == Activity.RESULT_OK) {
                    try {
                        Uri uri = data.getData();
                        MessageEntry messageEntry = new MessageEntry();
                        messageEntry.setFilepath(getImageRealPathFromURI(uri));
                        messageEntry.setFileType(Constants.CHAT_FILE_TYPE_SNAP);
                        EventBus.getDefault().post(messageEntry);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(Constants.TAG, e.getMessage());
                    }
                } else {
                    Log.d(Constants.TAG, "失败");
                }
                break;
            case REQUEST_CODE_PICK_FILE:    //文件夹
                if (res == Activity.RESULT_OK) {
                    try {
                        Uri uri = data.getData();
                        Log.e(TAG, "onActivityResult: ->" + uri.getPath());
                        MessageEntry messageEntry = new MessageEntry();
                        messageEntry.setFilepath(JxdUtils.getPath(getActivity(), uri));
                        messageEntry.setFileType(Constants.CHAT_FILE_TYPE_FILE);
                        EventBus.getDefault().post(messageEntry);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(Constants.TAG, e.getMessage());
                    }
                } else {
                    Log.d(Constants.TAG, "失败");
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
                    showToast("请同意系统权限后继续");
                }
                break;
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    choosePhoto();
                } else {
                    showToast("请同意系统权限后继续");
                }
                break;
            case MY_PERMISSIONS_REQUEST_SNAP:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    snapChat();
                } else {
                    showToast("请同意系统权限后继续");
                }
                break;
            case MY_PERMISSIONS_REQUEST_CAMERACODE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    imageUri = Uri.fromFile(fileUri);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        PhotoUtils.takePicture(this, imageUri, CODE_CROP_PHOTO);
                    }
                }
                break;
        }
    }

    public String getImageRealPathFromURI(Uri contentUri) {
        //TODO upload file、image、voice then return url;
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
}
