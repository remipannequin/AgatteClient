package com.agatteclient;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Broadcast Receiver that is fired when an alarm expire.
 *
 * Created by Rémi Pannequin on 01/11/13.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private class PunchResultReceiver extends ResultReceiver {

        private final Context ctx;
        private final NotificationManager mNotifyManager;
        private final NotificationCompat.Builder mBuilder;

        public PunchResultReceiver(Context ctx) {
            super(new Handler(Looper.getMainLooper()));
            this.ctx = ctx;
            mNotifyManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder = new NotificationCompat.Builder(ctx);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            AgatteResponse rsp = AgatteResponse.fromBundle(resultData);
            //TODO: set notification text and title based on result
            StringBuilder notification_text = new StringBuilder();
            StringBuilder notification_title = new StringBuilder();

            int icon;
            if (rsp.isError()) {
                notification_title.append(ctx.getString(R.string.programmed_punch_success_title));
                icon = R.drawable.ic_stat_notify;
            } else {
                notification_title.append(ctx.getString(R.string.programmed_punch_failed_title));
                icon = R.drawable.ic_stat_notify;
            }

            switch (rsp.getCode()) {
                case NetworkNotAuthorized:
                    notification_text.append(ctx.getString(R.string.unauthorized_network_toast));
                    break;
                case UnknownError:

                    break;
                case LoginFailed:
                    notification_text.append(ctx.getString(R.string.login_failed_toast));
                    break;
                case IOError:
                    notification_text.append(ctx.getString(R.string.network_error_toast));
                    if (rsp.hasDetail()) {
                        notification_text.append(" : ").append(rsp.getDetail());
                    }
                    break;
                case PunchOK:
                case QueryOK:
                    notification_text.append(ctx.getString(R.string.successful_programmed_punch));
                    break;
                default:


            }


            mBuilder.setContentTitle(notification_title)
                    .setContentText(notification_text)
                    .setSmallIcon(icon);

            PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
                    new Intent(ctx, MainActivity.class), 0);

            mNotifyManager.notify(0, mBuilder.build());




        }
    }


    //Manage a collection of alarm
    Map<PunchAlarmTime, PendingIntent> alarms;

    public AlarmReceiver() {
        this.alarms = new HashMap<PunchAlarmTime, PendingIntent>();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();
        //Do the punch by calling the punching service
        final Intent i = new Intent(context, PunchService.class);
        i.setAction(PunchService.DO_PUNCH);
        i.putExtra(PunchService.RESULT_RECEIVER, new PunchResultReceiver(context));
        context.startService(i);

        wl.release();
    }


    public void AddAlarm(Context context, PunchAlarmTime alarm) {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmReceiver.class);
        //TODO: use request code
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_ONE_SHOT);
        alarms.put(alarm, pi);
        long now = System.currentTimeMillis();
        long delay = alarm.nextAlarm(now);
        am.set(AlarmManager.RTC_WAKEUP, delay, pi);
    }


    public Collection<Date> getAlarms() {
        //TODO


        return null;
    }

    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }



}
