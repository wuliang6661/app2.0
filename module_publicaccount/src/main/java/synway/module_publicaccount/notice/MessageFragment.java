package synway.module_publicaccount.notice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgNotice;
import synway.module_publicaccount.notice.job.FirstLoadNoticeRecord;
import synway.module_publicaccount.notice.job.SyncGetNoticeRecord;
import synway.module_publicaccount.public_chat.PAWebViewAct;
import synway.module_publicaccount.push.PushUtil;
import synway.module_publicaccount.weex_module.beans.WxMapData;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MessageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    public MessageFragment() {
        // Required empty public constructor
    }

    private ListView listView;
    private RefreshLayout refreshLayout;
    private NoticeAdapter adapter;
    private String loginUserID;
    private static final int PAGE_TYPE = 0;

    private SyncGetNoticeRecord syncGetNoticeRecord;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessageFragment newInstance(String param1, String param2) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        loginUserID = Sps_RWLoginUser.readUserID(getActivity());

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        refreshLayout = view.findViewById(R.id.dumbRefreshLayout);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()));
        refreshLayout.setOnLoadmoreListener(onLoadmoreListener);

        listView = view.findViewById(R.id.listView);

        adapter = new NoticeAdapter(getActivity(),noticeItemClick);
        listView.setAdapter(adapter);

        ArrayList<Obj_PublicMsgNotice> list;
        if (Main.instance() == null || Main.instance().moduleHandle == null) {
            list = new ArrayList<>();
        }
        else {
            list = FirstLoadNoticeRecord.load(
                    Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase(),PAGE_TYPE);
        }

        adapter.reset(list);
        adapter.refresh();

        registerReceiver();
        syncGetNoticeRecord = new SyncGetNoticeRecord();
        syncGetNoticeRecord.setOnGetRecordResult(onGetRecordResult);
        return view;
    }

    private void registerReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PushUtil.PublicNewMsg.NOTICE_ACTION);
        getActivity().registerReceiver(receiver,intentFilter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            Obj_PublicMsgNotice notice = (Obj_PublicMsgNotice) intent.getSerializableExtra(PushUtil.PublicNewMsg.EXTRA_PUBLIC_NEWMSG_SOBJ);
            adapter.addNewItemFromTop(notice);
            adapter.refresh();
        }
    };

    private SyncGetNoticeRecord.OnGetRecordResult  onGetRecordResult = new SyncGetNoticeRecord.OnGetRecordResult() {
        @Override public void onFinish(ArrayList<Obj_PublicMsgNotice> list) {

            for (Obj_PublicMsgNotice notice : list) {
                adapter.addNewItem(notice);
            }
            adapter.refresh();
        }
    };


    @Override public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
        }

        if (syncGetNoticeRecord != null) {
            syncGetNoticeRecord.stop();
        }
    }


    private OnLoadmoreListener onLoadmoreListener = new OnLoadmoreListener() {
        @Override public void onLoadmore(RefreshLayout refreshLayout) {
            syncGetNoticeRecord.start(adapter.getCount(),25,PAGE_TYPE);
            refreshLayout.finishLoadmore();
        }
    };

    private NoticeItemClick noticeItemClick = new NoticeItemClick() {
        @Override public void onItemClick(int position) {
            Obj_PublicMsgNotice objByPosition = adapter.getObjByPosition(position);
            if (objByPosition.clickItem != null) {
                if (objByPosition.clickItem.type == 1 ) {
                    //url类型
                    if (objByPosition.clickItem.clickUrlItem != null) {

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
                            String userInfo = Sps_RWLoginUser.readUser(getActivity()).userInfoJson;
                            if (userInfo == null) {
                                userInfo = "";
                            }
                            Log.d("dym------------------->", "userInfoJson= " + userInfo);
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
                                //这个与公众号无关，所以该项值可以填入一个空字符串
                                params.put("PaId", "");
                                params.put("SourceUrl", urlParm);
                                params.put("UserInfo", userInfo);
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
                    intent.putExtra(NoticeDetailActivity.CLICK_INFO_JSON,objByPosition.clickItem.clickInfoJson);
                    startActivity(intent);
                }
            }
        }
    };


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
