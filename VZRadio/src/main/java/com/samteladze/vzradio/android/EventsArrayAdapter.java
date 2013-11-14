package com.samteladze.vzradio.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class EventsArrayAdapter extends ArrayAdapter<String> {

	private final Context context;
	private final String[] arrEventTitles;

	public EventsArrayAdapter(Context context, String[] values) {

		super(	context,
				R.layout.events_list_row,
				values);
		this.context = context;
		this.arrEventTitles = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater =
				(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.events_list_row,
										parent,
										false);
		
		TextView tvEventTitle = (TextView) rowView.findViewById(R.id.event_title);
		tvEventTitle.setText(arrEventTitles[position]);
		
		return rowView;
	}
}
