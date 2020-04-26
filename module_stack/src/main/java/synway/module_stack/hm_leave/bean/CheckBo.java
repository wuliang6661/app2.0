package synway.module_stack.hm_leave.bean;

public class CheckBo {


    /**
     * check_result : 123
     * check_time : 2019-02-27 09:31:39.0
     * comment : 123
     * ctrl_processing_rule_name : 核查
     * warning_address : 哈密市xx街道
     * warning_rule : 不得出哈密
     * warning_time : 2019-01-01 01:01:01.0
     */

    private String check_result;
    private String check_time;
    private String comment;
    private String ctrl_processing_rule_name;
    private String warning_address;
    private String warning_rule;
    private String warning_time;
    private String type;    //1 为核查 0未核查
    /**
     * check_person_name : qwe
     * check_person_organname : asd
     */

    private String check_person_name;
    private String check_person_organname;
    private String warning_uuid;
    private String check_person_phone;

    public String getCheck_person_phone() {
        return check_person_phone;
    }

    public void setCheck_person_phone(String check_person_phone) {
        this.check_person_phone = check_person_phone;
    }

    public String getWarning_uuid() {
        return warning_uuid;
    }

    public void setWarning_uuid(String warning_uuid) {
        this.warning_uuid = warning_uuid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCheck_result() {
        return check_result;
    }

    public void setCheck_result(String check_result) {
        this.check_result = check_result;
    }

    public String getCheck_time() {
        return check_time;
    }

    public void setCheck_time(String check_time) {
        this.check_time = check_time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCtrl_processing_rule_name() {
        return ctrl_processing_rule_name;
    }

    public void setCtrl_processing_rule_name(String ctrl_processing_rule_name) {
        this.ctrl_processing_rule_name = ctrl_processing_rule_name;
    }

    public String getWarning_address() {
        return warning_address;
    }

    public void setWarning_address(String warning_address) {
        this.warning_address = warning_address;
    }

    public String getWarning_rule() {
        return warning_rule;
    }

    public void setWarning_rule(String warning_rule) {
        this.warning_rule = warning_rule;
    }

    public String getWarning_time() {
        return warning_time;
    }

    public void setWarning_time(String warning_time) {
        this.warning_time = warning_time;
    }

    public String getCheck_person_name() {
        return check_person_name;
    }

    public void setCheck_person_name(String check_person_name) {
        this.check_person_name = check_person_name;
    }

    public String getCheck_person_organname() {
        return check_person_organname;
    }

    public void setCheck_person_organname(String check_person_organname) {
        this.check_person_organname = check_person_organname;
    }
}
