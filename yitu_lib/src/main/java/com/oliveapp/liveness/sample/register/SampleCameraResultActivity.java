package com.oliveapp.liveness.sample.register;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.oliveapp.liveness.sample.R;

public class SampleCameraResultActivity extends Activity{
    public static final String TAG = SampleCameraResultActivity.class.getSimpleName();

    public static final String EXTRA_IMAGE_CONTENT = "image_content";
    public static final String EXTRA_FACE_EXISTS = "face_exists";
    public static final String EXTRA_FACE_RECT = "face_rect";

    private ImageView mImageView;
    private Button mRetakeButton;
    private Button mBackButton;
    public static final int livenessMode = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_register_result);

        mImageView = (ImageView) findViewById(R.id.captureImageImageView);
        mRetakeButton = (Button) findViewById(R.id.retakeButton);
        mBackButton = (Button) findViewById(R.id.confirmButton);

        Bundle result = getIntent().getExtras();

        final int mode = result.getInt(SampleImageCaptureActivity.EXTRA_CAPTURE_MODE);
        byte[] imageContent = result.getByteArray(EXTRA_IMAGE_CONTENT);
        boolean faceExists = result.getBoolean(EXTRA_FACE_EXISTS);

        Bitmap capturedImage = BitmapFactory.decodeByteArray(imageContent, 0, imageContent.length);

        if (faceExists) {
            Bitmap imageWithRect = capturedImage.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(imageWithRect);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10);
            String rectString = result.getString(EXTRA_FACE_RECT);
            Rect rect = Rect.unflattenFromString(rectString);
            canvas.drawRect(rect, paint);
            mImageView.setImageBitmap(imageWithRect);
        } else {
            mImageView.setImageBitmap(capturedImage);
        }

        //
        if (livenessMode == mode) {
             mRetakeButton.setVisibility(View.INVISIBLE);
        }
        mRetakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SampleCameraResultActivity.this, SampleImageCaptureActivity.class);
                i.putExtra(SampleImageCaptureActivity.EXTRA_CAPTURE_MODE, mode);
                startActivity(i);
                finish();
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
