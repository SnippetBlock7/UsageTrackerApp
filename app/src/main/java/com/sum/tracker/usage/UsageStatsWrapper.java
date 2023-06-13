package com.sum.tracker.usage;

import android.app.usage.UsageStats;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

public final class UsageStatsWrapper implements Comparable<UsageStatsWrapper> {

    private final UsageStats usageStats;
    private final Drawable appIcon;
    private final String appName;

    private final String packageName;

    /* It provides getter methods to retrieve the usageStats, app icon, app name, and package name.*/

    public UsageStatsWrapper(UsageStats usageStats, Drawable appIcon, String appName,String packageName) {
        this.usageStats = usageStats;
        this.appIcon = appIcon;
        this.appName = appName;
        this.packageName = packageName;
    }

    public UsageStats getUsageStats() {
        return usageStats;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }
    @Override
    public int compareTo(@NonNull UsageStatsWrapper usageStatsWrapper) {
        if (usageStats == null && usageStatsWrapper.getUsageStats() != null) {
            return 1;
        } else if (usageStatsWrapper.getUsageStats() == null && usageStats != null) {
            return -1;
        } else if (usageStatsWrapper.getUsageStats() == null && usageStats == null) {
            return 0;
        } else {
            return Long.compare(usageStatsWrapper.getUsageStats().getLastTimeUsed(),
                    usageStats.getLastTimeUsed());
        }
    }
}
