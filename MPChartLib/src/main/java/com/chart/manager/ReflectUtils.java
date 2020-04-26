package com.chart.manager;

import android.text.TextUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by leo on 2018/12/3.
 */

public class ReflectUtils {
    /**
     * 反射的方式设置某个类的成员变量的值
     *
     * @param paramClass  类对象
     * @param paramString 域的名称
     * @param newClass    新的对象
     */
    public static void setField(Object paramClass, String paramString, Object newClass) {
        if (paramClass == null || TextUtils.isEmpty(paramString)) return;
        Field field = null;
        Class cl = paramClass.getClass();
        for (; field == null && cl != null; ) {
            try {
                field = cl.getDeclaredField(paramString);
                if (field != null) {
                    field.setAccessible(true);
                }
            } catch (Throwable ignored) {

            }
            if (field == null) {
                cl = cl.getSuperclass();
            }
        }
        if (field != null) {
            try {
                field.set(paramClass, newClass);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            System.err.println(paramString + " is not found in " + paramClass.getClass().getName());
        }
    }

    /**
     * 反射的方式获取某个类的方法
     *
     * @param cl             类的class
     * @param name           方法名称
     * @param parameterTypes 方法对应的输入参数类型
     * @return 方法
     */
    public static Method getMethod(Class cl, String name, Class... parameterTypes) {
        Method method = null;
        for (; method == null && cl != null; ) {
            try {
                method = cl.getDeclaredMethod(name, parameterTypes);
                if (method != null) {
                    method.setAccessible(true);
                }
            } catch (Exception ignored) {

            }
            if (method == null) {
                cl = cl.getSuperclass();
            }
        }
        return method;
    }

    /**
     * 反射的方式获取某个类的某个成员变量值
     *
     * @param paramClass  类对象
     * @param paramString field的名字
     * @return field对应的值
     */
    public static Object getField(Object paramClass, String paramString) {
        if (paramClass == null) return null;
        Field field = null;
        Object object = null;
        Class cl = paramClass.getClass();
        for (; field == null && cl != null; ) {
            try {
                field = cl.getDeclaredField(paramString);
                if (field != null) {
                    field.setAccessible(true);
                }
            } catch (Exception ignored) {

            }
            if (field == null) {
                cl = cl.getSuperclass();
            }
        }
        try {
            if (field != null)
                object = field.get(paramClass);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return object;
    }
}
