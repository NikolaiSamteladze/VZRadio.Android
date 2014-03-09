package com.samteladze.vzradio.android;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.samteladze.vzradio.android.common.ILog;
import com.samteladze.vzradio.android.common.LogManager;
import com.samteladze.vzradio.android.domain.TopChartEntry;

import java.util.ArrayList;
import java.util.List;

public class TopChartFragment extends ListFragment {
    private ILog mLog;

    private final String[] topSongs = new String[] {
        "Сальвадор Вдали — Красота",
        "АПИ Тупые Предметы — Черепаха",
        "Бар Дюр — Отпускаю",
        "Два Льва — Просроченная Любовь",
        "Crazy Jump — Художник",
        "Виражи — Далеко",
        "Внутри Себя.. — Целого мира мало",
    };

    public TopChartFragment() {
        mLog = LogManager.getLog(TopChartFragment.class);
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
                R.layout.fragment_top_chart, container, false);
        superViewGroup.addView(mLinearLayout, superListViewIndex, superListView.getLayoutParams());
        return superView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        List<TopChartEntry> topChartEntries = new ArrayList<TopChartEntry>();
        for (int i = 0; i < 7; i++) {
            topChartEntries.add(new TopChartEntry(topSongs[i]));
        }

        ArrayAdapter<TopChartEntry> topChartArrayAdapter =
                new TopChartArrayAdapter(getActivity().getApplicationContext(), topChartEntries);
        setListAdapter(topChartArrayAdapter);
    }
}