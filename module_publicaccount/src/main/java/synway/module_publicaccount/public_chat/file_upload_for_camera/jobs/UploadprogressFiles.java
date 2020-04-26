package synway.module_publicaccount.public_chat.file_upload_for_camera.jobs;

import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.UUID;

import qyc.tool.qjob.QJobTask;
import synway.common.upload.UploadFileQueue;
import synway.module_publicaccount.public_chat.file_upload.CamreaUpload;
import synway.module_publicaccount.public_chat.file_upload_for_camera.entity.MediaFile;
import synway.module_publicaccount.public_chat.file_upload_for_camera.entity.UploadFiles;
import synway.module_publicaccount.until.StringUtil;

/**
 * 公众号上传图片/视频
 *
 * @author 朱铁超
 */

public class UploadprogressFiles extends QJobTask<UploadFiles> {

    private String ftpIP;
    private int ftoPort;
    private String showErrorInfoJob = null;

    private String mSuffix;
    private UploadProgress mUploadProgress;

    private boolean beRemove = false; //是否被移除，移除不再进行上传操作

    public UploadprogressFiles(String ip, int port, String showErrorInfoJob, String suffix,UploadProgress uploadProgress) {
        this.ftpIP = ip;
        this.ftoPort = port;
        this.mSuffix = suffix;
        this.showErrorInfoJob = showErrorInfoJob;
        this.mUploadProgress = uploadProgress;
    }

    private volatile String msgId =null;
    @Override
    public boolean onStart(UploadFiles arrestInfo) {
        beRemove = false;
        //上传图片文件
        if(arrestInfo.getLocalFiles() == null || arrestInfo.getLocalFiles().size()==0){
            if (showErrorInfoJob != null) {
                startJob(showErrorInfoJob, "上传失败，本地文件不存在!");
            }
            return false;
        }

        List<String> picList = arrestInfo.getLocalFiles();
        List<MediaFile> videoNetPaths = arrestInfo.getNetFiles();
       int i= 0;
        for (String path : picList) {
            if(beRemove) break;
            if(mUploadProgress!=null){
                mUploadProgress.progress(picList.size(),++i,-1,-1);
            }
            if(StringUtil.isEmpty(path)){
               continue;
            }
            File file = new File(path);
            if (!file.exists()) {
                continue;
            }
            times = 0;
            String[] result = getUrlPath(path, mSuffix, getTypeFromSuffix(mSuffix),mUploadProgress, new UploadManger() {
                @Override
                public void setMsgID(String msgID) {
                    msgId = msgID;
                }
            });
            if (StringUtil.isEmpty(result[0])) {
                // 失败了
                if (showErrorInfoJob != null) {
                    startJob(showErrorInfoJob, result);
                }
                return false;
            }
            MediaFile mediaFile = new MediaFile();
            mediaFile.setLocalPath(path);
            mediaFile.setNetPath(result[0]);
            videoNetPaths.add(mediaFile);
//            if(mUploadProgress!=null){
//                mUploadProgress.progress(picList.size(),i,100,100);
//            }
        }
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            //
//        }
        return true;
    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        beRemove = true;
        if(!StringUtil.isEmpty(msgId)){
            times = 3;//取消后重连次数设成最大，防止取消上传后再重连上传
            UploadFileQueue.instance().stopCall(msgId);
        }
    }

    @Override
    protected boolean isUIThread() {
        return false;
    }

    private int times = 0;//单个文件上传次数

    /**
     * 单个文件上传并获取网络地址
     *
     * @param path
     * @param suffix 文件后缀
     * @param type   1=图片 2=视频 3=声音 4=其他
     * @return
     */
    private String[] getUrlPath(String path, String suffix, int type,UploadProgress uploadProgress,UploadManger uploadManger) {
        times = times + 1;
        String uploadresult[] = new String[2];
        String urlStr = String.format("http://%s:%s/SynwayOSCFTP/FTP/FileUpload.osc", ftpIP, ftoPort);
        String neturl = "";
        String deatil = "";
        String msgID = UUID.randomUUID().toString();
        if(uploadManger!=null){
            uploadManger.setMsgID(msgID);
        }
        String[] result = CamreaUpload.uploadFileSyn(urlStr, path, suffix, type, msgID,uploadProgress);
        try {
            deatil = result[1];
            JSONObject resultJson = new JSONObject(result[1]);
            int resultCode = resultJson.optInt("RESPONSE_STATE", 500);

            if (resultCode == 200) {
                String fileUrl = resultJson.optString("NET_PATH", "");
                neturl = String.format("http://%s:%s/" + fileUrl, ftpIP, ftoPort);
                uploadresult[0] = neturl;
                uploadresult[1] = deatil;
            } else {
                neturl = "";
                deatil = result[1];
            }
        } catch (Exception e) {
            uploadresult[0] = neturl;
            uploadresult[1] = deatil;
        }

        if (StringUtil.isEmpty(neturl)) {
            if (times < 3) {
                getUrlPath(path, suffix, type,uploadProgress,new UploadManger() {
                    @Override
                    public void setMsgID(String msgID) {
                        msgId = msgID;
                    }
                });
            }
        }
        uploadresult[0] = neturl;
        uploadresult[1] = deatil;

        return uploadresult;
    }

    /**
     * 1=图片 2=视频 3=声音 4=其他
     */
    private static int getTypeFromSuffix(String suffix) {
        //当文件后缀名为大写的 .JPG .PNG 格式的情况,需要转小写进行比对,不然会识别为附件格式
        suffix = suffix.toLowerCase();
        if (suffix.equals("png")) {
            return 1;
        } else if (suffix.equals("jpg")) {
            return 1;
        } else if (suffix.equals("bmp")) {
            return 1;
        } else if (suffix.equals("amr")) {
            return 3;
        } else if (suffix.equals("mp3")) {
            return 3;
        } else if (suffix.equals("wav")) {
            return 3;
        } else if (suffix.equals("3gp")) {
            return 2;
        } else if (suffix.equals("mp4")) {
            return 2;
        } else {
            return 4;
        }

    }

    /**
     * 获取后缀名，不带“.”
     */
    private static String getSuffix(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

}
