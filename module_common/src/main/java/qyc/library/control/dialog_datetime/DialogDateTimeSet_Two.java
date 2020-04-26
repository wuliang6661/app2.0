package qyc.library.control.dialog_datetime;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import java.util.Calendar;

import qyc.library.control.datetime_wheel.DateTimeWheel;
import synway.common.R;

public class DialogDateTimeSet_Two extends Dialog {

	private Calendar calStart, calStop;
	private DateTimeWheel dtwStart, dtwStop;
	private String title = null;
	private Button btn_setTime, btn_cancel = null;
	private OnClickListener onClickListener = null;

	public DialogDateTimeSet_Two(Context context) {
		super(context);
	}

	public DialogDateTimeSet_Two(Context context, String title) {
		super(context);
		this.title = title;
	}

	public DialogDateTimeSet_Two(Context context, String title, Calendar calStart,
			Calendar calStop) {
		super(context);
		this.title = title;
		this.calStart = calStart;
		this.calStop = calStop;
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
		setContentView(R.layout.lib_dialogdatetimesettwo_cv);
		if (title != null) {
			setTitle(title);
		}
		btn_cancel = findViewById(R.id.button2);
		btn_cancel.setOnClickListener(btnListener);
		btn_setTime = findViewById(R.id.button1);
		btn_setTime.setOnClickListener(btnListener);
		dtwStart = findViewById(R.id.dateTimeWheel1);
		dtwStop = findViewById(R.id.dateTimeWheel2);
	}

	private View.OnClickListener btnListener = new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			if (arg0 == btn_cancel) {
				if (onClickListener != null) {
					onClickListener.onClick(DialogDateTimeSet_Two.this,
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
					onClickListener.onClick(DialogDateTimeSet_Two.this,
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

		int yearStop = calStop.get(Calendar.YEAR);
		int monthStop = calStop.get(Calendar.MONTH) + 1;
		int dayOfMonthStop = calStop.get(Calendar.DAY_OF_MONTH);
		this.dtwStop.setTheDate(yearStop, monthStop, dayOfMonthStop, -1, -1);
	}

	/** 设置为按钮1 取消为按钮2 */
	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

}
