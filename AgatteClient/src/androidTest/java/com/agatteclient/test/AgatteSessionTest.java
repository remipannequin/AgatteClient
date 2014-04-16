package com.agatteclient.test;

import android.test.AndroidTestCase;

import com.agatteclient.agatte.AgatteCounterResponse;
import com.agatteclient.agatte.AgatteResponse;
import com.agatteclient.agatte.AgatteSession;

/**
 * Created by Rémi Pannequin on 08/11/13.
 */
public class AgatteSessionTest extends AndroidTestCase {

    private String login;
    private String password;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        login = AgatteCredentials.login;
        password = AgatteCredentials.password;
    }

    public void testInit() throws Exception {
        AgatteSession instance = new AgatteSession("agatte.univ-lorraine.fr", login, password);
    }

    public void testQueryDay() throws Exception {
        AgatteSession instance = new AgatteSession("agatte.univ-lorraine.fr", login, password);
        AgatteResponse rsp = instance.query_day();
    }

    public void testQueryTopOk() throws Exception {
        AgatteSession instance = new AgatteSession("agatte.univ-lorraine.fr", login, password);
        AgatteResponse rsp = instance.queryPunchOk();
    }

    public void testQueryCounter1() throws Exception {
        AgatteSession instance = new AgatteSession("agatte.univ-lorraine.fr", login, password);
        instance.queryCounterWeek(2014, 10);
    }

}
