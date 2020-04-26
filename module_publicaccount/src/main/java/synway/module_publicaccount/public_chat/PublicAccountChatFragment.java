package synway.module_publicaccount.public_chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import synway.module_interface.config.userConfig.Sps_RWLoginUser;
import synway.module_publicaccount.Main;
import synway.module_publicaccount.R;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgBase;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgPicTxt;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgTaskNotice;
import synway.module_publicaccount.analytical.obj.view.Obj_ViewUrlTxt;
import synway.module_publicaccount.db.table_util.Table_PublicMenu;
import synway.module_publicaccount.notice.NoticeDetailActivity;
import synway.module_publicaccount.public_chat.adapter.IOnPublicChatItemClick;
import synway.module_publicaccount.public_chat.adapter.PAChatAdapter;
import synway.module_publicaccount.push.PushUtil;
import synway.module_publicaccount.rtvideovoice.rtvoice.RTVoiceView;
import synway.module_publicaccount.weex_module.beans.WxMapData;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PublicAccountChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PublicAccountChatFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LOGIN_USER_ID = "LOGIN_USER_ID";
    private static final String PUBLIC_GUID = "PUBLIC_GUID";
    private static final String PUSH_MSG_PAGE_CODE = "PUSH_MSG_PAGE_CODE";
    private static final String PUSH_MSG_PAGE_NAME = "PUSH_MSG_PAGE_NAME";

    // TODO: Rename and change types of parameters
    private String pushMsgPageCode = "";
    private String pushMsgPageName = "";
    private String publicGUID;
    private String loginUserID;
    private RTVoiceView rtVoiceView = null;
    private ListView listView;
    private RefreshLayout refreshLayout;
    private PAChatAdapter adapter;
    private SyncGetPublicChatRecord syncGetPublicChatRecord;
    private SQLiteDatabase db;
    private NewMsgReceiver onNewMsgReceive = null;

    public PublicAccountChatFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PublicAccountChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PublicAccountChatFragment newInstance(String publicGUID, String loginUserID, String pushMsgPageCode, String pushMsgPageName) {
        PublicAccountChatFragment fragment = new PublicAccountChatFragment();
        Bundle args = new Bundle();
        args.putString(PUBLIC_GUID, publicGUID);
        args.putString(PUSH_MSG_PAGE_CODE, pushMsgPageCode);
        args.putString(PUSH_MSG_PAGE_NAME, pushMsgPageName);
        args.putString(LOGIN_USER_ID, loginUserID);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pushMsgPageCode = getArguments().getString(PUSH_MSG_PAGE_CODE);
            pushMsgPageName = getArguments().getString(PUSH_MSG_PAGE_NAME);
            publicGUID = getArguments().getString(PUBLIC_GUID);
            loginUserID = getArguments().getString(LOGIN_USER_ID);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_public_account_chat, container, false);
        refreshLayout = view.findViewById(R.id.dumbRefreshLayout);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()));
        refreshLayout.setOnLoadmoreListener(onLoadmoreListener);
        refreshLayout.setEnableAutoLoadmore(false);


        listView = view.findViewById(R.id.listView);
        adapter = new PAChatAdapter(publicGUID, getActivity(), iOnPublicChatItemClick);
        listView.setAdapter(adapter);
        db = Main.instance().moduleHandle.getSQLiteHelp().getReadableDatabase();
        ArrayList<Obj_PublicMsgBase> objPublicMsgBases = FirstLoadPublicRecord.load(db, publicGUID, pushMsgPageCode,pushMsgPageName);

        adapter.reset(objPublicMsgBases);
        adapter.refresh();

        registerReceiver();

        syncGetPublicChatRecord = new SyncGetPublicChatRecord();
        syncGetPublicChatRecord.setOnGetRecordResult(onGetRecordResult);


        return view;
    }


    private void registerReceiver() {
        onNewMsgReceive = new NewMsgReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(PushUtil.PublicNewMsg.getAction(publicGUID));
        getActivity().registerReceiver(onNewMsgReceive, filter);

        IntentFilter deleteMsgFilter = new IntentFilter();
        deleteMsgFilter.addAction(PushUtil.PublicDeleteMsg.getAction(publicGUID));
        getActivity().registerReceiver(deleteMsgReceiver, deleteMsgFilter);
    }


    /**
     * 新的公众推送消息
     */
    private class NewMsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Obj_PublicMsgBase obj = (Obj_PublicMsgBase) intent.getSerializableExtra(PushUtil.PublicNewMsg.EXTRA_PUBLIC_NEWMSG_SOBJ);
            if (obj instanceof Obj_PublicMsgTaskNotice) {
                Log.d("dym------------------->", "shoudaoguangbo :tasknotice");
            } else if (obj instanceof Obj_PublicMsgPicTxt) {
                Log.d("dym------------------->", "shoudaoguangbo :pictxt");
            }
            obj.MsgType = intent.getIntExtra(PushUtil.PublicNewMsg.EXTRA_PUBLIC_NEWMSG_MSGTYPE, 0);
            obj.localTime = intent.getLongExtra(PushUtil.PublicNewMsg.EXTRA_PUBLIC_NEWMSG_TIME, System.currentTimeMillis());
            obj.msgID = intent.getStringExtra(PushUtil.PublicNewMsg.EXTRA_PUBLIC_NEWMSG_MSG_GUID);
            String pushMsgPageCodeFromIntent = intent.getStringExtra(
                PushUtil.PublicNewMsg.EXTRA_PUBLIC_NEWMSG_MSG_PUSH_PAGE_CODE);
            String pushMsgPageNameFromIntent = intent.getStringExtra(
                PushUtil.PublicNewMsg.EXTRA_PUBLIC_NEWMSG_MSG_PUSH_PAGE_NAME);

            boolean isMsgUpdate = intent.getBooleanExtra(
                PushUtil.PublicNewMsg.EXTRA_PUBLIC_NEWMSG_MSG_IS_UPDATE, false);

            if (isMsgUpdate) {
                adapter.remove(obj.msgID);
                adapter.refresh();
            }

            if (TextUtils.isEmpty(pushMsgPageCode) &&
                TextUtils.isEmpty(pushMsgPageCodeFromIntent) &&
                TextUtils.isEmpty(pushMsgPageNameFromIntent)) {

                //当前页面为无分类的情况，且收到新的消息也是无分类的才可以加入到界面
                adapter.addNewItemFromTop(obj);
                adapter.refresh();
            } else {
                //当前页面为分类的情况，且收到新的消息 分类的信息包含当前页面的 pushMsgPage
                if (!TextUtils.isEmpty(pushMsgPageCodeFromIntent)) {

                    String[] splits = pushMsgPageCodeFromIntent.split("\\|", -1);
                    boolean isNeedAdd = false;
                    for (int i = 0; i < splits.length; i++) {
                        if (splits[i].equals(pushMsgPageCode)) {
                            isNeedAdd = true;
                            break;
                        }
                    }
                    if (isNeedAdd) {
                        adapter.addNewItemFromTop(obj);
                        adapter.refresh();
                    }

                    return;
                }

                if (!TextUtils.isEmpty(pushMsgPageNameFromIntent)) {
                    String[] splits = pushMsgPageNameFromIntent.split("\\|", -1);
                    boolean isNeedAdd = false;
                    for (int i = 0; i < splits.length; i++) {
                        if (splits[i].equals(pushMsgPageName)) {
                            isNeedAdd = true;
                            break;
                        }
                    }
                    if (isNeedAdd) {
                        adapter.addNewItemFromTop(obj);
                        adapter.refresh();
                    }

                    return;
                }
            }

        }

    }


    private BroadcastReceiver deleteMsgReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            String msgGUID = intent.getStringExtra(
                PushUtil.PublicDeleteMsg.EXTRA_PUBLIC_DELETE_MSG_GUID);
            Log.d("dym------------------->", "收到删除的消息了 id= "+msgGUID);

            if (!TextUtils.isEmpty(msgGUID)) {
                adapter.remove(msgGUID);
                adapter.refresh();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (onNewMsgReceive != null) {
            getActivity().unregisterReceiver(onNewMsgReceive);
        }

        if (deleteMsgReceiver != null) {
            getActivity().unregisterReceiver(deleteMsgReceiver);
        }

        if (syncGetPublicChatRecord != null) {
            syncGetPublicChatRecord.stop();
        }
    }


    private OnLoadmoreListener onLoadmoreListener = new OnLoadmoreListener() {
        @Override
        public void onLoadmore(RefreshLayout refreshLayout) {
            int lastPos = adapter.getCount() - 1;
            if (lastPos < 0) {
                return;
            }
            Obj_PublicMsgBase base = (Obj_PublicMsgBase) adapter.getItem(lastPos);
            syncGetPublicChatRecord.start(publicGUID, base.msgID, db, pushMsgPageCode,pushMsgPageName);

            refreshLayout.finishLoadmore();
        }
    };

    private SyncGetPublicChatRecord.OnGetRecordResult onGetRecordResult = new SyncGetPublicChatRecord.OnGetRecordResult() {
        @Override
        public void onFinish(ArrayList<Obj_PublicMsgBase> list) {
            if (list != null && list.size() > 0) {
                for (Obj_PublicMsgBase base : list) {
                    adapter.addNewItem(base);
                }
                adapter.refresh();
            }

        }
    };

    private IOnPublicChatItemClick iOnPublicChatItemClick = new IOnPublicChatItemClick() {
        @Override
        public void onUrlTextItemClick(Obj_ViewUrlTxt objViewUrlTxt) {
//            FileTestLog.write("PublicAccountChatFragmentTest","IS_DISPLAY_BANNER: "+objViewUrlTxt.isShowTitle+"   SOURCE_URL:"+objViewUrlTxt.sourceUrl);
            //！！！这个需要从协议里面拿到然后进行相应的更新,目前从menu里面取出来
            String wxSourceUrl = "";
            if (TextUtils.isEmpty(objViewUrlTxt.sourceUrl)) {
                wxSourceUrl = getWxSourceUrlFromFirstMenu(publicGUID);
            } else {
                wxSourceUrl = objViewUrlTxt.sourceUrl;
            }
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                StringBuilder builder = new StringBuilder();
                builder.append(objViewUrlTxt.url);
                Uri uri = Uri.parse(builder.toString());
                if (TextUtils.equals("http", uri.getScheme()) || TextUtils.equals("https", uri.getScheme())) {
                    intent.setData(uri);
                    intent.putExtra("Title", objViewUrlTxt.urlName);
                    intent.putExtra("IsShowTitle", objViewUrlTxt.isShowTitle);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.addCategory("com.SynwayOsc.android.intent.category.WEEX");
                    Map<String, Object> params = new HashMap<>();
                    params.put("SourceUrl", wxSourceUrl);
                    params.put("UserId", loginUserID);
                    params.put("PaId", publicGUID);
                    params.put("QueryData", objViewUrlTxt.data);
                    WxMapData map = new WxMapData();
                    map.setWxMapData(params);
                    intent.putExtra("DATA", map);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "跳转地址格式错误,请检查格式", Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "报错。。。。" + e.toString(), Toast.LENGTH_LONG).show();
            }
        }


        @Override
        public void onTaskNoticeItemClick(int position) {
            Obj_PublicMsgTaskNotice objByPosition = (Obj_PublicMsgTaskNotice) adapter.getItem(position);
            if (objByPosition.clickItem != null) {
                if (objByPosition.clickItem.type == 1) {
                    //url类型
                    if (objByPosition.clickItem.clickUrlItem != null) {
                        Log.d("dym------------------->", "urlType= " + objByPosition.clickItem.clickUrlItem.urlType);
                        if (objByPosition.clickItem.clickUrlItem.urlType == 0) {
                            //html
                            Log.d("dym------------------->", "html...");
                            Intent intent = new Intent();
                            intent.putExtra("URL", objByPosition.clickItem.clickUrlItem.url);
                            intent.putExtra("NAME", objByPosition.clickItem.clickUrlItem.urlName);
                            intent.setClass(getActivity(), PAWebViewAct.class);
                            startActivity(intent);
                        } else if (objByPosition.clickItem.clickUrlItem.urlType == 1) {
                            //weex
                            Log.d("dym------------------->", "weex...");
                            String url = objByPosition.clickItem.clickUrlItem.url;
                            String urlTitle = objByPosition.clickItem.clickUrlItem.urlName;
                            String urlParm = objByPosition.clickItem.clickUrlItem.sourceUrl;
                            int isShowTitle = objByPosition.clickItem.clickUrlItem.isShowTitle;
//                            String userInfo = Sps_RWLoginUser.readUser(getActivity()).userInfoJson;
//                            if (userInfo == null) {
//                                userInfo = "";
//                            }
//                            Log.d("dym------------------->", "userInfoJson= " + userInfo);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            StringBuilder builder = new StringBuilder();
                            builder.append(url);
                            Uri uri = Uri.parse(builder.toString());
                            if (TextUtils.equals("http", uri.getScheme()) || TextUtils.equals("https", uri.getScheme())) {
                                intent.setData(uri);
                                intent.putExtra("Title", urlTitle);
                                intent.putExtra("IsShowTitle", isShowTitle);
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                intent.addCategory("com.SynwayOsc.android.intent.category.WEEX");
                                Map<String, Object> params = new HashMap<>();
                                params.put("UserId", loginUserID);
                                params.put("PaId", publicGUID);
                                params.put("SourceUrl", urlParm);
//                                params.put("UserInfo", userInfo);
                                WxMapData map = new WxMapData();
                                map.setWxMapData(params);
                                intent.putExtra("DATA", map);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getActivity(), "跳转地址格式错误,请检查格式", Toast.LENGTH_LONG).show();
                            }

                        }else if(objByPosition.clickItem.clickUrlItem.urlType == 2){
                            //预留给打开本地原生的应用
                        }

                    } else {
                        Toast.makeText(getActivity(), "网址出错了...", Toast.LENGTH_SHORT).show();
                    }

                } else if (objByPosition.clickItem.type == 2) {
                    //通知消息
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), NoticeDetailActivity.class);
                    intent.putExtra(NoticeDetailActivity.CLICK_INFO_JSON, objByPosition.clickItem.clickInfoJson);
                    startActivity(intent);
                }
            }
        }
    };


    private String getWxSourceUrlFromFirstMenu(String publicGUID) {
        String sql = "select * from " + Table_PublicMenu._TABLE_NAME + " where " + Table_PublicMenu.PAM_ID +
                " = '" + publicGUID + "' ";

        Cursor cursor = Main.instance().moduleHandle.getSQLiteHelp()
                .getReadableDatabase()
                .rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            String wxSourceUrl = cursor.getString(
                    cursor.getColumnIndex(Table_PublicMenu.PAC_urlParam));

            if (!TextUtils.isEmpty(wxSourceUrl)) {
                return wxSourceUrl;
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

}
