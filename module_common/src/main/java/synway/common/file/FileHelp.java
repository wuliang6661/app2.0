package synway.common.file;

import java.io.FilenameFilter;
import java.util.ArrayList;

import synway.common.file.progress.OnProgress;


public class FileHelp {

	public static ArrayList<String> move(ArrayList<FileMoveObj> fileMoveObjs, OnProgress onProgress) {
		return FileMove.move(fileMoveObjs, onProgress);
	}

	public static String move(FileMoveObj fileMoveObj) {
		return FileMove.move(fileMoveObj);
	}

	public static String delete(String file) {
		return FileDelete.delete(file, null);
	}

	public static String delete(String file, FilenameFilter filenameFilter) {
		return FileDelete.delete(file, filenameFilter);
	}

}
