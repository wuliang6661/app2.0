package qyc.library.control.spinner_edit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import synway.common.R;


public class SpinnerEdit extends RelativeLayout {
	private ArrayList<SpinnerEditItem> items = null;
	/**
	 * 当前选择项，如果?也没有，那就是null?
	 */
	private View clickPanel = null;
	private TextView mtxt = null;
	private EditText medt = null;
	private ImageButton mbtn = null;
	private PopupWindow popw = null;// 历史输入记录的popupwindow
	private View popView = null;// popw的view
	private ListView mlistView = null;
	private ArrayAdapter<String> mAdapter = null;// popw的listview的?配器
	private OnSpinnerEditClick spinnerEditClickListener = null;
	private OnSpinnerEditLongClick onSpinnerEditLongClick = null;
	private boolean needRefush = false;
	private boolean editAble = true;// 用来标记当前是否可编辑,控件默认为可编辑

	public SpinnerEditItem selectItem = null;

	public SpinnerEdit(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 初始化选项
		items = new ArrayList<SpinnerEditItem>();

		if (isInEditMode()) {
			inEditMode(context, attrs);
		} else {
			init(context, attrs);
		}
	}

	private void init(Context context, AttributeSet attrs) {
		// 获取背景layout
		View contentView = LayoutInflater.from(context).inflate(R.layout.lib_spinneredit_cv, this, true);
		mtxt = contentView.findViewById(R.id.lib_textView1);
		clickPanel = contentView.findViewById(R.id.lib_relativeLayout1);
		medt = contentView.findViewById(R.id.lib_editText1);
		mbtn = contentView.findViewById(R.id.lib_imageButton1);
		medt.setOnLongClickListener(onLongClickListener);

		setResValue(attrs);
	}

	private void inEditMode(Context context, AttributeSet attrs) {
		// 在可视化编辑器里面显示的内容,用于开发时模拟显示,不会影响程序性能
		try {
			init(context, attrs);
		}
		catch (Exception e) {
			setBackgroundColor(Color.argb(0, 0, 0, 0));
			EditText edt = new EditText(getContext());
			edt.setText("SpinnerEdit");
			edt.setTextColor(Color.BLUE);
			edt.setBackgroundColor(Color.argb(0, 0, 0, 0));
			addView(edt);
		}
	}

	/** 根据自定义属性,对控件样式进行调整 */
	private void setResValue(AttributeSet attrs) {
		String labelText = null;
		String editText = null;
		String hintText = null;
		int editColor = -1;
		int inputType = -1;
		float labelTextSize = -1;
		boolean editAble = false;
		int paddingLeft = -1;
		int paddingRight = -1;
		int paddingTop = -1;
		int paddingBottom = -1;

		// 获取控件自定义属性值
		TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SpinnerEdit);
		int N = a.getIndexCount();// 自定义属性被使用的数量
		for (int i = 0; i < N; i++) {
			int attr = a.getIndex(i);

			if (attr == R.styleable.SpinnerEdit_labelText) {
				labelText = a.getString(attr);
			} else if (attr == R.styleable.SpinnerEdit_editText) {
				editText = a.getString(attr);
			} else if (attr == R.styleable.SpinnerEdit_editHint) {
				hintText = a.getString(attr);
			} else if (attr == R.styleable.SpinnerEdit_inputType) {
				inputType = a.getInt(attr, -1);
			} else if (attr == R.styleable.SpinnerEdit_editAble) {
				editAble = a.getBoolean(attr, true);// 说明默认是可编辑的
			} else if (attr == R.styleable.SpinnerEdit_labelTextSize) {
				labelTextSize = a.getDimension(attr, -1);
			} else if (attr == R.styleable.SpinnerEdit_panelPadding) {
				int padding = (int) a.getDimension(attr, -1);
				if (padding > 0) {
					if (paddingLeft == -1) {
						paddingLeft = padding;
					}
					if (paddingRight == -1) {
						paddingRight = padding;
					}
					if (paddingTop == -1) {
						paddingTop = padding;
					}
					if (paddingBottom == -1) {
						paddingBottom = padding;
					}
				}
			} else if (attr == R.styleable.SpinnerEdit_panelPaddingLeft) {
				paddingLeft = (int) a.getDimension(attr, paddingLeft);
			} else if (attr == R.styleable.SpinnerEdit_panelPaddingRight) {
				paddingRight = (int) a.getDimension(attr, paddingRight);
			} else if (attr == R.styleable.SpinnerEdit_panelPaddingTop) {
				paddingTop = (int) a.getDimension(attr, paddingTop);
			} else if (attr == R.styleable.SpinnerEdit_panelPaddingBottom) {
				paddingBottom = (int) a.getDimension(attr, paddingBottom);
			} else if (attr == R.styleable.SpinnerEdit_editColor) {
				editColor = a.getColor(attr, editColor);
			}
		}
		a.recycle();

		if (paddingLeft == -1) {
			paddingLeft = getPaddingLeft();
		}
		if (paddingRight == -1) {
			paddingRight = getPaddingRight();
		}
		if (paddingTop == -1) {
			paddingTop = getPaddingTop();
		}
		if (paddingBottom == -1) {
			paddingBottom = getPaddingBottom();
		}

		this.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

		if (labelText != null) {
			mtxt.setText(labelText);
		}

		if (labelTextSize != -1) {
			mtxt.setTextSize(TypedValue.COMPLEX_UNIT_PX, labelTextSize);
		}

		if (editColor != -1) {
			medt.setTextColor(editColor);
		}

		setEditable(editAble);
		if (editAble) {
			medt.setText(editText);
			medt.setHint(hintText);
			if (inputType >= 0) {
				medt.setInputType(inputType);
			}
		}
	}

	/**
	 * 设置额外的下拉?项?中监?
	 * 
	 * @param itemclickListener
	 */
	public void setOnItemClickListener(OnSpinnerEditClick itemclickListener) {
		this.spinnerEditClickListener = itemclickListener;
	}

	public void removeItemClickListener() {
		this.spinnerEditClickListener = null;
	}

	/** 设置SpinnerEdit长按监听 */
	public void setOnSpinnerEditLongClick(OnSpinnerEditLongClick onSpinnerEditLongClick) {
		this.onSpinnerEditLongClick = onSpinnerEditLongClick;
	}

	/** 取消SpinnerEdit长按监听 */
	public void removeSpinnerEditLongClick() {
		this.onSpinnerEditLongClick = null;
	}

	public TextView getMtxt() {
		return mtxt;
	}

	public EditText getMedt() {
		return medt;
	}

	public ImageButton getMbtn() {
		return mbtn;
	}

	/**
	 * 设置输入框是否为可编辑，不可编辑点击了会触发下拉
	 * 
	 * @param isEdit
	 *            true表示可以编辑
	 */
	public void setEditable(boolean isEdit) {
		this.editAble = isEdit;
		if (isEdit) {
			clickPanel.setOnClickListener(null);
			medt.setOnClickListener(null);
			mbtn.setOnClickListener(null);
			medt.setFocusable(true);
			mbtn.setVisibility(View.INVISIBLE);

		} else {
			clickPanel.setOnClickListener(listener);
			medt.setOnClickListener(listener);
			mbtn.setOnClickListener(listener);

			medt.setFocusable(false);
			mbtn.setVisibility(View.VISIBLE);

		}
	}

	/** 设置是否可用,不可用的情况下既不能点也不能输入 */
	public void setEnabled(boolean enabled) {
		if (enabled) {
			setEditable(editAble);// 可用之后直接通过setEditable恢复可编辑,或不可编辑状态.
		} else {
			clickPanel.setOnClickListener(null);
			medt.setOnClickListener(null);
			mbtn.setOnClickListener(null);

			medt.setFocusable(false);
			mbtn.setVisibility(View.INVISIBLE);
		}
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (items.size() > 0) {
				setPopw();
				popw.showAsDropDown(v, -10, -10);
			} else {
				if (spinnerEditClickListener != null) {
					spinnerEditClickListener.noItemtoClick(SpinnerEdit.this);
				}
			}
		}

	};

	@SuppressWarnings("deprecation")
	private void setPopw() {
		if (this.popView == null) {
			popView = View.inflate(getContext(), R.layout.lib_spinneredit_popview, null);
		}
		if (popw == null) {
			popw = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			popw.setBackgroundDrawable(new BitmapDrawable());
			popw.setFocusable(true);
			popw.setOutsideTouchable(true);
			popw.update();

		}
		if (mlistView == null) {
			mlistView = popView.findViewById(R.id.listView1);
			mlistView.setId(-1);
			mlistView.setItemsCanFocus(true);
			mlistView.setOnItemClickListener(onItemClickListener);
		}

		bindData = getStrings(items);
		if (mAdapter == null || needRefush) {
			mAdapter = new ArrayAdapter<String>(getContext(), R.layout.lib_spinneredit_listitem, R.id.textView1,
					bindData);
		}
		mlistView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
		needRefush = false;
	}

	private ArrayList<String> bindData;

	public void setData(ArrayList<SpinnerEditItem> items) {
		if (items == null) {
			this.items.clear();
		} else {
			this.items.clear();
			this.items.addAll(items);
		}
		needRefush = true;
	}

	private ArrayList<String> getStrings(ArrayList<SpinnerEditItem> items) {
		ArrayList<String> ss = new ArrayList<String>();
		for (SpinnerEditItem s : items) {
			ss.add(s.text);
		}
		return ss;
	}

	public void addItems(ArrayList<SpinnerEditItem> items) {
		this.items.addAll(items);
	}

	public void addItem(SpinnerEditItem item) {
		this.items.add(item);
	}

	/**
	 * 手动选中了某?
	 * 
	 * @param position
	 */
	public boolean select(int position) {
		// System.out.println("select");
		if (items.size() > position && position >= 0) {
			medt.setText(items.get(position).text);
			selectItem = items.get(position);
			if (spinnerEditClickListener != null) {
				spinnerEditClickListener.onClick(this, position, items.get(position).text);
			}
			return true;
		} else {
			selectItem = null;
			medt.setText("");
			return false;
		}
	}

	/**
	 * 手动选中了某?
	 * 
	 * @param item
	 */
	public boolean select(SpinnerEditItem item) {
		int index = items.indexOf(item);
		return select(index);
	}

	/**
	 * 选中?,多项模糊匹配选中??
	 * 
	 * @param info
	 *            内容
	 * @param isFuzzy
	 *            是否模糊匹配
	 */
	public void select(String info, boolean isFuzzy) {
		int position = -1;
		int size = items.size();
		if (isFuzzy) {
			for (int i = 0; i < size; i++) {
				String str = items.get(i).text;
				if (str.matches("^.*" + info + ".*")) {
					position = i;
					select(position);
					break;
				}
			}
		} else {
			for (int i = 0; i < size; i++) {
				String str = items.get(i).text;
				if (str.equals(info)) {
					position = i;
					select(position);
					break;
				}
			}
		}
	}

	/**
	 * 最完美的选择方法,完全由你自己决定怎么选择!
	 * 
	 * @param selectSimpleListItem
	 *            选择接口
	 */
	public boolean select(OnSpinnerEditItemSelect spinnerEditItem) {
		int size = items.size();
		boolean result = false;
		int index = -1;
		for (int i = 0; i < size; i++) {
			result = spinnerEditItem.onSpinnerEditItemSelect(items.get(i));
			if (result) {
				index = i;
				break;
			}
		}
		return select(index);
	}

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// TODO Auto-generated method stub
			medt.setText(items.get(position).text);
			selectItem = items.get(position);
			if (spinnerEditClickListener != null) {
				spinnerEditClickListener.onClick(SpinnerEdit.this, position, items.get(position).text);
			}
			popw.dismiss();
		}
	};

	private OnLongClickListener onLongClickListener = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			if (v.equals(medt)) {
				if (onSpinnerEditLongClick != null) {
					onSpinnerEditLongClick.onSpinnerEditLongClick(SpinnerEdit.this);
				}

			}
			return false;
		}
	};

	/**
	 * 获取控件内部存储的数据集
	 * 
	 * @return
	 */
	public ArrayList<SpinnerEditItem> getMemoryData() {
		return items;
	}

	public interface OnSpinnerEditClick {
		/**
		 * 对外的下拉?中框监听
		 * 
		 * @param position
		 *            选中第几项，??
		 * @param itemTxt
		 *            选中项的文本内容
		 */
        void onClick(SpinnerEdit view, int position, String itemTxt);

		/**
		 * 没有任何item时，触发的接?
		 */
        void noItemtoClick(SpinnerEdit view);
	}

	/** 选择查询接口 */
	public interface OnSpinnerEditItemSelect {
		/** 如果是你需要的那一项你就返回ture,谢谢 */
        boolean onSpinnerEditItemSelect(SpinnerEditItem item);
	}

	public interface OnSpinnerEditLongClick {
		/** 当SpinnerEdit被长按 */
        void onSpinnerEditLongClick(SpinnerEdit se);
	}

}