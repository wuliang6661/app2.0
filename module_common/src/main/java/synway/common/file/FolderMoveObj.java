package synway.common.file;

public class FolderMoveObj {
	public FolderMoveObj(String folderSrc, String folderDest) {
		this.folderSrc = folderSrc;
		this.folderDest = folderDest;
	}

	public FolderMoveObj(String folderSrc, String folderDest, boolean isCover) {
		this.folderSrc = folderSrc;
		this.folderDest = folderDest;
		this.isCover = isCover;
	}

	public FolderMoveObj(String folderSrc, String folderDest, boolean isCover, boolean isCut) {
		this.folderSrc = folderSrc;
		this.folderDest = folderDest;
		this.isCover = isCover;
		this.isCut = isCut;
	}

	public String folderSrc;
	public String folderDest;
	public boolean isCover = true;
	public boolean isCut = true;
}
