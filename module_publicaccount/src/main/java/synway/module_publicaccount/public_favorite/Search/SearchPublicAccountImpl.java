package synway.module_publicaccount.public_favorite.Search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import synway.module_interface.config.userConfig.Sps_RWLoginUser;
import synway.module_interface.searchinterface.SearchInterface;
import synway.module_interface.searchinterface.SearchMenuEntity;
import synway.module_publicaccount.R;
import synway.module_publicaccount.public_chat.Obj_Menu;
import synway.module_publicaccount.public_chat.PAWebView;
import synway.module_publicaccount.public_chat.PAWebViewAct;
import synway.module_publicaccount.public_chat.PublicAccountChatActNormal;
import synway.module_publicaccount.public_chat.bottom.Base64Helper;
import synway.module_publicaccount.public_favorite.Search.adapter.MenuAccountAdapter;
import synway.module_publicaccount.public_favorite.Search.adapter.PublicAccountAdapter;
import synway.module_publicaccount.publiclist.Obj_PublicAccount;
import synway.module_publicaccount.until.AppUtil;
import synway.module_publicaccount.until.NetUrlUntil;
import synway.module_publicaccount.weex_module.beans.WxMapData;

import static synway.module_publicaccount.until.AppUtil.doStartApplicationWithPackageName;

/**
 * Created by ysm on 2017/2/13.
 */

public class SearchPublicAccountImpl extends SearchInterface {
    private View view = null;
    private View publicView = null,menuView=null;
    private ListView publicListView,menuListView;
    private PublicAccountAdapter publicAccountAdapter;
    private MenuAccountAdapter menuAccountAdapter;
    private View btnPublicMore,btnMenuMore;
    //搜索关键词
    private String  keyword;
    //搜索公众号
    private SearchPublicAccount searchPublicAccount=null;
    //搜索菜单
    private SearchPublicMnu searchPublicMnu;
    private ArrayList<Obj_PublicAccount> publicList;
    private boolean isPublicEmpty = false;
    private int publicShowNum = 3;
    private TextView publicNumTextView=null,menuNumTextView=null;
    private OnSearchListen onSearchListen = null;
    private ArrayList<Obj_Menu> menuresultList;
    private Activity act;
    @Override
    public void init(Activity activity) {
        this.act=activity;
        LayoutInflater layoutInflater = LayoutInflater.from(act);
        view = layoutInflater.inflate(R.layout.model_public_account_search, null, false);
        publicView = view.findViewById(R.id.public_result_linearLayout);
        publicListView = view.findViewById(R.id.public_listview);
        publicNumTextView= view.findViewById(R.id.public_num_textview);
        publicAccountAdapter = new PublicAccountAdapter(act);
        publicListView.setOnItemClickListener(onPublicItemClickListener);
        publicListView.setAdapter(publicAccountAdapter);
        searchPublicAccount=new SearchPublicAccount();
        searchPublicAccount.setOnSearchPublicAccount(onSearchPublicAccountListen);
        //更多公众号
        btnPublicMore = view.findViewById(R.id.public_more_linearLayout);
        btnPublicMore.setOnClickListener(onClickListener);

    }

    @Override
    public void start(String keyWord) {

        this.keyword=keyWord;
        searchPublicAccount.start(keyword);
//        searchPublicMnu.start(keyWord);
    }

    @Override
    public void stop() {
        searchPublicAccount.stop();
    }

    @Override
    public void onDestroy() {
        stop();
    }

    @Override
    public void setOnListen(OnSearchListen onSearchListen) {
        this.onSearchListen = onSearchListen;

    }

    private SearchPublicAccount.OnSearchPublicAccountListen onSearchPublicAccountListen = new SearchPublicAccount.OnSearchPublicAccountListen() {
        @Override
        public void onResult(ArrayList<Obj_PublicAccount> resultList) {
            SearchPublicAccountImpl.this.publicList = resultList;
            if (resultList!=null&&resultList.size() > 0) {
                isPublicEmpty = false;
                ArrayList<Obj_PublicAccount> showPublicList = new ArrayList<>();
                for (int i = 0; i < resultList.size() && i < publicShowNum; i++) {
                    showPublicList.add(resultList.get(i));
                }
                publicAccountAdapter.setKeyWord(keyword);
                publicAccountAdapter.reset(showPublicList);
                publicAccountAdapter.refresh();
                publicNumTextView.setText("(" + resultList.size() + ")");
                publicView.setVisibility(View.VISIBLE);
                setListViewHeightBasedOnChildren(publicListView);
            }
            else {
                view.setVisibility(View.GONE);
            }
            if(onSearchListen!=null){
                onSearchListen.onResult(view,resultList.size());
            }
        }

        @Override
        public void onError() {

        }
    };
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);  //<span style="font-family: Helvetica, Tahoma, Arial, sans-serif; font-size: 14px; line-height: 25px; text-align: left; ">在还没有构建View 之前无法取得View的度宽。 </span><span style="font-family: Helvetica, Tahoma, Arial, sans-serif; font-size: 14px; line-height: 25px; text-align: left; ">在此之前我们必须选 measure 一下. </span><br style="font-family: Helvetica, Tahoma, Arial, sans-serif; font-size: 14px; line-height: 25px; text-align: left; ">
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    public ArrayList<SearchMenuEntity> setSearchMenuEntity() {
        ArrayList<SearchMenuEntity> searchMenuEntities=new ArrayList<SearchMenuEntity>();
        SearchMenuEntity searchPublicEntity=new SearchMenuEntity();
        searchPublicEntity.buttonText="公众号";
        searchPublicEntity.bitmap=R.drawable.contact_public_account_png;
        searchPublicEntity.activity=SearchPublicAct.class;
        searchMenuEntities.add(searchPublicEntity);
        return searchMenuEntities;
    }

    private SearchPublicMnu.OnSearchMenuListen onSearchMenuListen=new SearchPublicMnu.OnSearchMenuListen() {
        @Override
        public void onResult(ArrayList<Obj_Menu> resultList) {
            SearchPublicAccountImpl.this.menuresultList=resultList;
            if (resultList!=null&&resultList.size() > 0) {
                ArrayList<Obj_Menu> showPublicList = new ArrayList<>();
                for (int i = 0; i < resultList.size() && i < publicShowNum; i++) {
                    showPublicList.add(resultList.get(i));
                }
                menuAccountAdapter.setKeyWord(keyword);
                menuAccountAdapter.reset(showPublicList);
                menuAccountAdapter.refresh();
                menuNumTextView.setText("(" + resultList.size() + ")");
                menuView.setVisibility(View.VISIBLE);
                setListViewHeightBasedOnChildren(menuListView);
            }
            else {
                menuView.setVisibility(View.GONE);
            }
            if(onSearchListen!=null){
                onSearchListen.onResult(view,resultList.size());
            }
        }

        @Override
        public void onError() {

        }
    };
    private AdapterView.OnItemClickListener onPublicItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Obj_PublicAccount obj_PublicAccount = (Obj_PublicAccount) publicAccountAdapter.getItem(position);
            String loginUserID = Sps_RWLoginUser.readUserID(act);
            if (obj_PublicAccount != null) {
                int type = obj_PublicAccount.type;
                //type=1未桌面应用，不处理
                if (type == 0 || type == 3) {
                    //普通公众号
                    Intent intent = new Intent();
                    intent.setClass(act, PublicAccountChatActNormal.class);
                    intent.putExtra("ACCOUNT_ID", obj_PublicAccount.ID);
                    intent.putExtra("ACCOUNT_NAME", obj_PublicAccount.name);
                    intent.putExtra("IS_PUBLIC_ACCOUNT", true);
                    act.startActivity(intent);
                }  else if (type == 2) {
                    //urlType=2位本地原生不处理
                    //只有一个主入口的页面
                    if (obj_PublicAccount.urlObj.urlType == 0) {
                        //跳转html页面
                        Intent intent = new Intent();
                        String name = Base64Helper.getBASE64(Sps_RWLoginUser.readUser(act).name);
//                        String ID =Sps_RWLoginUser.readUser(this).ID;
                        String phoneNumber = Sps_RWLoginUser.readUserTelNumber(act);
                        String url;
                        if (phoneNumber != null) {
                            url = obj_PublicAccount.urlObj.publicUrl + "?userName=" + name + "&phoneNumber=" + phoneNumber + "&userID=" + loginUserID;
                        } else {
                            url = obj_PublicAccount.urlObj.publicUrl + "?userName=" + name + "&currentTime=" + System.currentTimeMillis() + "&userID=" + loginUserID;
                        }
                        PAWebView
                                .builder()
                                .setUrl(url)
                                .setName(obj_PublicAccount.name)
                                .setIsShowTitle(obj_PublicAccount.urlObj.isShowTitle)
                                .setUrlParam(obj_PublicAccount.urlObj.urlParam)
                                .start(act);

                    } else if (obj_PublicAccount.urlObj.urlType == 1) {
                        //跳转weex页面
                        String url = obj_PublicAccount.urlObj.publicUrl;
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        StringBuilder builder = new StringBuilder();
                        builder.append(url);
                        Uri uri = Uri.parse(builder.toString());
                        if (TextUtils.equals("http", uri.getScheme()) || TextUtils.equals("https", uri.getScheme())) {
                            intent.setData(uri);
                            intent.putExtra("Title", obj_PublicAccount.name);
                            intent.putExtra("IsShowTitle", obj_PublicAccount.urlObj.isShowTitle);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.addCategory("com.SynwayOsc.android.intent.category.WEEX");
                            Map<String, Object> params = new HashMap<>();
                            params.put("SourceUrl", obj_PublicAccount.urlObj.urlParam);
                            params.put("UserId", loginUserID);
                            params.put("PaId", obj_PublicAccount.ID);
                            WxMapData map = new WxMapData();
                            map.setWxMapData(params);
                            intent.putExtra("DATA", map);
                            act.startActivity(intent);
                        } else {
                            Toast.makeText( act, "跳转地址格式错误,请检查格式", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == btnPublicMore ) {
                Intent intent = new Intent(act, SearchPublicAct.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("publicList", publicList);
                mBundle.putSerializable("KeyWord", keyword);
                mBundle.putSerializable("TypeIndex", 1);
                intent.putExtras(mBundle);
                act.startActivity(intent);
            }

        }
    };
}
