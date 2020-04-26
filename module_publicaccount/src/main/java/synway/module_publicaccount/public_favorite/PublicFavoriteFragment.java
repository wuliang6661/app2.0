package synway.module_publicaccount.public_favorite;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import it.sauronsoftware.base64.Base64;
import qyc.library.control.dialog_confirm.DialogConfirm;
import qyc.library.control.dialog_confirm.DialogConfirmCfg;
import qyc.library.control.dialog_confirm.OnDialogConfirmCancel;
import qyc.library.control.dialog_confirm.OnDialogConfirmClick;
import qyc.library.control.dialog_msg.DialogMsg;
import qyc.library.control.dialog_progress.DialogProgress;
import synway.module_interface.config.CustomConfigType;
import synway.module_interface.config.netConfig.NetConfig;
import synway.module_interface.config.netConfig.Sps_NetConfig;
import synway.module_interface.config.userConfig.Sps_RWLoginUser;
import synway.module_interface.module.HomeFragment;
import synway.module_publicaccount.Main;
import synway.module_publicaccount.R;
import synway.module_publicaccount.customconfig.CustomConfigRW;
import synway.module_publicaccount.public_chat.Obj_Menu;
import synway.module_publicaccount.public_chat.PublicAccountChatActNormal;
import synway.module_publicaccount.public_favorite.grid.OnManageStateChangeListener;
import synway.module_publicaccount.public_favorite.grid.OnStartDragListener;
import synway.module_publicaccount.public_favorite.grid.RecyclerGridAdapter;
import synway.module_publicaccount.public_favorite.grid.SimpleItemTouchHelperCallback;
import synway.module_publicaccount.public_favorite.obj.Obj_GridItem;
import synway.module_publicaccount.public_favorite.obj.Obj_PublicAccount_Favorite;
import synway.module_publicaccount.publiclist.Obj_PublicAccount;
import synway.module_publicaccount.publiclist.PicidPublicidBean;
import synway.module_publicaccount.publiclist.PublicAccountAct2;
import synway.module_publicaccount.publiclist.SyncGetHeadThu;
import synway.module_publicaccount.publiclist.SyncGetMenuList;
import synway.module_publicaccount.publiclist.bean.App_InformationBean;
import synway.module_publicaccount.until.AppUtil;
import synway.module_publicaccount.until.BroadCastUntil;
import synway.module_publicaccount.until.DbUntil;
import synway.module_publicaccount.until.DownLoad.SyncDownAPK;

import static synway.module_publicaccount.until.AppUtil.doStartApplicationWithPackageName;
import static synway.module_publicaccount.until.DownLoad.InstallApk.startInstall;
import static synway.module_publicaccount.until.PicUtil.getApkPath;
import static synway.module_publicaccount.until.StringUtil.getCLearFileName;

/**
 * Created by quintet on 2016/10/12.
 * 弃用,现在加载的是PublicAccountFragment
 */
public class PublicFavoriteFragment extends HomeFragment {

    public PublicFavoriteFragment() {
        super.imageView_normal = R.drawable.model_home_public_normal_png;
        super.imageView_selected = R.drawable.model_home_public_selected_png;
        super.text = "应用";
    }

    private View view;

    private RecyclerView mRecyclerView;

    //工具类，用于帮助RecyclerView实现 item的滑动消除、拖拽 效果
    private ItemTouchHelper mItemTouchHelper;

    //ItemTouchHelper的反馈类
    private ItemTouchHelper.Callback mCallback;

    //RecyclerView实现grid所需的 数据适配器
    private RecyclerGridAdapter mAdapter;

    //"管理/完成"按钮，用于启动、退出管理grid item的编辑模式
    private TextView button_manage;

    //"管理/完成"按钮的状态标识，true：编辑状态
    private boolean mBoolean_button_isManaging = false;

    //grid中item是否有点击事件，true：有点击事件。编辑状态不让触发点击事件
    private boolean isItemClickable = true;

    //grid中item是否有长按事件，true：有长按事件。编辑状态不让触发长按事件
    private boolean isItemLongClickable = true;

    private ArrayList<Obj_GridItem> mDataSet = null;

    //宫格grid的列数
    private int gridColumnNum = 4;

    //存放 grid编辑前的 常用应用
    private ArrayList<Obj_PublicAccount_Favorite> mFavorites_beforeEdited = null;

    //登录用户ID
    private String loginUserID;


    public static final String Broad_Cast_Action = "Broad_Cast_Action";

    public static final String Obj_PublicFavorite = "Obj_PublicFavorite";


    /**
     * 发送http请求查询此用户全部公众号的信息
     */
    private SyncGetPublicInfoByHttp syncGetPublicInfoByHttp;

    /**
     * 通过数据库查询此用户全部公众号的信息
     */
    private SynGetPublicInfoByDB synGetPublicInfoByDB;

    /**
     * 获取常用应用的信息
     */
    private SynGetPublicFavorite synGetPublicFavorite;

    /**
     * 上传和入库常用应用
     */
    private SynUploadAndSavePublicFav synUploadAndSavePublicFav;

    /**
     * 上传过程显示的dialog，友好的提示用户
     */
    private Dialog uploadDialog = null;
    /**
     * 拉取公众号数据
     */
//    private SyncGetPublicByHttp syncGetPublicByHttp = null;
    private NetConfig netConfig = null;
    private ArrayList<String> idList = null;
    public SyncGetMenuList syncGetMenuList = null;
    ArrayList<Obj_PublicAccount> publicAccountsHttp=null;
    private SyncGetHeadThu syncGetHeadThu = null;
    private Context context;
    private SyncDownAPK syncDownAPK = null;
    private ProgressDialog pgDown = null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        //对view进行null判断的原因是由于修改代码之后，会频繁调到onCreateView，为了和以前代码保持一致，就加上非空判断。
        if (view == null) {
            view = inflater.inflate(R.layout.model_public_favorite_fragment, container, false);
            init();
            loginUserID = Sps_RWLoginUser.readUserID(getActivity());
            netConfig = Sps_NetConfig.getNetConfigFromSpf(getActivity());
            idList = new ArrayList<String>();
            publicAccountsHttp = new ArrayList<Obj_PublicAccount>();
            syncGetHeadThu = new SyncGetHeadThu(netConfig.httpIP, netConfig.httpPort, getActivity());
            syncGetHeadThu.setOnListen(onGetHeadThu);
            mAdapter = new RecyclerGridAdapter(getActivity(), mOnStartDragListener,
                mOnManageStateChange,syncGetHeadThu);
            mAdapter.setOnItemActionListener(mOnItemActionListener);

            mRecyclerView = view.findViewById(R.id.recyclerview_common);
            mRecyclerView.setHasFixedSize(true);
            // mRecyclerView.addItemDecoration(new DividerGridItemDecoration(getActivity()));
            mRecyclerView.setAdapter(mAdapter);

            final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),
                gridColumnNum);
            mRecyclerView.setLayoutManager(layoutManager);

            mCallback = new SimpleItemTouchHelperCallback(mAdapter);
            mItemTouchHelper = new ItemTouchHelper(mCallback);
            mItemTouchHelper.attachToRecyclerView(mRecyclerView);

            button_manage = view.findViewById(R.id.tv_manage);
            button_manage.setTextColor(getActivity().getResources().getColor(R.color.blackblack));
            button_manage.setOnClickListener(onClickListener);
            synGetPublicFavorite = new SynGetPublicFavorite();
            synGetPublicFavorite.setOnSynGetPublicFavoriteByDBListener(onSynGetPublicFavoriteByDBListener);
            //================================================================
            /**
             * 仅仅是第一次上线的时候需要查询处理,如何证明是第一次呢，只运行一次呢
             * 这里的处理目的为了，是否显示无全部应用图标*/

            String result = CustomConfigRW.read(CustomConfigType.PUBLICFAVORITE,
                "SavePublicFavorite");

            if (result == null) {

                //Log.e("dym", "第一次上线才会有的");
                syncGetPublicInfoByHttp = new SyncGetPublicInfoByHttp();
                syncGetPublicInfoByHttp.setOnGetPublicInfoByHttpListener(
                    onGetPublicInfoByHttpListener);

                synGetPublicInfoByDB = new SynGetPublicInfoByDB();
                synGetPublicInfoByDB.setOnSynGetPublicInfoByDBListener(
                    onSynGetPublicInfoByDBListener);
                synGetPublicInfoByDB.start();
            } else {
                //Log.e("dym", "上线后正常使用");
                /** 获取常用公众号的数据，然后进行界面的加载*/
                synGetPublicFavorite.start();
            }

            //===================================================================

            synUploadAndSavePublicFav = new SynUploadAndSavePublicFav(getActivity());
            synUploadAndSavePublicFav.setOnSynUploadAndSavePublicFavListener(
                onSynUploadAndSavePublicFavListener);
            //公众号
//            syncGetPublicByHttp = new SyncGetPublicByHttp();
//            syncGetPublicByHttp.setLsn(onGetAccountByHttp);
            //菜单数据
            syncGetMenuList = new SyncGetMenuList(netConfig.functionIP, netConfig.functionPort);
            syncGetMenuList.setLsn(onGetMenuListLsn);
            //注册广播接收者
            IntentFilter intentFilter = new IntentFilter(PublicFavoriteFragment.Broad_Cast_Action);
            getActivity().registerReceiver(receiver, intentFilter);
            //服务端修改菜单的广播
            IntentFilter menuintentFilter = new IntentFilter(BroadCastUntil.ChangeMenuMsg.ChangeMenu);
            getActivity().registerReceiver(menureceiver, menuintentFilter);
            //刷新界面的广播
            IntentFilter menuFreshintentFilter = new IntentFilter(BroadCastUntil.ChangeMenuMsg.freshUI);
            getActivity().registerReceiver(menuFreshreceiver, menuFreshintentFilter);
            context=getActivity();
        }
        return view;
    }
   private void init(){
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
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (syncGetPublicInfoByHttp != null) {
            syncGetPublicInfoByHttp.stop();
        }
        if (synGetPublicInfoByDB != null) {
            synGetPublicInfoByDB.stop();
        }
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
        }
        if (synGetPublicFavorite != null) {
            synGetPublicFavorite.stop();
        }
        if (synUploadAndSavePublicFav != null) {
            synUploadAndSavePublicFav.stop();
        }
//        if (syncGetPublicByHttp != null) {
//            syncGetPublicByHttp.stop();
//        }
        if (syncGetMenuList != null) {
            syncGetMenuList.stop();
        }
        if (syncGetHeadThu != null) {
            syncGetHeadThu.stop();
        }
        if (menureceiver != null) {
            getActivity().unregisterReceiver(menureceiver);
        }
        if(menuFreshreceiver!=null){
            getActivity().unregisterReceiver(menuFreshreceiver);
        }
        if (syncDownAPK != null) {
            syncDownAPK.stop();
        }
        if(mAdapter!=null){
            mAdapter.onDestory();
        }


    }

    private SynUploadAndSavePublicFav.OnSynUploadAndSavePublicFavListener onSynUploadAndSavePublicFavListener =
            new SynUploadAndSavePublicFav.OnSynUploadAndSavePublicFavListener() {
                @Override
                public void onStart(Boolean ifstart) {
                    if (uploadDialog == null&&ifstart==false) {
                        uploadDialog = DialogProgress.get(getActivity(), "提示", "正在保存...");
                        uploadDialog.show();
                    }

                }

                @Override
                public void onSingleSuccess(String msg, Obj_PublicAccount_Favorite obj_publicFavorite) {

                }

                @Override
                public void onListSuccess(String msg) {
                    if (uploadDialog != null) {
                        if (uploadDialog.isShowing()) {
                            uploadDialog.dismiss();
                        }
                    }

                    setButtonStateToBeManaging(false);
                }

                @Override
                public void onFail(String reason,Boolean ifstart) {
                    Log.i("testy","得到的快速应用返回值"+ifstart);
                    if (uploadDialog != null) {
                        if (uploadDialog.isShowing()) {
                            uploadDialog.dismiss();
                        }
                    }


                    /** 为什么用添加这个"数据库错误"判断呢？是因为我们用到CustomConfigRW.write(),上传数据库的时候由于
                     * 之前数据库ConfigTable表设置value的字段不够长，现在改为text,但是外面的服务端可能数据库还没有及时
                     * 升级,所以在此进行提醒*/
                    if (reason.contains("数据库错误")) {
                        //获取编辑前的常用应用，grid恢复之前的状态,管理按钮恢复为 退出编辑 状态
                        resetGridItem(mFavorites_beforeEdited);

                        setButtonStateToBeManaging(false);

                        DialogMsg.show(getActivity(), "提示", "服务端数据库版本较旧,不支持加入常用应用,请升级数据库后重试。");
                    } else {
                        //说明不是一开始的同步
                        if(ifstart==false) {
                            DialogConfirmCfg cfg = new DialogConfirmCfg();
                            cfg.cancelBtnText = "取消";
                            cfg.confirmBtnText = "重试";
                            DialogConfirm.show(getActivity(), "提示", "保存常用应用失败，请重试或取消",
                                    onClick1, onClick2, cfg);
                        }
                    }

                }
            };

    /**
     * 同步失败后，弹出的dialog中"重试"按钮的点击回调
     */
    private OnDialogConfirmClick onClick1 = new OnDialogConfirmClick() {

        @Override
        public void onDialogConfirmClick() {
            ArrayList<Obj_PublicAccount_Favorite> dataSet_favorite = mAdapter.getDataSet_Favorite();

            if (dataSet_favorite == null) {
                dataSet_favorite = new ArrayList<>();
            }

            if (synUploadAndSavePublicFav != null) {
                synUploadAndSavePublicFav.stop();
            }

            synUploadAndSavePublicFav = new SynUploadAndSavePublicFav(getActivity());
            synUploadAndSavePublicFav.setOnSynUploadAndSavePublicFavListener(
                    onSynUploadAndSavePublicFavListener);
            synUploadAndSavePublicFav.start(dataSet_favorite, loginUserID,false);

        }
    };

    /**
     * 同步失败后，弹出的dialog中"取消"按钮的点击回调
     */
    private OnDialogConfirmCancel onClick2 = new OnDialogConfirmCancel() {

        @Override
        public void onDialogConfirmCancel() {
            //获取编辑前的常用应用，grid恢复之前的状态,管理按钮恢复为 退出编辑 状态
            resetGridItem(mFavorites_beforeEdited);

            setButtonStateToBeManaging(false);

        }
    };

    private SynGetPublicFavorite.OnSynGetPublicFavoriteListener
            onSynGetPublicFavoriteByDBListener =
            new SynGetPublicFavorite.OnSynGetPublicFavoriteListener() {
                @Override
                public void onResult(List<Obj_PublicAccount_Favorite> favoriteList) {
                    //把新的常用应用记录保存到本地数据库
//                    synUploadAndSavePublicFav.start(favoriteList, loginUserID,true);
                    /** 这里可以将数据设置进adapter，然后进行刷新*/
                    //Log.e("dym", "OnSynGetPublicFavoriteListener onResult()");
                    ArrayList<Obj_GridItem> dataSet = new ArrayList<>();
                    Obj_GridItem obj_gridItem;


                    if (favoriteList != null && !favoriteList.isEmpty()) {
                        for (int i = 0; i < favoriteList.size(); i++) {
                            obj_gridItem = new Obj_GridItem(favoriteList.get(i), getActivity(), Obj_GridItem.ITEM_TYPE_FAVORITE,syncGetHeadThu);
                            dataSet.add(obj_gridItem);
                        }
                        obj_gridItem = new Obj_GridItem(null, getActivity(), Obj_GridItem.ITEM_TYPE_MORE,syncGetHeadThu);
                        dataSet.add(obj_gridItem);
                    } else {
//                        obj_gridItem = new Obj_GridItem(null, getActivity(), Obj_GridItem.ITEM_TYPE_FAVORITE_EMPTY,syncGetHeadThu);
//                        dataSet.add(obj_gridItem);

                        obj_gridItem = new Obj_GridItem(null, getActivity(), Obj_GridItem.ITEM_TYPE_MORE,syncGetHeadThu);
                        dataSet.add(obj_gridItem);
                    }

                    if (mAdapter != null) {

                        mAdapter.reset(dataSet);
                        mAdapter.refresh();
                    }

                }

                @Override
                public void onFail(String reason) {
                    /** 数据库表内没有数据*/
                }
            };

    private SynGetPublicInfoByDB.OnSynGetPublicInfoByDBListener onSynGetPublicInfoByDBListener =
            new SynGetPublicInfoByDB.OnSynGetPublicInfoByDBListener() {
                @Override
                public void onResult(boolean isRecordExists) {
                    if (isRecordExists) {


                        /** 如果进来这里说明显示"全部应用"、"请添加常用应用"的图标，相关UI操作可以在下方写*/
                        //Log.e("dym", "OnSynGetPublicInfoByDBListener onResult() true");

                        ArrayList<Obj_GridItem> dataSet = new ArrayList<>();
                        Obj_GridItem obj_gridItem;

//                        obj_gridItem = new Obj_GridItem(null, getActivity(), Obj_GridItem.ITEM_TYPE_FAVORITE_EMPTY,syncGetHeadThu);
//                        dataSet.add(obj_gridItem);

                        obj_gridItem = new Obj_GridItem(null, getActivity(), Obj_GridItem.ITEM_TYPE_MORE,syncGetHeadThu);
                        dataSet.add(obj_gridItem);

                        if (mAdapter != null) {
                            mAdapter.reset(dataSet);
                            mAdapter.refresh();
                        }

                    } else {
                        NetConfig netConfig = Sps_NetConfig.getNetConfigFromSpf(getActivity());
                        String loginUserID = Sps_RWLoginUser.readUserID(getActivity());
                        syncGetPublicInfoByHttp.start(netConfig.httpIP, netConfig.httpPort,
                                loginUserID);
                        //Log.e("dym", "OnSynGetPublicInfoByDBListener onResult() false");
                    }
                }
            };

    private SyncGetPublicInfoByHttp.OnGetPublicInfoByHttpListener onGetPublicInfoByHttpListener
            = new SyncGetPublicInfoByHttp.OnGetPublicInfoByHttpListener() {
        @Override
        public void onExistResult() {

            //Log.e("dym", "OnGetPublicInfoByHttpListener onExistResult()");
            /** 如果进来这里说明显示"全部应用"、"请添加常用应用"的图标，相关UI操作可以在下方写*/
            ArrayList<Obj_GridItem> dataSet = new ArrayList<>();
            Obj_GridItem obj_gridItem;

//            obj_gridItem = new Obj_GridItem(null, getActivity(), Obj_GridItem.ITEM_TYPE_FAVORITE_EMPTY,syncGetHeadThu);
//            dataSet.add(obj_gridItem);

            obj_gridItem = new Obj_GridItem(null, getActivity(), Obj_GridItem.ITEM_TYPE_MORE,syncGetHeadThu);
            dataSet.add(obj_gridItem);

            if (mAdapter != null) {
                mAdapter.reset(dataSet);
                mAdapter.refresh();
            }
        }


        @Override
        public void onFail(String title, String reason, String detail) {


            //Log.e("dym", "OnGetPublicInfoByHttpListener onFail()");
            /** 如果进来这里，说明可以显示"无全部应用"的图标，相关UI操作可以在下方写*/
            ArrayList<Obj_GridItem> dataSet = new ArrayList<>();
            Obj_GridItem obj_gridItem;

            obj_gridItem = new Obj_GridItem(null, getActivity(), Obj_GridItem.ITEM_TYPE_EMPTY,syncGetHeadThu);
            dataSet.add(obj_gridItem);

            if (mAdapter != null) {

                mAdapter.reset(dataSet);
                mAdapter.refresh();
            }
        }


        @Override
        public void onEmptyResult() {

            //Log.e("dym", "OnGetPublicInfoByHttpListener onEmptyResult()");
            /** 如果进来这里，说明可以显示"无全部应用"的图标，相关UI操作可以在下方写*/
            ArrayList<Obj_GridItem> dataSet = new ArrayList<>();
            Obj_GridItem obj_gridItem;

            obj_gridItem = new Obj_GridItem(null, getActivity(), Obj_GridItem.ITEM_TYPE_EMPTY,syncGetHeadThu);
            dataSet.add(obj_gridItem);

            if (mAdapter != null) {

                mAdapter.reset(dataSet);
                mAdapter.refresh();
            }
        }
    };

    /**
     * 将字符串 s 进行 BASE64 编码
     *
     * @param s 待编码的字符串
     *
     * @return 编码后的结果字符串
     */
    public String getBase64Help(String s) {
        try {
            s = Base64.encode(s, "UTF-8");
            return s;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 广播接收器，用于接收 item添加 广播的数据
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Obj_PublicAccount_Favorite obj=(Obj_PublicAccount_Favorite) intent.getSerializableExtra(PublicFavoriteFragment.Obj_PublicFavorite);
            /**
             * 组装成item对象Obj_GridItem,添加并刷新界面
             */
            if (obj != null) {
                if (mAdapter != null) {
                    Obj_GridItem obj_gridItem;
                    if (mAdapter.getDataSet_Favorite() != null) {
                        System.out.println("--->在全部前添加一个常用应用");
                        //有 有效的“常用应用“”
                        obj_gridItem = new Obj_GridItem(obj, getActivity(), Obj_GridItem.ITEM_TYPE_FAVORITE,syncGetHeadThu);
                        //在最后一个item前添加
                        mAdapter.addItemBeforeLast(obj_gridItem);

                    } else {
                        System.out.println("--->清空原有数据，重新添加");
                        ArrayList<Obj_GridItem> dataSet = new ArrayList<>();

                        obj_gridItem = new Obj_GridItem(obj, getActivity(), Obj_GridItem.ITEM_TYPE_FAVORITE,syncGetHeadThu);
                        dataSet.add(obj_gridItem);

                        obj_gridItem = new Obj_GridItem(null, getActivity(), Obj_GridItem.ITEM_TYPE_MORE,syncGetHeadThu);
                        dataSet.add(obj_gridItem);

                        mAdapter.reset(dataSet);
                        mAdapter.refresh();

                    }

                }

            }

//            Log.e("dym  BroadcastReceiver", "ID= " + obj.ID + ",Name= " + obj.Name + ", menuName= " + obj.menuName +
//                    ", menuUrl= " + obj.menuUrl + ", isHtml= " + obj.isHtml);
        }
    };



    private Obj_PublicAccount_Favorite transferStringToPublicFavObj(String s) {
        Obj_PublicAccount_Favorite obj_publicFavorite = new Obj_PublicAccount_Favorite();
        try {
            JSONObject jsonObject = new JSONObject(s);
            obj_publicFavorite.ID = jsonObject.optString("ID");
            obj_publicFavorite.Name = jsonObject.optString("Name");
            obj_publicFavorite.menuName = jsonObject.optString("menuName");
            obj_publicFavorite.menuUrl = jsonObject.optString("menuUrl");
            obj_publicFavorite.isHtml = jsonObject.optBoolean("isHtml");
            obj_publicFavorite.fc_mobilepic=jsonObject.optString("fc_mobilepic");
            obj_publicFavorite.MenuId=jsonObject.optString("MenuId");
            obj_publicFavorite.type=jsonObject.optInt("Type");
            obj_publicFavorite.app_information=(App_InformationBean) jsonObject.opt("appInformation");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj_publicFavorite;
    }


    /**
     * 宫格UI中 item 点击、长按事件监听器
     */
    private RecyclerGridAdapter.OnItemActionListener mOnItemActionListener = new RecyclerGridAdapter.OnItemActionListener() {

        @Override
        public void onItemClick(View view, int position) {

            //如果不可点击
            if (!isItemClickable) {
                return;
            }

            //获取被 点击 的item对象
            Object obj = mAdapter.getDataByPosition(position);

            if (obj != null && obj instanceof Obj_GridItem) {
                //item 对象
                Obj_GridItem obj_gridItem = (Obj_GridItem) obj;
                gridItemClick(obj_gridItem);
            }
        }

        @Override
        public void onItemLongClick(View view, int position) {

            //如果不可长按
            if (!isItemLongClickable) {
                return;
            }

            //获取被 长按 的item对象
            Object obj = mAdapter.getDataByPosition(position);

            if (obj != null && obj instanceof Obj_GridItem) {
                //item 对象
                Obj_GridItem obj_gridItem = (Obj_GridItem) obj;
                gridItemLongClick(obj_gridItem);
            }
        }
    };

    /**
     * 拖拽监听器
     */
    private OnStartDragListener mOnStartDragListener = new OnStartDragListener() {
        @Override
        public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
            if (mItemTouchHelper != null) {
                mItemTouchHelper.startDrag(viewHolder);
            }
        }
    };

    /**
     * 管理状态改变 监听器
     */
    private OnManageStateChangeListener mOnManageStateChange = new OnManageStateChangeListener() {
        @Override
        public void onChange(boolean b) {
            if (b) {//切换到编辑状态
                if (!mBoolean_button_isManaging) {
                    isItemClickable = false;
                    isItemLongClickable = false;
                    if (mAdapter != null) {
                        mFavorites_beforeEdited = mAdapter.getDataSet_Favorite();
                    }
                    button_manage.setText("完成");
                    button_manage.setTextColor(getActivity().getResources().getColor(R.color.mred));
                    mBoolean_button_isManaging = true;
                }

            } else {
                isItemClickable = true;
                isItemLongClickable = true;
            }
        }
    };

    /**
     * 重置宫格UI中的item
     *
     * @param favorites 常用应用数据集
     */
    private void resetGridItem(List<Obj_PublicAccount_Favorite> favorites) {
        ArrayList<Obj_GridItem> dataSet = new ArrayList<>();
        Obj_GridItem obj_gridItem;

        if (favorites != null && !favorites.isEmpty()) {
            for (int i = 0; i < favorites.size(); i++) {
                obj_gridItem = new Obj_GridItem(favorites.get(i), getActivity(), Obj_GridItem.ITEM_TYPE_FAVORITE,syncGetHeadThu);
                dataSet.add(obj_gridItem);
            }
            obj_gridItem = new Obj_GridItem(null, getActivity(), Obj_GridItem.ITEM_TYPE_MORE,syncGetHeadThu);
            dataSet.add(obj_gridItem);
        } else {
//            obj_gridItem = new Obj_GridItem(null, getActivity(), Obj_GridItem.ITEM_TYPE_FAVORITE_EMPTY,syncGetHeadThu);
//            dataSet.add(obj_gridItem);

            obj_gridItem = new Obj_GridItem(null, getActivity(), Obj_GridItem.ITEM_TYPE_MORE,syncGetHeadThu);
            dataSet.add(obj_gridItem);
        }

        if (mAdapter != null) {

            mAdapter.reset(dataSet);
            mAdapter.refresh();
        }
    }

    /**
     * 常用应用 宫格UI中 item 点击方法
     * <p>
     * 通过传入的item对象获取item类型，根据类型的不同分别处理
     *
     * @param obj_gridItem item对象
     */
    private void gridItemClick(Obj_GridItem obj_gridItem) {

        if (obj_gridItem == null) {
            return;
        }
        Intent intent;

        int itemType = obj_gridItem.itemType;

        switch (itemType) {
            case Obj_GridItem.ITEM_TYPE_FAVORITE://常用应用 被点击

                Obj_PublicAccount_Favorite obj_favorite = obj_gridItem.mObj_publicAccount_favorite;
                if (obj_favorite == null) {
                    return;
                }
                /*这里要判断:
                * 1.正常公众号
                * 2.网页跳转类
                * */
                if(obj_favorite.type==1){//是APP应用
                    PackageInfo packageInfo = AppUtil.isAvilible(getActivity(), obj_favorite.app_information.app_packangename);
                    if (packageInfo == null) {//该应用没有安装
                        downloadApk(obj_favorite.app_information);
                    } else {
                        if (packageInfo.versionName.equals(obj_favorite.app_information.app_version)) {//版本一样，直接打开
                            doStartApplicationWithPackageName(getActivity(), packageInfo);
                        } else {
                            downloadApk(obj_favorite.app_information);
                        }
                    }
                    return;
                }
                if (obj_favorite.isHtml) {
                    if (obj_favorite.menuUrl.equals("")) {
                        Toast.makeText(getActivity(), "该网页跳转类常用应用无法跳转", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    intent=new Intent(getActivity(),PublicAccountChatActNormal.class);
                    Obj_Menu obj_menu=new Obj_Menu();
                    Bundle mBundle = new Bundle();
                    obj_menu.menuName=obj_favorite.menuName;
                    obj_menu.menuUrl=obj_favorite.menuUrl;
                    mBundle.putSerializable("obj_Menu",obj_menu);
                    intent.putExtra("ACCOUNT_ID",obj_favorite.ID);
                    intent.putExtra("ACCOUNT_NAME",obj_favorite.Name);
                    intent.putExtra("MenuKry_Click","weburl");
                    intent.putExtra("skipType","keyBoard");
                    intent.putExtras(mBundle);
                   startActivity(intent);
                } else {
                    intent = new Intent();
                    intent.setClass(getActivity(), PublicAccountChatActNormal.class);
                    intent.putExtra("ACCOUNT_ID", obj_favorite.ID);
                    intent.putExtra("ACCOUNT_NAME", obj_favorite.Name);
                    startActivity(intent);
                }
                break;
            case Obj_GridItem.ITEM_TYPE_MORE:
                intent = new Intent();
                intent.setClass(getActivity(),PublicAccountAct2.class);
                startActivity(intent);
                break;
            case Obj_GridItem.ITEM_TYPE_FAVORITE_EMPTY:
                //没有常用应用，点击该按钮
                intent = new Intent();
                intent.setClass(getActivity(), PublicAccountAct2.class);
                startActivity(intent);
                break;
            case Obj_GridItem.ITEM_TYPE_EMPTY:
                DialogMsg.show(getActivity(), "提示", "请联系管理员添加应用");
                break;
        }
    }

    /**
     * 常用应用 宫格UI中 item 长按方法，暂时未做什么处理，留作扩展
     * <p>
     * 通过传入的item对象获取item类型，根据类型的不同分别处理
     *
     * @param obj_gridItem item对象
     */
    private void gridItemLongClick(Obj_GridItem obj_gridItem) {

        if (obj_gridItem == null) {
            return;
        }

        int itemType = obj_gridItem.itemType;

        switch (itemType) {
            case Obj_GridItem.ITEM_TYPE_FAVORITE://常用应用 被长按

                Obj_PublicAccount_Favorite obj_favorite = obj_gridItem.mObj_publicAccount_favorite;
                if (obj_favorite == null) {
                    return;
                }
                break;
            case Obj_GridItem.ITEM_TYPE_MORE:

                break;
            case Obj_GridItem.ITEM_TYPE_FAVORITE_EMPTY:

                break;
            case Obj_GridItem.ITEM_TYPE_EMPTY:

                break;
        }
    }

    /**
     * 比较两个 常用应用list 是否相同
     *
     * @param newList 保存时获取的常用应用list
     * @param oldList 进入编辑状态时获取的常用应用list
     *
     * @return true：两个list相同
     */
    private boolean isListEqual(ArrayList<Obj_PublicAccount_Favorite> newList, ArrayList<Obj_PublicAccount_Favorite> oldList) {
        boolean isEqual = true;
        if (newList.size() == oldList.size()) {
            for (int i = 0; i < oldList.size(); i++) {
                if (!oldList.get(i).ID.equals(newList.get(i).ID)
                        || !oldList.get(i).menuName.equals(newList.get(i).menuName)) {
                    isEqual = false;
                    break;
                }
            }
        } else {
            isEqual = false;
        }
        return isEqual;
    }

    /**
     * 设置“管理”按钮的状态,并设置item是否有点击、长按事件，通知
     *
     * @param toBeManaging true：进入编辑状态
     */
    private void setButtonStateToBeManaging(boolean toBeManaging) {
        if (toBeManaging) {
            //编辑状态 设置item不触发点击、长按
            //编辑按钮 名称改为“完成”,设为编辑状态
            //通知adapter刷新并把“删除”图标显示
            isItemClickable = false;
            isItemLongClickable = false;
            button_manage.setText("完成");
            button_manage.setTextColor(getActivity().getResources().getColor(R.color.mred));
            if (mAdapter != null) {
                mAdapter.setIsManaging(true);
            }
            mBoolean_button_isManaging = true;
        } else {
            //退出编辑状态 设置item可触发点击、长按
            //编辑按钮 名称改为“管理”,设为非编辑状态
            //通知adapter刷新并把“删除”图标隐藏
            isItemClickable = true;
            isItemLongClickable = true;
            button_manage.setText("管理");
            button_manage.setTextColor(getActivity().getResources().getColor(R.color.blackblack));
            if (mAdapter != null) {
                mAdapter.setIsManaging(false);
            }
            mBoolean_button_isManaging = false;
        }
    }


//    /**
//     * 广播接收器，接收服务端修改菜单的推送
//     */
//    private BroadcastReceiver menureceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Log.i("testy","收到修改菜单的推送：需要从服务器重新拉取菜单以及公众号的数据以及刷新");
//            //从服务区拉取公众号数据
//            syncGetPublicByHttp.start(netConfig.httpIP, netConfig.httpPort, loginUserID, idList);
//        }
//    };
    /**
     * 广播接收器，接收服务端修改菜单的推送(新的是从本地数据库拉取数据)
     */
    private BroadcastReceiver menureceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("testy","收到修改菜单的推送：新的修改是只需要从本地拉取个人常用应用的所有信息");
            /** 获取常用公众号的数据，然后进行界面的加载*/
            if(synGetPublicFavorite!=null) {
                synGetPublicFavorite.start();
            }
        }
    };
    /**
     * 广播接收器，刷新界面
     */
    private BroadcastReceiver menuFreshreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("testy","需要刷新界面");
            mAdapter.refresh();

        }
    };
//    private SyncGetPublicByHttp.IOnGetAccountByHttp onGetAccountByHttp = new SyncGetPublicByHttp.IOnGetAccountByHttp() {
//        @Override
//        public void onResult(ArrayList<Obj_PublicAccount> arrayList, HashMap<String, Integer> selePos,ArrayList<Obj_PublicAccount> appList) {
//            Log.i("testy","公众号获取成功");
//            publicAccountsHttp=arrayList;
//            //拉取菜单数据
//            syncGetMenuList.start(netConfig.httpIP, netConfig.httpPort,loginUserID,arrayList);
//
//        }
//        @Override
//        public void onFail(String title, String reason, String detail) {
//            DialogMsg.showDetail(getActivity(), title, reason, detail);
//        }
//    };
    private SyncGetMenuList.IOnGetHttpMenuListLsn onGetMenuListLsn;{onGetMenuListLsn = new SyncGetMenuList.IOnGetHttpMenuListLsn() {

        @Override
        public void onResult(ArrayList<Obj_Menu> firstmenus, ArrayList<Obj_Menu> secondMenuList) {
            Log.i("testy","菜单获取成功");
            //查询公众账号是否包含菜单
            for(int i=0;i<publicAccountsHttp.size();i++){
                Obj_PublicAccount obj_publicAccount=publicAccountsHttp.get(i);
                ArrayList<Obj_Menu> obj_menus=new ArrayList<Obj_Menu>();
                obj_menus.clear();
                ArrayList<Obj_Menu> obj_menusecond=new ArrayList<Obj_Menu>();
                obj_menusecond.clear();
                Boolean isinclude=false;
                //遍历一级菜单
                if(firstmenus!=null) {
                    for (int j = 0; j < firstmenus.size(); j++) {
                        //包含一级菜单
                        if (firstmenus.get(j).ID.equals(obj_publicAccount.ID)) {
                            isinclude = true;
                            obj_menus.add(firstmenus.get(j));
                            for (int k = 0; k < secondMenuList.size(); k++) {
                                if (secondMenuList.get(k).menuFather.equals(firstmenus.get(j).menuGUID)) {
                                    obj_menusecond.add(secondMenuList.get(k));
                                }
                            }
                        }
                    }
                }
                if(isinclude){
                    DbUntil.DBDeal(obj_menus,obj_menusecond,obj_publicAccount.ID,Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase());
                }else{
                    DbUntil.clearDBfromGUID(obj_publicAccount.ID, Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase());
                }
                obj_publicAccount.firstmenus=obj_menus;
                obj_publicAccount.secondmenus=obj_menusecond;
                publicAccountsHttp.set(i,obj_publicAccount);
            }
            synGetPublicFavorite.start();
            downLoadUserHead(publicAccountsHttp);
        }

        @Override
        public void onFail(String title, String reason, String detail) {
            DialogMsg.showDetail(getActivity(), title, reason, detail);
        }
    };
    }

    /***
     * 图片下载
     */
    private void downLoadUserHead(ArrayList<Obj_PublicAccount> publicAccountsHttp) {
        if (publicAccountsHttp.size() == 0) {
            return;
        }
        ArrayList<String> listNoHeadAndShowing = new ArrayList<String>();
        ArrayList<PicidPublicidBean> picidPublicidBeens = new ArrayList<PicidPublicidBean>();
        PicidPublicidBean picidPublicidBean = new PicidPublicidBean();
        for (int i = 0; i < publicAccountsHttp.size(); i++) {
            String showingID = publicAccountsHttp.get(i).ID;
            String piid=publicAccountsHttp.get(i).fc_mobilepic;
            picidPublicidBean.publicid = showingID;
            picidPublicidBean.picid = piid;
            if(piid!=null&&!piid.equals("")) {
                listNoHeadAndShowing.add(piid);
                picidPublicidBeens.add(picidPublicidBean);
            }
                //遍历添加一级或者二级菜单中的图片
                ArrayList<Obj_Menu> obj_first_menus=publicAccountsHttp.get(i).firstmenus;
                if(obj_first_menus!=null&&obj_first_menus.size()!=0){
                    for(int j=0;j<obj_first_menus.size();j++){
                        String firstpicid=obj_first_menus.get(j).menuPicUrl;
                        picidPublicidBean.picid = firstpicid;
                        picidPublicidBean.publicid = obj_first_menus.get(j).menuGUID;
                        if(firstpicid!=null&&!firstpicid.equals("")) {
                            listNoHeadAndShowing.add(firstpicid);
                            picidPublicidBeens.add(picidPublicidBean);
                        }

                    }}
                ArrayList<Obj_Menu> obj_secnd_menus=publicAccountsHttp.get(i).secondmenus;
                if(obj_secnd_menus!=null&&obj_secnd_menus.size()!=0) {
                    for (int k=0;k<obj_secnd_menus.size();k++) {
                        String secondmenupic=obj_secnd_menus.get(k).menuPicUrl;
                        picidPublicidBean.picid = secondmenupic;
                        picidPublicidBean.publicid = obj_secnd_menus.get(k).menuGUID;
                        if(secondmenupic!=null&&!secondmenupic.equals("")) {
                            listNoHeadAndShowing.add(secondmenupic);
                            picidPublicidBeens.add(picidPublicidBean);
                        }
                    }
                }

        }
        if (listNoHeadAndShowing.size() == 0) {
            return;
        }

        //下载图片
        syncGetHeadThu.startPublicIdIcon(picidPublicidBeens);
//        syncGetHeadThu.start(listNoHeadAndShowing);
    }
    private SyncGetHeadThu.IOnGetHeadThu onGetHeadThu = new SyncGetHeadThu.IOnGetHeadThu() {
        @Override
        public void onHeadGet(String ID) {
            Log.i("testy","服务器推送的修改  图片下载成功");
            mAdapter.refresh();

        }

        @Override
        public void onFailGet(String ID) {
            Log.i("testy","服务器推送的修改  图片下载失敗");
        }
    };
    private View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.equals(button_manage)){
                if (!mBoolean_button_isManaging) {

                    if (mAdapter != null) {
                        mFavorites_beforeEdited = mAdapter.getDataSet_Favorite();

                        //如果grid中没有常用应用，就不让进入编辑模式
                        if (mFavorites_beforeEdited == null) {
                            isItemClickable = true;
                            isItemLongClickable = true;
                            return;
                        }
                    } else {
                        return;
                    }

                    setButtonStateToBeManaging(true);

                } else {
                    /**点击"完成"，并同步常用应用*/
                    if (mAdapter != null) {
                        ArrayList<Obj_PublicAccount_Favorite> dataSet_favorite = mAdapter.getDataSet_Favorite();
                        if (dataSet_favorite != null) {
                            if (mFavorites_beforeEdited != null) {
                                //如果编辑前后 没有改变，就直接退出，不同步
                                if (isListEqual(dataSet_favorite, mFavorites_beforeEdited)) {

                                    setButtonStateToBeManaging(false);

                                    return;
                                }
                            }

                            /**会阻塞，改成设置观察者模式*/
                            synUploadAndSavePublicFav.start(dataSet_favorite, loginUserID,false);


                        } else {
                            /**
                             * 避免删光“常用应用”后无法同步这种无应用状态
                             * 避免本身 已经为空状态的 重复同步
                             * */
                            ArrayList<Obj_PublicAccount_Favorite> dataSet_favorite1 = new ArrayList<>();

                            /**会阻塞，改成设置观察者模式*/
                            synUploadAndSavePublicFav.start(dataSet_favorite1, loginUserID,false);

                            //删光常用应用后，再次显示“无常用应用”item
                            resetGridItem(null);
                        }
                    } else {

                        setButtonStateToBeManaging(false);
                    }
                }

            }
        }
    };

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) { // 相当于onResume()方法
            Log.i("testy","当前fragment可见");
            if(context!=null) {
                SharedPreferences spf = context.getSharedPreferences("public_account_download", Context.MODE_PRIVATE);
                Boolean aBoolean = spf.getBoolean(loginUserID, true);
                Log.i("testy", "是否已经有更新完成" + aBoolean);
                if (aBoolean == false) {//推送更新失敗 ，重新向服务器拉数据
//                    syncGetPublicByHttp.start(netConfig.httpIP, netConfig.httpPort, loginUserID, idList);
                }
            }
        } else {
            Log.i("testy","当前fragment不可见");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(mBoolean_button_isManaging){
                resetGridItem(mFavorites_beforeEdited);
                setButtonStateToBeManaging(false);
                return  true;
            }
            return  false;
        }
      return  false;
    }
    private SyncDownAPK.IOnDownApkLsn onDownApkLsn = new SyncDownAPK.IOnDownApkLsn() {

        @Override
        public void onStart() {
            Log.i("testy", "开始下载");
            if (pgDown.isShowing()) {
                pgDown.dismiss();
            }
            pgDown.setProgress(0);
            pgDown.show();
        }

        @Override
        public void onProgress(int progress) {
            Log.i("testy", "下载进度" + progress);
            pgDown.setProgress(progress);
        }

        @Override
        public void onFinish(File resultFile) {
            pgDown.setProgress(100);
            pgDown.dismiss();
            startInstall(getActivity(), resultFile);
//             publicAccount.adapter.refresh();
        }

        @Override
        public void onFail(String error) {
            DialogMsg.showDetail(getActivity(), "更新失败", "更新失败，请重试", error);
        }

    };
    private void downloadApk(App_InformationBean app_informationBean) {
        String url = app_informationBean.app_download_url;
        final String path = getApkPath();
        File apkSaveFolder = new File(path);
        if (!apkSaveFolder.exists()) {
            apkSaveFolder.mkdirs();
        }
        if (null != syncDownAPK) {
            syncDownAPK.stop();
        }
        syncDownAPK = new SyncDownAPK();
        syncDownAPK.setLsn(onDownApkLsn);
        syncDownAPK.start(path, getCLearFileName(url), url);
    }
}
