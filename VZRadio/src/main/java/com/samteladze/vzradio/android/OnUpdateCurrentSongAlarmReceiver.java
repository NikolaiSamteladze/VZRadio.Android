package com.samteladze.vzradio.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samteladze.vzradio.android.common.ILog;
import com.samteladze.vzradio.android.common.Intents;
import com.samteladze.vzradio.android.common.LogManager;
import com.samteladze.vzradio.android.domain.Song;

import java.io.IOException;

/**
* Created by user441 on 2/3/14.
*/
public class OnUpdateCurrentSongAlarmReceiver extends BroadcastReceiver {
    private final ILog mLog;
    private Context mContext;

    public OnUpdateCurrentSongAlarmReceiver() {
        mLog = LogManager.getLog(this.getClass().getSimpleName());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        new UpdateCurrentSongAsyncTask().execute();
    }

    private void broadcastCurrentSongUpdated(Song newSong) {
        String songAsString;
        if (newSong.artist.equals("Радио «Время Звучать!»"))
        {
            songAsString = "Yoo-hoo! Commercials! Back in a second ...";
        }
        else {
            songAsString = String.format("%s - %s", newSong.artist, newSong.title);
        }

        Intent currentSongChangedIntent = new Intent(Intents.CURRENT_SONG_UPDATED);
        currentSongChangedIntent.putExtra("newSong", songAsString);
        mContext.sendBroadcast(currentSongChangedIntent);
    }

    private class UpdateCurrentSongAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                VzRadioDataAdapter dataAdapter = new VzRadioDataAdapter();
                String currentSongAsJson = dataAdapter.getCurrentSongAsJson();
                mLog.debug("Received song information: %s", currentSongAsJson);
                return currentSongAsJson;
            } catch (Exception e) {
                mLog.error(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String json) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                Song song = mapper.readValue(json, Song.class);
                broadcastCurrentSongUpdated(song);
            } catch (IOException e) {
                mLog.error(e.getMessage());
            }
        }
    }
}
