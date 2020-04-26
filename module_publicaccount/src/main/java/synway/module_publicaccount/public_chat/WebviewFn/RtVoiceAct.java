package synway.module_publicaccount.public_chat.WebviewFn;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import qyc.library.control.dialog_msg.DialogMsg;
import qyc.library.control.dialog_progress.DialogProgress;
import synway.module_publicaccount.R;
import synway.module_publicaccount.public_chat.RTMPEvent;
import synway.module_publicaccount.public_chat.SyncStartRTMP;
import synway.module_publicaccount.rtvideovoice.rtvoice.RTVoiceView;
import synway.module_publicaccount.until.StringUtil;

/**
 * Created by ysm on 2016/12/12.
 */
public class RtVoiceAct extends Activity{
    private View view = null;
    private TextView tvTitle = null;
    private ImageButton btnback = null;
    private RTVoiceView rtVoiceView = null;
    private Dialog dialogWait = null;
    private SyncStartRTMP syncStartRTMP = null;
    private  String mediaurl;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaurl= getIntent().getStringExtra("mediaurl");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.model_public_rtvice);
        init();
//        PublicAccountURL publicAccountURL = PublicAccountURLFactory.getPublicVoicURL(mediaurl);
//        PublicAccountURL.URL_MEDIA url_media = (PublicAccountURL.URL_MEDIA) publicAccountURL;
        RTMPEvent event = new RTMPEvent();
        event.postURL = mediaurl;
        event.rtmpURL =mediaurl.replace("http", "rtmp");
        event.urlName = "";
//        prepareRTMP(event);
//        EventBus.getDefault().register(this);
//        rtVoiceView.play();
        rtVoiceView.resetRTMPurl(mediaurl);
        Log.e("testy", "准备播放RTMP:" + mediaurl);
        rtVoiceView.stop();
        rtVoiceView.play();
    }

    public void init() {
        view = findViewById(R.id.titlebar_block);
        tvTitle = view.findViewById(R.id.lblTitle);
        btnback = view.findViewById(R.id.back);
//		titlelayout=(LinearLayout)view.findViewById(R.id.titlelayout);
//		View v = getlayoutInflater().inflate(((LinearLayout)findViewById(R.id.headerInclude)), null);
//		oneline=(ImageView)view.findViewById(R.id.hhhhh);
        btnback.setOnClickListener(onClickListener);
        rtVoiceView = findViewById(R.id.publicrtVoiceView);
        rtVoiceView.init();
        rtVoiceView.resetRTMPurl(mediaurl);
        rtVoiceView.setOnPlayListen(onPlayListen);
    }

    private RTVoiceView.OnPlayListen onPlayListen = new RTVoiceView.OnPlayListen() {
        @Override
        public void onPlayCompletion() {
            Log.e("testy", "RTMP PLAY COMPLETE");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Animation animation = AnimationUtils.loadAnimation(RtVoiceAct.this, R.anim.public_account_rtvoiceview_slide_out);
                    animation.setFillAfter(true);
                    rtVoiceView.startAnimation(animation);

                }
            });
        }

        @Override
        public void onError() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("testy", "RTMP PLAY ERROR");
                    Animation animation = AnimationUtils.loadAnimation(RtVoiceAct.this, R.anim.public_account_rtvoiceview_slide_out);
                    animation.setFillAfter(true);
                    rtVoiceView.startAnimation(animation);

                }
            });
        }
    };
    /**
     * 激活实时语音RTMP结果监听
     */
    private SyncStartRTMP.OnStartRTMPResult onStartRTMPResult = new SyncStartRTMP.OnStartRTMPResult() {


        @Override
        public void onSuccess(String rtmpURL, String urlName) {
            // 激活RTMP成功，准备播放直播流
            if (dialogWait != null) {
                dialogWait.dismiss();
                dialogWait = null;
            }

            if (StringUtil.isEmpty(rtmpURL)) {
                return;
            }
            Animation animation = AnimationUtils.loadAnimation(RtVoiceAct.this, R.anim.public_account_rtvoiceview_slide_in);
            animation.setFillAfter(true);
            rtVoiceView.startAnimation(animation);
            rtVoiceView.setText(urlName);
            rtVoiceView.resetRTMPurl(rtmpURL);
            Log.e("testy", "准备播放RTMP:" + rtmpURL);
            rtVoiceView.stop();
            rtVoiceView.play();
        }

        @Override
        public void onFail(String title, String reason, String detail) {
            if (dialogWait != null) {
                dialogWait.dismiss();
                dialogWait = null;
            }
            Log.i("testy","播放音频失败了");
            DialogMsg.showDetail(RtVoiceAct.this, title, reason, detail);
        }
    };

    public void prepareRTMP(RTMPEvent event) {
        if (syncStartRTMP != null) {
            syncStartRTMP.stop();
        }
        dialogWait = DialogProgress.get(this, "等待", "正在获取语音地址");
        dialogWait.show();
        syncStartRTMP = new SyncStartRTMP();
        syncStartRTMP.setOnStartRTMPResult(onStartRTMPResult);
        syncStartRTMP.start(event.postURL, event.rtmpURL, event.urlName);
    }

    @Override
    protected void onDestroy() {
        if (rtVoiceView != null) {
            rtVoiceView.destroy();
        }
//        EventBus.getDefault().unregister(this);
        super.onDestroy();

    }
    private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if(v == btnback){
                finish();
            }
        }
    };
}
