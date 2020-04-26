package cn.synway.synmonitor.event.eventcache;

import java.io.File;
import java.util.List;

import cn.synway.synmonitor.event.eventmodel.BaseEvent;
import cn.synway.synmonitor.logutil.LogUtil;

/**
 * 存在两个线程，线程一:主线程数据收集，线程n:数据上报,网络线程*n。
 * mMemoryCacheUtil: Vector ,mDiskCacheUtil: synchronized
 */
public class CacheManager {
    private static CacheManager instance;
    private final MemoryCacheUtil mMemoryCacheUtil;
    private final DiskCacheUtil mDiskCacheUtil;


    private CacheManager() {
        mMemoryCacheUtil = MemoryCacheUtil.getInstance();
        mDiskCacheUtil = DiskCacheUtil.getInstance();
    }

    public static CacheManager getInstance() {
        if (instance == null) {
            synchronized (CacheManager.class) {
                if (instance == null) {
                    instance = new CacheManager();
                }
            }
        }
        return instance;
    }

    /**
     * 增加事件本地缓存 不安全的
     */
    public void addEvent(BaseEvent event) {
        if (mMemoryCacheUtil.getSize() > MemoryCacheUtil.MAX_SIZE) {
            //TODO:假如这个时候缓存数据正在上报，这将导致数据重复
            List<BaseEvent> eventList = mMemoryCacheUtil.getEventList();
            mDiskCacheUtil.addMemoryCacheEvent(eventList);
            //可以直接ClearAll
            mMemoryCacheUtil.clearAll();
        }
        else {
            mMemoryCacheUtil.addEvent(event);
        }
    }

    /**
     * 只能由网络线程调用，存在并发隐患
     * <p>
     * 清除内存中缓存事件
     * 安全的
     *
     * @param memoryEvent
     */
    //TODO:上报间隔是默认是5min , 执行该方法时可能有上报线程获取xgEventList导致数据重复上报。嗯嗯嗯嗯，概率极小，5min 网络早就超时了
    public void clearMemory(List<BaseEvent> memoryEvent) {
        //不能直接clearAll,避免主线程在数据上报过程中添加了新数据而导致数据丢失。
        mMemoryCacheUtil.clearMemory(memoryEvent);
    }

    /**
     * 取出本地缓存的文件
     * 安全的
     */
    public File[] getFileEvent() {
        return mDiskCacheUtil.getFileData();
    }

    /**
     * 取出内存中缓存事件
     * 安全的
     */
    public List<BaseEvent> getMemoryEvent() {
        return mMemoryCacheUtil.getEventList();
    }

    /**
     * 删除文件
     * 安全的
     *
     * @param file
     */
    public void deleteFile(File file) {
        mDiskCacheUtil.deleteFile(file);
    }

    /**
     * 将缓存中未上报数据保存在本地  不安全的
     */
    public void saveNotUpData() {
        LogUtil.e("up","savenoup");
        if (mMemoryCacheUtil!=null&&mDiskCacheUtil!=null&&mMemoryCacheUtil.getSize() > 0) {
            //TODO:假如这个时候缓存数据正在上报，这将导致数据重复
            LogUtil.e("up","savenoup2");
            mDiskCacheUtil.addMemoryCacheEvent(mMemoryCacheUtil.getEventList());
            //可以直接clearAll
            mMemoryCacheUtil.clearAll();
        }
    }
}
