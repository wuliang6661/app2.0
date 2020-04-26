package cn.synway.app.widget.lgrecycleadapter;

import android.view.View;

public interface OnRVItemClickListener<T> {

    void ItemClick(View view, int position, T entity);

}
