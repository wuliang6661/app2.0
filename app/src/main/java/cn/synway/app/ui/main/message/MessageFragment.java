package cn.synway.app.ui.main.message;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.TimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.synway.app.R;
import cn.synway.app.bean.MessageFcBo;
import cn.synway.app.bean.event.KeepEvent;
import cn.synway.app.bean.event.MainMenuEvent;
import cn.synway.app.bean.event.MessageRefreshEvent;
import cn.synway.app.bean.event.RefreshMsgEvent;
import cn.synway.app.bean.event.UpdateEvent;
import cn.synway.app.db.dbmanager.RecentContactIml;
import cn.synway.app.db.table.RecentContactsEntry;
import cn.synway.app.db.table.UserEntry;
import cn.synway.app.mvp.MVPBaseFragment;
import cn.synway.app.ui.chat.ImActivity;
import cn.synway.app.ui.messagedetails.MessageDetailsActivity;
import cn.synway.app.ui.serachmessage.SerachMessageActivity;
import cn.synway.app.widget.imutil.Constants;
import cn.synway.app.widget.lgrecycleadapter.LGRecycleViewAdapter;
import cn.synway.app.widget.lgrecycleadapter.LGViewHolder;

/**
 * MVPPlugin
 * 消息页
 */

public class MessageFragment extends MVPBaseFragment<MessageContract.View, MessagePresenter>
        implements MessageContract.View {


    @BindView(R.id.menu)
    ImageView menu;
    @BindView(R.id.message_recycle)
    RecyclerView messageRecycle;
    Unbinder unbinder;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.search)
    ImageView search;
    @BindView(R.id.today_point)
    TextView todayPoint;
    @BindView(R.id.keep_hint)
    TextView keepHint;


    List<MessageFcBo> messageFcBos;   //服务器返回的应用消息列表

    List<MessageFcBo> allMessage;    //即使通信和应用的总列表

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fra_message, null);
        EventBus.getDefault().register(this);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        messageFcBos = new ArrayList<>();
        allMessage = new ArrayList<>();
        titleText.setText("消息");
        initView();
    }


    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        mPresenter.getFcList();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        messageRecycle.setLayoutManager(manager);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.divider_inset));
        messageRecycle.addItemDecoration(itemDecoration);

    }

    @OnClick({R.id.menu, R.id.search})
    public void clickImage(View view) {
        switch (view.getId()) {
            case R.id.menu:
                EventBus.getDefault().post(new MainMenuEvent());
                break;
            case R.id.search:
                gotoActivity(SerachMessageActivity.class, false);
                break;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().removeStickyEvent(this);
        EventBus.getDefault().unregister(this);
    }


    /**
     * 刷新消息数
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageRefreshEvent event) {
        mPresenter.getFcList();
    }

    /**
     * 是否有新版本更新
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateEvent(UpdateEvent event) {
        todayPoint.setVisibility(View.VISIBLE);
    }

    /**
     * 长连接是否断开
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void connectEvent(KeepEvent event) {
        if (event.isContect) {
            keepHint.setVisibility(View.GONE);
        } else {
            keepHint.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 是否有即时通信新消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshMsg(RefreshMsgEvent event) {
        bindRecent();
    }

    @Override
    public void onRequestError(String msg) {
        showToast(msg);
        bindRecent();
    }

    @Override
    public void onRequestEnd() {

    }

    /**
     * 请求返回消息列表
     */
    @Override
    public void getFcMenuList(List<MessageFcBo> messageFcBos) {
        this.messageFcBos = messageFcBos;
        bindRecent();
    }


    /**
     * 将会话列表合并进显示列表
     */
    private void bindRecent() {
        allMessage.clear();
        allMessage.addAll(messageFcBos);
        List<RecentContactsEntry> entries = RecentContactIml.getAllData();
        for (RecentContactsEntry item : entries) {
            MessageFcBo messageFcBo = new MessageFcBo();
            messageFcBo.setCreateDate(item.msgLocalTime);
            messageFcBo.setMobilePic(item.contactHeadUrl);
            messageFcBo.setId(item.contactId);
            messageFcBo.setType(1);
            if (item.chatMsg.getTarget() != null) {
                messageFcBo.setPushTitle(item.chatMsg.getTarget().getContent());
                messageFcBo.setMsgType(item.chatMsg.getTarget().getFileType());
            } else {
                messageFcBo.setPushTitle("");
                messageFcBo.setMsgType("");
            }
            messageFcBo.setTypeName(item.contactName);
            messageFcBo.setNoReadTotal(item.unReadNum);
            allMessage.add(messageFcBo);
        }
        setAdapter();
    }


    /**
     * 设置消息列表数据
     */
    private void setAdapter() {
        LGRecycleViewAdapter<MessageFcBo> adapter = new LGRecycleViewAdapter<MessageFcBo>(allMessage) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_home_message;
            }

            @Override
            public void convert(LGViewHolder holder, MessageFcBo messageFcBo, int position) {
                holder.setText(R.id.message_name, messageFcBo.getTypeName());
                if (messageFcBo.getType() == 0) {
                    holder.setText(R.id.message_message, messageFcBo.getPushTitle());
                } else {
                    switch (messageFcBo.getMsgType()) {
                        case Constants.CHAT_FILE_TYPE_TEXT:
                            holder.setText(R.id.message_message, messageFcBo.getPushTitle());
                            break;
                        case Constants.CHAT_FILE_TYPE_IMAGE:
                            holder.setText(R.id.message_message, "[图片]");
                            break;
                        case Constants.CHAT_FILE_TYPE_VOICE:
                            holder.setText(R.id.message_message, "[语音]");
                            break;
                        case Constants.CHAT_FILE_TYPE_FILE:
                            holder.setText(R.id.message_message, "[文件]");
                            break;
                        case Constants.CHAT_FILE_TYPE_SNAP:
                            holder.setText(R.id.message_message, "[阅后即焚]");
                            break;
                        case Constants.CHAT_FILE_TYPE_LINK:
                            holder.setText(R.id.message_message, "[网页]");
                            break;
                        default:
                            holder.setText(R.id.message_message, "[其他]");
                            break;
                    }
                }
                holder.setImageUrl(getActivity(), R.id.message_img, messageFcBo.getMobilePic());
                holder.setText(R.id.message_time, TimeUtils.getFriendlyTimeSpanByNow(messageFcBo.getCreateDate()));
                if (messageFcBo.getNoReadTotal() <= 0) {
                    holder.getView(R.id.today_point).setVisibility(View.INVISIBLE);
                } else {
                    holder.getView(R.id.today_point).setVisibility(View.VISIBLE);
                    holder.setText(R.id.today_point, messageFcBo.getNoReadTotal() < 100 ? messageFcBo.getNoReadTotal()
                            + "" : "99+");
                }
            }
        };
        adapter.setOnItemClickListener(R.id.item_layout, (view, position) -> {
            if (allMessage.get(position).getType() == 0) {
                Bundle bundle = new Bundle();
                bundle.putString("fcId", allMessage.get(position).getPushType());
                bundle.putString("fcName", allMessage.get(position).getTypeName());
                gotoActivity(MessageDetailsActivity.class, bundle, false);
            } else {
                UserEntry userBO = new UserEntry();
                userBO.setUserName(allMessage.get(position).getTypeName());
                userBO.setUserID(allMessage.get(position).getId());
                userBO.setUserPic(allMessage.get(position).getMobilePic());
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", userBO);
                List<RecentContactsEntry> entries = RecentContactIml.getDataByContactId(userBO.getUserID());
                if (!entries.isEmpty()) {
                    RecentContactsEntry item = entries.get(0);
                    item.unReadNum = 0;
                    RecentContactIml.addData(item);
                }
                gotoActivity(ImActivity.class, bundle, false);
            }
        });
        messageRecycle.setAdapter(adapter);
    }
}
