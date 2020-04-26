package synway.module_stack.hm_leave;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import synway.common.base.BaseFragment;
import synway.module_stack.R;
import synway.module_stack.api.HttpResultSubscriber;
import synway.module_stack.hm_leave.bean.CarBo;
import synway.module_stack.hm_leave.bean.ControlBo;
import synway.module_stack.hm_leave.bean.PersonBo;
import synway.module_stack.hm_leave.net.HttpServiceIml;

public class ClothControlFragment extends BaseFragment {


    TextView controllRule;
    TextView controllRuleTwo;
    TextView controllTime;
    TextView disposeRule;
    TextView startPerson;
    TextView personMechanism;
    TextView personPhone;
    RecyclerView recyclerView;

    View view;
    private PersonBo personBo;

    public static ClothControlFragment newInstance(PersonBo personBo) {
        ClothControlFragment fragment = new ClothControlFragment();
        Bundle args = new Bundle();
        args.putSerializable("person", personBo);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fra_cloth_control, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        personBo = (PersonBo) getArguments().getSerializable("person");
        initview();
        getControlData();
        getCarInfo();
    }

    /**
     * 初始化控件
     */
    private void initview() {
        controllRule = view.findViewById(R.id.controll_rule);
        controllRuleTwo = view.findViewById(R.id.controll_rule_two);
        controllTime = view.findViewById(R.id.controll_time);
        disposeRule = view.findViewById(R.id.dispose_rule);
        startPerson = view.findViewById(R.id.start_person);
        personMechanism = view.findViewById(R.id.person_mechanism);
        personPhone = view.findViewById(R.id.person_phone);
        recyclerView = view.findViewById(R.id.recycle_view);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setNestedScrollingEnabled(false);
    }


    /**
     * 获取布撤控信息
     */
    private void getControlData() {
        HttpServiceIml.getControlData(personBo.getIdx_idcard()).subscribe(new HttpResultSubscriber<ControlBo>() {
            @Override
            public void onSuccess(ControlBo pro) {
                if (getActivity() == null) {
                    return;
                }
                controllRule.setText(pro.getCtrlRulesName());
                controllRuleTwo.setText(pro.getCtrlRulesName());
                controllTime.setText(pro.getCtrlDuration());
                disposeRule.setText(pro.getProcessingRuleName());
                startPerson.setText(pro.getCreateUserName());
                personMechanism.setText(pro.getCreateOrganName());
                personPhone.setText(pro.getCreateUserPhone());
            }

            @Override
            public void onFiled(String code, String message) {

            }
        });
    }


    /**
     * 获取车辆数据
     */
    private void getCarInfo() {
        HttpServiceIml.getCarInfo(personBo.getIdx_idcard()).subscribe(new HttpResultSubscriber<List<CarBo>>() {
            @Override
            public void onSuccess(List<CarBo> carInfo) {
                if (getActivity() == null) {
                    return;
                }
                setCarAdapter(carInfo);
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
     * 设置车辆数据
     */
    private void setCarAdapter(List<CarBo> carInfo) {
        BaseQuickAdapter<CarBo, BaseViewHolder> adapter =
                new BaseQuickAdapter<CarBo, BaseViewHolder>(R.layout.item_car, carInfo) {
                    @Override
                    protected void convert(BaseViewHolder helper, CarBo item) {
                        helper.setText(R.id.car_num, item.getHao_pai_hao_ma());
                        helper.setText(R.id.car_num_type, item.getZhong_wen_pin_pai());
                        helper.setText(R.id.car_type, item.getChe_liang_xing_hao());
                    }
                };
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
