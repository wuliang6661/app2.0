package synway.module_publicaccount.map.publicrtgis;

import java.io.Serializable;

/**
 * Created by 13itch on 2016/7/15.
 */
public class Obj_RTGis_Point implements Serializable{
    public String PublicGUID;
    public String ID;
    public int type=-1;
    public String name;
    /**
     * 经度
     */
    public String X;
    /**
     * 纬度
     */
    public String Y;
    /**
     * 提示
     */
    public String tip;
    public String time;

}
