package synway.module_publicaccount.analytical.fac;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

import synway.module_publicaccount.analytical.obj.Obj_PublicMsgBase;
import synway.module_publicaccount.analytical.obj.Obj_PulibcMsgDynamicLocation;

/**
 * Created by QSJH on 2016/5/6 0006.
 * gis动态消息
 */
public class Analytical_DynamicLocation implements IAnalytical_Base {

    private Context context;

    @Override
    public int msgType() {
        return 6;
    }

    @Override
    public void onInit(Context context) {
        this.context = context;
    }

    @Override
    public Obj_PublicMsgBase onDeal(JSONObject jsonObject) {
        Obj_PulibcMsgDynamicLocation obj = new Obj_PulibcMsgDynamicLocation();
        JSONArray points;
        try {
            obj.screenType=jsonObject.getInt("ScreenType");
            if(obj.screenType!=1){//手机端只接收地图大屛类型的，指挥大屛的不接受
                return null;
            }
            obj.DataType=jsonObject.getInt("DataType");//数据类型：普通和设备
            obj.type = jsonObject.getInt("Type");//GIS类型  友方敌方
            obj.num = jsonObject.getInt("Num");
//            obj.showNum=jsonObject.getInt("showNum");
            obj.dataGuid=jsonObject.getString("DataGuid");
            obj.dataCount=jsonObject.getInt("DataCount");
            points = jsonObject.getJSONArray("Points");
            ArrayList<Obj_PulibcMsgDynamicLocation.Point> pointList = new ArrayList<>();
            obj.points = pointList;
            for (int i = 0; i < points.length(); i++) {
                JSONObject point;
                point = points.getJSONObject(i);
                Obj_PulibcMsgDynamicLocation.Point p = new Obj_PulibcMsgDynamicLocation.Point();
                p.uuid = UUID.randomUUID().toString();
                p.id = point.getString("ID");
                p.name = point.getString("Name");
                p.group = point.getString("Group");
                p.type = point.getInt("Type");
                p.x = point.getString("X");
                p.y = point.getString("Y");
                p.textColor = point.getString("TextColor");
                p.tagType = point.getInt("TagType");
                JSONObject tagobj=point.getJSONObject("Tag");
                Obj_PulibcMsgDynamicLocation.equip equip=new Obj_PulibcMsgDynamicLocation.equip();
                if(obj.type==1){
                    Obj_PulibcMsgDynamicLocation.enemyTag  enemyTag=new Obj_PulibcMsgDynamicLocation.enemyTag();
                    enemyTag.number=tagobj.getString("号码");
                    enemyTag.publiccase=tagobj.getString("案件");
                    enemyTag.time=tagobj.getString("时间");
                    enemyTag.type=tagobj.getInt("TYPE");
                    enemyTag.publicObject=tagobj.getString("对象");
                    if( obj.DataType==2) {
                        enemyTag.equip = getequip(tagobj.getJSONObject("equip"));
                    }
                    p.tag=enemyTag;
                }else  if(obj.type==2){
                    Obj_PulibcMsgDynamicLocation.friendTag friendTag=new Obj_PulibcMsgDynamicLocation.friendTag();
                    friendTag.name=tagobj.getString("姓名");
                    friendTag.time=tagobj.getString("时间");
                    friendTag.group=tagobj.getString("群组");
                    friendTag.type=tagobj.getInt("TYPE");
                    friendTag.policeNum=tagobj.getString("警号");
                    if( obj.DataType==2) {
                        friendTag.equip = getequip(tagobj.getJSONObject("equip"));
                    }
                    p.tag=friendTag;
                }
                p.time = point.getString("Time");
                p.pType = point.getInt("PType");
                p.tip = point.optString("Tip");
                JSONObject pInfo = point.getJSONObject("PointInfo");
                if (p.pType == 1) {
                    Obj_PulibcMsgDynamicLocation.Dot dot = new Obj_PulibcMsgDynamicLocation.Dot();
                    dot.wink = pInfo.getInt("WINK");
                    dot.color = pInfo.getString("COLOR");
                    dot.size = pInfo.getInt("SIZE");
                    p.pointInfo = dot;
                } else if (p.pType == 2) {
                    Obj_PulibcMsgDynamicLocation.Picture picture = new Obj_PulibcMsgDynamicLocation.Picture();
                    picture.picSize = pInfo.getInt("PICSIZE");
                    picture.picURL = pInfo.getString("PICURL");
                    p.pointInfo = picture;
                }
                JSONArray floatArray = point.getJSONArray("Floats");
                for (int j = 0; j < floatArray.length(); j++) {
                    JSONObject floatJSON = floatArray.getJSONObject(j);
                    Obj_PulibcMsgDynamicLocation.Float f = new Obj_PulibcMsgDynamicLocation.Float();
                    f.fType = floatJSON.getInt("FType");
                    f.fPicURL = floatJSON.getString("FPicURL");
                    f.fURL = floatJSON.getString("FURL");
                    p.floats.add(f);
                }
                pointList.add(p);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return obj;
    }
    public Obj_PulibcMsgDynamicLocation.equip getequip(JSONObject equipjson){
        Obj_PulibcMsgDynamicLocation.equip equip=new Obj_PulibcMsgDynamicLocation.equip();
        equip.typeName = equipjson.optString("typeName");
        equip.equipId = equipjson.optString("equipId");
        equip.UserGroupType = equipjson.optString("UserGroupType");
        equip.equipName = equipjson.optString("equipName");
        equip.memo = equipjson.optString("memo");
        equip.locType = equipjson.optString("locType");
        equip.locId = equipjson.optString("locId");
        equip.equipModel = equipjson.optString("equipModel");
        equip.userType = equipjson.optString("userType");
        equip.userMemo = equipjson.optString("userMemo");
        return equip;

    }
}
