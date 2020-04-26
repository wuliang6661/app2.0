package com.oliveapp.liveness.sample.register;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.oliveapp.libimagecapture.CaptureImageFragment;
import com.oliveapp.libimagecapture.OnImageCapturedEventListener;
import com.oliveapp.libimagecapture.datatype.DetectedRect;
import com.oliveapp.liveness.sample.R;

public class SampleImageCaptureActivity extends Activity implements OnImageCapturedEventListener {
    public static final String TAG = SampleImageCaptureActivity.class.getSimpleName();

    public static final String EXTRA_CAPTURE_MODE = "capture_mode";

    private CaptureImageFragment mCaptureImageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_image_capture);

        attachUserImageRegistractionFragment();
    }

    private void attachUserImageRegistractionFragment() {
        FragmentManager mFragmentManager = getFragmentManager();
        CaptureImageFragment userImageRegisterFragment = (CaptureImageFragment) mFragmentManager.findFragmentByTag(CaptureImageFragment.TAG);

        if (userImageRegisterFragment == null) {
            int captureType = getIntent().getExtras().getInt(EXTRA_CAPTURE_MODE, CaptureImageFragment.CAPTURE_MODE_IDCARD_FRONT);
            userImageRegisterFragment = CaptureImageFragment.newInstance(captureType);

            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.add(R.id.imageCaptureMainLayout, userImageRegisterFragment, CaptureImageFragment.TAG);
            ft.commit();
        } else {
            if (userImageRegisterFragment.isAdded()) {
                Log.i(TAG, "user image register fragment already attached");
            }
        }
        userImageRegisterFragment.setArgs(this, this);
        mCaptureImageFragment = userImageRegisterFragment;
    }

    @Override
    public void OnImageCaptured(byte[] imageContent, DetectedRect faceRect) {
        boolean faceExists = (faceRect != null);

        Intent i = new Intent(this, SampleCameraResultActivity.class);
        i.putExtra(EXTRA_CAPTURE_MODE, getIntent().getExtras().getInt(EXTRA_CAPTURE_MODE, CaptureImageFragment.CAPTURE_MODE_IDCARD_FRONT));
        i.putExtra(SampleCameraResultActivity.EXTRA_IMAGE_CONTENT, imageContent);
        i.putExtra(SampleCameraResultActivity.EXTRA_FACE_EXISTS, faceExists);
        if (faceExists) {
            i.putExtra(SampleCameraResultActivity.EXTRA_FACE_RECT, faceRect.rect.flattenToString());
        }
        startActivity(i);
        finish();
    }
}
