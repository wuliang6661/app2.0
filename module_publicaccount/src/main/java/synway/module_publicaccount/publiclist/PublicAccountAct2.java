package synway.module_publicaccount.publiclist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import qyc.library.control.dialog_confirm.DialogConfirm;
import qyc.library.control.dialog_confirm.DialogConfirmCfg;
import qyc.library.control.dialog_confirm.OnDialogConfirmCancel;
import qyc.library.control.dialog_confirm.OnDialogConfirmClick;
import qyc.library.control.dialog_msg.DialogMsg;
import qyc.library.control.dialog_msg.TheDialog;
import qyc.library.control.dialog_progress.DialogProgress;
import qyc.library.control.list_pulldown.ListPullDown;
import qyc.library.control.list_pulldown.ListPullDown.OnPDListListener;
import synway.module_interface.config.netConfig.NetConfig;
import synway.module_interface.config.netConfig.Sps_NetConfig;
import synway.module_interface.config.userConfig.Sps_RWLoginUser;
import synway.module_publicaccount.R;
import synway.module_publicaccount.public_chat.Obj_Menu;
import synway.module_publicaccount.public_favorite.PublicFavoriteFragment;
import synway.module_publicaccount.public_favorite.SynUploadAndSavePublicFav;
import synway.module_publicaccount.public_favorite.obj.Obj_PublicAccount_Favorite;
import synway.module_publicaccount.publiclist.SyncGetHeadThu.IOnGetHeadThu;
import synway.module_publicaccount.publiclist.SyncGetPublicByHttp2.IOnGetAccountByHttp;

public class PublicAccountAct2 extends Activity {

    private ListPullDown listview = null;
    public PublicAccountAdapter2 adapter = null;

    // =================
    private LinearLayout layoutIndex = null;
    private TextView tv_show = null;
    private String[] indexStr = {"#", "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
            "V", "W", "X", "Y", "Z"};
    private int height;//
    private boolean flag = false;
    // 'zλ
    private HashMap<String, Integer> selector = null;

    private TitleView titleView = null;

    // ================Щ
    private NetConfig netConfig = null;
    private String loginUserID = null;
    // ChatActDB
    // private SQLiteDatabase db = null;
    // ^uItem
    private ArrayList<String> idList = null;

    // ================g
    private SyncGetPublicByHttp2 syncGetPublicByHttp = null;
    private SyncGetHeadThu syncGetHeadThu = null;
    private SyncGetPublicByDB2 syncGetPublicByDB = null;

    // ===================

    /**
     * 常用应用的上传和入库
     */
    public SynUploadAndSavePublicFav synUploadAndSavePublicFav = null;
    /**
     * 上传和入库过程中显示的dialog,友好地提示用户
     */
    private Dialog uploadDialog = null;
    /**
     * 记录长按加入常用应用的在listview中item的position
     */
    private int longClickItemPosition = -1;
    private int Aposition;
//    public SyncGetMenuListByDB syncGetMenuByDB = null;
//    public SyncGetMenuList syncGetMenuList = null;
    private
    ArrayList<Obj_PublicAccount> publicAccountsHttp = null;
    private BroadcastReceiver broadcastReceiver=null;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.model_public_account_act);
        titleView = new TitleView(this);
        titleView.setTitle("");
        netConfig = Sps_NetConfig.getNetConfigFromSpf(this);
        syncGetHeadThu = new SyncGetHeadThu(netConfig.httpIP,
                netConfig.httpPort, this);
        syncGetHeadThu.setOnListen(onGetHeadThu);
        initUI();
        loginUserID = Sps_RWLoginUser.readUserID(this);
        idList = new ArrayList<String>();
        publicAccountsHttp = new ArrayList<Obj_PublicAccount>();
        syncGetPublicByHttp = new SyncGetPublicByHttp2();
        syncGetPublicByHttp.setLsn(onGetAccountByHttp);
        syncGetPublicByDB = new SyncGetPublicByDB2();
        syncGetPublicByDB.setLsn(onGetAccountByDB);
        syncGetPublicByDB.start(idList);
        synUploadAndSavePublicFav = new SynUploadAndSavePublicFav(PublicAccountAct2.this);
        synUploadAndSavePublicFav.setOnSynUploadAndSavePublicFavListener(onSynUploadAndSavePublicFavListener);

    }

    private void initUI() {
        adapter = new PublicAccountAdapter2(this, this, this, syncGetHeadThu);
        listview = findViewById(R.id.listView1);
        listview.setAdapter(adapter);
        listview.setOnScrollListener(onScrollListener);
        listview.setOnPDListen(onPDListListener);
        listview.loadingMoreView_IsEnabled(false);
        layoutIndex = findViewById(R.id.linearLayout1);
        tv_show = findViewById(R.id.textView5);
    }

    /**
     * μActivity-onWindowFocusChanged
     * <p>
     * ,ijViewLonWindowFocusChanged() ijviewonGlobalLayoutListenerk' 磬l 1/4 жδig
     * ui
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // oncreate^J^uoncreate~ogetHeight=0
        if (!flag) {
            height = layoutIndex.getMeasuredHeight() / indexStr.length;
            getIndexView();
            flag = true;
        }
    }


    /**
     * б
     */
    private void getIndexView() {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                height);
        for (int i = 0; i < indexStr.length; i++) {
            final TextView tv = new TextView(this);
            tv.setLayoutParams(params);
            tv.setText(indexStr[i]);
            tv.setPadding(10, 0, 10, 0);
            layoutIndex.addView(tv);
            layoutIndex.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (null == selector || selector.size() == 0) {
                        return false;
                    }
                    float y = event.getY();
                    int index = (int) (y / height);
                    if (index > -1 && index < indexStr.length) {//
                        String key = indexStr[index];
                        if (selector.containsKey(key)) {
                            int pos = selector.get(key);
                            listview.setSelection(pos);
                        }
                        tv_show.setVisibility(View.VISIBLE);
                        tv_show.setText(indexStr[index]);
                    }
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            layoutIndex.setBackgroundColor(Color
                                    .parseColor("#D9D9D9"));
                            break;

                        case MotionEvent.ACTION_MOVE:

                            break;
                        case MotionEvent.ACTION_UP:
                            layoutIndex.setBackgroundColor(Color
                                    .parseColor("#00ffffff"));
                            tv_show.setVisibility(View.GONE);
                            break;
                    }
                    return true;
                }
            });
        }
    }

    private OnPDListListener onPDListListener = new OnPDListListener() {

        @Override
        public void onloadMore() {

        }

        @Override
        public void onRefresh() {
            syncGetPublicByHttp.start(netConfig.httpIP, netConfig.httpPort,
                    loginUserID, idList);
        }
    };

    private OnScrollListener onScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                int a = view.getFirstVisiblePosition();
                int b = view.getLastVisiblePosition();
                downLoadUserHead(a, b);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
        }
    };


    /**
     * 将Obj_PublicAccount_Favorite对象转换成相应的json数据
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
            jsonObject.put("Type",favorite.type);
            jsonObject.putOpt("appInformation",favorite.app_information);
            jsonObject.put("appInformation",favorite.app_information);
            returnStr = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return returnStr;

    }

    public void setAdpaterPosition(Obj_PublicAccount Aposition_obj) {
        this.Aposition = Aposition;
    }


//    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
//
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position,
//                                long id) {
//
//            Obj_PublicAccount obj_PublicAccount = (Obj_PublicAccount) adapter
//                    .getItem(position);
//
//            Intent intent = new Intent();
//            intent.setClass(PublicAccountAct2.this, PublicAccountChatAct.class);
//            intent.putExtra("ACCOUNT_ID", obj_PublicAccount.ID);
//            intent.putExtra("ACCOUNT_NAME", obj_PublicAccount.name);
//            startActivity(intent);
//
//        }
//    };

    private SyncGetPublicByDB2.IOnGetAccountByDB onGetAccountByDB = new SyncGetPublicByDB2.IOnGetAccountByDB() {

        @Override
        public void onResult(ArrayList<Obj_PublicAccount> arrayList, HashMap<String, Integer> selePos) {
            selector = selePos;
			adapter.reset(arrayList);
			adapter.refresh();
			listview.postDelayed(new Runnable() {
				@Override
				public void run() {
					int a = listview.getFirstVisiblePosition();
					int b = listview.getLastVisiblePosition();
					downLoadUserHead(a, b);
				}
			}, 500);
        }

        @Override
        public void onFail() {
            // syncGetPublicByHttp.start(netConfig.httpIP, netConfig.httpPort,
            // loginUserID, idList);
            listview.startRefresh();
        }
    };

    private IOnGetAccountByHttp onGetAccountByHttp = new IOnGetAccountByHttp() {
        @Override
        public void onResult(ArrayList<Obj_PublicAccount> arrayList, HashMap<String, Integer> selePos,ArrayList<Obj_PublicAccount> AppList) {
            Log.i("testy", "公众号获取成功");
            listview.stopRefresh();
            selector = selePos;
            publicAccountsHttp = arrayList;
            selector = selePos;
            adapter.reset(arrayList);
            adapter.refresh();
            listview.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int a = listview.getFirstVisiblePosition();
                    int b = listview.getLastVisiblePosition();
                    downLoadUserHead(a, b);
                }
            }, 500);
//            syncGetMenuList.start(netConfig.httpIP, netConfig.httpPort, loginUserID, arrayList);

        }

        @Override
        public void onFail(String title, String reason, String detail) {
            listview.stopRefresh();

            if ("没有公众号数据".equals(reason)) {
                //针对没有公众号界面又没有更新做处理
                adapter.reset(new ArrayList<Obj_PublicAccount>());
                adapter.refresh();
                TheDialog dialog = new TheDialog(PublicAccountAct2.this, title, reason);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override public void onDismiss(DialogInterface dialog) {
                        PublicAccountAct2.this.finish();
                    }
                });
                dialog.show();

            } else {
                //网络原因导致的失败
                DialogMsg.showDetail(PublicAccountAct2.this, title, reason, detail);
            }
        }
    };


    private IOnGetHeadThu onGetHeadThu = new IOnGetHeadThu() {
        @Override
        public void onHeadGet(String ID) {
            adapter.refresh();
        }

        @Override
        public void onFailGet(String ID) {

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private SynUploadAndSavePublicFav.OnSynUploadAndSavePublicFavListener
            onSynUploadAndSavePublicFavListener =
            new SynUploadAndSavePublicFav.OnSynUploadAndSavePublicFavListener() {
                @Override
                public void onStart(Boolean ifstart) {
                    if (uploadDialog == null) {
                        uploadDialog = DialogProgress.get(PublicAccountAct2.this, "提示", "正在加入常用应用");
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

//                Toast.makeText(PublicAccountAct.this, "加入常用公众号成功",
//                    Toast.LENGTH_SHORT).show();

                    /** 发广播，更新UI */
                    Intent intent = new Intent();
                    intent.setAction(PublicFavoriteFragment.Broad_Cast_Action);
                    intent.putExtra(PublicFavoriteFragment.Obj_PublicFavorite,favorite);
//                    String jsonString = tranferPublicFavObjToJsonString(favorite);
//                    intent.putExtra(PublicFavoriteFragment.Obj_PublicFavorite,
//                            jsonString);
                    PublicAccountAct2.this.sendBroadcast(intent);

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
                    /** 为什么用添加这个"数据库错误"判断呢？是因为我们用到CustomConfigRW.write(),上传数据库的时候由于
                     * 之前数据库ConfigTable表设置value的字段不够长，现在改为text,但是外面的服务端可能数据库还没有及时
                     * 升级,所以在此进行提醒*/
                    if (reason.contains("数据库错误")) {
                        DialogMsg.show(PublicAccountAct2.this, "提示", "服务端数据库版本较旧,不支持加入常用应用,请升级数据库后重试。");
                    } else {
                        DialogConfirmCfg cfg = new DialogConfirmCfg();
                        cfg.cancelBtnText = "取消";
                        cfg.confirmBtnText = "重试";
                        DialogConfirm.show(PublicAccountAct2.this, "提示", "加入常用应用失败，请重试或取消",
                                onClick1, onClick2, cfg);
                    }


                }
            };

    private OnDialogConfirmClick onClick1 = new OnDialogConfirmClick() {

        @Override
        public void onDialogConfirmClick() {

            if (longClickItemPosition != -1) {
                Obj_PublicAccount obj_PublicAccount = (Obj_PublicAccount) adapter
                        .getItem(longClickItemPosition);

                Obj_PublicAccount_Favorite favorite = new Obj_PublicAccount_Favorite();
                favorite.ID = obj_PublicAccount.ID;
                favorite.Name = obj_PublicAccount.name;
                favorite.isHtml = false;
                if (synUploadAndSavePublicFav != null) {
                    synUploadAndSavePublicFav.stop();
                }

                synUploadAndSavePublicFav = new SynUploadAndSavePublicFav(PublicAccountAct2.this);
                synUploadAndSavePublicFav.setOnSynUploadAndSavePublicFavListener(
                        onSynUploadAndSavePublicFavListener);
                synUploadAndSavePublicFav.start(favorite, loginUserID);

            }


        }
    };

    private OnDialogConfirmCancel onClick2 = new OnDialogConfirmCancel() {

        @Override
        public void onDialogConfirmCancel() {

        }
    };

    @Override
    protected void onDestroy() {

        this.syncGetPublicByDB.stop();

        this.syncGetPublicByHttp.stop();

        this.syncGetHeadThu.stop();

        if (synUploadAndSavePublicFav != null) {
            synUploadAndSavePublicFav.stop();
        }
//        if (syncGetMenuByDB != null) {
//            syncGetMenuByDB.stop();
//        }
//        if (syncGetMenuList != null) {
//            syncGetMenuList.stop();
//        }
        if(adapter!=null){
            adapter.onDestory();
        }
        // db.close();

        super.onDestroy();
    }


    /**
     * ^u SynGetUserInfo
     */
    private void downLoadUserHead(int topPostion, int bottomPosition) {
        if (idList.size() == 0) {
            // ^uID
            System.out.println("downLoadHead:^uID");
            return;
        }

        // LЩ^uIDб
        List<Obj_PublicAccount> listShowing = adapter.sub(topPostion,
                bottomPosition);

        // бУListID
        ArrayList<String> listNoHeadAndShowing = new ArrayList<String>();
        ArrayList<PicidPublicidBean> picidPublicidBeens = new ArrayList<PicidPublicidBean>();
        PicidPublicidBean picidPublicidBean = new PicidPublicidBean();
        for (int i = 0; i < listShowing.size(); i++) {
            String showingID = listShowing.get(i).ID;
            String piid = listShowing.get(i).fc_mobilepic;
            picidPublicidBean.publicid = showingID;
            picidPublicidBean.picid = piid;
            if (idList.contains(showingID)) {
                listNoHeadAndShowing.add(piid);
                picidPublicidBeens.add(picidPublicidBean);
                //遍历添加一级或者二级菜单中的图片
                ArrayList<Obj_Menu> obj_first_menus = listShowing.get(i).firstmenus;
                if (obj_first_menus != null) {
                    for (int j = 0; j < obj_first_menus.size(); j++) {
                        picidPublicidBean.picid = obj_first_menus.get(j).menuPicUrl;
                        picidPublicidBean.publicid = obj_first_menus.get(j).menuGUID;
                        picidPublicidBeens.add(picidPublicidBean);
                        listNoHeadAndShowing.add(obj_first_menus.get(j).menuPicUrl);

                    }
                }
                ArrayList<Obj_Menu> obj_secnd_menus = listShowing.get(i).secondmenus;
                if (obj_secnd_menus != null) {
                    for (int k = 0; k < obj_secnd_menus.size(); k++) {
                        picidPublicidBean.picid = obj_secnd_menus.get(k).menuPicUrl;
                        picidPublicidBean.publicid = obj_secnd_menus.get(k).menuGUID;
                        picidPublicidBeens.add(picidPublicidBean);
                        listNoHeadAndShowing.add(obj_secnd_menus.get(k).menuPicUrl);
                    }
                }
            }
        }

        // У^og
        if (listNoHeadAndShowing.size() == 0) {
            return;
        }
        syncGetHeadThu.startPublicIdIcon(picidPublicidBeens);
//        syncGetHeadThu.start(listNoHeadAndShowing);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==110&&resultCode==111){
            listview.startRefresh();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}