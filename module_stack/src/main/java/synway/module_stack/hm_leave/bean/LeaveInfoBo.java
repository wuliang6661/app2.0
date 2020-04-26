package synway.module_stack.hm_leave.bean;

import com.contrarywind.interfaces.IPickerViewData;

import java.util.List;

public class LeaveInfoBo {


    private String idx_name;
    private String idx_phone;
    private String person_type;
    private String idx_current_residence_country;
    private String person_level;
    private String idx_current_residence_province;
    private String idx_current_residence_town;
    private String photo_url;
    private String idx_current_residence_rural;
    private String idx_current_residence_city;
    private List<AbleAreaBean> ableArea;

    public String getIdx_name() {
        return idx_name;
    }

    public void setIdx_name(String idx_name) {
        this.idx_name = idx_name;
    }

    public String getIdx_phone() {
        return idx_phone;
    }

    public void setIdx_phone(String idx_phone) {
        this.idx_phone = idx_phone;
    }

    public String getPerson_type() {
        return person_type;
    }

    public void setPerson_type(String person_type) {
        this.person_type = person_type;
    }

    public String getIdx_current_residence_country() {
        return idx_current_residence_country;
    }

    public void setIdx_current_residence_country(String idx_current_residence_country) {
        this.idx_current_residence_country = idx_current_residence_country;
    }

    public String getPerson_level() {
        return person_level;
    }

    public void setPerson_level(String person_level) {
        this.person_level = person_level;
    }

    public String getIdx_current_residence_province() {
        return idx_current_residence_province;
    }

    public void setIdx_current_residence_province(String idx_current_residence_province) {
        this.idx_current_residence_province = idx_current_residence_province;
    }

    public String getIdx_current_residence_town() {
        return idx_current_residence_town;
    }

    public void setIdx_current_residence_town(String idx_current_residence_town) {
        this.idx_current_residence_town = idx_current_residence_town;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getIdx_current_residence_rural() {
        return idx_current_residence_rural;
    }

    public void setIdx_current_residence_rural(String idx_current_residence_rural) {
        this.idx_current_residence_rural = idx_current_residence_rural;
    }

    public String getIdx_current_residence_city() {
        return idx_current_residence_city;
    }

    public void setIdx_current_residence_city(String idx_current_residence_city) {
        this.idx_current_residence_city = idx_current_residence_city;
    }

    public List<AbleAreaBean> getAbleArea() {
        return ableArea;
    }

    public void setAbleArea(List<AbleAreaBean> ableArea) {
        this.ableArea = ableArea;
    }

    public static class AbleAreaBean implements IPickerViewData {

        private String code;
        private String codeName;
        private String parentCode;
        private String parentCodeName;
        private List<ChildNodesBeanX> childNodes;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getCodeName() {
            return codeName;
        }

        public void setCodeName(String codeName) {
            this.codeName = codeName;
        }

        public String getParentCode() {
            return parentCode;
        }

        public void setParentCode(String parentCode) {
            this.parentCode = parentCode;
        }

        public String getParentCodeName() {
            return parentCodeName;
        }

        public void setParentCodeName(String parentCodeName) {
            this.parentCodeName = parentCodeName;
        }

        public List<ChildNodesBeanX> getChildNodes() {
            return childNodes;
        }

        public void setChildNodes(List<ChildNodesBeanX> childNodes) {
            this.childNodes = childNodes;
        }

        @Override
        public String getPickerViewText() {
            return codeName;
        }

        public static class ChildNodesBeanX implements IPickerViewData {

            private String code;
            private String codeName;
            private String parentCode;
            private String parentCodeName;
            private List<ChildNodesBean> childNodes;

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public String getCodeName() {
                return codeName;
            }

            public void setCodeName(String codeName) {
                this.codeName = codeName;
            }

            public String getParentCode() {
                return parentCode;
            }

            public void setParentCode(String parentCode) {
                this.parentCode = parentCode;
            }

            public String getParentCodeName() {
                return parentCodeName;
            }

            public void setParentCodeName(String parentCodeName) {
                this.parentCodeName = parentCodeName;
            }

            public List<ChildNodesBean> getChildNodes() {
                return childNodes;
            }

            public void setChildNodes(List<ChildNodesBean> childNodes) {
                this.childNodes = childNodes;
            }

            @Override
            public String getPickerViewText() {
                return codeName;
            }

            public static class ChildNodesBean implements IPickerViewData {
                /**
                 * code : 650502001200
                 * codeName : 上阿牙村委会
                 * parentCode : 650502001
                 * parentCodeName : 东河区街道
                 * childNodes : null
                 */

                private String code;
                private String codeName;
                private String parentCode;
                private String parentCodeName;
                private Object childNodes;

                public String getCode() {
                    return code;
                }

                public void setCode(String code) {
                    this.code = code;
                }

                public String getCodeName() {
                    return codeName;
                }

                public void setCodeName(String codeName) {
                    this.codeName = codeName;
                }

                public String getParentCode() {
                    return parentCode;
                }

                public void setParentCode(String parentCode) {
                    this.parentCode = parentCode;
                }

                public String getParentCodeName() {
                    return parentCodeName;
                }

                public void setParentCodeName(String parentCodeName) {
                    this.parentCodeName = parentCodeName;
                }

                public Object getChildNodes() {
                    return childNodes;
                }

                public void setChildNodes(Object childNodes) {
                    this.childNodes = childNodes;
                }

                @Override
                public String getPickerViewText() {
                    return codeName;
                }
            }
        }
    }
}
