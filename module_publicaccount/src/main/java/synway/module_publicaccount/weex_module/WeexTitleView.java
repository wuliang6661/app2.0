package synway.module_publicaccount.weex_module;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import synway.module_publicaccount.R;
import synway.module_publicaccount.weex_module.extend.interfaces.PageOperaInterface;
import synway.module_publicaccount.weex_module.interfaces.RefreshInterface;

/**
 * Created by dell on 2018/9/21.
 * 说明：
 */

public class WeexTitleView {
    private View view = null, closeview = null;
    private TextView tvTitle = null;
    private View btnback = null, close = null;
    private Activity activity = null;
    private View freshview = null;
    private RefreshInterface refreshInterface;
    private PageOperaInterface weexTitleInterface;

    public void setRefreshListener(RefreshInterface listener) {
        this.refreshInterface = listener;
    }


    public WeexTitleView(Activity act,PageOperaInterface weexTitleInterface) {
        this.weexTitleInterface = weexTitleInterface;
        this.activity = act;
        view = act.findViewById(R.id.titlebar_block);
        tvTitle = view.findViewById(R.id.lblTitle);
        btnback = view.findViewById(R.id.back);
        freshview = view.findViewById(R.id.fresh);
        closeview = view.findViewById(R.id.closeview);
        closeview.setVisibility(View.VISIBLE);
        freshview.setVisibility(View.GONE);
        freshview.setOnClickListener(onClickListener);
        btnback.setOnClickListener(onClickListener);
        closeview.setOnClickListener(onClickListener);
    }
    public void setVisible(int isable){
        if(view!=null){
            view.setVisibility(isable);
        }
    }

    //设置标题文字和文字颜色
    public void setTitle(String title) {
        this.tvTitle.setText(title);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == btnback) {
                if(weexTitleInterface==null){
                    Intent intent = new Intent();
                    String backtype = "backkey";
                    intent.putExtra("backtype", backtype);
                    activity.setResult(3, intent);
                    activity.finish();
                }else{
                    weexTitleInterface.closeActivity();
                }

            } else if (v == freshview) {
                if (refreshInterface != null) {
                    refreshInterface.refreshWeexView();
                }
            } else if (v == closeview) {
                if(weexTitleInterface==null) {
                    Intent intent = new Intent();
                    String backtype = "backkey";
                    intent.putExtra("backtype", backtype);
                    activity.setResult(3, intent);
                    activity.finish();
                }else{
                    weexTitleInterface.closeActivity();
                }
            }
        }
    };
}
