package synway.module_interface.push;

import java.io.Serializable;

import synway.module_interface.config.netConfig.NetConfig;

/**
 * 工厂配置实体
 * Created by qyc on 2016/8/9.
 */
public class FacConfig implements Serializable{

    public FacConfig(NetConfig netConfig, String userID) {
        this.netConfig = netConfig;
        this.userID = userID;
    }

    public NetConfig netConfig = null;
    public String userID = null;
}
