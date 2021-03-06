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

package com.agatteclient.card;

import android.util.Pair;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


/**
 * This class record all the (virtual) "punch" on a daily basis.
 * <p/>
 * Created by Rémi Pannequin on 02/10/13.
 */
public class DayCard implements Serializable {

    //All these in milliseconds
    public static final int NOON_PAUSE_MIN = 45 * 60 * 1000;
    public static final int NOON_PAUSE_DEFAULT = 150 * 60 * 1000;
    public static final int MORNING_START_MS = 9 * 60 * 60 * 1000;
    public static final float MORNING_START = 9;
    public static final int MORNING_END_MS = (11 * 60 + 30) * 60 * 1000;
    public static final float MORNING_END = 11.5f;
    public static final int AFTERNOON_START_MS = 14 * 60 * 60 * 1000;
    public static final float AFTERNOON_START = 14;
    public static final int AFTERNOON_END_MS = 16 * 60 * 60 * 1000;
    public static final float AFTERNOON_END = 16;
    public static final int DAY_END = 19 * 60 * 60 * 1000;
    public static final String HH_MM_FORMAT = "HH:mm";//NON-NLS
    private final List<Long> punches;
    private final List<Long> corrected_punches;
    private final int day;
    private final int year;
    private final long start;
    private final List<Long> virtual_punches;

    /**
     * Crete a new instance of a DayCard with the current day and year
     */
    public DayCard() {
        this.punches = new ArrayList<Long>(12);
        this.corrected_punches = new ArrayList<Long>(12);
        this.virtual_punches = new ArrayList<Long>(4);
        Calendar cal = Calendar.getInstance();
        Date now = new Date(System.currentTimeMillis());
        cal.setTime(now);
        this.year = cal.get(Calendar.YEAR);
        this.day = cal.get(Calendar.DAY_OF_YEAR);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        this.start = cal.getTimeInMillis();
    }

    /**
     * Create a new instance of a DayCard with the specified year and day (of year)
     *
     * @param day  the day of year (i.e. from 1 to 365)
     * @param year the year
     */
    public DayCard(int day, int year) {
        Calendar cal = Calendar.getInstance();
        this.punches = new ArrayList<Long>(12);
        this.corrected_punches = new ArrayList<Long>(12);
        this.virtual_punches = new ArrayList<Long>(4);
        this.year = year;
        this.day = day;
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_YEAR, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        this.start = cal.getTimeInMillis();
    }

    /**
     * Add an array of punches to the card
     * <p/>
     * If a punch is already present in the card, it won't be added twice.
     * <p/>
     * If a correction must be applied, this method will manage it.
     *
     * @param punches the dates to add
     * @param virtual if true the punches added are virtual ones
     */
    public void addPunches(String[] punches, boolean virtual) throws ParseException {
        boolean need_correction = false;
        for (String punch : punches) {
            boolean r = addPunchRaw(punch, virtual);
            need_correction = need_correction || r;
        }
        if (need_correction) {
            applyCorrection(now());
        }
    }

    /**
     * Add some punch in the card.
     * <p/>
     * If a punch is already present in the card, it won't be added twice.
     * <p/>
     * If a correction must be applied, this method will manage it.
     * <p/>
     * The punch is assumed to be a real one
     *
     * @param time the date to add
     * @throws ParseException
     */
    public void addPunch(String time) throws ParseException {
        addPunch(time, false);
    }

    /**
     * @param time    the date to add
     * @param virtual if true the punches added are virtual ones
     * @throws ParseException
     */
    public void addPunch(String time, boolean virtual) throws ParseException {
        if (addPunchRaw(time, virtual)) {
            applyCorrection(now());
        }
    }

    /**
     * Add Punching data to the card
     *
     * @param time    the date to add
     * @param virtual if true the punches added are virtual ones
     * @return true if some correction need to be applied
     * @throws ParseException
     */
    private boolean addPunchRaw(String time, boolean virtual) throws ParseException {
        //1. Parse date
        Date date;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(HH_MM_FORMAT);
        date = df.parse(time);
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.YEAR, this.year);
        cal.set(Calendar.DAY_OF_YEAR, this.day);
        date = cal.getTime();
        Long date_l = date.getTime();

        //2a. Check existence, add, and sort (real punch)
        if (!virtual) {
            if (!this.punches.contains(date_l)) {
                this.punches.add(date_l);
                Collections.sort(this.punches);
            } else {
                //no need to apply correction
                return false;
            }
            //2b. Check existence, add, and sort (virtual punch)
        } else {
            if (!this.virtual_punches.contains(date_l)) {
                this.virtual_punches.add(date_l);
                Collections.sort(this.virtual_punches);
            }
            //Don't compute corrections for virtual punches
            return false;
        }
        return true;
    }

    /**
     * Apply work time corrections
     */
    public void applyCorrection() {
        applyCorrection(now());
    }

    /**
     * Apply work time corrections
     */
    public void applyCorrection(Date now) {
        corrected_punches.clear();
        //compute non worked time at mid-day
        long l1 = start + MORNING_END_MS;
        long l2 = start + AFTERNOON_START_MS;
        long l3 = start + DAY_END;
        long ti = start;//Doesn't matter
        long tf = start;
        int last_noon_tf = 0;
        int i = 0;
        long noon = l2 - l1;
        boolean open = true;
        boolean has_noon = true;

        //If day is odd, append now to the end of the list of punches
        List<Long> punches_with_now;
        if (isOdd()) {
            punches_with_now = new ArrayList<Long>(punches);
            punches_with_now.add(now.getTime());
        } else {
            punches_with_now = punches;
        }
        for (long t : punches_with_now) {
            if (open) {
                ti = t;
            } else {
                tf = t;
            }

            //e.g. 9h 12h 12h30 ??
            if (!open && tf > l1 && ti < l2) {
                if (ti < l1 && tf > l2) {
                    //ti before 11h, tf after 14h
                    noon = 0;
                    has_noon = false;
                } else if (ti < l1 && tf < l2) {
                    //ti before 11h, tf before 14h
                    noon -= tf - l1;
                } else if (ti > l1 && tf < l2) {
                    //ti and tf between 11h and 14h
                    noon -= tf - ti;
                } else {
                    //ti after 11h, tf after 14h
                    noon -= l2 - ti;
                }
                last_noon_tf = i - 1;
            }
            // Limit at 19h
            if (tf > l3) {
                this.corrected_punches.add(tf);
            }
            if (ti > l3) {
                this.corrected_punches.add(ti);
            }

            open = !open;
            i++;
        }
        //apply correction
        if (has_noon && noon < NOON_PAUSE_MIN) {
            long t = this.punches.get(last_noon_tf);
            this.corrected_punches.add(t);
            this.corrected_punches.add(t + NOON_PAUSE_MIN - noon);

        }
        if (!has_noon) {
            //add 150min if there is no midday pause
            this.corrected_punches.add(l1 + NOON_PAUSE_DEFAULT);
            this.corrected_punches.add(l2 - NOON_PAUSE_DEFAULT);
        }
    }

    /**
     * @return the number of virtual punches of the day
     */
    public int getNumberOfVirtualPunches() {
        return virtual_punches.size();
    }

    /**
     * @return the number of (real) punches of the day
     */
    public int getNumberOfPunches() {
        return punches.size();
    }

    /**
     * @return return true if the number of punch is even
     */
    public boolean isEven() {
        return ((getNumberOfPunches() % 2) == 0);
    }

    /**
     * @return true if the number of punch is odd
     */
    public boolean isOdd() {
        return ((getNumberOfPunches() % 2) == 1);
    }

    /**
     * @return true if the instance correspond to the current date according to system date
     */
    public boolean isCurrentDay() {
        return isInCardDay(now());
    }

    /**
     * @param now the date representing the current time
     * @return true if the card is in the same day as "now"
     */
    public boolean isCurrentDay(Date now) {
        return isInCardDay(now);
    }


    /**
     * @return the day of year of the card
     */
    public Date getDay() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_YEAR, day);
        cal.set(Calendar.YEAR, year);
        return cal.getTime();
    }


    /**
     * @return the number of the day in the current year of the card (1-366)
     */
    public int getDayOfYear() {
        return day;
    }


    /**
     * @return the year of the card
     */
    public int getYear() {
        return year;
    }


    /**
     * @param to_test the date to test
     * @return true if the date To_test is inside the day of the card
     */
    boolean isInCardDay(Date to_test) {
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
        return getTotalTime(now());
    }

    /**
     * Compute the total worked time between punches (if event) or between punches and now (if odd)
     *
     * @param now the date representing the current time
     * @return the total time computed
     */
    public double getTotalTime(Date now) {
        double result = 0.;
        Long ti, tf;
        Iterator<Long> iterator;
        List[] l;

        //Iterate on virtual and real punches
        //ensure sanity
        if ((getNumberOfVirtualPunches() % 2) == 0) {
            l = new List[]{virtual_punches, punches};
        } else {
            l = new List[]{punches};
        }
        for (int i = 0; i < 2; i++) {

            iterator = (Iterator<Long>) l[i].iterator();
            while (iterator.hasNext()) {
                ti = iterator.next();
                if (iterator.hasNext()) {
                    tf = iterator.next();
                } else {
                    tf = now.getTime();

                }
                double delta_h = ((double) (tf - ti)) / (1000.0 * 60.0 * 60.0);
                result += delta_h;
            }
        }
        return result;
    }


    /**
     * Return the total time, with correction such as min. 45min at noon, at most 10 hours per day...
     *
     * @return the time, in hours
     */
    public double getCorrectedTotalTime() {
        return getCorrectedTotalTime(now());
    }

    /**
     * @param now the current time
     * @return the worked time in hours
     */
    public double getCorrectedTotalTime(Date now) {
        double correction = 0.;
        Long ti, tf;

        Iterator<Long> iterator = corrected_punches.iterator();
        while (iterator.hasNext()) {
            ti = iterator.next();
            if (iterator.hasNext()) {
                tf = iterator.next();
            } else {
                tf = now.getTime();
            }
            double delta_h = ((double) (tf - ti)) / (1000.0 * 60.0 * 60.0);
            correction += delta_h;
        }
        return getTotalTime(now) - correction;
    }

    /**
     * @return the array of (real)  punches of the card
     */
    public Date[] getPunches() {
        Date[] result = new Date[this.punches.size()];
        int i = 0;
        for (Long date_l : this.punches) {
            result[i++] = new Date(date_l);
        }
        return result;
    }

    /**
     * @return the array of virtual punches of the card
     */
    public Date[] getVirtualPunches() {
        Date[] result = new Date[this.virtual_punches.size()];
        int i = 0;
        for (Long date_l : this.virtual_punches) {
            result[i++] = new Date(date_l);
        }
        return result;
    }

    /**
     * @return the date corresponding to the system time
     */
    public Date now() {
        return new Date(System.currentTimeMillis());
    }

    /**
     * Get the array of Pair of Date defining periods when time has been punched in, but as been excluded
     *
     * @return the array of Pair (di, df) with di < df
     */
    public Pair<Date, Date>[] getCorrectedPunches() {
        int size = this.corrected_punches.size();
        Pair<Date, Date>[] result = (Pair<Date, Date>[]) new Pair[size / 2];
        Date di, df;
        for (int i = 0; i < size; i += 2) {
            di = new Date(this.corrected_punches.get(i));
            df = new Date(this.corrected_punches.get(i + 1));
            result[i++] = new Pair<Date, Date>(di, df);
        }
        return result;
    }

    /**
     * Return an Array of all the punches of the card (real and virtual) and now if the day is odd
     *
     * @return the array of punches (possibly unsorted)
     */
    public Date[] getAllPunches() {
        int s = this.punches.size() + this.virtual_punches.size();
        Date[] result = new Date[s + s % 2];
        int i = 0;
        for (Long date_l : this.virtual_punches) {
            result[i++] = new Date(date_l);
        }
        for (Long date_l : this.punches) {
            result[i++] = new Date(date_l);
        }
        if (isOdd()) {
            result[i] = now();
        }
        return result;


    }

    /**
     * Return the first punch of the card or null if there is no punches in the card
     *
     * @return the date of the first punch of the day
     */
    public Date getFirstPunch() {
        boolean empty = (this.punches.size() == 0);
        boolean empty_virtual = (this.virtual_punches.size() == 0);

        if (!empty & !empty_virtual) {
            long l = Math.min(this.virtual_punches.get(0), this.punches.get(0));
            return new Date(l);
        } else if (empty & !empty_virtual) {
            return new Date(this.virtual_punches.get(0));
        } else if (!empty & empty_virtual) {
            return new Date(this.punches.get(0));
        } else {
            return null;
        }
    }
}