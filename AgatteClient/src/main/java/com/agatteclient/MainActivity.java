package com.agatteclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class MainActivity extends Activity {


    static final String SERVER_PREF = "server";
    static final String LOGIN_PREF = "login";
    static final String PASSWD_PREF = "password";
    static final String SERVER_DEFAULT = "agatte.univ-lorraine.fr";
    static final String LOGIN_DEFAULT = "login";
    static final String PASSWD_DEFAULT = "";
    static final String DAY_CARD = "day-card";

    protected MenuItem refreshItem = null;
    private AgatteSession session;
    private DayCard cur_card;
    private DayCardView dc_view;

    private class AgatteQueryTask extends AsyncTask<Void, Void, AgatteResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //start animation
            runRefresh();
        }

        @Override
        protected AgatteResponse doInBackground(Void... voids) {
            return session.query_day();
        }

        @Override
        protected void onPostExecute(AgatteResponse rsp) {
            //check for status
            if (rsp.isError()) {
                //display error (in a Toast)
                StringBuilder toast = new StringBuilder();
                switch (rsp.getCode()) {
                    case IOError:
                        toast.append(getString(R.string.networl_error_toast));
                        if (rsp.hasDetail()) {
                            toast.append(" : ").append(rsp.getDetail());
                        }
                        break;
                    case NetworkNotAuthorized:
                        toast.append(getString(R.string.unauthorized_network_toast));
                        break;
                    case LoginFailed:
                        toast.append(getString(R.string.login_failed_toast));
                        break;
                    case UnknownError:
                        toast.append(getString(R.string.error_toast));
                }
                Context context = getApplicationContext();
                if (context!=null) Toast.makeText(context, toast, Toast.LENGTH_LONG).show();

            }
            if (rsp.hasTops()) {
                for (String top : rsp.getTops()) {
                    try {
                        cur_card.addPunch(top);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                dc_view.invalidate();
            }
            //stop animation, restore button
            stopRefresh();
        }
    }

    private class AgatteDoPunchTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            session.doPunch();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {


        }
    }

    public class ConfirmPunchDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            Activity activity = getActivity();
            if (activity != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(R.string.dialog_confirm_punch)
                        .setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Create Async Task and Send it
                                AgatteDoPunchTask punchTask = new AgatteDoPunchTask();
                                punchTask.execute();

                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                // Create the AlertDialog object and return it
                return builder.create();
            } else {
                return null;
            }
        }
    }

    protected class AgattePreferenceListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        private final AgatteSession session;

        protected AgattePreferenceListener(AgatteSession session) {
            this.session = session;
        }

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
            }
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(DAY_CARD, cur_card);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get old instance or create a new one
        if (savedInstanceState == null) {
            cur_card = new DayCard();
        } else {
            cur_card = (DayCard) savedInstanceState.getSerializable(DAY_CARD);
        }
        //create a new day card if required
        assert cur_card != null;
        if (!cur_card.isCurrentDay()) {
            cur_card = new DayCard();
        }

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


        dc_view = (DayCardView) findViewById(R.id.day_card_view);
        dc_view.setCard(cur_card);
        SimpleDateFormat fmt = new SimpleDateFormat("E dd MMM yyyy");
        StringBuilder t = new StringBuilder("Agatte : ").append(fmt.format(cur_card.getDay()));
        setTitle(t);
        try {
            String server = preferences.getString(SERVER_PREF, SERVER_DEFAULT);
            String login = preferences.getString(LOGIN_PREF, LOGIN_DEFAULT);
            String passwd = preferences.getString(PASSWD_PREF, PASSWD_DEFAULT);
            session = new AgatteSession(server, login, passwd);
            AgattePreferenceListener pref_listener = new AgattePreferenceListener(session);
            preferences.registerOnSharedPreferenceChangeListener(pref_listener);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    protected void stopRefresh() {
        if (refreshItem != null && refreshItem.getActionView() != null) {
            refreshItem.getActionView().clearAnimation();
            refreshItem.setActionView(null);
        }
    }

    protected void runRefresh() {
        if (getApplication() != null) {
            LayoutInflater inflater = (LayoutInflater) getApplication().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ImageView refresh_action_iv = (ImageView) inflater.inflate(R.layout.update_action_view, null);
            Animation rotation = AnimationUtils.loadAnimation(getApplication(), R.anim.clockwise_refresh);
            assert rotation != null;
            assert refresh_action_iv != null;
            rotation.setRepeatCount(Animation.INFINITE);
            /* Attach a rotating ImageView to the refresh item as an ActionView */
            refresh_action_iv.startAnimation(rotation);
            refreshItem.setActionView(refresh_action_iv);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    public void doPunch(View v) {
        DialogFragment confirm = new ConfirmPunchDialogFragment();
        confirm.show(getFragmentManager(), "confirm_punch");
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                //display Settings activity
                Intent intent = new Intent(this, AgattePreferenceActivity.class);
                startActivity(intent);
                break;
            case R.id.action_update:
                AgatteQueryTask queryTask = new AgatteQueryTask();
                refreshItem = item;//menu.getItem(R.id.action_update);
                queryTask.execute();
                break;
        }
        return true;
    }
}
