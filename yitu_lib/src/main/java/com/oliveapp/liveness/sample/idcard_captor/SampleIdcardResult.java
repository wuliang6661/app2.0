package com.oliveapp.liveness.sample.idcard_captor;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.oliveapp.liveness.sample.R;


public class SampleIdcardResult extends Activity {
    public static final String TAG = SampleIdcardResult.class.getSimpleName();

    private ImageView mOliveappLivenessImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oliveapp_activity_sample_idcard_result);

        mOliveappLivenessImageView = (ImageView) findViewById(R.id.oliveappLivenessImageView);

        byte[] imageContent = getIntent().getByteArrayExtra("image");

        Bitmap image = BitmapFactory.decodeByteArray(imageContent, 0, imageContent.length);
        mOliveappLivenessImageView.setImageBitmap(image);
    }
}
