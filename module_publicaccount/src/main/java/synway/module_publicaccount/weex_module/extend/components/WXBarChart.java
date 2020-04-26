package synway.module_publicaccount.weex_module.extend.components;

import android.content.Context;
import android.support.annotation.NonNull;

import com.chart.manager.BarChartManager;
import com.github.mikephil.charting.charts.BarChart;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.ui.action.BasicComponentData;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXComponentProp;
import com.taobao.weex.ui.component.WXVContainer;

import java.util.List;

//import com.taobao.weex.dom.WXDomObject;

/**
 * @author XuKaiyin
 * @class com.sys.weexdemo.weex_module.extend.components
 * @name
 * @describe Weex 柱状图
 * @time 2018/12/6 8:56
 */
public class WXBarChart extends WXComponent<BarChart> {
    private BarChart mBarChart;
    private BarChartManager mBarChartManager;

    public WXBarChart(WXSDKInstance instance, WXVContainer parent, BasicComponentData basicComponentData) {
        super(instance, parent, basicComponentData);
    }
//    public WXBarChart(WXSDKInstance instance, WXDomObject dom, WXVContainer parent) {
//        super(instance, dom, parent);
//    }

    @Override
    protected BarChart initComponentHostView(@NonNull Context context) {
        mBarChart = new BarChart(context);
        mBarChartManager = new BarChartManager(mBarChart, context);
        return mBarChart;
    }

    @WXComponentProp(name = "showType")
    public void setShowType(int showType) {
        if (mBarChartManager != null) {
            mBarChartManager.setShowType(showType);
        }
    }

    @JSMethod
    public void setMultitermIntData(List<List<Integer>> lineYValuesList, List<String> lineXValues,
                                    List<String> lineNameList, String markViewlabelAttr) {
        if (mBarChartManager != null) {
            mBarChartManager.setIntegerData(lineYValuesList, lineXValues, lineNameList,
                    markViewlabelAttr);
        }
    }

    @JSMethod
    public void setMultitermFloatData(List<List<Float>> lineYValuesList, List<String> lineXValues,
                                      List<String> lineNameList, String markViewlabelAttr) {
        if (mBarChartManager != null) {
            mBarChartManager.setData(lineYValuesList, lineXValues, lineNameList, markViewlabelAttr);
        }
    }

    @JSMethod
    public void setSingleIntData(List<Integer> lineYValues, List<String> lineXValues,
                                 String lineName, String markViewlabelAttr) {
        if (mBarChartManager != null) {
            mBarChartManager.setIntegerData(lineYValues, lineXValues, lineName, markViewlabelAttr);
        }
    }

    @JSMethod
    public void setSingleFloatData(List<Float> lineYValues, List<String> lineXValues,
                                   String lineName, String markViewlabelAttr) {
        if (mBarChartManager != null) {
            mBarChartManager.setData(lineYValues, lineXValues, lineName, markViewlabelAttr);
        }
    }
}
