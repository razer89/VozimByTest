package com.example.vozimbytest;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public final class Utils {

	public static void hideKeyboard(View v) {
		Context context = v.getContext();
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}
}
