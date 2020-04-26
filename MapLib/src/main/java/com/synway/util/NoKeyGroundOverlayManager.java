package com.synway.util;



import com.amap.api.maps.model.GroundOverlay;

import java.util.LinkedList;

/**
 * @author XuKaiyin
 * @class com.synway.util
 * @name NoKeyGroundOverlayManager
 * @describe 无ID的Ground覆盖物管理类
 * @time 2019/1/25 13:23
 */
public class NoKeyGroundOverlayManager {

    // NoKeyGroundOverlayManager
    public LinkedList<GroundOverlay> mGroundOverlayList = new LinkedList<GroundOverlay>();
    private static NoKeyGroundOverlayManager sGroundOverlayManager;

    public static NoKeyGroundOverlayManager getInstance() {
        synchronized (NoKeyGroundOverlayManager.class) {
            if (sGroundOverlayManager == null) {
                sGroundOverlayManager = new NoKeyGroundOverlayManager();
            }
        }
        return sGroundOverlayManager;
    }

    /**
     * 添加一Ground覆盖物
     * @param groundOverlay
     */
    public void addGroundOverlay(GroundOverlay groundOverlay) {
        mGroundOverlayList.add(groundOverlay);
    }


    public boolean isEmpty() {
        return mGroundOverlayList.isEmpty();
    }

    /**
     * 清除所有Ground覆盖物
     */
    public void clearAll() {
        if(!isEmpty()) {
            // 获取地图上所有GroundOverlay
            for (int i = 0; i < mGroundOverlayList.size(); i++) {
                GroundOverlay groundOverlay = mGroundOverlayList.get(i);
                removeGroundOverlay(groundOverlay);
            }
            mGroundOverlayList.clear();
        }

    }

    private void removeGroundOverlay(GroundOverlay groundOverlay){
        if (groundOverlay != null) {
            groundOverlay.destroy();
        }
    }

    /**
     * 设置隐藏或显示所有Ground覆盖物
     * @param isVisible
     */
    public void setAllVisib(Boolean isVisible) {
        if (!isEmpty()) {
            for(int i = 0; i< mGroundOverlayList.size(); i++) {
                mGroundOverlayList.get(i).setVisible(isVisible);
            }
        }
    }
}
