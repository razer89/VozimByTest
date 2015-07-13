package com.example.vozimbytest.task;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import android.os.AsyncTask;

import com.example.vozimbytest.polyutil.PolyUtil;
import com.example.vozimbytest.task.DrawRouteTask.ResponseStructure;
import com.google.android.gms.maps.model.LatLng;

public class DrawRouteTask extends AsyncTask<LatLng, Void, ResponseStructure> {

	private static final String JSON_PREFIX = "http://maps.googleapis.com/maps/api/directions/json?origin=";
	private static final String JSON_POSTFIX = "&sensor=false&units=metric&mode=driving";
	
	private DrawRouteListener listener;
	
	public interface DrawRouteListener {
		
		public void success(List<LatLng> result, boolean status);
		public void error();
	}
	
	public DrawRouteTask(DrawRouteListener listener) {
		this.listener = listener;
	}
	
	protected class ResponseStructure {
		public List<LatLng> result;
		public boolean status;
	}
	
	@Override
	protected ResponseStructure doInBackground(LatLng... params) {
		String query = JSON_PREFIX + params[0].latitude + "," + params[0].longitude
				+ "&destination=" + params[1].latitude + "," + params[1].longitude
				+ JSON_POSTFIX;
		try {
			JSONObject parent = new JSONObject((Jsoup.connect(query)
					.userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
					.ignoreContentType(true).followRedirects(true)
					.get()).body().text());
			JSONArray routes = parent.getJSONArray("routes");
			JSONObject route = routes.getJSONObject(0);
			JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
			String polyline = overviewPolyline.getString("points");
			ResponseStructure response = new ResponseStructure();
			response.result = PolyUtil.decode(polyline);
			String status = parent.getString("status");
			response.status = status.equals("OK");
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(ResponseStructure response) {
		if (listener != null) {
			if (response.result != null) {
				listener.success(response.result, response.status);
			} else {
				listener.error();
			}
		}
		super.onPostExecute(response);
	}
}
