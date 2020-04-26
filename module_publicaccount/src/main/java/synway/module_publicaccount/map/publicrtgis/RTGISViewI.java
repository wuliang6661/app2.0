package synway.module_publicaccount.map.publicrtgis;

import java.util.List;

/**
 * Created by 13itch on 2016/7/18.
 */
public interface RTGISViewI {
    void setPoint(List<Obj_RTGis_Point> pointList);
    void addPoint(Obj_RTGis_Point point);
    void removePoint(Obj_RTGis_Point point);
}
