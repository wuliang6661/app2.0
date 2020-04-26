package cn.synway.app.ui.downmap;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.synway.app.R;
import cn.synway.app.bean.MyAreaDO;
import cn.synway.app.widget.lgrecycleadapter.OnRVItemClickListener;

public class DownMapAdapter extends Adapter {
    private final DownMapActivity context;
    private final OnRVItemClickListener<MyAreaDO> clickLisener;
    private List<MyAreaDO> data;

    public DownMapAdapter(DownMapActivity downMapActivity, List<MyAreaDO> list, OnRVItemClickListener<MyAreaDO> onRVItemClick) {
        this.context = downMapActivity;
        this.clickLisener = onRVItemClick;
        this.data = list;
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).isHead() ? 0 : 1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View inflate = LayoutInflater.from(context).inflate(R.layout.item_dowm_map, parent, false);
            return new DownMapViewHolder(inflate);
        }
        else {
            View inflate = LayoutInflater.from(context).inflate(R.layout.item_dowm_map_head, parent, false);
            return new DownMapHeadViewHolder(inflate);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MyAreaDO myAreaDO = data.get(position);

        if (myAreaDO.isHead()) {

            //头部视图(省市,概要图)
            DownMapHeadViewHolder headHodel = (DownMapHeadViewHolder) holder;
            headHodel.mName.setText(myAreaDO.getName());
        }
        else {
            DownMapViewHolder itemHolder = (DownMapViewHolder) holder;
            itemHolder.mName.setText(myAreaDO.getName());
            itemHolder.hint.setVisibility(View.GONE);

            if (!myAreaDO.hasChild() || "概要图".equals(myAreaDO.getName())) {
                //概要图,城市
                bindCityItem(myAreaDO, itemHolder);
            }
            else {
                //省
                itemHolder.itemView.setBackgroundColor(context.getResources().getColor(R.color.f_white));
                itemHolder.itemView.setOnClickListener(itemHolder);
                itemHolder.downLoad.setVisibility(View.GONE);
                itemHolder.state.setVisibility(View.GONE);
                itemHolder.downUp.setVisibility(View.VISIBLE);
                if (myAreaDO.isExpand()) {
                    itemHolder.downUp.setText(context.getResources().getString(R.string.arrow_up));
                }
                else {
                    itemHolder.downUp.setText(context.getResources().getString(R.string.arrow_down));
                }

            }
        }
    }

    /**
     * 城市/概略图 Item数据绑定
     *
     * @param myAreaDO
     * @param holder
     */
    private void bindCityItem(MyAreaDO myAreaDO, DownMapViewHolder holder) {

        if ("概要图".equals(myAreaDO.getName())) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.f_white));
            if (myAreaDO.getState() == 0)
                holder.hint.setVisibility(View.VISIBLE);
        }
        else {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.hui_layout_bg));
        }
        holder.downUp.setVisibility(View.GONE);
        holder.state.setTextColor(context.getResources().getColor(R.color.head_blue));
        holder.state.setVisibility(View.VISIBLE);
        holder.downLoad.setVisibility(View.GONE);
        //下载状态
        switch (myAreaDO.getState()) {
            case 0:
                holder.state.setVisibility(View.GONE);
                holder.downLoad.setVisibility(View.VISIBLE);
                holder.downLoad.setOnClickListener(holder);
                break;
            case 1:
                holder.state.setText("准备中");
                break;
            case 2:
                holder.state.setText("下载中" + myAreaDO.getPercentage() + "%");
                break;
            case 3:
                holder.state.setTextColor(context.getResources().getColor(R.color.text_error));
                holder.state.setText("出现错误");
                break;
            case 4:
                holder.state.setText("下载完成");
                break;
            case 5:
                holder.state.setText("解压中");

                break;
            case 6:
                holder.state.setText("已下载");
                break;
            case 7:
                holder.state.setTextColor(context.getResources().getColor(R.color.text_error));
                holder.state.setText("解压异常");
                break;
            case 8:
                holder.state.setText("解压中");//从流写入本地
                break;
            default:
                break;
        }


    }

    /**
     * 状态更新
     *
     * @param areaDO
     */
    public void notifyItemByEntity(MyAreaDO areaDO) {
        int position = this.data.indexOf(areaDO);
        if (position >= 0 && position < this.data.size()) {
            notifyItemChanged(position);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void changeData(List<MyAreaDO> list) {
        this.data = list;
        notifyDataSetChanged();
    }


    protected class DownMapViewHolder extends ViewHolder implements OnClickListener {

        private final TextView mName;
        private final View downLoad;
        private final TextView downUp;
        private final TextView state;
        private final View hint;
        private int position;

        public DownMapViewHolder(View inflate) {
            super(inflate);
            mName = inflate.findViewById(R.id.name);
            downLoad = inflate.findViewById(R.id.download);
            downUp = inflate.findViewById(R.id.down_up);
            state = inflate.findViewById(R.id.state);
            hint = inflate.findViewById(R.id.hint);

        }

        @Override
        public void onClick(View v) {

            position = getAdapterPosition();
            MyAreaDO myAreaDO = data.get(position);
            if (v == itemView && myAreaDO.hasChild()) {
                if (!myAreaDO.isExpand()) {
                    data.addAll(position + 1, myAreaDO.getChild());
                    myAreaDO.setExpand(true);
                    notifyItemRangeInserted(position + 1, myAreaDO.getChild().size());
                    notifyItemRangeChanged(position,  myAreaDO.getChild().size()+1);//0-data.size() 无法刷新viewHolder
                    //延迟执行，影响动画
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            smootScrollToPosition(position);
                        }
                    }, 500);
                }
                else {
                    data.removeAll(myAreaDO.getChild());
                    myAreaDO.setExpand(false);
                    notifyItemRangeRemoved(position + 1, myAreaDO.getChild().size());
                    notifyItemRangeChanged(position,  myAreaDO.getChild().size()+1);
                }

            }
            else if (v.getId() == R.id.download && clickLisener != null) {
                clickLisener.ItemClick(v, position, myAreaDO);
            }
        }

        /**
         * 滚动到顶部
         *
         * @param position
         */
        private void smootScrollToPosition(int position) {
            RecyclerView dowmMapRv = context.getDowmMapRv();
            LinearSmoothScroller smoothScroller = new LinearSmoothScroller(context) {
                @Override
                protected int getVerticalSnapPreference() {
                    return SNAP_TO_START;
                }
            };
            smoothScroller.setTargetPosition(position);
            dowmMapRv.getLayoutManager().startSmoothScroll(smoothScroller);
        }

    }


    private class DownMapHeadViewHolder extends ViewHolder {

        private final TextView mName;

        public DownMapHeadViewHolder(View inflate) {
            super(inflate);
            mName = inflate.findViewById(R.id.item_head_name);
        }
    }
}
