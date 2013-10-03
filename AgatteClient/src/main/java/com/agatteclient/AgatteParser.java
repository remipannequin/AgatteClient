package com.agatteclient;

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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
                result.append(line);
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = factory.newDocumentBuilder();
            StringReader sr = new StringReader(result.toString());
            Document doc = db.parse(new InputSource(sr));

            //get element from doc
            Element div = doc.getElementById("rappelTop");




        } catch (IOException e1) {

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return tops;
    }

}
