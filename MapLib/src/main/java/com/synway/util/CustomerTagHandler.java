package com.synway.util;

import android.content.Context;
import android.text.Editable;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StrikethroughSpan;
import android.util.TypedValue;

import org.xml.sax.Attributes;

import java.util.Collection;
import java.util.Stack;

/**
 * @author XuKaiyin
 * @class com.synway.util
 * @name
 * @describe
 * @time 2019/3/12 10:35
 */
public class CustomerTagHandler implements HtmlParser.TagHandler {
    /**
     * html 标签的开始下标
     */
    private Stack<Integer> startIndex;

    /**
     * html的标签的属性值 value，如:<size value='16'></size>
     * 注：value的值不能带有单位,默认就是sp
     */
    private Stack<String> propertyValue;

    private Context mContext;

    public CustomerTagHandler(Context context) {
        super();
        this.mContext = context;
    }

    @Override
    public boolean handleTag(boolean opening, String tag, Editable output, Attributes attributes) {
        if (opening) {
            handlerStartTAG(tag, output, attributes);
        } else {
            handlerEndTAG(tag, output, attributes);
        }
        return handlerBYDefault(tag);
    }

    private void handlerStartTAG(String tag, Editable output, Attributes attributes) {
        if (tag.equalsIgnoreCase("font")) {
            handlerStartFONT(output, attributes);
        } else if (tag.equalsIgnoreCase("del")) {
            handlerStartDEL(output);
        }
    }


    private void handlerEndTAG(String tag, Editable output, Attributes attributes) {
        if (tag.equalsIgnoreCase("font")) {
            handlerEndFONT(output);
        } else if (tag.equalsIgnoreCase("del")) {
            handlerEndDEL(output);
        }
    }

    private void handlerStartFONT(Editable output, Attributes attributes) {
        if (startIndex == null) {
            startIndex = new Stack<>();
        }
        startIndex.push(output.length());

        if (propertyValue == null) {
            propertyValue = new Stack<>();
        }

        propertyValue.push(HtmlParser.getValue(attributes, "size"));
    }

    private void handlerEndFONT(Editable output) {
        if (!isEmpty(propertyValue)) {
            try {
                int value = Integer.parseInt(propertyValue.pop());
                output.setSpan(new AbsoluteSizeSpan(sp2px(mContext, value)), startIndex.pop(),
                        output.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void handlerStartDEL(Editable output) {
        if (startIndex == null) {
            startIndex = new Stack<>();
        }
        startIndex.push(output.length());
    }

    private void handlerEndDEL(Editable output) {
        output.setSpan(new StrikethroughSpan(), startIndex.pop(), output.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }


    /**
     * 返回true表示不交给系统后续处理
     * false表示交给系统后续处理
     * @param tag
     * @return
     */
    private boolean handlerBYDefault(String tag) {
        return tag.equalsIgnoreCase("del");
    }

    /**
     * 集合是否为空
     * @param collection
     * @return
     */
    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 缩放独立像素 转换成 像素
     * @param context
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics()) + 0.5f);
    }
}
