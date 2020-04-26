package cn.synway.app.utils;

import android.support.v7.widget.RecyclerView;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/7/19:27
 * desc   :
 * version: 1.0
 */
public class RecycleUtils {


    public static boolean isSlideToBottom(RecyclerView recyclerView) {
        if (recyclerView == null) return false;
        return recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange();
    }
}