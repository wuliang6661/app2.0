package qyc.library.control.list_pulldown;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import synway.common.R;


public class ListPullDown extends RelativeLayout {

    public ListPullDown(Context context) {
        super(context);
        if (isInEditMode()) {
            initEditMode();
        } else {
            init();
        }
    }

    public ListPullDown(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) {
            initEditMode();
        } else {
            init();
            setResValue(attrs);
        }
    }

    public ListPullDown(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (isInEditMode()) {
            initEditMode();
        } else {
            init();
            setResValue(attrs);
        }
    }

    private void initEditMode() {
        setBackgroundColor(Color.rgb(134, 154, 190));
        TextView textView = new TextView(getContext());
        textView.setText("下拉刷新列表");
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 35);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(textView, lp);
    }

    /**
     * <p>
     * SwipeRefreshLayout的下拉监听只有用户手动下拉时才触发
     * <p>
     * SwipeRefreshLayout代码设置为下拉在onCreat时无效
     */

    // 底部工作条
    private View bottomView = null;
    private ProgressBar bottomProgressBar = null;
    private TextView bottomText = null;

    // 下拉容器和列表
    private SwipeRefreshLayout swipeLayout = null;
    private ListView listView = null;

    // 虽然SwipeRefreshLayout自己也有Refreshing属性,但由于startRefush方法需要post,考虑到线程安全,增加一个变量单独控制.
    private volatile boolean isRefreshing = false;

    // 观察者
    private OnPDListListener onPDListListener = null;

    // 等待的下拉次数,当代码主动调用startRefush时,在refush完成后继续刷.
    private int waitingRefushTimes = 0;

    // 当已经在刷新的情况下,再次调用startRefush方法是否叠加.如果选择叠加,那么刷新完后会自动再次刷新.
    private boolean isSuperPositionAble = false;

    private void init() {
        //handler = new Handler();
        LayoutInflater.from(getContext()).inflate(R.layout.lib_listpulldown_cv, this, true);

        bottomView = findViewById(R.id.relativeLayout1);
        bottomView.setOnClickListener(onClickListener);

        bottomText = findViewById(R.id.textView1);
        bottomProgressBar = findViewById(R.id.progressBar1);
        loadingMoreView_IsEnabled(false);// 默认不开启

        swipeLayout = findViewById(R.id.swipeRefreshLayout1);
        swipeLayout.setColorSchemeResources(R.color.lib_blue);// 默认设置为蓝色
        swipeLayout.setOnRefreshListener(onRefreshListener);

        listView = findViewById(R.id.listview1);
    }

    /**
     * 根据自定义属性,对控件样式进行调整
     */
    private void setResValue(AttributeSet attrs) {
        // 获取控件自定义属性值
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ListPullDown);
        int N = a.getIndexCount();// 自定义属性被使用的数量
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.ListPullDown_loadingMoreBackground) {
                int loadingMoreBKColorID = a.getResourceId(attr, 0);
                if (loadingMoreBKColorID != 0) {
                    bottomView.setBackgroundResource(loadingMoreBKColorID);
                } else {
                    int loadingMoreBKColor = a.getColor(attr, 0);
                    if (loadingMoreBKColor != 0) {
                        bottomView.setBackgroundColor(loadingMoreBKColor);
                    }
                }
            } else if (attr == R.styleable.ListPullDown_listPullDownLoadingMoreTextColor) {
                int loadingMoreTextColor = a.getColor(attr, 0);
                if (loadingMoreTextColor != 0) {
                    bottomText.setTextColor(loadingMoreTextColor);
                }
            } else if (attr == R.styleable.ListPullDown_listPullDownLoadingMoreTextSize) {
                float loadingMoreTextSize = a.getDimension(attr, 0);
                if (loadingMoreTextSize != 0) {
                    bottomText.setTextSize(TypedValue.COMPLEX_UNIT_PX, loadingMoreTextSize);
                }
            } else if (attr == R.styleable.ListPullDown_refreshBkColor) {
                int refreshBkColor = a.getColor(attr, 0);
                if (refreshBkColor != 0) {
                    swipeLayout.setProgressBackgroundColor(refreshBkColor);
                }
            } else if (attr == R.styleable.ListPullDown_listPullDownBackground) {
                int backgroundID = a.getResourceId(attr, 0);
                if (backgroundID != 0) {
                    listView.setBackgroundResource(backgroundID);
                } else {
                    int backgroundColor = a.getColor(attr, 0);
                    if (backgroundColor != 0) {
                        listView.setBackgroundColor(backgroundColor);
                    }
                }
            } else if (attr == R.styleable.ListPullDown_listPullDownCacheColorHint) {
                int cacheColorHintColor = a.getColor(attr, 0);
                if (cacheColorHintColor != 0) {
                    listView.setCacheColorHint(cacheColorHintColor);
                }
            } else if (attr == R.styleable.ListPullDown_listPullDownDivider) {
                Drawable dividerDrawable = a.getDrawable(attr);
                if (dividerDrawable != null) {
                    listView.setDivider(dividerDrawable);
                }
            } else if (attr == R.styleable.ListPullDown_listPullDownDividerHeight) {
                float dividerHeight = a.getDimension(attr, 0);
                if (dividerHeight != 0) {
                    listView.setDividerHeight((int) dividerHeight);
                }
            } else if (attr == R.styleable.ListPullDown_listPullDownTranscriptMode) {
                int mode = a.getInt(attr, 1);
                listView.setTranscriptMode(mode);
            }
        }
        a.recycle();
    }

    // 只有手动下拉才会触发
    private OnRefreshListener onRefreshListener = new OnRefreshListener() {

        @Override
        public void onRefresh() {
            if (onPDListListener != null) {
                isRefreshing = true;
                onPDListListener.onRefresh();
            } else {
                swipeLayout.setRefreshing(false);
            }
        }
    };

    // 底部工具条的点击事件
    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (onPDListListener != null) {
                setBottomView(true);
                onPDListListener.onloadMore();
            }
        }
    };

    /**
     * 设置底部工作条的样式
     *
     * @param isLoading isLoading
     */
    private void setBottomView(boolean isLoading) {
        if (isLoading) {
            bottomProgressBar.setVisibility(View.VISIBLE);
            bottomText.setText("正在努力加载...");
        } else {
            bottomProgressBar.setVisibility(View.INVISIBLE);
            bottomText.setText("点击加载更多");
        }
    }

    // 当控件从窗体中被移除,类似于onDestory()
    @Override
    protected void onDetachedFromWindow() {
        // 不管3721都去停一下延时计时器.

        super.onDetachedFromWindow();
    }

    /**
     * 当已经在刷新的情况下,再次调用startRefush方法是否叠加.如果选择叠加,那么刷新完后会自动再次刷新.
     *
     * @param isSuperpositionAble 默认为false
     */
    public void setSuperpositionAble(boolean isSuperpositionAble) {
        this.isSuperPositionAble = isSuperpositionAble;
    }

    /**
     * 立即开始刷新,如果已经在刷新了,调用无效.
     */
    public synchronized void startRefresh() {
        if (onPDListListener == null) {
            return;
        }

        if (isRefreshing()) {
            if (isSuperPositionAble) {
                waitingRefushTimes++;
            }
            return;
        }
        isRefreshing = true;
        swipeLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(true);// 不会触发onRefreshListener
                onPDListListener.onRefresh();
            }
        });
    }


    /**
     * 延时刷新
     *
     * @deprecated 该方法已弃用.请直接使用{@link #startRefresh()}
     */
    public void startRefreshDelay() {
        startRefresh();
    }

    /**
     * 停止刷新
     */
    public synchronized void stopRefresh() {
        swipeLayout.setRefreshing(false);
        isRefreshing = false;
        if (waitingRefushTimes > 0) {
            waitingRefushTimes--;
            startRefresh();
        }
    }

    /**
     * <p>
     * 是否正在刷新
     * <p>
     * 延时刷新的延时阶段不会算进"正在刷新"里,由于延时目前只有0.5秒,而且一般只会在UI建立时调用,这个瑕疵很难影响到程序功能,
     * 所以暂时不解决这个问题.
     */
    public synchronized boolean isRefreshing() {
        return isRefreshing;
    }

    /**
     * 是否显示加载更多
     *
     * @param isEnabled true将会显示"加载更多"工具条,并且恢复为待点击状态 false将会隐藏"加载更多"工具条
     */
    public void loadingMoreView_IsEnabled(boolean isEnabled) {
        if (isEnabled) {
            bottomView.setVisibility(View.VISIBLE);
        } else {
            setBottomView(false);
            bottomView.setVisibility(View.GONE);
        }
    }

	/* 刷新控件公开部分 */

    /**
     * 设置刷新球的箭头颜色,可以设置多个,会交替显示
     */
    public void setColorSchemeColors(int... refreshColorID) {
        swipeLayout.setColorSchemeColors(refreshColorID);
    }

    /**
     * 设置刷新球的背景颜色
     */
    public void setProgressBackgroundColor(int colorRes) {
        swipeLayout.setProgressBackgroundColor(colorRes);
    }

    /* 列表公开部分 */
    public void setAdapter(ListAdapter adapter) {
        listView.setAdapter(adapter);
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        listView.setOnTouchListener(l);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        listView.setOnItemClickListener(listener);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        listView.setOnItemLongClickListener(listener);
    }

    public void setOnItemSelectListener(OnItemSelectedListener listener) {
        listView.setOnItemSelectedListener(listener);
    }

    public void setDivider(Drawable drawable) {
        listView.setDivider(drawable);
    }

    /**
     * Sets the currently selected item. If in touch mode, the item will not be
     * selected but it will still be positioned appropriately. If the specified
     * selection position is less than 0, then the item at position 0 will be
     * selected.
     *
     * @param position Index (starting at 0) of the data item to be selected.
     */
    public void setSelection(int position) {
        listView.setSelection(position);
    }

    /**
     * Sets the selected item and positions the selection y pixels from the top
     * edge of the ListView. (If in touch mode, the item will not be selected
     * but it will still be positioned appropriately.)
     *
     * @param position Index (starting at 0) of the data item to be selected.
     * @param y        The distance from the top edge of the ListView (plus padding)
     *                 that the item will be positioned.
     */
    public void setSelectionFromTop(int position, int y) {
        listView.setSelectionFromTop(position, y);
    }

    /**
     * Returns the position within the adapter's data set for the first item
     * displayed on screen.
     *
     * @return The position within the adapter's data set
     */
    public int getFirstVisiblePosition() {
        return listView.getFirstVisiblePosition();
    }

    /**
     * Returns the position within the adapter's data set for the last item
     * displayed on screen.
     *
     * @Returns: The position within the adapter's data set
     */
    public int getLastVisiblePosition() {
        return listView.getLastVisiblePosition();
    }

    /**
     * Set the listener that will receive notifications every time the list
     * scrolls
     *
     * @param l the scroll listener
     */
    public void setOnScrollListener(OnScrollListener l) {
        this.listView.setOnScrollListener(l);
    }

    /**
     * 设置刷新监听
     */
    public void setOnPDListen(OnPDListListener onPDListListener) {
        this.onPDListListener = onPDListListener;
    }

    /**
     * 移除刷新监听
     */
    public void removePDListen() {
        this.onPDListListener = null;
    }

    public interface OnPDListListener {
        /**
         * 下拉刷新
         */
        void onRefresh();

        /**
         * 加载更多
         */
        void onloadMore();
    }
}
