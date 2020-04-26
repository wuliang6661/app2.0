package synway.module_publicaccount.public_message;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import synway.module_interface.HmIdUtils;
import synway.module_interface.config.userConfig.LoginUser;
import synway.module_interface.config.userConfig.Sps_RWLoginUser;
import synway.module_publicaccount.Main;
import synway.module_publicaccount.R;
import synway.module_publicaccount.db.table_util.Table_PublicAccount;
import synway.module_publicaccount.public_chat.PAWebView;
import synway.module_publicaccount.public_chat.PAWebViewAct;
import synway.module_publicaccount.public_chat.PublicAccountChatActNormal;
import synway.module_publicaccount.public_chat.SetLCForPublicAccount;
import synway.module_publicaccount.public_list_new.SynGetPublicAccountDB;
import synway.module_publicaccount.public_list_new.adapter.PublicGridItem;
import synway.module_publicaccount.publiclist.until.ShortCUtUntil;
import synway.module_publicaccount.push.PushUtil;
import synway.module_publicaccount.weex_module.beans.WxMapData;

/**
 * Created by huangxi
 * DATE :2019/1/17
 * Description ：
 */

public class PAMessageViewAct extends Activity implements SynGetPaMessage.OnGetPaMessageListen {
    // =================titleView
    private PaMessageTitleView titleView = null;
    private AdapterPaMessage adapterPaMessage;
    private DialogForPaMsgItemLongPress dialogForPaMsgItemLongPress = null;
    private ListView lvMsg;
    private SynGetPaMessage synGetPaMessage;
    private SynGetPublicAccountDB synGetPublicAccountDB;
    private ArrayList<PublicGridItem> items;
    private String loginUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.model_public_account_message);
        loginUserID = Sps_RWLoginUser.readUserID(this);
        titleView = new PaMessageTitleView(this);
        titleView.setTitle("应用消息查看");
        lvMsg = findViewById(R.id.lv_message);
        dialogForPaMsgItemLongPress = new DialogForPaMsgItemLongPress(this);
        dialogForPaMsgItemLongPress.setLsn(onDialogItemClickLsn);
        adapterPaMessage = new AdapterPaMessage(this);
        lvMsg.setAdapter(adapterPaMessage);
        lvMsg.setOnItemClickListener(onItemClickListener);
        lvMsg.setOnItemLongClickListener(onItemLongClickListener);

        // 注册新消息的广播
        IntentFilter ifl = new IntentFilter();
        ifl.addAction(PublicMessage.ACTION_UPDATE_PUBLIC_ACCOUNT);
        ifl.addAction(PublicMessage.ACTION_CLEAR_PUBLIC_ACCOUNT);
        ifl.addAction(PushUtil.PublicLastUnreadCountMinus.ACTION);
        registerReceiver(onNewMsgReceive, ifl);
        synGetPaMessage = new SynGetPaMessage(this);
        synGetPaMessage.setOnListen(this);
        synGetPaMessage.start();

        synGetPublicAccountDB = new SynGetPublicAccountDB();
        synGetPublicAccountDB.setListener(new SynGetPublicAccountDB.OnGetPublicAccountDBListener() {
            @Override
            public void onResult(ArrayList<PublicGridItem> resultList) {
                items = resultList;
            }
        });
        synGetPublicAccountDB.start();

    }

    /**
     * @param id 公众号ID
     * @return 对应ID的 PublicGridItem,没查到返回null。
     */
    private PublicGridItem QuerryByID(String id) {
        if (items != null) {
            for (PublicGridItem item : items) {
                if (item.id.equals(id)) {
                    return item;
                }
            }
        }
        return null;
    }

    //可否置顶、删除应用弹出框
    private DialogForPaMsgItemLongPress.IOnDialogItemClickLsn onDialogItemClickLsn = new DialogForPaMsgItemLongPress.IOnDialogItemClickLsn() {

        @Override
        public void onTopIndex(String paID) {

            SQLiteOpenHelper sqliteHelp = Main.instance().moduleHandle.getSQLiteHelp();

            int maxValue = PaMsgTopIndexDeal.GetMaxTopIndex(sqliteHelp
                    .getWritableDatabase());
            maxValue = maxValue + 1;
            PaMsgTopIndexDeal.update(sqliteHelp.getWritableDatabase(), paID,
                    maxValue);
            ObjPaMessage objPaMessage = adapterPaMessage.getObjFromID(paID);
            if (null != objPaMessage) {
                objPaMessage.topIndex = maxValue;
                adapterPaMessage.setTop(objPaMessage);
                adapterPaMessage.notifyDataSetChanged();
            }

        }

        @Override
        public void onCancelTopIndex(String paID) {
            SQLiteOpenHelper sqliteHelp = Main.instance().moduleHandle.getSQLiteHelp();

            PaMsgTopIndexDeal.cancelTop(sqliteHelp.getWritableDatabase(), paID);

            ObjPaMessage objPaMessage = adapterPaMessage.getObjFromID(paID);
            if (null != objPaMessage) {
                adapterPaMessage.cancelTop(objPaMessage);
                adapterPaMessage.notifyDataSetChanged();
                //取消置顶后恢复原来的位置顺序
                if (synGetPaMessage != null) {
                    synGetPaMessage.start();
                }
            }
        }

        @Override
        public void onDelete(String paID) {
            SQLiteOpenHelper sqliteHelp = Main.instance().moduleHandle.getSQLiteHelp();

            int unReadCount = PublicMessage.getUnReadCount(
                    sqliteHelp.getWritableDatabase(), paID);

            PublicMessage.delete(PAMessageViewAct.this, sqliteHelp.getWritableDatabase(), paID);

            ObjPaMessage objPaMessage = adapterPaMessage.getObjFromID(paID);
            if (null != objPaMessage) {
                adapterPaMessage.removeItem(objPaMessage);
                adapterPaMessage.notifyDataSetChanged();
            }

        }

    };
    //长按监听
    private AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view,
                                       int position, long id) {
            final ObjPaMessage paMessage = (ObjPaMessage) adapterPaMessage
                    .getItem(position);

            dialogForPaMsgItemLongPress.setParams(paMessage.id);
            dialogForPaMsgItemLongPress.show();

            return true;
        }
    };
    //点击监听
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            ObjPaMessage paMessage = (ObjPaMessage) adapterPaMessage
                    .getItem(position);
//            SQLiteOpenHelper sqliteHelp= Main.instance().moduleHandle.getSQLiteHelp();
//            PublicMessage.clearUnReadCount(PAMessageViewAct.this,sqliteHelp.getWritableDatabase(),paMessage.id);
            if (!getPublicAccountData(paMessage.id.trim())) {
                ShortCUtUntil.deleteShortCut(PAMessageViewAct.this, paMessage.name);
                clearTip(paMessage.id.trim());
                Toast.makeText(getApplicationContext(), "您已被取消该公众号授权，请联系管理员", Toast.LENGTH_SHORT).show();
                return;
            }
            PublicGridItem item = QuerryByID(paMessage.id.trim());
            if (item != null) {
                int type = item.type;
                if (type == 0 || type == 3) {
                    //普通公众号
                    Intent intent = new Intent();
                    intent.setClass(PAMessageViewAct.this, PublicAccountChatActNormal.class);
                    intent.putExtra("ACCOUNT_ID", item.id);
                    intent.putExtra("ACCOUNT_NAME", item.name);
                    intent.putExtra("IS_PUBLIC_ACCOUNT", true);
                    startActivity(intent);
                } else if (type == 2) {
                    //只有一个主入口的页面
                    if (item.urlObj.urlType == 0) {
                        //跳转html页面
                        Intent intent = new Intent();
                        LoginUser user = Sps_RWLoginUser.readUser(PAMessageViewAct.this);
                        String url;
                        if (user.telNumber != null) {
                            url = item.urlObj.publicUrl + "?userName=" + user.name + "&phoneNumber=" +
                                    user.telNumber + "&userID=" + HmIdUtils.getId(user) +
                                    "&LoginOragian=" + user.userOragian + "&LoginOragianCode=" + user.userOragianId;
                        } else {
                            url = item.urlObj.publicUrl + "?userName=" + user.name + "&currentTime=" +
                                    System.currentTimeMillis() + "&userID=" + HmIdUtils.getId(user) +
                                    "&LoginOragian=" + user.userOragian + "&LoginOragianCode=" + user.userOragianId;
                        }
                        intent.putExtra("URL", url);
                        intent.putExtra("NAME", item.name);
                        intent.putExtra("IsShowTitle", item.urlObj.isShowTitle);
                        intent.putExtra("URL_PARAM", item.urlObj.urlParam);
                        intent.putExtra(PAWebView.EXTRA_PUBLIC_ID, item.id);
                        intent.setClass(PAMessageViewAct.this, PAWebViewAct.class);
                        // startActivity(intent);
                        startActivity(intent);

                    } else if (item.urlObj.urlType == 1) {
                        //跳转weex页面
                        String url = item.urlObj.publicUrl;
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        StringBuilder builder = new StringBuilder();
                        builder.append(url);
                        Uri uri = Uri.parse(builder.toString());
                        if (TextUtils.equals("http", uri.getScheme()) || TextUtils.equals("https", uri.getScheme())) {
                            intent.setData(uri);
                            intent.putExtra("Title", item.name);
                            intent.putExtra("IsShowTitle", item.urlObj.isShowTitle);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.addCategory("com.SynwayOsc.android.intent.category.WEEX");
                            Map<String, Object> params = new HashMap<>();
                            params.put("SourceUrl", item.urlObj.urlParam);
                            params.put("UserId", loginUserID);
                            params.put("PaId", item.id);
                            WxMapData map = new WxMapData();
                            map.setWxMapData(params);
                            intent.putExtra("DATA", map);
                            startActivity(intent);
                        } else {
                            Toast.makeText(PAMessageViewAct.this, "跳转地址格式错误,请检查格式", Toast.LENGTH_LONG).show();
                        }
                    } else if (item.urlObj.urlType == 2) {

                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), "您已被取消该公众号授权，请联系管理员", Toast.LENGTH_SHORT).show();

            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (synGetPaMessage != null) {
            synGetPaMessage.stop();
        }
        if (dialogForPaMsgItemLongPress != null) {
            dialogForPaMsgItemLongPress.removeLsn();
        }
        if (onNewMsgReceive != null) {
            unregisterReceiver(onNewMsgReceive);
        }
    }

    //数据库中获取消息列表
    @Override
    public void onGet(ArrayList<ObjPaMessage> paMessageArrayList) {
        if (adapterPaMessage != null) {
            adapterPaMessage.reset(paMessageArrayList);
            adapterPaMessage.notifyDataSetChanged();
        }


    }

    // 收到新消息
    private BroadcastReceiver onNewMsgReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PublicMessage.ACTION_UPDATE_PUBLIC_ACCOUNT)) {
                String paID = intent.getExtras().getString(
                        PublicMessage.PUBLIC_ACCOUNT_ID);
                String paName = intent.getExtras().getString(
                        PublicMessage.PUBLIC_ACCOUNT_NAME);
                long time = intent.getExtras().getLong(
                        PublicMessage.PUBLIC_ACCOUNT_SHOW_TIME);
                int unReadCount = intent.getExtras().getInt(
                        PublicMessage.PUBLIC_ACCOUNT_UN_READ_COUNT);
                String briefMsg = intent.getExtras().getString(
                        PublicMessage.PUBLIC_ACCOUNT_BRIEF_MSG);

                // 在列表里找到它！
                ObjPaMessage objPaMessage = null;
                objPaMessage = adapterPaMessage.getObjFromID(paID);
                if (objPaMessage == null) {
                    // 列表里没有，那么就是新的。
                    objPaMessage = new ObjPaMessage();
                }
                objPaMessage.id = paID;
                //如果传递
                if (paName != null) {
                    objPaMessage.name = paName;
                }
                objPaMessage.showTime = time;
                objPaMessage.unReadCount = unReadCount;
                objPaMessage.briefMsg = briefMsg;
                adapterPaMessage.receiveBC(objPaMessage);
                adapterPaMessage.notifyDataSetChanged();
            } else if (intent.getAction().equals(PublicMessage.ACTION_CLEAR_PUBLIC_ACCOUNT)) {
                String paId = intent.getStringExtra(PublicMessage.PUBLIC_ACCOUNT_ID);
                ObjPaMessage objPaMessage = null;
                objPaMessage = adapterPaMessage.getObjFromID(paId);
                if (objPaMessage != null) {
                    objPaMessage.unReadCount = 0;
//                    adapterPaMessage.receiveBC(objPaMessage);
                    adapterPaMessage.notifyDataSetChanged();
                }
//                if(synGetPaMessage!=null){
//                    synGetPaMessage.start();
//                }
            } else if (intent.getAction().equals(PushUtil.PublicLastUnreadCountMinus.ACTION)) {
                String paId = intent.getStringExtra(
                        PushUtil.PublicLastUnreadCountMinus.EXTRA_PUBLIC_ACCOUNT_ID);
                int unReadCount = intent.getIntExtra(
                        PushUtil.PublicLastUnreadCountMinus.EXTRA_PUBLIC_UNREAD_COUNT, 0);
                ObjPaMessage objFromID = adapterPaMessage.getObjFromID(paId);
                if (objFromID != null) {
                    objFromID.unReadCount = unReadCount;
//                    adapterPaMessage.receiveBC(objFromID);
                    adapterPaMessage.notifyDataSetChanged();
                }

            }

        }
    };

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

    public void clearTip(String publicAccountID) {
        // 清除 消息提醒
        SetLCForPublicAccount.clearUnReadCount(this,
                Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase(),
                publicAccountID);
        if (Main.instance().handlerLCTCount != null) {
            Main.instance().handlerLCTCount.updateLCTCount();
        }
        //后续新增一个专门显示公众号最近消息的界面，也是需要清除的
        PublicMessage.clearUnReadCount(this,
                Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase(), publicAccountID);
    }

}
