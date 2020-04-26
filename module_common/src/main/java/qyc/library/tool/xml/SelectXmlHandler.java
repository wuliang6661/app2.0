package qyc.library.tool.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

class SelectXmlHandler extends DefaultHandler {

	/**
	 * 查询用的XML Handler
	 * 
	 * @param eleName
	 *            节点名
	 * @param anchorAttributeName
	 *            已知的属性名
	 * @param anchorAttributeValue
	 *            已知的属性值
	 * @param selectAttributeName
	 *            要查询的属性名,支持多个
	 * @param isOnlyOneResult
	 *            查询结果是否只有一行,如果在大量数据的XML中查询其中一行,把它设为true可极大的提高查询效率(数据越靠前速度越快)
	 */
	public SelectXmlHandler(String eleName, String anchorAttributeName, String anchorAttributeValue,
			String[] selectAttributeName, boolean isOnlyOneResult) {
		this.eleName = eleName;
		this.anchorAttributeName = anchorAttributeName;
		this.anchorAttributeValue = anchorAttributeValue;
		this.selectAttributeName = selectAttributeName;
		this.selectAttributeValue = new ArrayList<String[]>();
	}

	/**
	 * 要查询的节点名
	 */
	String eleName;
	/**
	 * 已知的属性名
	 */
	String anchorAttributeName;
	/**
	 * 已知的属性值
	 */
	String anchorAttributeValue;
	/**
	 * 要查询的属性名
	 */
	String[] selectAttributeName;
	/**
	 * 结果集
	 */
	ArrayList<String[]> selectAttributeValue = null;
	/**
	 * 是否只有一条结果
	 */
	boolean isOnlyOneResult = false;

	/**
	 * 获取查询结果
	 * 
	 * @return 查询结果可能有多行,每行都是一个String[](属性值),分别对应要查询的属性名
	 */
	public ArrayList<String[]> getValue() {
		return this.selectAttributeValue;
	}

	/**
	 * 查找某个字符串是否在字符串数组里面,找到则返回数组下标,找不到返回-1
	 * 
	 * @param str
	 * @param strs
	 * @return
	 */
	private int isStrInStrArray(String str, String[] strs) {
		for (int i = 0; i < strs.length; i++) {
			if (strs[i].equals(str)) {
				return i;
			}
		}
		return -1;
	}

	private boolean isContinue = true;
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (!isContinue) {
			return;
		}

		// 寻找到对应的元素名
		if (localName.equals(this.eleName)) {
			boolean isAdd = false;
			String[] result = null;
			// 在这个XML元素的多个属性中遍历
			for (int i = 0; i < attributes.getLength(); i++) {
				// 找到属性后如果属性值不相等,就不必再遍历了,切到下一行
				// 找到属性后如果属性值也相等,就认为是要找的那条
				if (attributes.getLocalName(i).equals(this.anchorAttributeName)) {
					String attributeValue = attributes.getValue(i);
					// 找到
					if (attributeValue.equals(this.anchorAttributeValue)) {
						// 正是这一条
						isAdd = true;
						// 寻找需要的结果
						result = new String[selectAttributeName.length];
						for (int j = 0; j < attributes.getLength(); j++) {
							int findResult = isStrInStrArray(attributes.getLocalName(j), selectAttributeName);
							if (findResult >= 0) {
								// 该属性是我要找的属性
								result[findResult] = attributes.getValue(j);
							}
						}
						// 不再继续遍历属性
						break;
					} else {
						// 不是这一条
						isAdd = false;
						break;
					}
				} else {
					continue;
				}
			}

			if (isAdd) {
				selectAttributeValue.add(result);
				if (isOnlyOneResult) {
					isContinue = false;
				}
			}
		}
	}
}
