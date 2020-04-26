package synway.module_publicaccount.analytical.fac;

import android.content.Context;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgBase;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgTaskNotice;
import synway.module_publicaccount.analytical.obj.noticeview.ClickItem;
import synway.module_publicaccount.analytical.obj.noticeview.ClickUrlItem;
import synway.module_publicaccount.analytical.obj.noticeview.ContentItem;
import synway.module_publicaccount.analytical.obj.noticeview.SectionItem;
import synway.module_publicaccount.analytical.obj.noticeview.TagItem;

/**
 * Created by leo on 2019/1/17.
 */

public class Analytical_TaskNotice implements IAnalytical_Base{
    private Context context = null;


    @Override public int msgType() {
        return 8;
    }


    @Override public void onInit(Context context) {
        this.context = context;
    }


    @Override public Obj_PublicMsgBase onDeal(JSONObject jsonObject) {
        Obj_PublicMsgTaskNotice obj_publicMsgNotice = new Obj_PublicMsgTaskNotice();
        obj_publicMsgNotice.title = jsonObject.optString("TITLE");

        JSONArray topTagArray = jsonObject.optJSONArray("TOPTAGS");

        if (topTagArray != null && topTagArray.length() > 0) {
            ArrayList<TagItem> topTags = new ArrayList<>();

            for (int i = 0; i < topTagArray.length(); i++) {
                JSONObject tagJsonObj = topTagArray.optJSONObject(i);
                if (tagJsonObj != null) {
                    TagItem tagItem = new TagItem();
                    tagItem.bgColor = tagJsonObj.optString("BGCOLOR");
                    tagItem.tagText = tagJsonObj.optString("TEXT");
                    topTags.add(tagItem);
                }
            }
            obj_publicMsgNotice.topTags = topTags;

        } else {
            obj_publicMsgNotice.topTags = null;
        }

        JSONArray bottomTagArray = jsonObject.optJSONArray("BOTTOMTAGS");

        if (bottomTagArray != null && bottomTagArray.length() > 0) {
            ArrayList<TagItem> bottomTags = new ArrayList<>();

            for (int i = 0; i < bottomTagArray.length(); i++) {
                JSONObject tagJsonObj = bottomTagArray.optJSONObject(i);
                if (tagJsonObj != null) {
                    TagItem tagItem = new TagItem();
                    tagItem.bgColor = tagJsonObj.optString("BGCOLOR");
                    tagItem.tagText = tagJsonObj.optString("TEXT");
                    bottomTags.add(tagItem);
                }
            }

            obj_publicMsgNotice.bottomTags = bottomTags;
        } else {
            obj_publicMsgNotice.bottomTags = null;
        }

        JSONObject trangleJsonObj = jsonObject.optJSONObject("TRIANGLETAG");
        if (trangleJsonObj == null) {
            obj_publicMsgNotice.triangleTag = null;
        } else {
            TagItem tagItem = new TagItem();
            tagItem.bgColor = trangleJsonObj.optString("BGCOLOR");
            tagItem.tagText = trangleJsonObj.optString("TEXT");
            obj_publicMsgNotice.triangleTag = tagItem;
        }

        JSONObject clickJsonObj = jsonObject.optJSONObject("CLICKLINK");

        if (clickJsonObj == null) {
            obj_publicMsgNotice.clickItem = null;
        } else {
            ClickItem clickItem = new ClickItem();
            clickItem.type = clickJsonObj.optInt("TYPE");
            clickItem.clickInfoJson = clickJsonObj.toString();
            if (clickItem.type == 1) {
                //URL类型(包括了 html 和 weex)
                JSONObject urlJsonObj = clickJsonObj.optJSONObject("URL");
                if (urlJsonObj == null) {
                    clickItem.clickUrlItem = null;
                } else {
                    ClickUrlItem clickUrlItem = new ClickUrlItem();

                    clickUrlItem.url = urlJsonObj.optString("URL");
                    clickUrlItem.urlName = urlJsonObj.optString("URLNAME");
                    clickUrlItem.urlType = urlJsonObj.optInt("URLTYPE");
                    clickUrlItem.sourceUrl = urlJsonObj.optString("URLPARM");
                    clickUrlItem.isShowTitle = urlJsonObj.optInt("ISSHOWTITLE",1);

                    clickItem.clickUrlItem = clickUrlItem;
                }
            } else {
                clickItem.clickUrlItem = null;
            }
            obj_publicMsgNotice.clickItem = clickItem;
        }

        JSONArray sectionsArray = jsonObject.optJSONArray("SECTIONS");
        if (sectionsArray == null || sectionsArray.length() ==0 ) {
            obj_publicMsgNotice.sections = null;
        } else {
            ArrayList<SectionItem> sectionList = new ArrayList<>();
            for (int i = 0; i < sectionsArray.length(); i++) {
                SectionItem sectionItem = new SectionItem();
                JSONObject sectionJsonObj = sectionsArray.optJSONObject(i);
                if (sectionJsonObj == null) {
                    continue;
                } else {
                    sectionItem.type = sectionJsonObj.optInt("TYPE");
                    if (sectionItem.type == 1) {
                        //文字段落
                        JSONObject contentJsonObj = sectionJsonObj.optJSONObject("CONTENT");
                        if (contentJsonObj == null) {
                            sectionItem.contentItem = null;
                        } else {
                            ContentItem contentItem = new ContentItem();
                            contentItem.contentText = contentJsonObj.optString("TEXT");
                            contentItem.contentColor = contentJsonObj.optString("COLOR");
                            contentItem.contentSize = contentJsonObj.optInt("SIZE");
                            contentItem.isContentBold = contentJsonObj.optBoolean("BOLD");
                            contentItem.isContentUnderline = contentJsonObj.optBoolean("UNDERLINE");
                            contentItem.isContentItalic = contentJsonObj.optBoolean("ITAlIC");
                            sectionItem.contentItem = contentItem;
                        }
                    } else if (sectionItem.type == 2) {
                        sectionItem.contentItem = null;
                    } else {
                        sectionItem.contentItem = null;
                    }
                    sectionList.add(sectionItem);
                }
            }

            obj_publicMsgNotice.sections = sectionList;
        }

        return obj_publicMsgNotice;
    }
}
