package synway.module_publicaccount.public_list_new;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import qyc.library.control.dialog_msg.DialogMsg;
import synway.module_interface.AppConfig;
import synway.module_interface.config.BaseUtil;
import synway.module_interface.config.netConfig.NetConfig;
import synway.module_interface.config.netConfig.Sps_NetConfig;
import synway.module_interface.config.userConfig.Sps_RWLoginUser;
import synway.module_interface.lastcontact.LastContact;
import synway.module_interface.module.HomeFragment;
import synway.module_interface.sharedate.SharedPreferencesQ;
import synway.module_publicaccount.Main;
import synway.module_publicaccount.R;
import synway.module_publicaccount.public_chat.PAWebView;
import synway.module_publicaccount.public_chat.PublicAccountChatActNormal;
import synway.module_publicaccount.public_chat.SetLCForPublicAccount;
import synway.module_publicaccount.public_chat.bottom.Base64Helper;
import synway.module_publicaccount.public_list_new.adapter.PublicGridAdapter;
import synway.module_publicaccount.public_list_new.adapter.PublicGridItem;
import synway.module_publicaccount.public_message.PublicMessage;
import synway.module_publicaccount.public_message.TitleViewPaMsg;
import synway.module_publicaccount.public_message.TitleViewPaMsg_HX;
import synway.module_publicaccount.public_message.TitleViewPaMsg_YDJW;
import synway.module_publicaccount.publiclist.PicidPublicidBean;
import synway.module_publicaccount.publiclist.SyncGetHeadThu;
import synway.module_publicaccount.until.AppUtil;
import synway.module_publicaccount.until.DownLoad.SyncDownAPK;
import synway.module_publicaccount.until.NetUrlUntil;
import synway.module_publicaccount.weex_module.beans.WxMapData;

import static synway.module_publicaccount.until.AppUtil.doStartApplicationWithPackageName;
import static synway.module_publicaccount.until.DownLoad.InstallApk.startInstall;
import static synway.module_publicaccount.until.PicUtil.getApkPath;
import static synway.module_publicaccount.until.StringUtil.getCLearFileName;

/**
 * Created by leo on 2018/6/15.
 */

public class PublicAccountFragment extends HomeFragment {

    //清理聊天记录
    public static final String CLEAR_CHAT_RECORD = "clear_chat_record";
    //进入详情等清零未读数
    public static final String ACTION_CLEAR = "lastcontact.clear";
    //最近联系人处直接删除
    public static final String ACTION_DELETE = "lastcontact.del";

    private View view;

    private RecyclerView recyclerView;
    //工具类，用于帮助RecyclerView实现 item的滑动消除、拖拽 效果
    private ItemTouchHelper itemTouchHelper;

    //ItemTouchHelper的反馈类
    private ItemTouchHelper.Callback callback;

    private PublicGridAdapter gridAdapter;


    //登录用户ID
    private String loginUserID;
    private NetConfig netConfig;

    private ProgressDialog pgDown;
    private SyncGetHeadThu syncGetHeadThu;
    private SyncDownAPK syncDownAPK;
    private SwipeRefreshLayout refreshLayout;
    private volatile boolean isRefreshing = false;
    private SyncGetPublicByHttp3 syncGetPublicByHttp3;
    private SynGetPublicAccountDB synGetPublicAccountDB;
    private SyncGetAllMenuList syncGetAllMenuList;

    public PublicAccountFragment() {
        super.imageView_normal = R.drawable.model_home_public_normal_png;
        super.imageView_selected = R.drawable.model_home_public_selected_png;
        super.text = "应用";
    }

    private LocalItemInterface publicItemInterface;
    private TitleViewPaMsg titleViewPaMsg;

    /**
     * 设置本地原生应用接口
     *
     * @param publicItemInterface
     */
    public void setLocalInterface(LocalItemInterface publicItemInterface) {
        this.publicItemInterface = publicItemInterface;
    }

    // 根据推送更新公众号
    private SharedPreferencesQ sharedPreferencesQ = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("dym------------------->", "PublicAccountFragment onCreate...");
        super.onCreate(savedInstanceState);
        if (AppConfig.PUBLIC_UNREAD_TYPE == 0) {
            // 注册新消息的广播
            IntentFilter ifl = new IntentFilter();
            //清理聊天记录
            ifl.addAction(CLEAR_CHAT_RECORD);
            //新消息
            ifl.addAction(LastContact.ACTION_UPDATE_CONTACT);
            //进入详情等清零未读数
            ifl.addAction(ACTION_CLEAR);
            //最近联系人处直接删除
            ifl.addAction(ACTION_DELETE);
            //更新未读数
            ifl.addAction(LastContact.ACTION_UPDATE_UNREAD_COUNT);
            //软件清理应用消息后的广播接收
            ifl.addAction(PublicMessage.ACTION_UPDATE_PUBLIC_ACCOUNT_UNREAD);
            getActivity().registerReceiver(onUnReadChangeReceive, ifl);
        } else if (AppConfig.PUBLIC_UNREAD_TYPE == 1) {
            // 注册新消息的广播
            IntentFilter ifl = new IntentFilter();
            //公众号收到消息
            ifl.addAction(PublicMessage.ACTION_UPDATE_PUBLIC_ACCOUNT);
            ifl.addAction(PublicMessage.ACTION_CLEAR_PUBLIC_ACCOUNT);
            ifl.addAction(PublicMessage.ACTION_UNREAD_COUNT_MINUS);
            ifl.addAction(PublicMessage.ACTION_DELETE_PUBLIC_ACCOUNT);
            //软件清理应用消息后的广播接收
            ifl.addAction(PublicMessage.ACTION_UPDATE_PUBLIC_ACCOUNT_UNREAD);
            getActivity().registerReceiver(onPaMessageReceive, ifl);

        } else {
            // 注册新消息的广播
            IntentFilter ifl1 = new IntentFilter();
            //清理聊天记录
            ifl1.addAction(CLEAR_CHAT_RECORD);
            //新消息
            ifl1.addAction(LastContact.ACTION_UPDATE_CONTACT);
            //进入详情等清零未读数
            ifl1.addAction(ACTION_CLEAR);
            //最近联系人处直接删除
            ifl1.addAction(ACTION_DELETE);
            //软件清理应用消息后的广播接收
            ifl1.addAction(PublicMessage.ACTION_UPDATE_PUBLIC_ACCOUNT_UNREAD);
            //更新未读数
            ifl1.addAction(LastContact.ACTION_UPDATE_UNREAD_COUNT);
            getActivity().registerReceiver(onUnReadChangeReceive, ifl1);


            IntentFilter ifl = new IntentFilter();
            //公众号收到消息
            ifl.addAction(PublicMessage.ACTION_UPDATE_PUBLIC_ACCOUNT);
            ifl.addAction(PublicMessage.ACTION_CLEAR_PUBLIC_ACCOUNT);
            ifl.addAction(PublicMessage.ACTION_UNREAD_COUNT_MINUS);
            ifl.addAction(PublicMessage.ACTION_DELETE_PUBLIC_ACCOUNT);
            //软件清理应用消息后的广播接收
            ifl.addAction(PublicMessage.ACTION_UPDATE_PUBLIC_ACCOUNT_UNREAD);
            getActivity().registerReceiver(onPaMessageReceive, ifl);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("dym------------------->", "PublicAccountFragment onCreateView... view = " + view);
        //对view进行null判断的原因是由于修改代码之后，会频繁调到onCreateView，为了和以前代码保持一致，就加上非空判断。
        if (view == null) {
            view = inflater.inflate(R.layout.model_public_list_fragment, container, false);
            init();
            gridAdapter = new PublicGridAdapter(getActivity());
            gridAdapter.setOnItemClickEventListener(onItemClickEventListener);
            loginUserID = Sps_RWLoginUser.readUserID(getActivity());
            netConfig = Sps_NetConfig.getNetConfigFromSpf(getActivity());
            refreshLayout = view.findViewById(R.id.swipeRefreshLayout);
            refreshLayout.setOnRefreshListener(onRefreshListener);
            refreshLayout.setColorSchemeResources(R.color.lib_blue);
            recyclerView = view.findViewById(R.id.recyclerview_public_list_new);
            recyclerView.setHasFixedSize(true);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int viewType = gridAdapter.getItemViewType(position);
                    switch (viewType) {
                        case 1://标题
                            return 4;
                        default:
                            return 1;
                    }
                }
            });
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setAdapter(gridAdapter);
//            callback = new SimpleItemTouchHelperCallback(gridAdapter);
//            itemTouchHelper = new ItemTouchHelper(callback);
//            itemTouchHelper.attachToRecyclerView(recyclerView);


        }
        syncGetHeadThu = new SyncGetHeadThu(netConfig.publicServerIP, netConfig.publicServerPort, getActivity());
        syncGetHeadThu.setOnListen(onGetHeadThu);
        synGetPublicAccountDB = new SynGetPublicAccountDB();
        synGetPublicAccountDB.setListener(onGetPublicAccountDBListener);
        synGetPublicAccountDB.start();
        syncGetAllMenuList = new SyncGetAllMenuList(getContext());
        syncGetPublicByHttp3 = new SyncGetPublicByHttp3();
        syncGetPublicByHttp3.setLsn(onGetAccountByHttp);
        //查看是否需要更新通讯录
        sharedPreferencesQ = new SharedPreferencesQ(getActivity(), "isPublicUpdate");
        boolean isUpdate = sharedPreferencesQ.get("isPublicUpdate", true);//默认为true,以便第一次安装后更新公众号.
        if (isUpdate) {
            startRefresh();
        }
        //监听推送是否需要更新公众号
        sharedPreferencesQ.addDataChangeListener(new SharedPreferencesQ.OnShardPreferencesQListener() {
            @Override
            public void onChanged(String name, boolean isSelf) {
                if (!isSelf && !isRefreshing && sharedPreferencesQ.get("isPublicUpdate", true)) {
                    Log.d("zjw", "startRefresh");
                    startRefresh();
                } else {
                    Log.d("zjw", "unStartRefresh:isSelf=" + isSelf + "  isRefresh");
                }
            }
        });

        return view;
    }

    private void init() {
        pgDown = new ProgressDialog(getActivity());
        pgDown.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pgDown.setMessage("下载中请等待");
        pgDown.setCanceledOnTouchOutside(false);
        pgDown.setCancelable(false);
        pgDown.setMax(100);
        pgDown.setProgress(0);
        pgDown.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return false;
            }
        });

    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            //手动下拉，当正在刷新的情况手动下拉是无效的
            isRefreshing = true;
            Log.d("dym------------------->", "手动下拉一次");
            refresh();
        }
    };

    private void refresh() {
        Log.d("dym------------------->", "refresh。。。");
        if (syncGetPublicByHttp3 != null) {
            syncGetPublicByHttp3.start(netConfig.publicServerIP, netConfig.publicServerPort, loginUserID);
        }
        if (syncGetAllMenuList != null) {
            syncGetAllMenuList.start(netConfig.publicServerIP, netConfig.publicServerPort, loginUserID);
        }
    }

    private synchronized void startRefresh() {
        if (isRefreshing) {
            Log.d("dym------------------->", "startRefresh() 正在更新中，拒绝更新");
            return;
        }
        isRefreshing = true;
        //post是为了防止onCreate的时候马上调用refreshLayout.setRefreshing(true),而SwipeRefreshLayout
        //的onMeasure尚未完成,导致下拉的小圆圈不会出现
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                // 不会触发onRefreshListener,只是会显示ui
                refreshLayout.setRefreshing(true);
                refresh();
            }
        });
    }

    private synchronized void stopRefresh() {
        refreshLayout.setRefreshing(false);
        isRefreshing = false;
    }

    private SyncGetHeadThu.IOnGetHeadThu onGetHeadThu = new SyncGetHeadThu.IOnGetHeadThu() {
        @Override
        public void onHeadGet(String ID) {
            gridAdapter.refresh();

        }

        @Override
        public void onFailGet(String ID) {
        }
    };

    private PublicGridAdapter.OnItemClickEventListener onItemClickEventListener = new PublicGridAdapter.OnItemClickEventListener() {
        @Override
        public void onItemClick(View view, int position) {
            PublicGridItem item = gridAdapter.getDataByPosition(position);

            //设置点击接口，如果存在的话，点击事件由接口来处理
            if (publicItemInterface != null) {
                publicItemInterface.click(item);
                return;
            }

            if (item != null) {
                int type = item.type;
                if (type == 0 || type == 3) {
                    //普通公众号
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), PublicAccountChatActNormal.class);
                    intent.putExtra("ACCOUNT_ID", item.id);
                    intent.putExtra("ACCOUNT_NAME", item.name);
                    intent.putExtra("IS_PUBLIC_ACCOUNT", true);
                    startActivity(intent);
                } else if (type == 1) {
                    //APP应用
                    PackageInfo packageInfo = AppUtil.isAvilible(getActivity(), item.app_information.app_packangename);
                    if (packageInfo == null) {
                        //尚未安装
                        String url = item.app_information.app_download_url;
                        // Log.d("dym------------------->", "url = "+url);
                        //应用商店的app下载地址 进行ip 和port的替换
                        url = NetUrlUntil.changeIpPortFromUrl(url, netConfig.httpIP, netConfig.httpPort);
                        // Log.d("dym------------------->", "change = "+url);
                        downloadApk(url);
                    } else {
                        if (packageInfo.versionName.equals(item.app_information.app_version)) {
                            //版本一样，直接打开
                            doStartApplicationWithPackageName(getActivity(), packageInfo);
                        } else {
                            //需要去更新
                            String url = item.app_information.app_download_url;
                            // Log.d("dym------------------->", "url = "+url);
                            //应用商店的app下载地址 进行ip 和port的替换
                            url = NetUrlUntil.changeIpPortFromUrl(url, netConfig.httpIP, netConfig.httpPort);
                            // Log.d("dym------------------->", "change = "+url);
                            downloadApk(url);
                        }
                    }
                } else if (type == 2) {
                    //只有一个主入口的页面
                    if (item.urlObj.urlType == 0) {
                        //跳转html页面
                        Intent intent = new Intent();
                        String name = Base64Helper.getBASE64(Sps_RWLoginUser.readUser(getActivity()).name);
//                        String ID =Sps_RWLoginUser.readUser(this).ID;
                        String phoneNumber = Sps_RWLoginUser.readUserTelNumber(getActivity());
                        String url;
                        if (phoneNumber != null) {
                            url = item.urlObj.publicUrl + "?userName=" + name + "&phoneNumber=" + phoneNumber + "&userID=" + loginUserID;
                        } else {
                            url = item.urlObj.publicUrl + "?userName=" + name + "&currentTime=" + System.currentTimeMillis() + "&userID=" + loginUserID;
                        }
                        // intent.putExtra("URL", url);
                        // intent.putExtra("NAME", item.name);
                        // intent.putExtra("IsShowTitle", item.urlObj.isShowTitle);
                        // intent.putExtra("URL_PARAM", item.urlObj.urlParam);
                        // intent.setClass(getActivity(), PAWebViewAct.class);
                        // startActivity(intent);
                        PAWebView
                                .builder()
                                .setUrl(url)
                                .setName(item.name)
                                .setIsShowTitle(item.urlObj.isShowTitle)
                                .setUrlParam(item.urlObj.urlParam)
                                .start(getActivity());

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
                            Toast.makeText(getActivity(), "跳转地址格式错误,请检查格式", Toast.LENGTH_LONG).show();
                        }

                    } else if (item.urlObj.urlType == 2) {
                        //跳转本地原生应用,测试的时候可以把type写死成3
                        if (publicItemInterface != null) {
                            publicItemInterface.click(item);

                        } else {
                            Toast.makeText(getActivity(), "本地原生应用未实现", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
        // @Override public void onItemLongClick(View view, int position) {
        //     final PublicGridItem item = gridAdapter.getDataByPosition(position);
        //     if (item != null) {
        //         if (item.type == 0) {
        //             //是否加入桌面快捷方式
        //             Window window = DialogTool.dialog(getActivity(), R.layout.model_public_dialog);
        //             LinearLayout uploadIn = (LinearLayout) window.findViewById(R.id.uploadIntellgence);
        //             uploadIn.setVisibility(View.GONE);
        //             LinearLayout addNormalApplication = (LinearLayout) window.findViewById(R.id.addNormalApplication);
        //             addNormalApplication.setVisibility(View.GONE);
        //             LinearLayout addToDeskTop = (LinearLayout) window.findViewById(R.id.addToDeskTop);
        //             addToDeskTop.setVisibility(View.VISIBLE);
        //             addToDeskTop.setOnClickListener(new View.OnClickListener() {
        //                 @Override public void onClick(View v) {
        //                     Intent intent = new Intent(Intent.ACTION_MAIN);
        //                     ComponentName cn = new ComponentName(getActivity().getPackageName(),"synway.module_publicaccount.public_chat.PublicAccountChatAct");
        //                     intent.setComponent(cn);
        //                     intent.putExtra("ACCOUNT_ID", item.id);
        //                     intent.putExtra("ACCOUNT_NAME", item.name);
        //                     String path = BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" +item.id;
        //                     addShortcut(getActivity(), item.name, BitmapFactory.decodeFile(path), intent, false);
        //                 }
        //             });
        //         }
        //     }
        // }
    };

    @Override
    public View onCreateTitleView(Activity activity) {
        View titleView = null;
        if (AppConfig.PUBLIC_UNREAD_TYPE != 0) {
            if (AppConfig.IS_HX_VERSION) {
                titleViewPaMsg = new TitleViewPaMsg_HX();
            } else {
                titleViewPaMsg = new TitleViewPaMsg_YDJW();
            }
            titleView = titleViewPaMsg.initTitleView(activity);
        }
        return titleView;
    }

    private void downloadApk(String url) {
        final String path = getApkPath();
        File apkSaveFolder = new File(path);
        if (!apkSaveFolder.exists()) {
            apkSaveFolder.mkdirs();
        }
        if (null != syncDownAPK) {
            syncDownAPK.stop();
        }
        //再次重置一下pgDown
        init();
        syncDownAPK = new SyncDownAPK();
        syncDownAPK.setLsn(onDownApkLsn);
        syncDownAPK.start(path, getCLearFileName(url), url);
    }

    private SyncDownAPK.IOnDownApkLsn onDownApkLsn = new SyncDownAPK.IOnDownApkLsn() {

        @Override
        public void onStart() {
            if (pgDown.isShowing()) {
                pgDown.dismiss();
            }
            pgDown.setProgress(0);
            pgDown.show();
        }

        @Override
        public void onProgress(int progress) {
            pgDown.setProgress(progress);
        }

        @Override
        public void onFinish(File resultFile) {
            pgDown.setProgress(100);
            pgDown.dismiss();
            Toast.makeText(getActivity(), "下载成功", Toast.LENGTH_SHORT).show();
            startInstall(getActivity(), resultFile);
        }

        @Override
        public void onFail(String error) {
            pgDown.dismiss();
            DialogMsg.showDetail(getActivity(), "下载失败", "下载失败，请重试", error);
        }

    };

    private SynGetPublicAccountDB.OnGetPublicAccountDBListener onGetPublicAccountDBListener = new SynGetPublicAccountDB.OnGetPublicAccountDBListener() {
        @Override
        public void onResult(ArrayList<PublicGridItem> resultList) {
            if (resultList != null && resultList.size() == 0) {
                gridAdapter.reset(resultList);
                gridAdapter.refresh();
                //清除保存的数据顺序信息
                OrderConfigRW.clear(Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase());
                return;
            }

            //对查询的数据进行排序处理
//            String orderData = OrderConfigRW.read(
//                Main.instance().moduleHandle.getSQLiteHelp().getReadableDatabase());
            String orderData = null;
            Log.d("dym------------------->", "onGetPublicAccountDBListener orderData= " + orderData);
            if (!TextUtils.isEmpty(orderData)) {
                //说明当前app已经有存储过顺序的数据
                Log.d("dym------------------->", "orderData= " + orderData);
                ArrayList<PublicGridItem> sortResultList = sortResultList(orderData, resultList);
                gridAdapter.reset(sortResultList);
                gridAdapter.refresh();
                Log.d("dym------------------->", "进行重排序");
            } else {
                Log.d("dym------------------->", "暂时没有存储过顺序的数据");
                //进行数据刷新
                gridAdapter.reset(resultList);
                gridAdapter.refresh();
            }

            downPublicIcon(resultList);

        }
    };

    private void saveOrder(ArrayList<PublicGridItem> list) {
        String orderStr = "";
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                orderStr = orderStr + list.get(i).id;
                if (i != (list.size() - 1)) {
                    orderStr = orderStr + "|";
                }
            }
        }
        //写到数据库
        Log.d("dym------------------->", "onGetAccountByHttp 将新的顺序写入数据库" + orderStr);
        OrderConfigRW.write(orderStr, Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase());
    }


    private ArrayList<PublicGridItem> sortResultList(String orderData, ArrayList<PublicGridItem> list) {
        ArrayList<PublicGridItem> resultList = new ArrayList<>();
        String[] strs = orderData.split("\\|", -1);
        //这里其实也存在数据库存储的顺序数据和数据库中读出的数据不一致,但是可能性非常小
        ArrayList<String> containIds = new ArrayList<>();

        //提取有顺序信息的数据加入resultList
        for (int i = 0; i < strs.length; i++) {
            String publicId = strs[i];
            for (PublicGridItem item : list) {
                if (publicId.equals(item.id)) {
                    resultList.add(item);
                    containIds.add(item.id);
                }
            }
        }

        //将数据库中读取处理的list中没有顺序信息的数据也加入resultList
        //containIds加入的id都是list里面包含的了,所以只有resultList的size大于containIds的size才有将
        //没有顺序信息的数据加上去的必要
        if (containIds.size() > 0) {
            if (list.size() > containIds.size()) {
                Log.d("dym------------------->", "读取本地的小可能性事件...");
                //数据不一致的小可能性出现后
                for (PublicGridItem item : list) {
                    if (!containIds.contains(item.id)) {
                        resultList.add(item);
                    }
                }
            }
        } else {
            //说明都是不含顺序信息的数据
            resultList = list;

        }
        if (list.size() != containIds.size()) {
            //将新的顺序写入数据库
            Log.d("dym------------------->", "数据库读取数据的情况下 将新的顺序写入数据库");
            saveOrder(resultList);
        }
        Log.d("dym------------------->", "sortResultList.size()= " + resultList.size() + ",resultSize= " + list.size());
        return resultList;
    }


    private SyncGetPublicByHttp3.IOnGetAccountByHttp onGetAccountByHttp = new SyncGetPublicByHttp3.IOnGetAccountByHttp() {
        @Override
        public void onResult(ArrayList<PublicGridItem> resultList, String proxyUrl) {
            stopRefresh();
            if (TextUtils.isEmpty(proxyUrl) || "1".equals(proxyUrl)) {
                // proxyUrl为空或者 为关状态"1"
                netConfig.proxyIP = "";
                netConfig.proxyPort = "";
                Sps_NetConfig.saveNetConfigInSpf(getActivity(), netConfig);
            } else {
                // 开状态
                netConfig.proxyIP = netConfig.publicServerIP;
                netConfig.proxyPort = String.valueOf(netConfig.publicServerPort);
                Sps_NetConfig.saveNetConfigInSpf(getActivity(), netConfig);
            }
            Log.d("dym------------------->", "edit 写入false");
            sharedPreferencesQ.put("isPublicUpdate", false);

            if (resultList != null && resultList.size() == 0) {
                gridAdapter.reset(resultList);
                gridAdapter.refresh();
                //清除保存的数据顺序信息
                OrderConfigRW.clear(Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase());
                return;
            }

            String orderData = OrderConfigRW.read(Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase());

            if (!TextUtils.isEmpty(orderData)) {
                //进行排序处理,每次从服务器拉取最新的数据,可能某些公众号授权会有修改导致当前的公众号的数量发生变化
                //这里又要考虑依然保存的公众号的顺序问题,这里这么处理，先读出已经存储的顺序数据，跟最新拉下来的数据
                //进行比对,优先提取出有顺序的公众号进行排序,后面没有顺序的公众号则自动依次排列
                ArrayList<PublicGridItem> sortResultList = new ArrayList<>();
                Log.d("dym------------------->", "onGetAccountByHttp orderData= " + orderData);
                //解析出数据,比对摘取出有顺序的数据
                String[] strs = orderData.split("\\|", -1);
                ArrayList<String> containOrderId = new ArrayList<>();
                for (int i = 0; i < strs.length; i++) {
                    String publicId = strs[i];
                    for (PublicGridItem item : resultList) {
                        if (publicId.equals(item.id)) {
                            sortResultList.add(item);
                            containOrderId.add(item.id);
                            break;
                        }
                    }
                }
                //将剩余的resultList中没有顺序的数据一次加到末尾
                if (containOrderId.size() > 0) {
                    if (resultList.size() > containOrderId.size()) {
                        for (PublicGridItem item : resultList) {
                            if (!containOrderId.contains(item.id)) {
                                sortResultList.add(item);
                            }
                        }
                    }

                } else {
                    sortResultList = resultList;
                }


                saveOrder(sortResultList);
                Log.d("dym------------------->", "onGetAccountByHttp 将新的顺序写入数据库");

                gridAdapter.reset(sortResultList);
                gridAdapter.refresh();
                //每次从网上拉最新的数据后去查询有没有需要下载的头像，若有需要则下载
                downPublicIcon(sortResultList);
            } else {
                gridAdapter.reset(resultList);
                gridAdapter.refresh();
                //每次从网上拉最新的数据后去查询有没有需要下载的头像，若有需要则下载
                downPublicIcon(resultList);
            }


        }


        @Override
        public void onFail(String title, String reason, String detail) {
            //网络原因导致的失败
            stopRefresh();
            if (isVisible) {
                DialogMsg.showDetail(getActivity(), "业务号更新失败", reason, detail);
            }
        }
    };


    @Override
    public void onDestroy() {
        Log.d("dym------------------->", "PublicAccountFragment onDestroy...");
        super.onDestroy();
        if (AppConfig.PUBLIC_UNREAD_TYPE == 0 && onUnReadChangeReceive != null) {
            getActivity().unregisterReceiver(onUnReadChangeReceive);
        }
        if (AppConfig.PUBLIC_UNREAD_TYPE == 1 && onPaMessageReceive != null) {
            getActivity().unregisterReceiver(onPaMessageReceive);
        }
        if (AppConfig.PUBLIC_UNREAD_TYPE == 2 && onUnReadChangeReceive != null && onPaMessageReceive != null) {
            getActivity().unregisterReceiver(onUnReadChangeReceive);
            getActivity().unregisterReceiver(onPaMessageReceive);
        }
        if (synGetPublicAccountDB != null) {
            synGetPublicAccountDB.stop();
        }

        if (syncGetPublicByHttp3 != null) {
            syncGetPublicByHttp3.stop();
            syncGetPublicByHttp3 = null;
        }
        if (syncGetAllMenuList != null) {
            syncGetAllMenuList = null;
        }

        if (gridAdapter != null) {
            gridAdapter.destroy();
        }

        if (syncGetHeadThu != null) {
            syncGetHeadThu.stop();
        }
    }

    private void downPublicIcon(ArrayList<PublicGridItem> list) {
        ArrayList<PicidPublicidBean> needDownList = new ArrayList<>();
        for (PublicGridItem item : list) {
            if (item.type == 0 || item.type == 2 || item.type == 3) {
                String path = BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + item.id;
                Log.d("dym------------------->", "path= " + path + ",exist= " + new File(path).exists());
                File file = new File(path);
                if (!file.exists()) {
                    //说明公众号图标没有下载，或者还有一种情况，下载失败导致只有残缺的图片文件，都需要重新下载
                    PicidPublicidBean bean = new PicidPublicidBean();
                    bean.picid = item.mobilePic;
                    bean.publicid = item.id;
                    needDownList.add(bean);
                    Log.d("dym------------------->", "needDownId = " + item.id);
                }
            }
//            ToastUtils.showShort(item.mobilePic);
        }

        if (needDownList.size() == 0) {
            return;
        }
        syncGetHeadThu.startPublicIdIcon(needDownList);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("dym------------------->", "PublicAccountFragment onDestroyView...");
        if (sharedPreferencesQ != null) {
            sharedPreferencesQ.removeDataChangeListener();
        }
    }

    private boolean isVisible = false;


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible = getUserVisibleHint();
        // Log.d("dym------------------->", "setUserVisibleHint isVisible= " + isVisible);
    }

    // 收到新消息
    private BroadcastReceiver onPaMessageReceive = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (titleViewPaMsg != null) {
                titleViewPaMsg.setCountViewText(titleViewPaMsg.getAllUnReadCount());
            }
            if (intent.getAction().equals(PublicMessage.ACTION_DELETE_PUBLIC_ACCOUNT)) {
                String paid = intent.getStringExtra(PublicMessage.PUBLIC_ACCOUNT_ID);
                SetLCForPublicAccount.clearUnReadCount(getContext(),
                        Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase(),
                        paid);
                if (Main.instance().handlerLCTCount != null) {
                    Main.instance().handlerLCTCount.updateLCTCount();
                }
            }else if(intent.getAction().equals(PublicMessage.ACTION_UPDATE_PUBLIC_ACCOUNT_UNREAD)){
                if (Main.instance().handlerLCTCount != null) {
                    Main.instance().handlerLCTCount.updateLCTCount();
                }
            }
        }
    };

    // 收到新消息
    private BroadcastReceiver onUnReadChangeReceive = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(LastContact.ACTION_UPDATE_CONTACT)) {
                int chatType = intent.getExtras().getInt(
                        LastContact.TARGET_TYPE);
                if (chatType != 2) {//不是公众号信息，直接过滤
                    return;
                }
                String targetID = intent.getExtras().getString(
                        LastContact.TARGET_ID);
                int unReadCount = intent.getExtras().getInt(
                        LastContact.UN_READ_COUNT);
                // 替换未读数
                gridAdapter.changeObjByID(targetID, unReadCount);
                gridAdapter.notifyDataSetChanged();
            } else if (intent.getAction().equals(
                    ACTION_DELETE)) {
                String targetID = intent
                        .getStringExtra(LastContact.TARGET_ID);
                // 替换未读数
                gridAdapter.changeObjByID(targetID, 0);
                gridAdapter.notifyDataSetChanged();
            } else if (intent.getAction().equals(
                    ACTION_CLEAR)) {
                String targetID = intent
                        .getStringExtra("lastcontact.send.targetid");
                // 替换未读数
                gridAdapter.changeObjByID(targetID, 0);
                gridAdapter.notifyDataSetChanged();
            } else if (intent.getAction().equals(
                    CLEAR_CHAT_RECORD)) {
                String targetID = intent.getExtras().getString("targetID");
                if (TextUtils.isEmpty(targetID)) {
                    return;
                }
                gridAdapter.changeObjByID(targetID, 0);
                gridAdapter.notifyDataSetChanged();
            } else if (intent.getAction().equals(LastContact.ACTION_UPDATE_UNREAD_COUNT)) {
                String targetID = intent.getStringExtra(LastContact.TARGET_ID);
                int unReadCount = intent.getIntExtra(LastContact.UN_READ_COUNT, 0);
                gridAdapter.changeObjByID(targetID, unReadCount);
                gridAdapter.notifyDataSetChanged();
            }else if(intent.getAction().equals(PublicMessage.ACTION_UPDATE_PUBLIC_ACCOUNT_UNREAD)){
                synGetPublicAccountDB = new SynGetPublicAccountDB();
                synGetPublicAccountDB.setListener(onGetPublicAccountDBListener);
                synGetPublicAccountDB.start();
            }
        }
    };
}
