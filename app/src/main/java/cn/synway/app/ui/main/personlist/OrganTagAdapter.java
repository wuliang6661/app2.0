package cn.synway.app.ui.main.personlist;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.synway.app.R;
import cn.synway.app.bean.PersonInPsListBO.TagList;
import cn.synway.app.ui.main.personlist.OrganTagAdapter.MyViewHolder;
import cn.synway.app.widget.lgrecycleadapter.OnRVItemClickListener;

public class OrganTagAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private final FragmentActivity activity;
    private OnRVItemClickListener listener;
    private List<TagList> data;

    public OrganTagAdapter(FragmentActivity activity, List<TagList> tagLists, OnRVItemClickListener onRVItemClickListener) {
        this.data = tagLists;
        this.activity = activity;
        this.listener = onRVItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_organ_tag, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (position == 0) {
            holder.mImg.setVisibility(View.GONE);
        }
        else {
            holder.mImg.setVisibility(View.VISIBLE);
        }
        if (position == data.size() - 1) {
            holder.mName.setTextColor(activity.getResources().getColor(R.color.text_999));
        }
        else {
            holder.mName.setTextColor(activity.getResources().getColor(R.color.title_bg));
        }
        TagList tagList = data.get(position);
        holder.mName.setText(tagList.getName());
        holder.itemView.setOnClickListener(holder);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<TagList> tagLists) {
        this.data = tagLists;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends ViewHolder implements OnClickListener {

        private final TextView mName;
        private final View mImg;

        public MyViewHolder(View view) {
            super(view);

            mName = view.findViewById(R.id.name);
            mImg = view.findViewById(R.id.img);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int position = getAdapterPosition();
                listener.ItemClick(v, position, data.get(position));
            }
        }
    }
}
