package synway.module_stack.hm_leave;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import synway.common.base.BaseActivity;
import synway.module_interface.HmIdUtils;
import synway.module_interface.config.userConfig.LoginUser;
import synway.module_interface.config.userConfig.Sps_RWLoginUser;
import synway.module_stack.R;
import synway.module_stack.api.HttpResultSubscriber;
import synway.module_stack.hm_leave.bean.PersonBo;
import synway.module_stack.hm_leave.net.HttpServiceIml;
import synway.module_stack.utils.BitmapUtils;

public class PersonListAct extends BaseActivity implements View.OnClickListener {

    TextView textTitle;
    AppCompatImageView imageBack;
    RecyclerView recycleView;


    private List<PersonBo> personBos;

    @Override
    protected int getLayout() {
        return R.layout.act_my_person;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        textTitle.setText("我的人员");
        imageBack.setOnClickListener(this);

        LoginUser user = Sps_RWLoginUser.readUser(this);
        showProgress("加载中...");

        getMyPersonList(HmIdUtils.getId(user));
    }


    /**
     * 初始化布局
     */
    private void initView() {
        textTitle = findViewById(R.id.text_title);
        imageBack = findViewById(R.id.image_back);
        recycleView = findViewById(R.id.recycle_view);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recycleView.setLayoutManager(manager);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.image_back) {
            finish();
        }
    }


    /**
     * 获取我的人员列表
     */
    private void getMyPersonList(String loginId) {
        HttpServiceIml.getAttentionList(loginId).subscribe(new HttpResultSubscriber<List<PersonBo>>() {
            @Override
            public void onSuccess(List<PersonBo> s) {
                stopProgress();
                personBos = s;
                setAdapter();
            }

            @Override
            public void onFiled(String code, String message) {
                stopProgress();
                showToast(message);
            }
        });
    }

    /**
     * 设置适配器
     */
    private void setAdapter() {
        BaseQuickAdapter<PersonBo, BaseViewHolder> adapter =
                new BaseQuickAdapter<PersonBo, BaseViewHolder>(R.layout.item_person, personBos) {
                    @Override
                    protected void convert(BaseViewHolder helper, PersonBo item) {
                        helper.setText(R.id.person_name, item.getIdx_name());
                        helper.setText(R.id.person_sex, item.getGender());
                        helper.setText(R.id.person_nation, item.getIdx_nation());
                        helper.setText(R.id.person_level, item.getPerson_level());
                        helper.setText(R.id.person_phone, item.getIdx_phone());
                        ImageView imageView = helper.getView(R.id.person_img);
//                        Glide.with(PersonListAct.this).load(item.getPhoto_url()).into(imageView);
                        if (!TextUtils.isEmpty(item.getPhotoDetail())) {
                            imageView.setImageBitmap(BitmapUtils.base64ToBitmap(item.getPhotoDetail()));
                        }
                        helper.getView(R.id.item_layout).setOnClickListener(view -> {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("person", item);
                            bundle.putBoolean("isJson", false);
                            gotoActivity(PersonMessageAct.class, bundle, false);
                        });
                    }
                };
        recycleView.setAdapter(adapter);
    }
}
