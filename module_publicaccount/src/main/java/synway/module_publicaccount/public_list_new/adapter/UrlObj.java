package synway.module_publicaccount.public_list_new.adapter;

import java.io.Serializable;

/**
 * UrlObj 类型包含 url值，url类型，url参数,是否显示标题栏
 */

public class UrlObj implements Serializable {

    public UrlObj(){

    }
    public UrlObj(int urlType,String publicUrl, String urlParam,int isShowTitle){
        this.urlType = urlType;
        this.publicUrl = publicUrl;
        this.urlParam = urlParam;
        this.isShowTitle = isShowTitle;
    }
    //当公众号类型type 为2时,点击后直接跳转的url类型，0：html,1：weex，2：本地原生应用跳转；
     public int urlType;
    //跳转的url
    public String publicUrl;
    //包含中间服务地址或者其他url地址的备注字段,本地原生应用在网站注册的类型等信息
    public String urlParam;
    //是否是标题栏1 是  0否
    public int isShowTitle = 1;
}
