package cn.synway.app.ui.main.none;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.synway.app.R;
import cn.synway.app.ui.main.publicappcenter.PublicAppCenterFragment;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by dell on 2018/12/29.
 */

public class NoneFragment2 extends SupportFragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fra_load, container, false);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (findChildFragment(PublicAppCenterFragment.class) == null) {
            loadRootFragment(R.id.fl_first_container, new PublicAppCenterFragment());
        }
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);


    }
}
