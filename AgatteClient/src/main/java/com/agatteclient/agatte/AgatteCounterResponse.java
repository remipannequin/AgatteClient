package com.agatteclient.agatte;

/**
 * Created by Rémi Pannequin on 12/04/14.
 */
public class AgatteCounterResponse {

    public enum Type {Year, Week}

    ;

    private boolean anomaly;
    private int contract_year;
    private int contract;

    private Type query_type;
    int queried_year;
    int queried_week;

    // TODO: value, and such

    public AgatteCounterResponse(boolean anomaly, int contract_year, int contract) {
        this.anomaly = anomaly;
        this.contract_year = contract_year;
        this.contract = contract;
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
}
