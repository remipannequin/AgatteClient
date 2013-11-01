/*This file is part of AgatteClient.

    AgatteClient is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    AgatteClient is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with AgatteClient.  If not, see <http://www.gnu.org/licenses/>.*/

package com.agatteclient;

import android.os.Bundle;

import java.util.Collection;

/**
 * Created by Rémi Pannequin on 06/10/13.
 */
public class AgatteResponse {


    private Code code;
    private String detail;
    private String[] punches;
    private String[] virtual_punches;
    public AgatteResponse(Code code) {
        this.code = code;
        this.detail = null;

    }

    private AgatteResponse() {
    }

    public AgatteResponse(Code code, Collection<String> punches) {
        this(code);
        this.punches = punches.toArray(new String[punches.size()]);
    }

    public AgatteResponse(Code code, Collection<String> punches, Collection<String> virtual_punches) {
        this(code, punches);
        this.virtual_punches = virtual_punches.toArray(new String[punches.size()]);
    }

    public AgatteResponse(Code code, Exception cause) {
        this(code);
        this.virtual_punches = new String[0];
        if (cause.getCause() != null) {
            this.detail = cause.getCause().getLocalizedMessage();
        } else {
            this.detail = cause.getLocalizedMessage();
        }
    }

    public AgatteResponse(Code code, String s) {
        this(code);
        this.virtual_punches = new String[0];
        this.detail = s;
    }

    public Code getCode() {
        return code;
    }

    public String[] getPunches() {
        return punches;
    }

    public String[] getVirtualPunches() {
        return virtual_punches;
    }

    public String getDetail() {
        return detail;
    }

    public boolean isError() {
        //true for IOError, login failed, NetworkNotauthorized
        return code.isError();
    }

    public boolean hasPunches() {
        //true for 'OK' types
        return code.hasTops();
    }

    public boolean hasVirtualPunches() {
        //true for 'OK' types
        return (virtual_punches.length != 0);
    }

    public boolean hasDetail() {
        //true if isError
        return (detail != null);
    }



    public Bundle toBundle() {
        Bundle result = new Bundle();
        result.putInt("code", this.code.ordinal());
        result.putString("detail", this.detail);
        result.putStringArray("punches", this.punches);
        result.putStringArray("virtual_punches", this.virtual_punches);
        return result;
    }

    public static AgatteResponse fromBundle(Bundle bundle){
        AgatteResponse instance = new AgatteResponse();
        instance.code = Code.values()[bundle.getInt("code")];
        instance.detail = bundle.getString("detail");
        instance.punches = bundle.getStringArray("punches");
        instance.virtual_punches = bundle.getStringArray("virtual_punches");
        return instance;
    }


    public enum Code {
        IOError(true),//IOError happened
        LoginFailed(true),//Login into server failed
        NetworkNotAuthorized(true),//the server refused to give the data because the network is not authorized
        TemporaryOK(false),//To be used in intermediate responses : so far, transaction went OK
        QueryOK(false),//Query returned a valid result
        PunchOK(false),//Punch action returned a valid result
        UnknownError(true);
        private boolean isErr;

        Code(boolean err) {
            isErr = err;
        }

        public boolean isError() {
            //true for IOError, login failed, NetworkNotauthorized
            return isErr;
        }

        public boolean hasTops() {
            //true for 'OK' types
            return !isErr;
        }
    }

}
