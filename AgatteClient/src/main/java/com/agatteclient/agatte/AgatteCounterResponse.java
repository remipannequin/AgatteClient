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
    private double value;

    private Type query_type;
    int queried_year;
    int queried_week;

    // TODO: value, and such

    public AgatteCounterResponse(boolean anomaly, int contract_year, int contract, double value) {
        this.anomaly = anomaly;
        this.contract_year = contract_year;
        this.contract = contract;
        this.value = value;
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

    public double getValue() {
        return value;
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
