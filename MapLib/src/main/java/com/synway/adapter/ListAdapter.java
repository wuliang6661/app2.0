package com.synway.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.synway.bean.InfoBean;
import com.synway.map.R;
import com.synway.util.CustomerTagHandler;
import com.synway.util.HtmlParser;

import java.util.List;

/**
 * @author XuKaiyin
 * @class com.synway.adapter
 * @name
 * @describe
 * @time 2018/12/25 13:20
 */
public class ListAdapter extends BaseAdapter {
    private Context        mContext;
    private List<InfoBean> mBeans;

    public ListAdapter(Context context, List<InfoBean> dataList) {
        this.mContext = context;
        this.mBeans = dataList;
    }

    @Override
    public int getCount() {
        return mBeans == null ? 0 : mBeans.size();
    }

    @Override
    public InfoBean getItem(int position) {
        return mBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_layout, parent,
                    false);
            holder = new ViewHolder();
            holder.mTvContent = convertView.findViewById(R.id.tv_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final InfoBean bean = getItem(position);
        if(!bean.isUseHtml()) {
            holder.mTvContent.setText(TextUtils.isEmpty(bean.getContent()) ? "" : bean.getContent());
            holder.mTvContent.setTextSize(bean.getTextSize());
            holder.mTvContent.setTextColor(TextUtils.isEmpty(bean.getContent()) ?
                    Color.parseColor("#666666") : Color.parseColor(bean.getColor()));
        } else {
            holder.mTvContent.setText(TextUtils.isEmpty(bean.getContent()) ? ""
                    : HtmlParser.buildSpannedText(bean.getContent(),new CustomerTagHandler(mContext)));
        }
        if(bean.isClickable()) {
            holder.mTvContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext,bean.getClickUrl() + "",Toast.LENGTH_SHORT).show();
                }
            });
        }
        return convertView;
    }

    class ViewHolder {
        TextView mTvContent;
    }
}
