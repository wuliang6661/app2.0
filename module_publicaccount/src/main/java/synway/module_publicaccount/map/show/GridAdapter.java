package synway.module_publicaccount.map.show;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import synway.module_publicaccount.R;

/**
 * Created by QSJH on 2016/4/27 0027.
 */
public class GridAdapter extends BaseAdapter {

    //定义一个flag来判断是否显示关注某人的标志
    private  boolean isShowFollowFlag;
    //定义一个ID进行判断，对关注的人增加关注标志
    private String followID;

    private Context context;
    private ArrayList<PicItem> msgList = new ArrayList<PicItem>();
    private int itemHeigh = 0;
    private int itemWidth = 0;

    //定义一个设置是否显示关注标志的方法
    public void setIsShowFollowFlag(boolean flag,String followID) {
        this.isShowFollowFlag = flag;
        this.followID = followID;
    }

    public void addItem(PicItem item) {
        msgList.add(item);
    }

    public void removeItem(PicItem item) {
        msgList.remove(item);
    }

    public void removeAll(){
        msgList.clear();
    }

    public GridAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return msgList.size();
    }

    @Override
    public Object getItem(int position) {
        return msgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        PicItem picItem = msgList.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.model_public_account_location_head_imgview, null);
            vh = new ViewHolder();
            convertView.setTag(vh);

            vh.iv_big = convertView.findViewById(R.id.imageView1);
            vh.iv_small = convertView.findViewById(R.id.imageView2);
            vh.tv_name = convertView.findViewById(R.id.textView1);

            vh.iv_follow = convertView.findViewById(R.id.imageView_follow);


        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.iv_big.setImageDrawable(picItem.bigPic);
        vh.iv_small.setImageDrawable(picItem.smallPic);
        vh.tv_name.setText(picItem.picName);

        vh.iv_follow.setImageResource(R.drawable.location_sharing_follow);



        if (isShowFollowFlag) {
            if (picItem.tag.toString().equals(followID)) {
                vh.iv_follow.setVisibility(View.VISIBLE);
            } else {
                vh.iv_follow.setVisibility(View.GONE);

            }

        } else {
            vh.iv_follow.setVisibility(View.GONE);
        }


        // 第一次需要显示View的时候测量行高和宽
        if (itemHeigh == 0 && itemWidth == 0) {
            initKeyTextView(convertView);
        }
        return convertView;
    }

    private static class ViewHolder {
        public TextView tv_name;
        public ImageView iv_big;
        public ImageView iv_small;

        //增加一个标注关注某人实时位置的小icon
        public ImageView iv_follow;
    }

    public static class PicItem {
        public PicItem() {
        }

        public PicItem(String picName, Drawable bigPic, Drawable smallPic) {
            this.picName = picName;
            this.bigPic = bigPic;
            this.smallPic = smallPic;
        }

        public String picName;
        public Drawable bigPic;
        public Drawable smallPic;



        public int state;
        public Object tag;

    }

    //======

    public void initKeyTextView(final View ll) {
        ViewTreeObserver vto2 = ll.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ll.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                itemHeigh = ll.getHeight();
                itemWidth = ll.getWidth();
                if (onItemMesure != null) {
                    onItemMesure.onItemMesure(itemHeigh, itemWidth);
                }
            }
        });
    }


    private OnItemMesure onItemMesure;

    public void setOnItemMesure(OnItemMesure onItemMesure) {
        this.onItemMesure = onItemMesure;
    }

    public void removeOnItemMesure() {
        this.onItemMesure = null;
    }

    public interface OnItemMesure {
        /**
         * 当这个GridView需要显示第一个Item的时候，这个方法会被回调，
         *
         * @param height GridView的行高
         * @param width  GridView的行宽
         */
        void onItemMesure(int height, int width);
    }


}
