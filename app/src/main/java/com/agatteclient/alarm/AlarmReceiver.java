/*
 * This file is part of AgatteClient.
 *
 * AgatteClient is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AgatteClient is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AgatteClient.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2014 Rémi Pannequin (remi.pannequin@gmail.com).
 */

package com.agatteclient.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.ResultReceiver;
import android.util.Log;

import com.agatteclient.MainActivity;
import com.agatteclient.R;
import com.agatteclient.agatte.AgatteResponse;
import com.agatteclient.agatte.AgatteResultCode;
import com.agatteclient.agatte.PunchService;
import com.agatteclient.card.CardBinder;
import com.agatteclient.card.DayCard;

import java.text.ParseException;

/**
 * Broadcast Receiver that is fired when an alarm expire.
 * <p/>
 * Created by Rémi Pannequin on 01/11/13.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();
        // get the schedule ID
        int schedule_id = intent.getIntExtra(AlarmRegistry.ALARM_ID, -1);
        if (schedule_id == -1) {
            Log.e(MainActivity.LOG_TAG, "Unable to get schedule ID for alarm");//NON-NLS
        }
        // Check that the alarm is scheduled (i.e. there is a corresponding entry in the DB
        AlarmRegistry.RecordedAlarm rec = AlarmRegistry.getInstance().getIdFromSchedule(context, schedule_id);
        if (rec != null) {//valid ID found
            //Do the punch by calling the punching service
            final Intent i = new Intent(context, PunchService.class);
            switch (rec.constraint) {
                case unconstraigned:
                    i.setAction(PunchService.DO_PUNCH);
                    break;
                case arrival:
                    i.setAction(PunchService.DO_PUNCH_ARRIVAL);
                    break;
                case leaving:
                    i.setAction(PunchService.DO_PUNCH_LEAVING);
                    break;
                default:
                    Log.wtf(MainActivity.LOG_TAG, "Unknown constraint " + rec.constraint.toString());//NON-NLS
            }
            i.putExtra(PunchService.RESULT_RECEIVER, new PunchResultReceiver(context, rec.alarm_id));
            context.startService(i);
        } else {
            Log.w(MainActivity.LOG_TAG, "Trying to execute spurious alarm (not found in DB)");//NON-NLS
        }
        wl.release();
    }


    private class PunchResultReceiver extends ResultReceiver {

        private final Context ctx;
        private final long alarm_id;

        public PunchResultReceiver(Context ctx, long alarm_id) {
            super(new Handler(Looper.getMainLooper()));
            this.ctx = ctx;
            this.alarm_id = alarm_id;
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            AgatteResultCode code = AgatteResultCode.values()[resultCode];
            //set notification text and title based on result
            StringBuilder notification_text = new StringBuilder();

            switch (code) {
                case network_not_authorized:
                    notification_text.append(ctx.getString(R.string.unauthorized_network_toast));
                    AlarmRegistry.getInstance().setFailed(ctx, alarm_id);
                    break;
                case query_counter_ok:
                    break;
                case query_counter_unavailable:
                    break;
                case exception:
                    notification_text.append(ctx.getString(R.string.network_error_toast));
                    String message = resultData.getString("message"); //NON-NLS
                    if (message != null && message.length() != 0) {
                        notification_text.append(" : ").append(message);
                    }
                    AlarmRegistry.getInstance().setFailed(ctx, alarm_id);
                    break;
                case login_failed:
                    notification_text.append(ctx.getString(R.string.login_failed_toast));
                    AlarmRegistry.getInstance().setFailed(ctx, alarm_id);
                    break;
                case io_exception:
                    notification_text.append(ctx.getString(R.string.error));
                    message = resultData.getString("message"); //NON-NLS
                    if (message != null && message.length() != 0) {
                        notification_text.append(" : ").append(message);
                    }
                    AlarmRegistry.getInstance().setFailed(ctx, alarm_id);
                    break;
                case punch_ok:
                case query_ok:
                    AgatteResponse rsp = AgatteResponse.fromBundle(resultData);
                    String time = rsp.getLastPunch();
                    notification_text.append(String.format(ctx.getString(R.string.successful_programmed_punch), time));
                    /* update day view with new punch times */
                    try {
                        DayCard cur_card = CardBinder.getInstance().getTodayCard();
                        if (rsp.hasVirtualPunches()) {
                            cur_card.addPunches(rsp.getVirtualPunches(), true);
                        }
                        if (rsp.hasPunches()) {
                            cur_card.addPunches(rsp.getPunches(), false);
                        }
                    } catch (ParseException e) {
                        Log.e(MainActivity.LOG_TAG, "Parse exception when updating the punch-card");//NON-NLS
                    }
                    //Update AlarmRegistry with value
                    AlarmRegistry.getInstance().setSucessfull(ctx, alarm_id);
                    break;
                case invalidPunchingCondition:
                    notification_text.append("Required conditions were not met");//NON-NLS
                    message = resultData.getString("message");//NON-NLS
                    if (message != null && message.length() != 0) {
                        notification_text.append(" : ").append(message);
                    }
                    //TODO: add setInvalid
                    AlarmRegistry.getInstance().setFailed(ctx, alarm_id);
                    break;
                default:
                    Log.w(MainActivity.LOG_TAG, String.format("Unknown response code %s", code.toString()));//NON-NLS
            }

            AlarmDoneNotification.notify(ctx, code, notification_text.toString(), 0);
        }
    }


}
