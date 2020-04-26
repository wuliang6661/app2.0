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

public class DialogDateSet extends Dialog {

	private Calendar cal = null;

	private DateTimeWheel dtw = null;

	private Button btn_setTime, btn_cancel = null;

	private EditText edtYear, edtMonth, edtDay;

	private OnDdtDismissListener dismissListener = null;

	public DialogDateSet(Context context) {
		super(context);
	}

	public void setCalendar(Calendar cal) {
		this.cal = cal;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.lib_dialogdateset_cv);

		btn_cancel = findViewById(R.id.button2);
		btn_cancel.setOnClickListener(btnListener);
		btn_setTime = findViewById(R.id.button1);
		btn_setTime.setOnClickListener(btnListener);
		dtw = findViewById(R.id.dateTimeWheel1);
		dtw.setOnScrolledListen(onDateTimeWheelScrolled);
		edtYear = findViewById(R.id.editText1);
		edtMonth = findViewById(R.id.editText2);
		edtDay = findViewById(R.id.editText3);
		edtYear.addTextChangedListener(textWatcher);
		edtMonth.addTextChangedListener(textWatcher);
		edtDay.addTextChangedListener(textWatcher);
	}

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
			View v = DialogDateSet.this.getCurrentFocus();
			// 尚未获取焦点
			if (v == null) {
				return;
			}
			// 找到是哪个EditText的值变了
			int viewId = v.getId();
			// 把值更到滚轮
			if (viewId == R.id.editText1) {
				int year = Integer.valueOf(edtYear.getText().toString());
				dtw.setTheDate(year, -1, -1, -1, -1);
			} else if (viewId == R.id.editText2) {
				int mon = Integer.valueOf(edtMonth.getText().toString());
				dtw.setTheDate(-1, mon, -1, -1, -1);
			} else if (viewId == R.id.editText3) {
				int day = Integer.valueOf(edtDay.getText().toString());
				dtw.setTheDate(-1, -1, day, -1, -1);
			}
		}
	};

	/** 　当滚轮的值改变了之后，通知EditText进行更新 */
	private OnDateTimeWheelScrolled onDateTimeWheelScrolled = new OnDateTimeWheelScrolled() {

		@Override
		public void onDateTimeWheelScrolled(View dt, int type, int value) {

			if (type == Calendar.YEAR) {
				if (!edtYear.getText().toString().equals(value + "")) {
					edtYear.setText(value + "");
				}
			} else if (type == Calendar.MONTH) {
				if (!edtMonth.getText().toString().equals(value + "")) {
					edtMonth.setText(value + "");
				}
			} else if (type == Calendar.DAY_OF_MONTH) {
				if (!edtDay.getText().toString().equals(value + "")) {
					edtDay.setText(value + "");
				}
			}
		}
	};

	private View.OnClickListener btnListener = new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			if (arg0 == btn_cancel) {
				dismiss();
				if (dismissListener != null) {
					dismissListener.onDdtDismiss(false);
				}
			} else if (arg0 == btn_setTime) {
				int year = Integer.valueOf(dtw.getYear());
				int month = Integer.valueOf(dtw.getMonth()) - 1;
				int day = Integer.valueOf(dtw.getDay());
				cal.set(year, month, day);
				dismiss();
				if (dismissListener != null) {
					dismissListener.onDdtDismiss(true);
				}
			}
		}
	};

	@Override
	public void show() {
		super.show();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day_of_month = cal.get(Calendar.DAY_OF_MONTH);
		this.dtw.setTheDate(year, month, day_of_month, -1, -1);

		edtYear.setText(year + "");
		edtMonth.setText(month + "");
		edtDay.setText(day_of_month + "");

		// 对话框出现500毫秒之后弹出软键盘,不加延时的话会因为界面还没有用绘制完成而无法弹出软键盘
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				((InputMethodManager) edtYear.getContext().getSystemService(
						Context.INPUT_METHOD_SERVICE))
						.showSoftInput(edtYear, 0);
			}
		}, 500);

	}

	public void setOnDismissListener(OnDdtDismissListener dismissListener) {
		this.dismissListener = dismissListener;
	}

}
