package com.synway.util;

import com.amap.api.maps.model.Text;

import java.util.HashMap;

/**
 * @author XuKaiyin
 * @class com.synway.util
 * @name KeyTextManager
 * @describe 有对应ID的文字标记（text）管理类 (文字标记（text）对象)
 * @time 2019/1/24 13:53
 */
public class KeyTextManager {
    // KeyTextManager
    public HashMap<String, Text> mHashMap = new HashMap<>();
    private static KeyTextManager sKeyTextManager;

    public static KeyTextManager getInstance() {
        synchronized (KeyTextManager.class) {
            if (sKeyTextManager == null) {
                sKeyTextManager = new KeyTextManager();
            }
        }
        return sKeyTextManager;
    }

    /**
     * 添加文字标记
     * @param id     key唯一Id
     * @param text
     */
    public void addText(String id, Text text) {
        // 去掉相同Id 的 Text
        if (mHashMap.containsKey(id)) {
            removeText(mHashMap.get(id));
        }
        mHashMap.put(id, text);
    }

    public boolean isEmpty() {
        return mHashMap.isEmpty();
    }

    /**
     * 根据key,ID清除稀疏矩阵里的Text
     * @param id
     */
    public void remove(String id) {
        if (!isEmpty()) {
            removeText(mHashMap.get(id));
            mHashMap.remove(id);
        }
    }

    /**
     * 清空所有文字标记
     */
    public void clearAll() {
        if (!isEmpty()) {
            for(Text text : mHashMap.values())
            {
                removeText(text);
            }
            mHashMap.clear();
        }
    }

    /**
     * Text删除
     * @param text
     */
    private void removeText(Text text) {
        if (text != null) {
            text.remove();
        }
    }

    /**
     * 根据key,ID,设置隐藏或显示指定文字标记
     * @param id
     * @param isVisible
     */
    public void setVisibility(String id,Boolean isVisible) {
        if (!isEmpty()) {
            setVisibleOrGone(mHashMap.get(id), isVisible);
        }
    }

    /**
     * text隐藏或显示
     * @param text
     * @param isVisible
     */
    private void setVisibleOrGone(Text text, Boolean isVisible) {
        if (text != null) {
            text.setVisible(isVisible);
        }
    }

    /**
     * 设置隐藏或显示, 所有文字标记
     * @param isVisible
     */
    public void setAllVisib(Boolean isVisible) {
        if (!isEmpty()) {
            for(Text text : mHashMap.values())
            {
                setVisibleOrGone(text, isVisible);
            }
        }
    }
}
