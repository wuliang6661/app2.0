package synway.module_publicaccount.public_favorite.grid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import synway.module_interface.config.BaseUtil;
import synway.module_publicaccount.R;
import synway.module_publicaccount.public_favorite.obj.Obj_GridItem;
import synway.module_publicaccount.public_favorite.obj.Obj_PublicAccount_Favorite;
import synway.module_publicaccount.publiclist.Obj_PublicAccount;
import synway.module_publicaccount.publiclist.SyncGetHeadThu;
import synway.module_publicaccount.until.AppUtil;
import synway.module_publicaccount.until.StringUtil;

import static synway.module_publicaccount.until.AppUtil.getAppIcon;
import static synway.module_publicaccount.until.BroastCastUtil.INSTALLBROAST;
import static synway.module_publicaccount.until.BroastCastUtil.PACKGENAME;
import static synway.module_publicaccount.until.BroastCastUtil.UNINSTALLBROAST;


/**
 * Created by Mfh on 2016/9/30.
 */

public class RecyclerGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {

    //常用对象数据集
    private ArrayList<Obj_GridItem> mDataSet = null;

    private final OnStartDragListener mDragStartListener;

    private final OnManageStateChangeListener mOnManageStateChange;

    private OnItemActionListener mOnItemActionListener;

    //是否处于编辑模式
    private boolean isManaging = false;
    private Context mContext = null;
     private SyncGetHeadThu syncGetHeadThu=null;

    public RecyclerGridAdapter(Context context, OnStartDragListener dragStartListener, OnManageStateChangeListener onManageStateChange,SyncGetHeadThu syncGetHeadThu) {
        mDragStartListener = dragStartListener;
        mOnManageStateChange = onManageStateChange;
        mContext = context;
        this.syncGetHeadThu=syncGetHeadThu;
        IntentFilter intentFilter = new IntentFilter(INSTALLBROAST);
        context.registerReceiver(installreceiver, intentFilter);
        IntentFilter unintentFilter = new IntentFilter(UNINSTALLBROAST);
        context.registerReceiver(uninstallreceiver, unintentFilter);
    }


    /**
     * 重设数据集
     * <p>
     * 先清空原有数据，在设置新的数据
     *
     * @param dataSet item的数据集
     */
    public void reset(ArrayList<Obj_GridItem> dataSet) {
        if (mDataSet != null) {
            this.mDataSet.clear();
        }
        this.mDataSet = dataSet;
    }

    /**
     * 刷新UI
     */
    public void refresh() {
        this.notifyDataSetChanged();
    }

    /**
     * 设置编辑状态标识，并刷新UI(暂时是控制 删除按钮是否显示)
     *
     * @param isManaging 是否进入编辑状态标识，true：编辑状态
     */
    public void setIsManaging(boolean isManaging) {
        /*
        //原方法，直接置反，易造成标识混乱
        this.isManaging = !this.isManaging;

        notifyDataSetChanged();*/
        if (this.isManaging == isManaging) {
            return;
        }
        this.isManaging = isManaging;
        notifyDataSetChanged();
    }


    /**
     * 重写Adapter中的getItemViewType方法，给position位置上的item设置view type以作区分,
     * 这可以用来实现multiple Recyclerview
     *
     * @param position 数据集中的
     * @return 一个int型标志，传递给onCreateViewHolder的第二个参数
     */
    @Override
    public int getItemViewType(int position) {
        if (mDataSet == null) {
            return super.getItemViewType(position);
        }
        try {
            return mDataSet.get(position).itemType;
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return super.getItemViewType(position);
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet == null ? 0 : mDataSet.size();
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_public_account_grid_item, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);

        //对应类型的item的属性可以修改，如：是否长按
        switch (viewType) {
            case Obj_GridItem.ITEM_TYPE_FAVORITE:
                itemViewHolder.isDraggable = true;
                itemViewHolder.isDeletable = true;
                itemViewHolder.isClickable = true;
                itemViewHolder.isLongClickable = true;
                break;
            case Obj_GridItem.ITEM_TYPE_MORE:
                itemViewHolder.isDraggable = false;
                itemViewHolder.isDeletable = false;
                itemViewHolder.isClickable = true;
                itemViewHolder.isLongClickable = false;
                break;
            case Obj_GridItem.ITEM_TYPE_FAVORITE_EMPTY:
                itemViewHolder.isDraggable = false;
                itemViewHolder.isDeletable = false;
                itemViewHolder.isClickable = true;
                itemViewHolder.isLongClickable = false;
                break;
            case Obj_GridItem.ITEM_TYPE_EMPTY:
                itemViewHolder.isDraggable = false;
                itemViewHolder.isDeletable = false;
                itemViewHolder.isClickable = false;
                itemViewHolder.isLongClickable = false;
                break;
        }
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof ItemViewHolder) {
            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

            Object obj = getDataByPosition(position);

            if (obj == null) {
                //设置item名称
                itemViewHolder.itemName.setText("数据异常");

                //默认删除按钮不可见
                itemViewHolder.deleteButton.setVisibility(View.GONE);
                return;
            }
            Obj_GridItem obj_gridItem = (Obj_GridItem) obj;
            itemViewHolder.downButton.setVisibility(View.GONE);
            itemViewHolder.gengxinButton.setVisibility(View.GONE);
            //设置item图标
            if(obj_gridItem.itemIconDrawable!=null){
                itemViewHolder.itemIcon.setImageDrawable(obj_gridItem.itemIconDrawable);
            }else{
                String publicid;
                if(StringUtil.isEmpty(obj_gridItem.mObj_publicAccount_favorite.MenuId)){//为公众号
                    publicid=obj_gridItem.mObj_publicAccount_favorite.ID;
                }else {
                    publicid = obj_gridItem.mObj_publicAccount_favorite.MenuId;
                }
                String path = getPublicActIconPath(publicid);
                Drawable drawable = Drawable.createFromPath(path);
                if(drawable!=null){
                    itemViewHolder.itemIcon.setImageDrawable(drawable);
                }else{

                        itemViewHolder.itemIcon.setImageResource(R.drawable.contact_public_account_png);
                        syncGetHeadThu.startPublicIdIcon(publicid, obj_gridItem.mObj_publicAccount_favorite.fc_mobilepic);

                }

            }

            if(obj_gridItem.mObj_publicAccount_favorite!=null) {
                if (obj_gridItem.mObj_publicAccount_favorite.type == 1) {
                    PackageInfo packageInfo = AppUtil.isAvilible(mContext, obj_gridItem.mObj_publicAccount_favorite.app_information.app_packangename);
                    if (packageInfo == null) {//该应用没有安装，添加下载小图标
                        itemViewHolder.gengxinButton.setVisibility(View.GONE);
                        itemViewHolder.downButton.setVisibility(View.VISIBLE);
                    } else {
                        itemViewHolder.itemIcon.setImageDrawable(getAppIcon(packageInfo.packageName, mContext));
                        if (!packageInfo.versionName.equals(
                                obj_gridItem.mObj_publicAccount_favorite.app_information.app_version)) {//版本不一样，添加更新红点
                            itemViewHolder.downButton.setVisibility(View.GONE);
                            itemViewHolder.gengxinButton.setVisibility(View.VISIBLE);
                        }else{
                            itemViewHolder.downButton.setVisibility(View.GONE);
                            itemViewHolder.gengxinButton.setVisibility(View.GONE);
                        }
                    }
                }
            }
            //设置item名称
            itemViewHolder.itemName.setText(obj_gridItem.itemName);

            //默认删除按钮不可见
            itemViewHolder.deleteButton.setVisibility(View.GONE);

            if (itemViewHolder.isDeletable) {
                if (isManaging) {//编辑状态，显示删除按钮
                    itemViewHolder.deleteButton.setVisibility(View.VISIBLE);
                    itemViewHolder.gengxinButton.setVisibility(View.GONE);
                    itemViewHolder.downButton.setVisibility(View.GONE);
                } else {
                    itemViewHolder.deleteButton.setVisibility(View.GONE);
                }
                itemViewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDataSet.remove(itemViewHolder.getAdapterPosition());
                        notifyItemRemoved(itemViewHolder.getAdapterPosition());
                    }
                });
            }

            if (mOnItemActionListener != null) {

                if (itemViewHolder.isClickable) {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnItemActionListener.onItemClick(v, itemViewHolder.getAdapterPosition());
                        }
                    });
                }

                if (itemViewHolder.isLongClickable) {
                    holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {

                            mOnItemActionListener.onItemLongClick(v, itemViewHolder.getAdapterPosition());
                            //为不影响其他item扩展 长按 效果，下面进行特殊情况判断
                            if (itemViewHolder.getItemViewType() == Obj_GridItem.ITEM_TYPE_FAVORITE) {
                                if (!isManaging) {
                                    isManaging = true;

                                    mOnManageStateChange.onChange(true);//通知按钮切换状态
                                    notifyDataSetChanged();
                                }
                            }
                            return false;
                        }
                    });
                }
            }
        }

    }


    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {

        /**
         * 根据ItemTouchHelper中设定的item移动动画方式，移动数据
         * 注意：如果item移动动画改变，要根据新的移动方式，修改下面的移动数据方法，
         *      否则会出现 界面对应的数据 跟 dataset中的数据 顺序 不一致
         */
        if (mDataSet != null && !mDataSet.isEmpty()) {

            try {
                if (fromPosition > toPosition) {
                    int i = fromPosition;
                    while (i > toPosition) {
                        Collections.swap(mDataSet, i, --i);
                    }
                } else if (fromPosition < toPosition) {
                    int i = fromPosition;
                    while (i < toPosition) {
                        Collections.swap(mDataSet, i, ++i);
                    }
                }
                //这是原始的交换数据项的方法，只是交换了对应position中的数据，跟界面显示的位置有差别，不利于保存位置信息
                //Collections.swap(mItems, fromPosition, toPosition);
                /**
                 * 这里有待讨论，是否只notify这两个位置即可？
                 */
                notifyItemMoved(fromPosition, toPosition);

            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        return false;
    }


    @Override
    public void onItemSwiped(int position) {
        if (mDataSet == null) {
            return;
        }
        try {
            mDataSet.remove(position);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return;
        }
        notifyItemRemoved(position);
    }


    /**
     * 当整个Item被点击、长按时，回调方法会被调用的监听接口定义
     */
    public interface OnItemActionListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    /**
     * 设置Item动作监听
     *
     * @param onItemActionListener
     */
    public void setOnItemActionListener(OnItemActionListener onItemActionListener) {
        mOnItemActionListener = onItemActionListener;
    }

    /**
     * 添加 一个 Item 的方法
     */
    public void addItem(int position, Obj_GridItem obj_gridItem) {
        if (mDataSet == null) {
            return;
        }
        try {
            mDataSet.add(position, obj_gridItem);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return;
        }
        //通知增加了 Item
        notifyItemInserted(position);

    }

    /**
     * 在最后一个item前 添加 一个 Item 的方法
     */
    public void addItemBeforeLast(Obj_GridItem obj_gridItem) {
        if (mDataSet == null) {
            return;
        }
        int size = mDataSet.size();
        if (size == 0) {
            size = 1;
        }
        try {
            //下面是是具体的添加过程
            mDataSet.add(size - 1, obj_gridItem);

        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return;
        }
        //通知增加了 Item
        notifyItemInserted(size - 1);
    }

    /**
     * 添加 几个 Item 的方法
     */
    public void addItems(int position, ArrayList<Obj_GridItem> obj_gridItems) {
        if (mDataSet == null) {
            return;
        }
        try {
            mDataSet.addAll(position, obj_gridItems);

        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return;
        }
        notifyItemRangeInserted(position, obj_gridItems.size());
    }

    /**
     * 删除 一个 Item 的方法
     */
    public void removeItem(int position) {
        if (mDataSet == null) {
            return;
        }
        try {
            mDataSet.remove(position);

        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return;
        }
        //通知删除了 Item
        notifyItemRemoved(position);
    }

    /**
     * 获取position下的数据
     *
     * @param position 数据集中的下标
     * @return 数据对象
     */
    public Object getDataByPosition(int position) {
        if (mDataSet != null && !mDataSet.isEmpty()) {
            try {
                return mDataSet.get(position);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }


    /**
     * 获取 常用应用对象 集合
     * <p>
     * 过滤掉不是常用应用(公众号、网页跳转类)的item
     * <p>
     * 调用时要判断是否为null,非null:有 有效的常用应用
     *
     * @return 对象集合，如果没有符合要求的item就返回null
     */
    public ArrayList<Obj_PublicAccount_Favorite> getDataSet_Favorite() {
        ArrayList<Obj_PublicAccount_Favorite> obj_publicAccount_favorites = new ArrayList<>();
        Obj_GridItem obj_gridItem;

        if (mDataSet != null && !mDataSet.isEmpty()) {
            for (int i = 0; i < mDataSet.size(); i++) {
                obj_gridItem = mDataSet.get(i);

                if (obj_gridItem.itemType == Obj_GridItem.ITEM_TYPE_FAVORITE && obj_gridItem.mObj_publicAccount_favorite != null) {
                    obj_publicAccount_favorites.add(obj_gridItem.mObj_publicAccount_favorite);
                }
            }
            return obj_publicAccount_favorites.isEmpty() ? null : obj_publicAccount_favorites;
        }

        return null;
    }

    /**
     * 获取item数据集
     *
     * @return item对象集合
     */
    public ArrayList<Obj_GridItem> getDataSet() {
        return mDataSet;
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        public final ImageView itemIcon;
        public final TextView itemName;

        public final ImageView deleteButton;
        public final ImageView downButton;
        public final ImageView gengxinButton;

        /***
         * 是否可拖拽
         */
        public boolean isDraggable = true;

        /***
         * 是否可删除
         */
        public boolean isDeletable = true;

        /***
         * 是否可点击
         */
        public boolean isClickable = true;

        /***
         * 是否可长按
         */
        public boolean isLongClickable = true;

        public ItemViewHolder(View itemView) {
            super(itemView);

            itemIcon = itemView.findViewById(R.id.item_icon);

            itemName = itemView.findViewById(R.id.item_name);

            deleteButton = itemView.findViewById(R.id.delete);
            downButton= itemView.findViewById(R.id.download);
            gengxinButton= itemView.findViewById(R.id.gengxin);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
    /**
     * 获取应用的头像路径
     *
     * @param id 应用的id
     * @return 头像路径
     */
    private String getPublicActIconPath(String id) {
        return BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + id;
    }
    private BroadcastReceiver installreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("testy","安装刷新");
            BroastFresh(intent);
        }
    };
    private BroadcastReceiver uninstallreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("testy","卸载刷新");
            BroastFresh(intent);
        }
    };
    private void BroastFresh(Intent intent){
        String packgename=intent.getStringExtra(PACKGENAME);
        if(mDataSet!=null) {
            for (Obj_GridItem obj_gridItem : mDataSet) {
                if(obj_gridItem.mObj_publicAccount_favorite!=null) {
                    if (obj_gridItem.mObj_publicAccount_favorite.app_information != null) {
                        if (("package:" + obj_gridItem.mObj_publicAccount_favorite.app_information.app_packangename).equals(packgename)) {
                            Log.i("testy", "真的刷新界面了");
                            refresh();
                        }
                    }
                }
            }
        }
    }
    public void onDestory(){
        mContext.unregisterReceiver(installreceiver);
        mContext.unregisterReceiver(uninstallreceiver);
    }
}
