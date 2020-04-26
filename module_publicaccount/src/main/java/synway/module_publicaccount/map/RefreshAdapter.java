package synway.module_publicaccount.map;

import java.security.acl.Group;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import qyc.synjob.SynJobTask;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgTrail;
import synway.module_publicaccount.analytical.obj.Obj_PulibcMsgDynamicLocation;
import synway.module_publicaccount.map.Adapter.DynamiclocationAdapter;
import synway.module_publicaccount.map.Adapter.UserlistExpandableAdapter;
import synway.module_publicaccount.map.Bean.GroupUserPoint;
import synway.module_publicaccount.map.Bean.UserPointBean;
import synway.module_publicaccount.until.StringUtil;

/**
 * Created by ysm on 2017/3/21.
 */

public class RefreshAdapter extends SynJobTask {
//    private DynamiclocationAdapter friendViewAdapter;
//    private DynamiclocationAdapter animyViewAdapter;
//    private DynamiclocationAdapter equipViewAdapter;
    private UserlistExpandableAdapter friendViewAdapter;
    private UserlistExpandableAdapter animyViewAdapter;
    private UserlistExpandableAdapter equipViewAdapter;
//    public RefreshAdapter(DynamiclocationAdapter friendViewAdapter,DynamiclocationAdapter animyViewAdapter, DynamiclocationAdapter equipViewAdapter){
//        this.friendViewAdapter=friendViewAdapter;
//        this.animyViewAdapter=animyViewAdapter;
//        this.equipViewAdapter=equipViewAdapter;
//    }
public RefreshAdapter(UserlistExpandableAdapter friendViewAdapter,UserlistExpandableAdapter animyViewAdapter, UserlistExpandableAdapter equipViewAdapter){
    this.friendViewAdapter=friendViewAdapter;
    this.animyViewAdapter=animyViewAdapter;
    this.equipViewAdapter=equipViewAdapter;
}
    @Override
    public boolean inUIThread() {
        return true;
    }

    @Override
    public void onStart(Object... objects) {
        List<UserPointBean> userPointBeens=(List<UserPointBean>)objects[0];
        ArrayList<String> userlist=(ArrayList<String>)objects[1];
        ArrayList<UserPointBean> friendPointBeens=new ArrayList<UserPointBean>();//友方的点
        ArrayList<UserPointBean> animyPointBeens=new ArrayList<UserPointBean>();//敌方的点
        ArrayList<UserPointBean> equipPointBeens=new ArrayList<UserPointBean>();//设备的点
        for(UserPointBean userPointBean:userPointBeens){
            Boolean ifexit=false;//是否存在这个人的ID
            for(String userid:userlist){
                if(userPointBean.userid.equals(userid)){
                    ifexit=true;
                }
            }
            userPointBean.isCheck = ifexit;
            if(userPointBean.DataType==1&&userPointBean.type==1){//敌方
                animyPointBeens.add(userPointBean);
            }else if(userPointBean.DataType==1&&userPointBean.type==2){//友方
                friendPointBeens.add(userPointBean);
            }else if(userPointBean.DataType==2){//设备
                equipPointBeens.add(userPointBean);
            }
        }
//        double y = 30.269861;
//        double x = 120.196538;
//        for(int i=0;i<18;i++){
//            x=x+0.1;
//            UserPointBean userPointBean=new UserPointBean();
//            userPointBean.userid="ysm"+i;
//            userPointBean.userid="ysm"+i+i+i;
//            userPointBean.isCheck=false;
//            userPointBean.group="測試分組";
//            userPointBean.username="测试"+i;
//            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");;
//            ArrayList<Obj_PublicMsgTrail.Point> points = new ArrayList<Obj_PublicMsgTrail.Point>();
//            for(int j=0;j<1;j++){
//                x=x+0.1;
//                Obj_PublicMsgTrail.Point point=new Obj_PublicMsgTrail.Point();
//                point.x=String.valueOf(x);
//                point.y=String.valueOf(y);
//                point.time=df.format(new Date());
//                points.add(point);
//            };
//            userPointBean.points=points;
//            friendPointBeens.add(userPointBean);
//        }
//        for(int j=0;j<25;j++){
//            y=y+0.1;
//            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");;
//            UserPointBean userPointBean=new UserPointBean();
//            userPointBean.userid="ysmm"+j;
//            userPointBean.userid="ysmm"+j+j+j;
//            userPointBean.isCheck=false;
//            userPointBean.group="222";
//            userPointBean.username="测试"+j;
//            ArrayList<Obj_PublicMsgTrail.Point> points = new ArrayList<Obj_PublicMsgTrail.Point>();
//            for(int k=0;k<1;k++){
//                x=x+0.1;
//                Obj_PublicMsgTrail.Point point=new Obj_PublicMsgTrail.Point();
//                point.x=String.valueOf(x);
//                point.y=String.valueOf(y);
//                point.time=df.format(new Date());
//                points.add(point);
//            };
//            userPointBean.points=points;
//            animyPointBeens.add(userPointBean);
//        }
//        animyViewAdapter.resetMlist(animyPointBeens);
//        animyViewAdapter.refresh();
//        friendViewAdapter.resetMlist(friendPointBeens);
//        friendViewAdapter.refresh();
//        equipViewAdapter.resetMlist(equipPointBeens);
//        equipViewAdapter.refresh();

//        animyViewAdapter.resetMlist(animyPointBeens);
//        animyViewAdapter.refresh();
//        friendViewAdapter.resetMlist(friendPointBeens);
//        friendViewAdapter.refresh();
//        equipViewAdapter.resetMlist(equipPointBeens);
//        equipViewAdapter.refresh();
        animyViewAdapter.resetMlist(getGroupUserList(animyPointBeens));
        animyViewAdapter.refresh();
        friendViewAdapter.resetMlist(getGroupUserList(friendPointBeens));
        friendViewAdapter.refresh();
        equipViewAdapter.resetMlist(getGroupUserList(equipPointBeens));
        equipViewAdapter.refresh();
    }

    public ArrayList<GroupUserPoint> getGroupUserList( ArrayList<UserPointBean> userPointBeens){
        ArrayList<GroupUserPoint> groupUserPoints=new ArrayList<GroupUserPoint>();
        for(UserPointBean userPointBean:userPointBeens){
            if(StringUtil.isNotEmpty(userPointBean.group)){//分组信息不为空
             if(groupUserPoints.size()==0){//数列里面还没有分组数据
                 GroupUserPoint groupUserPoint=new GroupUserPoint();
                 groupUserPoint.groupName=userPointBean.group;
                 groupUserPoint.list_child.add(userPointBean);
                 groupUserPoints.add(groupUserPoint);
             }else{//数列不是空的，循环数列把当前的用户加到该组里
//                 groupUserPoints=addGroupUser(groupUserPoints,userPointBean,userPointBean.group);
                 Boolean ifexit = false;//标识是否存在这个组
                 for(GroupUserPoint groupUserPoint:groupUserPoints){
                     if(StringUtil.isNotEmpty(groupUserPoint.groupName)&&groupUserPoint.groupName.equals(userPointBean.group)){
                         groupUserPoint.list_child.add(userPointBean);
                         ifexit=true;
                     }
                 }
                 if(!ifexit){//如果组里不存在这个组，則創建
                     List<UserPointBean> list_child = new ArrayList<UserPointBean>();
                     list_child.add(userPointBean);
                     GroupUserPoint groupUserPoint=new GroupUserPoint();
                     groupUserPoint.groupName=userPointBean.group;
                     groupUserPoint.list_child=list_child;
                     groupUserPoints.add(groupUserPoint);
                 }
             }
            }else{//分组信息为空
//                groupUserPoints=addGroupUser(groupUserPoints,userPointBean,"无分组");
                Boolean ifexit = false;//标识是否存在这个组
                for(GroupUserPoint groupUserPoint1:groupUserPoints){
                    if(groupUserPoint1.groupName.equals("无分组")){
                        groupUserPoint1.list_child.add(userPointBean);
                        ifexit=true;
                    }
                }
                if(!ifexit){  //如果组里不存在这个组，則創建)
                    List<UserPointBean> list_child = new ArrayList<UserPointBean>();
                    list_child.add(userPointBean);
                    GroupUserPoint groupUserPoint=new GroupUserPoint();
                    groupUserPoint.list_child=list_child;
                    groupUserPoint.groupName="无分组";
                    groupUserPoints.add(groupUserPoint);
                }

            }
        }
        for(GroupUserPoint groupUserPoint:groupUserPoints){
            Boolean ifcheck=true;
            for(UserPointBean userPointBean:groupUserPoint.list_child){
                if(!userPointBean.isCheck){//如果有一个没有选中，就整组不选中
                    ifcheck=false;
                }
            }
            groupUserPoint.ifcheck=ifcheck;
        }
        return  groupUserPoints;
    }

}
