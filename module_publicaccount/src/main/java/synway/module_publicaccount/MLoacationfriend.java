package synway.module_publicaccount;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import synway.common.download.SynDownload;
import synway.module_interface.config.BaseUtil;
import synway.module_interface.config.ThrowExp;
import synway.module_interface.config.netConfig.NetConfig;
import synway.module_interface.module.LoacationObj;
import synway.module_interface.module.LocationInterface;
import synway.module_publicaccount.analytical.fac.AnalyticalPath;
import synway.module_publicaccount.analytical.fac.IAnalytical_Base;
import synway.module_publicaccount.db.table_util.Table_PublicAccount_Gis;
import synway.module_publicaccount.until.StringUtil;

import static synway.module_publicaccount.until.ConfigUtil.FRIENDCOLOR;
import static synway.module_publicaccount.until.PicUtil.Bitmap2Bytes;
import static synway.module_publicaccount.until.PicUtil.getPublicBigPath;

public class MLoacationfriend extends LocationInterface {

    //    //类型
//    public int type = 1 ;
//    //类型的名字
//    public String typeName = "敌方";
    @Override
    public ArrayList<LoacationObj> getList(SQLiteDatabase sqLiteDatabase, Context context, NetConfig netConfig) {
        SparseArray<IAnalytical_Base> analys = new SparseArray<>();
        ArrayList<LoacationObj> loacationObjs = new ArrayList<>();
        Inanyly(analys, context);
        Resources resources = context.getResources();
        //查询数据库后返回
        String querysql = null;
         querysql = "select *" + " from " + Table_PublicAccount_Gis._TABLE_NAME +" where DataType='" + 1+ "' and GisType='"+2+"'";
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery(querysql, null);
        } catch (Exception e) {
            return loacationObjs;
        }
        if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                            LoacationObj loacationObj = new LoacationObj();
                            loacationObj.type = 2;
                            loacationObj.guid=cursor.getString(cursor.getColumnIndex(Table_PublicAccount_Gis.USER_ID));
                            loacationObj.color=  Color.parseColor(FRIENDCOLOR);
                            loacationObj.action=1;
                            loacationObj.move=1;
                            loacationObj.text="";
                            loacationObj.angle=0;
                            loacationObj.name=cursor.getString(cursor.getColumnIndex(Table_PublicAccount_Gis.USER_NAME));
                            String picurl=cursor.getString(cursor.getColumnIndex(Table_PublicAccount_Gis.USER_PIC));
//                            picurl=getIpPortFromUrl(picurl, netConfig.)
//                                loacationObj.headPath= getPublicBigPath(loacationObj.guid);
//                                Drawable drawable = Drawable.createFromPath(loacationObj.headPath);
//                                if(drawable==null&& StringUtil.isNotEmpty(picurl)){
//                                    picurl=getIpPortFromUrl(picurl,netConfig.functionIP,netConfig.functionPort);
//                                    new Thread(new PicRunnable( loacationObj.guid, picurl)).start();
//                                }
                            loacationObj.PicUrl=getIpPortFromUrl(picurl, netConfig.functionIP,netConfig.functionPort);
                            loacationObj.picDrwa=Bitmap2Bytes(BitmapFactory.decodeResource(resources, R.drawable.public_friend));
                            loacationObj.x=cursor.getString(cursor.getColumnIndex(Table_PublicAccount_Gis.X));
                            loacationObj.y=cursor.getString(cursor.getColumnIndex(Table_PublicAccount_Gis.Y));
                            loacationObjs.add(loacationObj);
                }

        }
        return loacationObjs;
    }
    @Override
    public int getType() {
        return 2;
    }

    @Override
    public String getTypeName() {
        return "友方位置";
    }

    private void Inanyly(SparseArray<IAnalytical_Base> analys, Context context) {
        IAnalytical_Base analytical = null;
        for (int i = 0; i < AnalyticalPath.CLASS_PATH.length; i++) {
            try {
                analytical = (IAnalytical_Base) Class.forName(
                        AnalyticalPath.CLASS_PATH[i]).newInstance();
            } catch (Exception e) {
                ThrowExp.throwRxp("SyncGetRecord 公众帐号解析工厂类的包路径错误");
            }

            int msgType = analytical.msgType();
            if (msgType <= 0) {
                ThrowExp.throwRxp("SyncGetRecord 公众帐号解析工厂类没有注册需要接收的msgType");
            }
            if (analys.get(msgType) != null) {
            }

            analys.put(msgType, analytical);
            analytical.onInit(context);
        }
    }
    private  static String getIpPortFromUrl(String url,String ip,int port) {
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
        return url.replace(host,ip+":"+port);
    }
    private class PicRunnable implements Runnable {

        private String uuid, url;
        private PicRunnable(String uuid, String url){
            this.uuid = uuid;
            this.url = url;
        }

        @Override
        public void run() {
            SynDownload.httpDownload(new File(BaseUtil.FILE_PUBLIC_ACCOUNT_ORI + "/" + (uuid)), url);

        }
    }
}
