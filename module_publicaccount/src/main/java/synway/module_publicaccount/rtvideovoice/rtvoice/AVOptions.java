package synway.module_publicaccount.rtvideovoice.rtvoice;

/**
 * Created by 朱铁超 on 2018/5/22.
 */


import java.util.HashMap;
import java.util.Map;

public class AVOptions {
    public static final int MEDIA_CODEC_SW_DECODE = 0;
    public static final int MEDIA_CODEC_HW_DECODE = 1;
    public static final int MEDIA_CODEC_AUTO = 2;
    public static final int PREFER_FORMAT_M3U8 = 1;
    public static final int PREFER_FORMAT_MP4 = 2;
    public static final int PREFER_FORMAT_FLV = 3;
    public static final String KEY_PREPARE_TIMEOUT = "timeout";
    public static final String KEY_MEDIACODEC = "mediacodec";
    public static final String KEY_LIVE_STREAMING = "live-streaming";
    public static final String KEY_CACHE_BUFFER_DURATION = "cache-buffer-duration";
    public static final String KEY_MAX_CACHE_BUFFER_DURATION = "max-cache-buffer-duration";
    public static final String KEY_DRM_KEY = "drm-key";
    public static final String KEY_CACHE_DIR = "cache-dir";
    public static final String KEY_CACHE_EXT = "cache-ext";
    public static final String KEY_VIDEO_DATA_CALLBACK = "video-data-callback";
    public static final String KEY_AUDIO_DATA_CALLBACK = "audio-data-callback";
    public static final String KEY_VIDEO_RENDER_EXTERNAL = "video-render-external";
    public static final String KEY_AUDIO_RENDER_EXTERNAL = "audio-render-external";
    public static final String KEY_FAST_OPEN = "fast-open";
    public static final String KEY_PREFER_FORMAT = "prefer-format";
    public static final String KEY_DNS_SERVER = "dns-server";
    public static final String KEY_DOMAIN_LIST = "domain-list";
    public static final String KEY_SEEK_MODE = "accurate-seek";
    public static final String KEY_OPEN_RETRY_TIMES = "open-retry-times";
    public static final String KEY_LOG_LEVEL = "log-level";
    private Map<String, Object> a = new HashMap();

    public AVOptions() {
    }

    public final boolean containsKey(String name) {
        return this.a.containsKey(name);
    }

    public final int getInteger(String name) {
        return ((Integer)this.a.get(name)).intValue();
    }

    public final int getInteger(String name, int defaultValue) {
        try {
            return this.getInteger(name);
        } catch (NullPointerException var4) {
        } catch (ClassCastException var5) {
        }

        return defaultValue;
    }

    public final long getLong(String name) {
        return ((Long)this.a.get(name)).longValue();
    }

    public final float getFloat(String name) {
        return ((Float)this.a.get(name)).floatValue();
    }

    public final String getString(String name) {
        return (String)this.a.get(name);
    }

    public final Object getByteArray(String name) {
        return this.a.get(name);
    }

    public final Object getStringArray(String name) {
        return this.a.get(name);
    }

    public final Object getIntegerArray(String name) {
        return this.a.get(name);
    }

    public final void setInteger(String name, int value) {
        this.a.put(name, Integer.valueOf(value));
    }

    public final void setLong(String name, long value) {
        this.a.put(name, Long.valueOf(value));
    }

    public final void setFloat(String name, float value) {
        this.a.put(name, Float.valueOf(value));
    }

    public final void setString(String name, String value) {
        this.a.put(name, value);
    }

    public final void setByteArray(String name, byte[] value) {
        this.a.put(name, value);
    }

    public final void setStringArray(String name, String[] value) {
        this.a.put(name, value);
    }

    public final void setIntegerArray(String name, int[] value) {
        this.a.put(name, value);
    }

    public Map<String, Object> getMap() {
        return this.a;
    }
}
