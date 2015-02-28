package me.roovent.morning;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.format.DateFormat;

import java.util.Date;

import me.roovent.morning.model.Alarm;

/**
 * Created by Qing Yang on 2/27/15.
 */
public class SnoozedNotification {

    private Context mContext;
    private Alarm mAlarm;
    private NotificationManager mNotificationManager;

    static public int getNotificationId(int alarmId) {
        return alarmId + 1; /* '+1' to make sure it's greater than 0 */
    }

    public SnoozedNotification(Context context, Alarm alarm) {
        mContext = context;
        mAlarm = alarm;
        mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void send() {
        Intent intent = new Intent(mContext, AlarmService.class);
        intent.setAction(AlarmService.CANCEL);
        intent.putExtra(Alarm.KEY_ALARM_ID, mAlarm.id);
        intent.putExtra(AlarmService.KEY_IS_SNOOZED, true);
        PendingIntent notifyIntent = PendingIntent.getService(mContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(mContext)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(mContext.getString(R.string.notification_title_snoozed))
                .setContentText("A snoozed alarm is about to ring at " + DateFormat.format("hh:mm a", new Date()))
                .setOngoing(true);

        Notification notification;
        if (Build.VERSION.SDK_INT < 16) {
            builder.setContentIntent(notifyIntent);
            notification = builder.getNotification();
        } else {
            builder.addAction(R.drawable.ic_action_cancel_dark, mContext.getString(R.string.cancel), notifyIntent);
            notification = builder.build();
        }

        mNotificationManager.notify(getNotificationId(mAlarm.id), notification);
    }
}
