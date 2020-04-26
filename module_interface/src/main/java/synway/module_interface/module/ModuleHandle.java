package synway.module_interface.module;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

/** 系统的一些全局参数 */
public interface ModuleHandle {

	/** 有哪些全局参数可以放在这里??*/

	/** 系统进程的App接口 */
    Context getAppContext();

	/** 获取数据库操作 */
    SQLiteOpenHelper getSQLiteHelp();

}
