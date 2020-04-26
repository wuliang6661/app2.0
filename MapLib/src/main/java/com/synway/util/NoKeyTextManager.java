package com.synway.util;

import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.Text;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author XuKaiyin
 * @class com.synway.util
 * @name NoKeyTextManager
 * @describe 无ID的文字标记（text）管理类 (文字标记（text）对象)
 * @time 2019/1/24 13:53
 */
public class NoKeyTextManager {
    // TextManager
    public LinkedList<Text> textList = new LinkedList<Text>();
    private static NoKeyTextManager textManager;

    public static NoKeyTextManager getInstance() {
        synchronized (NoKeyTextManager.class) {
            if (textManager == null) {
                textManager = new NoKeyTextManager();
            }
        }
        return textManager;
    }

    /**
     * 添加一个文字标记
     * @param text
     */
    public void addText(Text text) {
        textList.add(text);
    }

    public boolean isEmpty() {
        return textList.isEmpty();
    }

    /**
     * 清除所有文字标记
     */
    public void clearAll() {
        if(!isEmpty()) {
            // 获取地图上所有Text
            for (int i = 0; i < textList.size(); i++) {
                Text text = textList.get(i);
                removeText(text);
            }
            textList.clear();
        }

    }

    private void removeText(Text text){
        if (text != null) {
            text.remove();
        }
    }

    /**
     * 设置隐藏或显示所有文字标记
     * @param isVisible
     */
    public void setAllVisib(Boolean isVisible) {
        if (!isEmpty()) {
            for(int i = 0; i< textList.size(); i++) {
                textList.get(i).setVisible(isVisible);
            }
        }
    }
}
