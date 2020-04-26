package cn.synway.app.bean;

import java.util.List;

public class PersonInPsListBO {
    List<OrganAndUserInfoBO> organList;
    List<OrganAndUserInfoBO> userInfoList;
    List<TagList> fatherOrganList;//tags

    public void setUserInfoList(List<OrganAndUserInfoBO> userInfoList) {
        this.userInfoList = userInfoList;
    }

    public List<OrganAndUserInfoBO> getUserInfoList() {
        return userInfoList;
    }

    public List<OrganAndUserInfoBO> getOrganList() {
        return organList;
    }

    public void setOrganList(List<OrganAndUserInfoBO> organList) {
        this.organList = organList;
    }

    public List<OrganAndUserInfoBO> getList() {
        organList.addAll(userInfoList);
        return organList;
    }

    public void setFatherOrganList(List<TagList> fatherOrganList) {
        this.fatherOrganList = fatherOrganList;
    }

    public List<TagList> getFatherOrganList() {
        return fatherOrganList;
    }

    public class OrganAndUserInfoBO {

        /**
         * id : 1
         * father : 0
         * name : 父机构
         * province : null
         * area : null
         * date : null
         * areaType : 1
         */

        private String userCount;
        private String id;
        private Integer father;
        private String name;
        private String province;
        private String area;
        private String date;
        private int areaType;

        /**
         * userID : 123456
         * userName : 人员
         * mobilePhoneNo : 13213121
         * sex : 1
         * organ : 1
         * code : 123456
         * passwd :
         * organName :
         * updateTime : 1560997808025
         * isdelete : 0
         * type : 1
         */

        private String userID;
        private String userName;
        private String mobilePhoneNo;
        private int sex;
        private String organ;
        private String code;
        private String passwd;
        private String organName;
        private long updateTime;
        private int isdelete;
        private int type;
        private String firstChar;
        private boolean isHead;
        private String userPic;

        public OrganAndUserInfoBO(String s, boolean b) {
            this.setUserName(s + b);
            this.setFirstChar(s);
            this.isHead = b;
        }

        public void setUserCount(String userCount) {
            this.userCount = userCount;
        }

        public String getUserCount() {
            return userCount;
        }

        public void setUserPic(String userPic) {
            this.userPic = userPic;
        }

        public String getUserPic() {
            return userPic;
        }

        public void setHead(boolean head) {
            isHead = head;
        }

        public boolean isHead() {
            return isHead;
        }

        public String getFirstChar() {
            return firstChar;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Integer getFather() {
            return father;
        }

        public void setFather(Integer father) {
            this.father = father;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public Object getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public Object getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getAreaType() {
            return areaType;
        }

        public void setAreaType(int areaType) {
            this.areaType = areaType;
        }

        public String getUserID() {
            return userID;
        }

        public void setUserID(String userID) {
            this.userID = userID;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getMobilePhoneNo() {
            return mobilePhoneNo;
        }

        public void setMobilePhoneNo(String mobilePhoneNo) {
            this.mobilePhoneNo = mobilePhoneNo;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public String getOrgan() {
            return organ;
        }

        public void setOrgan(String organ) {
            this.organ = organ;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getPasswd() {
            return passwd;
        }

        public void setPasswd(String passwd) {
            this.passwd = passwd;
        }

        public String getOrganName() {
            return organName;
        }

        public void setOrganName(String organName) {
            this.organName = organName;
        }

        public long getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }

        public int getIsdelete() {
            return isdelete;
        }

        public void setIsdelete(int isdelete) {
            this.isdelete = isdelete;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public void setFirstChar(String chart) {
            this.firstChar = chart;
        }
    }

    public class TagList{

        /**
         * id : 1
         * father : 0
         * name : 父机构
         * province : null
         * area : null
         * date : null
         * areaType : 1
         */

        private String id;
        private int father;
        private String name;
        private String province;
        private String area;
        private String date;
        private int areaType;
        public TagList(String id, String name) {
            this.id= id;
            this.name=name;
        }
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getFather() {
            return father;
        }

        public void setFather(int father) {
            this.father = father;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getAreaType() {
            return areaType;
        }

        public void setAreaType(int areaType) {
            this.areaType = areaType;
        }
    }
}
