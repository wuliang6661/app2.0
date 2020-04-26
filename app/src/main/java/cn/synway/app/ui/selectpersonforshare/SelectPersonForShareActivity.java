package cn.synway.app.ui.selectpersonforshare;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import cn.synway.app.R;
import cn.synway.app.mvp.MVPBaseActivity;
import cn.synway.app.ui.main.personlist.PersonListFragment;
import cn.synway.app.ui.selectpersonforshare.SelectPersonForShareContract.View;


/**
 * 分享，选择人
 * 2019.8.13
 */

public class SelectPersonForShareActivity extends MVPBaseActivity<View, SelectPersonForSharePresenter> implements View {


    @BindView(R.id.back)
    LinearLayout mBack;
    @BindView(R.id.title_text)
    TextView mTitleText;
    @BindView(R.id.title_bar)
    LinearLayout mTitleBar;
    @BindView(R.id.sel_per_framelayout)
    FrameLayout mSelPerFramelayout;


    @Override
    protected int getLayout() {
        return R.layout.act_select_person_for_share;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitleText.setText("选择联系人");
        goBack();

        Bundle bundle = getIntent().getExtras();
        String title = bundle.getString("title");
        String message = bundle.getString("message");
        String imageUrl = bundle.getString("imageUrl");
        String shareUrl = bundle.getString("shareUrl");

        PersonListFragment selectPersonForShareActivity = PersonListFragment.getInstanse(
                PersonListFragment.Sel_Per_For_Share,
                title, message, imageUrl, shareUrl);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.sel_per_framelayout, selectPersonForShareActivity)
                .commit();
    }
}
