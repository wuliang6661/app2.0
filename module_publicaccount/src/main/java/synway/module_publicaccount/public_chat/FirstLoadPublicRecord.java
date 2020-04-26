package synway.module_publicaccount.public_chat;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import java.util.ArrayList;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgBase;
import synway.module_publicaccount.db.table_util.Table_PublicAccountRecord;

/**
 * Created by leo on 2019/1/19.
 */


class FirstLoadPublicRecord {



    public static ArrayList<Obj_PublicMsgBase> load(SQLiteDatabase db, String publicGUID, String pushMsgPageCode,String pushMsgPageName) {
        ArrayList<Obj_PublicMsgBase> resultList = new ArrayList<>();
        String sql;
        if (TextUtils.isEmpty(pushMsgPageCode)) {
            //不分类，查询所有数据。
            sql = "select * from "+ Table_PublicAccountRecord.getTableName(publicGUID)+" order by "+
                Table_PublicAccountRecord.publicRecord_col_localeTime+" desc limit 0,10";
        } else {
            sql = "select * from " + Table_PublicAccountRecord.getTableName(publicGUID)+" where ( "+
                Table_PublicAccountRecord.publicRecord_col_pushMsgPageCodes+" like '%"+pushMsgPageCode+"%' or " +
                Table_PublicAccountRecord.publicRecord_col_pushMsgPageNames+" like '%"+pushMsgPageName+"%' ) order by "+
                Table_PublicAccountRecord.publicRecord_col_localeTime+" desc limit 0,10";
        }

        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            Obj_PublicMsgBase objPublicMsgBase = PublicMsgRecord.read(cursor);
            if (objPublicMsgBase != null) {
                resultList.add(objPublicMsgBase);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return resultList;
    }




}
