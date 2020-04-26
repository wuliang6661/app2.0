package synway.module_publicaccount.public_favorite.Search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import synway.module_publicaccount.public_chat.PAWebViewAct;
import synway.module_publicaccount.public_chat.bottom.Base64Helper;
import synway.module_publicaccount.public_favorite.Search.adapter.MenuAccountAdapter;
import synway.module_publicaccount.public_favorite.Search.adapter.PublicAccountAdapter;
import synway.module_publicaccount.publiclist.Obj_PublicAccount;
import synway.module_publicaccount.weex_module.beans.WxMapData;

/**
 * Created by ysm on 2017/2/13.
 */

public class SearchMenuImpl extends SearchInterface {
    private View view = null;
    private View menuView = null;
    private ListView menuListView;
    private PublicAccountAdapter publicAccountAdapter;
    private MenuAccountAdapter menuAccountAdapter;
    private View btnMenuMore;
    //搜索关键词
    private String keyword;
    //搜索公众号
    private SearchPublicAccount searchPublicAccount = null;
    //搜索菜单
    private SearchPublicMnu searchPublicMnu;
    private ArrayList<Obj_PublicAccount> publicList;
    private int publicShowNum = 3;
    private TextView menuNumTextView = null;
    private OnSearchListen onSearchListen = null;
    private ArrayList<Obj_Menu> menuresultList;
    private Activity act;

    @Override
    public void init(Activity activity) {
        this.act = activity;
        LayoutInflater layoutInflater = LayoutInflater.from(act);
        view = layoutInflater.inflate(R.layout.model_public_account_search, null, false);
        //菜单页面
        menuView = view.findViewById(R.id.menu_result_linearLayout);
        menuListView = view.findViewById(R.id.menu_listview);
        menuNumTextView = view.findViewById(R.id.menu_num_textview);
        menuAccountAdapter = new MenuAccountAdapter(act);
        menuListView.setOnItemClickListener(onMenuItemClickListener);
        menuListView.setAdapter(menuAccountAdapter);
        searchPublicMnu = new SearchPublicMnu();
        searchPublicMnu.setOnSearchPublicAccount(onSearchMenuListen);
        //更多公衆號菜單
        btnMenuMore = view.findViewById(R.id.menu_more_linearLayout);
        btnMenuMore.setOnClickListener(onClickListener);
    }

    @Override
    public void start(String keyWord) {

        this.keyword = keyWord;
        searchPublicMnu.start(keyWord);
    }

    @Override
    public void stop() {
        searchPublicMnu.stop();
    }

    @Override
    public void onDestroy() {
        stop();
    }

    @Override
    public void setOnListen(OnSearchListen onSearchListen) {
        this.onSearchListen = onSearchListen;

    }

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
        ArrayList<SearchMenuEntity> searchMenuEntities = new ArrayList<SearchMenuEntity>();
        SearchMenuEntity searchPublicMenuEntity = new SearchMenuEntity();
        searchPublicMenuEntity.buttonText = "公众号菜单";
        searchPublicMenuEntity.bitmap = R.drawable.searchmenu;
        searchPublicMenuEntity.activity = SearchMenuAct.class;
        searchMenuEntities.add(searchPublicMenuEntity);
        return searchMenuEntities;
    }


    private SearchPublicMnu.OnSearchMenuListen onSearchMenuListen = new SearchPublicMnu.OnSearchMenuListen() {
        @Override
        public void onResult(ArrayList<Obj_Menu> resultList) {
            SearchMenuImpl.this.menuresultList = resultList;
            if (resultList != null && resultList.size() > 0) {
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

            } else {
                view.setVisibility(View.GONE);
            }
            if (onSearchListen != null) {
                onSearchListen.onResult(view, resultList.size());
            }
        }

        @Override
        public void onError() {

        }
    };

    private AdapterView.OnItemClickListener onMenuItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Obj_Menu obj_Menu = (Obj_Menu) menuAccountAdapter.getItem(position);
            String loginUserID = Sps_RWLoginUser.readUserID(act);
            if (obj_Menu.menuType == 1) {
                if (obj_Menu.menuUrl == null) {
                    return;
                }
                //直接跳转Weex页面或H5页面
                if (obj_Menu.menuUrlType == 1) {
                    // 新：weex 界面跳转
                    String url = obj_Menu.menuUrl;
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    StringBuilder builder = new StringBuilder();
                    builder.append(url);
                    Uri uri = Uri.parse(builder.toString());
                    if (TextUtils.equals("http", uri.getScheme()) || TextUtils.equals("https", uri.getScheme())) {
                        intent.setData(uri);
                        intent.putExtra("Title", obj_Menu.menuName);
                        intent.putExtra("IsShowTitle", obj_Menu.isShowTitle);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.addCategory("com.SynwayOsc.android.intent.category.WEEX");
                        Map<String, Object> params = new HashMap<>();
                        params.put("SourceUrl", obj_Menu.sourceUrl);
                        params.put("UserId", loginUserID);
                        params.put("PaId", obj_Menu.ID);
                        WxMapData map = new WxMapData();
                        map.setWxMapData(params);
                        intent.putExtra("DATA", map);
                        act.startActivity(intent);
//                    FileTestLog.write("Weex页面跳转","SourceUrl:"+obj_Menu.sourceUrl+"UserId:"+loginUserID+"PaId"+publicGUID);
                    } else {
                        Toast.makeText(act, "跳转地址格式错误,请检查格式", Toast.LENGTH_LONG).show();
                    }

                } else {
                    // 兼容旧的页面跳转
                    Intent intent = new Intent();
                    String name = Base64Helper.getBASE64(Sps_RWLoginUser.readUser(act).name);
                    String ID = Sps_RWLoginUser.readUser(act).ID;
                    String url = obj_Menu.menuUrl + "?userName=" + name + "&currentTime=" + System.currentTimeMillis() + "&userID=" + ID;
                    intent.putExtra("URL", url);
                    intent.putExtra("NAME", obj_Menu.menuName);
                    intent.putExtra("IsShowTitle", obj_Menu.isShowTitle);
                    intent.putExtra("URL_PARAM", obj_Menu.sourceUrl);
                    intent.setClass(act, PAWebViewAct.class);
                    act.startActivity(intent);
                }

            }
        }

    };
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == btnMenuMore) {
                Intent intent = new Intent(act, SearchMenuAct.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("menuresultList", menuresultList);
                mBundle.putSerializable("KeyWord", keyword);
                mBundle.putSerializable("TypeIndex", 2);
                intent.putExtras(mBundle);
                act.startActivity(intent);
            }

        }
    };
}
