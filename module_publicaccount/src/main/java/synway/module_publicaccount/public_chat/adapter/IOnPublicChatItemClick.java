package synway.module_publicaccount.public_chat.adapter;

import synway.module_publicaccount.analytical.obj.view.Obj_ViewUrlTxt;

/**
 * Created by leo on 2019/1/17.
 */

public interface IOnPublicChatItemClick {
    //图文消息中Obj_ViewUrlTxt类型item点击接口
    void onUrlTextItemClick(Obj_ViewUrlTxt obj_viewUrlTxt);

    void onTaskNoticeItemClick(int position);

}
