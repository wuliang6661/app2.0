package synway.module_publicaccount.map.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import synway.module_interface.config.BaseUtil;
import synway.module_publicaccount.R;
import synway.module_publicaccount.map.Bean.GroupUserPoint;
import synway.module_publicaccount.map.Bean.UserPointBean;
import synway.module_publicaccount.push.DownLoadPic;

public class UserlistExpandableAdapter extends BaseExpandableListAdapter {
	private Context context;
	private ArrayList<GroupUserPoint> list_data;

	public UserlistExpandableAdapter(Context context) {
		this.context = context;
		this.list_data =new ArrayList<GroupUserPoint>();
	}


	private ParentViewHolder getParentViewHolder(View convertView) {
		ParentViewHolder holder = new ParentViewHolder();
		holder.ck = convertView.findViewById(R.id.checkBox);
		holder.groupname = convertView.findViewById(R.id.groupname);
		holder.usernum= convertView.findViewById(R.id.usernum);
		return holder;
	}
	private ChileViewHolder getViewHolder(View convertView) {
		ChileViewHolder holder = new ChileViewHolder();
		holder.ck = convertView.findViewById(R.id.checkBox);
		holder.userimage = convertView.findViewById(R.id.userimage);
		holder.username = convertView.findViewById(R.id.username);
		holder.pointnum= convertView.findViewById(R.id.pointnum);
		return holder;
	}
	private void setParentViewHolder(ParentViewHolder parentViewHolder,final int groupPosition) {
		parentViewHolder.groupname.setText(list_data.get(groupPosition).groupName+"("+list_data.get(groupPosition).list_child.size()+")");
		parentViewHolder.ck.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (list_data.get(groupPosition).ifcheck) {
					list_data.get(groupPosition).ifcheck = false;
					for(UserPointBean userPointBean:list_data.get(groupPosition).list_child){
						userPointBean.isCheck=false;
					}
				} else {
					list_data.get(groupPosition).ifcheck = true;
					for(UserPointBean userPointBean:list_data.get(groupPosition).list_child){
						userPointBean.isCheck=true;
					}
				}
				refresh();
			}
		});
		parentViewHolder.ck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                list_data.get(groupPosition).ifcheck = list_data.get(groupPosition).ifcheck;
			}
		});
		parentViewHolder.ck.setChecked(list_data.get(groupPosition).ifcheck);
	}


	public void refresh() {

		this.notifyDataSetChanged();
	}


//子目录
	private void setViewHolder(final ChileViewHolder holder, final UserPointBean obj, final int groupPosition) {
		String picname= DownLoadPic.getImgName(obj.picurl);
		String path = getPath(picname);
		Drawable drawable = Drawable.createFromPath(path);
		if (drawable == null) {
			holder.userimage.setImageResource(R.drawable.defaultlocation);
		}else{
			holder.userimage.setImageDrawable(drawable);
		}
		holder.username.setText(obj.username);
		if (obj.points != null) {
			holder.pointnum.setText(obj.points.get(0).time);
		}else{
			holder.pointnum.setText("");
		}
		holder.ck.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (obj.isCheck) {
					obj.isCheck = false;
					list_data.get(groupPosition).ifcheck=false;
				} else {
					obj.isCheck = true;
					Boolean ifallnocheck=false;//是否有没被选中的
					for(UserPointBean userPointBean:list_data.get(groupPosition).list_child){//循环该群组中所有人是否都没有被选中
						if(!userPointBean.isCheck){//如果有没被选中的
							ifallnocheck=true;
						}
					}
                    list_data.get(groupPosition).ifcheck = !ifallnocheck;
				}
				refresh();
			}
		});
		holder.ck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                obj.isCheck = obj.isCheck;
			}
		});
		holder.ck.setChecked(obj.isCheck);
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		return true;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getGroupView(int groupPosition, boolean arg1, View arg2, ViewGroup arg3) {
		LayoutInflater inflater = LayoutInflater.from(context);

		if (arg2 == null) {
			arg2 = inflater.inflate(R.layout.model_public_account_expandablelistviw_group, null);
			ParentViewHolder viewHolder = getParentViewHolder(arg2);
			setParentViewHolder(viewHolder, groupPosition);
			arg2.setTag(viewHolder);

		} else {
			ParentViewHolder holder = (ParentViewHolder) arg2.getTag();
			setParentViewHolder(holder, groupPosition);
		}

		return arg2;
	}
	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public int getGroupCount() {//获取当前有几个组
		return list_data.size();
	}

	@Override
	public Object getGroup(int groupPosition) {//获取当前分组群组的内容
		return list_data.get(groupPosition);
	}

	@Override
	public int getChildrenCount(int arg0) {//获取群组的长度
		return list_data.get(arg0).list_child.size();
	}

	@SuppressLint("InflateParams")
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean arg2, View convertView, ViewGroup arg4) {
		LayoutInflater inflater = LayoutInflater.from(context);
		UserPointBean userPointBean=list_data.get(groupPosition).list_child.get(childPosition);
		ChileViewHolder viewHolder=null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.model_public_account_expandablelistview_child, null);
			viewHolder = getViewHolder(convertView);
			setViewHolder(viewHolder, userPointBean,groupPosition);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ChileViewHolder) convertView.getTag();
		}
		setViewHolder(viewHolder, userPointBean,groupPosition);
		return convertView;
	}
	class ChileViewHolder {
		private CheckBox ck = null;
		private ImageView userimage = null;
		private TextView username = null,pointnum=null;
	}

	class ParentViewHolder {
		private CheckBox ck = null;
		private TextView groupname = null,usernum=null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return list_data.get(groupPosition).list_child.get(childPosition);
	}


	public UserPointBean getChildbyposition(int arg0, int arg1) {
		return list_data.get(arg0).list_child.get(arg1);
	}
	private String getPath(String id) {
		return BaseUtil.FILE_HEAD_IMG_THU + "/" + id;
	}
	public void resetMlist(ArrayList<GroupUserPoint> dataList) {
		if (this.list_data.size() >= 0) {
			this.list_data.clear();
		}
		this.list_data = dataList;
	}
	public ArrayList<GroupUserPoint> getMlist() {
		return list_data;
	}
}
