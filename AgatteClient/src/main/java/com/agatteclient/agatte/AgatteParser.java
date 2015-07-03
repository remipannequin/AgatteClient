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

import android.util.Base64;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

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
    private static final String PATTERN_COUNTER_NUM_CONTRACT = "<select.*id=\"numCont\".*<option value=\"([0-9]+)\".*selected>";
    private static final String PATTERN_COUNTER_YEAR_CONTRACT = "<select.*id=\"codAnu\".*<option value=\"(20[0-9][0-9])\".*selected>";
    private static final String PATTERN_COUNTER_ERROR = "<div class=\"error\">Compteurs non disponibles</div>";
    private static final String PATTERN_COUNTER_VALUE = "Avance / Retard pour la p.riode</span><span class=\"valCptWeb\".*>([ |-]?[0-9]+) h ([0-9]+) min";


    private static final AgatteParser ourInstance = new AgatteParser();
    private final XPathFactory xPathFactory;


    private AgatteParser() {
        xPathFactory = XPathFactory.newInstance();
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
        Collection<String> tops = new ArrayList<>(6);
        Pattern p = Pattern.compile(PATTERN_TOPS);
        Matcher matcher = p.matcher(result);
        while (matcher.find()) {
            tops.add(matcher.group(1));
        }
        return tops;
    }


    private Collection<String> searchForVirtualTops(String result) {
        Collection<String> tops = new ArrayList<>(4);
        Pattern p = Pattern.compile(PATTERN_VIRTUAL_TOPS);
        Matcher matcher = p.matcher(result);
        while (matcher.find()) {
            tops.add(matcher.group(1));
        }
        return tops;
    }


    /**
     * Search for the contract number
     *
     * @param result the HTML page to parse
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
     * @param result  the HTML page to parse
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
     * @param result  the HTML page to parse
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
     * @param result the HTML page to parse
     * @return the value for the counter, or 0, if not found
     */
    private float searchForValue(String result) {
        Pattern p = Pattern.compile(PATTERN_COUNTER_VALUE);
        Matcher matcher = p.matcher(result);
        float h = 0;
        if (matcher.find()) {
            String h_str = matcher.group(1);
            int hour = Integer.valueOf(h_str.trim());
            boolean neg = h_str.startsWith("-");
            h += hour;
            h += (neg ? -1 : 1) * Double.valueOf(matcher.group(2)) / 60;
        }
        return h;
    }

    private String decode(String coded) throws UnsupportedEncodingException {
        byte[] decoded_bytes = Base64.decode(coded.getBytes(), Base64.DEFAULT);
        return new String(decoded_bytes, "UTF-8");
    }

    private String encode(String clear) throws UnsupportedEncodingException {
        byte[] clear_bytes = clear.getBytes();
        return Base64.encodeToString(clear_bytes, Base64.URL_SAFE);
    }


    private AgatteSecret extractSecrets(String result) throws IOException, XPathExpressionException, AgatteException {

        Pattern p_d1 = Pattern.compile("<div id=\"d1\">(.*?)</div>");
        Pattern p_d2 = Pattern.compile("<div id=\"d2\">(.*?)</div>");
        Pattern p_d3 = Pattern.compile("<div id=\"d3\">(.*?)</div>");
        String d1, d2, d3;
        Matcher matcher = p_d1.matcher(result);
        if (matcher.find()) {
            d1 = decode(matcher.group(1));
        } else {
            throw new AgatteException();
        }
        matcher = p_d2.matcher(result);
        if (matcher.find()) {
            d2 = decode(matcher.group(1));
        } else {
            throw new AgatteException();
        }
        matcher = p_d3.matcher(result);
        if (matcher.find()) {
            d3 = decode(matcher.group(1));
        } else {
            throw new AgatteException();
        }

        String xml = String.format("<lol>%s%s<div>%s</lol>", d1, d2, d3);
        XPath xPath = xPathFactory.newXPath();
        String sp0 = xPath.evaluate("//*[@id='sp0']/@class", new InputSource(new StringReader(xml)));
        String sp1 = xPath.evaluate("//*[@id='sp1']/@class", new InputSource(new StringReader(xml)));
        String dv0 = xPath.evaluate("//*[@id='dv0']/@class", new InputSource(new StringReader(xml)));

        String secret_url = decode(sp0);
        String key = decode(sp1);
        String secret = encode(dv0);
        return new AgatteSecret(secret_url, key, secret);
    }


    public AgatteSecret parse_secrets_from_query_response(HttpResponse response) throws IOException, AgatteException {
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new AgatteException(response.getStatusLine().getReasonPhrase());
        }

        //get response as a string
        String result = entityToString(response);
        //search for Unauthorized network
        if (searchNetworkNotAuthorized(result)) {
            throw new AgatteNetworkNotAuthorizedException();
        }

        //search secrets
        try {
            return extractSecrets(result);
        } catch (XPathExpressionException e) {
            throw new AgtSecurityException(String.format("Unable to extract punching password from page: %s", e.getLocalizedMessage()));
        }
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
