package cn.synway.app.ui.search;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.synway.app.R;
import cn.synway.app.bean.OrganDao;
import cn.synway.app.db.table.UserEntry;
import cn.synway.app.mvp.MVPBaseActivity;
import cn.synway.app.ui.main.personlist.personinfo.PersonInfoActivity;
import cn.synway.app.ui.search.SearchContract.View;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class SearchActivity extends MVPBaseActivity<View, SearchPresenter> implements View {

    @BindView(R.id.back)
    TextView mBack;
    @BindView(R.id.search_input)
    EditText mSearchInput;
    @BindView(R.id.title_bar)
    RelativeLayout mTitleBar;
    @BindView(R.id.search_rv)
    RecyclerView mSearchRv;
    @BindView(R.id.none_layout)
    LinearLayout noneLayout;

    private int SEARCHWHAT = 1;
    private MyHandler mMyHandler;


    @Override
    protected int getLayout() {
        return R.layout.act_search;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }


    private void init() {

        goBack();
        mSearchInput.setHint("请输入用户名或机构名");
        mMyHandler = new MyHandler(this);
        mSearchRv.setLayoutManager(new LinearLayoutManager(this));

        mSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0 && s.toString().trim().length() != 0) {
                    Message msg = Message.obtain();
                    msg.what = SEARCHWHAT;
                    msg.obj = s.toString(); //携带当前值
                    mMyHandler.removeMessages(SEARCHWHAT);
                    mMyHandler.sendMessageDelayed(msg, 1000);//隔一小段时间发送msg
//                    mPresenter.getSearchVal(s.toString());
                }
                else {
                    getData(new ArrayList<>(), new ArrayList<>(), "");

                }
            }
        });

        mSearchInput.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(android.view.View v, int keyCode, KeyEvent event) {
                if (keyCode == EditorInfo.IME_ACTION_SEARCH) {
                    String search = mSearchInput.getText().toString().trim();
                    mPresenter.getSearchVal(search);
                    return true;
                }
                return false;
            }
        });
    }

    private static class MyHandler extends Handler {

        private final WeakReference<SearchActivity> mWeakReference;

        public MyHandler(SearchActivity searchActivity) {
            mWeakReference = new WeakReference<>(searchActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SearchActivity activity = mWeakReference.get();
            if (activity != null && msg.what == activity.SEARCHWHAT) {
                activity.mPresenter.getSearchVal((String) msg.obj);
            }
        }
    }


    @Override
    public void getData(List<UserEntry> list, List<OrganDao> oList, String key) {

        if (!key.equals(mSearchInput.getText().toString().trim())) {
            return;
        }

        if (list.size() == 0 && oList.size() == 0) {
            noneLayout.setVisibility(android.view.View.VISIBLE);
            mSearchRv.setVisibility(android.view.View.GONE);
        }
        else {
            noneLayout.setVisibility(android.view.View.GONE);
            mSearchRv.setVisibility(android.view.View.VISIBLE);
        }
        Adapter adapter = mSearchRv.getAdapter();
        if (adapter == null) {

            mSearchRv.setAdapter(new SearchAdapter(this, key, list, oList, view -> {
                itemClick(view);
            }));
        }
        else {
            ((SearchAdapter) adapter).changeData(list, oList, key);
        }


    }

    private void itemClick(android.view.View view) {
        String OrganId = (String) view.getTag(R.string.ORGANTAGKEY);
        String UserId = (String) view.getTag(R.string.USERTAGKEY);
        LogUtils.e(OrganId, UserId);
        if (UserId != null) {
            Bundle bundle = new Bundle();
            bundle.putString("id", UserId);
            gotoActivity(PersonInfoActivity.class, bundle, false);
        }
        else {
            Intent intent = new Intent();
            intent.putExtra("OrganId", OrganId);
            setResult(RESULT_OK, intent);
            finish();
        }

    }
}
