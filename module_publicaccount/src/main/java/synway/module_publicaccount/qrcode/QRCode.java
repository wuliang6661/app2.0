package synway.module_publicaccount.qrcode;

import android.content.Intent;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import java.util.concurrent.ArrayBlockingQueue;
import synway.module_publicaccount.public_chat.PAWebViewAct;
import ysm.qrcode.QrQodeact.QrMainActivity;

public class QRCode implements QRCodeI {

    /**
     *
     */
    public static final int REQUEST_CODE = 1225;
    private WebView webView;
    private PAWebViewAct paWebViewAct;
    private ArrayBlockingQueue<String> queueI;
    public QRCode( WebView webView, PAWebViewAct activity) {
        this.paWebViewAct=activity;
        this.webView = webView;
        queueI = new ArrayBlockingQueue<>(1);
//        queue = new ArrayBlockingQueue<>(1);
    }


//    @Subscriber(tag = "QRCODE")
//    public void onQRCodeResult(String s) {
//        try {
//            queue.put(s);
//        } catch (InterruptedException e) {
//        }
//    }

//    @Override
//    @JavascriptInterface
//    public String QRCodeResultFn() {
//        String result = null;
//        // 打开二维码扫描
//        Intent intent = new Intent();
//        intent.setClass(context, CaptureActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        EventBus.getDefault().register(this);
//        context.startActivity(intent);
//        try {
//            result = queue.take();
//        } catch (InterruptedException e) {
//        }
//        EventBus.getDefault().unregister(this);
//        if (result != null) {
//            return result;
//        }
//        return "";
//    }

    String result = null;
    private Handler mHandler = new Handler();
    String  resulttype=null;
    /**
     * 打开二维码并调用js方法
     * 不需要返回值
     */
//    @Override
//    @JavascriptInterface
//    public void QRCodeInterfaceFn(String interfacetype) {
//        // 打开二维码扫描
//        Intent intent = new Intent();
//        intent.setClass(context, CaptureActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        EventBus.getDefault().register(this);
//        context.startActivity(intent);
//        try {
//            resulttype=interfacetype;
//            result = queue.take();
//            EventBus.getDefault().unregister(this);
//            webView.post(new Runnable() {
//                @Override
//                public void run() {
//                    webView.loadUrl("javascript:callQRCodeFn('"+result+"','"+resulttype+"')");
//                }
//            });
//        } catch (InterruptedException e) {
//        }
//    }
    /**
     * 打开二维码并调用js方法
     * 不需要返回值
     */
    @Override
    @JavascriptInterface
    public void QRCodeInterfaceFn(String typeresult) {
        Intent intent=new Intent();
        intent.setClass(paWebViewAct, QrMainActivity.class);
        paWebViewAct.startActivityForResult(intent,1225);
        try {
            result = queueI.take();
            if(result.equals("ysm no result")){
                return;
            }
            resulttype=typeresult;
            webView.post(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl("javascript:callQRCodeFn('"+result+"','"+resulttype+"')");
                }
            });
        } catch (InterruptedException e) {
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == -1) {
            String qoderesult = data.getStringExtra("QrCode_Result");
            try {
                queueI.put(qoderesult);
            } catch (InterruptedException e) {
            }
        }
    }


    public void destroy() {
        if (queueI != null) {
            try {
                queueI.put("noresult");
            } catch (InterruptedException e) {
            }
        }
    }
}
