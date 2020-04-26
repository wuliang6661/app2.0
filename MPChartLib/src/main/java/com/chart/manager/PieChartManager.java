package com.chart.manager;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leo on 2018/12/7.
 */

public class PieChartManager {

    private int showType = HOLE_SHOW_TYPE;

    private boolean isMarkerViewEnabled = true;

    public static final int FULL_SHOW_TYPE = 0;
    public static final int HOLE_SHOW_TYPE = 1;

    private PieChart pieChart;
    private Context context;
    public PieChartManager(PieChart pieChart, Context context) {
        this.pieChart = pieChart;
        this.context = context;
        initChart();
    }


    private void initChart() {
        pieChart.setUsePercentValues(true);//显示成百分比  
        pieChart.getDescription().setEnabled(false);

        pieChart.setHoleRadius(58f);//半径    
        pieChart.setTransparentCircleRadius(61f);// 半透明圈一般要比半径大，才有效果    
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        pieChart.setDrawCenterText(true);//饼状图中间可以添加文字    
        // pieChart.setRotationAngle(90);// 初始旋转角度    
        pieChart.setRotationEnabled(true);// 可以手动旋转 

        //默认不绘制slice上的标签
        pieChart.setDrawEntryLabels(false);

        pieChart.setDrawCenterText(false);//饼状图中间不可以添加文字  

        //legend的设置
        //显示位置 下方居中
        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        //设置多个标签的情况自动换行
        legend.setWordWrapEnabled(true);
        //是否绘制在图表里面
        legend.setDrawInside(false);
    }

    public void setIntegerData(List<Integer> valueList,List<String> labelList){
        if (valueList == null || valueList.size() == 0) {
            return;
        }

        List<Float> valueListFloat = new ArrayList<>();
        for (int i = 0; i < valueList.size(); i++) {
            Integer integer = valueList.get(i);
            if (integer == null) {
                valueListFloat.add(0f);
            } else {
                valueListFloat.add(Float.valueOf(integer));
            }
        }

        setData(valueListFloat, labelList);
    }

    public void setData(List<Float> valueList,List<String> labelList){
        //只支持最多显示二十组数据
        if (valueList == null || valueList.size() == 0 || labelList == null ||
            labelList.size() == 0 || valueList.size() != labelList.size() || valueList.size()>20) {
            pieChart.clear();
            return;
        }

        for (int i = 0; i < valueList.size(); i++) {
            Float value = valueList.get(i);
            if (value == null) {
                valueList.set(i, 0f);
            }
        }

        for (int i = 0; i < labelList.size(); i++) {
            String label = labelList.get(i);
            if (label == null) {
                labelList.set(i, "");
            }
        }

        ArrayList<PieEntry> entries = new ArrayList<>();

        for (int i = 0; i < valueList.size(); i++) {

            entries.add(new PieEntry(valueList.get(i), labelList.get(i)));
        }

        PieDataSet pieDataSet = new PieDataSet(entries,"");
        pieDataSet.setDrawIcons(false);
        pieDataSet.setSliceSpace(2f);
        pieDataSet.setSelectionShift(5f);

        ArrayList<Integer> colors = new ArrayList<>();


        for (int i = 0; i < ColorUtils.DEFAULT_COLORS.length; i++) {
            colors.add(ColorUtils.DEFAULT_COLORS[i]);
        }
        for (int c : ColorTemplate.MATERIAL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        Log.d("dym------------------->", "colors.size= "+colors.size());
        pieDataSet.setColors(colors);

        if (isMarkerViewEnabled) {
            PieChartMarkView mv = new PieChartMarkView(context,labelList,valueList);
            mv.setChartView(pieChart); // For bounds control
            pieChart.setMarker(mv); // Set the marker to the chart
        } else {
            pieChart.setMarker(null);
        }

        if (showType == HOLE_SHOW_TYPE) {
            pieChart.setHoleRadius(50f);//半径    
            pieChart.setTransparentCircleRadius(53f);// 半透明圈一般要比半径大，才有效果    
            pieChart.setDrawHoleEnabled(true);
            pieChart.setHoleColor(Color.WHITE);
            pieChart.setTransparentCircleColor(Color.WHITE);
            pieChart.setTransparentCircleAlpha(110);
        } else if(showType == FULL_SHOW_TYPE){
            pieChart.setDrawHoleEnabled(false);
            pieChart.setHoleRadius(0f);
            pieChart.setTransparentCircleRadius(0f);
        }

        PieData data = new PieData(pieDataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);



        // undo all highlights
        pieChart.highlightValues(null);

        pieChart.invalidate();

    }


    public void setShowType(int showType) {
        if (showType == HOLE_SHOW_TYPE || showType == FULL_SHOW_TYPE) {
            this.showType = showType;
        }
    }

    public void setMarkerViewEnabled(boolean markerViewEnabled) {
        this.isMarkerViewEnabled = markerViewEnabled;
    }
}
