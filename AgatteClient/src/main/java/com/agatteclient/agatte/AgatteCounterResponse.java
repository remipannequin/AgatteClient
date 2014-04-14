package com.agatteclient.agatte;

/**
 * Created by Rémi Pannequin on 12/04/14.
 */
public class AgatteCounterResponse {

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
}
