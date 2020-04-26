package cn.synway.app.utils;

import android.app.Activity;

import java.util.Stack;

import cn.synway.app.ui.login.LoginActivity;
import cn.synway.app.ui.main.MainActivity;

/**
 * 作者 by wuliang 时间 16/10/31.
 * <p>
 * 建立一个activity的栈
 */

public class AppManager {
    private static Stack<Activity> activityStack;
    private static AppManager mInstance;

    private AppManager() {
    }


    public static AppManager getAppManager() {
        if (mInstance == null) {
            synchronized (AppManager.class) {
                if (mInstance == null) {
                    mInstance = new AppManager();
                }
            }
        }
        return mInstance;
    }

    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        activityStack.add(activity);
    }

    public void removeActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
        }
    }


    public Activity curremtActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }


    public void finishActivity(Activity activity) {
        if (activity != null && activityStack.contains(activity) && !activity.isFinishing()) {
            activityStack.remove(activity);
            activity.finish();
        }
    }

    public void goHome() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i) && !(activityStack.get(i) instanceof MainActivity)) {
                activityStack.get(i).finish();
            }
        }
    }


    public void goLogin() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i) && !(activityStack.get(i) instanceof LoginActivity)) {
                activityStack.get(i).finish();
            }
        }
    }

    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }
}
