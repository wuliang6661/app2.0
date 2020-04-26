package synway.module_publicaccount.rt_gis_msg.show;

import java.util.List;

import synway.module_publicaccount.rt_gis_msg.Obj_RTGis_Point;


/**
 * Created by 13itch on 2016/7/18.
 */
public interface RTGISViewI {
    void setPoint(List<Obj_RTGis_Point> pointList);
    void addPoint(Obj_RTGis_Point point);
    void removePoint(Obj_RTGis_Point point);
}
