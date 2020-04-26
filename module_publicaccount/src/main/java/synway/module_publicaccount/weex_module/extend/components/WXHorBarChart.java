package synway.module_publicaccount.weex_module.extend.components;

import android.content.Context;
import android.support.annotation.NonNull;

import com.chart.manager.HorBarChartManager;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.ui.action.BasicComponentData;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXComponentProp;
import com.taobao.weex.ui.component.WXVContainer;

import java.util.List;

/**
 * @author XuKaiyin
 * @class com.sys.weexdemo.weex_module.extend.components
 * @name
 * @describe
 * @time 2019/4/22 16:53
 */
public class WXHorBarChart extends WXComponent<HorizontalBarChart> {
    private HorizontalBarChart mHorBarChart;
    private HorBarChartManager mHorBarChartManager;

    public WXHorBarChart(WXSDKInstance instance, WXVContainer parent, BasicComponentData
            basicComponentData) {
        super(instance, parent, basicComponentData);
    }
    //    public WXHorBarChart(WXSDKInstance instance, WXDomObject dom, WXVContainer parent) {
    //        super(instance, dom, parent);
    //    }

    @Override
    protected HorizontalBarChart initComponentHostView(@NonNull Context context) {
        mHorBarChart = new HorizontalBarChart(context);
        mHorBarChartManager = new HorBarChartManager(mHorBarChart, context);
        return mHorBarChart;
    }

    @WXComponentProp(name = "showType")
    public void setShowType(int showType) {
        if (mHorBarChartManager != null) {
            mHorBarChartManager.setShowType(showType);
        }
    }

    @JSMethod
    public void setMultitermIntData(List<List<Integer>> lineYValuesList, List<String> lineXValues,
                                    List<String> lineNameList, String markViewlabelAttr) {
        if (mHorBarChartManager != null) {
            mHorBarChartManager.setIntegerData(lineYValuesList, lineXValues, lineNameList,
                    markViewlabelAttr);
        }
    }

    @JSMethod
    public void setMultitermFloatData(List<List<Float>> lineYValuesList, List<String> lineXValues,
                                      List<String> lineNameList, String markViewlabelAttr) {
        if (mHorBarChartManager != null) {
            mHorBarChartManager.setData(lineYValuesList, lineXValues, lineNameList,
                    markViewlabelAttr);
        }
    }

    @JSMethod
    public void setSingleIntData(List<Integer> lineYValues, List<String> lineXValues,
                                 String lineName, String markViewlabelAttr) {
        if (mHorBarChartManager != null) {
            mHorBarChartManager.setIntegerData(lineYValues, lineXValues, lineName,
                    markViewlabelAttr);
        }
    }

    @JSMethod
    public void setSingleFloatData(List<Float> lineYValues, List<String> lineXValues,
                                   String lineName, String markViewlabelAttr) {
        if (mHorBarChartManager != null) {
            mHorBarChartManager.setData(lineYValues, lineXValues, lineName, markViewlabelAttr);
        }
    }
}
