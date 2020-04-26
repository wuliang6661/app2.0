package cn.synway.app.db.dbmanager;

import java.util.List;

import cn.synway.app.db.DataManager;
import cn.synway.app.db.table.MessageEntry;
import cn.synway.app.db.table.MessageEntry_;
import cn.synway.app.db.table.RecentContactsEntry_;
import io.objectbox.Box;
import io.objectbox.query.QueryBuilder;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/7/2314:20
 * desc   :  消息记录表的操作管理类
 * version: 1.0
 */
public class MessageIml {


    /**
     * 新增一条消息
     */
    public static void addData(MessageEntry recordEntry) {
        Box<MessageEntry> box = DataManager.getInstance().boxStore.boxFor(MessageEntry.class);
        try {
            recordEntry.setUserId(UserIml.getUser().getUserID());
            box.put(recordEntry);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 获取某个会话的消息记录
     */
    public static List<MessageEntry> getDataByRecentId(String recentId) {
        Box<MessageEntry> box = DataManager.getInstance().boxStore.boxFor(MessageEntry.class);
        return box.query()
                .equal(MessageEntry_.recentId, recentId)
                .equal(MessageEntry_.userId, UserIml.getUser().getUserID())
                .build().find();
    }


    /**
     * 根据msgId，查询消息
     */
    public static List<MessageEntry> getDataByMsgId(String msgId) {
        Box<MessageEntry> box = DataManager.getInstance().boxStore.boxFor(MessageEntry.class);
        return box.query()
                .equal(MessageEntry_.msgId, msgId)
                .build().find();
    }


    /**
     * 删除指定msgId 的消息
     */
    public static void deleteByMsgId(String msgId) {
        Box<MessageEntry> box = DataManager.getInstance().boxStore.boxFor(MessageEntry.class);
        box.remove(getDataByMsgId(msgId));
    }


}
