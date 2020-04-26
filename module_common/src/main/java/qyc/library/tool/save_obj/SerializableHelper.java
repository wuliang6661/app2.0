package qyc.library.tool.save_obj;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SerializableHelper {

	/** 获取序列化字节数组 */
	public static final byte[] getSerializeBytes(Serializable serializable) {
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		try {
			// 序列化
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(serializable);
			byte[] bytes = baos.toByteArray();
			return bytes;
		}
		catch (Exception e) {
			System.out.println(e.toString());
		}
		return null;
	}

	/** 获取序列化字符串 */
	public static final String getSerializeStr(Serializable serializable) {
		byte[] data = getSerializeBytes(serializable);
		if (data != null) {
			return Base64.encodeToString(data, Base64.DEFAULT);
		} else {
			return null;
		}
	}

	/** 将序列化的字节数组转换回对象 */
	public static final Object getObj(byte[] bytes) {
		ByteArrayInputStream bais = null;
		try {
			// 反序列化
			bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		}
		catch (Exception e) {
		}
		return null;
	}

	/** 将序列化的字符串转换回对象 */
	public static final Object getObj(String base64Str) {
		byte[] bytes = null;
		try {
			bytes = Base64.decode(base64Str, Base64.DEFAULT);
		}
		catch (Exception ex) {
			return null;
		}
		return getObj(bytes);
	}
}
