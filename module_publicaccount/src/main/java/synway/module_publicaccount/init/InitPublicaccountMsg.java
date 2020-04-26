package synway.module_publicaccount.init;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import qyc.library.tool.http.HttpDownLoad;
import qyc.library.tool.http.HttpPost;
import synway.module_interface.config.netConfig.NetConfig;
import synway.module_interface.config.userConfig.Sps_RWLoginUser;
import synway.module_interface.module.OnInit;
import synway.module_publicaccount.Main;
import synway.module_publicaccount.publiclist.GetHttpData.GetPublicList2;
import synway.module_publicaccount.publiclist.GetHttpData.PublicPost;
import synway.module_publicaccount.publiclist.Obj_PublicAccount;

/**
 * Created by 13itch on 2016/11/9.
 */

public class InitPublicaccountMsg implements OnInit {
    private Context context=null;
//    public SyncGetMenuList syncGetMenuList = null;
    private NetConfig netConfig=null;
    ArrayList<Obj_PublicAccount> publicAccountsHttp=null;
    private HttpDownLoad httpDownLoad=null;
    @Override
    public String[] onInit(Context context, NetConfig netConfig) {
        this.context=context;
        this.netConfig=netConfig;
        publicAccountsHttp=new ArrayList<Obj_PublicAccount>();
        this.httpDownLoad = new HttpDownLoad(100);
        return get(context, netConfig);
    }

    @Override
    public String getTip() {
        return "正在同步公众帐号列表...";
    }


    private String[] get(Context context, NetConfig netConfig) {
        String strUserID = Sps_RWLoginUser.readUserID(context);
        if (strUserID == null) {
            return new String[] { "同步同步公众模块列表失败", "没有用户ID" };
        }
         ArrayList<String> idList=new ArrayList<String>();
        String url = GetPublicList2.geturl(netConfig.publicServerIP, netConfig.publicServerPort,strUserID);
        JSONObject requestJson = GetPublicList2.getJson(strUserID);
        final JSONObject resultJson = HttpPost.postJsonObj(url, new JSONObject());
        final String reuslt[] = PublicPost.checkResult(resultJson);
        if(null != reuslt){
            final String deatil = resultJson.optString("Detail");
            return new String[] { "同步公众模块列表失败", reuslt[0] + ",请重试", reuslt[1] + "\n" + deatil};
        }
        JSONArray jsonArray  = resultJson.optJSONArray("result");
        int length = jsonArray.length();
        if(length == 0){
            return new String[] { "同步公众模块列表失败", "没有公众号数据"};
        }
        GetPublicList2.getPublicList2(jsonArray,Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase());
        return null;
//        publicAccountsHttp=(ArrayList<Obj_PublicAccount>)publicList.get("alllist");
//        //这里开始拉取菜单数据
//        String menuurl = GetMenuList2.geturl(netConfig.publicServerIP, netConfig.publicServerPort,strUserID);
//        ArrayList<String> publicidList = GetMenuList2.getidList(publicAccountsHttp);
//        JSONObject menurequestJson = GetMenuList2.getJson(strUserID, publicidList);
//        menurequestJson = HttpPost.postJsonObj(menuurl, menurequestJson);
////        String menureuslt []= HttpPost.checkResult(resultJson);
//        if (null != reuslt) {
//            return new String[] {"同步公众模块菜单列表失败",resultJson.optString("DETAIL")};
//        }
//        JSONArray jsonMenuArray = menurequestJson.optJSONArray("FC_MENU_INFO");
//        ArrayList<Obj_Menu> firstMenu=new ArrayList<Obj_Menu>();
//        ArrayList<Obj_Menu> secondMenu=new ArrayList<Obj_Menu>();
//        if(jsonMenuArray!=null) {
//            if (jsonMenuArray.length() > 0) {
//                HashMap<String, ArrayList<Obj_Menu>> menu = GetMenuList2.getMenu(jsonMenuArray);
//                firstMenu = menu.get("firstmenu");
//                secondMenu = menu.get("secondmenu");
//            }
//        }
//        savedb(firstMenu,secondMenu);
    }
//    public  void savedb(ArrayList<Obj_Menu> firstmenus, ArrayList<Obj_Menu> secondMenuList) {
//        Log.i("testy", "初始化菜单获取成功");
//        //先清空菜单表
////        SQLite.del(Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase(), Table_PublicMenu._TABLE_NAME, null, null);
//        //查询公众账号是否包含菜单
//        for (int i = 0; i < publicAccountsHttp.size(); i++) {
//            Obj_PublicAccount obj_publicAccount = publicAccountsHttp.get(i);
//            ArrayList<Obj_Menu> obj_menus = new ArrayList<Obj_Menu>();
//            obj_menus.clear();
//            ArrayList<Obj_Menu> obj_menusecond = new ArrayList<Obj_Menu>();
//            obj_menusecond.clear();
//            Boolean isinclude = false;
//            //遍历一级菜单
//            if (firstmenus != null && firstmenus.size() > 0) {
//                for (int j = 0; j < firstmenus.size(); j++) {
//                    //包含一级菜单
//                    if (firstmenus.get(j).ID.equals(obj_publicAccount.ID)) {
//                        isinclude = true;
//                        obj_menus.add(firstmenus.get(j));
//                        if (secondMenuList != null && secondMenuList.size() > 0)
//                            for (int k = 0; k < secondMenuList.size(); k++) {
//                                if (secondMenuList.get(k).menuFather.equals(firstmenus.get(j).menuGUID)) {
//                                    obj_menusecond.add(secondMenuList.get(k));
//                                }
//                            }
//                    }
//                }
//            }
//            if (isinclude) {
//                DbUntil.DBDeal(obj_menus, obj_menusecond, obj_publicAccount.ID,Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase());
//            } else {
//                DbUntil.clearDBfromGUID(obj_publicAccount.ID,Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase());
//            }
//            obj_publicAccount.firstmenus = obj_menus;
//            obj_publicAccount.secondmenus = obj_menusecond;
//            publicAccountsHttp.set(i, obj_publicAccount);
//        }
//            //下载头像
//            downLoadUserHead();
//
//    }
//
//    private void downLoadUserHead() {
//        if (publicAccountsHttp.size() == 0) {
//            // ^uID
//            System.out.println("downLoadHead:^uID");
//            return;
//        }
//        ArrayList<String> listNoHeadAndShowing = new ArrayList<String>();
//        for (int i = 0; i < publicAccountsHttp.size(); i++) {
//            String showingID = publicAccountsHttp.get(i).ID;
//            String piid=publicAccountsHttp.get(i).fc_mobilepic;
//                listNoHeadAndShowing.add(piid);
//                //遍历添加一级或者二级菜单中的图片
//                ArrayList<Obj_Menu> obj_first_menus=publicAccountsHttp.get(i).firstmenus;
//                if(obj_first_menus!=null){
//                    for(int j=0;j<obj_first_menus.size();j++){
//                        listNoHeadAndShowing.add(obj_first_menus.get(j).menuPicUrl);
//                    }}
//                ArrayList<Obj_Menu> obj_secnd_menus=publicAccountsHttp.get(i).secondmenus;
//                if(obj_secnd_menus!=null) {
//                    for (int k=0;k<obj_secnd_menus.size();k++) {
//                        listNoHeadAndShowing.add(obj_secnd_menus.get(k).menuPicUrl);
//                    }
//                }
//        }
//        if (listNoHeadAndShowing.size() == 0) {
//            return;
//        }
//        File folder = new File(BaseUtil.FILE_PUBLIC_ACCOUNT_THU);
//        if (!folder.exists()) {
//            folder.mkdirs();
//        }
//        start(listNoHeadAndShowing);
//    }
//    public void start(ArrayList<String> idList) {
//        for (int i = 0; i < idList.size(); i++) {
//            start(idList.get(i));
//        }
//    }
//    public void start(String id) {
//        // 如果头像文件存在就不要去下了
//        if(id!=null) {
//            File filePath = new File(BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + (id));
//            if (filePath.exists()) {
//            } else {
//                Log.i("testy", "初始化下载头像的id" + id);
//                String urlStr = "http://" + netConfig.httpIP + ":" + netConfig.httpPort + "/" + "OSCUserPic/Public_" + id;
//                httpDownLoad.startDownLoad(urlStr, BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + (id), id);
//            }
//        }
//    }



}
