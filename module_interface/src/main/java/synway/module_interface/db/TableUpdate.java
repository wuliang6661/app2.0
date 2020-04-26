package synway.module_interface.db;

import android.database.sqlite.SQLiteDatabase;

public interface TableUpdate {

	/**
	 * 升级,你可以根据BE_VERSION的版本号,来确定这次程序发布将把DB升级到哪一版.
	 * 
	 * @param verson
	 *            verson=1表示建表 2表示在原始表的基础上开始迭代式升级
	 *            <p>
	 *            如果某一个版本号你不需要作升级,那么直接跳过该版本号.当你需要升级的时候,要根据最新的版本号来
	 * <p>
	 * @param db 对象
	 * <p>
	 * @return 该版本下你需要执行的SQL语句
	 */
    String[] update(int verson, SQLiteDatabase db);

	void delete(SQLiteDatabase db);

}
