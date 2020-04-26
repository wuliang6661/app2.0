package synway.module_publicaccount.public_message;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ParseDate {
	// 将文本日期转为日期对象
	// private static final SimpleDateFormat sdf_Parse = new SimpleDateFormat(
	// "yyyy-MM-dd HH:mm:ss", Locale.CHINA);

	// 跨年：年月日
	private static final SimpleDateFormat sdf1 = new SimpleDateFormat("yy年M月d日", Locale.CHINA);

	// 月日
	private static final SimpleDateFormat sdf2 = new SimpleDateFormat("M月d日 H点", Locale.CHINA);

	// 时分
	private static final SimpleDateFormat sdf3 = new SimpleDateFormat("H点mm", Locale.CHINA);

	public static final String parseDate(long time) {
		if (time == 0) {
			return "";
		}

		return parseDate(Calendar.getInstance(Locale.CHINA).getTimeInMillis(), time);
	}
	public static final String parseDate(long now, long time) {
		if (time == 0) {
			return "";
		}
		String str = "";
		int[] dayOffset_yearOffset = getGapCount(time, now);
		int dayOffset = dayOffset_yearOffset[0];
		int yearOffset = dayOffset_yearOffset[1];
		if (dayOffset < 0) {
			str = "未来 " + sdf3.format(new Date(time));
		} else if (dayOffset == 0) {
			str = "今天" + sdf3.format(new Date(time));
		} else if (dayOffset == 1) {
			str = "昨天" + sdf3.format(new Date(time));
		} else if (dayOffset == 2) {
			str = "前天 " + sdf3.format(new Date(time));
		} else {
			if (yearOffset != 0) {
				str = sdf1.format(new Date(time));
			} else {
				str = sdf2.format(new Date(time));
			}
		}

		// 好几天前的，就看有没有跨年
		return str;
	}

	/** 判断两个时间之间,差了几个自然日_自然年 */
	public static int[] getGapCount(long startDate, long endDate) {
		Calendar startCalendar = Calendar.getInstance(Locale.CHINA);
		startCalendar.setTimeInMillis(startDate);
		startCalendar.set(Calendar.HOUR_OF_DAY, 0);
		startCalendar.set(Calendar.MINUTE, 0);
		startCalendar.set(Calendar.SECOND, 0);
		startCalendar.set(Calendar.MILLISECOND, 0);

		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTimeInMillis(endDate);
		endCalendar.set(Calendar.HOUR_OF_DAY, 0);
		endCalendar.set(Calendar.MINUTE, 0);
		endCalendar.set(Calendar.SECOND, 0);
		endCalendar.set(Calendar.MILLISECOND, 0);

		int dayOffset = (int) ((endCalendar.getTime().getTime() - startCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
		int yearOffset = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
		return new int[]{dayOffset, yearOffset};
	}

}
