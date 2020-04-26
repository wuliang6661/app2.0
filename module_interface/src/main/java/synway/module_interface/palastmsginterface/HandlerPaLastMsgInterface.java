package synway.module_interface.palastmsginterface;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by huangxi
 * DATE :2019/3/11
 * Description ：批量清理消息中删除公众号简略消息
 */

public abstract class HandlerPaLastMsgInterface {
    public abstract int deletePaLastMsg(Context context,SQLiteDatabase db, long limitTime);

}
