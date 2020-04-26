package synway.module_publicaccount.public_favorite.grid;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Mfh on 2016/9/30.
 *
 * 作用域：自定义拖拽移动触发方式（如：触碰某个按钮就可以直接移动item）时，在ItemTouchHelper实例化所在的类中实现该接口
 */
public interface OnStartDragListener {

    /**
     *
     * 当自定义拽移动触发时 调用该方法
     *
     * example：
     * private OnStartDragListener mOnStartDragListener = new OnStartDragListener() {
     *      @Override
     *      public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
     *          if (mItemTouchHelper != null) {
     *              mItemTouchHelper.startDrag(viewHolder);
     *          }
     *      }
     * };
     *
     * @param viewHolder
     */
    void onStartDrag(RecyclerView.ViewHolder viewHolder);
}
