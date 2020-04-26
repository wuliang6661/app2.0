package qyc.library.control.dialog_progress;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import synway.common.R;

public class TheDialog extends Dialog {

    public TheDialog(Context context, String title, String msg) {
        super(context);
        this.title = title;
        this.msg = msg;
    }

    private ProgressBar progressWheel = null;
    private TextView txtTitle, txtMsg;
    private String title, msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawableResource(R.color.lib_transparent);
        setContentView(R.layout.lib_dialogprogress_cv);
        progressWheel = findViewById(R.id.progressWheel1);
//		txtTitle = (TextView) findViewById(R.id.textView1);
//		txtMsg = (TextView) findViewById(R.id.textView2);
//		txtTitle.setText(title);
//		txtMsg.setText(msg);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
