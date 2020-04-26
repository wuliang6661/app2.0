package synway.module_publicaccount.map.show;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import synway.module_publicaccount.R;


/**
 * Created by QSJH on 2016/4/28 0028.
 */
public class GridHeadView extends LinearLayout {

    private GridView gridView;
    private TextView tv_bottom;
    private LinearLayout ll_bottom;
    private LinearLayout ll_expand;
    private GridAdapter adapter;
    private View panel;
    private GestureDetector gestureDetector;


    private int max_ll_expand_height;// GridView最大高度
    private int min_ll_expand_height;// GridView最小高度
    private int baseHeight;// 每一次Scoll动作开始时的基准高度


    public GridHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        panel = LayoutInflater.from(context).inflate(R.layout.model_public_account_slide_layout, this, true);
        tv_bottom = panel.findViewById(R.id.bottomTxt);
        gridView = panel.findViewById(R.id.gridView);
        ll_bottom = panel.findViewById(R.id.bottomLi);
        ll_expand = panel.findViewById(R.id.lilayout);

        adapter = new GridAdapter(context);
        gridView.setAdapter(adapter);
        adapter.setOnItemMesure(new GridAdapter.OnItemMesure() {
            @Override
            public void onItemMesure(int height, int width) {
                if (min_ll_expand_height == 0) {
                    min_ll_expand_height = height;
                }
                if (max_ll_expand_height == 0) {
                    max_ll_expand_height = min_ll_expand_height * 4;
                }

                if (baseHeight == 0) {
                    ll_expand.getLayoutParams().height = min_ll_expand_height;
                } else {
                    ll_expand.getLayoutParams().height = baseHeight;
                }
            }
        });
        ll_bottom.setClickable(true);
        ll_bottom.setOnTouchListener(onTouchListener);
        gestureDetector = new GestureDetector(context, onGestureListener);
        gestureDetector.setIsLongpressEnabled(false);

    }


    //定义一个方法设置是否显示关注标志
    public void setIsShowFollowFlag(boolean flag,String followID) {
        if (adapter != null) {
            adapter.setIsShowFollowFlag(flag,followID);
            adapter.notifyDataSetChanged();
        }
    }

    public void initHeight(int height) {
        baseHeight = height;
    }

    /**
     * 设置最大高度
     *
     * @param maxHeight
     */
    public void setMaxHeight(int maxHeight) {
        this.max_ll_expand_height = maxHeight;
    }

    /**
     * 设置最小高度
     *
     * @param minHeight
     */
    public void setMinHeight(int minHeight) {
        this.min_ll_expand_height = minHeight;
    }

    public int getCurrentHeight() {
        return ll_expand.getLayoutParams().height;
    }

    public GridAdapter.PicItem getPicItem(int position) {
        return (GridAdapter.PicItem) adapter.getItem(position);
    }

    /**
     * 设置底部文字描述
     *
     * @param text
     */
    public void setBottomDescription(String text) {
        tv_bottom.setText(text);
    }


    /**
     * Call of updateView() is required to update the UI.
     *
     * @param picItem
     */
    public void addPicItem(GridAdapter.PicItem picItem) {
        adapter.addItem(picItem);
    }

    /**
     * Call of updateView() is required to update the UI.
     *
     * @param picItem
     */
    public void removePicItem(GridAdapter.PicItem picItem) {
        adapter.removeItem(picItem);
    }

    public void removeAll(){
        adapter.removeAll();
        adapter.notifyDataSetChanged();
    }

    /**
     *
     */
    public void updateView() {
        adapter.notifyDataSetChanged();
    }

    public void setOnGridItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        gridView.setOnItemClickListener(onItemClickListener);
    }

    //设置长按弹出dialog
    public void setOnGridItemLongClickListener(AdapterView.OnItemLongClickListener onGridItemLongClickListener) {
        gridView.setOnItemLongClickListener(onGridItemLongClickListener);
    }

    public void removeOnGridItemClickListener() {
        gridView.setOnItemClickListener(null);
    }


    private OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                baseHeight = ll_expand.getLayoutParams().height;
                Log.i("qsjh", "ACTION_UP()");
                return false;
            }
            return gestureDetector.onTouchEvent(event);
        }
    };

    private GestureDetector.OnGestureListener onGestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            Log.i("qsjh", "onDown()" + e.toString());
            baseHeight = ll_expand.getLayoutParams().height;
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            Log.i("qsjh", "onShowPress()" + e.toString());
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.i("qsjh", "onSingleTapUp()" + e.toString());
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            // 以Down事件的height为坐标系原点，手指移动终点相对于原点的位移
            float moveY = e2.getRawY() - e1.getRawY();
            ViewGroup.LayoutParams lp = ll_expand.getLayoutParams();
            int toHead = baseHeight + (int) moveY;
//            if (toHead > max_ll_expand_height || toHead < min_ll_expand_height) {
//                return true;
//            }
            if (toHead > max_ll_expand_height) {
                toHead = max_ll_expand_height;
            }
            if (toHead < min_ll_expand_height) {
                toHead = min_ll_expand_height;
            }
            lp.height = toHead;
            ll_expand.setLayoutParams(lp);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.i("qsjh", "onLongPress()" + e.toString());


        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.i("qsjh", "onFling()" + e1.toString() + "|" + e2.toString());
            return true;
        }
    };


//    private class AsyncMove extends AsyncTask<Integer, Integer, Void> {
//
//        @Override
//        protected Void doInBackground(Integer... arg0) {
//            publishProgress(arg0[0]);
//            return null;
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            ViewGroup.LayoutParams lp = ll_expand.getLayoutParams();
//            if (values[0] > 0) {
//                lp.height = max_ll_expand_height;
//            } else {
//                lp.height = min_ll_expand_height;
//            }
//            ll_expand.setLayoutParams(lp);
//
//            if (lp.height == max_ll_expand_height) {
//                iv_arrow.setImageResource(R.drawable.arrow_up);
//            } else if (lp.height == min_ll_expand_height) {
//                iv_arrow.setImageResource(R.drawable.arrow_down);
//            }
//        }
//
//
//    }

}
