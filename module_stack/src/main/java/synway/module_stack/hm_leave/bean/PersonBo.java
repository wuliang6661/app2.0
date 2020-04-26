package synway.module_stack.hm_leave.bean;

import java.io.Serializable;

public class PersonBo implements Serializable {


    /**
     * gender : 男
     * idx_current_address : 杭州三汇
     * idx_idcard : 330312345
     * idx_name : 张三
     * idx_nation : 汉族a
     * idx_phone : 13847284493
     * person_level : 一般类
     * person_type : 涉军人员,信访人员
     * photo_url : 照片url
     * pk_uuid : 5f83db7c-fbc4-4d49-99d5-fc46394b6341
     */

    private String gender;
    private String idx_current_address;
    private String idx_idcard;
    private String idx_name;
    private String idx_nation;
    private String idx_phone;
    private String person_level;
    private String person_type;
    private String photo_url;
    private String pk_uuid;
    private String photoDetail;
    /**
     * birth_residence_adress :
     */

    private String birth_residence_adress;

    public String getPhotoDetail() {
        return photoDetail;
    }

    public void setPhotoDetail(String photoDetail) {
        this.photoDetail = photoDetail;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIdx_current_address() {
        return idx_current_address;
    }

    public void setIdx_current_address(String idx_current_address) {
        this.idx_current_address = idx_current_address;
    }

    public String getIdx_idcard() {
        return idx_idcard;
    }

    public void setIdx_idcard(String idx_idcard) {
        this.idx_idcard = idx_idcard;
    }

    public String getIdx_name() {
        return idx_name;
    }

    public void setIdx_name(String idx_name) {
        this.idx_name = idx_name;
    }

    public String getIdx_nation() {
        return idx_nation;
    }

    public void setIdx_nation(String idx_nation) {
        this.idx_nation = idx_nation;
    }

    public String getIdx_phone() {
        return idx_phone;
    }

    public void setIdx_phone(String idx_phone) {
        this.idx_phone = idx_phone;
    }

    public String getPerson_level() {
        return person_level;
    }

    public void setPerson_level(String person_level) {
        this.person_level = person_level;
    }

    public String getPerson_type() {
        return person_type;
    }

    public void setPerson_type(String person_type) {
        this.person_type = person_type;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getPk_uuid() {
        return pk_uuid;
    }

    public void setPk_uuid(String pk_uuid) {
        this.pk_uuid = pk_uuid;
    }

    public String getBirth_residence_adress() {
        return birth_residence_adress;
    }

    public void setBirth_residence_adress(String birth_residence_adress) {
        this.birth_residence_adress = birth_residence_adress;
    }
}
