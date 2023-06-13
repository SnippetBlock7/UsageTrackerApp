package com.sum.tracker.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.sum.tracker.R;

public class MainActivity extends AppCompatActivity {

    /* the MainActivity class serves as the entry point of the application and sets up
    the initial fragment, which is the AppListFragment, to be displayed in the activity's layout.*/
      @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AppListFragment fragment = new AppListFragment();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();

    }
}