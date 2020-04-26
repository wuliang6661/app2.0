package com.synway.util;

import com.amap.api.maps.model.NavigateArrow;
import com.amap.api.maps.model.NavigateArrow;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author XuKaiyin
 * @class synway.synwaylocating.main.location
 * @name KeyNavigateManager
 * @describe 无ID的NavigateArrow管理类 （导航指示箭头对象）
 * @time 2018/6/19 15:46
 */
public class NoKeyNavigateManager {
    // NavigateArrowManager
    public LinkedList<NavigateArrow> mNavigateArrowList = new LinkedList<NavigateArrow>();
    private static NoKeyNavigateManager sNoKeyNavigateArrowManager;

    public static NoKeyNavigateManager getInstance() {
        synchronized (NoKeyNavigateManager.class) {
            if (sNoKeyNavigateArrowManager == null) {
                sNoKeyNavigateArrowManager = new NoKeyNavigateManager();
            }
        }
        return sNoKeyNavigateArrowManager;
    }

    /**
     * 添加一导航指示箭头对象
     * @param navigateArrow
     */
    public void addNavigateArrow(NavigateArrow navigateArrow) {
        mNavigateArrowList.add(navigateArrow);
    }


    public boolean isEmpty() {
        return mNavigateArrowList.isEmpty();
    }

    /**
     * 清除所有导航指示箭头对象
     */
    public void clearAll() {
        if(!isEmpty()) {
            // 获取地图上所有NavigateArrow
            for (int i = 0; i < mNavigateArrowList.size(); i++) {
                NavigateArrow navigateArrow = mNavigateArrowList.get(i);
                removeNavigateArrow(navigateArrow);
            }
            mNavigateArrowList.clear();
        }

    }

    private void removeNavigateArrow(NavigateArrow navigateArrow){
        if (navigateArrow != null) {
            navigateArrow.remove();
        }
    }

    /**
     * 设置隐藏或显示所有导航指示箭头对象
     * @param isVisible
     */
    public void setAllVisib(Boolean isVisible) {
        if (!isEmpty()) {
            for(int i = 0; i< mNavigateArrowList.size(); i++) {
                mNavigateArrowList.get(i).setVisible(isVisible);
            }
        }
    }
}
