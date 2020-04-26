package qyc.library.control.adapter_tree;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import synway.common.R;


/**
 * 树数据源构造器
 * 
 * @author 钱园超
 */
public class AdapterTree extends BaseAdapter {

	private LayoutInflater layoutInflater;
	private ArrayList<SimpleNode> rootNodes = null;// 存放根节点
	private ArrayList<SimpleNode> showNodes = null;
	private boolean autoCheckBox = true;

	/**
	 * TreeAdapter构造函数
	 * 
	 * @param context
	 * @param autoCheckBox
	 *            根节点
	 */
	public AdapterTree(Context context, ArrayList<SimpleNode> rootNodeList, boolean autoCheckBox) {
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.showNodes = new ArrayList<SimpleNode>();
		this.rootNodes = rootNodeList;
		this.autoCheckBox = autoCheckBox;

		synShowNodes();
	}

	public AdapterTree(Context context, SimpleNode rootNode, boolean autoCheckBox) {
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.showNodes = new ArrayList<SimpleNode>();
		this.rootNodes = new ArrayList<SimpleNode>();
		this.autoCheckBox = autoCheckBox;
		this.rootNodes.add(rootNode);
		synShowNodes();
	}

	public AdapterTree(Context context, boolean autoCheckBox) {
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.showNodes = new ArrayList<SimpleNode>();
		this.rootNodes = new ArrayList<SimpleNode>();
		this.autoCheckBox = autoCheckBox;
	}

	/** 取消当前所有被选中的node的选中状态 */
	public void clearSelectedNodes() {
		clearSelectedNodes(rootNodes);
	}

	/**
	 * 获取被checkbox选中的node
	 */
	public ArrayList<SimpleNode> getSeletedNodes() {
		ArrayList<SimpleNode> selectNodes = new ArrayList<SimpleNode>();
		getSeletedNodes(rootNodes, selectNodes);
		return selectNodes;
	}

	/** 获取所有根节点node */
	public ArrayList<SimpleNode> getRootNodes() {
		return rootNodes;
	}

	/** 根据所在行来获取node */
	public SimpleNode getItemNode(int position) {
		return showNodes.get(position);
	}

	/** 加入一个根节点 */
	public void addRootNode(SimpleNode rootNode) {
		if (rootNode.isRoot()) {
			rootNodes.add(0, rootNode);// 倒着加进来
		}
	}

	/** 重置根节点 */
	public void resetRootNode(ArrayList<SimpleNode> rootNodes) {
		// 倒一下
		Collections.reverse(rootNodes);
		this.rootNodes = rootNodes;
		this.synShowNodes();
	}

	/** 重置根节点 */
	public void resetRootNode(SimpleNode rootNode) {
		this.rootNodes.clear();
		this.rootNodes.add(rootNode);
		this.synShowNodes();
	}

	/**
	 * <p>
	 * 描述:清除所有
	 * <p>
	 * 异常:无 void
	 */
	public void clear() {
		rootNodes.clear();
		showNodes.clear();
	}

	/**
	 * 展开或收缩节点
	 */
	public void expandOrCollapse(SimpleNode n) {
		int childSize = n.children.size();
		if (childSize > 0) {// 具有子节点
			if (n.isExpanded)// 已展开,做收缩操作
			{
				n.isExpanded = false;// 设置收缩标记
				// 将子节点从要显示的列表中删去
				for (int i = 0; i < childSize; i++) {
					deleteFormShowNodes(n.children.get(i));
				}
			} else// 已关闭,做展开操作
			{
				n.isExpanded = true;// 设置展开标记
				// 将子节点添加回要显示的列表
				for (int i = 0; i < childSize; i++) {
					addToShowNodes(n.children.get(i));
				}
			}
			// notifyDataSetChanged();// 刷新
		}
	}

	/** 根据根节点,去同步需要显示的节点 */
	private void synShowNodes() {
		showNodes.clear();
		for (int i = 0; i < rootNodes.size(); i++) {
			resetShowNodes(rootNodes.get(i));
		}
	}

	private void resetShowNodes(SimpleNode node) {
		// 是否显示自身
		int index = showNodes.indexOf(node.parent) + 1;
		showNodes.add(index, node);
		// 是否显示子节点
		if (node.isExpanded) {
			// 加入子节点
			int childrenSize = node.children.size();
			SimpleNode childNode = null;
			for (int i = 0; i < childrenSize; i++) {
				childNode = node.children.get(i);
				resetShowNodes(childNode);
			}
		}

	}

	// 复选框联动
	// 原则是该项选中后,所有子项都选中,父项也选中
	// 该项未选中后,所有子项都取消选中,同级项若都未选中,则父项也取消选中
	private void autoCheck(SimpleNode node, boolean isChecked) {
		autoCheckChild(node.children, isChecked);
		autoCheckParent(node, isChecked);
	}

	// 自动选中子节点
	private void autoCheckChild(ArrayList<SimpleNode> childNodes, boolean isChecked) {
		int childCount = childNodes.size();
		SimpleNode childNode = null;
		for (int i = 0; i < childCount; i++) {
			childNode = childNodes.get(i);
			childNode.isChecked = isChecked;
			autoCheckChild(childNode.children, isChecked);
		}
	}

	// 自动选中父节点
	private void autoCheckParent(SimpleNode node, boolean isChecked) {
		// 选中父节点
		SimpleNode parentNode = node.parent;
		if (isChecked) {
			while (parentNode != null) {
				parentNode.isChecked = true;
				parentNode = parentNode.parent;
			}
		} else {
			while (parentNode != null) {
				boolean isChildCheck = false;
				// 获取所有同级节点的点击状态
				int childCount = parentNode.children.size();
				for (int i = 0; i < childCount; i++) {
					isChildCheck = parentNode.children.get(i).isChecked;
					if (isChildCheck) {
						break;
					}
				}
				// 所有同级节点都没有选中,父节点也取消选中
				if (isChildCheck == false) {
					parentNode.isChecked = false;
					parentNode = parentNode.parent;
				} else {
					break;
				}
			}
		}
	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			CheckBox cbx = (CheckBox) v;
			SimpleNode node = (SimpleNode) cbx.getTag();// 获取数据
			node.isChecked = cbx.isChecked();
			if (autoCheckBox) {
				autoCheck(node, cbx.isChecked());// 自动选中
				notifyDataSetChanged();// 刷新
			}
		}
	};

	/** 清除所有已被选中借点的选中状态 */
	private void clearSelectedNodes(ArrayList<SimpleNode> nodes) {
		int rootNodesSize = nodes.size();
		SimpleNode node = null;
		for (int i = 0; i < rootNodesSize; i++) {
			node = nodes.get(i);
			node.isChecked = false;
			clearSelectedNodes(node.children);
		}
	}

	/**
	 * 获得选中节点
	 * 
	 * @return
	 */
	private void getSeletedNodes(ArrayList<SimpleNode> nodes, ArrayList<SimpleNode> selectNodes) {
		int rootNodeSize = nodes.size();
		SimpleNode node = null;
		for (int i = 0; i < rootNodeSize; i++) {
			node = nodes.get(i);
			if (node.isChecked) {
				selectNodes.add(node);
			}
			getSeletedNodes(node.children, selectNodes);
		}
	}

	// 加入到需要显示的列表中
	private void addToShowNodes(SimpleNode node) {
		this.showNodes.add(showNodes.indexOf(node.parent) + 1, node);
		int childSize = node.children.size();
		if (childSize > 0 && node.isExpanded)// 处于展开状态并且有子节点,显示其子节点
		{
			for (int i = 0; i < childSize; i++) {
				addToShowNodes(node.children.get(i));
			}
		}
	}

	// 设置控件显示状态
	private void setViewItems(ViewItems viewItems, SimpleNode node, View view) {
		// 是否显示复选框
		if (node.hasCheckBox) {
			viewItems.chbSelect.setVisibility(View.VISIBLE);// 显示
			if (viewItems.chbSelect.getTag() == null) {
				// 如果是第一次使用,则设置事件监听
				viewItems.chbSelect.setOnClickListener(onClickListener);
			}
			// 设置点击状态
			viewItems.chbSelect.setChecked(node.isChecked);
			// 绑定node
			viewItems.chbSelect.setTag(node);
		} else {
			viewItems.chbSelect.setVisibility(View.GONE);
		}

		// 是否显示图标
		if (node.icon == -1) {
			viewItems.ivIcon.setVisibility(View.GONE);
		} else {
			viewItems.ivIcon.setVisibility(View.VISIBLE);
			viewItems.ivIcon.setBackgroundResource(node.icon);
			viewItems.ivIcon.setImageResource(node.icon);
		}
		// 显示文本
		viewItems.tvText.setText(node.text);
		// 设置文件颜色
		viewItems.tvText.setTextColor(node.textColor);

		if (node.isLeaf()) {
			// 是叶节点 不显示展开和折叠状态图标
			viewItems.ivExEc.setVisibility(View.GONE);
			// 再看是否要显示加载
			if (node.isLoading) {
				viewItems.progressBar.setVisibility(View.VISIBLE);
			} else {
				viewItems.progressBar.setVisibility(View.GONE);
			}
		} else {
			// 单击时控制子节点展开和折叠,状态图标改变
			viewItems.ivExEc.setVisibility(View.VISIBLE);
			viewItems.progressBar.setVisibility(View.GONE);
			if (node.isExpanded) {
				viewItems.ivExEc.setImageResource(R.drawable.lib_adaptertree_ec_png);
			} else {
				viewItems.ivExEc.setImageResource(R.drawable.lib_adaptertree_ex_png);
			}
		}

		// 控制缩进
		if (node.getLevel() < 1) {
			view.setPadding(node.getLevel(), 3, 3, 3);
		} else {
			view.setPadding(30 * node.getLevel(), 3, 3, 3);
		}
	}

	// 从显示的列表中删除
	private void deleteFormShowNodes(SimpleNode node) {
		this.showNodes.remove(node);
		int childSize = node.children.size();
		if (childSize > 0 && node.isExpanded)// 处于展开状态并且有子节点
		{
			for (int i = 0; i < childSize; i++) {
				deleteFormShowNodes(node.children.get(i));
			}
		}
	}

	@Override
	public void notifyDataSetChanged() {
		synShowNodes();
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return showNodes.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return showNodes.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewItems viewItems = null;
		if (view == null) {
			// 从布局中获取控件
			view = layoutInflater.inflate(R.layout.lib_adaptertree_listitem, null);
			viewItems = getViewItems(view);
			// 获取数据
			SimpleNode node = showNodes.get(position);
			// 设置控件
			setViewItems(viewItems, node, view);
			view.setTag(viewItems);
		} else {
			// 从tag中重用控件
			viewItems = (ViewItems) view.getTag();
			// 获取数据
			SimpleNode node = showNodes.get(position);
			// 设置控件
			setViewItems(viewItems, node, view);
		}
		return view;
	}

	/**
	 * 
	 * 列表项控件集合
	 * 
	 */
	private class ViewItems {
		CheckBox chbSelect;// 选中与否
		ImageView ivIcon;// 图标
		TextView tvText;// 文本〉〉〉
		ImageView ivExEc;// 展开或折叠标记">"或"v"
		ProgressBar progressBar;// 等待条条~
	}

	/** 获取布局文件中的相关控件给ViewItems对象 */
	private ViewItems getViewItems(View convertView) {
		// 文本
		ViewItems vi = new ViewItems();
		vi.chbSelect = convertView.findViewById(R.id.checkBox1);
		vi.ivIcon = convertView.findViewById(R.id.imageView2);
		vi.tvText = convertView.findViewById(R.id.textView1);
		vi.ivExEc = convertView.findViewById(R.id.imageView1);
		vi.progressBar = convertView.findViewById(R.id.progressBar1);
		return vi;
	}
}
