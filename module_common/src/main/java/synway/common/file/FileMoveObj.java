package synway.common.file;

public class FileMoveObj {
	/**
	 * 文件复制描述,默认以剪切的方式,默认自动覆盖.
	 * 
	 * @param src
	 *            源文件
	 * @param destFolder
	 *            目标文件夹
	 * @param isCover
	 *            如果文件已存在,是否覆盖
	 */
	public FileMoveObj(String src, String destFolder) {
		this.src = src;
		this.destFolder = destFolder;
	}

	/**
	 * 文件复制描述,默认以剪切的方式.
	 * 
	 * @param src
	 *            源文件
	 * @param destFolder
	 *            目标文件夹
	 * @param isCover
	 *            如果文件已存在,是否覆盖
	 */
	public FileMoveObj(String src, String destFolder, boolean isCover) {
		this.src = src;
		this.destFolder = destFolder;
		this.isCover = isCover;
	}

	/**
	 * 文件复制描述
	 * 
	 * @param src
	 *            源文件
	 * @param destFolder
	 *            目标文件夹
	 * @param isCover
	 *            如果文件已存在,是否覆盖
	 * @param isCut
	 *            是否剪切
	 */
	public FileMoveObj(String src, String destFolder, boolean isCover, boolean isCut) {
		this.src = src;
		this.destFolder = destFolder;
		this.isCover = isCover;
		this.isCut = isCut;
	}

	public String src;
	public String destFolder;
	public boolean isCover = true;
	public boolean isCut = true;
}
