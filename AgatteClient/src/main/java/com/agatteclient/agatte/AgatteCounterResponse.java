package com.agatteclient.agatte;

import android.os.Bundle;

import java.io.Serializable;

/**
 * Created by Rémi Pannequin on 12/04/14.
 */
public class AgatteCounterResponse implements Serializable {

    public enum Type {Year, Week}

    private boolean anomaly;
    private int contract_year;
    private int contract;
    private double value_week, value_year;

    private Type query_type;
    int queried_year;
    int queried_week;


    public AgatteCounterResponse(CounterPage page) {
        this.anomaly = page.anomaly;
        this.contract_year = page.contract_year;
        this.contract = page.contract;
    }


    public boolean isAnomaly() {
        return anomaly;
    }


    public int getContractYear() {
        return contract_year;
    }


    public int getContractNumber() {
        return contract;
    }


    public double getValueWeek() {
        return value_week;
    }


    public void setValue(Type type, double value) {
        switch (type) {
            case Week:
                value_week = value;
                break;
            case Year:
                value_year = value;
        }
    }


    public double getValueYear() {
        return value_year;
    }

    public Bundle toBundle() {
        Bundle result = new Bundle();
        result.putSerializable("counter-response", this);
        return result;
    }

    public static AgatteCounterResponse fromBundle(Bundle bundle) {
        return (AgatteCounterResponse) bundle.getSerializable("counter-response");
    }

}
