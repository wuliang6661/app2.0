package qyc.library.control.dialog_confirm;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import synway.common.R;


class TheDialog extends Dialog {

	private TextView btnConfirm, btnCancel;
	private TextView txtTitle, txtMsg;
	private String title, msg;
	private OnDialogConfirmClick confirmClick = null;
	private OnDialogConfirmCancel confirmCancel = null;
	private DialogConfirmCfg dialogConfirmCfg = null;
	private Context context;
	public TheDialog(Context context, String title, String msg, OnDialogConfirmClick onDialogConfirmClick,
			OnDialogConfirmCancel onDialogConfirmCancel, DialogConfirmCfg dialogConfirmCfg) {
		super(context);
		this.context=context;
		this.title = title;
		this.msg = msg;
		this.confirmClick = onDialogConfirmClick;
		this.confirmCancel = onDialogConfirmCancel;
		this.dialogConfirmCfg = dialogConfirmCfg;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawableResource(R.color.lib_transparent);
		setContentView(R.layout.lib_dialogconfirm_cv);

		WindowManager.LayoutParams params = getWindow().getAttributes();
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		int w = displayMetrics.widthPixels;
		params.width = (int) (w * 0.8);
		getWindow().setAttributes(params);

		btnConfirm = findViewById(R.id.button1);
		btnCancel = findViewById(R.id.button2);
		txtTitle = findViewById(R.id.textView1);
		txtMsg = findViewById(R.id.textView2);
		txtTitle.setText(title);
		txtMsg.setText(msg);
		btnCancel.setOnClickListener(clickListener);
		btnConfirm.setOnClickListener(clickListener);
		btnCancel.setVisibility(View.VISIBLE);

		// 设置样式
		if (dialogConfirmCfg != null) {
			//文本颜色
			if (dialogConfirmCfg.textColor != 0) {
				txtMsg.setText(dialogConfirmCfg.textColor);
			}

			//确认按钮  颜色
			if (dialogConfirmCfg.confirmBtnColor != 0) {
				btnConfirm.setText(dialogConfirmCfg.confirmBtnColor);
			}

			//确认按钮 文本
			if (dialogConfirmCfg.confirmBtnText != null) {
				btnConfirm.setText(dialogConfirmCfg.confirmBtnText);
			}

			//取消按钮 颜色
			if (dialogConfirmCfg.cancelBtnColor != 0) {
				btnCancel.setText(dialogConfirmCfg.cancelBtnColor);
			}

			//取消按钮 
			if (dialogConfirmCfg.cancelBtnText != null) {
				btnCancel.setText(dialogConfirmCfg.cancelBtnText);
			}

		}

	}

	private View.OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v == btnConfirm) {
				if (confirmClick != null) {
					confirmClick.onDialogConfirmClick();
				}
			} else if (v == btnCancel) {
				if (confirmCancel != null) {
					confirmCancel.onDialogConfirmCancel();
				}
			}
			dismiss();
		}
	};
}
