package com.synway.util;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.NavigateArrow;
import com.amap.api.maps.model.NavigateArrowOptions;
import com.amap.api.maps.model.PolylineOptions;

import java.util.HashMap;
import java.util.List;

/**
 * @author XuKaiyin
 * @class synway.synwaylocating.main.location
 * @name KeyNavigateManager
 * @describe 有对应ID的NavigateArrow管理类 （导航指示箭头对象）
 * @time 2018/6/19 15:46
 */
public class KeyNavigateManager {

    // KeyNavigateArrowManager
    public HashMap<String, NavigateArrow> mHashMap = new HashMap<>();
    private static KeyNavigateManager sKeyNavigateArrowManager;

    public static KeyNavigateManager getInstance() {
        synchronized (KeyNavigateManager.class) {
            if (sKeyNavigateArrowManager == null) {
                sKeyNavigateArrowManager = new KeyNavigateManager();
            }
        }
        return sKeyNavigateArrowManager;
    }

    /**
     * 添加导航指示箭头对象
     * @param id     key唯一Id
     * @param navigateArrow
     */
    public void addNavigateArrow(String id, NavigateArrow navigateArrow) {
        // 去掉相同Id 的 NavigateArrow
        if (mHashMap.containsKey(id)) {
            removeNavigateArrow(mHashMap.get(id));
        }
        mHashMap.put(id, navigateArrow);
    }

    public boolean isEmpty() {
        return mHashMap.isEmpty();
    }

    /**
     * 根据key,ID清除稀疏矩阵里的NavigateArrow
     * @param id
     */
    public void remove(String id) {
        if (!isEmpty()) {
            removeNavigateArrow(mHashMap.get(id));
            mHashMap.remove(id);
        }
    }

    /**
     * 清空所有导航指示箭头对象
     */
    public void clearAll() {
        if (!isEmpty()) {
            for(NavigateArrow navigateArrow : mHashMap.values())
            {
                removeNavigateArrow(navigateArrow);
            }
            mHashMap.clear();
        }
    }

    /**
     * NavigateArrow删除
     * @param navigateArrow
     */
    private void removeNavigateArrow(NavigateArrow navigateArrow) {
        if (navigateArrow != null) {
            navigateArrow.remove();
        }
    }

    /**
     * 根据key,ID,设置隐藏或显示指定导航指示箭头对象
     * @param id
     * @param isVisible
     */
    public void setVisibility(String id,Boolean isVisible) {
        if (!isEmpty()) {
            setVisibleOrGone(mHashMap.get(id), isVisible);
        }
    }

    /**
     * navigateArrow隐藏或显示
     * @param navigateArrow
     * @param isVisible
     */
    private void setVisibleOrGone(NavigateArrow navigateArrow, Boolean isVisible) {
        if (navigateArrow != null) {
            navigateArrow.setVisible(isVisible);
        }
    }

    /**
     * 设置隐藏或显示, 所有导航指示箭头对象
     * @param isVisible
     */
    public void setAllVisib(Boolean isVisible) {
        if (!isEmpty()) {
            for(NavigateArrow navigateArrow : mHashMap.values())
            {
                setVisibleOrGone(navigateArrow, isVisible);
            }

        }
    }
}
