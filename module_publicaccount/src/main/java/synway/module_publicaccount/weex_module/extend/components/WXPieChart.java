package synway.module_publicaccount.weex_module.extend.components;

import android.content.Context;
import android.support.annotation.NonNull;

import com.chart.manager.PieChartManager;
import com.github.mikephil.charting.charts.PieChart;
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
 * @name 饼状图
 * @describe
 * @time 2018/12/10 10:27
 */
public class WXPieChart extends WXComponent<PieChart> {
    private PieChart mPieChart;
    private PieChartManager mPieChartManager;

    public WXPieChart(WXSDKInstance instance, WXVContainer parent, BasicComponentData basicComponentData) {
        super(instance, parent, basicComponentData);
    }
//    public WXPieChart(WXSDKInstance instance, WXDomObject dom, WXVContainer parent) {
//        super(instance, dom, parent);
//    }

    @Override
    protected PieChart initComponentHostView(@NonNull Context context) {
        mPieChart = new PieChart(context);
        mPieChartManager = new PieChartManager(mPieChart, context);
        return mPieChart;
    }

    @WXComponentProp(name = "showType")
    public void setShowType(int showType) {
        if (mPieChartManager != null) {
            mPieChartManager.setShowType(showType);
        }
    }

    @JSMethod
    public void setFloatData(List<Float> valueList, List<String> labelList) {
        if (mPieChartManager != null) {
            mPieChartManager.setData(valueList, labelList);
        }
    }

    @JSMethod
    public void setIntData(List<Integer> valueList, List<String> labelList) {
        if (mPieChartManager != null) {
            mPieChartManager.setIntegerData(valueList, labelList);
        }
    }
}
