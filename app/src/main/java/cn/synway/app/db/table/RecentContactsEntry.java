package cn.synway.app.db.table;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.NameInDb;
import io.objectbox.relation.ToOne;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/7/2310:34
 * desc   :  最新联系人表
 * version: 1.0
 */
@Entity
public class RecentContactsEntry {

    @Id
    public long id;

    /**
     * 所属的用户
     */
    @NameInDb("user_id")
    public String userId;

    /**
     * 最近联系人类型
     * <p>
     * 0=单聊，1=群聊，2=公众
     */
    @NameInDb("contact_type")
    public String contactType;

    /**
     * 最近联系人id
     */
    @NameInDb("contact_id")
    public String contactId;

    /**
     * 最近联系人名字
     */
    @NameInDb("contact_name")
    public String contactName;

    /**
     * 最近联系人头像
     */
    @NameInDb("contact_head")
    public String contactHeadUrl;

    /**
     * 未读消息数
     */
    @NameInDb("contact_unread_num")
    public int unReadNum;

    /**
     * 最后一条消息(表关联)
     */
    public ToOne<MessageEntry> chatMsg;

    /**
     * 最后一条消息的本机时间，用于排序
     */
    @NameInDb("last_msg_local_time")
    public long msgLocalTime;

    /**
     * 是否置顶，值越大，置顶优先级越高
     */
    @NameInDb("index_top")
    public int topIndex;

    public RecentContactsEntry() {

    }


    public RecentContactsEntry(String contactType, String contactId, String contactName, String contactHeadUrl,
                               int unReadNum, long msgLocalTime, int topIndex) {
        this.contactType = contactType;
        this.contactId = contactId;
        this.contactName = contactName;
        this.contactHeadUrl = contactHeadUrl;
        this.unReadNum = unReadNum;
        this.msgLocalTime = msgLocalTime;
        this.topIndex = topIndex;
    }
}
