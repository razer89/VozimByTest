package com.example.vozimbytest.task;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import android.os.AsyncTask;

import com.example.vozimbytest.data.AdressData;
import com.google.android.gms.maps.model.LatLng;

public class AdressSearchTask extends AsyncTask<String, Void, ArrayList<AdressData>> {

	private static final String JSON_PREFIX = "http://maps.googleapis.com/maps/api/geocode/json?address=";
	private static final String JSON_END = "&sensor=false&language=ru&components=country:BY";

	private AddressSearchListener listener;

	public interface AddressSearchListener {

		public void onSuccess(ArrayList<AdressData> result);
		public void onError();
	}
	
	public AdressSearchTask(AddressSearchListener listener) {
		this.listener = listener;
	}

	@Override
	protected ArrayList<AdressData> doInBackground(String... params) {
		String query = JSON_PREFIX + params[0] + JSON_END;
		JSONObject parent;
		JSONArray items;
		try {
			parent = new JSONObject((Jsoup.connect(query)
							.userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
							.ignoreContentType(true).followRedirects(true)
							.get()).body().text());
			items = parent.getJSONArray("results");
			ArrayList<AdressData> adressData = new ArrayList<AdressData>();
			for (int i = 0; i < items.length(); i++) {
				if (items.getJSONObject(i) != null) {
					JSONObject item = items.getJSONObject(i);
					//if address have not route - return
					boolean haveRoute = false;
					JSONArray addressComponents = item.getJSONArray("address_components");
					for (int j = 0; j < addressComponents.length(); j++){
						JSONObject component = addressComponents.getJSONObject(j);
						JSONArray typeComponent = component.getJSONArray("types");
						for (int k = 0; k < typeComponent.length(); k++) {
							haveRoute = typeComponent.getString(k).equals("route");
							if (haveRoute) break;
						}
						if (haveRoute) break;
					}
					//
					if (haveRoute) {
						String name = item.getString("formatted_address");
						JSONObject geometry = item.getJSONObject("geometry");
						JSONObject location = geometry.getJSONObject("location");
						LatLng latLng = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
						adressData.add(new AdressData(name, latLng));
					}
				}
			}
			return adressData;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(ArrayList<AdressData> result) {
		super.onPostExecute(result);
		if (listener != null) {
			if (result != null) {
				listener.onSuccess(result);
			} else {
				listener.onError();
			}
		}
	}
}
