package synway.module_publicaccount.config;

/**
 * Created by 13itch on 2016/8/4.
 */
public class APPConfigObj {
    public boolean IS_WIFI_OPEN = true;
    public boolean IS_INTENT_OPEN = true;
    public boolean IS_PISH_SLEEP = false;
    public int LOCATION_DEFAULT_TYPE = 0;
    public int LOCATION_DEFAULT_PERIOD = 8;
    public int REPORT_CONNECT_CLOSE_OUTTIME = 5;
    public boolean PPVOICE_GROUP_OPEN = true;
    public boolean PPVOICE_PERSON_OPEN = false;



    @Override
    public boolean equals(Object o) {
        final APPConfigObj newObj = (APPConfigObj) o;
        return this.IS_INTENT_OPEN == newObj.IS_INTENT_OPEN && this.IS_PISH_SLEEP == newObj.IS_PISH_SLEEP
                && this.IS_WIFI_OPEN == newObj.IS_WIFI_OPEN && this.PPVOICE_GROUP_OPEN == newObj.PPVOICE_GROUP_OPEN
                && this.PPVOICE_PERSON_OPEN == newObj.PPVOICE_PERSON_OPEN && this.LOCATION_DEFAULT_PERIOD == newObj.LOCATION_DEFAULT_PERIOD
                && this.LOCATION_DEFAULT_TYPE == newObj.LOCATION_DEFAULT_TYPE && this.REPORT_CONNECT_CLOSE_OUTTIME == newObj.REPORT_CONNECT_CLOSE_OUTTIME;
    }

    public void setIS_WIFI_OPEN(boolean IS_WIFI_OPEN) {
        this.IS_WIFI_OPEN = IS_WIFI_OPEN;
    }

    public void setIS_INTENT_OPEN(boolean IS_INTENT_OPEN) {
        this.IS_INTENT_OPEN = IS_INTENT_OPEN;
    }

    public void setIS_PISH_SLEEP(boolean IS_PISH_SLEEP) {
        this.IS_PISH_SLEEP = IS_PISH_SLEEP;
    }

    public void setLOCATION_DEFAULT_TYPE(int LOCATION_DEFAULT_TYPE) {
        this.LOCATION_DEFAULT_TYPE = LOCATION_DEFAULT_TYPE;
    }

    public void setLOCATION_DEFAULT_PERIOD(int LOCATION_DEFAULT_PERIOD) {
        this.LOCATION_DEFAULT_PERIOD = LOCATION_DEFAULT_PERIOD;
    }

    public void setREPORT_CONNECT_CLOSE_OUTTIME(int REPORT_CONNECT_CLOSE_OUTTIME) {
        this.REPORT_CONNECT_CLOSE_OUTTIME = REPORT_CONNECT_CLOSE_OUTTIME;
    }

    public void setPPVOICE_GROUP_OPEN(boolean PPVOICE_GROUP_OPEN) {
        this.PPVOICE_GROUP_OPEN = PPVOICE_GROUP_OPEN;
    }

    public void setPPVOICE_PERSON_OPEN(boolean PPVOICE_PERSON_OPEN) {
        this.PPVOICE_PERSON_OPEN = PPVOICE_PERSON_OPEN;
    }
}
