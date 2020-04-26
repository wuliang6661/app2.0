package cn.synway.app.db.dbmanager;

import com.blankj.utilcode.util.LogUtils;

import java.util.List;

import cn.synway.app.db.DataManager;
import cn.synway.app.db.table.UserEntry;
import cn.synway.app.db.table.UserEntry_;
import io.objectbox.Box;
import io.objectbox.query.QueryBuilder;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/7/259:29
 * desc   :  登录用户表操作管理类
 * version: 1.0
 */
public class UserIml {


    /**
     * 登录用户进来
     */
    public static void addData(UserEntry recordEntry) {
        Box<UserEntry> box = DataManager.getInstance().boxStore.boxFor(UserEntry.class);
        List<UserEntry> users = getUserByUserId(recordEntry.getUserID());
        if (users.isEmpty()) {   //当前用户没登录过
            recordEntry.setLoginTime(System.currentTimeMillis());
            box.put(recordEntry);
        } else {   //当前用户登录过
            recordEntry.setLoginTime(System.currentTimeMillis());
            recordEntry.setId(users.get(0).id);
            box.put(recordEntry);
        }
    }


    /**
     * 获取当前正在登录的用户信息
     */
    public static UserEntry getUser() {
        Box<UserEntry> box = DataManager.getInstance().boxStore.boxFor(UserEntry.class);
        List<UserEntry> users = box.query().order(UserEntry_.loginTime, QueryBuilder.DESCENDING).build().find();
        if (users.isEmpty()) {
            LogUtils.e("当前登录用户为空！，程序异常！");
            return null;
        } else {
            return users.get(0);
        }
    }


    /**
     * 获取一个登录用户的信息
     */
    private static List<UserEntry> getUserByUserId(String userId) {
        Box<UserEntry> box = DataManager.getInstance().boxStore.boxFor(UserEntry.class);
        return box.query().equal(UserEntry_.userID, userId).build().find();
    }

}
