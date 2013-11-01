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

package com.agatteclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.IntentService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOError;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 */
public class MainActivity extends Activity {

    static final String SERVER_PREF = "server";
    static final String LOGIN_PREF = "login";
    static final String PASSWD_PREF = "password";
    static final String SERVER_DEFAULT = "agatte.univ-lorraine.fr";
    static final String LOGIN_DEFAULT = "login";
    static final String PASSWD_DEFAULT = "";
    static final String DAY_CARD = "day-card";
    private static final String CONFIRM_PUNCH_PREF = "confirm_punch";
    private static final String PROFILE_PREF = "week_profile";
    protected MenuItem refreshItem = null;
    private ScaleGestureDetector mScaleDetector;
    private AgatteSession session;
    private DayCard cur_card;
    private DayCardView dc_view;
    private UpdateViewTask redraw_task;
    private Timer timer;
    private ProgressBar day_progress;
    private TextView day_textView;
    private AgattePreferenceListener pref_listener;//need to be explicitly declared to avoid garbage collection
    private Button punch_button;
    private ScrollView day_sv;

    /**
     * Save the state of the activity (the DayCard)
     *
     * @param outState outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(DAY_CARD, cur_card);
    }

    /**
     * Restore the state of the activity (the DayCard)
     *
     * @param savedInstanceState saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get old instance or create a new one
        if (savedInstanceState == null) {
            cur_card = new DayCard();
        } else {
            cur_card = (DayCard) savedInstanceState.getSerializable(DAY_CARD);
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
        if (!preferences.contains(CONFIRM_PUNCH_PREF)) {
            editor.putBoolean(CONFIRM_PUNCH_PREF, true); // value to store
        }
        editor.commit();

        mScaleDetector = new ScaleGestureDetector(getApplicationContext(), new ScaleListener());

        setContentView(R.layout.activity_main);

        dc_view = (DayCardView) findViewById(R.id.day_card_view);
        dc_view.setCard(cur_card);

        String profile = preferences.getString(PROFILE_PREF,"1");
        int profile_n = Integer.decode(profile)-1;
        float day_goal = TimeProfile.values()[profile_n].daily_time;

        Resources r = getResources();
        float scale = r.getDisplayMetrics().density;
        day_progress = (ProgressBar) findViewById(R.id.day_progress);
        day_progress.setProgressDrawable(new TimeProgressDrawable(1200, day_goal, 100, scale));
        day_progress.setMax(1200);

        day_textView = (TextView) findViewById(R.id.day_textView);
        punch_button = (Button) findViewById(R.id.button_doPunch);
        day_sv = (ScrollView) findViewById(R.id.day_scrollview);
        //Schedule redraw in 1 minute (60 000 ms)
        timer = new Timer();
        redraw_task = new UpdateViewTask();
        timer.schedule(redraw_task, 0l, 60000l);

        try {
            String server = preferences.getString(SERVER_PREF, SERVER_DEFAULT);
            String login = preferences.getString(LOGIN_PREF, LOGIN_DEFAULT);
            String password = preferences.getString(PASSWD_PREF, PASSWD_DEFAULT);
            session = new AgatteSession(server, login, password);
            pref_listener = new AgattePreferenceListener(session);
            preferences.registerOnSharedPreferenceChangeListener(pref_listener);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        /*/TESTING !
        try {
            //cur_card.addPunch("5:00", true);
            //cur_card.addPunch("7:30", true);
            cur_card.addPunch("8:00");
            cur_card.addPunch("15:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }/*/


        updateCard();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        updateCard();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            //get Y position from dc_view
            int top = dc_view.getFirstPunchY();
            day_sv.scrollTo(0, top);
            super.onWindowFocusChanged(hasFocus);
        }
    }

    /**
     * Update view to reflect the current state of the card
     */
    private void updateCard() {

        if (!cur_card.isCurrentDay()) {
            cur_card = new DayCard();
            dc_view.setCard(cur_card);
        }
        if (cur_card.isEven()) {
            punch_button.setText(R.string.punch_button1);
        } else {
            punch_button.setText(R.string.punch_button2);
        }

        double p = (cur_card.getCorrectedTotalTime());
        StringBuilder sb = new StringBuilder();
        sb.append((int) Math.floor(p)).append("h");
        int min = (int) (p * 60) % 60;
        if (min != 0) {
            sb.append(String.format("%02d", min));
        }
        day_textView.setText(sb.toString());
        day_progress.setIndeterminate(false);
        day_progress.setProgress((int) (p * 100));
        day_progress.invalidate();
        dc_view.invalidate();

        SimpleDateFormat fmt = new SimpleDateFormat("E dd MMM yyyy");
        StringBuilder t = new StringBuilder("Agatte : ").append(fmt.format(cur_card.getDay()));
        setTitle(t);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        return mScaleDetector.onTouchEvent(ev);
    }

    /**
     * Stop refresh image animation
     */
    protected void stopRefresh() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            if (refreshItem != null && refreshItem.getActionView() != null) {
                refreshItem.getActionView().clearAnimation();
                refreshItem.setActionView(null);
            }
        }
    }

    /**
     * Animate the refresh icon
     */
    protected void runRefresh() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    /**
     * Send a punch to the server
     *
     * @param v the current view
     */
    public void doPunch(View v) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean mustConfirm = pref.getBoolean(CONFIRM_PUNCH_PREF, true);
        final Intent i = new Intent(this, PunchService.class);
        i.setAction(PunchService.DO_PUNCH);
        i.putExtra(PunchService.RESULT_RECEIVER, new AgatteResultReceiver());

        if (!mustConfirm) {
            startService(i);
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.dialog_confirm_punch)
                        .setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startService(i);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                builder.show();
            } else {
                DialogFragment confirm = new DialogFragment() {
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {
                        // Use the Builder class for convenient dialog construction
                        Activity activity = getActivity();
                        if (activity != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setMessage(R.string.dialog_confirm_punch)
                                    .setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            startService(i);
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
                };
                confirm.show(getFragmentManager(), "confirm_punch");
            }
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                //display Settings activity
                Intent intent;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    intent = new Intent(this, AgattePreferenceActivity.class);
                } else {
                    intent = new Intent(this, AgattePreferenceActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.action_update:
                refreshItem = item;//menu.getItem(R.id.action_update);
                doUpdate();
                break;
            case R.id.action_about:
                Intent about_intent = new Intent(this, AboutActivity.class);
                startActivity(about_intent);

        }
        return true;
    }

    private void doUpdate() {
        Intent i = new Intent(this, PunchService.class);
        i.setAction(PunchService.QUERY);
        i.putExtra(PunchService.RESULT_RECEIVER, new AgatteResultReceiver());
        startService(i);
        runRefresh();
    }


    private class AgatteResultReceiver extends ResultReceiver {

        /**
         * Create a new ResultReceive to receive results.  Your
         * {@link #onReceiveResult} method will be called from the thread running
         * <var>handler</var> if given, or from an arbitrary thread if null.
         *
         */
        public AgatteResultReceiver() {
            super(new Handler(Looper.getMainLooper()));
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            AgatteResponse rsp = AgatteResponse.fromBundle(resultData);

            //display error (in a Toast)
            StringBuilder toast = new StringBuilder();
            switch (rsp.getCode()) {
                case IOError:
                    toast.append(getString(R.string.punch_error_toast)).append(" ");
                    toast.append(getString(R.string.network_error_toast));
                    if (rsp.hasDetail()) {
                        toast.append(" : ").append(rsp.getDetail());
                    }
                    break;
                case NetworkNotAuthorized:
                    toast.append(getString(R.string.punch_error_toast)).append(" ");
                    toast.append(getString(R.string.unauthorized_network_toast));
                    break;
                case LoginFailed:
                    toast.append(getString(R.string.punch_error_toast)).append(" ");
                    toast.append(getString(R.string.login_failed_toast));
                    break;
                case PunchOK:
                case QueryOK:
                    try {
                        DayCard cur_card = CardBinder.getInstance().getTodayCard();
                        if (rsp.hasVirtualPunches()) {
                            cur_card.addPunches(rsp.getVirtualPunches(), true);
                        }
                        if (rsp.hasPunches()) {
                            cur_card.addPunches(rsp.getPunches(), false);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    toast.append(getString(R.string.punch_ok_toast));
                    break;
                case UnknownError:
                    toast.append(getString(R.string.punch_error_toast)).append(" ");
                    toast.append(getString(R.string.error_toast));
            }
            Context context = getApplicationContext();
            if (context != null) {

                Toast.makeText(context, toast, Toast.LENGTH_LONG).show();

            }


            updateCard();
            stopRefresh();

        }
    }

    /**
     *
     */
    private class UpdateViewTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateCard();
                }
            });
        }
    }

    /**
     *
     */
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

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float mScaleFactor = detector.getScaleFactor();
            float center = detector.getFocusY();
            float delta_scroll = day_sv.getScrollY();
            //scroll view to maintain center
            day_sv.smoothScrollBy(0, (int) ((center + delta_scroll) * (mScaleFactor - 1)));
            dc_view.applyScale(mScaleFactor);
            dc_view.invalidate();
            return true;
        }
    }
}
