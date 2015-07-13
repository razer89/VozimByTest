package com.example.vozimbytest.task;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

public class LocationToAdressTask extends AsyncTask<LatLng, Void, String> {

	private static final String JSON_PREFIX = "http://maps.googleapis.com/maps/api/geocode/json?latlng=";
	private static final String JSON_POSTFIX = "&sensor=false&language=ru";
	
	private LocationToAddressListener listener;
	
	public interface LocationToAddressListener {
		
		public void success(String result);
		public void error();
	}
	
	public LocationToAdressTask(LocationToAddressListener listener) {
		this.listener = listener;
	}
	
	@Override
	protected String doInBackground(LatLng... params) {
		String query = JSON_PREFIX + params[0].latitude + "," + params[0].longitude + JSON_POSTFIX;
		try {
			JSONObject parent = new JSONObject((Jsoup.connect(query)
					.userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
					.ignoreContentType(true).followRedirects(true)
					.get()).body().text());
			JSONArray results = parent.getJSONArray("results");
			for (int i = 0; i < results.length(); i++) {
				JSONArray types = results.getJSONObject(i).getJSONArray("types");
				for (int j = 0; j < types.length(); j++) {
					String type = types.getString(j);
					if (type.equals("street_address")) {
						return results.getJSONObject(i).getString("formatted_address");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		if (listener != null) {
			if (result != null) {
				listener.success(result);
			} else {
				listener.error();
			}
		}
		super.onPostExecute(result);
	}
}
