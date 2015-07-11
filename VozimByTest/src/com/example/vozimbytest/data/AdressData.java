package com.example.vozimbytest.data;

import com.google.android.gms.maps.model.LatLng;

public class AdressData {

	private String name;
	private LatLng latLng;
	
	public AdressData(String name, LatLng latLng) {
		this.name = name;
		this.latLng = latLng;
	}
	
	public String getName() {
		return name;
	}
	
	public LatLng getLatLng() {
		return latLng;
	}
}
