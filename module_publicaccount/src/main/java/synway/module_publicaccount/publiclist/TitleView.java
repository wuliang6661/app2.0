package synway.module_publicaccount.publiclist;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import synway.module_publicaccount.R;

class TitleView {

	private View view = null;
	private TextView tvTitle = null;
	private ImageButton btnback = null;
	private Activity activity = null;

	public TitleView(Activity act){
		this.activity = act;
		view = act.findViewById(R.id.titlebar_block);
		tvTitle = view.findViewById(R.id.lblTitle);
		btnback = view.findViewById(R.id.back);
		btnback.setOnClickListener(onClickListener);
	}
	
	public void setTitle(String title){
		this.tvTitle.setText(title);
	}
	
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v == btnback){
				activity.finish();
			}
		}
	};
}