package cn.synway.app.ui.messagedetails;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import cn.synway.app.R;
import cn.synway.app.bean.FcTypeBo;
import cn.synway.app.bean.MessageListBo;
import cn.synway.app.bean.event.MessageRefreshEvent;
import cn.synway.app.db.dbmanager.UserIml;
import cn.synway.app.db.table.UserEntry;
import cn.synway.app.mvp.MVPBaseActivity;
import cn.synway.app.ui.web.SynWebBuilder;
import cn.synway.app.ui.weex.entity.WxMapData;
import cn.synway.app.utils.RecycleUtils;
import cn.synway.app.widget.lgrecycleadapter.LGRecycleViewAdapter;
import cn.synway.app.widget.lgrecycleadapter.LGViewHolder;
import jaydenxiao.com.expandabletextview.ExpandableTextView;


/**
 * MVPPlugin
 * 消息列表页
 */
public class MessageDetailsActivity extends MVPBaseActivity<MessageDetailsContract.View, MessageDetailsPresenter>
        implements MessageDetailsContract.View {

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.message_recycle)
    RecyclerView messageRecycle;
    @BindView(R.id.load_more_layout)
    LinearLayout loadMoreLayout;

    private String fcId;
    private String fcTypeId;

    //应用消息类型
    List<FcTypeBo> fcTypeBos;

    //显示的消息列表
    List<MessageListBo.ListBean> listBeans;

    private boolean isHasAdd = true;  //是否还有下一页

    @Override
    protected int getLayout() {
        return R.layout.act_message_list;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
        goBack();
        String title = getIntent().getExtras().getString("fcName");
        fcId = getIntent().getExtras().getString("fcId");
        setTitleText(title);

        initView();
        listBeans = new ArrayList<>();
        showProgress("");
        mPresenter.getMsgTypeList(fcId);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 初始化布局
     */
    private void initView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        messageRecycle.setLayoutManager(manager);

        messageRecycle.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (RecycleUtils.isSlideToBottom(recyclerView)) {
                    if (isHasAdd) {
                        mPresenter.getMessageList(fcId, fcTypeId, true);
                        loadMoreLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public void onRequestError(String msg) {
        loadMoreLayout.setVisibility(View.GONE);
        stopProgress();
        showToast(msg);
    }

    @Override
    public void getFcTypeList(List<FcTypeBo> fcTypeBos) {
        stopProgress();
        setTabLayout(fcTypeBos);
        this.fcTypeBos = fcTypeBos;
    }

    @Override
    public void getMessageList(boolean isAddPage, MessageListBo messageListBo) {
        stopProgress();
        loadMoreLayout.setVisibility(View.GONE);
        if (messageListBo.getList().size() < 30) {
            isHasAdd = false;
        } else {
            isHasAdd = true;
        }
        if (isAddPage) {
            listBeans.addAll(messageListBo.getList());
        } else {
            listBeans = messageListBo.getList();
        }
        setMessageAdapter();
    }

    @Override
    public void readSourss() {
        onEvent(null);
    }


    /**
     * 刷新消息数
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageRefreshEvent event) {
        if (fcTypeBos.size() == 0) {
            tabLayout.setVisibility(View.GONE);
            mPresenter.getMessageList(fcId, null, false);
            return;
        }
        mPresenter.getMessageList(fcId, fcTypeBos.get(tabLayout.getSelectedTabPosition()).getId(), false);
    }


    /**
     * 设置Tab的显示
     */
    private void setTabLayout(List<FcTypeBo> fcTypeBos) {
        if (fcTypeBos.size() == 0) {
            tabLayout.setVisibility(View.GONE);
            mPresenter.getMessageList(fcId, null, false);
            return;
        }
        if (fcTypeBos.size() > 4) {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        } else {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        }
        for (int i = 0; i < fcTypeBos.size(); i++) {
            tabLayout.addTab(tabLayout.newTab().setText(fcTypeBos.get(i).getFcName()));
        }
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                fcTypeId = fcTypeBos.get(tab.getPosition()).getId();
                showProgress("");
                mPresenter.getMessageList(fcId, fcTypeBos.get(tab.getPosition()).getId(), false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        fcTypeId = fcTypeBos.get(0).getId();
        showProgress("");
        mPresenter.getMessageList(fcId, fcTypeBos.get(0).getId(), false);
    }


    LGRecycleViewAdapter<MessageListBo.ListBean> adapter;

    /**
     * 设置消息列表适配器
     */
    private void setMessageAdapter() {
        if (adapter != null) {
            adapter.setData(listBeans);
            return;
        }
        adapter = new LGRecycleViewAdapter<MessageListBo.ListBean>(listBeans) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_message;
            }

            @Override
            public void convert(LGViewHolder holder, MessageListBo.ListBean listBean, int position) {
                holder.setText(R.id.message_title, listBean.getPushTitle());
                ExpandableTextView messageDetails = (ExpandableTextView) holder.getView(R.id.message_details);
                messageDetails.setText(listBean.getPushContent(), position);
                if (StringUtils.isEmpty(listBean.getPushUrl())) {
                    holder.getView(R.id.more_layout).setVisibility(View.GONE);
                } else {
                    holder.getView(R.id.more_layout).setVisibility(View.VISIBLE);
                }
                if (listBean.getIsRead() == 0) {   //未读
                    holder.getView(R.id.read_img).setVisibility(View.VISIBLE);
                } else {
                    holder.getView(R.id.read_img).setVisibility(View.GONE);
                }
                messageDetails.setOnExpandStateChangeListener((textView, isExpanded) -> {
                    if (isExpanded) {
                        mPresenter.updateRead(listBean.getPushId());
                    }
                });
            }
        };
        adapter.setOnItemClickListener(R.id.item_layout, (view, position) -> {
            mPresenter.updateRead(listBeans.get(position).getPushId());
            UserEntry userBO = UserIml.getUser();
            String url;
            switch (listBeans.get(position).getBusinessType()) {
                case 0:
                    if (!StringUtils.isEmpty(listBeans.get(position).getPushUrl())) {
                        url = listBeans.get(position).getPushUrl() + "?userName=" + userBO.getUserName() + "&phoneNumber=" +
                                userBO.getMobilePhoneNo() + "&userID=" + userBO.getUserID() + "&loginCode=" + userBO.getCode() +
                                "&LoginOragian=" + userBO.getOrganName() + "&LoginOragianCode=" + userBO.getOrgan()
                                + "&userType=" + userBO.getType() + "&mobileAppUserid=" + userBO.getUserID();
                        //url = "http://172.18.100.37:8188/face/#/fullsearch";
                        SynWebBuilder.builder()
                                .setUrl(url)
                                .setName("消息")
//                            .setUserId(userBO.getUserID())
                                .start(MessageDetailsActivity.this);
                    }
                    break;
                case 1:
                    //跳转weex页面
                    url = listBeans.get(position).getPushUrl();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    StringBuilder builder = new StringBuilder();
                    builder.append(url);
                    Uri uri = Uri.parse(builder.toString());
                    if (TextUtils.equals("http", uri.getScheme()) || TextUtils.equals("https", uri.getScheme())) {
                        intent.setData(uri);
                        intent.putExtra("Title", "消息");
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.addCategory("com.SynwayOsc.android.intent.category.WEEX");
                        Map<String, Object> params = new HashMap<>();
                        params.put("UserId", userBO.getUserID());
                        WxMapData map = new WxMapData();
                        map.setWxMapData(params);
                        intent.putExtra("DATA", map);
                        startActivity(intent);
                    } else {
                        showToast("跳转地址格式错误,请检查格式");
                    }
                    break;
                case 2:

                    break;
            }
        });
        messageRecycle.setAdapter(adapter);
    }
}
