package com.example.vozimbytest.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.Toast;

import com.example.vozimbytest.R;
import com.example.vozimbytest.Utils;
import com.example.vozimbytest.activity.MainActivity;
import com.example.vozimbytest.data.AdressData;
import com.example.vozimbytest.task.AdressSearchTask;
import com.example.vozimbytest.task.AdressSearchTask.AddressSearchListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class RouteFragment extends Fragment {
	
	private GoogleMap mapFrom, mapTo;
	private AutoCompleteTextView teViewFrom, teViewTo;
	private LocationManager locationManager;
	private SupportMapFragment mapFragmentFrom, mapFragmentTo;
	private TabHost tabHost;
	private Location locationFrom, locationTo;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.route_fragment, null);
		initMaps();
		initTextFields(v);
		
		Button computeButton = (Button)v.findViewById(R.id.compute_button);
		computeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				computePath();
			}
		});
		
		tabHost = (TabHost) v.findViewById(android.R.id.tabhost);
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
	
	private void initMaps() {
		mapFragmentFrom = SupportMapFragment.newInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.map_container_from, mapFragmentFrom).commit();
        mapFrom = mapFragmentFrom.getMap();
        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 10, 10, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 10, 10, locationListener);
        
        mapFragmentTo = SupportMapFragment.newInstance();
        transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.map_container_to, mapFragmentTo).commit();
        mapTo = mapFragmentTo.getMap();
	}
	
	private void initTextFields(View v) {
		teViewFrom = (AutoCompleteTextView) v.findViewById(R.id.search_text_from);
		teViewFrom.setThreshold(1);
		teViewFrom.addTextChangedListener(new CustomTextWatcher(true));
		
		teViewTo = (AutoCompleteTextView) v.findViewById(R.id.search_text_to);
		teViewTo.setThreshold(1);
		teViewTo.addTextChangedListener(new CustomTextWatcher(false));
	}
	
	private void computePath() {
		if (locationFrom == null || locationTo == null) {
			Toast.makeText(getActivity(), getString(R.string.no_locations), Toast.LENGTH_SHORT).show();
		}
		Bundle args = new Bundle();
		Parcelable[] coords = {locationFrom, locationTo};
		args.putParcelableArray(PathFragment.class.getSimpleName(), coords);
		((MainActivity)getActivity()).openPathFragment(args);
	}
	
	private void showLocation(LatLng latLng, boolean from) {
		Location targetLocation = new Location("");
	    targetLocation.setLatitude(latLng.latitude);
	    targetLocation.setLongitude(latLng.longitude);
	    showLocation(targetLocation, from);
	}
	
	private void showLocation(Location location, boolean from) {
	    if (location == null) return;
	    CameraPosition cameraPosition = new CameraPosition.Builder()
	    .target(new LatLng(location.getLatitude(), location.getLongitude()))
	    .zoom(18)
	    .build();
	    CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);
	    if (from) {
		    if (mapFrom == null) {
		    	mapFrom = mapFragmentFrom.getMap();
		    }
		    if (mapFrom != null) {
			    mapFrom.animateCamera(update);
			    mapFrom.clear();
			    mapFrom.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),
			    		location.getLongitude())));
		    	locationFrom = location;
		    }
	    } else {
	    	if (mapTo == null) {
	    		mapTo = mapFragmentTo.getMap();
		    }
		    if (mapTo != null) {
		    	mapTo.animateCamera(update);
		    	mapTo.clear();
		    	mapTo.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),
			    		location.getLongitude())));
		    	locationTo = location;
		    }
	    }
	}
	
	private LocationListener locationListener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
		
		@Override
		public void onProviderEnabled(String provider) {
			showLocation(locationManager.getLastKnownLocation(provider), true);			
		}
		
		@Override
		public void onProviderDisabled(String provider) {}
		
		@Override
		public void onLocationChanged(Location location) {
			showLocation(location, true);
		}
	};
	
	private class CustomTextWatcher implements TextWatcher {
		
		private boolean from;
		private AutoCompleteTextView currentView;
		
		public CustomTextWatcher(boolean from) {
			this.from = from;
			currentView = from ? teViewFrom : teViewTo;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (count > 1) return;
			new AdressSearchTask(new AddressSearchListener() {
				
				@Override
				public void onSuccess(final ArrayList<AdressData> result) {
					String[] hints = new String[result.size()];
					for (int i = 0; i < result.size(); i++) {
						hints[i] = result.get(i).getName();
					}
					currentView.setAdapter(new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, hints));
					currentView.showDropDown();
					currentView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							showLocation(result.get(position).getLatLng(), from);
							Utils.hideKeyboard(getActivity().getCurrentFocus());
						}
					});
				}
				
				@Override
				public void onError() {}
			}).execute(currentView.getText().toString());
		}

		@Override
		public void afterTextChanged(Editable s) {}
	}
}
