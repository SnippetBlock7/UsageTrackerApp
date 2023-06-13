package com.sum.tracker.util;

import com.sum.tracker.usage.UsageStatsWrapper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DateFile {

    public static String format(UsageStatsWrapper usageStatsWrapper){

        DateFormat format = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
        return format.format(usageStatsWrapper.getUsageStats().getLastTimeUsed());
    }
}
