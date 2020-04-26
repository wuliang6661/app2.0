package im.bean;

import im.utils.StringUtil;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/7/2316:55
 * desc   :  服务端返回的消息状态报告
 * version: 1.0
 */
public class ServerStateMessage extends BaseMessage implements Cloneable {


    @Override
    public int hashCode() {
        try {
            return this.msgId.hashCode();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof ServerStateMessage)) {
            return false;
        }

        return StringUtil.equals(this.msgId, ((ServerStateMessage) obj).getMsgId());
    }

}
