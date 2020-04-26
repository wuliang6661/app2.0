package cn.synway.app.db.dbmanager;

import java.util.List;

import cn.synway.app.db.DataManager;
import cn.synway.app.db.table.ConfigEntry;
import cn.synway.app.db.table.ConfigEntry_;
import io.objectbox.Box;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/7/2614:17
 * desc   :  配置表操作管理类
 * version: 1.0
 */
public class ConfigIml {


    /**
     * 查询配置表单
     */
    public static List<ConfigEntry> queryAllConfig() {
        //对应操作对应表的类
        Box<ConfigEntry> userEntityBox = DataManager.getInstance().boxStore.boxFor(ConfigEntry.class);
        return userEntityBox.getAll();
    }


    /**
     * 给配置表单增加数据
     */
    public static void addConfig(ConfigEntry configEntry) {
        Box<ConfigEntry> box = DataManager.getInstance().boxStore.boxFor(ConfigEntry.class);
        box.put(configEntry);
    }

    /**
     * 增加配置清单表
     */
    public static void addConfigs(List<ConfigEntry> configEntries) {
        Box<ConfigEntry> box = DataManager.getInstance().boxStore.boxFor(ConfigEntry.class);
        box.put(configEntries);
    }


    /**
     * 清空配置清单表
     */
    public static void clearConfig() {
        Box<ConfigEntry> box = DataManager.getInstance().boxStore.boxFor(ConfigEntry.class);
        box.removeAll();
    }


    /**
     * 查询当前正在使用的配置
     */
    public static List<ConfigEntry> getSelectConfig() {
        Box<ConfigEntry> box = DataManager.getInstance().boxStore.boxFor(ConfigEntry.class);
        return box.query().equal(ConfigEntry_.isSelector, true).build().find();
    }

}
