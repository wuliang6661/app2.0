package synway.module_publicaccount.map.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import synway.module_interface.config.BaseUtil;
import synway.module_publicaccount.R;
import synway.module_publicaccount.map.Bean.UserPointBean;
import synway.module_publicaccount.push.DownLoadPic;
import android.view.View.OnClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
/**
 * Created by ysm on 2017/3/15.
 */

public class DynamiclocationAdapter extends BaseAdapter {

        private ArrayList<UserPointBean> mlist;
        private Context context;

        public DynamiclocationAdapter(Context context) {
            mlist = new ArrayList<>();
            this.context = context;
        }

        public void resetMlist(ArrayList<UserPointBean> dataList) {
            if (this.mlist.size() >= 0) {
                this.mlist.clear();
            }
            this.mlist = dataList;
        }

        public void refresh() {
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mlist.size();
        }



    @Override
        public Object getItem(int position) {
            return mlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            UserPointBean  obj = mlist.get(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.model_public_account_dynamic_item, null);
                holder = getViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
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
                holder.pointnum.setText(obj.points.size()+"个轨迹点");
            }else{
                holder.pointnum.setText(0+"个轨迹点");
            }
            holder.ck.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mlist.get(position).isCheck = !mlist.get(position).isCheck;
                }
            });
            holder.ck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mlist.get(position).isCheck = mlist.get(position).isCheck;
                }
            });
            holder.ck.setChecked(obj.isCheck);
            return convertView;
        }

        @NonNull
        private ViewHolder getViewHolder(View convertView) {
            ViewHolder holder = new ViewHolder();
            holder.ck = convertView.findViewById(R.id.checkBox);
            holder.userimage = convertView.findViewById(R.id.userimage);
            holder.username = convertView.findViewById(R.id.username);
            holder.pointnum= convertView.findViewById(R.id.pointnum);
            return holder;
        }

        private class ViewHolder {
            private CheckBox ck = null;
            private ImageView userimage = null;
            private TextView username = null,pointnum=null;
        }

        private String getPath(String id) {
            return BaseUtil.FILE_HEAD_IMG_THU + "/" + id;
        }

    public ArrayList<UserPointBean> getMlist() {
        return mlist;
    }
}
