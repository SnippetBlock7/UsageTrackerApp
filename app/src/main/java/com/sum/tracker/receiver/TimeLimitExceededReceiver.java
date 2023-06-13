package com.sum.tracker.receiver;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.sum.tracker.R;

public class TimeLimitExceededReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 1;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String CHANNEL_ID = "channel_id";

    /*onReceive() method, retrieves the name of the application from the received intent.
    It checks the API level to determine which notification builder to use.
    If the API level is 26 or above, it calls the createNotificationChannel() method to
    create a notification channel for the notification.
    It then calls the appropriate createNotificationBuilder() method based on the API level to
    create the notification builder object.*/
    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the name of the application from the intent
        String name = intent.getStringExtra("name");

        // Check the API level to determine the notification builder to use
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // For API level 26 and above, use notification channels
            createNotificationChannel(context);
            NotificationCompat.Builder builder = createNotificationBuilder(context, name);
            showNotification(context, builder);
        } else {
            // For API level 23 and below, use the default notification builder
            NotificationCompat.Builder builder = createLegacyNotificationBuilder(context, name);
            showNotification(context, builder);
        }
    }

    public static void createNotificationChannel(Context context) {
        // Create a notification channel for API level 26 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            String channelName = "Time Limit Exceeded";
            String channelDescription = "Channel for time limit exceeded notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            channel.setDescription(channelDescription);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public NotificationCompat.Builder createNotificationBuilder(Context context, String name) {
        // Create a notification builder for API level 26 and above
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Time Limit Exceeded")
                .setContentText("ALERT!! Time limit exceeded On this app.")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Customize the builder further if needed
        // ...

        return builder;
    }

    public NotificationCompat.Builder createLegacyNotificationBuilder(Context context, String name) {
        // Create a notification builder for API level 23 and below
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle("Time Limit Exceeded")
                .setContentText("ALERT!! Time limit exceeded On this app.")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Customize the builder further if needed
        // ...

        return builder;
    }

    //method to display the notification using the created builder.
    public static void showNotification(Context context, NotificationCompat.Builder builder) {
        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // Check if the notification channel exists and create it if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            createNotificationChannel(context);
        }

        try {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } catch (SecurityException e) {
            // Handle the SecurityException here
            // You can log the error, show a toast, or take any other appropriate action
            Log.e("Receiver","Error with permissions");
            e.printStackTrace();
        }
    }

}


