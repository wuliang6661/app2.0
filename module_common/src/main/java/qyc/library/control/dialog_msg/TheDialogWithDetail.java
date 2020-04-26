package qyc.library.control.dialog_msg;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import synway.common.R;


public class TheDialogWithDetail extends Dialog {

	private TextView btnConfirm;
	private TextView txtTitle, txtMsg;
	private TextView txtDetailClick,txtDetailInfo;
	private String title, msg, detail;
	private Context context;
	public TheDialogWithDetail(Context context, String title, String msg, String detail) {
		super(context);
		this.context=context;
		this.title = title;
		this.msg = msg;
		this.detail = detail;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawableResource(R.color.lib_transparent);
		setContentView(R.layout.lib_dialogmsg_detail_cv);

		WindowManager.LayoutParams params = getWindow().getAttributes();
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		int w = displayMetrics.widthPixels;
		params.width = (int) (w * 0.8);
		getWindow().setAttributes(params);

		btnConfirm = findViewById(R.id.button1);
		txtTitle = findViewById(R.id.textView1);
		txtMsg = findViewById(R.id.textView2);
		txtTitle.setText(title);
		txtMsg.setText(msg);
		
		txtDetailClick = findViewById(R.id.textView3);
		txtDetailClick.setOnClickListener(clickListener);
		txtDetailInfo = findViewById(R.id.textView4);
		txtDetailInfo.setText(detail);
		
		btnConfirm.setOnClickListener(clickListener);
		btnConfirm.setText("确定");
	}

	private View.OnClickListener clickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v == btnConfirm) {
				dismiss();
			}else if(v == txtDetailClick){
				if(txtDetailInfo.getVisibility() != View.VISIBLE)
					txtDetailInfo.setVisibility(View.VISIBLE);
				else
					txtDetailInfo.setVisibility(View.GONE);
			}
		}
	};

}
