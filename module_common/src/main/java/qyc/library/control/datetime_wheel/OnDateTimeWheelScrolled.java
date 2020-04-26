package qyc.library.control.datetime_wheel;

import android.view.View;

public interface OnDateTimeWheelScrolled {

	/** 
	 * 当日期的值发生改变的时候回调
	 * @param dt	
	 * 	发生改变的日期控件
	 * @param type	
	 * 	具体是哪一个值发生了改变，即Calendar.YEAR,Calendar.MONTH,Calendar.DAY_OF_MONTH,Calendar.HOUR,Calendar.MINUTE
	 * @param value
	 * 	改变之后的值
	 */
    void onDateTimeWheelScrolled(View dt, int type, int value);
}
