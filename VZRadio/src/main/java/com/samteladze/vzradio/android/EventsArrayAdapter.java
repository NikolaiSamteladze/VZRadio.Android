package com.samteladze.vzradio.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.samteladze.vzradio.android.common.ILog;
import com.samteladze.vzradio.android.common.LogManager;
import com.samteladze.vzradio.android.domain.Event;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class EventsArrayAdapter extends ArrayAdapter<Event> {
    private final ILog mLog;
	private final Context mContext;
	private final List<Event> mEvents;
    private final ImageLoader mImageLoader;

	public EventsArrayAdapter(Context context, List<Event> values) {
		super(context, R.layout.events_list_row, values);

        this.mLog = LogManager.getLog(EventsArrayAdapter.class);
		this.mContext = context;
		this.mEvents = values;

        ImageLoaderConfiguration config =
                new ImageLoaderConfiguration.Builder(context.getApplicationContext()).build();
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(config);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater =
				(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Event event = mEvents.get(position);

		View rowView = inflater.inflate(R.layout.events_list_row, parent, false);

		TextView eventTitle = (TextView) rowView.findViewById(R.id.event_title);
		eventTitle.setText(event.title);

        TextView eventVenue = (TextView) rowView.findViewById(R.id.event_venue);
        eventVenue.setText(event.venue);

        TextView eventTime = (TextView) rowView.findViewById(R.id.event_time);
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);

        eventTime.setText(dateFormat.format(new Date(1000 * Long.parseLong(event.startTime))));

        ImageView imageView = (ImageView) rowView.findViewById(R.id.event_image);
        if ((event.imagesResources != null) && !event.imagesResources.isEmpty()) {
            mImageLoader.displayImage(event.imagesResources.get(0).smallThumbUri, imageView);
        } else {
            imageView.setImageResource(R.drawable.no_event_image);
        }
		
		return rowView;
	}
}
