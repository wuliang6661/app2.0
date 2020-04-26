package synway.module_publicaccount.public_chat;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.TextView;

import synway.module_publicaccount.R;
import synway.module_publicaccount.public_chat.WebviewFn.newVideoVoice.VoicePlayerView;
import synway.module_publicaccount.public_chat.adapter.TextParamDeal;

public class WebTitleView {

    private View view = null, closeview = null;
    private TextView tvTitle = null;
    private View btnback = null, close = null;
    private Activity activity = null;
    private View freshview = null;
    private WebView webView = null;
    private VoicePlayerView voicePlayerView = null;
    private WebTitleInterface webTitleInterface;


    public WebTitleView(Activity act, WebView webView, VoicePlayerView voicePlayerView, WebTitleInterface webTitleInterface) {
        this.webTitleInterface = webTitleInterface;
        this.activity = act;
        this.webView = webView;
        this.voicePlayerView = voicePlayerView;
        view = act.findViewById(R.id.titlebar_block);
        btnback = view.findViewById(R.id.back);

        tvTitle = view.findViewById(R.id.lblTitle);

        freshview = view.findViewById(R.id.fresh);
        closeview = view.findViewById(R.id.closeview);
        closeview.setVisibility(View.VISIBLE);
        freshview.setVisibility(View.VISIBLE);
        freshview.setOnClickListener(onClickListener);
        btnback.setOnClickListener(onClickListener);
        closeview.setOnClickListener(onClickListener);
    }

    //设置标题文字和文字颜色
    public void setTitle(String title) {
        this.tvTitle.setText(title);
    }

    public void setVisible(int isable) {
        if (view != null) {
            view.setVisibility(isable);
        }
    }

    //設置標題背景
    public void setBbackgroundColor(String BbackgroundColor) {
        this.view.setBackgroundColor(TextParamDeal.getColor(BbackgroundColor));
    }

    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == btnback) {
                if (webView.canGoBack()) {
                    webView.goBack();// 返回上一页面
                    voicePlayerView.destory();
                    voicePlayerView.setVisibility(View.GONE);
                }
                else {
                    if (webTitleInterface == null) {
                        Intent intent = new Intent();
                        String backtype = "backkey";
                        intent.putExtra("backtype", backtype);
                        activity.setResult(3, intent);
                        activity.finish();
                    }
                    else {
                        webTitleInterface.closeActivity();
                    }
                }
            }
            else if (v == freshview) {
                webView.reload();
            }
            else if (v == closeview) {
                if (webTitleInterface == null) {
                    Intent intent = new Intent();
                    String backtype = "backkey";
                    intent.putExtra("backtype", backtype);
                    activity.setResult(3, intent);
                    activity.finish();
                }
                else {
                    webTitleInterface.closeActivity();
                }
            }
        }
    };

    public interface WebTitleInterface {
        void closeActivity();
    }
}