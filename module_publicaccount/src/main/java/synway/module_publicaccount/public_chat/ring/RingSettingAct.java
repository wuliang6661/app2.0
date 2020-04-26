package synway.module_publicaccount.public_chat.ring;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import qyc.library.tool.switchbutton.SwitchButton;
import synway.module_publicaccount.Main;
import synway.module_publicaccount.R;
import synway.module_publicaccount.db.table_util.Table_PublicConfigMsg;
//import synway.osc.app.MainApp;
//import synway.osc.db.SQLIteHelp;
//import synway.osc.db.table_util.Table_UserConfig;
import synway.module_publicaccount.public_chat.ring.RingAdapter.ViewHolder;
import synway.module_publicaccount.until.StringUtil;
//import synway.osc.mine.ring.SynUpLoadRing.OnUpLoadRing;

public class RingSettingAct extends Activity {

	//	private SynUpLoadRing loadRing = null;
	public final static String MUTE = "Mute";
	private RingAdapter mAdapter;
	private ListView listView;
	// private Button sureBtn;
	// private SQLiteDatabase db = null;
	private SQLiteOpenHelper sqliteHelp = null;
	private String Target = "";// 目标ID
//	private String ringuri = "";

//	private int secondlastindex = 0;
//	private int lastindex = 0;

//	private Dialog dialogProgress = null;

	private RingtoneManager rm;
	private Ringtone lastRingtone;//记录最后一个Ringtone
	private Ringtone lastDefaultRingtone;//记录最后一个Ringtone

	private SwitchButton switchButtonVibrate;
	private SwitchButton switchButtonSound;

	private int isVibrateOpen = 1;
	private String ringUri = null ;
	private Boolean isSoundOpen = true;
	private int position = 0;

	@Override
	protected void onDestroy() {
//		if (loadRing != null) {
//			loadRing.stop();
//		}
		stopLastRingtone();
		stopLastDefaultRingtone();
		super.onDestroy();
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pa_ring_setting_activity);
		Intent intent = getIntent();
		Target = intent.getStringExtra("ID");
		rm = new RingtoneManager(RingSettingAct.this);
		rm.setType(RingtoneManager.TYPE_NOTIFICATION);
		rm.getCursor();
		initDate();
		TextView lblTitle = findViewById(R.id.lblTitle);
		lblTitle.setText("设置铃声与震动");// "这里显示标题"
		ImageButton btn_back = findViewById(R.id.back);
		btn_back.setOnClickListener(clickListener);
		listView = findViewById(R.id.ring_lv);
		switchButtonVibrate = findViewById(R.id.swbt_vibrate);
		switchButtonVibrate.setOnCheckedChangeListener(onCheckedChangeListenerVibrate);
		switchButtonSound = findViewById(R.id.swbt_sound);
		switchButtonSound.setOnCheckedChangeListener(onCheckedChangeListenerSound);

		switchButtonVibrate.setChecked(isVibrateOpen==1);
		switchButtonSound.setChecked(!MUTE.equals(ringUri));



		if(!MUTE.equals(ringUri)){//不静音才显示铃声选择列表
			listView.setVisibility(View.VISIBLE);
		}
//		int p = getList();
//		lastindex = p + 1;
		mAdapter = new RingAdapter(this, position + 1);
		listView.setAdapter(mAdapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setOnItemClickListener(mOnItemClickListener);

		/* 初始化返回按钮和保存按钮 */
		// Button backBtn = (Button) findViewById(R.id.back_btn);
		// sureBtn = (Button) findViewById(R.id.sure_btn);
		// backBtn.setOnClickListener(mOnClickListener);
		// sureBtn.setOnClickListener(mOnClickListener);

	}

	private void initDate() {
		getIsVibrateOpen();
		getRingUri();
	}

	//获取铃声uri
	private void getRingUri() {
		String uri = "";
		sqliteHelp = Main.instance().moduleHandle.getSQLiteHelp();
		// db = SQLiteDatabase.openOrCreateDatabase(dbFilePath, null);
		String sql = "select * from " + Table_PublicConfigMsg._TABLE_NAME + " where "
				+ Table_PublicConfigMsg.PAC_ID + "='" + Target + "'";
		Log.i("lmly3", "sql:" + sql);
		Cursor cursor = sqliteHelp.getReadableDatabase().rawQuery(sql, null);
		if (cursor.moveToNext()) {
			uri = cursor.getString(cursor
					.getColumnIndex(Table_PublicConfigMsg.PAM_RingUri));
			Log.i("lmly3", "uri:" + uri);
		}
		if (!StringUtil.isEmpty(uri)) {
//			RingtoneManager rm = new RingtoneManager(RingSettingAct.this);
//			rm.setType(RingtoneManager.TYPE_NOTIFICATION);
//			rm.getCursor();
			ringUri = uri;
			if(MUTE.equals(ringUri)){//静音
				position = -1;
				isSoundOpen = false;
			}else{
				position = rm.getRingtonePosition(Uri.parse(uri));
			}
		}else{//默认
			position = -1;
		}
		cursor.close();
	}

	//获取来电是否震动
	private void getIsVibrateOpen() {
		sqliteHelp = Main.instance().moduleHandle.getSQLiteHelp();
		String sql = "select * from " + Table_PublicConfigMsg._TABLE_NAME + " where "
				+ Table_PublicConfigMsg.PAC_ID + "='" + Target + "'";
		Log.i("lmly3", "sql:" + sql);
		Cursor cursor = sqliteHelp.getReadableDatabase().rawQuery(sql, null);
		if (cursor.moveToNext()) {
			isVibrateOpen = cursor.getInt(cursor
					.getColumnIndex(Table_PublicConfigMsg.PAM_IsOpenVibrate));
			Log.i("lmly3", "uri:" + isVibrateOpen);
		}
		cursor.close();
	}

	/**
	 * 停止播放最近一次点击的铃声(stop()内部释放了播放器，防止内存泄漏)
	 */
	private void stopLastRingtone(){
		if (lastRingtone != null) {
			lastRingtone.stop();
			lastRingtone = null;
		}
	}

	/**
	 * 停止播放最近一次点击的默认铃声(stop()内部释放了播放器，防止内存泄漏)
	 */
	private void stopLastDefaultRingtone(){
		if (lastDefaultRingtone != null) {
			lastDefaultRingtone.stop();
			lastDefaultRingtone = null;
		}
	}

	CompoundButton.OnCheckedChangeListener onCheckedChangeListenerVibrate = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(isChecked){
				isVibrateOpen = 1;
			}else{
				isVibrateOpen = 0;
			}
		}
	};
	CompoundButton.OnCheckedChangeListener onCheckedChangeListenerSound = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			isSoundOpen = isChecked;
			if(isChecked){
				listView.setVisibility(View.VISIBLE);
			}else{
				listView.setVisibility(View.GONE);
			}

		}
	};

	/**
	 * 获取出之前设置的铃声。
	 *
	 * @return 之前铃声对应的索引
	 */
	private int getList() {
		String uri = "";
		int p = 0;

		//String dbFilePath = BaseUtil.getDBPath(RingSettingAct.this);

		sqliteHelp = Main.instance().moduleHandle.getSQLiteHelp();
		// db = SQLiteDatabase.openOrCreateDatabase(dbFilePath, null);
		String sql = "select * from " + Table_PublicConfigMsg._TABLE_NAME + " where "
				+ Table_PublicConfigMsg.PAC_ID + "='" + Target + "'";
		Log.i("lmly3", "sql:" + sql);
		Cursor cursor = sqliteHelp.getWritableDatabase().rawQuery(sql, null);
		if (cursor.moveToNext()) {
			uri = cursor.getString(cursor
					.getColumnIndex(Table_PublicConfigMsg.PAM_RingUri));
			Log.i("lmly3", "uri:" + uri);
		}
		if (!StringUtil.isEmpty(uri)) {
//			RingtoneManager rm = new RingtoneManager(RingSettingAct.this);
//			rm.setType(RingtoneManager.TYPE_NOTIFICATION);
//			rm.getCursor();
			ringUri = uri;
			p = rm.getRingtonePosition(Uri.parse(uri));
		}
		cursor.close();
		// db.close();

		return p;
	}

	/** listView的按钮点击事件 */
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
								long id) {

//			secondlastindex = position;
			// positions = position;
			ViewHolder mHolder = new ViewHolder(parent);
			/* 设置Imageview不可被点击 */
			mHolder.iv.setClickable(false);
			/* 清空map */
			mAdapter.map.clear();
			// mAdapter.map.put(position, 1);
			/* 将所点击的位置记录在map中 */
			mAdapter.map.put(position, true);
			/* 刷新数据 */
			mAdapter.notifyDataSetChanged();
			/* 判断位置不为0则播放的条目为position-1 */
			if (position != 0) {
				try {
					stopLastDefaultRingtone();

					ringUri = rm.getRingtoneUri(position - 1).toString();

					lastRingtone = rm.getRingtone(position - 1);//同一个rm情况下，内部会stop上一次播放的铃声
					lastRingtone.play();

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else/* position为0是跟随系统，先得到系统所使用的铃声，然后播放 */
			{
				stopLastRingtone();
				stopLastDefaultRingtone();

				Uri uri = RingtoneManager.getActualDefaultRingtoneUri(
						RingSettingAct.this, RingtoneManager.TYPE_NOTIFICATION);
				ringUri = uri.toString();
				// saveRing(ringuri);
				lastDefaultRingtone = RingtoneManager.getRingtone(RingSettingAct.this, uri);
				lastDefaultRingtone.play();
			}
		}

	};

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			saveRing();
		}
	};

	@Override
	public void onBackPressed() {
		saveRing();
		super.onBackPressed();
	}

	private void saveRing() {
		// String dbFilePath = BaseUtil.getDBPath(RingSettingAct.this);
		// db = SQLiteDatabase.openOrCreateDatabase(dbFilePath, null);
		String ringuriTemp = isSoundOpen ? (MUTE.equals(ringUri) ? "" : ringUri) : MUTE;

		String sql = "select * from " + Table_PublicConfigMsg._TABLE_NAME + " where "
				+ Table_PublicConfigMsg.PAC_ID+ "='" + Target + "'";
		Cursor cursor = sqliteHelp.getReadableDatabase().rawQuery(sql, null);
		if (cursor.moveToNext()) {
			int count = cursor.getCount();
			if (count != 0) {
				sql = "update " + Table_PublicConfigMsg._TABLE_NAME + " set "
						+ Table_PublicConfigMsg.PAM_RingUri + "='" + ringuriTemp
						+ "' , "+Table_PublicConfigMsg.PAM_IsOpenVibrate+"= " + isVibrateOpen
						+ " where " + Table_PublicConfigMsg.PAC_ID + "='" + Target
						+ "'";
				sqliteHelp.getWritableDatabase().execSQL(sql);
			}

		} else {
			sql = "insert into " + Table_PublicConfigMsg._TABLE_NAME + " ("
					+ Table_PublicConfigMsg.PAC_ID + "," + Table_PublicConfigMsg.PAM_RingUri+ "," + Table_PublicConfigMsg.PAM_IsOpenVibrate
					+ ") values ('" + Target + "','" + ringuriTemp + "'," +isVibrateOpen+ ")";
			sqliteHelp.getWritableDatabase().execSQL(sql);
		}
		cursor.close();
		// db.close();
//		lastindex = secondlastindex;
		RingSettingAct.this.finish();
	}

}
