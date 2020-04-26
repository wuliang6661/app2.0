package synway.module_publicaccount.public_chat.WebviewFn;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import synway.module_interface.chatinterface.obj.ChatMsgObj;
import synway.module_interface.chatinterface.obj.ChatPublicUrlObj;
import synway.module_interface.chatinterface.obj.ChatPublicVoiceObj;
import synway.module_interface.chatinterface.obj.ChatTextObj;
import synway.module_publicaccount.Main;
import synway.module_publicaccount.R;
import synway.module_publicaccount.public_chat.LBSMapAct;
import synway.module_publicaccount.public_chat.PAWebViewAct;
import synway.module_publicaccount.public_chat.WebTitleView;
import synway.module_publicaccount.public_chat.WebviewFn.newVideoVoice.VoicePlayerView;
import synway.module_publicaccount.until.DialogTool;

import static synway.module_publicaccount.public_chat.util.ConfigUtil.RECORD_HTTPURL;
import static synway.module_publicaccount.public_chat.util.ConfigUtil.RECORD_PICURL;
import static synway.module_publicaccount.public_chat.util.ConfigUtil.RECORD_TEXT;
import static synway.module_publicaccount.public_chat.util.ConfigUtil.RECORD_VIDEOURL;
import static synway.module_publicaccount.public_chat.util.ConfigUtil.RECORD_VOICEURL;

/**
 * Created by leo on 2019/2/19.
 */

public class SynOSCFn {

    private Handler handler;
    private PAWebViewAct paWebViewAct;
    private WebTitleView titleView;
    private VoicePlayerView voicePlayerView;
    private Context context;

    public SynOSCFn(PAWebViewAct paWebViewAct, WebTitleView titleView, VoicePlayerView voicePlayerView) {
        this.paWebViewAct = paWebViewAct;
        this.titleView = titleView;
        this.voicePlayerView = voicePlayerView;

        this.handler = new Handler();
        this.context = paWebViewAct;
    }


    /**
     * 获取base64 格式的人脸图片
     */
    @JavascriptInterface
    public String getFaceImg() {
        return paWebViewAct.getFaceImg();
    }

    /***
     * 传递一个长字符串
     */
    @JavascriptInterface
    public String getUrlParam() {
        return paWebViewAct.getUrlParam();
    }

    @JavascriptInterface
    public void finishAct() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (paWebViewAct != null) {
//                    paWebViewAct.activiyCloseCheck();
                    paWebViewAct.finish();
                }
            }
        });
    }


    private String titleName, titleColor, backgroundColor;

    /***
     * 标题背景文本显示
     * 不用返回值
     *
     * @param titleName       标题
     * @param titleColor      标题颜色
     * @param backgroundColor 背景颜色（十六进制颜色例如：#FFFFFF）
     */
    @JavascriptInterface
    public void setTitleAndBgFn(String titleName, String titleColor, String backgroundColor) {
        this.titleName = titleName;
        this.titleColor = titleColor;
        this.backgroundColor = backgroundColor;
        new Thread() {
            public void run() {
                handler.post(runnableUi);
            }
        }.start();
    }

    // 构建Runnable对象，在runnable中更新界面
    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            //更新界面
            if (titleView != null) {
                titleView.setTitle(titleName);
                titleView.setBbackgroundColor(backgroundColor);
            }
        }

    };

    @JavascriptInterface
    public void showMap(String lon, String lat, String info) {
        Intent intent = new Intent();
        intent.putExtra("LON", lon);
        intent.putExtra("LAT", lat);
        intent.putExtra("INFO", info);
        intent.setClass(context, LBSMapAct.class);
        context.startActivity(intent);
    }

    /***
     * 上传情报的接口
     */
    @JavascriptInterface
    public void longClickInterfacnFn(String json) {
        Window window = DialogTool.dialog(context, R.layout.model_public_dialog);
        LinearLayout uploadIn = window.findViewById(R.id.uploadIntellgence);
        uploadIn.setOnClickListener(uploadInClick);
        uploadIn.setTag(json);

    }

    private View.OnClickListener uploadInClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                int type;
                String record;
                String json = (String) v.getTag();
                JSONObject jsonObject = new JSONObject(json);
                type = jsonObject.optInt("type");
                record = jsonObject.optString("record");
                String result[] = record.split("|");
                String description = "";
                String url = "";
                String urltext = "";
                String video = "";
                String picurl = "";
                String voice = "";
                if (result.length > 0) {
                    for (String content : result) {
                        String singlereult[] = content.split("：");
                        switch (singlereult[0]) {
                            case RECORD_TEXT:
                                description = description + singlereult[1] + ":" + singlereult[2];
                                break;
                            case RECORD_HTTPURL:
                                url = singlereult[1];
                                break;
                            case RECORD_VIDEOURL:
                                video = singlereult[1];
                                break;
                            case RECORD_PICURL:
                                picurl = singlereult[1];
                                break;
                            case RECORD_VOICEURL:
                                voice = singlereult[1];
                                break;
                        }
                    }
                }
                switch (type) {

                    case 1://语音，音频文件
                        ArrayList<ChatMsgObj> chatMsgList = new ArrayList<>();
                        ChatPublicVoiceObj chatPublicVoiceObj = new ChatPublicVoiceObj();
                        chatPublicVoiceObj.url = voice;
                        chatPublicVoiceObj.description = description;
                        chatPublicVoiceObj.context = description;
                        chatMsgList.add(chatPublicVoiceObj);
                        Main.instance().handlerIntelligence.goIntelligenceAct(paWebViewAct, "", 2, chatMsgList);
                        break;
                    case 2://短信，文字信息
                        ArrayList<ChatMsgObj> chatMsgList2 = new ArrayList<>();
                        ChatTextObj chatTextObj = new ChatTextObj();
                        chatTextObj.description = description;
                        chatTextObj.content = description;
                        chatMsgList2.add(chatTextObj);
                        Main.instance().handlerIntelligence.goIntelligenceAct(paWebViewAct, "", 2, chatMsgList2);
                        break;
                    case 3://公安网,有url
                        ArrayList<ChatMsgObj> chatMsgList3 = new ArrayList<>();
                        ChatPublicUrlObj chatPublicUrlObj = new ChatPublicUrlObj();
                        chatPublicUrlObj.url = url;
                        chatPublicUrlObj.description = description;
                        chatPublicUrlObj.content = description;
                        chatMsgList3.add(chatPublicUrlObj);
                        Main.instance().handlerIntelligence.goIntelligenceAct(paWebViewAct, "", 2, chatMsgList3);
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    /***
     * 音频播放
     * 不用返回值
     *
     * @param mediaurl 音频地址
     */
    private String mediaurl = "";

    @JavascriptInterface
    public void playFn(String mediaurl) {
        this.mediaurl = mediaurl;
        new Thread() {
            @Override
            public void run() {
                handler.post(runnableVoice);
            }
        }.start();
    }

    // 构建Runnable对象，在runnable中更新界面
    Runnable runnableVoice = new Runnable() {
        @Override
        public void run() {
            //显示播放界面
            if (voicePlayerView != null) {
                voicePlayerView.setmediaurl(mediaurl);
                voicePlayerView.start();
                voicePlayerView.setVisibility(View.VISIBLE);
            }
        }

    };

    /***
     * 音频关闭
     * 不需要返回值
     */
    @JavascriptInterface
    public void closeMediaFn() {
        new Thread() {
            @Override
            public void run() {
                handler.post(runnableVoiceClose);
            }
        }.start();

    }

    // 构建Runnable对象，在runnable中更新界面
    Runnable runnableVoiceClose = new Runnable() {
        @Override
        public void run() {
            //显示播放界面
            if (voicePlayerView != null) {
                voicePlayerView.destory();
                voicePlayerView.setVisibility(View.GONE);
            }
        }

    };
}
