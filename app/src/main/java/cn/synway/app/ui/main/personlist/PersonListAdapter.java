package cn.synway.app.ui.main.personlist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.synway.app.R;
import cn.synway.app.bean.PersonInPsListBO.OrganAndUserInfoBO;
import cn.synway.app.widget.CornersImageView;

public class PersonListAdapter extends RecyclerView.Adapter {
    private List<OrganAndUserInfoBO> data;
    private Context context;
    protected ItemClick mItemClick;

    public PersonListAdapter(List<OrganAndUserInfoBO> persons, ItemClick itemClick) {
        this.data = persons;
        this.mItemClick = itemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == 0) {
            return new PersonHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_person, parent, false));
        }
        else {
            return new PersonHeadHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_person_head, parent, false));
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        OrganAndUserInfoBO organAndUserInfoBO = data.get(position);
        boolean head = organAndUserInfoBO.isHead();
        if (head) {
            PersonHeadHolder headHolder = (PersonHeadHolder) holder;
            TextView itemView = (TextView) headHolder.itemView;
            itemView.setText(organAndUserInfoBO.getFirstChar());
        }
        else {
            PersonHolder personHolder = (PersonHolder) holder;
            String name = organAndUserInfoBO.getUserID() != null ?
                    organAndUserInfoBO.getUserName() :
                    (organAndUserInfoBO.getName() + "(" + organAndUserInfoBO.getUserCount() + ")");
            personHolder.name.setText(name);

            personHolder.setEntity(data.get(position));
            holder.itemView.setOnClickListener(personHolder);

            if (organAndUserInfoBO.getFather() != null) {
                //机构
                personHolder.arrowRight.setVisibility(View.VISIBLE);
                Glide.with(context).load(organAndUserInfoBO.getUserPic()).placeholder(R.mipmap.organ).into(personHolder.mIcon);
            }
            else {
                //人
                Glide.with(context).load(organAndUserInfoBO.getUserPic()).placeholder(R.mipmap.preson_default).into(personHolder.mIcon);
                personHolder.arrowRight.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        OrganAndUserInfoBO organAndUserInfoBO = data.get(position);
        if (organAndUserInfoBO.isHead()) {
            return 1;
        }
        else {
            return 0;
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void changeData(List<OrganAndUserInfoBO> persons) {
        this.data = persons;
        notifyDataSetChanged();
    }

    /**
     * 机构和人
     */

    protected class PersonHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView name;
        public CornersImageView mIcon;
        private OrganAndUserInfoBO entity;
        public ImageView arrowRight;

        public PersonHolder(View itemView) {

            super(itemView);

            mIcon = itemView.findViewById(R.id.icon);
            mIcon.setCorners(5);
            name = itemView.findViewById(R.id.name);
            arrowRight = itemView.findViewById(R.id.arrow_right);
        }

        @Override
        public void onClick(View v) {
            if (entity.getFather() != null && mItemClick != null) {
                //机构，下级成员
                mItemClick.getGroupPerson(entity.getName(), entity.getId());
            }
            else if (entity.getUserID() != null && mItemClick != null) {
                //人，名片
                mItemClick.goPersonCard(entity);
            }
        }

        public void setEntity(OrganAndUserInfoBO person) {
            this.entity = person;
        }
    }

    /**
     * A_Z head
     */
    public class PersonHeadHolder extends ViewHolder {

        public PersonHeadHolder(View itemView) {
            super(itemView);
        }
    }

    public interface ItemClick {
        void goPersonCard(OrganAndUserInfoBO userInfoBO);

        void getGroupPerson(String name, String id);
    }
}
