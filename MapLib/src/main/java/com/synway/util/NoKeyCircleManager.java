package com.synway.util;

import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.Polyline;

import java.util.LinkedList;

/**
 * @author XuKaiyin
 * @class com.synway.util
 * @name NoKeyCircleManager
 * @describe 无对应ID的Circle管理类
 * @time 2018/12/26 10:57
 */
public class NoKeyCircleManager {

    // CircleManager
    public LinkedList<Circle> mCircleList = new LinkedList<Circle>();
    private static NoKeyCircleManager sNoKeyCircleManager;

    public static NoKeyCircleManager getInstance() {
        synchronized (NoKeyCircleManager.class) {
            if (sNoKeyCircleManager == null) {
                sNoKeyCircleManager = new NoKeyCircleManager();
            }
        }
        return sNoKeyCircleManager;
    }

    /**
     * 添加一个圆
     * @param circle
     */
    public void addCircle(Circle circle) {
        mCircleList.add(circle);
    }


    public boolean isEmpty() {
        return mCircleList.isEmpty();
    }

    /**
     * 清除所有圆
     */
    public void clearAll() {
        if(!isEmpty()) {
            // 获取地图上所有Circle
            for (int i = 0; i < mCircleList.size(); i++) {
                Circle circle = mCircleList.get(i);
                removeCircle(circle);
            }
            mCircleList.clear();
        }

    }

    private void removeCircle(Circle circle){
        if (circle != null) {
            circle.remove();
        }
    }

    /**
     * 设置隐藏或显示所有圆
     * @param isVisible
     */
    public void setAllVisib(Boolean isVisible) {
        if (!isEmpty()) {
            for(int i = 0; i< mCircleList.size(); i++) {
                mCircleList.get(i).setVisible(isVisible);
            }
        }
    }
}
