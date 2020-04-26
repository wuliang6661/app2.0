package synway.module_publicaccount.public_chat;

import android.database.Cursor;
import android.util.SparseArray;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import synway.module_interface.config.ThrowExp;
import synway.module_publicaccount.analytical.fac.AnalyticalPath;
import synway.module_publicaccount.analytical.fac.IAnalytical_Base;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgBase;
import synway.module_publicaccount.db.table_util.Table_PublicAccountRecord;

/**
 * Created by leo on 2019/1/19.
 */

public class PublicMsgRecord {
    private static SparseArray<IAnalytical_Base> analys = new SparseArray<>();
    static {
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
            analys.put(msgType, analytical);
        }
    }

    public static Obj_PublicMsgBase read(Cursor cursor){
        return cursor2Obj(cursor);
    }

    private static Obj_PublicMsgBase cursor2Obj(Cursor cursor) {
        String msgGuid = cursor.getString(cursor.getColumnIndex(Table_PublicAccountRecord.publicRecord_col_GUID));
        long localTime = Long.valueOf(cursor.getString(cursor.getColumnIndex(Table_PublicAccountRecord.publicRecord_col_localeTime)));
        String msg = cursor.getString(cursor.getColumnIndex(Table_PublicAccountRecord.publicRecord_col_msg));
        int msgType = cursor.getInt(cursor.getColumnIndex(Table_PublicAccountRecord.publicRecord_col_msgType));
        String urlData = cursor.getString(cursor.getColumnIndex(Table_PublicAccountRecord.publicRecord_col_data));
        JSONObject jsonObject;
        JSONObject msgInfo;
        try {
            jsonObject = new JSONObject(msg);
            msgInfo = jsonObject.getJSONObject("MSG_INFO");
            if (urlData != null && urlData.length() > 0) {
                JSONArray jsonMsg = new JSONArray(urlData);
                //将URL域以URLMSG字段加入MSG_INFO字段中，这样不用更改onDeal接口，方便统一解析
                msgInfo.put("URL_MSG", jsonMsg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        Obj_PublicMsgBase base = analys.get(msgType).onDeal(msgInfo);
        if (base == null) {
            return null;
        }
        base.msgID = msgGuid;
        base.MsgType = msgType;
        base.localTime = localTime;

        return base;
    }
}
