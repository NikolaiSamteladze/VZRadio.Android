package com.samteladze.vzradio.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MyLibraryFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View myLibraryView = inflater.inflate(R.layout.fragment_my_library, container, false);
		return myLibraryView;
	}
}