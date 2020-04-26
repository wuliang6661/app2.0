package synway.module_publicaccount.publiclist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import qyc.library.control.list_pulldown.ListPullDown;
import synway.module_interface.config.netConfig.NetConfig;
import synway.module_interface.config.netConfig.Sps_NetConfig;
import synway.module_interface.config.userConfig.Sps_RWLoginUser;
import synway.module_publicaccount.Main;
import synway.module_publicaccount.R;
import synway.module_publicaccount.db.PublicAccountFocusedList;

public class FocusedAccountAct extends Activity {

    private ListPullDown listview = null;
    private FocusedAccountAdapter adapter = null;
//    private FocusedAccountAct adapter = null;

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
//    private SyncGetPublicByHttp syncGetPublicByHttp = null;
    private SyncGetHeadThu syncGetHeadThu = null;
    private SyncGetPublicByDB syncGetPublicByDB = null;
    private ArrayList<String> focusedList = null;


    private View topRightButton = null;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.model_public_account_act);

        titleView = new TitleView(this);
        titleView.setTitle("");

        initUI();

        // SQLIteHelp sqliteHelp= MainApp.getInstance().sqlIteHelp;
        // try {
        // db = SQLiteDatabase.openDatabase(BaseUtil.getDBPath(this), null,
        // SQLiteDatabase.OPEN_READWRITE);
        // } catch (Exception e) {
        // ThrowExp.throwRxp("SyncGetRecordFromDB ");
        // }

        netConfig = Sps_NetConfig.getNetConfigFromSpf(this);
        loginUserID = Sps_RWLoginUser.readUserID(this);
        idList = new ArrayList<String>();

//        syncGetPublicByHttp = new SyncGetPublicByHttp();
//        syncGetPublicByHttp.setLsn(onGetAccountByHttp);

        syncGetHeadThu = new SyncGetHeadThu(netConfig.httpIP,
                netConfig.httpPort,this);
        syncGetHeadThu.setOnListen(onGetHeadThu);

        syncGetPublicByDB = new SyncGetPublicByDB();
        syncGetPublicByDB.setLsn(onGetAccountByDB);
        syncGetPublicByDB.start(idList);

        // try {
        // getActionBar().setDisplayHomeAsUpEnabled(true);
        // getActionBar().setHomeAsUpIndicator(R.drawable.icon_back);
        // getActionBar().setDisplayShowHomeEnabled(false);
        // }catch (NoSuchMethodError e) {
        // e.printStackTrace();
        // }
    }

    private void initUI() {
        adapter = new FocusedAccountAdapter(this);

        listview = findViewById(R.id.listView1);
        listview.setOnItemClickListener(onItemClickListener);
        listview.setAdapter(adapter);
        listview.setOnScrollListener(onScrollListener);
        listview.setOnPDListen(onPDListListener);
        listview.loadingMoreView_IsEnabled(false);

        layoutIndex = findViewById(R.id.linearLayout1);

        tv_show = findViewById(R.id.textView5);

        topRightButton = findViewById(R.id.confirm);
        topRightButton.setVisibility(View.VISIBLE);
        ((TextView) topRightButton.findViewById(R.id.textview1)).setText("确定");
        topRightButton.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ArrayList<String> list = adapter.getFocusedList();
            Intent intent = new Intent();
            intent.putExtra("FocusedList", list);
            setResult(1, intent);
            finish();
        }
    };

    /**
     * μActivity-onWindowFocusChanged
     * <p/>
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
        for (int i = 0; i < listShowing.size(); i++) {
            String showingID = listShowing.get(i).ID;
            if (idList.contains(showingID)) {
                listNoHeadAndShowing.add(showingID);
            }
        }

        // У^og
        if (listNoHeadAndShowing.size() == 0) {
            return;
        }
        syncGetHeadThu.start(listNoHeadAndShowing);
    }

    /**
     * б
     */
    private void getIndexView() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                height);
        for (int i = 0; i < indexStr.length; i++) {
            final TextView tv = new TextView(this);
            tv.setLayoutParams(params);
            tv.setText(indexStr[i]);
            tv.setPadding(10, 0, 10, 0);
            layoutIndex.addView(tv);
            layoutIndex.setOnTouchListener(new View.OnTouchListener() {
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
                            // if (listview.getHeaderViewsCount() > 0) {//
                            // ListViewб^uС
                            // listview.setSelectionFromTop(
                            // pos + listview.getHeaderViewsCount(), 0);
                            // } else {
                            // listview.setSelectionFromTop(pos, 0);//
                            // }

                            // //tv_show.setVisibility(View.VISIBLE);
                            // //tv_show.setText(indexStr[index]);
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

    private ListPullDown.OnPDListListener onPDListListener = new ListPullDown.OnPDListListener() {

        @Override
        public void onloadMore() {

        }

        @Override
        public void onRefresh() {
//            syncGetPublicByHttp.start(netConfig.httpIP, netConfig.httpPort,
//                    loginUserID, idList);
        }
    };

    private AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
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

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            Obj_PublicAccount obj_publicAccount = (Obj_PublicAccount) adapter.getItem(position);
            obj_publicAccount.isChecked = !obj_publicAccount.isChecked;
            adapter.notifyDataSetChanged();

        }
    };

    private SyncGetPublicByDB.IOnGetAccountByDB onGetAccountByDB = new SyncGetPublicByDB.IOnGetAccountByDB() {

        @Override
        public void onResult(ArrayList<Obj_PublicAccount> arrayList, HashMap<String, Integer> selePos,ArrayList<Obj_PublicAccount> appList) {
            selector = selePos;


            if (focusedList == null) {
                synchronized (this) {
                    if (focusedList == null) {
                        focusedList = PublicAccountFocusedList.getFocusedIDArrayList(Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase());
                    }
                }
            }

            for (Obj_PublicAccount obj : arrayList) {
                if (focusedList.contains(obj.ID)) {
                    obj.isChecked = true;
                }
            }

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

//    private SyncGetPublicByHttp.IOnGetAccountByHttp onGetAccountByHttp = new SyncGetPublicByHttp.IOnGetAccountByHttp() {
//        @Override
//        public void onResult(ArrayList<Obj_PublicAccount> arrayList, HashMap<String, Integer> selePos,ArrayList<Obj_PublicAccount> appList) {
//            listview.stopRefresh();
//            selector = selePos;
//
//            if (focusedList == null) {
//                synchronized (this) {
//                    if (focusedList == null) {
//                        focusedList = PublicAccountFocusedList.getFocusedIDArrayList(Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase());
//                    }
//                }
//            }
//
//            for (Obj_PublicAccount obj : arrayList) {
//                if (focusedList.contains(obj.ID)) {
//                    obj.isChecked = true;
//                }
//            }
//
//
//            adapter.reset(arrayList);
//            adapter.refresh();
//
//            listview.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    int a = listview.getFirstVisiblePosition();
//                    int b = listview.getLastVisiblePosition();
//                    downLoadUserHead(a, b);
//                }
//            }, 500);
//        }
//
//        @Override
//        public void onFail(String title, String reason, String detail) {
//            listview.stopRefresh();
//            DialogMsg.showDetail(FocusedAccountAct.this, title, reason, detail);
//        }
//    };

    private SyncGetHeadThu.IOnGetHeadThu onGetHeadThu = new SyncGetHeadThu.IOnGetHeadThu() {
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

    @Override
    protected void onDestroy() {

        this.syncGetPublicByDB.stop();

//        this.syncGetPublicByHttp.stop();

        this.syncGetHeadThu.stop();

        // db.close();

        super.onDestroy();
    }
}
