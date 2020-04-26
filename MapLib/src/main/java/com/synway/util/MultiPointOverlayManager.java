package com.synway.util;

import com.amap.api.maps.model.MultiPointOverlay;

import java.util.LinkedList;

/**
 * @author XuKaiyin
 * @class com.synway.util
 * @name MultiPointOverlayManager
 * @describe MultiPointOverlay管理类 (海量点覆盖物)
 * @time 2019/1/25 10:45
 */
public class MultiPointOverlayManager {
    // MultiPointOverlayManager
    public LinkedList<MultiPointOverlay> mMultiPointOverlayList = new LinkedList<MultiPointOverlay>();
    private static MultiPointOverlayManager sMultiPointOverlayManager;

    public static MultiPointOverlayManager getInstance() {
        synchronized (MultiPointOverlayManager.class) {
            if (sMultiPointOverlayManager == null) {
                sMultiPointOverlayManager = new MultiPointOverlayManager();
            }
        }
        return sMultiPointOverlayManager;
    }

    /**
     * 添加一海量点覆盖物
     * @param multiPointOverlay
     */
    public void addMultiPointOverlay(MultiPointOverlay multiPointOverlay) {
        mMultiPointOverlayList.add(multiPointOverlay);
    }


    public boolean isEmpty() {
        return mMultiPointOverlayList.isEmpty();
    }

    /**
     * 清除所有海量点覆盖物
     */
    public void clearAll() {
        if(!isEmpty()) {
            // 获取地图上所有MultiPointOverlay
            for (int i = 0; i < mMultiPointOverlayList.size(); i++) {
                MultiPointOverlay multiPointOverlay = mMultiPointOverlayList.get(i);
                removeMultiPointOverlay(multiPointOverlay);
            }
            mMultiPointOverlayList.clear();
        }

    }

    private void removeMultiPointOverlay(MultiPointOverlay multiPointOverlay){
        if (multiPointOverlay != null) {
            multiPointOverlay.remove();
        }
    }

    /**
     * 设置隐藏或显示所有海量点覆盖物
     * @param isVisible
     */
    public void setAllVisib(Boolean isVisible) {
        if (!isEmpty()) {
            for(int i = 0; i< mMultiPointOverlayList.size(); i++) {
                mMultiPointOverlayList.get(i).setEnable(isVisible);
            }
        }
    }
}
