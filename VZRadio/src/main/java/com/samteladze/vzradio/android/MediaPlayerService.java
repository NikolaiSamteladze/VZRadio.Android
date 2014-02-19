package com.samteladze.vzradio.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.samteladze.vzradio.android.common.Actions;
import com.samteladze.vzradio.android.common.ILog;
import com.samteladze.vzradio.android.common.Intents;
import com.samteladze.vzradio.android.common.LogManager;

public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener {
    private MediaPlayer mMediaPlayer;
    private WifiLock mWifiLock;
    private ILog mLog;

    public MediaPlayerService() {
        super();
        mLog = LogManager.getLog(this.getClass().getSimpleName());
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
    	mLog.debug("Starting MediaPlayerService");

    	if (intent.getAction().equals(Actions.PLAY_RADIO)) {

            mLog.debug("PLAY_RADIO action received");

    		String url = "http://vzradio.ru:8000/onair";

            mLog.debug("Creating MediaPlayer ...");

    		mMediaPlayer = new MediaPlayer();
    		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    		
    		try {
    			mMediaPlayer.setDataSource(url);

                mLog.debug("Setting media player data source to %s", url);
			} catch (Exception e) {
				mLog.error(e, "Failed to set media player data source to: %s", url);
                return 0;
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
    		
    		// Show some text while media player is preparing
    		Toast.makeText(getApplicationContext(), getString(R.string.wait_while_preparing), Toast.LENGTH_LONG).show();
    		
        }

        mLog.debug("Returning from service onStartCommand");

    	return START_STICKY;
    }
    
    /** Called when MediaPlayer is ready */
	public void onPrepared(MediaPlayer mediaPlayer) {
        mLog.debug("MediaPlayerService is prepared");

		if ((mMediaPlayer != null) && (!mMediaPlayer.isPlaying())) {
            mLog.debug("Starting playback");

    		mMediaPlayer.start();
            broadcastRadioPlaybackStateChangedIntent("on");
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
        mLog.debug("Destroying MediaPlayerService");

		if (mMediaPlayer != null)  {
			if (mMediaPlayer.isPlaying()) {
                mLog.debug("Stopping playback");
				mMediaPlayer.stop();
				stopForeground(true);
			}

            broadcastRadioPlaybackStateChangedIntent("off");
			
			// Clean resources (wi-fi lock and media player itself)
			mWifiLock.release();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		
		super.onDestroy();
	}

    private void broadcastRadioPlaybackStateChangedIntent(String state) {
        Intent radioPlaybackStateChangedIntent = new Intent(Intents.RADIO_PLAYBACK_STATE_CHANGED);
        radioPlaybackStateChangedIntent.putExtra("state", state);
        getApplicationContext().sendBroadcast(radioPlaybackStateChangedIntent);
    }

}
