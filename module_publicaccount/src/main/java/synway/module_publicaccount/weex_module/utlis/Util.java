package synway.module_publicaccount.weex_module.utlis;

/**
 * Created by dell on 2018/10/22.
 * 说明：
 */

public class Util {
    public static  String getResourceUrl(String url){
        int s=url.indexOf(":");
        int ss=url.indexOf("/",s+3);
        return url.substring(0,ss+1);
    }

}
