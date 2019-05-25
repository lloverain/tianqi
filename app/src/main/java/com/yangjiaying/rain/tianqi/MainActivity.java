package com.yangjiaying.rain.tianqi;



import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements
        OnChartValueSelectedListener {
    private Typeface mTfRegular;
    private Typeface mTfLight;
    protected BarChart mChart;
    private HorizontalBarChart hBarChart;
    private LineChart lineChart;
    private ArrayList<BarEntry> datas = new ArrayList<>();//低温
    private ArrayList<BarEntry> gaowen = new ArrayList<>();//高温
    private List<String> tian = new ArrayList<>();
//    private String json;
private List<Entry> zheqian = new ArrayList<>();
    private List<Entry> zheqian1 = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
            if (Build.VERSION.SDK_INT >= 11) {
                  StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
                  StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
            }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mChart = findViewById(R.id.chart1);
        hBarChart = findViewById(R.id.hBarChart);
        lineChart = findViewById(R.id.lineChart);
        shuju();
//        initdata();
        initBarChart();
        initHBarChart();
        initLineChart();

    }

    /**
     * 初始化柱形图控件属性
     */
    private void initBarChart() {
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);
        mChart.getDescription().setEnabled(false);
        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);
        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);
        mChart.setDrawGridBackground(false);

//        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(mChart);

        //自定义坐标轴适配器，配置在X轴，xAxis.setValueFormatter(xAxisFormatter);
//        ValueFormatter xAxisFormatter = new CustomerValueFormatter();
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTfLight);//可以去掉，没什么用
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
//        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(tian));

        //自定义坐标轴适配器，配置在Y轴。leftAxis.setValueFormatter(custom);
        ValueFormatter custom = new CustomerValueFormatter();

        //设置限制临界线
        LimitLine limitLine = new LimitLine(20f, "舒适温度");
        limitLine.setLineColor(Color.BLACK);
        limitLine.setLineWidth(1f);
        limitLine.setTextColor(Color.RED);

        //获取到图形左边的Y轴
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.addLimitLine(limitLine);
        leftAxis.setTypeface(mTfLight);//可以去掉，没什么用
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f);

        //获取到图形右边的Y轴，并设置为不显示
        mChart.getAxisRight().setEnabled(false);

        //图例设置
        Legend legend = mChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setFormSize(9f);
        legend.setTextSize(11f);
        legend.setXEntrySpace(4f);

        setBarChartData();
    }

    /**
     * 初始化水平柱形图图控件属性
     */
    private void initHBarChart() {
        hBarChart.setOnChartValueSelectedListener(this);
        hBarChart.setDrawBarShadow(false);
        hBarChart.setDrawValueAboveBar(true);
        hBarChart.getDescription().setEnabled(false);

        hBarChart.setMaxVisibleValueCount(60);

        hBarChart.setPinchZoom(false);

        hBarChart.setDrawGridBackground(false);

        //自定义坐标轴适配器，设置在X轴
//        DecimalFormatter formatter = new DecimalFormatter();
        XAxis xl = hBarChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setTypeface(mTfLight);
        xl.setLabelRotationAngle(-45f);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);
        xl.setGranularity(1f);
//        xl.setAxisMinimum(0);
//        xl.setValueFormatter(formatter);
        xl.setValueFormatter(new IndexAxisValueFormatter(tian));
        //对Y轴进行设置
        YAxis yl = hBarChart.getAxisLeft();
        yl.setTypeface(mTfLight);
        yl.setDrawAxisLine(true);
        yl.setDrawGridLines(true);
        yl.setAxisMinimum(0f); // this replaces setStartAtZero(true)
//        yl.setInverted(true);

        hBarChart.getAxisRight().setEnabled(false);

        //图例设置
        Legend l = hBarChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setFormSize(8f);
        l.setXEntrySpace(4f);

        setHBarChartData();
        hBarChart.setFitBars(true);
        hBarChart.animateY(2500);
    }

    /**
     * 初始化折线图控件属性
     */
    private void initLineChart() {
        lineChart.setOnChartValueSelectedListener(this);
        lineChart.getDescription().setEnabled(false);
        lineChart.setBackgroundColor(Color.WHITE);

        //自定义适配器，适配于X轴
        ValueFormatter xAxisFormatter = new CustomerValueFormatter();

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTfLight);
        xAxis.setGranularity(1f);
//        xAxis.setValueFormatter(xAxisFormatter);

//准备好每个点对应的x轴数值

        xAxis.setValueFormatter(new IndexAxisValueFormatter(tian));//////////////////////////////////////////////////////////////////////


        //自定义适配器，适配于Y轴
        ValueFormatter custom = new CustomerValueFormatter();

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f);

        lineChart.getAxisRight().setEnabled(false);

        setLineChartData();
    }

//    private float getRandom(float range, float startsfrom) {
//        return (float) (Math.random() * range) + startsfrom;
//    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    private void setBarChartData() {
        BarDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(gaowen);//1
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            Date date = new Date();
            SimpleDateFormat ft = new SimpleDateFormat ("yyyy.MM.dd hh:mm:ss");
            set1 = new BarDataSet(gaowen, "数据采集时间:"+ft.format(date));
            set1.setDrawIcons(false);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setValueTypeface(mTfLight);//可以去掉，没什么用
            data.setBarWidth(0.9f);

            mChart.setData(data);
        }
    }

    /**
     * 设置水平柱形图数据的方法
     */
    private void setHBarChartData() {
        //填充数据，在这里换成自己的数据源
//        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
//
//        yVals1.add(new BarEntry(0, 5));
//        yVals1.add(new BarEntry(1, 4));
//        yVals1.add(new BarEntry(2, 3));
//        yVals1.add(new BarEntry(3, 2));
//        yVals1.add(new BarEntry(4,1));
        BarDataSet set1;

        if (hBarChart.getData() != null &&
                hBarChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) hBarChart.getData().getDataSetByIndex(0);
            set1.setValues(datas);//2
            hBarChart.getData().notifyDataChanged();
            hBarChart.notifyDataSetChanged();
        } else {
            Date date = new Date();
            SimpleDateFormat ft = new SimpleDateFormat ("yyyy.MM.dd hh:mm:ss");
            set1 = new BarDataSet(datas, "数据采集时间:"+ft.format(date));

            set1.setDrawIcons(false);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setValueTypeface(mTfLight);//可以去掉，没什么用
            data.setBarWidth(0.5f);

            hBarChart.setData(data);
        }
    }

    /**
     * 设置折线图的数据
     */
    private void setLineChartData() {
        //填充数据，在这里换成自己的数据源

//        List<Entry> valsComp2 = new ArrayList<>();
//
//        valsComp1.add(new Entry(0, 1));
//        valsComp1.add(new Entry(1, 2));
//        valsComp1.add(new Entry(2, 3));
//        valsComp1.add(new Entry(3, 4));

//        valsComp2.add(new Entry(0, 2));
//        valsComp2.add(new Entry(1, 0));
//        valsComp2.add(new Entry(2, 4));
//        valsComp2.add(new Entry(3, 2));


        LineDataSet setComp1 = new LineDataSet(zheqian, "最低温");
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp1.setColor(getResources().getColor(R.color.colorPrimary));
        setComp1.setDrawCircles(false);
        setComp1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

        LineDataSet setComp2 = new LineDataSet(zheqian1, "最高温");
        setComp2.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp2.setDrawCircles(false);
        setComp2.setColor(getResources().getColor(R.color.colorAccent));
        setComp2.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(setComp1);
        dataSets.add(setComp2);

        LineData lineData = new LineData(dataSets);

        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);
        context.startActivity(intent);
    }
//    public void initdata() {
//        //在这里设置自己的数据源,BarEntry 只接收float的参数，
//        //图形横纵坐标默认为float形式，如果想展示文字形式，需要自定义适配器，
//
//    }

    public void shuju(){

        OkHttpClient okHttpClient = new OkHttpClient();
        String url = "http://apis.juhe.cn/simpleWeather/query?city=成都&key=cd3fc169c0304cf7d7de9edaf62e050c";
        Request request = new Request.Builder().url(url).build();
        Call call =  okHttpClient.newCall(request);
        Response response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String json = null;
        try {
            json = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("aaaaaaaaa",json);
        //        JSON 解析
        pojo pojo = JSON.parseObject(json,pojo.class);
        String a = pojo.getResult().getFuture().get(0).getTemperature();
        a = a.substring(0,a.indexOf("/"));

        String b = pojo.getResult().getFuture().get(1).getTemperature();
        b = b.substring(0,b.indexOf("/"));

        String c = pojo.getResult().getFuture().get(2).getTemperature();
        c= c.substring(0,c.indexOf("/"));

        String d = pojo.getResult().getFuture().get(3).getTemperature();
        d = d.substring(0,d.indexOf("/"));

        String e = pojo.getResult().getFuture().get(4).getTemperature();
        e = e.substring(0,e.indexOf("/"));

//        valsComp1.add(new Entry(0, 1));
        zheqian.add(new Entry(0,Integer.parseInt(a)));
        zheqian.add(new Entry(1,Integer.parseInt(b)));
        zheqian.add(new Entry(2,Integer.parseInt(c)));
        zheqian.add(new Entry(3,Integer.parseInt(d)));
        zheqian.add(new Entry(4,Integer.parseInt(e)));

        String a1 = pojo.getResult().getFuture().get(0).getTemperature();
        a1 = a1.substring(a1.indexOf("/")+1);
        a1 = a1.replace("℃","");

        String b1 = pojo.getResult().getFuture().get(1).getTemperature();
        b1 = b1.substring(b1.indexOf("/")+1);
        b1 = b1.replace("℃","");

        String c1 = pojo.getResult().getFuture().get(2).getTemperature();
        c1 = c1.substring(c1.indexOf("/")+1);
        c1 = c1.replace("℃","");

        String d1 = pojo.getResult().getFuture().get(3).getTemperature();
        d1 = d1.substring(d1.indexOf("/")+1);
        d1 = d1.replace("℃","");

        String e1 = pojo.getResult().getFuture().get(4).getTemperature();
        e1 = e1.substring(e1.indexOf("/")+1);
        e1 = e1.replace("℃","");
        zheqian1.add(new Entry(0,Integer.parseInt(a1)));
        zheqian1.add(new Entry(1,Integer.parseInt(b1)));
        zheqian1.add(new Entry(2,Integer.parseInt(c1)));
        zheqian1.add(new Entry(3,Integer.parseInt(d1)));
        zheqian1.add(new Entry(4,Integer.parseInt(e1)));
        int num = pojo.getResult().getFuture().size();
        for(int i=0;i<num;i++){
            String tianshu = pojo.getResult().getFuture().get(i).getDate();
            tian.add(tianshu);
        }


        datas.add(new BarEntry(0, Integer.parseInt(a)));
        datas.add(new BarEntry(1, Integer.parseInt(b)));
        datas.add(new BarEntry(2, Integer.parseInt(c)));
        datas.add(new BarEntry(3, Integer.parseInt(d)));
        datas.add(new BarEntry(4, Integer.parseInt(e)));

        gaowen.add(new BarEntry(0, Integer.parseInt(a1)));
        gaowen.add(new BarEntry(1, Integer.parseInt(b1)));
        gaowen.add(new BarEntry(2, Integer.parseInt(c1)));
        gaowen.add(new BarEntry(3, Integer.parseInt(d1)));
        gaowen.add(new BarEntry(4, Integer.parseInt(e1)));
    }

}