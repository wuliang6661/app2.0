package synway.module_publicaccount.public_list_new.grid_helper;

/**
 * Created by leo on 2018/6/15.
 */

public interface ItemTouchHelperAdapter {
    boolean onItemMove(int fromPosition, int toPosition);
    void onFinish();

}
