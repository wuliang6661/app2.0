package synway.module_publicaccount.public_chat.ring;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import synway.module_publicaccount.R;


@SuppressLint({ "NewApi", "UseSparseArrays" })
 class RingAdapter extends BaseAdapter {

	public List<String> ringList;
	Context mContext;
	public Cursor cursor;
	public RingtoneManager rm;
	public Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
	public ViewHolder mHodler;
	public ListView ringView;
	public int index;
	public boolean firstItemState = true;

	/**
	 * 构造方法，index参数作为记录所选铃声的position传入数据库记录并调取。
	 */
	public RingAdapter(Context context, int index) {
		this.mContext = context;
		this.index = index;
		if (firstItemState) {
			firstItemState = false;
			map.put(index, true);
		}
		getRing();
	}

	
	public void back(int last){
		this.index=last;
		if (firstItemState) {
			firstItemState = false;
			map.clear();
			map.put(index, true);
		}
	}
	
	public void reset(){
		this.notifyDataSetChanged();
	}
	
	public void getRing() {
		/* 新建一个arraylist来接收从系统中获取的短信铃声数据 */
		ringList = new ArrayList<String>();
		/* 添加“跟随系统”选项 */
		ringList.add("跟随系统");
		/* 获取RingtoneManager */
		rm = new RingtoneManager(mContext);
		/* 指定获取类型为短信铃声 */
		rm.setType(RingtoneManager.TYPE_NOTIFICATION);
		/* 创建游标 */
		cursor = rm.getCursor();
		/* 游标移动到第一位，如果有下一项，则添加到ringlist中 */
		if (cursor.moveToFirst()) {
			do { // 游标获取RingtoneManager的列inde x
				ringList.add(cursor
						.getString(RingtoneManager.TITLE_COLUMN_INDEX));
			} while (cursor.moveToNext());
		}
	}

	@Override
	public int getCount() {
		return ringList.size();
	}

	@Override
	public Object getItem(int position) {
		return ringList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.ring_setting_child, null);
			mHodler = new ViewHolder(convertView);
			convertView.setTag(mHodler);
		} else {
			/* 不为空则直接使用已有的封装类 */
			mHodler = (ViewHolder) convertView.getTag();
		}
		/* 设定按钮背景图 */
		mHodler.iv
				.setBackgroundResource(map.get(position) == null ? R.drawable.pressed
						: R.drawable.checked);
		mHodler.tv.setText(ringList.get(position));
		return convertView;
	}

	public static class ViewHolder {
		TextView tv;
		ImageView iv;

		public ViewHolder(View v) {
			/* 组件初始化 */
			this.tv = v.findViewById(R.id.select_imagebtn_ring_tv);
			this.iv = v.findViewById(R.id.select_imagebtn_btn);
		}

	}

}
