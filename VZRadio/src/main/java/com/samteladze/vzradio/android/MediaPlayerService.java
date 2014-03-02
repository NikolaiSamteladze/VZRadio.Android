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

    private static final String SAMARA_RADIO_STREAM_URI = "http://vzradio.ru:8000/onair";

    private MediaPlayer mMediaPlayer;
    private WifiLock mWifiLock;
    private ILog mLog;

    private OnCurrentSongUpdatedReceiver mOnCurrentSongUpdatedReceiver;

    public MediaPlayerService() {
        super();
        mLog = LogManager.getLog(MediaPlayerService.class.getSimpleName());
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mMediaPlayer.setDataSource(SAMARA_RADIO_STREAM_URI);
        } catch (Exception e) {
            mLog.error(e, "Failed to set media player data source to: %s", SAMARA_RADIO_STREAM_URI);
            failAndExit();
        }

        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);

        // Acquire CPU lock and wi-fi lock
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mWifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "Media Player Wi-Fi Lock");
        mWifiLock.acquire();

        mMediaPlayer.prepareAsync();

        // Create notification and start service in foreground
        Notification notification = RadioNotificationManager.getNotification(getApplicationContext());
        startForeground(RadioNotificationManager.RADIO_NOTIFICATION_ID, notification);

        // Register receiver for current songs updates
        IntentFilter intentFilter = new IntentFilter(Intents.CURRENT_SONG_UPDATED);
        mOnCurrentSongUpdatedReceiver = new OnCurrentSongUpdatedReceiver();
        registerReceiver(mOnCurrentSongUpdatedReceiver, intentFilter);

        // Schedule an alarm to update currently played song from server
        scheduleUpdateCurrentSongAlarm();

        // Show some text while media player is preparing
        Toast.makeText(getApplicationContext(), getString(R.string.wait_while_preparing), Toast.LENGTH_LONG).show();

    	return START_STICKY;
    }
    
    @Override
	public void onPrepared(MediaPlayer mediaPlayer) {
		if ((mMediaPlayer != null) && (!mMediaPlayer.isPlaying())) {
    		mMediaPlayer.start();
            broadcastRadioPlaybackStateChangedIntent(RadioPlaybackState.Started);
    	} else {
            mLog.warning("MediaPlayer is null: %s; isPlaying: %s",
                    mMediaPlayer, mMediaPlayer.isPlaying());
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

		if (mMediaPlayer != null)  {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}

			// Clean resources (wi-fi lock and media player itself)
			mWifiLock.release();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}

        cancelUpdateCurrentSongAlarm();
        unregisterReceiver(mOnCurrentSongUpdatedReceiver);

        broadcastRadioPlaybackStateChangedIntent(RadioPlaybackState.Stopped);

        stopForeground(true);
	}

    private void broadcastRadioPlaybackStateChangedIntent(RadioPlaybackState state) {
        Intent radioPlaybackStateChangedIntent = new Intent(Intents.RADIO_PLAYBACK_STATE_CHANGED);
        radioPlaybackStateChangedIntent.putExtra("state", state);
        getApplicationContext().sendBroadcast(radioPlaybackStateChangedIntent);
    }

    private void scheduleUpdateCurrentSongAlarm() {
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
        mLog.error("MediaPlayer service failed. Cleaning up.");

        cancelUpdateCurrentSongAlarm();
        unregisterReceiver(mOnCurrentSongUpdatedReceiver);

        broadcastRadioPlaybackStateChangedIntent(RadioPlaybackState.Error);

        stopForeground(true);
    }

    private void failAndExit() {
        fail();
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
            String newSongName = intent.getStringExtra("newSong");
            RadioNotificationManager.update(newSongName, getApplicationContext());
        }
    }
}
