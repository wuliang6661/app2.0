package qyc.library.tool.save_obj;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * <p>
 * 字节数据和对象之间的转换,用于对象的硬盘存储.
 * <p>
 * 这种转换对象本身必须是静态的(只是一个存储基本数据类型的实体类),它不可以是复杂对象,如包含其他无法序列化对象,接口,甚至线程等
 * 
 * @author qyc
 */
@SuppressWarnings("serial")
public abstract class SaveObj implements Serializable {

	/** 获取序列化字节数组 */
	public final byte[] getSerializeBytes() {
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		try {
			// 序列化
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			byte[] bytes = baos.toByteArray();
			return bytes;
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return null;
	}

	/** 获取序列化字符串 */
	public final String getSerializeStr() {
		byte[] data = getSerializeBytes();
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
		} catch (Exception e) {
		}
		return null;
	}

	/** 将序列化的字符串转换回对象 */
	public static final Object getObj(String base64Str) {
		byte[] bytes = null;
		try {
			bytes = Base64.decode(base64Str, Base64.DEFAULT);
		} catch (Exception ex) {
			return null;
		}
		return getObj(bytes);
	}
}
