package synway.module_interface.chatinterface;

/**
 * 聊天菜单实体,用于模块动态加入到聊天菜单中.
 * Created by 钱园超 on 2016/12/23.
 */

public class ChatMenuEntity {

    /**
     * 按钮文本
     */
    public String buttonText;
    /**
     * 加入到哪个大菜单下
     */
    public BigMenu bitMenu;
    /**
     * 打开的activity
     */
    public Class<?> activity;

    /**
     * 聊天界面菜单类型
     */
    public enum BigMenu {
        /**
         * 照片类
         */
        photo,
        /**
         * 视频类(非实时)
         */
        video,
        /**
         * 实时位置
         */
        rtlocation,
        /**
         * 静态位置(位置标注)
         */
        staticlocation,
        /**
         * 实时取证
         */
        rtvideo,
        /**
         * 实时对讲
         */
        rtvoice,
        /**
         * 叮模块
         */
        ding

    }

}
