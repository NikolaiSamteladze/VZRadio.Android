package com.samteladze.vzradio.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.samteladze.vzradio.android.common.ILog;
import com.samteladze.vzradio.android.common.LogManager;
import com.samteladze.vzradio.android.domain.TopChartEntry;

import java.util.List;

public class TopChartArrayAdapter extends ArrayAdapter<TopChartEntry> {
    private final ILog mLog;
    private final List<TopChartEntry> mTopChartEntries;

    public TopChartArrayAdapter(Context context, List<TopChartEntry> values) {
        super(context, R.layout.events_list_row, values);

        this.mLog = LogManager.getLog(EventsArrayAdapter.class);
        this.mTopChartEntries = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TopChartEntry topChartEntry = mTopChartEntries.get(position);

        View rowView = inflater.inflate(R.layout.top_chart_row, parent, false);

        TextView topChartEntryTitle = (TextView) rowView.findViewById(R.id.top_chart_entry_title);
        topChartEntryTitle.setText(topChartEntry.title);

        ImageView topChartEntryImage = (ImageView) rowView.findViewById(R.id.top_chart_entry_image);
        switch (position) {
            case 0:
                topChartEntryImage.setImageResource(R.drawable.medal_gold);
                break;
            case 1:
                topChartEntryImage.setImageResource(R.drawable.medal_silver);
                break;
            case 2:
                topChartEntryImage.setImageResource(R.drawable.medal_bronze);
                break;
            case 3:
                topChartEntryImage.setImageResource(R.drawable.top_chart_4);
                break;
            case 4:
                topChartEntryImage.setImageResource(R.drawable.top_chart_5);
                break;
            case 5:
                topChartEntryImage.setImageResource(R.drawable.top_chart_6);
                break;
            case 6:
                topChartEntryImage.setImageResource(R.drawable.top_chart_7);
                break;
            default:
                mLog.error("No image exists for top chart entry at position %d", position);
                break;

        }

        return rowView;
    }

    @Override
    public boolean areAllItemsEnabled () {
        return false;
    }

//    @Override
//    public boolean isEnabled(int position) {
//        return false;
//    }
}