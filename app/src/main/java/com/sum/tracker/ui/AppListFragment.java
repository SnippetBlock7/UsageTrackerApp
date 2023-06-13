package com.sum.tracker.ui;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sum.tracker.R;
import com.sum.tracker.usage.UsageContract;
import com.sum.tracker.usage.UsagePresenter;
import com.sum.tracker.usage.UsageStatAdapter;
import com.sum.tracker.usage.UsageStatsWrapper;

import java.util.List;

public class AppListFragment extends Fragment implements UsageContract.View, UsageStatAdapter.OnItemClickListener {

    private ProgressBar progressBar;
    private TextView permissionMessage;
    private UsageContract.Presenter presenter;
    private UsageStatAdapter adapter;

    public AppListFragment() {
        // Required empty public constructor
    }

    public static AppListFragment newInstance() {

        return new AppListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_app_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        progressBar = view.findViewById(R.id.progress_bar);
        permissionMessage = view.findViewById(R.id.grant_permission_message);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        String packageName = "com.example.package";

        adapter = new UsageStatAdapter(packageName);
        recyclerView.setAdapter(adapter);

        permissionMessage.setOnClickListener(v -> openSettings());

        presenter = new UsagePresenter(requireActivity(), this);

        adapter.setItemClickListener(this);
    }

    private void openSettings() {
        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
    }

    @Override
    public void onResume() {
        super.onResume();
        showProgressBar(true);
        presenter.retrieveUsageStats();
    }

    @Override
    public void onUsageStatsRetrieved(List<UsageStatsWrapper> list) {
        showProgressBar(false);
        permissionMessage.setVisibility(GONE);
        adapter.setUsageStatsList(list);
    }

    @Override
    public void onUserHasNoPermission() {
        showProgressBar(false);
        permissionMessage.setVisibility(VISIBLE);
    }

    private void showProgressBar(boolean show) {
        if (show) {
            progressBar.setVisibility(VISIBLE);
        } else {
            progressBar.setVisibility(GONE);
        }
    }

    @Override
    public void onItemClick(UsageStatsWrapper usageStatsWrapper) {
        // Create a new instance of AppUsageFragment and pass the necessary data
        AppUsageFragment appUsageFragment = AppUsageFragment.newInstance();
        Bundle args = new Bundle();
        args.putString("name", usageStatsWrapper.getAppName());
        args.putString("packageName", usageStatsWrapper.getPackageName());
        args.putLong("totalTimeInForeground", usageStatsWrapper.getUsageStats().getTotalTimeInForeground());
        appUsageFragment.setArguments(args);

        // Replace the current fragment with the AppUsageFragment
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frameLayout, appUsageFragment)
                .addToBackStack(null)
                .commit();
    }
}