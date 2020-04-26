package synway.module_publicaccount.push;

import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import qyc.net.push.synwayoscpush.SPushFacInterface;

/**
 * Created by huangxi
 * DATE :2019/1/3
 * Description ：公众号推送给Weex页面端消息
 */

public class OnPublicWeexMsg extends SPushFacInterface {
    @Override
    public int[] regist() {
        return new int[]{5003};
    }

    @Override
    public void onCreat(Object o) {

    }

    @Override
    public void onDestory() {

    }

    @Override
    public void onReceive(int type, String jsonStr) {
        if (type == 5003) {
            run5003(type, jsonStr);
        }

    }

    private void run5003(int type, String jsonStr) {
//        jsonStr="{\"MSG\":{\"captureTime\":\"1547020500\",\"cardNo\":\"\",\"userCode\":\"0571_01\",\"phoneNo\":\"\",\"lac\":\"22582\",\"targetValue\":\"13500001254\",\"telecom\":\"03\",\"realtimeTaskCode\":\"hx_alarm_app\",\"id\":\"b731fd2c-2d91-4f0e-a288-4feaade500fa\",\"create_date\":1547020650215,\"lat\":\"30.734321\",\"otherPhoneNo\":\"\",\"lng\":\"123.470324\",\"ci\":\"191466499\",\"baseStationName\":\"杭州三汇数字公司\",\"eventTypeText\":\"主叫\",\"targetType\":\"1\",\"eventType\":\"1\",\"phoneMisNo\":\"\",\"targetTypeText\":\"手机号码\",\"systemCode\":\"alarm_app\",\"back1\":\" \",\"back2\":\" \",\"dataSource\":\"144-med\",\"messageContent\":\"\"},\"GUID\":\"8e5fbc6d-4456-4ac0-b3a9-1b086ff64666\",\"TIME\":1547020650215}";

        if (jsonStr == null || jsonStr.equals("")) {
            return;
        }
        String publicGUID = null;
        String showTime = null;
        String jsonMsg = null;
        JSONObject jsonTotal = null;
        Log.d("OnPublicWeexMsg", "收到未解析的推送数据" + jsonStr);
        try {
            jsonTotal = new JSONObject(jsonStr);
            publicGUID = jsonTotal.getString("GUID");
            showTime = jsonTotal.getString("TIME");
            jsonMsg = jsonTotal.getString("MSG");
        } catch (JSONException e) {

            e.printStackTrace();
            return;
        }
        Intent intent = new Intent();
        intent.setAction(PushUtil.PublicWeexMsg.getAction(publicGUID));
        intent.putExtra(PushUtil.PublicWeexMsg.EXTRA_PUBLIC_WEEXMSG_GUID, publicGUID);
        intent.putExtra(PushUtil.PublicWeexMsg.EXTRA_PUBLIC_WEEXMSG_SOBJ, jsonMsg);
        intent.putExtra(PushUtil.PublicWeexMsg.EXTRA_PUBLIC_WEEXMSG_TIME, showTime);
        context.sendBroadcast(intent);
    }
}
