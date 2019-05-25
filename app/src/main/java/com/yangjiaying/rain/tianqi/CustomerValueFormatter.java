package com.yangjiaying.rain.tianqi;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

public class CustomerValueFormatter extends ValueFormatter {

    private DecimalFormat mFormat;

    public CustomerValueFormatter() {
        //此处是显示数据的方式，显示整型或者小数后面小数位数自己随意确定
        mFormat = new DecimalFormat("###,###,##0");
//        mFormat = new DecimalFormat("abcd");
    }

}