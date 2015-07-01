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


import android.net.http.AndroidHttpClient;
import android.util.Log;

import com.agatteclient.MainActivity;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * A session with the Agatte server. It can be used multiple times.
 * <p/>
 * Created by Rémi Pannequin on 24/09/13.
 */
@SuppressWarnings("HardCodedStringLiteral")
public class AgatteSession {

    public static final int TIMEOUT_CONEECTION = 3000;
    public static final int TIMEOUT_SO = 5000;
    public static final int SESSION_DELAY = 120000;
    private static final String LOGIN_DIR = "/app/login.form";
    private static final String LOGOUT_DIR = "/app/logout.form";
    private static final String AUTH_DIR = "/j_acegi_security_check";
    private static final String PUNCH_DIR = "/top/";
    private static final String PUNCH_OK_DIR = "/top/topOk.htm";
    private static final String QUERY_DIR = "/";
    private static final String WEEK_COUNTER_DIR = "/top/feuille-top.form";
    private static final String USER = "j_username";
    private static final String PASSWORD = "j_password";
    private static final String COUNTER_CONTRACT_NUMBER = "numCtp";
    private static final String COUNTER_CONTRACT_YEAR = "codeAnu";
    private static final String COUNTER_WEEK = "numSem";
    private static final String COUNTER_TYPE = "nivCpt";
    private static final String AGENT = "Mozilla/5.0 (Linux; U; Android 2.2; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
    private final BasicCookieStore cookieStore;
    private final List<NameValuePair> credentials;
    private final HttpContext httpContext;
    private String session_id;
    private String server;
    private long session_expire;
    private HttpGet login_rq;
    private HttpGet logout_rq;
    private HttpPost auth_rq;
    private HttpGet query_day_rq;
    private HttpGet query_top_ok_rq;
    private HttpGet query_week_counter_rq1;
    private HttpPost exec_rq;


    /**
     * Create a new Agatte session.
     */
    private AgatteSession() {
        //super ("AgatteConnectionService");
        credentials = new ArrayList<NameValuePair>(2);
        httpContext = new BasicHttpContext();
        cookieStore = new BasicCookieStore();
        httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
    }

    /**
     * Create a new session with parameters
     *
     * @param server the hostname of the server
     * @param user   the username to use
     * @param passwd the password to use
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     */
    public AgatteSession(String server, String user, String passwd) throws URISyntaxException, UnsupportedEncodingException {
        //super ("AgatteConnectionService");
        this();
        this.setServer(server);
        this.setUser(user);
        this.setPassword(passwd);
    }

    /**
     * Send a logout to the server.
     *
     * @param client the HttpClient to use
     */
    public void logout(AndroidHttpClient client) {

        try {
            client.execute(logout_rq, httpContext);
            //this.session_expire = new Date(cookie.getMaxAge());
            //compute expiration date according to System.date(); ??
            this.session_id = null;
        } catch (IOException e) {
            Log.w(MainActivity.LOG_TAG, "IOException on logout", e);//NON-NLS
        }
    }

    /**
     * Attempt to log in Agatte :ie open an agatte session
     *
     * @return false if login failed, true if no errors were detected
     */
    private boolean login(AndroidHttpClient client) throws IOException {

        HttpResponse response1 = client.execute(login_rq, httpContext);
        response1.getEntity().consumeContent();
        //test whether response is OK
        if (response1.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            return false;
        }
        //update sessionID and expiration date
        boolean found_id = false;
        for (Cookie cookie : cookieStore.getCookies())
            if (cookie.getName().equals("JSESSIONID")) {
                //this.session_expire = new Date(cookie.getMaxAge());
                //compute expiration date according to System.date(); ??
                this.session_expire = System.currentTimeMillis();
                this.session_id = cookie.getValue();
                found_id = true;
            }
        if (!found_id) {
            return false;
        }

        HttpResponse response2 = client.execute(auth_rq, httpContext);
        response2.getEntity().consumeContent();
        //if (response2.getStatusLine().getStatusCode() != HttpStatus.SC_TEMPORARY_REDIRECT) {
        //    return false;
        //}
        for (Header h : response2.getHeaders("Location")) {
            //value should be URL("https", server, "/");
            if (h.getValue().contains("login_error=1")) {
                this.session_expire = 0;
                this.session_id = null;
                return false;
            }
        }


        return true;
    }

    /**
     * Attempt to get the list of tops from the server (doing a login if necessary)
     *
     * @return an AgatteResponse instance
     */
    public AgatteResponse query_day() throws AgatteException {
        AndroidHttpClient client = null;
        try {
            client = AndroidHttpClient.newInstance(AGENT);
            HttpParams httpParam = client.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParam, TIMEOUT_CONEECTION);
            HttpConnectionParams.setSoTimeout(httpParam, TIMEOUT_SO);
            if (loginNotRequired()) {
                if (!login(client)) {
                    //return new AgatteResponse(AgatteResponse.Code.LoginFailed);
                    throw new AgatteLoginFailedException();
                }
            }
            client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
            client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
            HttpResponse response = client.execute(query_day_rq, httpContext);
            return AgatteParser.getInstance().parse_query_response(response);
        } catch (IOException e) {
            Log.w(MainActivity.LOG_TAG, "IOException in query_day", e);//NON-NLS
            throw new AgatteException(e);
        } finally {
            if (client != null) {
                logout(client);
                client.close();
            }
        }
    }

    /**
     * Send a "punch" to the server
     *
     * As of june 2015, the new code on the servers requires to to a tree-step request:
     * 1) GET top/top.form, and extract some "secrets" : key in the header, secret URL, and secret
     * 2) POST top/location.href (XMLHttpRequest), and extract a returned key (in the header)
     * 3) POST to the secret URL (which ATM does not change) the secret, while putting the key in the header.
     *
     * The method to hide the values in the HTML is Base64. Which is just as lame as it sounds. But
     * Its encoded twice, for super-extra-security. Good job, IT guys, this was really funny.
     * Wink at Université de Lorrraine devs
     *
     *
     *
     * @return an AgatteResponse instance
     */
    public AgatteResponse doPunch() throws AgatteException {
        AndroidHttpClient client = AndroidHttpClient.newInstance(AGENT);
        try {
            if (loginNotRequired()) {
                if (!login(client)) {
                    //return new AgatteResponse(AgatteResponse.Code.LoginFailed);
                    throw new AgatteLoginFailedException();
                }
            }
            //Make sure to follow redirection
            client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
            client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
            //Simulate a first query
            client.execute(query_day_rq, httpContext).getEntity().consumeContent();
            //TODO extract secrets



            //Then send a punching request
            HttpResponse response1 = client.execute(exec_rq, httpContext);
            //should be a redirect to topOk

            if (!AgatteParser.getInstance().parse_punch_response(response1)) {
                throw new AgatteException();
            }
            HttpResponse response2 = client.execute(query_top_ok_rq, httpContext);
            return AgatteParser.getInstance().parse_topOk_response(response2);

        } catch (IOException e) {
            Log.w(MainActivity.LOG_TAG, "IOException while doing doPunch", e);
            throw new AgatteException(e);
        } finally {
            logout(client);
            client.close();
        }
    }

    /**
     * Send a "punch" to the server, only if the number of previous punches is even (resp odd)
     *
     * @param even if true, the number of param must be even, if false, it must be odd
     * @return an AgatteResponse instance
     */
    public AgatteResponse doCheckAndPunch(boolean even) throws AgatteException {
        AndroidHttpClient client = AndroidHttpClient.newInstance(AGENT);
        try {
            if (loginNotRequired()) {
                if (!login(client)) {
                    //return new AgatteResponse(AgatteResponse.Code.LoginFailed);
                    throw new AgatteLoginFailedException();
                }
            }
            //Make sure to follow redirection
            client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
            client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
            //Query first
            HttpResponse query_response = client.execute(query_day_rq, httpContext);
            AgatteResponse state = AgatteParser.getInstance().parse_query_response(query_response);
            int punch_nb = state.getPunches().length;

            if ((punch_nb % 2 == 0 && even) || (punch_nb % 2 == 1 && !even)) {
                //Then send a punching request, if condition ar met
                HttpResponse punch_response = client.execute(exec_rq, httpContext);
                //should be a redirect to topOk

                if (!AgatteParser.getInstance().parse_punch_response(punch_response)) {
                    throw new AgatteException();
                }
                HttpResponse response2 = client.execute(query_top_ok_rq, httpContext);
                return AgatteParser.getInstance().parse_topOk_response(response2);
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
        } finally {
            logout(client);
            client.close();
        }
    }


    /**
     * Query the agatte server to get the default counter. It also get contract num, and other data
     * that can be useful in following queries
     *
     * @param client the http client to use
     * @return a CountPage instance
     * @throws IOException if the network connection fail
     * @throws AgatteException if the server send an error
     */
    CounterPage queryCounterContext(AndroidHttpClient client) throws IOException, AgatteException {
        if (loginNotRequired()) {
            if (!login(client)) {
                throw new AgatteLoginFailedException();
            }
        }

        client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
        client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        //Simulate a first query
        client.execute(query_day_rq, httpContext).getEntity().consumeContent();
        // First query : get context and week
        HttpResponse response1 = client.execute(query_week_counter_rq1, httpContext);

        // Extract contract number (numCont) and year (codeAnu), detect a "counter unavailable" message
        return AgatteParser.getInstance().parse_counter_response(response1);
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
    CounterPage queryCounter(AndroidHttpClient client, AgatteCounterResponse.Type type, int year, int week, int contract, int contract_year) throws IOException, URISyntaxException, AgatteException {
        String date = String.format("%04d%02d", year, week);

        if (loginNotRequired()) {
            if (!login(client)) {
                throw new AgatteLoginFailedException();
            }
        }

        client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
        client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        // Create post request
        HttpPost query_week_counter_rq2 = new HttpPost(new URI("https", this.getServer(), WEEK_COUNTER_DIR, null));
        List<NameValuePair> request = new ArrayList<NameValuePair>(4);
        request.add(0, new BasicNameValuePair(COUNTER_CONTRACT_NUMBER, String.format("%d", contract)));
        request.add(0, new BasicNameValuePair(COUNTER_CONTRACT_YEAR, String.format("%d", contract_year)));
        switch (type) {
            case Year:
                request.add(0, new BasicNameValuePair(COUNTER_TYPE, "A"));
                break;
            case Week:
                request.add(0, new BasicNameValuePair(COUNTER_TYPE, "H"));
                request.add(0, new BasicNameValuePair(COUNTER_WEEK, date));
                break;
            default:
        }
        query_week_counter_rq2.setEntity(new UrlEncodedFormEntity(request));
        HttpResponse response2 = client.execute(query_week_counter_rq2, httpContext);

        // Extract counter's value
        return AgatteParser.getInstance().parse_counter_response(response2);
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
        AndroidHttpClient client = null;
        try {
            client = AndroidHttpClient.newInstance(AGENT);
            CounterPage r1 = queryCounterContext(client);
            //if counter are unavailable, exit
            if (r1.anomaly) {
                return new AgatteCounterResponse(r1);
            }
            // Extract counter's value
            CounterPage r2 = queryCounter(client, AgatteCounterResponse.Type.Week, year, week, r1.contract, r1.contract_year);
            return new AgatteCounterResponse(r2);
        } catch (IOException e) {
            throw new AgatteException(e);
        } catch (URISyntaxException e) {//can't happen
            throw new AgatteException(e);
        } finally {
            if (client != null) client.close();
        }
    }

    /**
     * Get the counter value for the current week
     *
     * @return an AgatteCounterResponse
     */
    public AgatteCounterResponse queryCounterCurrent() throws AgatteException {
        AndroidHttpClient client = null;
        try {
            client = AndroidHttpClient.newInstance(AGENT);
            CounterPage r1 = queryCounterContext(client);
            AgatteCounterResponse response = new AgatteCounterResponse(r1);
            //if counter are unavailable, exit
            if (r1.anomaly) {
                return response;
            } else {
                response.setValue(AgatteCounterResponse.Type.Week, r1.value);
            }
            // Extract counter's value
            CounterPage r2 = queryCounter(client, AgatteCounterResponse.Type.Year, 0, 0, r1.contract, r1.contract_year);
            response.setValue(AgatteCounterResponse.Type.Year, r2.value);
            return response;
        } catch (IOException e) {
            throw new AgatteException(e);
        } catch (URISyntaxException e) {//can't happen
            throw new AgatteException(e);
        } finally {
            if (client != null) client.close();
        }
    }


    /**
     * Request the "topOk" page from the server
     * <p/>
     * Useful for testing mainly
     *
     * @return an AgatteResponse instance
     */
    public AgatteResponse queryPunchOk() throws AgatteException {
        AndroidHttpClient client = AndroidHttpClient.newInstance(AGENT);
        try {
            if (loginNotRequired()) {
                if (!login(client)) {
                    throw new AgatteLoginFailedException();
                }
            }

            client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
            client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
            //Simulate a first query
            client.execute(query_day_rq, httpContext).getEntity().consumeContent();

            HttpResponse response2 = client.execute(query_top_ok_rq, httpContext);

            return AgatteParser.getInstance().parse_topOk_response(response2);

        } catch (IOException e) {
            Log.w(MainActivity.LOG_TAG, "IOException in queryPunchOk", e);//NON-NLS
            throw new AgatteException(e);
        } finally {
            logout(client);
            client.close();
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
    public void setServer(String server) throws URISyntaxException {
        this.server = server;

        this.login_rq = new HttpGet(new URI("https", this.getServer(), LOGIN_DIR, null));
        this.logout_rq = new HttpGet(new URI("https", this.getServer(), LOGOUT_DIR, null));
        this.auth_rq = new HttpPost(new URI("https", this.getServer(), AUTH_DIR, null));
        this.exec_rq = new HttpPost(new URI("https", this.getServer(), PUNCH_DIR, null));
        this.query_day_rq = new HttpGet(new URI("https", this.getServer(), QUERY_DIR, null));
        this.query_top_ok_rq = new HttpGet(new URI("https", this.getServer(), PUNCH_OK_DIR, null));
        this.query_week_counter_rq1 = new HttpGet(new URI("https", this.getServer(), WEEK_COUNTER_DIR, null));
    }

    /**
     * Set the username to use to connect
     *
     * @param user the username to set
     * @throws UnsupportedEncodingException
     */
    public void setUser(String user) throws UnsupportedEncodingException {
        credentials.add(0, new BasicNameValuePair(USER, user));
        auth_rq.setEntity(new UrlEncodedFormEntity(credentials));
    }

    /**
     * Set the password to use in the connection
     *
     * @param password the password
     * @throws UnsupportedEncodingException if password contain invalid
     */
    public void setPassword(String password) throws UnsupportedEncodingException {
        credentials.add(1, new BasicNameValuePair(PASSWORD, password));
        auth_rq.setEntity(new UrlEncodedFormEntity(credentials));
    }

    /**
     * Return the connection status of the agatte session.
     *
     * @return false if a login must be made
     */
    boolean loginNotRequired() {
        return (((this.session_id == null) || (this.session_expire >= System.currentTimeMillis() + SESSION_DELAY)));
    }
}
