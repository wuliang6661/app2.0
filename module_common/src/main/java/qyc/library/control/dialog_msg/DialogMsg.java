package qyc.library.control.dialog_msg;

import android.content.Context;
import android.content.DialogInterface.OnDismissListener;

public class DialogMsg {

	public static final TheDialog show(Context context, String title, String msg) {
		TheDialog theDialog = new TheDialog(context, title, msg);
		theDialog.show();
		return theDialog;
	}

	public static final TheDialog show(Context context, String title, String msg, OnDismissListener onDismissListener) {
		TheDialog theDialog = new TheDialog(context, title, msg);
		theDialog.setOnDismissListener(onDismissListener);
		theDialog.show();
		return theDialog;
	}

	public static final TheDialogWithDetail showDetail(Context context, String title, String msg, String detail) {
		TheDialogWithDetail dialogWithDetail = new TheDialogWithDetail(context, title, msg, detail);
		dialogWithDetail.show();
		return dialogWithDetail;
	}

	public static final TheDialogWithDetail showDetail(Context context, String title, String msg, String detail,
			OnDismissListener onDismissListener) {
		TheDialogWithDetail dialogWithDetail = new TheDialogWithDetail(context, title, msg, detail);
		dialogWithDetail.setOnDismissListener(onDismissListener);
		dialogWithDetail.show();
		return dialogWithDetail;
	}
}
