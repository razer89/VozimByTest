package com.example.vozimbytest;

import android.content.Context;
import android.location.Location;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.maps.model.LatLng;

public final class Utils {

	public static void hideKeyboard(View v) {
		Context context = v.getContext();
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}
	
	public static Location convertLatLngToLocation(LatLng latLng) {
		Location targetLocation = new Location("");
	    targetLocation.setLatitude(latLng.latitude);
	    targetLocation.setLongitude(latLng.longitude);
	    return targetLocation;
	}
}
