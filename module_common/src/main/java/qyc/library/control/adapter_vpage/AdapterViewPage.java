package qyc.library.control.adapter_vpage;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

/*
 * 网上查阅了不少资料,大概理念就是必须事先初始化好View,而adapter的作用只是选择性的把加入到ViewPager中来显示.
 *
 * 那么有多少页就会有多少个View..这好处是每一页可以有完全不同的控件.坏处是页面很多时,占用资源多.
 *
 * 应该将它优化成翻来覆去用那么几个View,它固定同时使用3个,这意味着一共可能有4个View,有一个View应该会作为互换时的Temp
 *
 * */

/**
 * 最多4个View翻来覆去用的PageAdapter,因为复用的原因,View的布局必须相同,就像BaseAdapter一样的道理.
 * */
public abstract class AdapterViewPage extends PagerAdapter {

	private static final int LENGTH = 4;
	private View[] views = new View[LENGTH];

	// view即viewPage,如果不是viewPage就报错好了.将View放到ViewPage中
	@Override
	public Object instantiateItem(View view, int position) {
		System.out.println("instantiateItem=" + position);

		int index = position % LENGTH;
		View subView = views[index];
		subView = getView(subView, position, (ViewGroup) view);
		views[index] = subView;
		((ViewPager) view).addView(subView, 0);
		return subView;
	}

	protected abstract View getView(View view, int position, ViewGroup parent);

	// 将View从ViewPage中移除.
	// 实际上ViewPage一共只需要3页即3个View就能达到效果(左中右),当翻到第4页时,第1页的View就可以移除了.
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		int index = position % LENGTH;
		container.removeView(views[index]);
	}

	/** 根据position来获取View,因为一共只有4个View,真正获取到的是position%4的那个View */
	public View getViewInPosition(int position) {
		return views[position % LENGTH];
	}

	// 这是防止PagerAdapter调用notifyDataSetChanged无效的问题.
	// PagerAdapter的notifyDataSetChanged调用后并不更新当前三页.
	// http://www.cnblogs.com/maoyu417/p/3740209.html
	private int mChildCount = 0;

	@Override
	public void notifyDataSetChanged() {
		mChildCount = getCount();
		super.notifyDataSetChanged();
	}

	@Override
	public int getItemPosition(Object object) {
		if (mChildCount > 0) {
			mChildCount--;
			return POSITION_NONE;
		}
		return super.getItemPosition(object);
	}

}
