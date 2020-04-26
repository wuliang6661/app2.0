package synway.module_publicaccount.public_chat;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import qyc.library.control.dialog_msg.DialogMsg;
import qyc.library.control.dialog_progress.DialogProgress;
import synway.common.ThreadPool;
import synway.module_interface.config.BaseUtil;
import synway.module_interface.config.userConfig.Sps_RWLoginUser;
import synway.module_interface.db.SQLite;
import synway.module_publicaccount.Main;
import synway.module_publicaccount.R;
import synway.module_publicaccount.db.table_util.Table_PublicAccount;
import synway.module_publicaccount.db.table_util.Table_PublicAccountRecord;
import synway.module_publicaccount.db.table_util.Table_PublicMenu;
import synway.module_publicaccount.public_chat.bottom.Base64Helper;
import synway.module_publicaccount.public_chat.bottom.PAChatBottomViewTools;
import synway.module_publicaccount.public_chat.ring.RingSettingAct;
import synway.module_publicaccount.public_message.PublicMessage;
import synway.module_publicaccount.push.NewMsgNotifyDeal;
import synway.module_publicaccount.push.Sps_Notify;
import synway.module_publicaccount.rtvideovoice.rtvoice.RTVoiceView;
import synway.module_publicaccount.until.PicCompress;
import synway.module_publicaccount.until.StringUtil;
import synway.module_publicaccount.weex_module.beans.WxMapData;

import static synway.module_publicaccount.publiclist.until.ShortCUtUntil.addShortcut2;
import static synway.module_publicaccount.publiclist.until.ShortCUtUntil.deleteShortCut;

//import synway.osc.lastcontact.open.SetLCForPublicAccount;   最近联系人  要和主模块交互   ？？？？


public class PublicAccountChatActNormal extends FragmentActivity {

    // ================一些全局
    private String loginUserID = null;
    private String publicAccountID = null;
    private String publicAccountName = null, MenuKry_Click = null, menukey = null, skiptype = null;//chatPublic公众号界面跳转，keyBoard快捷方式跳转


    // ================bottom View
    public PAChatBottomViewTools bottomViewTools = null;

    // =================titleView
    private PAChatTitleView titleView = null;

    private Dialog dialogWait = null;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<Fragment> fragmentList;
    private PublicChatPageFragmentAdapter pageFragmentAdapter;

    // ================【异模】
    private SyncStartRTMP syncStartRTMP = null;
    // TODO 这个听音控件在加了一个动画效果之后，它本身的动画会间歇性失效，我暂时还不知道为什么
    private RTVoiceView rtVoiceView = null;
    private String publicAccountInfo;

    //是否是公众号，而非一级二级菜单
    private boolean isPublicAccount = false;
    private Boolean ifEnter = true;
    private boolean isDestroyed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        publicAccountInfo = getIntent().getStringExtra("ACCOUNT_ID");
        publicAccountName = getIntent().getStringExtra("ACCOUNT_NAME");
        isPublicAccount = getIntent().getBooleanExtra("IS_PUBLIC_ACCOUNT", false);
//        if (publicAccountInfo.contains(",")) {//是菜单，不判断
//            publicAccountID = publicAccountInfo.split(",")[0];
//            if (!getPublicAccountData(publicAccountID)) {
//                deleteShortCut(this, publicAccountName);//删除快捷方式
//                setResult(111);
//                finish();
//                return;
//            }
//        } else {//是公众号
//            publicAccountID = publicAccountInfo;
//            if (!getPublicAccountData(publicAccountID)) {
//                deleteShortCut(this, publicAccountName);//删除快捷方式
//                setResult(111);
//                finish();
//                return;
//            }
//        }
        MenuKry_Click = getIntent().getStringExtra("MenuKry_Click");
        // 尝试建立通讯记录表
        SyncGetRecord.creatTable(publicAccountID);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.model_public_account_chat_act);

        clearTip();
        setMsgRead(publicAccountID);

        //获取当前公众号聊天界面显示分类的属性
        ArrayList<Obj_PushMsgPage> pushMsgPageList = getPushMsgPageList(publicAccountID);
        //===================测试代码============================================
//        if (pushMsgPageList != null && pushMsgPageList.size() != 0) {
//            for (Obj_PushMsgPage objPushMsgPage : pushMsgPageList) {
//                Log.d("dym------------------->", "type= " + objPushMsgPage.pushMsgPageCode + ",name= " +
//                    objPushMsgPage.pushMsgPageName);
//            }
//        } else {
//            Log.d("dym------------------->", "没有进行分类");
//        }
        //===================测试代码============================================
        loginUserID = Sps_RWLoginUser.readUserID(this);
        titleView = new PAChatTitleView(this);
        titleView.setTitle(publicAccountName);

        initUI(pushMsgPageList);

        Sps_Notify.setCurrent(this, publicAccountID);
        NewMsgNotifyDeal.dismissPushMsgAll(this);
        EventBus.getDefault().register(this);

        //处理关于从桌面快捷方式的跳转
        handleShortCutGoTo();
    }

    private void initUI(ArrayList<Obj_PushMsgPage> pushMsgPageList) {


        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewpager);

        if (pushMsgPageList == null || pushMsgPageList.size() == 0) {
            //无分类模式的
            Log.d("dym------------------->", "无分类模式");
            tabLayout.setVisibility(View.GONE);
            fragmentList = new ArrayList<>();
            PublicAccountChatFragment chatFragment = PublicAccountChatFragment.newInstance(publicAccountID,loginUserID,"", "");
            fragmentList.add(chatFragment);
            pageFragmentAdapter = new PublicChatPageFragmentAdapter(getSupportFragmentManager(),fragmentList,null);
            viewPager.setOffscreenPageLimit(fragmentList.size());
            viewPager.setAdapter(pageFragmentAdapter);

        } else {
            //分类模式的
            Log.d("dym------------------->", "分类模式");
            String names[] = new String[pushMsgPageList.size()];
            for (int i = 0; i < pushMsgPageList.size(); i++) {
                names[i] = pushMsgPageList.get(i).pushMsgPageName;
            }
            fragmentList = new ArrayList<>();
            //建议最多显示五个推送分类，这里不做限制
            for (int i = 0; i < pushMsgPageList.size(); i++) {
                Obj_PushMsgPage objPushMsgPage = pushMsgPageList.get(i);
                PublicAccountChatFragment chatFragment = PublicAccountChatFragment.newInstance(
                    publicAccountID,loginUserID,objPushMsgPage.pushMsgPageCode, objPushMsgPage.pushMsgPageName);
                fragmentList.add(chatFragment);
            }

            pageFragmentAdapter = new PublicChatPageFragmentAdapter(getSupportFragmentManager(),fragmentList,names);
            viewPager.setOffscreenPageLimit(fragmentList.size());
            viewPager.setAdapter(pageFragmentAdapter);
            tabLayout.setupWithViewPager(viewPager);

            if (fragmentList.size() > 1) {
                tabLayout.setVisibility(View.VISIBLE);
            } else {
                tabLayout.setVisibility(View.GONE);
            }

            if (pushMsgPageList.size() > 5) {
                tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            }


        }

        // init bottom
        bottomViewTools = new PAChatBottomViewTools(this, loginUserID, publicAccountID, publicAccountName);
        bottomViewTools.init();
        rtVoiceView = findViewById(R.id.rtVoiceView);
        rtVoiceView.init();
//        rtVoiceView.resetRTMPurl("rtmp://172.16.1.112:1935/vod/sample.mp4");
        rtVoiceView.setOnPlayListen(onPlayListen);
//
//        View addShortCut = findViewById(R.id.addShortCut);
//        if (isPublicAccount) {
//            //属于公众号类型，不属于菜单类型
//            addShortCut.setVisibility(View.VISIBLE);
//            addShortCut.setOnClickListener(addShortClickListener);
//        } else {
//            //菜单类型屏蔽按钮
//            addShortCut.setVisibility(View.GONE);
//        }
//        View changeRingUri = findViewById(R.id.rl_change_ringuri);
//        changeRingUri.setVisibility(View.VISIBLE);
//        changeRingUri.setOnClickListener(changeRingUriClickListener);
    }

    //
    private RTVoiceView.OnPlayListen onPlayListen = new RTVoiceView.OnPlayListen() {
        @Override
        public void onPlayCompletion() {
            Log.e("qsjh", "RTMP PLAY COMPLETE");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    rtVoiceView.playView_Reset_Hide();
                    rtVoiceView.setVisibility(View.GONE);
//                    Animation animation = AnimationUtils.loadAnimation(PublicAccountChatAct.this, R.anim.public_account_rtvoiceview_slide_out);
//                    animation.setFillAfter(true);
//                    rtVoiceView.startAnimation(animation);

                }
            });
        }

        @Override
        public void onError() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("qsjh", "RTMP PLAY ERROR");
                    rtVoiceView.playView_Reset_Hide();
                    rtVoiceView.setVisibility(View.GONE);
//                    Animation animation = AnimationUtils.loadAnimation(PublicAccountChatAct.this, R.anim.public_account_rtvoiceview_slide_out);
//                    animation.setFillAfter(true);
//                    rtVoiceView.startAnimation(animation);

                }
            });
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void prepareRTMP(RTMPEvent event) {
        if (syncStartRTMP != null) {
            syncStartRTMP.stop();
        }
        dialogWait = DialogProgress.get(this, "等待", "正在获取语音地址");
        dialogWait.show();
        syncStartRTMP = new SyncStartRTMP();
        syncStartRTMP.setOnStartRTMPResult(onStartRTMPResult);
        syncStartRTMP.start(event.postURL, event.rtmpURL, event.urlName);
    }

    /**
     * 激活实时语音RTMP结果监听
     */
    private SyncStartRTMP.OnStartRTMPResult onStartRTMPResult = new SyncStartRTMP.OnStartRTMPResult() {


        @Override
        public void onSuccess(String rtmpURL, String urlName) {
            // 激活RTMP成功，准备播放直播流
            if (dialogWait != null) {
                dialogWait.dismiss();
                dialogWait = null;
            }

            if (StringUtil.isEmpty(rtmpURL)) {
                return;
            }
//            Animation animation = AnimationUtils.loadAnimation(PublicAccountChatAct.this, R.anim.public_account_rtvoiceview_slide_in);
//            animation.setFillAfter(true);
//            rtVoiceView.startAnimation(animation);
            rtVoiceView.setAnimotion();
            rtVoiceView.playView_Show();
            rtVoiceView.setVisibility(View.VISIBLE);
            rtVoiceView.setText(urlName);
            rtVoiceView.resetRTMPurl(rtmpURL);
            Log.e("qsjh", "准备播放RTMP:" + rtmpURL);
            rtVoiceView.stop();
            rtVoiceView.play();
        }

        @Override
        public void onFail(String title, String reason, String detail) {
            if (dialogWait != null) {
                dialogWait.dismiss();
                dialogWait = null;
            }
            DialogMsg.showDetail(PublicAccountChatActNormal.this, title, reason, detail);
        }
    };
    @Override
    protected void onDestroy() {
        Log.d("dym------------------->", "PublicAccountChatActNormal onDestroy...");
        //需要在onDestroy方法中进一步检测是否回收资源等
        destroy();

        if (syncStartRTMP != null) {
            syncStartRTMP.stop();
        }
        if (bottomViewTools != null) {
            bottomViewTools.destory();
        }
        if (rtVoiceView != null) {
            rtVoiceView.destroy();
        }
        EventBus.getDefault().unregister(this);
        if (ifEnter) {
            clearTip();
        }
        super.onDestroy();

    }


    @Override protected void onStop() {
        super.onStop();
        Sps_Notify.clear(this);
    }


    public void clearTip() {
        // 清除 消息提醒
        // SQLiteDatabase db = null;
        try {
            // db = SQLiteDatabase.openDatabase(BaseUtil.getDBPath(this), null,
            // SQLiteDatabase.OPEN_READWRITE);
            SetLCForPublicAccount.clearUnReadCount(this,
                    Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase(),
                    publicAccountID);
            if (Main.instance().handlerLCTCount != null) {
                Main.instance().handlerLCTCount.updateLCTCount();
            }
//            Main.instance().newFragment().handlerTipCount.updateLCTCount();
        } catch (Exception e) {
            throw new RuntimeException("SyncGetRecordFromDB 打开数据库出错");
        } finally {
            // if (db != null) {
            // db.close();
            // }
        }
        //后续新增一个专门显示公众号最近消息的界面，也是需要清除的
        PublicMessage.clearUnReadCount(this,
            Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase(), publicAccountID);
    }

    private void setMsgRead(final String publicAccountID){

        if (TextUtils.isEmpty(publicAccountID)) {
            return;
        }
        ThreadPool.instance().execute(new Runnable() {
            @Override public void run() {
                // 尝试建立通讯记录表
                SyncGetRecord.creatTable(publicAccountID);

                long time1 = System.currentTimeMillis();
                Log.d("dym------------------->", "====================================");
                ContentValues cv = new ContentValues();
                cv.put(Table_PublicAccountRecord.publicRecord_col_isMsgRead, 1);
                SQLite.update(Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase(),
                    Table_PublicAccountRecord.getTableName(publicAccountID), cv,
                    Table_PublicAccountRecord.publicRecord_col_isMsgRead + "=?", new String[] { "0" });
                long time2 = System.currentTimeMillis();
                Log.d("dym------------------->", "setMsgRead 耗时："+(time2-time1));
                Log.d("dym------------------->", "====================================");
            }
        });
    }


    public Obj_Menu getObjMenuData(String menuId, String menuName) {
        Obj_Menu obj_menu = new Obj_Menu();
        String sql = "select * " + " from " + Table_PublicMenu._TABLE_NAME + " where " + Table_PublicMenu.PAM_menuGUID + "='" + menuId + "'";
        Cursor cursor = Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase().rawQuery(sql, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    obj_menu.ID = cursor.getString(cursor.getColumnIndex(Table_PublicMenu.PAM_ID));
                    obj_menu.menuGUID = menuId;
                    obj_menu.menuName = cursor.getString(cursor.getColumnIndex(Table_PublicMenu.PAM_menuName));
                    obj_menu.menuFather = cursor.getString(cursor.getColumnIndex(Table_PublicMenu.PAM_menuFather));
                    obj_menu.menuKey = cursor.getString(cursor.getColumnIndex(Table_PublicMenu.PAM_menuKey));
                    obj_menu.menuType = cursor.getInt(cursor.getColumnIndex(Table_PublicMenu.PAM_menuType));
                    obj_menu.menuUrl = cursor.getString(cursor.getColumnIndex(Table_PublicMenu.PAM_menuUrl));
                    obj_menu.menuUrlType = cursor.getInt(cursor.getColumnIndex(Table_PublicMenu.PAM_menuUrlType));
                    obj_menu.menuPicUrl = obj_menu.ID;
                }
                cursor.close();
                return obj_menu;
            } else {
//                Toast.makeText(getApplication(),"该公众平台应用不存在",Toast.LENGTH_SHORT).show();
                //删除快捷方式
                deleteShortCut(this, menuName);
                cursor.close();
                return null;
            }
        } else {
            //删除快捷方式
//            Toast.makeText(getApplication(),"该公众平台应用不存在",Toast.LENGTH_SHORT).show();
            deleteShortCut(this, menuName);
            return null;
        }
    }

    /**
     * 判断公众号是否被授权
     *
     * @param publicAccountID 公众号ID
     * @return 是否被授权
     */
    public Boolean getPublicAccountData(String publicAccountID) {
        String sql = "select * " + " from " + Table_PublicAccount._TABLE_NAME + " where " + Table_PublicAccount.FC_ID + "='" + publicAccountID + "'";
        Cursor cursor = Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase().rawQuery(sql, null);
        if (cursor != null) {
            return cursor.getCount() > 0;
        }
        return false;
    }
//
//    /***
//     * 没有登录信息则返回登录界面
//     */
//    public Boolean ifLoginSuccess() {
//
//        LoginUser loginUser = Sps_RWLoginUser.readUser(this);
//        if (loginUser == null || StringUtil.isEmpty(loginUser.ID)) {
//            try {
//                Main.instance().handlerActIntent.goLoginAct(this);
//            } catch (Exception e) {
//                Toast.makeText(getApplication(), "汇信未登录，请登录", Toast.LENGTH_SHORT).show();
//                e.printStackTrace();
//            }
//            finish();
//            ifEnter = false;
//            return false;
//        }
//        return true;
//    }
    private ArrayList<Obj_PushMsgPage> getPushMsgPageList(String publicAccountID){
        String sql = "select *  from " + Table_PublicAccount._TABLE_NAME + " where " + Table_PublicAccount.FC_ID + "='" + publicAccountID + "'";
        Cursor cursor = Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase().rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            String pushMsgTypeListStr = cursor.getString(
                cursor.getColumnIndex(Table_PublicAccount.PAM_PushMsgTypeList));

            try {
                JSONObject json = new JSONObject(pushMsgTypeListStr);
                JSONArray jsonArray = json.getJSONArray("msgType");
                ArrayList<Obj_PushMsgPage> list = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Obj_PushMsgPage objPushMsgType = new Obj_PushMsgPage();
                    String pushMsgCodeStr = jsonObject.getString("key");
                    //目前的实例:
                    // {"msgType":[{"value":"代办","key":"msgType0"},{"value":"已办理","key":"msgType1"},{"value":"正在处理","key":"msgType2"}]}
                    //这里的切分，后期可能需要修改的
                    if (pushMsgCodeStr.contains("msgType")) {
                        pushMsgCodeStr = pushMsgCodeStr.replace("msgType", "");
                    }
                    objPushMsgType.pushMsgPageCode = pushMsgCodeStr;
                    objPushMsgType.pushMsgPageName = jsonObject.getString("value");
                    list.add(objPushMsgType);
                }
                return list;
            } catch (Exception e) {
                Log.d("dym------------------->", "getPushMsgTypeList 出错: " + e.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }

    private View.OnClickListener addShortClickListener = new View.OnClickListener() {
        @Override public void onClick(View v) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ShortcutManager shortcutManager = (ShortcutManager) PublicAccountChatActNormal.this.getSystemService(Context.SHORTCUT_SERVICE);
                if (shortcutManager.isRequestPinShortcutSupported()) {
                    Intent launcherIntent= new Intent(getApplicationContext(), PublicAccountChatActNormal.class);
                    launcherIntent.setAction(Intent.ACTION_VIEW);
                    launcherIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    launcherIntent.putExtra("ACCOUNT_ID", publicAccountID);
                    launcherIntent.putExtra("ACCOUNT_NAME", publicAccountName);
                    Bitmap iconBitmap = BitmapFactory.decodeFile(BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + publicAccountID);
                    if (iconBitmap == null) {
                        iconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.publicaccountpic_default);
                    }
                    ShortcutInfo info = new ShortcutInfo.Builder(PublicAccountChatActNormal.this, publicAccountID)
                        .setIcon(Icon.createWithBitmap(iconBitmap))
                        .setShortLabel(publicAccountName)
                        .setIntent(launcherIntent)
                        .build();
                    //当添加快捷方式的确认弹框弹出来时，将被回调
                    PendingIntent shortcutCallbackIntent = PendingIntent.getBroadcast(PublicAccountChatActNormal.this, 0, new Intent(PublicAccountChatActNormal.this, ShortCutReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
                    shortcutManager.requestPinShortcut(info, shortcutCallbackIntent.getIntentSender());
                }
            } else {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                ComponentName cn = new ComponentName(
                    PublicAccountChatActNormal.this.getPackageName(),
                    "synway.module_publicaccount.public_chat.PublicAccountChatActNormal");
                Log.d("dym------------------->", "addShortCut ComponentName packageName= " + PublicAccountChatActNormal.this.getPackageName());
                intent.setComponent(cn);
                intent.putExtra("ACCOUNT_ID", publicAccountID);
                intent.putExtra("ACCOUNT_NAME", publicAccountName);
                String path = BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + publicAccountID;
                Bitmap bitmap = PicCompress.compressSize(path);
                addShortcut2(PublicAccountChatActNormal.this, publicAccountName,
                        bitmap, intent, false, 0);
            }
        }
    };
    private View.OnClickListener changeRingUriClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            startActivity(new Intent(PublicAccountChatActNormal.this,RingSettingAct.class).putExtra("ID",publicAccountInfo));
        }
    };
    private void handleShortCutGoTo(){
        if (MenuKry_Click != null) {
            //请求事件的菜单跳转
            if (MenuKry_Click.equals("click")) {
                Obj_Menu obj_Menu = (Obj_Menu) getIntent().getSerializableExtra("obj_Menu");
                if (obj_Menu == null) {
                    obj_Menu = getObjMenuData(publicAccountInfo.split(",")[1], publicAccountInfo.split(",")[2]);
                }
                if (obj_Menu != null) {
                    bottomViewTools.performMenuClick(obj_Menu);
                } else {
                    finish();
                }
            }
            //网页类型的菜单跳转
            else if (MenuKry_Click.equals("weburl")) {
                skiptype = getIntent().getStringExtra("skipType");
                Obj_Menu obj_Menu = (Obj_Menu) getIntent().getSerializableExtra("obj_Menu");
                if (obj_Menu == null) {
                    obj_Menu = getObjMenuData(publicAccountInfo.split(",")[1], publicAccountInfo.split(",")[2]);
                }
                if (obj_Menu != null) {
                    if (obj_Menu.menuUrlType == 1) {
                        String url = obj_Menu.menuUrl;
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        StringBuilder builder = new StringBuilder();
                        builder.append(url);
                        Uri uri = Uri.parse(builder.toString());
                        if (TextUtils.equals("http", uri.getScheme()) || TextUtils.equals("https", uri.getScheme())) {
                            intent.setData(uri);
                            intent.putExtra("Title", obj_Menu.menuName);
                            intent.putExtra("IsShowTitle",obj_Menu.isShowTitle);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.addCategory("com.SynwayOsc.android.intent.category.WEEX");
                            Map<String, Object> params = new HashMap<>();
                            params.put("SourceUrl", obj_Menu.sourceUrl);
                            params.put("UserId", loginUserID);
                            params.put("PaId", publicAccountID);
                            WxMapData map = new WxMapData();
                            map.setWxMapData(params);
                            intent.putExtra("DATA", map);
                            startActivity(intent);
                        }else {
                            Toast.makeText(PublicAccountChatActNormal.this, "跳转地址格式错误,请检查格式", Toast.LENGTH_LONG).show();
                        }

                        //                        startActivityForResult(intent, 0);
                    } else {
                        Intent intent = new Intent();
                        String name = Base64Helper.getBASE64(Sps_RWLoginUser.readUser(this).name);
                        String phoneNumber =Sps_RWLoginUser.readUserTelNumber(this);
                        //                        String ID = Sps_RWLoginUser.readUser(this).ID;
                        String url;
                        if(phoneNumber!=null){
                            url= obj_Menu.menuUrl + "?userName=" + name + "&phoneNumber=" + phoneNumber+"&userID=" + loginUserID;
                        }else {
                            url = obj_Menu.menuUrl + "?userName=" + name + "&currentTime=" + System.currentTimeMillis() + "&userID=" + loginUserID;
                        }
                        intent.putExtra("URL", url);
                        intent.putExtra("NAME", obj_Menu.menuName);
                        intent.putExtra("IsShowTitle",obj_Menu.isShowTitle);
                        intent.putExtra("URL_PARAM",obj_Menu.sourceUrl);
                        intent.setClass(this, PAWebViewAct.class);
                        // startActivity(intent);
                        startActivityForResult(intent, 0);
                    }
                } else {
                    finish();
                }
            }
        }
    }

    private void destroy(){
        if (isDestroyed) {
            return;
        }
        //回收资源.因为 onDestroy()执行的时机可能较晚,可根据实际需要这里可以执行一些耗时久的释放资源的工作

        if (ifEnter) {
            setMsgRead(publicAccountID);
        }
        isDestroyed = true;
    }


    @Override protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            destroy();
        }
    }
}