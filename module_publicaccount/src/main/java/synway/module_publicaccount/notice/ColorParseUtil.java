package synway.module_publicaccount.notice;

import android.graphics.Color;
import android.text.TextUtils;

/**
 * Created by leo on 2018/11/26.
 */

public class ColorParseUtil {
    public static final String LABEL_DEFAULT_COLOR = "#EB6876";
    public static final String FONT_DEFAULT_COLOR = "#333333";
    public static final String TAG_DEFAULT_COLOR = "#3295FA";
    public static int parseTag(String colorStr){

        if (TextUtils.isEmpty(colorStr)) {
            return Color.parseColor(TAG_DEFAULT_COLOR);
        }

        if(colorStr.length()==6 && colorStr.charAt(0) != '#'){
            //如果只是六位数没有加#，给它加上"#"进行解析
            colorStr = "#"+colorStr;
        }
        int result;
        try {
            result =  Color.parseColor(colorStr);
        }catch (Exception e){
            result = Color.parseColor(TAG_DEFAULT_COLOR);
        }
        return result;
    }

    public static int parseFont(String colorStr){
        if (TextUtils.isEmpty(colorStr)) {
            return Color.parseColor(FONT_DEFAULT_COLOR);
        }
        if(colorStr.length()==6 && colorStr.charAt(0) != '#'){
            //如果只是六位数没有加#，给它加上"#"进行解析
            colorStr = "#"+colorStr;
        }
        int result;
        try {
            result =  Color.parseColor(colorStr);
        }catch (Exception e){
            result = Color.parseColor(FONT_DEFAULT_COLOR);
        }
        return result;
    }

    public static int parseLabel(String colorStr){
        if (TextUtils.isEmpty(colorStr)) {
            return Color.parseColor(LABEL_DEFAULT_COLOR);
        }
        if(colorStr.length()==6 && colorStr.charAt(0) != '#'){
            //如果只是六位数没有加#，给它加上"#"进行解析
            colorStr = "#"+colorStr;
        }
        int result;
        try {
            result =  Color.parseColor(colorStr);
        }catch (Exception e){
            result = Color.parseColor(LABEL_DEFAULT_COLOR);
        }
        return result;
    }
}
