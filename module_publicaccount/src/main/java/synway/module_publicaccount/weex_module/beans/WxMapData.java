package synway.module_publicaccount.weex_module.beans;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by dell on 2018/11/13.
 * 说明：
 */

public class WxMapData implements Serializable {
    private Map<String, Object> wxMapData;

    public Map<String, Object> getWxMapData() {
        return wxMapData;
    }

    public void setWxMapData(Map<String, Object> wxMapData) {
        this.wxMapData = wxMapData;
    }
    public Map<String, Object> addMapData(String key,String value){
        if(wxMapData!=null){
            wxMapData.put(key,value);
        }
        return  wxMapData;

    }
}
