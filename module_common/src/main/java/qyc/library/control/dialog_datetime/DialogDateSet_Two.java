package qyc.library.control.dialog_datetime;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import qyc.library.control.datetime_wheel.DateTimeWheel;
import qyc.library.control.datetime_wheel.OnDateTimeWheelScrolled;
import synway.common.R;

public class DialogDateSet_Two extends Dialog {

	private Calendar calStart, calStop;
	private DateTimeWheel dtwStart, dtwStop;
	private String title = null;
	private Button btn_setTime, btn_cancel = null;
	private EditText edtYear_Start, edtMonth_Start, edtDay_Start, edtYear_Stop,
			edtMonth_Stop, edtDay_Stop = null;
	private OnClickListener onClickListener = null;

	public DialogDateSet_Two(Context context) {
		super(context);
	}

	public DialogDateSet_Two(Context context, String title) {
		super(context);
		this.title = title;
	}

	public DialogDateSet_Two(Context context, String title, Calendar calStart,
			Calendar calStop) {
		super(context);
		this.title = title;
		// this.calStart = calStart;
		// this.calStop = calStop;
		this.setTime(calStart, calStop);

	}

	public void setTime(Calendar calStart, Calendar calStop) {
		this.calStart = calStart;
		this.calStop = calStop;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawableResource(R.color.lib_transparent);
		setContentView(R.layout.lib_dialogdatesettwo_cv);
		if (title != null) {
			setTitle(title);
		}
		btn_cancel = findViewById(R.id.button2);
		btn_cancel.setOnClickListener(btnListener);
		btn_setTime = findViewById(R.id.button1);
		btn_setTime.setOnClickListener(btnListener);
		dtwStart = findViewById(R.id.dateTimeWheel1);
		dtwStop = findViewById(R.id.dateTimeWheel2);
		dtwStart.setOnScrolledListen(onDateTimeWheelScrolled);
		dtwStop.setOnScrolledListen(onDateTimeWheelScrolled);

		/** 日期输入部分 */
		edtYear_Start = findViewById(R.id.editText1);
		edtMonth_Start = findViewById(R.id.editText2);
		edtDay_Start = findViewById(R.id.editText3);
		edtYear_Stop = findViewById(R.id.editText4);
		edtMonth_Stop = findViewById(R.id.editText5);
		edtDay_Stop = findViewById(R.id.editText6);
		edtYear_Start.addTextChangedListener(textWatcher);
		edtMonth_Start.addTextChangedListener(textWatcher);
		edtDay_Start.addTextChangedListener(textWatcher);
		edtYear_Stop.addTextChangedListener(textWatcher);
		edtMonth_Stop.addTextChangedListener(textWatcher);
		edtDay_Stop.addTextChangedListener(textWatcher);

	}

	/** 　当滚轮的值改变了之后，通知EditText进行更新 */
	private OnDateTimeWheelScrolled onDateTimeWheelScrolled = new OnDateTimeWheelScrolled() {

		@Override
		public void onDateTimeWheelScrolled(View dt, int type, int value) {

			if (dt == dtwStart) {
				if (type == Calendar.YEAR) {
					if (!edtYear_Start.getText().toString().equals(value + "")) {
						edtYear_Start.setText(value + "");
					}
				} else if (type == Calendar.MONTH) {
					if (!edtMonth_Start.getText().toString().equals(value + "")) {
						edtMonth_Start.setText(value + "");
					}
				} else if (type == Calendar.DAY_OF_MONTH) {
					if (!edtDay_Start.getText().toString().equals(value + "")) {
						edtDay_Start.setText(value + "");
					}
				}
			} else if (dt == dtwStop) {
				if (type == Calendar.YEAR) {
					if (!edtYear_Stop.getText().toString().equals(value + "")) {
						edtYear_Stop.setText(value + "");
					}
				} else if (type == Calendar.MONTH) {
					if (!edtMonth_Stop.getText().toString().equals(value + "")) {
						edtMonth_Stop.setText(value + "");
					}
				} else if (type == Calendar.DAY_OF_MONTH) {
					if (!edtDay_Stop.getText().toString().equals(value + "")) {
						edtDay_Stop.setText(value + "");
					}
				}
			}
		}
	};

	/**
	 * 当用户在某个EditText中输入了值，将该值同步到对应的滚轮上
	 */
	private TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			// 编辑的时候把EditText删光了，如果继续执行下去就会报类型转换错误
			// ""是没有办法转化为int的
			// 当然也可以在下面的valueOf()...加try{}catch{},那还不如这样来得优雅
			if (s.toString().equals("")) {
				return;
			}
			View v = DialogDateSet_Two.this.getCurrentFocus();
			// 尚未获取焦点
			if (v == null) {
				return;
			}
			// 找到是哪个EditText的值变了
			int viewId = v.getId();
			// 把值更到滚轮
			if (viewId == R.id.editText1) {
				int year = Integer.valueOf(edtYear_Start.getText().toString());
				dtwStart.setTheDate(year, -1, -1, -1, -1);
			} else if (viewId == R.id.editText2) {
				int mon = Integer.valueOf(edtMonth_Start.getText().toString());
				dtwStart.setTheDate(-1, mon, -1, -1, -1);
			} else if (viewId == R.id.editText3) {
				int day = Integer.valueOf(edtDay_Start.getText().toString());
				dtwStart.setTheDate(-1, -1, day, -1, -1);
			} else if (viewId == R.id.editText4) {
				int year = Integer.valueOf(edtYear_Stop.getText().toString());
				dtwStop.setTheDate(year, -1, -1, -1, -1);
			} else if (viewId == R.id.editText5) {
				int mon = Integer.valueOf(edtMonth_Stop.getText().toString());
				dtwStop.setTheDate(-1, mon, -1, -1, -1);
			} else if (viewId == R.id.editText6) {
				int day = Integer.valueOf(edtDay_Stop.getText().toString());
				dtwStop.setTheDate(-1, -1, day, -1, -1);
			}

		}
	};

	private View.OnClickListener btnListener = new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			if (arg0 == btn_cancel) {
				if (onClickListener != null) {
					onClickListener.onClick(DialogDateSet_Two.this,
							BUTTON_NEGATIVE);
				}
				dismiss();
			} else if (arg0 == btn_setTime) {
				int yearStart = Integer.valueOf(dtwStart.getYear());
				int monthStart = Integer.valueOf(dtwStart.getMonth()) - 1;
				int dayStart = Integer.valueOf(dtwStart.getDay());
				calStart.set(yearStart, monthStart, dayStart);

				int yearStop = Integer.valueOf(dtwStop.getYear());
				int monthStop = Integer.valueOf(dtwStop.getMonth()) - 1;
				int dayStop = Integer.valueOf(dtwStop.getDay());
				calStop.set(yearStop, monthStop, dayStop);

				if (onClickListener != null) {
					onClickListener.onClick(DialogDateSet_Two.this,
							BUTTON_POSITIVE);
				}
				dismiss();
			}
		}
	};

	@Override
	public void show() {
		super.show();

		int yearStart = calStart.get(Calendar.YEAR);
		int monthStart = calStart.get(Calendar.MONTH) + 1;
		int dayOfMonthStart = calStart.get(Calendar.DAY_OF_MONTH);
		this.dtwStart
				.setTheDate(yearStart, monthStart, dayOfMonthStart, -1, -1);
		edtYear_Start.setText(yearStart + "");
		edtMonth_Start.setText(monthStart + "");
		edtDay_Start.setText(dayOfMonthStart + "");

		int yearStop = calStop.get(Calendar.YEAR);
		int monthStop = calStop.get(Calendar.MONTH) + 1;
		int dayOfMonthStop = calStop.get(Calendar.DAY_OF_MONTH);
		this.dtwStop.setTheDate(yearStop, monthStop, dayOfMonthStop, -1, -1);
		edtYear_Stop.setText(yearStop + "");
		edtMonth_Stop.setText(monthStop + "");
		edtDay_Stop.setText(dayOfMonthStop + "");

		// 对话框出现500毫秒之后弹出软键盘,不加延时的话会因为界面还没有用绘制完成而无法弹出软键盘
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				((InputMethodManager) edtYear_Start.getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE))
						.showSoftInput(edtYear_Start, 0);
			}
		}, 500);

	}

	/** "设置"为按钮1 "取消"为按钮2 */
	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

}
