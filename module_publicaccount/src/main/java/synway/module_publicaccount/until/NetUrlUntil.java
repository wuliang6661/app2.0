package synway.module_publicaccount.until;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ysm on 2017/1/6.
 */

public class NetUrlUntil {
    /**
     * 从url中分析出hostIP<br/>
     * @param url
     * @author wull
     * @return
     */
//    public static String getIpFromUrl(String url) {
//        // 1.判断是否为空
//        if (url == null || url.trim().equals("")) {
//            return "";
//        }
//
//        String host = "";
//        Pattern p = Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+");
//        Matcher matcher = p.matcher(url);
//        if (matcher.find()) {
//            host = matcher.group();
//        }
//        return host;
//    }

    /**
     * 从url中分析出hostIP:PORT<br/>
     * @param url
     * @author wull  */
    public static String changeIpPortFromUrl(String url,String NewIp,int NewPort) {
        // 1.判断是否为空
        if (url == null || url.trim().equals("")) {
            return null;
        }

        String host = "";
        Pattern p = Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+(:\\d{0,5})?");
        Matcher matcher = p.matcher(url);
        if (matcher.find()) {
            host = matcher.group() ;
        }
        // 如果
//        if(host.contains(":") == false){
//            return new IpPortAddr(host, 80 );
//        }
        String newurl=url.replace(host,NewIp+":"+NewPort);
//        url.replace(host,NewIp+":"+NewPort);
//        String[] ipPortArr = host.split(":");
        return  newurl;
    }
    public static String getUrlId(String url){
        String id=null;
        String[]ids=null;
        String newid=null;
        if(url!=null&&!url.equals("")){
            String[]
                    splitUrl = url.split("/");
            id=splitUrl[splitUrl.length-1];
            if(id.contains("_")) {
                ids = id.split("_");
                newid=ids[1];
            }else{
                newid=id;
            }

        }
        return  newid;
    }
    public static void main(String [] args){
        String url = "http://10.33.32.81:8080/login.action";
//        System.out.println(NetUrlUntil.getIpFromUrl(url) );
        String addr= NetUrlUntil.changeIpPortFromUrl(url,"11.11.11.11",9090) ;
        String id=getUrlId(url);
        System.out.println(id);
    }
}
