package com.au.berthaau.ChartAxisValueFormatters;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateValueDayFormatter implements IAxisValueFormatter {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("E 'd.' d-MMM", Locale.getDefault());

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        long millisToAddUtc = 7200000;
        return dateFormat.format(new Date((long) value + millisToAddUtc));
    }
}