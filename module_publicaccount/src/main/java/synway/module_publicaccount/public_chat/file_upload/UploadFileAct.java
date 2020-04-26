package synway.module_publicaccount.public_chat.file_upload;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.synway.filebrowser.FileBrowserActivity;
import com.synway.fileupload.FileUploadActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import qyc.library.control.dialog_confirm.DialogConfirm;
import qyc.library.control.dialog_confirm.OnDialogConfirmClick;
import qyc.library.control.dialog_msg.DialogMsg;
import synway.module_interface.config.netConfig.NetConfig;
import synway.module_interface.config.netConfig.Sps_NetConfig;
import synway.module_publicaccount.R;

//import java.io.File;
//import java.util.List;
//import synway.osc.chat.downvideo.*;

public class UploadFileAct extends Activity {
    private FileUploadTool fileUploadTool;
    private String url;
    private NetConfig netConfig = new NetConfig();
    private TextView textView;

    private ArrayList arrayList;
    private HashMap<Object, Integer> objectHashMap = new HashMap<>();

    private TextView progressTextView = null;
    private ProgressBar downProgressBar = null;
    private Button bt_stop;

    private View view = null;
    private TextView tvTitle = null;
    private ImageButton imgvBack = null;

    private int p = 0;
    private boolean isNeedClose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.model_activity_upload_file);

        isNeedClose = getIntent().getBooleanExtra("isNeedClose", false);
        textView= findViewById(R.id.textView3);
        downProgressBar = findViewById(R.id.progressBar1);
        progressTextView = findViewById(R.id.textView1);
        bt_stop = findViewById(R.id.bt_stop);
        bt_stop.setOnClickListener(onClickListener);

        view = findViewById(R.id.include1);
        tvTitle = view.findViewById(R.id.lblTitle);
        imgvBack = view.findViewById(R.id.back);
        imgvBack.setOnClickListener(onClickListener);
        tvTitle.setText("文件上传");
        netConfig= Sps_NetConfig.getNetConfigFromSpf(this);
        url = String.format("http://%s:%s/SynwayOSCFTP/FTP/FileUpload.osc", netConfig.ftpIP, netConfig.ftpPort);
//        url = String.format("http://%s:%s/SynwayOSCFTP/FTP/FileUpload.osc", netConfig.ftpIP, netConfig.ftpPort);
        fileUploadTool = new FileUploadTool();
        fileUploadTool.addListen(fileUploadLsn);
        if (isNeedClose) {
            FileBrowserActivity.actActivity4(UploadFileAct.this,101,"上传",1);
        } else {
            FileBrowserActivity.actActivity(UploadFileAct.this, 101, true);
        }

    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == bt_stop) {
                DialogConfirm.show(UploadFileAct.this, "提示", "停止所以文件的上传", new OnDialogConfirmClick() {
                    @Override
                    public void onDialogConfirmClick() {
                        fileUploadTool.stopAll();
                        UploadFileAct.this.finish();
                    }
                });
            } else if (v == imgvBack) {
                if (p == 100) {
                    UploadFileAct.this.finish();
                } else {
                    DialogConfirm.show(UploadFileAct.this, "提示", "退出本界面后将停止上传文件", new OnDialogConfirmClick() {
                        @Override
                        public void onDialogConfirmClick() {
                            fileUploadTool.stopAll();
                            UploadFileAct.this.finish();
                        }
                    });
                }

            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (data != null) {
                arrayList = data.getStringArrayListExtra(FileUploadActivity.SEND_PATH);
                if (arrayList != null) {
                    fileUploadTool.startUpload(arrayList, url);
                }
            } else {
                Toast.makeText(this, "未选择文件", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        imgvBack.performClick();
    }

    @Override
    protected void onDestroy() {
        fileUploadTool.destory();
        super.onDestroy();
    }

    private FileUploadLsn fileUploadLsn = new FileUploadLsn() {

        @Override
        public void onError(Object o, int errorCode, String errorMsg) {
            if (isNeedClose) {
                DialogConfirm.show(UploadFileAct.this, "提示", "上传出错,请退出重试!",
                    new OnDialogConfirmClick() {
                        @Override
                        public void onDialogConfirmClick() {
                            Intent intent = new Intent();
                            intent.putExtra("fileNetUrl", "fail");
                            setResult(RESULT_CANCELED, intent);
                            finish();
                        }
                    });
                return;
            }
            String path = ((UploadObj) o).filepath;
            String name = path.substring(path.lastIndexOf("/") + 1);
            DialogMsg.show(UploadFileAct.this, "提示", "文件:" + name + " 上传失败");
        }

        @Override
        public void onFinish(Object o,String resultJsonStr) {

            if (isNeedClose) {
                String fileNetUrl = "";
                try {
                    JSONObject resultJson = new JSONObject(resultJsonStr);
                    int resultCode = resultJson.optInt("RESPONSE_STATE", 500);
                    if (resultCode == 200) {
                        String netPath = resultJson.optString("NET_PATH", "");
                        fileNetUrl = String.format("http://%s:%s/" + netPath, netConfig.ftpIP, netConfig.ftpPort);
                    } else {
                        fileNetUrl = "";
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.putExtra("fileNetUrl", fileNetUrl);
                setResult(RESULT_OK,intent);
                finish();
            }
        }

        @Override
        public void onProgress(Object o, int progress, long bytesWritten) {
            objectHashMap.put(o, progress);
            int progressC = 0;
            Iterator iter = objectHashMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                int val = (int) entry.getValue();
                progressC += val;
            }
            p = progressC / arrayList.size();
            downProgressBar.setProgress(p);
            progressTextView.setText(p + "%");
            if(p==100){
                bt_stop.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.VISIBLE);
            }

        }
    };

}
