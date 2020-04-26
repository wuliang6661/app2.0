package synway.module_stack.hm_leave;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import synway.common.base.BaseActivity;
import synway.module_stack.R;
import synway.module_stack.hm_leave.bean.PersonBo;
import synway.module_stack.utils.BitmapUtils;
import synway.module_stack.utils.CallPhoneUtils;
import synway.module_stack.utils.CopyUtils;
import synway.module_stack.weight.FragmentPaerAdapter;

public class PersonMessageAct extends BaseActivity implements View.OnClickListener {

    TextView textTitle;
    AppCompatImageView imageBack;
    ImageView personImg;
    TextView personName;
    TextView personCard;
    TextView personSex;
    TextView personNation;
    TextView personLevel;
    TextView personPhone;
    TabLayout tabLayout;
    ViewPager viewPager;

    private PersonBo personBo;
    private boolean isCheck;

    @Override
    protected int getLayout() {
        return R.layout.act_person_message;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras().getBoolean("isJson")) {
            personBo = new Gson().fromJson(getIntent().getExtras().getString("strPerson"), PersonBo.class);
        } else {
            personBo = (PersonBo) getIntent().getExtras().getSerializable("person");
        }
        isCheck = getIntent().getBooleanExtra("isCheck", false);
        initView();
        textTitle.setText("人员详情");
        imageBack.setOnClickListener(this);
        personName.setOnClickListener(this);
        personCard.setOnClickListener(this);
        personImg.setOnClickListener(this);
        personPhone.setOnClickListener(this);
        showMessage();
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
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
    }


    /**
     * 设置页面显示
     */
    private void showMessage() {
//        Glide.with(this).load(personBo.getPhoto_url()).into(personImg);
        if (!TextUtils.isEmpty(personBo.getPhotoDetail())) {
            personImg.setImageBitmap(BitmapUtils.base64ToBitmap(personBo.getPhotoDetail()));
        }
        personName.setText(personBo.getIdx_name());
        personCard.setText(personBo.getIdx_idcard());
        personSex.setText(personBo.getGender());
        personNation.setText(personBo.getIdx_nation());
        personLevel.setText(personBo.getPerson_level());
        personPhone.setText(personBo.getIdx_phone());

        tabLayout.addTab(tabLayout.newTab().setText("基本信息"));
//        tabLayout.addTab(tabLayout.newTab().setText("布撤控"));
        tabLayout.addTab(tabLayout.newTab().setText("预警"));
        tabLayout.addTab(tabLayout.newTab().setText("请销假"));

        BasicInfoFragment fragment1 = BasicInfoFragment.newInstance(personBo);
//        ClothControlFragment fragment2 = ClothControlFragment.newInstance(personBo);
        WraningFragment fragment3 = WraningFragment.newInstance(personBo);
        LeaveFragment fragment4 = LeaveFragment.newInstance(personBo);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(fragment1);
//        fragments.add(fragment2);
        fragments.add(fragment3);
        fragments.add(fragment4);
        FragmentPaerAdapter adapter = new FragmentPaerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);//将TabLayout和ViewPager关联起来。
        tabLayout.setTabsFromPagerAdapter(adapter);//给Tabs设置适配器
        viewPager.post(() -> {
            if (isCheck) {
                viewPager.setCurrentItem(1);
            }
        });
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.image_back) {
            finish();
        } else if (view.getId() == R.id.person_name) {
            CopyUtils.CopyToClipboard(this, personBo.getIdx_name());
            showToast("姓名已复制到剪贴板！");
        } else if (view.getId() == R.id.person_card) {
            CopyUtils.CopyToClipboard(this, personBo.getIdx_idcard());
            showToast("身份证号已复制到剪贴板！");
        } else if (view.getId() == R.id.person_img) {
            if (!TextUtils.isEmpty(personBo.getPhotoDetail())) {
                Intent intent = new Intent(this, BigImgAct.class);
                intent.putExtra("image_url", personBo.getPhotoDetail());
                // 添加跳转动画
                startActivity(intent,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                this,
                                personImg,
                                "图片")
                                .toBundle());
            }
        } else if (view.getId() == R.id.person_phone) {
            CallPhoneUtils.showCallPhone(this, personBo.getIdx_phone());
        }
    }
}
