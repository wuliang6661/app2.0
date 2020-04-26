package cn.synway.app.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.StringUtils;

import java.util.List;

import butterknife.BindView;
import cn.synway.app.R;
import cn.synway.app.api.HttpResultSubscriber;
import cn.synway.app.api.HttpServerImpl;
import cn.synway.app.base.SynBaseActivity;
import cn.synway.app.bean.VersionNodeBO;
import cn.synway.app.widget.lgrecycleadapter.LGRecycleViewAdapter;
import cn.synway.app.widget.lgrecycleadapter.LGViewHolder;

/**
 * 版本列表页面
 */
public class VersionListActivity extends SynBaseActivity {


    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        goBack();
        setTitleText("版本日志");

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider_inset));
        mRecyclerView.addItemDecoration(itemDecoration);
        showProgress("");
        getVersionList();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_releasenote;
    }


    /**
     * 获取版本列表
     */
    private void getVersionList() {
        HttpServerImpl.getVersionList().subscribe(new HttpResultSubscriber<List<VersionNodeBO>>() {
            @Override
            public void onSuccess(List<VersionNodeBO> s) {
                stopProgress();
                setAdapter(s);
            }

            @Override
            public void onFiled(String message) {
                stopProgress();
                showToast(message);
            }
        });
    }


    private void setAdapter(List<VersionNodeBO> versionNodeBOS) {
        LGRecycleViewAdapter<VersionNodeBO> adapter = new LGRecycleViewAdapter<VersionNodeBO>(versionNodeBOS) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_release_note;
            }

            @Override
            public void convert(LGViewHolder holder, VersionNodeBO versionNodeBO, int position) {
                TextView leave = (TextView) holder.getView(R.id.version_leave);
                switch (versionNodeBO.getLevel()) {
                    case 0:
                        leave.setText("普通");
                        leave.setBackgroundResource(R.drawable.leave_bg_blue);
                        break;
                    case 1:
                        leave.setText("高");
                        leave.setBackgroundResource(R.drawable.leave_bg_red);
                        break;
                    case 2:
                        leave.setText("严重");
                        leave.setBackgroundResource(R.drawable.leave_bg_yello);
                        break;
                }
                if (StringUtils.isEmpty(versionNodeBO.getCreateTime())) {
                    holder.setText(R.id.version_date, "");
                } else {
                    holder.setText(R.id.version_date, versionNodeBO.getCreateTime());
                }
                holder.setText(R.id.version_name, versionNodeBO.getVersionName());
                holder.setText(R.id.version_message, versionNodeBO.getReleaseNote());
                TextView version_news = (TextView) holder.getView(R.id.version_news);
                if (position == 0 && versionNodeBO.getVersionCode() != AppUtils.getAppVersionCode()) {
                    version_news.setText("最新版本");
                    version_news.setTextColor(ContextCompat.getColor(VersionListActivity.this, R.color.blue_color));
                }
                if (versionNodeBO.getVersionCode() == AppUtils.getAppVersionCode()) {
                    version_news.setText("当前版本");
                    version_news.setTextColor(Color.parseColor("#666666"));
                } else if (position != 0) {
                    version_news.setText("");
                }
            }
        };
        mRecyclerView.setAdapter(adapter);
    }

}
