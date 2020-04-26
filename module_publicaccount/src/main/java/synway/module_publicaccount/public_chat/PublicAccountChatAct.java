//package synway.module_publicaccount.public_chat;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.view.Window;
//import android.widget.Toast;
//
//import org.simple.eventbus.EventBus;
//import org.simple.eventbus.Subscriber;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//
//import qyc.library.control.dialog_msg.DialogMsg;
//import qyc.library.control.dialog_progress.DialogProgress;
//import qyc.library.control.list_pulldown.ListPullDown;
//import qyc.library.control.list_pulldown.ListPullDown.OnPDListListener;
//import synway.module_interface.config.userConfig.LoginUser;
//import synway.module_interface.config.userConfig.Sps_RWLoginUser;
//import synway.module_publicaccount.Main;
//import synway.module_publicaccount.R;
//import synway.module_publicaccount.analytical.obj.Obj_PublicMsgBase;
//import synway.module_publicaccount.db.table_util.Table_PublicAccount;
//import synway.module_publicaccount.db.table_util.Table_PublicMenu;
//import synway.module_publicaccount.public_chat.SyncGetRecord.IOnGetRecordLsn;
//import synway.module_publicaccount.public_chat.adapter.PAChatAdapter;
//import synway.module_publicaccount.public_chat.bottom.Base64Helper;
//import synway.module_publicaccount.public_chat.bottom.PAChatShortBottomViewTools;
//import synway.module_publicaccount.public_chat.interfaces.UrlItemInterface;
//import synway.module_publicaccount.public_chat.interfaces.WxUrlInterface;
//import WxMapData;
//import Util;
//import synway.module_publicaccount.push.NewMsgNotifyDeal;
//import synway.module_publicaccount.push.PushUtil;
//import synway.module_publicaccount.push.Sps_Notify;
//import synway.module_publicaccount.rtvideovoice.rtvoice.RTVoiceView;
//import synway.module_publicaccount.until.ParseTime;
//import synway.module_publicaccount.until.StringUtil;
//
//import static synway.module_publicaccount.publiclist.until.ShortCUtUntil.deleteShortCut;
//
////import synway.osc.lastcontact.open.SetLCForPublicAccount;   最近联系人  要和主模块交互   ？？？？
//合并至PublicAccountChatActNormal
//
//public class PublicAccountChatAct extends Activity implements UrlItemInterface,WxUrlInterface {
//
//    private ListPullDown contentlistView = null;
//    private PAChatAdapter adapter = null;
//
//    // ================一些全局
//    private String loginUserID = null;
//    private String publicAccountID = null;
//    private String publicAccountName = null, MenuKry_Click = null, menukey = null, skiptype = null;//chatPublic公众号界面跳转，keyBoard快捷方式跳转
//
//    // ================广播
//    private NewMsgReceiver onNewMsgReceive = null;
//
//    // ================bottom View
//    public PAChatShortBottomViewTools bottomViewTools = null;
//
//    // =================titleView
//    private PAChatTitleView titleView = null;
//
//    private Dialog dialogWait = null;
//
//
//    // ================【异模】
//    private SyncGetRecord syncGetRecord = null;
//    private SyncStartRTMP syncStartRTMP = null;
//    // TODO 这个听音控件在加了一个动画效果之后，它本身的动画会间歇性失效，我暂时还不知道为什么
//    private RTVoiceView rtVoiceView = null;
//    private Boolean ifEnter = true;
//    private String wxSourceUrl;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
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
//            return;
//        }
//        String publicAccountInfo = getIntent().getStringExtra("ACCOUNT_ID");
//        publicAccountName = getIntent().getStringExtra("ACCOUNT_NAME");
//        if (publicAccountInfo.contains(",")) {//是菜单，不判断
//            publicAccountID = publicAccountInfo.split(",")[0];
//            if (!getPublicAccountData(publicAccountID)) {
//                deleteShortCut(this, publicAccountName);//删除快捷方式
//                // Toast.makeText(getApplicationContext(),"您已被取消该公众号授权，请联系管理员",Toast.LENGTH_SHORT).show();
//                finish();
//                return;
//            }
//        } else {//是公众号
//            publicAccountID = publicAccountInfo;
//            if (!getPublicAccountData(publicAccountID)) {
//                deleteShortCut(this, publicAccountName);//删除快捷方式
//                // Toast.makeText(getApplicationContext(),"您已被取消该公众号授权，请联系管理员",Toast.LENGTH_SHORT).show();
//                finish();
//                return;
//            }
//        }
//        MenuKry_Click = getIntent().getStringExtra("MenuKry_Click");
////        skiptype = getIntent().getStringExtra("skipType");
////        Obj_Menu testobj_Menu = (Obj_Menu) getIntent().getSerializableExtra("obj_Menu");
//////        menukey=getIntent().getStringExtra("menukey");
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.model_public_account_chat_act);
//        clearTip();
//        titleView = new PAChatTitleView(this);
//        titleView.setTitle(publicAccountName);
//        initUI();
//
//        // 尝试建立通讯记录表
//        SyncGetRecord.creatTable(this, publicAccountID);
//
//        syncGetRecord = new SyncGetRecord(this);
//        syncGetRecord.setLsn(onGetRecordLsn);
//
//        /** 预警公众号，模拟消息 start */
////        if("8e5fbc6d-4456-4ac0-b3a9-1b086ff64666".equals(publicAccountID)) {
////            TestJson test = new TestJson();
////            String json = test.getJson();
////            OnPublicNewMsgTest msg = OnPublicNewMsgTest.newInstance();
////            FacConfig facConfig = new FacConfig(null, "200");
////            msg.onCreat(facConfig);
////            msg.run5001(0, json);
////        }
//        /** 预警公众号，模拟消息 end */
//
//        onNewMsgReceive = new NewMsgReceiver();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(PushUtil.PublicNewMsg.getAction(publicAccountID));
//        // MLog.Log("liujie", PushUtil.PublicNewMsg.getAction(publicAccountID));
//        registerReceiver(onNewMsgReceive, filter);
//
//        contentlistView.startRefresh();
//        Sps_Notify.setCurrent(this, publicAccountID);
//        NewMsgNotifyDeal.dismissPushMsgAll(this);
//
//
//        EventBus.getDefault().register(this);
//        // TODO 左下角添加一个浮动按钮，点击进入实时位置公众号管理界面，界面支持批量对公众号进行加入和退出
//        if (MenuKry_Click != null) {
//            //请求事件的菜单跳转
//            if (MenuKry_Click.equals("click")) {
//                Obj_Menu obj_Menu = (Obj_Menu) getIntent().getSerializableExtra("obj_Menu");
//                if (obj_Menu == null) {
//                    obj_Menu = getObjMenuData(publicAccountInfo.split(",")[1], publicAccountInfo.split(",")[2]);
//                }
////              NetConfig netConfig = Sps_NetConfig.getNetConfigFromSpf(this);
//                if (obj_Menu != null) {
//                    bottomViewTools.performMenuClick(obj_Menu);
//                } else {
//                    finish();
//                }
//            }
//            //网页类型的菜单跳转
//            else if (MenuKry_Click.equals("weburl")) {
//                skiptype = getIntent().getStringExtra("skipType");
//                Obj_Menu obj_Menu = (Obj_Menu) getIntent().getSerializableExtra("obj_Menu");
//                if (obj_Menu == null) {
//                    obj_Menu = getObjMenuData(publicAccountInfo.split(",")[1], publicAccountInfo.split(",")[2]);
//                }
//
//                if (obj_Menu != null) {
////                    Toast.makeText(this,"menuUrlType = "+obj_Menu.menuUrlType,Toast.LENGTH_LONG).show();
//                    if(obj_Menu.menuUrlType ==1){
//                        String url=obj_Menu.menuUrl;
//                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                        StringBuilder builder = new StringBuilder();
//                        builder.append(url);
//                        Uri uri = Uri.parse(builder.toString());
//                        intent.setData(uri);
//                        intent.putExtra("Title",obj_Menu.menuName);
//                        intent.addCategory(Intent.CATEGORY_DEFAULT);
//                        intent.addCategory("com.SynwayOsc.android.intent.category.WEEX");
//                        Map<String, Object> params = new HashMap<>();
//                        params.put("SourceUrl", wxSourceUrl);
//                        params.put("UserId", loginUserID);
//                        params.put("PaId", publicAccountID);
//                        WxMapData map=new WxMapData();
//                        map.setWxMapData(params);
//                        intent.putExtra("DATA",map);
//                        startActivity(intent);
////                        startActivityForResult(intent, 0);
//                    }else {
//                        Intent intent = new Intent();
//                        String name = Base64Helper.getBASE64(Sps_RWLoginUser.readUser(this).name);
////                        String ID =Sps_RWLoginUser.readUser(this).ID;
//                        String phoneNumber =Sps_RWLoginUser.readUserTelNumber(this);
//                        String url;
//                        if(phoneNumber!=null){
//                            url= obj_Menu.menuUrl + "?userName=" + name + "&phoneNumber=" + phoneNumber+"&userID=" + loginUserID;
//                        }else {
//                            url = obj_Menu.menuUrl + "?userName=" + name + "&currentTime=" + System.currentTimeMillis() + "&userID=" + loginUserID;
//                        }
//                        intent.putExtra("URL", url);
//                        intent.putExtra("NAME", obj_Menu.menuName);
//                        intent.setClass(this, PAWebViewAct.class);
//                        // startActivity(intent);
//                       startActivityForResult(intent, 0);
//                    }
//                } else {
//                    finish();
//                }
//            }
//        }
//    }
//
//    private void initUI() {
//        adapter = new PAChatAdapter(this, publicAccountID, this);
//        adapter.setUrlItemListener(this);
//        contentlistView = (ListPullDown) findViewById(R.id.listView1);
//        contentlistView.loadingMoreView_IsEnabled(false);
//        contentlistView.setOnPDListen(onPDListListener);
//        contentlistView.setAdapter(adapter);
//        contentlistView.setDivider(null);
//        loginUserID = Sps_RWLoginUser.readUserID(this);
////        PublicAccountAdapter.ViewHolder viewHolder = null;
//        // init bottom
//        bottomViewTools = new PAChatShortBottomViewTools(this, loginUserID, publicAccountID, publicAccountName);
//        bottomViewTools.setWxUrlListener(this);
//        bottomViewTools.init();
//        rtVoiceView = (RTVoiceView) findViewById(R.id.rtVoiceView);
//        rtVoiceView.init();
////        rtVoiceView.resetRTMPurl("rtmp://172.16.1.112:1935/vod/sample.mp4");
//        rtVoiceView.setOnPlayListen(onPlayListen);
////        rtVoiceView.setAnimotion();
////        rtVoiceView.playView_Show();
////        rtVoiceView.setVisibility(View.VISIBLE);
////        Animation animation = AnimationUtils.loadAnimation(PublicAccountChatAct.this, R.anim.public_account_rtvoiceview_slide_in);
////        animation.setFillAfter(true);
////        rtVoiceView.startAnimation(animation);
////        rtVoiceView.setVisibility(View.VISIBLE);
//    }
//
//    //
//    private RTVoiceView.OnPlayListen onPlayListen = new RTVoiceView.OnPlayListen() {
//        @Override
//        public void onPlayCompletion() {
//            Log.e("qsjh", "RTMP PLAY COMPLETE");
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    rtVoiceView.playView_Reset_Hide();
//                    rtVoiceView.setVisibility(View.GONE);
////                    Animation animation = AnimationUtils.loadAnimation(PublicAccountChatAct.this, R.anim.public_account_rtvoiceview_slide_out);
////                    animation.setFillAfter(true);
////                    rtVoiceView.startAnimation(animation);
//
//                }
//            });
//        }
//
//        @Override
//        public void onError() {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.e("qsjh", "RTMP PLAY ERROR");
//                    rtVoiceView.playView_Reset_Hide();
//                    rtVoiceView.setVisibility(View.GONE);
////                    Animation animation = AnimationUtils.loadAnimation(PublicAccountChatAct.this, R.anim.public_account_rtvoiceview_slide_out);
////                    animation.setFillAfter(true);
////                    rtVoiceView.startAnimation(animation);
//
//                }
//            });
//        }
//    };
//
//    @Subscriber(tag = "PREPARE_RTMP")
//    public void prepareRTMP(RTMPEvent event) {
//        if (syncStartRTMP != null) {
//            syncStartRTMP.stop();
//        }
//        dialogWait = DialogProgress.get(this, "等待", "正在获取语音地址");
//        dialogWait.show();
//        syncStartRTMP = new SyncStartRTMP();
//        syncStartRTMP.setOnStartRTMPResult(onStartRTMPResult);
//        syncStartRTMP.start(event.postURL, event.rtmpURL, event.urlName);
//    }
//
//    /**
//     * 激活实时语音RTMP结果监听
//     */
//    private SyncStartRTMP.OnStartRTMPResult onStartRTMPResult = new SyncStartRTMP.OnStartRTMPResult() {
//
//
//        @Override
//        public void onSuccess(String rtmpURL, String urlName) {
//            // 激活RTMP成功，准备播放直播流
//            if (dialogWait != null) {
//                dialogWait.dismiss();
//                dialogWait = null;
//            }
//
//            if (StringUtil.isEmpty(rtmpURL)) {
//                return;
//            }
////            Animation animation = AnimationUtils.loadAnimation(PublicAccountChatAct.this, R.anim.public_account_rtvoiceview_slide_in);
////            animation.setFillAfter(true);
////            rtVoiceView.startAnimation(animation);
//            rtVoiceView.setAnimotion();
//            rtVoiceView.playView_Show();
//            rtVoiceView.setVisibility(View.VISIBLE);
//            rtVoiceView.setText(urlName);
//            rtVoiceView.resetRTMPurl(rtmpURL);
//            Log.e("qsjh", "准备播放RTMP:" + rtmpURL);
//            rtVoiceView.stop();
//            rtVoiceView.play();
//        }
//
//        @Override
//        public void onFail(String title, String reason, String detail) {
//            if (dialogWait != null) {
//                dialogWait.dismiss();
//                dialogWait = null;
//            }
//            DialogMsg.showDetail(PublicAccountChatAct.this, title, reason, detail);
//        }
//    };
//
//
//    /**
//     * 下拉刷新
//     */
//    private OnPDListListener onPDListListener = new OnPDListListener() {
//
//        @Override
//        public void onloadMore() {
//        }
//
//        @Override
//        public void onRefresh() {
//            int startIndex = adapter.getCount();
//            syncGetRecord.start(publicAccountID, startIndex);
//        }
//
//    };
//
//    /**
//     * 从数据库加载历史记录
//     */
//    private IOnGetRecordLsn onGetRecordLsn = new IOnGetRecordLsn() {
//        @Override
//        public void onResult(ArrayList<Obj_PublicMsgBase> arrayList) {
//            contentlistView.stopRefresh();
//            long now = System.currentTimeMillis();
//            // 排好顺序
//            // localSDFTime 赋值好
//            // 再整个放入adapter中
//            ArrayList<Obj_PublicMsgBase> filterList = new ArrayList<>();
//            boolean isFirst = true;
//            for (int i = 0; i < arrayList.size(); i++) {
//                Obj_PublicMsgBase obj = arrayList.get(i);
//                // 集合中的第一条记录肯定添加时间
//                if (isFirst) {
//                    obj.showSDFTime = ParseTime.parseDate(now, obj.localTime);
//                    filterList.add(obj);
//                    Log.e("zjw", "showtime:" + obj.showTime + "   localTime:"
//                            + obj.localTime + "   showSDFTime:"
//                            + obj.showSDFTime);
//                    isFirst = false;
//                    continue;
//                }
//
//                Obj_PublicMsgBase targetDisplay = null;
//                for (int k = filterList.size() - 1; k >= 0; k--) {
//                    if (filterList.get(k).showSDFTime != null) {
//                        targetDisplay = filterList.get(k);
//                        break;
//                    }
//                }
//
//                if (targetDisplay == null) {
//                    // 应该不会出现这种状况吧
//                    obj.showSDFTime = null;
//                    filterList.add(obj);
//                    return;
//                } else {
//                    obj.showSDFTime = ParseTime.getSDFLocalTime(now,
//                            obj.localTime, targetDisplay.localTime);
//                    filterList.add(obj);
//                }
//            }
//            adapter.addHistory(filterList);
//            // adapter.addItem(dataLines);
//            // Log.e("zjw", filterList.toString());
//            // adapter.addItem(dataLines);
//            adapter.notifyDataSetChanged();
//            contentlistView.setSelection(arrayList.size());
//        }
//
//        @Override
//        public void onFail() {
//            contentlistView.stopRefresh();
//        }
//    };
//
//    @Override
//    public void UrlItemClick(String url, String urlName,String data) {
//        if(!TextUtils.isEmpty(wxSourceUrl)){
////            url= Util.getResourceUrl(wxResourceUrl)+url;
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            StringBuilder builder = new StringBuilder();
//            builder.append(url);
//            Uri uri = Uri.parse(builder.toString());
//            intent.setData(uri);
//            intent.putExtra("Title",urlName);
//            intent.addCategory(Intent.CATEGORY_DEFAULT);
//            intent.addCategory("com.SynwayOsc.android.intent.category.WEEX");
//            Map<String, Object> params = new HashMap<>();
//            params.put("SourceUrl", wxSourceUrl);
//            params.put("UserId", loginUserID);
//            params.put("PaId", publicAccountID);
//            params.put("QueryData", data);
//            WxMapData map=new WxMapData();
//            map.setWxMapData(params);
//            intent.putExtra("DATA",map);
//            startActivity(intent);
//        }
//    }
//
//    @Override
//    public void getWxUrl( String sourceUrl) {
//        wxSourceUrl = sourceUrl;
//    }
//
//    /**
//     * 新的公众推送消息
//     */
//    private class NewMsgReceiver extends BroadcastReceiver {
//        long receiveLong;
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            receiveLong = System.currentTimeMillis();
//            // MLog.Log("liujie", "收到 公众号消息, 时间:" + receiveLong);
//
//            // String publicID = intent
//            // .getStringExtra(PushUtil.PublicNewMsg.EXTRA_PUBLIC_NEWMSG_GUID);
//            // int msgType = intent.getIntExtra(
//            // PushUtil.PublicNewMsg.EXTRA_PUBLIC_NEWMSG_MSGTYPE, 0);
//            // String time = intent
//            // .getStringExtra(PushUtil.PublicNewMsg.EXTRA_PUBLIC_NEWMSG_TIME);
//            Obj_PublicMsgBase obj = (Obj_PublicMsgBase) intent.getSerializableExtra(PushUtil.PublicNewMsg.EXTRA_PUBLIC_NEWMSG_SOBJ);
//            obj.MsgType = intent.getIntExtra(PushUtil.PublicNewMsg.EXTRA_PUBLIC_NEWMSG_MSGTYPE, 0);
//            adapter.addItem(obj);
//            adapter.notifyDataSetChanged();
//            // MLog.Log("liujie", "接收消息到展示 耗时： "
//            // + (System.currentTimeMillis() - receiveLong));
//        }
//
//    }
//
//    @Override
//    protected void onDestroy() {
//        if (syncGetRecord != null) {
//            syncGetRecord.stop();
//        }
//        if (syncStartRTMP != null) {
//            syncStartRTMP.stop();
//        }
//        if (bottomViewTools != null) {
//            bottomViewTools.destory();
//        }
//        if (contentlistView != null) {
//            contentlistView.removePDListen();
//        }
//        if (null != onNewMsgReceive) {
//            unregisterReceiver(onNewMsgReceive);
//        }
//
//        if (rtVoiceView != null) {
//            rtVoiceView.destroy();
//        }
//        EventBus.getDefault().unregister(this);
//        if (ifEnter) {
//            clearTip();
//        }
//        super.onDestroy();
//
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
////        if (requestCode == 0 && resultCode == 3) {
////            String backtype = data.getStringExtra("backtype");
////            if (backtype.equals("backkey")) {
////                finish();
////            }
////        }
//
//
//    }
//
//    public void clearTip() {
//        // 清除 消息提醒
//        // SQLiteDatabase db = null;
//        try {
//            // db = SQLiteDatabase.openDatabase(BaseUtil.getDBPath(this), null,
//            // SQLiteDatabase.OPEN_READWRITE);
//            SetLCForPublicAccount.clearUnReadCount(this, Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase(), publicAccountID);
//            if (Main.instance().handlerLCTCount != null) {
//                Main.instance().handlerLCTCount.updateLCTCount();
//            }
////            Main.instance().newFragment().handlerTipCount.updateLCTCount();
//        } catch (Exception e) {
//            throw new RuntimeException("SyncGetRecordFromDB 打开数据库出错");
//        } finally {
//            // if (db != null) {
//            // db.close();
//            // }
//        }
//    }
//
//    public Obj_Menu getObjMenuData(String menuId, String menuName) {
//        Obj_Menu obj_menu = new Obj_Menu();
//        String sql = "select * " + " from " + Table_PublicMenu._TABLE_NAME + " where " + Table_PublicMenu.PAM_menuGUID + "='" + menuId + "'";
//        Cursor cursor = Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase().rawQuery(sql, null);
//        if (cursor != null) {
//            if (cursor.getCount() > 0) {
//                while (cursor.moveToNext()) {
//                    obj_menu.ID = cursor.getString(cursor.getColumnIndex(Table_PublicMenu.PAM_ID));
//                    obj_menu.menuGUID = menuId;
//                    obj_menu.menuName = cursor.getString(cursor.getColumnIndex(Table_PublicMenu.PAM_menuName));
//                    obj_menu.menuFather = cursor.getString(cursor.getColumnIndex(Table_PublicMenu.PAM_menuFather));
//                    obj_menu.menuKey = cursor.getString(cursor.getColumnIndex(Table_PublicMenu.PAM_menuKey));
//                    obj_menu.menuType = cursor.getInt(cursor.getColumnIndex(Table_PublicMenu.PAM_menuType));
//                    obj_menu.menuUrl = cursor.getString(cursor.getColumnIndex(Table_PublicMenu.PAM_menuUrl));
//                    obj_menu.menuUrlType = cursor.getInt(cursor.getColumnIndex(Table_PublicMenu.PAM_menuUrlType));
//                    obj_menu.menuPicUrl = obj_menu.ID;
//                }
//                cursor.close();
//                return obj_menu;
//            } else {
////                Toast.makeText(getApplication(),"该公众平台应用不存在",Toast.LENGTH_SHORT).show();
//                //删除快捷方式
//                deleteShortCut(this, menuName);
//                cursor.close();
//                return null;
//            }
//        } else {
//            //删除快捷方式
////            Toast.makeText(getApplication(),"该公众平台应用不存在",Toast.LENGTH_SHORT).show();
//            deleteShortCut(this, menuName);
//            return null;
//        }
//    }
//
//    /**
//     * 判断公众号是否被授权
//     *
//     * @param publicAccountID 公众号ID
//     * @return 是否被授权
//     */
//    public Boolean getPublicAccountData(String publicAccountID) {
//        String sql = "select * " + " from " + Table_PublicAccount._TABLE_NAME + " where " + Table_PublicAccount.FC_ID + "='" + publicAccountID + "'";
//        Cursor cursor = Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase().rawQuery(sql, null);
//        if (cursor != null) {
//            if (cursor.getCount() > 0) {
//                cursor.close();
//                return true;
//            } else {
//                cursor.close();
//                return false;
//            }
//        }
//        return false;
//    }
//
//    /***
//     * 没有登录信息则返回登录界面
//     */
//    public Boolean ifLoginSuccess(LoginUser loginUser) {
//        if (loginUser == null || StringUtil.isEmpty(loginUser.ID)) {
//            try {
//                Main.instance().handlerActIntent.goLoginAct(this);
//            } catch (Exception e) {
//                Toast.makeText(getApplication(), "汇信未登录，请登录", Toast.LENGTH_SHORT).show();
//                e.printStackTrace();
//            }
//            return false;
//        }
//        return true;
//    }
//}