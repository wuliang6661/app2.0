package synway.common.base;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;

import me.yokeyword.fragmentation.SupportFragment;
import qyc.library.control.dialog_progress.DialogProgress;
import synway.common.R;

/**
 * Created by wuliang on 2018/12/6.
 * <p>
 * 所有Fragment的基类
 */

public abstract class BaseFragment extends SupportFragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * 常用的跳转方法
     */
    public void gotoActivity(Class<?> cls, boolean isFinish) {
        Intent intent = new Intent(getActivity(), cls);
        startActivity(intent);
        if (isFinish) {
            getActivity().finish();
        }
    }

    public void gotoActivity(Class<?> cls, Bundle bundle, boolean isFinish) {
        Intent intent = new Intent(getActivity(), cls);
        intent.putExtras(bundle);
        startActivity(intent);
        if (isFinish) {
            getActivity().finish();
        }
    }
    public void gotoActivityForResult(Class<?> cls, int resultCode, boolean isFinish) {
        Intent intent = new Intent(getActivity(), cls);
        startActivityForResult(intent,resultCode);
        if (isFinish) {
            getActivity().finish();
        }
    }


    /**
     * 滚动的菊花
     */
    private Dialog progressDialog = null;

    protected void showProgress(String message) {
        progressDialog = DialogProgress.get(getActivity(), "加载中....",
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
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }


    /**
     * 设置fragment沉浸式
     */
    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        //请在onSupportVisible实现沉浸式
        if (isImmersionBarEnabled()) {
            initImmersionBar();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
    }

    public void initImmersionBar() {
        ImmersionBar.with(this).statusBarColor(R.color.zhu_color)
                .statusBarDarkFont(true).keyboardEnable(true).init();   //解决虚拟按键与状态栏沉浸冲突
    }

    private boolean isImmersionBarEnabled() {
        return true;
    }
}
