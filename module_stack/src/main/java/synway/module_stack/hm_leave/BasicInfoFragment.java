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
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Subscription;
import synway.common.base.BaseFragment;
import synway.module_stack.R;
import synway.module_stack.api.HttpResultSubscriber;
import synway.module_stack.hm_leave.bean.BasicInfoBo;
import synway.module_stack.hm_leave.bean.CarBo;
import synway.module_stack.hm_leave.bean.PersonBo;
import synway.module_stack.hm_leave.net.HttpServiceIml;
import synway.module_stack.utils.CallPhoneUtils;

public class BasicInfoFragment extends BaseFragment implements View.OnClickListener {

    TextView personName;
    TextView personDate;
    TextView personHeight;
    TextView personNationality;
    TextView personMarriage;
    TextView personAge;
    TextView personWeight;
    TextView personCulture;
    TextView personJob;
    TextView personWork;
    TextView personAddress;
    TextView personPolice;
    TextView personNowAddress;
    TextView personReson;
    TagFlowLayout idFlowlayout;
    TextView personStart;
    TextView personCity;
    TextView startPersonPhone;
    RecyclerView recyclerView;

    View view;

    private PersonBo personBo;
    private BasicInfoBo basicInfoBo;

    List<Subscription> subscriptions;

    public static BasicInfoFragment newInstance(PersonBo personBo) {
        BasicInfoFragment fragment = new BasicInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable("person", personBo);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fra_basic_info, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        personBo = (PersonBo) getArguments().getSerializable("person");
        initView();

        subscriptions = new ArrayList<>();
        getPersonInfo();
        getCarInfo();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        personName = view.findViewById(R.id.person_name);
        personDate = view.findViewById(R.id.person_date);
        personHeight = view.findViewById(R.id.person_height);
        personNationality = view.findViewById(R.id.person_nationality);
        personMarriage = view.findViewById(R.id.person_marriage);
        personAge = view.findViewById(R.id.person_age);
        personWeight = view.findViewById(R.id.person_weight);
        personCulture = view.findViewById(R.id.person_culture);
        personJob = view.findViewById(R.id.person_job);
        personWork = view.findViewById(R.id.person_work);
        personAddress = view.findViewById(R.id.person_address);
        personPolice = view.findViewById(R.id.person_police);
        personNowAddress = view.findViewById(R.id.person_now_address);
        personReson = view.findViewById(R.id.person_reson);
        idFlowlayout = view.findViewById(R.id.id_flowlayout);
        recyclerView = view.findViewById(R.id.recycle_view);
        personStart = view.findViewById(R.id.person_start);
        personCity = view.findViewById(R.id.person_city);
        startPersonPhone = view.findViewById(R.id.start_person_phone);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setNestedScrollingEnabled(false);
    }


    /**
     * 获取人员基本详情
     */
    private void getPersonInfo() {
        subscriptions.add(HttpServiceIml.getPersonInfo(personBo.getIdx_idcard()).subscribe(new HttpResultSubscriber<BasicInfoBo>() {
            @Override
            public void onSuccess(BasicInfoBo s) {
                if (getActivity() != null) {
                    basicInfoBo = s;
                    setMessage();
                }
            }

            @Override
            public void onFiled(String code, String message) {
                if (getActivity() != null) {
                    showToast(message);
                }
            }
        }));
    }


    /**
     * 获取车辆数据
     */
    private void getCarInfo() {
        subscriptions.add(HttpServiceIml.getCarInfo(personBo.getIdx_idcard()).subscribe(new HttpResultSubscriber<List<CarBo>>() {
            @Override
            public void onSuccess(List<CarBo> carInfo) {
                if (getActivity() != null) {
                    setCarAdapter(carInfo);
                }
            }

            @Override
            public void onFiled(String code, String message) {
                if (getActivity() != null) {
                    showToast(message);
                }
            }
        }));
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


    /**
     * 设置界面数据
     */
    private void setMessage() {
        personName.setText(basicInfoBo.getIdx_name());
        personNationality.setText(basicInfoBo.getCountry());
        personDate.setText(basicInfoBo.getBirthday());
        personHeight.setText(basicInfoBo.getIdx_height());
        personMarriage.setText(basicInfoBo.getMarital_status());
        personAge.setText(basicInfoBo.getIdx_age());
        personCulture.setText(basicInfoBo.getEducation());
        personJob.setText(basicInfoBo.getEmployment());
        personWork.setText(basicInfoBo.getWorkplace());
        personAddress.setText(basicInfoBo.getBirth_residence_adress());
        personPolice.setText(basicInfoBo.getBirth_police_name());
        personNowAddress.setText(basicInfoBo.getIdx_current_address());
        personReson.setText(basicInfoBo.getComment());
        personStart.setText(basicInfoBo.getPerson_in_charge());
        personCity.setText(basicInfoBo.getPerson_in_charge_organname());
        startPersonPhone.setText(basicInfoBo.getPerson_in_charge_phone());

        String[] types = basicInfoBo.getPerson_type().split(",");
        List<String> tags = Arrays.asList(types);
        idFlowlayout.setAdapter(new TagAdapter<String>(tags) {
            @Override
            public View getView(FlowLayout parent, int position, String o) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_tag, idFlowlayout, false);
                TextView text = view.findViewById(R.id.tag_text);
                text.setText(o);
                return view;
            }
        });
        startPersonPhone.setOnClickListener(this);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.start_person_phone) {
            if (getActivity() != null) {
                CallPhoneUtils.showCallPhone(getActivity(), basicInfoBo.getPerson_in_charge_phone());
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        for (Subscription subscription : subscriptions) {
            if (subscription != null && !subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
        }
    }
}
