package synway.module_publicaccount.map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.SparseArray;

import com.amap.api.maps.AMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import qyc.synjob.SynJobTask;
import synway.module_interface.config.ThrowExp;
import synway.module_interface.db.SQLite;
import synway.module_interface.db.table_util.Table_LastContact;
import synway.module_publicaccount.Main;
import synway.module_publicaccount.analytical.fac.AnalyticalPath;
import synway.module_publicaccount.analytical.fac.IAnalytical_Base;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgBase;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgTrail;
import synway.module_publicaccount.analytical.obj.Obj_PulibcMsgDynamicLocation;
import synway.module_publicaccount.db.table_util.Table_PublicAccountRecord;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgTrail.Point;
import synway.module_publicaccount.analytical.obj.Obj_PulibcMsgDynamicLocation.enemyTag;
import synway.module_publicaccount.analytical.obj.Obj_PulibcMsgDynamicLocation.friendTag;
import synway.module_publicaccount.db.table_util.Table_PublicAccount_Gis;
import synway.module_publicaccount.map.Bean.UserPointBean;
import synway.module_publicaccount.until.StringUtil;
import synway.module_publicaccount.until.TimeToDate;

/**
 * Created by ysm on 2017/3/9.
 * 查询type=6的实时轨迹
 */

public class SelectAllDynamicLocation extends SynJobTask {
    private SparseArray<IAnalytical_Base> analys = null;
    public int type;//0为查询当前推送人员的point，1为查询全部，即为列表页面的查询
    private Obj_PulibcMsgDynamicLocation obj_pulibcMsgDynamicLocation;

    public SelectAllDynamicLocation(int type, Obj_PulibcMsgDynamicLocation obj_pulibcMsgDynamicLocation) {
        this.type = type;
        this.obj_pulibcMsgDynamicLocation = obj_pulibcMsgDynamicLocation;
    }

    @Override
    public boolean inUIThread() {
        return false;
    }

    @Override
    public void onStart(Object... objects) {
//        ArrayList<Obj_PulibcMsgDynamicLocation.Point> points = getdbpoints();//数据库所有的点
        ArrayList<String> userlist = getuserlist();//存放当前推送里面的人的ID
//        List<UserPointBean> userPointBeens = getuserpointbeans(points);//每个人对应的点的list
        List<UserPointBean> userPointBeens=getNewUserPoint();//每个人对应的点的list
        if (type == 0) {//推送界面的查询，只要查询当前推送人员的轨迹点
            ArrayList<UserPointBean> pushUserPointBeens = new ArrayList<>();//推送成员的点
            for(String userid:userlist){
                if(userPointBeens!=null) {
                    for (UserPointBean userPointBean : userPointBeens) {
                        if (userid.equals(userPointBean.userid)) {
                            pushUserPointBeens.add(userPointBean);
                        }
                    }
                }
            }
            AMap aMap = (AMap) objects[0];
            if (aMap != null) {
                aMap.clear();
            }
            startJob("addMapPoints", pushUserPointBeens);//操作地图，在地图上画轨迹线和点
            Log.i("testy", "得到的最后要显示的人员的数量为" + pushUserPointBeens.size());
        } else if (type == 1) {//1为查询全部，即为列表页面的查询
//            if (userPointBeens.size() > 0) {//如果查出来的点不为空
                startJob("refreshAdapter", userPointBeens, userlist);//刷新列表
//            }
        }
    }


    public void Inanyly() {
        IAnalytical_Base analytical = null;
        for (int i = 0; i < AnalyticalPath.CLASS_PATH.length; i++) {
            try {
                analytical = (IAnalytical_Base) Class.forName(
                        AnalyticalPath.CLASS_PATH[i]).newInstance();
            } catch (Exception e) {
                ThrowExp.throwRxp("SyncGetRecord 公众帐号解析工厂类的包路径错误");
            }

            int msgType = analytical.msgType();
            if (msgType <= 0) {
                ThrowExp.throwRxp("SyncGetRecord 公众帐号解析工厂类没有注册需要接收的msgType");
            }

            if (analys.get(msgType) != null) {
//				ThrowExp.throwRxp("SyncGetRecord 公众帐号解析工厂注册的msgType重合了",
//						"重合的类为：", analys.get(msgType).getClass().getName(),
//						PushClassPath.CLASS_PATH[i]);
            }

            analys.put(msgType, analytical);
            analytical.onInit(context());
        }
    }

    //    //  mapToList方法:
//    public static List<UserPointBean> listmapToList(Map map) {
//        List<UserPointBean> list = new ArrayList<UserPointBean>();
//        Iterator iter = map.entrySet().iterator(); // 获得map的Iterator
//        while (iter.hasNext()) {
//            Map.Entry entry = (Map.Entry) iter.next();
//            UserPointBean userPointBean = new UserPointBean();
//            userPointBean.userid = (String) entry.getKey();
//            userPointBean.points = (ArrayList<Point>) entry.getValue();
//            list.add(userPointBean);
//        }
//        return list;
//    }
    public ArrayList<UserPointBean> getNewUserPoint() {
        ArrayList<UserPointBean> userPointBeens = new ArrayList<UserPointBean>();
        String cols[] = new String[]{
                Table_PublicAccount_Gis.TIME,
                Table_PublicAccount_Gis.GISTYPE,
                Table_PublicAccount_Gis.USER_GROUP,
                Table_PublicAccount_Gis.USER_ID,
                Table_PublicAccount_Gis.USER_NAME,
                Table_PublicAccount_Gis.USER_PIC,
                Table_PublicAccount_Gis.DataType,
                Table_PublicAccount_Gis.X,
                Table_PublicAccount_Gis.Y,
                Table_PublicAccount_Gis.EQUIP_TYPE,
        };
        String queryList[] = SQLite.query(Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase(),
                Table_PublicAccount_Gis._TABLE_NAME, cols, "|", null,
                null, Table_PublicAccount_Gis.TIME + " desc");
        if (null == queryList || queryList.length == 0) {
            return null;
        }
        for (int i = queryList.length - 1; i >= 0; i--) {
            String str[] = queryList[i].split("\\|", -1);
            if (str.length != cols.length) {
                continue;
            }
            UserPointBean userPointBean = new UserPointBean();
                userPointBean.type=  Integer.valueOf(str[1]);
                userPointBean.group=str[2];
                userPointBean.userid=str[3];
                userPointBean.username=str[4];
                userPointBean.picurl=str[5];
                 userPointBean.DataType=Integer.valueOf(str[6]);
                 userPointBean.EquipType=str[9];
                Point point=new Point();
                point.x=str[7];
                point.y=str[8];
                Long time=Long.parseLong(str[0]);
                point.time= TimeToDate.LongToDate(time);
                ArrayList<Point> points=new ArrayList<>();
                points.add(point);
                userPointBean.points=points;

            userPointBeens.add(userPointBean);
        }
        return userPointBeens;
    }

    /****
     * 查询数据库所有的点
     *
     * @return
     */
    public ArrayList<Obj_PulibcMsgDynamicLocation.Point> getdbpoints() {
        ArrayList<Obj_PulibcMsgDynamicLocation.Point> points = new ArrayList<Obj_PulibcMsgDynamicLocation.Point>();
        String warnSql = "select *" + " from " + Table_PublicAccountRecord.getTableName("c6653c8c-9b7c-4fba-b86e-d8bcdff799e3") + " where msgType='6'";
        Cursor cursor = Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase().rawQuery(warnSql, null);

        while (cursor.moveToNext()) {
            Obj_PulibcMsgDynamicLocation obj_pulibcMsgDynamicLocation = new Obj_PulibcMsgDynamicLocation();
            StringBuffer sb = new StringBuffer();
            sb.append(cursor.getString(cursor.getColumnIndex(Table_PublicAccountRecord.publicRecord_col_msg)));
            analys = new SparseArray<IAnalytical_Base>();
            Inanyly();
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(sb.toString());
                JSONObject msgInfo = jsonObject.getJSONObject("MSG_INFO");
                IAnalytical_Base analyTical = analys.get(6);
                Obj_PublicMsgBase result = analyTical.onDeal(msgInfo);
                obj_pulibcMsgDynamicLocation = (Obj_PulibcMsgDynamicLocation) result;
                for (Obj_PulibcMsgDynamicLocation.Point p : obj_pulibcMsgDynamicLocation.points) {
                    points.add(p);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return points;
    }

    public ArrayList<String> getuserlist() {        //收集当前推送的人
        ArrayList<String> userlist = new ArrayList<String>();//存放当前推送里面的人的ID
                for (Obj_PulibcMsgDynamicLocation.Point point : obj_pulibcMsgDynamicLocation.points) {
                    if(obj_pulibcMsgDynamicLocation.DataType==1) {//普通数据
                        if (obj_pulibcMsgDynamicLocation.type == 1) {
                            Obj_PulibcMsgDynamicLocation.enemyTag enemyTag = (Obj_PulibcMsgDynamicLocation.enemyTag) point.tag;
                            userlist.add(enemyTag.publiccase + enemyTag.number);
                        } else if (obj_pulibcMsgDynamicLocation.type == 2) {
                            Obj_PulibcMsgDynamicLocation.friendTag friendTag = (Obj_PulibcMsgDynamicLocation.friendTag) point.tag;
                            userlist.add(friendTag.group + friendTag.policeNum);
                        }
                    }else if(obj_pulibcMsgDynamicLocation.DataType==2){//信标（设备）数据
                        userlist.add(point.tag.equip.locId);
                    }
                }
        return userlist;
    }

    public ArrayList<UserPointBean> getuserpointbeans(ArrayList<Obj_PulibcMsgDynamicLocation.Point> points) {
        ArrayList<UserPointBean> userPointBeens = new ArrayList<UserPointBean>();//每个人对应的点的list
        if (points.size() > 0) {        //所有的点，取出某一个点
            for (Obj_PulibcMsgDynamicLocation.Point p : points) {
                if (p.type == 1) {//如果是新增的点，不是要删除的，还是有有必要判断，虽然在推送的时候已经判断过一遍，0的时候删除这个点
                    //存某个人点的list
                    if (p.tag.type == 1) {//敵方 enemyTag
                        enemyTag enemyTag = (enemyTag) p.tag;
                        if (userPointBeens.size() == 0) {
                            ArrayList<Point> pointtypelist = new ArrayList<Point>();
                            pointtypelist.add(p);
                            UserPointBean userPointBeanSing = new UserPointBean();
                            userPointBeanSing.userid = enemyTag.publiccase + enemyTag.number;
                            userPointBeanSing.username = enemyTag.publicObject;
                            userPointBeanSing.points = pointtypelist;
                            userPointBeanSing.type = 1;
                            userPointBeanSing.group = enemyTag.publiccase;
                            if (p.pType == 2) {//有头像，没有则为空
                                Obj_PulibcMsgDynamicLocation.Picture picture = (Obj_PulibcMsgDynamicLocation.Picture) p.pointInfo;
                                userPointBeanSing.picurl = picture.picURL;
                            }
                            userPointBeens.add(userPointBeanSing);
                        } else {
                            Boolean ifexitenimy = false;//标识列表中是否存在这个人
                            for (UserPointBean userPointBeen : userPointBeens) {     //如果bean中存在這個人的id，則直接添加
                                if (userPointBeen.userid.equals(enemyTag.publiccase + enemyTag.number)) {
                                    userPointBeen.points.add(p);
                                    ifexitenimy = true;
                                }
                            }
                            if (!ifexitenimy) {//如果bean中不存在這個人的id，則創建
                                ArrayList<Point> pointtypelist = new ArrayList<Point>();
                                pointtypelist.add(p);
                                UserPointBean userPointBeanSing = new UserPointBean();
                                userPointBeanSing.userid = enemyTag.publiccase + enemyTag.number;
                                userPointBeanSing.username = enemyTag.publicObject;
                                userPointBeanSing.points = pointtypelist;
                                userPointBeanSing.type = 1;
                                userPointBeanSing.group = enemyTag.publiccase;
                                if (p.pType == 2) {//有头像，没有则为空
                                    Obj_PulibcMsgDynamicLocation.Picture picture = (Obj_PulibcMsgDynamicLocation.Picture) p.pointInfo;
                                    userPointBeanSing.picurl = picture.picURL;
                                }
                                userPointBeens.add(userPointBeanSing);
                            }

                        }
                    } else if (p.tag.type == 2) {//友方 friendTag
                        friendTag friendTag = (friendTag) p.tag;
                        if (StringUtil.isEmpty(friendTag.policeNum)) {//警号为空，用equip中locId判断,是設備類型
                            if (userPointBeens.size() == 0) {
                                ArrayList<Point> pointtypelist = new ArrayList<Point>();
                                pointtypelist.add(p);
                                UserPointBean userPointBeanSing = new UserPointBean();
                                userPointBeanSing.userid = friendTag.equip.locId;
                                userPointBeanSing.username = friendTag.equip.equipName;
                                userPointBeanSing.points = pointtypelist;
                                userPointBeanSing.type = 2;
                                userPointBeanSing.group = friendTag.group;//可能为空
                                if (p.pType == 2) {//有头像，没有则为空
                                    Obj_PulibcMsgDynamicLocation.Picture picture = (Obj_PulibcMsgDynamicLocation.Picture) p.pointInfo;
                                    userPointBeanSing.picurl = picture.picURL;
                                }
                                userPointBeens.add(userPointBeanSing);
                            } else {
                                Boolean ifexitfriend = false;//标识列表中是否存在这个人
                                for (UserPointBean userPointBeen : userPointBeens) {     //如果bean中存在這個人的id，則直接添加
                                    if (userPointBeen.userid.equals(friendTag.equip.locId)) {
                                        userPointBeen.points.add(p);
                                        ifexitfriend = true;
                                    }
                                }
                                if (!ifexitfriend) {//如果bean中不存在這個人的id，則創建
                                    ArrayList<Point> pointtypelist = new ArrayList<Point>();
                                    pointtypelist.add(p);
                                    UserPointBean userPointBeanSing = new UserPointBean();
                                    userPointBeanSing.userid = friendTag.equip.locId;
                                    userPointBeanSing.username = friendTag.equip.equipName;
                                    userPointBeanSing.points = pointtypelist;
                                    userPointBeanSing.type = 2;
                                    userPointBeanSing.group = friendTag.group;//可能为空
                                    if (p.pType == 2) {//有头像，没有则为空
                                        Obj_PulibcMsgDynamicLocation.Picture picture = (Obj_PulibcMsgDynamicLocation.Picture) p.pointInfo;
                                        userPointBeanSing.picurl = picture.picURL;
                                    }
                                    userPointBeens.add(userPointBeanSing);
                                }

                            }
                        } else {//警号不为空，用警号判断，是友方类型
                            if (userPointBeens.size() == 0) {
                                ArrayList<Point> pointtypelist = new ArrayList<Point>();
                                pointtypelist.add(p);
                                UserPointBean userPointBeanSing = new UserPointBean();
                                userPointBeanSing.userid = friendTag.policeNum;
                                userPointBeanSing.username = friendTag.name;
                                userPointBeanSing.points = pointtypelist;
                                userPointBeanSing.type = 3;
                                userPointBeanSing.group = friendTag.group;//可能为空
                                if (p.pType == 2) {//有头像，没有则为空
                                    Obj_PulibcMsgDynamicLocation.Picture picture = (Obj_PulibcMsgDynamicLocation.Picture) p.pointInfo;
                                    userPointBeanSing.picurl = picture.picURL;
                                }
                                userPointBeens.add(userPointBeanSing);
                            } else {
                                Boolean ifexitequip = false;
                                for (UserPointBean userPointBeen : userPointBeens) {     //如果bean中存在這個人的id，則直接添加
                                    if (userPointBeen.userid.equals(friendTag.policeNum)) {
                                        userPointBeen.points.add(p);
                                        ifexitequip = true;
                                    }
                                }
                                if (!ifexitequip) {                 //如果bean中不存在這個人的id，則創建
                                    ArrayList<Point> pointtypelist = new ArrayList<Point>();
                                    pointtypelist.add(p);
                                    UserPointBean userPointBeanSing = new UserPointBean();
                                    userPointBeanSing.userid = friendTag.policeNum;
                                    userPointBeanSing.username = friendTag.name;
                                    userPointBeanSing.points = pointtypelist;
                                    userPointBeanSing.type = 3;
                                    userPointBeanSing.group = friendTag.group;//可能为空
                                    if (p.pType == 2) {//有头像，没有则为空
                                        Obj_PulibcMsgDynamicLocation.Picture picture = (Obj_PulibcMsgDynamicLocation.Picture) p.pointInfo;
                                        userPointBeanSing.picurl = picture.picURL;
                                    }
                                    userPointBeens.add(userPointBeanSing);

                                }
                            }
                        }
                    }
                } else if (p.type == 0) {//刪除該軌跡點

                }
            }
        }
        return userPointBeens;
    }
}
