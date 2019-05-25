package com.yangjiaying.rain.tianqi;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

/**
 * @author yangjiaying
 * @data 19-4-8 23 20
 * @email 1296813487@qq.com
 */
public class DecimalFormatter extends ValueFormatter implements IAxisValueFormatter {
    private DecimalFormat format;

    public DecimalFormatter() {
        format = new DecimalFormat("###,###,##0.00");
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return format.format(value) + "$";
    }
}