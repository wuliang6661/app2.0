package qyc.library.tool.tcp_long;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

import qyc.library.tool.tcp_long.TcpLongAdapter.AdapterResult;

/**
 * 负责初始化的线程，如果它初始化成功，就可以从它身上拿到一个已经建好的socket连接。
 * 如果它初始化失败，（不包括业务上的失败，例如密码错误），会自动销毁连接。
 *
 * @author 钱园超 [2015年7月29日 上午9:57:12]
 */
class InitRunnable implements Runnable {

    AdapterResult initResult = null;
    Socket socket = null;
    private String IP;
    private int port;
    private byte[] data; // 发给服务器的数据
    private long outTime; // 超时时间
    private TcpLongAdapter adapter;// 解析适配器
    private boolean isOutTimeShutDown = false;

    InitRunnable(String ip, int port, byte[] data, long outTime, TcpLongAdapter adapter) {
        this.IP = ip;
        this.port = port;
        this.data = data;
        this.outTime = outTime;
        this.adapter = adapter;
    }

    @Override
    public void run() {
        socket = new Socket();

        // 设置保持长连接(实际上这个对于手机网络来说无效,还是要通过人为的心跳来解决),设置异常也无所谓
        try {
            socket.setKeepAlive(true);
        } catch (SocketException e) {
        }

        // 建立Socket连接
        InetSocketAddress remoteAddr = new InetSocketAddress(IP, port);
        try {
            socket.connect(remoteAddr, 10000);
        } catch (IOException e) {
            // 连接失败,直接跳出
            initResult = new AdapterResult(null, new TcpLongInitFail("[" + IP + ":" + port + "]网络连接失败。", e.toString()));
            return;
        }

        // 发送数据
        try {
            socket.getOutputStream().write(data);
            socket.getOutputStream().flush();
        } catch (IOException e) {
            // 数据发送失败，直接跳出
            initResult = new AdapterResult(null, new TcpLongInitFail("数据发送失败。", "IOException\n" + e.toString()));
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
                System.out.println("TCP长连接-->初始化收到长度=" + readLength + "的数据");
            } catch (IOException e) {
                // 接收出现异常,立即跳出.
                initResult = new AdapterResult(null, new TcpLongInitFail("数据接收失败", "网络被中断了\n" + e.toString()));
                break;
            }
            if (readLength < 0) {
                // 接收出现异常,立即结束数据接收.
                initResult = new AdapterResult(null, new TcpLongInitFail("数据接收失败", "网络被中断了\nSocket输入流的read长度小于0"));
                break;
            }

            if (adapter != null) {
                // 有适配器的情况,就合并数据,统一让适配器来解析,若适配器返回结果时表示解析已经完成
                allData = byteCopy(allData, allData.length, receiveData, readLength);
                AdapterResult result = adapter.onInitDecode(allData);
                // result=null;
                if (result == null) {
                    continue;// 适配器返回null表示仍然要继续接收.
                } else {
                    initResult = result;// 适配器返回结果,表示已经完成了.
                    break;
                }
            } else {
                // 没有适配器的情况,收到任何数据都结束接收部分
                initResult = new AdapterResult(null, new TcpLongInitFail("无解析适配器", "无解析适配器"));
                break;
            }

        }

        // 数据接收部分结束,关闭计时器.
        timer.cancel();

        // 如果是超时导致的结束,将returnObj重新设置为超时的提示.
        if (isOutTimeShutDown) {
            initResult = new AdapterResult(null, new TcpLongInitFail("请求超时，请重试。", "超时计时器已经关闭了接收流"));
        }

        // 如果初始化是失败的，那么就关闭连接
        if (initResult.result instanceof TcpLongInitFail) {
            try {
                socket.close();
            } catch (IOException e) {
            }
            System.out.println("TCP长连接-->Init将socket关闭了");
        }
    }

    private static final byte[] byteCopy(byte[] byte1, int byte1Length, byte[] byte2, int byte2Length) {
        byte[] byteTemp_receiveByte = new byte[byte1Length + byte2Length];
        System.arraycopy(byte1, 0, byteTemp_receiveByte, 0, byte1Length);
        System.arraycopy(byte2, 0, byteTemp_receiveByte, byte1Length, byte2Length);
        return byteTemp_receiveByte;
    }

}
