package com.samteladze.vzradio.android;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mLog = LogManager.getLog(EventsFragment.class.getSimpleName());

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

//        return inflater.inflate(R.layout.fragment_events, container, false);
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

		Toast.makeText(getActivity().getApplicationContext(), "Hey! Stop it!",
						Toast.LENGTH_SHORT).show();
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
                mLog.debug("Received events information:\n%s", eventsAsJson);
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
