package synway.module_stack.hm_leave;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import synway.common.base.BaseActivity;
import synway.module_stack.R;
import synway.module_stack.hm_leave.bean.PushWanningBO;
import synway.module_stack.utils.BitmapUtils;
import synway.module_stack.utils.CallPhoneUtils;
import synway.module_stack.utils.CopyUtils;

public class PushWanningAct extends BaseActivity implements View.OnClickListener {


    TextView textTitle;
    AppCompatImageView imageBack;
    ImageView personImg;
    TextView personName;
    TextView personCard;
    TextView personSex;
    TextView personNation;
    TextView personLevel;
    TextView personPhone;

    TextView waningTitle;
    TextView wanningTime;
    TextView wanningAddress;

    TextView person_ganbu_name;
    TextView person_ganbu_phone;
    TextView person_rule;

    private PushWanningBO wanningBO;

    @Override
    protected int getLayout() {
        return R.layout.act_push_wanning;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String msg = getIntent().getStringExtra("wanning");
        wanningBO = new Gson().fromJson(msg, PushWanningBO.class);

        initView();
        textTitle.setText("预警消息");
        imageBack.setOnClickListener(this);
        personPhone.setOnClickListener(this);
        personImg.setOnClickListener(this);
        personName.setOnClickListener(this);
        personCard.setOnClickListener(this);
        person_ganbu_phone.setOnClickListener(this);
        setMessage();
    }


    /**
     * 初始化布局
     */
    private void initView() {
        textTitle = findViewById(R.id.text_title);
        imageBack = findViewById(R.id.image_back);
        personImg = findViewById(R.id.person_img);
        personName = findViewById(R.id.person_name);
        personCard = findViewById(R.id.person_card);
        personSex = findViewById(R.id.person_sex);
        personNation = findViewById(R.id.person_nation);
        personLevel = findViewById(R.id.person_level);
        personPhone = findViewById(R.id.person_phone);
        waningTitle = findViewById(R.id.waning_title);
        wanningTime = findViewById(R.id.wanning_time);
        wanningAddress = findViewById(R.id.wanning_address);
        person_ganbu_name = findViewById(R.id.person_ganbu_name);
        person_ganbu_phone = findViewById(R.id.person_ganbu_phone);
        person_rule = findViewById(R.id.person_rule);
    }


    /**
     * 填写页面数据
     */
    private void setMessage() {
        if (!TextUtils.isEmpty(wanningBO.getPhoneDetail())) {
            personImg.setImageBitmap(BitmapUtils.base64ToBitmap(wanningBO.getPhoneDetail()));
        }
        personName.setText(wanningBO.getIdx_name());
        personCard.setText(wanningBO.getIdcard());
        personSex.setText(wanningBO.getGender());
        personNation.setText(wanningBO.getIdx_nation());
        personLevel.setText(wanningBO.getPerson_level());
        personPhone.setText(wanningBO.getIdx_phone());
        waningTitle.setText(wanningBO.getWarning_rule());
        wanningAddress.setText(wanningBO.getWarning_address());
        person_rule.setText(wanningBO.getCtrl_processing_rule_name());
        person_ganbu_name.setText(wanningBO.getPerson_in_charge());
        person_ganbu_phone.setText(wanningBO.getPerson_in_charge_phone());
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.image_back) {
            finish();
        }
        else if (view.getId() == R.id.person_img) {
            Intent intent = new Intent(this, BigImgAct.class);
            intent.putExtra("image_url", wanningBO.getPhoneDetail());
            // 添加跳转动画
            startActivity(intent,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                            this,
                            personImg,
                            "图片")
                            .toBundle());
        }
        else if (view.getId() == R.id.person_phone) {
            CallPhoneUtils.showCallPhone(this, wanningBO.getIdx_phone());
        }
        else if (view.getId() == R.id.person_ganbu_phone) {
            CallPhoneUtils.showCallPhone(this, wanningBO.getPerson_in_charge_phone());
        }
        else if (view.getId() == R.id.person_name) {
            CopyUtils.CopyToClipboard(this, wanningBO.getIdx_name());
            showToast("姓名已复制到剪贴板！");
        }
        else if (view.getId() == R.id.person_card) {
            CopyUtils.CopyToClipboard(this, wanningBO.getIdcard());
            showToast("身份证号已复制到剪贴板！");
        }
    }
}
