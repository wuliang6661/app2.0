package cn.synway.app.widget.lgrecycleadapter;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cn.synway.app.R;


/**
 * Created by guizhigang on 16/8/8.
 */
public class LGViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> mViews;
    private View mConvertView;//缓存itemView内部的子View

    public LGViewHolder(View itemView) {
        super(itemView);
        mConvertView = itemView;
        mViews = new SparseArray<>();
    }

    /**
     * 加载layoutId视图并用LGViewHolder保存
     *
     * @param parent
     * @param layoutId
     * @return
     */
    protected static LGViewHolder getViewHolder(ViewGroup parent, int layoutId) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new LGViewHolder(itemView);
    }

    /**
     * 根据ItemView的id获取子视图View
     *
     * @param viewId
     * @return
     */
    public View getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return view;
    }

    /**
     * 设置文字内容
     */
    public void setText(int viewId, String text) {
        TextView textView = (TextView) getView(viewId);
        textView.setText(text);
    }

    public void setImageUrl(Context mContext, int viewId, String url) {
        ImageView view = (ImageView) getView(viewId);

        Glide.with(mContext).load(url).error(R.drawable.police_picture).placeholder(R.drawable.police_picture)
                .into(view);
    }

    public void setImageResurce(@IdRes int viewId, @DrawableRes int drawable) {
        ImageView view = (ImageView) getView(viewId);
        view.setImageResource(drawable);
    }

}
