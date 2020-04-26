package synway.module_interface.searchinterface;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import java.util.ArrayList;

/**
 *
 */

public abstract class SearchInterface {

    /**
     * 搜索菜单接口,列在搜索主界面，点击之后跳转到子模块自定义搜索。
     */
    public abstract ArrayList<SearchMenuEntity> setSearchMenuEntity();

    //初始化
    public abstract void init( Activity activity);
    //开始搜索
    public abstract void start(String keyWord);
    //停止搜索
    public abstract void stop();

    public abstract void onDestroy();

    public abstract void setOnListen(OnSearchListen onSearchListen);

    public interface OnSearchListen{
        void onResult(View resultView, int resultSize);
    }

}
