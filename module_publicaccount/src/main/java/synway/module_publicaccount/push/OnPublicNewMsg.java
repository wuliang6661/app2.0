package synway.module_publicaccount.push;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.UUID;

import qyc.net.push.synwayoscpush.SPushFacInterface;
import synway.common.download.SynDownload;
import synway.module_interface.AppConfig;
import synway.module_interface.config.BaseUtil;
import synway.module_interface.config.netConfig.NetConfig;
import synway.module_interface.db.SQLite;
import synway.module_interface.db.table_util.Table_LastContact;
import synway.module_interface.lastcontact.LastContact;
import synway.module_interface.push.FacConfig;
import synway.module_publicaccount.analytical.fac.AnalyticalPath;
import synway.module_publicaccount.analytical.fac.IAnalytical_Base;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgBase;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgNotice;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgPicTxt;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgTaskNotice;
import synway.module_publicaccount.db.table_util.Table_PublicAccountRecord;
import synway.module_publicaccount.db.table_util.Table_PublicAccount_LastMsg;
import synway.module_publicaccount.public_message.PublicMessage;

public class OnPublicNewMsg extends SPushFacInterface {

    private SparseArray<IAnalytical_Base> analys = null;

    private NewPANotiftDeal newPANotiftDeal = null;

    private NetConfig netConfig = null;
    private String userID = null;

    @Override
    public int[] regist() {
        return new int[]{5001, 5002};
    }

    @Override
    public void onCreat(Object o) {
        FacConfig facConfig = (FacConfig) o;
        userID = facConfig.userID;
        netConfig = facConfig.netConfig;
        newPANotiftDeal = new NewPANotiftDeal(context);

        this.analys = new SparseArray<IAnalytical_Base>();

        IAnalytical_Base analytical = null;

        for (int i = 0; i < AnalyticalPath.CLASS_PATH.length; i++) {
            try {
                analytical = (IAnalytical_Base) Class.forName(
                        AnalyticalPath.CLASS_PATH[i]).newInstance();
            }
            catch (Exception e) {
                return;
                // ThrowExp.throwRxp("公众帐号解析工厂类的包路径错误");
            }

            int msgType = analytical.msgType();
            if (msgType <= 0) {
                return;
                // ThrowExp.throwRxp("公众帐号解析工厂类没有注册需要接收的msgType");
            }

            if (analys.get(msgType) != null) {
                return;
            }

            analys.put(msgType, analytical);
            analytical.onInit(context);
        }
    }

    @Override
    public void onDestory() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReceive(int type, String jsonStr) {
        if (type == 5001) {
            run5001(type, jsonStr);
        }
    }


    private void run5001(int type, String jsonStr) {
        if (jsonStr == null || jsonStr.equals("")) {
            return;
        }
        //公众号推送测试
//        if (jsonStr.equals("HEELLO1")) {
//            jsonStr = "{\"MSG\":{\"MSG_TYPE\":4,\"MSG_GUID\":\"\",\"MSG_POSITION\":1,\"MSG_INFO\":{\"TITLE_SIZE\":0,\"TITLE_URL\":\"\",\"TITLE_URL_NAME\":\"\",\"TITLE_URL_W\":600,\"TITLE_URL_TYPE\":1,\"TITLE_URL_H\":500,\"TITLE\":\"重点人分级分类统计\",\"TITLE_POSITION\":0,\"DATALINE\":[{\"DATATYPE\":1,\"DATA\":{\"TEXT\":[{\"URL_W\":600,\"COLOR\":\"#000000\",\"SIZE\":1,\"URL_NAME\":\"\",\"CONTENT\":\"查询时间：2018-12-10 15:43:59\",\"URL_H\":500,\"URL\":\"\",\"URL_TYPE\":1}]}},{\"DATATYPE\":1,\"DATA\":{\"TEXT\":[{\"URL_W\":600,\"COLOR\":\"#000000\",\"SIZE\":1,\"URL_NAME\":\"\",\"CONTENT\":\"支队数量：14\",\"URL_H\":500,\"URL\":\"\",\"URL_TYPE\":1}]}},{\"DATATYPE\":1,\"DATA\":{\"TEXT\":[{\"URL_W\":600,\"COLOR\":\"#000000\",\"SIZE\":1,\"URL_NAME\":\"\",\"CONTENT\":\"种类数量：34\",\"URL_H\":500,\"URL\":\"\",\"URL_TYPE\":1}]}},{\"DATATYPE\":6,\"DATA\":{\"EXTEND_CONTENT\":\"{\\\"condition\\\":{\\\"organIds\\\":[242,243,244,245,246,247,248,249,250,251,29,25,221,241],\\\"types\\\":[{\\\"parent\\\":\\\"利益诉求\\\",\\\"children\\\":[\\\"\\\\\\\"失独\\\\\\\"群体\\\",\\\"\\\\\\\"民代幼\\\\\\\"维权群体\\\",\\\"\\\\\\\"涉众\\\\\\\"经济案件\\\",\\\"个访类\\\",\\\"企业下岗人员集体维权\\\",\\\"其它\\\",\\\"农村基层选举维权群体\\\",\\\"出租车群体\\\",\\\"拆迁上访维权\\\",\\\"涉军维权类\\\",\\\"涉法涉诉类\\\",\\\"环保引发群体纠纷\\\",\\\"非津籍考生家长\\\"]},{\\\"parent\\\":\\\"反分裂\\\",\\\"children\\\":[\\\"涉港、澳、台\\\",\\\"涉疆、涉恐\\\",\\\"涉藏、涉蒙\\\"]},{\\\"parent\\\":\\\"境外使领馆、境内外NGO组织\\\",\\\"children\\\":[\\\"其它\\\",\\\"国内民间组织\\\",\\\"境外NGO\\\",\\\"境外使领馆\\\"]},{\\\"parent\\\":\\\"意识形态、颠覆渗透\\\",\\\"children\\\":[\\\"\\\\\\\"左\\\\\\\"\\\\\\\"右\\\\\\\"思想领域\\\",\\\"不法律师群体\\\",\\\"其它\\\",\\\"境外媒体\\\",\\\"民运重点人\\\"]},{\\\"parent\\\":\\\"邪教和非法宗教\\\",\\\"children\\\":[\\\"其它\\\",\\\"呼喊派\\\",\\\"善人道\\\",\\\"地下天主教\\\",\\\"基督教家庭教会\\\",\\\"实际神\\\",\\\"法轮功\\\",\\\"藏传佛教\\\",\\\"门徒会\\\"]}],\\\"cityCodes\\\":[\\\"330100\\\"]},\\\"time\\\":{\\\"week\\\":0,\\\"hour\\\":0,\\\"day\\\":1544371200}}\",\"COLOR\":\"#000000\",\"SIZE\":1,\"URL_NAME\":\"在杭重点人员详情\",\"CONTENT\":\"目标当天在杭数量：0\",\"URL\":\"/FTPText/weexResource/yj_detail.js\",\"URL_TYPE\":1}},{\"DATATYPE\":6,\"DATA\":{\"EXTEND_CONTENT\":\"{\\\"condition\\\":{\\\"organIds\\\":[242,243,244,245,246,247,248,249,250,251,29,25,221,241],\\\"types\\\":[{\\\"parent\\\":\\\"利益诉求\\\",\\\"children\\\":[\\\"\\\\\\\"失独\\\\\\\"群体\\\",\\\"\\\\\\\"民代幼\\\\\\\"维权群体\\\",\\\"\\\\\\\"涉众\\\\\\\"经济案件\\\",\\\"个访类\\\",\\\"企业下岗人员集体维权\\\",\\\"其它\\\",\\\"农村基层选举维权群体\\\",\\\"出租车群体\\\",\\\"拆迁上访维权\\\",\\\"涉军维权类\\\",\\\"涉法涉诉类\\\",\\\"环保引发群体纠纷\\\",\\\"非津籍考生家长\\\"]},{\\\"parent\\\":\\\"反分裂\\\",\\\"children\\\":[\\\"涉港、澳、台\\\",\\\"涉疆、涉恐\\\",\\\"涉藏、涉蒙\\\"]},{\\\"parent\\\":\\\"境外使领馆、境内外NGO组织\\\",\\\"children\\\":[\\\"其它\\\",\\\"国内民间组织\\\",\\\"境外NGO\\\",\\\"境外使领馆\\\"]},{\\\"parent\\\":\\\"意识形态、颠覆渗透\\\",\\\"children\\\":[\\\"\\\\\\\"左\\\\\\\"\\\\\\\"右\\\\\\\"思想领域\\\",\\\"不法律师群体\\\",\\\"其它\\\",\\\"境外媒体\\\",\\\"民运重点人\\\"]},{\\\"parent\\\":\\\"邪教和非法宗教\\\",\\\"children\\\":[\\\"其它\\\",\\\"呼喊派\\\",\\\"善人道\\\",\\\"地下天主教\\\",\\\"基督教家庭教会\\\",\\\"实际神\\\",\\\"法轮功\\\",\\\"藏传佛教\\\",\\\"门徒会\\\"]}],\\\"cityCodes\\\":[\\\"330100,330700,331100,330800,330600,330200,330900,331000,330300,330400,330500\\\"]},\\\"time\\\":{\\\"week\\\":0,\\\"hour\\\":0,\\\"day\\\":1544371200}}\",\"COLOR\":\"#000000\",\"SIZE\":1,\"URL_NAME\":\"在浙重点人员详情\",\"CONTENT\":\"目标当天在浙数量：0\",\"URL\":\"/FTPText/weexResource/yj_detail.js\",\"URL_TYPE\":1}},{\"DATATYPE\":6,\"DATA\":{\"EXTEND_CONTENT\":\"{\\\"condition\\\":{\\\"organIds\\\":[242,243,244,245,246,247,248,249,250,251,29,25,221,241],\\\"types\\\":[{\\\"parent\\\":\\\"利益诉求\\\",\\\"children\\\":[\\\"\\\\\\\"失独\\\\\\\"群体\\\",\\\"\\\\\\\"民代幼\\\\\\\"维权群体\\\",\\\"\\\\\\\"涉众\\\\\\\"经济案件\\\",\\\"个访类\\\",\\\"企业下岗人员集体维权\\\",\\\"其它\\\",\\\"农村基层选举维权群体\\\",\\\"出租车群体\\\",\\\"拆迁上访维权\\\",\\\"涉军维权类\\\",\\\"涉法涉诉类\\\",\\\"环保引发群体纠纷\\\",\\\"非津籍考生家长\\\"]},{\\\"parent\\\":\\\"反分裂\\\",\\\"children\\\":[\\\"涉港、澳、台\\\",\\\"涉疆、涉恐\\\",\\\"涉藏、涉蒙\\\"]},{\\\"parent\\\":\\\"境外使领馆、境内外NGO组织\\\",\\\"children\\\":[\\\"其它\\\",\\\"国内民间组织\\\",\\\"境外NGO\\\",\\\"境外使领馆\\\"]},{\\\"parent\\\":\\\"意识形态、颠覆渗透\\\",\\\"children\\\":[\\\"\\\\\\\"左\\\\\\\"\\\\\\\"右\\\\\\\"思想领域\\\",\\\"不法律师群体\\\",\\\"其它\\\",\\\"境外媒体\\\",\\\"民运重点人\\\"]},{\\\"parent\\\":\\\"邪教和非法宗教\\\",\\\"children\\\":[\\\"其它\\\",\\\"呼喊派\\\",\\\"善人道\\\",\\\"地下天主教\\\",\\\"基督教家庭教会\\\",\\\"实际神\\\",\\\"法轮功\\\",\\\"藏传佛教\\\",\\\"门徒会\\\"]}],\\\"cityCodes\\\":[\\\"110000\\\"]},\\\"time\\\":{\\\"week\\\":0,\\\"hour\\\":0,\\\"day\\\":1544371200}}\",\"COLOR\":\"#000000\",\"SIZE\":1,\"URL_NAME\":\"在京重点人员详情\",\"CONTENT\":\"目标当天在京数量：0\",\"URL\":\"/FTPText/weexResource/yj_detail.js\",\"URL_TYPE\":1}},{\"DATATYPE\":6,\"DATA\":{\"EXTEND_CONTENT\":\"7091cef6-5035-423c-9b8d-7936a9855275\",\"COLOR\":\"#000000\",\"SIZE\":1,\"URL_NAME\":\"\",\"CONTENT\":\"点击查看详细信息\",\"URL\":\"\",\"URL_TYPE\":1}}]}},\"TO_USER\":[\"0571_U1038\"],\"SEND_TYPE\":2,\"GUID\":\"2c3b0aa8-0410-1f17-b83c-ac398ce2ed46f\",\"FC_BT_GUID\":\"\",\"TIME\":\"2018-12-10 15:43:59\"}";
//        }else if(jsonStr.equals("HEELLO2")){
//            jsonStr = "{\"MSG\":{\"MSG_TYPE\":4,\"MSG_GUID\":\"\",\"MSG_POSITION\":1,\"MSG_INFO\":{\"TITLE_SIZE\":0,\"TITLE_URL\":\"\",\"TITLE_URL_NAME\":\"\",\"TITLE_URL_W\":600,\"TITLE_URL_TYPE\":1,\"TITLE_URL_H\":500,\"TITLE\":\"实时跟踪\",\"TITLE_POSITION\":0,\"DATALINE\":[{\"DATATYPE\":1,\"DATA\":{\"TEXT\":[{\"URL_W\":600,\"COLOR\":\"#000000\",\"SIZE\":1,\"URL_NAME\":\"\",\"CONTENT\":\"查询时间：2018-12-10 15:43:59\",\"URL_H\":500,\"URL\":\"\",\"URL_TYPE\":1}]}},{\"DATATYPE\":1,\"DATA\":{\"TEXT\":[{\"URL_W\":600,\"COLOR\":\"#000000\",\"SIZE\":1,\"URL_NAME\":\"\",\"CONTENT\":\"支队数量：14\",\"URL_H\":500,\"URL\":\"\",\"URL_TYPE\":1}]}},{\"DATATYPE\":1,\"DATA\":{\"TEXT\":[{\"URL_W\":600,\"COLOR\":\"#000000\",\"SIZE\":1,\"URL_NAME\":\"\",\"CONTENT\":\"种类数量：34\",\"URL_H\":500,\"URL\":\"\",\"URL_TYPE\":1}]}},{\"DATATYPE\":6,\"DATA\":{\"EXTEND_CONTENT\":\"{\\\"condition\\\":{\\\"organIds\\\":[242,243,244,245,246,247,248,249,250,251,29,25,221,241],\\\"types\\\":[{\\\"parent\\\":\\\"利益诉求\\\",\\\"children\\\":[\\\"\\\\\\\"失独\\\\\\\"群体\\\",\\\"\\\\\\\"民代幼\\\\\\\"维权群体\\\",\\\"\\\\\\\"涉众\\\\\\\"经济案件\\\",\\\"个访类\\\",\\\"企业下岗人员集体维权\\\",\\\"其它\\\",\\\"农村基层选举维权群体\\\",\\\"出租车群体\\\",\\\"拆迁上访维权\\\",\\\"涉军维权类\\\",\\\"涉法涉诉类\\\",\\\"环保引发群体纠纷\\\",\\\"非津籍考生家长\\\"]},{\\\"parent\\\":\\\"反分裂\\\",\\\"children\\\":[\\\"涉港、澳、台\\\",\\\"涉疆、涉恐\\\",\\\"涉藏、涉蒙\\\"]},{\\\"parent\\\":\\\"境外使领馆、境内外NGO组织\\\",\\\"children\\\":[\\\"其它\\\",\\\"国内民间组织\\\",\\\"境外NGO\\\",\\\"境外使领馆\\\"]},{\\\"parent\\\":\\\"意识形态、颠覆渗透\\\",\\\"children\\\":[\\\"\\\\\\\"左\\\\\\\"\\\\\\\"右\\\\\\\"思想领域\\\",\\\"不法律师群体\\\",\\\"其它\\\",\\\"境外媒体\\\",\\\"民运重点人\\\"]},{\\\"parent\\\":\\\"邪教和非法宗教\\\",\\\"children\\\":[\\\"其它\\\",\\\"呼喊派\\\",\\\"善人道\\\",\\\"地下天主教\\\",\\\"基督教家庭教会\\\",\\\"实际神\\\",\\\"法轮功\\\",\\\"藏传佛教\\\",\\\"门徒会\\\"]}],\\\"cityCodes\\\":[\\\"330100\\\"]},\\\"time\\\":{\\\"week\\\":0,\\\"hour\\\":0,\\\"day\\\":1544371200}}\",\"COLOR\":\"#000000\",\"SIZE\":1,\"URL_NAME\":\"在杭重点人员详情\",\"CONTENT\":\"目标当天在杭数量：0\",\"URL\":\"/FTPText/weexResource/yj_detail.js\",\"URL_TYPE\":1}},{\"DATATYPE\":6,\"DATA\":{\"EXTEND_CONTENT\":\"{\\\"condition\\\":{\\\"organIds\\\":[242,243,244,245,246,247,248,249,250,251,29,25,221,241],\\\"types\\\":[{\\\"parent\\\":\\\"利益诉求\\\",\\\"children\\\":[\\\"\\\\\\\"失独\\\\\\\"群体\\\",\\\"\\\\\\\"民代幼\\\\\\\"维权群体\\\",\\\"\\\\\\\"涉众\\\\\\\"经济案件\\\",\\\"个访类\\\",\\\"企业下岗人员集体维权\\\",\\\"其它\\\",\\\"农村基层选举维权群体\\\",\\\"出租车群体\\\",\\\"拆迁上访维权\\\",\\\"涉军维权类\\\",\\\"涉法涉诉类\\\",\\\"环保引发群体纠纷\\\",\\\"非津籍考生家长\\\"]},{\\\"parent\\\":\\\"反分裂\\\",\\\"children\\\":[\\\"涉港、澳、台\\\",\\\"涉疆、涉恐\\\",\\\"涉藏、涉蒙\\\"]},{\\\"parent\\\":\\\"境外使领馆、境内外NGO组织\\\",\\\"children\\\":[\\\"其它\\\",\\\"国内民间组织\\\",\\\"境外NGO\\\",\\\"境外使领馆\\\"]},{\\\"parent\\\":\\\"意识形态、颠覆渗透\\\",\\\"children\\\":[\\\"\\\\\\\"左\\\\\\\"\\\\\\\"右\\\\\\\"思想领域\\\",\\\"不法律师群体\\\",\\\"其它\\\",\\\"境外媒体\\\",\\\"民运重点人\\\"]},{\\\"parent\\\":\\\"邪教和非法宗教\\\",\\\"children\\\":[\\\"其它\\\",\\\"呼喊派\\\",\\\"善人道\\\",\\\"地下天主教\\\",\\\"基督教家庭教会\\\",\\\"实际神\\\",\\\"法轮功\\\",\\\"藏传佛教\\\",\\\"门徒会\\\"]}],\\\"cityCodes\\\":[\\\"330100,330700,331100,330800,330600,330200,330900,331000,330300,330400,330500\\\"]},\\\"time\\\":{\\\"week\\\":0,\\\"hour\\\":0,\\\"day\\\":1544371200}}\",\"COLOR\":\"#000000\",\"SIZE\":1,\"URL_NAME\":\"在浙重点人员详情\",\"CONTENT\":\"目标当天在浙数量：0\",\"URL\":\"/FTPText/weexResource/yj_detail.js\",\"URL_TYPE\":1}},{\"DATATYPE\":6,\"DATA\":{\"EXTEND_CONTENT\":\"{\\\"condition\\\":{\\\"organIds\\\":[242,243,244,245,246,247,248,249,250,251,29,25,221,241],\\\"types\\\":[{\\\"parent\\\":\\\"利益诉求\\\",\\\"children\\\":[\\\"\\\\\\\"失独\\\\\\\"群体\\\",\\\"\\\\\\\"民代幼\\\\\\\"维权群体\\\",\\\"\\\\\\\"涉众\\\\\\\"经济案件\\\",\\\"个访类\\\",\\\"企业下岗人员集体维权\\\",\\\"其它\\\",\\\"农村基层选举维权群体\\\",\\\"出租车群体\\\",\\\"拆迁上访维权\\\",\\\"涉军维权类\\\",\\\"涉法涉诉类\\\",\\\"环保引发群体纠纷\\\",\\\"非津籍考生家长\\\"]},{\\\"parent\\\":\\\"反分裂\\\",\\\"children\\\":[\\\"涉港、澳、台\\\",\\\"涉疆、涉恐\\\",\\\"涉藏、涉蒙\\\"]},{\\\"parent\\\":\\\"境外使领馆、境内外NGO组织\\\",\\\"children\\\":[\\\"其它\\\",\\\"国内民间组织\\\",\\\"境外NGO\\\",\\\"境外使领馆\\\"]},{\\\"parent\\\":\\\"意识形态、颠覆渗透\\\",\\\"children\\\":[\\\"\\\\\\\"左\\\\\\\"\\\\\\\"右\\\\\\\"思想领域\\\",\\\"不法律师群体\\\",\\\"其它\\\",\\\"境外媒体\\\",\\\"民运重点人\\\"]},{\\\"parent\\\":\\\"邪教和非法宗教\\\",\\\"children\\\":[\\\"其它\\\",\\\"呼喊派\\\",\\\"善人道\\\",\\\"地下天主教\\\",\\\"基督教家庭教会\\\",\\\"实际神\\\",\\\"法轮功\\\",\\\"藏传佛教\\\",\\\"门徒会\\\"]}],\\\"cityCodes\\\":[\\\"110000\\\"]},\\\"time\\\":{\\\"week\\\":0,\\\"hour\\\":0,\\\"day\\\":1544371200}}\",\"COLOR\":\"#000000\",\"SIZE\":1,\"URL_NAME\":\"在京重点人员详情\",\"CONTENT\":\"目标当天在京数量：0\",\"URL\":\"/FTPText/weexResource/yj_detail.js\",\"URL_TYPE\":1}},{\"DATATYPE\":6,\"DATA\":{\"EXTEND_CONTENT\":\"7091cef6-5035-423c-9b8d-7936a9855275\",\"COLOR\":\"#000000\",\"SIZE\":1,\"URL_NAME\":\"\",\"CONTENT\":\"点击查看详细信息\",\"URL\":\"\",\"URL_TYPE\":1}}]}},\"TO_USER\":[\"0571_U1038\"],\"SEND_TYPE\":2,\"GUID\":\"f1199e91-a341-deef-2e4f-b6d62f5718f53\",\"FC_BT_GUID\":\"\",\"TIME\":\"2018-12-10 19:45:59\"}";
//
//        }
        Obj_PublicMsgBase result = null;
        String loginUserID = userID;
        JSONObject jsonTotal = null;
        String publicGUID = null;
        String showTime = null;
        String toUser = null;
        JSONObject jsonMsg = null;
        JSONObject msgInfo = null;
        int msgType = 0;
        String msgGUID = null;
        String pushMsgPageCodeStr = "";
        String pushMsgPageNameStr = "";
        Log.i("testy", "收到未解析的推送数据" + jsonStr);
        Log.d("dym------------------->", "OnPublicNewMsg 收到推送,jsonStr= " + jsonStr);
        try {
            jsonTotal = new JSONObject(jsonStr);
            publicGUID = jsonTotal.getString("GUID");
            showTime = jsonTotal.getString("TIME");
            toUser = jsonTotal.getString("TO_USER");
            String msg = jsonTotal.getString("MSG");
            jsonMsg = new JSONObject(msg);
            msgType = jsonMsg.getInt("MSG_TYPE");
            msgInfo = jsonMsg.getJSONObject("MSG_INFO");

            if (msgType != 7) {
                //需要注意的是：msgType=7 统一通知的模式,不需要这两个值，这样能使得
                //一体化使用旧的推送SDK手机端部分，依然可以保持正常。

                //PAPushSDK V1.5 将MSG_GUID这个字段调整到最外层的json
                msgGUID = jsonTotal.optString("MSG_GUID");
                //PAPushSDK V1.5 在最外层的json新增字段PUSH_MSG_PAGE(消息的推送分页)
                //样式“0|1|2” ，当SDK调用者不设置这个字段的时候，则该值为空字符串。
                pushMsgPageCodeStr = jsonTotal.optString("PUSH_MSG_PAGE_CODE");
                pushMsgPageNameStr = jsonTotal.optString("PUSH_MSG_PAGE_NAME");
            }


        }
        catch (JSONException e) {

            e.printStackTrace();
            return;
        }
        Log.i("testy", "收到原始的推送数据" + jsonMsg);
        IAnalytical_Base analyTical = analys.get(msgType);
        if (analyTical == null) {
            return;
        }
        result = analyTical.onDeal(msgInfo);
        if (null == result) {
            return;
        }

        String publicName = null;
        String publicpicid = null;
        //是否是修改过的消息
        boolean isMsgUpdate = false;
        if (msgType != 7) {
            //需要注意的是：msgType=7 统一通知的模式 无需 对公众号id和名称进行校验

            // 获取公众号名称
            String publicinfo = PublicInfoDeal.getName(getDB(), publicGUID);

            if (publicinfo != null) {
                String strlist[] = publicinfo.split("\\|", -1);
                publicName = strlist[0];
                publicpicid = strlist[1];
            }
            String path = getPublicName(publicGUID);
            Drawable drawable = Drawable.createFromPath(path);
            // 如果数据库中不存该公众号信息，
            // 去http 补全该公众号的信息，
            // 并且入库
            if (publicName == null) {
                Obj_PublicInfo objInfo = PublicInfoGet.get(loginUserID, publicGUID, netConfig.publicServerIP, netConfig.publicServerPort);
                if (null == objInfo) {
                    return;
                }
                PublicInfoDeal.insert(getDB(), publicGUID,
                        objInfo.fcName, objInfo.fcCompany, objInfo.fcContact,
                        objInfo.fcTel, objInfo.mobile_pic);
                publicName = objInfo.fcName;
                publicpicid = objInfo.mobile_pic;
            }
            /**
             * 本地图片不存在，说明服务器图片更新了，和服务器图片是否一样的比对在推送或者同步或者拉列表的时候就判断过了
             */
            if (drawable == null) {
                String urlHead = "http://" + netConfig.httpIP + ":" + netConfig.httpPort + "/" + "OSCUserPic/Public_";
                String urlStr = "";
                if (publicpicid != null) {
                    if (publicpicid.contains(".")) {
                        urlStr = "http://" + netConfig.httpIP + ":" + netConfig.httpPort + "/" + "OSCUserPic/" + publicpicid;
                    }
                    else {
                        urlStr = urlHead + publicpicid;
                    }

                    Boolean ifsuccess = SynDownload.httpDownload(new File(BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + (publicGUID)), urlStr);
                }
            }
        }
        long serverTime = System.currentTimeMillis();
        long localeTime = System.currentTimeMillis();

        //如果存在该公众号

        if (result instanceof Obj_PublicMsgPicTxt) {

            /** Url域解析 */
            /** data url域所需的data */
            String data = null;
            try {
                /** dateLine 解析 */
                JSONArray dataLine = null;

                JSONArray dataArray = new JSONArray();
                dataLine = msgInfo.getJSONArray("DATALINE");
                for (int i = 0; i < dataLine.length(); i++) {
                    JSONObject jsonDataLine = (JSONObject) dataLine.get(i); // 得到对象中的第i条记录
                    int DATATYPE = jsonDataLine.getInt("DATATYPE");
                    if (DATATYPE == 6) {
                        // URL域
                        JSONObject jsonData = jsonDataLine.getJSONObject("DATA");
                        JSONObject object = new JSONObject();
                        object.put("ROW", i);
                        object.put("URLDATA", jsonData.get("EXTEND_CONTENT"));
                        jsonData.remove("EXTEND_CONTENT");
                        dataArray.put(object);
                    }
                }
                if (dataArray != null || dataArray.length() != 0) {
                    data = dataArray.toString();
                }
            }
            catch (JSONException e) {

                e.printStackTrace();
                return;
            }
            // 下载图片
            DownLoadPic.down(loginUserID, publicGUID, jsonMsg);
            //数据库插入操作，是否存在，不存在建立表
            String sql = Table_PublicAccountRecord.getCreatTableSql(publicGUID);
            getDB().execSQL(sql);
            long now = System.currentTimeMillis();
            // 插入到数据库中
            //检查本地数据库是否存在这条记录，如果存在，则这条新收到的消息为修改后的消息
            isMsgUpdate = isRecordExist(msgGUID, publicGUID);
            Log.d("dym------------------->",
                    "jinlai Obj_PublicMsgPicTxt isMsgUpdate= " + isMsgUpdate);
            if (isMsgUpdate) {
                //如果存在，将之前的旧纪录删除
                getDB().delete(Table_PublicAccountRecord.getTableName(publicGUID),
                        Table_PublicAccountRecord.publicRecord_col_GUID + " = ?",
                        new String[]{msgGUID});
            }

            RecordDeal.insert(getDB(), publicGUID, msgGUID, showTime, jsonMsg, data,
                    now, msgType, toUser, pushMsgPageCodeStr, pushMsgPageNameStr);

            //修改的消息无需通知联系人，更改未读数
            if (!isMsgUpdate) {
                sendPublicMsgBaseToLastContact(publicName, publicGUID, showTime, result, serverTime,
                        localeTime, ((Obj_PublicMsgPicTxt) result).title);
            }

        }
        else if (result instanceof Obj_PublicMsgTaskNotice) {
            // //数据库插入操作
            // //是否存在， 不存在建立表
            String sql = Table_PublicAccountRecord.getCreatTableSql(publicGUID);
            getDB().execSQL(sql);
            long now = System.currentTimeMillis();

            //检查本地数据库是否存在这条记录，如果存在，则这条新收到的消息为修改后的消息
            isMsgUpdate = isRecordExist(msgGUID, publicGUID);
            Log.d("dym------------------->",
                    "jinlai Obj_PublicMsgTaskNotice isMsgUpdate= " + isMsgUpdate);
            if (isMsgUpdate) {
                //如果存在，将之前的旧纪录删除
                getDB().delete(Table_PublicAccountRecord.getTableName(publicGUID),
                        Table_PublicAccountRecord.publicRecord_col_GUID + " = ?",
                        new String[]{msgGUID});
            }

            RecordDeal.insert(getDB(), publicGUID, msgGUID, showTime, jsonMsg, "", now, msgType,
                    toUser, pushMsgPageCodeStr, pushMsgPageNameStr);

            //修改的消息无需通知联系人，更改未读数
            if (!isMsgUpdate) {
                sendPublicMsgBaseToLastContact(publicName, publicGUID, showTime, result,
                        serverTime, localeTime, ((Obj_PublicMsgTaskNotice) result).title);
            }

        }
        else if (result instanceof Obj_PublicMsgNotice) {
            Obj_PublicMsgNotice obj_publicMsgNotice = (Obj_PublicMsgNotice) result;
            //对通知类消息进行处理
            long now = System.currentTimeMillis();
            Log.d("dym------------------->", "统一通知消息插入的时间= " + now);
            String noticeID = UUID.randomUUID().toString();
            Log.d("dym------------------->", "通知类消息存进数据库");
            obj_publicMsgNotice.noticeMsgID = noticeID;
            obj_publicMsgNotice.localTime = now;
            RecordDeal.insertNotice(getDB(), publicGUID, showTime, msgInfo, now, msgType, toUser,
                    noticeID, obj_publicMsgNotice.pageType);
            //发送最近联系人广播
            Log.d("dym------------------->", "发送最近联系人广播");
            String targetName = LastContact.PUBLIC_NOTICE_TARGET_NAME;
            String targetID = LastContact.PUBLIC_NOTICE_TARGET_ID;
            String content = obj_publicMsgNotice.title;
            String fromPublicGuid = publicGUID;
            sendNoticeToLastContact(targetName, targetID, showTime, result, serverTime, localeTime,
                    content, fromPublicGuid);

            Intent noticeIntent = new Intent();
            noticeIntent.setAction(PushUtil.PublicNewMsg.NOTICE_ACTION);
            noticeIntent.putExtra(PushUtil.PublicNewMsg.EXTRA_PUBLIC_NEWMSG_SOBJ,
                    obj_publicMsgNotice);
            context.sendBroadcast(noticeIntent);
            Log.d("dym------------------->", "发送通知界面的广播");
        }


        if (msgType != 7) {
            //以前的类型，图文、历史轨迹、实时轨迹,后续新增 任务通知类型
            Intent intent = new Intent();
            intent.setAction(PushUtil.PublicNewMsg.getAction(publicGUID));
            intent.putExtra(PushUtil.PublicNewMsg.EXTRA_PUBLIC_NEWMSG_TOUSER, toUser);
            intent.putExtra(PushUtil.PublicNewMsg.EXTRA_PUBLIC_NEWMSG_SOBJ, result);
            intent.putExtra(PushUtil.PublicNewMsg.EXTRA_PUBLIC_NEWMSG_TIME, localeTime);
            intent.putExtra(PushUtil.PublicNewMsg.EXTRA_PUBLIC_NEWMSG_MSGTYPE, msgType);
            intent.putExtra(PushUtil.PublicNewMsg.EXTRA_PUBLIC_NEWMSG_MSG_GUID, msgGUID);
            intent.putExtra(PushUtil.PublicNewMsg.EXTRA_PUBLIC_NEWMSG_MSG_PUSH_PAGE_CODE, pushMsgPageCodeStr);
            intent.putExtra(PushUtil.PublicNewMsg.EXTRA_PUBLIC_NEWMSG_MSG_PUSH_PAGE_NAME, pushMsgPageNameStr);
            intent.putExtra(PushUtil.PublicNewMsg.EXTRA_PUBLIC_NEWMSG_MSG_IS_UPDATE, isMsgUpdate);
            context.sendBroadcast(intent);
            if (msgType == 6) {//实时轨迹消息，通知地图和列表界面进行更新
                Intent intentGis = new Intent();
                intentGis.setAction(PushUtil.PublicNewMsg.getGisAction(publicGUID));
                intentGis.putExtra(PushUtil.PublicNewMsg.EXTRA_PUBLIC_NEWMSG_TOUSER, toUser);
                intentGis.putExtra(PushUtil.PublicNewMsg.EXTRA_PUBLIC_NEWMSG_SOBJ, result);
                intentGis.putExtra(PushUtil.PublicNewMsg.EXTRA_PUBLIC_NEWMSG_TIME, showTime);
                intentGis.putExtra(PushUtil.PublicNewMsg.EXTRA_PUBLIC_NEWMSG_MSGTYPE, msgType);
                context.sendBroadcast(intentGis);
            }
        }

    }

    //专指一体化统一通知(其实这个和公众号关系不大，因为不和任何公众号进行绑定)
    private void sendNoticeToLastContact(String targetName, String targetID, String showTime, Obj_PublicMsgBase result, long serverTime, long localeTime,
                                         String content, String fromPublicGUID) {
        long now = System.currentTimeMillis();
        // 设置最近联系人的未读数
        int unReadCount;
        String selection = Table_LastContact.TARGET_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(targetID)};
        String unReadCountStr = SQLite.queryOneRecord(getDB(),
                Table_LastContact._TABLE_NAME,
                new String[]{Table_LastContact.UN_READ_COUNT}, "|",
                selection, selectionArgs, null);
        if (unReadCountStr != null && !unReadCountStr.equals("")) {
            unReadCount = Integer.valueOf(unReadCountStr) + 1;
        }
        else {
            unReadCount = 1;
        }
        //通过接口模块插入最近联系人
        SQLiteDatabase db = getDB();
        LastContact.insertOrUpdate(5, context, db, targetID, targetName,
                content, localeTime, serverTime, unReadCount,
                "", "");
        // 通知栏 告知 统一通知新消息 暂时不弄。
        newPANotiftDeal.pushNoticeNewMsg(LastContact.PUBLIC_NOTICE_TARGET_ID, LastContact.PUBLIC_NOTICE_TARGET_NAME);
        result.publicGUID = fromPublicGUID;
        result.showTime = showTime;
        result.localTime = now;
    }

    private void sendPublicMsgBaseToLastContact(String publicName, String publicGUID, String showTime, Obj_PublicMsgBase result, long serverTime, long localeTime, String briefMsg) {
        long now = System.currentTimeMillis();

        if (AppConfig.PUBLIC_UNREAD_TYPE == 0) {
            dealLastContact(publicName, publicGUID, showTime, result, serverTime, localeTime, briefMsg);
        }
        else if (AppConfig.PUBLIC_UNREAD_TYPE == 1) {
            dealPublicLastMsg(publicName, publicGUID, showTime, result, serverTime, localeTime, briefMsg);
        }
        else {
            dealLastContact(publicName, publicGUID, showTime, result, serverTime, localeTime, briefMsg);
            dealPublicLastMsg(publicName, publicGUID, showTime, result, serverTime, localeTime, briefMsg);
        }

        // 通知栏 告知公众号新消息
        newPANotiftDeal.notiyNewMsg(getSqliteHelp(), publicGUID);
        result.publicGUID = publicGUID;
        result.showTime = showTime;
        result.localTime = now;
    }

    private void dealLastContact(String publicName, String publicGUID, String showTime, Obj_PublicMsgBase result, long serverTime, long localeTime, String briefMsg) {
        // 设置最近联系人的未读数
        int unReadCount = 0;
        String selection = Table_LastContact.TARGET_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(publicGUID)};
        String unReadCountStr = SQLite.queryOneRecord(
                getDB(),
                Table_LastContact._TABLE_NAME,
                new String[]{Table_LastContact.UN_READ_COUNT}, "|",
                selection, selectionArgs, null);
        if (unReadCountStr != null && !unReadCountStr.equals("")) {
            unReadCount = Integer.valueOf(unReadCountStr) + 1;
        }
        else {
            unReadCount = 1;
        }
        //通过接口模块插入最近联系人
        SQLiteDatabase db = getDB();
        LastContact.insertOrUpdate(2, context,
                db, publicGUID, publicName,
                publicName + "的消息", localeTime, serverTime, unReadCount,
                publicGUID, publicName);
    }

    private void dealPublicLastMsg(String publicName, String publicGUID, String showTime, Obj_PublicMsgBase result, long serverTime, long localeTime, String briefMsg) {
        // 设置公众号消息未读数
        int unRead = 0;
        String select = Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_ID + "=?";
        String[] selectArgs = new String[]{String.valueOf(publicGUID)};
        String unReadtStr = SQLite.queryOneRecord(
                getDB(),
                Table_PublicAccount_LastMsg._TABLE_NAME,
                new String[]{Table_PublicAccount_LastMsg.PUBLIC_ACCOUNT_UN_READ_COUNT}, "|",
                select, selectArgs, null);
        if (unReadtStr != null && !unReadtStr.equals("")) {
            unRead = Integer.valueOf(unReadtStr) + 1;
        }
        else {
            unRead = 1;
        }
        //通过接口模块插入公众号消息表
        SQLiteDatabase db1 = getDB();
        PublicMessage.insertOrUpdate(context,
                db1, publicGUID, publicName,
                localeTime, serverTime, unRead, briefMsg + "收到一条消息");
    }

    private String getPublicName(String id) {
        return BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + id;
    }

    private boolean isRecordExist(String msgGUID, String publicAccountID) {
        if (TextUtils.isEmpty(msgGUID)) {
            return false;
        }

        boolean result = false;
        String sql = "select * from " + Table_PublicAccountRecord.getTableName(publicAccountID) + " where " +
                Table_PublicAccountRecord.publicRecord_col_GUID + " = '" + msgGUID + "' ";
        Cursor cursor = getDB().rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            result = true;
        }
        if (cursor != null) {
            cursor.close();
        }
        return result;
    }
}