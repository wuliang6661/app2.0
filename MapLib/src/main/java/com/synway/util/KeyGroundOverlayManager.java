package com.synway.util;

import com.amap.api.maps.model.GroundOverlay;
import com.amap.api.maps.model.GroundOverlay;

import java.util.HashMap;

/**
 * @author XuKaiyin
 * @class com.synway.util
 * @name ID的Ground覆盖物管理类
 * @describe
 * @time 2019/2/18 9:32
 */
public class KeyGroundOverlayManager {
    
    // KeyGroundOverlayManager
    public HashMap<String, GroundOverlay> mHashMap = new HashMap<>();
    private static KeyGroundOverlayManager sKeyGroundOverlayManager;

    public static KeyGroundOverlayManager getInstance() {
        synchronized (KeyGroundOverlayManager.class) {
            if (sKeyGroundOverlayManager == null) {
                sKeyGroundOverlayManager = new KeyGroundOverlayManager();
            }
        }
        return sKeyGroundOverlayManager;
    }

    /**
     * 添加瓦片图层覆盖物
     * @param id     key唯一Id
     * @param groundOverlay
     */
    public void addGroundOverlay(String id, GroundOverlay groundOverlay) {
        // 去掉相同Id 的 groundOverlay
        if (mHashMap.containsKey(id)) {
            removeGroundOverlay(mHashMap.get(id));
        }
        mHashMap.put(id, groundOverlay);
    }

    public boolean isEmpty() {
        return mHashMap.isEmpty();
    }

    /**
     * 根据key,ID清除稀疏矩阵里的TileOverlay
     * @param id
     */
    public void remove(String id) {
        if (!isEmpty()) {
            removeGroundOverlay(mHashMap.get(id));
            mHashMap.remove(id);
        }
    }

    /**
     * 清空所有瓦片图层覆盖物
     */
    public void clearAll() {
        if (!isEmpty()) {
            for(GroundOverlay groundOverlay : mHashMap.values())
            {
                removeGroundOverlay(groundOverlay);
            }
            mHashMap.clear();
        }
    }

    /**
     * TileOverlay删除
     * @param groundOverlay
     */
    private void removeGroundOverlay(GroundOverlay groundOverlay) {
        if (groundOverlay != null) {
            groundOverlay.remove();
        }
    }

    /**
     * 根据key,ID,设置隐藏或显示指定瓦片图层覆盖物
     * @param id
     * @param isVisible
     */
    public void setVisibility(String id,Boolean isVisible) {
        if (!isEmpty()) {
            setVisibleOrGone(mHashMap.get(id), isVisible);
        }
    }

    /**
     * tileOverlay隐藏或显示
     * @param groundOverlay
     * @param isVisible
     */
    private void setVisibleOrGone(GroundOverlay groundOverlay, Boolean isVisible) {
        if (groundOverlay != null) {
            groundOverlay.setVisible(isVisible);
        }
    }

    /**
     * 设置隐藏或显示, 所有瓦片图层覆盖物
     * @param isVisible
     */
    public void setAllVisib(Boolean isVisible) {
        if (!isEmpty()) {
            for(GroundOverlay groundOverlay : mHashMap.values())
            {
                setVisibleOrGone(groundOverlay, isVisible);
            }
        }
    }
}
