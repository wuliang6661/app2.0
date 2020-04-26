package qyc.library.tool.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileZIP {
	/** 解压缩 */
	public static void unZip(String unZipfileName, String mDestPath) {
		if (!mDestPath.endsWith("/")) {
			mDestPath = mDestPath + "/";
		}
		FileOutputStream fileOut = null;
		ZipInputStream zipIn = null;
		ZipEntry zipEntry = null;
		File file = null;
		int readedBytes = 0;
		byte buf[] = new byte[4096];
		try {
			zipIn = new ZipInputStream(new BufferedInputStream(
					new FileInputStream(unZipfileName)));
			while ((zipEntry = zipIn.getNextEntry()) != null) {
				file = new File(mDestPath + zipEntry.getName());
				if (zipEntry.isDirectory()) {
					file.mkdirs();
				} else {
					// 如果指定文件的目录不存在,则创建之.
					File parent = file.getParentFile();
					if (!parent.exists()) {
						parent.mkdirs();
					}
					fileOut = new FileOutputStream(file);
					while ((readedBytes = zipIn.read(buf)) > 0) {
						fileOut.write(buf, 0, readedBytes);
					}
					fileOut.close();
				}
				zipIn.closeEntry();
			}
		} catch (IOException ioe) {
			System.out.println(ioe.toString());
		}
	}

}
