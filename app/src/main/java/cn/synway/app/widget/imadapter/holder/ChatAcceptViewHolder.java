package cn.synway.app.widget.imadapter.holder;

import android.content.Context;
import android.os.Handler;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import cn.synway.app.R;
import cn.synway.app.db.table.MessageEntry;
import cn.synway.app.utils.UrlUtils;
import cn.synway.app.widget.im.BubbleImageView;
import cn.synway.app.widget.im.BubbleLinearLayout;
import cn.synway.app.widget.im.GifTextView;
import cn.synway.app.widget.imadapter.ChatAdapter;
import cn.synway.app.widget.imutil.Constants;
import cn.synway.app.widget.imutil.FileUtils;
import cn.synway.app.widget.imutil.Utils;

/**
 * 作者：Rance on 2016/11/29 10:47
 * 邮箱：rance935@163.com
 */
public class ChatAcceptViewHolder extends BaseViewHolder<MessageEntry> {

    private static final String TAG = "ChatAcceptViewHolder";
    private TextView chatItemDate;
    private ImageView chatItemHeader;
    private GifTextView chatItemContentText;
    private BubbleImageView chatItemContentImage;
    private ImageView chatItemVoice;
    private BubbleLinearLayout chatItemLayoutContent;
    private TextView chatItemVoiceTime;
    private BubbleLinearLayout chatItemLayoutFile;
    private ImageView ivFileType;
    private TextView tvFileName;
    private BubbleLinearLayout chatItemLayoutContact;
    private BubbleLinearLayout chatItemLayoutLink;
    private BubbleLinearLayout chatItemLayoutSnap;
    private TextView linkTitle;
    private TextView linkText;
    private ImageView linkImage;

    private ChatAdapter.onItemClickListener onItemClickListener;
    private Handler handler;
    private RelativeLayout.LayoutParams layoutParams;
    private Context mContext;

    public ChatAcceptViewHolder(ViewGroup parent, ChatAdapter.onItemClickListener onItemClickListener, Handler handler) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_accept, parent, false));
        findViewByIds(itemView);
        setItemLongClick();
        setItemClick();
        this.mContext = parent.getContext();
        this.onItemClickListener = onItemClickListener;
        this.handler = handler;
        layoutParams = (RelativeLayout.LayoutParams) chatItemLayoutContent.getLayoutParams();
    }

    private void findViewByIds(View itemView) {
        chatItemDate = itemView.findViewById(R.id.chat_item_date);
        chatItemHeader = itemView.findViewById(R.id.chat_item_header);
        chatItemContentText = itemView.findViewById(R.id.chat_item_content_text);
        chatItemContentImage = itemView.findViewById(R.id.chat_item_content_image);
        chatItemVoice = itemView.findViewById(R.id.chat_item_voice);
        chatItemLayoutContent = itemView.findViewById(R.id.chat_item_layout_content);
        chatItemVoiceTime = itemView.findViewById(R.id.chat_item_voice_time);
        chatItemLayoutFile = itemView.findViewById(R.id.chat_item_layout_file);
        ivFileType = itemView.findViewById(R.id.iv_file_type);
        tvFileName = itemView.findViewById(R.id.tv_file_name);
        chatItemLayoutContact = itemView.findViewById(R.id.chat_item_layout_contact);
        chatItemLayoutLink = itemView.findViewById(R.id.chat_item_layout_link);
        chatItemLayoutSnap = itemView.findViewById(R.id.chat_item_layout_snap);
        linkTitle = itemView.findViewById(R.id.tv_link_title);
        linkText = itemView.findViewById(R.id.tv_link_text);
        linkImage = itemView.findViewById(R.id.iv_link_picture);
    }

    @Override
    public void setData(MessageEntry data) {
        chatItemDate.setText(data.getTime() != null ? data.getTime() : "");
        Glide.with(mContext).load(data.getHeader()).error(R.drawable.police_picture)
                .placeholder(R.drawable.police_picture).into(chatItemHeader);
        chatItemHeader.setOnClickListener(v -> onItemClickListener.onHeaderClick((Integer) itemView.getTag()));

        chatItemVoice.setVisibility(View.GONE);
        chatItemLayoutContent.setVisibility(View.GONE);
        chatItemVoiceTime.setVisibility(View.GONE);
        chatItemContentText.setVisibility(View.GONE);
        chatItemContentImage.setVisibility(View.GONE);
        chatItemLayoutContact.setVisibility(View.GONE);
        chatItemLayoutFile.setVisibility(View.GONE);
        chatItemLayoutSnap.setVisibility(View.GONE);
        chatItemLayoutLink.setVisibility(View.GONE);

        switch (data.getFileType()) {
            case Constants.CHAT_FILE_TYPE_TEXT:
                chatItemContentText.setSpanText(handler, data.getContent(), true);
                chatItemVoice.setVisibility(View.GONE);
                chatItemContentText.setVisibility(View.VISIBLE);
                chatItemLayoutContent.setVisibility(View.VISIBLE);
                chatItemVoiceTime.setVisibility(View.GONE);
                chatItemContentImage.setVisibility(View.GONE);
                chatItemLayoutContact.setVisibility(View.GONE);

                TextPaint paint = chatItemContentText.getPaint();
                // 计算textview在屏幕上占多宽
                int len = (int) paint.measureText(chatItemContentText.getText().toString().trim());
                if (len < Utils.dp2px(mContext, 200)) {
                    layoutParams.width = len + Utils.dp2px(mContext, 30);
                } else {
                    layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
                }
                layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                chatItemLayoutContent.setLayoutParams(layoutParams);
                break;
            case Constants.CHAT_FILE_TYPE_IMAGE:    //图片
                chatItemVoice.setVisibility(View.GONE);
                chatItemLayoutContent.setVisibility(View.GONE);
                chatItemVoiceTime.setVisibility(View.GONE);
                chatItemContentText.setVisibility(View.GONE);
                chatItemContentImage.setVisibility(View.VISIBLE);
                chatItemLayoutContact.setVisibility(View.GONE);

                Glide.with(mContext).load(data.getFilepath()).error(R.drawable.loding_img)
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .centerCrop()
                        .placeholder(R.drawable.loding_img).into(chatItemContentImage);
                chatItemContentImage.setOnClickListener(v -> onItemClickListener.onImageClick(chatItemContentImage, (Integer) itemView.getTag()));
                layoutParams.width = Utils.dp2px(mContext, 120);
                layoutParams.height = Utils.dp2px(mContext, 35);
                chatItemLayoutContent.setLayoutParams(layoutParams);
                break;
            case Constants.CHAT_FILE_TYPE_SNAP:   //阅后即焚
                LogUtils.i("阅后即焚！");
                chatItemVoice.setVisibility(View.GONE);
                chatItemLayoutContent.setVisibility(View.GONE);
                chatItemVoiceTime.setVisibility(View.GONE);
                chatItemContentText.setVisibility(View.GONE);
                chatItemContentImage.setVisibility(View.GONE);
                chatItemLayoutContact.setVisibility(View.GONE);
                chatItemLayoutSnap.setVisibility(View.VISIBLE);

                break;
            case Constants.CHAT_FILE_TYPE_VOICE:
                chatItemVoice.setVisibility(View.VISIBLE);
                chatItemLayoutContent.setVisibility(View.VISIBLE);
                chatItemContentText.setVisibility(View.GONE);
                chatItemVoiceTime.setVisibility(View.VISIBLE);
                chatItemContentImage.setVisibility(View.GONE);
                chatItemLayoutContact.setVisibility(View.GONE);

                chatItemVoiceTime.setText(Utils.formatTime(data.getVoiceTime()));
                chatItemLayoutContent.setOnClickListener(v -> onItemClickListener.onVoiceClick(chatItemVoice, (Integer) itemView.getTag()));
                layoutParams.width = Utils.dp2px(mContext, 120);
                layoutParams.height = Utils.dp2px(mContext, 35);
                chatItemLayoutContent.setLayoutParams(layoutParams);
                break;
            case Constants.CHAT_FILE_TYPE_FILE:
                chatItemVoice.setVisibility(View.GONE);
                chatItemContentText.setVisibility(View.GONE);
                chatItemContentImage.setVisibility(View.GONE);
                chatItemVoiceTime.setVisibility(View.GONE);
                chatItemLayoutContent.setVisibility(View.GONE);
                chatItemLayoutContact.setVisibility(View.GONE);

//                chatItemLayoutContent.setVisibility(View.VISIBLE);
                chatItemLayoutFile.setVisibility(View.VISIBLE);
                if (UrlUtils.isValidURL(data.getFilepath())) {
                    tvFileName.setText(data.getFilepath().substring(data.getFilepath().lastIndexOf("/") + 1));
                } else {
                    tvFileName.setText(FileUtils.getFileName(data.getFilepath()));
                }
                try {
//                    tvFileSize.setText(FileUtils.getFileSize(data.getFilepath()));
                    switch (data.getFilepath().substring(data.getFilepath().lastIndexOf(".") + 1)) {
                        case "doc":
                        case "docx":
                            ivFileType.setImageResource(R.drawable.icon_file_word);
                            break;
                        case "ppt":
                        case "pptx":
                            ivFileType.setImageResource(R.drawable.icon_file_ppt);
                            break;
                        case "xls":
                        case "xlsx":
                            ivFileType.setImageResource(R.drawable.icon_file_excel);
                            break;
                        case "pdf":
                            ivFileType.setImageResource(R.drawable.icon_file_pdf);
                            break;
                        default:
                            ivFileType.setImageResource(R.drawable.icon_file_other);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case Constants.CHAT_FILE_TYPE_LINK:   //分享链接
                chatItemVoice.setVisibility(View.GONE);
                chatItemContentText.setVisibility(View.GONE);
                chatItemContentImage.setVisibility(View.GONE);
                chatItemVoiceTime.setVisibility(View.GONE);
                chatItemLayoutContent.setVisibility(View.GONE);
                chatItemLayoutFile.setVisibility(View.GONE);
                chatItemLayoutContact.setVisibility(View.GONE);
                chatItemLayoutLink.setVisibility(View.VISIBLE);

                String[] content = data.getContent().split("%");
                linkTitle.setText(content[0]);
                linkText.setText(content[1]);
                Glide.with(mContext).load(content[2]).error(R.drawable.share_img)
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .centerCrop()
                        .placeholder(R.drawable.share_img).into(linkImage);
                break;
//            case Constants.CHAT_FILE_TYPE_CONTACT:
//                chatItemVoice.setVisibility(View.GONE);
//                chatItemContentText.setVisibility(View.GONE);
//                chatItemContentImage.setVisibility(View.GONE);
//                chatItemVoiceTime.setVisibility(View.GONE);
//                chatItemLayoutContent.setVisibility(View.GONE);
//                chatItemLayoutFile.setVisibility(View.GONE);
//
//                chatItemLayoutContact.setVisibility(View.VISIBLE);
//
//                IMContact imContact = (IMContact) data.getObject();
//                tvContactSurname.setText(imContact.getSurname());
//                tvContactName.setText(imContact.getName());
//                tvContactPhone.setText(imContact.getPhonenumber());
//                break;
//            case Constants.CHAT_FILE_TYPE_LINK:
//                chatItemVoice.setVisibility(View.GONE);
//                chatItemContentText.setVisibility(View.GONE);
//                chatItemContentImage.setVisibility(View.GONE);
//                chatItemVoiceTime.setVisibility(View.GONE);
//                chatItemLayoutContent.setVisibility(View.GONE);
//                chatItemLayoutFile.setVisibility(View.GONE);
//                chatItemLayoutContact.setVisibility(View.GONE);
//
//                chatItemLayoutLink.setVisibility(View.VISIBLE);
//                Link link = (Link) data.getObject();
//
//                tvLinkSubject.setText(link.getSubject());
////                tvLinkText.setText(link.getText());
//                Glide.with(mContext).load(link.getStream()).into(ivLinkPicture);
//                break;
        }
    }

    public void setItemLongClick() {
        chatItemContentImage.setOnLongClickListener(v -> {
            onItemClickListener.onLongClickImage(v, (Integer) itemView.getTag());
            return true;
        });
        chatItemContentText.setOnLongClickListener(v -> {
            onItemClickListener.onLongClickText(v, (Integer) itemView.getTag());
            return true;
        });
        chatItemLayoutContent.setOnLongClickListener(v -> {
            onItemClickListener.onLongClickItem(v, (Integer) itemView.getTag());
            return true;
        });
        chatItemLayoutFile.setOnLongClickListener(v -> {
            onItemClickListener.onLongClickFile(v, (Integer) itemView.getTag());
            return true;
        });
    }

    public void setItemClick() {
        chatItemContentImage.setOnClickListener(v -> onItemClickListener.onImageClick(chatItemContentImage, (Integer) itemView.getTag()));
        chatItemLayoutSnap.setOnClickListener(view -> onItemClickListener.onImageClick(chatItemContentImage, (Integer) itemView.getTag()));
        chatItemLayoutContent.setOnClickListener(v -> onItemClickListener.onVoiceClick(chatItemVoice, (Integer) itemView.getTag()));
        chatItemLayoutFile.setOnClickListener(v -> onItemClickListener.onFileClick(v, (Integer) itemView.getTag()));
        chatItemLayoutLink.setOnClickListener(v -> onItemClickListener.onLinkClick(v, (Integer) itemView.getTag()));
    }
}
