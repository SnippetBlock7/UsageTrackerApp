package com.sum.tracker.usage;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.sum.tracker.R;

public class UsageStatVH extends RecyclerView.ViewHolder {
    /* viewholder class of recyclerview */
    private ImageView appIcon;
    private TextView appName;
    private UsageStatsWrapper usageStatsWrapper;

    public UsageStatVH(View itemView) {
        super(itemView);

        appIcon = itemView.findViewById(R.id.icon);
        appName = itemView.findViewById(R.id.title);

    }

    public void bindTo(UsageStatsWrapper usageStatsWrapper) {
        this.usageStatsWrapper = usageStatsWrapper;
        appIcon.setImageDrawable(usageStatsWrapper.getAppIcon());
        appName.setText(usageStatsWrapper.getAppName());
    }
}
