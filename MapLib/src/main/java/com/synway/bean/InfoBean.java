package com.synway.bean;

/**
 * @author XuKaiyin
 * @class com.synway.bean
 * @name
 * @describe
 * @time 2018/12/25 13:20
 */
public class InfoBean {

    /**
     * content : 文字内容
     * color : 颜色
     * clickable : 是否可点击
     * clickUrl : 跳转URL
     * textSize : 字体大小
     * isHtml : 是否html
     */

    private String  content;
    private String  color;
    private boolean clickable;
    private String  clickUrl;
    private int     textSize;
    private boolean useHtml;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isClickable() {
        return clickable;
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    public String getClickUrl() {
        return clickUrl;
    }

    public void setClickUrl(String clickUrl) {
        this.clickUrl = clickUrl;
    }

    public boolean isUseHtml() {
        return useHtml;
    }

    public void setUseHtml(boolean useHtml) {
        this.useHtml = useHtml;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }
}
