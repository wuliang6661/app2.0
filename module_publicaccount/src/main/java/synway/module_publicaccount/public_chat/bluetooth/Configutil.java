package synway.module_publicaccount.public_chat.bluetooth;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.UUID;

import static synway.module_publicaccount.until.ConfigUtil.getBlueToothPath;

/**
 * Created by 杨思敏 on 2017/9/20.
 */

public  class Configutil {
    public static final UUID UUID= java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    /**
     * 如果该文件名已经存在，则删除
     */

    public static  String getFileName(String fileName) {
        File file = new File(getBlueToothPath() + "/" + fileName);
        if (file.exists()) {
            if(file.delete()){
                Log.i("testy","删除"+fileName+"成功");
            }
        }
        return fileName;
    }

}
