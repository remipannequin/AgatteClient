package com.agatteclient.agatte;

/**
 * Data found on the counters page. Value may be interpreted as for year or week
 * depending on request
 * <p/>
 * Created by Rémi Pannequin on 15/04/14.
 */

public class CounterPage {
    public final boolean anomaly;
    public final int contract_year;
    public final int contract;
    public final float value;

    CounterPage(boolean anomaly, int contract_year, int contract, float value) {
        this.anomaly = anomaly;
        this.contract_year = contract_year;
        this.contract = contract;
        this.value = value;
    }
}
