package com.synway.util;

import com.amap.api.maps.model.Polyline;

import java.util.LinkedList;
import java.util.List;

/**
 * @author XuKaiyin
 * @class com.synway.util
 * @name NoKeyPolylineManager
 * @describe 无对应ID的Polyline管理类
 * @time 2018/12/26 10:57
 */
public class NoKeyPolylineManager {

    // PolylineManager
    public LinkedList<Polyline> mPolylineList = new LinkedList<Polyline>();
    private static NoKeyPolylineManager sNoKeyPolylineManager;

    public static NoKeyPolylineManager getInstance() {
        synchronized (NoKeyPolylineManager.class) {
            if (sNoKeyPolylineManager == null) {
                sNoKeyPolylineManager = new NoKeyPolylineManager();
            }
        }
        return sNoKeyPolylineManager;
    }

    /**
     * 添加一线
     * @param polyline
     */
    public void addPolyline(Polyline polyline) {
        mPolylineList.add(polyline);
    }


    public boolean isEmpty() {
        return mPolylineList.isEmpty();
    }

    /**
     * 清除所有线
     */
    public void clearAll() {
        if(!isEmpty()) {
            // 获取地图上所有Polyline
            for (int i = 0; i < mPolylineList.size(); i++) {
                Polyline polyline = mPolylineList.get(i);
                removePolyline(polyline);
            }
            mPolylineList.clear();
        }

    }

    private void removePolyline(Polyline polyline){
        if (polyline != null) {
            polyline.remove();
        }
    }

    /**
     * 设置隐藏或显示所有线
     * @param isVisible
     */
    public void setAllVisib(Boolean isVisible) {
        if (!isEmpty()) {
            for(int i = 0; i< mPolylineList.size(); i++) {
                mPolylineList.get(i).setVisible(isVisible);
            }
        }
    }
}
