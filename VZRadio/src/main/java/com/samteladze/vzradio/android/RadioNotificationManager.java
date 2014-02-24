package com.samteladze.vzradio.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import com.samteladze.vzradio.android.common.ILog;
import com.samteladze.vzradio.android.common.LogManager;

/**
 * Created by nsamteladze on 2/21/14.
 */
public class RadioNotificationManager {

    private static Notification.Builder sNotificationBuilder;

    private static ILog sLog = LogManager.getLog(RadioNotificationManager.class.getSimpleName());

    private static int sRadioNotificationPendingIntentId = 0;
    public static final int RADIO_NOTIFICATION_ID = 1;

    public static void initialize(Context context) {
        
    }

    public static Notification getNotification(Context context) {
        sLog.debug("In getNotification");
        return getNotificationBuilder(context).getNotification();
    }

    public static void create(Context context) {
        sLog.debug("In create");
        Notification notification = getNotificationBuilder(context).getNotification();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(RADIO_NOTIFICATION_ID, notification);
    }

    public static void update(String text, Context context) {
        sLog.debug("In update");

        if (!exists(context)) {
            sLog.warning("Trying to update Radio Notification that does not exist");
            return;
        }

        Notification.Builder builder = getNotificationBuilder(context);
        builder.setContentText(text);

        Notification notification = builder.getNotification();
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(RADIO_NOTIFICATION_ID, notification);
    }

    public static  void cancel(Context context) {
        sLog.debug("In cancel");

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(RADIO_NOTIFICATION_ID);
    }

    private static Notification.Builder getNotificationBuilder(Context context) {
        sLog.debug("In getNotificationBuilder");

        if (sNotificationBuilder != null) return sNotificationBuilder;

        // Get application context if another context type was passed
        Context applicationContext = context.getApplicationContext();

        sNotificationBuilder = new Notification.Builder(applicationContext);

        Intent intent = new Intent(applicationContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(applicationContext, sRadioNotificationPendingIntentId,
                        intent, PendingIntent.FLAG_NO_CREATE);

        Resources resources = applicationContext.getResources();

        sNotificationBuilder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.just_fish_logo_cut_72x72)
                .setTicker(resources.getString(R.string.play_radio_notification_title))
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .setContentTitle(resources.getString(R.string.play_radio_notification_title));

        return sNotificationBuilder;
    }

    public static boolean exists(Context context) {
        sLog.debug("In exists");

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, sRadioNotificationPendingIntentId,
                        intent, PendingIntent.FLAG_NO_CREATE);

        sLog.debug("Notification exists %s", (pendingIntent != null));

        return (pendingIntent != null);
    }
}