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

package com.agatteclient.agatte;

import android.os.Bundle;

import java.io.Serializable;

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
        return (AgatteCounterResponse) bundle.getSerializable("counter-response"); //NON-NLS
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
        result.putSerializable("counter-response", this); //NON-NLS
        return result;
    }

    public enum Type {Year, Week}

}
