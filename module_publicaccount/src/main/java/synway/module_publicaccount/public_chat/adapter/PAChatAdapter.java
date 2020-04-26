package synway.module_publicaccount.public_chat.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import synway.module_interface.config.userConfig.Sps_RWLoginUser;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgBase;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgPicTxt;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgTaskNotice;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgTrail;
import synway.module_publicaccount.analytical.obj.Obj_PulibcMsgDynamicLocation;
import synway.module_publicaccount.until.StringUtil;


public class PAChatAdapter extends BaseAdapter{

    //	private LayoutInflater inflater = null;
    private ArrayList<Obj_PublicMsgBase> msgArrayList = null;
    private HashMap<String, Obj_PublicMsgBase> msgbaseMap = null;
    private Context context = null;
    private Activity activity = null;
    private String publicGUID = null;
    private String loginUserID = null;
    private int screenWidth = 0;
    private IOnPublicChatItemClick onPublicChatItemClick;


    public PAChatAdapter(String publicGUID, Activity activity,IOnPublicChatItemClick onPublicChatItemClick) {
//		inflater = LayoutInflater.from(context);
        this.context = activity;
        this.publicGUID = publicGUID;
        this.onPublicChatItemClick = onPublicChatItemClick;
        this.msgArrayList = new ArrayList<Obj_PublicMsgBase>();
        this.msgbaseMap = new HashMap<String, Obj_PublicMsgBase>();
        this.loginUserID = Sps_RWLoginUser.readUserID(context);
        this.screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        this.activity = activity;
    }

    //
    // public void addItem(ArrayList<Obj_PublicMsgBase> arrayList) {
    //     this.msgArrayList.addAll(0, arrayList);
    // }


//     public void addItem(Obj_PublicMsgBase obj) {
// //        if (isShowType(obj)) {
//         if (obj.MsgType == 6) {//实时轨迹
//             Obj_PulibcMsgDynamicLocation pushobj_pulibcMsgDynamicLocation = (Obj_PulibcMsgDynamicLocation) obj;
//             if (pushobj_pulibcMsgDynamicLocation.points.get(0).type == 1) {//新增的点
//                 for (int i = 0; i < msgArrayList.size(); i++) {
//                     if (msgArrayList.get(i).MsgType == 6) {
//                         msgArrayList.remove(i);
//                     }
//                 }
//             } else {//删除用户的实时轨迹
//                 for (int i = 0; i < msgArrayList.size(); i++) {
//                     if (msgArrayList.get(i).MsgType == 6) {//刪除當前頁面推送的人
//                         Obj_PulibcMsgDynamicLocation showobj = (Obj_PulibcMsgDynamicLocation) msgArrayList.get(i);//展示在当前的实时轨迹
//                         ArrayList<Obj_PulibcMsgDynamicLocation.Point> points = new ArrayList<Obj_PulibcMsgDynamicLocation.Point>();
//                         for (int j = 0; j < showobj.points.size(); j++) {//循环展示的人
//                             String userid = getuserid(showobj.points.get(j));
//                             Boolean ifdelete = false;//是否要删除当前的点
//                             for (Obj_PulibcMsgDynamicLocation.Point point : pushobj_pulibcMsgDynamicLocation.points) {
//                                 String pushuderid = getuserid(point);
//                                 if (userid.equals(pushuderid)) {
//                                     ifdelete = true;
//                                 }
//                             }
//                             if (!ifdelete) {
//                                 points.add(showobj.points.get(j));
//                             }
//                         }
//                         if (points.size() == 0) {
//                             msgArrayList.remove(i);
//                         } else {
//                             showobj.points = points;
//                         }
//                     }
//                 }
//                 obj = null;
//             }
//         }
//         if (obj != null) {
//             this.msgArrayList.add(obj);
//         }
// //        }
//     }

    //时间格式
    // public void addHistory(ArrayList<Obj_PublicMsgBase> crList) {
    //     this.msgArrayList.addAll(0, crList);
    //     for (int i = 0; i < crList.size(); i++) {
    //         msgbaseMap.put(crList.get(i).msgID, crList.get(i));
    //     }
    // }

    public void reset(ArrayList<Obj_PublicMsgBase> list){
        this.msgArrayList.clear();
        this.msgbaseMap.clear();
        for (Obj_PublicMsgBase obj_publicMsgBase : list) {
            addNewItem(obj_publicMsgBase);
        }
    }

    public void addNewItemFromTop(Obj_PublicMsgBase obj){
        if (obj.MsgType == 6) {//实时轨迹
            Obj_PulibcMsgDynamicLocation pushobj_pulibcMsgDynamicLocation = (Obj_PulibcMsgDynamicLocation) obj;
            if (pushobj_pulibcMsgDynamicLocation.points.get(0).type == 1) {//新增的点
                for (int i = 0; i < msgArrayList.size(); i++) {
                    if (msgArrayList.get(i).MsgType == 6) {
                        msgArrayList.remove(i);
                    }
                }
            } else {//删除用户的实时轨迹
                for (int i = 0; i < msgArrayList.size(); i++) {
                    if (msgArrayList.get(i).MsgType == 6) {//刪除當前頁面推送的人
                        Obj_PulibcMsgDynamicLocation showobj = (Obj_PulibcMsgDynamicLocation) msgArrayList.get(i);//展示在当前的实时轨迹
                        ArrayList<Obj_PulibcMsgDynamicLocation.Point> points = new ArrayList<Obj_PulibcMsgDynamicLocation.Point>();
                        for (int j = 0; j < showobj.points.size(); j++) {//循环展示的人
                            String userid = getuserid(showobj.points.get(j));
                            Boolean ifdelete = false;//是否要删除当前的点
                            for (Obj_PulibcMsgDynamicLocation.Point point : pushobj_pulibcMsgDynamicLocation.points) {
                                String pushuderid = getuserid(point);
                                if (userid.equals(pushuderid)) {
                                    ifdelete = true;
                                }
                            }
                            if (!ifdelete) {
                                points.add(showobj.points.get(j));
                            }
                        }
                        if (points.size() == 0) {
                            msgArrayList.remove(i);
                        } else {
                            showobj.points = points;
                        }
                    }
                }
                obj = null;
            }
        }

        if (obj != null) {
            this.msgArrayList.add(0, obj);
            this.msgbaseMap.put(obj.msgID, obj);
        }

    }


    public void refresh() {
        this.notifyDataSetChanged();
    }


    public void addNewItem(Obj_PublicMsgBase base) {
        this.msgArrayList.add(base);
        this.msgbaseMap.put(base.msgID, base);
    }

    @Override
    public int getCount() {
        return msgArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return msgArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override public int getItemViewType(int position) {
        if (msgArrayList.get(position) instanceof Obj_PublicMsgPicTxt
            || msgArrayList.get(position) instanceof Obj_PulibcMsgDynamicLocation
            || msgArrayList.get(position) instanceof Obj_PublicMsgTrail) {
            return 0;
        } else if (msgArrayList.get(position) instanceof Obj_PublicMsgTaskNotice) {
            return 1;
        } else {
            return 2;
        }

    }


    @Override public int getViewTypeCount() {
        return 3;
    }


    /**
     * 本消息是否是需要在聊天界面展示的类型
     */
    private boolean isShowType(Obj_PublicMsgBase obj) {
        return obj instanceof Obj_PublicMsgPicTxt;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Obj_PublicMsgBase obj = msgArrayList.get(position);

//        msgArrayList.get(msgArrayList.size()-1).MsgType=7;
        Boolean ifbright = false;//是否需要高亮顯示，默认不显示高亮
        // 重用思路:
        // 公众消息内容是按“行”来划分的
        // 公众消息最外层是一个LinearLayout，然后根据每一行的具体类型加载具体的View
        // 由于行的数量是不确定的，因此无法在LinearLayout层对convertView进行重用
        // 现在我们希望能够尽量少地重新加载view，那么现在只能尽可能重用那些已经加载出来的"行"
        // 如果"行"的数量不足，那么新建;如果"行"的数量有余，那么隐藏。
        // 这个过程最重要的一步，就是要统一所有行的layout


        // if (convertView == null) {
        //     viewHolder = new RecycleViewHolder(context, publicGUID, activity);
        //     convertView = viewHolder.getConvertView();
        //     convertView.setTag(viewHolder);
        //     viewHolder.setUrlTextItemListener(this);
        // } else {
        //     viewHolder = (RecycleViewHolder) convertView.getTag();
        // }
        //
        // if (position == (msgArrayList.size() - 1)) {//如果是公众号消息中最后一条
        //     if (ifpush) {//是当前的推送 需要高亮显示
        //         ifbright = true;
        //     }
        // }
        // if (obj.MsgType == 4) {
        //     viewHolder.render((Obj_PublicMsgPicTxt) obj, ifbright);
        // } else if (obj.MsgType == 5) {     //轨迹消息
        //     viewHolder.renderTypeTrail((Obj_PublicMsgTrail) obj, ifbright);
        // } else if (obj.MsgType == 6) {   //GIS动态消息
        //     Obj_PulibcMsgDynamicLocation obj_pulibcMsgDynamicLocation = (Obj_PulibcMsgDynamicLocation) obj;
        //     if (((Obj_PulibcMsgDynamicLocation) obj).points.size() > 0) {
        //         if (((Obj_PulibcMsgDynamicLocation) obj).points.get(0).type == 1) {
        //             viewHolder.renderTypeGis((Obj_PulibcMsgDynamicLocation) obj, ifbright);
        //         } else {
        //
        //         }
        //     }
        // }

        AdapterTypeRender typeRender;
        if (null == convertView) {
            typeRender = getAdapterTypeRender(position);

            convertView = typeRender.getConvertView();
            typeRender.initView();

            convertView.setTag(typeRender);
        } else {
            typeRender = (AdapterTypeRender) convertView.getTag();
        }

        typeRender.setData(position,obj);

        return convertView;
    }

    public String getuserid(Obj_PulibcMsgDynamicLocation.Point p) {
        String userid = "";
        if (p.tag.type == 1) {//敵方 enemyTag
            Obj_PulibcMsgDynamicLocation.enemyTag enemyTag = (Obj_PulibcMsgDynamicLocation.enemyTag) p.tag;
            userid = enemyTag.publiccase + enemyTag.number;
        } else if (p.tag.type == 2) {//友方 friendTag
            Obj_PulibcMsgDynamicLocation.friendTag friendTag = (Obj_PulibcMsgDynamicLocation.friendTag) p.tag;
            if (StringUtil.isEmpty(friendTag.policeNum)) {//警号为空，用equip中locId判断,是設備類型
                userid = friendTag.equip.locId;
            } else {//警号不为空，用警号判断，是友方类型
                userid = friendTag.policeNum;
            }
        }
        return userid;
    }

    private AdapterTypeRender getAdapterTypeRender(int position){
        AdapterTypeRender adapterTypeRender = null;
        Obj_PublicMsgBase base = msgArrayList.get(position);
        if (base instanceof Obj_PublicMsgPicTxt ||
            base instanceof Obj_PublicMsgTrail ||
            base instanceof Obj_PulibcMsgDynamicLocation) {
            adapterTypeRender = new ViewHolder_PicText(publicGUID, context, onPublicChatItemClick,activity);
        }else if(base instanceof Obj_PublicMsgTaskNotice){
            adapterTypeRender = new ViewHolder_TaskNotice(context, onPublicChatItemClick);
        }

        return adapterTypeRender;
    }

    public void remove(String msgGUID){
        Obj_PublicMsgBase removeBase = this.msgbaseMap.remove(msgGUID);
        if (removeBase != null) {
            this.msgArrayList.remove(removeBase);
        }
    }
}
