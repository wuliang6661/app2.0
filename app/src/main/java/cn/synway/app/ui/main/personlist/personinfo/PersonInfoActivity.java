package cn.synway.app.ui.main.personlist.personinfo;


import android.Manifest;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.AppBarLayout.OnOffsetChangedListener;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.gyf.barlibrary.ImmersionBar;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import butterknife.OnClick;
import cn.synway.app.R;
import cn.synway.app.db.dbmanager.UserIml;
import cn.synway.app.db.table.UserEntry;
import cn.synway.app.mvp.MVPBaseActivity;
import cn.synway.app.ui.chat.ImActivity;
import cn.synway.app.ui.main.personlist.personinfo.PersonInfoContract.View;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class PersonInfoActivity extends MVPBaseActivity<View, PersonInfoPresenter> implements View {

    @BindView(R.id.company)
    TextView mCompany;
    @BindView(R.id.head_name)
    TextView headName;
    @BindView(R.id.phone)
    TextView mPhone;
    @BindView(R.id.organ)
    TextView mOrgan;
    @BindView(R.id.name)
    TextView mName2;
    @BindView(R.id.back)
    TextView mBack;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.appbar)
    AppBarLayout mAppbar;
    @BindView(R.id.call_phone)
    ImageView mCallPhone;
    @BindView(R.id.iv)
    ImageView mIv;
    @BindView(R.id.manager_layout)
    LinearLayout sendMsgLayout;


    private boolean hasAllPermissions;
    private int appBarOffsize; //appbar移动偏移
    private int appBarHeight; //appbar的高度
    private int mAppbarCanMoveSize; //appbar的活动范围

    private float mStartMarginLeft; //TextView姓名初始marginLeft
    private int mMarstMarginLeft;//TextView姓名最大marginLeft

    private UserEntry userBO;


    @Override
    protected int getLayout() {
        return R.layout.act_person_info;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }


    private void initData() {
        Bundle extras = getIntent().getExtras();
        String id = extras.getString("id");
        mPresenter.getPersonInfo(id);

        Disposable subscribe = new RxPermissions(this).requestEachCombined(Manifest.permission.CALL_PHONE).subscribe(new Consumer<Permission>() {

            @Override
            public void accept(Permission permission) {

                if (permission.granted) {
                    PersonInfoActivity.this.hasAllPermissions = true;
                }
            }
        });


        mAppbar.addOnOffsetChangedListener(new OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (appBarOffsize == verticalOffset || mAppbar.getHeight() > appBarHeight) {
                    return;
                }
                PersonInfoActivity.this.appBarOffsize = verticalOffset;
                moveUserName(verticalOffset);
            }
        });


    }

    /**
     * 获取各种尺寸
     */
    private void getSizeParam() {

        this.appBarHeight = (int) (Resources.getSystem().getDisplayMetrics().density * 220);

        int statusBarHeight = ImmersionBar.getStatusBarHeight(this);
        //appbar活动范围  40:toorbar
        this.mAppbarCanMoveSize = (int) (Resources.getSystem().getDisplayMetrics().density * (220 - 40 - statusBarHeight));

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager manager = this.getWindowManager();
        manager.getDefaultDisplay().getMetrics(displayMetrics);
        int mWindowWidth = displayMetrics.widthPixels;

        mStartMarginLeft = Resources.getSystem().getDisplayMetrics().density * 15;
        headName.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = headName.getWidth();
                mMarstMarginLeft = mWindowWidth - width / 2;
                headName.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    /**
     * 姓名的联动效果
     *
     * @param verticalOffset
     */
    private void moveUserName(double verticalOffset) {
        if (mAppbarCanMoveSize == 0) {
            return;
        }
        double offset = Math.abs(verticalOffset / mAppbarCanMoveSize * 100);
        int marginLeft = (int) (mStartMarginLeft + mMarstMarginLeft / mAppbarCanMoveSize * offset);
        //LogUtils.e(marginLeft);
        MarginLayoutParams layoutParams = (MarginLayoutParams) headName.getLayoutParams();
        layoutParams.setMargins(marginLeft, 0, 0, 0);
        headName.setLayoutParams(layoutParams);
        // headName.requestLayout();
    }

    @Override
    public void setUser(UserEntry user) {
        this.userBO = user;
        headName.setText(user.getUserName());
        mName2.setText(user.getUserName());
        mCompany.setText(user.getOrganName());
        mOrgan.setText(user.getOrganName());
        mPhone.setText(user.getMobilePhoneNo());
        Glide.with(this).load(user.getUserPic()).into(mIv);
        if (user.getUserID().equals(UserIml.getUser().getUserID())) {  //如果是当前用户
            sendMsgLayout.setVisibility(android.view.View.GONE);
        }
        //放在这避免姓名宽度计算有问题
        getSizeParam();
    }

    @OnClick({R.id.back, R.id.phone, R.id.call_phone})
    public void onViewClicked(android.view.View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.call_phone:
            case R.id.phone:
                callPhone();
                break;
        }
    }

    /**
     * 打电话
     */
    private void callPhone() {
        if (!hasAllPermissions) {
            ToastUtils.showShort("权限缺失");
            return;
        }
        String phone = mPhone.getText().toString().trim();
        if (StringUtils.isEmpty(phone)) {
            showToast("当前用户未录入电话号码！");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel://" + phone));
        startActivity(intent);
    }


    @OnClick(R.id.send_msg)
    public void sendMsg() {
        if (userBO == null) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", userBO);
        gotoActivity(ImActivity.class, bundle, false);
    }


    float startY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LogUtils.e(ev.getY());
                if (appBarOffsize == 0) {
                    startY = ev.getY();
                } else {
                    startY = appBarHeight;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float y = ev.getY();
                int move = (int) ((y - startY) / 5);
                if (move > 1 && appBarOffsize == 0) {
                    zommAppBar(move);
                    this.startY = y;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (appBarOffsize == 0) {
                    resizeAppBar();
                }

                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    /**
     * 还原
     */
    private void resizeAppBar() {
        int height = this.mAppbar.getHeight();
        if (height > appBarHeight) {
            LayoutParams params = this.mAppbar.getLayoutParams();
            params.height = appBarHeight;
            this.mAppbar.setLayoutParams(params);
        }
    }

    /**
     * 拉伸
     *
     * @param moveSize
     */
    private void zommAppBar(int moveSize) {
        LayoutParams params = this.mAppbar.getLayoutParams();
        params.height = params.height + moveSize;
        this.mAppbar.setLayoutParams(params);
    }
}
