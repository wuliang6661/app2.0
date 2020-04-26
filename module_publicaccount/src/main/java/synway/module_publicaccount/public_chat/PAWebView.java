package synway.module_publicaccount.public_chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * Created by leo on 2019/2/20.
 */

public class PAWebView {
    /**
     * 应用ID
     */
    public static final String EXTRA_ID = "ID";
    public static final String EXTRA_URL = "URL";
    public static final String EXTRA_NAME = "NAME";
    public static final String EXTRA_IS_SHOW_TITLE = "IsShowTitle";
    public static final String EXTRA_URL_PARAM = "URL_PARAM";

    public static final String EXTRA_FACE = "FACE";
    public static final String EXTRA_IS_FINISH = "isFinish";
    public static final String EXTRA_IS_ALL = "isAll";
    public static final String EXTRA_PUBLIC_ID = "PublicId";
    public static final String EXTRA_URL_END = "URL_postfix";
    public static final String EXTRA_USER_ID = "userId";


    public static PAWebViewBuilder builder() {
        return new PAWebViewBuilder();
    }

    public static class PAWebViewBuilder {
        private Intent mIntent;
        private Bundle mOptionsBundle;


        public PAWebViewBuilder() {
            mIntent = new Intent();
            mOptionsBundle = new Bundle();
        }

        public void start(@NonNull Context context) {
            context.startActivity(getIntent(context));
        }


        public PAWebViewBuilder setUrl(String url) {
            mOptionsBundle.putString(EXTRA_URL, url);
            return this;
        }
        public PAWebViewBuilder setId(String id) {
            mOptionsBundle.putString(EXTRA_ID, id);
            return this;
        }
        public PAWebViewBuilder setName(String name) {
            mOptionsBundle.putString(EXTRA_NAME, name);
            return this;
        }

        public PAWebViewBuilder setIsShowTitle(int isShowTitle) {
            mOptionsBundle.putInt(EXTRA_IS_SHOW_TITLE, isShowTitle);
            return this;
        }


        public PAWebViewBuilder setUrlParam(String urlParam) {
            mOptionsBundle.putString(EXTRA_URL_PARAM, urlParam);
            return this;
        }

        public PAWebViewBuilder setFace(String faceImgPath) {
            mOptionsBundle.putString(EXTRA_FACE, faceImgPath);
            return this;
        }

        public PAWebViewBuilder setUrlEnd(String urlEnd) {
            mOptionsBundle.putString(EXTRA_URL_END, urlEnd);
            return this;
        }


        public PAWebViewBuilder setIsAll(boolean isAll) {
            mOptionsBundle.putBoolean(EXTRA_IS_ALL, isAll);
            return this;
        }

        public PAWebViewBuilder setPublicId(String publicId) {
            mOptionsBundle.putString(EXTRA_PUBLIC_ID, publicId);
            return this;
        }


        public PAWebViewBuilder setIsFinish(boolean isFinish) {
            mOptionsBundle.putBoolean(EXTRA_IS_FINISH, isFinish);
            return this;
        }

        public PAWebViewBuilder setUserId(String userID) {
            mOptionsBundle.putString(EXTRA_USER_ID, userID);
            return this;
        }


        private Intent getIntent(@NonNull Context context) {
            mIntent.setClass(context, PAWebViewAct.class);
            mIntent.putExtras(mOptionsBundle);
            return mIntent;
        }


    }
}
