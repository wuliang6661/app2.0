package cn.synway.app.ui.main.publicappcenter;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.synway.app.R;
import cn.synway.app.bean.NetAPPBO;
import cn.synway.app.widget.CornersImageView;
import cn.synway.app.widget.GlideRoundTransform;
import cn.synway.app.widget.lgrecycleadapter.OnRVItemClickListener;

public class AppCenterGridAdapter extends Adapter {

    private final OnRVItemClickListener clickListener;
    private final FragmentActivity activity;
    private List<NetAPPBO> data;

    public AppCenterGridAdapter(FragmentActivity activity, OnRVItemClickListener itemClick) {
        this.activity = activity;
        this.clickListener = itemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder itemViewHolder = null;
        switch (viewType) {
            case 1:
                View vTitle = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_center_head, parent, false);
                itemViewHolder = new TitleHolder(vTitle);
                break;
            default:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_center, parent, false);
                itemViewHolder = new ItemViewHolder(view);
                break;
        }
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int itemViewType = getItemViewType(position);
        NetAPPBO netAPPBO = data.get(position);
        if (itemViewType == 0) {

            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.itemName.setText(netAPPBO.getName());
            itemViewHolder.itemIcon.setCorners(5);
            itemViewHolder.point.setText(netAPPBO.getNoReadCount() > 99 ? "99+" : netAPPBO.getNoReadCount() + "");

            itemViewHolder.point.setVisibility(netAPPBO.getNoReadCount() > 0 ? View.VISIBLE : View.GONE);

            Glide.with(activity).load(netAPPBO.getMobilePic()).error(R.mipmap.contact_public_account_png).placeholder(R.mipmap.contact_public_account_png)
                    .transform(new GlideRoundTransform(activity, 5)).into(itemViewHolder.itemIcon);
            itemViewHolder.itemView.setOnClickListener(itemViewHolder);


        } else if (itemViewType == 1) {

            TitleHolder titleHolder = (TitleHolder) holder;
            titleHolder.titleName.setText(netAPPBO.getHeadName());

        }
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).isHead() ? 1 : 0;
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public void changeData(List list) {
        this.data = list;
        this.notifyDataSetChanged();
    }

    public class TitleHolder extends ViewHolder {
        public TextView titleName;


        public TitleHolder(View itemView) {
            super(itemView);
            titleName = itemView.findViewById(R.id.head_name);
        }
    }

    public class ItemViewHolder extends ViewHolder implements OnClickListener {

        public CornersImageView itemIcon;
        public TextView itemName;
        public TextView point;


        public ItemViewHolder(View itemView) {
            super(itemView);
            point = itemView.findViewById(R.id.point);
            itemIcon = itemView.findViewById(R.id.item_icon);
            itemName = itemView.findViewById(R.id.item_name);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.ItemClick(v, getAdapterPosition(), data.get(getAdapterPosition()));
            }
        }
    }
}
