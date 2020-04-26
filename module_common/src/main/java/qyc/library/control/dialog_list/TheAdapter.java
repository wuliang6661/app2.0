package qyc.library.control.dialog_list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import synway.common.R;


class TheAdapter extends BaseAdapter {

	private String[] lists = null;
	private LayoutInflater layoutInflater;

	TheAdapter(Context context, String[] items) {
		this.lists = items;
		this.layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lists.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = getItemControls(parent);
		}

		setItemControls((TextView) convertView.getTag(), lists[position]);
		return convertView;
	}

	private void setItemControls(TextView textView, String str) {
		textView.setText(str);
	}

	public View getItemControls(ViewGroup parent) {
		View view = layoutInflater.inflate(R.layout.lib_dialoglist_listitem, parent, false);
		TextView textView = view.findViewById(R.id.textView1);
		view.setTag(textView);
		return view;
	}

	// public class ItemControls {
	// public TextView txtName;
	// public TextView txtUnReadCount;
	// public TextView txtMsg;
	// public TextView txtTime;
	// public ImageView imgHead;
	// public ImageView imgSendState;
	//
	// public View totalItem = null;
	// }

}
