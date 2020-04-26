package synway.common.base;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.widget.Toast;

import com.blankj.utilcode.util.Utils;
import com.gyf.barlibrary.ImmersionBar;

import me.yokeyword.fragmentation.SupportActivity;
import qyc.library.control.dialog_progress.DialogProgress;


/**
 * Created by wuliang on 2018/12/6.
 * <p>
 * 所有activity的基类
 */

public abstract class BaseActivity extends SupportActivity {

    /**
     * 滚动的菊花
     */
    private Dialog progressDialog = null;
    protected ImmersionBar immersionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getSupportActionBar().hide();
        setContentView(getLayout());
        immersionBar = ImmersionBar.with(this);
        immersionBar.statusBarDarkFont(true).keyboardEnable(true).init();   //解决虚拟按键与状态栏沉浸冲突
        Utils.init(getApplicationContext());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
    }


    /**
     * 常用的跳转方法
     */
    public void gotoActivity(Class<?> cls, boolean isFinish) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
        if (isFinish) {
            finish();
        }
    }

    public void gotoActivity(Class<?> cls, Bundle bundle, boolean isFinish) {
        Intent intent = new Intent(this, cls);
        intent.putExtras(bundle);
        startActivity(intent);
        if (isFinish) {
            finish();
        }
    }


    protected void showProgress(String message) {
        progressDialog = DialogProgress.get(this, "加载中....",
                "message");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    protected void stopProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    /**
     * 显示toast弹窗
     */
    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    protected abstract int getLayout();

}
