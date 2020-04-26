package synway.module_publicaccount.public_chat.adapter;

import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import qyc.library.control.dialog_msg.DialogMsg;
import synway.module_publicaccount.public_chat.adapter.PublicAccountURL;

/**
 * Created by QSJH on 2016/6/8 0008.
 */
public class PublicAccountURLFactory {

    public static PublicAccountURL getPublicAccountURL(String url) {
        if (url.indexOf(PublicAccountURL.URL_GPS.tag) != -1) {
            PublicAccountURL.URL_GPS url_gps = new PublicAccountURL.URL_GPS();
            if (url_gps.createFromUrl(url)) {
                return url_gps;
            } else {
                return getDefault(url);
            }
        } else if (url.indexOf(PublicAccountURL.URL_MEDIA.tag) != -1) {
            PublicAccountURL.URL_MEDIA url_media = new PublicAccountURL.URL_MEDIA();
            if (url_media.createFromUrl(url)) {
                return url_media;
            } else {
                return getDefault(url);
            }
        } else if (!cutStrHead(url, PublicAccountURL.URL_RTMP.tag).equals(url)) {
//            Toast.makeText(MainApp.getInstance(),"识别为RTMP\n"+url+"\ntag= "+PublicAccountURL.URL_RTMP.tag,Toast.LENGTH_LONG).show();
            PublicAccountURL.URL_RTMP url_rtmp = new PublicAccountURL.URL_RTMP();
            if (url_rtmp.createFromUrl(url)) {
                return url_rtmp;
            } else {
                return getDefault(url);
            }
        } else {
//            Toast.makeText(MainApp.getInstance(),"识别为网页\n"+url+"\ntag= "+PublicAccountURL.URL_RTMP.tag,Toast.LENGTH_LONG).show();
            return getDefault(url);
        }

    }

    public static PublicAccountURL getPublicVoicURL(String url) {
        PublicAccountURL.URL_MEDIA url_media = new PublicAccountURL.URL_MEDIA();
        if (url_media.createFromUrl(url)) {
            return url_media;
        } else {
            return getDefault(url);
        }
    }

    private static PublicAccountURL getDefault(String superLinkUrl) {
        PublicAccountURL.URL_SUPERLINK publicAccountURL = new PublicAccountURL.URL_SUPERLINK();
        publicAccountURL.createFromUrl(superLinkUrl);
        return publicAccountURL;
    }

    /**
     * 把head剪掉
     */
    public static final String cutStrHead(String str, String head) {

        Pattern pattern = Pattern.compile(head, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        // 替换第一个符合正则的数据
        return matcher.replaceFirst("");

    }
}
