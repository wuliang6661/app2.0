package com.chart.manager;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.widget.TextView;
import com.github.mikephil.charting.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import java.util.List;

/**
 * Created by leo on 2018/11/29.
 */

public class BarChartSpecialMarkView extends MarkerView {
    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     */
    private TextView tvXValue;
    private TextView tvYValue;
    private List<String> barNameList;
    private List<String> xValueList;
    private List<List<Float>> yValuesList;
    private String labelAttr;
    //是否对值取整
    private boolean isRound = false;


    public BarChartSpecialMarkView(Context context, List<String> barNameList, List<String> xValueList, List<List<Float>> yValuesList, String labelAttr) {
        super(context, R.layout.bar_chart_marker_item);
        tvXValue = findViewById(R.id.tv_Xvalue);
        tvYValue = findViewById(R.id.tv_Yvalue);
        this.xValueList = xValueList;
        this.barNameList = barNameList;
        this.yValuesList = yValuesList;
        this.labelAttr = labelAttr;
    }

    public BarChartSpecialMarkView(Context context, List<String> barNameList, List<String> xValueList, List<List<Float>> yValuesList, String labelAttr, boolean isRound) {
        super(context, R.layout.bar_chart_marker_item);
        tvXValue = findViewById(R.id.tv_Xvalue);
        tvYValue = findViewById(R.id.tv_Yvalue);
        this.xValueList = xValueList;
        this.barNameList = barNameList;
        this.yValuesList = yValuesList;
        this.labelAttr = labelAttr;
        this.isRound = isRound;
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }


    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        try {

            String tvXValueStr = xValueList.get((int) e.getX());

            String tvYValueStr = "";

            for (int i = 0; i < barNameList.size(); i++) {

                if (i > 19) {
                    //当前只支持最多显示20组
                    break;
                }

                String yValue;
                if (isRound) {
                    yValue = (int)Math.floor(yValuesList.get(i).get((int) e.getX())) + "";
                    // Log.d("dym------------------->", "isRound= "+yValue);
                } else {
                    yValue = yValuesList.get(i).get((int) e.getX())+ "";
                    // Log.d("dym------------------->", "not isRound= "+yValue);
                }

                if (i == 19 || i == (barNameList.size() - 1)) {
                    //当遍历到最后一个值的时候无需加\n 换行
                    tvYValueStr = tvYValueStr + barNameList.get(i) + "  " +
                        yValue+ labelAttr;
                } else {
                    tvYValueStr = tvYValueStr + barNameList.get(i) + "  " +
                        yValue+ labelAttr + " \n";
                }
            }


            tvXValue.setText(tvXValueStr);
            tvYValue.setText(tvYValueStr);

        } catch (Exception ex) {
            Log.d("dym------------------->", "refreshContent error= " + ex.getMessage());
        }
        super.refreshContent(e, highlight);
    }

    @Override public void draw(Canvas canvas, float posX, float posY) {
        MPPointF offset = getOffsetForDrawingAtPoint(posX, posY);

        int saveId = canvas.save();
        // translate to the correct position and draw
        //因为为了满足预警的需求需要在同一位置叠加显示柱状图，但是当同一位置显示柱状图的时候
        //chart点击事件只会响应第一组，所以只能让markView默认显示
        // markview在y轴方向统一是置顶显示
        canvas.translate(posX + offset.x, 0);
        draw(canvas);
        canvas.restoreToCount(saveId);
    }
}
