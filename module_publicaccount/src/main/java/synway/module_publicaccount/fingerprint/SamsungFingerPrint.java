package synway.module_publicaccount.fingerprint;

import android.content.Context;
import android.os.Handler;
import android.util.SparseArray;
import android.webkit.JavascriptInterface;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pass.Spass;
import com.samsung.android.sdk.pass.SpassFingerprint;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 三星指纹认证模块
 * 需要在AndroidManifest.xml中添加以下权限
 * <uses-permission android:name="com.samsung.android.providers.context.permission.WRITE_USE_APP_FEATURE_SURVEY"/>
 */
public class SamsungFingerPrint implements FingerPrintI {

    private Context context;
    private Spass spass;
    private SpassFingerprint spassFingerprint;
    private boolean isFeatureEnabled_fingerprint = false;
    private boolean isFeatureEnabled_index = false;
    private boolean isFeatureEnabled_uniqueId = false;
    private ArrayBlockingQueue<String> queueDialog;
    private ArrayBlockingQueue<String> queueNoDialog;
    private ArrayBlockingQueue<String> queueRegister;
    Handler handler;

    public SamsungFingerPrint(Context context) {
        handler=new Handler();
        this.context = context;
        queueDialog = new ArrayBlockingQueue<>(1);
        queueNoDialog = new ArrayBlockingQueue<String>(1);
        queueRegister = new ArrayBlockingQueue<String>(1);
        if (spass == null) {
            spass = new Spass();

            try {
                spass.initialize(context);
            } catch (SsdkUnsupportedException e) {
                e.printStackTrace();
                return;
            } catch (UnsupportedOperationException e) {
                e.printStackTrace();
                return;
            }
        }

        // 本设备是否支持指纹
        isFeatureEnabled_fingerprint = spass
                .isFeatureEnabled(Spass.DEVICE_FINGERPRINT);
        // 本设备是否支持对指纹记录index
        isFeatureEnabled_index = spass
                .isFeatureEnabled(Spass.DEVICE_FINGERPRINT_FINGER_INDEX);
        // 本设备是否支持对指纹记录UniqueID
        isFeatureEnabled_uniqueId = spass
                .isFeatureEnabled(Spass.DEVICE_FINGERPRINT_UNIQUE_ID);


        if (isFeatureEnabled_fingerprint && isFeatureEnabled_index
                && isFeatureEnabled_uniqueId) {
            if (spassFingerprint == null) {
                spassFingerprint = new SpassFingerprint(context);
            }
        }
    }


    @Override
    @JavascriptInterface
    public String getFingerprintUniqueID() {
        if (spassFingerprint != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    spassFingerprint.startIdentifyWithDialog(context,
                            mIdentifyListenerDialog, false);
                }
            });
            String id = "";
            try {
                id = queueDialog.take();
            } catch (InterruptedException e) {
            }
            return id;
        } else {
            return "";
        }
    }

    @Override
    /**
     * This may take some time,and never try to call it in main thread in order to avoid ANR.
     */
    public String identifyWithoutDialog() {
        if (spassFingerprint != null) {
            try {
                spassFingerprint.cancelIdentify();
            } catch (Exception e) {
            }
            try {
                spassFingerprint.startIdentify(mIdentyfyListener);
            } catch (Exception e) {
                return "";
            }
        }
        String result = "";
        try {
            result = queueNoDialog.take();
        } catch (InterruptedException e) {
        }
        return result;
    }

    @Override
    public boolean isFingerprintSupported() {
        return isFeatureEnabled_fingerprint && isFeatureEnabled_index
                && isFeatureEnabled_uniqueId;
    }

    // 这个变量是用来计算注册结果的，当调用register()的时候，会计算一次当前的最大index；
    // 当register onFinish()的时候会再计算一次当前的最大index，两个值比较就可以知道register是否成功
    // 然后maxIndex会被重置为-1
    private int maxIndex = -1;

    @Override
    public String register() {
        if (spassFingerprint == null) {
            throw new RuntimeException("Fingerprint is not supported.");
        }
        maxIndex = getMaxIndex();
        spassFingerprint.registerFinger(context, mRegisterListener);
        String result = "";
        try {
            result = queueRegister.take();
        } catch (InterruptedException e) {
        }
        return result;
    }

    @Override
    public void cancelIdentify() {
        spassFingerprint.cancelIdentify();
    }

    @Override
    public boolean hasRegisteredFinger() {
        if(spassFingerprint==null){
            return false;
        }
        return spassFingerprint.hasRegisteredFinger();
    }

    private String getUniqueID(int index) {
        String id = "";
        SparseArray<String> ids = spassFingerprint.getRegisteredFingerprintUniqueID();
        for (int i = 0; i < ids.size(); i++) {
            if (ids.keyAt(i) == index) {
                id = ids.get(ids.keyAt(i));
            }
        }
        return id;
    }

    /** 获取本地已采集指纹数量 */
    private int getRegisteredCount(){
        return 0;
    }

    private int getMaxIndex() {
        if (spassFingerprint == null) {
            throw new RuntimeException("Fingerprint is not supported.");
        }
        int max_index = 0;
        SparseArray<String> names = spassFingerprint.getRegisteredFingerprintName();
        if(names==null){
            return 0;
        }
        for (int i = 0; i < names.size(); i++) {
            if (names.keyAt(i) > max_index) {
                max_index = names.keyAt(i);
            }
        }
        return max_index;
    }

    /**
     * 指纹注册监听
     */
    private SpassFingerprint.RegisterListener mRegisterListener = new SpassFingerprint.RegisterListener() {
        @Override
        public void onFinished() {
            // 注册过程有这么一些结果:
            // 1.注册指纹成功
            // 2.注册指纹失败
            // 3.用户取消注册
            int currentMaxIndex = getMaxIndex();
            String uniqueID;
            if (maxIndex!= -1 && currentMaxIndex > maxIndex) {
                uniqueID = getUniqueID(currentMaxIndex);// 注册指纹成功
            } else {
                uniqueID = "";// 注册指纹失败
            }
            try {
                queueRegister.put(uniqueID);
            } catch (InterruptedException e) {
            }

        }
    };

    /**
     * 不带对话框验证时的监听
     */
    private SpassFingerprint.IdentifyListener mIdentyfyListener = new SpassFingerprint.IdentifyListener() {
        @Override
        public void onFinished(int eventStatus) {
            int fingerprintIndex = 0;
            try {
                fingerprintIndex = spassFingerprint
                        .getIdentifiedFingerprintIndex();
            } catch (IllegalStateException ise) {
                try {
                    queueNoDialog.put("");
                } catch (InterruptedException e) {
                }
            }


            if (eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_SUCCESS) {
                @SuppressWarnings("unchecked")
                SparseArray<String> mList = spassFingerprint
                        .getRegisteredFingerprintUniqueID();
                String id = "";
                for (int i = 0; i < mList.size(); i++) {
                    int keyIndex = mList.keyAt(i);
                    if (keyIndex == fingerprintIndex) {
                        id = mList.get(keyIndex);
                    }
                }
                try {
                    queueNoDialog.put(id);
                } catch (InterruptedException e) {
                }
            } else {
                // 有结果就结束，不论成功失败
                try {
                    spassFingerprint.cancelIdentify();
                } catch (Exception e) {
                }
                try {
                    queueNoDialog.put("");
                } catch (InterruptedException e) {
                }
            }
        }

        @Override
        public void onReady() {
        }

        @Override
        public void onStarted() {
        }

        @Override
        public void onCompleted() {
        }
    };

    /**
     * 带对话框验证时的监听
     */
    private SpassFingerprint.IdentifyListener mIdentifyListenerDialog = new SpassFingerprint.IdentifyListener() {
        @Override
        public void onFinished(int eventStatus) {
            int fingerprintIndex = 0;
            try {
                fingerprintIndex = spassFingerprint
                        .getIdentifiedFingerprintIndex();
            } catch (IllegalStateException ise) {
                try {
                    queueDialog.put("");
                } catch (InterruptedException e) {
                }
            }

            if (eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_SUCCESS) {
                @SuppressWarnings("unchecked")
                SparseArray<String> mList = spassFingerprint
                        .getRegisteredFingerprintUniqueID();
                String id = "";
                for (int i = 0; i < mList.size(); i++) {
                    int keyIndex = mList.keyAt(i);
                    if (keyIndex == fingerprintIndex) {
                        id = mList.get(keyIndex);
                    }
                }
                try {
                    queueDialog.put(id);
                } catch (InterruptedException e) {
                }
            } else {
                spassFingerprint.cancelIdentify();
                try {
                    queueDialog.put("");
                } catch (InterruptedException e) {
                }
            }
        }

        @Override
        public void onReady() {
        }

        @Override
        public void onStarted() {
        }

        @Override
        public void onCompleted() {
        }
    };

}
