package com.example.vozimbytest.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.example.vozimbytest.R;
import com.example.vozimbytest.fragment.RouteFragment;
import com.example.vozimbytest.fragment.SplashFragment;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new SplashFragment()).commit();
    }
    
    public void removeSplash() {
    	FragmentManager fragmentManager = getSupportFragmentManager();
    	fragmentManager.beginTransaction().replace(R.id.content_frame, new RouteFragment()).commit();
    }
}