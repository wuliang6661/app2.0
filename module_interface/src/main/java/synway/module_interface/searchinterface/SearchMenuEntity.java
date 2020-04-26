package synway.module_interface.searchinterface;

/**
 * 搜索菜单实体,用于模块动态加入到搜索菜单中.
 * Created by zjw on 2017/2/14.
 */

public class SearchMenuEntity {
    /**
     * 标题文本
     */
    public String buttonText;
    /**
     * 搜索的图标
     */
    public int bitmap;
    /**
     * 打开的activity
     */
    public Class<?> activity;
}
