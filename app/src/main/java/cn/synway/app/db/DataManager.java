package cn.synway.app.db;

import cn.synway.app.base.SynApplication;
import cn.synway.app.db.table.ConfigEntry;
import cn.synway.app.db.table.MessageEntry;
import cn.synway.app.db.table.RecentContactsEntry;
import cn.synway.app.db.table.UserEntry;
import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/7/114:45
 * desc   :  数据库统一管理类
 * version: 1.0
 */
public class DataManager {

    private static DataManager dataManager;

    public static synchronized DataManager getInstance() {
        if (dataManager == null) {
            dataManager = new DataManager();
        }
        return dataManager;
    }

    public BoxStore boxStore;


    public void init(SynApplication demoApplication) {
        boxStore = demoApplication.getBoxStore();
    }


    /**
     * 清空所有表的数据
     */
    public void clearAll() {
        Box<ConfigEntry> box = boxStore.boxFor(ConfigEntry.class);
        box.removeAll();
        Box<MessageEntry> messageBox = boxStore.boxFor(MessageEntry.class);
        messageBox.removeAll();
        Box<RecentContactsEntry> recentBox = boxStore.boxFor(RecentContactsEntry.class);
        recentBox.removeAll();
        Box<UserEntry> userBox = boxStore.boxFor(UserEntry.class);
        userBox.removeAll();
    }

}
