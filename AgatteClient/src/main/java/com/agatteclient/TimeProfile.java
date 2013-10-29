package com.agatteclient;

/**
 * Created by remi on 29/10/13.
 */
public enum TimeProfile {

    week_38h10(7.66666f),
    week_37h30(7.5f),
    week_36h40(7.33333f);
    public float daily_time;

    TimeProfile(float daily_time) {
        this.daily_time = daily_time;
    }
}
