package synway.module_publicaccount.weex_module.extend.components;

import android.content.Context;
import android.support.annotation.NonNull;

import com.chart.manager.LineChartManager;
import com.github.mikephil.charting.charts.LineChart;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.ui.action.BasicComponentData;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXVContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.taobao.weex.dom.WXDomObject;

/**
 * Created by huangxi
 * DATE :2018/12/4
 * Description ：Weex 折线图
 */

public class WXLineChart extends WXComponent<LineChart> implements LineChartManager.OnValueClickListener {
    private LineChart lineChart;
    private LineChartManager lineChartManager;

    public WXLineChart(WXSDKInstance instance, WXVContainer parent, BasicComponentData basicComponentData) {
        super(instance, parent, basicComponentData);
    }
//    public WXLineChart(WXSDKInstance instance, WXDomObject dom, WXVContainer parent) {
//        super(instance, dom, parent);
//    }

    @Override
    protected LineChart initComponentHostView(@NonNull Context context) {
        lineChart = new LineChart(context);
        lineChartManager = new LineChartManager(lineChart, context);
        return lineChart;
    }

    @JSMethod
    public void setMultitermIntData(List<List<Integer>> lineYValuesList, List<String> lineXValues,
                                    List<String> lineNameList, String markViewlabelAttr) {
        if (lineChartManager != null) {
            lineChartManager.setIntegerData(lineYValuesList, lineXValues, lineNameList,
                    markViewlabelAttr);
        }
    }

    @JSMethod
    public void setMultitermFloatData(List<List<Float>> lineYValuesList, List<String> lineXValues,
                                      List<String> lineNameList, String markViewlabelAttr) {
        if (lineChartManager != null) {
            lineChartManager.setData(lineYValuesList, lineXValues, lineNameList, markViewlabelAttr);
        }
    }

    @JSMethod
    public void setSingleIntData(List<Integer> lineYValues, List<String> lineXValues,
                                 String lineName, String markViewlabelAttr) {
        if (lineChartManager != null) {
            lineChartManager.setIntegerData(lineYValues, lineXValues, lineName, markViewlabelAttr);
        }
    }

    @JSMethod
    public void setSingleFloatData(List<Float> lineYValues, List<String> lineXValues,
                                   String lineName, String markViewlabelAttr) {
        if (lineChartManager != null) {
            lineChartManager.setData(lineYValues, lineXValues, lineName, markViewlabelAttr);
        }
    }

    @JSMethod
    public void setLineChartDescription(String description) {
        if (lineChartManager != null) {
            lineChartManager.setDescription(description);
        }
    }

    @JSMethod
    public void setOnValueClickListener() {
        if (lineChartManager != null) {
            lineChartManager.setOnValueClickListener(this);
        }
    }

    @JSMethod
    public void setMarkerViewEnabled(boolean isMarkerViewEnabled) {
        if (lineChartManager != null) {
            lineChartManager.setMarkerViewEnabled(isMarkerViewEnabled);
        }
    }

    @Override
    public void onClick(String xValueFormat, Float yValue, List<Float> yValues, List<String> lineNameList, String description) {
        Map<String, Object> params = new HashMap<>();
        params.put("xValueFormat", xValueFormat);
        params.put("yValue", yValue);
        params.put("yValues", yValues);
        params.put("lineNameList", lineNameList);
        params.put("description", description);
        getInstance().fireGlobalEventCallback("WxChartValue", params);
    }
}
