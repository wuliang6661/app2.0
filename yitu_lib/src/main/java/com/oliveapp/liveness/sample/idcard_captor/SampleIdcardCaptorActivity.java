package com.oliveapp.liveness.sample.idcard_captor;

import android.content.Intent;
import android.os.Bundle;

import com.oliveapp.face.idcardcaptorsdk.captor.CapturedIDCardImage;
import com.oliveapp.liveness.sample.idcard_captor.view_controller.SampleIdcardCaptorMainActivity;

/**
 * SampleIdcardCaptorActivity
 * 如果不想关心界面实现细节，可以直接在此Activity中实现界面跳转逻辑
 */
public class SampleIdcardCaptorActivity extends SampleIdcardCaptorMainActivity {

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onFrameResult(int status) {
        super.onFrameResult(status);
    }

    /**
     * 捕获到最好的一张身份证照片
     */
    @Override
    public void onIDCardCaptured(CapturedIDCardImage data) {
		super.onIDCardCaptured(data);
	
		// 处理Activity跳转逻辑
        Intent i = new Intent(SampleIdcardCaptorActivity.this, SampleIdcardResult.class);
        i.putExtra("image", data.idcardImageData);
        startActivity(i);
        finish();
    }
}
