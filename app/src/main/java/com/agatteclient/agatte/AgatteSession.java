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


import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.agatteclient.MainActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

/**
 * A session with the Agatte server. It can be used multiple times.
 * <p/>
 * Created by Rémi Pannequin on 24/09/13.
 */
@SuppressWarnings("HardCodedStringLiteral")
public class AgatteSession {

    //public static final int TIMEOUT_CONEECTION = 3000;
    //public static final int TIMEOUT_SO = 5000;
    public static final int SESSION_DELAY = 120000;
    private static final String LOGIN_DIR = "/app/login.form";
    private static final String LOGOUT_DIR = "/app/logout.form";
    private static final String AUTH_DIR = "/j_acegi_security_check";
    private static final String PUNCH_DIR = "/top/location.href";
    private static final String QUERY_DIR = "/";
    private static final String WEEK_COUNTER_DIR = "/top/feuille-top.form";
    private static final String USER = "j_username";
    private static final String PASSWORD = "j_password";
    private static final String COUNTER_CONTRACT_NUMBER = "numCtp";
    private static final String COUNTER_CONTRACT_YEAR = "codeAnu";
    private static final String COUNTER_WEEK = "numSem";
    private static final String COUNTER_TYPE = "nivCpt";
    //private static final String AGENT = "Mozilla/5.0 (Linux; U; Android 2.2; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
    private String user;
    private String passwd;

    private final CookieManager cookieManager;
    private String session_id;
    private String server;
    private long session_expire;

    private URL login_url;
    private URL auth_url;
    private URL logout_url;
    private URL query_day_url;
    private URL query_week_counter_url;
    private URL exec_url;


    /**
     * Create a new session with parameters
     *
     * @param server the hostname of the server
     * @param user   the username to use
     * @param passwd the password to use
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     */
    public AgatteSession(String server, String user, String passwd) throws URISyntaxException, UnsupportedEncodingException, MalformedURLException, NoSuchAlgorithmException, KeyManagementException {
        cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        HttpURLConnection.setFollowRedirects(true);

        this.user = user;
        this.passwd = passwd;
        this.setServer(server);


        //should be done in the constructor ?
        SSLContext sslcontext = SSLContext.getInstance("TLSv1.2");
        sslcontext.init(null, null, null);
        SSLSocketFactory NoSSLv3Factory = new NoSSLv3SocketFactory(sslcontext.getSocketFactory());
        HttpsURLConnection.setDefaultSSLSocketFactory(NoSSLv3Factory);

    }




    /**
     * Send a logout to the server.
     *
     */
    public void logout() {
        try {
            HttpsURLConnection l_connection = (HttpsURLConnection) logout_url.openConnection();
            l_connection.setInstanceFollowRedirects(true);

            l_connection.setRequestMethod("GET");

            l_connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.session_id = null;
    }

    /**
     * Attempt to log in Agatte :ie open an agatte session
     *
     * @return false if user failed, true if no errors were detected
     */
    private boolean login() throws IOException {

        HttpsURLConnection login_connection = (HttpsURLConnection) login_url.openConnection();
        login_connection.setRequestMethod("GET");
        login_connection.connect();

        //HttpResponse response1 = client.execute(login_rq, httpContext);

        //response1.getEntity().consumeContent();
        //test whether response is OK
        if (login_connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            return false;
        }

        boolean found = false;

        for (HttpCookie c : cookieManager.getCookieStore().getCookies()) {
            if (c.getName().equals("JSESSIONID")) {
                found = true;
            }
        }
        if (!found) {
            //session ID not found in response
            return false;
        }

        HttpsURLConnection authentication_connection = (HttpsURLConnection) auth_url.openConnection();
        authentication_connection.setInstanceFollowRedirects(false);
        authentication_connection.setRequestMethod("POST");
        authentication_connection.setDoInput(true);
        authentication_connection.setDoOutput(true);

        Uri.Builder builder = new Uri.Builder()
        .appendQueryParameter(USER, user)
        .appendQueryParameter(PASSWORD, passwd);
        String query = builder.build().getEncodedQuery();
        OutputStream os = authentication_connection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
        writer.write(query);
        writer.flush();
        writer.close();
        os.close();

        authentication_connection.connect();


        //String response = getContent(authentication_connection.getInputStream());


        Map<String,List<String>> headerFields = authentication_connection.getHeaderFields();
        for (String h : headerFields.get("Location")) {
            //value should be URL("https", server, "/");
            if (h.contains("login_error=1")) {
                this.session_expire = 0;
                this.session_id = null;
                return false;
            }
        }
        return true;
    }

    /**
     * Attempt to get the list of tops from the server (doing a user if necessary)
     *
     * @return an AgatteResponse instance
     */
    public AgatteResponse query_day() throws AgatteException {
        try {
            if (loginNotRequired()) {
                if (!login()) {
                    //return new AgatteResponse(AgatteResponse.Code.LoginFailed);
                    throw new AgatteLoginFailedException();
                }
            }

            HttpsURLConnection l_connection = (HttpsURLConnection) query_day_url.openConnection();
            l_connection.setRequestMethod("GET");
            l_connection.connect();

            String response = getContent(l_connection.getInputStream());

            if (l_connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new AgatteException(l_connection.getResponseMessage());
            }
            return AgatteParser.getInstance().parse_query_response(response);
        } catch (IOException e) {
            Log.w(MainActivity.LOG_TAG, "IOException in query_day", e);//NON-NLS
            throw new AgatteException(e);
        }
    }


    /**
     * Extract content of a URLConnexion as a string,
     * and remove all \t \r characters. Leave only \n if there is a
     * closing XML mark in it
     *
     *
     * @param is the inputstream of the URLConnexion
     * @return the content as a String
     * @throws IOException
     */
    private String getContent(InputStream is) throws IOException {
        StringBuilder content = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        String line;

        // read from the urlconnection via the buffered reader
        while ((line = bufferedReader.readLine()) != null) {
            if (line.length() != 0) {
                content.append(line.replaceAll("[\\n|\\t|\\r]", " "));
                if (line.contains("</")) {
                    content.append(System.getProperty("line.separator"));
                }
            }
        }
        bufferedReader.close();
        return content.toString();
    }

    /**
     * Send a "punch" to the server
     * <p>
     * As of june 2015, the new code on the servers requires to to a tree-step request:
     * 1) GET top/top.form, and extract some "secrets" : key in the header, secret URL, and secret
     * 2) POST top/location.href (XMLHttpRequest), and extract a returned key (in the header)
     * 3) POST to the secret URL (which ATM does not change) the secret, while putting the key in the header.
     * <p>
     * The method to hide the values in the HTML is Base64. Which is just as lame as it sounds. But
     * Its encoded twice, for super-extra-security. Good job, IT guys, this was really funny.
     * Wink at Université de Lorrraine devs
     *
     * @return an AgatteResponse instance
     */
    public AgatteResponse doPunch() throws AgatteException {
        return doCheckAndPunch(false, false);
    }

    /**
     * Send a "punch" to the server, only if the number of previous punches is even (resp odd)
     *
     * @param even if true, the number of param must be even, if false, it must be odd
     * @return an AgatteResponse instance
     */
    public AgatteResponse doCheckAndPunch(boolean even) throws AgatteException {
        return doCheckAndPunch(true, even);
    }


    /**
     * Send a "punch" to the server, only if the number of previous punches is even (resp odd)
     *
     * @param check check eveness before punching
     * @param even  if true, the number of param must be even, if false, it must be odd
     * @return an AgatteResponse instance
     */
    public AgatteResponse doCheckAndPunch(boolean check, boolean even) throws AgatteException {

        try {
            if (loginNotRequired()) {
                if (!login()) {
                    //return new AgatteResponse(AgatteResponse.Code.LoginFailed);
                    throw new AgatteLoginFailedException();
                }
            }


            //First query
            HttpsURLConnection l_connection = (HttpsURLConnection) query_day_url.openConnection();
            l_connection.setRequestMethod("GET");
            l_connection.connect();
            if (l_connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new AgatteException(l_connection.getResponseMessage());
            }
            String query_response = getContent(l_connection.getInputStream());


            AgatteResponse state = AgatteParser.getInstance().parse_query_response(query_response);
            int punch_nb = state.getPunches().length;

            if (!check || (punch_nb % 2 == 0 && even) || (punch_nb % 2 == 1 && !even)) {
                //Then send a punching request, if condition ar met
                //extract secrets
                AgatteSecret secrets = AgatteParser.getInstance().parse_secrets_from_query_response(query_response);


                //Then send a punching request
                HttpsURLConnection exec_connection = (HttpsURLConnection) exec_url.openConnection();
                exec_connection.addRequestProperty("X-Requested-With", "XMLHttpRequest");
                exec_connection.connect();

                String secret_header = exec_connection.getHeaderField(secrets.getHeader_key());


                //Header secret_header = response2.getFirstHeader(secrets.getHeader_key());
                if (secret_header == null || secret_header.length() == 0) {
                    throw new AgtSecurityException("Unexpected response while punching: secret header not found");
                }
                String new_value = Base64.encodeToString(secret_header.getBytes(), Base64.DEFAULT);

                URI base_url = null;
                try {
                    base_url = new URI("https", this.getServer(), PUNCH_DIR, null);
                } catch (URISyntaxException e) {
                    //can't happen
                    e.printStackTrace();
                }
                URI url = base_url.resolve(secrets.getUrl());
                HttpsURLConnection exec_connection2 = (HttpsURLConnection) (url.toURL().openConnection());
                //set headers
                exec_connection2.addRequestProperty("X-Requested-With", "XMLHttpRequest");
                exec_connection2.addRequestProperty(secrets.getHeader_key(), new_value.trim());
                exec_connection2.addRequestProperty("Referer", "https://agatte.univ-lorraine.fr/top/top.form?numMen=2");
                exec_connection2.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

                exec_connection2.setRequestMethod("POST");


                // set content
                String content = String.format("pt=%s", secrets.getURLEncodedSecret());

                OutputStream os = exec_connection2.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(content);
                writer.flush();
                writer.close();
                os.close();
                exec_connection2.connect();
                if (exec_connection2.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    throw new AgatteException(exec_connection2.getResponseMessage());
                }
                String response3 = getContent(exec_connection2.getInputStream());
                return AgatteParser.getInstance().parse_query_response(response3);


            } else {
                //Do nothing
                String msg = String.format("An %s number of punches was required, but %d punch%s %s found",
                        (even ? "even" : "odd"),
                        punch_nb,
                        (punch_nb == 1 ? "" : "es"),
                        (punch_nb == 1 ? "was" : "were"));
                throw new InvalidPunchingConditionException(msg);
            }
        } catch (IOException e) {
            Log.w(MainActivity.LOG_TAG, "IOException while doing doCheckAndPunch", e);
            throw new AgatteException(e);
        }
    }


    /**
     * Query the agatte server to get the default counter. It also get contract num, and other data
     * that can be useful in following queries
     *
     * @return a CountPage instance
     * @throws IOException if the network connection fail
     * @throws AgatteException if the server send an error
     */
    CounterPage queryCounterContext() throws IOException, AgatteException {
        if (loginNotRequired()) {
            if (!login()) {
                throw new AgatteLoginFailedException();
            }
        }


        //Simulate a first query (server is dumb and want that)
        HttpsURLConnection l_connection = (HttpsURLConnection) query_day_url.openConnection();
        l_connection.setRequestMethod("GET");
        l_connection.connect();
        getContent(l_connection.getInputStream());


        // First query : get context and week
        HttpsURLConnection query_week_connection = (HttpsURLConnection) query_week_counter_url.openConnection();
        query_week_connection.setRequestMethod("GET");
        query_week_connection.connect();
        if (query_week_connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new AgatteException(query_week_connection.getResponseMessage());
        }
        String response = getContent(query_week_connection.getInputStream());

        // Extract contract number (numCont) and year (codeAnu), detect a "counter unavailable" message
        return AgatteParser.getInstance().parse_counter_response(response);
    }

    /**
     * Query the agatte server to get a specific counter.
     *
     * @param type the type of counter to query (week or year)
     * @param year the year to query counter for
     * @param week the week to query counter for (if applicable) value in range 1-52
     * @param contract the contract number
     * @param contract_year the accounting year
     * @return a CounterPage instance
     * @throws IOException if the network connection fail
     * @throws URISyntaxException if the server send an error
     */
    CounterPage queryCounter(AgatteCounterResponse.Type type, int year, int week, int contract, int contract_year) throws IOException, URISyntaxException, AgatteException {
        String date = String.format("%04d%02d", year, week);

        if (loginNotRequired()) {
            if (!login()) {
                throw new AgatteLoginFailedException();
            }
        }
        HttpsURLConnection query_connection = (HttpsURLConnection) query_week_counter_url.openConnection();
        query_connection.setInstanceFollowRedirects(false);
        query_connection.setRequestMethod("POST");
        query_connection.setDoInput(true);
        query_connection.setDoOutput(true);

        // Create post request
        Uri.Builder builder = new Uri.Builder()
        .appendQueryParameter(COUNTER_CONTRACT_NUMBER, String.format("%d", contract))
        .appendQueryParameter(COUNTER_CONTRACT_YEAR, String.format("%d", contract_year));
        switch (type) {
            case Year:
                builder.appendQueryParameter(COUNTER_TYPE, "A");
                break;
            case Week:
                builder.appendQueryParameter(COUNTER_TYPE, "H")
                       .appendQueryParameter(COUNTER_WEEK, date);
                break;
            default:
        }
        String query = builder.build().getEncodedQuery();
        OutputStream os = query_connection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
        writer.write(query);
        writer.flush();
        writer.close();
        os.close();

        query_connection.connect();
        if (query_connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new AgatteException(query_connection.getResponseMessage());
        }
        String response = getContent(query_connection.getInputStream());

        // Extract counter's value
        return AgatteParser.getInstance().parse_counter_response(response);
    }


    /**
     * Get the counter page from the server, and parse the response.
     * <p/>
     * 1) Send a GET request to extract contract number and contract year, then
     * 2) send a POST request to the server with the following content
     * <p/>
     * numSem:YYYYWW where YYYY is the year and WW is the week number in the year. Only in "A" mode
     * numCont: 10259  number of the work contract
     * nivCpt: X X="H" for weekly counter, X="A" for yearly
     * codeAnu: YYYY where YYYY is the year of the contract
     */


    /**
     * Get the counter page from the server, and parse the response.
     * <p/>
     * 1) Send a GET request to extract contract number and contract year, then
     * 2) send a POST request to the server with the following content
     * <p/>
     * numSem:YYYYWW where YYYY is the year and WW is the week number in the year. Only in "A" mode
     * numCont: 10259  number of the work contract
     * nivCpt: X X="H" for weekly counter, X="A" for yearly
     * codeAnu: YYYY where YYYY is the year of the contract
     *
     * @param year the year to query
     * @param week the week number to query
     * @return an AgatteCounterResponse instance
     * @throws AgatteException
     */
    public AgatteCounterResponse queryCounterWeek(int year, int week) throws AgatteException {

        try {

            CounterPage r1 = queryCounterContext();
            //if counter are unavailable, exit
            if (r1.anomaly) {
                return new AgatteCounterResponse(r1);
            }
            // Extract counter's value
            CounterPage r2 = queryCounter(AgatteCounterResponse.Type.Week, year, week, r1.contract, r1.contract_year);
            return new AgatteCounterResponse(r2);
        } catch (IOException | URISyntaxException e) {
            throw new AgatteException(e);
        }
    }

    /**
     * Get the counter value for the current week
     *
     * @return an AgatteCounterResponse
     */
    public AgatteCounterResponse queryCounterCurrent() throws AgatteException {

        try {

            CounterPage r1 = queryCounterContext();
            AgatteCounterResponse response = new AgatteCounterResponse(r1);
            //if counter are unavailable, exit
            if (r1.anomaly) {
                return response;
            } else {
                response.setValue(AgatteCounterResponse.Type.Week, r1.value);
            }
            // Extract counter's value
            CounterPage r2 = queryCounter(AgatteCounterResponse.Type.Year, 0, 0, r1.contract, r1.contract_year);
            response.setValue(AgatteCounterResponse.Type.Year, r2.value);
            return response;
        } catch (IOException | URISyntaxException e) {
            throw new AgatteException(e);
        }
    }


    /**
     * Return the host name of the agatte server
     *
     * @return a String like "agatte.univ-lorraine.fr" (without protocol string "https://)
     */
    String getServer() {
        return server;
    }

    /**
     * Set the hostname of the server to connect
     *
     * @param server a String like "agatte.univ-lorraine.fr" (without protocol string "https://)
     * @throws URISyntaxException
     */
    public void setServer(String server) throws URISyntaxException, MalformedURLException {
        this.server = server;

        this.login_url = new URL("https", this.getServer(), LOGIN_DIR);
        //this.logout_rq = new HttpGet(new URI("https", this.getServer(), LOGOUT_DIR, null));
        this.logout_url = new URL("https", this.getServer(), LOGOUT_DIR);
        //this.auth_rq = new HttpPost(new URI("https", this.getServer(), AUTH_DIR, null));
        this.auth_url = new URL("https", this.getServer(), AUTH_DIR);
        //this.exec_rq1 = new HttpPost(new URI("https", this.getServer(), PUNCH_DIR, null));
        //this.exec_rq1.addHeader(new BasicHeader("X-Requested-With", "XMLHttpRequest"));
        this.exec_url = new URL("https", this.getServer(), PUNCH_DIR);
        //this.query_day_rq = new HttpGet(new URI("https", this.getServer(), QUERY_DIR, null));
        this.query_day_url = new URL("https", this.getServer(), QUERY_DIR);
        //this.query_week_counter_rq1 = new HttpGet(new URI("https", this.getServer(), WEEK_COUNTER_DIR, null));
        this.query_week_counter_url = new URL("https", this.getServer(), WEEK_COUNTER_DIR);
    }

    /**
     * Set the username to use to connect
     *
     * @param user the username to set
     * @throws UnsupportedEncodingException
     */
    public void setUser(String user) throws UnsupportedEncodingException, MalformedURLException {
        this.user = user;
    }

    /**
     * Set the password to use in the connection
     *
     * @param password the password
     * @throws UnsupportedEncodingException if password contain invalid
     */
    public void setPassword(String password) throws UnsupportedEncodingException {
        this.passwd = password;
    }

    /**
     * Return the connection status of the agatte session.
     *
     * @return false if a user must be made
     */
    boolean loginNotRequired() {
        return (((this.session_id == null) || (this.session_expire >= System.currentTimeMillis() + SESSION_DELAY)));
    }
}
