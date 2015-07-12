package com.example.vozimbytest.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TabHost;

import com.example.vozimbytest.R;
import com.example.vozimbytest.data.AdressData;
import com.example.vozimbytest.task.AdressSearchTask;
import com.example.vozimbytest.task.TaskResultListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class RouteFragment extends Fragment {
	
	private GoogleMap map;
	private AutoCompleteTextView teView;
	private LocationManager locationManager;
	private SupportMapFragment mapFragment;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.route_fragment, null);
		mapFragment = SupportMapFragment.newInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.map_container, mapFragment).commit();
        map = mapFragment.getMap();
        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 10, 10, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 10, 10, locationListener);
		teView = (AutoCompleteTextView) v.findViewById(R.id.search_text);
		teView.setThreshold(1);
		teView.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (count > 1) return;
				android.util.Log.d("logd", "onTextChanged()");
				new AdressSearchTask(new TaskResultListener() {
					
					@Override
					public void onSuccess(final ArrayList<AdressData> result) {
						String[] hints = new String[result.size()];
						for (int i = 0; i < result.size(); i++) {
							hints[i] = result.get(i).getName();
						}
						teView.setAdapter(new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, hints));
						teView.showDropDown();
						teView.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								showLocation(result.get(position).getLatLng());
							}
						});
					}
					
					@Override
					public void onError() {}
				}).execute(teView.getText().toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		TabHost tabHost = (TabHost) v.findViewById(android.R.id.tabhost);
        tabHost.setup();
        TabHost.TabSpec tabSpec;
        tabSpec = tabHost.newTabSpec(getString(R.string.from));
        tabSpec.setIndicator(getString(R.string.from));
        tabSpec.setContent(R.id.tab1);
        tabHost.addTab(tabSpec);
        tabSpec = tabHost.newTabSpec(getString(R.string.to));
        tabSpec.setIndicator(getString(R.string.to));
        tabSpec.setContent(R.id.tab2);
        tabHost.addTab(tabSpec);
		return v;
	}
	
	private void showLocation(LatLng latLng) {
		Location targetLocation = new Location("");
	    targetLocation.setLatitude(latLng.latitude);
	    targetLocation.setLongitude(latLng.longitude);
	    showLocation(targetLocation);
	}
	
	private void showLocation(Location location) {
	    if (location == null) return;
	    CameraPosition cameraPosition = new CameraPosition.Builder()
	    .target(new LatLng(location.getLatitude(), location.getLongitude()))
	    .zoom(18)
	    .build();
	    CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);
	    if (map == null) {
	    	map = mapFragment.getMap();
	    }
	    if (map != null) {
		    map.animateCamera(update);
	    }
	  }

	
	private LocationListener locationListener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
		
		@Override
		public void onProviderEnabled(String provider) {
			showLocation(locationManager.getLastKnownLocation(provider));			
		}
		
		@Override
		public void onProviderDisabled(String provider) {}
		
		@Override
		public void onLocationChanged(Location location) {
			showLocation(location);
		}
	};
}
