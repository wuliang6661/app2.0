package synway.module_interface.module;

import android.content.Context;

import synway.module_interface.config.netConfig.NetConfig;

/**
 * @author 钱园超 2015年7月23日下午1:43:13 程序初始化接口.
 */
public interface OnInit {

	/**
	 * 初始化动作将在线程里按顺序执行.这里即使有耗时动作也做成同步
	 * 
	 * @param context
	 * @return 错误信息[0=粗略 1=详细],正确返回null
	 */
    String[] onInit(Context context, NetConfig netConfig);

	/**
	 * 获取提示文字
	 * 
	 * @return 提示文字
	 */
    String getTip();
}
