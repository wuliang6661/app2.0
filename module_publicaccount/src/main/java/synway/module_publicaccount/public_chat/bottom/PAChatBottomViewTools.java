package synway.module_publicaccount.public_chat.bottom;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import qyc.library.control.dialog_confirm.DialogConfirm;
import qyc.library.control.dialog_confirm.DialogConfirmCfg;
import qyc.library.control.dialog_confirm.OnDialogConfirmCancel;
import qyc.library.control.dialog_confirm.OnDialogConfirmClick;
import qyc.library.control.dialog_msg.DialogMsg;
import qyc.library.control.dialog_progress.DialogProgress;
import qyc.library.tool.http.HttpPost;
import synway.module_publicaccount.until.PicCompress;
import synway.common.upload.SynUpload;
import synway.module_interface.config.BaseUtil;
import synway.module_interface.config.netConfig.NetConfig;
import synway.module_interface.config.netConfig.Sps_NetConfig;
import synway.module_interface.config.userConfig.LoginUser;
import synway.module_interface.config.userConfig.Sps_RWLoginUser;
import synway.module_interface.db.SQLite;
import synway.module_interface.sharedate.SharedPreferencesQ;
import synway.module_publicaccount.Main;
import synway.module_publicaccount.R;
import synway.module_publicaccount.db.table_util.Table_PublicMenu;
import synway.module_publicaccount.public_chat.Obj_Menu;
import synway.module_publicaccount.public_chat.PAWebViewAct;
import synway.module_publicaccount.public_chat.PublicAccountChatActNormal;
import synway.module_publicaccount.public_chat.ShortCutReceiver;
import synway.module_publicaccount.public_chat.bluetooth.BluetoothReceiveFile;
import synway.module_publicaccount.public_chat.bluetooth.FileInfo;
import synway.module_publicaccount.public_chat.bluetooth.MReceiveAdapter;
import synway.module_publicaccount.public_chat.bottom.SynStep1.OnSynStepListen;
import synway.module_publicaccount.public_chat.bottom.SyncGetMenuByDB.IOnGetMenuByDB;
import synway.module_publicaccount.public_chat.bottom.SyncGetMenuList3.IOnGetMenuListLsn;
import synway.module_publicaccount.public_chat.bottom.SyncSendMenuLsn.IOnSendLsn;
import synway.module_publicaccount.public_chat.horizontal_listview.LinearListView;
import synway.module_publicaccount.public_favorite.PublicDBUtils;
import synway.module_publicaccount.public_favorite.PublicFavoriteFragment;
import synway.module_publicaccount.public_favorite.SynUploadAndSavePublicFav;
import synway.module_publicaccount.public_favorite.obj.Obj_PublicAccount_Favorite;
import synway.module_publicaccount.publiclist.PicidPublicidBean;
import synway.module_publicaccount.publiclist.SyncGetHeadThu;
import synway.module_publicaccount.until.DialogTool;
import synway.module_publicaccount.until.PublicAccountDialog;
import synway.module_publicaccount.until.StringUtil;
import synway.module_publicaccount.weex_module.beans.WxMapData;

import static synway.module_publicaccount.publiclist.until.ShortCUtUntil.addShortcut2;
import static synway.module_publicaccount.until.ConfigUtil.SENDIMRIURL;
import static synway.module_publicaccount.until.ConfigUtil.UPLOADFILEURL;
import static synway.module_publicaccount.until.ConfigUtil.getBlueToothPath;

public class PAChatBottomViewTools {

//    private Dialog dialog = null;
    private Context context = null;
    private Activity activity = null;
    private LayoutInflater inflater = null;
    // private View toast = null;

    // ================bottom View
    @SuppressWarnings("unused")
    private HorizontalScrollView h_ScrollView = null;
    private LinearListView h_ListView = null;
    private MenuAdapter menuAdapter = null;
    private ImageView iv_location = null;

    // ======================pop
    private PopupWindow popupWindow = null;
    private PopAdapter popAdapter = null;
    private final int defaultPopWidth = 100;
    private final int defaultPopHeight = 200;

    // ======================
    private SyncGetMenuList3 syncGetMenuList = null;
    private SyncSendMenuLsn syncSend = null;

    private SyncGetMenuByDB syncGetMenuByDB = null;
//    private SyncGetPaConfigByDB syncGetPaConfigByDB = null;
    //
    private NetConfig netConfig = null;
    private LoginUser loginUser = null;
    private String publicGUID = null;

    private String publicAccountName = null;

    private String loginUserID = null;
    private SyncGetHeadThu syncGetHeadThu;
    //===============================================

    /**
     * 常用应用的上传和入库
     */
    private SynUploadAndSavePublicFav synUploadAndSavePublicFav = null;
    /**
     * 记录长按item的position
     */
    private int longClickItemPosition = -1;
    /**
     * 判断是否为一级菜单，默认为false
     */
    private boolean isMenuFather = false;

    private Dialog uploadDialog = null;
    private BluetoothReceiveFile bluetoothReceiveFile2 = null;
    private ProgressDialog pgDown = null;
    private int proGress;
    private String fileName;
    private Dialog progressDialog;
    // 根据推送更新公众号
    private SharedPreferencesQ sharedPreferencesQ = null;


    public PAChatBottomViewTools(Activity act, String loginUserID, String publicGUID, String publicAccountName) {

        this.h_ScrollView = act
                .findViewById(R.id.horizontalScrollView1);
//        h_ScrollView.setVisibility(View.GONE);
//        WindowManager wm = (WindowManager)act.getSystemService(Context.WINDOW_SERVICE);
//        h_ScrollView.setLayoutParams(new HorizontalScrollView.LayoutParams(wm.getDefaultDisplay().getWidth(), wm.getDefaultDisplay().getHeight()/13));
        // toast l
        // this.toast = (View) act.findViewById(R.id.toast);
        iv_location = act.findViewById(R.id.iv_plus);
//        iv_location.setVisibility(View.INVISIBLE);
        iv_location.setOnClickListener(onClickListener2);
        // ImageView infoOperatingIV = (ImageView) act.findViewById(R.id.circle);
        Animation operatingAnim = AnimationUtils.loadAnimation(act, R.anim.tip);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        // infoOperatingIV.startAnimation(operatingAnim);

        this.inflater = LayoutInflater.from(act);
        this.menuAdapter = new MenuAdapter(act);

        this.h_ListView = act.findViewById(R.id.listView2);
        this.h_ListView.setOnItemClickListener(onItemClickListener);
        //增加h_ListView的长按事件,增添 "加入常用应用" 的功能
        this.h_ListView.setOnItemLongClickListener(onItemLongClickListener);
        this.h_ListView.setAdapter(menuAdapter);

        this.loginUser = Sps_RWLoginUser.readUser(act);
        this.publicGUID = publicGUID;
        this.netConfig = Sps_NetConfig.getNetConfigFromSpf(act);
        this.publicAccountName = publicAccountName;
        this.loginUserID = loginUserID;

        this.context = act;
        this.activity = act;
        this.popAdapter = new PopAdapter(context);

    }

    public void init() {
        setProcess();
        // DB
        this.syncGetMenuByDB = new SyncGetMenuByDB(context);
        this.syncGetMenuByDB.setLsn(iOnGetMenuByDB);
        this.syncGetMenuByDB.start(publicGUID);
//        this.syncGetPaConfigByDB=new SyncGetPaConfigByDB(context);
//        this.syncGetPaConfigByDB.setLsn(iOnGetPaConfigByDB);
//        this.syncGetPaConfigByDB.start(publicGUID);
        //Log.e("zjw", "start DB");
        //Log.e("zjw", "publicGUID:" + publicGUID);
        //
        this.syncSend = new SyncSendMenuLsn();
        this.syncSend.setLsn(sendReuslt);

        this.syncGetMenuList = new SyncGetMenuList3(publicGUID, context);
        this.syncGetMenuList.setLsn(onGetMenuListLsn);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(publicGUID);
//        this.dialog = DialogProgress.get(context, "", "");
//        this.dialog.show();

//        this.syncGetMenuList.start(netConfig.httpIP, netConfig.httpPort,
//                loginUser.ID, arrayList);

        this.synUploadAndSavePublicFav = new SynUploadAndSavePublicFav(context);
        this.synUploadAndSavePublicFav.setOnSynUploadAndSavePublicFavListener(onSynUploadListener);

        syncGetHeadThu = new SyncGetHeadThu(netConfig.publicServerIP, netConfig.publicServerPort, context);
        sharedPreferencesQ = new SharedPreferencesQ(activity, "isPublicUpdate");
        //监听推送是否需要更新公众号
        sharedPreferencesQ.addDataChangeListener(new SharedPreferencesQ.OnShardPreferencesQListener() {
            @Override
            public void onChanged(String name, boolean isSelf) {
                if(syncGetMenuList!=null&&sharedPreferencesQ.get("isPublicUpdate", true)){
                    syncGetMenuList.start(netConfig.publicServerIP, netConfig.publicServerPort,
                            loginUser.ID);
                }
            }
        });

    }

    private IOnGetMenuByDB iOnGetMenuByDB = new IOnGetMenuByDB() {

        @Override
        public void onResult(ArrayList<Obj_Menu> arrayList, ArrayList<Obj_Menu> secondMenuList) {
//            menuAdapter.setSecondList(secondMenuList);
            menuAdapter.reset(arrayList);
            menuAdapter.refresh();
            //Log.e("zjw", "query_sqlite_succeed");
            Log.d("dym------------------->", "iOnGetMenuByDB ..");
        }

        @Override
        public void onFail(String title, String reason, String detail) {
            //Log.e("zjw", title + reason + detail);
        }
    };

//    private View.OnClickListener onClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            if (v == iv_location) {
//                Intent intent = new Intent();
//
//                // 判断自己现在是否已开启位置共享
////                String locationID = GetMineLocationID.getLocationingID(context);
////				if (StringUtil.isEmpty(locationID)) {
////					Toast.makeText(context, "你尚未开启位置共享", Toast.LENGTH_SHORT)
////							.show();
////					return;
////				}
//
//                // 加入关注列表
//                SQLIteHelp sqliteHelp = Main.instance().moduleHandle.getSQLiteHelp();
//                try {
//                    if (!PublicAccountFocusedList.exist(sqliteHelp.getWritableDatabase(), publicGUID)) {
//                        PublicAccountFocusedList.addToDB(
//                                sqliteHelp.getWritableDatabase(), publicGUID);
//                    }
//                } catch (Exception e) {
//                    ThrowExp.throwRxp("Sync Get PublicAccount Http ");
//                }
//
//                // 开启地图界面
//                String[] uploadInfo = SpfUploadInfo.getUploadInfo(context);
//                String locationID;
//                String targetID;
//                int targetType;
//                if (uploadInfo == null) {
//                    locationID = "";
//                    targetID = "";
//                    targetType = 0;
//                } else {
//                    targetID = uploadInfo[1];
//                    targetType = Integer.valueOf(uploadInfo[2]);
//                    locationID = GetMineLocationID.getLocationingID(context);
//                }
//                intent.putExtra("LOCATION_ID", locationID);
//                intent.putExtra("TARGET_ID", targetID);
//                intent.putExtra("TARGET_TYPE", targetType);
//                intent.setClass(context, LocationAct.class);
//                context.startActivity(intent);
//            }
//        }
//    };

    private View.OnClickListener onClickListener2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == iv_location) {
//                Intent intent = new Intent();
//                intent.putExtra("publicGUID", publicGUID);
//                intent.setClass(context, RTGISAct.class);
//                context.startActivity(intent);
//                Intent intent = new Intent(context, NewVideoPlayActivity.class);
//                intent.putExtra("video_url", "");
//                intent.putExtra("video_url_local", Environment.getExternalStorageDirectory().getPath()+"/fighting.mp4");
//                context.startActivity(intent);
                Main.instance().handlerRTGIS.goRTGISAct(activity, publicGUID);
            }
        }
    };

    @SuppressWarnings("deprecation")
    private void showPop(PopAdapter popAdapter) {
        if (null != popupWindow) {
            popupWindow.dismiss();
            return;
        }
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int everywidth = width / (menuAdapter.getCount());
        View view = inflater.inflate(R.layout.model_public_account_chat_menu_pop, null);
        popupWindow = new PopupWindow(view, everywidth,
                dip2px(context, defaultPopHeight), true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // popwindow
        popupWindow.setOutsideTouchable(true);
        //添加pop窗口关闭事件
        popupWindow.setOnDismissListener(new poponDismissListener());
        // ^o 1/4
        popupWindow.setTouchInterceptor(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    popupWindow.dismiss();
                    return true;
                }
                return false;
            }
        });

        ListView popListview = view.findViewById(R.id.listView1);
        popListview.setOnItemClickListener(popItemClick);
        //增加二级菜单的长按事件
        popListview.setOnItemLongClickListener(popItemLongClick);

        popListview.setAdapter(popAdapter);
    }

    private IOnGetMenuListLsn onGetMenuListLsn = new IOnGetMenuListLsn() {

        @Override
        public void onResult(ArrayList<Obj_Menu> arrayList) {
//            if (dialog.isShowing()) {
//                dialog.dismiss();
//            }

            if (arrayList == null || arrayList.size() == 0) {
//                menuAdapter.setSecondList(new ArrayList<Obj_Menu>());
                menuAdapter.reset(new ArrayList<Obj_Menu>());
                menuAdapter.refresh();
//                clearDBfromGUID();
                return;
            }
//            menuAdapter.setSecondList(secondMenuList);
            menuAdapter.reset(arrayList);
            menuAdapter.refresh();
//            DBDeal(arrayList);
//
//            downMenuIcon(arrayList);
        }

        @Override
        public void onFail(String title, String reason, String detail) {
//            if (dialog.isShowing()) {
//                dialog.dismiss();
//            }
            PublicAccountDialog.show(context, title, reason, detail);
//            DialogMsg.showDetail(context, title, reason, detail);
        }
    };

    private void clearDBfromGUID() {
        SQLiteOpenHelper sqliteHelp = Main.instance().moduleHandle.getSQLiteHelp();
        SQLite.del(sqliteHelp.getWritableDatabase(),
                Table_PublicMenu._TABLE_NAME, "ID=?",
                new String[]{publicGUID});
    }

    private void DBDeal(ArrayList<Obj_Menu> arrayList) {
        // SQLiteDatabase db = null;
        // try {
        // db = SQLiteDatabase.openDatabase(
        // BaseUtil.getDBPath(MainApp.getInstance()), null,
        // SQLiteDatabase.OPEN_READWRITE);
        // } catch (Exception e) {
        // ThrowExp.throwRxp("Sync Get PublicAccount Http ");
        // }
        SQLiteOpenHelper sqliteHelp = Main.instance().moduleHandle.getSQLiteHelp();

        //先对比和服务器的图片ID是否一样，如果不一样，就把图片直接删除，如果一样，就不删除
        String col = Table_PublicMenu.PAM_menuGUID + "," + Table_PublicMenu.PAM_menuPicId;
        String sql = "select " + col + " from " + Table_PublicMenu._TABLE_NAME + " where " + Table_PublicMenu.PAM_ID + "=" + "'" + publicGUID + "'";
        Cursor cursor = sqliteHelp.getWritableDatabase().rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                StringBuffer sb = new StringBuffer();
                String menuid = cursor.getString(cursor.getColumnIndex(Table_PublicMenu.PAM_menuGUID));
                String picid = cursor.getString(cursor.getColumnIndex(Table_PublicMenu.PAM_menuPicId));
                if (arrayList.size() > 0) {
                    for (Obj_Menu firstmenu : arrayList) {
                        if (firstmenu.menuGUID.equals(menuid)) {
                            //如果本来是空的，就不用删除
                            if (StringUtil.isNotEmpty(picid)) {
                                if (!picid.equals(firstmenu.menuPicUrl)) {//如果图片名称相同的话，就不删除，如果不相同。就删除本地的图片
                                    File filePath = new File(BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + (menuid));
                                    if (filePath.exists()) {//如果存在该公众号ID的头像，先删除
                                        filePath.delete();
                                    }
                                }
                            }
                        }
                    }
                }
//                if (secondMenuList.size() > 0) {
//                    for (Obj_Menu secondtmenu : secondMenuList) {
//                        if (secondtmenu.menuGUID.equals(menuid)) {
//                            if (StringUtil.isNotEmpty(picid)) {
//                                if (!picid.equals(secondtmenu.menuPicUrl)) {//如果图片名称相同的话，就不删除，如果不相同。就删除本地的图片
//                                    File filePath = new File(BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + (secondtmenu.menuGUID));
//                                    if (filePath.exists()) {//如果存在该公众号ID的头像，先删除
//                                        filePath.delete();
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
            }
        }
        // 清空该公众号对应的菜单表
        SQLite.del(sqliteHelp.getWritableDatabase(), Table_PublicMenu._TABLE_NAME, "ID=?", new String[]{publicGUID});
        ArrayList<ContentValues> contentValues = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            ContentValues cv = new ContentValues();
            cv.put(Table_PublicMenu.PAM_ID, arrayList.get(i).ID);
            cv.put(Table_PublicMenu.PAM_menuName, arrayList.get(i).menuName);
            cv.put(Table_PublicMenu.PAM_menuFather, arrayList.get(i).menuFather);
            cv.put(Table_PublicMenu.PAM_menuGUID, arrayList.get(i).menuGUID);
            cv.put(Table_PublicMenu.PAM_menuKey, arrayList.get(i).menuKey);
            cv.put(Table_PublicMenu.PAM_menuType, arrayList.get(i).menuType);
            cv.put(Table_PublicMenu.PAM_menuUrl, arrayList.get(i).menuUrl);
            cv.put(Table_PublicMenu.PAM_menuPicId, arrayList.get(i).menuPicUrl);
            cv.put(Table_PublicMenu.PAM_menuUrlType, arrayList.get(i).menuUrlType);
            cv.put(Table_PublicMenu.PAC_urlParam, arrayList.get(i).sourceUrl);
            cv.put(Table_PublicMenu.PAM_ISSHowTitle, arrayList.get(i).isShowTitle);
            contentValues.add(cv);
        }
//        for (int i = 0; i < secondMenuList.size(); i++) {
//            ContentValues cv = new ContentValues();
//            cv.put(Table_PublicMenu.PAM_ID, secondMenuList.get(i).ID);
//            cv.put(Table_PublicMenu.PAM_menuName,
//                    secondMenuList.get(i).menuName);
//            cv.put(Table_PublicMenu.PAM_menuFather,
//                    secondMenuList.get(i).menuFather);
//            cv.put(Table_PublicMenu.PAM_menuGUID,
//                    secondMenuList.get(i).menuGUID);
//            cv.put(Table_PublicMenu.PAM_menuKey, secondMenuList.get(i).menuKey);
//            cv.put(Table_PublicMenu.PAM_menuType,
//                    secondMenuList.get(i).menuType);
//            cv.put(Table_PublicMenu.PAM_menuUrl, secondMenuList.get(i).menuUrl);
//            cv.put(Table_PublicMenu.PAM_menuPicId, secondMenuList.get(i).menuPicUrl);
//            cv.put(Table_PublicMenu.PAM_menuUrlType, secondMenuList.get(i).menuUrlType);
//            contentValues.add(cv);
//        }
        SQLite.inserts(sqliteHelp.getWritableDatabase(),
                Table_PublicMenu._TABLE_NAME, contentValues);

        // db.close();
    }

    private IOnSendLsn sendReuslt = new IOnSendLsn() {

        @Override
        public void onResult() {
            // toast.setVisibility(View.GONE);

        }

        @Override
        public void onFail(String title, String reason, String detail) {
//            DialogMsg.showDetail(context, title, reason, detail);
            PublicAccountDialog.show(context, title, reason, detail);
            // 5
            // syn1 = new SynStep1();
            // syn1.setonSynStepListen(onSynStepListen);
            // syn1.start();
            // toast.setVisibility(View.GONE);
            // Thread thread1 = new Thread(new Runnable(){
            // public void run(){
            //
            // try {
            // Thread.sleep(5000);
            // } catch (InterruptedException e) {
            // e.printStackTrace();
            // }
            //
            // toast.setVisibility(View.GONE); //
            //
            // }
            //
            // });
            // thread1.start();

        }
    };
    //
    OnSynStepListen onSynStepListen = new OnSynStepListen() {

        @Override
        public void onResult() {
            // toast.setVisibility(View.GONE);
        }

        @Override
        public void onFail() {
            // toast.setVisibility(View.GONE);
        }
    };


    private boolean isSavePublicFavorite(String publicAccountID, String menuName) {
        boolean result = false;

        List<Obj_PublicAccount_Favorite> favorites = PublicDBUtils.read();
        if (favorites != null) {

            if (favorites.size() == 0) {
                //表示常用应用个数为0,也返回false
                result = false;
            } else {

                for (int i = 0; i < favorites.size(); i++) {
                    if (publicAccountID.equals(favorites.get(i).ID) && menuName.equals(favorites.get(i).menuName)) {
                        result = true;
                        break;
                    }
                }
            }

        }

        return result;
    }

    private LinearListView.OnItemLongClickListener onItemLongClickListener = new LinearListView.OnItemLongClickListener() {
        @Override
        public void onItemLongClick(LinearListView parent, View view, int position, long id) {
            longClickItemPosition = position;
            isMenuFather = true;
            Obj_Menu obj_Menu = (Obj_Menu) menuAdapter.getItem(position);
            final String menuName = obj_Menu.menuName;
            final String menuUrl = obj_Menu.menuUrl;
            final String menupicurl = obj_Menu.menuPicUrl;
            final String menuid = obj_Menu.menuGUID;
            final String accountid = obj_Menu.ID;
            if (menuName.equals(context.getString(R.string.receiveBlueToothFile))) {
                // Toast.makeText(context, "暂时不支持加入常用应用", Toast.LENGTH_SHORT).show();
                return;
            }
            if (obj_Menu.menuType == 1) {

                Window window = DialogTool.dialog(activity, R.layout.model_public_dialog);
                LinearLayout uploadIn = window.findViewById(R.id.uploadIntellgence);
                uploadIn.setVisibility(View.GONE);
                LinearLayout addNormalApplication = window.findViewById(R.id.addNormalApplication);
                addNormalApplication.setTag(obj_Menu);
                addNormalApplication.setOnClickListener(addNormalAppOnclick);
                // addNormalApplication.setVisibility(View.VISIBLE);
                addNormalApplication.setVisibility(View.GONE);
                LinearLayout addToDeskTop = window.findViewById(R.id.addToDeskTop);
                addToDeskTop.setOnClickListener(addToDesktop);
                addToDeskTop.setTag(obj_Menu);
                addToDeskTop.setVisibility(View.VISIBLE);


//                //判断是否已经加入了常用应用
//                boolean isSavePublicFavorite = isSavePublicFavorite(publicGUID, menuName);
//                if (isSavePublicFavorite) {
//                    Toast.makeText(context, "请不要重复加入常用公众号", Toast.LENGTH_SHORT).show();
//                } else {
//                    DialogConfirm.show(context, "提示", "是否加入常用应用？",
//                            new OnDialogConfirmClick() {
//                                @Override
//                                public void onDialogConfirmClick() {
//                                    Obj_PublicAccount_Favorite favorite = new Obj_PublicAccount_Favorite();
//                                    favorite.ID = accountid;
//                                    favorite.Name = publicAccountName;
//                                    favorite.isHtml = true;
//                                    favorite.menuName = menuName;
//                                    favorite.menuUrl = menuUrl;
//                                    favorite.fc_mobilepic = menupicurl;
//                                    favorite.MenuId = menuid;
//                                    //开始进行上传和入库
//                                    synUploadAndSavePublicFav.start(favorite, loginUserID);
//
//                                }
//                            }, new OnDialogConfirmCancel() {
//                                @Override
//                                public void onDialogConfirmCancel() {
//
//                                }
//                            });
//                }


            } else {
                // Toast.makeText(context, "暂时不支持加入常用应用", Toast.LENGTH_SHORT).show();
            }

        }
    };


    private LinearListView.OnItemClickListener onItemClickListener = new LinearListView.OnItemClickListener() {
        @Override
        public void onItemClick(LinearListView parent, View view, int position,
                                long id) {
            // ~o menu б
            Obj_Menu obj_Menu = (Obj_Menu) menuAdapter.getItem(position);
            if (obj_Menu.menuName.equals(context.getString(R.string.receiveBlueToothFile))) {
                //开启蓝牙
                //启动修改蓝牙可见性的Intent
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                //设置蓝牙可见性的时间，方法本身规定最多可见300秒
                intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                context.startActivity(intent);
                MReceiveAdapter mReceiveAdapter = new MReceiveAdapter(onProgress, getBlueToothPath());
                bluetoothReceiveFile2 = new BluetoothReceiveFile(mReceiveAdapter);
                bluetoothReceiveFile2.start();
                Toast.makeText(context.getApplicationContext(), "蓝牙文件接收系统已开启", Toast.LENGTH_SHORT).show();
                return;
            }
            if (obj_Menu.menuType == 0) {
                ArrayList<Obj_Menu> arrayList = menuAdapter
                        .getSecondMenu(obj_Menu.menuGUID);
                if (arrayList.size() == 0) {
                    Toast.makeText(context, "κt", Toast.LENGTH_SHORT).show();
                    return;
                }

                popAdapter.reset(arrayList);
                showPop(popAdapter);
                backgroundAlpha(0.9f);
                // С5^opopupWindow
                if (arrayList.size() < 5) {
                    popupWindow.setHeight(dip2px(context, defaultPopHeight)
                            * arrayList.size() / 5 + 10);// +105ListViewL
                }

                // y "+3, "
                popupWindow.showAsDropDown(view, 0, dip2px(context, 3));
            }
            // ^u
            performMenuClick(obj_Menu);

        }
    };


    /**
     * 将Obj_PublicAccount_Favorite转换为json数据
     *
     * @param favorite 常用公众平台应用
     */
    private String tranferPublicFavObjToJsonString(Obj_PublicAccount_Favorite favorite) {
        String returnStr = "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ID", favorite.ID);
            jsonObject.put("Name", favorite.Name);
            jsonObject.put("menuName", favorite.menuName);
            jsonObject.put("menuUrl", favorite.menuUrl);
            jsonObject.put("isHtml", favorite.isHtml);
            jsonObject.put("fc_mobilepic", favorite.fc_mobilepic);
            jsonObject.put("MenuId", favorite.MenuId);
            returnStr = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return returnStr;

    }

    private AdapterView.OnItemLongClickListener popItemLongClick = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            longClickItemPosition = position;
            isMenuFather = false;
            Obj_Menu obj_Menu = (Obj_Menu) popAdapter.getItem(position);
            //判断是否为网页跳转类
            if (obj_Menu.menuType == 1) {
                Window window = DialogTool.dialog(activity, R.layout.model_public_dialog);
                LinearLayout uploadIn = window.findViewById(R.id.uploadIntellgence);
                uploadIn.setVisibility(View.GONE);
                LinearLayout addNormalApplication = window.findViewById(R.id.addNormalApplication);
                addNormalApplication.setTag(obj_Menu);
                addNormalApplication.setOnClickListener(addNormalAppOnclick);
                // addNormalApplication.setVisibility(View.VISIBLE);
                addNormalApplication.setVisibility(View.GONE);
                LinearLayout addToDeskTop = window.findViewById(R.id.addToDeskTop);
                addToDeskTop.setOnClickListener(addToDesktop);
                addToDeskTop.setTag(obj_Menu);
                addToDeskTop.setVisibility(View.VISIBLE);
            } else {
                // Toast.makeText(context, "暂时不支持加入常用应用", Toast.LENGTH_SHORT).show();
            }

            popupWindow.dismiss();

            return true;
        }
    };

    private AdapterView.OnItemClickListener popItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Obj_Menu obj_Menu = (Obj_Menu) popAdapter.getItem(position);
            // url
            // || !"".equals(obj_Menu.menuUrl) || "null".e
            performMenuClick(obj_Menu);
            popupWindow.dismiss();
        }
    };

    /**
     * 菜单点击事件
     *
     * @param obj_Menu 菜单实体类
     */
    public void performMenuClick(Obj_Menu obj_Menu) {
        if (obj_Menu.menuType == 1) {
            if (obj_Menu.menuUrl == null) {
                return;
            }
            //改成先打开公众号页面再打开网页
//                Intent intent = new Intent(context, PublicAccountChatActNormal.class);
//                Bundle mBundle = new Bundle();
//                mBundle.putSerializable("obj_Menu", obj_Menu);
//                intent.putExtra("ACCOUNT_ID", publicGUID);
//                intent.putExtra("ACCOUNT_NAME", publicAccountName);
//                intent.putExtra("MenuKry_Click", "weburl");
//                intent.putExtra("skipType", "chatPublic");
//                intent.putExtras(mBundle);
//                context.startActivity(intent);
            //直接跳转Weex页面或H5页面
            if (obj_Menu.menuUrlType == 1) {
                // 新：weex 界面跳转
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
                    params.put("PaId", publicGUID);
                    WxMapData map = new WxMapData();
                    map.setWxMapData(params);
                    intent.putExtra("DATA", map);
                    context.startActivity(intent);
//                    FileTestLog.write("Weex页面跳转","SourceUrl:"+obj_Menu.sourceUrl+"UserId:"+loginUserID+"PaId"+publicGUID);
                }else {
                    Toast.makeText(context, "跳转地址格式错误,请检查格式", Toast.LENGTH_LONG).show();
                }

            } else {
                // 兼容旧的页面跳转
                Intent intent = new Intent();
                String name = Base64Helper.getBASE64(Sps_RWLoginUser.readUser(context).name);
//                 name = "6ZKx5a6H5aWH==";
                String ID = Sps_RWLoginUser.readUser(context).ID;
                String url = obj_Menu.menuUrl + "?userName=" + name + "&currentTime=" + System.currentTimeMillis() + "&userID=" + ID;
                intent.putExtra("URL", url);
                intent.putExtra("NAME", obj_Menu.menuName);
                intent.putExtra("IsShowTitle",obj_Menu.isShowTitle);
                intent.putExtra("URL_PARAM",obj_Menu.sourceUrl);
                intent.setClass(context, PAWebViewAct.class);
                context.startActivity(intent);
            }

        } else if (obj_Menu.menuType == 2) {
            // menu 1/4 http
            syncSend.start(netConfig.httpIP, netConfig.httpPort, loginUser.ID,
                    publicGUID, obj_Menu.menuKey);
            // toast.setVisibility(View.VISIBLE);

        }
    }

    /**
     * k dp ^jλ px()
     */
    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    private SynUploadAndSavePublicFav.OnSynUploadAndSavePublicFavListener onSynUploadListener = new SynUploadAndSavePublicFav.OnSynUploadAndSavePublicFavListener() {
        @Override
        public void onStart(Boolean ifstart) {

            if (uploadDialog == null) {
                uploadDialog = DialogProgress.get(context, "提示", "正在加入常用应用");
            }
            uploadDialog.show();
        }


        @Override
        public void onSingleSuccess(String msg, Obj_PublicAccount_Favorite favorite) {
            if (uploadDialog != null) {
                if (uploadDialog.isShowing()) {
                    uploadDialog.dismiss();
                }
            }

//            Toast.makeText(context, "加入常用公众号成功",
//                Toast.LENGTH_SHORT).show();

            //发广播，更新UI
            Intent intent = new Intent();
            intent.setAction(PublicFavoriteFragment.Broad_Cast_Action);
//            String jsonString = tranferPublicFavObjToJsonString(favorite);
//            intent.putExtra(PublicFavoriteFragment.Obj_PublicFavorite,
//                    jsonString);
            intent.putExtra(PublicFavoriteFragment.Obj_PublicFavorite, favorite);
            context.sendBroadcast(intent);
        }


        @Override
        public void onListSuccess(String msg) {

        }


        @Override
        public void onFail(String reason, Boolean aBoolean) {
            if (uploadDialog != null) {
                if (uploadDialog.isShowing()) {
                    uploadDialog.dismiss();
                }
            }
//             为什么用添加这个"数据库错误"判断呢？是因为我们用到CustomConfigRW.write(),上传数据库的时候由于
//              之前数据库ConfigTable表设置value的字段不够长，现在改为text,但是外面的服务端可能数据库还没有及时
//             *升级,所以在此进行提醒
            if (reason.contains("数据库错误")) {
                DialogMsg.show(context, "提示", "服务端数据库版本较旧,不支持加入常用应用,请升级数据库后重试。");
            } else {
                DialogConfirmCfg cfg = new DialogConfirmCfg();
                cfg.cancelBtnText = "取消";
                cfg.confirmBtnText = "重试";
                DialogConfirm.show(context, "提示", "加入常用应用失败，请重试或取消",
                        onClick1, onClick2, cfg);
            }


        }
    };

    private OnDialogConfirmClick onClick1 = new OnDialogConfirmClick() {

        @Override
        public void onDialogConfirmClick() {

            if (longClickItemPosition != -1) {
                if (isMenuFather) {
                    //一级菜单的时候，通过menuAdapter获取相应的item
                    Obj_Menu obj_Menu = (Obj_Menu) menuAdapter.getItem(longClickItemPosition);

                    if (obj_Menu.menuType == 1) {
                        Obj_PublicAccount_Favorite favorite = new Obj_PublicAccount_Favorite();
                        favorite.ID = publicGUID;
                        favorite.Name = publicAccountName;
                        favorite.isHtml = true;
                        favorite.menuName = obj_Menu.menuName;
                        favorite.menuUrl = obj_Menu.menuUrl;
                        favorite.fc_mobilepic = obj_Menu.menuPicUrl;
                        favorite.MenuId = obj_Menu.menuGUID;
                        if (synUploadAndSavePublicFav != null) {
                            synUploadAndSavePublicFav.stop();
                        }

                        synUploadAndSavePublicFav = new SynUploadAndSavePublicFav(context);
                        synUploadAndSavePublicFav.setOnSynUploadAndSavePublicFavListener(
                                onSynUploadListener);
                        synUploadAndSavePublicFav.start(favorite, loginUserID);
                    }

                } else {
                    //一级菜单的时候，通过menuAdapter获取相应的item
                    Obj_Menu obj_Menu = (Obj_Menu) popAdapter.getItem(longClickItemPosition);

                    if (obj_Menu.menuType == 1) {
                        Obj_PublicAccount_Favorite favorite = new Obj_PublicAccount_Favorite();
                        favorite.ID = publicGUID;
                        favorite.Name = publicAccountName;
                        favorite.isHtml = true;
                        favorite.menuName = obj_Menu.menuName;
                        favorite.menuUrl = obj_Menu.menuUrl;
                        favorite.fc_mobilepic = obj_Menu.menuPicUrl;
                        favorite.MenuId = obj_Menu.menuGUID;
                        if (synUploadAndSavePublicFav != null) {
                            synUploadAndSavePublicFav.stop();
                        }

                        synUploadAndSavePublicFav = new SynUploadAndSavePublicFav(context);
                        synUploadAndSavePublicFav.setOnSynUploadAndSavePublicFavListener(
                                onSynUploadListener);
                        synUploadAndSavePublicFav.start(favorite, loginUserID);
                    }

                }
            }

        }
    };

    private OnDialogConfirmCancel onClick2 = new OnDialogConfirmCancel() {

        @Override
        public void onDialogConfirmCancel() {

        }
    };

    public void destory() {
        if (null != syncSend) {
            syncSend.stop();
        }
        if(sharedPreferencesQ!=null){
            sharedPreferencesQ.removeDataChangeListener();
        }
        if (null != syncGetMenuList) {
            syncGetMenuList.stop();
        }

        if (syncGetMenuByDB != null) {
            syncGetMenuByDB.stop();
        }
//        if (syncGetPaConfigByDB != null) {
//            syncGetPaConfigByDB.stop();
//        }

        if (synUploadAndSavePublicFav != null) {
            synUploadAndSavePublicFav.stop();
        }
        if (bluetoothReceiveFile2 != null) {
            bluetoothReceiveFile2.stop();
        }
        if (syncGetHeadThu != null) {
            syncGetHeadThu.stop();
        }
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha 透明度数值
     */
    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        activity.getWindow().setAttributes(lp);
    }

    /**
     * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     *
     * @author cg
     */
    private class poponDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            //Log.v("List_noteTypeActivity:", "我是关闭事件");
            backgroundAlpha(1f);
        }

    }

    private void setProcess() {
        pgDown = new ProgressDialog(context);
        pgDown.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pgDown.setMessage("正在接收中。。。");
        pgDown.setCanceledOnTouchOutside(false);
        pgDown.setCancelable(false);
        pgDown.setMax(100);
        pgDown.setProgress(0);
        pgDown.setProgressNumberFormat("");
//        pgDown.setOnKeyListener(new DialogInterface.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//
//                }
//                return false;
//            }
//        });
    }

    //        //蓝牙文件接收进度
//    private MReceiveAdapter.onProgress onProgress = new MReceiveAdapter.onProgress() {
//        @Override
//        public void onProgress(final int progress, int receiveSize, int allReceiveSize, final int currentIndex, String filename, final ArrayList<FileInfo> fileList) {
//            proGress = progress;
//            fileName = filename;
////            new Handler(Looper.getMainLooper()).post(getRunnable);
//            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                @Override
//                public void run() {
//                    pgDown.setProgress(proGress);
//                    pgDown.setMessage(fileName + " 正在接收中。。。");
//                    if (pgDown != null) {
//                        pgDown.show();
//                    }
//                    if (proGress == 100) {
//                        if (pgDown.isShowing()) {
//                            pgDown.dismiss();
//                        }
//                        if (!IfUpload) {
//                            if ((currentIndex + 1) == fileList.size()) {
//                                showProgressDialog("提示", "文件接收成功，正在上传中。。。");
//                                new Thread(new FileUploadRunnable(String.format(UPLOADFILEURL, netConfig.httpIP, netConfig.httpPort), fileList)).start();
//                            }
//                        } else {
//                            DialogMsg.show(activity, "提示", "当前有文件正在上传中，请稍后再试。。。");
//                        }
//                    }
//                }
//            });
//        }};
//蓝牙文件接收进度
    private MReceiveAdapter.onProgress onProgress = new MReceiveAdapter.onProgress() {
        @Override
        public void onProgress(int progress, int receiveSize, int allReceiveSize, int currentIndex, String filename, ArrayList<FileInfo> fileInfos) {
            proGress = progress;
            fileName = filename;
            new Handler(Looper.getMainLooper()).post(getRunnable);
        }
    };
    private Boolean IfUpload = false;
    private Runnable getRunnable = new Runnable() {
        @Override
        public void run() {
            if (!activity.isFinishing()) {
                pgDown.setProgress(proGress);
                pgDown.setMessage(fileName + " 正在接收中。。。");
                pgDown.show();
                if (proGress == 100) {
                    if (pgDown.isShowing()) {
                        pgDown.dismiss();
                    }
                    //先上传文件
                    if (!IfUpload) {
                        showProgressDialog("提示", "文件接收成功，正在上传中。。。");
                        new Thread(new FileUploadRunnable(String.format(UPLOADFILEURL, netConfig.httpIP, netConfig.httpPort), getBlueToothPath() + "/" + fileName)).start();
                    }
                }
            }
        }
    };

    private JSONObject getJson(String imri_url) {
        JSONObject postJson = new JSONObject();
        try {
            LoginUser loginUser = Sps_RWLoginUser.readUser(context);
            String userName = "";
            if (loginUser != null) {
                userName = loginUser.name;
            }
            postJson.put("USER_NAME", userName);
            postJson.put("IMRI_URL", imri_url);
        } catch (JSONException e) {
            return postJson;
        }
        return postJson;
    }

    private String dialogResult;

    private class FileUploadRunnable implements Runnable {

        private String urlStr, path;
        private String error = "";

        private FileUploadRunnable(String urlStr, String path) {
            this.urlStr = urlStr;
            this.path = path;
        }

        @Override
        public void run() {
            IfUpload = true;
            String[] result = SynUpload.uploadFile(urlStr, path, "txt", 4, UUID.randomUUID().toString());
            try {
                JSONObject resultJson = new JSONObject(result[1]);
                int resultCode = resultJson.optInt("RESPONSE_STATE", 500);
                if (resultCode == 200) {
                    String neturl = String.format("http://%s:%s/" + resultJson.optString("NET_PATH", ""), netConfig.ftpIP, netConfig.ftpPort);
                    JSONObject requestJson = getJson(neturl);
                    String postUrl = String.format(SENDIMRIURL, netConfig.httpIP, netConfig.httpPort);
                    JSONObject imsiResultJson = HttpPost.postJsonObj(postUrl, requestJson);
                    String Uploadresult[] = HttpPost.checkResult(imsiResultJson);
                    if (Uploadresult != null) {//上传失败
                        error = "上传失败：" + Uploadresult[0] + "\n" + Uploadresult[1];
//                        String detail = resultJson.optString("DETAIL");
//                        error="上传失败："+detail;
                        Log.i("testy", "上传IMSI失败" + error);
                    } else {
                        Log.i("testy", "上传成功");
                    }
                } else {
                    error = "上传文件失败";
                    Log.i("testy", "上传文件失败");
                }
            } catch (Exception e) {
                Log.i("testy", "上传文件失败：" + e.toString());
                error = "上传文件失败" + "\n" + e.toString();
            } finally {
                IfUpload = false;
                dismissProgressDialog();
                dialogResult = error;
                new Handler(Looper.getMainLooper()).post(dialogRunnable);

            }
        }
    }

    private void showProgressDialog(String title, String content) {
        if (pgDown.isShowing()) {
            pgDown.dismiss();
        }
        progressDialog = DialogProgress.get(activity, title, content);
        progressDialog.setCancelable(false);// 点击 返回键 dialog不消失
        progressDialog.setCanceledOnTouchOutside(false);// 点击 dialog 外部 dialog不消失
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private Runnable dialogRunnable = new Runnable() {
        @Override
        public void run() {
            File file = new File(getBlueToothPath() + "/" + fileName);
            if (file.exists()) {
                file.delete();
            }
            if (StringUtil.isNotEmpty(dialogResult)) {
                DialogMsg.show(activity, "提示", dialogResult);
            } else {
                DialogMsg.show(activity, "提示", "上传成功");
            }
        }
    };
    private View.OnClickListener addNormalAppOnclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Obj_Menu obj_menu1 = (Obj_Menu) v.getTag();
            DialogTool.dialog.dismiss();
            //有菜单
            Obj_Menu obj_Menu = obj_menu1;
            final String menuName = obj_Menu.menuName;
            final String menuUrl = obj_Menu.menuUrl;
            final String menupicurl = obj_Menu.menuPicUrl;
            final String menuid = obj_Menu.menuGUID;
            final String publicid = obj_Menu.ID;
            boolean isSavePublicFavorite = isSavePublicFavorite(publicGUID, menuName);
            if (isSavePublicFavorite) {
                Toast.makeText(activity, "请不要重复加入常用公众号", Toast.LENGTH_SHORT).show();
            } else {
                Obj_PublicAccount_Favorite favorite = new Obj_PublicAccount_Favorite();
                favorite.ID = publicid;
                favorite.Name = publicAccountName;
                favorite.isHtml = true;
                favorite.menuName = menuName;
                favorite.menuUrl = menuUrl;
                favorite.fc_mobilepic = menupicurl;
                favorite.MenuId = menuid;
                synUploadAndSavePublicFav.start(favorite, loginUserID);
            }

        }
    };
    private View.OnClickListener addToDesktop = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Obj_Menu obj_menu = (Obj_Menu) v.getTag();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                if (DialogTool.dialog != null && DialogTool.dialog.isShowing()) {
                    DialogTool.dialog.dismiss();
                }

                ShortcutManager shortcutManager = (ShortcutManager) activity.getSystemService(Context.SHORTCUT_SERVICE);
                if (shortcutManager.isRequestPinShortcutSupported()) {
//                    调整启动zzw
                    Intent launcherIntent= new Intent(activity, PublicAccountChatActNormal.class);
//                    Intent launcherIntent= new Intent(activity.getApplication(), PublicAccountChatAct.class);
                    launcherIntent.setAction(Intent.ACTION_VIEW);
                    launcherIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    if (obj_menu.menuType == 1) {
                        //改成先打开公众号页面再打开网页
                        launcherIntent.putExtra("ACCOUNT_NAME", publicAccountName);
                        launcherIntent.putExtra("ACCOUNT_ID", publicGUID + "," + obj_menu.menuGUID + "," + obj_menu.menuName);
                        launcherIntent.putExtra("MenuKry_Click", "weburl");
                    } else if (obj_menu.menuType == 2) {
                        launcherIntent.putExtra("ACCOUNT_NAME", publicAccountName);
                        launcherIntent.putExtra("ACCOUNT_ID", publicGUID + "," + obj_menu.menuGUID + "," + obj_menu.menuName);
                        launcherIntent.putExtra("MenuKry_Click", "click");
                    }
                    String menuPath = getPublicName(obj_menu.menuGUID);
                    Bitmap iconMenuBitmap = BitmapFactory.decodeFile(menuPath);
                    if (iconMenuBitmap == null) {
                        iconMenuBitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.publicmenupic_default);
                    }
                    ShortcutInfo info = new ShortcutInfo.Builder(activity, obj_menu.menuGUID)
                        .setIcon(Icon.createWithBitmap(iconMenuBitmap))
                        .setShortLabel(obj_menu.menuName)
                        .setIntent(launcherIntent)
                        .build();
                    //当添加快捷方式的确认弹框弹出来时，将被回调
                    PendingIntent shortcutCallbackIntent = PendingIntent.getBroadcast(activity, 0, new Intent(activity, ShortCutReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
                    shortcutManager.requestPinShortcut(info, shortcutCallbackIntent.getIntentSender());
                }

            } else {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                //调整启动zzw
                ComponentName cn = new ComponentName(activity.getPackageName(), "synway.module_publicaccount.public_chat.PublicAccountChatActNormal");
//                ComponentName cn = new ComponentName("synway.osc.home.HomeAct", "synway.module_publicaccount.public_chat.PublicAccountChatAct");
                intent.setComponent(cn);
                String path;
                path = getPublicName(obj_menu.menuGUID);
                if (obj_menu.menuType == 1) {
                    //改成先打开公众号页面再打开网页
                    intent.putExtra("ACCOUNT_NAME", publicAccountName);
                    intent.putExtra("ACCOUNT_ID", publicGUID + "," + obj_menu.menuGUID + "," + obj_menu.menuName);
                    intent.putExtra("MenuKry_Click", "weburl");
                } else if (obj_menu.menuType == 2) {
                    intent.putExtra("ACCOUNT_ID", publicGUID + "," + obj_menu.menuGUID + "," + obj_menu.menuName);
                    intent.putExtra("ACCOUNT_NAME", publicAccountName);
                    intent.putExtra("MenuKry_Click", "click");
                }
                Bitmap bitmap = PicCompress.compressSize(path);
                addShortcut2(activity, obj_menu.menuName, bitmap, intent, false, 1);
            }
        }

    };

    private String getPublicName(String id) {
        return BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + id;
    }

    public void downMenuIcon(ArrayList<Obj_Menu> firstMenus) {

        //下载需要下载的菜单图标
        ArrayList<PicidPublicidBean> needDownMenuList = new ArrayList<>();
        for (Obj_Menu firstMenu : firstMenus) {
            Log.d("dym------------------->", "firstMenu= " + firstMenu.menuName + "," + firstMenu.menuGUID + "," + firstMenu.menuPicUrl + "," + firstMenu.menuType);
            if (firstMenu.menuType == 1) {
                String path = BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + firstMenu.menuGUID;
                if (!new File(path).exists()) {
                    //图片本地不存在
                    PicidPublicidBean picidPublicidBean = new PicidPublicidBean();
                    picidPublicidBean.publicid = firstMenu.menuGUID;
                    picidPublicidBean.picid = firstMenu.menuPicUrl;
                    Log.d("dym------------------->", "publicid= " + picidPublicidBean.publicid + ",picid= " + picidPublicidBean.picid);
                    needDownMenuList.add(picidPublicidBean);
                }
            }
        }

//        for (Obj_Menu secondMenu : secondMenus) {
//            Log.d("dym------------------->", "firstMenu= " + secondMenu.menuName + "," + secondMenu.menuGUID + "," + secondMenu.menuPicUrl + "," + secondMenu.menuType);
//
//            if (secondMenu.menuType == 1) {
//                String path = BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + secondMenu.menuGUID;
//                if (!new File(path).exists()) {
//                    //图片本地不存在
//                    PicidPublicidBean picidPublicidBean = new PicidPublicidBean();
//                    picidPublicidBean.publicid = secondMenu.menuGUID;
//                    picidPublicidBean.picid = secondMenu.menuPicUrl;
//                    Log.d("dym------------------->", "publicid= " + picidPublicidBean.publicid + ",picid= " + picidPublicidBean.picid);
//                    needDownMenuList.add(picidPublicidBean);
//
//                }
//            }
//        }
        Log.d("dym------------------->", "需要下载的个数. " + needDownMenuList.size());
        syncGetHeadThu.startPublicIdIcon(needDownMenuList);
    }
}