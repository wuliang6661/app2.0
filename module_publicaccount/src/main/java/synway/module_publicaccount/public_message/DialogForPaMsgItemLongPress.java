package synway.module_publicaccount.public_message;

import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import qyc.library.tool.main_thread.MainThread;
import qyc.library.tool.main_thread.MainThread.Runnable_MainThread;
import synway.module_publicaccount.Main;
import synway.module_publicaccount.R;

public class DialogForPaMsgItemLongPress extends Dialog {

	private Context context = null;

	public DialogForPaMsgItemLongPress(Context context) {
		super(context);
		this.context = context;
	}

	private View topIndex1 = null;
	private View topIndex2 = null;
	private View delete = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_for_pamessage);

		topIndex1 = findViewById(R.id.linearLayout1);
		topIndex1.setOnClickListener(onClickListener);

		topIndex2 = findViewById(R.id.linearLayout3);
		topIndex2.setOnClickListener(onClickListener);

		delete = findViewById(R.id.linearLayout2);
		delete.setOnClickListener(onClickListener);

	}

	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

			if (null == lsn) {
				return;
			}

			if (v == topIndex1) {
				lsn.onTopIndex(paID);
				dismiss();
			} else if (v == topIndex2) {
				lsn.onCancelTopIndex(paID);
				dismiss();
			} else if (v == delete) {
				lsn.onDelete(paID);
				dismiss();
			}

		}
	};

	private String paID = null;

	public void setParams(String paID) {
		this.paID = paID;
		SQLiteOpenHelper sqliteHelp= Main.instance().moduleHandle.getSQLiteHelp();
		final int topIndex = PaMsgTopIndexDeal.getTopIndex(sqliteHelp.getWritableDatabase(), paID);

		MainThread.joinMainThread(new Runnable_MainThread() {
			@Override
			public void run() {
				if (topIndex == 0) {
					topIndex1.setVisibility(View.VISIBLE);
					topIndex2.setVisibility(View.GONE);
				} else {
					topIndex1.setVisibility(View.GONE);
					topIndex2.setVisibility(View.VISIBLE);
				}
			}
		});

		//db.close();
	}

	private IOnDialogItemClickLsn lsn = null;

	public void setLsn(IOnDialogItemClickLsn lsn) {
		this.lsn = lsn;
	}

	public void removeLsn() {
		this.lsn = null;
	}

	interface IOnDialogItemClickLsn {
		void onTopIndex(String paID);

		void onCancelTopIndex(String paID);

		void onDelete(String paID);
	}

}