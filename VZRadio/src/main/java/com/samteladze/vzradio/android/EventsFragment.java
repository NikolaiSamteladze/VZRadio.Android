package com.samteladze.vzradio.android;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.samteladze.vzradio.android.common.ILog;
import com.samteladze.vzradio.android.common.LogManager;
import com.samteladze.vzradio.android.domain.Event;

import java.util.ArrayList;
import java.util.List;

public class EventsFragment extends ListFragment {
    private ILog mLog;

    public EventsFragment() {
        mLog = LogManager.getLog(EventsFragment.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View superView = super.onCreateView(inflater, container, savedInstanceState);
        ListView superListView = (ListView) superView.findViewById(android.R.id.list);
        ViewGroup superViewGroup = (ViewGroup) superListView.getParent();

        // Remove ListView and add CustomView  in its place
        int superListViewIndex = superViewGroup.indexOfChild(superListView);
        superViewGroup.removeViewAt(superListViewIndex);
        RelativeLayout mLinearLayout = (RelativeLayout) inflater.inflate(
                R.layout.fragment_events, container, false);
        superViewGroup.addView(mLinearLayout, superListViewIndex, superListView.getLayoutParams());
        return superView;
    }

    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        UpdateEventsListAsyncTask updateEventsListTask = new UpdateEventsListAsyncTask();
        updateEventsListTask.execute();

        setListShown(false);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
        Intent eventDetailsActivityIntent = new Intent(getActivity(), EventsDetailsActivity.class);
        Event selectedEvent = (Event) getListAdapter().getItem(position);
        eventDetailsActivityIntent.putExtra("title", selectedEvent.title);
        eventDetailsActivityIntent.putExtra("venue", selectedEvent.venue);
        eventDetailsActivityIntent.putExtra("startTime", selectedEvent.startTime);
        eventDetailsActivityIntent.putExtra("description", selectedEvent.description);
        eventDetailsActivityIntent.putExtra("imageUri",
            (selectedEvent.imagesResources != null) && (selectedEvent.imagesResources.size() > 0) ?
            selectedEvent.imagesResources.get(0).mediumThumbUri :
            null
        );
        getActivity().startActivity(eventDetailsActivityIntent);
	}

    private void updateEventsList(List<Event> updatedEvents) {
		ArrayAdapter<Event> arrayAdapter = new EventsArrayAdapter(getActivity(), updatedEvents);
		setListAdapter(arrayAdapter);
        setListShown(true);
    }

    private class UpdateEventsListAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                VzRadioDataProvider dataAdapter = new VzRadioDataProvider();
                String eventsAsJson = dataAdapter.getEventsAsJson();
                return eventsAsJson;
            } catch (Exception e) {
                mLog.error(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String json) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                ArrayList<Event> events = mapper.readValue(json, new TypeReference<ArrayList<Event>>() { });
                updateEventsList(events);
            } catch (Exception e) {
                mLog.error(e.getMessage());
            }
        }
    }
}
