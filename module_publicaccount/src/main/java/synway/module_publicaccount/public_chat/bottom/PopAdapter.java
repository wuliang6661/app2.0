package synway.module_publicaccount.public_chat.bottom;

import java.util.ArrayList;

import synway.module_publicaccount.R;
import synway.module_publicaccount.public_chat.Obj_Menu;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PopAdapter extends BaseAdapter {

	private LayoutInflater inflater = null;
	private ArrayList<Obj_Menu> arrayList = null;

	public PopAdapter(Context context) {
		this.inflater = LayoutInflater.from(context);
		this.arrayList = new ArrayList<Obj_Menu>();
	}

	public void reset(ArrayList<Obj_Menu> arrayList) {
		this.arrayList.clear();
		this.arrayList.addAll(arrayList);
	}

	public void refresh() {
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return arrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return arrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		Obj_Menu obj_Menu = arrayList.get(position);
		if (null == convertView) {
			convertView = inflater.inflate(
					R.layout.model_public_account_chat_menu_pop_item, parent, false);
			holder = getHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		setHolder(holder, obj_Menu);

		return convertView;
	}

	private ViewHolder getHolder(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.tvName = convertView.findViewById(R.id.textView1);
		return holder;
	}

	private void setHolder(ViewHolder holder, Obj_Menu obj_Menu) {
		holder.tvName.setText(obj_Menu.menuName);
	}

	private class ViewHolder {
		TextView tvName = null;
	}

}