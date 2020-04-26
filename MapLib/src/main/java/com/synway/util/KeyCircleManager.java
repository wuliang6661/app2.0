package com.synway.util;

import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.Marker;

import java.util.HashMap;
import java.util.List;

/**
 * @author XuKaiyin
 * @class synway.synwaylocating.main.location
 * @name KeyCircleManager
 * @describe 有对应ID的circle管理类
 * @time 2018/6/19 15:46
 */
public class KeyCircleManager {

    // KeyCircleManager
    public HashMap<String, Circle> mHashMap = new HashMap<>();
    private static KeyCircleManager sKeyCircleManager;

    public static KeyCircleManager getInstance() {
        synchronized (KeyCircleManager.class) {
            if (sKeyCircleManager == null) {
                sKeyCircleManager = new KeyCircleManager();
            }
        }
        return sKeyCircleManager;
    }

    /**
     * 添加一个圆
     * @param id     key唯一Id
     * @param circle
     */
    public void addCircle(String id, Circle circle) {
        // 去掉相同Id 的 Circle
        if (mHashMap.containsKey(id)) {
            removeCircle(mHashMap.get(id));
        }
        mHashMap.put(id, circle);
    }

    public boolean isEmpty() {
        return mHashMap.isEmpty();
    }

    /**
     * 根据key,ID清除稀疏矩阵里的circle
     * @param id
     */
    public void remove(String id) {
        if (!isEmpty()) {
            removeCircle(mHashMap.get(id));
            mHashMap.remove(id);
        }
    }

    /**
     * 清空所有
     */
    public void clearAll() {
        if (!isEmpty()) {
            for(Circle circle : mHashMap.values())
            {
                removeCircle(circle);
            }
            mHashMap.clear();
        }
    }

    /**
     * circle删除
     * @param circle
     */
    private void removeCircle(Circle circle) {
        if (circle != null) {
            circle.remove();
        }
    }

    /**
     * 根据key,ID,设置隐藏或显示指定圆
     * @param id
     * @param isVisible
     */
    public void setVisibility(String id,Boolean isVisible) {
        if (!isEmpty()) {
            setVisibleOrGone(mHashMap.get(id), isVisible);
        }
    }

    /**
     * circle隐藏或显示
     * @param circle
     * @param isVisible
     */
    private void setVisibleOrGone(Circle circle, Boolean isVisible) {
        if (circle != null) {
            circle.setVisible(isVisible);
        }
    }

    /**
     * 设置隐藏或显示, 所有圆
     * @param isVisible
     */
    public void setAllVisib(Boolean isVisible) {
        if (!isEmpty()) {
            for(Circle circle : mHashMap.values())
            {
                setVisibleOrGone(circle, isVisible);
            }

        }
    }
}
