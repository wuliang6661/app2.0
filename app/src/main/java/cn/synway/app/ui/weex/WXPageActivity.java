/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package cn.synway.app.ui.weex;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.google.gson.Gson;
import com.taobao.weex.IWXRenderListener;
import com.taobao.weex.RenderContainer;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.WXSDKManager;
import com.taobao.weex.adapter.IWXJSExceptionAdapter;
import com.taobao.weex.common.WXJSExceptionInfo;
import com.taobao.weex.common.WXRenderStrategy;
import com.taobao.weex.ui.component.NestedContainer;
import com.taobao.weex.utils.WXLogUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.synway.app.R;
import cn.synway.app.base.SynBaseActivity;
import cn.synway.app.db.dbmanager.UserIml;
import cn.synway.app.ui.weex.entity.WxMapData;
import cn.synway.app.ui.weex.exception.FileLog;
import cn.synway.app.ui.weex.exception.WeexErrorHandler;
import cn.synway.app.ui.weex.https.WXHttpManager;
import cn.synway.app.ui.weex.https.WXHttpTask;
import cn.synway.app.ui.weex.https.WXRequestListener;
import cn.synway.app.ui.weex.interfaces.PageOperaInterface;
import cn.synway.app.ui.weex.interfaces.RefreshInterface;


public class WXPageActivity extends SynBaseActivity implements
        IWXRenderListener, WXSDKInstance.NestedInstanceInterceptor,
        RefreshInterface, PageOperaInterface, IWXJSExceptionAdapter {

    private static final String TAG = "WXPageActivity";
    private ViewGroup mContainer;
    private ProgressBar mProgressBar;
    private WXSDKInstance mInstance;
    private Uri mUri;
    private HashMap mConfigMap = new HashMap<String, Object>();
    private Uri imageUri;
    public static File tempFile;
    public static final int PHOTO_REQUEST_CAREMA = 1111;// 拍照
    public static final int CROP_PHOTO = 2;
    private WeexTitleView weexTitleView;

    private String titleName;
    private int isShowTitle;
    private MaterialDialog cancelUploadDialog = null;
    //报错处理
    private WeexErrorHandler weexErrorHandler;
    private WxMapData wxMapData;

    @Override
    public void onCreateNestInstance(WXSDKInstance instance, NestedContainer container) {
        Log.d(TAG, "Nested Instance created.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mInstance != null) {
            mInstance.onActivityResume();
            // 发送onResume的生命周期到weex中
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Map<String, Object> params = new HashMap<>();
                    mInstance.fireGlobalEventCallback("onWxResume", params);
                }
            }, 500);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mInstance != null) {
            mInstance.onActivityPause();
            // 发送onPause的生命周期到weex中
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Map<String, Object> params = new HashMap<>();
                    mInstance.fireGlobalEventCallback("onWxPause", params);
                }
            }, 500);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mInstance != null) {
            mInstance.onActivityStop();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Map<String, Object> params = new HashMap<>();
                    mInstance.fireGlobalEventCallback("onWxStop", params);
                }
            }, 500);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mInstance != null) {
            mInstance.onActivityDestroy();
            WXSDKManager.getInstance().notifyTrimMemory();
        }
        mContainer = null;


    }

    @Override
    protected int getLayout() {
        return R.layout.model_activity_wxpage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUIAndData();
        weexErrorHandler = new WeexErrorHandler(this);
        WXSDKEngine.setJSExcetptionAdapter(this);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        wxMapData = (WxMapData) getIntent().getSerializableExtra("DATA");
        mUri = getIntent().getData();

        if (mUri == null) {
            Toast.makeText(this, "the uri is empty!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mInstance = new WXSDKInstance(this);
        loadWeex();
        mInstance.onActivityCreate();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (mInstance != null) {
                    Map<String, Object> params = new HashMap<>();
                    mInstance.fireGlobalEventCallback("onWxCreate", params);
                }
            }
        }, 1000);
    }

    private void loadWeex() {
        if (TextUtils.equals("http", mUri.getScheme()) || TextUtils.equals("https", mUri.getScheme())) {
            loadWXfromService(mUri.toString());
        }
        else {
            Toast.makeText(this, "跳转地址格式错误,请检查格式", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    private String assembleFilePath(Uri uri) {
        if (uri != null && uri.getPath() != null) {
            return uri.getPath().replaceFirst("/", "");
        }
        return "";
    }

    private void initUIAndData() {
        initToorBar();

        titleName = getIntent().getStringExtra("Title");
        isShowTitle = getIntent().getIntExtra("IsShowTitle", 1);
        weexTitleView = new WeexTitleView(this, this);
        weexTitleView.setTitle(titleName);
        weexTitleView.setRefreshListener(this);
        if (isShowTitle == 1) {
            weexTitleView.setVisible(View.VISIBLE);
        }
        else {
            weexTitleView.setVisible(View.GONE);
        }

        mContainer = findViewById(R.id.container);
        mProgressBar = findViewById(R.id.progress);

    }

    private void loadWXfromService(final String url) {

        mProgressBar.setVisibility(View.VISIBLE);
        RenderContainer renderContainer = new RenderContainer(this);
        mContainer.addView(renderContainer);
        mInstance.setRenderContainer(renderContainer);
        mInstance.registerRenderListener(this);
        mInstance.setNestedInstanceInterceptor(this);
        mInstance.setBundleUrl(url);
        mInstance.setTrackComponent(true);

        WXHttpTask httpTask = new WXHttpTask();
        httpTask.url = url;
        httpTask.requestListener = new WXRequestListener() {

            @Override
            public void onSuccess(WXHttpTask task) {
                Log.i(TAG, "into--[http:onSuccess] url:" + url);
                try {
                    mConfigMap.put("bundleUrl", url);
                    mInstance.render(TAG, new String(task.response.data, "utf-8"), mConfigMap, null, WXRenderStrategy.APPEND_ASYNC);
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(WXHttpTask task) {
                Log.i(TAG, "into--[http:onError]");
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "network error,请确认网络及相关配置！", Toast.LENGTH_SHORT).show();
                weexErrorHandler.writeWeexError("1", "network error", mUri.toString(), wxMapData);
            }
        };

        WXHttpManager.getInstance().sendRequest(httpTask);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera(this);
        }
        else {
            // 没有获取 到权限，从新请求，或者关闭app
            Toast.makeText(this, "需要存储权限", Toast.LENGTH_SHORT).show();
        }
        if (mInstance != null) {
            mInstance.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PHOTO_REQUEST_CAREMA:
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CROP_PHOTO); // 启动裁剪程序
                }
                break;
            case CROP_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver()
                                .openInputStream(imageUri));
                        String sss = String.valueOf(imageUri);
                        Map<String, Object> params = new HashMap<>();
                        params.put("imageUrl", sss);
                        mInstance.fireGlobalEventCallback("WxCameraPath", params);
                    }
                    catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }


    @Override
    public void onViewCreated(WXSDKInstance instance, View view) {
        WXLogUtils.e("into--[onViewCreated]");
        View wrappedView = null;
        if (wrappedView != null) {
            view = wrappedView;
        }

        if (view.getParent() == null) {
            mContainer.addView(view);
        }
        mContainer.requestLayout();
        Log.d("WARenderListener", "renderSuccess");
    }

    @Override
    public void onRenderSuccess(WXSDKInstance instance, int width, int height) {
        mProgressBar.setVisibility(View.INVISIBLE);

        if (wxMapData != null) {
            String userInfo = new Gson().toJson(UserIml.getUser());
            if (userInfo == null) {
                userInfo = "";
            }
            wxMapData.setWxMapData(wxMapData.addMapData("UserInfo", userInfo));
            Log.d("hx------------------->", "userInfoJson= " + userInfo);
            mInstance.fireGlobalEventCallback("WxData", wxMapData.getWxMapData());
        }
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onRefreshSuccess(WXSDKInstance instance, int width, int height) {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(WXSDKInstance instance, String errCode,
                            String msg) {
        mProgressBar.setVisibility(View.GONE);
        weexErrorHandler.writeWeexError(errCode, msg, mUri.toString(), wxMapData);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("错误" + errCode)
                .setCancelable(false)
                .setMessage("请到" + FileLog.getFolderPathToday(WeexErrorHandler.LOG_FOLDER_NAME) + "文件下查看错误详情")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                    }
                });
        builder.create().show();
    }


    //页面刷新接口
    @Override
    public void refreshWeexView() {
        // loadWeex();
    }

    //获取调取摄像头权限
    public void applyWritePermission() {

        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (Build.VERSION.SDK_INT >= 23) {
            int check = ContextCompat.checkSelfPermission(this, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (check == PackageManager.PERMISSION_GRANTED) {
                //调用相机
                openCamera(this);
            }
            else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        else {
            openCamera(this);
        }
    }

    public void openCamera(Activity activity) {
        //獲取系統版本
        int currentapiVersion = Build.VERSION.SDK_INT;
        // 激活相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            SimpleDateFormat timeStampFormat = new SimpleDateFormat(
                    "yyyy_MM_dd_HH_mm_ss");
            String filename = timeStampFormat.format(new Date());
            tempFile = new File(Environment.getExternalStorageDirectory(),
                    filename + ".jpg");
            if (currentapiVersion < 24) {
                // 从文件中创建uri
                imageUri = Uri.fromFile(tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            }
            else {
                //兼容android7.0 使用共享文件的形式
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(MediaStore.Images.Media.DATA, tempFile.getAbsolutePath());
                //检查是否有存储权限，以免崩溃
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    Toast.makeText(this, "请开启存储权限", Toast.LENGTH_SHORT).show();
                    return;
                }
                imageUri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            }
        }
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
        activity.startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }


    /*
     * 判断sdcard是否被挂载
     */
    public static boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }


    @Override
    public void closeActivity() {
        boolean isUploading = false;
        if (isUploading) {
            showCancelUploadDialog();
        }
        else {
            closeActiviy();
        }
    }

    private void closeActiviy() {
        Intent intent = new Intent();
        String backtype = "backkey";
        intent.putExtra("backtype", backtype);
        this.setResult(3, intent);
        this.finish();
    }


    public void showCancelUploadDialog() {
        if (cancelUploadDialog == null) {
            cancelUploadDialog = new MaterialDialog(this);
            cancelUploadDialog.isTitleShow(false)
                    .content(
                            "是否停止上传并退出？")
                    .btnText("否", "是")
                    .show();
            cancelUploadDialog.setOnBtnClickL(
                    new OnBtnClickL() {//left btn click listener
                        @Override
                        public void onBtnClick() {
                            cancelUploadDialog.dismiss();
                        }
                    },
                    new OnBtnClickL() {//right btn click listener
                        @Override
                        public void onBtnClick() {
                            cancelUploadDialog.dismiss();
                            closeActiviy();
                        }
                    }
            );
            cancelUploadDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    cancelUploadDialog.dismiss();
                }
            });
        }
        else {
            cancelUploadDialog.show();
        }
    }


    /**
     * 下载异常
     * 白屏异常
     * js异常
     * 降级异常
     *
     * @param exception
     */
    @Override
    public void onJSException(WXJSExceptionInfo exception) {
        weexErrorHandler.writeWeexError(exception.getErrCode().toString(), exception.getException(), mUri.toString(), wxMapData);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("错误" + exception.getErrCode())
                .setCancelable(false)
                .setMessage("请到" + FileLog.getFolderPathToday(WeexErrorHandler.LOG_FOLDER_NAME) + "文件下查看错误详情")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                    }
                });
        builder.create().show();
    }

}
