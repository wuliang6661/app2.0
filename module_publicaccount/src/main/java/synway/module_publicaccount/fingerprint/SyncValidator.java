package synway.module_publicaccount.fingerprint;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.webkit.JavascriptInterface;

import qyc.library.control.dialog_progress.DialogProgress;
import synway.module_interface.config.netConfig.Sps_NetConfig;
import synway.module_interface.config.userConfig.Sps_RWLoginUser;

/**
 * Created by QSJH on 2016/5/26 0026.
 */
public class SyncValidator {

    private Context context;
    private Dialog dialogProgress;
    private Handler handler = new Handler();

    public SyncValidator(Context context) {
        this.context = context;
        dialogProgress = DialogProgress.get(context, "正在验证", "");
    }

    @JavascriptInterface
    public String fingerprintValidate(String userID) {
        SamsungFingerPrint fingerprint = new SamsungFingerPrint(context);
        if (!fingerprint.isFingerprintSupported()) {
            return "0";
        }
        String fingerprintID = fingerprint.getFingerprintUniqueID();
        SyncGetFingerprint syncGetFingerprint = new SyncGetFingerprint();
        String[] idList;
        handler.post(new Runnable() {
            @Override
            public void run() {
                dialogProgress.show();
            }
        });

        String result = "";
        try {
            idList = syncGetFingerprint.getFingerprint(Sps_NetConfig.getNetConfigFromSpf(context).httpIP, Sps_NetConfig.getNetConfigFromSpf(context).httpPort, Sps_RWLoginUser.readUserID(context));
        } catch (Exception e) {
            result = "0";
            idList=null;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                dialogProgress.dismiss();
            }
        });

        if(result.equals("0")){
            return result;
        }

        for (String id : idList) {
            if (id.equals(fingerprintID)) {
                return "1";
            }
        }

        return "0";
    }
}
