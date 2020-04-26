package synway.module_interface.lastcontact;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import synway.module_interface.db.SQLite;
import synway.module_interface.db.table_util.Table_LastContact;

/**
 * Created by zjw on 2017/3/29.
 */

public class LastContact {
    /**
     * 最近联系人类型，tinyint，0=单聊，1=群聊，2=公众 4= 广播   5 =一体化通知
     */
    public static final String TARGET_TYPE = "modulepushUtil.newLastContact.extra.TargetType";

    /**
     * 目标Id，varchar(40)
     */
    public static final String TARGET_ID = "modulepushUtil.newLastContact.extra.TargetId";

    /**
     * 目标名称，varchar(50)
     */
    public static final String TARGET_NAME = "modulepushUtil.newLastContact.extra.TargetName";

    /**
     * 目标图像的id
     */
    public static final String TARGET_PIC = "modulepushUtil.newLastContact.extra.TargetPic";



    /**
     * 最近一次通讯时间， dateTime
     */
    public static final String LAST_MSG_SERVER_TIME = "modulepushUtil.newLastContact.extra.LastMsgServerTime";

    /**
     * 最近一次通讯的内容， varchar
     */
    public static final String LAST_MSG_CONTENT = "modulepushUtil.newLastContact.extra.LastMsgContent";

    /**
     * 未读消息数量
     */
    public static final String UN_READ_COUNT = "modulepushUtil.newLastContact.extra.UnReadCount";

    /**
     * 消息发送结果广播
     */

    public static final String ACTION_CLEAR = "modulepushUtil.lastcontact.clear";

    public static final String EXTRA_SEND_TARGET_ID = "modulepushUtil.lastcontact.send.targetid";

    /**
     * 消息产生者的ID
     */
    public static final String FROM_USER_ID = "modulepushUtil.newLastContact.extra.FromUserID";

    /**
     * 消息产生者的名字
     */
    public static final String FROM_USER_NAME = "modulepushUtil.newLastContact.extra.FromUserName";

    /**
     * 插入或更新最近联系人
     */
    public static final String ACTION_UPDATE_CONTACT = "action.modulepushUtil.newLastMsg";


    /**
     * 更新最近联系人标题
     */
    public static final String ACTION_UPDATE_TITLE = "action.modulepushUtil.updateTitle";
    /**
     * 更新最近联系人内容，但不是一条新的消息，而是对旧消息的更新
     */
    public static final String ACTION_UPDATE_CONTENT = "action.modulepushUtil.updateContent";

    /**
     * 更新最近联系人的时间，暂时没用到，先提供出来
     */
    public static final String ACTION_UPDATE_TIME = "action.modulepushUtil.updateTime";
    /**
     * 刷新最近联系人
     */
    public static final String ACTION_REFRESH = "action.modulepushUtil.refresh";

    /**
     * 更新最近联系人的未读数
     */
    public static final String ACTION_UPDATE_UNREAD_COUNT = "action.modulepushUtil.update.unread.count";


    /**
     * 删除最近联系人
     */
    public static final String ACTION_DELETE_CONTACT = "action.modulepushUtil.deleteLastContact";

    public static final String PUBLIC_NOTICE_TARGET_NAME = "通知消息";
    public static final String PUBLIC_NOTICE_TARGET_ID = "public_notice_target_id";

    /**
     * 清除未读数
     */
    public static final void clearUnReadCount( Context context, SQLiteDatabase db, String targetID) {
            ContentValues cv = new ContentValues();
            cv.put(Table_LastContact.UN_READ_COUNT, 0);// 消息
            SQLite.update(db, Table_LastContact._TABLE_NAME, cv, Table_LastContact.TARGET_ID + "=?", new String[]{targetID});
            // 再发送广播
            Intent intent = new Intent(ACTION_CLEAR);
            intent.putExtra(EXTRA_SEND_TARGET_ID, targetID);
            context.sendBroadcast(intent);
    }


    /**
     * @param context
     * @param db
     * @param type         消息类型
     * @param targetID     目标ID
     * @param targetName   目标名称
     * @param msgContent   消息内容
     * @param serverTime   消息本地时间
     * @param serverTime   消息服务器时间
     * @param unReadCount  消息未读数
     * @param fromUserID  消息来源ID
     * @param fromUserName  消息来源用户的名称
     */
    //插入最近联系人或更新 最近联系人的消息内容，有可能被用户手动删除。所以，调用该方法必须把 最近联系人显示的标题，内容，时间（头像不需）都传递过来
    public static final void insertOrUpdate(int type, Context context, SQLiteDatabase db,
                                            String targetID, String targetName, String msgContent,
                                            long localTime,
                                             long serverTime, int unReadCount,String fromUserID, String fromUserName) {

        ContentValues cv2 = new ContentValues();
        cv2.put(Table_LastContact.TARGET_ID, targetID);
        cv2.put(Table_LastContact.TARGET_NAME, targetName);
        cv2.put(Table_LastContact.TARGET_TYPE, type);
        cv2.put(Table_LastContact.LAST_MSG_CONTENT, msgContent);
        cv2.put(Table_LastContact.LAST_MSG_LOCALE_TIME, localTime);
        cv2.put(Table_LastContact.LAST_MSG_SHOW_TIME, serverTime);
        cv2.put(Table_LastContact.UN_READ_COUNT, unReadCount);
        cv2.put(Table_LastContact.FROM_USER_ID, fromUserID);
        cv2.put(Table_LastContact.FROM_USER_NAME, fromUserName);
        String cols[] = new String[]{Table_LastContact.ID,
                Table_LastContact.TARGET_ID, Table_LastContact.TARGET_NAME};

        String strs = SQLite.queryOneRecord(db, Table_LastContact._TABLE_NAME, cols,
                "|", Table_LastContact.TARGET_ID + "=?", new String[]{targetID}, null);
        //已有记录，修改操作
        if (strs != null) {
            String selectionArgs[] = new String[]{targetID};
            SQLite.update(db, Table_LastContact._TABLE_NAME, cv2,
                    Table_LastContact.TARGET_ID + "=?", selectionArgs);
            //发送广播到最近联系人，进行修改


        } else {
            // [修改/新增数据库里的最近联系人]
            SQLite.insert(db, Table_LastContact._TABLE_NAME, null, cv2);
        }

        // 发送广播到 最近联系人
        Intent sendLCBC = new Intent();
        sendLCBC.setAction(ACTION_UPDATE_CONTACT);
        sendLCBC.putExtra(TARGET_ID, targetID);
        sendLCBC.putExtra(TARGET_NAME, targetName);
        sendLCBC.putExtra(TARGET_TYPE, type);
        sendLCBC.putExtra(LAST_MSG_CONTENT, msgContent);
        sendLCBC.putExtra(LAST_MSG_SERVER_TIME, serverTime);

        // 消息未读数
        // 设置最近联系人的未读数
        //unReadCount =-1，表示该公众号消息不参与未读消息计数
        sendLCBC.putExtra(UN_READ_COUNT, unReadCount);
        context.sendBroadcast(sendLCBC);
    }

    //删除最近联系人
    public static final void delete(final Context context,
                                               SQLiteDatabase db, final String targetID) {

        String[] cols = new String[]{Table_LastContact.SEND_STATE,
                Table_LastContact.TARGET_ID,};

        String strs = SQLite.queryOneRecord(db, Table_LastContact._TABLE_NAME,
                cols, "|", Table_LastContact.TARGET_ID + "=?",
                new String[]{targetID}, null);

        if (null != strs) {
            // 删除数据库中的记录
            SQLite.del(db, Table_LastContact._TABLE_NAME,
                    Table_LastContact.TARGET_ID + "=?",
                    new String[]{targetID});
        }

        // 告知界面删除对应项
        Intent intent = new Intent();
        intent.setAction(ACTION_DELETE_CONTACT);
        intent.putExtra("TYPE", 0);//该项是为了区别是自己退出的还是被别人踢出的，0为自己退出，其他则为被人踢出
        intent.putExtra(TARGET_ID, targetID);
        context.sendBroadcast(intent);

        new Thread(new Runnable() {
            @Override
            public void run() {
                DeleteLCUser.deleteLCUsera(context, targetID);
            }
        }).start();

    }

    //修改最近联系人三个区域的内容:标题，消息，时间
    public static final void updateTitle(int type, Context context, SQLiteDatabase db,
                                         String targetID, String targetName){
        ContentValues cv2 = new ContentValues();
        cv2.put(Table_LastContact.TARGET_ID, targetID);
        cv2.put(Table_LastContact.TARGET_NAME, targetName);
        cv2.put(Table_LastContact.TARGET_TYPE, type);
        String cols[] = new String[]{Table_LastContact.ID,
                Table_LastContact.TARGET_ID, Table_LastContact.TARGET_NAME};

        String strs = SQLite.queryOneRecord(db, Table_LastContact._TABLE_NAME, cols,
                "|", Table_LastContact.TARGET_ID + "=?", new String[]{targetID}, null);
        //已有记录，修改操作
        if (strs != null) {
            String selectionArgs[] = new String[]{targetID};
            SQLite.update(db, Table_LastContact._TABLE_NAME, cv2,
                    Table_LastContact.TARGET_ID + "=?", selectionArgs);
            //发送广播到最近联系人，进行修改


        } else {
            // [修改/新增数据库里的最近联系人]
            SQLite.insert(db, Table_LastContact._TABLE_NAME, null, cv2);
        }

        // 发送广播到 最近联系人
        Intent sendLCBC = new Intent();
        sendLCBC.setAction(ACTION_UPDATE_TITLE);
        sendLCBC.putExtra(TARGET_ID, targetID);
        sendLCBC.putExtra(TARGET_NAME, targetName);
        sendLCBC.putExtra(TARGET_TYPE, type);
        context.sendBroadcast(sendLCBC);
    }

    //修改最近联系人三个区域的内容:消息，时间
    public static final void updateContent(int type, Context context, SQLiteDatabase db,
                                         String targetID, String targetName,String content){
        ContentValues cv2 = new ContentValues();
        cv2.put(Table_LastContact.TARGET_ID, targetID);
        cv2.put(Table_LastContact.TARGET_NAME, targetName);
        cv2.put(Table_LastContact.LAST_MSG_CONTENT, content);
        cv2.put(Table_LastContact.TARGET_TYPE, type);
        String cols[] = new String[]{Table_LastContact.ID,
                Table_LastContact.TARGET_ID, Table_LastContact.TARGET_NAME};

        String strs = SQLite.queryOneRecord(db, Table_LastContact._TABLE_NAME, cols,
                "|", Table_LastContact.TARGET_ID + "=?", new String[]{targetID}, null);
        //已有记录，修改操作
        if (strs != null) {
            String selectionArgs[] = new String[]{targetID};
            SQLite.update(db, Table_LastContact._TABLE_NAME, cv2,
                    Table_LastContact.TARGET_ID + "=?", selectionArgs);
            //发送广播到最近联系人，进行修改


        } else {
            // [修改/新增数据库里的最近联系人]
            SQLite.insert(db, Table_LastContact._TABLE_NAME, null, cv2);
        }

        // 发送广播到 最近联系人
        Intent sendLCBC = new Intent();
        sendLCBC.setAction(ACTION_UPDATE_CONTENT);
        sendLCBC.putExtra(TARGET_ID, targetID);
        sendLCBC.putExtra(TARGET_NAME, targetName);
        sendLCBC.putExtra(TARGET_TYPE, type);
        sendLCBC.putExtra(LAST_MSG_CONTENT, content);
        context.sendBroadcast(sendLCBC);
    }

    //刷新最近联系人，比如用户头像更新
    public static final void refresh(Context context) {
        // 发送广播到 最近联系人
        Intent sendLCBC = new Intent();
        sendLCBC.setAction(ACTION_REFRESH);
        context.sendBroadcast(sendLCBC);
    }

}
