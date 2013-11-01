package com.agatteclient;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.ParseException;

/**
 * This class create a AgatteSession, try to login, send a punch, and logout.
 *
 *
 *
 * Created by Rémi Pannequin on 29/10/13.
 */
public class PunchService extends IntentService {


    public static final String DO_PUNCH = "punch";
    public static final String QUERY = "query";
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
     *
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Object receiver_raw = intent.getParcelableExtra(RESULT_RECEIVER);
        AgatteResponse rsp;
        if (intent.getAction().equals(DO_PUNCH)) {
            rsp = session.doPunch();
        } else if (intent.getAction().equals(QUERY)) {
            rsp = session.query_day();
        } else {
            //TODO: manage error

            return;
        }



        if (receiver_raw != null && receiver_raw instanceof ResultReceiver) {
            ResultReceiver receiver = (ResultReceiver) receiver_raw;
            receiver.send(rsp.getCode().ordinal(), rsp.toBundle());
        }
    }
}
