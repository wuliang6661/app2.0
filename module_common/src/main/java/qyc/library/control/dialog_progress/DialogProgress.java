package qyc.library.control.dialog_progress;

import android.app.Dialog;
import android.content.Context;

public class DialogProgress {
	public static final Dialog get(Context context, String title, String msg) {
		TheDialog theDialog = new TheDialog(context, title, msg);
		theDialog.setCancelable(true);
		return theDialog;
	}

}
