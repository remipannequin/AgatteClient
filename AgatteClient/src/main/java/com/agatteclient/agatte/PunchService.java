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

package com.agatteclient.agatte;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;

import com.agatteclient.MainActivity;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

/**
 * This class create a AgatteSession, try to login, send a punch, and logout.
 * <p/>
 * <p/>
 * <p/>
 * Created by Rémi Pannequin on 29/10/13.
 */
public class PunchService extends IntentService {


    public static final String DO_PUNCH = "punch";
    public static final String QUERY = "query";
    public static final String QUERY_COUNTER = "query_counter";
    public static final String RESULT_RECEIVER = "result_receiver";
    private static final String TAG = "PunchService";
    private AgatteSession session;

    public PunchService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String server = preferences.getString(MainActivity.SERVER_PREF, MainActivity.SERVER_DEFAULT);
        String login = preferences.getString(MainActivity.LOGIN_PREF, MainActivity.LOGIN_DEFAULT);
        String password = preferences.getString(MainActivity.PASSWD_PREF, MainActivity.PASSWD_DEFAULT);
        try {
            session = new AgatteSession(server, login, password);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Object receiver_raw = intent.getParcelableExtra(RESULT_RECEIVER);
        Bundle bundle = new Bundle();
        AgatteResultCode code = AgatteResultCode.exception;
        try {
            if (intent.getAction() == null) {
                throw new AgatteException("Null intent");
            } else if (intent.getAction().equals(DO_PUNCH)) {

                AgatteResponse rsp = session.doPunch();
                bundle = rsp.toBundle();
                code = AgatteResultCode.punch_ok;

            } else if (intent.getAction().equals(QUERY)) {

                AgatteResponse rsp = session.query_day();
                bundle = rsp.toBundle();
                code = AgatteResultCode.query_ok;

            } else if (intent.getAction().equals(QUERY_COUNTER)) {
                AgatteCounterResponse rsp = session.queryCounterCurrent();
                if (rsp.isAvailable()) {
                    bundle = rsp.toBundle();
                    code = AgatteResultCode.query_counter_ok;
                } else {
                    code = AgatteResultCode.query_counter_unavailable;
                }
            } else {
                throw new AgatteException("Unrecognized intent");
            }
        } catch (AgatteLoginFailedException e) {
            code = AgatteResultCode.login_failed;
            bundle = e.toBundle();
        } catch (AgatteNetworkNotAuthorizedException e) {
            code = AgatteResultCode.network_not_authorized;
            bundle = e.toBundle();
        } catch (AgatteException e) {
            code = AgatteResultCode.exception;
            bundle = e.toBundle();
        }


        if (receiver_raw != null && receiver_raw instanceof ResultReceiver) {
            ResultReceiver receiver = (ResultReceiver) receiver_raw;

            receiver.send(code.ordinal(), bundle);
        }
    }
}
