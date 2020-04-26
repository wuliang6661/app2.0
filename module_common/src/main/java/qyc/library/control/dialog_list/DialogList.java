package qyc.library.control.dialog_list;

import android.content.Context;
import android.content.DialogInterface;

public class DialogList {

	/** 显示多选对话框,需要调用show()函数才能弹出 */
	public static final void show(Context context, String title, String[] listItem,
			DialogInterface.OnClickListener listener) {
		TheDialog theDialog=new TheDialog(context, title, listItem, listener);
		theDialog.show();
		
//		AlertDialog.Builder diaLog = new AlertDialog.Builder(context);
//		diaLog = diaLog.setTitle(title);
//		diaLog = diaLog.setItems(listItem, listener);
//		diaLog.setPositiveButton("取消", null).create();
//		diaLog.show();
	}
}
