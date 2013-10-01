package com.agatteclient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;


import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

public class MainActivity extends Activity {

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

    private class AgatteExecTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {
            if (!session.isConnected()) {
                session.login();
            }
            session.exec();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            session = new AgatteSession("agatte.univ-lorraine.fr", "login", "passwd");
            //TODO: get login server and password from app configuration

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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

    private void authenticate() {

    }

}
