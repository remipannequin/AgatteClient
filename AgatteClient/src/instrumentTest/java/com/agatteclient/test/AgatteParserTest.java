package com.agatteclient.test;

import android.test.AndroidTestCase;

import com.agatteclient.AgatteParser;
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

/**
 * Created by remi on 03/10/13.
 */
public class AgatteParserTest extends AndroidTestCase {

    public void testParseResponse() throws Exception {
        AgatteParser instance  = AgatteParser.getInstance();
        HttpResponse test = new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), 20, "OK");
        BasicHttpEntity test_entity = new BasicHttpEntity();
        test_entity.setContent(new ByteArrayInputStream(response_test1.getBytes()));
        test.setEntity(test_entity);

        instance.parse_query_response(test);
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

}
