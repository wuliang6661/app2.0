package qyc.library.control.fragment_easy;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class MFragmentAdapter extends FragmentPagerAdapter {

	private ArrayList<Fragment> fragments = null;

	public MFragmentAdapter(FragmentManager fm) {
		super(fm);
		// 加入页面
		this.fragments = new ArrayList<Fragment>();
	}

	@Override
	public Fragment getItem(int index) {
		// 我见过有Demo在getItem的时候每次都去new一个新的,这是不符合adapter架构的,随着拓展必然会出问题
		return fragments.get(index);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return fragments.size();
	}

	public void addItem(Fragment fragment)
	{
		fragments.add(fragment);
	}

}
