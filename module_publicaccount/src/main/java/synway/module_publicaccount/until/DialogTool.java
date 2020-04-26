package synway.module_publicaccount.until;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

/**
 * Created by ysm on 2017/5/27.
 * 弹出对话框
 */

public class DialogTool {
    private static AlertDialog.Builder mAlertBuilder;
    public static AlertDialog dialog;
    /*
     * 自定义对话框
    */
    public static Window dialog(Context context, int layid) {
        mAlertBuilder = new AlertDialog.Builder(context);
        dialog = mAlertBuilder.create();
        View layout = LayoutInflater.from(context).inflate(layid, null);
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(layid);
        dialog.setCanceledOnTouchOutside(true);
        return window;

    }
}
