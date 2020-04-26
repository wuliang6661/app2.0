package synway.module_publicaccount.push;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import org.json.JSONObject;
import synway.module_interface.db.SQLite;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgTrail;
import synway.module_publicaccount.db.table_util.Table_PublicAccountRecord;
import synway.module_publicaccount.db.table_util.Table_PublicAccount_Gis;
import synway.module_publicaccount.db.table_util.Table_PublicAccount_Notice;
import synway.module_publicaccount.until.StringUtil;

class RecordDeal {
    /***
     * 插入单个公众号表的推送信息
     * @param db
     * @param guid
     * @param time
     * @param jsonMsg
     * @param data
     * @param now
     * @param type
     * @param toUser
     */
    static final void insert(SQLiteDatabase db, String publicGUID,String guid, String time,
                             JSONObject jsonMsg, String data, long now, int type, String toUser,String pushMsgPageCodes,String pushMsgPageNames) {

        String tableName = Table_PublicAccountRecord.getTableName(publicGUID);
        ContentValues cv = new ContentValues();
        cv.put(Table_PublicAccountRecord.publicRecord_col_GUID, guid);
        cv.put(Table_PublicAccountRecord.publicRecord_col_serverTime, time);
        cv.put(Table_PublicAccountRecord.publicRecord_col_msg,
                jsonMsg.toString());
        cv.put(Table_PublicAccountRecord.publicRecord_col_data, data);
        cv.put(Table_PublicAccountRecord.publicRecord_col_localeTime, now);
        cv.put(Table_PublicAccountRecord.publicRecord_col_msgType, type);
        cv.put(Table_PublicAccountRecord.publicRecord_col_toUser, toUser);
        cv.put(Table_PublicAccountRecord.publicRecord_col_pushMsgPageCodes, pushMsgPageCodes);
        cv.put(Table_PublicAccountRecord.publicRecord_col_pushMsgPageNames, pushMsgPageNames);

        SQLite.insert(db, tableName, null, cv);

    }

    static final void insertNotice(SQLiteDatabase db, String publicGuid, String serverTime, JSONObject msgInfoJson, long now, int msgType, String toUser, String noticeID, int pageType) {

        ContentValues cv = new ContentValues();
        cv.put(Table_PublicAccount_Notice.PUBLIC_NOTICE_COL_PUBLIC_GUID, publicGuid);
        cv.put(Table_PublicAccount_Notice.PUBLIC_NOTICE_COL_SERVERTIME, serverTime);
        cv.put(Table_PublicAccount_Notice.PUBLIC_NOTICE_COL_MSG, msgInfoJson.toString());
        cv.put(Table_PublicAccount_Notice.PUBLIC_NOTICE_COL_LOCALETIME, now);
        cv.put(Table_PublicAccount_Notice.PUBLIC_NOTICE_COL_MSGTYPE, msgType);
        cv.put(Table_PublicAccount_Notice.PUBLIC_NOTICE_COL_TOUSER, toUser);
        cv.put(Table_PublicAccount_Notice.PUBLIC_NOTICE_COL_MSG_ID, noticeID);
        cv.put(Table_PublicAccount_Notice.PUBLIC_NOTICE_COL_PAGE_TYPE,pageType);

        SQLite.insert(db, Table_PublicAccount_Notice._TABLE_NAME, null, cv);

    }

    /***
     * 插入用户实时轨迹表
     * @param db
     * @param userid
     * @param username
     * @param type
     * @param Group
     * @param PicUrl
     * @param point
     * @param time
     */
    static final void insertGis(SQLiteDatabase db, String userid, String username, int type, String Group,
                                String PicUrl, Obj_PublicMsgTrail.Point point, Long time,int DataType,String equipType) {
//        JSONObject jsonObj = new JSONObject();
        String tableName = Table_PublicAccount_Gis._TABLE_NAME;
        ContentValues cv = new ContentValues();
        cv.put(Table_PublicAccount_Gis.USER_ID, userid);
        cv.put(Table_PublicAccount_Gis.USER_NAME, username);
        cv.put(Table_PublicAccount_Gis.GISTYPE, type);
        cv.put(Table_PublicAccount_Gis.USER_GROUP, Group);
        cv.put(Table_PublicAccount_Gis.USER_PIC, PicUrl);
        cv.put(Table_PublicAccount_Gis.X,point.x );
        cv.put(Table_PublicAccount_Gis.Y,point.y );
        cv.put(Table_PublicAccount_Gis.TIME, time);
        cv.put(Table_PublicAccount_Gis.DataType,DataType);
        cv.put(Table_PublicAccount_Gis.EQUIP_TYPE,equipType);
        String result = SQLite.queryOneRecord(db, tableName, new String[]{Table_PublicAccount_Gis.USER_ID, Table_PublicAccount_Gis.X}, "|", Table_PublicAccount_Gis.USER_ID + "=?",
                new String[]{userid}, null);
        if (StringUtil.isEmpty(result)) {
            Long insertresult = SQLite.insert(db, tableName, null, cv);
        } else {
            SQLite.update(db, tableName, cv, Table_PublicAccount_Gis.USER_ID + "=?", new String[]{userid});
        }
    }

    /***
     * 删除单个用户的实时轨迹
     * @param db
     * @param userid
     */
    static final void deleteGis(SQLiteDatabase db, String userid) {
        String tableName = Table_PublicAccount_Gis._TABLE_NAME;
        SQLite.del(db, tableName, Table_PublicAccount_Gis.USER_ID + "=?", new String[]{userid});
    }

    /***
     * 插入单个公众号表的推送信息-- 若msg超过1024位
     * @param db
     * @param guid
     * @param time
     * @param jsonMsg
     * @param now
     * @param type
     * @param toUser
     */
    static final void insert2(SQLiteDatabase db, String guid, String time,
                             JSONObject jsonMsg, long now, int type, String toUser) {

        String tableName = Table_PublicAccountRecord.getTableName(guid);
        ContentValues cv = new ContentValues();
        cv.put(Table_PublicAccountRecord.publicRecord_col_GUID, guid);
        cv.put(Table_PublicAccountRecord.publicRecord_col_serverTime, time);
        cv.put(Table_PublicAccountRecord.publicRecord_col_msg,
                "nonuse");
        cv.put(Table_PublicAccountRecord.publicRecord_col_data,
                jsonMsg.toString());
        cv.put(Table_PublicAccountRecord.publicRecord_col_localeTime, now);
        cv.put(Table_PublicAccountRecord.publicRecord_col_msgType, type);
        cv.put(Table_PublicAccountRecord.publicRecord_col_toUser, toUser);

        SQLite.insert(db, tableName, null, cv);

    }

}