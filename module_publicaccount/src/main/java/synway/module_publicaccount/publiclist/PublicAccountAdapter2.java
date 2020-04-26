package synway.module_publicaccount.publiclist;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
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
import java.util.ArrayList;
import java.util.List;
import synway.module_interface.config.BaseUtil;
import synway.module_interface.config.userConfig.Sps_RWLoginUser;
import synway.module_publicaccount.R;
import synway.module_publicaccount.public_chat.PublicAccountChatActNormal;
import synway.module_publicaccount.public_favorite.obj.Obj_PublicAccount_Favorite;
import synway.module_publicaccount.until.DialogTool;

import static synway.module_publicaccount.publiclist.until.ShortCUtUntil.addShortcut;
import static synway.module_publicaccount.until.BroastCastUtil.INSTALLBROAST;
import static synway.module_publicaccount.until.BroastCastUtil.PACKGENAME;
import static synway.module_publicaccount.until.BroastCastUtil.UNINSTALLBROAST;

public class PublicAccountAdapter2 extends BaseAdapter {

    private ArrayList<Obj_PublicAccount> objList = null;
    private ArrayList<Obj_PublicAccount> realList=null;
    private LayoutInflater inflater = null;
    // ================bottom View
    private PublicAccountBottomViewTools2 bottomViewTools = null;
    private Context context = null;
    private Activity activity = null;
    private Obj_PublicAccount obj = null;
    private PublicAccountAct2 publicAccount2 = null;
    private SyncGetHeadThu syncGetHeadThu = null;
    public PublicAccountAdapter2(Context context, Activity activity, PublicAccountAct2 publicAccount, SyncGetHeadThu syncGetHeadThu) {
        objList = new ArrayList<>();
        realList=new ArrayList<>();
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.activity = activity;
        this.publicAccount2 = publicAccount;
        this.syncGetHeadThu=syncGetHeadThu;
        IntentFilter intentFilter = new IntentFilter(INSTALLBROAST);
        context.registerReceiver(installreceiver, intentFilter);
        IntentFilter unintentFilter = new IntentFilter(UNINSTALLBROAST);
        context.registerReceiver(uninstallreceiver, unintentFilter);
    }

    public void reset(ArrayList<Obj_PublicAccount> list) {
        realList=list;
        //同一个拼音首字母的公众号放在一起
        ArrayList<Obj_PublicAccount> newObjs=new ArrayList<>();
        for(int j=0;j<list.size();j++){
            if(!list.get(j).isItem){//不是公众号，是拼音
              for(int i=j+1;i<list.size();i++){
                     if(!list.get(i).isItem){
                         break;
                     }
                  list.get(j).publicobjs.add(list.get(i));
              }
              newObjs.add(list.get(j));
            }
        }
        this.objList.clear();
        this.objList = newObjs;
//        this.hashMap.clear();
//        for (Obj_PublicAccount objP : list) {
//            hashMap.put(objP.ID, objP);
//
//        }

    }

    /**
     * 取List里面的其中一段，根据ListView提供的topIndex和bottomIndex
     */
    public List<Obj_PublicAccount> sub(int topIndex, int bottomIndex) {
        // 根据注释，list的sub函数，end是不包括的。
        // 而我们是start和end都包括的，因此这里去取end要+1
        return objList.subList(topIndex, bottomIndex + 1);
    }

//    public Obj_PublicAccount getObjByID(String id) {
//        return hashMap.get(id);
//    }

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
                // addNormalApplication.setVisibility(View.VISIBLE);
                addNormalApplication.setVisibility(View.GONE);
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
        if(!obj.isItem){
            holder.relLayout.setBackgroundResource(R.color.mgray_light);
            holder.relLayout.setEnabled(false);
            holder.imgvHead.setVisibility(View.GONE);
            holder.tvText.setText(obj.name);
            holder.addbottom.setVisibility(View.VISIBLE);
            bottomViewTools = new PublicAccountBottomViewTools2(publicAccount2,activity, holder,obj,syncGetHeadThu);
            bottomViewTools.init();
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
                                    publicAccount2.synUploadAndSavePublicFav.start(favorite, Sps_RWLoginUser.readUserID(context));
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
            intent.putExtra("ACCOUNT_ID", obj_PublicAccount.ID);
            intent.putExtra("ACCOUNT_NAME", obj_PublicAccount.name);
            String path = getPublicName(obj_PublicAccount.ID);
            addShortcut(activity, obj_PublicAccount.name,BitmapFactory.decodeFile(path), intent, false);
        }
    };
    public void onDestory(){
        if(bottomViewTools!=null) {
            bottomViewTools.onDestory();
        }
        if(installreceiver!=null) {
            context.unregisterReceiver(installreceiver);
        }
        if(uninstallreceiver!=null) {
            context.unregisterReceiver(uninstallreceiver);
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
        for(Obj_PublicAccount obj_publicAccount:realList){
            if(obj_publicAccount.app_information!=null) {
                if (("package:"+obj_publicAccount.app_information.app_packangename).equals(packgename)) {
                    Log.i("testy","真的刷新界面了");
                    refresh();
                }
            }
        }
    }
}