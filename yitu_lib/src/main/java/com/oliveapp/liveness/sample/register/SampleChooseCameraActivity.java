package com.oliveapp.liveness.sample.register;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.oliveapp.face.idcardcaptorsdk.captor.IDCardCaptor;
import com.oliveapp.libcommon.utility.LogUtil;
import com.oliveapp.libimagecapture.CaptureImageFragment;
import com.oliveapp.liveness.sample.R;
import com.oliveapp.liveness.sample.idcard_captor.SampleIdcardCaptorActivity;

public class SampleChooseCameraActivity extends Activity {
    public static final String TAG = SampleChooseCameraActivity.class.getSimpleName();

    private Button mIdCardButton;
    private Button mSelfieButton;
    private Button mIdcardFrontCaptureButton;
    private Button mIdcardBackCaptureButton;
    private IdcardCaptureListener mIdcardCaptureListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_register);

        mIdCardButton = (Button) findViewById(R.id.IDcard);
        mSelfieButton = (Button) findViewById(R.id.Selfie);
        mIdcardFrontCaptureButton = (Button) findViewById(R.id.IdcardFrontCapture);
        mIdcardBackCaptureButton = (Button) findViewById(R.id.IdcardBackCapture);

        /**
         * 拍摄身份证正面，反面请使用CaptureImageFragment.CAPTURE_MODE_IDCARD_BACK
         */
        mIdCardButton.setOnClickListener(
                new CaptureImageListener(CaptureImageFragment.CAPTURE_MODE_IDCARD_FRONT));

        /**
         * 拍摄类登记照
         */
        mSelfieButton.setOnClickListener(
                new CaptureImageListener(CaptureImageFragment.CAPTURE_MODE_SELFIE));

        mIdcardCaptureListener = new IdcardCaptureListener();

        mIdcardFrontCaptureButton.setOnClickListener(mIdcardCaptureListener);

        mIdcardBackCaptureButton.setOnClickListener(mIdcardCaptureListener);

        requestPermission();
    }

    private class CaptureImageListener implements View.OnClickListener {
        private int mCaptureType;
        public CaptureImageListener(int captureMode) {
            mCaptureType = captureMode;
        }

        @Override
        public void onClick(View v) {
            if (requestPermission()) {
                Intent i = new Intent(SampleChooseCameraActivity.this, SampleImageCaptureActivity.class);
                i.putExtra(SampleImageCaptureActivity.EXTRA_CAPTURE_MODE, mCaptureType);
                startActivity(i);
            }
        }
    }

    private class IdcardCaptureListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(SampleChooseCameraActivity.this, SampleIdcardCaptorActivity.class);
            /**
             * 设置身份证识别正面还是反面
             */
            int i1 = v.getId();
            if (i1 == R.id.IdcardFrontCapture) {
                i.putExtra(SampleIdcardCaptorActivity.EXTRA_CARD_TYPE, IDCardCaptor.CARD_TYPE_FRONT);

            } else if (i1 == R.id.IdcardBackCapture) {
                i.putExtra(SampleIdcardCaptorActivity.EXTRA_CARD_TYPE, IDCardCaptor.CARD_TYPE_BACK);

            }
            /**
             * 设置捕获模式，共有三种模式模式：
             * 1.SampleIdcardCaptorActivity.CAPTURE_MODE_AUTO 自动捕获模式
             * 2.SampleIdcardCaptorActivity.CAPTURE_MODE_MANUAL 手动拍摄模式
             * 3.SampleIdcardCaptorActivity.CAPTURE_MODE_MIXED 混合模式，首先尝试自动捕获，指定时间后，采取手动拍摄
             * 在本例中，使用混合捕获模式，需要设置自动捕获持续时间，单位为秒，默认10秒
             */
            i.putExtra(SampleIdcardCaptorActivity.EXTRA_CAPTURE_MODE, SampleIdcardCaptorActivity.CAPTURE_MODE_MIXED);
            i.putExtra(SampleIdcardCaptorActivity.EXTRA_DURATION_TIME, 10);

            if (requestPermission()) {
                startActivity(i);
            }
        }
    }

    /**==============================请求相机权限=========================**/
    private static final int PERMISSION_READ_EXTERNAL_STORAGE = 101;
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 102;
    private static final int PERMISSION_CAMERA = 103;
    private boolean requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE);
            return false;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_EXTERNAL_STORAGE);
            return false;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        try {
            switch (requestCode) {
                case PERMISSION_CAMERA: {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    } else {
                        Toast.makeText(this, "没有摄像头权限我什么都做不了哦!", Toast.LENGTH_LONG).show();
                    }
                    break;
                }
                case PERMISSION_READ_EXTERNAL_STORAGE: {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    } else {
                        Toast.makeText(this, "请打开存储读写权限，确保APP正常运行", Toast.LENGTH_LONG).show();
                    }
                    break;
                }
                case PERMISSION_WRITE_EXTERNAL_STORAGE: {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    } else {
                        Toast.makeText(this, "请打开存储读写权限，确保APP正常运行", Toast.LENGTH_LONG).show();
                    }
                    break;
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "Failed to request Permission", e);
        }
    }

}
