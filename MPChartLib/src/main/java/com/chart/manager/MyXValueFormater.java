package com.chart.manager;

import android.util.Log;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import java.util.List;

/**
 * Created by leo on 2018/12/3.
 */

public class MyXValueFormater implements IAxisValueFormatter {
    private List<String> xValues;
    public MyXValueFormater(List<String> xValues){
        this.xValues = xValues;
    }

    @Override public String getFormattedValue(float value, AxisBase axis) {
        int formatValueIndex = (int) value;
        Log.d("dym------------------->", "value= "+value+",formatValueIndex= "+formatValueIndex);
        if (formatValueIndex >= 0 && formatValueIndex < xValues.size()) {
            return xValues.get(formatValueIndex);
        } else {
            return "";
        }
        // return xValues.get((int) value % xValues.size());
    }
}
