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
        //Do the punch by calling the punching service
        final Intent i = new Intent(context, PunchService.class);
        i.setAction(PunchService.DO_PUNCH);
        i.putExtra(PunchService.RESULT_RECEIVER, new PunchResultReceiver(context));
        context.startService(i);
        wl.release();
    }


    private class PunchResultReceiver extends ResultReceiver {

        private final Context ctx;

        public PunchResultReceiver(Context ctx) {
            super(new Handler(Looper.getMainLooper()));
            this.ctx = ctx;
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            AgatteResultCode code = AgatteResultCode.values()[resultCode];
            //set notification text and title based on result
            StringBuilder notification_text = new StringBuilder();

            switch (code) {
                case network_not_authorized:
                    notification_text.append(ctx.getString(R.string.unauthorized_network_toast));
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
                    break;
                case login_failed:
                    notification_text.append(ctx.getString(R.string.login_failed_toast));
                    break;
                case io_exception:
                    notification_text.append(ctx.getString(R.string.error));
                    message = resultData.getString("message"); //NON-NLS
                    if (message != null && message.length() != 0) {
                        notification_text.append(" : ").append(message);
                    }
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
                    break;
                default:


            }
            AlarmDoneNotification.notify(ctx, code, notification_text.toString(), 0);


            //Update the AlarmRegistry: re-schedule next event
            AlarmRegistry.getInstance().update(ctx);
        }
    }


}
