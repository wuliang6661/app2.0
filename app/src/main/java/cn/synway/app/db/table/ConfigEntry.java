package cn.synway.app.db.table;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.NameInDb;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/7/114:46
 * desc   :  配置表
 * version: 1.0
 */
@Entity
public class ConfigEntry {

    @Id
    long id;

    /**
     * 配置名称
     */
    @NameInDb("CONFIG_NAME")
    public String configName;

    /**
     * 服务器IP
     */
    @NameInDb("SERVER_IP")
    public String serverIp;

    /**
     * 服务器端口
     */
    @NameInDb("SERVER_PORT")
    public String serverPort;

    /**
     * 是否选中
     */
    @NameInDb("IS_SELECTOR")
    public boolean isSelector;

    public ConfigEntry() {

    }


    public ConfigEntry(long id, String configName, String serverIp, String serverPort, boolean isSelector) {
        this.id = id;
        this.configName = configName;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.isSelector = isSelector;
    }

    public ConfigEntry(String configName, String serverIp, String serverPort, boolean isSelector) {
        this.configName = configName;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.isSelector = isSelector;
    }
}
