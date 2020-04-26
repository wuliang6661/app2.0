package cn.synway.synmonitor.event.xgupload;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public class OKHttpFactory {
    private OkHttpClient okHttpClient=null;

    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8")  ;
    public static MediaType MEDIA_TYPE_TEXT = MediaType.parse("text/plain; charset=utf-8");



    private OKHttpFactory(){
        if (okHttpClient==null){
            okHttpClient=new OkHttpClient.Builder().sslSocketFactory(SSLSocketClient.getSSLSocketFactory()).hostnameVerifier(SSLSocketClient.getHostnameVerifier()).build();
        }
    }
    public static OKHttpFactory getInstance(){
        return OKHttpFactoryHolder.instance;
    }

    private static class OKHttpFactoryHolder{
        private static OKHttpFactory instance=new OKHttpFactory();
    }

    public OkHttpClient getOkHttpClient(){
        return this.okHttpClient;
    }


}
