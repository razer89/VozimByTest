package com.example.vozimbytest.fragment;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vozimbytest.R;
import com.example.vozimbytest.task.DrawRouteTask;
import com.example.vozimbytest.task.DrawRouteTask.DrawRouteListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class PathFragment extends Fragment {

	private SupportMapFragment mapFragment;
	private GoogleMap map;
	private LocationManager locationManager;
	private LatLng userLocation;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.path_fragment, null);
		
		mapFragment = SupportMapFragment.newInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.map_container_from, mapFragment).commit();
        map = mapFragment.getMap();
        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 10, 10, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 10, 10, locationListener);
        
        if (getArguments() != null) {
            final LatLng latLngFrom = (LatLng) getArguments().getParcelableArray(PathFragment.class.getSimpleName())[0];
            final LatLng latLngTo = (LatLng) getArguments().getParcelableArray(PathFragment.class.getSimpleName())[1];
            LatLng[] params = {latLngFrom, latLngTo};
            new DrawRouteTask(new DrawRouteListener() {
				
				@Override
				public void success(List<LatLng> result) {
					PolylineOptions line = new PolylineOptions();
					line.width(4).color(Color.BLACK);
					LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
					for (LatLng point : result) {
						line.add(point);
						latLngBuilder.include(point);
					}
					if (userLocation != null) {
						latLngBuilder.include(userLocation);
					}
					if (map == null) {
						map = mapFragment.getMap();
						if (map == null) {
							return;
						}
					}
					map.addMarker(new MarkerOptions().position(latLngFrom));
					map.addMarker(new MarkerOptions().position(latLngTo));
					map.addPolyline(line);
					int size = getResources().getDisplayMetrics().widthPixels;
					LatLngBounds latLngBounds = latLngBuilder.build();
					CameraUpdate track = CameraUpdateFactory.newLatLngBounds(latLngBounds, size, size, 25);
					map.moveCamera(track);
				}
				
				@Override
				public void error() {}
			}).execute(params);
        }
		return v;
	}
	
	private void setUserLocation(Location location) {
		if (map == null) {
			map = mapFragment.getMap();
	    }
	    if (map != null) {
	    	map.clear();
	    	map.addMarker(new MarkerOptions()
	    		.position(new LatLng(location.getLatitude(), location.getLongitude()))
		    	.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
	    }
	    userLocation = new LatLng(location.getLatitude(), location.getLongitude());
	}
	
	private LocationListener locationListener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
		
		@Override
		public void onProviderEnabled(String provider) {
			setUserLocation(locationManager.getLastKnownLocation(provider));			
		}
		
		@Override
		public void onProviderDisabled(String provider) {}
		
		@Override
		public void onLocationChanged(Location location) {
			setUserLocation(location);
		}
	};
}
