package cn.synway.app.ui.serachmessage;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.synway.app.R;
import cn.synway.app.bean.MessageListBo;
import cn.synway.app.db.dbmanager.UserIml;
import cn.synway.app.db.table.UserEntry;
import cn.synway.app.mvp.MVPBaseActivity;
import cn.synway.app.ui.web.SynWebBuilder;
import cn.synway.app.ui.weex.entity.WxMapData;
import cn.synway.app.utils.RecycleUtils;
import cn.synway.app.widget.ClearEditText;
import cn.synway.app.widget.lgrecycleadapter.LGRecycleViewAdapter;
import cn.synway.app.widget.lgrecycleadapter.LGViewHolder;
import jaydenxiao.com.expandabletextview.ExpandableTextView;


/**
 * MVPPlugin
 * 消息搜索页面
 */

public class SerachMessageActivity extends MVPBaseActivity<SerachMessageContract.View, SerachMessagePresenter>
        implements SerachMessageContract.View {

    @BindView(R.id.search_input)
    ClearEditText searchInput;
    @BindView(R.id.search_rv)
    RecyclerView searchRv;
    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.none_layout)
    LinearLayout noneLayout;

    //显示的消息列表
    List<MessageListBo.ListBean> listBeans;

    @Override
    protected int getLayout() {
        return R.layout.act_search;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listBeans = new ArrayList<>();
        initView();

    }


    /**
     * 初始化布局
     */
    private void initView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        searchRv.setLayoutManager(manager);
        searchInput.setHint("请输入消息标题或内容关键字");

        //paddingTop 标题栏
        immersionBar.titleBar(titleBar).init();
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (StringUtils.isEmpty(editable.toString())) {
                    listBeans.clear();
                    setMessageAdapter();
                } else {
                    mPresenter.getMessageListByKey(editable.toString(), false);
                }
            }
        });
        searchRv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                if (RecycleUtils.isSlideToBottom(recyclerView)) {
//                    mPresenter.getMessageListByKey(searchInput.getText().toString(), true);
//                }
            }
        });
    }


    @OnClick(R.id.back)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    public void onRequestError(String msg) {
        showToast(msg);
    }

    @Override
    public void getMessageList(boolean isPageAdd, MessageListBo messageListBo) {
        if (isPageAdd) {
            listBeans.addAll(messageListBo.getList());
        } else {
            listBeans = messageListBo.getList();
            if (listBeans.isEmpty()) {
                noneLayout.setVisibility(View.VISIBLE);
                searchRv.setVisibility(View.GONE);
            } else {
                noneLayout.setVisibility(View.GONE);
                searchRv.setVisibility(View.VISIBLE);
            }
        }
        setMessageAdapter();
    }

    @Override
    public void readSourss() {
        mPresenter.getMessageListByKey(searchInput.getText().toString(), false);
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
                    if (StringUtils.isEmpty(listBeans.get(position).getPushUrl())) {
                        return;
                    }
                    url = listBeans.get(position).getPushUrl() + "?userName=" + userBO.getUserName() + "&phoneNumber=" +
                            userBO.getMobilePhoneNo() + "&userID=" + userBO.getUserID() + "&loginCode=" + userBO.getCode() +
                            "&LoginOragian=" + userBO.getOrganName() + "&LoginOragianCode=" + userBO.getOrgan()
                            + "&userType=" + userBO.getType();
                    //url = "http://172.18.100.37:8188/face/#/fullsearch";
                    SynWebBuilder.builder()
                            .setUrl(url)
                            .setName("消息")
                            .start(SerachMessageActivity.this);
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
//            PublicAccountItemInterface itemInterface = new PublicAccountItemInterface();
//            NetAPPBO netAPPDao = new NetAPPBO();
//            netAPPDao.setBusinessType(listBeans.get(position).getBusinessType() + "");
//            netAPPDao.setSourceUrl(listBeans.get(position).getPushUrl());
//            itemInterface.handlerClick(SerachMessageActivity.this, netAPPDao);
//            mPresenter.updateRead(listBeans.get(position).getPushId());
        });
        searchRv.setAdapter(adapter);
    }
}
