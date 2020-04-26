package synway.module_publicaccount.public_chat.bluetooth;

import java.io.DataInputStream;

/**
 * 数据接收适配器
 * Created by ysm on 2017/9/25.
 */

public abstract class ReceiveAdapter {

    /**
     * 执行完onReceive后就断开连接.
     */
    public abstract void onReceive(DataInputStream inputStream);
}
