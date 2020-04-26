package synway.common;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import synway.common.okhttp.OkHttpUtils;
import synway.common.okhttp.log.LoggerInterceptor;
import synway.common.upload.UploadFileQueue;

/**
 * 通用功能类
 * 现在包括HTTP请求的发送消息,上传下载
 */
public class Common {
    public Common() {
        //初始化OkHttp
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                //OkHttp的Log,调试结束可以注释掉
                .addInterceptor(new LoggerInterceptor("SYN_COMMON"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();
        // 默认的maxRequests = 64: 最大并发请求数为64
        // 默认的maxRequestsPerHost = 5: 每个主机最大请求数为5
        okHttpClient.dispatcher().setMaxRequests(128);
        okHttpClient.dispatcher().setMaxRequestsPerHost(24);
        OkHttpUtils.initClient(okHttpClient);
        //初始化上传文件队列
        UploadFileQueue.instance().init();

        //初始化线程池
        ThreadPool.instance().start();
    }
}
