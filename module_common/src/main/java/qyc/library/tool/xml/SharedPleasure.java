package qyc.library.tool.xml;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * <p>
 * 模块编号:1.0
 * <p>
 * 模块名称:SharedPleasure
 * <p>
 * 功能:快乐的数据共享
 * <p>
 * 注意:整套应用程序之间都可以共享的轻型数据存储器,不会形成可见的文件
 * <p>
 * 相关模块:
 * 
 * @author 钱园超
 * @version 创建时间: 2011-8-4 下午07:28:59
 */
public class SharedPleasure{

	/**
	 *<p>功能:获取读取配置的工具
	 *<p>异常:NO
	 * @param context
	 * @param tableName 表名
	 * @return SharedPreferences
	 */
	public static SharedPreferences getReadTool(Context context,String tableName) {
		return context.getSharedPreferences(tableName, Context.MODE_PRIVATE);
	}

	/**
	 *<p>功能:获取保存配置的工具
	 *<p>异常:NO
	 * @param context
	 * @param tableName 表名
	 * @return Editor
	 */
	public static Editor getSaveTool(Context context,String tableName) {
		return context.getSharedPreferences(tableName, Context.MODE_PRIVATE).edit();
	}

}
