package qyc.library.tool.file;

import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileHelp {

	/**
	 * 判断SD卡是否存在
	 * 
	 * @return
	 */
	public static boolean getSDCardState() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
	}

	/**
	 * 复制文件夹或者文件，只能文件夹复制到文件夹，文件复制到文件
	 * 
	 * @param src
	 *            起始文件夹/文件
	 * @param tar
	 *            目标文件夹/文件
	 * @param isMakeTargetFileDir
	 *            目标文件路径是否需要创建,调用时只管选是就行了
	 * @throws Exception
	 */
	public static void copyFile(File src, File tar, boolean isMakeTargetFileDir)
			throws Exception {

		if (src.isFile()) {
			if (isMakeTargetFileDir) {
				int pathIndex = tar.toString().lastIndexOf("/");
				File tarPath = null;
				if (pathIndex != -1) {
					tarPath = new File(tar.toString().substring(0, pathIndex));
				} else {
					tarPath = tar;
				}
				tarPath.mkdirs();
				isMakeTargetFileDir = false;
			}
			InputStream is = new FileInputStream(src);
			OutputStream op = new FileOutputStream(tar);
			BufferedInputStream bis = new BufferedInputStream(is);
			BufferedOutputStream bos = new BufferedOutputStream(op);
			byte[] bt = new byte[8192];
			int len = bis.read(bt);
			while (len != -1) {
				bos.write(bt, 0, len);
				len = bis.read(bt);
			}
			bis.close();
			bos.close();
		} else if (src.isDirectory()) {
			File[] f = src.listFiles();
			tar.mkdirs();
			for (int i = 0; i < f.length; i++) {
				copyFile(f[i].getAbsoluteFile(), new File(tar.getAbsoluteFile()
						+ File.separator + f[i].getName()), isMakeTargetFileDir);
			}
		}
	}

	/**
	 * 特定文件夹根目录下查找一个文件是否存在
	 * 
	 * @return
	 */
	public static File[] getSubFiles(String dir) {
		File filePath = new File(dir);
		if (!filePath.exists()) {
			return null;
		}
		if (filePath.isDirectory()) {
			File[] subFiles = filePath.listFiles();
			return subFiles;
		} else {
			return null;
		}
	}

	/**
	 * 删除一个文件或者一个文件夹
	 * 
	 * @param file
	 */
	public static void deleteFile(File file) {
		if (file.isFile()) {
			file.delete();
		} else if (file.isDirectory()) {
			File[] f = file.listFiles();
			for (int i = 0; i < f.length; i++) {
				deleteFile(f[i].getAbsoluteFile());
			}
			file.delete();// 最后删掉自己
		}
	}
}
