package synway.module_publicaccount.analytical.fac;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

import synway.module_publicaccount.analytical.obj.Obj_PublicMsgBase;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgTrail;
import synway.module_publicaccount.analytical.obj.Obj_PulibcMsgDynamicLocation;
import synway.module_publicaccount.analytical.obj.Obj_PulibcMsgDynamicLocation.tag;
import synway.module_publicaccount.analytical.obj.Obj_PulibcMsgDynamicLocation.friendTag;
import synway.module_publicaccount.analytical.obj.Obj_PulibcMsgDynamicLocation.enemyTag;
import synway.module_publicaccount.analytical.obj.Obj_PulibcMsgDynamicLocation.equip;
import synway.module_publicaccount.analytical.obj.Obj_PulibcMsgDynamicLocation.Float;
import synway.module_publicaccount.analytical.obj.Obj_PulibcMsgDynamicLocation.Dot;
import synway.module_publicaccount.analytical.obj.Obj_PulibcMsgDynamicLocation.Picture;
import synway.module_publicaccount.until.StringUtil;

/**
 * Created by ysm on 2017/2/24.
 * 轨迹消息
 */

public class Analytical_Trail implements IAnalytical_Base {

    private Context context;

    @Override
    public int msgType() {
        return 5;
    }

    @Override
    public void onInit(Context context) {
        this.context = context;
    }

    @Override
    public Obj_PublicMsgBase onDeal(JSONObject jsonObject) {

        Obj_PublicMsgTrail obj = new Obj_PublicMsgTrail();
        JSONArray points;
        try {
            obj.Auto = jsonObject.getInt("Auto");
//            obj.Group = jsonObject.getString("Group");
            obj.ID = jsonObject.getString("ID");
            obj.Name = jsonObject.getString("Name");
            obj.Num = jsonObject.getInt("Num");
            obj.TextColor = jsonObject.getString("TextColor");
            obj.Type = jsonObject.getInt("Type");
            obj.Visble = jsonObject.getInt("Visible");
            points = jsonObject.getJSONArray("Points");
            ArrayList<Obj_PublicMsgTrail.Point> pointList = new ArrayList<>();
            if(points.length()==0||points==null||points.equals("")){
                return  null;
            }
            for (int i = 0; i < points.length(); i++) {
                JSONObject point;
                point = points.getJSONObject(i);
                Obj_PublicMsgTrail.Point p = new Obj_PublicMsgTrail.Point();
                p.x = point.getString("X");
                p.y = point.getString("Y");
                p.tagType = point.getInt("TagType");
                JSONObject tagobj=point.getJSONObject("Tag");
                if(obj.Type==1){
                    enemyTag enemyTag=new enemyTag();
                    enemyTag.number=tagobj.getString("号码");
                    enemyTag.publiccase=tagobj.getString("案件");
                    enemyTag.time=tagobj.getString("时间");
                    enemyTag.type=tagobj.getInt("TYPE");
                    enemyTag.publicObject=tagobj.getString("对象");
                    enemyTag.equip=getequip(tagobj.optJSONObject("equip"));
                    p.tag=enemyTag;
                }else  if(obj.Type==2){
                    friendTag friendTag=new friendTag();
                    friendTag.name=tagobj.getString("姓名");
                    friendTag.time=tagobj.getString("时间");
                    friendTag.group=tagobj.getString("群组");
                    friendTag.type=tagobj.getInt("TYPE");
                    friendTag.policeNum=tagobj.getString("警号");
                    friendTag.equip=getequip(tagobj.optJSONObject("equip"));
                    p.tag=friendTag;
                }
//                p.tag = point.getString("Tag");
                p.time = point.getString("Time");
                p.pType = point.getInt("PType");
                JSONObject pInfo = point.getJSONObject("PointInfo");
                if (p.pType == 1) {
                    Dot dot = new Dot();
                    dot.wink = pInfo.getInt("WINK");
                    dot.color = pInfo.getString("COLOR");
                    dot.size = pInfo.getInt("SIZE");
                    p.pointInfo = dot;
                } else if (p.pType == 2) {
                    Picture picture = new Picture();
                    picture.picSize = pInfo.getInt("PICSIZE");
                    picture.picURL = pInfo.getString("PICURL");
                    p.pointInfo = picture;
                }
                JSONArray floatArray = point.getJSONArray("Floats");
                for (int j = 0; j < floatArray.length(); j++) {
                    JSONObject floatJSON = floatArray.getJSONObject(j);
                    Float f = new Float();
                    f.fType = floatJSON.getInt("FType");
                    f.fPicURL = floatJSON.getString("FPicURL");
                    f.fURL = floatJSON.getString("FURL");
                    p.floats.add(f);
                }
                pointList.add(p);
            }
            obj.Points = pointList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return obj;
    }
    public equip getequip(JSONObject equipjson){
        equip equip=new equip();
            if(equipjson!=null&& (!equipjson.equals(""))) {
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
            }


        return equip;

    }
}
