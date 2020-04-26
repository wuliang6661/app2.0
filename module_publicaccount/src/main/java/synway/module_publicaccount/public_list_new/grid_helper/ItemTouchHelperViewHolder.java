package synway.module_publicaccount.public_list_new.grid_helper;

/**
 * Created by leo on 2018/6/15.
 */

public interface ItemTouchHelperViewHolder {
    /**
     * 当item被选中，即item正在被滑动 或 拖拽 时的处理方法
     */
    void onItemSelected();

    /**
     * 当item的滑动、移动动画结束后的处理方法
     */
    void onItemClear();
}
