package com.example.vozimbytest.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.vozimbytest.R;
import com.example.vozimbytest.fragment.RouteFragment;
import com.example.vozimbytest.fragment.SplashFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, new SplashFragment()).commit();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
    
    public void removeSplash() {
    	FragmentManager fragmentManager = getSupportFragmentManager();
    	fragmentManager.beginTransaction().replace(R.id.content_frame, new RouteFragment()).commit();
    }
}