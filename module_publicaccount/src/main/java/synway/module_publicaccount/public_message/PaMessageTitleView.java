package synway.module_publicaccount.public_message;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import synway.module_publicaccount.R;

/**
 * Created by huangxi
 * DATE :2019/1/18
 * Description ：
 */

public class PaMessageTitleView {
    private View view = null;
    private TextView tvTitle = null;
    private ImageButton btnback = null;
    private Activity activity = null;

    public PaMessageTitleView(Activity act) {
        this.activity = act;
        view = act.findViewById(R.id.titlebar_block);
        view.setBackgroundColor(activity.getResources().getColor(R.color.mwhite));
        tvTitle = view.findViewById(R.id.lblTitle);
        btnback = view.findViewById(R.id.back);
        btnback.setOnClickListener(onClickListener);
    }

    public void setTitle(String title) {
        this.tvTitle.setText(title);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == btnback) {
                activity.finish();
            }
        }
    };
}
