package com.samteladze.vzradio.android;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
    private final ILog mLog;

    private Button mButtonPlay;
    private Button mButtonStop;

    private OnCurrentSongUpdatedReceiver mOnCurrentSongUpdatedReceiver;
    private OnRadioPlaybackStateChangedReceiver mOnRadioPlaybackStateChangedReceiver;

    private RadioPlaybackState mRadioPlaybackState;
    private String mCurrentlyPlayedSongName;

    private static final String IS_PLAY_BUTTON_ENABLED_EXTRA_KEY = "isPlayButtonEnabled";
    private static final String IS_STOP_BUTTON_ENABLED_EXTRA_KEY = "isStopButtonEnabled";

    public RadioFragment() {
        super();
        mLog = LogManager.getLog(RadioFragment.class.getSimpleName());
        mRadioPlaybackState = RadioPlaybackState.Stopped;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View playerView = inflater.inflate(R.layout.fragment_player, container, false);
        
        mButtonPlay = (Button) playerView.findViewById(R.id.buttonPlay);
    	mButtonPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start radio playback
                Intent serviceIntent = new Intent();
                serviceIntent.setAction(Actions.PLAY_RADIO);
                getActivity().startService(serviceIntent);
            }
        });

    	mButtonStop = (Button) playerView.findViewById(R.id.buttonStop);
    	mButtonStop.setOnClickListener(new OnClickListener() {
  		  	@Override
  		  	public void onClick(View v) {
                // Stop radio playback
	  		  	getActivity().stopService(new Intent(getActivity(), RadioPlaybackService.class));
  		  	}
  		});

        if ((savedInstanceState != null) &&
                savedInstanceState.containsKey(IS_PLAY_BUTTON_ENABLED_EXTRA_KEY) &&
                savedInstanceState.containsKey(IS_STOP_BUTTON_ENABLED_EXTRA_KEY)) {

            updateRadioPlaybackControls(savedInstanceState.getBoolean(IS_PLAY_BUTTON_ENABLED_EXTRA_KEY),
                    savedInstanceState.getBoolean(IS_STOP_BUTTON_ENABLED_EXTRA_KEY));
        } else {
            mLog.info("Setting radio controls state to default");
            updateRadioPlaybackControls(true, false);
        }

        registerBroadcastReceivers();

        return playerView;
    }

    @Override
    public void onResume() {
        super.onResume();

        registerBroadcastReceivers();
    }

    @Override
    public void onPause() {
        super.onPause();

        unregisterBroadcastReceivers();
        clearCurrentlyPlayedSongName();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putBoolean(IS_PLAY_BUTTON_ENABLED_EXTRA_KEY, mButtonPlay.isEnabled());
        savedInstanceState.putBoolean(IS_STOP_BUTTON_ENABLED_EXTRA_KEY, mButtonStop.isEnabled());
    }

    private void registerBroadcastReceivers() {
        // Receive currently played song changes
        IntentFilter currentSongUpdatedIntentFilter = new IntentFilter(Intents.CURRENT_SONG_UPDATED);
        if (mOnCurrentSongUpdatedReceiver  == null) {
            mOnCurrentSongUpdatedReceiver = new OnCurrentSongUpdatedReceiver();
        }
        getActivity().registerReceiver(mOnCurrentSongUpdatedReceiver, currentSongUpdatedIntentFilter);

        // Receive radio playback state changes
        IntentFilter radioPlaybackStateChangedIntentFilter =
                new IntentFilter(Intents.RADIO_PLAYBACK_STATE_CHANGED);
        if (mOnRadioPlaybackStateChangedReceiver == null) {
            mOnRadioPlaybackStateChangedReceiver = new OnRadioPlaybackStateChangedReceiver();
        }
        getActivity().registerReceiver(mOnRadioPlaybackStateChangedReceiver,
                radioPlaybackStateChangedIntentFilter);
    }

    private void unregisterBroadcastReceivers() {
        if (mOnCurrentSongUpdatedReceiver != null) {
            getActivity().unregisterReceiver(mOnCurrentSongUpdatedReceiver);
        } else {
            mLog.warning("Trying to unregister OnCurrentSongUpdatedReceiver that does not exist");
        }

        if (mOnRadioPlaybackStateChangedReceiver != null) {
            getActivity().unregisterReceiver(mOnRadioPlaybackStateChangedReceiver);
        } else {
            mLog.warning("Trying to unregister OnRadioPlaybackStateChangedReceiver that does not exist");
        }
    }

    private void updateRadioPlaybackControls(boolean isPlayButtonEnabled, boolean isStopButtonEnabled) {
        mButtonPlay.setEnabled(isPlayButtonEnabled);
        mButtonStop.setEnabled(isStopButtonEnabled);

        mButtonPlay.setBackgroundResource(isPlayButtonEnabled ?
                R.drawable.player_play_enabled :
                R.drawable.player_play_disabled);
        mButtonStop.setBackgroundResource(isStopButtonEnabled ?
                R.drawable.player_stop_enabled :
                R.drawable.player_stop_disabled);
    }

    private void updateCurrentlyPlayedSongName(String newSongName) {
        if (newSongName == null) {
            mLog.warning("Attempting to update currently played song name with null value");
            return;
        }

        TextView textView = (TextView) getActivity().findViewById(R.id.currentSongName);
        textView.setText(newSongName);
    }

    private void clearCurrentlyPlayedSongName() {
        updateCurrentlyPlayedSongName("");
    }

    private class OnCurrentSongUpdatedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newSongName = intent.getStringExtra("song");

            mCurrentlyPlayedSongName = newSongName;

            if (mRadioPlaybackState == RadioPlaybackState.Started) {

                updateCurrentlyPlayedSongName(newSongName);
            }
        }
    }

    private class OnRadioPlaybackStateChangedReceiver extends  BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            RadioPlaybackState state = (RadioPlaybackState) intent.getSerializableExtra("state");
            mRadioPlaybackState = state;

            switch (state) {
                case Preparing:
                    updateRadioPlaybackControls(false, false);
                    updateCurrentlyPlayedSongName("Buffering radio ...");
                    break;
                case Started:
                    updateRadioPlaybackControls(false, true);
                    if (mCurrentlyPlayedSongName != null) {
                        updateCurrentlyPlayedSongName(mCurrentlyPlayedSongName);
                    }
                    break;
                case Stopped:
                    updateRadioPlaybackControls(true, false);
                    clearCurrentlyPlayedSongName();
                    break;
                case Error:
                    updateRadioPlaybackControls(true, false);
                    clearCurrentlyPlayedSongName();
                    break;
            }
        }
    }
}