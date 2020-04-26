package synway.module_publicaccount.publiclist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import synway.module_interface.config.BaseUtil;
import synway.module_interface.config.userConfig.Sps_RWLoginUser;
import synway.module_publicaccount.R;
import synway.module_publicaccount.public_chat.PublicAccountChatActNormal;
import synway.module_publicaccount.public_favorite.obj.Obj_PublicAccount_Favorite;
import synway.module_publicaccount.until.DialogTool;
import synway.module_publicaccount.until.StringUtil;

import static synway.module_publicaccount.publiclist.until.ShortCUtUntil.addShortcut;
import static synway.module_publicaccount.until.AppUtil.getAppIcon;
import static synway.module_publicaccount.until.BroastCastUtil.INSTALLBROAST;
import static synway.module_publicaccount.until.BroastCastUtil.PACKGENAME;
import static synway.module_publicaccount.until.BroastCastUtil.UNINSTALLBROAST;

public class PublicAccountAdapter extends BaseAdapter {

    private ArrayList<Obj_PublicAccount> objList = null;
    private HashMap<String, Obj_PublicAccount> hashMap = null;
    private LayoutInflater inflater = null;
    // ================bottom View
    private PublicAccountBottomViewTools bottomViewTools = null;
    private Context context = null;
    private Activity activity = null;
    private Obj_PublicAccount obj = null;
    private PublicAccountAct publicAccount = null;
    private SyncGetHeadThu syncGetHeadThu = null;
    public PublicAccountAdapter(Context context, Activity activity, PublicAccountAct publicAccount, SyncGetHeadThu syncGetHeadThu) {
        objList = new ArrayList<>();
        hashMap = new HashMap<>();
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.activity = activity;
        this.publicAccount = publicAccount;
        this.syncGetHeadThu=syncGetHeadThu;
        IntentFilter intentFilter = new IntentFilter(INSTALLBROAST);
        context.registerReceiver(installreceiver, intentFilter);
        IntentFilter unintentFilter = new IntentFilter(UNINSTALLBROAST);
        context.registerReceiver(uninstallreceiver, unintentFilter);
    }

    public void reset(ArrayList<Obj_PublicAccount> list,ArrayList<Obj_PublicAccount> appList) {
        this.objList.clear();
        if(appList!=null&&appList.size()>0){
            Obj_PublicAccount obj2 = new Obj_PublicAccount();
            obj2.isItem = true;
            obj2.name = "应用超市";
            obj2.appList=appList;
            list.add(obj2);
        }
        this.objList = list;
        this.hashMap.clear();
        for (Obj_PublicAccount objP : list) {
            hashMap.put(objP.ID, objP);

        }

    }

    /**
     * 取List里面的其中一段，根据ListView提供的topIndex和bottomIndex
     */
    public List<Obj_PublicAccount> sub(int topIndex, int bottomIndex) {
        // 根据注释，list的sub函数，end是不包括的。
        // 而我们是start和end都包括的，因此这里去取end要+1
        return objList.subList(topIndex, bottomIndex + 1);
    }

    public Obj_PublicAccount getObjByID(String id) {
        return hashMap.get(id);
    }

    public void refresh() {
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return objList.size();
    }

    @Override
    public Object getItem(int position) {
        return objList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        if (!objList.get(position).isItem) {
            return false;// 表示不能点击
        }
        return super.isEnabled(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        obj = objList.get(position);
        ViewHolder holder ;
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.model_public_account_content_item, parent, false);
            holder = getHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        initHolder(holder, obj);
        //公众号号码监听
        holder.relLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Obj_PublicAccount obj = objList.get(position);
                if(obj.type==1){

                }else{
                    Intent intent = new Intent();
                    intent.setClass(context, PublicAccountChatActNormal.class);
                    intent.putExtra("ACCOUNT_ID", obj.ID);
                    intent.putExtra("ACCOUNT_NAME", obj.name);
                    context.startActivity(intent);
                }
            }
        });
        //公众号长按监听 弹出添加到桌面和加入常用应用对话框
        holder.relLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Obj_PublicAccount obj = objList.get(position);
                Window window = DialogTool.dialog(activity, R.layout.model_public_dialog);
                LinearLayout uploadIn = window.findViewById(R.id.uploadIntellgence);
                uploadIn.setVisibility(View.GONE);
                LinearLayout addNormalApplication = window.findViewById(R.id.addNormalApplication);
                addNormalApplication.setTag(position);
                addNormalApplication.setOnClickListener(addNormalAppOnclick);
                addNormalApplication.setVisibility(View.VISIBLE);
                LinearLayout addToDeskTop = window.findViewById(R.id.addToDeskTop);
                if(obj.type==1) {
                    addToDeskTop.setVisibility(View.GONE);
                }else {
                    addToDeskTop.setVisibility(View.VISIBLE);
                }
                addToDeskTop.setTag(position);
                addToDeskTop.setOnClickListener(addToDeskTopOnClick);
                return true;
            }
        });
        return convertView;
    }

    private ViewHolder getHolder(View convertView) {
        ViewHolder holder = new ViewHolder();
        holder.relLayout = convertView.findViewById(R.id.relativeLayout1);
        holder.imgvHead = convertView.findViewById(R.id.imageView1);
        holder.tvText = convertView.findViewById(R.id.textView1);
        holder.PublicActBottom = convertView.findViewById(R.id.PublicActBottom);
        holder.addbottom = convertView.findViewById(R.id.addbottom);
        return holder;
    }

    private void initHolder(ViewHolder holder, Obj_PublicAccount obj) {
        if (obj.isItem) {
            holder.relLayout.setEnabled(true);
            holder.relLayout.setBackgroundResource(R.drawable.alpha);
            holder.imgvHead.setVisibility(View.VISIBLE);
            holder.addbottom.setVisibility(View.VISIBLE);
            String path = getPublicName(obj.ID);
            Drawable drawable = Drawable.createFromPath(path);
            if (null != drawable) {
                holder.imgvHead.setImageDrawable(drawable);
            } else {
                if(StringUtil.isEmpty(obj.fc_mobilepic)) {
                    holder.imgvHead.setImageResource(R.drawable.contact_public_account_png);
//                    if(obj.type==1){//如果是APP式應用
//                        Drawable Appdrawable=getAppIcon(obj.app_information.app_packangename,activity);
//                        if(Appdrawable!=null){
//                            holder.imgvHead.setImageDrawable(Appdrawable);
//                        }
//                    }
                }else{
                    holder.imgvHead.setImageResource(R.drawable.contact_public_account_png);
                    syncGetHeadThu.startPublicIdIcon(obj.ID,obj.fc_mobilepic);
//                    syncGetHeadThu.start(obj.fc_mobilepic);
                }

            }
            holder.tvText.setText(obj.name);
            // init bottom
            bottomViewTools = new PublicAccountBottomViewTools(publicAccount,activity, holder, Sps_RWLoginUser.readUser(context),
                  obj,syncGetHeadThu);
            bottomViewTools.init();

        } else {
            holder.relLayout.setBackgroundResource(R.color.mgray_light);
            holder.relLayout.setEnabled(false);
            holder.imgvHead.setVisibility(View.GONE);
            holder.tvText.setText(obj.name);
            holder.addbottom.setVisibility(View.GONE);
        }

    }
    public class ViewHolder {
        RelativeLayout relLayout = null;
        ImageView imgvHead = null;
        TextView tvText = null;
        LinearLayout PublicActBottom = null;
        LinearLayout addbottom = null;
    }
    private String getPublicName(String id) {
        return BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + id;
    }
    private View.OnClickListener addNormalAppOnclick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position=(int)v.getTag();
            Obj_PublicAccount obj_PublicAccount = objList.get(position);
                final String publicAccountID = obj_PublicAccount.ID;
                final String publicAccountName = obj_PublicAccount.name;
                final String fcmobilepic=obj_PublicAccount.fc_mobilepic;
                //判断是否已经加入常用应用了
                boolean isSavePublicFavorite = IsSaveJudge.isSavePublicFavorite(publicAccountID);
                DialogTool.dialog.dismiss();
                if (isSavePublicFavorite) {
                    Toast.makeText(context, "请不要重复加入常用公众号", Toast.LENGTH_SHORT).show();
                } else {
//                    DialogConfirm.show(context, "提示", "是否加入常用应用？",
//                            new OnDialogConfirmClick() {
//                                @Override
//                                public void onDialogConfirmClick() {
                                    Obj_PublicAccount_Favorite favorite = new Obj_PublicAccount_Favorite();
                                    favorite.ID = publicAccountID;
                                    favorite.Name = publicAccountName;
                                    favorite.isHtml = false;
                                    favorite.fc_mobilepic=fcmobilepic;
                                    //开始进行上传和入库
                                    publicAccount.synUploadAndSavePublicFav.start(favorite, Sps_RWLoginUser.readUserID(context));
//                                }
//                            }, new OnDialogConfirmCancel() {
//                                @Override
//                                public void onDialogConfirmCancel() {
//                                }
//                            });
                }
        }
    };
    private View.OnClickListener addToDeskTopOnClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position=(int)v.getTag();
            Obj_PublicAccount obj_PublicAccount = objList.get(position);
            Intent intent = new Intent(Intent.ACTION_MAIN);
            //调整启动zzw
                        ComponentName cn = new ComponentName(context.getPackageName(),"synway.module_publicaccount.public_chat.PublicAccountChatActNormal");

//            ComponentName cn = new ComponentName("synway.osc.home.HomeAct","synway.module_publicaccount.public_chat.PublicAccountChatAct");
            intent.setComponent(cn);
//            intent.setClassName(context.getPackageName(),"synway.module_publicaccount.public_chat.PublicAccountChatAct");
//            intent.setClass(context, PublicAccountChatAct.class);
            intent.putExtra("ACCOUNT_ID", obj_PublicAccount.ID);
            intent.putExtra("ACCOUNT_NAME", obj_PublicAccount.name);
            String path = getPublicName(obj_PublicAccount.ID);
            addShortcut(activity, obj_PublicAccount.name,BitmapFactory.decodeFile(path), intent, false);
        }
    };
    public void onDestory()
    {
        if(bottomViewTools!=null) {
            bottomViewTools.onDestory();
        }
    }

    private BroadcastReceiver installreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("testy","安装刷新");
            BroastFresh(intent);
        }
    };
    private BroadcastReceiver uninstallreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("testy","卸载刷新");
         BroastFresh(intent);
        }
    };
    private void BroastFresh(Intent intent){
        String packgename=intent.getStringExtra(PACKGENAME);
        for(Obj_PublicAccount obj_publicAccount:objList){
            if(obj_publicAccount.app_information!=null) {
                if (("package:"+obj_publicAccount.app_information.app_packangename).equals(packgename)) {
                    Log.i("testy","真的刷新界面了");
                    refresh();
                }
            }
        }
    }
}