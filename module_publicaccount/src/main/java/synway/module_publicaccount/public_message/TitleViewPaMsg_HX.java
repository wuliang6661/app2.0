package synway.module_publicaccount.public_message;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import synway.module_publicaccount.Main;
import synway.module_publicaccount.R;

/**
 * 汇信版本的应用中心的标题栏
 */

public class TitleViewPaMsg_HX extends TitleViewPaMsg{

    private Activity activity = null;
    private View view = null;
    private View btn_popup = null;
    private int count = 0;
    private TextView countView;


    @Override public void setCountViewText(int count) {
        if (countView != null) {
            if (count > 0) {
                countView.setVisibility(View.VISIBLE);
                if (count <= 99) {
                    countView.setText(String.valueOf(count));
                } else {
                    countView.setText("99+");
                }

            } else {
                countView.setVisibility(View.INVISIBLE);
            }
        }
    }


    @Override public View initTitleView(Activity act) {
        this.activity = act;
        LayoutInflater layoutInflater = LayoutInflater.from(act);
        view = layoutInflater.inflate(R.layout.titlebar_public_message, null);
        SQLiteOpenHelper sqliteHelp = Main.instance().moduleHandle.getSQLiteHelp();
        count = PublicMessage.getAllUnReadCount(sqliteHelp.getWritableDatabase());
        countView = view.findViewById(R.id.textView1);
        if (count > 0) {
            countView.setVisibility(View.VISIBLE);
            if (count <= 99) {
                countView.setText(String.valueOf(count));
            } else {
                countView.setText("99+");
            }
        } else {
            countView.setVisibility(View.INVISIBLE);
        }
        btn_popup = view.findViewById(R.id.popup);
        btn_popup.setVisibility(View.VISIBLE);
        btn_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(activity, PAMessageViewAct.class);
                activity.startActivity(intent);
            }
        });
        return view;
    }
}
