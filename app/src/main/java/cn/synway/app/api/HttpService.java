package cn.synway.app.api;

import java.util.List;

import cn.synway.app.bean.AppConfigBO;
import cn.synway.app.bean.BaseResult;
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
import cn.synway.app.db.table.UserEntry;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by wuliang on 2017/3/9.
 * <p>
 * 此处存放后台服务器的所有接口数据
 */

public interface HttpService {


    /**
     * 登录
     */
    @POST("/OSCService/BasicData/CheckLoginInfo")
    Observable<BaseResult<AppConfigBO>> login(@Body LoginRequest request);

    /**
     * 绑定机身码
     */
    @POST("/OSCService/BasicDataSet/bindAccountAndImei")
    Observable<BaseResult<String>> bindImei(@Body BindIMEIRequest request);

    /**
     * 人脸识别
     */
    @POST("/OSCService/BasicDataSet/photoToCardCompare")
    Observable<BaseResult<String>> faceAuth(@Body FaceRequest request);

    /**
     * 获取用户信息
     */
    @POST("/OSCService/BasicData/GetUserInfoByToken")
    Observable<BaseResult<UserEntry>> getUserInfo();

    /**
     * 检查更新
     */
    @POST("/OSCService/Version/CheckAPKUpdate")
    Observable<BaseResult<VersionBO>> checkUpdate(@Body UpdateRequest request);

    /**
     * 获取版本列表
     */
    @POST("/OSCService/Version/getVersionsYth")
    Observable<BaseResult<List<VersionNodeBO>>> getVersionList();

    /**
     * 修改密码
     */
    @POST("/OSCService/BasicData/AlterPWD")
    Observable<BaseResult<String>> editPassword(@Body EditPassRequest request);


    /**
     * 通讯录列表
     */
    @POST("/OSCService/BasicData/GetContactList")
    Observable<BaseResult<PersonInPsListBO>> getPeopleList(@Body PersonRequest request);

    /**
     * 人员详情 id
     */
    @POST("/OSCService/BasicData/GetUserInfoByID")
    Observable<BaseResult<UserEntry>> getUserInfoById(@Body UserRequest userRequest);

    /**
     * 人员详情 name 模糊
     */
    @POST("/OSCService/BasicData/GetUserInfoByName")
    Observable<BaseResult<List<UserEntry>>> getUserInfoByName(@Body UserRequest userRequest);

    /**
     * 机构模糊查询
     *
     * @param userRequest
     * @return
     */
    @POST("/OSCService/BasicData/GetOrganInfoByName")
    Observable<BaseResult<List<OrganDao>>> getOrganInfoByName(@Body UserRequest userRequest);

    /**
     * 获取地图信息
     */
    @POST("/OSCService/DownMap/getNewMapInfo")
    Observable<BaseResult<List<MyAreaDO>>> getMapInfo();

    /**
     * 获取地图下载地址
     */
    @POST("/OSCService/DownMap/getNewMapPath")
    Observable<BaseResult<String>> getDownMap(@Body DownMapRequest downMapRequest);

    /**
     * 查询首页消息列表
     */
    @POST("/OSCService/AppPush/SelectPushTypeList")
    Observable<BaseResult<List<MessageFcBo>>> getFcList();

    /**
     * 根据应用查询消息类型
     */
    @POST("/OSCService/AppPush/SelectFcTypeList")
    Observable<BaseResult<List<FcTypeBo>>> getTypeByFc(@Body FcTypeRequest request);

    /**
     * 查询消息列表
     */
    @POST("/OSCService/AppPush/SelectPushInfoList")
    Observable<BaseResult<MessageListBo>> getMessageList(@Body MessageRequest request);

    /**
     * 模糊查询消息
     */
    @POST("/OSCService/AppPush/SelectPushInfoByKey")
    Observable<BaseResult<MessageListBo>> getMessageListByKey(@Body MessageKeyRequest request);

    /**
     * 更新消息为已读
     */
    @POST("/OSCService/AppPush/UpdateRead")
    Observable<BaseResult<String>> updateRead(@Body UpdateReadRequest request);

    /**
     * 检测该服务器IP是否可用
     */
    @POST("http://{ip}:{port}/OSCService/Main/DownloadConfig")
    Observable<BaseResult<AppConfigBO>> checkConfig(@Path("ip") String ip, @Path("port") String port);

    /**
     * 下载
     */
    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url);

    /**
     * 应用中心内置的应用
     */
    @POST("/OSCService/Function/GetFunctionList")
    Observable<BaseResult<List<NetAPPBO>>> getNewAPP();


    /**
     * 设置消息为已读
     */
    @POST("/OSCService/AppPush/UpdateRead")
    Observable<BaseResult<Object>> setMessageRead();

    /**
     * 上传文件
     */
    @Multipart
    @POST("/OSCService/Upload/UploadFile")
    Observable<BaseResult<String>> updateFile(@Part MultipartBody.Part file);

    /**
     * 更新用户头像
     */
    @POST("/OSCService/BasicData/UpdateUserPic")
    Observable<BaseResult<String>> updateUserImage(@Body UpdatePicRequest request);

    /**
     * 增加水印
     */
    @POST("/OSCService/Watermark/addWatermarkInfo")
    Observable<BaseResult<String>> addWatermarkInfo(@Body WaterRequest request);

    /**
     * 上传日志
     */
    @POST("/OSCService/Upload/writeLog")
    Observable<BaseResult<String>> updateLog(@Body RequestBody requestBody);


    /**
     * 聊天上传文件
     */
    @Multipart
    @POST("http://{ip}:{port}/AppPush/Upload/uploadMessageFile")
    Observable<BaseResult<String>> updateMessageFile(@Path("ip") String ip, @Path("port") String port,
                                                     @Part MultipartBody.Part file);


    /**
     * 根据数据类型查询可搜索的应用
     */
    @POST("/OSCService/Function/getAndroidFunctionList")
    Observable<BaseResult<List<MsgSelectFcBo>>> getFunctionList(@Body MsgSelectRequest request);

    /**
     * 依图人脸识别接口
     */
    @POST("/OSCService/BasicDataSet/yiTuCompare")
    Observable<BaseResult<String>> yiTuCompare(@Body FacePackgeRequest request);

}
