package com.agatteclient;

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


}
