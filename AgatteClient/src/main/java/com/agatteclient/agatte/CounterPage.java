package com.agatteclient.agatte;

/**
 * Data found on the counters page. Value may be interpreted as for year or week
 * depending on request
 *
 * Created by Rémi Pannequin on 15/04/14.
 */

public class CounterPage {
    public boolean anomaly;
    public int contract_year;
    public int contract;
    public float value;

    CounterPage(boolean anomaly, int contract_year, int contract, float value) {
        this.anomaly = anomaly;
        this.contract_year = contract_year;
        this.contract = contract;
        this.value = value;
    }
}
