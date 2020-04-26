package synway.module_publicaccount.map;

import qyc.synjob.SynJobTask;
import synway.module_publicaccount.analytical.obj.Obj_PulibcMsgDynamicLocation;

/**
 * Created by ysm on 2017/3/21.
 * 删除一个轨迹点
 */

public class DeleteDynamic extends SynJobTask {
    @Override
    public boolean inUIThread() {
        return false;
    }

    @Override
    public void onStart(Object... objects) {
        Obj_PulibcMsgDynamicLocation.Point p = (Obj_PulibcMsgDynamicLocation.Point)objects[0];
    }
}
