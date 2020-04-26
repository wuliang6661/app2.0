package synway.module_publicaccount.push;

/**
 * 推送类的广播
 *
 * @author 刘杰 [2015年8月7日 下午1:45:35]
 * @author 钱园超 [2015年8月7日 下午1:45:35]
 */
public class PushUtil {

    public static final class ServiceTask {
        public static final String ACTION_ADD = "push.serviceTask.add";
        public static final String ACTION_REMOVE = "push.serviceTask.remove";
    }

    /**
     * 公众号 消息推送
     *
     * @author 刘杰[20151125]
     */
    public static class PublicNewMsg {

        public static final String ACTION = "push.public.newMsg.action";
        public static final String GISACTION = "push.public.newMsg.action.gis";
        public static final String NOTICE_ACTION = "push.public.newMsg.action.notice";
        public static final String getAction(String targetID) {
            return ACTION + "_" + targetID;
        }
        public static final String getGisAction(String targetID) {
            return GISACTION + "_" + targetID;
        }
        /**
         * 公众功能guid
         */
        public static final String EXTRA_PUBLIC_NEWMSG_GUID = "push.public.newMsg.guid";

        /**
         * 消息类型
         */
        public static final String EXTRA_PUBLIC_NEWMSG_MSGTYPE = "push.public.newMsg.msgType";

        /**
         * 消息推送分页Code，用于分类展示
         */
        public static final String EXTRA_PUBLIC_NEWMSG_MSG_PUSH_PAGE_CODE = "push.public.newMsg.msgPushPageCode";
        /**
         * 消息推送分页Name，用于分类展示
         */
        public static final String EXTRA_PUBLIC_NEWMSG_MSG_PUSH_PAGE_NAME = "push.public.newMsg.msgPushPageName";

        /**
         * 这条消息是否是修改的
         */
        public static final String EXTRA_PUBLIC_NEWMSG_MSG_IS_UPDATE = "push.public.newMsg.msgIsUpdate";

        /**
         * 消息msgGUID
         */
        public static final String EXTRA_PUBLIC_NEWMSG_MSG_GUID = "push.public.newMsg.msgGUID";

        /**
         * toUser
         */
        public static final String EXTRA_PUBLIC_NEWMSG_TOUSER = "push.public.newMsg.toUser";

        /**
         * json 消息内容
         */
        public static final String EXTRA_PUBLIC_NEWMSG_SOBJ = "push.public.newMsg.msg";

        /**
         * 消息时间
         */
        public static final String EXTRA_PUBLIC_NEWMSG_TIME = "push.public.newMsg.time";
    }

    /**
     * 所以最近联系人的未读数总和
     *
     * @author 刘杰 [20151118]
     */
    public static class AllMsgUnReadCount {

        public static final String EXTRA_ALL_UNREADCOUNT = "push.allUnReadCount.extra.count";

        public static final String ACTION = "push.allUnReadCount.action";
    }

    /**
     * 新的最近联系人的广播
     *
     * @author 钱园超 [2015年8月14日 上午10:02:03]
     */
    public static class NewLastContact {

        /**
         * 最近联系人类型，tinyint，0=单聊，1=群聊，2=公众
         */
        public static final String TARGET_TYPE = "pushUtil.newLastContact.extra.TargetType";

        /**
         * 目标Id，varchar(40)
         */
        public static final String TARGET_ID = "pushUtil.newLastContact.extra.TargetId";

        /**
         * 目标名称，varchar(50)
         */
        public static final String TARGET_NAME = "pushUtil.newLastContact.extra.TargetName";

        /**
         * 最近一次通讯时间， dateTime
         */
        public static final String LAST_MSG_SERVER_TIME = "pushUtil.newLastContact.extra.LastMsgServerTime";

        /**
         * 最近一次通讯的内容， varchar
         */
        public static final String LAST_MSG_CONTENT = "pushUtil.newLastContact.extra.LastMsgContent";

        /**
         * 未读消息数量
         */
        public static final String UN_READ_COUNT = "pushUtil.newLastContact.extra.UnReadCount";

        /**
         * 消息产生者的ID
         */
        public static final String FROM_USER_ID = "pushUtil.newLastContact.extra.FromUserID";

        /**
         * 消息产生者的名字
         */
        public static final String FROM_USER_NAME = "pushUtil.newLastContact.extra.FromUserName";

        public static final String ACTION = "pushUtil.newLastContact.action.newLastContact";

    }

    /**
     * 新消息的广播
     *
     * @author 钱园超 [2015年8月7日 下午1:44:14]
     */
    public static class NewMsg {

        /**
         * 收到一条别人发来的新的聊天消息
         */
        public static final String EXTRA_MSG_ID = "pushUtil.newMsg.extra.msgID";

        public static final String EXTRA_MSG_INDEX = "pushUtil.newMsg.extra.msgIndex";

        public static final String EXTRA_CHAT_TYPE = "pushUtil.newMsg.extra.chatType";

        public static final String EXTRA_GROUP_ID = "pushUtil.newMsg.extra.groupID";

        public static final String EXTRA_FROM_USER_AREA = "pushUtil.newMsg.extra.fromUserArea";

        public static final String EXTRA_FROM_USER_ID = "pushUtil.newMsg.extra.fromUserID";

        public static final String EXTRA_FROM_USER_NAME = "pushUtil.newMsg.extra.fromUserName";

        public static final String EXTRA_TO_USER_AREA = "pushUtil.newMsg.extra.toUserArea";

        public static final String EXTRA_TO_USER_ID = "pushUtil.newMsg.extra.toUserID";

        public static final String EXTRA_TO_USER_NAME = "pushUtil.newMsg.extra.toUserName";

        public static final String EXTRA_MSG = "pushUtil.newMsg.extra.msg";

        public static final String EXTRA_SERVER_TIME = "pushUtil.newMsg.extra.serverTime";

        public static final String EXTRA_LOCALE_TIME = "pushUtil.newMsg.extra.localeTime";

        public static final String EXTRA_ISREAD = "pushUtil.newMsg.extra.isRead";

        public static final String EXTRA_PLAY_LENGTH = "pushUtil.newMsg.extra.playLength";

        private static final String ACTION = "pushUtil.newMsg.action.newMsg";

        public static final String getAction(String targetID) {
            return ACTION + "_" + targetID;
        }
    }

    /**
     * 消息接收回执
     *
     * @author 赵江武 [2016年1月27日 ]
     */
    public static class MsgReceived {
        /**
         * 收到自己发送的某条消息已经被接受
         */
        public static final String RECEIVED_MSG_ID = "pushUtil.newMsg.msgReceived.receivedMsgID";
        public static final String TARGET_ID = "pushUtil.newMsg.msgReceived.targetID";
        public static final String MSG_RECEIVED = "pushUtil.newMsg.msgReceived";

    }
    /**
     * 群消息接收回执
     *
     * @author 赵江武 [2016年10月27日 ]
     */
    public static class GroupMsgReceived {
        /**
         * 收到自己发送的某条消息已经被接受
         */
        public static final String RECEIVED_MSG_ID = "pushUtil.newMsg.groupmsgReceived.receivedMsgID";
        public static final String GROUP_ID = "pushUtil.newMsg.groupmsgReceived.groupID";
        public static final String RECEIVED_MEMBER_ID = "pushUtil.newMsg.groupmsgReceived.receivedMemberID";
        public static final String GROUPMSG_RECEIVED = "pushUtil.newMsg.groupMsgReceived";

    }

    /**
     * 暴力杀死service
     */
    public static final String ACTION_SERVICE_EXIT = "synosc.push.killService";

    /**
     * 公众号消息推送weex页面
     *
     * @author 黄曦 [2019年1月3日 ]
     */
    public static class PublicWeexMsg {

        public static final String ACTION = "push.public.weexMsg.action";
        public static final String getAction(String targetID) {
            return ACTION + "_" + targetID;
        }
        /**
         * 公众功能guid
         */
        public static final String EXTRA_PUBLIC_WEEXMSG_GUID = "push.public.weexMsg.guid";

        /**
         * json 消息内容
         */
        public static final String EXTRA_PUBLIC_WEEXMSG_SOBJ = "push.public.weexMsg.msg";

        /**
         * 消息时间
         */
        public static final String EXTRA_PUBLIC_WEEXMSG_TIME = "push.public.weexMsg.time";
    }


    /**
     * 公众号删除消息
     */
    public static class PublicDeleteMsg{
        public static final String ACTION = "push.public.deleteMsg.action";
        public static final String getAction(String targetID) {
            return ACTION + "_" + targetID;
        }

        //需要删除的消息guid
        public static final String EXTRA_PUBLIC_DELETE_MSG_GUID = "push.public.deleteMsg.guid";
    }



    /**
     * 公众号最近消息界面 关于公众号消息未读数减一的广播
     */
    public static class PublicLastUnreadCountMinus{
        public static final String ACTION = "push.public.publicLastUnReadCountMinus.action";

        public static final String EXTRA_PUBLIC_ACCOUNT_ID = "push.public.account.id";

        public static final String EXTRA_PUBLIC_UNREAD_COUNT = "push.public.unread.count";
    }








}