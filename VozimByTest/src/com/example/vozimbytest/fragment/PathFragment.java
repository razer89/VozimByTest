package com.example.vozimbytest.fragment;

import java.util.ArrayList;

import org.w3c.dom.Document;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vozimbytest.GMapV2Direction;
import com.example.vozimbytest.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class PathFragment extends Fragment {

	private SupportMapFragment mapFragment;
	private GoogleMap map;
	private LocationManager locationManager;

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
            new AsyncTask<Void, Void, PolylineOptions>() {

				@Override
				protected PolylineOptions doInBackground(Void... params) {
					GMapV2Direction md = new GMapV2Direction();
		            Location locationFrom, locationTo;
		            locationFrom = (Location) getArguments().getParcelableArray(PathFragment.class.getSimpleName())[0];
		            locationTo = (Location) getArguments().getParcelableArray(PathFragment.class.getSimpleName())[1];
		            LatLng latLngFrom = new LatLng(locationFrom.getLatitude(), locationTo.getLongitude());
		            LatLng latLngTo = new LatLng(locationTo.getLatitude(), locationTo.getLongitude());
					Document doc = md.getDocument(latLngFrom, latLngTo, GMapV2Direction.MODE_DRIVING);
		            ArrayList<LatLng> directionPoint = md.getDirection(doc);
		            PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.BLACK);
		            for (int i = 0; i < directionPoint.size(); i++) {
		                rectLine.add(directionPoint.get(i));
		            }
		            return rectLine;
				}
				
				protected void onPostExecute(PolylineOptions result) {
					if (map == null) {
		            	map = mapFragment.getMap();
		            }
		            if (map != null) {
			            map.addPolyline(result);
		            }
				};
			}.execute();
        }
		return v;
	}
	
	private void setUserLocation(Location location) {
		if (map == null) {
			map = mapFragment.getMap();
	    }
	    if (map != null) {
	    	map.addMarker(new MarkerOptions()
	    		.position(new LatLng(location.getLatitude(), location.getLongitude()))
		    	.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
	    }
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
