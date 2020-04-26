package cn.synway.synmonitor.event.eventcache;


import java.util.List;
import java.util.Vector;

import cn.synway.synmonitor.event.eventmodel.BaseEvent;

class MemoryCacheUtil {

    public static int MAX_SIZE = 2;
    private List<BaseEvent> xgEventList = new Vector<>();

    private MemoryCacheUtil() {

    }

    public static MemoryCacheUtil getInstance() {
        return MemoryCacheUtilHolder.instance;
    }

    /**
     * @param memoryEvent
     */
    //TODO:上报间隔是默认是5min , 执行该方法时可能有上报线程获取xgEventList导致数据重复上报。嗯嗯嗯嗯，概率极小，5min 网络早就超时了
    public void clearMemory(List<BaseEvent> memoryEvent) {
        for (int i = 0; i < memoryEvent.size(); i++) {
            BaseEvent baseEvent = memoryEvent.get(i);
            if (xgEventList.contains(baseEvent)) {
                xgEventList.remove(baseEvent);
            }
        }

    }


    private static class MemoryCacheUtilHolder {
        private static MemoryCacheUtil instance = new MemoryCacheUtil();
    }

    public void addEvent(BaseEvent xgEvent) {

        this.xgEventList.add(xgEvent);
    }

    public void remove() {
        this.xgEventList.clear();
    }

    public List<BaseEvent> getEventList() {
        return this.xgEventList;
    }

    public int getSize() {
        return xgEventList.size();
    }

    public void clearAll() {
        xgEventList.clear();
    }
}
