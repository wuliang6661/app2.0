package synway.module_stack.hm_leave.net;

import java.util.List;

import rx.Observable;
import synway.module_stack.api.ApiManager;
import synway.module_stack.api.RxResultHelper;
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

public class HttpServiceIml {

    static HttpService service;

    /**
     * 获取代理对象
     *
     * @return
     */
    public static HttpService getService() {
        if (service == null)
            service = ApiManager.getInstance().configRetrofit(HttpService.class, HttpService.url1);
        return service;
    }


    /**
     * 获取关注人员列表
     */
    public static Observable<List<PersonBo>> getAttentionList(String loginId) {
        AttentionBO attentionBO = new AttentionBO();
        attentionBO.setRecorderId(loginId);
        attentionBO.setStatus("1");
        return getService().getAttentionList(attentionBO).compose(RxResultHelper.httpRusult());
    }


    /**
     * 获取关注人员详情
     */
    public static Observable<BasicInfoBo> getPersonInfo(String card) {
        AttentionPersonInfoBO personInfoBO = new AttentionPersonInfoBO();
        personInfoBO.setIdx_idcard(card);
        return getService().getPersonInfo(personInfoBO).compose(RxResultHelper.httpRusult());
    }

    /**
     * 获取关注人员详情
     */
    public static Observable<List<CarBo>> getCarInfo(String cardId) {
        CarRequestBO requestBO = new CarRequestBO();
        requestBO.setIdcard(cardId);
        return getService().getCarInfo(requestBO).compose(RxResultHelper.httpRusult());
    }


    /**
     * 新增请假获取数据
     */
    public static Observable<LeaveInfoBo> getLeavePersonInfo(String cardId, String orgain) {
        SfzhBo sfzhBo = new SfzhBo();
        sfzhBo.setSfzh(cardId);
        sfzhBo.setLrrjg(orgain);
        return getService().getLeavePersonInfo(sfzhBo).compose(RxResultHelper.httpRusult());
    }

    /**
     * 通过身份证号查询请销假详细
     */
    public static Observable<LeaveBO> getQxjxq(String cardId) {
        SfzhBo sfzhBo = new SfzhBo();
        sfzhBo.setSfzh(cardId);
        return getService().getQxjxqBySfzh(sfzhBo).compose(RxResultHelper.httpRusult());
    }

    /**
     * 撤销假
     */
    public static Observable<String> endLeave(String cardId) {
        SfzhBo sfzhBo = new SfzhBo();
        sfzhBo.setSfzh(cardId);
        return getService().cancleLeave(sfzhBo).compose(RxResultHelper.httpRusult());
    }

    /**
     * 续假
     */
    public static Observable<String> xuLeave(String cardId, String time) {
        SfzhBo sfzhBo = new SfzhBo();
        sfzhBo.setSfzh(cardId);
        sfzhBo.setEnd(time);
        return getService().xuLeave(sfzhBo).compose(RxResultHelper.httpRusult());
    }

    /**
     * 请假
     */
    public static Observable<String> createLeave(CreateLeaveBo leaveBo) {
        return getService().createLeave(leaveBo)
                .compose(RxResultHelper.httpRusult());
    }


    /**
     * 根据身份证号查询预警信息
     */
    public static Observable<CheckBo> getWanningInfo(String cardId) {
        CarRequestBO requestBO = new CarRequestBO();
        requestBO.setIdcard(cardId);
        return getService().getWanningByIdCard(requestBO).compose(RxResultHelper.httpRusult());
    }

    /**
     * 提交预警核查信息
     */
    public static Observable<List<Object>> saveCheck(String uuid, String personCode, String personName, String personOrganCode,
                                                     String personOrganName, String checkResult, String comment, String checkPersonPhone) {
        SaveCheckBO saveCheckBO = new SaveCheckBO();
        saveCheckBO.setWarning_uuid(uuid);
        saveCheckBO.setCheck_person_code(personCode);
        saveCheckBO.setCheck_person_name(personName);
        saveCheckBO.setCheck_person_organcode(personOrganCode);
        saveCheckBO.setCheck_person_organname(personOrganName);
        saveCheckBO.setCheck_result(checkResult);
        saveCheckBO.setComment(comment);
        saveCheckBO.setCheck_person_phone(checkPersonPhone);
        return getService().saveCheckInfo(saveCheckBO).compose(RxResultHelper.httpRusult());
    }

    /**
     * 获取布撤控信息
     */
    public static Observable<ControlBo> getControlData(String idCard) {
        PersonIdCardBo personIdCardBo = new PersonIdCardBo();
        personIdCardBo.setPerson_idcard(idCard);
        return getService().getDataByPerson(personIdCardBo).compose(RxResultHelper.httpRusult());
    }

}
