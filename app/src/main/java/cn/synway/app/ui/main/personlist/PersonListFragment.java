package cn.synway.app.ui.main.personlist;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.synway.app.R;
import cn.synway.app.bean.PersonInPsListBO.OrganAndUserInfoBO;
import cn.synway.app.bean.PersonInPsListBO.TagList;
import cn.synway.app.bean.event.KeepEvent;
import cn.synway.app.bean.event.MainMenuEvent;
import cn.synway.app.bean.event.UpdateEvent;
import cn.synway.app.bean.event.UpdatePicSoressEvent;
import cn.synway.app.db.table.UserEntry;
import cn.synway.app.mvp.MVPBaseFragment;
import cn.synway.app.push.ChatMsgManager;
import cn.synway.app.ui.chat.ImActivity;
import cn.synway.app.ui.main.personlist.PersonListAdapter.ItemClick;
import cn.synway.app.ui.main.personlist.personinfo.PersonInfoActivity;
import cn.synway.app.ui.search.SearchActivity;
import cn.synway.app.widget.AlertDialog;
import cn.synway.app.widget.a_zView;
import cn.synway.app.widget.a_zView.MoveListener;
import cn.synway.app.widget.lgrecycleadapter.OnRVItemClickListener;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class PersonListFragment extends MVPBaseFragment<PersonListContract.View, PersonListPresenter>
        implements PersonListContract.View {
    public static String Sel_Per_For_Share = "SelectPersonForShareActivity";

    @BindView(R.id.menu)
    ImageView menu;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.search)
    ImageView search;

    @BindView(R.id.person_list)
    RecyclerView personList;
    Unbinder unbinder;
    @BindView(R.id.letter_view)
    a_zView mLetterView;

    @BindView(R.id.refresh)
    SwipeRefreshLayout mRefresh;
    @BindView(R.id.today_point)
    TextView todayPoint;
    @BindView(R.id.keep_hint)
    TextView keepHint;
    @BindView(R.id.organ_tag_rv)
    RecyclerView mOrganTagRv;
    @BindView(R.id.none_layout)
    LinearLayout mNoneLayout;

    private Dialog mleterDialog;
    private Bundle mArguments;

    public static PersonListFragment getInstanse(String from, String title, String message, String imageUrl, String shareUrl) {
        PersonListFragment personListFragment = new PersonListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("from", from);
        bundle.putString("title", title);
        bundle.putString("message", message);
        bundle.putString("imageUrl", imageUrl);
        bundle.putString("shareUrl", shareUrl);
        personListFragment.setArguments(bundle);
        return personListFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fra_person_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleText.setText("通讯录");
        initView(view);

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
        }
        else {
            keepHint.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe()
    public void updatePic(UpdatePicSoressEvent update) {
        if (mPresenter != null) {
            mPresenter.getPeopleList("0", true);
        }
    }

    /**
     * 初始化布局
     *
     * @param view
     */
    private void initView(View view) {


        //隐藏标题栏
        mArguments = getArguments();
        if (mArguments != null && Sel_Per_For_Share.equals(mArguments.get("from"))) {
            view.findViewById(R.id.fra_per_head).setVisibility(View.GONE);
        }

        mOrganTagRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        personList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        mRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getPeopleList("0", true);
            }
        });
        mPresenter.getPeopleList("0", true);
        //右侧字母布局及触摸操作
        mLetterView.addOnMoveListener(new MoveListener() {
            @Override
            public void onLetter(String letter) {
                if (mleterDialog == null) {
                    mleterDialog = new Dialog(getActivity(), R.style.letter_dialog);
                    mleterDialog.setContentView(LayoutInflater.from(getActivity())
                            .inflate(R.layout.letter_view, null));
                }

                Integer position = mPresenter.getCharPosition(letter);

                TextView letterView = mleterDialog.findViewById(R.id.letter);
                letterView.setText(letter);
                if (!mleterDialog.isShowing()) {
                    mleterDialog.show();
                }

                if (position != null) {
                    smootScrollToPosition(position);
                }

            }

            @Override
            public void hiddeLetter() {
                if (mleterDialog != null) {
                    mleterDialog.dismiss();
                }
            }
        });

    }


    /**
     * 设置机构TAG点击
     */
    private void backOrganTag(TagList entity) {
        String id = entity.getId();
        mPresenter.getGroupPerson(id);
    }

    /**
     * 机构tag适配器
     * 人员和机构适配器
     */
    @Override
    public void getPeoleList(List<OrganAndUserInfoBO> data, List<TagList> tagLists) {
        stopProgress();
        mRefresh.setRefreshing(false);

        if (data.size() == 0) {
            mNoneLayout.setVisibility(View.VISIBLE);
            personList.setVisibility(View.GONE);
        }
        else {
            mNoneLayout.setVisibility(View.GONE);
            personList.setVisibility(View.VISIBLE);
        }

        //机构tag适配器
        OrganTagAdapter mOrganTagRvAdapter = (OrganTagAdapter) mOrganTagRv.getAdapter();
        if (mOrganTagRvAdapter == null) {
            mOrganTagRv.setAdapter(new OrganTagAdapter(getActivity(), tagLists, new OnRVItemClickListener<TagList>() {
                @Override
                public void ItemClick(View view, int position, TagList entity) {
                    backOrganTag(entity);
                }
            }));
            mOrganTagRv.smoothScrollToPosition(tagLists.size() - 1);
        }
        else {
            mOrganTagRvAdapter.setData(tagLists);
            mOrganTagRv.smoothScrollToPosition(tagLists.size() - 1);
        }

        //人员和机构列表适配器
        Adapter adapter = personList.getAdapter();
        if (adapter == null) {
            personList.setAdapter(new PersonListAdapter(data, myItemClick));
        }
        else {
            ((PersonListAdapter) adapter).changeData(data);
        }
    }

    /**
     * 获取数据失败
     */
    @Override
    public void onFaild() {
        stopProgress();
        mRefresh.setRefreshing(false);
    }

    /**
     * 垂直列表点击事件
     */
    private ItemClick myItemClick = new ItemClick() {
        @Override
        public void goPersonCard(OrganAndUserInfoBO userInfoBO) {
            //点击人员
            if (mArguments != null && Sel_Per_For_Share.equals(mArguments.get("from"))) {
                //选择分享联系人
                new AlertDialog(getActivity()).builder().setGone().setMsg("是否确认分享给" + userInfoBO.getUserName())
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", v -> {
                            UserEntry userEntry = new UserEntry();
                            userEntry.setUserID(userInfoBO.getUserID());
                            userEntry.setUserName(userInfoBO.getUserName());
                            userEntry.setUserPic(userInfoBO.getUserPic());
                            userEntry.setCode(userInfoBO.getCode());
                            String title = mArguments.getString("title");
                            String message = mArguments.getString("message");
                            String imageUrl = mArguments.getString("imageUrl");
                            String shareUrl = mArguments.getString("shareUrl");
                            ChatMsgManager.getInstance().sendShareLink(userEntry, title, message, imageUrl, shareUrl);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("user", userEntry);
                            gotoActivity(ImActivity.class, bundle, true);
                        }).show();
            }
            else {
                //查看人员名片
                Bundle bundle = new Bundle();
                bundle.putString("id", userInfoBO.getUserID());
                gotoActivity(PersonInfoActivity.class, bundle, false);
            }
        }

        @Override
        public void getGroupPerson(String name, String id) {
            //点击机构
            showProgress("");
            mPresenter.getGroupPerson(id);
        }
    };

    /**
     * 菜单
     */
    @OnClick({R.id.menu, R.id.search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.menu:
                EventBus.getDefault().post(new MainMenuEvent());
                break;
            case R.id.search:
                gotoActivityForResult(SearchActivity.class, 200, false);
                break;
        }
    }


    /**
     * 垂直列表滚动到顶部
     *
     * @param position
     */
    private void smootScrollToPosition(int position) {
        FragmentActivity activity = this.getActivity();
        if (activity == null)
            return;

        LinearSmoothScroller smoothScroller = new LinearSmoothScroller(activity.getBaseContext()) {
            @Override
            protected int getVerticalSnapPreference() {
                return SNAP_TO_START;
            }
        };
        smoothScroller.setTargetPosition(position);
        personList.getLayoutManager().startSmoothScroll(smoothScroller);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            String organId = data.getStringExtra("OrganId");
            if (organId != null) {
                showProgress("");
                mPresenter.getGroupPerson(organId);
            }


        }
    }
}
