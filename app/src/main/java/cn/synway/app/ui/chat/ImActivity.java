package cn.synway.app.ui.chat;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.TimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import cn.synway.app.R;
import cn.synway.app.bean.event.RefreshMsgEvent;
import cn.synway.app.db.dbmanager.MessageIml;
import cn.synway.app.db.dbmanager.UserIml;
import cn.synway.app.db.table.MessageEntry;
import cn.synway.app.db.table.UserEntry;
import cn.synway.app.mvp.MVPBaseActivity;
import cn.synway.app.push.ChatMsgManager;
import cn.synway.app.utils.SoftKeyBoardListener;
import cn.synway.app.widget.im.EmotionInputDetector;
import cn.synway.app.widget.im.NoScrollViewPager;
import cn.synway.app.widget.im.StateButton;
import cn.synway.app.widget.imadapter.ChatAdapter;
import cn.synway.app.widget.imadapter.CommonFragmentPagerAdapter;
import cn.synway.app.widget.imutil.Constants;
import cn.synway.app.widget.imutil.GlobalOnItemClickManagerUtils;


/**
 * MVPPlugin
 * 聊天页面
 */

public class ImActivity extends MVPBaseActivity<ImContract.View, ImPresenter> implements ImContract.View {

    @BindView(R.id.chat_list)
    RecyclerView chatList;
    @BindView(R.id.emotion_voice)
    ImageView emotionVoice;
    @BindView(R.id.edit_text)
    EditText editText;
    @BindView(R.id.voice_text)
    TextView voiceText;
    @BindView(R.id.emotion_button)
    ImageView emotionButton;
    @BindView(R.id.emotion_add)
    ImageView emotionAdd;
    @BindView(R.id.emotion_send)
    StateButton emotionSend;
    @BindView(R.id.viewpager)
    NoScrollViewPager viewpager;
    @BindView(R.id.emotion_layout)
    RelativeLayout emotionLayout;


    private EmotionInputDetector mDetector;
    private ChatAdapter chatAdapter;
    //聊天记录
    private List<MessageEntry> messageEntries;
    // 会话的用户
    private UserEntry userBO;

    @Override
    protected int getLayout() {
        return R.layout.act_im;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
        userBO = (UserEntry) getIntent().getExtras().getSerializable("user");
        immersionBar.statusBarDarkFont(true).keyboardEnable(true).statusBarColor(R.color.title_bg).init();   //解决虚拟按键与状态栏沉浸冲突
        setTitleText(userBO.getUserName());
        initWidget();
    }


    /**
     * 初始化布局
     */
    private void initWidget() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        ChatEmotionFragment chatEmotionFragment = new ChatEmotionFragment();
        fragments.add(chatEmotionFragment);
        ChatFunctionFragment chatFunctionFragment = new ChatFunctionFragment();
        fragments.add(chatFunctionFragment);
        CommonFragmentPagerAdapter adapter = new CommonFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(0);

        mDetector = EmotionInputDetector.with(this)
                .setEmotionView(emotionLayout)
                .setViewPager(viewpager)
                .bindToContent(chatList)
                .bindToEditText(editText)
                .bindToEmotionButton(emotionButton)
                .bindToAddButton(emotionAdd)
                .bindToSendButton(emotionSend)
                .bindToVoiceButton(emotionVoice)
                .bindToVoiceText(voiceText)
                .build();
        mDetector.setOnLayoutVisableListener(() -> chatList.scrollToPosition(chatAdapter.getItemCount() - 1));

        GlobalOnItemClickManagerUtils globalOnItemClickListener = GlobalOnItemClickManagerUtils.getInstance(this);
        globalOnItemClickListener.attachToEditText(editText);

        chatAdapter = new ChatAdapter(messageEntries);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        chatList.setLayoutManager(layoutManager);
        chatList.setAdapter(chatAdapter);
        chatList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:    //滚动通知
                        chatAdapter.handler.removeCallbacksAndMessages(null);
                        chatAdapter.notifyDataSetChanged();
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:   //开始拖拽
//                        chatAdapter.handler.removeCallbacksAndMessages(null);
//                        mDetector.hideEmotionLayout(false);
//                        mDetector.hideSoftInput();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        setChatListener();
        LoadData();
        chatAdapter.addItemClickListener(new ChatItemListener(userBO));
    }


    /**
     * 初始化布局，设置聊天视图的监听
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setChatListener() {
        chatList.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    chatAdapter.handler.removeCallbacksAndMessages(null);
                    mDetector.hideEmotionLayout(false);
                    mDetector.hideSoftInput();
                    break;
            }
            return false;
        });
        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                chatList.scrollToPosition(chatAdapter.getItemCount() - 1);
            }

            @Override
            public void keyBoardHide(int height) {

            }
        });
    }


    @OnClick(R.id.back)
    public void back() {
        finish();
    }


    /**
     * 加载聊天数据
     */
    private void LoadData() {
        messageEntries = MessageIml.getDataByRecentId(userBO.getUserID());
        chatAdapter.setData(messageEntries);
        chatList.scrollToPosition(chatAdapter.getItemCount() - 1);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEventBus(final MessageEntry messageEntry) {
        messageEntry.setHeader(UserIml.getUser().getUserPic());  //发送人头像
        messageEntry.setSendUserId(UserIml.getUser().getUserID());  // 发送人id
        messageEntry.setSendUserName(UserIml.getUser().getUserName());   //发送人名字
        messageEntry.setType(Constants.CHAT_ITEM_TYPE_RIGHT);
        messageEntry.setSendState(Constants.CHAT_ITEM_SENDING);
        messageEntry.setRecentId(userBO.getUserID());
        messageEntry.setTime(TimeUtils.getNowString());
//        messageEntry.setFileType(Constants.CHAT_FILE_TYPE_TEXT);
        messageEntry.setMsgId(UUID.randomUUID().toString());

        messageEntries.add(messageEntry);
        chatAdapter.notifyItemInserted(messageEntries.size() - 1);
        chatList.scrollToPosition(chatAdapter.getItemCount() - 1);
        switch (messageEntry.getFileType()) {
            case Constants.CHAT_FILE_TYPE_TEXT:  //文字消息
                ChatMsgManager.getInstance().sendTextMsg(userBO, messageEntry);
                break;
            case Constants.CHAT_FILE_TYPE_VOICE:  //语音消息
                ChatMsgManager.getInstance().sendVoiceMsg(userBO, messageEntry);
                break;
            case Constants.CHAT_FILE_TYPE_IMAGE:  //图片消息
                ChatMsgManager.getInstance().sendImageMsg(userBO, messageEntry);
                break;
            case Constants.CHAT_FILE_TYPE_FILE:  //文件消息
                ChatMsgManager.getInstance().sendFileMsg(userBO, messageEntry);
                break;
            case Constants.CHAT_FILE_TYPE_SNAP:   //阅后即焚
                ChatMsgManager.getInstance().sendSnap(userBO, messageEntry);
                break;
            case Constants.CHAT_FILE_TYPE_LINK:  //分享的链接
//                ChatMsgManager.getInstance().sendShareLink(userBO, messageEntry);
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageRefresh(RefreshMsgEvent event) {
        LoadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().removeStickyEvent(this);
        EventBus.getDefault().unregister(this);
    }
}
