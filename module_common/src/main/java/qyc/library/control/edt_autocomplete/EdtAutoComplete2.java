package qyc.library.control.edt_autocomplete;//package qyc.library.control.edt_autocomplete;
//
//import java.util.ArrayList;
//
//import qyc.library.R;
//import android.content.Context;
//import android.graphics.drawable.BitmapDrawable;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.AttributeSet;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.ArrayAdapter;
//import android.widget.AutoCompleteTextView;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.PopupWindow;
//
///**
// * 自定义的AutoCompleteTextView 初始化必须设定界面的父布局
// * 
// * @author hz
// * 
// */
//public class EdtAutoComplete2 extends AutoCompleteTextView {
//
//	// 下拉列表适配器
//	private  mAdapter = null;
//
//	// 界面悬浮窗,这里使用的是"下拉悬浮"
//	private PopupWindow pWindow = null;
//	// 用来控制悬浮窗里的控件只初始化一次,因为构造函数里得不到控件尺寸.
//	private boolean isInit = false;
//
//	// 对调用者的文本改变监听接口
//	private TextWatcher textWatcher = null;
//
//	public EdtAutoComplete2(Context context, AttributeSet attrs) {
//		super(context, attrs);
//		mAdapter = new TheAdapter(context);
//		// 添加文本改变监听,本函数已经改写了此方法给调用者,因此控件自身要直接调用父类
//		super.addTextChangedListener(watcher);
//	}
//
//	
//	
//	/**
//	 * 设置下拉内容的文字库
//	 * 
//	 * @param list
//	 */
//	public void setMemoryData(ArrayList<String> list) {
//		mAdapter.set(list);
//		mAdapter.notifyDataSetChanged();
//	}
//
//	// /**
//	// * 添加下拉内容的文字库
//	// *
//	// * @param list
//	// */
//	// public void addMemoryData(String str) {
//	// mAdapter.add(str);
//	// mAdapter.notifyDataSetChanged();
//	// }
//
//	/**
//	 * 增加文本改变监听
//	 * */
//	@Override
//	public void addTextChangedListener(TextWatcher watcher) {
//		this.textWatcher = watcher;
//	}
//
//	private TextWatcher watcher = new TextWatcher() {
//
//		@Override
//		public void onTextChanged(CharSequence s, int start, int before, int count) {
//			// TODO Auto-generated method stub
//			initPWindow();
//			// mAdapter.getFilter().filter(s);
//			System.out.println(s + ":" + mAdapter.getCount());
//			if (mAdapter.getCount() > 0) {
//				mAdapter.notifyDataSetChanged();
//				if (!pWindow.isShowing()) {
//					pWindow.showAsDropDown(EdtAutoComplete2.this, 0, 0);
//					//requestFocus();
//				}
//			} else {
//				pWindow.dismiss();
//			}
//
//			if (textWatcher != null) {
//				textWatcher.onTextChanged(s, start, before, count);
//			}
//		}
//
//		@Override
//		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//			if (textWatcher != null) {
//				textWatcher.beforeTextChanged(s, start, count, after);
//			}
//		}
//
//		@Override
//		public void afterTextChanged(Editable s) {
//			if (textWatcher != null) {
//				textWatcher.afterTextChanged(s);
//			}
//		}
//	};
//
//	// 初始化悬浮窗,以及里面的控件
//	@SuppressWarnings("deprecation")
//	private void initPWindow() {
//		if (isInit) {
//			return;
//		}
//
//		View pwView = View.inflate(getContext(), R.layout.lib_edtautocomplete_popview, null);
//		ListView mlistView = (ListView) pwView.findViewById(R.id.listView1);
//		mlistView.setAdapter(mAdapter);
//		mlistView.setId(-1);
//		mlistView.setOnItemClickListener(onItemClickListener);
//
//		pWindow = new PopupWindow(pwView,getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
////		pWindow.setBackgroundDrawable(new BitmapDrawable());
//		pWindow.setFocusable(false);
////		pWindow.setOutsideTouchable(true);
//		pWindow.update();
//
//		isInit = true;
//	}
//
//	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
//
//		@Override
//		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//			// TODO Auto-generated method stub
//			
//			setText((String) mAdapter.getItem(position));
//			pWindow.setFocusable(false);
//			pWindow.dismiss();
//			
//		}
//	};
//
//}
