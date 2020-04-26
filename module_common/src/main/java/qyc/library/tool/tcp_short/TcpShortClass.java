package qyc.library.tool.tcp_short;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TCP短连接
 */
public class TcpShortClass {

    private Object returnObj = null;

    public Object post(String IP, int port, byte[] data, long milliseconds, TcpShortAdapter shortAdapter) {
        // 启动线程
        Thread thread = new Thread(new TcpShortThread(IP, port, data, milliseconds, shortAdapter));
        thread.start();
        // 等待线程来填充返回结果
        try {
            thread.join();
        } catch (InterruptedException e) {
        }
        // 返回结果
        return returnObj;
    }

    // TCP收发线程
    private class TcpShortThread implements Runnable {
        private String ip; // 服务器IP
        private int port; // 服务器端口
        private byte[] data; // 发给服务器的数据
        private long outTime; // 超时时间
        private TcpShortAdapter shortAdapter;// 解析适配器
        private Socket socket = null;// SOCKET
        private boolean isOutTimeShutDown = false;// 在SOCKET结束接收时,用来判断是不是由超时计时器关闭接收流,而导致的接收结束.这决定了返回结果是超时还是其他

        public TcpShortThread(String ip, int port, byte[] data, long outTime, TcpShortAdapter shortAdapter) {
            this.ip = ip;
            this.port = port;
            this.data = data;
            this.outTime = outTime;
            this.shortAdapter = shortAdapter;
        }

        @Override
        public void run() {
            socket = new Socket();
            // 建立Socket连接
            InetSocketAddress remoteAddr = new InetSocketAddress(ip, port);
            try {
                socket.connect(remoteAddr, 10000);
            } catch (IOException e) {
                // 连接失败,直接返回
                returnObj = new TcpShortFail("[" + ip + ":" + port + "]网络连接失败。", e.toString());
                return;
            }

            // 发送数据
            try {
                socket.getOutputStream().write(data);
                socket.getOutputStream().flush();
            } catch (IOException e) {
                // 数据发送失败,尝试关闭socket,并直接返回.仍然提示网络连接失败.
                try {
                    socket.close();
                } catch (IOException e1) {
                }
                returnObj = new TcpShortFail("数据发送失败。", e.toString());
                return;
            }

            // 开始接收数据,同时启动计时器.当计时器时间到的时候,直接关闭socket输入流
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    // 直接关闭socket输入流,这会停止接收部分.设置超时变量为true,在最后反馈时将反馈超时
                    // TIP:这里和接收部分并没有加锁,是因为不必加.在接收部分的任何时间,从等待数据,到适配器处理,
                    // 到接收部分自行结束而尚未关闭计时器的一瞬间,只要计时器触发,都认为是超时的.而尝试关闭输入流
                    // 并不会和接收部分的任何环节起冲突.因此不加锁.
                    isOutTimeShutDown = true;
                    try {
                        socket.shutdownInput();
                    } catch (IOException e) {
                    }
                }
            }, outTime);

            // 接收数据部分
            byte[] allData = new byte[0];
            byte[] receiveData = new byte[1024];
            int readLength = 0;
            while (true) {
                try {
                    readLength = socket.getInputStream().read(receiveData);
                } catch (IOException e) {
                    // 接收出现异常,立即结束接收部分.
                    returnObj = new TcpShortFail("数据处理失败", e.toString());
                    break;
                }
                if (readLength < 0) {
                    // 接收出现异常,立即结束接收部分.
                    returnObj = new TcpShortFail("数据处理失败", "网络被中断了");
                    break;
                }

                //数据解析部分
                if (shortAdapter != null) {
                    // 有适配器的情况,就合并数据,统一让适配器来解析,若适配器返回结果时表示解析已经完成
                    allData = byteCopy(allData, allData.length, receiveData, readLength);
                    Object result = shortAdapter.onTcpShortDecoder(allData);
                    if (result == null) {
                        continue;// 适配器返回null表示仍然要继续接收.
                    } else {
                        returnObj = result;// 适配器返回结果,表示可以结果接收部分了.
                        break;
                    }
                } else {
                    // 没有适配器的情况,收到任何数据都结束接收部分
                    returnObj = null;
                    break;
                }
            }

            // 数据接收部分结束,关闭计时器.
            timer.cancel();

            // 关闭socket
            try {
                socket.close();
            } catch (IOException e) {
            }

            // 如果是超时导致的结束,将returnObj重新设置为超时的提示.
            if (isOutTimeShutDown) {
                returnObj = new TcpShortFail("请求超时，请重试。", "超时计时器已经关闭了接收流");
            }

        }
    }

    private static final byte[] byteCopy(byte[] byte1, int byte1Length, byte[] byte2, int byte2Length) {
        byte[] byteTemp_receiveByte = new byte[byte1Length + byte2Length];
        System.arraycopy(byte1, 0, byteTemp_receiveByte, 0, byte1Length);
        System.arraycopy(byte2, 0, byteTemp_receiveByte, byte1Length, byte2Length);
        return byteTemp_receiveByte;
    }

    public class TcpShortFail {
        public TcpShortFail(String msg, String detialMsg) {
            this.msg = msg;
            this.detialMsg = detialMsg;
        }

        public String msg;
        public String detialMsg;
    }

}
