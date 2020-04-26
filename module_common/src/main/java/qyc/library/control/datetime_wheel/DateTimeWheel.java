package qyc.library.control.datetime_wheel;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Calendar;

import qyc.library.control.datetime_wheel.adapter.AbstractWheelTextAdapter;
import qyc.library.control.datetime_wheel.adapter.NumericWheelAdapter;
import qyc.library.control.datetime_wheel.wheelview.OnWheelChangedListener;
import qyc.library.control.datetime_wheel.wheelview.WheelView;
import synway.common.R;

public class DateTimeWheel extends LinearLayout {

	// 年，月，日，时，分，秒 滚动条
	private WheelView mYear, mMonth, mDay, mHour, mMinute = null;

	// 各个滚动条的数据
	private int[] mYearData, mMonthData, mDayData, mHourData, mMinuteData = null;

	// 滚轮之间的空白
	private LinearLayout liLayout_beside_year, liLayout_beside_day = null;

	// 系统时间
	private Time time = null;

	// 当前滚动条所指示的内容
	private int mCurrentYear, mCurrentMonth, mCurrentDay, mCurrentHour, mCurrentMinute;

	// 年，月，日，时，分5个滚轮的集合
	private ArrayList<WheelView> wheels = null;

	// 对应的适配器
	private ArrayList<AbstractWheelTextAdapter> adapters = null;

	// 控件面板
	private View panel = null;

	// 最小的年份值
	private int minYear = DEFAULT_MIN_YEAR;

	// 最大的年份值
	private int maxYear = DEFAULT_MAX_YEAR;

	// 默认最小年份
	private static final int DEFAULT_MIN_YEAR = 1980;
	// 默认最大年份
	private static final int DEFAULT_MAX_YEAR = 2100;
	// 只显示日期样式
	private static final int DATESTYLE_ONLYDATE = 10;
	// 只显示时间样式
	private static final int DATESTYLE_ONLYTIME = 11;
	// 显示日期和时间
	private static final int DATASTYLE_DATEANDTIME = 12;
	// 日期改变监听
	private OnDateTimeWheelScrolled onDateTimeWheelScrolled = null;

	// 初始显示日期为系统日期
	public DateTimeWheel(Context context, AttributeSet attrs) {
		super(context, attrs);

		panel = LayoutInflater.from(context).inflate(R.layout.lib_datetimewheel_cv, this, true);
		mYear = panel.findViewById(R.id.id_year);
		mMonth = panel.findViewById(R.id.id_month);
		mDay = panel.findViewById(R.id.id_day);
		mHour = panel.findViewById(R.id.id_hour);
		mMinute = panel.findViewById(R.id.id_minute);
		liLayout_beside_day = findViewById(R.id.liLayout_beside_day);
		liLayout_beside_year = findViewById(R.id.liLayout_beside_year);

		wheels = new ArrayList<WheelView>();
		wheels.add(mYear);
		wheels.add(mMonth);
		wheels.add(mDay);
		wheels.add(mHour);
		wheels.add(mMinute);

		adapters = new ArrayList<AbstractWheelTextAdapter>();

		initCurrentValue();

		initWheelData();

		// 绑定监听器监听滚轮变化
		for (int i = 0; i < wheels.size(); i++) {
			wheels.get(i).addChangingListener(changeListener);
		}

		setResValues(attrs);

		setTheDate(mCurrentYear, mCurrentMonth, mCurrentDay, mCurrentHour, mCurrentMinute);
	}

	/** 初始化各个"mCurrent"值为 系统当前日期值 */
	private void initCurrentValue() {
		time = new Time();
		time.setToNow();

		mCurrentYear = time.year;
		mCurrentMonth = time.month + 1;
		mCurrentDay = time.monthDay;
		mCurrentHour = time.hour;
		mCurrentMinute = time.minute;
	}

	/**
	 * 更新所有"mCurrent"值为当前轮盘所指示的值
	 */
	private void updateValue(int type, int value) {
		switch (type) {
		case Calendar.YEAR :
			mCurrentYear = value;
		break;
		case Calendar.MONTH :
			mCurrentMonth = value;
		break;
		case Calendar.HOUR :
			mCurrentHour = value;
		break;
		case Calendar.MINUTE :
			mCurrentMinute = value;
		break;
		case Calendar.DAY_OF_MONTH :
			mCurrentDay = value;
		break;
		}

		if (onDateTimeWheelScrolled != null) {
			onDateTimeWheelScrolled.onDateTimeWheelScrolled(this, type, value);
		}
	}

	/** 根据自定义属性，对控件样式进行调整 */
	private void setResValues(AttributeSet attrs) {
		// 默认格式为DATEANDTIME,即同时显示日期和时间
		int dateStyle = DATASTYLE_DATEANDTIME;

		int paddingLeft = 0;
		int paddingRight = 0;
		int paddingTop = 0;
		int paddingBottom = 0;

		int wheelBg = -1;
		int wheelFg = -2;
		int textColor = -3;
		int textSize = -4;

		int minYearValue = 0;
		int maxYearValue = 0;

		boolean drawShadows = true;
		// 获取控件自定义属性值
		TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DateTimeWheel);
		int N = a.getIndexCount();// 自定义属性被使用的数量
		for (int i = 0; i < N; i++) {
			int attr = a.getIndex(i);
			if (attr == R.styleable.DateTimeWheel_wheelDateStyle) {
				dateStyle = a.getInt(attr, DATASTYLE_DATEANDTIME);
			} else if (attr == R.styleable.DateTimeWheel_wheelPaddingLeft) {
				paddingLeft = (int) a.getDimension(attr, panel.getPaddingLeft());
			} else if (attr == R.styleable.DateTimeWheel_wheelPaddingRight) {
				paddingRight = (int) a.getDimension(attr, panel.getPaddingRight());
			} else if (attr == R.styleable.DateTimeWheel_wheelPaddingTop) {
				paddingTop = (int) a.getDimension(attr, panel.getPaddingTop());
			} else if (attr == R.styleable.DateTimeWheel_wheelPaddingBottom) {
				paddingBottom = (int) a.getDimension(attr, panel.getPaddingBottom());
			} else if (attr == R.styleable.DateTimeWheel_wheelBackground) {
				wheelBg = a.getResourceId(attr, R.drawable.lib_datetimewheel_bg_ll);
			} else if (attr == R.styleable.DateTimeWheel_wheelForeground) {
				wheelFg = a.getResourceId(attr, R.drawable.lib_datetimewheel_fg_sp);
			} else if (attr == R.styleable.DateTimeWheel_wheelTextColor) {
				textColor = a.getColor(attr, AbstractWheelTextAdapter.DEFAULT_TEXT_COLOR);
			} else if (attr == R.styleable.DateTimeWheel_wheelTextSize) {
				textSize = (int) a.getDimension(attr, AbstractWheelTextAdapter.DEFAULT_TEXT_SIZE);
			} else if (attr == R.styleable.DateTimeWheel_wheelMaxYear) {
				maxYearValue = a.getInt(attr, DEFAULT_MAX_YEAR);
			} else if (attr == R.styleable.DateTimeWheel_wheelMinYear) {
				minYearValue = a.getInt(attr, DEFAULT_MIN_YEAR);
			} else if (attr == R.styleable.DateTimeWheel_wheelDrawShadows) {
				drawShadows = a.getBoolean(attr, true);
			} else {
			}
		}

		a.recycle();
		panel.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

		if (dateStyle == DATESTYLE_ONLYDATE) {
			mHour.setVisibility(View.GONE);
			mMinute.setVisibility(View.GONE);
			liLayout_beside_day.setVisibility(View.GONE);
			liLayout_beside_year.setVisibility(View.GONE);
		} else if (dateStyle == DATESTYLE_ONLYTIME) {
			mYear.setVisibility(View.GONE);
			mMonth.setVisibility(View.GONE);
			mMinute.setVisibility(View.GONE);
			liLayout_beside_day.setVisibility(View.GONE);
			liLayout_beside_year.setVisibility(View.GONE);
		}

		for (WheelView w : wheels) {
			w.setDrawShadows(drawShadows);
		}

		if (maxYearValue != 0) {
			this.maxYear = maxYearValue;
		}

		if (minYearValue != 0) {
			this.minYear = minYearValue;
		}

		// 年份范围minYear~maxYear(默认为1980~2100), 年份[index] = index + minYear;
		mYearData = new int[maxYear - minYear + 1];
		for (int i = 0; i < mYearData.length; i++) {
			mYearData[i] = i + minYear;
		}
		mYearAdapter = new NumericWheelAdapter(getContext(), mYearData[0], mYearData[mYearData.length - 1]);
		adapters.add(mYearAdapter);
		mYear.setViewAdapter(mYearAdapter);

		if (wheelBg != -1) {
			for (WheelView w : wheels) {
				w.setWheelBackground(wheelBg);
			}
		}

		if (wheelFg != -2) {
			for (WheelView w : wheels) {
				w.setWheelForeground(wheelFg);
			}
		}

		if (textColor != -3) {
			for (AbstractWheelTextAdapter abwa : adapters) {
				abwa.setTextColor(textColor);
			}
		}

		if (textSize != -4) {
			for (AbstractWheelTextAdapter abwa : adapters) {
				abwa.setTextSize(textSize);
			}
		}
	}

	// ======================= 外部获得当前滚轮值的方法 =======================
	public int getYear() {
		return mCurrentYear;
	}

	public int getMonth() {
		return mCurrentMonth;
	}

	public int getDay() {
		return mCurrentDay;
	}

	public int getHour() {
		return mCurrentHour;
	}

	public int getMinute() {
		return mCurrentMinute;
	}

	// =====================================================================

	/**
	 * 根据参数来设定滚轮的日期值，不需要更改的值设为-1
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 */
	public void setTheDate(int year, int month, int day, int hour, int minute) {
		// ======set the year======
		if (year >= minYear && year <= maxYear) {
			mYear.setCurrentItem(year - minYear);
			// updateValue(Calendar.YEAR, year);
		} else {
			System.out.println("setDate:------------->年份越界");
		}

		// ======set the month======
		if (month >= 1 && month <= 12) {
			mMonth.setCurrentItem(month - 1);
			// updateValue(Calendar.MONTH, month);
		} else {
			System.out.println("setDate:------------->月份越界");
		}

		// ======set the day======
		// 1~28号每个月都有
		boolean bool1 = (day <= 28 && day >= 1);

		// 29号，但不是2月
		boolean bool2 = (day == 29 && mCurrentMonth != 2);

		// 31号并且月份是1,3,5,7,8,10,12
		boolean bool3 = (day == 31 && (mCurrentMonth == 1 || mCurrentMonth == 3 || mCurrentMonth == 5
				|| mCurrentMonth == 7 || mCurrentMonth == 8 || mCurrentMonth == 9 || mCurrentMonth == 12));
		// 30号并且不是2月
		boolean bool4 = (day == 30 && mCurrentMonth != 2);

		// 29号，闰年2月
		boolean bool5 = (day == 29 && judgeLeapYear(mCurrentYear) && mCurrentMonth == 2);
		if (bool1 || bool2 || bool3 || bool4 || bool5) {
			// mCurrentDay = day + "";
			mDay.setCurrentItem(day - 1);
			// updateValue(Calendar.DAY_OF_MONTH, day);
		}

		// ======set the hour======
		if (hour >= 0 && hour <= 23) {
			mHour.setCurrentItem(hour);
			// updateValue(Calendar.HOUR, hour);
		}

		// ======set the minute======
		if (minute >= 0 && minute <= 59) {
			mMinute.setCurrentItem(minute);
			// updateValue(Calendar.MINUTE, minute);
		}

	}

	/**
	 * "天数"的滚轮不同于其他几个，使用了NumericWheelAdapter,这是为了方便更换， 从而适应天数分别为28 29 30 31的月份
	 * 比如，当“月份”滚轮在2月，那么30 31号的出现都是不合理的
	 */
	private NumericWheelAdapter dayAdapter = null;
	private NumericWheelAdapter mYearAdapter = null;
	private NumericWheelAdapter mMonthAdapter = null;
	private NumericWheelAdapter mHourAdapter = null;
	private NumericWheelAdapter mMinuteAdapter = null;

	/**
	 * 初始化除mYear外其他WheelView所需要的数据和Adapter
	 */
	private void initWheelData() {
		// 月份1 ~ 12, 月份[index] = index + 1
		mMonthData = new int[12];
		for (int i = 0; i < 12; i++) {
			mMonthData[i] = i + 1;
		}
		mMonthAdapter = new NumericWheelAdapter(getContext(), 1, 12);
		adapters.add(mMonthAdapter);
		mMonth.setViewAdapter(mMonthAdapter);

		// mDay所使用的适配器与其他不同,以适应频繁更换 天[index] = index + 1
		mDayData = new int[31];
		for (int i = 0; i < 31; i++) {
			mDayData[i] = i + 1;
		}
		dayAdapter = new NumericWheelAdapter(getContext(), 1, 31);
		adapters.add(dayAdapter);
		mDay.setViewAdapter(dayAdapter);

		// 小时0~23, 小时[index] = index
		mHourData = new int[24];
		for (int i = 0; i < 24; i++) {
			mHourData[i] = i;
		}
		mHourAdapter = new NumericWheelAdapter(getContext(), 0, 23);
		adapters.add(mHourAdapter);
		mHour.setViewAdapter(mHourAdapter);

		// 分钟0~59, 分钟[index] = index
		mMinuteData = new int[60];
		for (int i = 0; i < 60; i++) {
			if (i < 10) {
				mMinuteData[i] = i;
			} else {
				mMinuteData[i] = i;
			}
		}
		mMinuteAdapter = new NumericWheelAdapter(getContext(), 0, 59);
		adapters.add(mMinuteAdapter);
		mMinute.setViewAdapter(mMinuteAdapter);

		// 设置滚轮的可见项数量
		for (WheelView w : wheels) {
			w.setVisibleItems(5);
		}
	}

	/** 监听:滚轮滚动结束 */
	public void setOnScrolledListen(OnDateTimeWheelScrolled wheelScrolled) {
		this.onDateTimeWheelScrolled = wheelScrolled;
	}

	/** 监听滚轮值是否改变，来决定是否更新“天”滚轮，同时更新所有"mCurrent"值 */
	private OnWheelChangedListener changeListener = new OnWheelChangedListener() {

		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			// 改变的日期类型
			int type = -1;

			if (wheel == mYear) {
				type = Calendar.YEAR;
				updateValue(type, mYearData[newValue]);
			} else if (wheel == mMonth) {
				type = Calendar.MONTH;
				updateValue(type, mMonthData[newValue]);
			} else if (wheel == mHour) {
				type = Calendar.HOUR;
				updateValue(type, mHourData[newValue]);
			} else if (wheel == mMinute) {
				type = Calendar.MINUTE;
				updateValue(type, mMinuteData[newValue]);
			} else if (wheel == mDay) {
				type = Calendar.DAY_OF_MONTH;
				updateValue(type, mDayData[newValue]);
			}

			int currentDay = mDay.getCurrentItem() + 1;

			// 当前“天”小于27,29~31无法被显示在滚轮上，对于各月份来说无差别，也就不需要更新“天”滚轮
			// 更新"mCurrent"值，然后返回
			if (currentDay < 27) {
				return;
			}

			int currentMonth = mMonth.getCurrentItem() + 1;
			int currentYear = mYear.getCurrentItem() + minYear;

			// 是否为闰年，ifLeapYear = true 表示这一年是闰年
			boolean ifLeapYear = judgeLeapYear(currentYear);

			// 接下来就是 天 =27,28的情况了
			// 只有2月需要单独考虑，其他月份不受影响
			if (currentMonth == 2 && ifLeapYear) {
				boolean bool = mDay.getCurrentItem() + 1 > 29;
				dayAdapter.setMaxValue(29);
				dayAdapter.notifyDataInvalidatedEvent();
				if (bool) {
					mDay.setCurrentItem(28);
					// updateValue(type, 29);
				}
				return;
			} else if (currentMonth == 2 && !ifLeapYear) {
				boolean bool = mDay.getCurrentItem() + 1 > 28;
				dayAdapter.setMaxValue(28);
				dayAdapter.notifyDataInvalidatedEvent();
				if (bool) {
					mDay.setCurrentItem(27);
					// updateValue(type, 28);
				}
				return;
			} else if (currentDay <= 28) {
				boolean bool = mDay.getCurrentItem() + 1 > 30;
				dayAdapter.setMaxValue(30);
				dayAdapter.notifyDataInvalidatedEvent();
				if (bool) {
					mDay.setCurrentItem(29);
					// updateValue(type, 30);
				}
				return;
			}

			// 天>=29
			// 4,6,9,11 为 30天
			// 1,3,5,7,8,10,12 为 31天
			if (currentDay >= 29) {
				if (currentMonth == 4 || currentMonth == 6 || currentMonth == 9 || currentMonth == 11) {
					boolean bool = mDay.getCurrentItem() + 1 > 30;
					dayAdapter.setMaxValue(30);
					dayAdapter.notifyDataInvalidatedEvent();
					if (bool) {
						mDay.setCurrentItem(29);
						// updateValue(type, 30);
					}
				} else if (currentMonth != 2) {
					dayAdapter.setMaxValue(31);
					dayAdapter.notifyDataInvalidatedEvent();
				}
			}

		}
	};

	/**
	 * 判断是不是闰年，是则返回true,不是返回false
	 * 
	 * @param year
	 * @return
	 */
	public static boolean judgeLeapYear(int year) {
        return year % 400 == 0 || (year % 4 == 0 && year % 100 != 0);
	}
}
