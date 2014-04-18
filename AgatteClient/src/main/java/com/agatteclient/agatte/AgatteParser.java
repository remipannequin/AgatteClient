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

package com.agatteclient.agatte;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Singleton class to parse response from the Agatte Server
 * <p/>
 * Created by Rémi Pannequin on 03/10/13.
 */
@SuppressWarnings("HardCodedStringLiteral")
public class AgatteParser {

    private static final String PATTERN_TOPS = ".*<li.*Top r.el.*([0-9][0-9]:[0-9][0-9])\\s*</li>.*";
    private static final String PATTERN_VIRTUAL_TOPS = ".*<li.*Tops d\'absence.*([0-9][0-9]:[0-9][0-9])\\s*</li>.*";
    private static final String PATTERN_NETWORK_NOT_AUTHORIZED = "<legend>Acc\ufffds interdit</legend>";
    private static final String PATTERN_TOP_OK = "<p>Top pris en compte . ([0-9][0-9]:[0-9}][0-9])</p>";
    private static final String PATTERN_COUNTER_NUM_CONTRACT = "<select.*id=\"numCont\".*<option value=\"([0-9]+)\".*selected>";
    private static final String PATTERN_COUNTER_YEAR_CONTRACT = "<select.*id=\"codAnu\".*<option value=\"(20[0-9][0-9])\".*selected>";
    private static final String PATTERN_COUNTER_ERROR = "<div class=\"error\">Compteurs non disponibles</div>";
    private static final String PATTERN_COUNTER_VALUE = "Avance / Retard pour la p.riode</span><span class=\"valCptWeb\".*>([ |-]?[0-9]+) h ([0-9]+) min";


    private static final AgatteParser ourInstance = new AgatteParser();


    private AgatteParser() {
    }


    public static AgatteParser getInstance() {
        return ourInstance;
    }


    private String entityToString(HttpResponse response) throws IOException {
        InputStreamReader reader = new InputStreamReader(response.getEntity().getContent());
        BufferedReader rd = new BufferedReader(reader);
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line.replace("[\\n\\r\\t]", " "));
            if (line.contains("</")) {
                result.append(System.getProperty("line.separator"));
            }
        }
        return result.toString();
    }


    private boolean searchNetworkNotAuthorized(String result) {
        Pattern unauthorized = Pattern.compile(PATTERN_NETWORK_NOT_AUTHORIZED);
        Matcher matcher = unauthorized.matcher(result);
        return matcher.find();
    }


    private Collection<String> searchForTops(String result) {
        Collection<String> tops = new ArrayList<String>(6);
        Pattern p = Pattern.compile(PATTERN_TOPS);
        Matcher matcher = p.matcher(result);
        while (matcher.find()) {
            tops.add(matcher.group(1));
        }
        return tops;
    }


    private Collection<String> searchForVirtualTops(String result) {
        Collection<String> tops = new ArrayList<String>(4);
        Pattern p = Pattern.compile(PATTERN_VIRTUAL_TOPS);
        Matcher matcher = p.matcher(result);
        while (matcher.find()) {
            tops.add(matcher.group(1));
        }
        return tops;
    }


    private boolean searchForTopOk(String result) {
        Pattern p = Pattern.compile(PATTERN_TOP_OK);
        Matcher matcher = p.matcher(result);
        return matcher.find();
    }


    /**
     * Search for the contract number
     *
     * @param result
     * @return the contract number, or -1 if not found
     */
    private int searchForNumContract(String result) {
        Pattern p = Pattern.compile(PATTERN_COUNTER_NUM_CONTRACT);
        Matcher matcher = p.matcher(result);
        int counter = -1;
        if (matcher.find())

        {
            counter = Integer.valueOf(matcher.group(1));
        }
        return counter;
    }


    /**
     * Search if counter are (said to be) unavailable
     *
     * @param result
     * @return true if counter are *not* available
     */
    private boolean searchForCounterUnavailable(String result) {
        Pattern p = Pattern.compile(PATTERN_COUNTER_ERROR);
        Matcher matcher = p.matcher(result);
        return matcher.find();
    }


    /**
     * Search for contract year
     *
     * @param result
     * @return the year, or -1 if not found
     */
    private int searchForYearContract(String result) {
        Pattern p = Pattern.compile(PATTERN_COUNTER_YEAR_CONTRACT);
        Matcher matcher = p.matcher(result);
        int year = -1;
        if (matcher.find())

        {
            year = Integer.valueOf(matcher.group(1));
        }
        return year;
    }


    /**
     * Get the value of the counter (in hours)
     *
     * @param result
     * @return
     */
    private float searchForValue(String result) {
        Pattern p = Pattern.compile(PATTERN_COUNTER_VALUE);
        Matcher matcher = p.matcher(result);
        float h = 0;
        if (matcher.find()) {
            String h_str = matcher.group(1);
            int hour = Integer.valueOf(h_str.trim());
            h += hour;
            h += (hour < 0 ? -1 : 1) * Double.valueOf(matcher.group(2)) / 60;
        }
        return h;
    }


    public AgatteResponse parse_query_response(HttpResponse response) throws IOException, AgatteException {

        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new AgatteException(response.getStatusLine().getReasonPhrase());
        }

        //get response as a string
        String result = entityToString(response);
        //search for Unauthorized network
        if (searchNetworkNotAuthorized(result)) {
            throw new AgatteNetworkNotAuthorizedException();
        }
        //get tops
        Collection<String> tops = searchForTops(result);
        Collection<String> virtual_tops = searchForVirtualTops(result);

        if (virtual_tops.isEmpty()) {
            return new AgatteResponse(tops);
        } else {
            return new AgatteResponse(tops, virtual_tops);
        }
    }


    public boolean parse_punch_response(HttpResponse response) throws IOException, AgatteException {
        response.getEntity().consumeContent();
        //verify that response is a redirection to topOk.htm (ie punchOkDir)
        //This is actually a chain of redirection that should lead there...
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY) {
            for (Header h : response.getHeaders("Location")) {
                if (h.getValue().contains("app/accesInterdit.htm")) {
                    throw new AgatteNetworkNotAuthorizedException();
                }
            }
        }
        //SO, BE LAZY, AND DON'T REALLY TEST
        return true;
    }


    public AgatteResponse parse_topOk_response(HttpResponse response) throws IOException, AgatteException {
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new AgatteException(response.getStatusLine().getReasonPhrase());
        }
        //get response as a string
        String result = entityToString(response);
        //search for Unauthorized network
        if (searchNetworkNotAuthorized(result)) {
            throw new AgatteNetworkNotAuthorizedException();
        }
        if (!searchForTopOk(result)) {
            throw new AgatteException("Unable to find punch ack");
        }
        //get tops
        Collection<String> tops = searchForTops(result);
        Collection<String> virtual_tops = searchForVirtualTops(result);

        if (virtual_tops.isEmpty()) {
            return new AgatteResponse(tops);
        } else {
            return new AgatteResponse(tops, virtual_tops);
        }
    }


    public CounterPage parse_counter_response(HttpResponse response) throws IOException, AgatteException {
        int code = response.getStatusLine().getStatusCode();
        if (code != HttpStatus.SC_OK) {
            throw new AgatteException(response.getStatusLine().getReasonPhrase());
        }
        String result = entityToString(response);
        boolean ano = searchForCounterUnavailable(result);
        int year = searchForYearContract(result);
        int counter = searchForNumContract(result);
        float h = searchForValue(result);
        return new CounterPage(ano, year, counter, h);
    }
}
