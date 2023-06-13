package com.sum.tracker.service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.sum.tracker.R;
import com.sum.tracker.receiver.TimeLimitExceededReceiver;

import java.util.List;

public class TimeMonitorService extends Service {

    private static final long INTERVAL = 60 * 1000; // Check every minute
    private static final String PREFS_NAME = "AppUsageDetails";
    private static final String TIME_LIMIT_KEY_PREFIX = "TimeLimit_";
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "channel_id";
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;
    private UsageStatsManager usageStatsManager;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the alarm manager
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Initialize the shared preferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Create the alarm intent
        Intent intent = new Intent(this, TimeLimitExceededReceiver.class);
        int flags = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            flags = PendingIntent.FLAG_MUTABLE;
        }
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, flags);

        // Initialize the UsageStatsManager
        usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Get the name from the intent
        String name = intent.getStringExtra("name");
        long timeLimitInMillis = intent.getLongExtra("timeLimitInMillis", 0);

        long currentTime = System.currentTimeMillis();
        long triggerTime = currentTime + timeLimitInMillis;

//        // Check if the application is currently in the foreground
//        if (!isApplicationInForeground(name)) {
//            // Application is not in the foreground, don't schedule the alarm
//            stopSelf(); // Stop the service
//            return START_NOT_STICKY;
//        }

        // Schedule the alarm to check for time limit exceeded

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                INTERVAL,
                alarmIntent
        );

        // Start the service in the foreground
        startForeground(NOTIFICATION_ID, createNotification(name, timeLimitInMillis));

        return START_STICKY;
    }

//    private boolean isApplicationInForeground(String packageName) {
//        // Check if the application with the given package name is currently in the foreground
//        long currentTime = System.currentTimeMillis();
//        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(
//                UsageStatsManager.INTERVAL_DAILY, currentTime - 1000 * 10, currentTime);
//        if (usageStatsList != null) {
//            for (UsageStats usageStats : usageStatsList) {
//                if (usageStats.getPackageName().equals(packageName)
//                        && usageStats.getLastTimeUsed() >= currentTime - 1000 * 10) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cancel the alarm when the service is destroyed
        alarmManager.cancel(alarmIntent);
    }

    private Notification createNotification(String appName, long timeLimit) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Time Monitor Service")
                .setContentText(appName + " - Time Limit: " + timeLimit + "ms")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        return builder.build();
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        String channelName = "Time Monitor Service";
        String channelDescription = "Channel for time monitor service notifications";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
        channel.setDescription(channelDescription);
        notificationManager.createNotificationChannel(channel);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
