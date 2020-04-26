package synway.module_publicaccount.public_chat.file_upload_for_camera.local_video;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.shuyu.gsyvideoplayer.activity.PlayActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import synway.module_publicaccount.R;


/**
 * 公众号本地视频上传，根据 抓捕本地视频修改
 *
 * @author 朱铁超
 */
public class LocalVideo extends Activity {

    private GridView mGridView;
    private List<String> mImgs;

    private List<Obj_item> mPaths;
    private ImageAndVideoAdapter mImageAndVideoAdapter;

//    private List<String> paths;

    private TextView mTv_Title;
    private ImageButton mBt_Back;

    private RelativeLayout mBottomLy;

    private TextView mTv_DirName;
    private TextView mTv_DirCount;

    private File mFile_CurrentDir;
    private int mMaxCount;
    private int totalCount = 0;

    private ProgressDialog mProgressDialog;

    private static final int DATA_LOADED = 0x110;


    private AsyncGetVideo asyncGetVideo;

    private int mSelectCount = 0;
    private List<String> mSelectPaths = new ArrayList<>();
    // 最大的可被选中的项数，默认1
    private int mMaxSelectNum = 1;

    // gridView的列数，默认3
    private int mGridViewColumn = 3;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (msg.what == DATA_LOADED) {
//                mProgressDialog.dismiss();
//                // 绑定数据到View中
//                data2View();
//                initDirPopupWindow();
//            }

            if (msg.what == 1) {
                mPaths = (List<Obj_item>) msg.obj;
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                // 绑定数据到View中
                data2View();
            }
        }
    };
    private TextView mTv_ConfirmText;
    private View mV_Confirm;

    private String actionBtnText = "发送";

    private String targetID = null;

    public static final String KEY_SELECTED_VIDEOS = "SELECTED_VIDEOS";
    public static final String EXTRA_MAX_SELECT_NUM = "EXTRA_MAX_SELECT_NUM";
    public static final String EXTRA_GRIDVIEW_COLUMN = "EXTRA_GRIDVIEW_COLUMN";
    public static final String EXTRA_ACTION_BUTTON_TEXT = "EXTRA_ACTION_BUTTON_TEXT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.album_local_video_activity);

        initView();
        initDatas();
        initEvent();


    }

    @Override
    protected void onDestroy() {
        if (asyncGetVideo != null) {
            asyncGetVideo.stop();
        }
        mHandler = null;

        // 清理Glide内存缓存
        Glide.get(this).clearMemory();

        super.onDestroy();
    }



    /**
     * 内容区域变亮
     */
    private void lightOn() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1.0f;
        getWindow().setAttributes(lp);
    }

    /**
     * 内容区域变暗
     */
    private void lightOff() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.3f;
        getWindow().setAttributes(lp);
    }

    private void data2View() {
//        if (mFile_CurrentDir == null || mFile_CurrentDir.list() == null || mFile_CurrentDir.list().length == 0) {
//            Toast.makeText(this, "未扫描到任何图片", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        //mImgs = Arrays.asList(mFile_CurrentDir.list());
//        mImgs = Arrays.asList(mFile_CurrentDir.list(new FilenameFilter() {
//            @Override
//            public boolean accept(File dir, String name) {
//                if (name.endsWith(".jpg") ||
//                        name.endsWith(".jpeg") ||
//                        name.endsWith(".png") ||
//                        name.endsWith(".JPG") ||
//                        name.endsWith(".JPEG") ||
//                        name.endsWith(".PNG")) {
//                    return true;
//                }
//                return false;
//            }
//        }));

//        mImageAdapter = new ImageAdapter(this, mImgs, mFile_CurrentDir.getAbsolutePath());
//
//        mGridView.setAdapter(mImageAdapter);

        mImageAndVideoAdapter = new ImageAndVideoAdapter(LocalVideo.this, mPaths, mSelectPaths, null);
        mGridView.setAdapter(mImageAndVideoAdapter);
        mGridView.setNumColumns(mGridViewColumn);
        mImageAndVideoAdapter.setOnSelectListener(new ImageAndVideoAdapter.OnSelectListener() {
            @Override
            public void onSelectResult(HashSet<String> selected) {
                mSelectCount = selected.size();
                mSelectPaths.clear();// 清理
                mSelectPaths.addAll(selected);
                if (mSelectCount <= 0) {
                    mTv_ConfirmText.setText(actionBtnText);
                    mV_Confirm.setEnabled(false);
                } else {
                    mTv_ConfirmText.setText(actionBtnText + "(" + mSelectCount + "/" + mMaxSelectNum + ")");
                    mV_Confirm.setEnabled(true);
                }
            }
        });
        mImageAndVideoAdapter.setMaxSelectNum(mMaxSelectNum);
        mImageAndVideoAdapter.setGridViewColumn(mGridViewColumn);
        mImageAndVideoAdapter.setItemOnclickListener(mItemOnclickLter);

        mTv_DirName.setText("所有视频");
        mTv_DirCount.setText(mPaths.size() + "个");

        /*mTv_DirCount.setText(mMaxCount + "张");
        mTv_DirName.setText(mFile_CurrentDir.getName());
        Toast.makeText(this, "totalCount:" + totalCount, Toast.LENGTH_SHORT).show();*/
    }

    private void initView() {
        mGridView = findViewById(R.id.id_gridView_local);
        mBottomLy = findViewById(R.id.id_bottom_ly_local);
        mTv_DirName = findViewById(R.id.id_dir_name_local);
        mTv_DirCount = findViewById(R.id.id_dir_count_local);
        mTv_DirCount.setText("");

        mTv_Title = findViewById(R.id.lblTitle_local);
        mTv_Title.setText("本地视频");
        mBt_Back = findViewById(R.id.back_local);
        mTv_ConfirmText = findViewById(R.id.textview1_local);
        mV_Confirm = findViewById(R.id.confirm_local);
        mV_Confirm.setEnabled(false);
    }

    /**
     * 利用ContentProvider扫描手机中的所有图片
     */
    private void initDatas() {
        Intent intent = getIntent();
        mGridViewColumn = intent.getIntExtra(EXTRA_GRIDVIEW_COLUMN, 3);
        mMaxSelectNum = intent.getIntExtra(EXTRA_MAX_SELECT_NUM, 1);
        actionBtnText = intent.getStringExtra(EXTRA_ACTION_BUTTON_TEXT);

        mTv_ConfirmText.setText(actionBtnText);//设置确认按钮上的文字

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "当前存储卡不可用", Toast.LENGTH_SHORT).show();
            return;
        }

        mProgressDialog = ProgressDialog.show(this, null, "正在加载...");

        mSelectPaths = intent.getStringArrayListExtra(KEY_SELECTED_VIDEOS);
        if(mSelectPaths == null){
            mSelectPaths = new ArrayList<>();
        }
        if (mSelectPaths.size() > 0) {
            mTv_ConfirmText.setText(actionBtnText + "(" + mSelectPaths.size() + "/" + mMaxSelectNum + ")");
        }

        asyncGetVideo = new AsyncGetVideo();
        asyncGetVideo.setOnGetVideoListener(new AsyncGetVideo.OnGetVideoListener() {
            @Override
            public void onGetVideoResult(List<Obj_item> paths) {
                // 通知Handler扫描图片完成
                //mHandler.sendEmptyMessage(DATA_LOADED);
                if (mHandler != null) {
                    Message message = mHandler.obtainMessage();
                    message.what = 1;
                    message.obj = paths;
                    mHandler.sendMessage(message);
                }

            }
        });
        asyncGetVideo.start(this);

    }

    private void initEvent() {
        mBottomLy.setOnClickListener(onClickListener);

        mBt_Back.setOnClickListener(onClickListener);

        mV_Confirm.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mBottomLy) {
//                mDirPopupWindow.setAnimationStyle(R.style.dir_popupwindow_anim);
//                mDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);
//                lightOff();
            }

            if (v == mBt_Back) {
                finish();
            }

            if (v == mV_Confirm) {
                Intent intent = new Intent();
                intent.putStringArrayListExtra(KEY_SELECTED_VIDEOS, (ArrayList<String>) mSelectPaths);
                setResult(RESULT_OK, intent);
                finish();
                return;
            }
        }
    };

    private ImageAndVideoAdapter.ItemOnclickListener mItemOnclickLter = new ImageAndVideoAdapter.ItemOnclickListener() {
        @Override
        public void onClick(String path) {
            //Toast.makeText(LocalVideoPickerAct.this.getApplicationContext(), "点击了视频：" + path, Toast.LENGTH_SHORT)
            // .show();

            /** 下面这种方法，会遇到有些手机不遵循Android的规范（比如：锤子），导致抛出android.content.ActivityNotFoundException: No Activity found to
             * handle Intent， 所以不采用
             * */
                /*String extension = MimeTypeMap.getFileExtensionFromUrl(path);
                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                Intent mediaIntent = new Intent(Intent.ACTION_VIEW);
                mediaIntent.setDataAndType(Uri.parse(filePath), mimeType);
                LocalVideoPickerAct.this.startActivity(mediaIntent);*/
//            Intent intent = new Intent();
//            intent.setClass(LocalVideo.this, NewVideoPlayActivity.class);
//            intent.putExtra(NewVideoPlayActivity.EXTRA_VIDEO_URL, "");
//            intent.putExtra(NewVideoPlayActivity.EXTRA_VIDEO_URL_LOCAL, path);
//            startActivity(intent);
            String videoFilepath =path;
            PlayActivity.startVideoActivity(LocalVideo.this, "","",videoFilepath,"");
        }
    };

}
