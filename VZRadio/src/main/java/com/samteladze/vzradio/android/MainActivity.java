package com.samteladze.vzradio.android;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.samteladze.vzradio.android.common.ILog;
import com.samteladze.vzradio.android.common.LogManager;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    private ViewPager viewPager;
    private TabsPagerAdapter tabsPagerAdapter;
    private ActionBar actionBar;

    private OnUpdateCurrentSongAlarmReceiver currentSongUpdateAlarmReceiver;

    private ILog mLog;

    public MainActivity() {
        super();
        mLog = LogManager.getLog(this.getClass().getSimpleName());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLog.debug("Creating MainActivity");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager(), this);

        viewPager.setAdapter(tabsPagerAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding tabs
        for (int i = 0; i < tabsPagerAdapter.getCount(); i++) {
            actionBar.addTab(actionBar.newTab()
                    .setText(tabsPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        }

        // When user navigates between tabs, ViewPager will highlight the selected tab
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int position) {
                // Highlight the selected tab
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        // Show Radio Fragment on start
        viewPager.setCurrentItem(1);
    }

    @Override
    public void onResume() {
        mLog.debug("Resuming MainActivity");
        super.onResume();

        mLog.debug("Scheduling CurrentSongUpdateAlarm");
        scheduleCurrentSongUpdateAlarm();
    }

    @Override
    public void onPause() {
        mLog.debug("Pausing MainActivity");

        super.onPause();

        mLog.debug("Canceling CurrentSongUpdateAlarm");
        cancelCurrentSongUpdateAlarm();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_wrapper, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {

            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

    private void scheduleCurrentSongUpdateAlarm() {
        mLog.debug("MainActivity in scheduleCurrentSongUpdateAlarm method");

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

    private void cancelCurrentSongUpdateAlarm() {
        mLog.debug("MainActivity in cancelCurrentSongUpdateAlarm method");

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

}
