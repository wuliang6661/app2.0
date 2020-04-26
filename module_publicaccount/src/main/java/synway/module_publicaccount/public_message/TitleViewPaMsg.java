package synway.module_publicaccount.public_message;

import android.app.Activity;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;
import synway.module_publicaccount.Main;

/**
 * 作为应用中心标题栏的基类，可以通过继承这个类实现不同的样式
 */

public abstract class TitleViewPaMsg {

    /**
     * 设置界面的未读数
     * @param count
     */
    public abstract void setCountViewText(int count);

    /**
     * 初始化出TitleView
     * @param act
     * @return
     */
    public abstract View initTitleView(Activity act);

    public int getAllUnReadCount() {
        int s = 0;
        SQLiteOpenHelper sqliteHelp = Main.instance().moduleHandle.getSQLiteHelp();
        s = PublicMessage.getAllUnReadCount(sqliteHelp.getWritableDatabase());
        return s;
    }

}
