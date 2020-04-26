package synway.module_publicaccount.notice.job;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgNotice;
import synway.module_publicaccount.analytical.obj.noticeview.ClickItem;
import synway.module_publicaccount.analytical.obj.noticeview.ClickUrlItem;
import synway.module_publicaccount.analytical.obj.noticeview.ContentItem;
import synway.module_publicaccount.analytical.obj.noticeview.SectionItem;
import synway.module_publicaccount.analytical.obj.noticeview.TagItem;
import synway.module_publicaccount.db.table_util.Table_PublicAccount_Notice;

/**
 * Created by leo on 2018/10/23.
 */

public class FirstLoadNoticeRecord {

    public static ArrayList<Obj_PublicMsgNotice> load(SQLiteDatabase db, int type){

        int startIndex = 0;
        int count = 25;

        String sql;
        if (type == 0) {
            //全部
            sql = "select * from " + Table_PublicAccount_Notice._TABLE_NAME + " order by " +
                Table_PublicAccount_Notice.PUBLIC_NOTICE_COL_LOCALETIME + " desc  " + " limit " +
                startIndex + "," + count;
        } else {
            //取固定pageType的记录
            sql = "select * from " + Table_PublicAccount_Notice._TABLE_NAME + " where " +
                Table_PublicAccount_Notice.PUBLIC_NOTICE_COL_PAGE_TYPE + " = " + type + " order by " +
                Table_PublicAccount_Notice.PUBLIC_NOTICE_COL_LOCALETIME + " desc  " + " limit " +
                startIndex + "," + count;
        }


        Cursor cursor = db.rawQuery(sql, null);

        ArrayList<Obj_PublicMsgNotice> resultList = new ArrayList<>();
        while (cursor.moveToNext()) {
            Obj_PublicMsgNotice notice = cursor2Obj(cursor);
            if (notice != null) {
                resultList.add(notice);
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        return resultList;
    }


    private static Obj_PublicMsgNotice cursor2Obj(Cursor cursor) {
        Obj_PublicMsgNotice obj_publicMsgNotice = new Obj_PublicMsgNotice();

        obj_publicMsgNotice.noticeMsgID = cursor.getString(cursor.getColumnIndex(
            Table_PublicAccount_Notice.PUBLIC_NOTICE_COL_MSG_ID));
        obj_publicMsgNotice.publicGUID = cursor.getString(cursor.getColumnIndex(
            Table_PublicAccount_Notice.PUBLIC_NOTICE_COL_MSG_ID));
        obj_publicMsgNotice.pageType = cursor.getInt(cursor.getColumnIndex(
            Table_PublicAccount_Notice.PUBLIC_NOTICE_COL_PAGE_TYPE));
        obj_publicMsgNotice.localTime = Long.valueOf(cursor.getString(cursor.getColumnIndex(
            Table_PublicAccount_Notice.PUBLIC_NOTICE_COL_LOCALETIME)));
        obj_publicMsgNotice.showTime = cursor.getString(cursor.getColumnIndex(
            Table_PublicAccount_Notice.PUBLIC_NOTICE_COL_SERVERTIME));
        obj_publicMsgNotice.MsgType = cursor.getInt(cursor.getColumnIndex(
            Table_PublicAccount_Notice.PUBLIC_NOTICE_COL_MSGTYPE));

        String msgInfoJson = cursor.getString(cursor.getColumnIndex(Table_PublicAccount_Notice.PUBLIC_NOTICE_COL_MSG));
        try {
            JSONObject jsonObject = new JSONObject(msgInfoJson);
            obj_publicMsgNotice.title = jsonObject.optString("TITLE");

            obj_publicMsgNotice.pageType = jsonObject.optInt("PAGETYPE");

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
                    //URL类型
                    JSONObject urlJsonObj = clickJsonObj.optJSONObject("URL");
                    if (urlJsonObj == null) {
                        clickItem.clickUrlItem = null;
                    } else {
                        ClickUrlItem clickUrlItem = new ClickUrlItem();
                        clickUrlItem.url = urlJsonObj.optString("URL");
                        clickUrlItem.urlName = urlJsonObj.optString("URLNAME");
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


        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return obj_publicMsgNotice;
    }

    private static String checkNull(String s) {
        if (s == null) {
            return "";
        } else {
            return s;
        }
    }
}
