package synway.module_publicaccount.until;

/**
 * Created by ysm on 2017/1/6.
 */

public class IpPortAddr {
    private String ip;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    private int port;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }



    public  IpPortAddr(String ip, int port){
      this.ip=ip;
        this.port=port;
    }
}
