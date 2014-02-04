package com.samteladze.vzradio.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
* Created by user441 on 2/3/14.
*/
public class OnCurrentSongUpdateAlarmReceiver extends BroadcastReceiver {

    private ILog log;

    public OnCurrentSongUpdateAlarmReceiver() {
        this.log = new ConsoleLog(this.getClass().getCanonicalName());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent currentSongChangedIntent =
                new Intent("com.samteladze.vzradio.android.CURRENT_SONG_CHANGED");

        currentSongChangedIntent.putExtra("newSongName", "Arcade Fire - Reflector");

        context.sendBroadcast(currentSongChangedIntent);
    }
}
