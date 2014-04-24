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

package com.agatteclient;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.agatteclient.agatte.AgatteCounterResponse;
import com.agatteclient.agatte.AgatteResponse;
import com.agatteclient.agatte.AgatteResultCode;
import com.agatteclient.agatte.AgatteSession;
import com.agatteclient.agatte.PunchService;
import com.agatteclient.alarm.AlarmActivity;
import com.agatteclient.alarm.AlarmBinder;
import com.agatteclient.alarm.AlarmRegistry;
import com.agatteclient.card.CardBinder;
import com.agatteclient.card.DayCard;
import com.agatteclient.card.DayCardView;
import com.agatteclient.card.TimeProfile;
import com.agatteclient.card.TimeProgressDrawable;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    public static final String SERVER_PREF = "server"; //NON-NLS
    public static final String LOGIN_PREF = "login"; //NON-NLS
    public static final String PASSWD_PREF = "password"; //NON-NLS
    public static final String SERVER_DEFAULT = "agatte.univ-lorraine.fr";
    public static final String LOGIN_DEFAULT = "login"; //NON-NLS
    public static final String PASSWD_DEFAULT = "";
    public static final String LOG_TAG = "com.agatteclient"; //NON-NLS
    private static final String CONFIRM_PUNCH_PREF = "confirm_punch"; //NON-NLS
    private static final String AUTO_QUERY_PREF = "auto_query"; //NON-NLS
    private static final String PROFILE_PREF = "week_profile"; //NON-NLS
    private static final String COUNTER_WEEK_PREF = "counter-week"; //NON-NLS
    private static final String COUNTER_YEAR_PREF = "counter-year"; //NON-NLS
    private static final String COUNTER_LAST_UPDATE_PREF = "counter-update"; //NON-NLS
    private static final String DAY_CARD = "day-card"; //NON-NLS

    private MenuItem refreshItem = null;
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
    private TextView week_TextView;
    private TextView year_TextView;
    private TextView anomaly_TextView;

    /**
     * Save the state of the activity (the DayCard)
     *
     * @param outState outState
     */
    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
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
            cur_card = CardBinder.getInstance().getTodayCard();
        } else {
            cur_card = (DayCard) savedInstanceState.getSerializable(DAY_CARD);
        }

        AlarmBinder.getInstance(this);

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
        if (!preferences.contains(AUTO_QUERY_PREF)) {
            editor.putBoolean(AUTO_QUERY_PREF, false); // value to store
        }

        editor.commit();

        mScaleDetector = new ScaleGestureDetector(getApplicationContext(), new ScaleListener());

        setContentView(R.layout.activity_main);

        dc_view = (DayCardView) findViewById(R.id.day_card_view);
        dc_view.setCard(cur_card);

        String profile = preferences.getString(PROFILE_PREF, "1");
        int profile_n = Integer.decode(profile) - 1;
        float day_goal = TimeProfile.values()[profile_n].daily_time;

        Resources r = getResources();
        float scale = r.getDisplayMetrics().density;
        day_progress = (ProgressBar) findViewById(R.id.day_progress);
        day_progress.setProgressDrawable(new TimeProgressDrawable(1200, day_goal, 100, scale));
        day_progress.setMax(1200);

        day_textView = (TextView) findViewById(R.id.day_textView);
        week_TextView = (TextView) findViewById(R.id.week_textView);
        year_TextView = (TextView) findViewById(R.id.year_textView);
        anomaly_TextView = (TextView) findViewById(R.id.anomaly_textView);
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

        updateCard();
        boolean auto_query = preferences.getBoolean(AUTO_QUERY_PREF, true);
        if (auto_query) {
        /* Get last known value in the prefs, and request update if necessary */
            if (!preferences.contains(COUNTER_LAST_UPDATE_PREF)) {
                //TODO: if old value does not exist ?
            }
            int last_update = preferences.getInt(COUNTER_LAST_UPDATE_PREF, -1);
            if (last_update != cur_card.getDayOfYear() + 1000 * cur_card.getYear()) {
                doUpdateCounters();
            } else {
                updateCounter(true,
                        preferences.getFloat(COUNTER_WEEK_PREF, 0),
                        preferences.getFloat(COUNTER_YEAR_PREF, 0));
            }
        }
        //bind to alarm service (update alarms if needed)
        doAlarmUpdate();

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
            super.onWindowFocusChanged(true);
        }
    }

    /**
     * Update view to reflect the current state of the card
     */
    private void updateCard() {

        cur_card = CardBinder.getInstance().getTodayCard();
        if (cur_card.isEven()) {
            punch_button.setText(R.string.punch_button1);
        } else {
            cur_card.applyCorrection();
            punch_button.setText(R.string.punch_button2);
        }

        double p = (cur_card.getCorrectedTotalTime());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(getString(R.string.duration_hour), (int) Math.floor(p)));
        int min = (int) (p * 60) % 60;
        if (min != 0) {
            sb.append(String.format(getString(R.string.duration_minute), min));
        }
        day_textView.setText(sb.toString());
        day_progress.setIndeterminate(false);
        day_progress.setProgress((int) (p * 100));
        day_progress.invalidate();
        dc_view.invalidate();

        SimpleDateFormat fmt = new SimpleDateFormat(getString(R.string.date_format));
        StringBuilder t = new StringBuilder().append(fmt.format(cur_card.getDay()));
        setTitle(t);
    }


    /**
     * Update view with new counter value
     */
    private void updateCounter(boolean available, float week_hours, float global_hours) {

        // if available, record values in persistent storage
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (available) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putFloat(COUNTER_YEAR_PREF, global_hours);
            editor.putFloat(COUNTER_WEEK_PREF, week_hours);
            editor.putInt(COUNTER_LAST_UPDATE_PREF, cur_card.getDayOfYear() + cur_card.getYear() * 1000);
            editor.commit();
        } else {
            week_hours = preferences.getFloat(COUNTER_WEEK_PREF, 0);
            global_hours = preferences.getFloat(COUNTER_YEAR_PREF, 0);
        }

        // if unavailable display in italic (or in red ?)
        if (available) {
            week_TextView.setTypeface(null, Typeface.BOLD);
            year_TextView.setTypeface(null, Typeface.BOLD);
            anomaly_TextView.setText("");
        } else {
            week_TextView.setTypeface(null, Typeface.BOLD_ITALIC);
            year_TextView.setTypeface(null, Typeface.BOLD_ITALIC);
            anomaly_TextView.setText(getString(R.string.anomaly));
        }

        int neg = (week_hours < 0 ? -1 : 1);
        week_hours = week_hours * neg;
        int h = (int) Math.floor(week_hours);
        int m = Math.round((week_hours - h) * 60);
        if (h == 0) {
            week_TextView.setText(String.format(getString(R.string.counter_duration_min), neg * h, m));
        } else {
            week_TextView.setText(String.format(getString(R.string.counter_duration), neg * h, m));
        }
        neg = (global_hours < 0 ? -1 : 1);
        global_hours = global_hours * neg;
        String profile = preferences.getString(PROFILE_PREF, "1");
        int profile_n = Integer.decode(profile) - 1;
        float day_goal = TimeProfile.values()[profile_n].daily_time;
        int half_day = (int) Math.round(global_hours * 2.0 / day_goal);
        year_TextView.setText(String.format(getString(R.string.counter_year), neg * half_day / 2, (half_day % 2 == 11 ? " ½" : "")));
    }


    @Override
    public boolean dispatchTouchEvent(@NotNull MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        return mScaleDetector.onTouchEvent(ev);
    }


    /**
     * Stop refresh image animation
     */
    void stopRefresh() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            if (refreshItem != null && refreshItem.getActionView() != null) {
                refreshItem.getActionView().clearAnimation();
            }
        }
    }


    /**
     * Animate the refresh icon
     */
    void runRefresh() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            if (getApplication() != null) {
                Animation rotation = AnimationUtils.loadAnimation(getApplication(), R.anim.clockwise_refresh);
                rotation.setRepeatCount(Animation.INFINITE);
                /* Attach a rotating ImageView to the refresh item as an ActionView */
                refreshItem.getActionView().startAnimation(rotation);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            final Menu m = menu;
            refreshItem = menu.findItem(R.id.action_update);
            refreshItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    m.performIdentifierAction(refreshItem.getItemId(), 0);
                }
            });
        }
        return true;
    }

    /**
     * Try to update counters
     */
    void doUpdateCounters() {
        final Intent i = new Intent(this, PunchService.class);
        i.setAction(PunchService.QUERY_COUNTER);
        i.putExtra(PunchService.RESULT_RECEIVER, new AgatteResultReceiver());
        startService(i);
    }


    /**
     * Update the alarms Scheduled in the alarm manager.
     */
    private void doAlarmUpdate() {
        AlarmRegistry.getInstance().update(getApplicationContext());
    }


    /**
     * Show confirmation dialog
     *
     * @param i the intent to start if the dialog is confirmed
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void showPunchConfirm(final Intent i) {
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
        confirm.show(getFragmentManager(), "confirm_punch"); //NON-NLS
    }

    /**
     * Show confirmation dialog on older API
     *
     * @param i the intent to start if the dialog is confirmed
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    private void showPunchConfirmOld(final Intent i) {
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
                showPunchConfirmOld(i);
            } else {
                showPunchConfirm(i);
            }
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, @NotNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_settings:
                //display Settings activity

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    intent = new Intent(this, AgattePreferenceActivity.class);
                } else {
                    intent = new Intent(this, AgattePreferenceActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.action_alarm:
                intent = new Intent(this, AlarmActivity.class);
                startActivity(intent);
                break;
            case R.id.action_update:
                refreshItem = item;//menu.getItem(R.id.action_update);
                doUpdate();
                doUpdateCounters();
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
         */
        public AgatteResultReceiver() {
            super(new Handler(Looper.getMainLooper()));
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            //display error (in a Toast)
            StringBuilder toast = new StringBuilder();
            boolean isPunch = false;
            switch (AgatteResultCode.values()[resultCode]) {
                case io_exception:
                    toast.append(getString(R.string.error_toast)).append(" ");
                    toast.append(getString(R.string.network_error_toast));
                    String message = resultData.getString("message"); //NON-NLS
                    if (message.length() != 0) {
                        toast.append(" : ").append(message);
                    }
                    break;
                case network_not_authorized:
                    toast.append(getString(R.string.error_toast)).append(" ");
                    toast.append(getString(R.string.unauthorized_network_toast));
                    break;
                case login_failed:
                    toast.append(getString(R.string.error_toast)).append(" ");
                    toast.append(getString(R.string.login_failed_toast));
                    break;
                case punch_ok:
                    isPunch = true;
                case query_ok:
                    AgatteResponse rsp = AgatteResponse.fromBundle(resultData);
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
                    //Only if it was a punching request
                    if (isPunch) {
                        toast.append(getString(R.string.punch_ok_toast));
                    }
                    break;
                case query_counter_unavailable:
                    toast.append(getString(R.string.counter_unavailable));
                case query_counter_ok:
                    AgatteCounterResponse counter = AgatteCounterResponse.fromBundle(resultData);
                    // update UI with result
                    if (BuildConfig.DEBUG && counter == null)
                        throw new RuntimeException("AgatteCounterResult is null");
                    updateCounter(counter.isAvailable(), counter.getValueWeek(), counter.getValueYear());
                    break;
                case exception:
                    toast.append(getString(R.string.error)).append(" ");
                    toast.append(getString(R.string.error_toast));
            }
            Context context = getApplicationContext();
            if (context != null && toast.length() != 0) {

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
    class AgattePreferenceListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        private final AgatteSession session;

        AgattePreferenceListener(AgatteSession session) {
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
