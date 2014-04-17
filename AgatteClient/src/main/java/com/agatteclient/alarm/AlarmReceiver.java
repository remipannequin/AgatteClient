/*This file is part of AgatteClient.

    AgatteClient is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    AgatteClient is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with AgatteClient.  If not, see <http://www.gnu.org/licenses/>.*/

package com.agatteclient.alarm;

import android.app.AlarmManager;
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
import android.util.Pair;

import com.agatteclient.MainActivity;
import com.agatteclient.R;
import com.agatteclient.agatte.AgatteResponse;
import com.agatteclient.agatte.AgatteResultCode;
import com.agatteclient.agatte.PunchService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Broadcast Receiver that is fired when an alarm expire.
 * <p/>
 * Created by Rémi Pannequin on 01/11/13.
 */
public class AlarmReceiver extends BroadcastReceiver {

    //Manage a collection of alarm, with their fingerprint
    private final Map<PunchAlarmTime, Pair<PendingIntent, Long>> pendingIntentMap;


    public AlarmReceiver() {
        this.pendingIntentMap = new HashMap<PunchAlarmTime, Pair<PendingIntent, Long>>();
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

    public void updateAlarms(Context context, AlarmBinder binder) {
        //Extract longs representing the alarms
        Set<Long> alarms = new HashSet<Long>(binder.size());
        for (PunchAlarmTime a : binder) {
            alarms.add(a.toLong());
        }

        //Cancel alarms that are not in the binder any more
        for (PunchAlarmTime a : pendingIntentMap.keySet()) {
            if (!binder.contains(a)) {
                cancelAlarm(context, a);
            }
        }

        //Add alarms that are new, update the other ones
        for (PunchAlarmTime a : binder) {
            if (!pendingIntentMap.containsKey(a)) {
                addAlarm(context, a);
            } else {
                updateAlarm(context, a);
            }
        }
    }

    private void addAlarm(Context context, PunchAlarmTime alarm) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmReceiver.class);
        //TODO: use request code
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_ONE_SHOT);
        pendingIntentMap.put(alarm, new Pair<PendingIntent, Long>(pi, alarm.toLong()));

        long now = System.currentTimeMillis();
        long delay = alarm.nextAlarm(now);
        am.set(AlarmManager.RTC_WAKEUP, delay, pi);
    }

    private void cancelAlarm(Context context, PunchAlarmTime alarm) {
        PendingIntent sender = pendingIntentMap.get(alarm).first;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
        pendingIntentMap.remove(alarm);

    }

    private void updateAlarm(Context context, PunchAlarmTime alarm) {
        PendingIntent sender = pendingIntentMap.get(alarm).first;
        long fingerprint = pendingIntentMap.get(alarm).second;
        //check if alarm time has changed by comparing its current fingerprint with the stored one
        if (alarm.toLong() != fingerprint) {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.cancel(sender);
            Intent i = new Intent(context, AlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_ONE_SHOT);
            pendingIntentMap.put(alarm, new Pair(pi, alarm.toLong()));
            long now = System.currentTimeMillis();
            long delay = alarm.nextAlarm(now);
            am.set(AlarmManager.RTC_WAKEUP, delay, pi);
        }
    }

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

            AgatteResultCode code = AgatteResultCode.values()[resultCode];
            //set notification text and title based on result
            StringBuilder notification_text = new StringBuilder();
            StringBuilder notification_title = new StringBuilder();

            int icon;
            if (code == AgatteResultCode.punch_ok || code == AgatteResultCode.query_ok) {
                notification_title.append(ctx.getString(R.string.programmed_punch_failed_title));
                icon = R.drawable.ic_stat_agatte;
            } else {
                notification_title.append(ctx.getString(R.string.programmed_punch_success_title));
                icon = R.drawable.ic_stat_alerts_and_states_warning;
            }

            switch (code) {
                case network_not_authorized:
                    notification_text.append(ctx.getString(R.string.unauthorized_network_toast));
                    break;
                case exception:
                    notification_text.append(ctx.getString(R.string.network_error_toast));
                    String message = resultData.getString("message");
                    if (message != null && message.length() != 0) {
                        notification_text.append(" : ").append(message);
                    }
                    break;
                case login_failed:
                    notification_text.append(ctx.getString(R.string.login_failed_toast));
                    break;
                case io_exception:
                    notification_text.append("Error");
                    message = resultData.getString("message");
                    if (message != null && message.length() != 0) {
                        notification_text.append(" : ").append(message);
                    }
                    break;
                case punch_ok:
                case query_ok:
                    AgatteResponse rsp = AgatteResponse.fromBundle(resultData);
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


}
