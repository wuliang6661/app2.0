package synway.module_publicaccount.analytical.obj;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * <p>
 * 发送对象_文件
 * <p>
 * 首先由界面动作形成这个对象,将它入库. 然后送进发送异模进行文件上传,上传完毕后netFileName和netFileUrl就有值了.最后进行发送
 * 
 * @author 钱园超 2015年8月25日下午1:43:36
 */
public class Obj_Msg_File extends Obj_Msg {

	// 天生的参数
	/** ****.jpg */
	private String fileName;
	/** url不带http://IP:Port */
	private String url = "";

	// 自动生成的参数
	private String localeFileName;

	/** 附件后缀名称， 不带 . */
	private String localeFileSuffix;

	private int annexType = 0;

	/**
	 * @param FileNameWhitSuffix
	 *            本地存储的带后缀的文件名
	 */
	public Obj_Msg_File(String FileNameWhitSuffix) {
		String[] a = getSuffix_Name(FileNameWhitSuffix);
		this.localeFileName = a[1];
		this.localeFileName = a[1];
		this.localeFileSuffix = a[0];
		this.annexType = getTypeFromSuffix(a[0]);
		this.fileName = FileNameWhitSuffix;
	}

	/**
	 * @return 附件不带后缀的名字
	 */
	public String getLocalFileName() {
		return localeFileName;
	}

	public void setLocaleFileName(String localeFileName) {
		this.localeFileName = localeFileName;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * <p>
	 * 获取文件的下载地址
	 * <p>
	 * 对于文件没有上传成功的实体,Url是没有的
	 * */
	public String getUrl() {
		return url;
	}

	/** 附件 类型， 1=图片，2=视频，3=音频，4=其他 */
	public int getAnnexType() {
		return this.annexType;
	}

	/** 附件后缀名称， 不带 . */
	public String getAnnexName() {
		return this.localeFileSuffix;
	}

	/** 1=图片 2=视频 3=声音 4=其他 */
	private static final int getTypeFromSuffix(String suffix) {
		//当文件后缀名为大写的 .JPG .PNG 格式的情况,需要转小写进行比对,不然会识别为附件格式
		suffix = suffix.toLowerCase();
		if (suffix.equals("png")) {
			return 1;
		} else if (suffix.equals("jpg")) {
			return 1;
		} else if (suffix.equals("bmp")) {
			return 1;
		} else if (suffix.equals("amr")) {
			return 3;
		} else if (suffix.equals("mp3")) {
			return 3;
		} else if (suffix.equals("wav")) {
			return 3;
		} else if (suffix.equals("3gp")) {
			return 2;
		} else if (suffix.equals("mp4")) {
			return 2;
		} else {
			return 4;
		}

	}

	private static final String[] getSuffix_Name(String fileNameWhitSuffix) {
		String suffix = "";
		String fileName = "";

		int index2=fileNameWhitSuffix.lastIndexOf("/");
		int index = fileNameWhitSuffix.lastIndexOf(".");

		if (index >= 0) {
			suffix = fileNameWhitSuffix.substring(index + 1, fileNameWhitSuffix.length());
			fileName = fileNameWhitSuffix.substring(index2+1, index);
		} else {
			fileName = fileNameWhitSuffix;
		}

		return new String[]{suffix, fileName};
	}
	
	/** MSG 消息头部的GUID */
	private String msgGUID = UUID.randomUUID().toString();

	@Override
	public JSONObject toMsgJson() {
		JSONObject resultJson = new JSONObject();
		try {
			resultJson.put("MSG_TYPE", 2);
			resultJson.put("MSG_GUID", msgGUID);
			JSONObject jsonMsgInfo = new JSONObject();
			jsonMsgInfo.put("FILE_NAME", fileName);
//			jsonMsgInfo.put("FILE_URL", url.replace("FTPText", ""));
			jsonMsgInfo.put("FILE_URL", url);
			resultJson.put("MSG_INFO", jsonMsgInfo);
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		return resultJson;
	}

}
