package com.example.vozimbytest.fragment;

import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.path_fragment, null);
		setHasOptionsMenu(true);
		final TextView statusText = (TextView)v.findViewById(R.id.route_status);
		
		mapFragment = SupportMapFragment.newInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.map_container_from, mapFragment).commit();
        map = mapFragment.getMap();
        
        if (getArguments() != null) {
            final LatLng latLngFrom = (LatLng) getArguments().getParcelableArray(PathFragment.class.getSimpleName())[0];
            final LatLng latLngTo = (LatLng) getArguments().getParcelableArray(PathFragment.class.getSimpleName())[1];
            final LatLng userLocation = (LatLng)getArguments().getParcelableArray(PathFragment.class.getSimpleName())[2];
            LatLng[] params = {latLngFrom, latLngTo};
            new DrawRouteTask(new DrawRouteListener() {
				
				@Override
				public void success(List<LatLng> result, boolean status) {
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
					map.getUiSettings().setRotateGesturesEnabled(false);
					map.addMarker(new MarkerOptions().position(latLngFrom));
					map.addMarker(new MarkerOptions().position(latLngTo));
					if (userLocation != null) {
						map.addMarker(new MarkerOptions()
			    		.position(userLocation)
				    	.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
					}
					map.addPolyline(line);
					int size = getResources().getDisplayMetrics().widthPixels;
					LatLngBounds latLngBounds = latLngBuilder.build();
					CameraUpdate track = CameraUpdateFactory.newLatLngBounds(latLngBounds, size, size, 25);
					map.moveCamera(track);
					statusText.setText(status ? getString(R.string.found) : getString(R.string.unknown));
				}
				
				@Override
				public void error() {}
			}).execute(params);
        }
		return v;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			getActivity().onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
