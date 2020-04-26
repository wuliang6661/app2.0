package synway.module_stack.hm_leave;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.blankj.utilcode.util.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import qyc.library.control.dialog_confirm.DialogConfirm;
import synway.common.base.BaseFragment;
import synway.module_interface.config.userConfig.LoginUser;
import synway.module_interface.config.userConfig.Sps_RWLoginUser;
import synway.module_stack.R;
import synway.module_stack.api.HttpResultSubscriber;
import synway.module_stack.hm_leave.bean.CreateLeaveBo;
import synway.module_stack.hm_leave.bean.LeaveBO;
import synway.module_stack.hm_leave.bean.LeaveInfoBo;
import synway.module_stack.hm_leave.bean.PersonBo;
import synway.module_stack.hm_leave.net.HttpServiceIml;
import synway.module_stack.utils.CallPhoneUtils;
import synway.module_stack.utils.TimeUtils;

public class LeaveFragment extends BaseFragment implements View.OnClickListener {


    TextView takeLeave;
    LinearLayout noneLayout;
    TextView levelType;
    TextView goAddress;
    TextView leaveTime;
    TextView leaveContent;
    TextView leaveReson;
    TextView startPersonName;
    TextView personCity;
    TextView btCancleLeave;
    TextView btCommitLeave;
    ScrollView scollView;
    TextView leaveCreateType;
    TextView goCreateAddress;
    TextView startCreateTime;
    TextView endCreateTime;
    EditText leaveCreateContent;
    EditText leaveCreateReson;
    TextView leaveCreateCancle;
    TextView leaveCreateCommit;
    ScrollView leaveCreateLayout;
    TextView leaveXuType;
    TextView leaveXuAddress;
    TextView leaveXuStartTime;
    TextView leaveXuEndTime;
    LinearLayout leaveXuTimeLayout;
    EditText leaveXuContent;
    EditText leaveXuReson;
    TextView leaveXuPerson;
    TextView leaveXuPersonName;
    TextView leaveXuPersonPhone;
    TextView leaveXuCancle;
    TextView leaveXuCommit;
    ScrollView leaveXuLayout;
    TextView startPersonPhone;
    TextView go_address_text;

    View view;
    private PersonBo personBo;
    private LeaveBO leaveBO;
    private LeaveInfoBo leaveInfoBo;
    private LeaveInfoBo.AbleAreaBean cityBo;   //选中的城市
    private LeaveInfoBo.AbleAreaBean.ChildNodesBeanX childNodesBeanX;   //选中的区
    private LeaveInfoBo.AbleAreaBean.ChildNodesBeanX.ChildNodesBean childNodesBean;   //选中的街道
    private String goAddressCode;
    private String goAddressName;

    TimePickerView pvTime;

    public static LeaveFragment newInstance(PersonBo personBo) {
        LeaveFragment fragment = new LeaveFragment();
        Bundle args = new Bundle();
        args.putSerializable("person", personBo);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        personBo = (PersonBo) getArguments().getSerializable("person");
        initView();
        takeLeave.setOnClickListener(this);
        leaveCreateCancle.setOnClickListener(this);
        leaveCreateCommit.setOnClickListener(this);
        leaveXuCancle.setOnClickListener(this);
        leaveXuCommit.setOnClickListener(this);
        btCancleLeave.setOnClickListener(this);
        btCommitLeave.setOnClickListener(this);
        leaveXuTimeLayout.setOnClickListener(this);
        leaveCreateType.setOnClickListener(this);
        goCreateAddress.setOnClickListener(this);
        startCreateTime.setOnClickListener(this);
        endCreateTime.setOnClickListener(this);
        leaveXuPersonPhone.setOnClickListener(this);
        startPersonPhone.setOnClickListener(this);
        getLeave();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fra_leave, null);
        return view;
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.take_leave) {   //请假
            getLeaveInfo();
        } else if (view.getId() == R.id.leave_create_cancle) {  //取消请假
            noneLayout.setVisibility(View.VISIBLE);
            leaveXuLayout.setVisibility(View.GONE);
            leaveCreateLayout.setVisibility(View.GONE);
            scollView.setVisibility(View.GONE);
        } else if (view.getId() == R.id.leave_xu_cancle) {    //续假取消
            noneLayout.setVisibility(View.GONE);
            leaveXuLayout.setVisibility(View.GONE);
            leaveCreateLayout.setVisibility(View.GONE);
            scollView.setVisibility(View.VISIBLE);
        } else if (view.getId() == R.id.leave_xu_commit) {    //续假提交
            xuLeave();
        } else if (view.getId() == R.id.leave_create_commit) {   //请假提交
            createLeave();
        } else if (view.getId() == R.id.bt_commit_leave) {   //续假
            noneLayout.setVisibility(View.GONE);
            leaveXuLayout.setVisibility(View.VISIBLE);
            leaveCreateLayout.setVisibility(View.GONE);
            scollView.setVisibility(View.GONE);
        } else if (view.getId() == R.id.bt_cancle_leave) {  //撤销假
            DialogConfirm.show(getActivity(), "提示", "是否确认撤销请假？", this::cancleLeave);
        } else if (view.getId() == R.id.leave_xu_time_layout) {    //续假时间
            initTimePicker(2);
        } else if (view.getId() == R.id.leave_create_type) {   //请假类别
            showTypeDialog();
        } else if (view.getId() == R.id.go_create_address) {   //去往地区
            showAddressDialog();
        } else if (view.getId() == R.id.start_create_time) {   //开始时间
            initTimePicker(0);
        } else if (view.getId() == R.id.end_create_time) {
            initTimePicker(1);
        } else if (view.getId() == R.id.leave_xu_person_phone) {
            CallPhoneUtils.showCallPhone(getActivity(), leaveBO.getLrrlxfs());
        } else if (view.getId() == R.id.start_person_phone) {
            CallPhoneUtils.showCallPhone(getActivity(), leaveBO.getLrrlxfs());
        }
    }

    /**
     * 获取请销假详情
     */
    private void getLeave() {
        HttpServiceIml.getQxjxq(personBo.getIdx_idcard()).subscribe(new HttpResultSubscriber<LeaveBO>() {
            @Override
            public void onSuccess(LeaveBO s) {
                if (getActivity() == null) {
                    return;
                }
                leaveBO = s;
                setUIMessage();
            }

            @Override
            public void onFiled(String code, String message) {
                showToast(message);
            }
        });
    }


    /**
     * 设置界面显示
     */
    private void setUIMessage() {
        if (leaveBO == null || TextUtils.isEmpty(leaveBO.getQxj_uuid())) {   //无请假信息
            noneLayout.setVisibility(View.VISIBLE);
            leaveXuLayout.setVisibility(View.GONE);
            leaveCreateLayout.setVisibility(View.GONE);
            scollView.setVisibility(View.GONE);
        } else {                //有续假数据
            noneLayout.setVisibility(View.GONE);
            leaveXuLayout.setVisibility(View.GONE);
            leaveCreateLayout.setVisibility(View.GONE);
            scollView.setVisibility(View.VISIBLE);
            levelType.setText(leaveBO.getQjlx());
            goAddress.setText(leaveBO.getQwdq());
            leaveTime.setText(leaveBO.getStart() + "--" + leaveBO.getEnd());
            leaveReson.setText(leaveBO.getBz());
            startPersonName.setText(leaveBO.getLrr());
            startPersonPhone.setText(leaveBO.getLrrlxfs());
            personCity.setText(leaveBO.getLrrjg());
            leaveXuType.setText(leaveBO.getQjlx());
            leaveXuAddress.setText(leaveBO.getQwdq());
            leaveXuPerson.setText(leaveBO.getLrr());
            leaveXuPersonPhone.setText(leaveBO.getLrrlxfs());
            leaveXuPersonName.setText(leaveBO.getLrrjg());
            leaveXuStartTime.setText(leaveBO.getStart() + "--");
        }
    }


    /**
     * 获取请假时需要的类型与地区数据
     */
    private void getLeaveInfo() {
        showProgress("加载中...");
        LoginUser user = Sps_RWLoginUser.readUser(getActivity());
        HttpServiceIml.getLeavePersonInfo(personBo.getIdx_idcard(), user.userOragian)
                .subscribe(new HttpResultSubscriber<LeaveInfoBo>() {
                    @Override
                    public void onSuccess(LeaveInfoBo s) {
                        if (getActivity() == null) {
                            return;
                        }
                        stopProgress();
                        leaveInfoBo = s;
                        noneLayout.setVisibility(View.GONE);
                        leaveXuLayout.setVisibility(View.GONE);
                        leaveCreateLayout.setVisibility(View.VISIBLE);
                        scollView.setVisibility(View.GONE);
                        if (leaveInfoBo.getAbleArea() == null || leaveInfoBo.getAbleArea().size() == 0) {
                            go_address_text.setVisibility(View.GONE);
                            goCreateAddress.setVisibility(View.GONE);
                        } else {
                            go_address_text.setVisibility(View.VISIBLE);
                            goCreateAddress.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFiled(String code, String message) {
                        stopProgress();
                        showToast(message);
                    }
                });
    }

    /**
     * 显示类别选择器
     */
    private void showTypeDialog() {
        List<String> list = new ArrayList<>();
        list.add("探亲");
        list.add("访友");
        list.add("旅游");
        list.add("看病");
        list.add("其他");
        OptionsPickerView pvOptions = new OptionsPickerBuilder(getActivity(), (options1, options2, options3, v) -> {
            leaveCreateType.setText(list.get(options1));
            //返回的分别是三个级别的选中位置
        })
                .setTitleText("")
                .setDividerColor(Color.parseColor("#ededed"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setLineSpacingMultiplier(1.8f)
                .setContentTextSize(18)
                .build();
        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(list);//三级选择器
        pvOptions.show();

    }


    /**
     * 显示去往地区显示弹窗
     */
    private void showAddressDialog() {
        List<LeaveInfoBo.AbleAreaBean> list1 = leaveInfoBo.getAbleArea();
        List<List<LeaveInfoBo.AbleAreaBean.ChildNodesBeanX>> list2 = new ArrayList<>();
        List<List<List<LeaveInfoBo.AbleAreaBean.ChildNodesBeanX.ChildNodesBean>>> list3 = new ArrayList<>();
        for (int a = 0; a < list1.size(); a++) { //遍历市
            ArrayList<LeaveInfoBo.AbleAreaBean.ChildNodesBeanX> cityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<List<LeaveInfoBo.AbleAreaBean.ChildNodesBeanX.ChildNodesBean>> province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）
            if (list1.get(0).getChildNodes() != null) {
                for (int b = 0; b < list1.get(a).getChildNodes().size(); b++) {    //遍历县
                    cityList.add(list1.get(a).getChildNodes().get(b));

                    ArrayList<LeaveInfoBo.AbleAreaBean.ChildNodesBeanX.ChildNodesBean> city_AreaList = new ArrayList<>();
                    if (list1.get(a).getChildNodes().get(b).getChildNodes() != null) {
                        city_AreaList.addAll(list1.get(a).getChildNodes().get(b).getChildNodes());
                    }
                    province_AreaList.add(city_AreaList);//添加该省所有地区数据
                }

                list2.add(cityList);
                list3.add(province_AreaList);
            }
        }
        OptionsPickerView pvOptions = new OptionsPickerBuilder(getActivity(), (options1, options2, options3, v) -> {
            //返回的分别是三个级别的选中位置
            cityBo = list1.size() > 0 ? list1.get(options1) : null;
            childNodesBeanX = list2.size() > 0 && list2.get(options1).size() > 0 ? list2.get(options1).get(options2) : null;
            childNodesBean = list2.size() > 0 && list3.get(options1).size() > 0 && list3.get(options1).get(options2).size() > 0 ?
                    list3.get(options1).get(options2).get(options3) : null;
            if (childNodesBeanX == null) {
                goCreateAddress.setText(cityBo.getPickerViewText());
                goAddressCode = cityBo.getCode();
                goAddressName = cityBo.getCodeName();
            } else {
                if (childNodesBean == null) {
                    goCreateAddress.setText(cityBo.getPickerViewText() + "  " + childNodesBeanX.getPickerViewText());
                    goAddressCode = childNodesBeanX.getCode();
                    goAddressName = childNodesBeanX.getCodeName();
                } else {
                    goCreateAddress.setText(cityBo.getPickerViewText() + "  " + childNodesBeanX.getPickerViewText() + "  " + childNodesBean.getPickerViewText());
                    goAddressCode = childNodesBean.getCode();
                    goAddressName = childNodesBean.getCodeName();
                }
            }
        })
                .setDividerColor(Color.parseColor("#ededed"))
                .setTextColorCenter(Color.parseColor("#333333")) //设置选中项文字颜色
                .setLineSpacingMultiplier(1.8f)
                .setContentTextSize(18)
                .build();
        if (list2.size() == 0) {  //没有二级菜单
            pvOptions.setPicker(list1);//三级选择器
        } else {
            if (list3.size() == 0) {
                pvOptions.setPicker(list1, list2);//三级选择器
            } else {
                pvOptions.setPicker(list1, list2, list3);//三级选择器
            }
        }

        pvOptions.show();

    }


    /**
     * 撤销假
     */
    private void cancleLeave() {
        showProgress("加载中...");
        HttpServiceIml.endLeave(personBo.getIdx_idcard()).subscribe(new HttpResultSubscriber<String>() {
            @Override
            public void onSuccess(String s) {
                stopProgress();
                getLeave();
            }

            @Override
            public void onFiled(String code, String message) {
                stopProgress();
                showToast(message);
            }
        });
    }

    /**
     * 续假
     */
    private void xuLeave() {
        String time = leaveXuEndTime.getText().toString().trim();
        if (TextUtils.isEmpty(time)) {
            showToast("请选择请假结束时间！");
            return;
        }
        showProgress("加载中...");
        HttpServiceIml.xuLeave(personBo.getIdx_idcard(), time).subscribe(new HttpResultSubscriber<String>() {
            @Override
            public void onSuccess(String s) {
                stopProgress();
                if (getActivity() == null) {
                    return;
                }
                showToast("操作成功！");
                getLeave();
            }

            @Override
            public void onFiled(String code, String message) {
                stopProgress();
                if (getActivity() == null) {
                    return;
                }
                showToast(message);
            }
        });
    }


    /**
     * 请假
     */
    private void createLeave() {
        String goAddress = goCreateAddress.getText().toString().trim();
        String startTime = startCreateTime.getText().toString().trim();
        String endLeaveTime = endCreateTime.getText().toString().trim();
        String comment = leaveCreateReson.getText().toString().trim();
        String leaveType = leaveCreateType.getText().toString().trim();
        if (TextUtils.isEmpty(leaveType)) {
            showToast("请选择请假类别！");
            return;
        }
        if (goCreateAddress.getVisibility() == View.VISIBLE && TextUtils.isEmpty(goAddress)) {
            showToast("请选择去往地区！");
            return;
        }
        if (TextUtils.isEmpty(startTime)) {
            showToast("请选择请假开始时间！");
            return;
        }
        if (TextUtils.isEmpty(endLeaveTime)) {
            showToast("请选择请假结束时间！");
            return;
        }
        LoginUser user = Sps_RWLoginUser.readUser(getActivity());
        CreateLeaveBo leaveBo = new CreateLeaveBo();
        leaveBo.sfzh = personBo.getIdx_idcard();
        leaveBo.xm = personBo.getIdx_name();
        leaveBo.lxfs = personBo.getIdx_phone();
        leaveBo.rylx = personBo.getPerson_type();
        leaveBo.ryjb = personBo.getPerson_level();
        leaveBo.qjlx = leaveType;
        leaveBo.qwdq_code = goAddressCode;
        leaveBo.qwdq = goAddressName;
        leaveBo.bz = comment;
        leaveBo.start = startTime + ":00";
        leaveBo.end = endLeaveTime + ":00";
        leaveBo.lrrid = user.LoginCode;
        leaveBo.lrr = user.name;
        leaveBo.lrrlxfs = user.telNumber;
        leaveBo.lrrjg = user.userOragian;
        showProgress("加载中...");
        HttpServiceIml.createLeave(leaveBo).subscribe(new HttpResultSubscriber<String>() {
            @Override
            public void onSuccess(String s) {
                stopProgress();
                if (getActivity() == null) {
                    return;
                }
                showToast("操作成功！");
                getLeave();
            }

            @Override
            public void onFiled(String code, String message) {
                stopProgress();
                if (getActivity() == null) {
                    return;
                }
                showToast(message);
            }
        });
    }


    private void initTimePicker(int timeType) {//   0:为开始时间  1为结束时间  2为续假时间
        Calendar startDate = Calendar.getInstance();
        if (timeType == 1) {
            String time = startCreateTime.getText().toString();
            if (StringUtils.isEmpty(time)) {
                showToast("请先选择请假开始时间！");
                return;
            } else {
                startDate.setTime(TimeUtils.string2Date(time + ":00"));
            }
        }
        Calendar endDate = Calendar.getInstance();
        endDate.set(Calendar.DAY_OF_YEAR, endDate.get(Calendar.DAY_OF_YEAR) + (10 * 365));   //默认设置可选择一年，可配置
        pvTime = new TimePickerBuilder(getActivity(), (date, v) -> {
            switch (timeType) {
                case 0:   //开始时间
                    startCreateTime.setText(TimeUtils.date2String(date, "yyyy-MM-dd HH:mm"));
                    break;
                case 1:   //结束时间
                    endCreateTime.setText(TimeUtils.date2String(date, "yyyy-MM-dd HH:mm"));
                    break;
                case 2:    //续假时间
                    leaveXuEndTime.setText(TimeUtils.date2String(date, "yyyy-MM-dd HH:mm"));
                    break;
            }
        })
                .setType(new boolean[]{true, true, true, true, true, false})
                .isDialog(true) //默认设置false ，内部实现将DecorView 作为它的父控件。
                .setDate(startDate)
                .setRangDate(startDate, endDate)
                .build();
        Dialog mDialog = pvTime.getDialog();
        if (mDialog != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);
            params.leftMargin = 0;
            params.rightMargin = 0;
            pvTime.getDialogContainerLayout().setLayoutParams(params);
            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim);//修改动画样式
                dialogWindow.setGravity(Gravity.BOTTOM);//改成Bottom,底部显示
                dialogWindow.setDimAmount(0.1f);
            }
        }
        pvTime.show();
    }


    /**
     * 初始化布局
     */
    private void initView() {
        takeLeave = view.findViewById(R.id.take_leave);
        noneLayout = view.findViewById(R.id.none_layout);
        levelType = view.findViewById(R.id.level_type);
        goAddress = view.findViewById(R.id.go_address);
        leaveTime = view.findViewById(R.id.leave_time);
        leaveContent = view.findViewById(R.id.leave_content);
        leaveReson = view.findViewById(R.id.leave_reson);
        startPersonName = view.findViewById(R.id.start_person_name);
        personCity = view.findViewById(R.id.person_city);
        btCancleLeave = view.findViewById(R.id.bt_cancle_leave);
        btCommitLeave = view.findViewById(R.id.bt_commit_leave);
        scollView = view.findViewById(R.id.scoll_view);
        leaveCreateType = view.findViewById(R.id.leave_create_type);
        goCreateAddress = view.findViewById(R.id.go_create_address);
        startCreateTime = view.findViewById(R.id.start_create_time);
        endCreateTime = view.findViewById(R.id.end_create_time);
        leaveCreateContent = view.findViewById(R.id.leave_create_content);
        leaveCreateReson = view.findViewById(R.id.leave_create_reson);
        leaveCreateCancle = view.findViewById(R.id.leave_create_cancle);
        leaveCreateCommit = view.findViewById(R.id.leave_create_commit);
        leaveCreateLayout = view.findViewById(R.id.leave_create_layout);
        leaveXuType = view.findViewById(R.id.leave_xu_type);
        leaveXuAddress = view.findViewById(R.id.leave_xu_address);
        leaveXuStartTime = view.findViewById(R.id.leave_xu_start_time);
        leaveXuEndTime = view.findViewById(R.id.leave_xu_end_time);
        leaveXuTimeLayout = view.findViewById(R.id.leave_xu_time_layout);
        leaveXuContent = view.findViewById(R.id.leave_xu_content);
        leaveXuReson = view.findViewById(R.id.leave_xu_reson);
        leaveXuPerson = view.findViewById(R.id.leave_xu_person);
        leaveXuPersonName = view.findViewById(R.id.leave_xu_person_name);
        leaveXuPersonPhone = view.findViewById(R.id.leave_xu_person_phone);
        leaveXuCancle = view.findViewById(R.id.leave_xu_cancle);
        leaveXuCommit = view.findViewById(R.id.leave_xu_commit);
        leaveXuLayout = view.findViewById(R.id.leave_xu_layout);
        startPersonPhone = view.findViewById(R.id.start_person_phone);
        go_address_text = view.findViewById(R.id.go_address_text);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
