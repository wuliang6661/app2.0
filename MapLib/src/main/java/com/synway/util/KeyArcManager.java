package com.synway.util;

import com.amap.api.maps.model.Arc;
import java.util.HashMap;

/**
 * @author XuKaiyin
 * @class com.synway.util
 * @name KeyArcManager
 * @describe 有对应ID的Arc管理类 (圆弧)
 * @time 2019/1/24 14:29
 */
public class KeyArcManager {

    // KeyArcManager
    public HashMap<String, Arc> mHashMap = new HashMap<>();
    private static KeyArcManager sKeyArcManager;

    public static KeyArcManager getInstance() {
        synchronized (KeyArcManager.class) {
            if (sKeyArcManager == null) {
                sKeyArcManager = new KeyArcManager();
            }
        }
        return sKeyArcManager;
    }

    /**
     * 添加圆弧
     * @param id     key唯一Id
     * @param arc
     */
    public void addArc(String id, Arc arc) {
        // 去掉相同Id 的 Arc
        if (mHashMap.containsKey(id)) {
            removeArc(mHashMap.get(id));
        }
        mHashMap.put(id, arc);
    }

    public boolean isEmpty() {
        return mHashMap.isEmpty();
    }

    /**
     * 根据key,ID清除稀疏矩阵里的Arc
     * @param id
     */
    public void remove(String id) {
        if (!isEmpty()) {
            removeArc(mHashMap.get(id));
            mHashMap.remove(id);
        }
    }

    /**
     * 清空所有圆弧
     */
    public void clearAll() {
        if (!isEmpty()) {
            for(Arc arc : mHashMap.values())
            {
                removeArc(arc);
            }
            mHashMap.clear();
        }
    }

    /**
     * Arc删除
     * @param arc
     */
    private void removeArc(Arc arc) {
        if (arc != null) {
            arc.remove();
        }
    }

    /**
     * 根据key,ID,设置隐藏或显示指定圆弧
     * @param id
     * @param isVisible
     */
    public void setVisibility(String id,Boolean isVisible) {
        if (!isEmpty()) {
            setVisibleOrGone(mHashMap.get(id), isVisible);
        }
    }

    /**
     * arc隐藏或显示
     * @param arc
     * @param isVisible
     */
    private void setVisibleOrGone(Arc arc, Boolean isVisible) {
        if (arc != null) {
            arc.setVisible(isVisible);
        }
    }

    /**
     * 设置隐藏或显示, 所有线
     * @param isVisible
     */
    public void setAllVisib(Boolean isVisible) {
        if (!isEmpty()) {
            for(Arc arc : mHashMap.values())
            {
                setVisibleOrGone(arc, isVisible);
            }

        }
    }
}
