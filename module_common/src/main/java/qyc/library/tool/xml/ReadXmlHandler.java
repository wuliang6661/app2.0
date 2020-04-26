package qyc.library.tool.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

class ReadXmlHandler extends DefaultHandler {

	/**
	 * 确保keyName不为NULL值
	 * 
	 * @param eleName
	 */
	public ReadXmlHandler(String eleName, String[] attributeNames) {
		this.eleName = eleName;
		this.attributesNames = attributeNames;
		this.attributesValues = new ArrayList<String[]>();
	}

	/**
	 * 要查询的元素名
	 */
	String eleName;
	/**
	 * 要查询的属性名
	 */
	String[] attributesNames;
	/**
	 * 查询到的属性值
	 */
	ArrayList<String[]> attributesValues;

	/**
	 * <p>
	 * 集合中的每一项,都是keyNames中每一个key所对应的值
	 * <p>
	 * 比如keyNames为key,value 返回的就是key的值,value的值,当然这些值可能有多个
	 * 
	 * @return
	 */
	public ArrayList<String[]> getValue() {
		return this.attributesValues;
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

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		// 寻找到对应的元素名
		if (localName.equals(this.eleName)) {
			// 在这个XML元素的多个属性中找到需要的属性
			String[] attributesValues = new String[attributesNames.length];
			for (int i = 0; i < attributes.getLength(); i++) {
				// 当前属性名是否在我需要寻找的AttributesNames里面
				int id = isStrInStrArray(attributes.getLocalName(i), attributesNames);
				if (id != -1) {
					// 当前属性名正是我需要的那个属性名,把它的值放在values相应的位置下
					attributesValues[id] = attributes.getValue(i);
				}
			}

			// 遍历完当前元素的所有属性后,该得到的值已经得到了,将它加入到集合中
			this.attributesValues.add(attributesValues);
		}
	}
}
