package qyc.library.control.list_explorer;//package qyc.library.control.list_explorer;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//import qyc.library.R;
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.os.Environment;
//import android.util.AttributeSet;
//import android.util.TypedValue;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;
//import android.widget.SimpleAdapter;
//import android.widget.Toast;
//
//public class ListExplorer extends ListView {
//
//	public ListExplorer(Context context) {
//		super(context);
//		if (isInEditMode()) {
//			inEditMode();
//		} else {
//			init(null);
//		}
//	}
//
//	public ListExplorer(Context context, AttributeSet attrs) {
//		super(context, attrs);
//		if (isInEditMode()) {
//			inEditMode();
//		} else {
//			init(attrs);
//		}
//	}
//
//	public ListExplorer(Context context, AttributeSet attrs, int defStyle) {
//		super(context, attrs, defStyle);
//		if (isInEditMode()) {
//			inEditMode();
//		} else {
//			init(attrs);
//		}
//	}
//
//	// 在可视化编辑器里面显示的内容
//	private void inEditMode() {
//		if (isInEditMode()) {
//			setBackgroundColor(Color.rgb(134, 154, 190));
//			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//					getContext(), android.R.layout.simple_list_item_1);
//			adapter.add("资源管理器");
//			adapter.add("作者:QYC");
//			adapter.add("邮箱:68452263@qq.com");
//			setAdapter(adapter);
//			return;
//		}
//	}
//	
//	
//	private int root,back,folder,file;
//	
//	private void init(AttributeSet attrs)
//	{
//		
//	}
//	
//	
//	
////	images.put(OpenFlieDialog.sFileRoot,R.drawable.filedialog_root);	// 根目录图标
////	images.put(OpenFlieDialog.sFileParent,R.drawable.filedialog_folder_up);	//返回上一层的图标
////	images.put(OpenFlieDialog.sFileFolder, R.drawable.filedialog_folder);	//文件夹图标
////	images.put("xml", R.drawable.filedoalog_file_unroot);	//文件图标
////	images.put(OpenFlieDialog.sFileEmpty,R.drawable.filedialog_root);
//	
//
//	/** 根据自定义属性,对控件样式进行调整 */
//	private void setResValue(AttributeSet attrs) {
//		// 获取控件自定义属性值
//		TypedArray a = getContext().obtainStyledAttributes(attrs,
//				R.styleable.ListExplorer);
//		int N = a.getIndexCount();// 自定义属性被使用的数量
//		for (int i = 0; i < N; i++) {
//			int attr = a.getIndex(i);
//			switch (attr) {
//			case R.styleable.ListExplorer_ExplorerBackImg:
//				// 底部工具条背景色
//				int backImg = a.getResourceId(attr, R.drawable.b);
//				if (backImg != 0) {
//					
//				} else {
//
//				}
//				break;
//			case R.styleable.ListExplorer_ExplorerFileImg:
//				// 底部工具条文字颜色
//				int loadingMoreTextColor = a.getColor(attr, 0);
//				if (loadingMoreTextColor != 0) {
//					txtBottom.setTextColor(loadingMoreTextColor);
//				}
//				break;
//			case R.styleable.ListExplorer_ExplorerFoldeImg:
//				// 底部工具条文字大小
//				float loadingMoreTextSize = a.getDimension(attr, 0);
//				if (loadingMoreTextSize != 0) {
//					txtBottom.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//							loadingMoreTextSize);
//				}
//				break;
//			case R.styleable.ListExplorer_ExplorerBootImg:
//				// 顶部工具条提示文字颜色
//				int refreshTextColor = a.getColor(attr, 0);
//				if (refreshTextColor != 0) {
//					txtRefresh_top.setTextColor(refreshTextColor);
//				}
//				break;
//			case R.styleable.ListPullDown_refreshTextSize:
//				// 顶部工具条提示文字大小
//				float refreshTextSize = a.getDimension(attr, 0);
//				if (refreshTextSize != 0) {
//					txtRefresh_top.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//							refreshTextSize);
//				}
//				break;
//			case R.styleable.ListPullDown_refreshTimeColor:
//				// 顶部工具条时间文字颜色
//				int refreshTimeColor = a.getColor(attr, 0);
//				if (refreshTimeColor != 0) {
//					txtTime_top.setTextColor(refreshTimeColor);
//				}
//				break;
//			case R.styleable.ListPullDown_refreshTimeSize:
//				// 顶部工具条时间文字大小
//				float refreshTimeSize = a.getDimension(attr, 0);
//				if (refreshTimeSize != 0) {
//					txtTime_top.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//							refreshTimeSize);
//				}
//				break;
//			case R.styleable.ListPullDown_refreshArrowUp:
//				// 顶部工具条向上箭头
//				int refreshArrowUp = a.getResourceId(attr, 0);
//				if (refreshArrowUp != 0) {
//					this.imgRefreshUp = refreshArrowUp;
//				}
//				break;
//			case R.styleable.ListPullDown_refreshArrowDown:
//				// 顶部工具条向下箭头
//				int refreshArrowDown = a.getResourceId(attr, 0);
//				if (refreshArrowDown != 0) {
//					this.imgRefreshDown = refreshArrowDown;
//				}
//				break;
//			default:
//				break;
//			}
//		}
//		a.recycle();
//	}
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	static final String sFileRoot = "...";
//	
//	static final String sFileParent = "..";
//	static final String sFileFolder = ".";
//	static final String sFileEmpty = "....";
//
//	
//	private String path = Environment.getExternalStorageDirectory().getPath();
//	private List<Map<String, Object>> list = null;
//
//	private String suffix = null;
//
//	private Map<String, Integer> imagemap = null;
//
//	private String getSuffix(String filename) {
//		int dix = filename.lastIndexOf('.');
//		if (dix < 0) {
//			return "";
//		} else {
//			return filename.substring(dix + 1);
//		}
//	}
//
//	private int getImageId(String s) {
//		if (imagemap == null) {
//			return 0;
//		} else if (imagemap.containsKey(s)) {
//			return imagemap.get(s);
//		} else if (imagemap.containsKey(sFileEmpty)) {
//			return imagemap.get(sFileEmpty);
//		} else {
//			return 0;
//		}
//	}
//
//	private int refreshFileList() {
//		// 刷新文件列表
//		File[] files = null;
//		try {
//			files = new File(path).listFiles();
//		} catch (Exception e) {
//			files = null;
//		}
//		if (files == null) {
//			// 访问出错
//			Toast.makeText(getContext(), sFileOnErrorMsg, Toast.LENGTH_SHORT)
//					.show();
//			return -1;
//		}
//		if (list != null) {
//			list.clear();
//		} else {
//			list = new ArrayList<Map<String, Object>>(files.length);
//		}
//
//		// 用来先保存文件夹和文件夹的两个列表
//		ArrayList<Map<String, Object>> lfolders = new ArrayList<Map<String, Object>>();
//		ArrayList<Map<String, Object>> lfiles = new ArrayList<Map<String, Object>>();
//
//		if (!this.path.equals(sFileRoot)) {
//			// 添加根目录 和 上一层目录
//			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("name", sFileRoot);
//			map.put("path", sFileRoot);
//			map.put("img", R.drawable.filedialog_root);
//			list.add(map);
//
//			map = new HashMap<String, Object>();
//			map.put("name", sFileParent);
//			map.put("path", path);
//			map.put("img", R.drawable.filedialog_folder_up);
//			list.add(map);
//		}
//
//		for (File file : files) {
//			if (file.isDirectory() && file.listFiles() != null) {
//				// 添加文件夹
//				Map<String, Object> map = new HashMap<String, Object>();
//				map.put("name", file.getName());
//				map.put("path", file.getPath());
//				map.put("img", R.drawable.filedialog_folder);
//				lfolders.add(map);
//			} else if (file.isFile()) {
//				// 添加文件
//				String sf = getSuffix(file.getName()).toLowerCase(Locale.CHINA);
//				if (suffix == null
//						|| suffix.length() == 0
//						|| (sf.length() > 0 && suffix.indexOf("." + sf + ";") >= 0)) {
//					Map<String, Object> map = new HashMap<String, Object>();
//					map.put("name", file.getName());
//					map.put("path", file.getPath());
//					map.put("img", getImageId(sf));
//					lfiles.add(map);
//				}
//			}
//		}
//
//		list.addAll(lfolders); // 先添加文件夹，确保文件夹显示在上面
//		list.addAll(lfiles); // 再添加文件
//
//		SimpleAdapter adapter = new SimpleAdapter(getContext(), list,
//				R.layout.filedialogitem,
//				new String[] { "img", "name", "path" }, new int[] {
//						R.id.filedialogitem_img, R.id.filedialogitem_name,
//						R.id.filedialogitem_path });
//		this.setAdapter(adapter);
//		return files.length;
//	}
//
//	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
//
//		@Override
//		public void onItemClick(AdapterView<?> parent, View view, int position,
//				long id) {
//
//			// 条目选择
//			String pt = (String) list.get(position).get("path");
//			String fn = (String) list.get(position).get("name");
//			if (fn.equals(sFileRoot) || fn.equals(sFileParent)) {
//				// 如果是更目录或者上一层
//				File fl = new File(pt);
//				String ppt = fl.getParent();
//				if (ppt != null) {
//					// 返回上一层
//					path = ppt;
//				} else {
//					// 返回更目录
//					path = sFileRoot;
//				}
//			} else {
//				File fl = new File(pt);
//				if (fl.isFile()) {
//					// 如果是文件
//					// ((Activity) getContext()).dismissDialog(this.dialogid);
//					// // 让文件夹对话框消失
//					// 设置回调的返回值
//					Bundle bundle = new Bundle();
//					bundle.putString("path", pt);
//					bundle.putString("name", fn);
//					// 调用事先设置的回调函数
//					callback.callback(bundle);
//					return;
//				} else if (fl.isDirectory()) {
//					// 如果是文件夹
//					// 那么进入选中的文件夹
//					path = pt;
//				}
//			}
//			refreshFileList();
//		}
//	};
//
//	private OnFileSelect onFileSelect = null;
//
//	public void setonFileSelectListen(OnFileSelect onFileSelect) {
//		this.onFileSelect = onFileSelect;
//	}
//
//}
