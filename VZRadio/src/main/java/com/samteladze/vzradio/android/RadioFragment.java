package com.samteladze.vzradio.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.samteladze.vzradio.android.common.Actions;
import com.samteladze.vzradio.android.common.ILog;
import com.samteladze.vzradio.android.common.Intents;
import com.samteladze.vzradio.android.common.LogManager;

public class RadioFragment extends Fragment {
    private static boolean sMusicIsPlaying = false;

    /*
        This builder is reused when radio notification needs to be updated.
     */
    private Notification.Builder mRadioNotificationBuilder = null;

    private final static int NOTIFICATION_ID = 1;

    private Button mButtonPlay;
    private Button mButtonStop;
    private OnCurrentSongUpdatedReceiver mOnCurrentSongUpdatedReceiver;

    private final ILog mLog;

    public RadioFragment() {
        super();
        mLog = LogManager.getLog(this.getClass().getSimpleName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mLog.debug("Creating RadioFragment view");

        View playerView = inflater.inflate(R.layout.fragment_player, container, false);
        
        mButtonPlay = (Button) playerView.findViewById(R.id.buttonPlay);
    	// Enabled when media player is ready to start
    	mButtonPlay.setOnClickListener(new OnClickListener() {
  		  	@Override
  		  	public void onClick(View v) {
  		  		// Start MediaPlayerService using intent
	  		  	Intent serviceIntent = new Intent();
	  		    serviceIntent.setAction(Actions.PLAY_RADIO);
	  		    getActivity().startService(serviceIntent);

                createRadioNotification();

	  		    sMusicIsPlaying = true;
	  		    changeButtonsState(sMusicIsPlaying);
  		  	}
  		});

    	mButtonStop = (Button) playerView.findViewById(R.id.buttonStop);
    	mButtonStop.setOnClickListener(new OnClickListener() {
  		  	@Override
  		  	public void onClick(View v) {
	  		  	// Stop MediaPlayerService using intent
	  		  	getActivity().stopService(new Intent(getActivity(), MediaPlayerService.class));

                cancelRadioNotification();

	  		  	sMusicIsPlaying = false;
	  		  	changeButtonsState(sMusicIsPlaying);
  		  	}
  		});
    	
    	changeButtonsState(sMusicIsPlaying);

        IntentFilter intentFilter =
                new IntentFilter(Intents.CURRENT_SONG_UPDATED);
        mOnCurrentSongUpdatedReceiver = new OnCurrentSongUpdatedReceiver();
        getActivity().registerReceiver(mOnCurrentSongUpdatedReceiver, intentFilter);

        return playerView;
    }

    @Override
    public void onResume() {
        mLog.debug("Resuming RadioFragment");

        super.onResume();
        IntentFilter intentFilter =
                new IntentFilter(Intents.CURRENT_SONG_UPDATED);

        mLog.debug("Registering OnCurrentSongUpdatedReceiver");

        getActivity().registerReceiver(mOnCurrentSongUpdatedReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        mLog.debug("Pausing RadioFragment");

        super.onPause();

        mLog.debug("Unregistering OnCurrentSongUpdatedReceiver");

        getActivity().unregisterReceiver(mOnCurrentSongUpdatedReceiver);
    }

    private void changeButtonsState(boolean musicIsPlaying) {
    	mButtonPlay.setEnabled(!musicIsPlaying);
    	mButtonStop.setEnabled(musicIsPlaying);
    	mButtonPlay.setBackgroundResource(!musicIsPlaying ?
    									  R.drawable.player_play_enabled : 
										  R.drawable.player_play_disabled);
    	mButtonStop.setBackgroundResource(musicIsPlaying ?
										  R.drawable.player_stop_enabled : 
										  R.drawable.player_stop_disabled);
    }

    private class OnCurrentSongUpdatedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mLog.debug("OnCurrentSongUpdatedReceiver onReceive");

            // Change song name on the main radio screen
            String newSongName = intent.getStringExtra("newSong");
            TextView textView = (TextView) getActivity().findViewById(R.id.currentSongName);
            textView.setText(newSongName);

            // Change song name in notification if it is shown
            if (sMusicIsPlaying) {
                updateRadioNotification(newSongName);
            }
        }
    }

    private void createRadioNotification() {
        mLog.debug("Creating a new radio notification");

        updateRadioNotification("");
    }

    private void updateRadioNotification(String text) {
        mLog.debug("Updating radio notification with text: %s", text);

        Context context = getActivity().getApplicationContext();
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a new builder and construct notification from scratch
        if (mRadioNotificationBuilder == null) {
            Intent notificationIntent = new Intent(context, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            Resources resources = context.getResources();
            mRadioNotificationBuilder = new Notification.Builder(context);

            mRadioNotificationBuilder.setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.just_fish_logo_cut_72x72)
                    .setTicker(resources.getString(R.string.play_radio_notification_title))
                    .setWhen(System.currentTimeMillis())
                    .setOngoing(true)
                    .setContentTitle(resources.getString(R.string.play_radio_notification_title));
        }

        mRadioNotificationBuilder.setContentText(text);

        Notification notification = mRadioNotificationBuilder.getNotification();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void cancelRadioNotification() {
        mLog.debug("Canceling radio notification");

        Context context = getActivity().getApplicationContext();
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);

        // Clear builder. Notification will be constructed from scratch
        mRadioNotificationBuilder = null;
    }
}