package synway.module_publicaccount.public_favorite.grid;

/**
 * Created by Mfh on 2016/9/30.
 * 作用域：在RecyclerView.ViewHolder的子类中实现该接口，
 *        用于响应ItemTouchHelper.Callback的子类中的状态回调方法：onSelectedChanged、clearView
 */

interface ItemTouchHelperViewHolder {

    /**
     * 当item被选中，即item正在被滑动 或 拖拽 时的处理方法
     */
    void onItemSelected();

    /**
     * 当item的滑动、移动动画结束后的处理方法
     */
    void onItemClear();
}
