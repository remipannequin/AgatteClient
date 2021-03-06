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

import com.agatteclient.agatte.AgatteNetworkNotAuthorizedException;
import com.agatteclient.agatte.AgatteParser;
import com.agatteclient.agatte.AgatteResponse;
import com.agatteclient.agatte.AgatteSecret;
import com.agatteclient.agatte.CounterPage;



@SuppressWarnings({"HardcodedLineSeparator", "HardCodedStringLiteral"})
public class AgatteParserTest extends AndroidTestCase {

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
    private final static String response_test6 = "<html>\t<head>\t\t \t<meta http-equiv=\"refresh\" content=\"600;URL=/logout.htm\"><link rel=\"stylesheet\" href=\"/media/css/normalize.css\" type=\"text/css\"/> <link rel=\"stylesheet\" href=\"/media/css/agatte.css?Fri Nov 08 11:41:09 CET 2013\" type=\"text/css\"/><link rel=\"stylesheet\" href=\"/media/css/print.css\" type=\"text/css\" media=\"print\"/><link rel=\"stylesheet\" href=\"/media/css/displaytag.css\" type=\"text/css\"/><style type=\"text/css\" media=\"screen\">@import \"/media/css/tabs.css\";</style>\n" +
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
            "\t\t<div id=\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\"main-menu\"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t >\t\t\t<div id=\t\t\t\t\t\t\t\t\t\t\t\t\t\t\"contents-menu\"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t>\t\t\t\t<h1>Top</h1>\n" +
            "<p>Top pris en compte � 09:00</p>\n" +
            "<hr/><div id=\"rappelTop\">\tRappel des tops de la journ�e\t<ul>\t\t\t\t\t<li \t\t\t\t\t\t\t\t\ttitle=\"Top r�el\"\t\t\t\t\t\t\t\t\t\t\t\t>\t\t\t\t09:00\t\t\t</li>\n" +
            "\t\t\t</ul>\n" +
            "\t<p>\t\t<span>Tops r�els</span>\n" +
            "\t\t<br/>\t\t<span class=\"top-absence\">Tops d'absence</span>\n" +
            "\t</p>\n" +
            "</div>\n" +
            "<div class=\"spacer\">&nbsp;</div>\n" +
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
    private static final String response_counter1 = "<html>\t<head>\t\t \t<meta http-equiv=\"refresh\" content=\"600;URL=/logout.htm\"><link rel=\"stylesheet\" href=\"/media/css/normalize.css\" type=\"text/css\"/> <link rel=\"stylesheet\" href=\"/media/css/agatte.css?Mon Apr 14 14:23:39 CEST 2014\" type=\"text/css\"/><link rel=\"stylesheet\" href=\"/media/css/print.css\" type=\"text/css\" media=\"print\"/><link rel=\"stylesheet\" href=\"/media/css/displaytag.css\" type=\"text/css\"/><style type=\"text/css\" media=\"screen\">@import \"/media/css/tabs.css\";</style>\n" +
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
            "\t\t<div id=\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\"main-menu\"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t >\t\t\t<div id=\t\t\t\t\t\t\t\t\t\t\t\t\t\t\"contents-menu\"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t>\t\t\t\t<div id=\"dwr\"><script type='text/javascript' src='/dwr/interface/anneeDwr.js'></script>\n" +
            "<script type='text/javascript' src='/dwr/engine.js'></script>\n" +
            "<script type='text/javascript' src='/dwr/util.js'></script>\n" +
            "<script type='text/javascript'>\tvar anneeCallback = function(annees) {\t\tDWRUtil.removeAllOptions('codAnu');\t\tDWRUtil.addOptions('codAnu', annees,'codAnu','libAnu');\t\tanneeDwr.listerSemaine(document.getElementById('codAnu').value,semaineCallback);\t}\tvar semaineCallback = function(semaines) {\t\tDWRUtil.removeAllOptions('numSem');\t\tDWRUtil.addOptions('numSem', semaines,'numSemaine','libSemaine');\t}</script>\n" +
            "</div>\n" +
            "<h1>Feuille des tops</h1>\n" +
            "\t\t\t\t<form id=\"formFeuilleTop\" name=\"formFeuilleTop\" method=\"post\" action=\"feuille-top.form\">\t\t<div>\t\t\t<label>Contrat</label>\n" +
            "\t\t\t\t\t\t\t<select name=\"numCont\" id=\"numCont\" onchange=\"anneeDwr.listerAnneeContrat(this.value,119309,anneeCallback);\">\t\t\t\t\t\t\t\t\t\t\t<option value=\"10259\"\t\t\t\t\t\t\tselected>\t\t\t\t\t\t\tDu 01/09/2013\t\t\t\t\t\t\t\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t</select>\n" +
            "\t\t\t\t\t</div>\n" +
            "\t\t<div>\t\t\t<label>Ann�e</label>\n" +
            "\t\t\t\t\t\t\t<select name=\"codAnu\" id=\"codAnu\" \t\t\t\t\t\tonchange=\"anneeDwr.listerSemaine(this.value,semaineCallback);if (getValRadio(document.formFeuilleTop.nivCpt) == 'A') formFeuilleTop.submit();\">\t\t\t\t\t\t\t\t\t\t\t<option value=\"2013\"\t\t\t\t\t\t\tselected>\t\t\t\t\t\t\tAnnee 2013/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t</select>\n" +
            "\t\t\t\t\t</div>\n" +
            "\t\t<div>\t\t\t<label>Compteurs</label>\n" +
            "\t\t\t\t\t\t\t<input type=\"radio\" name=\"nivCpt\" id=\"nivCpt\" value=\"A\" onclick=\"disableInputs(['numSem','jour','cal']);enableInputs(['codAnu','numCont'])\" />\t\t\t\t\t\t\tAnnuels | \t\t\t\t\t\t\t<input type=\"radio\" name=\"nivCpt\" id=\"nivCpt\" value=\"H\" onclick=\"disableInputs(['jour','cal']);enableInputs(['numSem','codAnu','numCont'])\" checked/>\t\t\t\t\t\tHebdomadaires\t\t\t\t\t\t\t<select name=\"numSem\" id=\"numSem\" onchange=\"formFeuilleTop.submit()\">\t\t\t\t\t\t\t\t\t\t\t<option value=\"201335\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 35 du 01/09/2013 au 01/09/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201336\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 36 du 02/09/2013 au 08/09/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201337\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 37 du 09/09/2013 au 15/09/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201338\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 38 du 16/09/2013 au 22/09/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201339\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 39 du 23/09/2013 au 29/09/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201340\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 40 du 30/09/2013 au 06/10/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201341\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 41 du 07/10/2013 au 13/10/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201342\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 42 du 14/10/2013 au 20/10/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201343\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 43 du 21/10/2013 au 27/10/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201344\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 44 du 28/10/2013 au 03/11/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201345\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 45 du 04/11/2013 au 10/11/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201346\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 46 du 11/11/2013 au 17/11/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201347\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 47 du 18/11/2013 au 24/11/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201348\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 48 du 25/11/2013 au 01/12/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201349\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 49 du 02/12/2013 au 08/12/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201350\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 50 du 09/12/2013 au 15/12/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201351\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 51 du 16/12/2013 au 22/12/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201352\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 52 du 23/12/2013 au 29/12/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201401\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 1 du 30/12/2013 au 05/01/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201402\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 2 du 06/01/2014 au 12/01/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201403\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 3 du 13/01/2014 au 19/01/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201404\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 4 du 20/01/2014 au 26/01/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201405\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 5 du 27/01/2014 au 02/02/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201406\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 6 du 03/02/2014 au 09/02/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201407\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 7 du 10/02/2014 au 16/02/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201408\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 8 du 17/02/2014 au 23/02/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201409\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 9 du 24/02/2014 au 02/03/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201410\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 10 du 03/03/2014 au 09/03/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201411\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 11 du 10/03/2014 au 16/03/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201412\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 12 du 17/03/2014 au 23/03/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201413\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 13 du 24/03/2014 au 30/03/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201414\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 14 du 31/03/2014 au 06/04/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201415\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 15 du 07/04/2014 au 13/04/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201416\"\t\t\t\t\t\t\tselected>\t\t\t\t\t\t\tSemaine 16 du 14/04/2014 au 20/04/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201417\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 17 du 21/04/2014 au 27/04/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201418\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 18 du 28/04/2014 au 04/05/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201419\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 19 du 05/05/2014 au 11/05/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201420\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 20 du 12/05/2014 au 18/05/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201421\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 21 du 19/05/2014 au 25/05/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201422\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 22 du 26/05/2014 au 01/06/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201423\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 23 du 02/06/2014 au 08/06/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201424\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 24 du 09/06/2014 au 15/06/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201425\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 25 du 16/06/2014 au 22/06/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201426\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 26 du 23/06/2014 au 29/06/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201427\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 27 du 30/06/2014 au 06/07/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201428\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 28 du 07/07/2014 au 13/07/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201429\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 29 du 14/07/2014 au 20/07/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201430\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 30 du 21/07/2014 au 27/07/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201431\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 31 du 28/07/2014 au 03/08/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201432\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 32 du 04/08/2014 au 10/08/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201433\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 33 du 11/08/2014 au 17/08/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201434\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 34 du 18/08/2014 au 24/08/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201435\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 35 du 25/08/2014 au 31/08/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t</select>\n" +
            "\t\t\t\t\t\t\t\t</div>\n" +
            "\t\t<div>\t\t\t<label>&nbsp;</label>\n" +
            "\t\t\t<input type=\"submit\" class=\"button\" value=\"Valider\">\t\t</div>\n" +
            "\t</form>\n" +
            "\t<hr/>\t\t\t\t<div class=\"error\">Compteurs non disponibles</div>\n" +
            "\t\t\t\t\t\t\t\t<div class=\"spacer\">&nbsp;</div>\n" +
            "\t\t<script type=\"text/javascript\">\t\t \t\tinitCalendrier(\"jour\",\"cal\"); \t\t\t\tinitNivCpt(document.formFeuilleTop.nivCpt);\t</script>\n" +
            "\t\t\t\t\t</div>\n" +
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
    private static final String response_counter2 = "<html>\t<head>\t\t \t<meta http-equiv=\"refresh\" content=\"600;URL=/logout.htm\"><link rel=\"stylesheet\" href=\"/media/css/normalize.css\" type=\"text/css\"/> <link rel=\"stylesheet\" href=\"/media/css/agatte.css?Mon Apr 14 22:25:16 CEST 2014\" type=\"text/css\"/><link rel=\"stylesheet\" href=\"/media/css/print.css\" type=\"text/css\" media=\"print\"/><link rel=\"stylesheet\" href=\"/media/css/displaytag.css\" type=\"text/css\"/><style type=\"text/css\" media=\"screen\">@import \"/media/css/tabs.css\";</style>\n" +
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
            "\t\t<div id=\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\"main-menu\"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t >\t\t\t<div id=\t\t\t\t\t\t\t\t\t\t\t\t\t\t\"contents-menu\"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t>\t\t\t\t<div id=\"dwr\"><script type='text/javascript' src='/dwr/interface/anneeDwr.js'></script>\n" +
            "<script type='text/javascript' src='/dwr/engine.js'></script>\n" +
            "<script type='text/javascript' src='/dwr/util.js'></script>\n" +
            "<script type='text/javascript'>\tvar anneeCallback = function(annees) {\t\tDWRUtil.removeAllOptions('codAnu');\t\tDWRUtil.addOptions('codAnu', annees,'codAnu','libAnu');\t\tanneeDwr.listerSemaine(document.getElementById('codAnu').value,semaineCallback);\t}\tvar semaineCallback = function(semaines) {\t\tDWRUtil.removeAllOptions('numSem');\t\tDWRUtil.addOptions('numSem', semaines,'numSemaine','libSemaine');\t}</script>\n" +
            "</div>\n" +
            "<h1>Feuille des tops</h1>\n" +
            "\t\t\t\t<form id=\"formFeuilleTop\" name=\"formFeuilleTop\" method=\"post\" action=\"feuille-top.form\">\t\t<div>\t\t\t<label>Contrat</label>\n" +
            "\t\t\t\t\t\t\t<select name=\"numCont\" id=\"numCont\" onchange=\"anneeDwr.listerAnneeContrat(this.value,119309,anneeCallback);\">\t\t\t\t\t\t\t\t\t\t\t<option value=\"10259\"\t\t\t\t\t\t\tselected>\t\t\t\t\t\t\tDu 01/09/2013\t\t\t\t\t\t\t\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t</select>\n" +
            "\t\t\t\t\t</div>\n" +
            "\t\t<div>\t\t\t<label>Ann�e</label>\n" +
            "\t\t\t\t\t\t\t<select name=\"codAnu\" id=\"codAnu\" \t\t\t\t\t\tonchange=\"anneeDwr.listerSemaine(this.value,semaineCallback);if (getValRadio(document.formFeuilleTop.nivCpt) == 'A') formFeuilleTop.submit();\">\t\t\t\t\t\t\t\t\t\t\t<option value=\"2013\"\t\t\t\t\t\t\tselected>\t\t\t\t\t\t\tAnnee 2013/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t</select>\n" +
            "\t\t\t\t\t</div>\n" +
            "\t\t<div>\t\t\t<label>Compteurs</label>\n" +
            "\t\t\t\t\t\t\t<input type=\"radio\" name=\"nivCpt\" id=\"nivCpt\" value=\"A\" onclick=\"disableInputs(['numSem','jour','cal']);enableInputs(['codAnu','numCont'])\" />\t\t\t\t\t\t\tAnnuels | \t\t\t\t\t\t\t<input type=\"radio\" name=\"nivCpt\" id=\"nivCpt\" value=\"H\" onclick=\"disableInputs(['jour','cal']);enableInputs(['numSem','codAnu','numCont'])\" checked/>\t\t\t\t\t\tHebdomadaires\t\t\t\t\t\t\t<select name=\"numSem\" id=\"numSem\" onchange=\"formFeuilleTop.submit()\">\t\t\t\t\t\t\t\t\t\t\t<option value=\"201335\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 35 du 01/09/2013 au 01/09/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201336\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 36 du 02/09/2013 au 08/09/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201337\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 37 du 09/09/2013 au 15/09/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201338\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 38 du 16/09/2013 au 22/09/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201339\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 39 du 23/09/2013 au 29/09/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201340\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 40 du 30/09/2013 au 06/10/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201341\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 41 du 07/10/2013 au 13/10/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201342\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 42 du 14/10/2013 au 20/10/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201343\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 43 du 21/10/2013 au 27/10/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201344\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 44 du 28/10/2013 au 03/11/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201345\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 45 du 04/11/2013 au 10/11/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201346\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 46 du 11/11/2013 au 17/11/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201347\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 47 du 18/11/2013 au 24/11/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201348\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 48 du 25/11/2013 au 01/12/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201349\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 49 du 02/12/2013 au 08/12/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201350\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 50 du 09/12/2013 au 15/12/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201351\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 51 du 16/12/2013 au 22/12/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201352\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 52 du 23/12/2013 au 29/12/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201401\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 1 du 30/12/2013 au 05/01/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201402\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 2 du 06/01/2014 au 12/01/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201403\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 3 du 13/01/2014 au 19/01/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201404\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 4 du 20/01/2014 au 26/01/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201405\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 5 du 27/01/2014 au 02/02/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201406\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 6 du 03/02/2014 au 09/02/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201407\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 7 du 10/02/2014 au 16/02/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201408\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 8 du 17/02/2014 au 23/02/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201409\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 9 du 24/02/2014 au 02/03/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201410\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 10 du 03/03/2014 au 09/03/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201411\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 11 du 10/03/2014 au 16/03/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201412\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 12 du 17/03/2014 au 23/03/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201413\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 13 du 24/03/2014 au 30/03/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201414\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 14 du 31/03/2014 au 06/04/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201415\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 15 du 07/04/2014 au 13/04/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201416\"\t\t\t\t\t\t\tselected>\t\t\t\t\t\t\tSemaine 16 du 14/04/2014 au 20/04/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201417\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 17 du 21/04/2014 au 27/04/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201418\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 18 du 28/04/2014 au 04/05/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201419\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 19 du 05/05/2014 au 11/05/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201420\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 20 du 12/05/2014 au 18/05/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201421\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 21 du 19/05/2014 au 25/05/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201422\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 22 du 26/05/2014 au 01/06/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201423\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 23 du 02/06/2014 au 08/06/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201424\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 24 du 09/06/2014 au 15/06/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201425\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 25 du 16/06/2014 au 22/06/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201426\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 26 du 23/06/2014 au 29/06/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201427\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 27 du 30/06/2014 au 06/07/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201428\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 28 du 07/07/2014 au 13/07/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201429\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 29 du 14/07/2014 au 20/07/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201430\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 30 du 21/07/2014 au 27/07/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201431\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 31 du 28/07/2014 au 03/08/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201432\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 32 du 04/08/2014 au 10/08/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201433\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 33 du 11/08/2014 au 17/08/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201434\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 34 du 18/08/2014 au 24/08/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201435\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 35 du 25/08/2014 au 31/08/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t</select>\n" +
            "\t\t\t\t\t\t\t\t</div>\n" +
            "\t\t<div>\t\t\t<label>&nbsp;</label>\n" +
            "\t\t\t<input type=\"submit\" class=\"button\" value=\"Valider\">\t\t</div>\n" +
            "\t</form>\n" +
            "\t<hr/>\t\t\t\t\t\t<div id=\"cpt\">\t\t\t<h3>Compteurs</h3>\n" +
            "\t\t\t<ul class=\"cptWeb\">\t\t\t\t\t\t\t\t\t<li><span><a href=\"#\" onClick=\"maskViewNoeuds('TCRED','/media/img/lien_plus.gif','/media/img/lien_moins.gif');\"><img src=\"/media/img/lien_plus.gif\" class=\"imgNoeud\" id=\"imgTCRED\"></a></span><div class='tooltip' style=\"display:none;\"></div><a class=\"highlight\" href=\"#\"><span><span style=\"width:21em\">Temps valid�</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 0 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Temps valid�</h3><p>Temps badg� valid�+ cong�s pay�s (hors r�gularisation) + absences cr�ditrices + temps cr�dit� jours f�ri�s + (ou -) r�gularisations</p></div></div></span></a><ul id=\"TCRED\" style=\"display: none\"><li><span><a href=\"#\" onClick=\"maskViewNoeuds('null','/media/img/lien_plus.gif','/media/img/lien_moins.gif');\"><img src=\"/media/img/lien_plus.gif\" class=\"imgNoeud\" id=\"imgnull\"></a></span><div class='tooltip' style=\"display:none;\"></div><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">Temps badg� valid�</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 0 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Temps badg� valid�</h3><p>Temps base de calcul - �cr�tages journaliers - �cr�tages hebdomadaires</p></div></div></span></a><ul id=\"null\" style=\"display: none\"><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:17em\">+ Temps base de calcul</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 0 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>+ Temps base de calcul</h3><p>Temps de travail 'top�', calcul� sur vos tops �ventuellement corrig�s par le gestionnaire</p></div></div></span></a></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:17em\">-  Ecr�tages journaliers</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 0 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>-  Ecr�tages journaliers</h3><p>Pauses m�ridiennes trop courtes et/ou temps de travail journalier > au nombre d'heures autoris� par jour</p></div></div></span></a></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:17em\">-  Ecr�tages hebdomadaires</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 0 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>-  Ecr�tages hebdomadaires</h3><p>Temps de travail > au temps de travail autoris� par semaine apr�s �cr�tages journaliers</p></div></div></span></a></li></ul></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">Absences cr�ditrices</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 0 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Absences cr�ditrices</h3><p>Absences consid�r�es comme temps travaill�</p></div></div></span></a><ul id=\"TABSC\" style=\"display: none\"></ul></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">Temps cr�dit� jours f�ri�s</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 0 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Temps cr�dit� jours f�ri�s</h3><p>Jour f�ri� = temps travaill� s'il tombe un jour habituellement travaill�</p></div></div></span></a></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">R�gularisations</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 0 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>R�gularisations</h3><p>Ajustements sur le temps de travail r�alis� du 01/09/03 � l'entr�e en application d'Agatte, cong�s pay�s inclus</p></div></div></span></a></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">Ecr�tage et ajustements annuels</span><span class=\"valCptWeb\" > 0 h 00 min</span></span></a></li></ul></li>\n" +
            "\t\t\t\t\t\t\t\t\t<li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:21em\">Absences non cr�ditrices</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 0 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Absences non cr�ditrices</h3><p>Temps d'absence non pris en compte dans le temps de travail</p></div></div></span></a><ul id=\"ABNOCRE\" style=\"display: none\"></ul></li>\n" +
            "\t\t\t\t\t\t\t\t\t<li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:21em\">Cong�s pay�s</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 0 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Cong�s pay�s</h3><p>Cong�s pay�s utilis�s depuis le d�but de l'ann�e universitaire convertis en heures</p></div></div></span></a></li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</ul>\n" +
            "\t\t</div>\t\n" +
            "\t\t\t\t\t\t<div id=\"tabBord\">\t\t\t<h3>Tableau de bord</h3>\n" +
            "\t\t\t<ul class=\"cptWeb\">\t\t\t\t\t\t\t\t\t<li><span><a href=\"#\" onClick=\"maskViewNoeuds('TDU','/media/img/lien_plus.gif','/media/img/lien_moins.gif');\"><img src=\"/media/img/lien_plus.gif\" class=\"imgNoeud\" id=\"imgTDU\"></a></span><div class='tooltip' style=\"display:none;\"></div><a class=\"highlight\" href=\"#\"><span><span style=\"width:21em\">R�f�rence annuelle</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 2043 h 22 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>R�f�rence annuelle</h3><p>Temps de travail annuel d� + cong�s pay�s</p></div></div></span></a><ul id=\"TDU\" style=\"display: none\"><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">Temps de travail d�</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 1593 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Temps de travail d�</h3><p>Temps de travail �  r�aliser par an</p></div></div></span></a></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">Cong�s pay�s</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 450 h 22 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Cong�s pay�s</h3><p>Droits � cong�s pay�s annuels convertis en heures</p></div></div></span></a></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">R�gularisations</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 0 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>R�gularisations</h3><p>Intervention baissant le temps annuel d� (gr�ve, C.I.F., cong�s sans solde,...)</p></div></div></span></a></li></ul></li>\n" +
            "\t\t\t\t\t\t\t\t\t<li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:21em\">R�f�rence pour la p�riode</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 0 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>R�f�rence pour la p�riode</h3><p>Temps de travail moyen et droits � cong�s � une date donn�e, calcul bas� sur votre 'contrat' Agatte</p></div></div></span></a></li>\n" +
            "\t\t\t\t\t\t\t\t\t<li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:21em\">Avance / Retard pour la p�riode</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 0 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Avance / Retard pour la p�riode</h3><p>R�f�rence pour la p�riode  - temps valid� = avance (+) ou retard (-) ou �quilibre (0 mn)</p></div></div></span></a></li>\n" +
            "\t\t\t\t\t\t\t\t\t<li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:21em\">Temps d'avance maximum annuel</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 140 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Temps d'avance maximum annuel</h3><p>Ecr�tage des heures suppl�mentaires r�alis�es au-del� de ce seuil</p></div></div></span></a></li>\n" +
            "\t\t\t\t\t\t\t</ul>\n" +
            "\t\t</div>\n" +
            "\t\t\t<div class=\"spacer\">&nbsp;</div>\n" +
            "\t\t<script type=\"text/javascript\">\t\t \t\tinitCalendrier(\"jour\",\"cal\"); \t\t\t\tinitNivCpt(document.formFeuilleTop.nivCpt);\t</script>\n" +
            "\t\t\t\t\t</div>\n" +
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
    private static final String response_counter3 = "<html>\t<head>\t\t \t<meta http-equiv=\"refresh\" content=\"600;URL=/logout.htm\"><link rel=\"stylesheet\" href=\"/media/css/normalize.css\" type=\"text/css\"/> <link rel=\"stylesheet\" href=\"/media/css/agatte.css?Mon Apr 14 22:40:47 CEST 2014\" type=\"text/css\"/><link rel=\"stylesheet\" href=\"/media/css/print.css\" type=\"text/css\" media=\"print\"/><link rel=\"stylesheet\" href=\"/media/css/displaytag.css\" type=\"text/css\"/><style type=\"text/css\" media=\"screen\">@import \"/media/css/tabs.css\";</style>\n" +
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
            "\t\t<div id=\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\"main-menu\"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t >\t\t\t<div id=\t\t\t\t\t\t\t\t\t\t\t\t\t\t\"contents-menu\"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t>\t\t\t\t<div id=\"dwr\"><script type='text/javascript' src='/dwr/interface/anneeDwr.js'></script>\n" +
            "<script type='text/javascript' src='/dwr/engine.js'></script>\n" +
            "<script type='text/javascript' src='/dwr/util.js'></script>\n" +
            "<script type='text/javascript'>\tvar anneeCallback = function(annees) {\t\tDWRUtil.removeAllOptions('codAnu');\t\tDWRUtil.addOptions('codAnu', annees,'codAnu','libAnu');\t\tanneeDwr.listerSemaine(document.getElementById('codAnu').value,semaineCallback);\t}\tvar semaineCallback = function(semaines) {\t\tDWRUtil.removeAllOptions('numSem');\t\tDWRUtil.addOptions('numSem', semaines,'numSemaine','libSemaine');\t}</script>\n" +
            "</div>\n" +
            "<h1>Feuille des tops</h1>\n" +
            "\t\t\t\t<form id=\"formFeuilleTop\" name=\"formFeuilleTop\" method=\"post\" action=\"feuille-top.form\">\t\t<div>\t\t\t<label>Contrat</label>\n" +
            "\t\t\t\t\t\t\t<select name=\"numCont\" id=\"numCont\" onchange=\"anneeDwr.listerAnneeContrat(this.value,119309,anneeCallback);\">\t\t\t\t\t\t\t\t\t\t\t<option value=\"10259\"\t\t\t\t\t\t\tselected>\t\t\t\t\t\t\tDu 01/09/2013\t\t\t\t\t\t\t\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t</select>\n" +
            "\t\t\t\t\t</div>\n" +
            "\t\t<div>\t\t\t<label>Ann�e</label>\n" +
            "\t\t\t\t\t\t\t<select name=\"codAnu\" id=\"codAnu\" \t\t\t\t\t\tonchange=\"anneeDwr.listerSemaine(this.value,semaineCallback);if (getValRadio(document.formFeuilleTop.nivCpt) == 'A') formFeuilleTop.submit();\">\t\t\t\t\t\t\t\t\t\t\t<option value=\"2013\"\t\t\t\t\t\t\tselected>\t\t\t\t\t\t\tAnnee 2013/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t</select>\n" +
            "\t\t\t\t\t</div>\n" +
            "\t\t<div>\t\t\t<label>Compteurs</label>\n" +
            "\t\t\t\t\t\t\t<input type=\"radio\" name=\"nivCpt\" id=\"nivCpt\" value=\"A\" onclick=\"disableInputs(['numSem','jour','cal']);enableInputs(['codAnu','numCont'])\" checked/>\t\t\t\t\t\t\tAnnuels | \t\t\t\t\t\t\t<input type=\"radio\" name=\"nivCpt\" id=\"nivCpt\" value=\"H\" onclick=\"disableInputs(['jour','cal']);enableInputs(['numSem','codAnu','numCont'])\" />\t\t\t\t\t\tHebdomadaires\t\t\t\t\t\t\t<select name=\"numSem\" id=\"numSem\" onchange=\"formFeuilleTop.submit()\">\t\t\t\t\t\t\t\t\t\t\t<option value=\"201335\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 35 du 01/09/2013 au 01/09/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201336\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 36 du 02/09/2013 au 08/09/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201337\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 37 du 09/09/2013 au 15/09/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201338\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 38 du 16/09/2013 au 22/09/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201339\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 39 du 23/09/2013 au 29/09/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201340\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 40 du 30/09/2013 au 06/10/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201341\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 41 du 07/10/2013 au 13/10/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201342\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 42 du 14/10/2013 au 20/10/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201343\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 43 du 21/10/2013 au 27/10/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201344\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 44 du 28/10/2013 au 03/11/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201345\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 45 du 04/11/2013 au 10/11/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201346\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 46 du 11/11/2013 au 17/11/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201347\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 47 du 18/11/2013 au 24/11/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201348\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 48 du 25/11/2013 au 01/12/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201349\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 49 du 02/12/2013 au 08/12/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201350\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 50 du 09/12/2013 au 15/12/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201351\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 51 du 16/12/2013 au 22/12/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201352\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 52 du 23/12/2013 au 29/12/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201401\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 1 du 30/12/2013 au 05/01/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201402\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 2 du 06/01/2014 au 12/01/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201403\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 3 du 13/01/2014 au 19/01/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201404\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 4 du 20/01/2014 au 26/01/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201405\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 5 du 27/01/2014 au 02/02/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201406\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 6 du 03/02/2014 au 09/02/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201407\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 7 du 10/02/2014 au 16/02/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201408\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 8 du 17/02/2014 au 23/02/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201409\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 9 du 24/02/2014 au 02/03/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201410\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 10 du 03/03/2014 au 09/03/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201411\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 11 du 10/03/2014 au 16/03/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201412\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 12 du 17/03/2014 au 23/03/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201413\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 13 du 24/03/2014 au 30/03/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201414\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 14 du 31/03/2014 au 06/04/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201415\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 15 du 07/04/2014 au 13/04/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201416\"\t\t\t\t\t\t\tselected>\t\t\t\t\t\t\tSemaine 16 du 14/04/2014 au 20/04/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201417\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 17 du 21/04/2014 au 27/04/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201418\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 18 du 28/04/2014 au 04/05/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201419\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 19 du 05/05/2014 au 11/05/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201420\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 20 du 12/05/2014 au 18/05/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201421\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 21 du 19/05/2014 au 25/05/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201422\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 22 du 26/05/2014 au 01/06/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201423\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 23 du 02/06/2014 au 08/06/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201424\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 24 du 09/06/2014 au 15/06/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201425\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 25 du 16/06/2014 au 22/06/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201426\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 26 du 23/06/2014 au 29/06/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201427\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 27 du 30/06/2014 au 06/07/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201428\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 28 du 07/07/2014 au 13/07/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201429\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 29 du 14/07/2014 au 20/07/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201430\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 30 du 21/07/2014 au 27/07/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201431\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 31 du 28/07/2014 au 03/08/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201432\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 32 du 04/08/2014 au 10/08/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201433\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 33 du 11/08/2014 au 17/08/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201434\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 34 du 18/08/2014 au 24/08/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201435\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 35 du 25/08/2014 au 31/08/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t</select>\n" +
            "\t\t\t\t\t\t\t\t</div>\n" +
            "\t\t<div>\t\t\t<label>&nbsp;</label>\n" +
            "\t\t\t<input type=\"submit\" class=\"button\" value=\"Valider\">\t\t</div>\n" +
            "\t</form>\n" +
            "\t<hr/>\t\t\t\t\t\t<div id=\"cpt\">\t\t\t<h3>Compteurs</h3>\n" +
            "\t\t\t<ul class=\"cptWeb\">\t\t\t\t\t\t\t\t\t<li><span><a href=\"#\" onClick=\"maskViewNoeuds('TCRED','/media/img/lien_plus.gif','/media/img/lien_moins.gif');\"><img src=\"/media/img/lien_plus.gif\" class=\"imgNoeud\" id=\"imgTCRED\"></a></span><div class='tooltip' style=\"display:none;\"></div><a class=\"highlight\" href=\"#\"><span><span style=\"width:21em\">Temps valid�</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 1245 h 43 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Temps valid�</h3><p>Temps badg� valid�+ cong�s pay�s (hors r�gularisation) + absences cr�ditrices + temps cr�dit� jours f�ri�s + (ou -) r�gularisations</p></div></div></span></a><ul id=\"TCRED\" style=\"display: none\"><li><span><a href=\"#\" onClick=\"maskViewNoeuds('null','/media/img/lien_plus.gif','/media/img/lien_moins.gif');\"><img src=\"/media/img/lien_plus.gif\" class=\"imgNoeud\" id=\"imgnull\"></a></span><div class='tooltip' style=\"display:none;\"></div><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">Temps badg� valid�</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 1024 h 21 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Temps badg� valid�</h3><p>Temps base de calcul - �cr�tages journaliers - �cr�tages hebdomadaires</p></div></div></span></a><ul id=\"null\" style=\"display: none\"><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:17em\">+ Temps base de calcul</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 1042 h 14 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>+ Temps base de calcul</h3><p>Temps de travail 'top�', calcul� sur vos tops �ventuellement corrig�s par le gestionnaire</p></div></div></span></a></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:17em\">-  Ecr�tages journaliers</span><span class=\"valCptWeb\"  style=\"cursor: help;\">-17 h 53 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>-  Ecr�tages journaliers</h3><p>Pauses m�ridiennes trop courtes et/ou temps de travail journalier > au nombre d'heures autoris� par jour</p></div></div></span></a></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:17em\">-  Ecr�tages hebdomadaires</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 0 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>-  Ecr�tages hebdomadaires</h3><p>Temps de travail > au temps de travail autoris� par semaine apr�s �cr�tages journaliers</p></div></div></span></a></li></ul></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">Cong�s pay�s (hors r�gularisation)</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 171 h 45 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Cong�s pay�s (hors r�gularisation)</h3><p>Jours de cong�s pay�s convertis en heures pris depuis la mise en place d?Agatte</p></div></div></span></a></li><li><span><a href=\"#\" onClick=\"maskViewNoeuds('TABSC','/media/img/lien_plus.gif','/media/img/lien_moins.gif');\"><img src=\"/media/img/lien_plus.gif\" class=\"imgNoeud\" id=\"imgTABSC\"></a></span><div class='tooltip' style=\"display:none;\"></div><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">Absences cr�ditrices</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 19 h 05 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Absences cr�ditrices</h3><p>Absences consid�r�es comme temps travaill�</p></div></div></span></a><ul id=\"TABSC\" style=\"display: none\"><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:17em\">Garde d'enfant malade</span><span class=\"valCptWeb\" > 11 h 27 min</span></span></a></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:17em\">Mission - jours et/ou demi-journ�es</span><span class=\"valCptWeb\" > 7 h 38 min</span></span></a></li></ul></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">Temps cr�dit� jours f�ri�s</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 30 h 32 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Temps cr�dit� jours f�ri�s</h3><p>Jour f�ri� = temps travaill� s'il tombe un jour habituellement travaill�</p></div></div></span></a></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">R�gularisations</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 0 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>R�gularisations</h3><p>Ajustements sur le temps de travail r�alis� du 01/09/03 � l'entr�e en application d'Agatte, cong�s pay�s inclus</p></div></div></span></a></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">Ecr�tage et ajustements annuels</span><span class=\"valCptWeb\" > 0 h 00 min</span></span></a></li></ul></li>\n" +
            "\t\t\t\t\t\t\t\t\t<li><span><a href=\"#\" onClick=\"maskViewNoeuds('ABNOCRE','/media/img/lien_plus.gif','/media/img/lien_moins.gif');\"><img src=\"/media/img/lien_plus.gif\" class=\"imgNoeud\" id=\"imgABNOCRE\"></a></span><div class='tooltip' style=\"display:none;\"></div><a class=\"highlight\" href=\"#\"><span><span style=\"width:21em\">Absences non cr�ditrices</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 15 h 16 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Absences non cr�ditrices</h3><p>Temps d'absence non pris en compte dans le temps de travail</p></div></div></span></a><ul id=\"ABNOCRE\" style=\"display: none\"><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">R�cup�ration en jours-demi journ�es</span><span class=\"valCptWeb\" > 15 h 16 min</span></span></a></li></ul></li>\n" +
            "\t\t\t\t\t\t\t\t\t<li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:21em\">Cong�s pay�s</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 171 h 45 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Cong�s pay�s</h3><p>Cong�s pay�s utilis�s depuis le d�but de l'ann�e universitaire convertis en heures</p></div></div></span></a></li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</ul>\n" +
            "\t\t</div>\t\n" +
            "\t\t\t\t\t\t<div id=\"tabBord\">\t\t\t<h3>Tableau de bord</h3>\n" +
            "\t\t\t<ul class=\"cptWeb\">\t\t\t\t\t\t\t\t\t<li><span><a href=\"#\" onClick=\"maskViewNoeuds('TDU','/media/img/lien_plus.gif','/media/img/lien_moins.gif');\"><img src=\"/media/img/lien_plus.gif\" class=\"imgNoeud\" id=\"imgTDU\"></a></span><div class='tooltip' style=\"display:none;\"></div><a class=\"highlight\" href=\"#\"><span><span style=\"width:21em\">R�f�rence annuelle</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 2043 h 22 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>R�f�rence annuelle</h3><p>Temps de travail annuel d� + cong�s pay�s</p></div></div></span></a><ul id=\"TDU\" style=\"display: none\"><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">Temps de travail d�</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 1593 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Temps de travail d�</h3><p>Temps de travail �  r�aliser par an</p></div></div></span></a></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">Cong�s pay�s</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 450 h 22 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Cong�s pay�s</h3><p>Droits � cong�s pay�s annuels convertis en heures</p></div></div></span></a></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">R�gularisations</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 0 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>R�gularisations</h3><p>Intervention baissant le temps annuel d� (gr�ve, C.I.F., cong�s sans solde,...)</p></div></div></span></a></li></ul></li>\n" +
            "\t\t\t\t\t\t\t\t\t<li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:21em\">R�f�rence pour la p�riode</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 1221 h 20 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>R�f�rence pour la p�riode</h3><p>Temps de travail moyen et droits � cong�s � une date donn�e, calcul bas� sur votre 'contrat' Agatte</p></div></div></span></a></li>\n" +
            "\t\t\t\t\t\t\t\t\t<li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:21em\">Avance / Retard pour la p�riode</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 24 h 23 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Avance / Retard pour la p�riode</h3><p>R�f�rence pour la p�riode  - temps valid� = avance (+) ou retard (-) ou �quilibre (0 mn)</p></div></div></span></a></li>\n" +
            "\t\t\t\t\t\t\t\t\t<li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:21em\">Temps d'avance maximum annuel</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 140 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Temps d'avance maximum annuel</h3><p>Ecr�tage des heures suppl�mentaires r�alis�es au-del� de ce seuil</p></div></div></span></a></li>\n" +
            "\t\t\t\t\t\t\t</ul>\n" +
            "\t\t</div>\n" +
            "\t\t\t<div class=\"spacer\">&nbsp;</div>\n" +
            "\t\t<script type=\"text/javascript\">\t\t \t\tinitCalendrier(\"jour\",\"cal\"); \t\t\t\tinitNivCpt(document.formFeuilleTop.nivCpt);\t</script>\n" +
            "\t\t\t\t\t</div>\n" +
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
    private static final String response_counter4 = "<html>\t<head>\t\t \t<meta http-equiv=\"refresh\" content=\"600;URL=/logout.htm\"><link rel=\"stylesheet\" href=\"/media/css/normalize.css\" type=\"text/css\"/> <link rel=\"stylesheet\" href=\"/media/css/agatte.css?Mon Apr 14 22:52:20 CEST 2014\" type=\"text/css\"/><link rel=\"stylesheet\" href=\"/media/css/print.css\" type=\"text/css\" media=\"print\"/><link rel=\"stylesheet\" href=\"/media/css/displaytag.css\" type=\"text/css\"/><style type=\"text/css\" media=\"screen\">@import \"/media/css/tabs.css\";</style>\n" +
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
            "\t\t<div id=\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\"main-menu\"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t >\t\t\t<div id=\t\t\t\t\t\t\t\t\t\t\t\t\t\t\"contents-menu\"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t>\t\t\t\t<div id=\"dwr\"><script type='text/javascript' src='/dwr/interface/anneeDwr.js'></script>\n" +
            "<script type='text/javascript' src='/dwr/engine.js'></script>\n" +
            "<script type='text/javascript' src='/dwr/util.js'></script>\n" +
            "<script type='text/javascript'>\tvar anneeCallback = function(annees) {\t\tDWRUtil.removeAllOptions('codAnu');\t\tDWRUtil.addOptions('codAnu', annees,'codAnu','libAnu');\t\tanneeDwr.listerSemaine(document.getElementById('codAnu').value,semaineCallback);\t}\tvar semaineCallback = function(semaines) {\t\tDWRUtil.removeAllOptions('numSem');\t\tDWRUtil.addOptions('numSem', semaines,'numSemaine','libSemaine');\t}</script>\n" +
            "</div>\n" +
            "<h1>Feuille des tops</h1>\n" +
            "\t\t\t\t<form id=\"formFeuilleTop\" name=\"formFeuilleTop\" method=\"post\" action=\"feuille-top.form\">\t\t<div>\t\t\t<label>Contrat</label>\n" +
            "\t\t\t\t\t\t\t<select name=\"numCont\" id=\"numCont\" onchange=\"anneeDwr.listerAnneeContrat(this.value,119309,anneeCallback);\">\t\t\t\t\t\t\t\t\t\t\t<option value=\"10259\"\t\t\t\t\t\t\tselected>\t\t\t\t\t\t\tDu 01/09/2013\t\t\t\t\t\t\t\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t</select>\n" +
            "\t\t\t\t\t</div>\n" +
            "\t\t<div>\t\t\t<label>Ann�e</label>\n" +
            "\t\t\t\t\t\t\t<select name=\"codAnu\" id=\"codAnu\" \t\t\t\t\t\tonchange=\"anneeDwr.listerSemaine(this.value,semaineCallback);if (getValRadio(document.formFeuilleTop.nivCpt) == 'A') formFeuilleTop.submit();\">\t\t\t\t\t\t\t\t\t\t\t<option value=\"2013\"\t\t\t\t\t\t\tselected>\t\t\t\t\t\t\tAnnee 2013/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t</select>\n" +
            "\t\t\t\t\t</div>\n" +
            "\t\t<div>\t\t\t<label>Compteurs</label>\n" +
            "\t\t\t\t\t\t\t<input type=\"radio\" name=\"nivCpt\" id=\"nivCpt\" value=\"A\" onclick=\"disableInputs(['numSem','jour','cal']);enableInputs(['codAnu','numCont'])\" />\t\t\t\t\t\t\tAnnuels | \t\t\t\t\t\t\t<input type=\"radio\" name=\"nivCpt\" id=\"nivCpt\" value=\"H\" onclick=\"disableInputs(['jour','cal']);enableInputs(['numSem','codAnu','numCont'])\" checked/>\t\t\t\t\t\tHebdomadaires\t\t\t\t\t\t\t<select name=\"numSem\" id=\"numSem\" onchange=\"formFeuilleTop.submit()\">\t\t\t\t\t\t\t\t\t\t\t<option value=\"201335\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 35 du 01/09/2013 au 01/09/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201336\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 36 du 02/09/2013 au 08/09/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201337\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 37 du 09/09/2013 au 15/09/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201338\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 38 du 16/09/2013 au 22/09/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201339\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 39 du 23/09/2013 au 29/09/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201340\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 40 du 30/09/2013 au 06/10/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201341\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 41 du 07/10/2013 au 13/10/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201342\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 42 du 14/10/2013 au 20/10/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201343\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 43 du 21/10/2013 au 27/10/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201344\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 44 du 28/10/2013 au 03/11/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201345\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 45 du 04/11/2013 au 10/11/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201346\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 46 du 11/11/2013 au 17/11/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201347\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 47 du 18/11/2013 au 24/11/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201348\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 48 du 25/11/2013 au 01/12/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201349\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 49 du 02/12/2013 au 08/12/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201350\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 50 du 09/12/2013 au 15/12/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201351\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 51 du 16/12/2013 au 22/12/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201352\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 52 du 23/12/2013 au 29/12/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201401\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 1 du 30/12/2013 au 05/01/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201402\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 2 du 06/01/2014 au 12/01/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201403\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 3 du 13/01/2014 au 19/01/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201404\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 4 du 20/01/2014 au 26/01/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201405\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 5 du 27/01/2014 au 02/02/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201406\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 6 du 03/02/2014 au 09/02/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201407\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 7 du 10/02/2014 au 16/02/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201408\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 8 du 17/02/2014 au 23/02/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201409\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 9 du 24/02/2014 au 02/03/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201410\"\t\t\t\t\t\t\tselected>\t\t\t\t\t\t\tSemaine 10 du 03/03/2014 au 09/03/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201411\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 11 du 10/03/2014 au 16/03/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201412\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 12 du 17/03/2014 au 23/03/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201413\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 13 du 24/03/2014 au 30/03/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201414\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 14 du 31/03/2014 au 06/04/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201415\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 15 du 07/04/2014 au 13/04/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201416\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 16 du 14/04/2014 au 20/04/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201417\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 17 du 21/04/2014 au 27/04/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201418\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 18 du 28/04/2014 au 04/05/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201419\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 19 du 05/05/2014 au 11/05/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201420\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 20 du 12/05/2014 au 18/05/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201421\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 21 du 19/05/2014 au 25/05/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201422\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 22 du 26/05/2014 au 01/06/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201423\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 23 du 02/06/2014 au 08/06/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201424\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 24 du 09/06/2014 au 15/06/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201425\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 25 du 16/06/2014 au 22/06/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201426\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 26 du 23/06/2014 au 29/06/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201427\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 27 du 30/06/2014 au 06/07/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201428\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 28 du 07/07/2014 au 13/07/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201429\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 29 du 14/07/2014 au 20/07/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201430\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 30 du 21/07/2014 au 27/07/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201431\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 31 du 28/07/2014 au 03/08/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201432\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 32 du 04/08/2014 au 10/08/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201433\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 33 du 11/08/2014 au 17/08/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201434\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 34 du 18/08/2014 au 24/08/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201435\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 35 du 25/08/2014 au 31/08/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t</select>\n" +
            "\t\t\t\t\t\t\t\t</div>\n" +
            "\t\t<div>\t\t\t<label>&nbsp;</label>\n" +
            "\t\t\t<input type=\"submit\" class=\"button\" value=\"Valider\">\t\t</div>\n" +
            "\t</form>\n" +
            "\t<hr/>\t\t\t\t\t\t<div id=\"cpt\">\t\t\t<h3>Compteurs</h3>\n" +
            "\t\t\t<ul class=\"cptWeb\">\t\t\t\t\t\t\t\t\t<li><span><a href=\"#\" onClick=\"maskViewNoeuds('TCRED','/media/img/lien_plus.gif','/media/img/lien_moins.gif');\"><img src=\"/media/img/lien_plus.gif\" class=\"imgNoeud\" id=\"imgTCRED\"></a></span><div class='tooltip' style=\"display:none;\"></div><a class=\"highlight\" href=\"#\"><span><span style=\"width:21em\">Temps valid�</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 40 h 32 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Temps valid�</h3><p>Temps badg� valid�+ cong�s pay�s (hors r�gularisation) + absences cr�ditrices + temps cr�dit� jours f�ri�s + (ou -) r�gularisations</p></div></div></span></a><ul id=\"TCRED\" style=\"display: none\"><li><span><a href=\"#\" onClick=\"maskViewNoeuds('null','/media/img/lien_plus.gif','/media/img/lien_moins.gif');\"><img src=\"/media/img/lien_plus.gif\" class=\"imgNoeud\" id=\"imgnull\"></a></span><div class='tooltip' style=\"display:none;\"></div><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">Temps badg� valid�</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 10 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Temps badg� valid�</h3><p>Temps base de calcul - �cr�tages journaliers - �cr�tages hebdomadaires</p></div></div></span></a><ul id=\"null\" style=\"display: none\"><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:17em\">+ Temps base de calcul</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 10 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>+ Temps base de calcul</h3><p>Temps de travail 'top�', calcul� sur vos tops �ventuellement corrig�s par le gestionnaire</p></div></div></span></a></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:17em\">-  Ecr�tages journaliers</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 0 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>-  Ecr�tages journaliers</h3><p>Pauses m�ridiennes trop courtes et/ou temps de travail journalier > au nombre d'heures autoris� par jour</p></div></div></span></a></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:17em\">-  Ecr�tages hebdomadaires</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 0 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>-  Ecr�tages hebdomadaires</h3><p>Temps de travail > au temps de travail autoris� par semaine apr�s �cr�tages journaliers</p></div></div></span></a></li></ul></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">Cong�s pay�s (hors r�gularisation)</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 30 h 32 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Cong�s pay�s (hors r�gularisation)</h3><p>Jours de cong�s pay�s convertis en heures pris depuis la mise en place d?Agatte</p></div></div></span></a></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">Absences cr�ditrices</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 0 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Absences cr�ditrices</h3><p>Absences consid�r�es comme temps travaill�</p></div></div></span></a><ul id=\"TABSC\" style=\"display: none\"></ul></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">Temps cr�dit� jours f�ri�s</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 0 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Temps cr�dit� jours f�ri�s</h3><p>Jour f�ri� = temps travaill� s'il tombe un jour habituellement travaill�</p></div></div></span></a></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">R�gularisations</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 0 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>R�gularisations</h3><p>Ajustements sur le temps de travail r�alis� du 01/09/03 � l'entr�e en application d'Agatte, cong�s pay�s inclus</p></div></div></span></a></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">Ecr�tage et ajustements annuels</span><span class=\"valCptWeb\" > 0 h 00 min</span></span></a></li></ul></li>\n" +
            "\t\t\t\t\t\t\t\t\t<li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:21em\">Absences non cr�ditrices</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 0 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Absences non cr�ditrices</h3><p>Temps d'absence non pris en compte dans le temps de travail</p></div></div></span></a><ul id=\"ABNOCRE\" style=\"display: none\"></ul></li>\n" +
            "\t\t\t\t\t\t\t\t\t<li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:21em\">Cong�s pay�s</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 30 h 32 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Cong�s pay�s</h3><p>Cong�s pay�s utilis�s depuis le d�but de l'ann�e universitaire convertis en heures</p></div></div></span></a></li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</ul>\n" +
            "\t\t</div>\t\n" +
            "\t\t\t\t\t\t<div id=\"tabBord\">\t\t\t<h3>Tableau de bord</h3>\n" +
            "\t\t\t<ul class=\"cptWeb\">\t\t\t\t\t\t\t\t\t<li><span><a href=\"#\" onClick=\"maskViewNoeuds('TDU','/media/img/lien_plus.gif','/media/img/lien_moins.gif');\"><img src=\"/media/img/lien_plus.gif\" class=\"imgNoeud\" id=\"imgTDU\"></a></span><div class='tooltip' style=\"display:none;\"></div><a class=\"highlight\" href=\"#\"><span><span style=\"width:21em\">R�f�rence annuelle</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 2043 h 22 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>R�f�rence annuelle</h3><p>Temps de travail annuel d� + cong�s pay�s</p></div></div></span></a><ul id=\"TDU\" style=\"display: none\"><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">Temps de travail d�</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 1593 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Temps de travail d�</h3><p>Temps de travail �  r�aliser par an</p></div></div></span></a></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">Cong�s pay�s</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 450 h 22 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Cong�s pay�s</h3><p>Droits � cong�s pay�s annuels convertis en heures</p></div></div></span></a></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">R�gularisations</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 0 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>R�gularisations</h3><p>Intervention baissant le temps annuel d� (gr�ve, C.I.F., cong�s sans solde,...)</p></div></div></span></a></li></ul></li>\n" +
            "\t\t\t\t\t\t\t\t\t<li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:21em\">R�f�rence pour la p�riode</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 38 h 10 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>R�f�rence pour la p�riode</h3><p>Temps de travail moyen et droits � cong�s � une date donn�e, calcul bas� sur votre 'contrat' Agatte</p></div></div></span></a></li>\n" +
            "\t\t\t\t\t\t\t\t\t<li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:21em\">Avance / Retard pour la p�riode</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 2 h 22 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Avance / Retard pour la p�riode</h3><p>R�f�rence pour la p�riode  - temps valid� = avance (+) ou retard (-) ou �quilibre (0 mn)</p></div></div></span></a></li>\n" +
            "\t\t\t\t\t\t\t\t\t<li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:21em\">Temps d'avance maximum annuel</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 140 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Temps d'avance maximum annuel</h3><p>Ecr�tage des heures suppl�mentaires r�alis�es au-del� de ce seuil</p></div></div></span></a></li>\n" +
            "\t\t\t\t\t\t\t</ul>\n" +
            "\t\t</div>\n" +
            "\t\t\t<div class=\"spacer\">&nbsp;</div>\n" +
            "\t\t<script type=\"text/javascript\">\t\t \t\tinitCalendrier(\"jour\",\"cal\"); \t\t\t\tinitNivCpt(document.formFeuilleTop.nivCpt);\t</script>\n" +
            "\t\t\t\t\t</div>\n" +
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
    private static final String response_counter5 = "<html>\t<head>\t\t \t<meta http-equiv=\"refresh\" content=\"600;URL=/logout.htm\"><link rel=\"stylesheet\" href=\"/media/css/normalize.css\" type=\"text/css\"/> <link rel=\"stylesheet\" href=\"/media/css/agatte.css?Wed Apr 16 08:46:08 CEST 2014\" type=\"text/css\"/><link rel=\"stylesheet\" href=\"/media/css/print.css\" type=\"text/css\" media=\"print\"/><link rel=\"stylesheet\" href=\"/media/css/displaytag.css\" type=\"text/css\"/><style type=\"text/css\" media=\"screen\">@import \"/media/css/tabs.css\";</style>\n" +
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
            "\t\t<div id=\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\"main-menu\"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t >\t\t\t<div id=\t\t\t\t\t\t\t\t\t\t\t\t\t\t\"contents-menu\"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t>\t\t\t\t<div id=\"dwr\"><script type='text/javascript' src='/dwr/interface/anneeDwr.js'></script>\n" +
            "<script type='text/javascript' src='/dwr/engine.js'></script>\n" +
            "<script type='text/javascript' src='/dwr/util.js'></script>\n" +
            "<script type='text/javascript'>\tvar anneeCallback = function(annees) {\t\tDWRUtil.removeAllOptions('codAnu');\t\tDWRUtil.addOptions('codAnu', annees,'codAnu','libAnu');\t\tanneeDwr.listerSemaine(document.getElementById('codAnu').value,semaineCallback);\t}\tvar semaineCallback = function(semaines) {\t\tDWRUtil.removeAllOptions('numSem');\t\tDWRUtil.addOptions('numSem', semaines,'numSemaine','libSemaine');\t}</script>\n" +
            "</div>\n" +
            "<h1>Feuille des tops</h1>\n" +
            "\t\t\t\t<form id=\"formFeuilleTop\" name=\"formFeuilleTop\" method=\"post\" action=\"feuille-top.form\">\t\t<div>\t\t\t<label>Contrat</label>\n" +
            "\t\t\t\t\t\t\t<select name=\"numCont\" id=\"numCont\" onchange=\"anneeDwr.listerAnneeContrat(this.value,119309,anneeCallback);\">\t\t\t\t\t\t\t\t\t\t\t<option value=\"10259\"\t\t\t\t\t\t\tselected>\t\t\t\t\t\t\tDu 01/09/2013\t\t\t\t\t\t\t\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t</select>\n" +
            "\t\t\t\t\t</div>\n" +
            "\t\t<div>\t\t\t<label>Ann�e</label>\n" +
            "\t\t\t\t\t\t\t<select name=\"codAnu\" id=\"codAnu\" \t\t\t\t\t\tonchange=\"anneeDwr.listerSemaine(this.value,semaineCallback);if (getValRadio(document.formFeuilleTop.nivCpt) == 'A') formFeuilleTop.submit();\">\t\t\t\t\t\t\t\t\t\t\t<option value=\"2013\"\t\t\t\t\t\t\tselected>\t\t\t\t\t\t\tAnnee 2013/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t</select>\n" +
            "\t\t\t\t\t</div>\n" +
            "\t\t<div>\t\t\t<label>Compteurs</label>\n" +
            "\t\t\t\t\t\t\t<input type=\"radio\" name=\"nivCpt\" id=\"nivCpt\" value=\"A\" onclick=\"disableInputs(['numSem','jour','cal']);enableInputs(['codAnu','numCont'])\" />\t\t\t\t\t\t\tAnnuels | \t\t\t\t\t\t\t<input type=\"radio\" name=\"nivCpt\" id=\"nivCpt\" value=\"H\" onclick=\"disableInputs(['jour','cal']);enableInputs(['numSem','codAnu','numCont'])\" checked/>\t\t\t\t\t\tHebdomadaires\t\t\t\t\t\t\t<select name=\"numSem\" id=\"numSem\" onchange=\"formFeuilleTop.submit()\">\t\t\t\t\t\t\t\t\t\t\t<option value=\"201335\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 35 du 01/09/2013 au 01/09/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201336\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 36 du 02/09/2013 au 08/09/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201337\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 37 du 09/09/2013 au 15/09/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201338\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 38 du 16/09/2013 au 22/09/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201339\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 39 du 23/09/2013 au 29/09/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201340\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 40 du 30/09/2013 au 06/10/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201341\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 41 du 07/10/2013 au 13/10/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201342\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 42 du 14/10/2013 au 20/10/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201343\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 43 du 21/10/2013 au 27/10/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201344\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 44 du 28/10/2013 au 03/11/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201345\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 45 du 04/11/2013 au 10/11/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201346\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 46 du 11/11/2013 au 17/11/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201347\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 47 du 18/11/2013 au 24/11/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201348\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 48 du 25/11/2013 au 01/12/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201349\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 49 du 02/12/2013 au 08/12/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201350\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 50 du 09/12/2013 au 15/12/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201351\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 51 du 16/12/2013 au 22/12/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201352\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 52 du 23/12/2013 au 29/12/2013\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201401\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 1 du 30/12/2013 au 05/01/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201402\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 2 du 06/01/2014 au 12/01/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201403\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 3 du 13/01/2014 au 19/01/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201404\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 4 du 20/01/2014 au 26/01/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201405\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 5 du 27/01/2014 au 02/02/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201406\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 6 du 03/02/2014 au 09/02/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201407\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 7 du 10/02/2014 au 16/02/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201408\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 8 du 17/02/2014 au 23/02/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201409\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 9 du 24/02/2014 au 02/03/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201410\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 10 du 03/03/2014 au 09/03/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201411\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 11 du 10/03/2014 au 16/03/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201412\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 12 du 17/03/2014 au 23/03/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201413\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 13 du 24/03/2014 au 30/03/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201414\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 14 du 31/03/2014 au 06/04/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201415\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 15 du 07/04/2014 au 13/04/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201416\"\t\t\t\t\t\t\tselected>\t\t\t\t\t\t\tSemaine 16 du 14/04/2014 au 20/04/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201417\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 17 du 21/04/2014 au 27/04/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201418\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 18 du 28/04/2014 au 04/05/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201419\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 19 du 05/05/2014 au 11/05/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201420\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 20 du 12/05/2014 au 18/05/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201421\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 21 du 19/05/2014 au 25/05/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201422\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 22 du 26/05/2014 au 01/06/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201423\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 23 du 02/06/2014 au 08/06/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201424\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 24 du 09/06/2014 au 15/06/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201425\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 25 du 16/06/2014 au 22/06/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201426\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 26 du 23/06/2014 au 29/06/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201427\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 27 du 30/06/2014 au 06/07/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201428\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 28 du 07/07/2014 au 13/07/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201429\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 29 du 14/07/2014 au 20/07/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201430\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 30 du 21/07/2014 au 27/07/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201431\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 31 du 28/07/2014 au 03/08/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201432\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 32 du 04/08/2014 au 10/08/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201433\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 33 du 11/08/2014 au 17/08/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201434\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 34 du 18/08/2014 au 24/08/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t<option value=\"201435\"\t\t\t\t\t\t\t>\t\t\t\t\t\t\tSemaine 35 du 25/08/2014 au 31/08/2014\t\t\t\t\t\t</option>\n" +
            "\t\t\t\t\t\t\t\t\t</select>\n" +
            "\t\t\t\t\t\t\t\t</div>\n" +
            "\t\t<div>\t\t\t<label>&nbsp;</label>\n" +
            "\t\t\t<input type=\"submit\" class=\"button\" value=\"Valider\">\t\t</div>\n" +
            "\t</form>\n" +
            "\t<hr/>\t\t\t\t\t\t<div id=\"cpt\">\t\t\t<h3>Compteurs</h3>\n" +
            "\t\t\t<ul class=\"cptWeb\">\t\t\t\t\t\t\t\t\t<li><span><a href=\"#\" onClick=\"maskViewNoeuds('TCRED','/media/img/lien_plus.gif','/media/img/lien_moins.gif');\"><img src=\"/media/img/lien_plus.gif\" class=\"imgNoeud\" id=\"imgTCRED\"></a></span><div class='tooltip' style=\"display:none;\"></div><a class=\"highlight\" href=\"#\"><span><span style=\"width:21em\">Temps valid�</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 11 h 05 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Temps valid�</h3><p>Temps badg� valid�+ cong�s pay�s (hors r�gularisation) + absences cr�ditrices + temps cr�dit� jours f�ri�s + (ou -) r�gularisations</p></div></div></span></a><ul id=\"TCRED\" style=\"display: none\"><li><span><a href=\"#\" onClick=\"maskViewNoeuds('null','/media/img/lien_plus.gif','/media/img/lien_moins.gif');\"><img src=\"/media/img/lien_plus.gif\" class=\"imgNoeud\" id=\"imgnull\"></a></span><div class='tooltip' style=\"display:none;\"></div><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">Temps badg� valid�</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 11 h 05 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Temps badg� valid�</h3><p>Temps base de calcul - �cr�tages journaliers - �cr�tages hebdomadaires</p></div></div></span></a><ul id=\"null\" style=\"display: none\"><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:17em\">+ Temps base de calcul</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 11 h 05 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>+ Temps base de calcul</h3><p>Temps de travail 'top�', calcul� sur vos tops �ventuellement corrig�s par le gestionnaire</p></div></div></span></a></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:17em\">-  Ecr�tages journaliers</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 0 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>-  Ecr�tages journaliers</h3><p>Pauses m�ridiennes trop courtes et/ou temps de travail journalier > au nombre d'heures autoris� par jour</p></div></div></span></a></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:17em\">-  Ecr�tages hebdomadaires</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 0 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>-  Ecr�tages hebdomadaires</h3><p>Temps de travail > au temps de travail autoris� par semaine apr�s �cr�tages journaliers</p></div></div></span></a></li></ul></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">Absences cr�ditrices</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 0 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Absences cr�ditrices</h3><p>Absences consid�r�es comme temps travaill�</p></div></div></span></a><ul id=\"TABSC\" style=\"display: none\"></ul></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">Temps cr�dit� jours f�ri�s</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 0 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Temps cr�dit� jours f�ri�s</h3><p>Jour f�ri� = temps travaill� s'il tombe un jour habituellement travaill�</p></div></div></span></a></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">R�gularisations</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 0 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>R�gularisations</h3><p>Ajustements sur le temps de travail r�alis� du 01/09/03 � l'entr�e en application d'Agatte, cong�s pay�s inclus</p></div></div></span></a></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">Ecr�tage et ajustements annuels</span><span class=\"valCptWeb\" > 0 h 00 min</span></span></a></li></ul></li>\n" +
            "\t\t\t\t\t\t\t\t\t<li><span><a href=\"#\" onClick=\"maskViewNoeuds('ABNOCRE','/media/img/lien_plus.gif','/media/img/lien_moins.gif');\"><img src=\"/media/img/lien_plus.gif\" class=\"imgNoeud\" id=\"imgABNOCRE\"></a></span><div class='tooltip' style=\"display:none;\"></div><a class=\"highlight\" href=\"#\"><span><span style=\"width:21em\">Absences non cr�ditrices</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 3 h 49 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Absences non cr�ditrices</h3><p>Temps d'absence non pris en compte dans le temps de travail</p></div></div></span></a><ul id=\"ABNOCRE\" style=\"display: none\"><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">R�cup�ration en jours-demi journ�es</span><span class=\"valCptWeb\" > 3 h 49 min</span></span></a></li></ul></li>\n" +
            "\t\t\t\t\t\t\t\t\t<li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:21em\">Cong�s pay�s</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 0 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Cong�s pay�s</h3><p>Cong�s pay�s utilis�s depuis le d�but de l'ann�e universitaire convertis en heures</p></div></div></span></a></li>\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</ul>\n" +
            "\t\t</div>\t\n" +
            "\t\t\t\t\t\t<div id=\"tabBord\">\t\t\t<h3>Tableau de bord</h3>\n" +
            "\t\t\t<ul class=\"cptWeb\">\t\t\t\t\t\t\t\t\t<li><span><a href=\"#\" onClick=\"maskViewNoeuds('TDU','/media/img/lien_plus.gif','/media/img/lien_moins.gif');\"><img src=\"/media/img/lien_plus.gif\" class=\"imgNoeud\" id=\"imgTDU\"></a></span><div class='tooltip' style=\"display:none;\"></div><a class=\"highlight\" href=\"#\"><span><span style=\"width:21em\">R�f�rence annuelle</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 2043 h 22 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>R�f�rence annuelle</h3><p>Temps de travail annuel d� + cong�s pay�s</p></div></div></span></a><ul id=\"TDU\" style=\"display: none\"><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">Temps de travail d�</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 1593 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Temps de travail d�</h3><p>Temps de travail �  r�aliser par an</p></div></div></span></a></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">Cong�s pay�s</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 450 h 22 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Cong�s pay�s</h3><p>Droits � cong�s pay�s annuels convertis en heures</p></div></div></span></a></li><li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:19em\">R�gularisations</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 0 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>R�gularisations</h3><p>Intervention baissant le temps annuel d� (gr�ve, C.I.F., cong�s sans solde,...)</p></div></div></span></a></li></ul></li>\n" +
            "\t\t\t\t\t\t\t\t\t<li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:21em\">R�f�rence pour la p�riode</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 15 h 16 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>R�f�rence pour la p�riode</h3><p>Temps de travail moyen et droits � cong�s � une date donn�e, calcul bas� sur votre 'contrat' Agatte</p></div></div></span></a></li>\n" +
            "\t\t\t\t\t\t\t\t\t<li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:21em\">Avance / Retard pour la p�riode</span><span class=\"valCptWeb\"  style=\"cursor: help;\">-4 h 11 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Avance / Retard pour la p�riode</h3><p>R�f�rence pour la p�riode  - temps valid� = avance (+) ou retard (-) ou �quilibre (0 mn)</p></div></div></span></a></li>\n" +
            "\t\t\t\t\t\t\t\t\t<li><span style=\"width:15.7\" class=\"spacerCptWeb\">&nbsp;</span><a class=\"highlight\" href=\"#\"><span><span style=\"width:21em\">Temps d'avance maximum annuel</span><span class=\"valCptWeb\"  style=\"cursor: help;\"> 140 h 00 min</span><div class=\"tooltip\" style=\"display:none\"><div class=\"cmt\"><h3>Temps d'avance maximum annuel</h3><p>Ecr�tage des heures suppl�mentaires r�alis�es au-del� de ce seuil</p></div></div></span></a></li>\n" +
            "\t\t\t\t\t\t\t</ul>\n" +
            "\t\t</div>\n" +
            "\t\t\t<div class=\"spacer\">&nbsp;</div>\n" +
            "\t\t<script type=\"text/javascript\">\t\t \t\tinitCalendrier(\"jour\",\"cal\"); \t\t\t\tinitNivCpt(document.formFeuilleTop.nivCpt);\t</script>\n" +
            "\t\t\t\t\t</div>\n" +
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

    private static final String response_secret = "\n" +
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
            "\t\n" +
            "\t<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n" +
            "\n" +
            "\n" +
            "\n" +
            "<link rel=\"stylesheet\" href=\"/media/css/normalize.css\" type=\"text/css\"/> \n" +
            "<link rel=\"stylesheet\" href=\"/media/css/agatte.css?Wed Jul 01 11:40:39 CEST 2015\" type=\"text/css\"/>\n" +
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
            "\n" +
            "\n" +
            "\n" +
            "<div id=\"importJS\">\n" +
            "\t\n" +
            "\t<script type=\"text/javascript\" src=\"https://code.jquery.com/jquery-1.11.3.min.js\"></script>\n" +
            "\t\n" +
            "\t<script type='text/javascript' src='/media/js/agt.js'></script>\n" +
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
            "<div id=\"d1\">PHNwYW4gaWQ9InNwMCIgY2xhc3M9IkxpNHZZWEJ3TDIxdmRtVmtMbWgwYlE9PSI+PC9zcGFuPjxoMT5Ub3A8L2gxPjxzcGFuIGlkPSJzcDEiIGNsYXNzPSJNakl4WWpJM1lqUXlabUV6TlRSaE1ESmhNbU00WVdFNFl6UmlPREk0TlRJPSI+PC9zcGFuPg==</div>\n" +
            "\n" +
            "\n" +
            "<div id=\"zsec\">\n" +
            "\t\n" +
            "\t\t\n" +
            "\t\t\n" +
            "\t\t\t<p>Il est <span id=\"time\"></span></p>\n" +
            "\t\t\t<script language=\"JavaScript\">\n" +
            "\t\t\t\tvar dt= new Date(0,0,0,'11','40','39');\n" +
            "\t\t\t\tvar serveur_heu = dt.getHours();\n" +
            "\t\t\t\tvar serveur_min = dt.getMinutes(); \n" +
            "\t\t\t\tvar serveur_sec = dt.getSeconds();\n" +
            "\t\t\t\tvar timer=setInterval(\"horloge('time')\", 1000);\n" +
            "\t\t\t</script>\n" +
            "\t\t\t<div id=\"d2\">PHA+Q2xpcXVlciBzdXIgbGUgYm91dG9uIHBvdXIgdG9wZXIuLi48L3A+PGlucHV0IHR5cGU9ImJ1dHRvbiIgaWQ9InowIiBjbGFzcz0iYnV0dG9uIiBuYW1lPSJ6MCIgdmFsdWU9IlRvcGVyIi8+</div>\n" +
            "\t\t\n" +
            "\t\n" +
            "\t<div class=\"spacer\">&nbsp;</div>\n" +
            "\t<hr/>\n" +
            "\t\n" +
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
            "\t\t\t\t08:35\n" +
            "\t\t\t</li>\n" +
            "\t\t\n" +
            "\t</ul>\n" +
            "\t<p>\n" +
            "\t\t<span>Tops réels</span>\n" +
            "\t\t<br/>\n" +
            "\t\t<span class=\"top-absence\">Tops d'absence</span>\n" +
            "\t</p>\n" +
            "</div>\n" +
            "</div>\n" +
            "<div id=\"d3\">PGRpdiBpZD0iZHZPIiBjbGFzcz0iNTQyNDE3ZGZjYWU2YTUzMWVmNTJkMmVjNGIyMzA2ZjkiPjwvZGl2PjxkaXYgY2xhc3M9InNwYWNlciI+Jm5ic3A7PC9kaXY+PGRpdiBpZD0iZHYwIiBjbGFzcz0iMjliN2E3NGQzOTNmOGFiNDgzMGJmZDQwODUxNWVlOTgiLz48L2Rpdj4=</div>\n" +
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


    public void testParseSecret() throws Exception {
        AgatteParser instance = AgatteParser.getInstance();

        AgatteSecret rsp = instance.parse_secrets_from_query_response(response_secret);
        assertEquals(rsp.getUrl(), "../app/moved.htm");
        assertEquals(rsp.getHeader_key(), "221b27b42fa354a02a2c8aa8c4b82852");
        assertEquals(rsp.getSecret(),"MjliN2E3NGQzOTNmOGFiNDgzMGJmZDQwODUxNWVlOTg=");
        assertEquals(rsp.getURLEncodedSecret(), "MjliN2E3NGQzOTNmOGFiNDgzMGJmZDQwODUxNWVlOTg%3D");
    }

    public void testParseResponse() throws Exception {
        AgatteParser instance = AgatteParser.getInstance();

        AgatteResponse rsp = instance.parse_query_response(response_test1);
        assertTrue(rsp.hasPunches());
        String[] actual = rsp.getPunches();
        assertEquals(2, actual.length);
        assertEquals("12:10", actual[0]);
        assertEquals("13:00", actual[1]);

    }

    public void testParseResponse2() throws Exception {
        AgatteParser instance = AgatteParser.getInstance();

        AgatteResponse rsp = instance.parse_query_response(response_test2);
        assertTrue(rsp.hasPunches());
        String[] actual = rsp.getPunches();
        assertEquals(3, actual.length);
        assertEquals("09:01", actual[0]);
        assertEquals("12:15", actual[1]);
        assertEquals("13:00", actual[2]);

    }

    public void testParseResponse3() throws Exception {
        AgatteParser instance = AgatteParser.getInstance();

        try {
            instance.parse_query_response(response_test3);
            fail("Should have thrown AgatteNetworkNotAuthorizedException");
        } catch (AgatteNetworkNotAuthorizedException ignored) {
            //succeed
        }
    }

    public void testParseResponse6() throws Exception {
        AgatteParser instance = AgatteParser.getInstance();

        AgatteResponse rsp = instance.parse_query_response(response_test5);
        assertTrue(rsp.hasPunches());
        assertTrue(rsp.hasVirtualPunches());
        assertEquals(2, rsp.getVirtualPunches().length);
        assertEquals("07:56", rsp.getVirtualPunches()[0]);
        assertEquals("11:45", rsp.getVirtualPunches()[1]);
        assertEquals(2, rsp.getPunches().length);
        assertEquals("13:00", rsp.getPunches()[0]);
        assertEquals("17:00", rsp.getPunches()[1]);
    }


    public void testParseCounterResponse1() throws Exception {
        AgatteParser instance = AgatteParser.getInstance();

        CounterPage rsp = instance.parse_counter_response(response_counter1);
        assertTrue(rsp.anomaly);
        assertEquals(2013, rsp.contract_year);
        assertEquals(10259, rsp.contract);
        assertEquals(0.0, rsp.value, 0.001);
    }

    public void testParseCounterResponse2() throws Exception {
        AgatteParser instance = AgatteParser.getInstance();

        CounterPage rsp = instance.parse_counter_response(response_counter2);
        assertFalse(rsp.anomaly);
        assertEquals(2013, rsp.contract_year);
        assertEquals(10259, rsp.contract);
        assertEquals(0.0, rsp.value, 0.001);
    }

    public void testParseCounterResponse3() throws Exception {
        AgatteParser instance = AgatteParser.getInstance();

        CounterPage rsp = instance.parse_counter_response(response_counter3);
        assertFalse(rsp.anomaly);
        assertEquals(2013, rsp.contract_year);
        assertEquals(10259, rsp.contract);
        assertEquals(24 + 23. / 60., rsp.value, 0.001);
    }

    public void testParseCounterResponse4() throws Exception {
        AgatteParser instance = AgatteParser.getInstance();

        CounterPage rsp = instance.parse_counter_response(response_counter4);
        assertFalse(rsp.anomaly);
        assertEquals(2013, rsp.contract_year);
        assertEquals(10259, rsp.contract);
        assertEquals(2 + 22. / 60., rsp.value, 0.001);
    }

    public void testParseCounterResponse5() throws Exception {
        AgatteParser instance = AgatteParser.getInstance();

        CounterPage rsp = instance.parse_counter_response(response_counter5);
        assertFalse(rsp.anomaly);
        assertEquals(2013, rsp.contract_year);
        assertEquals(10259, rsp.contract);
        assertEquals(-(4 + (11. / 60.)), rsp.value, 0.001);//-4h11
    }

}
