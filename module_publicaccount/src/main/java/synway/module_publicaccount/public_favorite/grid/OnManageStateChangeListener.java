package synway.module_publicaccount.public_favorite.grid;

/**
 * Created by Mfh on 2016/10/9.
 * 作用域：在需要对进入、退出编辑状态进行处理的类中 实现该接口
 *        设置到adapter中
 */

public interface OnManageStateChangeListener {
    /**
     * 改变状态（如：长按进入编辑状态）时，调用该方法的实现
     * @param b true:切换为编辑状态
     */
    void onChange(boolean b);
}
