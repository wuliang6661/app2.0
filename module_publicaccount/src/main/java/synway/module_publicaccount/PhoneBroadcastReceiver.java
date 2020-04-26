package synway.module_publicaccount;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.json.JSONObject;

import qyc.library.tool.http.HttpPost;
import synway.module_interface.config.netConfig.NetConfig;
import synway.module_interface.config.netConfig.Sps_NetConfig;

/**
 * Created by admin on 2017/10/27.
 */

public class PhoneBroadcastReceiver  extends BroadcastReceiver {
    private static final String TAG = "message";
    private static boolean mIncomingFlag = false;
    private static String mIncomingNumber = null;
    private Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        // 如果是拨打电话
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            mIncomingFlag = false;
            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.i(TAG, "call OUT:" + phoneNumber);

        } else {
            // 如果是来电
            TelephonyManager tManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            switch (tManager.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING:
                    mIncomingNumber = intent.getStringExtra("incoming_number");
                    Log.i(TAG, "RINGING :" + mIncomingNumber);
                    thread.start();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if (mIncomingFlag) {
                        Log.i(TAG, "incoming ACCEPT :" + mIncomingNumber);
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    if (mIncomingFlag) {
                        Log.i(TAG, "incoming IDLE");
                    }
                    break;
            }
        }
    }
    Thread thread=new Thread(new Runnable() {
        @Override
        public void run() {
            NetConfig netConfig= Sps_NetConfig.getNetConfigFromSpf(context);
            String mURL = String.format("http://%s:%s/OSCService/Main/DownloadConfig.osc",netConfig.httpIP , netConfig.httpPort);
            JSONObject resultJson = HttpPost.postJsonObj(mURL, new JSONObject());
            String result[] = HttpPost.checkResult(resultJson);
            Log.i(TAG, "请求结果 :" + resultJson);
        }
    });
    /*@Override
    public void onReceive(Context context, Intent intent) {
        String number = getResultData();
        if("5556".equals(number)){
            setResultData(null);//挂断
        }else{
            number = "12593"+ number; //其他，则加区号
            setResultData(number);
        }
    }*/
}
