package qyc.library.control.fragment_easy;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

import synway.common.R;


public abstract class FragmentE extends FragmentActivity {

	/**
	 * 翻页控件
	 * */
	private ViewPager vPager = null;

	/** 页面适配器 */
	private MFragmentAdapter fragmentAdapter = null;
	/*
	 * 选项卡标题
	 */
	private View cursor = null;
	private LinearLayout linearLayout = null;
	private View bkView = null;

	/*
	 * 传递数据用
	 */
	public Bundle temp = null;
	/*
	 * 地址ID和名字对应表
	 */
	public HashMap<Integer, String> addressID_Name = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.lib_fragmenteasy_cv);

		// 页面适配器
		fragmentAdapter = new MFragmentAdapter(getSupportFragmentManager());

		// 选项卡
		cursor = findViewById(R.id.textView1);
		bkView = findViewById(R.id.linearLayout1);
		linearLayout = findViewById(R.id.linearLayout2);
		// 选项卡背景色
		int titleBkRes = titleBkRes();
		if (titleBkRes == -1) {
			bkView.setBackgroundColor(titileBkColor());
		} else {
			bkView.setBackgroundResource(titleBkRes);
		}

		// 提取SubFragmentInfo,将Fragment放进适配器,将标签信息形成顶部标签.
		SubFragmentInfo[] subFragments = setFragmentInfo();
		LayoutInflater inflater = LayoutInflater.from(this);
		for (int i = 0; i < subFragments.length; i++) {
			SubFragmentInfo sfi = subFragments[i];
			// 将fragment放进适配器
			fragmentAdapter.addItem(sfi.fragment);

			// 设置每一个选项卡,图片+文字
			// 这行已经指定了linearLayout为父布局,因此lib_fragmenteasy_title已经成为linearLayout的一个直系子布局,所以后面addView的时候不再需要加入LayoutParams参数,因为lib_fragmenteasy_title最外层布局参数起效果了..另外,false的意思是,lib_fragmenteasy_title目前还是一个独立的view,它虽然即将加入linearLayout,,但目前还没有加入,返回的view是lib_fragmenteasy_title自己.如果设为true,表示现在直接就加入了,返回的view就是linearLayout.
			View view = inflater.inflate(R.layout.lib_fragmenteasy_title, linearLayout, false);
			view.setOnClickListener(onClickListener);
			// 文字你先来
			TextView txt = view.findViewById(R.id.textView1);
			txt.setText(sfi.title);
			txt.setTag(new int[]{sfi.titleColorSelect, sfi.titleColorUnSelect});
			view.setTag(txt);
			// 然后是图片
			// 算了图片先不要了

			// 加入选项卡layout
			linearLayout.addView(view);
		}

		// 翻页控件
		vPager = findViewById(R.id.viewPager1);
		vPager.setOnPageChangeListener(onPageChangeListener);
		vPager.setAdapter(fragmentAdapter);

		// 得到选项卡滚动条的宽度
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels / linearLayout.getChildCount();
		cursor.setLayoutParams(new LinearLayout.LayoutParams(width, dip2px(3)));

	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int index = linearLayout.indexOfChild(v);
			vPager.setCurrentItem(index);
		}
	};

	// 翻页控件的页面翻动监听
	private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int index) {
			Fragment fragment = fragmentAdapter.getItem(index);
			if (fragment instanceof FragmentQyc) {
				((FragmentQyc) fragment).onSelect();
			}

			int count = linearLayout.getChildCount();
			for (int i = 0; i < count; i++) {
				View view = linearLayout.getChildAt(i);
				if (i == index) {
					setChecked((TextView) view.getTag());
				} else {
					setUnChecked((TextView) view.getTag());
				}
			}
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			int x = (int) ((position + positionOffset) * cursor.getWidth());
			((View) cursor.getParent()).scrollTo(-x, cursor.getScrollY());
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
		}
	};

	/** 设置Fragment信息 */
	protected abstract SubFragmentInfo[] setFragmentInfo();

	/** 设置顶部标签背景颜色 */
	protected int titileBkColor() {
		return Color.WHITE;
	}

	/** 设置顶部标签背景图片 */
	protected int titleBkRes() {
		return -1;
	}

	/** 跳转 */
	protected void setCurrentItem(int item, boolean smoothScroll) {
		vPager.setCurrentItem(item, smoothScroll);
	}

	/** 跳转 */
	protected void setCurrentItem(int item) {
		vPager.setCurrentItem(item);
	}

	private void setChecked(TextView txt) {
		int[] color = (int[]) txt.getTag();
		txt.setTextColor(color[0]);
	}

	private void setUnChecked(TextView txt) {
		int[] color = (int[]) txt.getTag();
		txt.setTextColor(color[1]);
	}

	private int dip2px(float dipValue) {
		final float scale = getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

}
