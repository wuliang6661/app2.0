package com.synway.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.synway.bean.InfoBean;
import com.synway.map.R;

import org.json.JSONObject;

import java.util.List;

/**
 * @author XuKaiyin
 * @class com.synway.map
 * @name 地图上自定义的infowindow的适配器
 * @describe
 * @time 2018/12/25 8:31
 */
public class InfoWinAdapter implements AMap.InfoWindowAdapter {
    private Context      mContext;
    private LatLng       latLng;
    private LinearLayout mLlytMap;
    private View         mLine;
    private TextView     mTvTitle;
    private ListView     mListView;
    private ListAdapter  mAdapter;

    private String       title;
    private String       snippet; // json

    public InfoWinAdapter(Context context) {
        mContext = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        initData(marker);
        View view = initView();
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private void initData(Marker marker) {
        latLng = marker.getPosition();
        snippet = marker.getSnippet();
        title = marker.getTitle();
    }

    private View initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_infowindow, null);
        mLlytMap = view.findViewById(R.id.llyt_map);
        mTvTitle = view.findViewById(R.id.tv_title);
        mLine = view.findViewById(R.id.view_line);
        mListView = view.findViewById(R.id.lv_desc);
        if(TextUtils.isEmpty(title)) {
            mTvTitle.setVisibility(View.GONE);
            mLine.setVisibility(View.GONE);
        } else {
            mTvTitle.setVisibility(View.VISIBLE);
            mLine.setVisibility(View.VISIBLE);
            mTvTitle.setText(title);
        }

        if(TextUtils.isEmpty(snippet)) {
            mListView.setVisibility(View.GONE);
        } else {

            try {
                JSONObject jsonObject = new JSONObject(snippet);
                List<InfoBean> contents = JSON.parseArray(jsonObject.getString
                        ("contents"), InfoBean.class);
                ListAdapter adapter = new ListAdapter(mContext, contents);
                mListView.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
                mListView.setVisibility(View.GONE);
            }
        }
        return view;
    }
}
