package me.roovent.morning;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * Created by Qing Yang on 2/11/15.
 * <p/>
 * When the AlarmRingingActivity is sent to background, a notification will be shown in status bar
 * for user to go back AlarmRingingActivity and dismiss the alarm.
 */
public class RingingNotification {
    private final int NOTIFICATION_ID = 18;

    private Context mContext;
    private NotificationManager mNotificationManager;

    public RingingNotification(Context context) {
        mContext = context;
        mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void send() {
        Notification.Builder builder = new Notification.Builder(mContext)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(mContext.getString(R.string.notification_title))
                .setContentText(mContext.getString(R.string.notification_content));

        Intent intent = new Intent(mContext, AlarmRingingActivity.class);
        intent.setAction("me.roovent.morning.TO_FRONT"); /* matches intent-filter in Manifest */
        PendingIntent notifyIntent = PendingIntent.getActivity(mContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(notifyIntent);

        Notification notification;
        if (Build.VERSION.SDK_INT < 16) {
            notification = builder.getNotification();
        } else {
            notification = builder.build();
        }

        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    public void cancel() {
        mNotificationManager.cancel(NOTIFICATION_ID);
    }
}
