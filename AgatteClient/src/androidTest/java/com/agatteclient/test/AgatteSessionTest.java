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

package com.agatteclient.test;

import android.test.AndroidTestCase;

import com.agatteclient.agatte.AgatteResponse;
import com.agatteclient.agatte.AgatteSession;

@SuppressWarnings("HardCodedStringLiteral")
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

    /*
    public void testConditionalPunch1() throws Exception {
        AgatteSession instance = new AgatteSession("agatte.univ-lorraine.fr", login, password);
        instance.doCheckAndPunch(true);
    }

    public void testConditionalPunch2() throws Exception {
        AgatteSession instance = new AgatteSession("agatte.univ-lorraine.fr", login, password);
        instance.doCheckAndPunch(false);
    }
    */
}
