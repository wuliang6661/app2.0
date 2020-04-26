package synway.module_publicaccount.public_chat.file_upload_for_camera.local_video;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import synway.module_publicaccount.R;


/**
 * Created by Mfh on 2017/1/9.
 */

public class ImageAndVideoAdapter extends BaseAdapter {

    private Set<String> mSelectImg = new HashSet<>();
    // 最大的可被选中的项数，默认1
    private int mMaxSelectNum = 1;
    // gridView的列数，默认3
    private int mGridViewColumn = 3;
    private String mDirPath;
    private List<String> mImgPaths;
    private List<Obj_item> mPaths;


    private LayoutInflater mInflater;
    private Context mContext;

    private int mScreenWidth;


    public ImageAndVideoAdapter(Context context, List<String> data, String dirPath) {
        this.mDirPath = dirPath;
        this.mImgPaths = data;
        mInflater = LayoutInflater.from(context);
        mContext = context;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
    }

    public ImageAndVideoAdapter(Context context, List<Obj_item> paths, List<String> data, String dirPath) {

        this.mPaths = paths;
        this.mDirPath = dirPath;
        this.mImgPaths = data;
        mSelectImg.addAll(data);
        mInflater = LayoutInflater.from(context);
        mContext = context;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
    }


    @Override
    public int getCount() {
        return mPaths.size();
    }

    @Override
    public Object getItem(int position) {
        return mPaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.album_local_video_griditem, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.mImg = convertView.findViewById(R.id.id_item_img_local);
            viewHolder.mSelect = convertView.findViewById(R.id.id_item_select_local);
            viewHolder.mDuration = convertView.findViewById(R.id.id_item_tv_duration);
            viewHolder.mSize = convertView.findViewById(R.id.id_item_tv_size);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //重置状态
        //viewHolder.mImg.setImageResource(R.drawable.album_pictures_no);
        viewHolder.mSelect.setImageResource(R.drawable.album_picture_unselected);
        viewHolder.mImg.setColorFilter(null);
        long duration = mPaths.get(position).duration;
        if (duration > 0L) {
            viewHolder.mDuration.setText(Utils.formatMediaDuration(duration));
        }
        long size = mPaths.get(position).size;
        if (size > 0L) {
            viewHolder.mSize.setText(Utils.formatFileSize(size));
        }

        viewHolder.mImg.setMaxWidth(mScreenWidth / mGridViewColumn);//这样可以在复用中节约内存，3为gridView中一行的item数

        Uri uri = Uri.fromFile(new File(mPaths.get(position).path));
        /** Glide加载视频缩略图 */
        Glide
                .with(mContext)
                .load(uri)
//                .skipMemoryCache(true)//跳过内存缓存，测试发现如果打开内存缓存，太吃内存
//                .centerCrop()
                //.thumbnail(0.1f)//先显示10%大小的缩略图，再显示原分辨率的视频截图
//                .dontAnimate()
//                .placeholder(R.drawable.album_pictures_no)
                .into(viewHolder.mImg);

        final String filePath = mPaths.get(position).path;

        viewHolder.mImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mItemOnclickListener != null) {
                    mItemOnclickListener.onClick(filePath);
                }
            }
        });


        viewHolder.mSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mSelectImg.contains(filePath)) {
                    Log.d("Mfh--->", "已经有这个视频了，mSelectImg-1");
                    //已经被选择
                    mSelectImg.remove(filePath);
                    viewHolder.mImg.setColorFilter(null);
                    //viewHolder.mImg.setColorFilter(Color.parseColor("#77000000"));
                    viewHolder.mSelect.setImageResource(R.drawable.album_picture_unselected);
                } else {
                    if (mSelectImg.size() + 1 > mMaxSelectNum) {
                        Toast.makeText(mContext, "你最多只能选择" + mMaxSelectNum + "个视频", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Log.d("Mfh--->", "还没有这个视频，mSelectImg+1");
                    //未被选择
                    mSelectImg.add(filePath);
                    viewHolder.mImg.setColorFilter(Color.parseColor("#77000000"));
                    viewHolder.mSelect.setImageResource(R.drawable.album_pictures_selected);
                }
                if (mOnSelectListener != null) {
                    mOnSelectListener.onSelectResult((HashSet<String>) mSelectImg);
                }
                //notifyDataSetChanged(); // 不能每次点击就刷新，否则会闪屏
            }
        });

        if (mSelectImg.contains(filePath)) {
            viewHolder.mImg.setColorFilter(Color.parseColor("#77000000"));
            viewHolder.mSelect.setImageResource(R.drawable.album_pictures_selected);
        }

        return convertView;
    }

    private class ViewHolder {
        ImageView mImg;
        ImageButton mSelect;
        TextView mSize;
        TextView mDuration;
    }

    /**
     * 设置最大可选择的 数量
     *
     * @param num
     */
    public void setMaxSelectNum(int num) {
        this.mMaxSelectNum = (num <= 0 ? 1 : num);
    }

    public interface OnSelectListener {
        void onSelectResult(HashSet<String> selected);
    }

    public void setGridViewColumn(int columnNum) {
        this.mGridViewColumn = (columnNum <= 0 ? 3 : columnNum);
    }

    private OnSelectListener mOnSelectListener;

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.mOnSelectListener = onSelectListener;
    }

    public interface ItemOnclickListener {
        void onClick(String path);
    }

    private ItemOnclickListener mItemOnclickListener;

    public void setItemOnclickListener(ItemOnclickListener listener) {
        mItemOnclickListener = listener;
    }
}
