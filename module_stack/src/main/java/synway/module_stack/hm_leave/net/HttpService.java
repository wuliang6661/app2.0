package synway.module_stack.hm_leave.net;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;
import synway.module_stack.hm_leave.bean.BasicInfoBo;
import synway.module_stack.hm_leave.bean.CarBo;
import synway.module_stack.hm_leave.bean.CheckBo;
import synway.module_stack.hm_leave.bean.ControlBo;
import synway.module_stack.hm_leave.bean.CreateLeaveBo;
import synway.module_stack.hm_leave.bean.LeaveBO;
import synway.module_stack.hm_leave.bean.LeaveInfoBo;
import synway.module_stack.hm_leave.bean.PersonBo;
import synway.module_stack.hm_leave.bean.request.AttentionBO;
import synway.module_stack.hm_leave.bean.request.AttentionPersonInfoBO;
import synway.module_stack.hm_leave.bean.request.CarRequestBO;
import synway.module_stack.hm_leave.bean.request.PersonIdCardBo;
import synway.module_stack.hm_leave.bean.request.SaveCheckBO;
import synway.module_stack.hm_leave.bean.request.SfzhBo;

public interface HttpService {

    String baseIp = "172.18.100.37";

    String url1 = "http://" + baseIp + ":8086";   //人员信息
    String url2 = "http://" + baseIp + ":8084";   //请销假
    String url3 = "http://" + baseIp + ":8087";   //预警消息
    String url4 = "http://" + baseIp + ":10004";   //布撤控

    /**
     * 获取我的关注人员列表
     */
    @POST(url1 + "/attention/getAttentionPersonInfoList")
    Observable<BaseResult<List<PersonBo>>> getAttentionList(@Body AttentionBO recoderId);

    /**
     * 查询关注人员基本详情
     */
    @POST(url1 + "/attention/getAttentionPersonInfoByIdcardOrUUID")
    Observable<BaseResult<BasicInfoBo>> getPersonInfo(@Body AttentionPersonInfoBO uuid);


    /**
     * 根据身份证号查询车辆信息
     */
    @POST(url1 + "/attention/getBaseCarInfoByIdcardOrPlateNumber")
    Observable<BaseResult<List<CarBo>>> getCarInfo(@Body CarRequestBO idCard);


    /**
     * 请假时身份证验证与获取数据
     */
    @POST(url2 + "/leave/getPersonInfo")
    Observable<BaseResult<LeaveInfoBo>> getLeavePersonInfo(@Body SfzhBo cardId);

    /**
     * 通过身份证号查询请销假详细
     */
    @POST(url2 + "/leave/getQxjxqBySfzh")
    Observable<BaseResult<LeaveBO>> getQxjxqBySfzh(@Body SfzhBo cardId);

    /**
     * 销假
     */
    @POST(url2 + "/leave/xiaojia")
    Observable<BaseResult<String>> cancleLeave(@Body SfzhBo cardId);

    /**
     * 请假
     */
    @POST(url2 + "/leave/qingjia")
    Observable<BaseResult<String>> createLeave(@Body CreateLeaveBo createLeaveBo);

    /**
     * 续假
     */
    @POST(url2 + "/leave/xujia")
    Observable<BaseResult<String>> xuLeave(@Body SfzhBo cardId);

    /**
     * 根据身份号获取预警信息
     */
    @POST(url3 + "/warning/getWarningInfoByIdcard")
    Observable<BaseResult<CheckBo>> getWanningByIdCard(@Body CarRequestBO idCard);


    /**
     * 提交核查
     */
    @POST(url3 + "/warning/saveCheckInfo")
    Observable<BaseResult<List<Object>>> saveCheckInfo(@Body SaveCheckBO saveCheckBO);

    /**
     * 查询布撤控信息
     */
    @POST(url4 + "/exeControl/cq/getDataByPersonIdcard.do")
    Observable<BaseResult<ControlBo>> getDataByPerson(@Body PersonIdCardBo idCard);

}
