package com.example.vozimbytest.fragment;

import java.util.Timer;
import java.util.TimerTask;

import com.example.vozimbytest.R;
import com.example.vozimbytest.activity.MainActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SplashFragment extends Fragment {

	private static final int SPLASH_DELAY = 2000;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.splash_fragment, null);
		return v;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				((MainActivity)getActivity()).removeSplash();
			}
		};
		Timer timer = new Timer();
		timer.schedule(task, SPLASH_DELAY);
	}
}
