package synway.common.fileManager;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.luseen.luseenbottomnavigation.BottomNavigation.BottomNavigationItem;
import com.luseen.luseenbottomnavigation.BottomNavigation.OnBottomNavigationItemClickListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import synway.common.R;


/**
 * 文件管理器
 *
 * @author (huangxi)
 */
public class FileManagerAct extends AppCompatActivity implements OnBottomNavigationItemClickListener {
    TextView currentPathTextView;// 显示当前路径的文本视图
    ListView fileListView;// 显示文件列表的视图
    File currentDirectory;// 当前文件目录
    File cutFile;// 将要剪切的文件
    File copyFile;// 将要复制的文件
    private BottomNavigationView bottomNavigationView;
    File[] files;// 当前文件列表
    RelativeLayout rlReturn;
    private AlertDialog alertDialog;
    private ImageView ivFlieCreate,ivReturn;
    private String filePath;//文件地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
            setContentView(R.layout.activity_file_manager);
        initview();

    }
    private void initview(){

        // 获取视图
        ivFlieCreate= findViewById(R.id.iv_file_creat);
        currentPathTextView = findViewById(R.id.tv_url);
        fileListView = findViewById(R.id.lv_files);
        ivReturn= findViewById(R.id.iv_bar_left);
        ivReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rlReturn= findViewById(R.id.rl_return);
        rlReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initFileList(currentDirectory.getParentFile());
            }
        });
        ivFlieCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentDirectory.canWrite()) {
                    // 创建一个弹窗供用户操作

                    // 获得新建弹窗的布局
                    View vv = LayoutInflater.from(FileManagerAct.this)
                            .inflate(R.layout.createfile, null);
                    // 获取上面的文本框和单选按钮
                    final EditText et = vv.findViewById(R.id.et);
                    et.setHint(R.string.editext_tip);
                    final RadioGroup rg = vv
                            .findViewById(R.id.rg);

                    Builder builder = new Builder(FileManagerAct.this);
                    builder.setTitle("新建");
                    builder.setView(vv);
                    // builder.setCancelable(false);//点击对话框外面 不消失

                    // 绑定按钮和点击事件
                    builder.setPositiveButton("取消", null);
                    builder.setNegativeButton("确定", new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // 获取用户选择的按钮和文本
                            int checkedRadioButtonId = rg
                                    .getCheckedRadioButtonId();
                            String newFileName = et.getText().toString()
                                    .trim();
                            if (newFileName.equals("")) {
                                alert("请输入文件/文件夹的名称");
                            } else {
                                // 根据当前目录和输入的名称 创建新文件
                                File newFile = new File(currentDirectory,
                                        newFileName);
                                if (checkedRadioButtonId == R.id.r0) {
                                    try {
                                        if (newFile.createNewFile()) {
                                            alert("创建文件成功");
                                            initFileList(currentDirectory);
                                        } else {
                                            alert("文件已存在");
                                        }
                                    } catch (IOException e) {
                                        alert("创建文件失败");
                                    }

                                } else if (checkedRadioButtonId == R.id.r1) {
                                    if (newFile.mkdirs()) {
                                        alert("创建文件夹成功");
                                        initFileList(currentDirectory);
                                    } else {
                                        alert("创建文件夹失败");
                                    }

                                }
                            }
                        }
                    });
                    builder.show();

                } else {
                    alert("没有权限创建");
                }
            }
        });
        // 初始化文件列表
        filePath=getIntent().getStringExtra("FilePath");
        File file = new File(filePath);
        initFileList(file);
        // 给文件列表视图绑定点击事件
        fileListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                    // 获取点击的文件
                    File f = files[arg2 ];
                    // 如果是文件夹
                    if (f.isDirectory()) {
                        // 重新初始化文件列表视图
                        initFileList(f);
                    } else {// 否则的话 打开文件
                        // 调用系统方法获取后缀
                        String extension = MimeTypeMap
                                .getFileExtensionFromUrl(f.getName());
                        // 获取MIME类型
                        String mimeType = MimeTypeMap.getSingleton()
                                .getMimeTypeFromExtension(extension);
                        mimeType = mimeType == null ? "*/*" : mimeType;
                        // 创建Intent
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        // 设置文件路径和MIME类型 让系统自己决定用什么打开
                        intent.setDataAndType(Uri.fromFile(f), mimeType);
                        startActivity(intent);

                }
            }
        });
        // 设置文件列表视图的长按事件
        fileListView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int arg2, long arg3) {
                // 获取点击的文件
                final File f = files[arg2 ];
                // 本程序只对文件进行复制等操作,请自行扩展
                if (f.isFile()) {
                    // 如果能读取
                    if (f.canRead()) {
                        Builder builder = new Builder(FileManagerAct.this);
                        builder.setTitle("请选择操作");
                        String[] items;
                        // 如果能写入(修改/删除)
                        if (f.canWrite()) {
                            items = new String[]{"复制","粘贴" ,"剪切", "删除"};
                        } else {
                            items = new String[]{"复制","粘贴"};
                        }
                        // 绑定选项和点击事件
                        builder.setItems(items, new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                switch (arg1) {
                                    case 0:
                                        // 复制
                                        copyFile = f;
                                        cutFile = null;
                                        break;
                                    case 1:
                                        File targetFile = null;
                                        if (!currentDirectory.canWrite()) {
                                            alert("此文件夹不可粘贴");
                                        } else if (copyFile != null) {// 复制
                                            // 去重复处理
                                            targetFile = distinctOfFile(copyFile);
                                            nioBufferCopy(copyFile, targetFile);
                                        } else if (cutFile != null) {// 粘贴
                                            targetFile = distinctOfFile(cutFile);
                                            nioBufferCopy(cutFile, targetFile);
                                            cutFile.delete();
                                        } else {
                                            alert("请先复制文件~");
//                                            initFileList(currentDirectory);
                                        }
                                        break;
                                    case 2:
                                        // 剪切
                                        cutFile = f;
                                        copyFile = null;
                                        break;
                                    case 3:
                                        // 删除
                                        AlertDialog.Builder builder = new Builder(FileManagerAct.this);
                                        builder.setMessage("是否确认删除");
                                        builder.setCancelable(false);
                                        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                f.delete();
                                                initFileList(currentDirectory);// 重新初始化文件列表
                                                dialog.cancel();
                                                alertDialog = null;

                                            }
                                        });
                                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                                alertDialog = null;
                                            }
                                        });
                                        if (alertDialog == null) {
                                            alertDialog = builder.create();
                                            alertDialog.show();
                                        }

                                        break;
                                }
                            }
                        });
                        builder.show();
                    } else {
                        alert("此文件/文件夹不能进行任何操作");
                    }
                } else {
                    alert("暂时只支持对文件进行操作~");
                }
                return true;
            }
        });
        initNavigationView();
    }

    /**
     * 初始化文件列表
     *
     * @param f 要打开的目录(文件夹)
     */
    void initFileList(File f) {
        // 如果f为null则默认为跟目录
        if (f == null) {
            f = new File("/");
        }
        // 如果是文件夹
        if (f.isDirectory()) {
            // 如果能读取
            if (f.canRead()) {
                // 保存当前路径
                currentDirectory = f;
                // 显示当前路径到界面
                currentPathTextView.setText("    " + f.getPath());

                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                Map<String, Object> map;

                // 在文件列表视图添加子文件或目录
                files = f.listFiles();
                // 排序
                Arrays.sort(files);

                for (File file : files) {
                    map = new HashMap<String, Object>();
                    map.put("text", file.getName());
                    // 根据是目录或文件来决定列表中的图标 (可根据后缀名来丰富更多图标)
                    if (file.isDirectory()) {
                        map.put("img", R.drawable.folder);
                    } else if (file.isFile()) {
                        map.put("img", R.drawable.others);
                    } else {
                        map.put("img", R.drawable.unknown);
                    }
                    list.add(map);
                }
                // 创建适配器
                SimpleAdapter sa = new SimpleAdapter(FileManagerAct.this, list,
                        R.layout.file_list, new String[]{"img", "text"},
                        new int[]{R.id.imageView1, R.id.tv_url});
                // 绑定适配器
                fileListView.setAdapter(sa);
            } else {
//                // 不能读取文件夹的提示
//                alert("没有权限");
                FileManagerAct.this.finish();
//                exitAlert();
            }
        }
    }

    /**
     * 去重复 当粘贴的目标文件存在时进行改名处理
     *
     * @param source 源文件
     * @return 处理后的 目标文件
     */
    File distinctOfFile(File source) {
        File targetFile = new File(currentDirectory, source.getName());
        int i = 0;
        while (targetFile.exists())// 如果目标文件已经存在的话(同名)
        {
            i++;
            int s=source.getName().lastIndexOf(".");
            String newFileName=source.getName().substring(0,s)+"(" + i + ")"+source.getName().substring(s,source.getName().length());
            targetFile = new File(currentDirectory, newFileName);
        }
        return targetFile;
    }

    // 创建全局文件流和管道 方便在线程中使用
    FileChannel in;
    FileChannel out;
    FileInputStream inStream;
    FileOutputStream outStream;
    ByteBuffer buffer;

    /**
     * 复制文件
     *
     * @param source 源文件
     * @param target 目标文件
     */
    void nioBufferCopy(File source, File target) {

        try {
            // 初始化
            inStream = new FileInputStream(source);
            outStream = new FileOutputStream(target);
            in = inStream.getChannel();
            out = outStream.getChannel();
            // 缓冲区为4K
            buffer = ByteBuffer.allocate(4096);

            // 实例化一个进度条对话框
            final ProgressDialog progressDialog = new ProgressDialog(
                    FileManagerAct.this);
            // 设置标题
            progressDialog.setTitle("粘贴中...");
            // 设置进度条的风格
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            // 设置点返回或按区域外不取消进度条
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);

            progressDialog.show();

            // 创建线程
            Thread t = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        // 设置进度条的最大值
                        progressDialog.setMax((int) in.size());
                        // 循环读取写入
                        while (in.read(buffer) != -1) {
                            buffer.flip();
                            out.write(buffer);
                            buffer.clear();
                            // 设置进度
                            progressDialog.setProgress((int) out.size());
                        }
                        // 取消进度条
                        progressDialog.cancel();
                        alert("粘贴完成");
                        // 在UI线程中初始化文件列表 (不能在非UI线程操作界面)
                        FileManagerAct.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initFileList(currentDirectory);
                            }
                        });
                    } catch (IOException e) {
                        alert("粘贴出错");
                        e.printStackTrace();
                    } finally {
                        // 关闭流和管道
                        try {
                            inStream.close();
                            in.close();
                            outStream.close();
                            out.close();
                        } catch (IOException e) {
                            alert("关闭流出错");
                            e.printStackTrace();
                        }
                    }
                }
            });
            t.start();

        } catch (FileNotFoundException e1) {
            alert("粘贴源丢失");
            e1.printStackTrace();
        }

    }
    /**
     * 封装Toast弹窗 方便使用
     *
     * @param message 需要弹窗的内容
     */
    void alert(Object message) {
        message = message == null ? "null" : message;
        final String text = message.toString();
        // 因为弹窗提示可能会在非UI线程中用到,所以使用runOnUiThread,如果当前是UI线程就直接弹窗,如果不是就加入到消息列队(列队循环很快,也会立刻弹窗)
        FileManagerAct.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FileManagerAct.this, text, Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    /**
     * 退出弹窗提示
     */
    void exitAlert() {
        Builder bexit = new Builder(FileManagerAct.this);
        bexit.setTitle("提示");
        bexit.setMessage("确定退出吗?");
        bexit.setPositiveButton("取消", null);
        bexit.setNegativeButton("确定", new OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // 结束MainActivity
                FileManagerAct.this.finish();
            }
        });
        bexit.show();
    }

    // 重写按键事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 如果按下了返回键
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {

            File f = currentDirectory.getParentFile();
            // 如果不是根目录就返回上层,如果是就提示退出
            if (f != null) {
                initFileList(f);
            } else {
//                exitAlert();
                FileManagerAct.this.finish();
            }
        }
        return true;
    }

    private void initNavigationView() {
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        int color = ContextCompat.getColor(this, R.color.tv_selector);

        if (bottomNavigationView != null) {
            bottomNavigationView.isWithText(false);
            bottomNavigationView.isColoredBackground(true);
            bottomNavigationView.setTextActiveSize(getResources().getDimension(R.dimen.text_active));
            bottomNavigationView.setTextInactiveSize(getResources().getDimension(R.dimen.text_inactive));
            bottomNavigationView.setItemActiveColorWithoutColoredBackground(ContextCompat.getColor(this, R.color.tv_selector));

        }
        BottomNavigationItem bottomNavigationItem = new BottomNavigationItem("手机", color, R.mipmap.ic_phone);
        BottomNavigationItem bottomNavigationItem1 = new BottomNavigationItem("SD卡", color, R.mipmap.ic_sd);
//        BottomNavigationItem bottomNavigationItem2 = new BottomNavigationItem("创建", color, R.mipmap.ic_create);
        BottomNavigationItem bottomNavigationItem3 = new BottomNavigationItem("粘贴", color, R.mipmap.ic_paste);
        bottomNavigationView.addTab(bottomNavigationItem);
        bottomNavigationView.addTab(bottomNavigationItem1);
//        bottomNavigationView.addTab(bottomNavigationItem2);
        bottomNavigationView.addTab(bottomNavigationItem3);
        bottomNavigationView.selectTab(1);
        bottomNavigationView.setOnBottomNavigationItemClickListener(this);
    }

    @Override
    public void onNavigationItemClick(int index) {
        switch (index) {
            case 0:// 点击了 手机
                // 初始化列表为跟目录
                initFileList(null);
                break;

            case 1:// 点击了SD卡
                // 初始化为SD卡目录
                initFileList(Environment.getExternalStorageDirectory());
                break;

            case 2:// 点击了粘贴
                // 目标文件(位置)
                File targetFile = null;
                if (!currentDirectory.canWrite()) {
                    alert("此文件夹不可粘贴");
                } else if (copyFile != null) {// 复制
                    // 去重复处理
                    targetFile = distinctOfFile(copyFile);
                    nioBufferCopy(copyFile, targetFile);
                } else if (cutFile != null) {// 粘贴
                    targetFile = distinctOfFile(cutFile);
                    nioBufferCopy(cutFile, targetFile);
                    cutFile.delete();
                } else {
                    alert("请先复制文件~");
//                    initFileList(currentDirectory);
                }
                break;
        }
    }
}
