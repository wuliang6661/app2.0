package cn.synway.app.ui.chat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

import cn.synway.app.R;
import cn.synway.app.api.HttpResultSubscriber;
import cn.synway.app.api.HttpServerImpl;
import cn.synway.app.bean.MsgSelectFcBo;
import cn.synway.app.bean.event.FullImageInfo;
import cn.synway.app.bean.event.RefreshMsgEvent;
import cn.synway.app.config.FileConfig;
import cn.synway.app.db.dbmanager.MessageIml;
import cn.synway.app.db.table.MessageEntry;
import cn.synway.app.db.table.UserEntry;
import cn.synway.app.push.ChatMsgManager;
import cn.synway.app.ui.BigImageActivity;
import cn.synway.app.ui.web.SynWebBuilder;
import cn.synway.app.utils.AppManager;
import cn.synway.app.utils.ProgressUtils;
import cn.synway.app.utils.Q5Utils;
import cn.synway.app.utils.UrlUtils;
import cn.synway.app.widget.imadapter.ChatAdapter;
import cn.synway.app.widget.imutil.Constants;
import cn.synway.app.widget.imutil.MediaManager;
import okhttp3.ResponseBody;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/7/2414:16
 * desc   :  聊天界面的item 点击处理
 * version: 1.0
 */
public class ChatItemListener implements ChatAdapter.onItemClickListener {

    private List<MessageEntry> entries;  //聊天记录
    private UserEntry userEntry;   //跟谁聊天，会话目标

    //录音相关
    private int animationRes = 0;
    private int res = 0;
    private ImageView animView;

    ChatItemListener(UserEntry entry) {
        this.userEntry = entry;
    }


    @Override
    public void onHeaderClick(int position) {
//            Toast.makeText(IMActivity.this, "onHeaderClick", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onImageClick(View view, int position) {
        entries = MessageIml.getDataByRecentId(userEntry.getUserID());
        if (position >= entries.size()) {
            return;
        }
        goImage(view, entries.get(position).getFilepath());
        if (entries.get(position).getType() == Constants.CHAT_ITEM_TYPE_LEFT) {   //我接收的
            if (Constants.CHAT_FILE_TYPE_SNAP.equals(entries.get(position).getFileType())) {
                ChatMsgManager.getInstance().sendSnapRead(userEntry, entries.get(position));
            }
        }
    }

    @Override
    public void onVoiceClick(final ImageView imageView, final int position) {
        entries = MessageIml.getDataByRecentId(userEntry.getUserID());
        if (position >= entries.size()) {
            return;
        }
        if (animView != null) {
            animView.setImageResource(res);
            animView = null;
        }
        switch (entries.get(position).getType()) {
            case 0:
                animationRes = R.drawable.voice_left;
                res = R.drawable.icon_voice_left3;
                break;
            case 1:
                animationRes = R.drawable.voice_right;
                res = R.drawable.icon_voice_right3;
                break;
        }
        animView = imageView;
        animView.setImageResource(animationRes);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
        animationDrawable.start();
        MediaManager.playSound(entries.get(position).getFilepath(), mp -> animView.setImageResource(res));
    }

    @Override
    public void onFileClick(View view, int position) {
        entries = MessageIml.getDataByRecentId(userEntry.getUserID());
        if (position >= entries.size()) {
            return;
        }
        MessageEntry messageInfo = entries.get(position);
        if (messageInfo.getSendState() != Constants.CHAT_ITEM_SEND_SUCCESS) {
            return;
        }
        if (messageInfo.getFilepath().endsWith("jpg")
                || messageInfo.getFilepath().endsWith("JPG")
                || messageInfo.getFilepath().endsWith("JPEG")
                || messageInfo.getFilepath().endsWith("PNG")
                || messageInfo.getFilepath().endsWith("png")) {   //如果该文件是图片
            goImage(view, messageInfo.getFilepath());
            return;
        }
        if (UrlUtils.isValidURL(messageInfo.getFilepath())
                || !FileUtils.isFileExists(messageInfo.getFilepath())) {   //网络路径的 或者文件不存在
            File file = new File(FileConfig.getIMFile() + File.separator +
                    messageInfo.getFilepath().substring(messageInfo.getFilepath().lastIndexOf("/") + 1));
            //下载文件
            ProgressUtils.getInstance().showProgress();
            HttpServerImpl.downLoad(messageInfo.getFilepath(), null, file)
                    .subscribe(new HttpResultSubscriber<ResponseBody>() {
                        @Override
                        public void onSuccess(ResponseBody responseBody) {
                            ProgressUtils.getInstance().stopProgress();
                            messageInfo.setFilepath(file.getAbsolutePath());
                            MessageIml.addData(messageInfo);
                            Q5Utils.openOtherFile(messageInfo.getFilepath());
                        }

                        @Override
                        public void onFiled(String message) {
                            ProgressUtils.getInstance().stopProgress();
                            ToastUtils.showShort(message);
                        }
                    });
        } else {
            Q5Utils.openOtherFile(messageInfo.getFilepath());
        }
    }


    @Override
    public void onLinkClick(View view, int position) {
        entries = MessageIml.getDataByRecentId(userEntry.getUserID());
        if (position >= entries.size()) {
            return;
        }
        MessageEntry messageInfo = entries.get(position);
        String[] content = messageInfo.getContent().split("%");
        if (content.length < 4) {
            return;
        }
        SynWebBuilder.builder()
                .setUrl(content[3])
                .setName(content[0])
                .start(AppManager.getAppManager().curremtActivity());
    }

    @Override
    public void onLongClickImage(View view, int position) {

//            ChatContextMenu chatContextMenu = new ChatContextMenu(view.getContext(),entries.get(position));
////            chatContextMenu.setAnimationStyle();
//            chatContextMenu.showOnAnchor(view, RelativePopupWindow.VerticalPosition.ABOVE,
//                    RelativePopupWindow.HorizontalPosition.CENTER);

    }

    @Override
    public void onLongClickText(View view, int position) {
//        ChatContextMenu chatContextMenu = new ChatContextMenu(view.getContext(), entries.get(position));
//        chatContextMenu.showOnAnchor(view, RelativePopupWindow.VerticalPosition.ABOVE,
//                RelativePopupWindow.HorizontalPosition.CENTER);
    }

    @Override
    public void onLongClickItem(View view, int position) {
//            ChatContextMenu chatContextMenu = new ChatContextMenu(view.getContext(),messageEntries.get(position));
//            chatContextMenu.showOnAnchor(view, RelativePopupWindow.VerticalPosition.ABOVE,
//                    RelativePopupWindow.HorizontalPosition.CENTER);
    }

    @Override
    public void onLongClickFile(View view, int position) {
//            ChatContextMenu chatContextMenu = new ChatContextMenu(view.getContext(),messageEntries.get(position));
//            chatContextMenu.showOnAnchor(view, RelativePopupWindow.VerticalPosition.ABOVE,
//                    RelativePopupWindow.HorizontalPosition.CENTER);
    }

    @Override
    public void onLongClickLink(View view, int position) {
//            ChatContextMenu chatContextMenu = new ChatContextMenu(view.getContext(),messageEntries.get(position));
//            chatContextMenu.showOnAnchor(view, RelativePopupWindow.VerticalPosition.ABOVE,
//                    RelativePopupWindow.HorizontalPosition.CENTER);
    }

    @Override
    public void onSendFiledClick(View view, int position) {
        entries = MessageIml.getDataByRecentId(userEntry.getUserID());
        if (position >= entries.size()) {
            return;
        }
        MessageEntry messageEntry = entries.get(position);
        messageEntry.setSendState(Constants.CHAT_ITEM_SENDING);
        EventBus.getDefault().post(new RefreshMsgEvent());
        switch (messageEntry.getFileType()) {
            case Constants.CHAT_FILE_TYPE_TEXT:  //文字消息
                ChatMsgManager.getInstance().sendTextMsg(userEntry, messageEntry);
                break;
            case Constants.CHAT_FILE_TYPE_VOICE:  //语音消息
                ChatMsgManager.getInstance().sendVoiceMsg(userEntry, messageEntry);
                break;
            case Constants.CHAT_FILE_TYPE_IMAGE:  //图片消息
                ChatMsgManager.getInstance().sendImageMsg(userEntry, messageEntry);
                break;
            case Constants.CHAT_FILE_TYPE_FILE:  //文件消息
                ChatMsgManager.getInstance().sendVoiceMsg(userEntry, messageEntry);
                break;
            case Constants.CHAT_FILE_TYPE_SNAP:  //阅后即焚
                ChatMsgManager.getInstance().sendSnap(userEntry, messageEntry);
                break;
            case Constants.CHAT_FILE_TYPE_LINK:   //网页消息
                ChatMsgManager.getInstance().sendLink(userEntry, messageEntry);
                break;
        }
    }


    /**
     * 不同的text 类型点击搜索
     */
    @Override
    public void onTextTypeClick(View view, int position, String data, int type) {
        ProgressUtils.getInstance().showProgress();
        HttpServerImpl.getDataBySelectType(type).subscribe(new HttpResultSubscriber<List<MsgSelectFcBo>>() {
            @Override
            public void onSuccess(List<MsgSelectFcBo> fcBos) {
                ProgressUtils.getInstance().stopProgress();
                if (fcBos != null && !fcBos.isEmpty()) {
                    showLongDialog(fcBos, type, data);
                }
            }

            @Override
            public void onFiled(String message) {
                ProgressUtils.getInstance().stopProgress();
                ToastUtils.showShort(message);
            }
        });
    }


    /**
     * 打开图片预览页面
     */
    private void goImage(View view, String imageUrl) {
        int location[] = new int[2];
        view.getLocationOnScreen(location);
        FullImageInfo fullImageInfo = new FullImageInfo();
        fullImageInfo.setLocationX(location[0]);
        fullImageInfo.setLocationY(location[1]);
        fullImageInfo.setWidth(view.getWidth());
        fullImageInfo.setHeight(view.getHeight());
        fullImageInfo.setImageUrl(imageUrl);
        EventBus.getDefault().postSticky(fullImageInfo);
        AppManager.getAppManager().curremtActivity()
                .startActivity(new Intent(AppManager.getAppManager().curremtActivity(), BigImageActivity.class));
        AppManager.getAppManager().curremtActivity().overridePendingTransition(0, 0);
    }


    /**
     * 根据消息类型弹出搜索弹窗
     */
    private void showLongDialog(List<MsgSelectFcBo> fcBos, int type, String data) {
        Context context = AppManager.getAppManager().curremtActivity();
        Dialog dialog = new Dialog(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_select_app, null);
        RecyclerView recyclerView = dialogView.findViewById(R.id.recycle_view);
        TextView title = dialogView.findViewById(R.id.pop_title);
        title.setText("快捷搜索\n" + data);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
//        DividerItemDecoration itemDecoration = new DividerItemDecoration(Objects.requireNonNull(context), DividerItemDecoration.VERTICAL);
//        itemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.divider_inset)));
//        recyclerView.addItemDecoration(itemDecoration);
        DlalogAdapter adapter = new DlalogAdapter(fcBos);
        adapter.setOnItemClickListener(R.id.item_layout, (view, position) -> {
            dialog.dismiss();
            String url = null;
            switch (type) {
                case 0:     //身份证
                    url = fcBos.get(position).getIdentityUrl();
                    break;
                case 1:     //车牌号
                    url = fcBos.get(position).getLicenseUrl();
                    break;
                case 2:    //手机号
                    url = fcBos.get(position).getPhoneUrl();
                    break;
            }
            SynWebBuilder.builder()
                    .setUrl(url.replace("%value%", data))
                    .setName(fcBos.get(position).getName())
                    .start(context);

        });
        recyclerView.setAdapter(adapter);
        dialog.setContentView(dialogView);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes();  //获取对话框当前的参数值
        p.width = (int) (ScreenUtils.getScreenWidth() * 0.8);    //宽度设置为屏幕的0.5
        dialog.getWindow().setAttributes(p);     //设置生效
        dialog.show();
    }
}
