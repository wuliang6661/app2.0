package synway.module_publicaccount.map.view;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;


import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import synway.module_publicaccount.R;


public class Map_Ready extends Activity {
    private GifImageView gif1 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.model_public_account_map__ready);
//        gif1 = (GifImageView) findViewById(R.id.gif1);
//        initGifView(gif1);
    }
    private void initGifView(GifImageView gif1) {
        try {
            GifDrawable gifDrawable = new GifDrawable(getResources(), R.drawable.voice);
            gif1.setImageDrawable(gifDrawable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // gif1加载一个动态图gif

//        String gifFilePath = "file:///android_asset/voice.pl.droidsonroids.gif";//首先将一张gif格式的动图放置在assets中
//        playingView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        String data = "<HTML><Div align=\"center\" margin=\"0px\"><IMG src=\"" + gifFilePath + "\" margin=\"0px\"/></Div>";//设置图片位于webview的中间位置
//        playingView.loadDataWithBaseURL(gifFilePath, data, "text/html", "utf-8", null);
//        WebSettings webSettings= playingView.getSettings(); // webView: 类WebView的实例
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);  //就是这句
//        playingView.loadUrl("file:///android_asset/voice.pl.droidsonroids.gif");
//        playingView.setVisibility(View.VISIBLE);
//        playingView.setFocusable(false);
    }

}
