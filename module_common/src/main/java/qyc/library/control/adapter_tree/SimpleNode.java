package qyc.library.control.adapter_tree;

import android.graphics.Color;

import java.util.ArrayList;

/**
 * 树节点
 * 
 * @author LongShao
 * 
 */

/**
 * @author Administrator
 * 
 */
public class SimpleNode {
	/**
	 * 父节点
	 */
	SimpleNode parent = null;
	/**
	 * 子节点
	 */
	ArrayList<SimpleNode> children = new ArrayList<SimpleNode>();
	/**
	 * 节点显示的文字
	 */
	public String text;
	/**
	 * 文字颜色,默认黑色
	 */
	public int textColor = Color.BLACK;
	/**
	 * 节点携带的值
	 */
	public Object value;
	/**
	 * 节点携带的int值
	 */
	public int integer = 0;
	/**
	 * 节点携带的bool值
	 */
	public boolean bool = false;
	/**
	 * 图标在R中的ID,-1表示不要图标
	 */
	public int icon = -1;
	/**
	 * 是否有复选框
	 */
	public boolean hasCheckBox = false;
	/**
	 * 是否处于选中状态
	 */
	boolean isChecked = false;
	/**
	 * 是否处于展开状态,决定子节点是否显示以右侧展开收缩标签的显示状态
	 */
	boolean isExpanded = false;

	/** 展开等级,根节点为0级,后面子节点依次递减 */
	int expandLevel = 0;
	/** 是否正在加载 */
	boolean isLoading = false;

	/**
	 * Node构造函数
	 * 
	 * @param text
	 *            节点显示的文字
	 * @param value
	 *            节点的值
	 */
	public SimpleNode(String text, Object value) {
		this.text = text;
		this.value = value;
	}

	/**
	 * Node构造函数
	 * 
	 * @param text
	 *            节点显示的文字
	 * @param value
	 *            节点的值
	 */
	public SimpleNode(String text, Object value, boolean hasCheckBox) {
		this.text = text;
		this.value = value;
		this.hasCheckBox = hasCheckBox;
	}

	/** 是否选中 */
	public boolean isChecked() {
		return isChecked;
	}

	/** 该方法不会"autocheck" */
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	/**
	 * Node构造函数
	 * 
	 * @param text
	 *            节点显示的文字
	 * @param value
	 *            节点的值
	 */
	public SimpleNode(String text, Object value, boolean hasCheckBox, int icon) {
		this.text = text;
		this.value = value;
		this.hasCheckBox = hasCheckBox;
		this.icon = icon;
	}

	/**
	 * 是否根节点
	 * 
	 * @return
	 */
	boolean isRoot() {
		return parent == null;
	}

	/** 是否已展开 */
	public boolean isExpanded() {
		return isExpanded;
	}

	// boolean isShowSelf() {
	// if (parent == null) {
	// return true;
	// } else {
	// return isParentExpanded();
	// }
	// }

	// /**
	// * 父节点是否处于展开状态
	// *
	// * @return
	// */
	// private boolean isParentExpanded() {
	// if (parent == null) {
	// return isExpanded;
	// } else {
	// if (parent.isExpanded)// 父节点展开
	// {
	// return parent.isParentExpanded();// 继续返回上层父节点
	// } else// 父节点收缩
	// {
	// return false;
	// }
	// }
	// }

	/**
	 * 获得节点的级数,根节点为0
	 * 
	 * @return
	 */
	public int getLevel() {
		return expandLevel;
	}

	/**
	 * 是否叶节点,即没有子节点的节点
	 * 
	 * @return
	 */
	public boolean isLeaf() {
        return children.size() <= 0;
	}

	/** 获取父节点 */
	public SimpleNode getParent() {
		return this.parent;
	}

	/** 获取子节点 */
	public ArrayList<SimpleNode> getChildren() {
		return this.children;
	}

	/**
	 * 设置父节点
	 * 
	 * @param node
	 */
	public void setParent(SimpleNode node) {
		if (node != null) {
			node.children.add(0, this);// 倒着加进来
			this.expandLevel = node.expandLevel + 1;
		}
		this.parent = node;
	}

	/** 设置是否展开 */
	public void setExpaned(boolean isExpanded) {
		this.isExpanded = isExpanded;
	}

	/**
	 * 递归判断所给的节点是否当前节点的父节点
	 * 
	 * @param node
	 *            所给节点
	 * @return
	 */
	public boolean isParent(SimpleNode node) {
		if (parent == null)
			return false;
		if (node.equals(parent))
			return true;
		return parent.isParent(node);
	}

	/** 通过文本查询下一级子节点(只是下一级,不递归到叶节点) */
	public SimpleNode queryChildNodeFromText(String txt) {
		for (int i = 0; i < children.size(); i++) {
			if (children.get(i).text.equals(txt)) {
				return children.get(i);
			}
		}
		return null;
	}

	/** 通过文本查询下一级子节点(只是下一级,不递归到叶节点) */
	public SimpleNode queryChildNodeFromValue(Object value) {
		for (int i = 0; i < children.size(); i++) {
			if (children.get(i).value.equals(value)) {
				return children.get(i);
			}
		}
		return null;
	}

	/** 通过值来寻找节点 */
	public static SimpleNode queryNodeFromValue(ArrayList<SimpleNode> nodes,
			Object object) {
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).value.equals(object)) {
				return nodes.get(i);
			}
		}
		return null;
	}

	/**
	 * <p>
	 * 设置加载状态
	 * <p>
	 * 只有叶节点才有效
	 * */
	public void setLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}
}
