package qyc.library.control.dialog_confirm;

import android.app.Dialog;
import android.content.Context;

public class DialogConfirm {

    public static Dialog show(Context context, String title, String msg, OnDialogConfirmClick onDialogConfirmClick) {
        TheDialog theDialog = new TheDialog(context, title, msg, onDialogConfirmClick, null, null);
        theDialog.show();
        return theDialog;
    }

    public static Dialog show(Context context, String title, String msg, OnDialogConfirmClick onDialogConfirmClick,
                              OnDialogConfirmCancel onDialogConfirmCancel) {
        TheDialog theDialog = new TheDialog(context, title, msg, onDialogConfirmClick, onDialogConfirmCancel, null);
        theDialog.show();
        return theDialog;
    }

    public static Dialog show(Context context, String title, String msg, OnDialogConfirmClick onDialogConfirmClick,
                              OnDialogConfirmCancel onDialogConfirmCancel, DialogConfirmCfg dialogConfirmCfg) {
        TheDialog theDialog = new TheDialog(context, title, msg, onDialogConfirmClick, onDialogConfirmCancel,
                dialogConfirmCfg);
        theDialog.show();
        return theDialog;
    }

    public static Dialog show(Context context, String title, String msg, OnDialogConfirmClick onDialogConfirmClick,
                              OnDialogConfirmCancel onDialogConfirmCancel,boolean cancel, DialogConfirmCfg dialogConfirmCfg) {
        TheDialog theDialog = new TheDialog(context, title, msg, onDialogConfirmClick, onDialogConfirmCancel,
                dialogConfirmCfg);
        theDialog.setCancelable(cancel);
        theDialog.show();
        return theDialog;
    }

    public static Dialog show(Context context, String title, String msg, OnDialogConfirmClick onDialogConfirmClick,
                              DialogConfirmCfg dialogConfirmCfg) {
        TheDialog theDialog = new TheDialog(context, title, msg, onDialogConfirmClick, null, dialogConfirmCfg);
        theDialog.show();
        return theDialog;
    }
}
