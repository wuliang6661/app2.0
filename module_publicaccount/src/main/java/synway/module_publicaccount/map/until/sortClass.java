package synway.module_publicaccount.map.until;

import java.util.Comparator;

import synway.module_publicaccount.analytical.obj.Obj_PublicMsgTrail.Point;

/**
 * Created by ysm on 2017/3/24.
 */

public class sortClass implements Comparator {
    public int compare(Object arg0,Object arg1){
        Point user0 = (Point)arg0;
        Point user1 = (Point)arg1;
        int flag = user0.datetime.compareTo(user1.datetime);
        return flag;
    }
}
