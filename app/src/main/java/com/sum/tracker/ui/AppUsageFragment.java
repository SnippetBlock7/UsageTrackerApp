package com.sum.tracker.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import com.sum.tracker.R;
import com.sum.tracker.receiver.TimeLimitExceededReceiver;
import com.sum.tracker.service.TimeMonitorService;

import java.util.concurrent.TimeUnit;

public class AppUsageFragment extends Fragment {

    /*this class provides a user interface to display the application's name, usage time,
     and toggle switch for setting the time limit. It handles user interactions and manages
     preferences related to the time limit and usage of the application.*/
    private static final String PREFS_NAME = "AppUsageDetails";
    private static final String TOGGLE_STATE_KEY_PREFIX = "ToggleState_";
    private static final String TIME_LIMIT_KEY_PREFIX = "TimeLimit_";
    private static final String TIME_USAGE_KEY_PREFIX = "TimeUsage_";
    private TimeMonitorService timeMonitorService;
    private TimeLimitExceededReceiver timeLimitExceededReceiver;

    private TextView time;
    private TextView appName;
    private String packageName;
    private String name;
    private SwitchCompat toggle;
    private SharedPreferences sharedPreferences;
    private static final int PERMISSION_REQUEST_CODE = 1;


    public AppUsageFragment() {
        // Required empty public constructor
    }

    public static AppUsageFragment newInstance() {
        return new AppUsageFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_app_usage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appName = view.findViewById(R.id.AppName);
        time = view.findViewById(R.id.time);
        toggle = view.findViewById(R.id.toggle);

        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        assert getArguments() != null;
        name = getArguments().getString("name");
        appName.setText(name);

        assert getArguments() != null;
        packageName = getArguments().getString("packageName");

        timeMonitorService = new TimeMonitorService();

        long totalTimeInForeground = getArguments().getLong("totalTimeInForeground");
        updateUsageTime(totalTimeInForeground);

        boolean toggleState = getToggleState();
        toggle.setChecked(toggleState);

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveToggleState(isChecked);
                if (isChecked) {
                    showTimeLimitDialog();
                } else {
                    // Toggle button is unchecked (set to false)
                    // Perform any necessary actions here
                    // ...
                }
            }
        });
    }

    private boolean getToggleState() {
        String toggleStateKey = TOGGLE_STATE_KEY_PREFIX + name;
        return sharedPreferences.getBoolean(toggleStateKey, false);
    }

    private void saveToggleState(boolean state) {
        String toggleStateKey = TOGGLE_STATE_KEY_PREFIX + name;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(toggleStateKey, state);
        editor.apply();
    }

    private void showTimeLimitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Set Time Limit");

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_time_limit, null);
        builder.setView(dialogView);

        final EditText input = dialogView.findViewById(R.id.timeLimitEditText);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String timeLimitStr = input.getText().toString();
                if (!TextUtils.isEmpty(timeLimitStr)) {
                    int timeLimit = Integer.parseInt(timeLimitStr);
                    saveTimeLimit(timeLimit);

                    long timeLimitInMillis = TimeUnit.MINUTES.toMillis(timeLimit);
                    Intent serviceIntent = new Intent(requireContext(), TimeMonitorService.class);
                    serviceIntent.putExtra("name", name);
                    serviceIntent.putExtra("timeLimitInMillis", timeLimitInMillis);
                    ContextCompat.startForegroundService(requireContext(), serviceIntent);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                toggle.setChecked(false); // Uncheck the toggle button if the user cancels
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void saveTimeLimit(int timeLimit) {
        String timeLimitKey = TIME_LIMIT_KEY_PREFIX + name;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(timeLimitKey, timeLimit);
        editor.apply();
    }

    private void saveTimeUsage(long timeUsage) {
        String timeUsageKey = TIME_USAGE_KEY_PREFIX + name;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(timeUsageKey, timeUsage);
        editor.apply();
    }

    private long getTimeUsage() {
        String timeUsageKey = TIME_USAGE_KEY_PREFIX + name;
        return sharedPreferences.getLong(timeUsageKey, 0);
    }

    private void updateUsageTime(long totalTimeInForeground) {
        String usageTime = formatUsageTime(totalTimeInForeground);
        time.setText(usageTime);
        saveTimeUsage(totalTimeInForeground);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, perform the desired action
                // Permission granted, perform the desired action
                handlePermissionGranted();
            } else {
                // Permission denied, handle the scenario
                handlePermissionDenied();
            }
        }
    }


    private String formatUsageTime(long timeInMillis) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % 60;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis) % 60;
        long hours = TimeUnit.MILLISECONDS.toHours(timeInMillis) % 24;
        long days = TimeUnit.MILLISECONDS.toDays(timeInMillis);

        String formattedTime;
        if (days > 0) {
            formattedTime = String.format("%dd %02dh %02dm %02ds", days, hours, minutes, seconds);
        } else if (hours > 0) {
            formattedTime = String.format("%02dh %02dm %02ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            formattedTime = String.format("%02dm %02ds", minutes, seconds);
        } else {
            formattedTime = String.format("%02ds", seconds);
        }

        return formattedTime;
    }

    private void handlePermissionGranted() {
        // Show the notification here or perform any other desired action
        String name = getArguments().getString("name");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // For API level 26 and above, use notification channels
            timeLimitExceededReceiver.createNotificationChannel(requireContext());
            NotificationCompat.Builder builder = timeLimitExceededReceiver.createNotificationBuilder(requireContext(),name);
            TimeLimitExceededReceiver.showNotification(requireContext(), builder);
        } else {
            // For API level 23 and below, use the default notification builder
            NotificationCompat.Builder builder = timeLimitExceededReceiver.createLegacyNotificationBuilder(requireContext(),name);
            TimeLimitExceededReceiver.showNotification(requireContext(), builder);
        }
    }

    private void handlePermissionDenied() {
        // Handle the scenario when permission is denied
        Toast.makeText(getContext(), "Permission denied. Requesting permission again.", Toast.LENGTH_SHORT).show();

        // Request the permission again
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}, PERMISSION_REQUEST_CODE);
    }

}