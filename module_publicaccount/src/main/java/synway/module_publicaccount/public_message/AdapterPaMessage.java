package synway.module_publicaccount.public_message;

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
import java.util.regex.Pattern;
import synway.module_interface.config.BaseUtil;
import synway.module_publicaccount.R;

public class AdapterPaMessage extends BaseAdapter {

    private ArrayList<ObjPaMessage> listObj = new ArrayList<ObjPaMessage>();
    private HashMap<String, ObjPaMessage> mapObj = new HashMap<String, ObjPaMessage>();

    public LayoutInflater lif = null;

    private RelativeLayout.LayoutParams layoutParams1 = null;
    private RelativeLayout.LayoutParams layoutParams2 = null;

    private Context context;
    public static final Pattern AT_RANGE = Pattern.compile("@[\\u4e00-\\u9fa5]+");

    public AdapterPaMessage(Context context) {
        lif = LayoutInflater.from(context);
        initLayoutParams(context);
        this.context = context;
    }


    private void initLayoutParams(Context context) {
        layoutParams1 = new RelativeLayout.LayoutParams(
                dip2px(context, 18), dip2px(context, 18));
//		layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_END);
        layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        layoutParams2 = new RelativeLayout.LayoutParams(
                dip2px(context, 10), dip2px(context, 10));
//		layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_END);
        layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_TOP);
    }

    /**
     * 重新设置适配器里显示的成员
     */
    public void reset(ArrayList<ObjPaMessage> list) {
        this.listObj = list;
        list2HashMap(list);// 也存一份到HashMap里
    }

    private void list2HashMap(ArrayList<ObjPaMessage> listObj) {
        mapObj.clear();
        for (int i = 0; i < listObj.size(); i++) {
            ObjPaMessage obj = listObj.get(i);
            mapObj.put(obj.id, obj);
        }
    }

    /**
     * 取List里面的其中一段，根据ListView提供的topIndex和bottomIndex
     */
    public List<ObjPaMessage> sub(int topIndex, int bottomIndex) {
        // 根据注释，list的sub函数，end是不包括的。
        // 而我们是start和end都包括的，因此这里去取end要+1
        return listObj.subList(topIndex, bottomIndex + 1);
    }

    /**
     * 根据公众号ID，来获取最近公众号实体
     */
    public ObjPaMessage getObjFromID(String id) {
        return mapObj.get(id);
    }

//	/**
//	 * 将最近公众号在列表中置顶，或在顶部新增
//	 */
//	public void setObjTop(ObjLastContact obj) {
//		int index = listObj.indexOf(obj);
//		if (index == 0) {
//			return;
//		} else if (index < 0) {
//			mapObj.put(obj.id, obj);
//			listObj.add(0, obj);
//		} else {
//			listObj.remove(obj);
//			listObj.add(0, obj);
//		}
//	}
//
//	/**
//	 * 增加一个最近公众号,并放在顶部
//	 */
//	public void addObjTop(ObjLastContact obj) {
//		listObj.add(0, obj);
//		mapObj.put(obj.id, obj);
//	}

    public void receiveBC(ObjPaMessage obj) {

        int index = listObj.indexOf(obj);
        if (index < 0) {
            //不存在在最近公众号中，新建
            newItem(obj);
        } else {
            //存在，更新
            itemNewMsg(obj);
        }

    }

    /***
     * 添加全新Item
     *
     * @param obj
     */
    private void newItem(ObjPaMessage obj) {
        int freshEext = listObj.size();
        for (int i = 0; i < listObj.size(); i++) {
            if (listObj.get(i).topIndex == 0) {
                freshEext = i;
                break;
            }
        }

        listObj.add(freshEext, obj);
        mapObj.put(obj.id, obj);
    }

    /**
     * Item，收到新消息
     */
    private void itemNewMsg(ObjPaMessage obj) {
        //置顶项收到新消息
        if (obj.topIndex != 0) {
            return;
        }

        int freshExt = listObj.size();
        for (int i = 0; i < listObj.size(); i++) {
            if (listObj.get(i).topIndex == 0) {
                freshExt = i;
                break;
            }
        }

        int index = listObj.indexOf(obj);
        //位置==当前位置
        if (index == freshExt) {
            return;
        }

        listObj.remove(obj);
        listObj.add(freshExt, obj);
    }

    /**
     * 设置置顶
     */
    public void setTop(ObjPaMessage obj) {
        listObj.remove(obj);
        int topCount =0;
        for(int i=0;i<listObj.size();i++){
            if(listObj.get(i).topIndex!=0){
                topCount++;
            }else{
                break;
            }
        }
        listObj.add(topCount, obj);
    }

    /**
     * 取消置顶
     */
    public void cancelTop(ObjPaMessage obj) {
//		if(obj.topIndex==0){
//			return;
//		}
        int freshExt = listObj.size();
        for (int i = 0; i < listObj.size(); i++) {
            if (listObj.get(i).topIndex == 0) {
                freshExt = i;
                break;
            }
        }
        int index = listObj.indexOf(obj);
        //位置==当前位置
        if (index == freshExt) {
            return;
        }
        removeItem(obj);
        obj.topIndex = 0;
        listObj.add(freshExt - 1, obj);
        mapObj.put(obj.id, obj);
    }

    public void removeItem(ObjPaMessage obj) {
        listObj.remove(obj);
        mapObj.remove(obj.id);
    }

    @Override
    public int getCount() {
        return listObj.size();
    }

    @Override
    public Object getItem(int position) {
        return listObj.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = getItemControls(parent);
        }

        setItemControls((ItemControls) convertView.getTag(), listObj.get(position));

        return convertView;
    }

    private void setItemControls(ItemControls controls, ObjPaMessage obj) {

        // ByteArrayInputStream stream = new
        // ByteArrayInputStream(cur.getBlob(cur.getColumnIndex("express_img")));
        // controls.imgHead.setImageDrawable(Drawable.createFromStream(stream,
        // "img"));

        if (obj.topIndex == 0) {
            controls.totalItem.setBackgroundResource(R.drawable.item_click);
        } else {
            controls.totalItem.setBackgroundResource(R.drawable.lc_set_topcancel_item_click);
        }

        // 设置头像
        Drawable img = Drawable.createFromPath(BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + obj.id.trim());
        if (img == null) {
            controls.imgHead.setImageResource(R.drawable.contact_public_account_png);
        } else {
            controls.imgHead.setImageDrawable(img);
        }

        // 最后目标名
        controls.txtName.setText(obj.name);
        controls.txtBriefMsg.setText(obj.briefMsg);
        controls.txtTime.setText(ParseDate.parseDate(obj.showTime));
        // 未读消息数
        if (obj.unReadCount == 0) {
            controls.txtUnReadCount.setVisibility(View.INVISIBLE);

        } else if (obj.unReadCount == -1) {
            controls.txtUnReadCount.setLayoutParams(layoutParams2);
            controls.txtUnReadCount.setVisibility(View.VISIBLE);
            controls.txtUnReadCount.setText("");
        } else {
            controls.txtUnReadCount.setLayoutParams(layoutParams1);
            controls.txtUnReadCount.setVisibility(View.VISIBLE);
            if (obj.unReadCount > 99) {
                controls.txtUnReadCount.setText("99");
            } else {
                controls.txtUnReadCount.setText(String.valueOf(obj.unReadCount));
            }
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public void notifyDataSetChanged() {
        //Toast.makeText(context, "notifyDataSetChanged", Toast.LENGTH_LONG).show();

        super.notifyDataSetChanged();
    }

    public View getItemControls(ViewGroup parent) {
        View view = lif.inflate(R.layout.message_publicaccount_listitem, parent, false);
        ItemControls itemControls = new ItemControls();
        itemControls.txtName = view.findViewById(R.id.textView1);
        itemControls.txtTime = view.findViewById(R.id.textView3);
        itemControls.txtBriefMsg = view.findViewById(R.id.textView2);
        itemControls.txtUnReadCount = view.findViewById(R.id.textView4);
        itemControls.imgHead = view.findViewById(R.id.imageView1);
        itemControls.totalItem = view.findViewById(R.id.linearLayout1);
        view.setTag(itemControls);
        return view;
    }

    public class ItemControls {
        public TextView txtName;
        public TextView txtUnReadCount;
        public TextView txtTime;
        public TextView txtBriefMsg;
        public ImageView imgHead;
        public View totalItem = null;
    }
}
