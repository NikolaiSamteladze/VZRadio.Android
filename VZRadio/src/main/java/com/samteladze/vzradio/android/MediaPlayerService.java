package com.samteladze.vzradio.android;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.samteladze.vzradio.android.common.Actions;
import com.samteladze.vzradio.android.common.ILog;
import com.samteladze.vzradio.android.common.Intents;
import com.samteladze.vzradio.android.common.LogManager;

public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener {

    private MediaPlayer mMediaPlayer;
    private WifiLock mWifiLock;
    private ILog mLog;

    private OnCurrentSongUpdatedReceiver mOnCurrentSongUpdatedReceiver;

    public MediaPlayerService() {
        super();
        mLog = LogManager.getLog(MediaPlayerService.class.getSimpleName());
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
    	mLog.debug("In onStartCommand");

        mLog.debug("PLAY_RADIO action received");

        String url = "http://vzradio.ru:8000/onair";

        mLog.debug("Creating MediaPlayer ...");

        mMediaPlayer = new MediaPlayer();

        mLog.debug("Setting MediaPlayer's events listeners ...");
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);

        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mMediaPlayer.setDataSource(url);

            mLog.debug("Setting media player data source to %s", url);
        } catch (Exception e) {
            mLog.error(e, "Failed to set media player data source to: %s", url);
            failAndExit();
        }

        mLog.debug("Setting onPreparedListener to self ...");

        mMediaPlayer.setOnPreparedListener(this);

        // Acquire CPU lock and wi-fi lock

        mLog.debug("Acquiring CPU and Wi-Fi lock ...");

        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mWifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "Media Player Wi-Fi Lock");
        mWifiLock.acquire();

        mLog.debug("Successfully acquired CPU and Wi-Fi logs");

        mLog.debug("Starting prepare async ...");

         // Prepare async to not block main thread
        mMediaPlayer.prepareAsync();

        mLog.debug("Starting MediaPlayerService in foreground with notification");
        Notification notification = RadioNotificationManager.getNotification(getApplicationContext());
        startForeground(RadioNotificationManager.RADIO_NOTIFICATION_ID, notification);

        mLog.debug("Registering OnUpdateCurrentSongAlarmReceiver");
        IntentFilter intentFilter = new IntentFilter(Intents.CURRENT_SONG_UPDATED);
        mOnCurrentSongUpdatedReceiver = new OnCurrentSongUpdatedReceiver();
        registerReceiver(mOnCurrentSongUpdatedReceiver, intentFilter);

        mLog.debug("Service is preparing. Scheduling UpdateCurrentSong alarm");
        scheduleUpdateCurrentSongAlarm();

        // Show some text while media player is preparing
        Toast.makeText(getApplicationContext(), getString(R.string.wait_while_preparing), Toast.LENGTH_LONG).show();

        // Create notification so app can be accessed with closed activity
//        mLog.debug("Creating Radio Notification");
//        RadioNotificationManager.create(getApplicationContext());

        mLog.debug("Returning from service onStartCommand");

    	return START_STICKY;
    }
    
    @Override
	public void onPrepared(MediaPlayer mediaPlayer) {
        mLog.debug("MediaPlayerService is prepared");

		if ((mMediaPlayer != null) && (!mMediaPlayer.isPlaying())) {
            mLog.debug("Starting playback");

    		mMediaPlayer.start();
            broadcastRadioPlaybackStateChangedIntent(RadioPlaybackState.Started);
    	} else {
            mLog.debug("MediaPlayer: %s, isPlaying: %s", mMediaPlayer, mMediaPlayer.isPlaying());

        }
    }

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onDestroy() {
        super.onDestroy();

        mLog.debug("Destroying MediaPlayerService");

		if (mMediaPlayer != null)  {
			if (mMediaPlayer.isPlaying()) {
                mLog.debug("Stopping playback");
				mMediaPlayer.stop();
			}

			// Clean resources (wi-fi lock and media player itself)
			mWifiLock.release();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}

        broadcastRadioPlaybackStateChangedIntent(RadioPlaybackState.Stopped);

        mLog.debug("MediaPlayer service is destroyed. Canceling UpdateCurrentSong alarm");
		cancelUpdateCurrentSongAlarm();

//        mLog.debug("Canceling Radio Notification");
//        RadioNotificationManager.cancel(getApplicationContext());

        mLog.debug("Failed. Unregistering CurrentSongUpdatedAlarmReceiver");
        unregisterReceiver(mOnCurrentSongUpdatedReceiver);

        stopForeground(true);
	}

    private void broadcastRadioPlaybackStateChangedIntent(RadioPlaybackState state) {
        mLog.debug("Broadcasting new radio playback state: %s", state);
        Intent radioPlaybackStateChangedIntent = new Intent(Intents.RADIO_PLAYBACK_STATE_CHANGED);
        radioPlaybackStateChangedIntent.putExtra("state", state);
        getApplicationContext().sendBroadcast(radioPlaybackStateChangedIntent);
    }

    private void scheduleUpdateCurrentSongAlarm() {
        mLog.debug("Scheduling CurrentSongUpdate Alarm");

        AlarmManager alarmManager =
                (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        // Create an Intent to start OnUpdateCurrentSongAlarmReceiver
        Intent alarmIntent = new Intent(getApplicationContext(), OnUpdateCurrentSongAlarmReceiver.class);
        // Create a PendingIntent from alarmIntent
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
                alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Set repeating alarm that will invoke OnUpdateCurrentSongAlarmReceiver
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 2000, 10000, alarmPendingIntent);

        mLog.info("An alarm was set to update current song");
    }

    private void cancelUpdateCurrentSongAlarm() {
        mLog.debug("Canceling UpdateCurrentSong alarm");

        AlarmManager alarmManager =
                (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        // Create an Intent to start OnUpdateCurrentSongAlarmReceiver
        Intent alarmIntent = new Intent(getApplicationContext(), OnUpdateCurrentSongAlarmReceiver.class);
        // Create a PendingIntent from alarmIntent
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
                alarmIntent, PendingIntent.FLAG_NO_CREATE);

        if (alarmPendingIntent != null) {
            alarmManager.cancel(alarmPendingIntent);
        } else {
            mLog.warning("Attempting to cancel current song update alarm that doesn't exist");
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mLog.error("An error occurred during an async operation. Code: %s, Extra: %s", what, extra);
        fail();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mLog.debug("In onCompletion");
    }

    private void fail() {
        mLog.debug("In fail");

        mLog.debug("MediaPlayer service failed. Canceling UpdateCurrentSong alarm");
        cancelUpdateCurrentSongAlarm();

        mLog.debug("Failed. Canceling radio notification if exists");

//        RadioNotificationManager.cancel(this);

        mLog.debug("Failed. Unregistering CurrentSongUpdatedAlarmReceiver");
        unregisterReceiver(mOnCurrentSongUpdatedReceiver);

        broadcastRadioPlaybackStateChangedIntent(RadioPlaybackState.Error);

        stopForeground(true);
    }

    private void failAndExit() {
        fail();

        mLog.debug("Stopping service");
        stopSelf();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        /*
            Percent argument has negative values up to MIN INTEGER. Weird...
         */
        // mLog.debug("Buffered %s percent", percent);
    }

    private class OnCurrentSongUpdatedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mLog.debug("OnCurrentSongUpdatedReceiver onReceive");

            String newSongName = intent.getStringExtra("newSong");

            mLog.debug("Updating Radio notification with %s", newSongName);
            RadioNotificationManager.update(newSongName, getApplicationContext());
        }
    }
}
