package synway.module_stack.hm_leave;

import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.widget.ImageView;

import synway.common.base.BaseActivity;
import synway.module_stack.R;
import synway.module_stack.utils.BitmapUtils;

public class BigImgAct extends BaseActivity {

    private ImageView bigImg;

    @Override
    protected int getLayout() {
        return R.layout.act_big_img;
    }


    @RequiresApi(api = VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bigImg = findViewById(R.id.iv_big_image);

        String imageUrl = getIntent().getStringExtra("image_url");
        if (!TextUtils.isEmpty(imageUrl)) {
            bigImg.setImageBitmap(BitmapUtils.base64ToBitmap(imageUrl));
        }

        bigImg.setOnClickListener(view -> finishAfterTransition());
    }
}
