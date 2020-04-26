package com.chart.manager;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.listener.BarLineChartTouchListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leo on 2018/12/5.
 */

public class BarChartManager {
    private BarChart barChart;
    private XAxis xAxis;                //X轴
    private YAxis leftYAxis;            //左侧Y轴
    private YAxis rightYAxis;           //右侧Y轴 自定义XY轴值
    private Legend legend;              //图例
    private LimitLine limitLine;        //限制线
    private boolean isMarkerViewEnabled = true;
    private Context context;
    private int showType = GROUP_SHOW_TYPE;
    //分组显示
    public static final int GROUP_SHOW_TYPE = 0;
    //覆盖重叠，不分组显示(不推荐这种显示方式)
    public static final int OVERLAY_SHOW_TYPE = 1;

    public BarChartManager(BarChart barChart, Context context) {
        this.barChart = barChart;
        leftYAxis = barChart.getAxisLeft();
        rightYAxis = barChart.getAxisRight();
        xAxis = barChart.getXAxis();
        legend = barChart.getLegend();
        this.context = context;
        initChart(barChart);
    }





    private void initChart(BarChart barChart) {
        /***图表设置***/
        //是否展示网格线
        barChart.setDrawGridBackground(false);
        barChart.setBackgroundColor(Color.WHITE);
        //是否显示边界
        barChart.setDrawBorders(false);
        //是否隐藏描述
        barChart.getDescription().setEnabled(false);
        //是否支持缩放
        barChart.setScaleEnabled(true);
        barChart.setScaleYEnabled(false);
        //是否支持两个手指缩放
        barChart.setPinchZoom(true);
        //是否支持双击缩放
        barChart.setDoubleTapToZoomEnabled(false);
        //背景阴影
        barChart.setDrawBarShadow(false);
        barChart.setHighlightFullBarEnabled(false);
        //显示边框,默认不显示
        barChart.setDrawBorders(false);

        //防止缩放拖动的时候也触发弹出markView
        barChart.setHighlightPerDragEnabled(false);


        /***XY轴的设置***/
        //X轴设置显示位置在底部
        xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        // xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);
        // xAxis.setLabelCount(30,false);
        //设置x轴下方label方向
        xAxis.setLabelRotationAngle(-60);

        leftYAxis.setDrawGridLines(false);

        rightYAxis.setEnabled(false);

        //设置x轴的起点,barChart的情况会出现第一柱状图显示不全的现象
        // xAxis.setAxisMinimum(0f);

        //设置y轴的起点
        leftYAxis.setAxisMinimum(0f);

        // xAxis.setAxisLineWidth(3);
        // leftYAxis.setAxisLineWidth(3);


        /***折线图例 标签 设置***/

        //显示位置 下方居中
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        //设置多个标签的情况自动换行
        legend.setWordWrapEnabled(true);
        //是否绘制在图表里面
        legend.setDrawInside(false);
    }

    /**
     * 柱状图始化设置 一个BarDataSet 代表一列柱状图
     *
     * @param barDataSet 柱状图
     * @param color      柱状图颜色
     */
    private void initBarDataSet(BarDataSet barDataSet, int color) {
        barDataSet.setColor(color);
        // barDataSet.setFormLineWidth(1f);
        // barDataSet.setFormSize(15.f);
        //不显示柱状图顶部值
        barDataSet.setDrawValues(false);
        //设置点击后的效果为透明
        barDataSet.setHighLightAlpha(0);
        //        barDataSet.setValueTextSize(10f);
        //        barDataSet.setValueTextColor(color);
    }

    public void setIntegerData(List<Integer> barYValues,List<String> barXValues, String barName,String markViewlabelAttr) {
        if (barYValues == null || barYValues.size() == 0) {
            return;
        }
        List<Float> barYValuesFloat = new ArrayList<>();
        for (int i = 0; i < barYValues.size(); i++) {
            Integer integer = barYValues.get(i);
            if (integer == null) {
                barYValuesFloat.add(0f);
            } else {
                barYValuesFloat.add(Float.valueOf(integer));
            }
        }
        setData(barYValuesFloat,barXValues,barName,markViewlabelAttr,true);
    }

    public void setIntegerData(List<List<Integer>> barYValuesList, List<String> barXValues, List<String> barNameList,String markViewlabelAttr){
        if (barYValuesList == null || barYValuesList.size() == 0) {
            return;
        }
        for (int i = 0; i < barYValuesList.size(); i++) {
            List<Integer> barYValues = barYValuesList.get(i);
            if (barYValues == null || barYValues.size() == 0) {
                return;
            }
        }
        List<List<Float>> barYValuesListFloat = new ArrayList<>();
        for (List<Integer> barYValues : barYValuesList) {
            List<Float> barYValuesFloat = new ArrayList<>();
            for (int i = 0; i < barYValues.size(); i++) {
                Integer integer = barYValues.get(i);
                if (integer == null) {
                    barYValuesFloat.add(0f);
                } else {
                    barYValuesFloat.add(Float.valueOf(integer));
                }
            }
            barYValuesListFloat.add(barYValuesFloat);
        }
        setData(barYValuesListFloat, barXValues, barNameList, markViewlabelAttr, true);

    }

    /**
     *
     * @param barYValues 关于柱状图y轴的值(Float型)
     * @param barXValues 关于柱线图x轴的值(String型)
     * @param barName 一个柱状图的名称，可以为null
     * @param markViewlabelAttr y轴值的单位，主要在markview上面显示，可以为null
     */
    public void setData(List<Float> barYValues, List<String> barXValues, String barName, String markViewlabelAttr){
        setData(barYValues,barXValues,barName,markViewlabelAttr,false);
    }
    /**
     *
     * @param barYValuesList 多组柱状图y轴上的值(Float型)
     * @param barXValues 关于柱线图x轴的值(String型)
     * @param barNameList 多个柱状图的名称，可以为null
     * @param markViewlabelAttr y轴值的单位，主要在markview上面显示，可以为null
     */
    public void setData(List<List<Float>> barYValuesList, List<String> barXValues, List<String> barNameList,String markViewlabelAttr){
        setData(barYValuesList,barXValues,barNameList,markViewlabelAttr,false);
    }


    /**
     *
     * @param barYValues 关于柱状图y轴的值(Float型)
     * @param barXValues 关于柱线图x轴的值(String型)
     * @param barName 一个柱状图的名称，可以为null
     * @param markViewlabelAttr y轴值的单位，主要在markview上面显示，可以为null
     * @param isRound 对y轴的值是否取整
     */
    private void setData(List<Float> barYValues, List<String> barXValues, String barName, String markViewlabelAttr, boolean isRound) {
        if (barChart == null) {
            return;
        }
        //对数据进行验证
        if (barYValues == null || barYValues.size() == 0 || barXValues == null ||
            barXValues.size() == 0 || barYValues.size() != barXValues.size()) {
            barChart.clear();
            return;
        }

        //加强对 barYValues 和 barXValues 里面值的校验，有可能存在null，这里处理为将null替换为相应的值
        for (int i = 0; i < barYValues.size(); i++) {
            Float value = barYValues.get(i);
            if (value == null) {
                Log.d("dym------------------->", "barYValue == null 重设值");
                barYValues.set(i,0f);
            }
        }

        for (int i = 0; i < barXValues.size(); i++) {
            String value = barXValues.get(i);
            if (value == null || value.equals("null")) {
                Log.d("dym------------------->", "barXValue == null 重设值");
                barXValues.set(i, "");
            }
        }


        if (TextUtils.isEmpty(barName)) {
            barName = "";
        }
        if (TextUtils.isEmpty(markViewlabelAttr)) {
            markViewlabelAttr = "";
        }

        barChart.clear();
        MyXValueFormater myXValueFormater = new MyXValueFormater(barXValues);
        xAxis.setValueFormatter(myXValueFormater);
        xAxis.setLabelCount(barXValues.size(),false);
        xAxis.resetAxisMinimum();
        xAxis.resetAxisMaximum();
        xAxis.setCenterAxisLabels(false);

        leftYAxis.setLabelCount(5,false);

        List<String> barNameList = new ArrayList<>();
        barNameList.add(barName);
        List<List<Float>> barYValuesList = new ArrayList<>();
        barYValuesList.add(barYValues);

        //设置markView
        if (isMarkerViewEnabled) {

            if (showType == GROUP_SHOW_TYPE) {
                BarChartNormalMarkView barChartMarkView = new BarChartNormalMarkView(context,
                    barNameList,
                    barXValues, barYValuesList, markViewlabelAttr, isRound);
                barChartMarkView.setChartView(barChart);
                barChart.setMarker(barChartMarkView);
            } else if (showType == OVERLAY_SHOW_TYPE) {
                BarChartSpecialMarkView barChartMarkView = new BarChartSpecialMarkView(context,
                    barNameList,
                    barXValues, barYValuesList, markViewlabelAttr, isRound);
                barChartMarkView.setChartView(barChart);
                barChart.setMarker(barChartMarkView);
            }

        } else {
            barChart.setMarker(null);
        }

        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < barYValues.size(); i++) {
            entries.add(new BarEntry(i, barYValues.get(i)));
        }
        // 每一个BarDataSet代表一个柱状图
        BarDataSet barDataSet = new BarDataSet(entries, barName);
        initBarDataSet(barDataSet, ColorUtils.DEFAULT_COLORS[0]);

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barChart.animateX(750);
        //当拉伸后再滑动查看，可能会因为滑动结束后绘制时间比较久，如果再次设置数据会出现样式错误，所以这里
        //当需要再次设置数据的时候，自动结束绘制。
        BarLineChartTouchListener listener = (BarLineChartTouchListener) barChart.getOnTouchListener();
        listener.stopDeceleration();
        // 重置折线图的格式
        barChart.fitScreen();


        barChart.invalidate();
    }


    /**
     *
     * @param barYValuesList 多组柱状图y轴上的值(Float型)
     * @param barXValues 关于柱线图x轴的值(String型)
     * @param barNameList 多个柱状图的名称，可以为null
     * @param markViewlabelAttr y轴值的单位，主要在markview上面显示，可以为null
     * @param isRound 对y轴的值是否取整
     */
    private void setData(List<List<Float>> barYValuesList, List<String> barXValues, List<String> barNameList,String markViewlabelAttr,boolean isRound) {
        if (barChart == null) {
            return;
        }
        //对数据进行验证
        if (barYValuesList == null || barYValuesList.size() == 0 || barXValues == null ||
            barXValues.size() == 0|| barYValuesList.size()>20) {
            barChart.clear();
            return;
        }

        for (int i = 0; i < barYValuesList.size(); i++) {
            List<Float> barYValues = barYValuesList.get(i);
            if (barYValues == null) {
                barChart.clear();
                return;
            }

            if (barYValues.size() != barXValues.size()) {
                barChart.clear();
                return;
            }
        }

        //加强对 barYValuesList中的每组barYValues 和 barXValues 里面值的校验，有可能存在null，这里处理为将null替换为相应的值
        for (int i = 0; i < barXValues.size(); i++) {
            String value = barXValues.get(i);
            if (value == null || value.equals("null")) {
                Log.d("dym------------------->", "barXValue == null 重设值");
                barXValues.set(i, "");
            }
        }

        for (List<Float> barYValues : barYValuesList) {
            for (int i = 0; i < barYValues.size(); i++) {
                Float value = barYValues.get(i);
                if (value == null) {
                    Log.d("dym------------------->", "barYValue == null 重设值");
                    barYValues.set(i,0f);
                }
            }
        }

        if (barNameList == null || barNameList.size() == 0 ||
            barYValuesList.size() != barNameList.size()) {
            //若lineNameList如若没有合适值则自定义一个
            barNameList = new ArrayList<>();
            int count = barYValuesList.size();
            for (int i = 0; i < count; i++) {
                barNameList.add("");
            }
        } else {
            for (int i = 0; i < barNameList.size(); i++) {
                String value = barNameList.get(i);
                if (value == null || value.equals("null")) {
                    Log.d("dym------------------->", "barName == null 重设值");
                    barNameList.set(i, "");
                }
            }
        }


        if (TextUtils.isEmpty(markViewlabelAttr)) {
            markViewlabelAttr = "";
        }

        barChart.clear();
        MyXValueFormater myXValueFormater = new MyXValueFormater(barXValues);
        xAxis.setValueFormatter(myXValueFormater);
        xAxis.setLabelCount(barXValues.size(),false);
        if (showType == GROUP_SHOW_TYPE && barYValuesList.size() == 1) {
            xAxis.resetAxisMinimum();
            xAxis.resetAxisMaximum();
            xAxis.setCenterAxisLabels(false);
        }
        leftYAxis.setLabelCount(5,false);

        //设置markView
        if (isMarkerViewEnabled) {
            if (showType == GROUP_SHOW_TYPE) {
                BarChartNormalMarkView barChartMarkView = new BarChartNormalMarkView(context,
                    barNameList, barXValues, barYValuesList, markViewlabelAttr, isRound);
                barChartMarkView.setChartView(barChart);
                barChart.setMarker(barChartMarkView);
            } else if (showType == OVERLAY_SHOW_TYPE) {
                BarChartSpecialMarkView barChartMarkView = new BarChartSpecialMarkView(context,
                    barNameList, barXValues, barYValuesList, markViewlabelAttr, isRound);
                barChartMarkView.setChartView(barChart);
                barChart.setMarker(barChartMarkView);
            }
        } else {
            barChart.setMarker(null);
        }

        BarData data = new BarData();
        for (int i = 0; i < barYValuesList.size(); i++) {

            //目前只支持同时展现20条线，超过20，则只展现前20条
            if (i > 19) {
                break;
            }

            List<Float> barYValues = barYValuesList.get(i);
            ArrayList<BarEntry> entries = new ArrayList<>();
            for (int j = 0; j < barYValues.size(); j++) {
                entries.add(new BarEntry(j, barYValues.get(j)));
            }
            // 每一个LineDataSet代表一条线
            BarDataSet barDataSet = new BarDataSet(entries, barNameList.get(i));
            initBarDataSet(barDataSet, ColorUtils.DEFAULT_COLORS[i]);
            data.addDataSet(barDataSet);

        }

        barChart.setData(data);
        barChart.animateX(750);
        //当拉伸后再滑动查看，可能会因为滑动结束后绘制时间比较久，如果再次设置数据会出现样式错误，所以这里
        //当需要再次设置数据的时候，自动结束绘制。
        BarLineChartTouchListener listener = (BarLineChartTouchListener) barChart.getOnTouchListener();
        listener.stopDeceleration();
        barChart.fitScreen();

        if (showType == GROUP_SHOW_TYPE) {
            //一组数据的时候没必要设置
            if (barYValuesList.size() > 1) {

                // float groupSpace = 0.08f;
                // float barSpace = 0.03f; // x4 DataSet
                // float barWidth = 0.2f; // x4 DataSet
                // (0.2 + 0.03) * 4 + 0.08 = 1.00 -> interval per "group"
                //( barWidth+barSpace )*barCount + groupSpace = 1

                int barCount = barYValuesList.size();
                if (barCount > 20) {
                    barCount = 20;
                }
                Float[] formatData = GroupFormatUtils.map.get(barCount);
                float barWidth = formatData[0];
                float barSpace = formatData[1];
                float groupSpace = formatData[2];

                barChart.getBarData().setBarWidth(barWidth);
                int groupCount = barXValues.size();
                //多组group样式展现的时候 需要设置为0f，不然的话第一个柱状图会离x轴还有一处空白
                barChart.getXAxis().setAxisMinimum(0f);
                barChart.getXAxis().setCenterAxisLabels(true);
                barChart.getXAxis().setAxisMaximum(barChart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
                barChart.groupBars(0f, groupSpace, barSpace);
            } else {
                // barChart.getBarData().setBarWidth(0.85f);
            }
        }
        barChart.invalidate();
    }


    /**
     * 是否在点击的时候显示markerView
     * @param isMarkerViewEnabled
     */
    public void setMarkerViewEnabled(boolean isMarkerViewEnabled){
        this.isMarkerViewEnabled = isMarkerViewEnabled;
    }

    /**
     * 设置Y轴的起始点
     * @param min
     */
    public void setYAxisMinimum(float min) {
        leftYAxis.setAxisMinimum(min);
        barChart.invalidate();
    }

    private boolean checkTypeValid(int showType) {
        return showType == GROUP_SHOW_TYPE || showType == OVERLAY_SHOW_TYPE;
    }


    public void setShowType(int showType) {
        if (checkTypeValid(showType)) {
            this.showType = showType;
        }
    }


}
