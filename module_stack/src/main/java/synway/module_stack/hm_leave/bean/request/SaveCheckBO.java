package synway.module_stack.hm_leave.bean.request;

public class SaveCheckBO {

    private String warning_uuid;
    private String check_person_code;
    private String check_person_name;
    private String check_person_organcode;
    private String check_person_organname;
    private String check_result;
    private String comment;
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

    public String getCheck_person_code() {
        return check_person_code;
    }

    public void setCheck_person_code(String check_person_code) {
        this.check_person_code = check_person_code;
    }

    public String getCheck_person_name() {
        return check_person_name;
    }

    public void setCheck_person_name(String check_person_name) {
        this.check_person_name = check_person_name;
    }

    public String getCheck_person_organcode() {
        return check_person_organcode;
    }

    public void setCheck_person_organcode(String check_person_organcode) {
        this.check_person_organcode = check_person_organcode;
    }

    public String getCheck_person_organname() {
        return check_person_organname;
    }

    public void setCheck_person_organname(String check_person_organname) {
        this.check_person_organname = check_person_organname;
    }

    public String getCheck_result() {
        return check_result;
    }

    public void setCheck_result(String check_result) {
        this.check_result = check_result;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
