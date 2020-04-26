package qyc.library.tool.http;

import android.content.Context;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import qyc.library.control.dialog_msg.DialogMsg;
import qyc.library.tool.main_thread.MainThread;
import qyc.library.tool.main_thread.MainThread.Runnable_MainThread;

public class HttpInThread {
	private OnHttpResult onHttpResult = null;
	private String httpIP;
	private int httpPort;
	private Object listenLock = new Object();
	private int outTime = 0;

	public HttpInThread(String httpIP, int httpPort) {
		this.httpIP = httpIP;
		this.httpPort = httpPort;
	}

	/** 设置超时时间 注意,单位是秒~! */
	public void setOutTime(int outTime) {
		this.outTime = outTime;
	}

	/** 重新设置HTTP地址 */
	public void resetAddress(String httpIP, int httpPort) {
		this.httpIP = httpIP;
		this.httpPort = httpPort;
	}

	/** 设置HTTP结果监听 */
	public void setOnHttpResultListener(OnHttpResult httpResult) {
		synchronized (listenLock) {
			this.onHttpResult = httpResult;
		}
	}

	/** 取消HTTP结果监听 */
	public void removeHttpResultListener() {
		synchronized (listenLock) {
			this.onHttpResult = null;
		}
	}

	/**
	 * POST JSON
	 * 
	 * @param pageName
	 *            页面名称,不带.aspx
	 * @param jsonObject
	 *            JSON对象
	 * @param id
	 *            识别ID,不要用-1,-1将用来表示异常
	 */
	public void postJSONObj(String pageName, JSONObject jsonObject, int id) {
		// System.out.println("HTTP_Send:" + jsonObject.toString());
		String url = "http://" + httpIP + ":" + httpPort + "/HZVideo/MoService/" + pageName + ".ashx";
		byte[] data = HttpClass.strToUnicodeBytes(jsonObject.toString());
		new Thread(new HttpThread(url, data, id, null)).start();
	}

	/**
	 * POST JSON
	 * 
	 * @param pageName
	 *            页面名称,不带.aspx
	 * @param jsonObject
	 *            JSON对象
	 * @param id
	 *            识别ID
	 */
	public void postJSONObj(String pageName, JSONObject jsonObject, int id, Object obj) {
		// System.out.println("HTTP_Send:" + jsonObject.toString());
		String url = "http://" + httpIP + ":" + httpPort + "/HZVideo/MoService/" + pageName + ".ashx";
		byte[] data = HttpClass.strToUnicodeBytes(jsonObject.toString());
		new Thread(new HttpThread(url, data, id, obj)).start();
	}

	/**
	 * POST到完整的网址
	 * 
	 * @param url
	 *            完整的网址
	 * @param jsonObject
	 *            JSON对象
	 * @param id
	 *            识别ID
	 * @param obj
	 *            传递对象
	 */
	public void postJSONObjToUrl(String url, JSONObject jsonObject, int id, Object obj) {
		// System.out.println("HTTP_Send:" + jsonObject.toString());
		byte[] data = HttpClass.strToUnicodeBytes(jsonObject.toString());
		new Thread(new HttpThread(url, data, id, obj)).start();
	}

	/**
	 * POST bytes
	 * 
	 * @param pageName
	 *            页面名称,不带.aspx
	 * @param jsonObject
	 *            JSON对象
	 * @param id
	 *            识别ID
	 * @param obj
	 *            传递对象
	 */
	public void postBytes(String pageName, byte[] bytes, int id, Object obj) {
		String url = "http://" + httpIP + ":" + httpPort + "/HZVideo/MoService/" + pageName + ".ashx";
		new Thread(new HttpThread(url, bytes, id, obj)).start();
	}

	public void postBytesToUrl(String url, byte[] bytes, int id, Object obj) {
		new Thread(new HttpThread(url, bytes, id, obj)).start();
	}

	private class HttpThread implements Runnable {
		private String url;
		private byte[] data;
		private int id;
		private Object obj;

		public HttpThread(String url, byte[] data, int id, Object obj) {
			this.url = url;
			this.data = data;
			this.id = id;
			this.obj = obj;
		}

		@Override
		public void run() {
			JSONObject jsonObject = null;
			String result = null;
			try {
				InputStream is = null;
				if (outTime <= 0) {
					is = HttpClass.postByte(url, data);
				} else {
					is = HttpClass.postByte(url, data, 5000, outTime * 1000);
				}
				result = HttpClass.stream2String(is);
				
				try {
					is.close();
				}
				catch (Exception e) {
				}
				// 降低网速
				// Thread.sleep(5000);
				// 降低网速

				jsonObject = new JSONObject(result);
			}
			catch (ClientProtocolException e) {
				jsonObject = getExpJSON(-1, e.toString());
			}
			catch (IOException e) {
				jsonObject = getExpJSON(-1, e.toString());
			}
			catch (JSONException e) {
				jsonObject = getExpJSON(-2, result);
			}
			catch (Exception e) {
				jsonObject = getExpJSON(-1, e.toString());
			}

			synchronized (listenLock) {
				if (onHttpResult != null) {
					boolean turnToMainThread = onHttpResult.onHttpResult(jsonObject, id, obj);
					if (turnToMainThread) {
						MainThread.joinMainThread(new ToMainThread(jsonObject, id, obj));
					}
				}
			}
		}
	}

	private class ToMainThread implements Runnable_MainThread {
		JSONObject jsonObject = null;
		int id;
		Object obj;

		public ToMainThread(JSONObject jsonObject, int id, Object obj) {
			this.jsonObject = jsonObject;
			this.id = id;
			this.obj = obj;
		}

		@Override
		public void run() {
			synchronized (listenLock) {
				if (onHttpResult != null) {
					onHttpResult.onHttpResult_MainThead(jsonObject, id, obj);
				}
			}
		}
	}

	/**
	 * 获取表示异常的JSON
	 * 
	 * @param result
	 *            结果 -2表示数据异常,-1表示请求失败,0表示服务端错误
	 * @param wrongMsg
	 *            错误消息
	 * @return
	 */
	private static final JSONObject getExpJSON(int result, String wrongMsg) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("result", result);
			jsonObject.put("reason", wrongMsg);
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	public interface OnHttpResult {
		/**
		 * @param jsonObject
		 *            通过checkResult来获取请求结果,这是我们内部的JSON协议规定的检验方式
		 * @param id
		 *            请求时的ID
		 * @param obj
		 *            请求时的对象
		 * @return true=通过主线程再触发一次,false=不再触发
		 */
        boolean onHttpResult(JSONObject jsonObject, int id, Object obj);

		void onHttpResult_MainThead(JSONObject jsonObject, int id, Object obj);
	}

	/**
	 * 检查结果,并负责弹出提示对话框
	 * 
	 * @param jsonObject
	 * @param context
	 * @return -2表示本地提交查询的参数异常,并没有执行查询 -1表示网络请求失败 0表示服务器查询失败 1表示查询成功
	 */
	public static int checkResult(JSONObject jsonObject, Context context) {
		int result = jsonObject.optInt("result", -1);
		// 检查结果
		if (result == -2) {
			DialogMsg.show(context, "提交参数异常", jsonObject.optString("reason", "JSON has no KEY=reason"));
			return -2;
		} else if (result == -1) {
			DialogMsg.show(context, "网络请求失败", jsonObject.optString("reason", "JSON has no KEY=reason"));
			return -1;
		} else if (result == 0) {
			DialogMsg.show(context, "操作失败", jsonObject.optString("reason", "JSON has no KEY=reason"));
			return 0;
		} else {
			return 1;
		}
	}

	/**
	 * 检查结果
	 * 
	 * @param jsonObject
	 * @return NULL表示结果正常，非NULL[0]表示错误信息描述,非NULL[1]表示详细错误信息
	 */
	public static String[] checkResult(JSONObject jsonObject) {
		int result = jsonObject.optInt("result", -1);
		// 检查结果
		if (result == -2) {
			return new String[]{"返回数据格式异常", jsonObject.optString("reason", "JSON has no KEY=reason")};
		} else if (result == -1) {
			return new String[]{"网络请求失败", jsonObject.optString("reason", "JSON has no KEY=reason")};
		} else if (result == 0) {
			return new String[]{"操作失败", jsonObject.optString("reason", "JSON has no KEY=reason")};
		} else {
			return null;
		}
	}
}
