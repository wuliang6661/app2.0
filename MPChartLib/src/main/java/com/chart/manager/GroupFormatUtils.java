package com.chart.manager;

import java.util.HashMap;

/**
 * Created by leo on 2018/12/6.
 */

public class GroupFormatUtils {

    //( barWidth+barSpace )*barCount + groupSpace = 1
    public static HashMap<Integer, Float[]> map = new HashMap<>();

    static {
        map.put(2, new Float[] { 0.4f, 0.05f, 0.1f });
        map.put(3, new Float[] { 0.25f, 0.05f, 0.1f });
        map.put(4, new Float[] { 0.2f, 0.03f, 0.08f });
        map.put(5, new Float[] { 0.18f, 0.004f, 0.08f });
        map.put(6, new Float[] { 0.15f, 0.005f, 0.07f });
        map.put(7, new Float[] { 0.14f, 0f, 0.02f });
        map.put(8, new Float[] { 0.12f, 0f, 0.04f });
        map.put(9, new Float[] { 0.11f, 0f, 0.01f });
        map.put(10, new Float[] { 0.099f, 0f, 0.01f });
        map.put(11, new Float[] { 0.09f, 0f, 0.01f });
        map.put(12, new Float[] { 0.08f, 0f, 0.04f });
        map.put(13, new Float[] { 0.07f, 0f, 0.09f });
        map.put(14, new Float[] { 0.07f, 0f, 0.02f });
        map.put(15, new Float[] { 0.066f, 0f, 0.01f });
        map.put(16, new Float[] { 0.06f, 0f, 0.04f });
        map.put(17, new Float[] { 0.058f, 0f, 0.014f });
        map.put(18, new Float[] { 0.055f, 0f, 0.01f });
        map.put(19, new Float[] { 0.052f, 0f, 0.012f });
        map.put(20, new Float[] { 0.049f, 0f, 0.02f });
    }
}
