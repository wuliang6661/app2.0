package cn.synway.app.widget.imadapter;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.synway.app.db.table.MessageEntry;
import cn.synway.app.widget.imadapter.holder.BaseViewHolder;
import cn.synway.app.widget.imadapter.holder.ChatAcceptViewHolder;
import cn.synway.app.widget.imadapter.holder.ChatSendViewHolder;
import cn.synway.app.widget.imutil.Constants;

/**
 * 作者：Rance on 2016/11/29 10:46
 * 邮箱：rance935@163.com
 */
public class ChatAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private onItemClickListener onItemClickListener;
    public Handler handler;
    private List<MessageEntry> messageEntryList;

    public ChatAdapter(List<MessageEntry> messageEntryList) {
        handler = new Handler();
        this.messageEntryList = messageEntryList;
    }

    public void addItemClickListener(onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder viewHolder = null;
        switch (viewType) {
            case Constants.CHAT_ITEM_TYPE_LEFT:
                viewHolder = new ChatAcceptViewHolder(parent, onItemClickListener, handler);
                break;
            case Constants.CHAT_ITEM_TYPE_RIGHT:
                viewHolder = new ChatSendViewHolder(parent, onItemClickListener, handler);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.setData(messageEntryList.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return messageEntryList.get(position).getType();
    }

    @Override
    public int getItemCount() {
        if (messageEntryList == null) {
            return 0;
        } else {
            return messageEntryList.size();
        }
    }

    public void addAll(List<MessageEntry> messageEntries) {
        if (messageEntryList == null) {
            messageEntryList = messageEntries;
        } else {
            messageEntryList.addAll(messageEntries);
        }

        notifyDataSetChanged();
    }

    public void add(MessageEntry messageEntry) {
        if (messageEntryList == null) {
            messageEntryList = new ArrayList<>();
        }

        messageEntryList.add(messageEntry);

        notifyDataSetChanged();
    }


    public void setData(List<MessageEntry> messageEntries) {
        messageEntryList = messageEntries;
        notifyDataSetChanged();
    }


    public interface onItemClickListener {
        void onHeaderClick(int position);

        void onImageClick(View view, int position);

        void onVoiceClick(ImageView imageView, int position);

        void onFileClick(View view, int position);

        void onLinkClick(View view, int position);

        void onLongClickImage(View view, int position);

        void onLongClickText(View view, int position);

        void onLongClickItem(View view, int position);

        void onLongClickFile(View view, int position);

        void onLongClickLink(View view, int position);

        void onSendFiledClick(View view, int position);

        void onTextTypeClick(View view, int position, String data, int type);

    }
}
