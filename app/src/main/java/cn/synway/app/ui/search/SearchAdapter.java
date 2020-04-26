package cn.synway.app.ui.search;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.synway.app.R;
import cn.synway.app.bean.OrganDao;
import cn.synway.app.db.table.UserEntry;

public class SearchAdapter extends Adapter {


    private final SearchActivity activity;
    private List<OrganDao> OrganData;//机构
    private List<UserEntry> data;//用户
    private String key;//搜索的值
    private final OnClickListener listener;


    public SearchAdapter(SearchActivity searchActivity, String key, List<UserEntry> list, List<OrganDao> olist, View.OnClickListener listener) {
        this.activity = searchActivity;
        this.data = list;
        this.OrganData = olist;
        this.key = key;
        this.listener = listener;
    }


    @Override
    public int getItemViewType(int position) {
        if (this.OrganData.size() > 0) {
            if (position == 0)
                return 1;
            if (position == this.OrganData.size() + 1)
                return 1;
        }
        else {
            if (position == 0)
                return 1;
        }

        return 0;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_search, parent, false);
            SearchHolder searchHolder = new SearchHolder(view);
            return searchHolder;
        }
        else {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_search_head, parent, false);
            SearchHeadHolder searchHolder = new SearchHeadHolder(view);
            return searchHolder;
        }


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (getItemViewType(position) == 1) {
            SearchHeadHolder headHolder = (SearchHeadHolder) holder;
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) headHolder.itemView.getLayoutParams();
            p.setMargins(0, 0, 0, 0);
            if (OrganData.size() > 0 && position == 0) {
                headHolder.mHead.setText("机构");
            }
            else {
                headHolder.mHead.setText("用户");
                if (OrganData.size() > 0) {
                    ViewGroup.MarginLayoutParams p2 = (ViewGroup.MarginLayoutParams) headHolder.itemView.getLayoutParams();
                    p2.setMargins(0, 20, 0, 0);
                }
            }
            return;
        }

        SearchHolder searchHolder = (SearchHolder) holder;
        String name = "";
        if (position <= OrganData.size()) {
            OrganDao organDao = OrganData.get(position - 1);
            name = organDao.getName();
            searchHolder.itemView.setTag(R.string.ORGANTAGKEY, organDao.getId());
            Glide.with(activity).load("").placeholder(R.mipmap.organ).into(searchHolder.mIcon);
        }
        else {
            int index = OrganData.size() > 0 ? OrganData.size() + 2 : 1;
            UserEntry userBO = data.get(position - index);
            name = userBO.getUserName();
            searchHolder.itemView.setTag(R.string.USERTAGKEY, userBO.getUserID());
            Glide.with(activity).load(userBO.getUserPic()).placeholder(R.mipmap.preson_default).into(searchHolder.mIcon);
        }

        SpannableString s = new SpannableString(name);
        Pattern p = Pattern.compile(key.toLowerCase());
        Pattern pp = Pattern.compile(key.toUpperCase());
        Matcher m = p.matcher(s);
        Matcher mm = pp.matcher(s);

        while (m.find()) {
            int start = m.start();
            int end = m.end();
            s.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        }
        while (mm.find()) {
            int start2 = mm.start();
            int end2 = mm.end();
            s.setSpan(new ForegroundColorSpan(Color.RED), start2, end2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        searchHolder.name.setText(s);
        searchHolder.itemView.setOnClickListener(searchHolder);
    }

    @Override
    public int getItemCount() {
        return data.size() + OrganData.size() + (data.size() > 0 ? 1 : 0) + (OrganData.size() > 0 ? 1 : 0);
    }

    public void changeData(List<UserEntry> list, List<OrganDao> oList, String key) {
        this.data = list;
        this.OrganData = oList;
        this.key = key;
        notifyDataSetChanged();
    }

    private class SearchHolder extends ViewHolder implements OnClickListener {
        public TextView name;
        public ImageView mIcon;

        public SearchHolder(View view) {
            super(view);
            mIcon = itemView.findViewById(R.id.icon);
            name = itemView.findViewById(R.id.name);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onClick(v);
            }
        }
    }

    private class SearchHeadHolder extends ViewHolder {

        //  private   View grayItem;
        private TextView mHead;

        public SearchHeadHolder(View itemView) {
            super(itemView);
            mHead = itemView.findViewById(R.id.head);
            //grayItem = itemView.findViewById(R.id.gray_item);
        }
    }
}
