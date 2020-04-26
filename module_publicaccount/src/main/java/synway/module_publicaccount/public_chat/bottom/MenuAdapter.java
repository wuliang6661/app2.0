package synway.module_publicaccount.public_chat.bottom;

import java.util.ArrayList;

import synway.module_publicaccount.R;
import synway.module_publicaccount.public_chat.Obj_Menu;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MenuAdapter extends BaseAdapter {

	private ArrayList<Obj_Menu> firstList = null;
	private LayoutInflater inflater = null;

	private ArrayList<Obj_Menu> secondList = null;

	public MenuAdapter(Context context) {
		this.firstList = new ArrayList<Obj_Menu>();
		this.inflater = LayoutInflater.from(context);

		this.secondList = new ArrayList<Obj_Menu>();
	}
//暂无多级菜单，所以先注释
//	public void setSecondList(ArrayList<Obj_Menu> secondList) {
//		this.secondList.clear();
//		this.secondList = secondList;
//	}

	public ArrayList<Obj_Menu> getSecondMenu(String guid) {
		ArrayList<Obj_Menu> arrayList = new ArrayList<Obj_Menu>();
		for (int i = 0; i < secondList.size(); i++) {
			if (guid.equals(secondList.get(i).menuFather)) {
				arrayList.add(secondList.get(i));
			}
		}
		return arrayList;
	}

	public void reset(ArrayList<Obj_Menu> arrayList) {
		this.firstList.clear();
		this.firstList = arrayList;
	}

	public void refresh() {
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return firstList.size();
	}

	@Override
	public Object getItem(int position) {
		return firstList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		Obj_Menu menu = firstList.get(position);
		if (menu.menuType == 0) {
			return LAYOUT_TYPE.MENU;
		} else {
			return LAYOUT_TYPE.CLICK_OR_VIEW;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	private static class LAYOUT_TYPE {
		public final static int MENU = 1;
		public final static int CLICK_OR_VIEW = 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		Obj_Menu obj_Menu = firstList.get(position);
		if (null == convertView) {
			switch (getItemViewType(position)) {
			case LAYOUT_TYPE.MENU:
				convertView = inflater.inflate(
						R.layout.model_public_account_chat_menu_item, parent, false);
				break;
			case LAYOUT_TYPE.CLICK_OR_VIEW:
				convertView = inflater.inflate(
						R.layout.model_public_account_chat_menu_item_nonsub, parent,
						false);
				break;
			}
			// convertView = inflater.inflate(
			// R.layout.model_public_account_chat_menu_item, parent, false);
			viewHolder = initHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		WindowManager wm = (WindowManager)convertView.getContext().getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		 int everywidth=width/(firstList.size());
		int height=wm.getDefaultDisplay().getHeight();
		viewHolder.layout.setLayoutParams(new LinearLayout.LayoutParams(everywidth, height/13));
		setHolder(viewHolder, obj_Menu);
		return convertView;
	}

	private ViewHolder initHolder(View convertView) {
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.tvName = convertView.findViewById(R.id.textView1);
		viewHolder.imgv = convertView.findViewById(R.id.imageView1);
		viewHolder.layout= convertView.findViewById(R.id.pubic_account_chat_layout);
		return viewHolder;
	}

	private void setHolder(ViewHolder viewHolder, Obj_Menu obj_Menu) {
		viewHolder.tvName.setText(obj_Menu.menuName);
	}

	private class ViewHolder {
		TextView tvName = null;
		@SuppressWarnings("unused")
		ImageView imgv = null;
		View layout=null;
	}

}