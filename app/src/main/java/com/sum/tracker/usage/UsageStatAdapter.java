package com.sum.tracker.usage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sum.tracker.R;
import com.sum.tracker.ui.AppUsageFragment;

import java.util.ArrayList;
import java.util.List;

public class UsageStatAdapter extends RecyclerView.Adapter<UsageStatVH> {

    private List<UsageStatsWrapper> usageStatsList;
    private String packageName;
    private AppUsageFragment appUsageFragment;

    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(UsageStatsWrapper usageStatsWrapper);
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public UsageStatAdapter(String packageName) {
        this.usageStatsList = new ArrayList<>();
        this.packageName = packageName;
    }

    @Override
    public UsageStatVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_list_item, parent, false);
        return new UsageStatVH(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UsageStatVH holder, int position) {
        UsageStatsWrapper usageStatsWrapper = usageStatsList.get(position);
        holder.bindTo(usageStatsWrapper);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usageStatsWrapper != null) {
                    itemClickListener.onItemClick(usageStatsWrapper);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return usageStatsList.size();
    }

    public void setUsageStatsList(List<UsageStatsWrapper> usageStatsList) {
        this.usageStatsList = usageStatsList;
        notifyDataSetChanged();
    }
}
