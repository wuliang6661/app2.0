package synway.module_publicaccount.notice;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import synway.module_publicaccount.R;
import synway.module_publicaccount.analytical.obj.noticeview.ClickNoticeItem;

public class NoticeDetailActivity extends Activity {

    public static final String CLICK_INFO_JSON = "clickInfoJson";
    private ImageView ivBack;
    private TextView tvTitle;
    private TextView tvSubTitle;
    private TextView tvPublishTime;
    private TextView tvPublishName;
    private LinearLayout llTextContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_notice_detail);

        initViews();

        String clickInfoJson = getIntent().getStringExtra(CLICK_INFO_JSON);

        ClickNoticeItem clickNoticeItem = getClickNoticeItem(clickInfoJson);

        if (clickNoticeItem != null) {
            updateViews(clickNoticeItem);
        }

    }


    private void updateViews(ClickNoticeItem clickNoticeItem) {
        tvTitle.setText(clickNoticeItem.title);
        tvSubTitle.setText(clickNoticeItem.subTitle);
        tvPublishTime.setText(clickNoticeItem.dateTime);
        tvPublishName.setText(clickNoticeItem.publishName);

        for (String section : clickNoticeItem.sections) {
            TextView textView = getTextView(section);
            llTextContent.addView(textView);
        }

    }


    private TextView getTextView(String section) {
        TextView textView = new TextView(this);
        textView.setText(section);
        textView.setTextColor(Color.parseColor("#3F3F3F"));
        textView.setTextSize(16);

        return textView;
    }






    private ClickNoticeItem getClickNoticeItem(String clickInfoJson) {
        ClickNoticeItem clickNoticeItem = new ClickNoticeItem();
        try {
            JSONObject jsonObject = new JSONObject(clickInfoJson);
            JSONObject notice = jsonObject.getJSONObject("NOTICE");
            clickNoticeItem.title = notice.optString("TITLE");
            clickNoticeItem.subTitle = notice.optString("SUBTITLE");
            clickNoticeItem.dateTime = notice.optString("DATETIME");
            clickNoticeItem.publishName = notice.optString("PUBLISHNAME");
            JSONArray sectionsArray = notice.optJSONArray("SECTIONS");
            ArrayList<String> sectionList = new ArrayList<>();
            for (int i = 0; i < sectionsArray.length(); i++) {
                sectionList.add(sectionsArray.getString(i));
            }
            clickNoticeItem.sections = sectionList;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return clickNoticeItem;
    }


    private void initViews() {
        ivBack = findViewById(R.id.iv_back);

        tvTitle = findViewById(R.id.tv_title);
        tvSubTitle = findViewById(R.id.tv_sub_title);
        tvPublishName = findViewById(R.id.tv_publish_name);
        tvPublishTime = findViewById(R.id.tv_publish_time);
        llTextContent = findViewById(R.id.ll_textContent);

        ivBack.setOnClickListener(onClickListener);
    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override public void onClick(View v) {
            if (v == ivBack) {
                finish();
            }
        }
    };
}
