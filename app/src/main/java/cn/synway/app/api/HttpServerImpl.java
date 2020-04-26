package cn.synway.app.api;

import android.annotation.SuppressLint;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import cn.synway.app.api.DownloadResponseBody.DownloadListener;
import cn.synway.app.api.rx.RxResultHelper;
import cn.synway.app.bean.AppConfigBO;
import cn.synway.app.bean.FcTypeBo;
import cn.synway.app.bean.MessageFcBo;
import cn.synway.app.bean.MessageListBo;
import cn.synway.app.bean.MsgSelectFcBo;
import cn.synway.app.bean.MyAreaDO;
import cn.synway.app.bean.NetAPPBO;
import cn.synway.app.bean.OrganDao;
import cn.synway.app.bean.PersonInPsListBO;
import cn.synway.app.bean.VersionBO;
import cn.synway.app.bean.VersionNodeBO;
import cn.synway.app.bean.request.BindIMEIRequest;
import cn.synway.app.bean.request.DownMapRequest;
import cn.synway.app.bean.request.EditPassRequest;
import cn.synway.app.bean.request.FacePackgeRequest;
import cn.synway.app.bean.request.FaceRequest;
import cn.synway.app.bean.request.FcTypeRequest;
import cn.synway.app.bean.request.LoginRequest;
import cn.synway.app.bean.request.MessageKeyRequest;
import cn.synway.app.bean.request.MessageRequest;
import cn.synway.app.bean.request.MsgSelectRequest;
import cn.synway.app.bean.request.PersonRequest;
import cn.synway.app.bean.request.UpdatePicRequest;
import cn.synway.app.bean.request.UpdateReadRequest;
import cn.synway.app.bean.request.UpdateRequest;
import cn.synway.app.bean.request.UserRequest;
import cn.synway.app.bean.request.WaterRequest;
import cn.synway.app.config.Config;
import cn.synway.app.db.dbmanager.UserIml;
import cn.synway.app.db.table.UserEntry;
import cn.synway.app.utils.DeviceUtils;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Observable;

public class HttpServerImpl {


    public static HttpService service;
    private static HttpService downLoadService;

    /**
     * 获取代理对象
     *
     * @return
     */
    public static HttpService getService() {
        if (service == null)
            service = ApiManager.getInstance().configRetrofit(HttpService.class, "http://" +
                    Config.getServerIp() + ":" + Config.getServerPort());
        return service;
    }

    /**
     * 获取代理对象
     *
     * @return
     */
    public static HttpService getDownLoadService(DownloadListener listener) {
//        if (downLoadService == null) {
        downLoadService = ApiManager.getInstance().downloadConfigRetrofit(HttpService.class, "http://" +
                Config.getServerIp() + ":" + Config.getServerPort(), listener);
//        }
        return downLoadService;

    }

    /**
     * 登录
     */
    public static Observable<AppConfigBO> login(LoginRequest request) {
        return getService().login(request).compose(RxResultHelper.httpRusult());
    }

    /**
     * 获取用户信息
     */
    public static Observable<String> bindImei(String Imei) {
        BindIMEIRequest request = new BindIMEIRequest();
        request.IMEI = Imei;
        return getService().bindImei(request).compose(RxResultHelper.httpRusult());
    }

    /**
     * 人脸识别
     */
    public static Observable<String> faceAuther(String face) {
        FaceRequest request = new FaceRequest();
        request.file64 = face;
        return getService().faceAuth(null).compose(RxResultHelper.httpRusult());
    }

    /**
     * 获取用户信息
     */
    public static Observable<UserEntry> getUserInfo() {
        return getService().getUserInfo().compose(RxResultHelper.httpRusult());
    }

    /**
     * 检查更新
     */
    public static Observable<VersionBO> checkUpdate() {
        UpdateRequest request = new UpdateRequest();
        request.versionCode = AppUtils.getAppVersionCode() + "";
        return getService().checkUpdate(request).compose(RxResultHelper.httpRusult());
    }

    /**
     * 获取版本更新列表
     */
    public static Observable<List<VersionNodeBO>> getVersionList() {
        return getService().getVersionList().compose(RxResultHelper.httpRusult());
    }

    /**
     * 修改密码接口
     */
    public static Observable<String> updatePass(String oldPass, String newPass) {
        EditPassRequest request = new EditPassRequest();
        request.setNewPWD(newPass);
        request.setOldPWD(oldPass);
        return getService().editPassword(request).compose(RxResultHelper.httpRusult());
    }

    /**
     * 通讯录列表
     */
    public static Observable<PersonInPsListBO> getPeopleList(PersonRequest request) {
        return getService().getPeopleList(request).compose(RxResultHelper.httpRusult());

    }

    /**
     * 人员名片
     */
    public static Observable<UserEntry> getUserInfoById(String id) {
        UserRequest userRequest = new UserRequest();
        userRequest.userId = id;
        return getService().getUserInfoById(userRequest).compose(RxResultHelper.httpRusult());
    }

    /**
     * 查人 name 模糊
     */
    public static Observable<List<UserEntry>> getUserInfoByName(String name) {
        UserRequest userRequest = new UserRequest();
        userRequest.keyword = name;
        return getService().getUserInfoByName(userRequest).compose(RxResultHelper.httpRusult());

    }

    /**
     * 机构模糊查询
     */
    public static Observable<List<OrganDao>> getOrganInfoByName(String name) {
        UserRequest userRequest = new UserRequest();
        userRequest.keyword = name;
        return getService().getOrganInfoByName(userRequest).compose(RxResultHelper.httpRusult());

    }

    /**
     * 地图信息
     */
    public static Observable<List<MyAreaDO>> getMapInfo() {
        return getService().getMapInfo().compose(RxResultHelper.httpRusult());
    }

    /**
     * 地图下载地址
     */
    public static Observable<String> getDownMap(DownMapRequest downMapRequest) {
        return getService().getDownMap(downMapRequest).compose(RxResultHelper.httpRusult());
    }

    /**
     * 获取应用消息列表
     */
    public static Observable<List<MessageFcBo>> getFcList() {
        return getService().getFcList().compose(RxResultHelper.httpRusult());
    }

    /**
     * 根据消息查询类型
     */
    public static Observable<List<FcTypeBo>> getFcType(String fcId) {
        FcTypeRequest request = new FcTypeRequest();
        request.pushType = fcId;
        return getService().getTypeByFc(request).compose(RxResultHelper.httpRusult());
    }

    /**
     * 根据消息列表
     */
    public static Observable<MessageListBo> getMessageList(MessageRequest request) {
        return getService().getMessageList(request).compose(RxResultHelper.httpRusult());
    }


    /**
     * 根据内容查询消息
     */
    public static Observable<MessageListBo> getMessageByKey(MessageKeyRequest request) {
        return getService().getMessageListByKey(request).compose(RxResultHelper.httpRusult());
    }


    /**
     * 更新消息为已读
     */
    public static Observable<String> updateRead(String pushId) {
        UpdateReadRequest request = new UpdateReadRequest();
        request.pushId = pushId;
        return getService().updateRead(request).compose(RxResultHelper.httpRusult());
    }


    /**
     * 检查服务器是否可用
     */
    public static Observable<AppConfigBO> checkConfig(String ip, String port) {
        return getService().checkConfig(ip, port).compose(RxResultHelper.httpRusult());
    }

    /**
     * 下载
     *
     * @param url              下载地址，全路径
     * @param downloadListener 进度监听
     * @param file             保存地址文件
     * @return
     */
    public static Observable<ResponseBody> downLoad(String url, DownloadListener
            downloadListener, File file) {
        //url = "http://172.18.100.26:8080/manager/images/kkk.7z";
        return getDownLoadService(downloadListener).download(url).compose(RxResultHelper.downRequest(file));
    }

    /**
     * 应用中心内置的应用
     */
    public static Observable<List<NetAPPBO>> getNewAPP() {
        return getService().getNewAPP().compose(RxResultHelper.httpRusult());
    }

    /**
     * 消息已读设置
     */
    public static Observable<Object> setMessageRead() {
        return getService().setMessageRead().compose(RxResultHelper.httpRusult());
    }

    /**
     * 上传文件
     */
    public static Observable<String> updateImage(File file) {
        File compressedImageFile;
        try {
            compressedImageFile = new Compressor(Utils.getApp()).setQuality(30).compressToFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            compressedImageFile = file;
        }
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), compressedImageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("fileName", file.getName(), requestFile);
        return getService().updateFile(body).compose(RxResultHelper.httpRusult());
    }


    /**
     * 更新用户头像
     */
    public static Observable<String> updateUserImage(String imageUrl) {
        UpdatePicRequest request = new UpdatePicRequest();
        request.userPic = imageUrl;
        return getService().updateUserImage(request).compose(RxResultHelper.httpRusult());
    }


    /**
     * 根据消息类型查询可搜索的应用
     */
    public static Observable<List<MsgSelectFcBo>> getDataBySelectType(int type) {
        MsgSelectRequest request = new MsgSelectRequest();
        request.androidType = type;
        return getService().getFunctionList(request).compose(RxResultHelper.httpRusult());
    }

    /**
     * 增加水印
     */
    @SuppressLint("MissingPermission")
    public static Observable<String> addWaterMaker() {
        WaterRequest request = new WaterRequest();
//        LocationManager lm = (LocationManager) Utils.getApp().getSystemService(Context.LOCATION_SERVICE);
//        Location mLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        request.loginLatitude = mLocation.getLatitude() + "";
//        request.loginLongitude = mLocation.getLongitude() + "";
        request.loginLatitude = "100.0012";
        request.loginLongitude = "200.0012";
        request.phoneCode = DeviceUtils.getMEIDCode();
        request.funcVersion = AppUtils.getAppVersionName();
        request.loginCode = UserIml.getUser().getCode();
        request.serverIp = Config.getServerIp();
        return getService().addWatermarkInfo(request).compose(RxResultHelper.httpRusult());
    }

    /**
     * 上传日志
     */
    public static Observable<String> updateLog(String logInfo) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), logInfo);
        return getService().updateLog(body).compose(RxResultHelper.httpRusult());
    }

    /**
     * 聊天时上传文件接口
     */
    public static Observable<String> imUpdateFile(String filePath) {
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("fileName", file.getName(), requestFile);
        return getService().updateMessageFile(Config.pushIp, Config.pushUpdatePort, body)
                .compose(RxResultHelper.httpRusult());
    }

    /**
     * 聊天时上传图片
     */
    public static Observable<String> imUpdateImage(File file) {
        File compressedImageFile;
        try {
            compressedImageFile = new Compressor(Utils.getApp()).setQuality(50).compressToFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            compressedImageFile = file;
        }
        RequestBody requestFile = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), compressedImageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("fileName", file.getName(), requestFile);
        return getService().updateMessageFile(Config.pushIp, Config.pushUpdatePort, body).compose(RxResultHelper.httpRusult());
    }


    /**
     * 依图人脸识别
     */
    public static Observable<String> yiTuCompare(String facePackage, String signature, String userId) {
        FacePackgeRequest request = new FacePackgeRequest();
        request.setQueryImagePackage(facePackage);
        request.setSignature(signature);
        request.setQueryImagePackageReturnImageLIst(false);
        request.setUserId(userId);
        return getService().yiTuCompare(request).compose(RxResultHelper.httpRusult());
    }


}
