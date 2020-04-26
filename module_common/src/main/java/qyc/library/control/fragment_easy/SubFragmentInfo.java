package qyc.library.control.fragment_easy;

import android.graphics.Color;
import android.support.v4.app.Fragment;

public class SubFragmentInfo {

	public SubFragmentInfo(Fragment fragment, String title) {
		this.title = title;
		this.fragment = fragment;
	}

	public Fragment fragment = null;
	public String title;

	/** 未选中时的标题颜色,默认白色 */
	int titleColorUnSelect = Color.BLACK;
	public SubFragmentInfo titleColorUnSelect(int color)
	{
		this.titleColorUnSelect=color;
		return this;
	}

	/** 选中时的标题颜色,默认蓝色 */
	int titleColorSelect = Color.BLUE;
	public SubFragmentInfo titleColorSelect(int color)
	{
		this.titleColorSelect=color;
		return this;
	}
}
