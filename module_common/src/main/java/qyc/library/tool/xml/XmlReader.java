package qyc.library.tool.xml;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

public class XmlReader {

	/**
	 * 读取XML,只能一次性读出
	 * 
	 * @param xmlPath
	 *            XML文件的路径
	 * @param eleName
	 *            要查询的属性的节点名
	 * @param attributeNames
	 *            该节点下的属性名(支持多个)
	 * @return 节点下属性所对应的属性值。ArrayList的数量即同名节点的行数
	 */
	public static ArrayList<String[]> readXml(String xmlPath, String eleName, String[] attributeNames) {
		try {
			if (eleName != null && !eleName.equals("") && attributeNames != null) {
				return startRead(xmlPath, eleName, attributeNames);
			} else {
				return null;
			}
		}
		catch (SAXException e) {
			return null;
		}
		catch (ParserConfigurationException e) {
			return null;
		}
		catch (FileNotFoundException e) {
			return null;
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * 加载模块配置XML 有可能返回NULL
	 */
	private static ArrayList<String[]> startRead(String xmlPath, String eleName, String[] attributeNames)
			throws SAXException, ParserConfigurationException, IOException {
		SAXParserFactory saxFac = SAXParserFactory.newInstance();
		XMLReader reader = saxFac.newSAXParser().getXMLReader();
		ReadXmlHandler rxh = new ReadXmlHandler(eleName, attributeNames);
		reader.setContentHandler(rxh);

		File file = new File(xmlPath);
		InputStream is = new FileInputStream(file);
		// 这时已经读完了
		reader.parse(new InputSource(is));
		ArrayList<String[]> attributeValues = rxh.getValue();
		return attributeValues;
	}

	public static ArrayList<String[]> selectXml(String xmlPath, String eleName, String anchorAttributeName,
			String anchorAttributeValue, String[] selectAttributeNames, boolean isOnlyOneResult) {
		try {
			if (eleName != null && !eleName.equals("") && selectAttributeNames != null) {
				return startSelect(xmlPath, eleName, anchorAttributeName, anchorAttributeValue, selectAttributeNames,
						isOnlyOneResult);
			} else {
				return null;
			}
		}
		catch (SAXException e) {
			return null;
		}
		catch (ParserConfigurationException e) {
			return null;
		}
		catch (FileNotFoundException e) {
			return null;
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * 加载模块配置XML 有可能返回NULL
	 */
	private static ArrayList<String[]> startSelect(String xmlPath, String eleName, String anchorAttributeName,
			String anchorAttributeValue, String[] selectAttributeNames, boolean isOnlyOneResult) throws SAXException,
			ParserConfigurationException, IOException {
		SAXParserFactory saxFac = SAXParserFactory.newInstance();
		XMLReader reader = saxFac.newSAXParser().getXMLReader();
		SelectXmlHandler selectXmlHandler = new SelectXmlHandler(eleName, anchorAttributeName, anchorAttributeValue,
				selectAttributeNames, isOnlyOneResult);
		reader.setContentHandler(selectXmlHandler);

		File file = new File(xmlPath);
		InputStream is = new FileInputStream(file);
		// 这时已经读完了
		reader.parse(new InputSource(is));
		ArrayList<String[]> attributeValues = selectXmlHandler.getValue();
		return attributeValues;
	}

}
