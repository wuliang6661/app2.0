package cn.synway.app.db.dbmanager;

import java.util.List;

import cn.synway.app.db.DataManager;
import cn.synway.app.db.table.RecentContactsEntry;
import cn.synway.app.db.table.RecentContactsEntry_;
import io.objectbox.Box;
import io.objectbox.query.QueryBuilder;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/7/2314:29
 * desc   : 最近联系人表的操作管理类
 * version: 1.0
 */
public class RecentContactIml {


    /**
     * 查询某个用户的最近联系数据
     */
    public static List<RecentContactsEntry> getDataByContactId(String contactId) {
        Box<RecentContactsEntry> box = DataManager.getInstance().boxStore.boxFor(RecentContactsEntry.class);
        return box.query()
                .equal(RecentContactsEntry_.userId, UserIml.getUser().getUserID())
                .equal(RecentContactsEntry_.contactId, contactId)
                .build().find();
    }


    /**
     * 查询全部最近联系人列表,按时间降序查询
     */
    public static List<RecentContactsEntry> getAllData() {
        Box<RecentContactsEntry> box = DataManager.getInstance().boxStore.boxFor(RecentContactsEntry.class);
        return box.query()
                .equal(RecentContactsEntry_.userId, UserIml.getUser().getUserID())
                .order(RecentContactsEntry_.msgLocalTime, QueryBuilder.DESCENDING)
                .build().find();
    }

    /**
     * 新增一条最近联系人数据
     */
    public static void addData(RecentContactsEntry entry) {
        Box<RecentContactsEntry> box = DataManager.getInstance().boxStore.boxFor(RecentContactsEntry.class);
        entry.userId = UserIml.getUser().getUserID();
        box.put(entry);
    }

}
