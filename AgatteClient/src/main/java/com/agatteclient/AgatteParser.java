package com.agatteclient;

import android.preference.PreferenceActivity;

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
 * Singleton calss to parse response from the Agatte Server
 * <p/>
 * Created by remi on 03/10/13.
 */
public class AgatteParser {

    private static final String PATTERN_TOPS = ".*<li.*([0-9][0-9]:[0-9][0-9])\\s*</li>.*";
    private static final String PATTERN_NETWORK_NOT_AUTHORIZED = "<legend>Acc\ufffds interdit</legend>";
    private static AgatteParser ourInstance = new AgatteParser();

    public static AgatteParser getInstance() {
        return ourInstance;
    }

    private AgatteParser() {
    }

    private String entitytoString(HttpResponse response) throws IOException {
        InputStreamReader reader = new InputStreamReader(response.getEntity().getContent());
        BufferedReader rd = new BufferedReader(reader);
        StringBuffer result = new StringBuffer();
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

    public AgatteResponse parse_query_response(HttpResponse response) throws IOException {

        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            return new AgatteResponse(AgatteResponse.Code.UnknownError, response.getStatusLine().getReasonPhrase());
        }

        //get response as a string
        String result = entitytoString(response);
        //search for Unauthorized network
        if (searchNetworkNotAuthorized(result)) {
            return new AgatteResponse(AgatteResponse.Code.NetworkNotAuthorized);
        }
        //get tops
        Collection<String> tops = searchForTops(result);
        return new AgatteResponse(AgatteResponse.Code.QueryOK, tops);
    }

    public AgatteResponse.Code parse_punch_response(HttpResponse response) {

        //verify that response is a redirection to topOk.htm (ie punchOkDir)
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY) {
            boolean found = false;
            for (Header h : response.getHeaders("Location")) {
                if (h.getValue().contains(AgatteSession.PUNCH_OK_DIR)) {
                    found = true;
                }
            }
            if (found) {
                return AgatteResponse.Code.TemporaryOK;
            }
        }
        return AgatteResponse.Code.UnknownError;
    }

/*
    public AgatteResponse parse_topok_response(HttpResponse response) throws IOException {



        Pattern p = Pattern.compile("<p>Top pris en compte à [0-9][0-9]:[0-9}][0-9]</p>";


    }
*/

}
