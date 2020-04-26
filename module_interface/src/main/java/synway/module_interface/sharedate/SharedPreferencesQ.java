package synway.module_interface.sharedate;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;

import java.util.UUID;

/**
 * 通过ContentProvider改装的SharedPreferences,以SharedPreferences的方式用于跨进程数据共享.
 * Created by 钱园超 on 2017/9/4.
 */

public class SharedPreferencesQ {

    private String name;
    private ContentResolver contentResolver;
    private OnShardPreferencesQListener onShardPreferencesQListener;
    private String guid;

    /**
     * 构造方法
     *
     * @param context context
     * @param name    name不可以是中文
     */
    public SharedPreferencesQ(@NonNull Context context, @NonNull String name) {
        this.contentResolver = context.getContentResolver();
        this.name = name;
        this.guid = UUID.randomUUID().toString();
    }

    public void clear() {
        Uri uri = Uri.parse(MContentProvider.URI_HOST + "/" + MContentProvider.BUSINESS_DELETE);
        contentResolver.delete(uri, "name=?", new String[]{name});
    }

    public void put(String key, int value) {
        mPut(key, String.valueOf(value));
    }

    public void put(String key, float value) {
        mPut(key, String.valueOf(value));
    }

    public void put(String key, double value) {
        mPut(key, String.valueOf(value));
    }

    public void put(String key, boolean value) {
        mPut(key, String.valueOf(value));
    }


    public void put(String key, String value) {
        mPut(key, value);
    }


    private void mPut(String key, String value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("key", key);
        contentValues.put("value", value);
        Uri uri = Uri.parse(MContentProvider.URI_HOST + "/" + MContentProvider.BUSINESS_INSERT_OR_UPDATE + "/" + name + "/" + guid);
        contentResolver.insert(uri, contentValues);
    }

    public int get(String key, int defValue) {
        int backValue = defValue;
        String value = mGet(key);

        try {
            backValue = Integer.valueOf(value);
        } catch (Exception e) {
            //
        }
        return backValue;
    }


    public float get(String key, float defValue) {
        float backValue = defValue;
        String value = mGet(key);

        try {
            backValue = Float.valueOf(value);
        } catch (Exception e) {
            //
        }
        return backValue;
    }

    public double get(String key, double defValue) {
        double backValue = defValue;
        String value = mGet(key);

        try {
            backValue = Double.valueOf(value);
        } catch (Exception e) {
            //
        }
        return backValue;
    }

    public boolean get(String key, boolean defValue) {
        boolean backValue = defValue;
        String value = mGet(key);

        try {
            backValue = Boolean.valueOf(value);
        } catch (Exception e) {
            //
        }
        return backValue;
    }

    public String get(String key, String defValue) {
        String value = mGet(key);
        return value == null ? defValue : value;
    }

    public String get(String key) {
        return mGet(key);
    }


    private String mGet(String key) {
        Uri uri = Uri.parse(MContentProvider.URI_HOST + "/" + MContentProvider.BUSINESS_READ);
        Cursor cursor = contentResolver.query(uri, new String[]{"value"},
                "name=? and key=?",
                new String[]{name, key},
                null);
        if (cursor == null) {
            return null;
        }
        String value = null;
        if (cursor.moveToNext()) {
            value = cursor.getString(cursor.getColumnIndex("value"));
        }
        cursor.close();
        return value;
    }

    /**
     * 添加数据监视器.<p>
     * 可以同来监视同一个name下的数据是否被修改.无论它是被你自己改的,还是被其他SharedPreferencesQ对象修改的,或者被其他进程修改,都可以在这里监视.
     * <br>注意:该监视并不会在意value是否真正被修改,只要同一个name被执行了put操作,监视器就会触发.
     *
     * @param listener 数据监听接口
     */
    public void addDataChangeListener(OnShardPreferencesQListener listener) {
        this.onShardPreferencesQListener = listener;
        //参数false表示该uri或uri祖先发生变化时会通知.
        //参数true表示该uri,或uri的祖先,或uri的后代发生变化时会通知.
        //这里要true,因为实际上put时,还要在uri后面加入一个辨识的guid,反馈的uri是它的后代.
        contentResolver.registerContentObserver(Uri.parse(MContentProvider.URI_HOST + "/" + MContentProvider.BUSINESS_INSERT_OR_UPDATE + "/" + name), true, contentObserver);
    }

    public void removeDataChangeListener() {
        this.onShardPreferencesQListener = null;
        contentResolver.unregisterContentObserver(contentObserver);
    }

    private ContentObserver contentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
//            关于selfChange的意义:
//            When ContentResolver#notifyChange(Uri,ContentObserver) is called with the ContentObserver object as the observer that initiated the change, that ContentObserver will have onChange() called with selfChange set to true

//            From the docs:
//            void android.content.ContentResolver.notifyChange(Uri uri, ContentObserver observer)
//            Parameters:
//            uri The uri of the content that was changed.
//            observer The observer that originated the change, may be null. The observer that originated the change will only receive the notification if it has requested to receive self-change notifications by implementing ContentObserver.deliverSelfNotifications() to return true.

//            So if you're calling notifyChange() from within your ContentProvider update/insert/delete methods which are in turn being called from the ContentResolver update/insert/delete methods, you won't have the ContentObserver reference to pass to notifyChange() and so won't have selfChange set to true.
            String guid = uri.getLastPathSegment();
            if (onShardPreferencesQListener != null) {
                onShardPreferencesQListener.onChanged(name, guid.equals(SharedPreferencesQ.this.guid));
            }
            super.onChange(selfChange, uri);
        }
    };

    public interface OnShardPreferencesQListener {
        void onChanged(String name, boolean isSelf);
    }


}
