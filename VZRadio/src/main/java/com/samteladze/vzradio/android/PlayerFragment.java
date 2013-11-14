package com.samteladze.vzradio.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class PlayerFragment extends Fragment {
    private Button mButtonPlay;
    private Button mButtonStop;
    
    private static boolean mMusicIsPlayingFlag = false;   
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View playerView = inflater.inflate(R.layout.fragment_player, container, false);
        
        mButtonPlay = (Button) playerView.findViewById(R.id.buttonPlay);
    	// Enabled when media player is ready to start
    	mButtonPlay.setOnClickListener(new OnClickListener() {
  		  	@Override
  		  	public void onClick(View v) {
  		  		// Start MediaPlayerService using intent
	  		  	Intent serviceIntent = new Intent();
	  		    serviceIntent.setAction("com.samteladze.vzradio.action.PLAY");
	  		    getActivity().startService(serviceIntent);
	  		    
	  		    mMusicIsPlayingFlag = true;
	  		    changeButtonsState(mMusicIsPlayingFlag);
  		  	}
  		});

    	mButtonStop = (Button) playerView.findViewById(R.id.buttonStop);
    	mButtonStop.setOnClickListener(new OnClickListener() {
  		  	@Override
  		  	public void onClick(View v) {
	  		  	// Stop MediaPlayerService using intent
	  		  	getActivity().stopService(new Intent(getActivity(), MediaPlayerService.class));
	  		  	
	  		  	mMusicIsPlayingFlag = false;
	  		  	changeButtonsState(mMusicIsPlayingFlag);
  		  	}
  		});
    	
    	changeButtonsState(mMusicIsPlayingFlag);
        
        return playerView;
    }
    
    private void changeButtonsState(boolean musicIsPlayingFlag) {
    	mButtonPlay.setEnabled(!musicIsPlayingFlag);
    	mButtonStop.setEnabled(musicIsPlayingFlag);
    	mButtonPlay.setBackgroundResource(!musicIsPlayingFlag ? 
    									  R.drawable.player_play_enabled : 
										  R.drawable.player_play_disabled);
    	mButtonStop.setBackgroundResource(musicIsPlayingFlag ? 
										  R.drawable.player_stop_enabled : 
										  R.drawable.player_stop_disabled);
    }
	

}