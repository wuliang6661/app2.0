package synway.module_publicaccount.public_favorite.grid;

/**
 * Created by Mfh on 2016/9/30.
 *
 * 作用域：RecyclerView.Adapter的 子类 实现该接口，
 *        用于响应ItemTouchHelper.Callback的子类中的状态回调方法：onMove、onSwiped
 */

interface ItemTouchHelperAdapter {

    /**
     * 当item从 位置fromPosition 被移动到 位置toPosition时的处理方法
     *
     * 在ItemTouchHelper.Callback中的onMove被调用
     *
     * @param fromPosition 起始位置
     * @param toPosition 目标位置
     * @return true：可成功移动
     */
    boolean onItemMove(int fromPosition, int toPosition);

    /**
     * 当item被滑掉时的处理方法
     * @param position 被滑动item的位置
     */
    void onItemSwiped(int position);
}
