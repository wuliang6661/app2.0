package cn.synway.app.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import butterknife.ButterKnife;
import cn.synway.app.R;
import cn.synway.app.utils.AppManager;
import synway.common.base.BaseActivity;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/6/1813:21
 * desc   :
 * version: 1.0
 */
public abstract class SynBaseActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        AppManager.getAppManager().addActivity(this);
        ButterKnife.bind(this);

        // 避免从桌面启动程序后，会重新实例化入口类的activity
        if (!this.isTaskRoot()) {
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                    finish();
                    return;
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().removeActivity(this);
    }


    /**
     * 关闭软键盘
     */
    private void hideSoft() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoft();
    }


    /**
     * 返回上一页
     */
    public void goBack() {
        initToorBar();
        View back = findViewById(R.id.back);
        back.setOnClickListener(view -> finish());
    }

    /**
     * 设置标题文字
     */
    public void setTitleText(String text) {
        TextView title = findViewById(R.id.title_text);
        title.setText(text);
    }


    /**
     * 为标题栏初始化高度
     */
    protected void initToorBar() {
        View titleLayout = findViewById(R.id.title_bar);
        immersionBar.titleBar(titleLayout).init();
    }

}
