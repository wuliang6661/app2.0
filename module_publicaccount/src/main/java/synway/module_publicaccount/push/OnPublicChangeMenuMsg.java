//package synway.module_publicaccount.push;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.database.sqlite.SQLiteDatabase;
//import android.util.Log;
//import android.util.SparseArray;
//import java.io.File;
//import java.util.ArrayList;
//import java.util.HashMap;
//import net.grandcentrix.tray.AppPreferences;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import qyc.library.tool.http.HttpDownLoad;
//import qyc.library.tool.http.HttpPost;
//import qyc.net.push.synwayoscpush.SPushFacInterface;
//import synway.module_interface.config.BaseUtil;
//import synway.module_interface.config.netConfig.NetConfig;
//import synway.module_interface.config.netConfig.Sps_NetConfig;
//import synway.module_interface.config.userConfig.Sps_RWLoginUser;
//import synway.module_publicaccount.analytical.fac.IAnalytical_Base;
//import synway.module_publicaccount.db.table_util.Table_PublicAccount;
//import synway.module_publicaccount.db.table_util.Table_PublicMenu;
//import synway.module_publicaccount.public_chat.Obj_Menu;
//import synway.module_publicaccount.publiclist.GetHttpData.GetMenuList;
//import synway.module_publicaccount.publiclist.GetHttpData.GetPublicList;
//import synway.module_publicaccount.publiclist.Obj_PublicAccount;
//import synway.module_publicaccount.until.BroadCastUntil;
//import synway.module_publicaccount.until.DbUntil;
//
//public class OnPublicChangeMenuMsg extends SPushFacInterface {
//
//    private SparseArray<IAnalytical_Base> analys = null;
//
//    private NewPANotiftDeal newPANotiftDeal = null;
//    private HttpDownLoad httpDownLoad = null;
//    private String urlHead = null;
//    private NetConfig netConfig=null;
//    private long lastPushTime = 0L;
//    private AppPreferences appPreferences=null;
//    @Override
//    public int[] regist() {
//        return new int[]{5004};
//    }
//
//    @Override
//    public void onCreat(Object o) {
//        this.appPreferences = new AppPreferences(context);
//    }
//
//    @Override
//    public void onDestory() {
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public void onReceive(int type,String jsonStr) {
//        run(type, jsonStr);
//    }
//    //1.通过Http重新下载公众平台信息.
//    //注意点A:由于本方法在推送线程中执行,因此不需要额外开线程去下载.需要注意,Http超时时间不能过长,
//    //       如果超时时间过长会导致推送线程被占用而收不到推送消息.建议最多设置为5秒.
//    //注意点B:由于超时时间不长,Http自动重试的次数就不会很多,下载失败的概率增加.当下载失败时,
//    //       应在数据库或SharedPreferences中增加一个标识符.表示公众平台虽然已经更新,但本次下载失败了.
//    //       当公众平台的界面展现出来时,如果该标识符存在,则重新去下载一次.直到下载成功再清理该标识符.
//    //2.将Http下载的公众平台信息更新到数据库中.
//    //3.通过广播通知界面重新从数据库里读取并加载.
//    //注意点:广播仅通知界面去重新读取数据库,本身不携带内容.
//    //这样做的原因是:界面从数据库里读取并加载是已经存在的动作,因此该动作可以复用.广播仅用来触发该动作,而不携带内容,这样改动最小.
//    //0316补充：菜单更新推送不再下载图片，界面如果找不到头像就自己去下载
//    private void run(int type, String jsonStr) {
//
//        //正常情况下后台第一次修改，然后推送过来，第二次马上修改推送过来，这里间隔一般是大于5秒的
//        //但是当一个用户一段时间没登陆，会短时间补推很多条，但是其实我们只需要去服务器更新一次就好，哪怕
//        //之前的时间里后台修改了很多次，我们只需要更新一次就足够了，所以这里对短时间内的大量推送做了处理
//        //避免频繁去拉取最新数据
//        long currentTime = System.currentTimeMillis();
//        if((currentTime-lastPushTime)<5*1000){
//            Log.d("dym------------------->", "屏蔽掉无效的推送");
//            return;
//        }
//        lastPushTime = currentTime;
//
//        Log.i("testy","收到更新的推送");
//        Log.d("dym------------------->", "OnPublicChangeMenuMsg 收到推送,jsonStr= "+jsonStr);
//        String userid= Sps_RWLoginUser.readUserID(context);
//
//        //并且在sharedPrefence保存一个值,目的是当公众号界面被detroyView后移除广播监听，会收不到广播，所以
//        //需要对此值进行判断，看是否需要进行一次更新
//        String key = "KEY_"+userid;
//        Log.d("dym------------------->", "edit11111 = "+appPreferences.getBoolean(key,false));
//        appPreferences.put(key,true);
//
//        Log.d("dym------------------->", "edit22222 = "+appPreferences.getBoolean(key,false));
//        Intent updateIntent = new Intent();
//        updateIntent.setAction("onPublicAuthorityUpdate");
//        context.sendBroadcast(updateIntent);
//
//
//        //以下是后台线程去更新菜单信息
//        SQLiteDatabase db= getDB();
//        netConfig= Sps_NetConfig.getNetConfigFromSpf(context);
//
//        JSONObject requestJson = GetPublicList.getJson(userid);
//        final JSONObject resultJson = HttpPost.postJsonObj(GetPublicList.geturl(netConfig.httpIP, netConfig.httpPort), requestJson);
//        final String reuslt[] = HttpPost.checkResult(resultJson);
//        ArrayList<String> idListpublic = new ArrayList<String>();
//        if(null != reuslt){//下载公众帐号信息失败   为了避免覆盖  以userid作为KEY
//            SharedPreferences sp = context.getSharedPreferences("public_account_download", Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = sp.edit();
//            editor.putBoolean(userid, false);
//            editor.commit();
//            return;
//        }
//        JSONArray jsonArray = resultJson.optJSONArray("FC_BASIC_INFO");
//        int length = jsonArray.length();
//        if(length == 0){//没有公众号数据,清空公众号表格
//            String sql = "delete from "+ Table_PublicAccount._TABLE_NAME;
//            db.execSQL(sql);
//            //清空菜单表格
//            String sqlmenu= "delete from "+ Table_PublicMenu._TABLE_NAME;
//            db.execSQL(sqlmenu);
//        }else{
//            HashMap<String,Object> publicList=GetPublicList.getPublicList(jsonArray,idListpublic,getDB());//已经包含存数据库的动作
//            ArrayList<Obj_PublicAccount> AllList=(ArrayList<Obj_PublicAccount>)publicList.get("publiclist");
//            Log.i("testy","得到的公众号数据"+AllList.size());
//            ArrayList<String> idList = GetMenuList.getidList(AllList);
//            JSONObject requestMenuJson = GetMenuList.getJson(userid, idList);
//            JSONObject resultMenuJson = HttpPost.postJsonObj(GetMenuList.geturl(netConfig.httpIP, netConfig.httpPort), requestMenuJson);
//            String menuReuslt[] = HttpPost.checkResult(resultJson);
//            if (null != menuReuslt) {//下载公众帐号菜单信息失败   为了避免覆盖  以userid作为KEY
//                SharedPreferences sp = context.getSharedPreferences("public_account_download", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = sp.edit();
//                editor.putBoolean(userid, false);
//                editor.commit();
//                return;
//            }
//            JSONArray jsonMenuArray = resultMenuJson.optJSONArray("FC_MENU_INFO");
//            Log.i("testy","得到的全部菜单信息是"+jsonMenuArray);
//            HashMap<String,ArrayList<Obj_Menu>> menu=GetMenuList.getMenu(jsonMenuArray);
//            ArrayList<Obj_Menu>  firstmenus =menu.get("firstmenu");
//            ArrayList<Obj_Menu> secondMenuList =menu.get("secondmenu");
//            if (firstmenus.size() == 0) {
//                //清空菜单表格
//                String sqlmenu= "delete from "+ Table_PublicMenu._TABLE_NAME;
//                db.execSQL(sqlmenu);
//            }else{
//                SharedPreferences sp = context.getSharedPreferences("public_account_download", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = sp.edit();
//                editor.putBoolean(userid, true);
//                editor.commit();
//                //查询公众账号是否包含菜单
//                for(int i=0;i<AllList.size();i++){
//                    Obj_PublicAccount obj_publicAccount=AllList.get(i);
//                    Log.i("testy","公众帐号是"+obj_publicAccount.name+obj_publicAccount.ID);
//                    ArrayList<Obj_Menu> obj_menus=new ArrayList<Obj_Menu>();
//                    obj_menus.clear();
//                    ArrayList<Obj_Menu> obj_menusecond=new ArrayList<Obj_Menu>();
//                    obj_menusecond.clear();
//                    Boolean isinclude=false;
//                    //遍历一级菜单
//                    if(firstmenus!=null) {
//                        for (int j = 0; j < firstmenus.size(); j++) {
//                            Log.i("testy","一级菜单的名字和id"+firstmenus.get(j).Name+firstmenus.get(j).ID);
//                            //包含一级菜单
//                            if (firstmenus.get(j).ID.equals(obj_publicAccount.ID)) {
//                                isinclude = true;
//                                obj_menus.add(firstmenus.get(j));
//                                for (int k = 0; k < secondMenuList.size(); k++) {
//                                    if (secondMenuList.get(k).menuFather.equals(firstmenus.get(j).menuGUID)) {
//                                        obj_menusecond.add(secondMenuList.get(k));
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    if(isinclude){
//                        DbUntil.DBDeal(obj_menus,obj_menusecond,obj_publicAccount.ID,getDB());
//                    }else{
//                        DbUntil.clearDBfromGUID(obj_publicAccount.ID,getDB());
//                    }
//                    obj_publicAccount.firstmenus=obj_menus;
//                    obj_publicAccount.secondmenus=obj_menusecond;
//                    AllList.set(i,obj_publicAccount);
//                }
//                // 发送到常用应用界面   常用应用界面重新拉取公众号以及菜单数据并刷新常用应用界面
//                Intent intent = new Intent();
//                intent.setAction(BroadCastUntil.ChangeMenuMsg.ChangeMenu);
//                context.sendBroadcast(intent);
////                downLoadUserHead(AllList);
//            }
//
//        }
//        Intent intent = new Intent();
//        intent.setAction(BroadCastUntil.ChangeMenuMsg.ChangeMenu);
//        context.sendBroadcast(intent);
//    }
//
//
//    /***
//     * 图片下载
//     */
//    private void downLoadUserHead(ArrayList<Obj_PublicAccount> publicAccountsHttp) {
//        if (publicAccountsHttp.size() == 0) {
//            return;
//        }
//        ArrayList<String> listNoHeadAndShowing = new ArrayList<String>();
//        for (int i = 0; i < publicAccountsHttp.size(); i++) {
//            String showingID = publicAccountsHttp.get(i).ID;
//            String piid=publicAccountsHttp.get(i).fc_mobilepic;
//            if(piid!=null&&!piid.equals("")) {
//                listNoHeadAndShowing.add(piid);
//            }
//            //遍历添加一级或者二级菜单中的图片
//            ArrayList<Obj_Menu> obj_first_menus=publicAccountsHttp.get(i).firstmenus;
//            if(obj_first_menus!=null&&obj_first_menus.size()!=0){
//                for(int j=0;j<obj_first_menus.size();j++){
//                    String firstpicid=obj_first_menus.get(j).menuPicUrl;
//                    if(firstpicid!=null&&!firstpicid.equals("")) {
//                        listNoHeadAndShowing.add(firstpicid);
//                    }
//
//                }}
//            ArrayList<Obj_Menu> obj_secnd_menus=publicAccountsHttp.get(i).secondmenus;
//            if(obj_secnd_menus!=null&&obj_secnd_menus.size()!=0) {
//                for (int k=0;k<obj_secnd_menus.size();k++) {
//                    String secondmenupic=obj_secnd_menus.get(k).menuPicUrl;
//                    if(secondmenupic!=null&&!secondmenupic.equals("")) {
//                        listNoHeadAndShowing.add(secondmenupic);
//                    }
//                }
//            }
//
//        }
//        if (listNoHeadAndShowing.size() == 0) {
//            return;
//        }
//        start(listNoHeadAndShowing); //下载图片
//    }
//    public void start(ArrayList<String> idList) {
//        File folder = new File(BaseUtil.FILE_PUBLIC_ACCOUNT_THU);
//        if (!folder.exists()) {
//            folder.mkdirs();
//        }
//        urlHead = "http://" + netConfig.httpIP + ":" + netConfig.httpPort + "/" + "OSCUserPic/Public_";
//        httpDownLoad = new HttpDownLoad(100);
//        httpDownLoad.setOnHttpDownloadListen(onHttpDownLoad);
//        for (int i = 0; i < idList.size(); i++) {
//            start(idList.get(i));
//        }
//    }
//    /**
//     * <p>
//     * 这里start可以调用多次，每次调用都加入头像下载；但是正在下载的头像不会重复下载，它根据ID来区分；如果头像文件存在就不去下载了。
//     */
//    public void start(String id) {
////		Log.i("testy","下载头像"+id);
//        // 如果头像文件存在就不要去下了
//        File filePath = new File(BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + (id));
//        if (filePath.exists()) {
//            // 发送到常用应用界面   常用应用界面刷新界面
//            Intent intent = new Intent();
//            intent.setAction(BroadCastUntil.ChangeMenuMsg.freshUI);
//            context.sendBroadcast(intent);
//        } else {
////			String urlStr = urlHead + id + "_small";
//            String urlStr="";
//            if(id!=null&&!id.equals("")) {
//                if (id.contains(".")) {
//                    urlStr = "http://" + netConfig.httpIP + ":" + netConfig.httpPort + "/" + "OSCUserPic/" + id;
//                } else {
//                    urlStr = urlHead + id;
//                }
//                httpDownLoad.startDownLoad(urlStr, BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + (id), id);
//            }
//
//        }
//    }
//    private HttpDownLoad.OnHttpDownLoad onHttpDownLoad = new HttpDownLoad.OnHttpDownLoad() {
//
//        @Override
//        public void onStart(Object key, long downloadLength, File filePath) {
//
//        }
//
//        @Override
//        public void onProgressChanged(Object key, int progress) {
//
//        }
//
//        @Override
//        public void onFinish(Object key, boolean result, String msg, File filePath) {
//            if (result) {
//                final String id = (String) key;
//                // 发送到常用应用界面   常用应用界面刷新界面
//                Intent intent = new Intent();
//                intent.setAction(BroadCastUntil.ChangeMenuMsg.freshUI);
//                context.sendBroadcast(intent);
//            } else {
//            }
//        }
//    };
//}