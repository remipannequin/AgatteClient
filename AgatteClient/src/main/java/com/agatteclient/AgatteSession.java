package com.agatteclient;


import android.net.http.AndroidHttpClient;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rémi Pannequin on 24/09/13.
 */
public class AgatteSession {

    private String session_id;
    private String server;
    private long session_expire;
    //private URI login_url;
    private HttpGet login_rq;
    private HttpGet logout_rq;
    private HttpPost auth_rq;
    private HttpGet query_day_rq;
    private HttpGet query_top_ok_rq;
    private HttpPost exec_rq;
    private final List<NameValuePair> credentials;
    private HttpClient client;
    private HttpContext context;


    private static final String LOGIN_DIR = "/app/login.form";
    private static final String LOGOUT_DIR = "/app/logout.form";
    private static final String AUTH_DIR = "/j_acegi_security_check";
    private static final String PUNCH_DIR = "/top/top.form";
    private static final String PUNCH_OK_DIR = "/top/topOk.htm";
    private static final String QUERY_DIR = "/";
    private static final String USER = "j_username";
    private static final String PASSWORD = "j_password";
    private static final String AGENT = "Mozilla/5.0 (Linux; U; Android 2.2; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";


    /**
     * Create a new Agatte session.
     *
     * @throws URISyntaxException           If server YRL is not correct
     * @throws UnsupportedEncodingException if username and password include chars unsupported in this encoding
     */
    public AgatteSession() throws URISyntaxException, UnsupportedEncodingException {
        //super ("AgatteConnectionService");
        credentials = new ArrayList<NameValuePair>(2);
    }

    public AgatteSession(String server, String user, String passwd) throws URISyntaxException, UnsupportedEncodingException {
        //super ("AgatteConnectionService");
        credentials = new ArrayList<NameValuePair>(2);
        this.setServer(server);
        this.setUser(user);
        this.setPassword(passwd);

    }


    /*@Override
    protected void onHandleIntent(Intent arg0) {
        login();
    }
    */

    void logout() {

        try {
            client.execute(logout_rq, context);
            //this.session_expire = new Date(cookie.getMaxAge());
            //compute expiration date according to System.date(); ??
            this.session_id = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Attempt to log in Agatte :ie open an agatte session
     *
     * @return false if login failed, true if no errors were detected
     */
    public boolean login() throws IOException {

        context = new BasicHttpContext();
        CookieStore cookieStore = new BasicCookieStore();
        context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        client = AndroidHttpClient.newInstance(AGENT);

        HttpResponse response1 = client.execute(login_rq, context);
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
        HttpResponse response2 = client.execute(auth_rq, context);
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
     * @return
     */
    public AgatteResponse query_day() {
        try {
            if (!isConnected()) {
                if (!login()) {
                    return new AgatteResponse(AgatteResponse.Code.LoginFailed);
                }
            }
            client = AndroidHttpClient.newInstance(AGENT);
            client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
            HttpResponse response = client.execute(query_day_rq, context);
            return AgatteParser.getInstance().parse_query_response(response);
        } catch (IOException e) {
            e.printStackTrace();
            return new AgatteResponse(AgatteResponse.Code.IOError, e);
        }
    }

    public AgatteResponse doPunch() {
        try {
            if (!isConnected()) {
                 if (!login()) {
                    return new AgatteResponse(AgatteResponse.Code.LoginFailed);
                 }
            }
            client = AndroidHttpClient.newInstance(AGENT);
            HttpResponse response1 = client.execute(exec_rq, context);


            AgatteResponse.Code code = AgatteParser.getInstance().parse_punch_response(response1, PUNCH_OK_DIR);
            switch (code) {
                case TemporaryOK:
                    HttpResponse response2 = client.execute(query_top_ok_rq, context);
                    return AgatteParser.getInstance().parse_query_response(response2);
                case NetworkNotAuthorized:
                    return new AgatteResponse(code, "Current wifi/3G network is not authorized.");
                default:
                    return new AgatteResponse(AgatteResponse.Code.UnknownError);
            }


        } catch (IOException e) {
            e.printStackTrace();
            return new AgatteResponse(AgatteResponse.Code.IOError, e);
        }
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) throws URISyntaxException {
        this.server = server;
        this.login_rq = new HttpGet(new URI("https", this.getServer(), LOGIN_DIR, null));
        this.logout_rq = new HttpGet(new URI("https", this.getServer(), LOGOUT_DIR, null));
        this.auth_rq = new HttpPost(new URI("https", this.getServer(), AUTH_DIR, null));
        this.exec_rq = new HttpPost(new URI("https", this.getServer(), PUNCH_DIR, null));
        this.query_day_rq = new HttpGet(new URI("https", this.getServer(), QUERY_DIR, null));
        this.query_top_ok_rq = new HttpGet(new URI("https", this.getServer(), PUNCH_OK_DIR, null));
    }

    public String getUser() {
        return credentials.get(0).getValue();
    }

    public void setUser(String user) throws UnsupportedEncodingException {
        credentials.add(0, new BasicNameValuePair(USER, user));
        auth_rq.setEntity(new UrlEncodedFormEntity(credentials));
    }

    public String getPassword() {
        return credentials.get(1).getValue();
    }

    public void setPassword(String password) throws UnsupportedEncodingException {
        credentials.add(1, new BasicNameValuePair(PASSWORD, password));
        auth_rq.setEntity(new UrlEncodedFormEntity(credentials));
    }

    /**
     * Return the connection status of the agatte session.
     *
     * @return
     */
    public boolean isConnected() {
        return ((this.session_id != null) && (this.session_expire < System.currentTimeMillis() + 120000));
    }
}
