package com.agatteclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

public class MainActivity extends Activity {


    protected static final String SERVER_PREF = "server";
    protected static final String LOGIN_PREF = "login";
    protected static final String PASSWD_PREF = "password";
    protected static final String SERVER_DEFAULT = "agatte.univ-lorraine.fr";
    protected static final String LOGIN_DEFAULT = "login";
    protected static final String PASSWD_DEFAULT = "";

    private AgatteSession session;

    private class AgatteQueryTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {
            if (!session.isConnected()) {
                session.login();
            }
            session.query_day();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }

    private class AgatteDoPunchTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            if (!session.isConnected()) {
                session.login();
            }
            session.doPunch();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = preferences.edit();
        if (!preferences.contains(SERVER_PREF)) {
            editor.putString(SERVER_PREF, SERVER_DEFAULT); // value to store
        }
        if (!preferences.contains(LOGIN_PREF)) {
            editor.putString(LOGIN_PREF, LOGIN_DEFAULT); // value to store
        }
        if (!preferences.contains(PASSWD_PREF)) {
            editor.putString(PASSWD_PREF, PASSWD_DEFAULT); // value to store
        }
        editor.commit();

        setContentView(R.layout.activity_main);

        try {
            String server = preferences.getString(SERVER_PREF, SERVER_DEFAULT);
            String login = preferences.getString(LOGIN_PREF, LOGIN_DEFAULT);
            String passwd = preferences.getString(PASSWD_PREF, PASSWD_DEFAULT);
            session = new AgatteSession(server, login, passwd);


        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        preferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                if (s.equals(SERVER_PREF)) {
                    String value = sharedPreferences.getString(s, SERVER_DEFAULT);
                    try {
                        session.setServer(value);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                if (s.equals(LOGIN_PREF)) {
                    String value = sharedPreferences.getString(s, LOGIN_DEFAULT);
                    try {
                        session.setUser(value);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                if (s.equals(PASSWD_PREF)) {
                    String value = sharedPreferences.getString(s, PASSWD_DEFAULT);
                    try {
                        session.setPassword(value);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            }
        });

    }

    public void connect(View view) {
        //Intent i = new Intent("com.agatteclient.AgatteSession");
        //view.getContext().startService(i);
        AgatteQueryTask querytask = new AgatteQueryTask();
        querytask.execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                //display Settings activity
                Intent intent = new Intent(this, AgattePreferenceActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}
