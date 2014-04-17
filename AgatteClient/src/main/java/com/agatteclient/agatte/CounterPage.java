package com.agatteclient.agatte;

/**
 * Created by Rémi Pannequin on 15/04/14.
 */

class CounterPage {
    boolean anomaly;
    int contract_year;
    int contract;
    double value;

    CounterPage(boolean anomaly, int contract_year, int contract, double value) {
        this.anomaly = anomaly;
        this.contract_year = contract_year;
        this.contract = contract;
        this.value = value;
    }
}
