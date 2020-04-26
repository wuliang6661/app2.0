package synway.module_publicaccount.publiclist;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import qyc.library.control.dialog_confirm.OnDialogConfirmCancel;
import qyc.library.control.dialog_msg.DialogMsg;
import synway.module_interface.config.BaseUtil;
import synway.module_interface.config.netConfig.NetConfig;
import synway.module_interface.config.netConfig.Sps_NetConfig;
import synway.module_interface.config.userConfig.LoginUser;
import synway.module_interface.config.userConfig.Sps_RWLoginUser;
import synway.module_publicaccount.R;
import synway.module_publicaccount.public_chat.Obj_Menu;
import synway.module_publicaccount.public_chat.PublicAccountChatActNormal;
import synway.module_publicaccount.public_chat.bottom.SyncGetMenuByDB.IOnGetMenuByDB;
import synway.module_publicaccount.public_chat.bottom.SyncSendMenuLsn.IOnSendLsn;
import synway.module_publicaccount.public_favorite.PublicDBUtils;
import synway.module_publicaccount.public_favorite.obj.Obj_PublicAccount_Favorite;
import synway.module_publicaccount.until.AppUtil;
import synway.module_publicaccount.until.DialogTool;
import synway.module_publicaccount.until.DownLoad.SyncDownAPK;
import synway.module_publicaccount.until.StringUtil;

import static synway.module_publicaccount.publiclist.until.ShortCUtUntil.addShortcut;
import static synway.module_publicaccount.until.AppUtil.doStartApplicationWithPackageName;
import static synway.module_publicaccount.until.AppUtil.getAppIcon;
import static synway.module_publicaccount.until.DownLoad.InstallApk.startInstall;
import static synway.module_publicaccount.until.PicUtil.getApkPath;
import static synway.module_publicaccount.until.StringUtil.getCLearFileName;


 class PublicAccountBottomViewTools {

//    private Dialog dialog = null;
//    private Context context = null;
    private Activity activity=null;
    private HorizontalScrollView h_ScrollView = null;
    private NetConfig netConfig = null;
    private LoginUser loginUser = null;
    private String publicGUID = null;

    private String publicAccountName = null;

    private String loginUserID = null,picid=null;

    //===============================================

    /**  常用应用的上传和入库*/
//    private SynUploadAndSavePublicFav synUploadAndSavePublicFav = null;
    /** 记录长按item的position*/
    private int longClickItemPosition = -1;
    /** 判断是否为一级菜单，默认为false*/
    private boolean isMenuFather = false;
    private PublicAccountAdapter.ViewHolder viewHolder;
    private Dialog uploadDialog = null;
    private PublicAccountAct publicAccount=null;
    private DialogMsg dialogMsg=null;
    private SyncGetHeadThu syncGetHeadThu = null;
    private ArrayList<Obj_Menu> firstmenus=null;
    private ArrayList<Obj_Menu> secondsmenus=null;
    private Obj_PublicAccount obj_publicAccount=null;
     private ProgressDialog pgDown = null;
     PublicAccountBottomViewTools(PublicAccountAct publicAccount, Activity activity, PublicAccountAdapter.ViewHolder viewHolder, LoginUser loginUser, Obj_PublicAccount obj, SyncGetHeadThu syncGetHeadThu) {
         this.obj_publicAccount=obj;
        this.firstmenus=obj.firstmenus;
        this.secondsmenus=obj.secondmenus;
        this.publicAccount=publicAccount;
        this.picid=obj.fc_mobilepic;
        this.viewHolder=viewHolder;
        this.activity=activity;
        this.loginUser = loginUser;
        this.publicGUID =obj.ID;
        this.netConfig = Sps_NetConfig.getNetConfigFromSpf(activity);
        this.publicAccountName = obj.name;
        this.loginUserID = Sps_RWLoginUser.readUserID(activity);
        dialogMsg=new DialogMsg();
        this.syncGetHeadThu=syncGetHeadThu;
//        this.menuAdapter = new MenuAdapter(activity);


    }

    public void init() {
        pgDown = new ProgressDialog(activity);
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
        //菜单是空的，添加一个虚拟菜单，点击直接进入公众平台详细界面
        if (firstmenus == null || firstmenus.size() == 0) {
            if(obj_publicAccount.appList!=null&&obj_publicAccount.appList.size()>0){
                SetMenuLayout(obj_publicAccount.appList);
            }else{
                addViewBottom(firstmenus,secondsmenus);
            }
        }else {
            addViewBottom(firstmenus, secondsmenus);
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(publicGUID);
        }
    }

    private IOnGetMenuByDB iOnGetMenuByDB = new IOnGetMenuByDB() {

        @Override
        public void onResult(ArrayList<Obj_Menu> arrayList,
                             ArrayList<Obj_Menu> secondMenuList) {
            //菜单是空的，添加一个虚拟菜单，点击直接进入公众平台详细界面
            if (arrayList == null || arrayList.size() == 0) {
                addViewBottom(arrayList,secondMenuList);
                return;
            }
            addViewBottom(arrayList,secondMenuList);
        }
       //没有查到菜单
        @Override
        public void onFail(String title, String reason, String detail) {
            //模拟一个
            addViewBottom(null,null);
            Log.e("zjw", title + reason + detail);
        }
    };
    //添加标题布局
    private void addViewBottom(ArrayList<Obj_Menu> arrayList, ArrayList<Obj_Menu> secondMenuList){
        //移除所有布局
        viewHolder.addbottom.removeAllViews();
        viewHolder.addbottom.removeAllViewsInLayout();
        ArrayList<LinearLayout> linearLayouts;
        //有菜单
          if(arrayList!=null&&arrayList.size()!=0){
              //所有需要显示的菜单
              ArrayList<Obj_Menu> arrayList_all= new ArrayList<>();
              for(int i=0;i<arrayList.size();i++){
                  //有子菜单
                 if(arrayList.get(i).menuType== 0){
                     //子菜单
                     for (int j= 0;j< secondMenuList.size(); j++) {
                         if (arrayList.get(i).menuGUID.equals(secondMenuList.get(j).menuFather)) {
                             arrayList_all.add(secondMenuList.get(j));
                         }
                     }
                 }
                 //没有子菜单的一级菜单
                 else {
                     arrayList_all.add(arrayList.get(i));
                 }
              }
              //算出总共需要几行放按钮
              int c = arrayList_all.size()%4==0?arrayList_all.size()/4:arrayList_all.size()/4+1;
              linearLayouts=biggest_layout(c);
              //总共有多少个放单个图片和标题的布局
              ArrayList<LinearLayout> linearLayout_detail_list=new ArrayList<LinearLayout>();
              LinearLayout linearLayout_detail=null;
              for(int h=0;h<arrayList_all.size();h++) {
                  linearLayout_detail = single_Layout("", arrayList_all.get(h).menuName,arrayList_all.get(h),arrayList_all.get(h).menuGUID,arrayList_all.get(h).menuPicUrl,null);
                  linearLayout_detail_list.add(linearLayout_detail);
              }
              //需要几行的循环
              if(c==0){
                  Obj_Menu obj_menu=null;
                  //公众帐号没有菜单的情况
                  LinearLayout linearLayout=biggest_layout(1).get(0);
                  LinearLayout linearLayout_detai=single_Layout("",publicAccountName,null,publicGUID,picid,null);
                  //给总布局添加单个布局
                  linearLayout.addView(linearLayout_detai);
                  viewHolder.addbottom.addView(linearLayout);
              }else {
                  for (int k = 0; k < c; k++) {
                      for (int z = k * 4; z < (k + 1) * 4; z++) {
                          //添加单个包图包文字的layout
                          if (z < linearLayout_detail_list.size()) {
                              linearLayouts.get(k).addView(linearLayout_detail_list.get(z));
                          }
                      }
                      viewHolder.addbottom.addView(linearLayouts.get(k));
                  }
              }
              return;
          }
        //公众帐号没有菜单的情况
        LinearLayout linearLayout=biggest_layout(1).get(0);
        LinearLayout linearLayout_detai=single_Layout("",publicAccountName,null,publicGUID,picid,null);
        //给总布局添加单个布局
        linearLayout.addView(linearLayout_detai);
        viewHolder.addbottom.addView(linearLayout);
    }
    // 包四个图标和名字的布局
    public ArrayList<LinearLayout> biggest_layout(int num){
        WindowManager wm = (WindowManager)activity.getSystemService(Context.WINDOW_SERVICE);
        ArrayList<LinearLayout> linearLayouts=new ArrayList<LinearLayout>();
        for(int i=0;i<num;i++){
            LinearLayout linearLayout= new LinearLayout(activity);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,dip2px(activity,100));
            layoutParams.rightMargin= wm.getDefaultDisplay().getWidth()/40;
            linearLayout.setLayoutParams( layoutParams);
            linearLayouts.add(linearLayout);
        }

        return  linearLayouts;
    }
    //包单个图标和名字的布局
    private LinearLayout single_Layout(String picurl, String bottomtext, Obj_Menu obj_menu, String picpath, String WebPicId, final Obj_PublicAccount appobj){
        WindowManager wm = (WindowManager)activity.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        LinearLayout linearLayout_detai=new LinearLayout(activity);
        linearLayout_detai.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(width/4-width/160,LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.gravity= Gravity.CENTER;
//        layoutParams.rightMargin=width/12;
        linearLayout_detai.setLayoutParams( layoutParams);
        //添加菜单图标
        ImageView imageView=new ImageView(activity);
        String path = getPublicName(picpath);
//        Log.i("testy","进来了詳情界面"+path);
        Drawable drawablepic= Drawable.createFromPath(path);
        if(null != drawablepic) {
            imageView.setImageDrawable(drawablepic);
        }else {
            if(StringUtil.isEmpty(WebPicId)) {
                imageView.setImageResource(R.drawable.contact_public_account_png);
            }else{
                imageView.setImageResource(R.drawable.contact_public_account_png);
                syncGetHeadThu.startPublicIdIcon(picpath,WebPicId);
            }
        }
        LinearLayout.LayoutParams layoutParamImageView=new LinearLayout.LayoutParams(dip2px(activity,40),dip2px(activity,60));
        layoutParamImageView.gravity=Gravity.CENTER_HORIZONTAL;
        imageView.setLayoutParams(layoutParamImageView);
        if(appobj!=null){//app应用
            PackageInfo packageInfo=AppUtil.isAvilible(activity,appobj.app_information.app_packangename);
            Button button = new Button(activity);
            button.setBackgroundResource(R.drawable.item_click);
            button.setTextSize(15);
            button.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams layoutParamsTextView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsTextView.gravity = Gravity.CENTER;
            if(packageInfo==null){//该应用没有安装
                button.setText("下载");
                button.setLayoutParams(layoutParamsTextView);
                button.setTag(appobj);
                button.setOnClickListener(DownloadAppOnClickListener);
            }else{
                if(null == drawablepic){
                    Drawable drawable=getAppIcon(packageInfo.packageName,activity);
                    if(drawable!=null){
                        imageView.setImageDrawable(drawable);
                    }
                }
                if(packageInfo.versionName.equals(appobj.app_information.app_version)){//版本一样，直接打开
                    button.setText("打开");
                    button.setLayoutParams(layoutParamsTextView);
                    button.setTag(packageInfo);
                    button.setOnClickListener(OpenAppOnClickListener);
                }else{
                    button.setText("更新");
                    button.setLayoutParams(layoutParamsTextView);
                    button.setTag(appobj);
                    button.setOnClickListener(DownloadAppOnClickListener);
                }
            }
            //单个图标和名字的布局添加图片
            linearLayout_detai.addView(imageView);
            linearLayout_detai.addView(button);
        }else {
            //添加菜单名字
            TextView textView = new TextView(activity);
            textView.setText(bottomtext);
            textView.setTextSize(15);
            textView.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams layoutParamsTextView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsTextView.gravity = Gravity.CENTER;
            textView.setLayoutParams(layoutParamsTextView);
            //单个图标和名字的布局添加图片
            linearLayout_detai.addView(imageView);
            //单个图标和名字的布局添加标题
            linearLayout_detai.addView(textView);
        }
        final Obj_Menu obj_menu1=obj_menu;
        linearLayout_detai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //虚拟菜单直接跳到公众号
                if(obj_menu1==null){
                    if(appobj!=null){//app式應用
                      Log.i("testy","app式应用");
                    }else {
                        Intent intent = new Intent();
                        intent.setClass(activity, PublicAccountChatActNormal.class);
                        intent.putExtra("ACCOUNT_ID", publicGUID);
                        intent.putExtra("ACCOUNT_NAME", publicAccountName);
                        activity.startActivity(intent);
                    }
                }
                //有菜单
                else {
                    performMenuClick(obj_menu1);
                }
            }
        });
        //长按
        linearLayout_detai.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Window window = DialogTool.dialog(activity, R.layout.model_public_dialog);
                LinearLayout uploadIn = window.findViewById(R.id.uploadIntellgence);
                uploadIn.setVisibility(View.GONE);
                LinearLayout addNormalApplication = window.findViewById(R.id.addNormalApplication);
                addNormalApplication.setTag(obj_menu1);
                addNormalApplication.setOnClickListener(addNormalAppOnclick);
                addNormalApplication.setVisibility(View.VISIBLE);
                LinearLayout addToDeskTop = window.findViewById(R.id.addToDeskTop);
                addToDeskTop.setOnClickListener(addToDesktop);
                addToDeskTop.setTag(obj_menu1);
                if(obj_menu1==null&& appobj!=null){
                    addToDeskTop.setVisibility(View.GONE);
                }else {
                    addToDeskTop.setVisibility(View.VISIBLE);
                }
//                isMenuFather = false;
                //如果是虚拟菜单
//
                return true;
            }
        });
        return  linearLayout_detai;
    }



    private boolean isSavePublicFavorite(String publicAccountID,String menuName) {
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



//    /**
//     * 将Obj_PublicAccount_Favorite转换为json数据
//     * @param favorite
//     * @return
//     */
//    private String tranferPublicFavObjToJsonString(Obj_PublicAccount_Favorite favorite) {
//        String returnStr = "";
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("ID", favorite.ID);
//            jsonObject.put("Name", favorite.Name);
//            jsonObject.put("menuName", favorite.menuName);
//            jsonObject.put("menuUrl", favorite.menuUrl);
//            jsonObject.put("isHtml", favorite.isHtml);
//
//            returnStr = jsonObject.toString();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return returnStr;
//
//    }



    /**
     * @param obj_Menu
     * 菜单点击事件
     */
    private void performMenuClick(Obj_Menu obj_Menu) {
//        MenuTypeDeal.performMenuClick(obj_Menu,activity);
        if (obj_Menu.menuType == 1) {
            if (obj_Menu.menuUrl == null) {
                return;
            }
            //改成先打开公众号页面再打开网页
            Intent intent=new Intent(activity,PublicAccountChatActNormal.class);
            Bundle mBundle = new Bundle();
            mBundle.putSerializable("obj_Menu",obj_Menu);
            intent.putExtra("ACCOUNT_ID",publicGUID);
            intent.putExtra("ACCOUNT_NAME",publicAccountName);
            intent.putExtra("MenuKry_Click","weburl");
            intent.putExtra("skipType","keyBoard");
            intent.putExtras(mBundle);
            activity.startActivity(intent);
//            Intent intent = new Intent();
//            String name = Base64Helper.getBASE64(loginUser.name);
//            String url=obj_Menu.menuUrl + "?userName=" + name
//                    + "&currentTime=" + System.currentTimeMillis();
//            intent.putExtra("URL", url);
//            intent.putExtra("NAME", obj_Menu.menuName);
//            intent.setClass(activity, PAWebViewAct.class);
//            activity.startActivity(intent);
        } else if (obj_Menu.menuType == 2) {
            Intent intent=new Intent(activity,PublicAccountChatActNormal.class);
            Bundle mBundle = new Bundle();
            mBundle.putSerializable("obj_Menu",obj_Menu);
            intent.putExtra("ACCOUNT_ID",publicGUID);
            intent.putExtra("ACCOUNT_NAME",publicAccountName);
            intent.putExtra("MenuKry_Click","click");
            intent.putExtras(mBundle);
            activity.startActivity(intent);


        }
    }

    /**
     * k dp ^jλ px()
     */
    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void destory() {
//        if (null != syncSend) {
//            syncSend.stop();
//        }

//        if (null != syncGetMenuList) {
//            syncGetMenuList.stop();
//        }

//        if (syncGetMenuByDB != null) {
//            syncGetMenuByDB.stop();
//        }

//        if (synUploadAndSavePublicFav != null) {
//            synUploadAndSavePublicFav.stop();
//        }
    }
//    /**
//     * 设置添加屏幕的背景透明度
//     * @param bgAlpha
//     */
//    public void backgroundAlpha(float bgAlpha)
//    {
//        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
//        lp.alpha = bgAlpha; //0.0-1.0
//        activity.getWindow().setAttributes(lp);
//    }


    private OnDialogConfirmCancel onClick2 = new OnDialogConfirmCancel() {

        @Override
        public void onDialogConfirmCancel() {

        }
    };
    private IOnSendLsn sendReuslt = new IOnSendLsn() {
        @Override
        public void onResult() {
//            toast.setVisibility(View.GONE);
        }

        @Override
        public void onFail(String title, String reason, String detail) {
            DialogMsg.showDetail(activity, title, reason, detail);
//            toast.setVisibility(View.GONE);
        }
    };
    private String getPublicName(String id) {
        return BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + id;
    }

 private  View.OnClickListener addNormalAppOnclick=new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          Obj_Menu obj_menu1=(Obj_Menu) v.getTag();
          DialogTool.dialog.dismiss();
          if(obj_menu1==null){
                    boolean isSavePublicFavorite = IsSaveJudge.isSavePublicFavorite(publicGUID);
                    if (isSavePublicFavorite) {
                        Toast.makeText(activity, "请不要重复加入常用公众号", Toast.LENGTH_SHORT).show();
                    } else {
//                        DialogConfirm.show(activity, "提示", "是否加入常用应用？",
//                                new OnDialogConfirmClick() {
//                                    @Override
//                                    public void onDialogConfirmClick() {
                                        Obj_PublicAccount_Favorite favorite = new Obj_PublicAccount_Favorite();
                                        favorite.ID = publicGUID;
                                        favorite.Name = publicAccountName;
                                        favorite.isHtml = false;
                                        favorite.fc_mobilepic=picid;
                                        //开始进行上传和入库
                                        publicAccount.synUploadAndSavePublicFav.start(favorite, Sps_RWLoginUser.readUserID(activity));
//                                    }
//                                }, new OnDialogConfirmCancel() {
//                                    @Override
//                                    public void onDialogConfirmCancel() {
//                                    }
//                                });
                    }
                }
                //有菜单
                else {
                    Obj_Menu obj_Menu = obj_menu1;
                    final String menuName = obj_Menu.menuName;
                    final String menuUrl = obj_Menu.menuUrl;
                    final String menupicurl=obj_Menu.menuPicUrl;
                    final String menuid=obj_Menu.menuGUID;
                    final String publicid=obj_Menu.ID;
                    //判断是否为网页跳转类
                    if (obj_Menu.menuType == 1) {
                        boolean isSavePublicFavorite = isSavePublicFavorite(publicGUID, menuName);
                        if (isSavePublicFavorite) {
                            Toast.makeText(activity, "请不要重复加入常用公众号", Toast.LENGTH_SHORT).show();
                        } else {
//                            DialogConfirm.show(activity, "提示", "是否加入常用应用？",
//                                    new OnDialogConfirmClick() {
//                                        @Override
//                                        public void onDialogConfirmClick() {
                                            Obj_PublicAccount_Favorite favorite = new Obj_PublicAccount_Favorite();
                                            favorite.ID = publicid;
                                            favorite.Name = publicAccountName;
                                            favorite.isHtml = true;
                                            favorite.menuName = menuName;
                                            favorite.menuUrl = menuUrl;
                                            favorite.fc_mobilepic=menupicurl;
                                            favorite.MenuId=menuid;
                                            publicAccount.synUploadAndSavePublicFav.start(favorite, loginUserID);
//                                        }
//                                    }, new OnDialogConfirmCancel() {
//                                        @Override
//                                        public void onDialogConfirmCancel() {
//                                        }
//                                    });
                        }

                    } else {
                        // Toast.makeText(activity, "暂时不支持加入常用应用", Toast.LENGTH_SHORT).show();
                    }
                }
      }
  };
 private  View.OnClickListener addToDesktop=new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Obj_Menu obj_menu=(Obj_Menu)v.getTag();
          //虚拟菜单直接跳到公众号
          Intent intent = new Intent(Intent.ACTION_MAIN);
          //调整启动zzw
          ComponentName cn = new ComponentName(activity.getPackageName(),"synway.module_publicaccount.public_chat.PublicAccountChatActNormal");
//          ComponentName cn = new ComponentName("synway.osc.home.HomeAct","synway.module_publicaccount.public_chat.PublicAccountChatAct");

          intent.setComponent(cn);
          String path;

          if(obj_menu==null){
              path= getPublicName(publicGUID);
              intent.putExtra("ACCOUNT_ID", publicGUID);
              intent.putExtra("ACCOUNT_NAME", publicAccountName);
              addShortcut(activity, publicAccountName, BitmapFactory.decodeFile(path), intent, false);
          }
          else {
              path = getPublicName(obj_menu.menuGUID);
              if (obj_menu.menuType == 1) {
                  //改成先打开公众号页面再打开网页
//                  Bundle mBundle = new Bundle();
                  intent.putExtra("ACCOUNT_NAME", publicAccountName);
                  intent.putExtra("ACCOUNT_ID", publicGUID+","+obj_menu.menuGUID+","+obj_menu.menuName);
                  intent.putExtra("MenuKry_Click","weburl");
//                  intent.putExtra("skipType","keyBoard");
//                  mBundle.putSerializable("obj_Menu",obj_menu);
//                  intent.putExtras(mBundle);
              } else if (obj_menu.menuType == 2) {
//                  Bundle mBundle = new Bundle();
                  intent.putExtra("ACCOUNT_ID", publicGUID+","+obj_menu.menuGUID+","+obj_menu.menuName);
                  intent.putExtra("ACCOUNT_NAME", publicAccountName);
                  intent.putExtra("MenuKry_Click","click");
//                  mBundle.putSerializable("obj_Menu",obj_menu);
//                  intent.putExtras(mBundle);
              }
              addShortcut(activity, obj_menu.menuName, BitmapFactory.decodeFile(path), intent, false);
          }


//          Drawable drawablepic= Drawable.createFromPath(path);

      }
  };


    //    private SyncGetHeadThu.IOnGetHeadThu onGetHeadThu = new SyncGetHeadThu.IOnGetHeadThu() {
//        @Override
//        public void onHeadGet(String ID) {
////            adapter.refresh();
//        }
//    };
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                finish();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
   private  View.OnClickListener OpenAppOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PackageInfo packageInfo=(PackageInfo) v.getTag();
            doStartApplicationWithPackageName(activity,packageInfo);
        }
    };
     private SyncDownAPK syncDownAPK = null;
     private  View.OnClickListener DownloadAppOnClickListener=new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Obj_PublicAccount appobj=(Obj_PublicAccount)v.getTag();
             String url=appobj.app_information.app_download_url;
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
     };
     private SyncDownAPK.IOnDownApkLsn onDownApkLsn = new SyncDownAPK.IOnDownApkLsn() {

         @Override
         public void onStart() {
           Log.i("testy","开始下载");
             if (pgDown.isShowing()) {
                 pgDown.dismiss();
             }
             pgDown.setProgress(0);
             pgDown.show();
         }

         @Override
         public void onProgress(int progress) {
          Log.i("testy","下载进度"+progress);
             pgDown.setProgress(progress);
         }

         @Override
         public void onFinish(File resultFile) {
             pgDown.setProgress(100);
             pgDown.dismiss();
             startInstall(activity,resultFile);
//             publicAccount.adapter.refresh();
         }

         @Override
         public void onFail(String error) {
             DialogMsg.showDetail(activity, "更新失败", "更新失败，请重试", error);
         }

     };
     public void  onDestory(){
         if(syncDownAPK!=null){
             syncDownAPK.stop();
         }
         if(syncGetHeadThu!=null){
             syncGetHeadThu.stop();
         }
     }
     private void SetMenuLayout(ArrayList<Obj_PublicAccount> arrayList_all){
         ArrayList<LinearLayout> linearLayouts;
         //算出总共需要几行放按钮
         int c = arrayList_all.size()%4==0?arrayList_all.size()/4:arrayList_all.size()/4+1;
         linearLayouts=biggest_layout(c);
         //总共有多少个放单个图片和标题的布局
         ArrayList<LinearLayout> linearLayout_detail_list=new ArrayList<LinearLayout>();
         LinearLayout linearLayout_detail=null;
         for(int h=0;h<arrayList_all.size();h++) {
             Obj_Menu obj_menu=new Obj_Menu();
             linearLayout_detail = single_Layout("", arrayList_all.get(h).name,null,arrayList_all.get(h).ID,arrayList_all.get(h).fc_mobilepic,arrayList_all.get(h));
             linearLayout_detail_list.add(linearLayout_detail);
         }
             for (int k = 0; k < c; k++) {
                 for (int z = k * 4; z < (k + 1) * 4; z++) {
                     //添加单个包图包文字的layout
                     if (z < linearLayout_detail_list.size()) {
                         linearLayouts.get(k).addView(linearLayout_detail_list.get(z));
                     }
                 }
                 viewHolder.addbottom.addView(linearLayouts.get(k));
             }
     }
 }