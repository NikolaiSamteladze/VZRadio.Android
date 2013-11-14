package com.samteladze.vzradio.android;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.TrackInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener {
    private static final String ACTION_PLAY = "com.samteladze.vzradio.action.PLAY";
    
    MediaPlayer mMediaPlayer;
    
    WifiLock mWifiLock;
    
    private int NOTIFICATION_ID = 1;
    
    public int onStartCommand(Intent intent, int flags, int startId) {

    	Log.d("Media Player", "Starting service");
    	if (intent.getAction().equals(ACTION_PLAY)) {
    		
    		String url = "http://vzradio.ru:8000/onair";
    		mMediaPlayer = new MediaPlayer();
    		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    		
    		try {
    			mMediaPlayer.setDataSource(url);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    		mMediaPlayer.setOnPreparedListener(this);
    		
    		// Acquire CPU lock and wi-fi lock
    		mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
    		mWifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
    			    .createWifiLock(WifiManager.WIFI_MODE_FULL, "Media Player Wi-Fi Lock");
    		mWifiLock.acquire();
    		
    		 // Prepare async to not block main thread
    		mMediaPlayer.prepareAsync();
    		
    		// Show some text while media player is preparing
    		Toast.makeText(getApplicationContext(), getString(R.string.wait_while_preparing), Toast.LENGTH_LONG).show();
    		
        }
    	
    	return START_STICKY;
    }
    
    /** Called when MediaPlayer is ready */
	@SuppressWarnings("deprecation")
	public void onPrepared(MediaPlayer mediaPlayer) {
		if ((mMediaPlayer != null) && (!mMediaPlayer.isPlaying())) {
    		mMediaPlayer.start();
        	
        	// Create an ongoing notification and make the service run in foreground
        	PendingIntent notificationPIntent = 
        			PendingIntent.getActivity(getApplicationContext(), 0,
    										  new Intent(getApplicationContext(), MainWrapperActivity.class),
    									  	  PendingIntent.FLAG_UPDATE_CURRENT);
        	Notification notification = new Notification();
        	notification.tickerText = "Playing radio";
        	notification.icon = R.drawable.just_fish_logo_cut_72x72;
        	notification.flags |= Notification.FLAG_ONGOING_EVENT;
        	// Need to use this deprecated method to support < 3.0
        	notification.setLatestEventInfo(getApplicationContext(), "Playing Radio",
        	                				"Playing: Unknown", notificationPIntent);
        	startForeground(NOTIFICATION_ID, notification);
    	}
    }

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onDestroy() {
		if (mMediaPlayer != null)  {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
				stopForeground(true);
			}
			
			
			// Clean resources (wi-fi lock and media player itself)
			mWifiLock.release();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		
		super.onDestroy();
	}

}
