package com.agatteclient;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.Html;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Singleton calss to parse response from the Agatte Server
 * <p/>
 * Created by remi on 03/10/13.
 */
public class AgatteParser {

    private static AgatteParser ourInstance = new AgatteParser();

    public static AgatteParser getInstance() {
        return ourInstance;
    }

    private AgatteParser() {
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public Collection<String> parse_query_response(HttpResponse response) {
        Collection<String> tops = new ArrayList<String>(6);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
        }
        try {
            InputStreamReader reader = new InputStreamReader(response.getEntity().getContent());
            BufferedReader rd = new BufferedReader(reader);
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line.replace("[\\n\\r\\t]", " "));
                if (line.contains("</")) {
                    result.append(System.getProperty("line.separator"));
                }
            }

            Pattern p = Pattern.compile(".*<li.*([0-9][0-9]:[0-9][0-9])\\s*</li>.*");
            Matcher matcher = p.matcher(result);
            while (matcher.find()) {
                tops.add(matcher.group(1));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tops;
    }

}
