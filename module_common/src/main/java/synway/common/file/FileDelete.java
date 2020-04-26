package synway.common.file;

import java.io.File;
import java.io.FilenameFilter;

class FileDelete {

	static String delete(String file, FilenameFilter filenameFilter) {
		File f = new File(file);
		if (f.exists()) {
			if (f.isDirectory()) {
				try {
					deleteRecursion(f, filenameFilter);
				} catch (Exception e) {
					return e.toString();
				}
			} else {
				if (filenameFilter != null) {
					if (!filenameFilter.accept(f, f.getName())) {
						return null;
					}
				}
				boolean result = f.delete();
				if (!result) {
					return f.getAbsolutePath() + "删除失败";
				}
			}
		}
		return null;
	}

	private static void deleteRecursion(File folder, FilenameFilter filenameFilter) throws Exception {
		File[] listFiles = folder.listFiles(filenameFilter);
		for (int i = 0; i < listFiles.length; i++) {
			File f = listFiles[i];
			if (f.isDirectory()) {
				deleteRecursion(f, filenameFilter);
			} else {
				boolean result = f.delete();
				if (!result) {
					throw new Exception(f.getAbsolutePath() + "删除失败");
				}
			}
		}

		if (folder.listFiles().length == 0) {
			boolean result = folder.delete();
			if (!result) {
				throw new Exception(folder.getAbsolutePath() + "删除失败");
			}
		}

	}

}
