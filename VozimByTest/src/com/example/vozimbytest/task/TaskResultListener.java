package com.example.vozimbytest.task;

import java.util.ArrayList;

import com.example.vozimbytest.data.AdressData;

public interface TaskResultListener {

	public void onSuccess(ArrayList<AdressData> result);
	public void onError();
}
