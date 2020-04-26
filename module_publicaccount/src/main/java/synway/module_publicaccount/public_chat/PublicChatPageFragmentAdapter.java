package synway.module_publicaccount.public_chat;

/**
 * Created by leo on 2019/1/19.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.ArrayList;
public class PublicChatPageFragmentAdapter extends FragmentPagerAdapter  {
    private ArrayList<Fragment> fragmentList;
    private String [] names;


    public PublicChatPageFragmentAdapter(FragmentManager fm, ArrayList<Fragment> fragmentList,String [] names) {
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
        return names != null ? names[position] : null;
    }

}
