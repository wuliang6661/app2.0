package com.chart.manager;

import android.content.Context;
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

public class BarChartNormalMarkView extends MarkerView {
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


    public BarChartNormalMarkView(Context context, List<String> barNameList, List<String> xValueList, List<List<Float>> yValuesList, String labelAttr) {
        super(context, R.layout.bar_chart_marker_item);
        tvXValue = findViewById(R.id.tv_Xvalue);
        tvYValue = findViewById(R.id.tv_Yvalue);
        this.xValueList = xValueList;
        this.barNameList = barNameList;
        this.yValuesList = yValuesList;
        this.labelAttr = labelAttr;
    }

    public BarChartNormalMarkView(Context context, List<String> barNameList, List<String> xValueList, List<List<Float>> yValuesList, String labelAttr, boolean isRound) {
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
}
