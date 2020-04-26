package synway.module_publicaccount.publiclist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import qyc.library.control.dialog_msg.DialogMsg;
import synway.module_interface.config.BaseUtil;
import synway.module_interface.config.userConfig.Sps_RWLoginUser;
import synway.module_publicaccount.R;
import synway.module_publicaccount.public_chat.PublicAccountChatActNormal;
import synway.module_publicaccount.public_favorite.obj.Obj_PublicAccount_Favorite;
import synway.module_publicaccount.until.AppUtil;
import synway.module_publicaccount.until.DialogTool;
import synway.module_publicaccount.until.DownLoad.SyncDownAPK;
import synway.module_publicaccount.until.StringUtil;
import static android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM;
import static android.widget.RelativeLayout.ALIGN_PARENT_END;
import static android.widget.RelativeLayout.ALIGN_PARENT_RIGHT;
import static android.widget.RelativeLayout.ALIGN_PARENT_TOP;
import static android.widget.RelativeLayout.CENTER_HORIZONTAL;
import static android.widget.RelativeLayout.CENTER_VERTICAL;
import static synway.module_publicaccount.publiclist.until.ShortCUtUntil.addShortcut;
import static synway.module_publicaccount.until.AppUtil.doStartApplicationWithPackageName;
import static synway.module_publicaccount.until.AppUtil.getAppIcon;
import static synway.module_publicaccount.until.DownLoad.InstallApk.startInstall;
import static synway.module_publicaccount.until.PicUtil.getApkPath;
import static synway.module_publicaccount.until.StringUtil.getCLearFileName;


class PublicAccountBottomViewTools2 {
    private Activity activity = null;
    private String picid = null;
    private PublicAccountAdapter2.ViewHolder viewHolder;
    private PublicAccountAct2 publicAccount = null;
    private SyncGetHeadThu syncGetHeadThu = null;
    private Obj_PublicAccount obj_publicAccount = null;
    private ProgressDialog pgDown = null;

    PublicAccountBottomViewTools2(PublicAccountAct2 publicAccount, Activity activity, PublicAccountAdapter2.ViewHolder viewHolder, Obj_PublicAccount obj, SyncGetHeadThu syncGetHeadThu) {
        this.obj_publicAccount = obj;
        this.publicAccount = publicAccount;
        this.viewHolder = viewHolder;
        this.activity = activity;
        this.syncGetHeadThu = syncGetHeadThu;
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
        SetMenuLayout(obj_publicAccount.publicobjs);
    }


    // 包四个图标和名字的布局
    private ArrayList<LinearLayout> biggest_layout(int num) {
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        ArrayList<LinearLayout> linearLayouts = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            LinearLayout linearLayout = new LinearLayout(activity);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dip2px(activity, 100));
            layoutParams.rightMargin = wm.getDefaultDisplay().getWidth() / 40;
            linearLayout.setLayoutParams(layoutParams);
            linearLayouts.add(linearLayout);
        }

        return linearLayouts;
    }

    //包单个图标和名字的布局
    private ViewGroup single_Layout(String bottomtext, String picpath, String WebPicId, final Obj_PublicAccount obj_publicAccount) {
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        RelativeLayout linearLayout_detai = new RelativeLayout(activity);
//        linearLayout_detai.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width / 4 - width / 160, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
//        layoutParams.rightMargin=width/12;
        linearLayout_detai.setLayoutParams(layoutParams);
        //添加菜单图标
        ImageView imageView = new ImageView(activity);
        String path = getPublicName(picpath);
        Drawable drawablepic = Drawable.createFromPath(path);
        if (null != drawablepic) {
            imageView.setImageDrawable(drawablepic);
        } else {
            if (StringUtil.isEmpty(WebPicId)) {
                imageView.setImageResource(R.drawable.contact_public_account_png);
            } else {
                imageView.setImageResource(R.drawable.contact_public_account_png);
                syncGetHeadThu.startPublicIdIcon(picpath, WebPicId);
            }
        }
        RelativeLayout.LayoutParams layoutParamImageView = new RelativeLayout.LayoutParams(dip2px(activity, 50), dip2px(activity, 50));
//        layoutParamImageView.gravity = Gravity.CENTER_HORIZONTAL;
        layoutParamImageView.topMargin = dip2px(activity, 10);
        layoutParamImageView.addRule(CENTER_HORIZONTAL);
        imageView.setLayoutParams(layoutParamImageView);
        //单个图标和名字的布局添加图片
        linearLayout_detai.addView(imageView);
        //添加菜单名字
        TextView textView = new TextView(activity);
        textView.setText(bottomtext);
        textView.setTextSize(15);
        textView.setGravity(Gravity.CENTER);
        textView.setSingleLine(true);
        textView.setEllipsize((TextUtils.TruncateAt.valueOf("END")));
        RelativeLayout.LayoutParams layoutParamsTextView = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsTextView.bottomMargin = dip2px(activity, 10);
//            layoutParamsTextView.gravity = Gravity.CENTER;
        layoutParamsTextView.addRule(ALIGN_PARENT_BOTTOM);
        layoutParamsTextView.addRule(CENTER_VERTICAL);
//           layoutParamsTextView.addRule(CENTER_IN_PARENT);
        textView.setLayoutParams(layoutParamsTextView);
        //单个图标和名字的布局添加标题
        linearLayout_detai.addView(textView);
        RelativeLayout.LayoutParams iconLayoutParamImageView = null;
        if (obj_publicAccount.type == 1) {
            PackageInfo packageInfo = AppUtil.isAvilible(activity, obj_publicAccount.app_information.app_packangename);
            TextView imageViewIcon = new TextView(activity);
            if (packageInfo == null) {//该应用没有安装，添加下载小图标
                iconLayoutParamImageView = new RelativeLayout.LayoutParams(dip2px(activity, 30), dip2px(activity, 30));
                imageViewIcon.setBackgroundResource(R.drawable.down);
            } else {
//                if (null == drawablepic) {
                    Drawable drawable = getAppIcon(packageInfo.packageName, activity);
                    if (drawable != null) {
                        imageView.setImageDrawable(drawable);
                    }
                    if (!packageInfo.versionName.equals(obj_publicAccount.app_information.app_version)) {//版本不一样，添加更新红点
                        iconLayoutParamImageView = new RelativeLayout.LayoutParams(dip2px(activity, 15), dip2px(activity, 15));
                        iconLayoutParamImageView.rightMargin = dip2px(activity, 12);
                        iconLayoutParamImageView.topMargin = dip2px(activity, 8);
                        imageViewIcon.setBackgroundResource(R.drawable.gengxin);
                    }
//                }
            }
//            iconLayoutParamImageView.bottomMargin=dip2px(activity, 10);
            if(iconLayoutParamImageView!=null){
                iconLayoutParamImageView.addRule(ALIGN_PARENT_RIGHT);
                iconLayoutParamImageView.addRule(ALIGN_PARENT_TOP);
                iconLayoutParamImageView.addRule(ALIGN_PARENT_END);
                imageViewIcon.setLayoutParams(iconLayoutParamImageView);
                linearLayout_detai.addView(imageViewIcon);
            }
        }
//        }
        linearLayout_detai.setTag(obj_publicAccount);
        linearLayout_detai.setOnClickListener(publicOnClickListener);
        //长按
        linearLayout_detai.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Window window = DialogTool.dialog(activity, R.layout.model_public_dialog);
                LinearLayout uploadIn = window.findViewById(R.id.uploadIntellgence);
                uploadIn.setVisibility(View.GONE);
                LinearLayout addNormalApplication = window.findViewById(R.id.addNormalApplication);
                addNormalApplication.setTag(obj_publicAccount);
                addNormalApplication.setOnClickListener(addNormalAppOnclick);
                // addNormalApplication.setVisibility(View.VISIBLE);
                addNormalApplication.setVisibility(View.GONE);
                LinearLayout addToDeskTop = window.findViewById(R.id.addToDeskTop);
                addToDeskTop.setOnClickListener(addToDesktop);
                addToDeskTop.setTag(obj_publicAccount);
                if (obj_publicAccount.type == 1) {
                    addToDeskTop.setVisibility(View.GONE);
                } else {
                    addToDeskTop.setVisibility(View.VISIBLE);
                }
//                isMenuFather = false;
                //如果是虚拟菜单
//
                return true;
            }
        });
        return linearLayout_detai;
    }


    /**
     * k dp ^jλ px()
     */
    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    private String getPublicName(String id) {
        return BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + id;
    }

    private View.OnClickListener addNormalAppOnclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Obj_PublicAccount obj_publicAccount = (Obj_PublicAccount) v.getTag();
            DialogTool.dialog.dismiss();
            boolean isSavePublicFavorite = IsSaveJudge.isSavePublicFavorite(obj_publicAccount.ID);
            if (isSavePublicFavorite) {
                Toast.makeText(activity, "请不要重复加入常用公众号", Toast.LENGTH_SHORT).show();
            } else {
                Obj_PublicAccount_Favorite favorite = new Obj_PublicAccount_Favorite();
                favorite.ID = obj_publicAccount.ID;
                favorite.Name = obj_publicAccount.name;
                favorite.isHtml = false;
                favorite.fc_mobilepic = obj_publicAccount.fc_mobilepic;
                favorite.type=obj_publicAccount.type;
                favorite.app_information=obj_publicAccount.app_information;
                //开始进行上传和入库
                publicAccount.synUploadAndSavePublicFav.start(favorite, Sps_RWLoginUser.readUserID(activity));
            }
        }
    };
    private View.OnClickListener addToDesktop = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Obj_PublicAccount obj_publicAccount = (Obj_PublicAccount) v.getTag();
            //虚拟菜单直接跳到公众号
            Intent intent = new Intent(Intent.ACTION_MAIN);
            //调整启动zzw
            ComponentName cn = new ComponentName(activity.getPackageName(), "synway.module_publicaccount.public_chat.PublicAccountChatActNormal");
//            ComponentName cn = new ComponentName("synway.osc.home.HomeAct", "synway.module_publicaccount.public_chat.PublicAccountChatAct");
            intent.setComponent(cn);
            String path;
            path = getPublicName(obj_publicAccount.ID);
            intent.putExtra("ACCOUNT_ID", obj_publicAccount.ID);
            intent.putExtra("ACCOUNT_NAME", obj_publicAccount.name);
            addShortcut(activity, obj_publicAccount.name, BitmapFactory.decodeFile(path), intent, false);
        }
    };

    private SyncDownAPK syncDownAPK = null;
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
            startInstall(activity, resultFile);
//             publicAccount.adapter.refresh();
        }

        @Override
        public void onFail(String error) {
            DialogMsg.showDetail(activity, "更新失败", "更新失败，请重试", error);
        }

    };

    public void onDestory() {
        if (syncDownAPK != null) {
            syncDownAPK.stop();
        }
        if (syncGetHeadThu != null) {
            syncGetHeadThu.stop();
        }
    }

    private void SetMenuLayout(ArrayList<Obj_PublicAccount> arrayList_all) {
        viewHolder.addbottom.removeAllViews();
        viewHolder.addbottom.removeAllViewsInLayout();
        ArrayList<LinearLayout> linearLayouts;
        //算出总共需要几行放按钮
        int c = arrayList_all.size() % 4 == 0 ? arrayList_all.size() / 4 : arrayList_all.size() / 4 + 1;
        linearLayouts = biggest_layout(c);
        //总共有多少个放单个图片和标题的布局
        ArrayList<ViewGroup> linearLayout_detail_list = new ArrayList<>();
//        LinearLayout linearLayout_detail ;
        for (int h = 0; h < arrayList_all.size(); h++) {
//            linearLayout_detail = single_Layout(arrayList_all.get(h).name, arrayList_all.get(h).ID, arrayList_all.get(h).fc_mobilepic, arrayList_all.get(h));
            linearLayout_detail_list.add(single_Layout(arrayList_all.get(h).name, arrayList_all.get(h).ID, arrayList_all.get(h).fc_mobilepic, arrayList_all.get(h)));
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

    private View.OnClickListener publicOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Obj_PublicAccount obj_publicAccount = (Obj_PublicAccount) v.getTag();
            if (obj_publicAccount.type == 1) {
                //APP应用
                PackageInfo packageInfo = AppUtil.isAvilible(activity, obj_publicAccount.app_information.app_packangename);
                if (packageInfo == null) {//该应用没有安装
                    downloadApk(obj_publicAccount);
                } else {
                    if (packageInfo.versionName.equals(obj_publicAccount.app_information.app_version)) {//版本一样，直接打开
                        doStartApplicationWithPackageName(activity, packageInfo);
                    } else {
                        downloadApk(obj_publicAccount);
                    }
                }
            } else {
                //直接跳到公众号
                Intent intent = new Intent();
                intent.setClass(activity, PublicAccountChatActNormal.class);
                intent.putExtra("ACCOUNT_ID", obj_publicAccount.ID);
                intent.putExtra("ACCOUNT_NAME", obj_publicAccount.name);
                activity.startActivityForResult(intent,110);
            }
        }
    };

    private void downloadApk(Obj_PublicAccount obj_publicAccount) {
        String url = obj_publicAccount.app_information.app_download_url;
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