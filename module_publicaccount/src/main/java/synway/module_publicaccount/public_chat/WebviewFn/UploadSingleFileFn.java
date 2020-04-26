package synway.module_publicaccount.public_chat.WebviewFn;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;
import java.util.concurrent.ArrayBlockingQueue;
import synway.module_publicaccount.public_chat.PAWebViewAct;
import synway.module_publicaccount.public_chat.file_upload.UploadFileAct;

import static android.app.Activity.RESULT_OK;

/**
 * Created by leo on 2018/6/26.
 */

public class UploadSingleFileFn {

    public static final int RESQUEST_CODE = 77098;
    private PAWebViewAct act;

    private ArrayBlockingQueue<String> queueFile;
    public UploadSingleFileFn(PAWebViewAct act) {
        this.act = act;
        this.queueFile = new ArrayBlockingQueue<String>(1);
    }

    @JavascriptInterface
    public String uploadSingleFileFn(){
        String result = null;

        Intent intent = new Intent(act, UploadFileAct.class);
        intent.putExtra("isNeedClose",true);
        act.startActivityForResult(intent,RESQUEST_CODE);

        try {
            Log.d("dym------------------->", "阻塞等待结果");
            result = queueFile.take();
            Log.d("dym------------------->", "获取到结果 "+result);
            if(result.equals("fail") || TextUtils.isEmpty(result)){
                return null;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }

        return result;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK) {

            Toast.makeText(act, "上传成功", Toast.LENGTH_SHORT).show();
            String netUrl = data.getStringExtra("fileNetUrl");
            if (TextUtils.isEmpty(netUrl)) {
                try {
                    queueFile.put("fail");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    queueFile.put(netUrl);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(act, "上传失败", Toast.LENGTH_SHORT).show();
            try {
                queueFile.put("fail");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void destroy() {
        if (queueFile != null) {
            try {
                queueFile.put("fail");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
