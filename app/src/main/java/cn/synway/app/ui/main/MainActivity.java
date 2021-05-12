package cn.synway.app.ui.main;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.SimpleDrawerListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.FragmentUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;
import com.guoqi.actionsheet.ActionSheet;
import com.xyz.tabitem.BottmTabItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import cn.synway.app.R;
import cn.synway.app.base.SynApplication;
import cn.synway.app.bean.event.MainMenuEvent;
import cn.synway.app.bean.event.UpdateEvent;
import cn.synway.app.bean.event.UpdatePicSoressEvent;
import cn.synway.app.config.Config;
import cn.synway.app.config.FileConfig;
import cn.synway.app.db.dbmanager.UserIml;
import cn.synway.app.db.table.UserEntry;
import cn.synway.app.mvp.MVPBaseActivity;
import cn.synway.app.push.PushManager;
import cn.synway.app.ui.AboutActivity;
import cn.synway.app.ui.downmap.DownMapActivity;
import cn.synway.app.ui.main.none.NoneFragment1;
import cn.synway.app.ui.main.none.NoneFragment2;
import cn.synway.app.ui.main.none.NoneFragment3;
import cn.synway.app.ui.main.publicappcenter.PublicAppCenterFragment;
import cn.synway.app.ui.useraccount.UserAccountActivity;
import cn.synway.app.utils.AppManager;
import cn.synway.app.utils.AvatarUtil;
import cn.synway.app.utils.PhotoFromPhotoAlbum;
import cn.synway.app.utils.SynCountlyFactory;
import cn.synway.app.utils.UpdateUtils;
import cn.synway.app.widget.AlertDialog;
import cn.synway.synmonitor.event.SynCountly;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.yokeyword.fragmentation.SupportFragment;
import synway.common.watermaker.WaterMarkUtil;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class MainActivity extends MVPBaseActivity<MainContract.View, MainPresenter> implements MainContract.View,
        ActionSheet.OnActionSheetSelected {

    @BindView(R.id.main1)
    BottmTabItem main1;
    @BindView(R.id.main3)
    BottmTabItem main3;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.orgin_name)
    TextView orginName;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.user_image)
    ImageView userImage;
    @BindView(R.id.about_point)
    TextView aboutPoint;
    @BindView(R.id.main2_img)
    ImageView main2Img;
    @BindView(R.id.main2_text)
    TextView main2Text;
    @BindView(R.id.buttom_tab)
    LinearLayout buttomTab;
    @BindView(R.id.main1_layout)
    RelativeLayout main1Layout;
    @BindView(R.id.line)
    View line;

    private int selectPosition = 0;
    private SupportFragment[] mFragments;


    private File cameraSavePath;//拍照照片路径
    private Uri uri;
    private final int INSTALL_PACKAGES_REQUESTCODE = 1111; //安装apk 权限code
    private File updateAPKFile;//apk地址


    @Override
    protected int getLayout() {
        return R.layout.act_main;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
        getPermission();
        cameraSavePath = new File(Environment.getExternalStorageDirectory().getPath() + "/" +
                System.currentTimeMillis() + ".jpg");
        main2Text.setText(StringUtils.isEmpty(Config.AppCenterName) ? "应用中心" : Config.AppCenterName);
        initFragment();
        initView();
        checkUpdate();
        initServer();
        initSynCountly();
    }


    /**
     * 初始化侧滑菜单显示
     */
    private void initView() {
        orginName.setText(UserIml.getUser().getOrganName());
        userName.setText(UserIml.getUser().getUserName());
        if (StringUtils.isEmpty(UserIml.getUser().getUserPic())) {
            // 注：目标图转换成Bitmap，多Bitmap拼接都放在子线程操作
            ArrayList bitmapList = new ArrayList();
            bitmapList.add(UserIml.getUser().getUserName().substring(0, 1));   // 可添加需展示的文字
            Bitmap avatar = AvatarUtil.getBuilder(this)
                    .setList(bitmapList)
                    .setTextSize(SizeUtils.dp2px(13))            // px
                    .setTextColor(R.color.f_white)      // 需传入color文件下的颜色值
                    .setBitmapSize(80, 80)
                    .create();
            userImage.setImageBitmap(avatar);

        } else {
            Glide.with(this).load(UserIml.getUser().getUserPic())
                    .error(R.drawable.police_picture)
                    .bitmapTransform(new CropCircleTransformation(this)).placeholder(R.drawable.police_picture).into(userImage);
        }
        //drawerLayout 主布局跟着一起滑动
        drawerLayout.addDrawerListener(new SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                //滑动过程中不断回调 slideOffset:0~1
                View content = drawerLayout.getChildAt(0);
                View menu = drawerView;
                float scale = 1 - slideOffset;
                content.setTranslationX(menu.getMeasuredWidth() * (1 - scale));//0~width
            }
        });
    }


    /**
     * 检查更新
     */
    private void checkUpdate() {
        UpdateUtils.checkUpdate(new UpdateUtils.onSourssListener() {
            @Override
            public void onComplan() {

            }

            @Override
            public void isHaveNewVersion() {
                Config.isHaveNewVersion = true;
                aboutPoint.setVisibility(View.VISIBLE);
                EventBus.getDefault().post(new UpdateEvent());
            }

            @Override
            public void nowIsNew() {

            }

            @Override
            public void update(File file) {
                checkPermissionAndupdate(file);

            }
        });
    }

    private void checkPermissionAndupdate(File file) {

        if (Build.VERSION.SDK_INT >= 26) {
            boolean b = getPackageManager().canRequestPackageInstalls();
            if (b) {
                AppUtils.installApp(file);
            } else {
                this.updateAPKFile = file;
                //请求安装未知应用来源的权限
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES},
                        INSTALL_PACKAGES_REQUESTCODE);
            }
        } else {
            AppUtils.installApp(file);
        }

    }


    /**
     * 启动长连接服务
     */
    public void initServer() {
        PushManager.getInstance().startServer();
    }

    /**
     * 开启数据收集
     */
    private void initSynCountly() {
        UserEntry user = UserIml.getUser();
        SynCountly synCountly = SynCountlyFactory.initSynCountly(this, Config.gartherUrl, Config.appKey)
                .setLogUtilSwitch(Config.SynCountlyLog)
                .setUser(user.getUserID(), user.getUserName(), user.getOrgan(), user.getOrganName(), "");
        synCountly.create();
        synCountly.gatherApplicationStart();
    }

    /**
     * 初始化fragment
     */
    private void initFragment() {
        if (Config.showMessageMenu && Config.showAddressListMenu) {
            buttomTab.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
            mFragments = new SupportFragment[1];
            FragmentUtils.replace(getSupportFragmentManager(), new PublicAppCenterFragment(), R.id.fragment_container);
            return;
        }
        //不显示消息界面
        if (Config.showMessageMenu) {
            mFragments = new SupportFragment[2];
            SupportFragment firstFragment = findFragment(NoneFragment2.class);
            if (firstFragment == null) {
                mFragments[0] = new NoneFragment2();
                mFragments[1] = new NoneFragment3();

                loadMultipleRootFragment(R.id.fragment_container, 0,
                        mFragments[0],
                        mFragments[1]);
            } else {
                // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题
                // 这里我们需要拿到mFragments的引用
                mFragments[0] = firstFragment;
                mFragments[1] = findFragment(NoneFragment3.class);
            }
            main1Layout.setVisibility(View.GONE);
            return;
        }
        if (Config.showAddressListMenu) {
            mFragments = new SupportFragment[2];
            SupportFragment firstFragment = findFragment(NoneFragment1.class);
            if (firstFragment == null) {
                mFragments[0] = new NoneFragment1();
                mFragments[1] = new NoneFragment2();

                loadMultipleRootFragment(R.id.fragment_container, 0,
                        mFragments[0],
                        mFragments[1]);
            } else {
                // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题
                // 这里我们需要拿到mFragments的引用
                mFragments[0] = firstFragment;
                mFragments[1] = findFragment(NoneFragment2.class);
            }
            main3.setVisibility(View.GONE);
            return;
        }
        mFragments = new SupportFragment[3];
        SupportFragment firstFragment = findFragment(NoneFragment1.class);
        if (firstFragment == null) {
            mFragments[0] = NoneFragment1.newInstance();
            mFragments[1] = new NoneFragment2();
            mFragments[2] = new NoneFragment3();

            loadMultipleRootFragment(R.id.fragment_container, 0,
                    mFragments[0],
                    mFragments[1],
                    mFragments[2]);
        } else {
            // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题
            // 这里我们需要拿到mFragments的引用
            mFragments[0] = firstFragment;
            mFragments[1] = findFragment(NoneFragment2.class);
            mFragments[2] = findFragment(NoneFragment3.class);
        }
    }


    @OnClick({R.id.main1, R.id.main2_layout, R.id.main3})
    public void clickBottomTab(View view) {
        switch (view.getId()) {
            case R.id.main1:
                showHideFragment(mFragments[0], mFragments[selectPosition]);
                selectPosition = 0;
                setButtom(0);
                break;
            case R.id.main2_layout:
                //不显示消息界面
                if (Config.showMessageMenu) {
                    showHideFragment(mFragments[0], mFragments[selectPosition]);
                    selectPosition = 0;
                } else {
                    showHideFragment(mFragments[1], mFragments[selectPosition]);
                    selectPosition = 1;
                }
                setButtom(1);
                break;
            case R.id.main3:
                //不显示消息界面
                if (Config.showMessageMenu) {
                    showHideFragment(mFragments[1], mFragments[selectPosition]);
                    selectPosition = 1;
                } else {
                    showHideFragment(mFragments[2], mFragments[selectPosition]);
                    selectPosition = 2;
                }
                setButtom(2);
                break;
        }
    }


    @OnClick({R.id.map_layout, R.id.user_number_layout, R.id.about_layout})
    public void menuClick(View view) {
        switch (view.getId()) {
            case R.id.map_layout:
                gotoActivity(DownMapActivity.class, false);
                break;
            case R.id.user_number_layout:
                gotoActivity(UserAccountActivity.class, false);
                break;
            case R.id.about_layout:
                gotoActivity(AboutActivity.class, false);
//                Bundle bundle = new Bundle();
//                bundle.putString("title", "标题");
//                bundle.putString("message", "分享内容");
//                bundle.putString("imageUrl", "");
//                bundle.putString("shareUrl", "https://www.jianshu.com/");
//                Intent intent = new Intent(this, SelectPersonForShareActivity.class);
//                intent.putExtras(bundle);
//                startActivity(intent);
                break;
        }
        drawerLayout.closeDrawers();
    }


    @OnClick(R.id.user_image)
    public void imageClick() {
        ActionSheet.showSheet(this, this, null);
    }


    /**
     * 设置底部按钮显示
     */
    private void setButtom(int position) {
        switch (position) {
            case 0:   // 消息
                main1.setSelectState(true);
                main3.setSelectState(false);
                main2Img.setImageResource(R.drawable.main2);
                main2Text.setTextColor(ContextCompat.getColor(this, R.color.edit_color));
                break;
            case 1:
                main1.setSelectState(false);
                main3.setSelectState(false);
                main2Img.setImageResource(R.drawable.main2_xz);
                main2Text.setTextColor(ContextCompat.getColor(this, R.color.blue_color));
                break;
            case 2:  // 通讯录
                main1.setSelectState(false);
                main3.setSelectState(true);
                main2Img.setImageResource(R.drawable.main2);
                main2Text.setTextColor(ContextCompat.getColor(this, R.color.edit_color));
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MainMenuEvent event) {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.openDrawer(GravityCompat.START);
        } else {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    /**
     * 双击退出程序
     */
    //记录用户首次点击返回键的时间
    private long firstTime = 0;

    //TODO:发现退出时，onDestroy未回调
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 2000) {
                    showToast("再按一次退出程序");
                    firstTime = secondTime;
                    return true;
                } else {
                    SynCountlyFactory.destorySynCountly();
                    AppManager.getAppManager().finishAllActivity();
                    System.exit(0);
                }
                break;

        }
        return super.onKeyUp(keyCode, event);
    }


    @Override
    public void onClick(int whichButton) {
        switch (whichButton) {
            case ActionSheet.CHOOSE_PICTURE:
                //相册
                goPhotoAlbum();
                break;
            case ActionSheet.TAKE_PICTURE:
                //拍照
                goCamera();
                break;
            case ActionSheet.CANCEL:
                //取消
                break;
        }
    }


    //获取权限
    private void getPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CALL_PHONE,
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.CAMERA,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.RECORD_AUDIO
                    }, 1);

        } else {
            if (Config.isOpenWaterMaker) {
                mPresenter.addWaterMaker();
            }
            initCrash();
        }
    }


    /**
     * 初始化崩溃日志收集上报
     */
    private void initCrash() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        CrashUtils.init(FileConfig.getLogFile(), (crashInfo, e) -> mPresenter.updateLog(crashInfo));
    }


    //激活相册操作
    private void goPhotoAlbum() {
        getPermission();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 2);
    }

    //激活相机操作
    private void goCamera() {
        getPermission();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(Objects.requireNonNull(this), "cn.synway.app.fileprovider", cameraSavePath);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(cameraSavePath);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, 1);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String photoPath;
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                photoPath = String.valueOf(cameraSavePath);
            } else {
                photoPath = uri.getEncodedPath();
            }
            Log.d("拍照返回图片路径:", photoPath);
            showProgress("");
            mPresenter.updateFile(new File(Objects.requireNonNull(photoPath)));
        } else if (requestCode == 2 && resultCode == RESULT_OK) {

            photoPath = PhotoFromPhotoAlbum.getRealPathFromUri(this, data.getData());
            showProgress("");
            mPresenter.updateFile(new File(photoPath));
        } else if (requestCode == INSTALL_PACKAGES_REQUESTCODE) {
            if (updateAPKFile != null) {
                AppUtils.installApp(updateAPKFile);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestError(String msg) {
        stopProgress();
        showToast(msg);
    }

    @Override
    public void upFileSourss(String url) {
        stopProgress();
        mPresenter.updateImage(url);
    }

    @Override
    public void updatePicSoress() {
        mPresenter.getUserInfo();
        EventBus.getDefault().post(new UpdatePicSoressEvent());
    }

    @Override
    public void getUserInfo(UserEntry userBO) {
        UserIml.addData(userBO);

        SynApplication.spUtils.put("userId", userBO.getUserID());
        initView();
    }

    @Override
    public void addWaterMaker(String water) {
        WaterMarkUtil.mWaterMarkDesc = water + "\n严禁数据泄露";
        WaterMarkUtil.getInstance().showWatermarkView(this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // 权限被用户同意。
                    // 执形我们想要的操作
                    if (Config.isOpenWaterMaker) {
                        mPresenter.addWaterMaker();
                    }
                    initCrash();
                } else {
                    // 权限被用户拒绝了。
                    //若是点击了拒绝和不再提醒
                    //关于shouldShowRequestPermissionRationale
                    // 1、当用户第一次被询问是否同意授权的时候，返回false
                    // 2、当之前用户被询问是否授权，点击了false,并且点击了不在询问（第一次询问不会出现“不再询问”的选项），
                    // 之后便会返回false
                    // 3、当用户被关闭了app的权限，该app不允许授权的时候，返回false
                    // 4、当用户上一次不同意授权，没有点击“不再询问”的时候，下一次返回true
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            || !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        //提示用户前往设置界面自己打开权限
                        Toast.makeText(this, "请前往设置界面打开权限", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
            break;
            case INSTALL_PACKAGES_REQUESTCODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (updateAPKFile != null) {
                        AppUtils.installApp(updateAPKFile);
                    }
                } else {
                    //Toast.makeText(this, "请前往设置界面打开权限", Toast.LENGTH_SHORT).show();
                    new AlertDialog(this).builder().setGone().setMsg("无安装权限,点击确认前往设置界面")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确定", v -> {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                                startActivityForResult(intent, INSTALL_PACKAGES_REQUESTCODE);
                            }).show();
                }
                break;
        }
    }
}
