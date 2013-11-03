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
            StringBuilder notification_text = new StringBuilder("le pointage programmé à XXhXX a été effectué");
            StringBuilder notification_title = new StringBuilder("Pointage effectué");


            mBuilder.setContentTitle(notification_title)
                    .setContentText(notification_text)
                    .setSmallIcon(R.drawable.ic_stat_notify);

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

        final Intent i = new Intent(context, PunchService.class);
        i.setAction(PunchService.DO_PUNCH);
        i.putExtra(PunchService.RESULT_RECEIVER, new PunchResultReceiver(context));
        context.startService(i);

        wl.release();

    }

    public void AddAlarm(Context context) {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        //TODO: add to the collection of alarms
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 10, pi); // Millisec * Second * Minute

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
