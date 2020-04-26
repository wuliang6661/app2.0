package qyc.library.control.dialog_list;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.common.StringUtils;

import synway.common.R;


public class TheDialog extends Dialog {

	private Button btnCancel;
	private TextView txtTitle;
	private ListView listView;
	private String[] strs;
	private String titleStr;
	private OnClickListener listener;
	private RelativeLayout title_layout;

	public TheDialog(Context context, String title, String[] strs, OnClickListener listener) {
		super(context);
		this.titleStr = title;
		this.strs = strs;
		this.listener = listener;

		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawableResource(R.color.lib_transparent);
		setContentView(R.layout.lib_dialoglist_cv);
		btnCancel = findViewById(R.id.button1);
		txtTitle = findViewById(R.id.textView1);
		title_layout = findViewById(R.id.title_layout);
		if(TextUtils.isEmpty(titleStr)){
			title_layout.setVisibility(View.GONE);
		}else{
			txtTitle.setText(titleStr);
		}
		listView = findViewById(R.id.listView1);
		TheAdapter theAdapter = new TheAdapter(getContext(), strs);
		listView.setAdapter(theAdapter);
		listView.setOnItemClickListener(onItemClickListener);
		btnCancel.setOnClickListener(clickListener);
	}

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			listener.onClick(TheDialog.this, position);
			dismiss();
		}
	};

	private View.OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v == btnCancel) {
				dismiss();
			}
		}
	};

}
