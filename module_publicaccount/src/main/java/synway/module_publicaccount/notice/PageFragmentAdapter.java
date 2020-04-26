package synway.module_publicaccount.notice;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.ArrayList;

/**
 * Created by leo on 2018/10/23.
 */

public class PageFragmentAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragmentList;
    private String [] names;


    public PageFragmentAdapter(FragmentManager fm, ArrayList<Fragment> fragmentList,String [] names) {
        super(fm);
        this.fragmentList = fragmentList;
        this.names = names;
    }


    @Override public Fragment getItem(int position) {
        return fragmentList.get(position);
    }


    @Override public int getCount() {
        return fragmentList.size();
    }


    @Override public CharSequence getPageTitle(int position) {
        return names[position];
    }


}
