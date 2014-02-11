package com.samteladze.vzradio.android;

import android.app.ActionBar;
import android.app.AlarmManager;
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

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    private ViewPager viewPager;
    private TabsPagerAdapter tabsPagerAdapter;
    private ActionBar actionBar;

    private OnCurrentSongUpdateAlarmReceiver currentSongUpdateAlarmReceiver;

    private ILog log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.log = new ConsoleLog(this.getClass().getCanonicalName());

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

        scheduleCurrentSongUpdateAlarm();
    }

    @Override
    public void onResume() {
        super.onResume();
        scheduleCurrentSongUpdateAlarm();
    }

    @Override
    public void onPause() {
        super.onPause();
        AlarmManager alarmManager =
                (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        // Create an Intent to start OnCurrentSongUpdateAlarmReceiver
        Intent alarmIntent = new Intent(getApplicationContext(), OnCurrentSongUpdateAlarmReceiver.class);
        // Create a PendingIntent from alarmIntent
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
                alarmIntent, PendingIntent.FLAG_NO_CREATE);

        alarmManager.cancel(alarmPendingIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_wrapper, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
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
        AlarmManager alarmManager =
                (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        // Create an Intent to start OnCurrentSongUpdateAlarmReceiver
        Intent alarmIntent = new Intent(getApplicationContext(), OnCurrentSongUpdateAlarmReceiver.class);
        // Create a PendingIntent from alarmIntent
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
                alarmIntent, PendingIntent.FLAG_NO_CREATE);

        alarmPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, 0);
        // Set repeating alarm that will invoke OnCurrentSongUpdateAlarmReceiver
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 2000, 10000, alarmPendingIntent);
        log.info("An alarm was set to update current song");
    }

}
