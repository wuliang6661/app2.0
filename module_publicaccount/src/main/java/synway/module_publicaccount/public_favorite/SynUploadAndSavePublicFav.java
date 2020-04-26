package synway.module_publicaccount.public_favorite;

import android.content.Context;
import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import synway.module_interface.config.netConfig.NetConfig;
import synway.module_interface.config.netConfig.Sps_NetConfig;
import synway.module_publicaccount.customconfig.CustomConfigRW;
import synway.module_interface.config.CustomConfigType;
import synway.module_publicaccount.public_favorite.obj.Obj_PublicAccount_Favorite;
import synway.module_publicaccount.publiclist.bean.App_InformationBean;

import static synway.module_publicaccount.publiclist.bean.App_InformationBean.getAppInforMationObject;

//import android.util.Log;

/**
 * Created by quintet on 2016/10/17.
 * <p>
 * 这是对于 常用应用配置 的上传和入库的包装类，是基于CustomConfigRW进一步封装。
 * 因为只能保存一条记录，所以这里将所有的信息组装成json，然后进行保存。同理读数据也是一样，
 * 读出json，然后恢复成需要的对象。
 */
public class SynUploadAndSavePublicFav {
    private Handler handler = null;

    private Context context = null;


    /**
     * PublicAccountID
     */
    public static final String ID = "ID";
    /**
     * PublicAccountName
     */
    public static final String Name = "Name";
    /**
     * MenuID
     */
    public static final String MenuId = "MenuId";

    /**
     * 网页跳转类的名称
     */
    public static final String menuName = "menuName";
    /**
     * 网页跳转类的url
     */
    public static final String menuUrl = "menuUrl";
    /**
     * 公众号和网页跳转的区分
     */
    public static final String isHtml = "isHtml";
    /*公众号图标的名字*/
    public static final String accountPicId = "accountpicid";
    /**
     * 常用应用的配置名称
     */
    public static final String PublicFavorite = "PublicFavorite";
    /**
     * 常用应用配置的key
     */
    public static final String key = "SavePublicFavorite";
    ///** 上传配置的IP*/
    //public static final String IP = "172.16.1.129";
    ///** 上传配置的Port*/
    //public static final int Port = 7000;
    public static String SynPublictype="Type";
    public static String AppInformation="appinformation";
    public SynUploadAndSavePublicFav(Context context) {
        handler = new Handler();
        this.context = context;


    }


    public void start(List<Obj_PublicAccount_Favorite> favoriteList, String userID, Boolean ifstart) {
        new Thread(new mRunnable(favoriteList, userID, ifstart)).start();
    }


    public void start(Obj_PublicAccount_Favorite obj_publicFavorite, String loginUserID) {

        new Thread(new mRunnable2(obj_publicFavorite, loginUserID)).start();
    }


    public void stop() {
        if (listener != null) {
            listener = null;
        }
    }


    /**
     * 用于单个上传及入库
     */
    private class mRunnable2 implements Runnable {
        private Obj_PublicAccount_Favorite obj_publicFavorite = null;

        private String loginUserID = null;


        private mRunnable2(Obj_PublicAccount_Favorite obj_publicFavorite, String loginUserID) {
            this.obj_publicFavorite = obj_publicFavorite;
            this.loginUserID = loginUserID;

        }


        @Override
        public void run() {
            NetConfig netConfig = Sps_NetConfig.getNetConfigFromSpf(context);


            if (obj_publicFavorite == null) {
                return;
            }

            String jsonToString = "";
            String result = CustomConfigRW.read(CustomConfigType.PUBLICFAVORITE, key);

            /** result 为null的情况表示数据库表中没有关于常用应用配置的记录，
             *         为"" 空字符串的时候情况表示常用应用的个数为0个 */
            if (result == null || result.equals("")) {

                JSONObject root = new JSONObject();

                JSONArray array = new JSONArray();

                try {
                    JSONObject object = new JSONObject();
                    object.put(ID, obj_publicFavorite.ID);
                    object.put(Name, obj_publicFavorite.Name);
                    object.put(menuName, obj_publicFavorite.menuName);
                    object.put(menuUrl, obj_publicFavorite.menuUrl);
                    object.put(isHtml, obj_publicFavorite.isHtml);
                    object.put(accountPicId, obj_publicFavorite.fc_mobilepic);
                    object.put(MenuId, obj_publicFavorite.MenuId);
                    object.put(SynPublictype,obj_publicFavorite.type);
                    object.put(AppInformation,getAppInforMationObject(obj_publicFavorite.app_information).toString());
                    array.put(object);

                    root.put(PublicFavorite, array);

                    jsonToString = root.toString();
                    //Log.e("dym", "jsonToString= " + jsonToString);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Future<String> future = CustomConfigRW.write(netConfig.httpIP, netConfig.httpPort, loginUserID,
                        CustomConfigType.PUBLICFAVORITE, key,
                        jsonToString);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onStart(false);
                        }
                    }
                });
                try {
                    final String futureResult = future.get();

                    if (futureResult.equals("")) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null) {
                                    listener.onSingleSuccess("保存成功", obj_publicFavorite);
                                }

                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null) {
                                    listener.onFail(futureResult, false);
                                }

                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            } else {

                /** 有多个常用应用的情况(大于或等于一个)
                 *  在之前保存的json基础上，继续添加数据*/

                try {
                    JSONObject root = new JSONObject(result);

                    //Log.e("dym", "root= " + root);
                    JSONArray array = root.getJSONArray(PublicFavorite);

                    JSONObject newObject = new JSONObject();
                    newObject.put(ID, obj_publicFavorite.ID);
                    newObject.put(Name, obj_publicFavorite.Name);
                    newObject.put(menuName, obj_publicFavorite.menuName);
                    newObject.put(menuUrl, obj_publicFavorite.menuUrl);
                    newObject.put(isHtml, obj_publicFavorite.isHtml);
                    newObject.put(accountPicId, obj_publicFavorite.fc_mobilepic);
                    newObject.put(MenuId, obj_publicFavorite.MenuId);
                    newObject.put(SynPublictype,obj_publicFavorite.type);
                    newObject.put(AppInformation,getAppInforMationObject(obj_publicFavorite.app_information).toString());
                    array.put(newObject);

                    root.put(PublicFavorite, array);

                    jsonToString = root.toString();
                    //Log.e("dym", "jsonToString= " + jsonToString);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Future<String> future = CustomConfigRW.write(netConfig.httpIP, netConfig.httpPort, loginUserID,
                        CustomConfigType.PUBLICFAVORITE, key,
                        jsonToString);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onStart(false);
                        }
                    }
                });
                try {
                    final String futureResult = future.get();

                    if (futureResult.equals("")) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null) {
                                    listener.onSingleSuccess("保存成功", obj_publicFavorite);
                                }

                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null) {
                                    listener.onFail(futureResult, false);
                                }

                            }
                        });
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    /**
     * 用于List的上传和入库
     */
    private class mRunnable implements Runnable {
        private List<Obj_PublicAccount_Favorite> favoriteList = null;

        private String userID = null;
        private Boolean ifstart = false;

        private mRunnable(List<Obj_PublicAccount_Favorite> favoriteList, String userID, Boolean ifstart) {
            this.favoriteList = favoriteList;
            this.userID = userID;
            this.ifstart = ifstart;

        }


        @Override
        public void run() {
            NetConfig netConfig = Sps_NetConfig.getNetConfigFromSpf(context);
            if (favoriteList == null) {
                return;
            }

            if (favoriteList.size() != 0) {
                JSONObject root = new JSONObject();

                JSONArray array = new JSONArray();
                String jsonToString = "";
                for (int i = 0; i < favoriteList.size(); i++) {
                    try {

                        JSONObject object = new JSONObject();
                        object.put(ID, favoriteList.get(i).ID);
                        object.put(Name, favoriteList.get(i).Name);
                        object.put(menuName, favoriteList.get(i).menuName);
                        object.put(menuUrl, favoriteList.get(i).menuUrl);
                        object.put(isHtml, favoriteList.get(i).isHtml);
                        object.put(accountPicId, favoriteList.get(i).fc_mobilepic);
                        object.put(MenuId, favoriteList.get(i).MenuId);
                        array.put(object);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    root.put(PublicFavorite, array);

                    jsonToString = root.toString();

                    //Log.e("dym", "jsonToString= " + jsonToString);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Future<String> future = CustomConfigRW.write(netConfig.httpIP, netConfig.httpPort, userID,
                        CustomConfigType.PUBLICFAVORITE, key,
                        jsonToString);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onStart(ifstart);
                        }
                    }
                });

                try {
                    final String futureResult = future.get();

                    if (futureResult.equals("")) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null) {
                                    listener.onListSuccess("保存成功");
                                }

                            }
                        });
                    } else {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                if (listener != null) {
                                    listener.onFail(futureResult, ifstart);
                                }

                            }
                        });
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            } else {

                /**
                 * 传过来的list为size为0 时候的处理,保存一个空字符串
                 *  */

                Future<String> future = CustomConfigRW.write(netConfig.httpIP, netConfig.httpPort, userID,
                        CustomConfigType.PUBLICFAVORITE, key,
                        "");

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onStart(ifstart);
                        }
                    }
                });

                try {
                    final String futureResult = future.get();

                    if (futureResult.equals("")) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null) {
                                    listener.onListSuccess("保存成功");
                                }

                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                if (listener != null) {
                                    listener.onFail(futureResult, ifstart);
                                }

                            }
                        });
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

        }
    }


    public interface OnSynUploadAndSavePublicFavListener {

        //上传和入库开始之前的回调
        void onStart(Boolean ifstart);

        //上传和入库成功之后的回调(单个)
        void onSingleSuccess(String msg, Obj_PublicAccount_Favorite obj_publicFavorite);

        //上传和入库成功之后的回调(多个)
        void onListSuccess(String msg);

        //失败之后的回调
        void onFail(String reason, Boolean aBoolean);
    }


    private OnSynUploadAndSavePublicFavListener listener = null;


    public void setOnSynUploadAndSavePublicFavListener(OnSynUploadAndSavePublicFavListener listener) {
        this.listener = listener;
    }

}
