package synway.common.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import synway.common.file.progress.OnProgress;
import synway.common.file.progress.ProgressHelp;


class FileMove {

	static ArrayList<String> move(ArrayList<FileMoveObj> fileMoveObjs, OnProgress onProgress) {
		ArrayList<String> error = new ArrayList<>();
		int fileMoveObjSize = fileMoveObjs.size();

		ProgressHelp progressHelp = null;
		if (onProgress != null) {
			progressHelp = new ProgressHelp(fileMoveObjSize, onProgress);
			onProgress.progress(0);
			onProgress.progressDetial(0, fileMoveObjSize);
		}
		for (int i = 0; i < fileMoveObjs.size(); i++) {
			String result = move(fileMoveObjs.get(i));
			if (result != null) {
				error.add(result);
			}
			if (onProgress != null) {
				progressHelp.add();
			}
		}

		if (onProgress != null) {
			onProgress.progress(100);
		}

		return error;
	}

	static void move(FolderMoveObj folderMoveObj) {

	}

	static String move(FileMoveObj fileMoveObj) {
		// 检测原始文件在不在
		if (fileMoveObj.src == null) {
			// 文件不存在
			return "源文件路径为[null]";
		}
		if (fileMoveObj.destFolder == null) {
			return "目标文件夹路径为[null]";
		}

		File fileSrc = new File(fileMoveObj.src);
		if (!fileSrc.exists()) {
			return "源文件[" + fileMoveObj.src + "]不存在";
		}

		File folderDest = new File(fileMoveObj.destFolder);
		if (folderDest.exists()) {
			if (!folderDest.isDirectory()) {
				return "目标文件夹[" + fileMoveObj.destFolder + "]不是一个正常的文件夹";
			}
		} else {
			boolean result = folderDest.mkdirs();
			if (!result) {
				return "目标文件夹[" + fileMoveObj.destFolder + "]创建失败";
			}
		}

		File fileDest = new File(folderDest.getAbsolutePath() + File.separator + fileSrc.getName());
		if (fileDest.exists()) {
			if (!fileMoveObj.isCover) {
				return null;
			} else {
				fileDest.delete();
			}
		}

		if (fileMoveObj.isCut) {
			boolean result = fileSrc.renameTo(fileDest);
			if (result) {
				return null;
			} else {
				return "文件剪切失败";
			}
		} else {
			return writeFile(fileSrc, fileDest);
		}
	}

	@SuppressWarnings("resource")
	private static String writeFile(File fileSrc, File fileDest) {
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(fileSrc);
		} catch (FileNotFoundException e) {
			return "源文件[" + fileSrc.getAbsolutePath() + "]不存在";
		}

		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(fileDest);
		} catch (FileNotFoundException e) {
			return "目标文件[" + fileDest.getAbsolutePath() + "]不存在";
		}

		byte[] data = new byte[10240];
		try {
			while (true) {

				int readLength = fileInputStream.read(data);
				if (readLength < 0) {
					break;
				}

				fileOutputStream.write(data, 0, readLength);
			}
		} catch (IOException e) {
			return "源文件[" + fileSrc.getAbsolutePath() + "]向目标文件[" + fileDest.getAbsolutePath() + "]写入失败:"
					+ e.toString();
		} finally {
			try {
				fileInputStream.close();
			} catch (Exception e) {
			}
			try {
				fileOutputStream.close();
			} catch (Exception e) {
			}
		}

		return null;

	}

}
