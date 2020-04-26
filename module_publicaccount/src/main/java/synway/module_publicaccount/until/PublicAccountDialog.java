package synway.module_publicaccount.until;

import android.content.Context;
import android.text.TextUtils;

import qyc.library.control.dialog_msg.DialogMsg;

/**
 *
 * Created by dell on 2017/7/24.
 */

public class PublicAccountDialog {
    public static void show(Context context,String title, String reason, String detail){
        if (TextUtils.isEmpty(detail)){
            if(reason.contains("org")) {
                DialogMsg.showDetail(context, title, "请检查网络", reason);
            }else {
                DialogMsg.show(context, title, reason);
            }
        }else {
            DialogMsg.showDetail(context, title, reason, detail);
        }
    }
}
