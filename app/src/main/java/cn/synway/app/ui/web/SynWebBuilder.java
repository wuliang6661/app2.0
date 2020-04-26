package cn.synway.app.ui.web;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * webview 的配置build
 */
public class SynWebBuilder {
    /**
     * 应用ID
     */
    public static final String EXTRA_ID = "ID";
    /**
     * 加载的url
     */
    public static final String EXTRA_URL = "URL";
    /**
     * 标题栏名称
     */
    public static final String EXTRA_NAME = "NAME";
    /**
     * 是否显示标题栏
     */
    public static final String EXTRA_IS_SHOW_TITLE = "IsShowTitle";
    /**
     * 右键全文检索时搜索的参数
     */
    public static final String EXTRA_URL_PARAM = "URL_PARAM";

    /**
     * 右键人脸识别时传入的人脸识别地址
     */
    public static final String EXTRA_FACE = "FACE";

    /**
     * url后面拼接的参数
     */
    public static final String EXTRA_URL_END = "URL_end";


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

        private Intent getIntent(@NonNull Context context) {
            mIntent.setClass(context, SynWebActivity.class);
            mIntent.putExtras(mOptionsBundle);
            return mIntent;
        }


    }
}
