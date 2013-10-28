/*This file is part of AgatteClient.

    AgatteClient is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    AgatteClient is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with AgatteClient.  If not, see <http://www.gnu.org/licenses/>.*/

package com.agatteclient.test;

import android.test.AndroidTestCase;

import com.agatteclient.AgatteParser;
import com.agatteclient.AgatteResponse;
import com.agatteclient.AgatteSession;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Rémi Pannequin on 03/10/13.
 */
public class AgatteParserTest extends AndroidTestCase {

    public void testParseResponse() throws Exception {
        AgatteParser instance  = AgatteParser.getInstance();
        HttpResponse test = new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), 200, "OK");
        BasicHttpEntity test_entity = new BasicHttpEntity();
        test_entity.setContent(new ByteArrayInputStream(response_test1.getBytes()));
        test.setEntity(test_entity);

        AgatteResponse rsp = instance.parse_query_response(test);
        assertEquals(AgatteResponse.Code.QueryOK, rsp.getCode());
        assertTrue(rsp.hasTops());
        String[] actual = rsp.getTops();
        assertEquals(2, actual.length);
        assertEquals("12:10", actual[0]);
        assertEquals("13:00", actual[1]);

    }

    public void testParseResponse2() throws Exception {
        AgatteParser instance = AgatteParser.getInstance();
        HttpResponse test = new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), 200, "OK");
        BasicHttpEntity test_entity = new BasicHttpEntity();
        test_entity.setContent(new ByteArrayInputStream(response_test2.getBytes()));
        test.setEntity(test_entity);
        AgatteResponse rsp = instance.parse_query_response(test);
        assertEquals(AgatteResponse.Code.QueryOK, rsp.getCode());
        assertTrue(rsp.hasTops());
        String[] actual = rsp.getTops();
        assertEquals(3, actual.length);
        assertEquals("09:01", actual[0]);
        assertEquals("12:15", actual[1]);
        assertEquals("13:00", actual[2]);

    }

    public void testParseResponse3() throws Exception {
        AgatteParser instance = AgatteParser.getInstance();
        HttpResponse test = new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), 200, "OK");
        BasicHttpEntity test_entity = new BasicHttpEntity();
        test_entity.setContent(new ByteArrayInputStream(response_test3.getBytes()));
        test.setEntity(test_entity);
        AgatteResponse rsp = instance.parse_query_response(test);
        assertEquals(AgatteResponse.Code.NetworkNotAuthorized, rsp.getCode());
    }

    public void testParseResponse4() throws Exception {
        AgatteParser instance = AgatteParser.getInstance();
        HttpResponse test = new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), 200, "OK");
        BasicHttpEntity test_entity = new BasicHttpEntity();
        test_entity.setContent(new ByteArrayInputStream(response_test4.getBytes()));
        test.setEntity(test_entity);
        AgatteResponse rsp = instance.parse_topOk_response(test);
        assertEquals(AgatteResponse.Code.PunchOK, rsp.getCode());
        assertTrue(rsp.hasTops());
        String[] actual = rsp.getTops();
        assertEquals(3, actual.length);
        assertEquals("07:48", actual[0]);
        assertEquals("12:15", actual[1]);
        assertEquals("12:51", actual[2]);
    }

    public void testParseResponse5() throws Exception {
        AgatteParser instance = AgatteParser.getInstance();
        HttpResponse test = new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), 200, "OK");
        BasicHttpEntity test_entity = new BasicHttpEntity();
        test_entity.setContent(new ByteArrayInputStream(response_test2.getBytes()));
        test.setEntity(test_entity);
        AgatteResponse rsp = instance.parse_topOk_response(test);
        assertEquals(AgatteResponse.Code.UnknownError, rsp.getCode());
    }

    public void testParseResponse6() throws Exception {
        AgatteParser instance = AgatteParser.getInstance();
        HttpResponse test = new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), 200, "OK");
        BasicHttpEntity test_entity = new BasicHttpEntity();
        test_entity.setContent(new ByteArrayInputStream(response_test5.getBytes()));
        test.setEntity(test_entity);
        AgatteResponse rsp = instance.parse_query_response(test);
        assertEquals(AgatteResponse.Code.QueryOK, rsp.getCode());
        assertTrue(rsp.hasTops());
        assertTrue(rsp.hasVirtualTops());
        assertEquals(2, rsp.getVirtualTops().length);
        assertEquals("07:56", rsp.getVirtualTops()[0]);
        assertEquals("11:45", rsp.getVirtualTops()[1]);
        assertEquals(2, rsp.getTops().length);
        assertEquals("13:00", rsp.getTops()[0]);
        assertEquals("17:00", rsp.getTops()[1]);
    }


    private final static String response_test1 = "<html>\n" +
            "<head>\n" +
            "    <meta content=\"600;URL=/logout.htm\" http-equiv=\"refresh\">\n" +
            "    <link href=\"/media/css/normalize.css\" rel=\"stylesheet\" type=\"text/css\"/>\n" +
            "    <link href=\"/media/css/agatte.css?Thu Oct 03 13:05:04 CEST 2013\" rel=\"stylesheet\"\n" +
            "          type=\"text/css\"/>\n" +
            "    <link href=\"/media/css/print.css\" media=\"print\" rel=\"stylesheet\" type=\"text/css\"/>\n" +
            "    <link href=\"/media/css/displaytag.css\" rel=\"stylesheet\" type=\"text/css\"/>\n" +
            "    <style media=\"screen\" type=\"text/css\">@import \"/media/css/tabs.css\";</style>\n" +
            "    <!--[if IE]>\n" +
            "    <link rel=\"stylesheet\" href=\"/media/css/agatteIE.css\" type=\"text/css\"/><![endif]-->\n" +
            "    <style type='text/css'>@import url(/media/js/jscalendar-1.0/skins/aqua/theme.css);</style>\n" +
            "    <title>Agatte</title></head>\n" +
            "<body>\n" +
            "<div id= page>\n" +
            "    <div id=\"importJS\">\n" +
            "        <script src='/media/js/agatte.js' type='text/javascript'></script>\n" +
            "        <script src='/media/js/jscalendar-1.0/calendar.js' type='text/javascript'></script>\n" +
            "        <script src='/media/js/jscalendar-1.0/lang/calendar-fr.js' type='text/javascript'></script>\n" +
            "        <script src='/media/js/jscalendar-1.0/calendar-setup.js' type='text/javascript'></script>\n" +
            "    </div>\n" +
            "    <script src='/media/js/scriptaculous-js-1.6.1/lib/prototype.js' type='text/javascript'></script>\n" +
            "    <script src='/media/js/scriptaculous-js-1.6.1/src/scriptaculous.js'\n" +
            "            type='text/javascript'></script>\n" +
            "    <script src='/media/js/Tooltip.js' type='text/javascript'></script>\n" +
            "    <script src=\"/media/js/windows_js_1.3/prototype.js\" type=\"text/javascript\"></script>\n" +
            "    <script src=\"/media/js/windows_js_1.3/effects.js\" type=\"text/javascript\"></script>\n" +
            "    <script src=\"/media/js/windows_js_1.3/window.js\" type=\"text/javascript\"></script>\n" +
            "    <script src=\"/media/js/windows_js_1.3/debug.js\" type=\"text/javascript\"></script>\n" +
            "    <link href=\"/media/js/windows_js_1.3/themes/alert_agatte.css\" rel=\"stylesheet\" type=\"text/css\">\n" +
            "    <link href=\"/media/js/windows_js_1.3/themes/default.css\" rel=\"stylesheet\" type=\"text/css\">\n" +
            "    <!--[if IE]>\n" +
            "    <script language=\"javascript\">function loadInfoDialog() {}</script><![endif]-->\n" +
            "    <div id=\"bandeau\">\n" +
            "        <div id=\"logo\"></div>\n" +
            "    </div>\n" +
            "    <div id=\"header\">\n" +
            "        <ul id=\"primary\">\n" +
            "            <li><a class='current' href=\"/top/top.form?numMen=1\" onclick='loadInfoDialog()'>Tops</a>\n" +
            "                <ul id=\"secondary\">\n" +
            "                    <li><a class='current' href=\"/top/top.form?numMen=2\" onclick='loadInfoDialog()'>Toper</a>\n" +
            "                    </li>\n" +
            "                    <li><a href=\"/top/feuille-top.form?numMen=3\" onclick='loadInfoDialog()'>Feuille\n" +
            "                        des tops</a></li>\n" +
            "                    <li><a href=\"/top/detail-jour.form?numMen=4\" onclick='loadInfoDialog()'>D�tail\n" +
            "                        d'une journ�e</a></li>\n" +
            "                    <li><a href=\"/top/liste-ano.form?numMen=5\" onclick='loadInfoDialog()'>Liste des\n" +
            "                        anomalies</a></li>\n" +
            "                </ul>\n" +
            "            </li>\n" +
            "            <li><a href=\"/abs/dem-abs.form?numMen=6\" onclick='loadInfoDialog()'>Absences</a></li>\n" +
            "            <li><a href=\"/planning/planning.form?numMen=607\" onclick='loadInfoDialog()'>Planning</a>\n" +
            "            </li>\n" +
            "            <li><a href=\"/pers/fichePersonnel.htm?numMen=612\" onclick='loadInfoDialog()'>Fiche\n" +
            "                Personnel</a></li>\n" +
            "            <li><a href=\"/app/pref.form?numMen=609\" onclick='loadInfoDialog()'>Pr�f�rences</a></li>\n" +
            "            <li><a href=\"/app/aide.htm?numMen=610\" onclick='loadInfoDialog()'>Aide</a></li>\n" +
            "            <li><a href=\"/logout.htm?numMen=611\" onclick='loadInfoDialog()'>Quitter</a></li>\n" +
            "        </ul>\n" +
            "    </div>\n" +
            "    <div id=\"main-menu\">\n" +
            "        <div id=\"contents-menu\"><h1>Top</h1>\n" +
            "\n" +
            "            <p>Il est <span id=\"time\"></span></p>\n" +
            "            <script language=\"JavaScript\">\n" +
            "                \t\t\tvar dt= new Date(0,0,0,'13','05','04');\t\t\tvar serveur_heu = dt.getHours();\t\t\tvar serveur_min = dt.getMinutes(); \t\t\tvar serveur_sec = dt.getSeconds();\t\t\tvar timer=setInterval(\"horloge('time')\", 1000);\n" +
            "\n" +
            "            </script>\n" +
            "            <p>Cliquer sur le bouton pour toper...</p>\n" +
            "\n" +
            "            <form name=\"formTop\" action=\"top.form\" id=\"formTop\" method=\"post\"\n" +
            "                  onsubmit=\"return desactiveForm(this);\"><input class=\"button\" id=\"boutonToper\"\n" +
            "                                                                type=\"submit\" value=\"Toper\"></form>\n" +
            "            <hr/>\n" +
            "            <div id=\"rappelTop\"> Rappel des tops de la journ�e\n" +
            "                <ul>\n" +
            "                    <li title=\"Top r�el\"> 12:10</li>\n" +
            "                    <li title=\"Top r�el\"> 13:00</li>\n" +
            "                </ul>\n" +
            "                <p><span>Tops r�els</span> <br/> <span class=\"top-absence\">Tops d'absence</span></p>\n" +
            "            </div>\n" +
            "            <div class=\"spacer\">&nbsp;</div>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "    <div id=\"dhtmltooltip\"></div>\n" +
            "    <div id=\"dhtmltooltipJS\">\n" +
            "        <script src=\"/media/js/dhtmlToolTip.js\" type=\"text/javascript\"></script>\n" +
            "    </div>\n" +
            "    <div id=\"pied\">\n" +
            "        <div class=\"info\">Profil : Personnel</div>\n" +
            "        <div>Le logiciel Agatte a fait l'objet d'une d�claration � la Commission Nationale de\n" +
            "            l'Informatique et des Libert�s (CNIL), enregistr�e sous le N� 1005966. Selon la loi\n" +
            "            78-17 du 6 janvier 1978 sur l'informatique et les libert�s, vous b�n�ficiez d'un droit\n" +
            "            d'information et de rectification sur les renseignements vous concernant qui sont saisis\n" +
            "            dans le logiciel. Si vous souhaitez utiliser ce droit, veuillez contacter la DRH -\n" +
            "            Pr�sidence de l'Universit�.\n" +
            "        </div>\n" +
            "        <p>&copy; 2013 - Universit� de Lorraine</p></div>\n" +
            "</div>\n" +
            "</body>\n" +
            "</html>";
    private final static String response_test2 = "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "<html>\n" +
            "\t<head>\n" +
            "\t\t\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            " \n" +
            "\n" +
            "\t<meta http-equiv=\"refresh\" content=\"600;URL=/logout.htm\">\n" +
            "\n" +
            "\n" +
            "\n" +
            "<link rel=\"stylesheet\" href=\"/media/css/normalize.css\" type=\"text/css\"/> \n" +
            "<link rel=\"stylesheet\" href=\"/media/css/agatte.css?Fri Oct 04 13:33:38 CEST 2013\" type=\"text/css\"/>\n" +
            "<link rel=\"stylesheet\" href=\"/media/css/print.css\" type=\"text/css\" media=\"print\"/>\n" +
            "<link rel=\"stylesheet\" href=\"/media/css/displaytag.css\" type=\"text/css\"/>\n" +
            "\n" +
            "<style type=\"text/css\" media=\"screen\">@import \"/media/css/tabs.css\";</style>\n" +
            "\n" +
            "\n" +
            "<!--[if IE]>\n" +
            "<link rel=\"stylesheet\" href=\"/media/css/agatteIE.css\" type=\"text/css\"/>\n" +
            "<![endif]-->\n" +
            "\n" +
            "\n" +
            "<style type='text/css'>@import url(/media/js/jscalendar-1.0/skins/aqua/theme.css);</style>\n" +
            "\n" +
            "<title>Agatte</title>\n" +
            "\n" +
            "\n" +
            "\t</head>\n" +
            "\t<body>\n" +
            "\t  <div id=page>\n" +
            "\t\t\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "<div id=\"importJS\">\n" +
            "\t\n" +
            "\t<script type='text/javascript' src='/media/js/agatte.js'></script>\n" +
            "\t\n" +
            "\t<script type='text/javascript' src='/media/js/jscalendar-1.0/calendar.js'></script>\n" +
            "\t<script type='text/javascript' src='/media/js/jscalendar-1.0/lang/calendar-fr.js'></script>\n" +
            "\t<script type='text/javascript' src='/media/js/jscalendar-1.0/calendar-setup.js'></script>\n" +
            "</div>\n" +
            "\n" +
            "\n" +
            "<script type='text/javascript' src='/media/js/scriptaculous-js-1.6.1/lib/prototype.js'></script>\n" +
            "<script type='text/javascript' src='/media/js/scriptaculous-js-1.6.1/src/scriptaculous.js'></script>\n" +
            "<script type='text/javascript' src='/media/js/Tooltip.js'></script>\t\n" +
            "\n" +
            "\n" +
            "<script type=\"text/javascript\" src=\"/media/js/windows_js_1.3/prototype.js\"></script>\n" +
            "<script type=\"text/javascript\" src=\"/media/js/windows_js_1.3/effects.js\"></script>\n" +
            "<script type=\"text/javascript\" src=\"/media/js/windows_js_1.3/window.js\"></script>\n" +
            "<script type=\"text/javascript\" src=\"/media/js/windows_js_1.3/debug.js\"></script>\n" +
            "\n" +
            "  \t\t\n" +
            "<link href=\"/media/js/windows_js_1.3/themes/alert_agatte.css\" rel=\"stylesheet\" type=\"text/css\">\t\n" +
            "<link href=\"/media/js/windows_js_1.3/themes/default.css\" rel=\"stylesheet\" type=\"text/css\">\n" +
            "\n" +
            "<!--[if IE]>\t\n" +
            "<script language=\"javascript\">function loadInfoDialog() {}</script>\n" +
            "<![endif]-->\t\n" +
            "\t\n" +
            "\n" +
            "<div id=\"bandeau\">\n" +
            "\t<div id=\"logo\"></div>\n" +
            "</div>\t\n" +
            "\t\t\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\t<div id=\"header\">\n" +
            "\t\t<ul id=\"primary\">\n" +
            "\t\t\t\n" +
            "\t\t\t\t<li><a href=\"/top/top.form?numMen=1\" onclick='loadInfoDialog()' class='current'>Tops</a><ul id=\"secondary\"><li><a href=\"/top/top.form?numMen=2\" onclick='loadInfoDialog()' class='current'>Toper</a></li><li><a href=\"/top/feuille-top.form?numMen=3\" onclick='loadInfoDialog()' >Feuille des tops</a></li><li><a href=\"/top/detail-jour.form?numMen=4\" onclick='loadInfoDialog()' >Détail d'une journée</a></li><li><a href=\"/top/liste-ano.form?numMen=5\" onclick='loadInfoDialog()' >Liste des anomalies</a></li></ul></li>\n" +
            "\t\t\t\n" +
            "\t\t\t\t<li><a href=\"/abs/dem-abs.form?numMen=6\" onclick='loadInfoDialog()' >Absences</a></li>\n" +
            "\t\t\t\n" +
            "\t\t\t\t<li><a href=\"/planning/planning.form?numMen=607\" onclick='loadInfoDialog()' >Planning</a></li>\n" +
            "\t\t\t\n" +
            "\t\t\t\t<li><a href=\"/cet/accueilCET.htm?numMen=613\" onclick='loadInfoDialog()' >CET</a></li>\n" +
            "\t\t\t\n" +
            "\t\t\t\t<li><a href=\"/pers/fichePersonnel.htm?numMen=612\" onclick='loadInfoDialog()' >Fiche Personnel</a></li>\n" +
            "\t\t\t\n" +
            "\t\t\t\t<li><a href=\"/app/pref.form?numMen=609\" onclick='loadInfoDialog()' >Préférences</a></li>\n" +
            "\t\t\t\n" +
            "\t\t\t\t<li><a href=\"/app/aide.htm?numMen=610\" onclick='loadInfoDialog()' >Aide</a></li>\n" +
            "\t\t\t\n" +
            "\t\t\t\t<li><a href=\"/logout.htm?numMen=611\" onclick='loadInfoDialog()' >Quitter</a></li>\n" +
            "\t\t\t\n" +
            "\t\t</ul>\n" +
            "\t\t\n" +
            "\t\t\t\n" +
            "\t\t\n" +
            "\t</div>\n" +
            "\n" +
            "\t\t<div id=\n" +
            "\t\t\t\n" +
            "\t\t\t\t\n" +
            "\t\t\t\t\n" +
            "\t\t\t\t\t\n" +
            "\t\t\t\t\t\t\n" +
            "\t\t\t\t\t\t\"main-menu\"\n" +
            "\t\t\t\t\t\n" +
            "\t\t\t\t\n" +
            "\t\t\t\t\n" +
            "\t\t\t\t\n" +
            "\t\t\t\t\n" +
            "\t\t >\n" +
            "\t\t\t<div id=\n" +
            "\t\t\t\t\n" +
            "\t\t\t\t\t\n" +
            "\t\t\t\t\t\"contents-menu\"\n" +
            "\t\t\t\t\t\n" +
            "\t\t\t\t\t\n" +
            "\t\t\t\t\t\n" +
            "\t\t\t>\n" +
            "\t\t\t\t\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "<h1>Top</h1>\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\t\n" +
            "\t\n" +
            "\t\t<p>Il est <span id=\"time\"></span></p>\n" +
            "\t\t<script language=\"JavaScript\">\n" +
            "\t\t\tvar dt= new Date(0,0,0,'13','33','38');\n" +
            "\t\t\tvar serveur_heu = dt.getHours();\n" +
            "\t\t\tvar serveur_min = dt.getMinutes(); \n" +
            "\t\t\tvar serveur_sec = dt.getSeconds();\n" +
            "\t\t\tvar timer=setInterval(\"horloge('time')\", 1000);\n" +
            "\t\t</script>\n" +
            "\t\t<p>Cliquer sur le bouton pour toper...</p>\n" +
            "\t\t<form id=\"formTop\" name=\"formTop\" action=\"top.form\" method=\"post\" onsubmit=\"return desactiveForm(this);\">\n" +
            "\t\t\t<input type=\"submit\" value=\"Toper\" class=\"button\" id=\"boutonToper\"> \n" +
            "\t\t</form>\n" +
            "\t\n" +
            "\n" +
            "<hr/>\n" +
            "<div id=\"rappelTop\">\n" +
            "\tRappel des tops de la journée\n" +
            "\t<ul>\n" +
            "\t\t\n" +
            "\t\t\t<li \n" +
            "\t\t\t\t\n" +
            "\t\t\t\t\ttitle=\"Top réel\"\n" +
            "\t\t\t\t\t\n" +
            "\t\t\t\t\n" +
            "\t\t\t>\n" +
            "\t\t\t\t\n" +
            "\t\t\t\t09:01\n" +
            "\t\t\t</li>\n" +
            "\t\t\n" +
            "\t\t\t<li \n" +
            "\t\t\t\t\n" +
            "\t\t\t\t\ttitle=\"Top réel\"\n" +
            "\t\t\t\t\t\n" +
            "\t\t\t\t\n" +
            "\t\t\t>\n" +
            "\t\t\t\t\n" +
            "\t\t\t\t12:15\n" +
            "\t\t\t</li>\n" +
            "\t\t\n" +
            "\t\t\t<li \n" +
            "\t\t\t\t\n" +
            "\t\t\t\t\ttitle=\"Top réel\"\n" +
            "\t\t\t\t\t\n" +
            "\t\t\t\t\n" +
            "\t\t\t>\n" +
            "\t\t\t\t\n" +
            "\t\t\t\t13:00\n" +
            "\t\t\t</li>\n" +
            "\t\t\n" +
            "\t</ul>\n" +
            "\t<p>\n" +
            "\t\t<span>Tops réels</span>\n" +
            "\t\t<br />\n" +
            "\t\t<span class=\"top-absence\">Tops d'absence</span>\n" +
            "\t</p>\n" +
            "</div>\n" +
            "\n" +
            "<div class=\"spacer\">&nbsp;</div>\n" +
            "\n" +
            "\t\t\t</div>\n" +
            "\t\t</div>\n" +
            "\t\t\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "<div id=\"dhtmltooltip\"></div>\n" +
            "<div id=\"dhtmltooltipJS\"> \n" +
            "<script type=\"text/javascript\" src=\"/media/js/dhtmlToolTip.js\"></script> \n" +
            "</div>\n" +
            "\n" +
            "<div id=\"pied\">\n" +
            "\t\n" +
            "\t\t<div class=\"info\">Profil : Personnel</div>\n" +
            "\t\n" +
            "\t<div>Le logiciel Agatte a fait l'objet d'une déclaration à la Commission Nationale de l'Informatique et des Libertés (CNIL), enregistrée sous le N° 1005966. Selon la loi 78-17 du 6 janvier 1978 sur l'informatique et les libertés, vous bénéficiez d'un droit d'information et de rectification sur les renseignements vous concernant qui sont saisis dans le logiciel. Si vous souhaitez utiliser ce droit, veuillez contacter la DRH - Présidence de l'Université.</div>\n" +
            "\t<p>&copy; 2013 - Université de Lorraine</p>\t\n" +
            "</div>\n" +
            "\n" +
            "\t  </div>\t\n" +
            "\t</body>\n" +
            "</html>";

    private final static String response_test3 = "<html>\t<head>\t\t \t<meta http-equiv=\"refresh\" content=\"600;URL=/logout.htm\"><link rel=\"stylesheet\" href=\"/media/css/normalize.css\" type=\"text/css\"/> <link rel=\"stylesheet\" href=\"/media/css/agatte.css?Sun Oct 06 11:25:56 CEST 2013\" type=\"text/css\"/><link rel=\"stylesheet\" href=\"/media/css/print.css\" type=\"text/css\" media=\"print\"/><link rel=\"stylesheet\" href=\"/media/css/displaytag.css\" type=\"text/css\"/><style type=\"text/css\" media=\"screen\">@import \"/media/css/tabs.css\";</style>\n" +
            "<!--[if IE]><link rel=\"stylesheet\" href=\"/media/css/agatteIE.css\" type=\"text/css\"/><![endif]--><style type='text/css'>@import url(/media/js/jscalendar-1.0/skins/aqua/theme.css);</style>\n" +
            "<title>Agatte</title>\n" +
            "\t</head>\n" +
            "\t<body>\t  <div id=page>\t\t<div id=\"importJS\">\t\t<script type='text/javascript' src='/media/js/agatte.js'></script>\n" +
            "\t\t<script type='text/javascript' src='/media/js/jscalendar-1.0/calendar.js'></script>\n" +
            "\t<script type='text/javascript' src='/media/js/jscalendar-1.0/lang/calendar-fr.js'></script>\n" +
            "\t<script type='text/javascript' src='/media/js/jscalendar-1.0/calendar-setup.js'></script>\n" +
            "</div>\n" +
            "<script type='text/javascript' src='/media/js/scriptaculous-js-1.6.1/lib/prototype.js'></script>\n" +
            "<script type='text/javascript' src='/media/js/scriptaculous-js-1.6.1/src/scriptaculous.js'></script>\n" +
            "<script type='text/javascript' src='/media/js/Tooltip.js'></script>\t\n" +
            "<script type=\"text/javascript\" src=\"/media/js/windows_js_1.3/prototype.js\"></script>\n" +
            "<script type=\"text/javascript\" src=\"/media/js/windows_js_1.3/effects.js\"></script>\n" +
            "<script type=\"text/javascript\" src=\"/media/js/windows_js_1.3/window.js\"></script>\n" +
            "<script type=\"text/javascript\" src=\"/media/js/windows_js_1.3/debug.js\"></script>\n" +
            "  \t\t<link href=\"/media/js/windows_js_1.3/themes/alert_agatte.css\" rel=\"stylesheet\" type=\"text/css\">\t<link href=\"/media/js/windows_js_1.3/themes/default.css\" rel=\"stylesheet\" type=\"text/css\"><!--[if IE]>\t<script language=\"javascript\">function loadInfoDialog() {}</script>\n" +
            "<![endif]-->\t\t<div id=\"bandeau\">\t<div id=\"logo\"></div>\n" +
            "</div>\t\n" +
            "\t\t\t<div id=\"header\">\t\t<ul id=\"primary\">\t\t\t\t\t\t\t<li><a href=\"/top/top.form?numMen=1\" onclick='loadInfoDialog()' class='current'>Tops</a><ul id=\"secondary\"><li><a href=\"/top/top.form?numMen=2\" onclick='loadInfoDialog()' class='current'>Toper</a></li><li><a href=\"/top/feuille-top.form?numMen=3\" onclick='loadInfoDialog()' >Feuille des tops</a></li><li><a href=\"/top/detail-jour.form?numMen=4\" onclick='loadInfoDialog()' >D�tail d'une journ�e</a></li><li><a href=\"/top/liste-ano.form?numMen=5\" onclick='loadInfoDialog()' >Liste des anomalies</a></li></ul></li>\n" +
            "\t\t\t\t\t\t\t<li><a href=\"/abs/dem-abs.form?numMen=6\" onclick='loadInfoDialog()' >Absences</a></li>\n" +
            "\t\t\t\t\t\t\t<li><a href=\"/planning/planning.form?numMen=607\" onclick='loadInfoDialog()' >Planning</a></li>\n" +
            "\t\t\t\t\t\t\t<li><a href=\"/cet/accueilCET.htm?numMen=613\" onclick='loadInfoDialog()' >CET</a></li>\n" +
            "\t\t\t\t\t\t\t<li><a href=\"/pers/fichePersonnel.htm?numMen=612\" onclick='loadInfoDialog()' >Fiche Personnel</a></li>\n" +
            "\t\t\t\t\t\t\t<li><a href=\"/app/pref.form?numMen=609\" onclick='loadInfoDialog()' >Pr�f�rences</a></li>\n" +
            "\t\t\t\t\t\t\t<li><a href=\"/app/aide.htm?numMen=610\" onclick='loadInfoDialog()' >Aide</a></li>\n" +
            "\t\t\t\t\t\t\t<li><a href=\"/logout.htm?numMen=611\" onclick='loadInfoDialog()' >Quitter</a></li>\n" +
            "\t\t\t\t\t</ul>\n" +
            "\t\t\t\t\t\t\t\t</div>\n" +
            "\t\t<div id=\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\"main-menu\"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t >\t\t\t<div id=\t\t\t\t\t\t\t\t\t\t\t\t\t\t\"contents-menu\"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t>\t\t\t\t<div id=\"zoneAppMsg\">\t<fieldset id=\"appMsg\">\t\t<legend>Acc�s interdit</legend>\n" +
            "\t\t<div id=\"img\">\t\t\t<img src=\"/media/img/lock.png\" alt=\"Acc�s interdit\" title=\"Acc�s interdit\" border=\"0\" align=\"absmiddle\"/>\t\t</div>\n" +
            "\t\t<div id=\"msg\">\t\t\t<p>La ressource demand�e n&#039;est accessible que depuis un r�seau autoris�</p>\n" +
            "\t\t</div>\n" +
            "\t\t<div class=\"spacer\">&nbsp;</div>\n" +
            "\t</fieldset>\n" +
            "</div>\n" +
            "\t\t\t</div>\n" +
            "\t\t</div>\n" +
            "\t\t<div id=\"dhtmltooltip\"></div>\n" +
            "<div id=\"dhtmltooltipJS\"> <script type=\"text/javascript\" src=\"/media/js/dhtmlToolTip.js\"></script> \n" +
            "</div>\n" +
            "<div id=\"pied\">\t\t\t<div class=\"info\">Profil : Personnel</div>\n" +
            "\t\t<div>Le logiciel Agatte a fait l'objet d'une d�claration � la Commission Nationale de l'Informatique et des Libert�s (CNIL), enregistr�e sous le N� 1005966. Selon la loi 78-17 du 6 janvier 1978 sur l'informatique et les libert�s, vous b�n�ficiez d'un droit d'information et de rectification sur les renseignements vous concernant qui sont saisis dans le logiciel. Si vous souhaitez utiliser ce droit, veuillez contacter la DRH - Pr�sidence de l'Universit�.</div>\n" +
            "\t<p>&copy; 2013 - Universit� de Lorraine</p>\t\n" +
            "</div>\n" +
            "\t  </div>\t\n" +
            "\t</body>\n" +
            "</html>\n";


    private static final String response_test4 = "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "<html>\n" +
            "\t<head>\n" +
            "\t\t\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            " \n" +
            "\n" +
            "\t<meta http-equiv=\"refresh\" content=\"600;URL=/logout.htm\">\n" +
            "\n" +
            "\n" +
            "\n" +
            "<link rel=\"stylesheet\" href=\"/media/css/normalize.css\" type=\"text/css\"/> \n" +
            "<link rel=\"stylesheet\" href=\"/media/css/agatte.css?Wed Oct 09 12:52:35 CEST 2013\" type=\"text/css\"/>\n" +
            "<link rel=\"stylesheet\" href=\"/media/css/print.css\" type=\"text/css\" media=\"print\"/>\n" +
            "<link rel=\"stylesheet\" href=\"/media/css/displaytag.css\" type=\"text/css\"/>\n" +
            "\n" +
            "<style type=\"text/css\" media=\"screen\">@import \"/media/css/tabs.css\";</style>\n" +
            "\n" +
            "\n" +
            "<!--[if IE]>\n" +
            "<link rel=\"stylesheet\" href=\"/media/css/agatteIE.css\" type=\"text/css\"/>\n" +
            "<![endif]-->\n" +
            "\n" +
            "\n" +
            "<style type='text/css'>@import url(/media/js/jscalendar-1.0/skins/aqua/theme.css);</style>\n" +
            "\n" +
            "<title>Agatte</title>\n" +
            "\n" +
            "\n" +
            "\t</head>\n" +
            "\t<body>\n" +
            "\t  <div id=page>\n" +
            "\t\t\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "<div id=\"importJS\">\n" +
            "\t\n" +
            "\t<script type='text/javascript' src='/media/js/agatte.js'></script>\n" +
            "\t\n" +
            "\t<script type='text/javascript' src='/media/js/jscalendar-1.0/calendar.js'></script>\n" +
            "\t<script type='text/javascript' src='/media/js/jscalendar-1.0/lang/calendar-fr.js'></script>\n" +
            "\t<script type='text/javascript' src='/media/js/jscalendar-1.0/calendar-setup.js'></script>\n" +
            "</div>\n" +
            "\n" +
            "\n" +
            "<script type='text/javascript' src='/media/js/scriptaculous-js-1.6.1/lib/prototype.js'></script>\n" +
            "<script type='text/javascript' src='/media/js/scriptaculous-js-1.6.1/src/scriptaculous.js'></script>\n" +
            "<script type='text/javascript' src='/media/js/Tooltip.js'></script>\t\n" +
            "\n" +
            "\n" +
            "<script type=\"text/javascript\" src=\"/media/js/windows_js_1.3/prototype.js\"></script>\n" +
            "<script type=\"text/javascript\" src=\"/media/js/windows_js_1.3/effects.js\"></script>\n" +
            "<script type=\"text/javascript\" src=\"/media/js/windows_js_1.3/window.js\"></script>\n" +
            "<script type=\"text/javascript\" src=\"/media/js/windows_js_1.3/debug.js\"></script>\n" +
            "\n" +
            "  \t\t\n" +
            "<link href=\"/media/js/windows_js_1.3/themes/alert_agatte.css\" rel=\"stylesheet\" type=\"text/css\">\t\n" +
            "<link href=\"/media/js/windows_js_1.3/themes/default.css\" rel=\"stylesheet\" type=\"text/css\">\n" +
            "\n" +
            "<!--[if IE]>\t\n" +
            "<script language=\"javascript\">function loadInfoDialog() {}</script>\n" +
            "<![endif]-->\t\n" +
            "\t\n" +
            "\n" +
            "<div id=\"bandeau\">\n" +
            "\t<div id=\"logo\"></div>\n" +
            "</div>\t\n" +
            "\t\t\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\t<div id=\"header\">\n" +
            "\t\t<ul id=\"primary\">\n" +
            "\t\t\t\n" +
            "\t\t\t\t<li><a href=\"/top/top.form?numMen=1\" onclick='loadInfoDialog()' class='current'>Tops</a><ul id=\"secondary\"><li><a href=\"/top/top.form?numMen=2\" onclick='loadInfoDialog()' class='current'>Toper</a></li><li><a href=\"/top/feuille-top.form?numMen=3\" onclick='loadInfoDialog()' >Feuille des tops</a></li><li><a href=\"/top/detail-jour.form?numMen=4\" onclick='loadInfoDialog()' >Détail d'une journée</a></li><li><a href=\"/top/liste-ano.form?numMen=5\" onclick='loadInfoDialog()' >Liste des anomalies</a></li></ul></li>\n" +
            "\t\t\t\n" +
            "\t\t\t\t<li><a href=\"/abs/dem-abs.form?numMen=6\" onclick='loadInfoDialog()' >Absences</a></li>\n" +
            "\t\t\t\n" +
            "\t\t\t\t<li><a href=\"/planning/planning.form?numMen=607\" onclick='loadInfoDialog()' >Planning</a></li>\n" +
            "\t\t\t\n" +
            "\t\t\t\t<li><a href=\"/cet/accueilCET.htm?numMen=613\" onclick='loadInfoDialog()' >CET</a></li>\n" +
            "\t\t\t\n" +
            "\t\t\t\t<li><a href=\"/pers/fichePersonnel.htm?numMen=612\" onclick='loadInfoDialog()' >Fiche Personnel</a></li>\n" +
            "\t\t\t\n" +
            "\t\t\t\t<li><a href=\"/app/pref.form?numMen=609\" onclick='loadInfoDialog()' >Préférences</a></li>\n" +
            "\t\t\t\n" +
            "\t\t\t\t<li><a href=\"/app/aide.htm?numMen=610\" onclick='loadInfoDialog()' >Aide</a></li>\n" +
            "\t\t\t\n" +
            "\t\t\t\t<li><a href=\"/logout.htm?numMen=611\" onclick='loadInfoDialog()' >Quitter</a></li>\n" +
            "\t\t\t\n" +
            "\t\t</ul>\n" +
            "\t\t\n" +
            "\t\t\t\n" +
            "\t\t\n" +
            "\t</div>\n" +
            "\n" +
            "\t\t<div id=\n" +
            "\t\t\t\n" +
            "\t\t\t\t\n" +
            "\t\t\t\t\n" +
            "\t\t\t\t\t\n" +
            "\t\t\t\t\t\t\n" +
            "\t\t\t\t\t\t\"main-menu\"\n" +
            "\t\t\t\t\t\n" +
            "\t\t\t\t\n" +
            "\t\t\t\t\n" +
            "\t\t\t\t\n" +
            "\t\t\t\t\n" +
            "\t\t >\n" +
            "\t\t\t<div id=\n" +
            "\t\t\t\t\n" +
            "\t\t\t\t\t\n" +
            "\t\t\t\t\t\"contents-menu\"\n" +
            "\t\t\t\t\t\n" +
            "\t\t\t\t\t\n" +
            "\t\t\t\t\t\n" +
            "\t\t\t>\n" +
            "\t\t\t\t\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "<h1>Top</h1>\n" +
            "\n" +
            "<p>Top pris en compte à 12:51</p>\n" +
            "<hr/>\n" +
            "<div id=\"rappelTop\">\n" +
            "\tRappel des tops de la journée\n" +
            "\t<ul>\n" +
            "\t\t\n" +
            "\t\t\t<li \n" +
            "\t\t\t\t\n" +
            "\t\t\t\t\ttitle=\"Top réel\"\n" +
            "\t\t\t\t\t\n" +
            "\t\t\t\t\n" +
            "\t\t\t>\n" +
            "\t\t\t\t07:48\n" +
            "\t\t\t</li>\n" +
            "\t\t\n" +
            "\t\t\t<li \n" +
            "\t\t\t\t\n" +
            "\t\t\t\t\ttitle=\"Top réel\"\n" +
            "\t\t\t\t\t\n" +
            "\t\t\t\t\n" +
            "\t\t\t>\n" +
            "\t\t\t\t12:15\n" +
            "\t\t\t</li>\n" +
            "\t\t\n" +
            "\t\t\t<li \n" +
            "\t\t\t\t\n" +
            "\t\t\t\t\ttitle=\"Top réel\"\n" +
            "\t\t\t\t\t\n" +
            "\t\t\t\t\n" +
            "\t\t\t>\n" +
            "\t\t\t\t12:51\n" +
            "\t\t\t</li>\n" +
            "\t\t\n" +
            "\t</ul>\n" +
            "\t<p>\n" +
            "\t\t<span>Tops réels</span>\n" +
            "\t\t<br/>\n" +
            "\t\t<span class=\"top-absence\">Tops d'absence</span>\n" +
            "\t</p>\n" +
            "</div>\n" +
            "\n" +
            "<div class=\"spacer\">&nbsp;</div>\n" +
            "\n" +
            "\t\t\t</div>\n" +
            "\t\t</div>\n" +
            "<div id=\"dhtmltooltip\"></div>\n" +
            "<div id=\"dhtmltooltipJS\"> \n" +
            "<script type=\"text/javascript\" src=\"/media/js/dhtmlToolTip.js\"></script> \n" +
            "</div>\n" +
            "\n" +
            "<div id=\"pied\">\n" +
            "\t\n" +
            "\t\t<div class=\"info\">Profil : Personnel</div>\n" +
            "\t\n" +
            "\t<div>Le logiciel Agatte a fait l'objet d'une déclaration à la Commission Nationale de l'Informatique et des Libertés (CNIL), enregistrée sous le N° 1005966. Selon la loi 78-17 du 6 janvier 1978 sur l'informatique et les libertés, vous bénéficiez d'un droit d'information et de rectification sur les renseignements vous concernant qui sont saisis dans le logiciel. Si vous souhaitez utiliser ce droit, veuillez contacter la DRH - Présidence de l'Université.</div>\n" +
            "\t<p>&copy; 2013 - Université de Lorraine</p>\t\n" +
            "</div>\n" +
            "\n" +
            "\t  </div>\t\n" +
            "\t</body>\n" +
            "</html>";

    private static final String response_test5 = "<html>\n" +
            "<head>\n" +
            "    <meta content=\"600;URL=/logout.htm\" http-equiv=\"refresh\">\n" +
            "    <link href=\"/media/css/normalize.css\" rel=\"stylesheet\" type=\"text/css\"/>\n" +
            "    <link href=\"/media/css/agatte.css?Thu Oct 03 13:05:04 CEST 2013\" rel=\"stylesheet\"\n" +
            "          type=\"text/css\"/>\n" +
            "    <link href=\"/media/css/print.css\" media=\"print\" rel=\"stylesheet\" type=\"text/css\"/>\n" +
            "    <link href=\"/media/css/displaytag.css\" rel=\"stylesheet\" type=\"text/css\"/>\n" +
            "    <style media=\"screen\" type=\"text/css\">@import \"/media/css/tabs.css\";</style>\n" +
            "    <!--[if IE]>\n" +
            "    <link rel=\"stylesheet\" href=\"/media/css/agatteIE.css\" type=\"text/css\"/><![endif]-->\n" +
            "    <style type='text/css'>@import url(/media/js/jscalendar-1.0/skins/aqua/theme.css);</style>\n" +
            "    <title>Agatte</title></head>\n" +
            "<body>\n" +
            "<div id= page>\n" +
            "    <div id=\"importJS\">\n" +
            "        <script src='/media/js/agatte.js' type='text/javascript'></script>\n" +
            "        <script src='/media/js/jscalendar-1.0/calendar.js' type='text/javascript'></script>\n" +
            "        <script src='/media/js/jscalendar-1.0/lang/calendar-fr.js' type='text/javascript'></script>\n" +
            "        <script src='/media/js/jscalendar-1.0/calendar-setup.js' type='text/javascript'></script>\n" +
            "    </div>\n" +
            "    <script src='/media/js/scriptaculous-js-1.6.1/lib/prototype.js' type='text/javascript'></script>\n" +
            "    <script src='/media/js/scriptaculous-js-1.6.1/src/scriptaculous.js'\n" +
            "            type='text/javascript'></script>\n" +
            "    <script src='/media/js/Tooltip.js' type='text/javascript'></script>\n" +
            "    <script src=\"/media/js/windows_js_1.3/prototype.js\" type=\"text/javascript\"></script>\n" +
            "    <script src=\"/media/js/windows_js_1.3/effects.js\" type=\"text/javascript\"></script>\n" +
            "    <script src=\"/media/js/windows_js_1.3/window.js\" type=\"text/javascript\"></script>\n" +
            "    <script src=\"/media/js/windows_js_1.3/debug.js\" type=\"text/javascript\"></script>\n" +
            "    <link href=\"/media/js/windows_js_1.3/themes/alert_agatte.css\" rel=\"stylesheet\" type=\"text/css\">\n" +
            "    <link href=\"/media/js/windows_js_1.3/themes/default.css\" rel=\"stylesheet\" type=\"text/css\">\n" +
            "    <!--[if IE]>\n" +
            "    <script language=\"javascript\">function loadInfoDialog() {}</script><![endif]-->\n" +
            "    <div id=\"bandeau\">\n" +
            "        <div id=\"logo\"></div>\n" +
            "    </div>\n" +
            "    <div id=\"header\">\n" +
            "        <ul id=\"primary\">\n" +
            "            <li><a class='current' href=\"/top/top.form?numMen=1\" onclick='loadInfoDialog()'>Tops</a>\n" +
            "                <ul id=\"secondary\">\n" +
            "                    <li><a class='current' href=\"/top/top.form?numMen=2\" onclick='loadInfoDialog()'>Toper</a>\n" +
            "                    </li>\n" +
            "                    <li><a href=\"/top/feuille-top.form?numMen=3\" onclick='loadInfoDialog()'>Feuille\n" +
            "                        des tops</a></li>\n" +
            "                    <li><a href=\"/top/detail-jour.form?numMen=4\" onclick='loadInfoDialog()'>D�tail\n" +
            "                        d'une journ�e</a></li>\n" +
            "                    <li><a href=\"/top/liste-ano.form?numMen=5\" onclick='loadInfoDialog()'>Liste des\n" +
            "                        anomalies</a></li>\n" +
            "                </ul>\n" +
            "            </li>\n" +
            "            <li><a href=\"/abs/dem-abs.form?numMen=6\" onclick='loadInfoDialog()'>Absences</a></li>\n" +
            "            <li><a href=\"/planning/planning.form?numMen=607\" onclick='loadInfoDialog()'>Planning</a>\n" +
            "            </li>\n" +
            "            <li><a href=\"/pers/fichePersonnel.htm?numMen=612\" onclick='loadInfoDialog()'>Fiche\n" +
            "                Personnel</a></li>\n" +
            "            <li><a href=\"/app/pref.form?numMen=609\" onclick='loadInfoDialog()'>Pr�f�rences</a></li>\n" +
            "            <li><a href=\"/app/aide.htm?numMen=610\" onclick='loadInfoDialog()'>Aide</a></li>\n" +
            "            <li><a href=\"/logout.htm?numMen=611\" onclick='loadInfoDialog()'>Quitter</a></li>\n" +
            "        </ul>\n" +
            "    </div>\n" +
            "    <div id=\"main-menu\">\n" +
            "        <div id=\"contents-menu\"><h1>Top</h1>\n" +
            "\n" +
            "            <p>Il est <span id=\"time\"></span></p>\n" +
            "            <script language=\"JavaScript\">\n" +
            "                \t\t\tvar dt= new Date(0,0,0,'13','05','04');\t\t\tvar serveur_heu = dt.getHours();\t\t\tvar serveur_min = dt.getMinutes(); \t\t\tvar serveur_sec = dt.getSeconds();\t\t\tvar timer=setInterval(\"horloge('time')\", 1000);\n" +
            "\n" +
            "            </script>\n" +
            "            <p>Cliquer sur le bouton pour toper...</p>\n" +
            "\n" +
            "            <form name=\"formTop\" action=\"top.form\" id=\"formTop\" method=\"post\"\n" +
            "                  onsubmit=\"return desactiveForm(this);\"><input class=\"button\" id=\"boutonToper\"\n" +
            "                                                                type=\"submit\" value=\"Toper\"></form>\n" +
            "            <hr/>\n" +
            "            <div id=\"rappelTop\"> Rappel des tops de la journ�e\n" +
            "                <ul>\n" +
            "                    <li class=\"Tops d'absence\"> 07:56</li>\n" +
            "                    <li class=\"Tops d'absence\"> 11:45</li>\n" +
            "                    <li title=\"Top r�el\"> 13:00</li>\n" +
            "                    <li title=\"Top r�el\"> 17:00</li>\n" +
            "                </ul>\n" +
            "                <p><span>Tops r�els</span> <br/> <span class=\"top-absence\">Tops d'absence</span></p>\n" +
            "            </div>\n" +
            "            <div class=\"spacer\">&nbsp;</div>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "    <div id=\"dhtmltooltip\"></div>\n" +
            "    <div id=\"dhtmltooltipJS\">\n" +
            "        <script src=\"/media/js/dhtmlToolTip.js\" type=\"text/javascript\"></script>\n" +
            "    </div>\n" +
            "    <div id=\"pied\">\n" +
            "        <div class=\"info\">Profil : Personnel</div>\n" +
            "        <div>Le logiciel Agatte a fait l'objet d'une d�claration � la Commission Nationale de\n" +
            "            l'Informatique et des Libert�s (CNIL), enregistr�e sous le N� 1005966. Selon la loi\n" +
            "            78-17 du 6 janvier 1978 sur l'informatique et les libert�s, vous b�n�ficiez d'un droit\n" +
            "            d'information et de rectification sur les renseignements vous concernant qui sont saisis\n" +
            "            dans le logiciel. Si vous souhaitez utiliser ce droit, veuillez contacter la DRH -\n" +
            "            Pr�sidence de l'Universit�.\n" +
            "        </div>\n" +
            "        <p>&copy; 2013 - Universit� de Lorraine</p></div>\n" +
            "</div>\n" +
            "</body>\n" +
            "</html>";
}
