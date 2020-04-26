package com.chart.manager;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.BarLineChartTouchListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leo on 2018/11/28.
 */

public class LineChartManager {

    private LineChart lineChart;
    private XAxis xAxis;                //X轴
    private YAxis leftYAxis;            //左侧Y轴
    private YAxis rightYAxis;           //右侧Y轴 自定义XY轴值
    private Legend legend;              //图例
    private LimitLine limitLine;        //限制线
    private boolean isMarkerViewEnabled = false;
    private Context context;
    private boolean isDataZero = false;
    //对于图表的描述，也可以用作特殊值的传入，可以在点击接口中反馈出来，这个值可以与图表无关，不作界面上的展示
    private String description = "";
    public LineChartManager(LineChart lineChart, Context context) {
        this.lineChart = lineChart;
        leftYAxis = lineChart.getAxisLeft();
        rightYAxis = lineChart.getAxisRight();
        xAxis = lineChart.getXAxis();
        legend = lineChart.getLegend();
        this.context = context;
        initChart(lineChart);
    }

    /**
     * 初始化图表
     */
    private void initChart(LineChart lineChart) {
        /***图表设置***/
        //是否展示网格线
        lineChart.setDrawGridBackground(false);
        // lineChart.setBackgroundColor(Color.WHITE);
        //是否显示边界
        lineChart.setDrawBorders(false);
        //是否隐藏描述
        lineChart.getDescription().setEnabled(false);
        //是否支持缩放
        lineChart.setScaleEnabled(true);
        lineChart.setScaleYEnabled(false);
        //是否支持两个手指缩放
        lineChart.setPinchZoom(true);
        //是否支持双击缩放
        lineChart.setDoubleTapToZoomEnabled(true);
        lineChart.setMaxHighlightDistance(20);

        /***XY轴的设置***/

        //是否禁用Y轴
        rightYAxis.setEnabled(false);
        leftYAxis.setEnabled(true);
        xAxis.setEnabled(true);

        xAxis.setDrawGridLines(false);
        leftYAxis.setDrawGridLines(false);

        //设置x轴方向
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //设置X轴坐标之间的最小间隔
        xAxis.setGranularity(1);
        //设置x轴方向
        xAxis.setLabelRotationAngle(-60);

        //设置x轴的起点
        xAxis.setAxisMinimum(0f);

        //设置y轴的起点
        leftYAxis.setAxisMinimum(0f);

        xAxis.setAxisLineWidth(3);
        leftYAxis.setAxisLineWidth(3);


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
     * 曲线初始化设置 一个LineDataSet 代表一条曲线
     *
     * @param lineDataSet 线条
     * @param color       线条颜色
     *
     */
    private void initLineDataSet(LineDataSet lineDataSet, int color) {

        lineDataSet.setLineWidth(3.5f);
        lineDataSet.setCircleRadius(3.5f);
        lineDataSet.setHighLightColor(Color.rgb(244, 117, 117));
        lineDataSet.setColor(color);
        lineDataSet.setCircleColor(color);
        lineDataSet.setDrawValues(false);

        //设置曲线值的圆点是实心还是空心
        // lineDataSet.setDrawCircles(true);
        // lineDataSet.setDrawCircleHole(true);
        // lineDataSet.setValueTextSize(10f);
        //设置折线图填充
        lineDataSet.setDrawFilled(false);
        // if (mode == null) {
        //     //设置曲线展示为圆滑曲线（如果不设置则默认折线）
        //     lineDataSet.setMode(LineDataSet.Mode.LINEAR);
        // } else {
        //     lineDataSet.setMode(mode);
        // }
    }


    /**
     *
     * @param lineYValues 一条折线y轴的值(Int型)
     * @param lineXValues 关于折线图x轴的值(String型)
     * @param lineName 一条折线的名称，可以为null
     * @param markViewlabelAttr y轴值的单位，主要在markview上面显示，可以为null
     */
    public void setIntegerData(List<Integer> lineYValues,List<String> lineXValues, String lineName,String markViewlabelAttr) {
        if (lineYValues == null || lineYValues.size() == 0) {
            lineChart.clear();
            return;
        }
        List<Float> lineYValuesFloat = new ArrayList<>();
        for (int i = 0; i < lineYValues.size(); i++) {
            Integer integer = lineYValues.get(i);
            if (integer == null) {
                lineYValuesFloat.add(0f);
            } else {
                lineYValuesFloat.add(Float.valueOf(integer));
            }
        }
        setData(lineYValuesFloat,lineXValues,lineName,markViewlabelAttr,true);
    }


    /**
     *
     * @param lineYValuesList 多条折线y轴的值(Int型)
     * @param lineXValues 关于折线图x轴的值(String型)
     * @param lineNameList 多条折线的名称，可以为null
     * @param markViewlabelAttr y轴值的单位，主要在markview上面显示，可以为null
     */
    public void setIntegerData(List<List<Integer>> lineYValuesList, List<String> lineXValues, List<String> lineNameList,String markViewlabelAttr){
        if (lineYValuesList == null || lineYValuesList.size() == 0) {
            lineChart.clear();
            return;
        }
        for (int i = 0; i < lineYValuesList.size(); i++) {
            List<Integer> lineYValues = lineYValuesList.get(i);
            if (lineYValues == null || lineYValues.size() == 0) {
                lineChart.clear();
                return;
            }
        }
        List<List<Float>> lineYValuesListFloat = new ArrayList<>();
        for (List<Integer> lineYValues : lineYValuesList) {
            List<Float> lineYValuesFloat = new ArrayList<>();
            for (int i = 0; i < lineYValues.size(); i++) {
                Integer integer = lineYValues.get(i);
                if (integer == null) {
                    lineYValuesFloat.add(0f);
                } else {
                    lineYValuesFloat.add(Float.valueOf(integer));
                }
            }
            lineYValuesListFloat.add(lineYValuesFloat);
        }
        setData(lineYValuesListFloat, lineXValues, lineNameList, markViewlabelAttr, true);
    }


    /**
     *
     * @param lineYValues 一条折线y轴的值(Float型)
     * @param lineXValues 关于折线图x轴的值(String型)
     * @param lineName 一条折线的名称，可以为null
     * @param markViewlabelAttr y轴值的单位，主要在markview上面显示，可以为null
     */
    public void setData(List<Float> lineYValues,List<String> lineXValues, String lineName,String markViewlabelAttr){
        setData(lineYValues,lineXValues,lineName,markViewlabelAttr,false);
    }
    /**
     *
     * @param lineYValuesList 多条折线y轴的值(Float型)
     * @param lineXValues 关于折线图x轴的值(String型)
     * @param lineNameList 多条折线的名称，可以为null
     * @param markViewlabelAttr y轴值的单位，主要在markview上面显示，可以为null
     */
    public void setData(List<List<Float>> lineYValuesList, List<String> lineXValues, List<String> lineNameList,String markViewlabelAttr){
        setData(lineYValuesList,lineXValues,lineNameList,markViewlabelAttr,false);
    }


    /**
     *
     * @param lineYValues 一条折线y轴的值(Float型)
     * @param lineXValues 关于折线图x轴的值(String型)
     * @param lineName 一条折线的名称，可以为null
     * @param markViewlabelAttr y轴值的单位，主要在markview上面显示，可以为null
     * @param isRound 对y轴的值是否取整
     */
    private void setData(final List<Float> lineYValues, final List<String> lineXValues,String lineName, String markViewlabelAttr, boolean isRound) {
        if (lineChart == null) {
            return;
        }
        //对数据进行验证
        if (lineYValues == null || lineYValues.size() == 0 || lineXValues == null ||
            lineXValues.size() == 0 || lineYValues.size() != lineXValues.size()) {
            lineChart.clear();
            return;
        }

        //加强对 lineYValues 和 lineXValues 里面值的校验，有可能存在null，这里处理为将null替换为相应的值
        for (int i = 0; i < lineYValues.size(); i++) {
            Float value = lineYValues.get(i);
            if (value == null) {
                // Log.d("dym------------------->", "lineYValue == null 重设值");
                lineYValues.set(i,0f);
            }
        }

        for (int i = 0; i < lineXValues.size(); i++) {
            String value = lineXValues.get(i);
            if (value == null || value.equals("null")) {
                // Log.d("dym------------------->", "lineXValue == null 重设值");
                lineXValues.set(i, "");
            }
        }

        if (TextUtils.isEmpty(lineName)) {
            lineName = "";
        }
        if (TextUtils.isEmpty(markViewlabelAttr)) {
            markViewlabelAttr = "";
        }

        lineChart.clear();
        MyXValueFormater myXValueFormater = new MyXValueFormater(lineXValues);
        xAxis.setValueFormatter(myXValueFormater);
        xAxis.setLabelCount(lineXValues.size(),false);
        xAxis.setAxisMinimum(0f);


        isDataZero = checkValueIsZero(lineYValues);

        if(isDataZero){
            leftYAxis.setAxisMinimum(-1f);
            leftYAxis.setLabelCount(3,false);
            leftYAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override public String getFormattedValue(float value, AxisBase axis) {
                    // Log.d("dym------------------->", "value= "+value);
                    if (value == 0f) {
                        // Log.d("dym------------------->", "进来了");
                        return 0 + "";
                    } else {
                        return "";
                    }
                }
            });
        }else{
            leftYAxis.setAxisMinimum(0f);
            leftYAxis.setLabelCount(5,false);
            leftYAxis.setValueFormatter(null);
        }

        List<String> lineNameList = new ArrayList<>();
        lineNameList.add(lineName);
        List<List<Float>> lineYValuesList = new ArrayList<>();
        lineYValuesList.add(lineYValues);

        //设置markView
        if (isMarkerViewEnabled) {
            LineChartMarkView lineChartMarkView = new LineChartMarkView(context, lineNameList,
                lineXValues, lineYValuesList, markViewlabelAttr, isRound);
            lineChartMarkView.setChartView(lineChart);
            lineChart.setMarker(lineChartMarkView);
        } else {
            lineChart.setMarker(null);
            if (onValueClickListener != null) {
                final String finalLineName = lineName;
                lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                    @Override public void onValueSelected(Entry e, Highlight h) {
                        String xValueFormat = lineXValues.get((int) e.getX());
                        List<Float> yValues = new ArrayList<>();
                        yValues.add(lineYValues.get((int) e.getX()));
                        List<String> lineNameList = new ArrayList<>();
                        lineNameList.add(finalLineName);
                        Log.d("dym------------------->",
                            "单条线点击事件 xValueFormat= " + xValueFormat + ",yValue= " +
                                yValues.get(0) + ",e.getY= " + e.getY() + ",lineName= " +
                                lineNameList.get(0) + ",description= " + description);
                        if (onValueClickListener != null) {
                            onValueClickListener.onClick(xValueFormat, e.getY(), yValues,
                                lineNameList, description);
                        }
                    }
                    @Override public void onNothingSelected() {

                    }
                });
            } else {
                lineChart.setOnChartValueSelectedListener(null);
            }
        }


        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < lineYValues.size(); i++) {
            entries.add(new Entry(i, lineYValues.get(i)));
        }

        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, lineName);
        initLineDataSet(lineDataSet, ColorUtils.DEFAULT_COLORS[0]);

        LineData data = new LineData();
        data.addDataSet(lineDataSet);
        lineChart.setData(data);
        lineChart.animateX(750);
        //当拉伸后再滑动查看，可能会因为滑动结束后绘制时间比较久，如果再次设置数据会出现样式错误，所以这里
        //当需要再次设置数据的时候，自动结束绘制。
        BarLineChartTouchListener listener = (BarLineChartTouchListener) lineChart.getOnTouchListener();
        listener.stopDeceleration();
        // 重置折线图的格式
        lineChart.fitScreen();
        lineChart.invalidate();

    }


    /**
     *
     * @param lineYValuesList 多条折线y轴的值(Float型)
     * @param lineXValues 关于折线图x轴的值(String型)
     * @param lineNameList 多条折线的名称，可以为null
     * @param markViewlabelAttr y轴值的单位，主要在markview上面显示，可以为null
     * @param isRound 对y轴的值是否取整
     */
    private void setData(final List<List<Float>> lineYValuesList, final List<String> lineXValues, List<String> lineNameList, String markViewlabelAttr, boolean isRound) {
        if (lineChart == null) {
            return;
        }
        //对数据进行验证
        if (lineYValuesList == null || lineYValuesList.size() == 0 || lineXValues == null ||
            lineXValues.size() == 0 || lineYValuesList.size()>20) {
            lineChart.clear();
            return;
        }

        for (int i = 0; i < lineYValuesList.size(); i++) {
            List<Float> lineYValues = lineYValuesList.get(i);
            if (lineYValues == null) {
                lineChart.clear();
                return;
            }

            if (lineYValues.size() != lineXValues.size()) {
                lineChart.clear();
                return;
            }
        }

        //加强对 lineYValuesList中的每组lineYValues 和 lineXValues 里面值的校验，有可能存在null，这里处理为将null替换为相应的值
        for (int i = 0; i < lineXValues.size(); i++) {
            String value = lineXValues.get(i);
            if (value == null || value.equals("null")) {
                // Log.d("dym------------------->", "lineXValue == null 重设值");
                lineXValues.set(i, "");
            }
        }

        for (List<Float> lineYValues : lineYValuesList) {
            for (int i = 0; i < lineYValues.size(); i++) {
                Float value = lineYValues.get(i);
                if (value == null) {
                    // Log.d("dym------------------->", "lineYValue == null 重设值");
                    lineYValues.set(i,0f);
                }
            }
        }

        if (lineNameList == null || lineNameList.size() == 0 || lineYValuesList.size()!= lineNameList.size()) {
            //若lineNameList如若没有合适值则自定义一个
            lineNameList = new ArrayList<>();
            int count = lineYValuesList.size();
            for (int i = 0; i < count; i++) {
                lineNameList.add("");
            }
        }else {
            for (int i = 0; i < lineNameList.size(); i++) {
                String value = lineNameList.get(i);
                if (value == null || value.equals("null")) {
                    // Log.d("dym------------------->", "lineName == null 重设值");
                    lineNameList.set(i, "");
                }
            }
        }

        if (TextUtils.isEmpty(markViewlabelAttr)) {
            markViewlabelAttr = "";
        }
        lineChart.clear();
        MyXValueFormater myXValueFormater = new MyXValueFormater(lineXValues);
        xAxis.setValueFormatter(myXValueFormater);
        xAxis.setLabelCount(lineXValues.size(),false);
        xAxis.setAxisMinimum(0f);

        isDataZero = checkValuesIsZero(lineYValuesList);

        if(isDataZero){
            leftYAxis.setAxisMinimum(-1f);
            leftYAxis.setLabelCount(3,false);
            leftYAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override public String getFormattedValue(float value, AxisBase axis) {
                    // Log.d("dym------------------->", "value= "+value);
                    if (value == 0f) {
                        // Log.d("dym------------------->", "进来了");
                        return 0 + "";
                    } else {
                        return "";
                    }
                }
            });
        }else{
            leftYAxis.setAxisMinimum(0f);
            leftYAxis.setLabelCount(5,false);
            leftYAxis.setValueFormatter(null);
        }

        //设置markView
        if (isMarkerViewEnabled) {
            LineChartMarkView lineChartMarkView = new LineChartMarkView(context, lineNameList,
                lineXValues, lineYValuesList, markViewlabelAttr, isRound);
            lineChartMarkView.setChartView(lineChart);
            lineChart.setMarker(lineChartMarkView);
        } else {
            lineChart.setMarker(null);

            if (onValueClickListener != null) {
                final List<String> finalLineNameList = lineNameList;
                lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                    @Override public void onValueSelected(Entry e, Highlight h) {
                        String xValueFormat = lineXValues.get((int) e.getX());
                        List<Float> yValues = new ArrayList<>();
                        List<String> lineNames = new ArrayList<>();
                        for (int i = 0; i < finalLineNameList.size(); i++) {
                            if (i > 19) {
                                //当前只支持最多显示20组
                                break;
                            }
                            yValues.add(lineYValuesList.get(i).get((int) e.getX()));
                            lineNames.add(finalLineNameList.get(i));
                        }

                        Log.d("dym------------------->",
                            "多条线点击事件 xValueFormat= " + xValueFormat + ",e.getY= " + e.getY() +
                                ",description= " + description);
                        for (int i = 0; i < yValues.size(); i++) {
                            Log.d("dym------------------->",
                                "多条线点击事件 lineYValue= " + yValues.get(i) + ",lineYName= " +
                                    finalLineNameList.get(i));
                        }
                        if (onValueClickListener != null) {
                            onValueClickListener.onClick(xValueFormat,e.getY(), yValues, lineNames, description);
                        }
                    }


                    @Override public void onNothingSelected() {

                    }
                });
            } else {
                lineChart.setOnChartValueSelectedListener(null);
            }
        }

        LineData data = new LineData();
        for (int i = 0; i < lineYValuesList.size(); i++) {

            //目前只支持同时展现20条线，超过20，则只展现前20条
            if (i > 19) {
                break;
            }

            List<Float> lineYValues = lineYValuesList.get(i);
            ArrayList<Entry> entries = new ArrayList<>();
            for (int j = 0; j < lineYValues.size(); j++) {
                entries.add(new Entry(j, lineYValues.get(j)));
            }
            // 每一个LineDataSet代表一条线
            LineDataSet lineDataSet = new LineDataSet(entries, lineNameList.get(i));
            initLineDataSet(lineDataSet, ColorUtils.DEFAULT_COLORS[i]);
            data.addDataSet(lineDataSet);
        }

        lineChart.setData(data);

        // 重置折线图的格式
        // Matrix m = new Matrix();
        // m.postScale(1.0f, 1f);
        // lineChart.getViewPortHandler().refresh(m, lineChart, false);
        lineChart.animateX(750);
        //当拉伸后再滑动查看，可能会因为滑动结束后绘制时间比较久，如果再次设置数据会出现样式错误，所以这里
        //当需要再次设置数据的时候，自动结束绘制。
        BarLineChartTouchListener listener = (BarLineChartTouchListener) lineChart.getOnTouchListener();
        listener.stopDeceleration();
        //利用反射调用方法
        // BarLineChartTouchListener listener = (BarLineChartTouchListener) ReflectUtils.getField(lineChart, "mChartTouchListener");
        // Method method = ReflectUtils.getMethod(listener.getClass(), "stopDeceleration", new Class[0]);
        // try {
        //     method.invoke(listener, new Object[] {});
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
        lineChart.fitScreen();

        lineChart.invalidate();


    }


    /**
     * 是否在点击的时候显示markerView
     * @param isMarkerViewEnabled
     */
    public void setMarkerViewEnabled(boolean isMarkerViewEnabled){
        //markview的模式和点击事件接口是互斥的，不可同时存在。
        this.isMarkerViewEnabled = isMarkerViewEnabled;
        if (isMarkerViewEnabled) {
            this.onValueClickListener = null;
        }
    }

    /**
     * 设置Y轴值 暂不对外提供
     *
     * @param max
     * @param min
     * @param labelCount
     */
    private void setYAxisData(float max, float min, int labelCount) {
        leftYAxis.setAxisMaximum(max);
        leftYAxis.setAxisMinimum(min);
        leftYAxis.setLabelCount(labelCount, false);

        lineChart.invalidate();
    }


    /**
     * 设置Y轴的起始点
     * @param min
     */
    public void setYAxisMinimum(float min) {
        leftYAxis.setAxisMinimum(min);
        lineChart.invalidate();
    }

    /**
     * 设置X轴的显示值,暂不对外提供
     *
     * @param min        x轴最小值
     * @param max        x轴最大值
     * @param labelCount x轴的分割数量
     */
    private void setXAxisData(float min, float max, int labelCount) {
        xAxis.setAxisMinimum(min);
        xAxis.setAxisMaximum(max);
        xAxis.setLabelCount(labelCount, false);
        lineChart.invalidate();
    }

    private boolean checkValueIsZero(List<Float> yValues){
        for (Float yValue : yValues) {
            //只要一个值不为0，就返回false
            if (yValue != 0f) {
                return false;
            }
        }

        return true;
    }

    private boolean checkValuesIsZero(List<List<Float>> yValuesList){

        for (List<Float> list : yValuesList) {
            for (Float value : list) {
                // 只要一个值不为0，就返回false
                if (value != 0f) {
                    return false;
                }
            }
        }
        return true;
    }



    public interface OnValueClickListener{
        void onClick(String xValueFormat,Float yValue,List<Float> yValues,List<String> lineNameList,String description);
    }
    private OnValueClickListener onValueClickListener;


    /**
     * 需要注意是要在 setData之前设置监听。
     * @param onValueClickListener
     */
    public void setOnValueClickListener(OnValueClickListener onValueClickListener) {
        this.onValueClickListener = onValueClickListener;
        //markview的模式和点击事件接口是互斥的，不可同时存在。
        if (onValueClickListener != null) {
            this.isMarkerViewEnabled = false;
        }
    }


    public void setDescription(String description) {
        if (!TextUtils.isEmpty(description)) {
            this.description = description;
        }

    }
}
