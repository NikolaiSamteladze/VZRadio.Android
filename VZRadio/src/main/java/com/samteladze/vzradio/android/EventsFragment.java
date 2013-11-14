package com.samteladze.vzradio.android;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class EventsFragment extends ListFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		String[] arrEventTitles =
				new String[] { "Item 1", "Item 2", "Item 3", "Item 4",
								"Item 5", "Item 6", "Item 7", "Item 8",
								"Item 9", "Item 10", "Item 11", "Item 12",
								"Item 13", "Item 14", "Item 15", };

		ArrayAdapter<String> arrayAdapter =
				new EventsArrayAdapter(	getActivity(),
										arrEventTitles);
		setListAdapter(arrayAdapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		Toast.makeText(	getActivity().getApplicationContext(),
						String.format(	"Blah at %d",
										position),
						Toast.LENGTH_SHORT)
				.show();

	}
}
