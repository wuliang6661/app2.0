package synway.module_publicaccount.public_chat.bluetooth;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import static synway.module_publicaccount.public_chat.bluetooth.Configutil.getFileName;
import static synway.module_publicaccount.until.ConfigUtil.getBlueToothPath;

/**
 * Created by ysm on 2017/9/25.
 * 文件接收器
 */
public class MReceiveAdapter extends ReceiveAdapter {
    private onProgress onProgress;
    private String fileName;

    public MReceiveAdapter(onProgress onProgress, String fileName) {
        this.onProgress = onProgress;
        this.fileName = fileName;
    }

    @Override
    public void onReceive(DataInputStream inputStream) {
        if (!new File(fileName).exists()) {
            if (!new File(fileName).mkdirs()) {
                return;
            }
        }
        int fileNum;
        try {
            fileNum = inputStream.readInt();//文件或数据总数
        } catch (IOException e) {
            return;
        }

        long totalLen;//总长度
        ArrayList<FileInfo> fileinfos = new ArrayList<>();
        try {
            for (int i = 0; i < fileNum; i++) {
                FileInfo fileInfo = new FileInfo();
                fileInfo.fileName = inputStream.readUTF();
                fileInfo.fileSize = inputStream.readLong();
                fileinfos.add(fileInfo);
            }
            totalLen = inputStream.readLong();
        } catch (IOException e) {
            //读取文件名出错
            return;
        }
        int leftLen = 0; // 写满文件后缓存区中剩余的字节长度。
        int bufferedLen; // 当前缓冲区中的字节数
        int writeLen; // 每次向文件里写入的字节数
        long writeLens; // 当前已经向单个文件里写入的字节总数
        long totalWriteLens = 0; // 写入的所有字节数
        int totalFileNum = 0;
        byte buf[] = new byte[2048];
        for (int i = 0; i < fileinfos.size(); i++) {
            try {
                writeLens = 0;
                FileOutputStream fout = new FileOutputStream(getBlueToothPath() + "/" + getFileName(fileinfos.get(i).fileName));
                long receivebl;
                while (true) {
                    if (leftLen > 0) {
                        bufferedLen = leftLen;
                    } else {
                        bufferedLen = inputStream.read(buf);
                    }
                    if (bufferedLen == -1)
                        return;
                    // 假设已写入文件的字节数加上缓存区中的字节数已大于文件的大小，仅仅写入缓存区的部分内容。
                    if (writeLens + bufferedLen >= fileinfos.get(i).fileSize) {
                        leftLen = (int) (writeLens + bufferedLen - fileinfos.get(i).fileSize);
                        writeLen = bufferedLen - leftLen;
                        fout.write(buf, 0, writeLen); // 写入部分
                        totalWriteLens += writeLen;
                        receivebl = ((totalWriteLens * 100) / totalLen);
                        onProgress.onProgress((int) receivebl, (int) totalWriteLens, (int) totalLen, i, fileinfos.get(i).fileName,fileinfos);
                        break;
                    } else {
                        fout.write(buf, 0, bufferedLen); // 所有写入
                        writeLens += bufferedLen;
                        totalWriteLens += bufferedLen;
                        receivebl = ((totalWriteLens * 100) / totalLen);
                        onProgress.onProgress((int) receivebl, (int) totalWriteLens, (int) totalLen, i, fileinfos.get(i).fileName,fileinfos);
                        if (totalWriteLens >= totalLen) {
                            return;
                        }
                        leftLen = 0;
                    }
                }
                fout.close();
            } catch (IOException e) {
                return;
            }
            totalFileNum = totalFileNum + 1;
        }
    }

    public interface onProgress {
        void onProgress(int progress, int receiveSize, int allReceiveSize, int currentIndex, String filename, ArrayList<FileInfo> fileList);
    }
}
