package synway.module_publicaccount.publiclist;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import synway.module_interface.config.BaseUtil;
import synway.module_publicaccount.R;


/**
 * Created by QSJH on 2016/6/23 0023.
 */
public class FocusedAccountAdapter extends BaseAdapter {
    private ArrayList<Obj_PublicAccount> objList = null;
    private HashMap<String, Obj_PublicAccount> hashMap = null;
    private LayoutInflater inflater = null;

    public FocusedAccountAdapter(Context context) {
        objList = new ArrayList<Obj_PublicAccount>();
        hashMap = new HashMap<String, Obj_PublicAccount>();
        inflater = LayoutInflater.from(context);
    }

    public void reset(ArrayList<Obj_PublicAccount> list) {
        this.objList.clear();
        this.objList = list;

        this.hashMap.clear();
        for (Obj_PublicAccount objP : list) {
            hashMap.put(objP.ID, objP);
        }

    }

    public ArrayList<String> getFocusedList() {
        ArrayList<String> list = new ArrayList<>();
        for (Obj_PublicAccount obj : objList) {
            if (obj.isChecked) {

                list.add(obj.ID);
            }
        }
        return list;
    }

    /**
     * 取List里面的其中一段，根据ListView提供的topIndex和bottomIndex
     */
    public List<Obj_PublicAccount> sub(int topIndex, int bottomIndex) {
        // 根据注释，list的sub函数，end是不包括的。
        // 而我们是start和end都包括的，因此这里去取end要+1
        return objList.subList(topIndex, bottomIndex + 1);
    }

    public Obj_PublicAccount getObjByID(String id) {
        return hashMap.get(id);
    }

    public void refresh() {
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return objList.size();
    }

    @Override
    public Object getItem(int position) {
        return objList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        if (!objList.get(position).isItem) {
            return false;// 表示不能点击
        }
        return super.isEnabled(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Obj_PublicAccount obj = objList.get(position);
        ViewHolder holder = null;
        if (null == convertView) {
            convertView = inflater.inflate(
                    R.layout.model_focus_account_item, parent, false);
            holder = getHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        initHolder(holder, obj);

        return convertView;
    }

    private ViewHolder getHolder(View convertView) {
        ViewHolder holder = new ViewHolder();
        holder.relLayout = convertView.findViewById(R.id.relativeLayout1);
        holder.imgvHead = convertView.findViewById(R.id.imageView1);
        holder.tvText = convertView.findViewById(R.id.textView1);
        holder.imageView = convertView.findViewById(R.id.imageView2);
        return holder;
    }

    private void initHolder(ViewHolder holder, Obj_PublicAccount obj) {
        if (obj.isItem) {
            holder.relLayout.setBackgroundResource(R.drawable.alpha);
            holder.imgvHead.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.VISIBLE);

            String path = getPublicName(obj.ID);
            Drawable drawable = Drawable.createFromPath(path);
            if (null != drawable) {
                holder.imgvHead.setImageDrawable(drawable);
            } else {
                holder.imgvHead
                        .setImageResource(R.drawable.contact_public_account_png);
            }

            if (obj.isChecked) {
                holder.imageView.setImageResource(R.drawable.group_item_bg_check_png);
            } else {
                holder.imageView.setImageResource(R.drawable.group_item_bg_uncheck_png);
            }
            holder.tvText.setText(obj.name);
        } else {
            holder.relLayout.setBackgroundResource(R.color.mgray_light);
            holder.imgvHead.setVisibility(View.GONE);
            holder.tvText.setText(obj.name);
            holder.imageView.setVisibility(View.GONE);
        }

    }

    private class ViewHolder {
        RelativeLayout relLayout = null;
        ImageView imgvHead = null;
        TextView tvText = null;
        ImageView imageView = null;
    }

    private String getPublicName(String id) {
        return BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + id;
    }
}
