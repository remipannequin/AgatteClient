package com.agatteclient;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class record all the (virtual) "punch" on a daily basis.
 * <p/>
 * Created by Rémi Pannequin on 02/10/13.
 */
public class DayCard {
    Set<Date> punches;
    Date day;

    public DayCard() {
        this.punches = new HashSet<Date>();
        long ctm = System.currentTimeMillis();
        this.day = new Date();
        this.day.setTime(ctm);

    }

    public void addPunch(String time) throws ParseException {
        Date date;
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        date = df.parse(time);
        //TODO : check that punches is empty or las punch is before date
        this.punches.add(date);

    }

    public int getNumberOfPunches() {
        return punches.size();
    }

    public boolean isEvent() {
        return ((getNumberOfPunches() % 2) == 0);
    }

    public boolean isOdd() {
        return ((getNumberOfPunches() % 2) == 1);
    }

    /**
     * Compute the total worked time between punches (if event) or between punches and now (if odd)
     *
     * @return the total time in decimal hours
     */
    public double getTotalTime() {
        double result = 0.;
        if (getNumberOfPunches() != 0) {
            List<Date> sorted = new ArrayList<Date>(punches.size() + 1);
            sorted.addAll(punches);

            if (isOdd()) {
                Date now = new Date(System.currentTimeMillis());
                sorted.add(now);
            }
            Collections.sort(sorted);

            for (int i = 0; i < sorted.size() / 2; i++) {
                Date ti = sorted.get(i);
                Date tf = sorted.get(i + 1);
                double delta_h = (tf.getTime() - ti.getTime()) / (1000 * 60 * 60);
                result += delta_h;
            }
        }
        return result;
    }

}
