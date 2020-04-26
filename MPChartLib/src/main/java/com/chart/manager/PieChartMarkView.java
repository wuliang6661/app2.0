package com.chart.manager;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import com.github.mikephil.charting.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by leo on 2018/11/29.
 */

public class PieChartMarkView extends MarkerView {
    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     */
    private TextView tvXValue;
    private TextView tvYValue;
    private List<String> labelNameList;

    private List<Float> valuesList;
    private String labelAttr;



    public PieChartMarkView(Context context, List<String> labelNameList, List<Float> valuesList) {
        super(context, R.layout.pie_chart_marker_item);
        tvXValue = findViewById(R.id.tv_Xvalue);
        tvYValue = findViewById(R.id.tv_Yvalue);
        this.labelNameList = labelNameList;
        this.valuesList = valuesList;
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }


    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        try {

            PieEntry pieEntry = (PieEntry) e;
            Log.d("dym------------------->", "pieEntry.getValue()= "+pieEntry.getValue()+",pieEntry.getLabel()= "+pieEntry.getLabel());
            float yValueSum = getYValueSum(valuesList);
            float value = pieEntry.getValue() / yValueSum * 100f;
            DecimalFormat format = new DecimalFormat("###,###,##0.0");
            String valueStr = format.format(value) + "%";
            String tvXValueStr = "占比";

            String tvYValueStr = pieEntry.getLabel()+ "  " +valueStr;

            tvXValue.setText(tvXValueStr);
            tvYValue.setText(tvYValueStr);

        } catch (Exception ex) {
            Log.d("dym------------------->", "refreshContent error= " + ex.getMessage());
        }
        super.refreshContent(e, highlight);
    }

    public float getYValueSum(List<Float> valuesList) {

        float sum = 0;

        for (int i = 0; i < valuesList.size(); i++)
            sum += valuesList.get(i);
        return sum;
    }
}
