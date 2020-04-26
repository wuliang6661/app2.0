package synway.module_publicaccount.public_favorite.Search;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import synway.common.SoftKeyBoardUtil;
import synway.module_interface.config.userConfig.Sps_RWLoginUser;
import synway.module_publicaccount.R;
import synway.module_publicaccount.public_chat.Obj_Menu;
import synway.module_publicaccount.public_chat.PAWebViewAct;
import synway.module_publicaccount.public_chat.PublicAccountChatActNormal;
import synway.module_publicaccount.public_chat.bottom.Base64Helper;
import synway.module_publicaccount.public_favorite.Search.adapter.MenuAccountAdapter;
import synway.module_publicaccount.publiclist.Obj_PublicAccount;
import synway.module_publicaccount.weex_module.beans.WxMapData;

/**
 * Created by ysm on 2016/10/8.
 */
public class SearchMenuAct extends Activity {

    // title View
    private View titleView = null;
    private ImageButton imgvBack = null;
    private View clearButton = null;
    private EditText inpText = null;
    private ScrollView scrollView;
    private LinearLayout linearLayout;
    private RelativeLayout relShowNone;
    private TextView textViewSearch;
    //public
    private TextView menuNumTextView;
    private View menuView;
    private ListView menuListView;
    private ArrayList<Obj_PublicAccount> publicList;
    //menu
    private ArrayList<Obj_Menu> menupublicList;
    private MenuAccountAdapter menuAccountAdapter;
    private String keyWord = "";
    private  int TypeIndex;
    //搜索菜单
    private SearchPublicMnu searchPublicMnu;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.model_search_onetype_act);
        init();
        keyWord = (String) getIntent().getSerializableExtra("KeyWord");
        TypeIndex=(int)getIntent().getSerializableExtra("TypeIndex");
        publicList = (ArrayList<Obj_PublicAccount>) getIntent().getSerializableExtra("publicList");
        menupublicList=(ArrayList<Obj_Menu>)getIntent().getSerializableExtra("menuresultList");
        if (!keyWord.equals("")) {
            textViewSearch.setVisibility(View.GONE);
            //公众帐号
                if (menupublicList.size() > 0) {
                    menuAccountAdapter.setKeyWord(keyWord);
                    menuAccountAdapter.reset(menupublicList);
                    menuAccountAdapter.refresh();
                    menuNumTextView.setText("(" + menupublicList.size() + ")");
                    menuView.setVisibility(View.VISIBLE);
            }
            linearLayout.requestFocus();
            inpText.setText(keyWord);
            inpText.addTextChangedListener(textWatcher);
            inpText.clearFocus();
        } else {
            textViewSearch.setText("搜索公众号菜单");
            textViewSearch.setVisibility(View.VISIBLE);
            inpText.addTextChangedListener(textWatcher);
            inpText.requestFocus();
            // 强制显示键盘
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    InputMethodManager imm = (InputMethodManager) inpText
                            .getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(inpText, InputMethodManager.SHOW_FORCED);
                }
            }, 200);
        }
    }
public void init(){
    textViewSearch = findViewById(R.id.textview_search);
    textViewSearch.setVisibility(View.GONE);
    titleView = this.findViewById(R.id.titlebar_block);
    linearLayout = findViewById(R.id.linearlayout_show);
    linearLayout.setOnTouchListener(onTouchListener);
    relShowNone = findViewById(R.id.relative_none);
    imgvBack = titleView.findViewById(R.id.back);
    imgvBack.setOnClickListener(onClickListener);
    clearButton = titleView.findViewById(R.id.clear_button);
    clearButton.setOnClickListener(onClickListener);
    inpText = findViewById(R.id.auto1);
    inpText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus) {
                // 此处为得到焦点时的处理内容
            } else {
                //关闭键盘
                if (KeyBoard(inpText)) {
                    InputMethodManager imm = (InputMethodManager) inpText
                            .getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(inpText.getWindowToken(), 0);
                }
            }
        }
    });
    //public
    menuNumTextView = findViewById(R.id.menu_num_textview);
    menuView = findViewById(R.id.menu_result_linearLayout);
    menuAccountAdapter=new MenuAccountAdapter(this);
    menuListView = menuView.findViewById(R.id.menu_listview);
    menuListView.setOnItemClickListener(onMenuItemClickListener);
    menuListView.setOnTouchListener(onTouchListener);
    menuListView.setAdapter(menuAccountAdapter);
}
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == imgvBack) {
                finish();
            } else if (v == clearButton) {
                if (searchPublicMnu != null)
                    searchPublicMnu.stop();
                linearLayout.setVisibility(View.GONE);
                relShowNone.setVisibility(View.GONE);
                inpText.setText("");
                inpText.requestFocus();
                textViewSearch.setVisibility(View.VISIBLE);
                // 强制显示键盘
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        InputMethodManager imm = (InputMethodManager) inpText
                                .getContext().getSystemService(
                                        Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(inpText, InputMethodManager.SHOW_FORCED);
                    }
                }, 200);
            }
        }
    };
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //关闭键盘
            InputMethodManager imm = (InputMethodManager) inpText
                    .getContext().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(inpText.getWindowToken(), 0);
            return false;
        }
    };




    //
    private AdapterView.OnItemClickListener onMenuItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Obj_Menu obj_Menu = (Obj_Menu) menuAccountAdapter.getItem(position);
//            if (obj_Menu.menuType == 1) {
//                if (obj_Menu.menuUrl == null) {
//                    return;
//                }
//                //改成先打开公众号页面再打开网页
//                Intent intent=new Intent(SearchMenuAct.this,PublicAccountChatActNormal.class);
//                Bundle mBundle = new Bundle();
//                mBundle.putSerializable("obj_Menu",obj_Menu);
//                intent.putExtra("ACCOUNT_ID",obj_Menu.ID);
//                intent.putExtra("ACCOUNT_NAME",obj_Menu.Name);
//                intent.putExtra("MenuKry_Click","weburl");
//                intent.putExtras(mBundle);
//                startActivity(intent);
////                Intent intent = new Intent();
////                String name = Base64Helper.getBASE64(Sps_RWLoginUser.readUser(SearchMenuAct.this).name);
////                String url=obj_Menu.menuUrl + "?userName=" + name
////                        + "&currentTime=" + System.currentTimeMillis();
////                intent.putExtra("URL", url);
////                intent.putExtra("NAME", obj_Menu.menuName);
////                intent.setClass(SearchMenuAct.this, PAWebViewAct.class);
////                startActivity(intent);
//            } else if (obj_Menu.menuType == 2) {
////                Log.i("testy","点击事件");
//                Intent intent=new Intent(SearchMenuAct.this,PublicAccountChatActNormal.class);
//                Bundle mBundle = new Bundle();
//                mBundle.putSerializable("obj_Menu",obj_Menu);
//                intent.putExtra("ACCOUNT_ID",obj_Menu.ID);
//                intent.putExtra("ACCOUNT_NAME",obj_Menu.Name);
//                intent.putExtra("MenuKry_Click","click");
//                intent.putExtras(mBundle);
//                startActivity(intent);
//            }
         String   loginUserID = Sps_RWLoginUser.readUserID(SearchMenuAct.this);
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
                        intent.putExtra("IsShowTitle",obj_Menu.isShowTitle);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.addCategory("com.SynwayOsc.android.intent.category.WEEX");
                        Map<String, Object> params = new HashMap<>();
                        params.put("SourceUrl", obj_Menu.sourceUrl);
                        params.put("UserId", loginUserID);
                        params.put("PaId", obj_Menu.ID);
                        WxMapData map = new WxMapData();
                        map.setWxMapData(params);
                        intent.putExtra("DATA", map);
                        startActivity(intent);
//                    FileTestLog.write("Weex页面跳转","SourceUrl:"+obj_Menu.sourceUrl+"UserId:"+loginUserID+"PaId"+publicGUID);
                    }else {
                        Toast.makeText(SearchMenuAct.this, "跳转地址格式错误,请检查格式", Toast.LENGTH_LONG).show();
                    }

                } else {
                    // 兼容旧的页面跳转
                    Intent intent = new Intent();
                    String name = Base64Helper.getBASE64(Sps_RWLoginUser.readUser(SearchMenuAct.this).name);
                    String ID = Sps_RWLoginUser.readUser(SearchMenuAct.this).ID;
                    String url = obj_Menu.menuUrl + "?userName=" + name + "&currentTime=" + System.currentTimeMillis() + "&userID=" + ID;
                    intent.putExtra("URL", url);
                    intent.putExtra("NAME", obj_Menu.menuName);
                    intent.putExtra("IsShowTitle",obj_Menu.isShowTitle);
                    intent.putExtra("URL_PARAM",obj_Menu.sourceUrl);
                    intent.setClass(SearchMenuAct.this, PAWebViewAct.class);
                    startActivity(intent);
                }

            }
        }

    };

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            keyWord = s.toString().trim();
            if (!keyWord.equals("")) {
                textViewSearch.setVisibility(View.GONE);
                        if (searchPublicMnu != null) {
                            searchPublicMnu.stop();
                        }
                searchPublicMnu=new SearchPublicMnu();
                searchPublicMnu.setOnSearchPublicAccount(onSearchMenuListen);
                searchPublicMnu.start(keyWord);
            } else {
                searchPublicMnu.stop();
                linearLayout.setVisibility(View.GONE);
                relShowNone.setVisibility(View.GONE);
                textViewSearch.setVisibility(View.VISIBLE);
            }

        }
    };

    // 输入法是否显示着
    public static boolean KeyBoard(EditText edittext) {
        boolean bool = false;
        InputMethodManager imm = (InputMethodManager) edittext.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            bool = true;
        }
        return bool;
    }

    @Override
    protected void onDestroy() {
        //关闭键盘
        if (KeyBoard(inpText)) {
            InputMethodManager imm = (InputMethodManager) inpText
                    .getContext().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(inpText.getWindowToken(), 0);
        }
        super.onDestroy();
    }
    private SearchPublicMnu.OnSearchMenuListen onSearchMenuListen=new SearchPublicMnu.OnSearchMenuListen() {
        @Override
        public void onResult(ArrayList<Obj_Menu> resultList) {
            if (resultList!=null&&resultList.size() > 0) {
                menuAccountAdapter.setKeyWord(keyWord);
                menuAccountAdapter.reset(resultList);
                menuAccountAdapter.refresh();
                menuNumTextView.setText("(" + resultList.size() + ")");
                linearLayout.setVisibility(View.VISIBLE);
                relShowNone.setVisibility(View.GONE);
                menuView.setVisibility(View.VISIBLE);
                setListViewHeightBasedOnChildren(menuListView);

            }
            else {
                linearLayout.setVisibility(View.GONE);
                relShowNone.setVisibility(View.VISIBLE);
                menuView.setVisibility(View.GONE);
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


    @Override protected void onPause() {
        super.onPause();
        if (inpText != null && KeyBoard(inpText)) {
            SoftKeyBoardUtil.hideKeyboard(SearchMenuAct.this);
        }
    }
}
