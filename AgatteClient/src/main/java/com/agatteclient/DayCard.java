package com.agatteclient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * This class record all the (virtual) "punch" on a daily basis.
 * <p/>
 * Created by Rémi Pannequin on 02/10/13.
 */
public class DayCard {
    final List<Date> punches;

    final int day;
    final int year;
    Date last;

    /**
     * Crete a new instance of a DayCard with the current day and year
     */
    public DayCard() {
        this.punches = new ArrayList<Date>(12);
        Calendar cal = Calendar.getInstance();
        Date now = new Date(System.currentTimeMillis());
        cal.setTime(now);
        this.year = cal.get(Calendar.YEAR);
        this.day = cal.get(Calendar.DAY_OF_YEAR);
        this.last  = null;
    }

    /**
     * Create a new instance of a DayCard with the specified year and day (of year)
     *
     * @param day
     * @param year
     */
    public DayCard(int day, int year) {
        this.punches = new ArrayList<Date>(12);
        this.year = year;
        this.day = day;
        this.last  = null;
    }


    public void addPunch(String time) throws ParseException {
        Date date;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        date = df.parse(time);
        cal.setTime(date);
        cal.set(Calendar.YEAR, this.year);
        cal.set(Calendar.DAY_OF_YEAR, this.day);
        date = cal.getTime();
        //verify that date is after last punch
        if (this.last == null){
            this.last = date;
            this.punches.add(date);
        } else if (this.last.before(date)) {
            this.punches.add(date);
        }
    }

    public int getNumberOfPunches() {
        return punches.size();
    }

    public boolean isEven() {
        return ((getNumberOfPunches() % 2) == 0);
    }

    public boolean isOdd() {
        return ((getNumberOfPunches() % 2) == 1);
    }

    public boolean isCurrentDay() {
        return isInCardDay(new Date(System.currentTimeMillis()));
    }

    public Date getDay() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_YEAR, day);
        cal.set(Calendar.YEAR, year);
        return cal.getTime();
    }

    public boolean isInCardDay(Date to_test) {
        if (getNumberOfPunches() == 0) {
            return true;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(to_test);
        int test_y = cal.get(Calendar.YEAR);
        int test_d = cal.get(Calendar.DAY_OF_YEAR);

        return (test_d == this.day) && (test_y == this.year);
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
                Date ti = sorted.get(2 * i);
                Date tf = sorted.get(2 * i + 1);
                double delta_h = ((double) (tf.getTime() - ti.getTime())) / (1000.0 * 60.0 * 60.0);
                result += delta_h;
            }
        }
        return result;
    }

    public Collection<Date> getPunches() {
        return punches;
    }

}
