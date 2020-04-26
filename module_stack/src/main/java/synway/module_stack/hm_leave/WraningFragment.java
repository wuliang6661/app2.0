package synway.module_stack.hm_leave;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;

import java.util.ArrayList;
import java.util.List;

import synway.common.base.BaseFragment;
import synway.module_interface.config.userConfig.LoginUser;
import synway.module_interface.config.userConfig.Sps_RWLoginUser;
import synway.module_stack.R;
import synway.module_stack.api.HttpResultSubscriber;
import synway.module_stack.hm_leave.bean.CheckBo;
import synway.module_stack.hm_leave.bean.PersonBo;
import synway.module_stack.hm_leave.net.HttpServiceIml;

public class WraningFragment extends BaseFragment implements View.OnClickListener {


    TextView waningTitle;
    TextView wanningTime;
    TextView wanningAddress;
    TextView addCheck;
    LinearLayout noneCheckLayout;
    TextView personName;
    TextView personPhone;
    TextView checkOutfit;
    TextView checkMode;
    TextView personNationality;
    TextView personMarriage;
    LinearLayout checkLayout;
    TextView editCheck;
    EditText editConclusion;
    EditText editResons;
    TextView commit;
    LinearLayout takeCheckLayout;
    LinearLayout noneText;
    LinearLayout wanning_message_layout;
    LinearLayout parent_layout;
    TextView checkTime;

    View view;
    private PersonBo personBo;
    private CheckBo checkBo;

    public static WraningFragment newInstance(PersonBo personBo) {
        WraningFragment fragment = new WraningFragment();
        Bundle args = new Bundle();
        args.putSerializable("person", personBo);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fra_warning, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        personBo = (PersonBo) getArguments().getSerializable("person");
        initView();
        checkMode.setOnClickListener(this);
        addCheck.setOnClickListener(this);
        commit.setOnClickListener(this);
        getWanningInfo();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.check_mode) {   //核查方式
            showPickerView();
        } else if (view.getId() == R.id.add_check) {
            noneCheckLayout.setVisibility(View.GONE);
            checkLayout.setVisibility(View.GONE);
            takeCheckLayout.setVisibility(View.VISIBLE);
        } else if (view.getId() == R.id.commit) {
            saveCheckInfo();
        }
    }

    /**
     * 根据身份证号获取预警信息
     */
    private void getWanningInfo() {
        HttpServiceIml.getWanningInfo(personBo.getIdx_idcard()).subscribe(new HttpResultSubscriber<CheckBo>() {
            @Override
            public void onSuccess(CheckBo s) {
                if (getActivity() == null) {
                    return;
                }
                checkBo = s;
                setUIMessage();
            }

            @Override
            public void onFiled(String code, String message) {
                if (getActivity() == null) {
                    return;
                }
                showToast(message);
            }
        });
    }


    /**
     * 保存核查信息
     */
    private void saveCheckInfo() {
        String strEditConclusion = editConclusion.getText().toString().trim();
        String strReson = editResons.getText().toString().trim();
        if (TextUtils.isEmpty(strEditConclusion)) {
            showToast("请填写核查结论！");
            return;
        }
        LoginUser user = Sps_RWLoginUser.readUser(getActivity());
        HttpServiceIml.saveCheck(checkBo.getWarning_uuid(), user.LoginCode,
                user.name, user.userOragianId, user.userOragian, strEditConclusion, strReson,
                user.telNumber).subscribe(new HttpResultSubscriber<List<Object>>() {
            @Override
            public void onSuccess(List<Object> s) {
                if (getActivity() == null) {
                    return;
                }
                getWanningInfo();
            }

            @Override
            public void onFiled(String code, String message) {
                if (getActivity() == null) {
                    return;
                }
                showToast(message);
            }
        });
    }


    /**
     * 根据返回数据显示界面
     */
    private void setUIMessage() {
        if (checkBo == null) {
            parent_layout.setVisibility(View.GONE);
            noneText.setVisibility(View.VISIBLE);
            return;
        }
        noneText.setVisibility(View.GONE);
        parent_layout.setVisibility(View.VISIBLE);
        if ("1".equals(checkBo.getType())) {   //已核查
            noneCheckLayout.setVisibility(View.GONE);
            takeCheckLayout.setVisibility(View.GONE);
            checkLayout.setVisibility(View.VISIBLE);
            wanning_message_layout.setVisibility(View.VISIBLE);
            waningTitle.setText(checkBo.getWarning_rule());
            wanningAddress.setText(checkBo.getWarning_address());
            if (checkBo.getWarning_time().length() > 2) {
                wanningTime.setText(checkBo.getWarning_time().substring(0, checkBo.getWarning_time().length() - 2));
            } else {
                wanningTime.setText(checkBo.getWarning_time());
            }
            if (checkBo.getCheck_time().length() > 2) {
                checkTime.setText(checkBo.getCheck_time().substring(0, checkBo.getCheck_time().length() - 2));
            } else {
                checkTime.setText(checkBo.getCheck_time());
            }
            personName.setText(checkBo.getCheck_person_name());
            checkOutfit.setText(checkBo.getCheck_person_organname());
            checkMode.setText(checkBo.getCtrl_processing_rule_name());
            personNationality.setText(checkBo.getCheck_result());
            personMarriage.setText(checkBo.getComment());
            personPhone.setText(checkBo.getCheck_person_phone());
        } else {
            noneCheckLayout.setVisibility(View.VISIBLE);
            takeCheckLayout.setVisibility(View.GONE);
            checkLayout.setVisibility(View.GONE);
            wanning_message_layout.setVisibility(View.VISIBLE);
            waningTitle.setText(checkBo.getWarning_rule());
            wanningAddress.setText(checkBo.getWarning_address());
            editCheck.setText(checkBo.getCtrl_processing_rule_name());
        }
    }


    private void showPickerView() {// 弹出选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(getActivity(), (options1, options2, options3, v) -> {
            //返回的分别是三个级别的选中位置
        })
                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .build();
        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        ArrayList<String> list = new ArrayList<>();
        list.add("嘿嘿");
        list.add("嘿嘿");
        list.add("嘿嘿");
        list.add("嘿嘿");
        pvOptions.setPicker(list);//三级选择器
        pvOptions.show();
    }


    /**
     * 初始化布局
     */
    private void initView() {
        waningTitle = view.findViewById(R.id.waning_title);
        wanningTime = view.findViewById(R.id.wanning_time);
        wanningAddress = view.findViewById(R.id.wanning_address);
        addCheck = view.findViewById(R.id.add_check);
        noneCheckLayout = view.findViewById(R.id.none_check_layout);
        personName = view.findViewById(R.id.person_name);
        personPhone = view.findViewById(R.id.person_phone);
        checkOutfit = view.findViewById(R.id.check_outfit);
        checkMode = view.findViewById(R.id.check_mode);
        personNationality = view.findViewById(R.id.person_nationality);
        personMarriage = view.findViewById(R.id.person_marriage);
        checkLayout = view.findViewById(R.id.check_layout);
        editCheck = view.findViewById(R.id.edit_check);
        editConclusion = view.findViewById(R.id.edit_conclusion);
        editResons = view.findViewById(R.id.edit_resons);
        commit = view.findViewById(R.id.commit);
        noneText = view.findViewById(R.id.none_text);
        takeCheckLayout = view.findViewById(R.id.take_check_layout);
        wanning_message_layout = view.findViewById(R.id.wanning_message_layout);
        parent_layout = view.findViewById(R.id.parent_layout);
        checkTime = view.findViewById(R.id.check_time);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
