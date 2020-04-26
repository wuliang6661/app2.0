package synway.module_stack.weight;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

import java.util.List;

public class FragmentPaerAdapter extends FragmentPagerAdapter {

    List<Fragment> fragments;
    private String[] titles = new String[]{"基本信息", "预警", "请销假"};

    public FragmentPaerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
