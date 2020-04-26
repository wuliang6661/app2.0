package synway.module_publicaccount.public_list_new.grid_helper;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import synway.module_publicaccount.until.DialogTool;

/**
 * Created by leo on 2018/6/15.
 */

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback  {

    private ItemTouchHelperAdapter itemTouchHelperAdapter;

    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter itemTouchHelperAdapter){
        this.itemTouchHelperAdapter = itemTouchHelperAdapter;
    }
    @Override public boolean isLongPressDragEnabled() {
        return true;
    }


    @Override public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

        return makeMovementFlags(dragFlags,0);
    }


    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

        if (DialogTool.dialog != null) {
            DialogTool.dialog.dismiss();
        }
        //判断两个item的类型是否相同，如果不同就不让移动
        if (viewHolder.getItemViewType() != target.getItemViewType()) {
            return false;
        }
        //数据集中的 数据位置 也跟着一起移动
        itemTouchHelperAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {

            if (viewHolder != null && viewHolder instanceof ItemTouchHelperViewHolder) {
                // Let the view holder know that this item is being moved or dragged
                ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
                itemViewHolder.onItemSelected();
            }
        }
    }


    @Override public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (viewHolder instanceof ItemTouchHelperViewHolder) {
            // Tell the view holder it's time to restore the idle state
            ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
            itemViewHolder.onItemClear();
        }
        itemTouchHelperAdapter.onFinish();
    }
}
