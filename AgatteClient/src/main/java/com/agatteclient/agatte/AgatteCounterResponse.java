package com.agatteclient.agatte;

import android.os.Bundle;

import java.io.Serializable;

/**
 * This file is part of AgatteClient
 * <p/>
 * Created by Rémi Pannequin on 12/04/14.
 */
public class AgatteCounterResponse implements Serializable {

    private final boolean anomaly;
    private final int contract_year;
    private final int contract;
    int queried_year;
    int queried_week;
    private float value_week, value_year;
    private Type query_type;

    public AgatteCounterResponse(CounterPage page) {
        this.anomaly = page.anomaly;
        this.contract_year = page.contract_year;
        this.contract = page.contract;
    }

    public static AgatteCounterResponse fromBundle(Bundle bundle) {
        return (AgatteCounterResponse) bundle.getSerializable("counter-response");
    }

    public boolean isAvailable() {
        return !anomaly;
    }


    public int getContractYear() {
        return contract_year;
    }


    public int getContractNumber() {
        return contract;
    }


    public float getValueWeek() {
        return value_week;
    }


    public void setValue(Type type, float value) {
        switch (type) {
            case Week:
                value_week = value;
                break;
            case Year:
                value_year = value;
        }
    }


    public float getValueYear() {
        return value_year;
    }

    public Bundle toBundle() {
        Bundle result = new Bundle();
        result.putSerializable("counter-response", this);
        return result;
    }

    public enum Type {Year, Week}

}
