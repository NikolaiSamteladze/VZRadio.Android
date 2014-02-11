package com.samteladze.vzradio.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.samteladze.vzradio.android.domain.Event;

import java.util.List;

public class EventsArrayAdapter extends ArrayAdapter<Event> {

	private final Context context;
	private final List<Event> mEvents;

	public EventsArrayAdapter(Context context, List<Event> values) {
		super(context, R.layout.events_list_row, values);

		this.context = context;
		this.mEvents = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater =
				(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.events_list_row, parent, false);
		
		TextView eventTitle = (TextView) rowView.findViewById(R.id.event_title);
		eventTitle.setText(mEvents.get(position).title);
		
		return rowView;
	}
}
