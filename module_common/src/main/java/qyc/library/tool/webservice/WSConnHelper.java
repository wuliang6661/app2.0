package qyc.library.tool.webservice;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.StrictMode;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

/** 访问WebService帮助类 */
@SuppressLint("NewApi")
public class WSConnHelper {
	static {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

			// 4.0以后需要加入如下设置才能访问WebService
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites()
					.detectNetwork().penaltyLog().build());

			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects()
					.detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
		}
	}

	/**
	 * @param url
	 *            资源地址，wsdl地址去掉“?wsdl”
	 * @param nameSpace
	 *            命名空间，可以从wsdl文档中找到
	 * @param serviceName
	 *            服务名
	 * @param methodName
	 *            方法名(wsdl文档中<operation name>标签下为方法名)
	 * @param soapAction
	 *            根据网上那群人的说法，soapAction的值有三种情况： 1.一般情况下为null 2.命名空间+方法名
	 *            3.直接从wsdl文档中搜索soapAction关键， 使用者请自行取舍
	 * @param params
	 *            方法的参数名和参数值
	 * @param timeout
	 *            超时时间，单位秒
	 * 
	 *            若返回null，表示出现异常，查询失败。
	 */
	public static SoapObject getSoapObject(String url, String nameSpace, String methodName, String soapAction,
			HashMap<String, Object> params, int timeout) {
		if (url == null || nameSpace == null || methodName == null || soapAction == null || params == null) {
			throw new NullPointerException("Method variables should not be null");
		}

		SoapObject rpc = new SoapObject(nameSpace, methodName);
		if (params != null && params.size() > 0) {
			for (Entry<String, Object> item : params.entrySet()) {
				rpc.addProperty(item.getKey(), item.getValue());
			}
		}

		System.out.println("Send:" + rpc.toString());

		// 这里的版本号要根据webservice服务器的版本来，使用者需要自己选择
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
		envelope.bodyOut = rpc;
		// 如果webservice是用.net进行开发，这个值需要设为true
		envelope.dotNet = false;

		/** ============身份验证============ */
		// 需要知道服务端那边从envelope中解析的时候3个变量的名称
		// 比如"Authtication","UserName","Password"
		// 需要知道具体的大小写之类的，不然解析会出错
		// Element[] header = new Element[1];
		// header[0] = new Element();
		// header[0].setName("authenticationtoken");
		//
		// Element userName = new Element();
		// userName.setName("username");
		// userName.addChild(Node.TEXT, "三支队单位负责人");
		// header[0].addChild(Node.ELEMENT, userName);
		//
		// Element passWord = new Element();
		// passWord.setName("password");
		// passWord.addChild(Node.TEXT, "1");
		// header[0].addChild(Node.ELEMENT, passWord);
		//
		// envelope.headerOut = header;
		/** ============身份验证============ */

		String exceptionStr = null;

		HttpTransportSE ht = null;
		if (timeout > 0) {
			ht = new HttpTransportSE(url, timeout * 1000);
		} else {
			ht = new HttpTransportSE(url);
		}
		ht.debug = true;
		try {
			ht.call(soapAction, envelope);
		}
		catch (NullPointerException e) {
			exceptionStr = "WEB服务数据格式错误!";
		}
		catch (IOException e) {
			exceptionStr = "WEB服务连接失败!";
		}
		catch (XmlPullParserException e) {
			// 这种情况可能是原因有很多：
			// <1>.传的方法参数名称错误，关于这个，网上有人说只要参数类型跟指定顺序一致就可以了，名称可以任意。
			// 但据我实测，这种说法是不严谨的。取决于服务端取值的方式是:
			// getProperty(int index)还是getProperty(String name);
			// <2>.可能是参数值类型错误，值类型错误有两种情况：
			// 就拿userId这个参数来讲，方法中要求是String类型，诸如“211”，第一种是传了其他类型如211；
			// 第二种是传了“211a”，表面上看依然是一个String类型，但是也会报这个错。
			// 而如果传一个不存在的id如“2111”那么不会报这个错，而是服务端返回一个error“获取用户信息出错”
			// <3>.也可能是方法名称methodName错误
			// <4>.可能是url末尾的"?wsdl"没有去掉等等
			// 所以这是一个相当恶心的exception,姿势之多令人发指。
			// 这种情况的提示还无法预见
			exceptionStr = e.toString();
		}
		catch (Exception e) {
			exceptionStr = e.toString();
		}

		SoapObject soapObj = (SoapObject) envelope.bodyIn;
		if (soapObj != null) {
			System.out.println("Return:" + soapObj.toString());
		} else {
			soapObj = new SoapObject(null, null);
			soapObj.addProperty("Exception", exceptionStr);
		}

		return soapObj;
	}
}
