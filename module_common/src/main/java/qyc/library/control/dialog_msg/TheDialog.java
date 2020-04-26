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


public class TheDialog extends Dialog {

    private TextView btnConfirm;
    private TextView txtTitle, txtMsg;
    private String title, msg;
    private Context context;

    public TheDialog(Context context, String title, String msg) {
        super(context);
        this.context = context;
        this.title = title;
        this.msg = msg;
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawableResource(R.color.lib_transparent);
        setContentView(R.layout.lib_dialogmsg_cv);

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
        btnConfirm.setOnClickListener(clickListener);
        btnConfirm.setText("确定");
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (v == btnConfirm) {
                dismiss();
            }
        }
    };

}
