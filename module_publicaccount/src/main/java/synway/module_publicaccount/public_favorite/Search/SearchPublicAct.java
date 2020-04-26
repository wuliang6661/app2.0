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
import synway.module_publicaccount.public_chat.PAWebView;
import synway.module_publicaccount.public_chat.PublicAccountChatActNormal;
import synway.module_publicaccount.public_chat.bottom.Base64Helper;
import synway.module_publicaccount.public_favorite.Search.adapter.PublicAccountAdapter;
import synway.module_publicaccount.publiclist.Obj_PublicAccount;
import synway.module_publicaccount.weex_module.beans.WxMapData;

/**
 * Created by ysm on 2016/10/8.
 */
public class SearchPublicAct extends Activity {

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
    private TextView publicNumTextView;
    private View publicView;
    private View btnPublicMore;
    private ListView publicListView;
    private PublicAccountAdapter publicAccountAdapter;
    private SearchPublicAccountImpl searchPublicAccount;
    private ArrayList<Obj_PublicAccount> publicList;
    private String keyWord = "";
    private  int TypeIndex;
    //搜索公众号
    private SearchPublicAccount searchPublic=null;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.model_search_onetype_act);
        init();
        keyWord = (String) getIntent().getSerializableExtra("KeyWord");
        TypeIndex = (int) getIntent().getSerializableExtra("TypeIndex");
        publicList = (ArrayList<Obj_PublicAccount>) getIntent().getSerializableExtra("publicList");
        searchPublicAccount = new SearchPublicAccountImpl();
        if (!keyWord.equals("")) {
            textViewSearch.setVisibility(View.GONE);
            //公众帐号
            if (TypeIndex == 1) {
                if (publicList.size() > 0) {
                    publicAccountAdapter.setKeyWord(keyWord);
                    publicAccountAdapter.reset(publicList);
                    publicAccountAdapter.refresh();
                    publicNumTextView.setText("(" + publicList.size() + ")");
                    publicView.setVisibility(View.VISIBLE);
                }
                linearLayout.requestFocus();
                inpText.setText(keyWord);
                inpText.addTextChangedListener(textWatcher);
                inpText.clearFocus();
            } else {
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
        }else {
            textViewSearch.setText("搜索公众号");
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
    publicNumTextView = findViewById(R.id.public_num_textview);
    publicView = findViewById(R.id.public_result_linearLayout);
    publicAccountAdapter = new PublicAccountAdapter(this);
    publicListView = publicView.findViewById(R.id.public_listview);
    publicListView.setOnItemClickListener(onPublicItemClickListener);
    publicListView.setOnTouchListener(onTouchListener);
    publicListView.setAdapter(publicAccountAdapter);
}
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == imgvBack) {
                finish();
            } else if (v == clearButton) {
                if (searchPublic != null)
                    searchPublic.stop();
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



    private AdapterView.OnItemClickListener onPublicItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Obj_PublicAccount obj_PublicAccount = (Obj_PublicAccount) publicAccountAdapter.getItem(position);
            String loginUserID = Sps_RWLoginUser.readUserID(SearchPublicAct.this);
            if (obj_PublicAccount != null) {
                int type = obj_PublicAccount.type;
                //type=1未桌面应用，不处理
                if (type == 0 || type == 3) {
                    //普通公众号
                    Intent intent = new Intent();
                    intent.setClass(SearchPublicAct.this, PublicAccountChatActNormal.class);
                    intent.putExtra("ACCOUNT_ID", obj_PublicAccount.ID);
                    intent.putExtra("ACCOUNT_NAME", obj_PublicAccount.name);
                    intent.putExtra("IS_PUBLIC_ACCOUNT", true);
                    startActivity(intent);
                }  else if (type == 2) {
                    //urlType=2位本地原生不处理
                    //只有一个主入口的页面
                    if (obj_PublicAccount.urlObj.urlType == 0) {
                        //跳转html页面
                        Intent intent = new Intent();
                        String name = Base64Helper.getBASE64(Sps_RWLoginUser.readUser(SearchPublicAct.this).name);
//                        String ID =Sps_RWLoginUser.readUser(this).ID;
                        String phoneNumber = Sps_RWLoginUser.readUserTelNumber(SearchPublicAct.this);
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
                                .start(SearchPublicAct.this);

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
                           startActivity(intent);
                        } else {
                            Toast.makeText(SearchPublicAct.this, "跳转地址格式错误,请检查格式", Toast.LENGTH_LONG).show();
                        }
                    }
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
                        if (searchPublic != null) {
                            searchPublic.stop();
                        }
                searchPublic=new SearchPublicAccount();
                searchPublic.setOnSearchPublicAccount(onSearchPublicAccountListen);
                searchPublic.start(keyWord);
                //????正式搜索
//                        searchPublicAccount = new SearchPublicAccount();
//                        searchPublicAccount.setOnSearchPublicAccount(onSearchPublicAccountListen);
//                        searchPublicAccount.start(keyWord);
            } else {
                searchPublic.stop();
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
    private SearchPublicAccount.OnSearchPublicAccountListen onSearchPublicAccountListen = new SearchPublicAccount.OnSearchPublicAccountListen() {
        @Override
        public void onResult(ArrayList<Obj_PublicAccount> resultList) {
            if (resultList!=null&&resultList.size() > 0) {
                publicAccountAdapter.setKeyWord(keyWord);
                publicAccountAdapter.reset(resultList);
                publicAccountAdapter.refresh();
                publicNumTextView.setText("(" + resultList.size() + ")");
                linearLayout.setVisibility(View.VISIBLE);
                relShowNone.setVisibility(View.GONE);
                publicView.setVisibility(View.VISIBLE);
                setListViewHeightBasedOnChildren(publicListView);
            }
            else {
                linearLayout.setVisibility(View.GONE);
                relShowNone.setVisibility(View.VISIBLE);
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
        if (inpText != null || KeyBoard(inpText)) {
            SoftKeyBoardUtil.hideKeyboard(SearchPublicAct.this);
        }
    }
}
