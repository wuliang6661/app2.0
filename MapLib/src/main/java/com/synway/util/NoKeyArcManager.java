package com.synway.util;

import com.amap.api.maps.model.Arc;
import com.amap.api.maps.model.Arc;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author XuKaiyin
 * @class com.synway.util
 * @name KeyArcManager
 * @describe 无ID的Arc管理类 (圆弧)
 * @time 2019/1/24 14:29
 */
public class NoKeyArcManager {

    // ArcManager
    public LinkedList<Arc> mArcList = new LinkedList<Arc>();
    private static NoKeyArcManager sNoKeyArcManager;

    public static NoKeyArcManager getInstance() {
        synchronized (NoKeyArcManager.class) {
            if (sNoKeyArcManager == null) {
                sNoKeyArcManager = new NoKeyArcManager();
            }
        }
        return sNoKeyArcManager;
    }

    /**
     * 添加一圆弧
     * @param arc
     */
    public void addArc(Arc arc) {
        mArcList.add(arc);
    }


    public boolean isEmpty() {
        return mArcList.isEmpty();
    }

    /**
     * 清除所有圆弧
     */
    public void clearAll() {
        if(!isEmpty()) {
            // 获取地图上所有Arc
            for (int i = 0; i < mArcList.size(); i++) {
                Arc arc = mArcList.get(i);
                removeArc(arc);
            }
            mArcList.clear();
        }

    }

    private void removeArc(Arc arc){
        if (arc != null) {
            arc.remove();
        }
    }

    /**
     * 设置隐藏或显示所有圆弧
     * @param isVisible
     */
    public void setAllVisib(Boolean isVisible) {
        if (!isEmpty()) {
            for(int i = 0; i< mArcList.size(); i++) {
                mArcList.get(i).setVisible(isVisible);
            }
        }
    }
}
