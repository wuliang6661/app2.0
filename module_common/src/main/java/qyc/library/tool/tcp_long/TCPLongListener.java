package qyc.library.tool.tcp_long;

import java.util.ArrayList;

import qyc.library.tool.main_thread.MainThread;

/**
 * Created by qyc on 2016/6/6.
 */
public class TCPLongListener {
    private ArrayList<OnTcpLongListen> onTcpLongList = new ArrayList<OnTcpLongListen>();

    /** 设置长连接监听 */
    void addTcpLongListen(OnTcpLongListen onTcpLong) {
        onTcpLongList.add(onTcpLong);
    }

    /** 取消长连接监听 */
    void removeTcpLongListen(OnTcpLongListen onTcpLong) {
        onTcpLongList.remove(onTcpLong);
    }

    void onReceive(final Object obj) {
        MainThread.joinMainThread(new MainThread.Runnable_MainThread() {

            @Override
            public void run() {
                for (int i = 0; i < onTcpLongList.size(); i++) {
                    onTcpLongList.get(i).onReceive(obj);
                }
            }

        });
    }

    void onReLogin() {

        MainThread.joinMainThread(new MainThread.Runnable_MainThread() {

            @Override
            public void run() {
                for (int i = 0; i < onTcpLongList.size(); i++) {
                    onTcpLongList.get(i).onReLogin();
                }
            }

        });
    }

    void onReLoginResult(final boolean result) {

        MainThread.joinMainThread(new MainThread.Runnable_MainThread() {

            @Override
            public void run() {
                for (int i = 0; i < onTcpLongList.size(); i++) {
                    onTcpLongList.get(i).onReLoginResult(result);
                }
            }

        });
    }

    public interface OnTcpLongListen {

        /** 收到数据 */
        void onReceive(Object obj);

        /** 长连接正在重新注册 */
        void onReLogin();

        /** 长连接注册结果 */
        void onReLoginResult(boolean result);
    }
}
