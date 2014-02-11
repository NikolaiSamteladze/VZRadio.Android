package com.samteladze.vzradio.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samteladze.vzradio.android.domain.Song;

import java.io.IOException;

/**
* Created by user441 on 2/3/14.
*/
public class OnCurrentSongUpdateAlarmReceiver extends BroadcastReceiver {

    public static final String CURRENT_SONG_CHANGED_INTENT_ID =
            "com.samteladze.vzradio.android.CURRENT_SONG_CHANGED";

    private ILog log;
    private Context mContext;

    public OnCurrentSongUpdateAlarmReceiver() {
        this.log = new ConsoleLog(this.getClass().getCanonicalName());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        new UpdateCurrentSongAsyncTask().execute();
    }

    private void broadcastSongChanged(Song newSong) {
        String songAsString;
        if (newSong.artist.equals("Радио «Время Звучать!»"))
        {
            songAsString = "Yoo-hoo! Commercials! Back in a second ...";
        }
        else {
            songAsString = String.format("%s - %s", newSong.artist, newSong.title);
        }

        Intent currentSongChangedIntent = new Intent(CURRENT_SONG_CHANGED_INTENT_ID);
        currentSongChangedIntent.putExtra("newSong", songAsString);
        mContext.sendBroadcast(currentSongChangedIntent);
    }

    private class UpdateCurrentSongAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                VzRadioDataAdapter dataAdapter = new VzRadioDataAdapter();
                String currentSongAsJson = dataAdapter.getCurrentSongAsJson();
                log.info(currentSongAsJson);
                return currentSongAsJson;
            } catch (Exception e) {
                log.error(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String json) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                Song song = mapper.readValue(json, Song.class);
                broadcastSongChanged(song);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }
}
