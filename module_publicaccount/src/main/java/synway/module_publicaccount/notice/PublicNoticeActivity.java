package synway.module_publicaccount.notice;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import java.util.ArrayList;

import synway.module_interface.lastcontact.LastContact;
import synway.module_publicaccount.R;
import synway.module_publicaccount.push.NewMsgNotifyDeal;
import synway.module_publicaccount.push.Sps_Notify;

public class PublicNoticeActivity extends FragmentActivity {

    private ImageView ivBack;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<Fragment> fragmentList;
    private PageFragmentAdapter pageFragmentAdapter;
    private String[] names = {"待办", "全部消息"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_public_notice);

        clearTip();
        Sps_Notify.setCurrent(this, LastContact.PUBLIC_NOTICE_TARGET_ID);
        NewMsgNotifyDeal.dismissPushMsgAll(this);
        // Sps_Notify.setCurrent(this, LastContact.PUBLIC_NOTICE_TARGET_ID);
        // NewMsgNotifyDeal.dismissPushMsgAll(this);
        initViews();


    }


    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(onClickListener);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewpager);



        fragmentList = new ArrayList<>();
        fragmentList.add(BacklogFragment.newInstance("",""));
        fragmentList.add(MessageFragment.newInstance("",""));
        pageFragmentAdapter = new PageFragmentAdapter(getSupportFragmentManager(),fragmentList,names);
        viewPager.setOffscreenPageLimit(fragmentList.size());
        viewPager.setAdapter(pageFragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);



    }


    @Override protected void onStop() {
        super.onStop();
        Sps_Notify.clear(this);
    }


    @Override protected void onDestroy() {
        super.onDestroy();
        clearTip();
    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override public void onClick(View v) {
            finish();
        }
    };

    public void clearTip() {
        //TODO:清除 消息提醒
//        // SQLiteDatabase db = null;
//            // db = SQLiteDatabase.openDatabase(BaseUtil.getDBPath(this), null,
//            // SQLiteDatabase.OPEN_READWRITE);
//            SetLCForPublicAccount.clearUnReadCount(this, Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase(),
//                LastContact.PUBLIC_NOTICE_TARGET_ID);
//            if (Main.instance().handlerLCTCount != null) {
//                Main.instance().handlerLCTCount.updateLCTCount();
//            }
//            //            Main.instance().newFragment().handlerTipCount.updateLCTCount();
//        } catch (Exception e) {
//            throw new RuntimeException("SyncGetRecordFromDB 打开数据库出错");
//        } finally {
//            // if (db != null) {
//            // db.close();
//            // }
//        }
    }
}
