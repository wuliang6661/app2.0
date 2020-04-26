package synway.module_publicaccount.until;

import android.content.Context;

public class ClearTipUtils {

    public static void clearTip(Context context, String publicAccountID) {

        //TODO: 清除未读
        return;
        // 清除 消息提醒
        // SQLiteDatabase db = null;
//        try {
//
//            // db = SQLiteDatabase.openDatabase(BaseUtil.getDBPath(this), null,
//            // SQLiteDatabase.OPEN_READWRITE);
//            SetLCForPublicAccount.clearUnReadCount(context,
//                    Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase(),
//                    publicAccountID);
//            if (Main.instance().handlerLCTCount != null) {
//                Main.instance().handlerLCTCount.updateLCTCount();
//            }
////            Main.instance().newFragment().handlerTipCount.updateLCTCount();
//        }
//        catch (Exception e) {
//            throw new RuntimeException("SyncGetRecordFromDB 打开数据库出错");
//        }
//        finally {
//            // if (db != null) {
//            // db.close();
//            // }
//        }
//        //后续新增一个专门显示公众号最近消息的界面，也是需要清除的
//        PublicMessage.clearUnReadCount(context,
//                Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase(), publicAccountID);
    }


}
