package synway.module_publicaccount.public_list_new.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import synway.module_interface.AppConfig;
import synway.module_interface.config.BaseUtil;
import synway.module_publicaccount.Main;
import synway.module_publicaccount.R;
import synway.module_publicaccount.public_chat.SetLCForPublicAccount;
import synway.module_publicaccount.public_list_new.OrderConfigRW;
import synway.module_publicaccount.public_list_new.grid_helper.ItemTouchHelperAdapter;
import synway.module_publicaccount.public_list_new.grid_helper.ItemTouchHelperViewHolder;
import synway.module_publicaccount.until.AppUtil;
import synway.module_publicaccount.until.StringUtil;

import static synway.module_publicaccount.until.BroastCastUtil.INSTALLBROAST;
import static synway.module_publicaccount.until.BroastCastUtil.PACKGENAME;
import static synway.module_publicaccount.until.BroastCastUtil.UNINSTALLBROAST;

/**
 * Created by leo on 2018/6/15.
 */

public class PublicGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        ItemTouchHelperAdapter {

    private Context context;

    private ArrayList<PublicGridItem> dataSet;
    //    private HashMap<String,Integer> fatherDataMap = new HashMap<>();
    private HashMap<String, Boolean> fatherSpreadMap = new HashMap<>();//是否展开收起

    public PublicGridAdapter(Context context) {
        this.context = context;
        this.dataSet = new ArrayList<>();
        IntentFilter intentFilter = new IntentFilter(INSTALLBROAST);
        context.registerReceiver(installreceiver, intentFilter);
        IntentFilter unintentFilter = new IntentFilter(UNINSTALLBROAST);
        context.registerReceiver(uninstallreceiver, unintentFilter);
    }


    @Override
    public int getItemViewType(int position) {
        return dataSet.get(position).isTitle;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder itemViewHolder = null;
        switch (viewType) {
            case 0:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_public_account_list_grid_item, parent, false);
                itemViewHolder = new ItemViewHolder(view);
                break;
            case 1:
                View vTitle = LayoutInflater.from(parent.getContext()).inflate(R.layout.titlebar_child, parent, false);
                itemViewHolder = new TitleHolder(vTitle);
                break;
        }
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ItemViewHolder) {
            onBindItemViewHolder((ItemViewHolder) holder, position);
        } else if (holder instanceof TitleHolder) {
            onBindTitleViewHolder((TitleHolder) holder, position);
        }
    }

    private void onBindTitleViewHolder(TitleHolder holder, int position) {
        final PublicGridItem item = getDataByPosition(position);
        if (item == null) {
            //设置item名称
            holder.titleName.setText("数据异常");
            if (position == 0) {
                holder.vLine.setVisibility(View.GONE);
            }
            return;
        }
        holder.titleName.setText(item.name);
        if (item.childPublicAccountCount > 4) {
            holder.tvSpread.setVisibility(View.VISIBLE);
            if (fatherSpreadMap.containsKey(item.id) && !fatherSpreadMap.get(item.id)) {
                holder.tvSpread.setText("更多");
            } else {
                holder.tvSpread.setText("收起");
            }
            holder.tvSpread.setOnClickListener(                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fatherSpreadMap.containsKey(item.id) && !fatherSpreadMap.get(item.id)) {
                        fatherSpreadMap.put(item.id, true);
                    } else {
                        fatherSpreadMap.put(item.id, false);
                    }
                    PublicGridAdapter.this.notifyDataSetChanged();
                }
            });
        } else {
            holder.tvSpread.setVisibility(View.GONE);
        }
        if (position == 0) {
            holder.vLine.setVisibility(View.GONE);
        }
    }

    private void onBindItemViewHolder(final ItemViewHolder holder, int position) {
        PublicGridItem item = getDataByPosition(position);
        if (item == null) {
            //设置item名称
            holder.itemName.setText("数据异常");
            return;
        }

        if (item.childNum > 4 && fatherSpreadMap.containsKey(item.fatherGroupID) && !fatherSpreadMap.get(item.fatherGroupID)) {
            holder.llItem.setVisibility(View.GONE);
        } else {
            holder.llItem.setVisibility(View.VISIBLE);
        }

        holder.downBtn.setVisibility(View.GONE);
        holder.updateBtn.setVisibility(View.GONE);

        if (item.UnReadCount > 99) {
            holder.tvUnRead.setVisibility(View.VISIBLE);
            holder.tvUnRead.setText("99+");
            holder.tvUnRead.setTextSize(10);
        } else if (item.UnReadCount > 0) {
            holder.tvUnRead.setVisibility(View.VISIBLE);
            holder.tvUnRead.setText(String.valueOf(item.UnReadCount));
            holder.tvUnRead.setTextSize(13);
        } else {
            holder.tvUnRead.setVisibility(View.GONE);
        }
        //设置图标图片
        String iconPath = BaseUtil.FILE_PUBLIC_ACCOUNT_THU + "/" + item.id;
        Drawable drawable = Drawable.createFromPath(iconPath);
        if (drawable == null) {
            holder.itemIcon.setImageResource(R.drawable.contact_public_account_png);
            // File iconFile = new File(iconPath);
            // if (iconFile.exists()) {
            //     //说明本地的是残缺的文件，形成不了drawable
            //     iconFile.delete();
            // }
        } else {
            holder.itemIcon.setImageDrawable(drawable);
        }

        //设置item名称
        holder.itemName.setText(item.name);
        //本地应用的情况,后续加了应用名称区别于包名,当存在的时候即设为应用名称而不是包名
        if (item.type == 1) {
            String appName = item.app_information.app_name;
            if (!TextUtils.isEmpty(appName)) {
                holder.itemName.setText(appName);
            }
        }

        //针对本地应用设置更新下载按钮
        if (item.type == 1) {
            PackageInfo packageInfo = AppUtil.isAvilible(context, item.app_information.app_packangename);
            if (packageInfo == null) {
                //该应用没有安装，添加下载小图标
                holder.downBtn.setVisibility(View.VISIBLE);
                holder.updateBtn.setVisibility(View.GONE);
            } else {
                holder.itemIcon.setImageDrawable(AppUtil.getAppIcon(item.app_information.app_packangename, context));
                if (!packageInfo.versionName.equals(item.app_information.app_version)) {
                    //版本不一样，添加更新红点
                    holder.downBtn.setVisibility(View.GONE);
                    holder.updateBtn.setVisibility(View.VISIBLE);
                } else {
                    holder.downBtn.setVisibility(View.GONE);
                    holder.updateBtn.setVisibility(View.GONE);
                }
            }
        }

        //绑定点击事件
        if (onItemClickEventListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickEventListener.onItemClick(v, holder.getAdapterPosition());
                }
            });
        }

    }


    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        /**
         * 根据ItemTouchHelper中设定的item移动动画方式，移动数据
         * 注意：如果item移动动画改变，要根据新的移动方式，修改下面的移动数据方法，
         *      否则会出现 界面对应的数据 跟 dataset中的数据 顺序 不一致
         */
        if (dataSet != null && !dataSet.isEmpty()) {
            try {
                if (fromPosition > toPosition) {
                    int i = fromPosition;
                    while (i > toPosition) {
                        Collections.swap(dataSet, i, --i);
                    }
                } else if (fromPosition < toPosition) {
                    int i = fromPosition;
                    while (i < toPosition) {
                        Collections.swap(dataSet, i, ++i);
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
    public void onFinish() {
        //当自由拖动完毕后,将此时的顺序存储到数据库
        String orderStr = "";
        if (dataSet != null && dataSet.size() > 0) {
            for (int i = 0; i < dataSet.size(); i++) {
                orderStr = orderStr + dataSet.get(i).id;
                if (i != (dataSet.size() - 1)) {
                    orderStr = orderStr + "|";
                }
            }
        }
        Log.d("dym------------------->", "拖动结束 orderStr= " + orderStr);
        //写到数据库
        OrderConfigRW.write(orderStr, Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase());
    }

    public void changeObjByID(String targetID, int unReadCount) {
        int i = 0;
        for (; i < dataSet.size(); i++) {
            if (targetID.equals(dataSet.get(i).id)) {
                break;
            }
        }
        if (i < dataSet.size()) {
            dataSet.get(i).UnReadCount = unReadCount;
        }
    }

    //清理所有消息的未读数
    public void clearAllObjUnread() {
        int i = 0;
        for (; i < dataSet.size(); i++) {
            dataSet.get(i).UnReadCount = 0;
        }
    }

    public static class TitleHolder extends RecyclerView.ViewHolder {
        public final TextView titleName;
        public final View vLine;
        public final TextView tvSpread;

        public TitleHolder(View itemView) {
            super(itemView);
            titleName = itemView.findViewById(R.id.tv_title);
            vLine = itemView.findViewById(R.id.v_line);
            tvSpread = itemView.findViewById(R.id.tv_spread);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        public final ImageView itemIcon;
        public final TextView itemName;

        public final ImageView downBtn;
        public final ImageView updateBtn;
        public final TextView tvUnRead;
        public final View llItem;

        public ItemViewHolder(View itemView) {
            super(itemView);

            itemIcon = itemView.findViewById(R.id.iv_item_icon);

            itemName = itemView.findViewById(R.id.tv_item_name);

            downBtn = itemView.findViewById(R.id.iv_download);
            updateBtn = itemView.findViewById(R.id.iv_update);
            tvUnRead = itemView.findViewById(R.id.tv_unread);
            llItem = itemView.findViewById(R.id.ll_item);
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
     * 刷新UI
     */
    public void refresh() {
        this.notifyDataSetChanged();
    }

    /**
     * 获取position下的数据
     *
     * @param position 数据集中的下标
     * @return 数据对象
     */
    public PublicGridItem getDataByPosition(int position) {
        if (dataSet != null && !dataSet.isEmpty()) {
            try {
                return dataSet.get(position);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public void reset(ArrayList<PublicGridItem> list) {
        if (this.dataSet != null) {
            this.dataSet.clear();
        }
        this.dataSet = change(list);
    }


    //调整列表
    private ArrayList<PublicGridItem> change(ArrayList<PublicGridItem> list) {
        HashMap<String, Integer> map = new HashMap<>();
        if (AppConfig.PUBLIC_UNREAD_TYPE!=1) {
            //获取未读消息对应map
            map = SetLCForPublicAccount.getUnReadCountMap(context,
                    Main.instance().moduleHandle.getSQLiteHelp().getWritableDatabase());
        }
//        //对数据根据大类id进行排序，
        Collections.sort(list, new Comparator<PublicGridItem>() {
            @Override
            public int compare(PublicGridItem o1, PublicGridItem o2) {
                int i1 = 0,i2 =0;
                if(!StringUtil.isEmpty(o1.fatherGroupID)){
                    i1++;
                }
                if(!StringUtil.isEmpty(o2.fatherGroupID)){
                    i2++;
                }
                return i2-i1;
            }
        });

        ArrayList<PublicGridItem> list2 = new ArrayList<>();
        String fatherId = "-1";
        int perFatherIndex = -1;//上一个父类型所在list2列表的顺序号。
        int fathercount = -1;
        int childCount = 1;//子类所在父类下的顺序号
        for (PublicGridItem item : list) {
            if (AppConfig.PUBLIC_UNREAD_TYPE!=0 && item.type == 3) {//无跳转公众号，不显示
                continue;
            }
            if(item.fatherGroupID==null){
                item.fatherGroupID ="";
            }
            if (!fatherId.equals(item.fatherGroupID)) {
                if (perFatherIndex != -1) {
                    list2.get(perFatherIndex).childPublicAccountCount = childCount;
                }
                perFatherIndex = list2.size();
                childCount = 0;

                fatherId = item.fatherGroupID;

//                fatherDataMap.put(fatherId,perFatherIndex);
                PublicGridItem publicGridItem = new PublicGridItem();
                if (StringUtil.isEmpty(item.fatherGroupName)) {
                    fathercount ++;
                    publicGridItem.name = "其他";
                } else {
                    fathercount +=2;
                    publicGridItem.name = item.fatherGroupName;
                }
                publicGridItem.id = item.fatherGroupID;
                publicGridItem.isTitle = 1;
                list2.add(publicGridItem);
            }
            if (AppConfig.PUBLIC_UNREAD_TYPE!=1) {
                item.UnReadCount = map.containsKey(item.id) ? map.get(item.id) : 0;
            }
            item.isTitle = 0;
            item.childNum = ++childCount;
            list2.add(item);
        }
        if (perFatherIndex != -1) {//最后一个父类型的数据更新补充
            list2.get(perFatherIndex).childPublicAccountCount = childCount;
        }
        if(fathercount==0){
            list2.remove(0);
        }
        return list2;
    }
//
//    //更改结构，增加父类对象
//    private ArrayList<PublicGridItem> transformationData(ArrayList<PublicGridItem> list) {
//        String fatherName = "";
//        for (PublicGridItem item : list) {
//            if (!fatherName.equals(item.name)) {
//                fatherName = item.name;
//                PublicGridItem publicGridItem = new PublicGridItem();
//            }
//
//        }
//        return null;
//    }


    /**
     * 当title中的展开view被点击时，回调方法会被调用的监听接口定义
     */
//    public interface OnTitleSpreadClickEventListener{
//        void onItemClick(View view, int position);
//    }
//

    /**
     * 当整个Item被点击、长按时，回调方法会被调用的监听接口定义
     */
    public interface OnItemClickEventListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickEventListener onItemClickEventListener;

    public void setOnItemClickEventListener(OnItemClickEventListener listener) {
        this.onItemClickEventListener = listener;
    }

    private BroadcastReceiver installreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BroastFresh(intent);
        }
    };
    private BroadcastReceiver uninstallreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BroastFresh(intent);
        }
    };

    private void BroastFresh(Intent intent) {
        String packgename = intent.getStringExtra(PACKGENAME);
        if (dataSet != null) {
            for (PublicGridItem item : dataSet) {
                if (item.app_information != null) {
                    if (("package:" + item.app_information.app_packangename).equals(packgename)) {
                        refresh();
                    }
                }
            }
        }
    }

    public void destroy() {
        if (installreceiver != null) {
            context.unregisterReceiver(installreceiver);
        }
        if (uninstallreceiver != null) {
            context.unregisterReceiver(uninstallreceiver);
        }
    }


}
