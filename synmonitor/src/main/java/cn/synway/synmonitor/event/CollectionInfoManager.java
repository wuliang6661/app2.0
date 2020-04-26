package cn.synway.synmonitor.event;

import android.support.annotation.Nullable;

import java.util.concurrent.ConcurrentHashMap;

import cn.synway.synmonitor.bean.DeviceBO;
import cn.synway.synmonitor.config.Config;
import cn.synway.synmonitor.event.eventcache.CacheManager;
import cn.synway.synmonitor.event.eventmodel.BaseEvent;
import cn.synway.synmonitor.logutil.LogUtil;

public class CollectionInfoManager {

    private static CollectionInfoManager instance;

    private final ConcurrentHashMap<String, Long> pageCache;//当前界面管理
    private final CacheManager mCacheManager;//缓存

    private CollectionInfoManager() {
        this.mCacheManager = CacheManager.getInstance();
        this.pageCache = new ConcurrentHashMap<>();
    }

    public static CollectionInfoManager getInstance() {
        if (instance == null) {
            synchronized (CollectionInfoManager.class) {
                if (instance == null) {
                    instance = new CollectionInfoManager();
                }
            }
        }
        return instance;
    }

    /**
     * 收集信息
     *
     * @param name
     */
    public void collectionPageStart(String name) {
        // String className = activity.getLocalClassName();
        pageCache.put(name, System.currentTimeMillis());
    }

    /**
     * 收集信息
     *
     * @param className
     * @param describe
     * @param storageType 0-app 1-应用
     */
    public void collectionPageEnd(String className, @Nullable String id, @Nullable String describe, int storageType) {
        //String className = activity.getLocalClassName();
        boolean containsKey = pageCache.containsKey(className);
        if (!containsKey) {
            //throw new IllegalStateException("collectionPageStart must be called before collectionPageEnd");
            LogUtil.e("CollectionInfoManager", "collectionPageStart must be called before collectionPageEnd");
        }
        Long startTime = pageCache.get(className);
        long duration = System.currentTimeMillis() - startTime;

        BaseEvent event = new BaseEvent();
        event.setDescrible(describe);
        event.setType("page");
        event.setName(className);
        event.setId(id);
        event.setStorageType(storageType);
        event.setDuration(duration);
        event.setDeviceBO(new DeviceBO(Config.context));
        event.setHappenTime(System.currentTimeMillis());
        event.setUserBO(Config.userBO);
        event.setSdkVersion(Config.SYNCOUNTLY_SDK_VERSION_STRING);
        mCacheManager.addEvent(event);
    }

    /**
     * 收集信息 点击事件
     */
    public void receiveClickEvent(String name, @Nullable String describe) {

        BaseEvent event = new BaseEvent();
        event.setDescrible(describe);
        event.setType("click");
        event.setName(name);
        event.setDeviceBO(new DeviceBO(Config.context));
        event.setHappenTime(System.currentTimeMillis());
        event.setUserBO(Config.userBO);
        event.setSdkVersion(Config.SYNCOUNTLY_SDK_VERSION_STRING);
        mCacheManager.addEvent(event);
    }

    /**
     * 将缓存中未上报数据保存在本地
     */
    public void saveNotUpData() {
        mCacheManager.saveNotUpData();
    }
}
