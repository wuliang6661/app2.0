package synway.module_interface.module;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.view.View;

import me.yokeyword.fragmentation.SupportFragment;
import synway.module_interface.invoke.HandlerTipCount;

/**
 * Created by zjw on 2016/11/24.
 * 修改:qyc
 * 将HomeFragment直接继承Fragment,使titleView和fragment合并为同一个,避免外部调用时titleView和fragment分开管理导致fragment和titleView生命周期不同.
 * 生命周期不同会导致fragment内部代码混乱,例如fragment已经是新的了,而titleView仍然是旧的.
 */

public abstract class HomeFragment extends SupportFragment {

    /**
     * 自定义titleView.<p>
     * 返回null表示使用主程序默认的titleView,否则加载fragment自定义的titleView<p>
     * onCreateTitleView一定在fragment的onCreatView,onDestoryView之间触发.即onCreateTitleView触发时,fragment的onCreatView一定已经触发.而fragment的onDestoryView触发后,titleView一定已经不再被使用.<br>
     * onCreatTitleView可能在onCreatView和onDestoryView之间多次触发.<p>
     * 建议在fragment的onCreatView里面一起初始化titleView,在fragment的onDestoryView里面一起销毁titleView.当onCreateTitleView触发时,返回已经初始化好的titleView.
     */
    public View onCreateTitleView(Activity activity) {
        return null;
    }

    /**
     * HomeAct onHomePause 子模块用于判断是否处于HomeAct
     */
    public void onHomePause(Context context) {
    }

    /**
     * HomeAct onHomeResume 子模块用于判断是否处于HomeAct
     */
    public void onHomeResume(Context context) {
    }

    /**
     * 返回true表示你已经拦截了这个按键事件,返回false表示你不捕获这个按键事件
     */
    public boolean onKeyDown(int keyCode) {
        return false;
    }

    /**
     * 在主程序下方按钮的图标(未选中)
     */
    public int imageView_normal;
    /**
     * 在主程序下方按钮的图标(选中)
     */
    public int imageView_selected;
    /**
     * 在主程序下方按钮的文字
     */
    public String text;
    /**
     * 在主程序下方按钮的文字颜色(未选中)
     */
    public int textColor_normal = Color.parseColor("#727d87");
    /**
     * 在主程序下方按钮的文字(选中)
     */
    public int textColor_selected = Color.parseColor("#38adff");
    /**
     * 操作底栏图标右上角的消息未读数
     */
    public HandlerTipCount handlerTipCount;


}
