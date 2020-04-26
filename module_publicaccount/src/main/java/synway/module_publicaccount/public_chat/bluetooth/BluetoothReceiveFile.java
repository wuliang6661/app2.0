package synway.module_publicaccount.public_chat.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import java.io.DataInputStream;
import java.io.IOException;
import static synway.module_publicaccount.public_chat.bluetooth.Configutil.UUID;

/**
 * Created by 杨思敏 on 2017/9/19.
 * 蓝牙接收段接口
 */

public class BluetoothReceiveFile {

    public BluetoothReceiveFile(ReceiveAdapter receiveAdapter) {
        this.receiveAdapter = receiveAdapter;
    }

    private ReceiveAdapter receiveAdapter = null;
    private BluetoothServerSocket serverSocket;
    private BluetoothSocket bluetoothSocket;
    private volatile boolean isCloseing = false;
    private volatile AcceptThread acceptThread = null;
    /**
     * 开始接收蓝牙数据,当不再接收时,需要调用{@link #stop()}来停止接收.
     */
    public void start() {
        if (acceptThread != null) {
            try {
                acceptThread.join();
            } catch (Exception e) {
                //并发空指针也捕获掉
            }
        }


        acceptThread = new AcceptThread();
        acceptThread.start();

    }

    /**
     * 停止接收蓝牙数据.
     */
    public void stop() {
        isCloseing = true;
        mClose(serverSocket);
        mClose(bluetoothSocket);
    }

    private static void mClose(BluetoothSocket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("testy","关闭失败"+e.toString());
            }
        }
    }

    private static void mClose(BluetoothServerSocket serverSocket) {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //服务端监听客户端的线程类
    private class AcceptThread extends Thread {

        private boolean isRun = false;

        private AcceptThread() {
            BluetoothAdapter blueToothAdapter = BluetoothAdapter.getDefaultAdapter();
            try {
                serverSocket = blueToothAdapter.listenUsingRfcommWithServiceRecord("Bluetooth_Socket", UUID);
                isRun = true;
            } catch (IOException e) {
                mClose(serverSocket);
                isRun = false;
            }

        }

        public void run() {
            if (!isRun) {
                return;
            }
            while (true) {
                try {
                    bluetoothSocket = serverSocket.accept();
                    Log.i("testy", "开启监听");
                } catch (Exception e) {
                    Log.i("testy", "开启监听错误" + e.toString());
                    mClose(serverSocket);
                    break;
                }
                //防accept极限并发
                if (isCloseing) {
                    mClose(bluetoothSocket);
                    continue;
                }
                receive:
                {
                    DataInputStream dataInputStream;
                    try {
                        dataInputStream = new DataInputStream(bluetoothSocket.getInputStream());
                    } catch (IOException e) {
                        Log.i("testy","监听接收客户端失败"+e.toString());
                        mClose(bluetoothSocket);
                        break receive;
                    }

                    if (receiveAdapter != null) {
                        receiveAdapter.onReceive(dataInputStream);
                    }

                    mClose(bluetoothSocket);
                }

                Log.d("testy", "本次接收结束");

            }
            acceptThread = null;
            Log.d("testy", "线程结束");
        }
    }


}
