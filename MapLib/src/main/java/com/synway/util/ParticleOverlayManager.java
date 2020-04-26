package com.synway.util;


import com.amap.api.maps.model.particle.ParticleOverlay;

import java.util.LinkedList;

/**
 * @author XuKaiyin
 * @class com.synway.util
 * @name  ParticleOverlayManager
 * @describe ParticleOverlay管理类 (粒子系统对象)
 * @time 2019/1/25 11:06
 */
public class ParticleOverlayManager {

    // ParticleOverlayManager
    public LinkedList<ParticleOverlay> mParticleOverlayList = new LinkedList<ParticleOverlay>();
    private static ParticleOverlayManager sParticleOverlayManager;

    public static ParticleOverlayManager getInstance() {
        synchronized (ParticleOverlayManager.class) {
            if (sParticleOverlayManager == null) {
                sParticleOverlayManager = new ParticleOverlayManager();
            }
        }
        return sParticleOverlayManager;
    }

    /**
     * 添加一粒子系统对象
     * @param particleOverlay
     */
    public void addParticleOverlay(ParticleOverlay particleOverlay) {
        if(!isEmpty()) {
            setAllVisib(false);
            mParticleOverlayList.clear();
        }

        mParticleOverlayList.add(particleOverlay);
    }


    public boolean isEmpty() {
        return mParticleOverlayList.isEmpty();
    }

    /**
     * 清除所有粒子系统对象
     */
    public void clearAll() {
        if(!isEmpty()) {
            setAllVisib(false);
            mParticleOverlayList.clear();
        }

    }

    /**
     * 设置隐藏或显示所有粒子系统对象
     * @param isVisible
     */
    public void setAllVisib(Boolean isVisible) {
        if (!isEmpty()) {
            for(int i = 0; i< mParticleOverlayList.size(); i++) {
                mParticleOverlayList.get(i).setVisible(isVisible);
            }
        }
    }
}
