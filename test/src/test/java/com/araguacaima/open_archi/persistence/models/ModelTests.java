package com.araguacaima.open_archi.persistence.models;

import com.araguacaima.commons.utils.JsonUtils;
import com.araguacaima.commons.utils.MapUtils;
import com.araguacaima.commons.utils.ReflectionUtils;
import com.araguacaima.open_archi.web.controller.ModelsController;
import com.araguacaima.orpheusdb.utils.OrpheusDbJPAEntityManagerUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.fail;

public class ModelTests {

    private static Map<String, String> environment;
    private static ProcessBuilder processBuilder = new ProcessBuilder();
    private static Logger log = LoggerFactory.getLogger(ModelTests.class);
    private static ReflectionUtils reflectionUtils = ReflectionUtils.getInstance();
    private static final JsonUtils jsonUtils = new JsonUtils();
    private static String model = "{\n" +
            "  \"status\": 0,\n" +
            "  \"name\": \"New Model\",\n" +
            "  \"kind\": \"ARCHITECTURE_MODEL\",\n" +
            "  \"prototype\": true,\n" +
            "  \"category\": \"ARCHITECTURE_MODEL\",\n" +
            "  \"shape\": {\n" +
            "    \"id\": \"061d46ad-7c6f-477c-a16f-3900dcacce57\",\n" +
            "    \"meta\": {\n" +
            "      \"id\": \"29074596-c659-4e7a-8589-24bc43fffe7e\",\n" +
            "      \"created\": 1549999000993\n" +
            "    },\n" +
            "    \"type\": \"ARCHITECTURE_MODEL\",\n" +
            "    \"colorSchemes\": [\n" +
            "      {\n" +
            "        \"id\": \"b4f2850a-886f-4d9b-9d92-1a8933810983\",\n" +
            "        \"meta\": {\n" +
            "          \"id\": \"16358c4d-a4a3-428c-896d-d35e87312c3c\",\n" +
            "          \"created\": 1549999000994\n" +
            "        },\n" +
            "        \"name\": \"SECONDARY\",\n" +
            "        \"fillColor\": \"#eaeded\",\n" +
            "        \"strokeColor\": \"DarkSlateGray\",\n" +
            "        \"textColor\": \"DarkSlateGray\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"fa1516dc-c224-4967-9def-97237b019121\",\n" +
            "        \"meta\": {\n" +
            "          \"id\": \"5d1a3d5f-983f-4d71-8170-7ee97e15a3af\",\n" +
            "          \"created\": 1549999000994\n" +
            "        },\n" +
            "        \"name\": \"PRIMARY\",\n" +
            "        \"fillColor\": \"DarkSlateGray\",\n" +
            "        \"strokeColor\": \"#eaeded\",\n" +
            "        \"textColor\": \"#eaeded\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"input\": true,\n" +
            "    \"output\": true,\n" +
            "    \"isGroup\": true,\n" +
            "    \"group\": true\n" +
            "  },\n" +
            "  \"fill\": \"DarkSlateGray\",\n" +
            "  \"stroke\": \"#eaeded\",\n" +
            "  \"isGroup\": true,\n" +
            "  \"selfCreated\": true,\n" +
            "  \"layers\": [\n" +
            "    {\n" +
            "      \"key\": \"2334349698167346\",\n" +
            "      \"status\": 0,\n" +
            "      \"name\": \"Frontend\",\n" +
            "      \"kind\": \"LAYER\",\n" +
            "      \"prototype\": true,\n" +
            "      \"category\": \"LAYER\",\n" +
            "      \"shape\": {\n" +
            "        \"id\": \"713af8da-0dca-4419-9cfd-b842438392d8\",\n" +
            "        \"meta\": {\n" +
            "          \"id\": \"131588fb-96e1-4d77-ae72-41b03b64bbc3\",\n" +
            "          \"created\": 1549998990239\n" +
            "        },\n" +
            "        \"type\": \"LAYER\",\n" +
            "        \"colorSchemes\": [\n" +
            "          {\n" +
            "            \"id\": \"ba3d7b64-7993-4cfd-adbb-8a512785dab1\",\n" +
            "            \"meta\": {\n" +
            "              \"id\": \"13621a55-5c32-434e-9b41-bd3bee5a1168\",\n" +
            "              \"created\": 1549998990246\n" +
            "            },\n" +
            "            \"name\": \"SECONDARY\",\n" +
            "            \"fillColor\": \"#eafaea\",\n" +
            "            \"strokeColor\": \"LimeGreen\",\n" +
            "            \"textColor\": \"LimeGreen\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": \"1b855bc8-ea5b-4d76-946e-3ae080dcaac0\",\n" +
            "            \"meta\": {\n" +
            "              \"id\": \"a059c66a-cf09-42be-924e-c4d48a349ea1\",\n" +
            "              \"created\": 1549998990246\n" +
            "            },\n" +
            "            \"name\": \"PRIMARY\",\n" +
            "            \"fillColor\": \"LimeGreen\",\n" +
            "            \"strokeColor\": \"#eafaea\",\n" +
            "            \"textColor\": \"#eafaea\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"input\": true,\n" +
            "        \"output\": true,\n" +
            "        \"isGroup\": true,\n" +
            "        \"group\": true\n" +
            "      },\n" +
            "      \"fill\": \"LimeGreen\",\n" +
            "      \"stroke\": \"#eafaea\",\n" +
            "      \"isGroup\": true,\n" +
            "      \"image\": {\n" +
            "        \"raw\": \"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\" standalone=\\\"no\\\"?><!-- Generator: Adobe Illustrator 19.0.0, SVG Export Plug-In . SVG Version: 6.00 Build 0)  --><svg   xmlns:dc=\\\"http://purl.org/dc/elements/1.1/\\\"   xmlns:cc=\\\"http://creativecommons.org/ns#\\\"   xmlns:rdf=\\\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\\\"   xmlns:svg=\\\"http://www.w3.org/2000/svg\\\"   xmlns=\\\"http://www.w3.org/2000/svg\\\"   xmlns:sodipodi=\\\"http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd\\\"   xmlns:inkscape=\\\"http://www.inkscape.org/namespaces/inkscape\\\"   version=\\\"1.1\\\"   id=\\\"Layer_1\\\"   x=\\\"0px\\\"   y=\\\"0px\\\"   viewBox=\\\"0 0 504 504\\\"   xml:space=\\\"preserve\\\"   sodipodi:docname=\\\"frontend3.svg\\\"   width=\\\"504\\\"   height=\\\"504\\\"   inkscape:version=\\\"0.92.2 (5c3e80d, 2017-08-06)\\\"><metadata     id=\\\"metadata5171\\\"><rdf:RDF><cc:Work         rdf:about=\\\"\\\"><dc:format>image/svg+xml</dc:format><dc:type           rdf:resource=\\\"http://purl.org/dc/dcmitype/StillImage\\\" /><dc:title></dc:title></cc:Work></rdf:RDF></metadata><defs     id=\\\"defs5169\\\" /><sodipodi:namedview     pagecolor=\\\"#ffffff\\\"     bordercolor=\\\"#666666\\\"     borderopacity=\\\"1\\\"     objecttolerance=\\\"10\\\"     gridtolerance=\\\"10\\\"     guidetolerance=\\\"10\\\"     inkscape:pageopacity=\\\"0\\\"     inkscape:pageshadow=\\\"2\\\"     inkscape:window-width=\\\"1366\\\"     inkscape:window-height=\\\"705\\\"     id=\\\"namedview5167\\\"     showgrid=\\\"false\\\"     fit-margin-top=\\\"0\\\"     fit-margin-left=\\\"0\\\"     fit-margin-right=\\\"0\\\"     fit-margin-bottom=\\\"0\\\"     inkscape:zoom=\\\"0.46825397\\\"     inkscape:cx=\\\"-13.881356\\\"     inkscape:cy=\\\"252\\\"     inkscape:window-x=\\\"-8\\\"     inkscape:window-y=\\\"-8\\\"     inkscape:window-maximized=\\\"1\\\"     inkscape:current-layer=\\\"Layer_1\\\" /><circle     style=\\\"fill:#ffd05b\\\"     cx=\\\"252\\\"     cy=\\\"252\\\"     r=\\\"252\\\"     id=\\\"circle5094\\\" /><path     style=\\\"fill:#324a5e\\\"     d=\\\"M 355.8,94.5 H 106.3 c -6.5,0 -11.8,5.3 -11.8,11.8 v 187.9 c 0,6.5 5.3,11.8 11.8,11.8 h 249.4 c 6.5,0 11.8,-5.3 11.8,-11.8 V 106.3 c 0.1,-6.5 -5.2,-11.8 -11.7,-11.8 z\\\"     id=\\\"path5096\\\"     inkscape:connector-curvature=\\\"0\\\" /><path     style=\\\"fill:#f6f6f6\\\"     d=\\\"M 336.3,118.1 H 125.8 c -4.2,0 -7.6,3.4 -7.6,7.6 v 149 c 0,4.2 3.4,7.6 7.6,7.6 h 210.6 c 4.2,0 7.6,-3.4 7.6,-7.6 v -149 c 0,-4.2 -3.5,-7.6 -7.7,-7.6 z\\\"     id=\\\"path5098\\\"     inkscape:connector-curvature=\\\"0\\\" /><polygon     style=\\\"fill:#2b3b4e\\\"     points=\\\"181.8,306 172.1,332.7 153.8,332.7 153.8,347.8 166.5,347.8 295.6,347.8 308.3,347.8 308.3,332.7 290,332.7 280.3,306 \\\"     id=\\\"polygon5100\\\" /><path     style=\\\"fill:#f1543f\\\"     d=\\\"m 407.6,409.5 h -90.8 c -1,0 -1.9,-0.8 -1.9,-1.9 V 276.2 c 0,-1 0.8,-1.9 1.9,-1.9 h 90.8 c 1,0 1.9,0.8 1.9,1.9 v 131.5 c 0,1 -0.8,1.8 -1.9,1.8 z\\\"     id=\\\"path5102\\\"     inkscape:connector-curvature=\\\"0\\\" /><rect     x=\\\"328\\\"     y=\\\"287.29999\\\"     style=\\\"fill:#4cdbc4\\\"     width=\\\"68.5\\\"     height=\\\"92.900002\\\"     id=\\\"rect5104\\\" /><circle     style=\\\"fill:#324a5e\\\"     cx=\\\"362.29999\\\"     cy=\\\"394.79999\\\"     r=\\\"9\\\"     id=\\\"circle5106\\\" /><g     id=\\\"g5112\\\"><rect       x=\\\"328\\\"       y=\\\"390.39999\\\"       style=\\\"fill:#e6e9ee\\\"       width=\\\"17.799999\\\"       height=\\\"8.8000002\\\"       id=\\\"rect5108\\\" /><rect       x=\\\"378.70001\\\"       y=\\\"390.39999\\\"       style=\\\"fill:#e6e9ee\\\"       width=\\\"17.799999\\\"       height=\\\"8.8000002\\\"       id=\\\"rect5110\\\" /></g><rect     x=\\\"134.10001\\\"     y=\\\"129.2\\\"     style=\\\"fill:#f1543f\\\"     width=\\\"33.599998\\\"     height=\\\"25.299999\\\"     id=\\\"rect5114\\\" /><rect     x=\\\"188\\\"     y=\\\"129.2\\\"     style=\\\"fill:#ff7058\\\"     width=\\\"139.89999\\\"     height=\\\"25.299999\\\"     id=\\\"rect5116\\\" /><rect     x=\\\"134.10001\\\"     y=\\\"207.10001\\\"     style=\\\"fill:#ffd05b\\\"     width=\\\"33.599998\\\"     height=\\\"25.299999\\\"     id=\\\"rect5118\\\" /><rect     x=\\\"188\\\"     y=\\\"207.10001\\\"     style=\\\"fill:#f9b54c\\\"     width=\\\"139.89999\\\"     height=\\\"25.299999\\\"     id=\\\"rect5120\\\" /><rect     x=\\\"134.10001\\\"     y=\\\"246.10001\\\"     style=\\\"fill:#4cdbc4\\\"     width=\\\"53.900002\\\"     height=\\\"25.299999\\\"     id=\\\"rect5122\\\" /><rect     x=\\\"274\\\"     y=\\\"246.10001\\\"     style=\\\"fill:#84dbff\\\"     width=\\\"53.900002\\\"     height=\\\"25.299999\\\"     id=\\\"rect5124\\\" /><g     id=\\\"g5134\\\"><rect       x=\\\"204.10001\\\"       y=\\\"246.10001\\\"       style=\\\"fill:#2c9984\\\"       width=\\\"53.900002\\\"       height=\\\"25.299999\\\"       id=\\\"rect5126\\\" /><rect       x=\\\"134.10001\\\"       y=\\\"168.2\\\"       style=\\\"fill:#2c9984\\\"       width=\\\"53.900002\\\"       height=\\\"25.299999\\\"       id=\\\"rect5128\\\" /><rect       x=\\\"274\\\"       y=\\\"168.2\\\"       style=\\\"fill:#2c9984\\\"       width=\\\"53.900002\\\"       height=\\\"25.299999\\\"       id=\\\"rect5130\\\" /><rect       x=\\\"204.10001\\\"       y=\\\"168.2\\\"       style=\\\"fill:#2c9984\\\"       width=\\\"53.900002\\\"       height=\\\"25.299999\\\"       id=\\\"rect5132\\\" /></g><g     id=\\\"g5136\\\" /><g     id=\\\"g5138\\\" /><g     id=\\\"g5140\\\" /><g     id=\\\"g5142\\\" /><g     id=\\\"g5144\\\" /><g     id=\\\"g5146\\\" /><g     id=\\\"g5148\\\" /><g     id=\\\"g5150\\\" /><g     id=\\\"g5152\\\" /><g     id=\\\"g5154\\\" /><g     id=\\\"g5156\\\" /><g     id=\\\"g5158\\\" /><g     id=\\\"g5160\\\" /><g     id=\\\"g5162\\\" /><g     id=\\\"g5164\\\" /></svg>\",\n" +
            "        \"type\": \"image/svg+xml\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"2401855948004458\",\n" +
            "      \"status\": 0,\n" +
            "      \"name\": \"Services\",\n" +
            "      \"kind\": \"LAYER\",\n" +
            "      \"prototype\": true,\n" +
            "      \"category\": \"LAYER\",\n" +
            "      \"shape\": {\n" +
            "        \"id\": \"713af8da-0dca-4419-9cfd-b842438392d8\",\n" +
            "        \"meta\": {\n" +
            "          \"id\": \"131588fb-96e1-4d77-ae72-41b03b64bbc3\",\n" +
            "          \"created\": 1549998990239\n" +
            "        },\n" +
            "        \"type\": \"LAYER\",\n" +
            "        \"colorSchemes\": [\n" +
            "          {\n" +
            "            \"id\": \"ba3d7b64-7993-4cfd-adbb-8a512785dab1\",\n" +
            "            \"meta\": {\n" +
            "              \"id\": \"13621a55-5c32-434e-9b41-bd3bee5a1168\",\n" +
            "              \"created\": 1549998990246\n" +
            "            },\n" +
            "            \"name\": \"SECONDARY\",\n" +
            "            \"fillColor\": \"#eafaea\",\n" +
            "            \"strokeColor\": \"LimeGreen\",\n" +
            "            \"textColor\": \"LimeGreen\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": \"1b855bc8-ea5b-4d76-946e-3ae080dcaac0\",\n" +
            "            \"meta\": {\n" +
            "              \"id\": \"a059c66a-cf09-42be-924e-c4d48a349ea1\",\n" +
            "              \"created\": 1549998990246\n" +
            "            },\n" +
            "            \"name\": \"PRIMARY\",\n" +
            "            \"fillColor\": \"LimeGreen\",\n" +
            "            \"strokeColor\": \"#eafaea\",\n" +
            "            \"textColor\": \"#eafaea\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"input\": true,\n" +
            "        \"output\": true,\n" +
            "        \"isGroup\": true,\n" +
            "        \"group\": true\n" +
            "      },\n" +
            "      \"fill\": \"LimeGreen\",\n" +
            "      \"stroke\": \"#eafaea\",\n" +
            "      \"isGroup\": true,\n" +
            "      \"image\": {\n" +
            "        \"raw\": \"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\" standalone=\\\"no\\\"?><!-- Generator: Adobe Illustrator 19.0.0, SVG Export Plug-In . SVG Version: 6.00 Build 0)  --><svg   xmlns:dc=\\\"http://purl.org/dc/elements/1.1/\\\"   xmlns:cc=\\\"http://creativecommons.org/ns#\\\"   xmlns:rdf=\\\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\\\"   xmlns:svg=\\\"http://www.w3.org/2000/svg\\\"   xmlns=\\\"http://www.w3.org/2000/svg\\\"   xmlns:sodipodi=\\\"http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd\\\"   xmlns:inkscape=\\\"http://www.inkscape.org/namespaces/inkscape\\\"   version=\\\"1.1\\\"   id=\\\"Capa_1\\\"   x=\\\"0px\\\"   y=\\\"0px\\\"   viewBox=\\\"0 0 62.40678 59.06356\\\"   xml:space=\\\"preserve\\\"   sodipodi:docname=\\\"services.svg\\\"   width=\\\"62.40678\\\"   height=\\\"59.06356\\\"   inkscape:version=\\\"0.92.2 (5c3e80d, 2017-08-06)\\\"><metadata     id=\\\"metadata292\\\"><rdf:RDF><cc:Work         rdf:about=\\\"\\\"><dc:format>image/svg+xml</dc:format><dc:type           rdf:resource=\\\"http://purl.org/dc/dcmitype/StillImage\\\" /><dc:title></dc:title></cc:Work></rdf:RDF></metadata><defs     id=\\\"defs290\\\" /><sodipodi:namedview     pagecolor=\\\"#ffffff\\\"     bordercolor=\\\"#666666\\\"     borderopacity=\\\"1\\\"     objecttolerance=\\\"10\\\"     gridtolerance=\\\"10\\\"     guidetolerance=\\\"10\\\"     inkscape:pageopacity=\\\"0\\\"     inkscape:pageshadow=\\\"2\\\"     inkscape:window-width=\\\"1366\\\"     inkscape:window-height=\\\"705\\\"     id=\\\"namedview288\\\"     showgrid=\\\"false\\\"     inkscape:zoom=\\\"4.2142857\\\"     inkscape:cx=\\\"39.444026\\\"     inkscape:cy=\\\"32.56356\\\"     inkscape:window-x=\\\"-8\\\"     inkscape:window-y=\\\"-8\\\"     inkscape:window-maximized=\\\"1\\\"     inkscape:current-layer=\\\"Capa_1\\\"     fit-margin-top=\\\"0\\\"     fit-margin-left=\\\"0\\\"     fit-margin-right=\\\"0\\\"     fit-margin-bottom=\\\"0\\\" /><path     style=\\\"fill:#e7eced;stroke-width:1.11440682\\\"     d=\\\"m 39.004237,18.387712 c 0,-0.726593 0.466937,-1.339517 1.114407,-1.569085 V 0 H 0 v 37.889831 h 34.54661 v -6.816827 c 0,-1.158983 0.939445,-2.098428 2.098428,-2.098428 h 2.359199 z\\\"     id=\\\"path233\\\"     inkscape:connector-curvature=\\\"0\\\" /><path     style=\\\"fill:#cbd4d8;stroke-width:1.11440682\\\"     d=\\\"M 30.334153,31.20339 H 10.088725 c -1.6170047,0 -3.0612758,-1.011882 -3.6129072,-2.530818 v 0 c -2.2845339,-6.28414 -2.2845339,-13.171174 0,-19.4553135 v 0 C 7.0274492,7.698322 8.4717203,6.6864407 10.088725,6.6864407 h 20.245428 c 1.617004,0 3.061275,1.0118813 3.612906,2.5308178 v 0 c 2.284534,6.2841395 2.284534,13.1711735 0,19.4553135 v 0 c -0.552745,1.518936 -1.995902,2.530818 -3.612906,2.530818 z\\\"     id=\\\"path235\\\"     inkscape:connector-curvature=\\\"0\\\" /><path     style=\\\"fill:#38454f;stroke-width:1.11440682\\\"     d=\\\"m 14.074958,21.173729 h -1.404153 c -0.839148,0 -1.526737,-0.686475 -1.526737,-1.526737 v -6.976187 c 0,-0.839148 0.686474,-1.526737 1.526737,-1.526737 h 1.405267 c 0.839148,0 1.526737,0.686474 1.526737,1.526737 v 6.977301 c -0.0011,0.839148 -0.687589,1.525623 -1.527851,1.525623 z\\\"     id=\\\"path237\\\"     inkscape:connector-curvature=\\\"0\\\" /><path     style=\\\"fill:#38454f;stroke-width:1.11440682\\\"     d=\\\"m 27.447839,21.173729 h -1.404153 c -0.839148,0 -1.526737,-0.686475 -1.526737,-1.526737 v -6.976187 c 0,-0.839148 0.686475,-1.526737 1.526737,-1.526737 h 1.405267 c 0.839149,0 1.526738,0.686474 1.526738,1.526737 v 6.977301 c -0.0011,0.839148 -0.687589,1.525623 -1.527852,1.525623 z\\\"     id=\\\"path239\\\"     inkscape:connector-curvature=\\\"0\\\" /><path     style=\\\"fill:#38454f;stroke-width:1.11440682\\\"     d=\\\"m 21.547055,27.86017 h -2.97658 c -0.407873,0 -0.739967,-0.332094 -0.739967,-0.741081 v -2.976581 c 0,-0.408987 0.332094,-0.74108 0.741081,-0.74108 h 2.97658 c 0.408988,0 0.741081,0.332093 0.741081,0.74108 v 2.976581 c -0.0011,0.408987 -0.333208,0.741081 -0.742195,0.741081 z\\\"     id=\\\"path241\\\"     inkscape:connector-curvature=\\\"0\\\" /><path     style=\\\"fill:none;stroke:#afb6bb;stroke-width:2.22881365;stroke-linecap:round;stroke-miterlimit:10\\\"     d=\\\"m 18.944915,52.377119 v 0 c 0,-3.677543 3.008899,-6.686441 6.686441,-6.686441 h 1.114407 c 3.677542,0 6.68644,3.008898 6.68644,6.686441 v 0 1.114406 c 0,2.463954 1.993674,4.457628 4.457628,4.457628 h 5.572033 c 2.463954,0 4.457628,-1.993674 4.457628,-4.457628\\\"     id=\\\"path243\\\"     inkscape:connector-curvature=\\\"0\\\" /><path     style=\\\"fill:#cbd4d8;stroke-width:1.11440682\\\"     d=\\\"m 60.346242,28.974576 h -2.397089 v 2.228814 h -3.343221 v -2.228814 h -4.457627 v 2.228814 h -3.34322 v -2.228814 h -4.457627 v 2.228814 h -3.343221 v -2.228814 h -2.359199 c -1.158983,0 -2.098428,0.939445 -2.098428,2.098428 v 12.12586 c 0,3.222865 2.61217,5.835034 5.835034,5.835034 h 16.190102 c 3.222864,0 5.835034,-2.612169 5.835034,-5.835034 v -12.16375 c 0,-1.137809 -0.922729,-2.060538 -2.060538,-2.060538 z\\\"     id=\\\"path245\\\"     inkscape:connector-curvature=\\\"0\\\" /><path     style=\\\"fill:#d3aa6e;stroke-width:1.11440682\\\"     d=\\\"M 42.347458,31.20339 H 39.004237 V 18.387712 c 0,-0.922729 0.748882,-1.67161 1.67161,-1.67161 v 0 c 0.922729,0 1.671611,0.748881 1.671611,1.67161 z\\\"     id=\\\"path247\\\"     inkscape:connector-curvature=\\\"0\\\" /><path     style=\\\"fill:#d3aa6e;stroke-width:1.11440682\\\"     d=\\\"M 57.949153,31.20339 H 54.605932 V 18.387712 c 0,-0.922729 0.748882,-1.67161 1.67161,-1.67161 v 0 c 0.922729,0 1.671611,0.748881 1.671611,1.67161 z\\\"     id=\\\"path249\\\"     inkscape:connector-curvature=\\\"0\\\" /><rect     x=\\\"46.805084\\\"     y=\\\"23.402542\\\"     style=\\\"fill:#839594;stroke-width:1.11440682\\\"     width=\\\"3.3432202\\\"     height=\\\"7.8008475\\\"     id=\\\"rect251\\\" /><rect     x=\\\"45.690678\\\"     y=\\\"49.033897\\\"     style=\\\"fill:#839594;stroke-width:1.11440682\\\"     width=\\\"5.5720339\\\"     height=\\\"4.4576273\\\"     id=\\\"rect253\\\" /><path     style=\\\"fill:#afb6bb;stroke-width:1.11440682\\\"     d=\\\"m 60.346242,28.974576 h -2.397089 v 2.228814 h -3.343221 v -2.228814 h -4.457627 v 2.228814 h -3.34322 v -2.228814 h -4.457627 v 2.228814 h -3.343221 v -2.228814 h -2.359199 c -1.158983,0 -2.098428,0.939445 -2.098428,2.098428 v 0.298661 c 0,1.13781 0.922729,2.060538 2.060538,2.060538 h 2.397089 3.343221 4.457627 3.34322 4.457627 3.343221 2.359199 c 1.158983,0 2.098428,-0.939445 2.098428,-2.098428 v -0.298661 c 0,-1.137809 -0.922729,-2.060538 -2.060538,-2.060538 z\\\"     id=\\\"path255\\\"     inkscape:connector-curvature=\\\"0\\\" /><g     id=\\\"g257\\\"     transform=\\\"translate(0,-1.5)\\\" /><g     id=\\\"g259\\\"     transform=\\\"translate(0,-1.5)\\\" /><g     id=\\\"g261\\\"     transform=\\\"translate(0,-1.5)\\\" /><g     id=\\\"g263\\\"     transform=\\\"translate(0,-1.5)\\\" /><g     id=\\\"g265\\\"     transform=\\\"translate(0,-1.5)\\\" /><g     id=\\\"g267\\\"     transform=\\\"translate(0,-1.5)\\\" /><g     id=\\\"g269\\\"     transform=\\\"translate(0,-1.5)\\\" /><g     id=\\\"g271\\\"     transform=\\\"translate(0,-1.5)\\\" /><g     id=\\\"g273\\\"     transform=\\\"translate(0,-1.5)\\\" /><g     id=\\\"g275\\\"     transform=\\\"translate(0,-1.5)\\\" /><g     id=\\\"g277\\\"     transform=\\\"translate(0,-1.5)\\\" /><g     id=\\\"g279\\\"     transform=\\\"translate(0,-1.5)\\\" /><g     id=\\\"g281\\\"     transform=\\\"translate(0,-1.5)\\\" /><g     id=\\\"g283\\\"     transform=\\\"translate(0,-1.5)\\\" /><g     id=\\\"g285\\\"     transform=\\\"translate(0,-1.5)\\\" /></svg>\",\n" +
            "        \"type\": \"image/svg+xml\"\n" +
            "      }\n" +
            "    }\n" +
            "  ],\n" +
            "  \"containers\": [\n" +
            "    {\n" +
            "      \"key\": \"1335840051469270\",\n" +
            "      \"status\": 0,\n" +
            "      \"name\": \"Smartphones\",\n" +
            "      \"kind\": \"CONTAINER\",\n" +
            "      \"prototype\": true,\n" +
            "      \"category\": \"CONTAINER\",\n" +
            "      \"shape\": {\n" +
            "        \"id\": \"6222894c-d307-49dd-97cf-bf95938bc9b8\",\n" +
            "        \"meta\": {\n" +
            "          \"id\": \"c5d37df2-b754-446d-b245-97b3966e0de4\",\n" +
            "          \"created\": 1549998990238\n" +
            "        },\n" +
            "        \"type\": \"CONTAINER\",\n" +
            "        \"colorSchemes\": [\n" +
            "          {\n" +
            "            \"id\": \"a540ccfb-2d24-4b8f-993d-f97a98e37472\",\n" +
            "            \"meta\": {\n" +
            "              \"id\": \"0972f5e3-6bb3-4edc-bb15-1d7d37c0fd8a\",\n" +
            "              \"created\": 1549998990248\n" +
            "            },\n" +
            "            \"name\": \"SECONDARY\",\n" +
            "            \"fillColor\": \"#e6ecf1\",\n" +
            "            \"strokeColor\": \"#08427B\",\n" +
            "            \"textColor\": \"#08427B\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": \"018b2e0f-f05b-4db5-a04a-aa34e8fecc56\",\n" +
            "            \"meta\": {\n" +
            "              \"id\": \"786aa0dc-bdfb-467c-8042-6c6a518fd66e\",\n" +
            "              \"created\": 1549998990247\n" +
            "            },\n" +
            "            \"name\": \"PRIMARY\",\n" +
            "            \"fillColor\": \"#08427B\",\n" +
            "            \"strokeColor\": \"#e6ecf1\",\n" +
            "            \"textColor\": \"#e6ecf1\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"input\": true,\n" +
            "        \"output\": true,\n" +
            "        \"isGroup\": true,\n" +
            "        \"group\": true\n" +
            "      },\n" +
            "      \"fill\": \"#08427B\",\n" +
            "      \"stroke\": \"#e6ecf1\",\n" +
            "      \"isGroup\": true,\n" +
            "      \"image\": {\n" +
            "        \"raw\": \"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\" standalone=\\\"no\\\"?><!-- Generator: Adobe Illustrator 19.0.0, SVG Export Plug-In . SVG Version: 6.00 Build 0)  --><svg   xmlns:dc=\\\"http://purl.org/dc/elements/1.1/\\\"   xmlns:cc=\\\"http://creativecommons.org/ns#\\\"   xmlns:rdf=\\\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\\\"   xmlns:svg=\\\"http://www.w3.org/2000/svg\\\"   xmlns=\\\"http://www.w3.org/2000/svg\\\"   xmlns:sodipodi=\\\"http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd\\\"   xmlns:inkscape=\\\"http://www.inkscape.org/namespaces/inkscape\\\"   version=\\\"1.1\\\"   id=\\\"Layer_1\\\"   x=\\\"0px\\\"   y=\\\"0px\\\"   viewBox=\\\"0 0 115.60519 206.2495\\\"   xml:space=\\\"preserve\\\"   width=\\\"168.153\\\"   height=\\\"299.99927\\\"   sodipodi:docname=\\\"smartphone.svg\\\"   inkscape:version=\\\"0.92.2 (5c3e80d, 2017-08-06)\\\"><metadata     id=\\\"metadata129\\\"><rdf:RDF><cc:Work         rdf:about=\\\"\\\"><dc:format>image/svg+xml</dc:format><dc:type           rdf:resource=\\\"http://purl.org/dc/dcmitype/StillImage\\\" /><dc:title></dc:title></cc:Work></rdf:RDF></metadata><defs     id=\\\"defs127\\\" /><sodipodi:namedview     pagecolor=\\\"#ffffff\\\"     bordercolor=\\\"#666666\\\"     borderopacity=\\\"1\\\"     objecttolerance=\\\"10\\\"     gridtolerance=\\\"10\\\"     guidetolerance=\\\"10\\\"     inkscape:pageopacity=\\\"0\\\"     inkscape:pageshadow=\\\"2\\\"     inkscape:window-width=\\\"1366\\\"     inkscape:window-height=\\\"705\\\"     id=\\\"namedview125\\\"     showgrid=\\\"false\\\"     fit-margin-top=\\\"0\\\"     fit-margin-left=\\\"0\\\"     fit-margin-right=\\\"0\\\"     fit-margin-bottom=\\\"0\\\"     inkscape:zoom=\\\"1.84375\\\"     inkscape:cx=\\\"-60.531803\\\"     inkscape:cy=\\\"193.86202\\\"     inkscape:window-x=\\\"-8\\\"     inkscape:window-y=\\\"-8\\\"     inkscape:window-maximized=\\\"1\\\"     inkscape:current-layer=\\\"g122\\\" /><g     id=\\\"g122\\\"     transform=\\\"matrix(0.58593607,0,0,0.58593607,-45.322155,0)\\\"><path       style=\\\"fill:#cccccc\\\"       d=\\\"m 112.35,0 h 127.3 c 19.33,0 35,15.67 35,35 v 282 c 0,19.33 -15.67,35 -35,35 h -127.3 c -19.33,0 -35,-15.67 -35,-35 V 35 c 0,-19.33 15.67,-35 35,-35 z\\\"       id=\\\"path88\\\"       inkscape:connector-curvature=\\\"0\\\" /><path       style=\\\"fill:#666666\\\"       d=\\\"m 112.35,0 h 63.15 v 352 h -63.15 c -19.33,0 -35,-15.67 -35,-35 V 35 c 0,-19.33 15.67,-35 35,-35 z\\\"       id=\\\"path90\\\"       inkscape:connector-curvature=\\\"0\\\" /><path       inkscape:connector-curvature=\\\"0\\\"       id=\\\"path94\\\"       d=\\\"m 191,303 h -31 c -4.142,0 -7.5,3.358 -7.5,7.5 0,4.142 3.358,7.5 7.5,7.5 h 31 c 4.142,0 7.5,-3.358 7.5,-7.5 0,-4.142 -3.358,-7.5 -7.5,-7.5 z\\\"       style=\\\"fill:#e6e6e6\\\" /><polygon       id=\\\"polygon96\\\"       points=\\\"258,54.5 258,271.5 175.5,271.5 94,271.5 94,54.5 175.5,54.5 \\\"       style=\\\"fill:#32a5b9\\\" /><rect       id=\\\"rect98\\\"       height=\\\"217\\\"       width=\\\"81.5\\\"       style=\\\"fill:#2b8a9a\\\"       y=\\\"54.5\\\"       x=\\\"94\\\" /><g       id=\\\"g104\\\"><rect         id=\\\"rect102\\\"         height=\\\"62.535999\\\"         width=\\\"104.647\\\"         style=\\\"fill:#f8b242\\\"         y=\\\"146.73199\\\"         x=\\\"123.676\\\" /></g><rect       x=\\\"123.676\\\"       y=\\\"146.73199\\\"       style=\\\"fill:#d09838\\\"       width=\\\"51.824001\\\"       height=\\\"62.535999\\\"       id=\\\"rect106\\\" /><polygon       id=\\\"polygon112\\\"       points=\\\"228.324,146.732 175.5,185.5 123.676,146.732 \\\"       style=\\\"fill:#e45527\\\" /><polygon       style=\\\"fill:#b7461d\\\"       points=\\\"123.676,146.732 175.5,146.732 175.5,185.5 \\\"       id=\\\"polygon116\\\" /></g></svg>\",\n" +
            "        \"type\": \"image/svg+xml\"\n" +
            "      }\n" +
            "    }\n" +
            "  ],\n" +
            "  \"components\": [\n" +
            "    {\n" +
            "      \"key\": \"3625870952211205\",\n" +
            "      \"status\": 0,\n" +
            "      \"name\": \"Android\",\n" +
            "      \"kind\": \"COMPONENT\",\n" +
            "      \"prototype\": true,\n" +
            "      \"category\": \"COMPONENT\",\n" +
            "      \"shape\": {\n" +
            "        \"id\": \"c92ce01e-9c32-4aa5-ada7-a8edc0c546fd\",\n" +
            "        \"meta\": {\n" +
            "          \"id\": \"ad94c39d-c7e7-419b-9ac9-ead41961f390\",\n" +
            "          \"created\": 1549998990239\n" +
            "        },\n" +
            "        \"type\": \"COMPONENT\",\n" +
            "        \"colorSchemes\": [\n" +
            "          {\n" +
            "            \"id\": \"4209c888-3cf1-44e2-b72b-3d6ad7c6b8e2\",\n" +
            "            \"meta\": {\n" +
            "              \"id\": \"ddc346f5-33d3-4a72-9893-f9e0868d9d58\",\n" +
            "              \"created\": 1549998990243\n" +
            "            },\n" +
            "            \"name\": \"SECONDARY\",\n" +
            "            \"fillColor\": \"#e7eff8\",\n" +
            "            \"strokeColor\": \"#1368BD\",\n" +
            "            \"textColor\": \"#1368BD\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": \"f355afea-1c1c-4986-8ef1-16b4b8e250af\",\n" +
            "            \"meta\": {\n" +
            "              \"id\": \"ad176e45-4b2b-4227-b1a1-41e3ffaf3708\",\n" +
            "              \"created\": 1549998990243\n" +
            "            },\n" +
            "            \"name\": \"PRIMARY\",\n" +
            "            \"fillColor\": \"#1368BD\",\n" +
            "            \"strokeColor\": \"#e7eff8\",\n" +
            "            \"textColor\": \"#e7eff8\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"input\": true,\n" +
            "        \"output\": true,\n" +
            "        \"isGroup\": false,\n" +
            "        \"group\": false\n" +
            "      },\n" +
            "      \"fill\": \"#1368BD\",\n" +
            "      \"stroke\": \"#e7eff8\",\n" +
            "      \"isGroup\": false,\n" +
            "      \"image\": {\n" +
            "        \"raw\": \"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\" standalone=\\\"no\\\"?><svg   xmlns:dc=\\\"http://purl.org/dc/elements/1.1/\\\"   xmlns:cc=\\\"http://creativecommons.org/ns#\\\"   xmlns:rdf=\\\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\\\"   xmlns:svg=\\\"http://www.w3.org/2000/svg\\\"   xmlns=\\\"http://www.w3.org/2000/svg\\\"   xmlns:xlink=\\\"http://www.w3.org/1999/xlink\\\"   xmlns:sodipodi=\\\"http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd\\\"   xmlns:inkscape=\\\"http://www.inkscape.org/namespaces/inkscape\\\"   viewBox=\\\"0 0 107.306 125.99955\\\"   version=\\\"1.1\\\"   id=\\\"svg3806\\\"   sodipodi:docname=\\\"android-logo.svg\\\"   width=\\\"107.306\\\"   height=\\\"125.99955\\\"   inkscape:version=\\\"0.92.2 (5c3e80d, 2017-08-06)\\\">  <metadata     id=\\\"metadata3812\\\">    <rdf:RDF>      <cc:Work         rdf:about=\\\"\\\">        <dc:format>image/svg+xml</dc:format>        <dc:type           rdf:resource=\\\"http://purl.org/dc/dcmitype/StillImage\\\" />        <dc:title></dc:title>      </cc:Work>    </rdf:RDF>  </metadata>  <defs     id=\\\"defs3810\\\" />  <sodipodi:namedview     pagecolor=\\\"#ffffff\\\"     bordercolor=\\\"#666666\\\"     borderopacity=\\\"1\\\"     objecttolerance=\\\"10\\\"     gridtolerance=\\\"10\\\"     guidetolerance=\\\"10\\\"     inkscape:pageopacity=\\\"0\\\"     inkscape:pageshadow=\\\"2\\\"     inkscape:window-width=\\\"1366\\\"     inkscape:window-height=\\\"705\\\"     id=\\\"namedview3808\\\"     showgrid=\\\"false\\\"     fit-margin-top=\\\"0\\\"     fit-margin-left=\\\"0\\\"     fit-margin-right=\\\"0\\\"     fit-margin-bottom=\\\"0\\\"     inkscape:zoom=\\\"1.5536232\\\"     inkscape:cx=\\\"80.268187\\\"     inkscape:cy=\\\"36.640799\\\"     inkscape:window-x=\\\"-8\\\"     inkscape:window-y=\\\"-8\\\"     inkscape:window-maximized=\\\"1\\\"     inkscape:current-layer=\\\"svg3806\\\" />  <g     id=\\\"g3796\\\"     style=\\\"fill:#a4c639\\\"     transform=\\\"matrix(0.36598226,0,0,0.36598226,53.652999,25.50082)\\\">    <use       xlink:href=\\\"#b\\\"       id=\\\"use3781\\\"       style=\\\"stroke:#ffffff;stroke-width:14.39999962\\\"       x=\\\"0\\\"       y=\\\"0\\\"       width=\\\"100%\\\"       height=\\\"100%\\\" />    <use       xlink:href=\\\"#a\\\"       transform=\\\"scale(-1,1)\\\"       id=\\\"use3783\\\"       x=\\\"0\\\"       y=\\\"0\\\"       width=\\\"100%\\\"       height=\\\"100%\\\" />    <g       id=\\\"a\\\"       style=\\\"stroke:#ffffff;stroke-width:7.19999981\\\">      <rect         rx=\\\"6.5\\\"         transform=\\\"rotate(29)\\\"         height=\\\"86\\\"         width=\\\"13\\\"         y=\\\"-86\\\"         x=\\\"14\\\"         id=\\\"rect3785\\\" />      <rect         id=\\\"c\\\"         rx=\\\"24\\\"         height=\\\"133\\\"         width=\\\"48\\\"         y=\\\"41\\\"         x=\\\"-143\\\" />      <use         y=\\\"97\\\"         x=\\\"85\\\"         xlink:href=\\\"#c\\\"         id=\\\"use3788\\\"         width=\\\"100%\\\"         height=\\\"100%\\\" />    </g>    <g       id=\\\"b\\\">      <ellipse         cy=\\\"41\\\"         rx=\\\"91\\\"         ry=\\\"84\\\"         id=\\\"ellipse3791\\\"         cx=\\\"0\\\" />      <rect         rx=\\\"22\\\"         height=\\\"182\\\"         width=\\\"182\\\"         y=\\\"20\\\"         x=\\\"-91\\\"         id=\\\"rect3793\\\" />    </g>  </g>  <g     id=\\\"g3804\\\"     style=\\\"fill:#ffffff;stroke:#ffffff;stroke-width:7.19999981\\\"     transform=\\\"matrix(0.36598226,0,0,0.36598226,53.652999,25.50082)\\\">    <path       d=\\\"M -95,44.5 H 95\\\"       id=\\\"path3798\\\"       inkscape:connector-curvature=\\\"0\\\" />    <circle       cx=\\\"-42\\\"       r=\\\"4\\\"       id=\\\"circle3800\\\"       cy=\\\"0\\\" />    <circle       cx=\\\"42\\\"       r=\\\"4\\\"       id=\\\"circle3802\\\"       cy=\\\"0\\\" />  </g></svg>\",\n" +
            "        \"type\": \"image/svg+xml\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"key\": \"4659431761646836\",\n" +
            "      \"status\": 0,\n" +
            "      \"name\": \"iPhone\",\n" +
            "      \"kind\": \"COMPONENT\",\n" +
            "      \"prototype\": true,\n" +
            "      \"category\": \"COMPONENT\",\n" +
            "      \"shape\": {\n" +
            "        \"id\": \"c92ce01e-9c32-4aa5-ada7-a8edc0c546fd\",\n" +
            "        \"meta\": {\n" +
            "          \"id\": \"ad94c39d-c7e7-419b-9ac9-ead41961f390\",\n" +
            "          \"created\": 1549998990239\n" +
            "        },\n" +
            "        \"type\": \"COMPONENT\",\n" +
            "        \"colorSchemes\": [\n" +
            "          {\n" +
            "            \"id\": \"4209c888-3cf1-44e2-b72b-3d6ad7c6b8e2\",\n" +
            "            \"meta\": {\n" +
            "              \"id\": \"ddc346f5-33d3-4a72-9893-f9e0868d9d58\",\n" +
            "              \"created\": 1549998990243\n" +
            "            },\n" +
            "            \"name\": \"SECONDARY\",\n" +
            "            \"fillColor\": \"#e7eff8\",\n" +
            "            \"strokeColor\": \"#1368BD\",\n" +
            "            \"textColor\": \"#1368BD\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"id\": \"f355afea-1c1c-4986-8ef1-16b4b8e250af\",\n" +
            "            \"meta\": {\n" +
            "              \"id\": \"ad176e45-4b2b-4227-b1a1-41e3ffaf3708\",\n" +
            "              \"created\": 1549998990243\n" +
            "            },\n" +
            "            \"name\": \"PRIMARY\",\n" +
            "            \"fillColor\": \"#1368BD\",\n" +
            "            \"strokeColor\": \"#e7eff8\",\n" +
            "            \"textColor\": \"#e7eff8\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"input\": true,\n" +
            "        \"output\": true,\n" +
            "        \"isGroup\": false,\n" +
            "        \"group\": false\n" +
            "      },\n" +
            "      \"fill\": \"#1368BD\",\n" +
            "      \"stroke\": \"#e7eff8\",\n" +
            "      \"isGroup\": false,\n" +
            "      \"image\": {\n" +
            "        \"raw\": \"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\" standalone=\\\"no\\\"?><svg   xmlns:dc=\\\"http://purl.org/dc/elements/1.1/\\\"   xmlns:cc=\\\"http://creativecommons.org/ns#\\\"   xmlns:rdf=\\\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\\\"   xmlns:svg=\\\"http://www.w3.org/2000/svg\\\"   xmlns=\\\"http://www.w3.org/2000/svg\\\"   xmlns:sodipodi=\\\"http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd\\\"   xmlns:inkscape=\\\"http://www.inkscape.org/namespaces/inkscape\\\"   width=\\\"102.595\\\"   height=\\\"125.99957\\\"   viewBox=\\\"0 0 102.595 125.99957\\\"   version=\\\"1.1\\\"   preserveAspectRatio=\\\"xMidYMid\\\"   id=\\\"svg4422\\\"   sodipodi:docname=\\\"apple-logo.svg\\\"   inkscape:version=\\\"0.92.2 (5c3e80d, 2017-08-06)\\\">  <metadata     id=\\\"metadata4428\\\">    <rdf:RDF>      <cc:Work         rdf:about=\\\"\\\">        <dc:format>image/svg+xml</dc:format>        <dc:type           rdf:resource=\\\"http://purl.org/dc/dcmitype/StillImage\\\" />        <dc:title></dc:title>      </cc:Work>    </rdf:RDF>  </metadata>  <defs     id=\\\"defs4426\\\" />  <sodipodi:namedview     pagecolor=\\\"#ffffff\\\"     bordercolor=\\\"#666666\\\"     borderopacity=\\\"1\\\"     objecttolerance=\\\"10\\\"     gridtolerance=\\\"10\\\"     guidetolerance=\\\"10\\\"     inkscape:pageopacity=\\\"0\\\"     inkscape:pageshadow=\\\"2\\\"     inkscape:window-width=\\\"1366\\\"     inkscape:window-height=\\\"705\\\"     id=\\\"namedview4424\\\"     showgrid=\\\"false\\\"     fit-margin-top=\\\"0\\\"     fit-margin-left=\\\"0\\\"     fit-margin-right=\\\"0\\\"     fit-margin-bottom=\\\"0\\\"     inkscape:zoom=\\\"2.9968254\\\"     inkscape:cx=\\\"25.114914\\\"     inkscape:cy=\\\"72.230437\\\"     inkscape:window-x=\\\"-8\\\"     inkscape:window-y=\\\"-8\\\"     inkscape:window-maximized=\\\"1\\\"     inkscape:current-layer=\\\"g4420\\\" />  <g     id=\\\"g4420\\\"     transform=\\\"scale(0.40076172)\\\">    <path       d=\\\"m 213.80339,167.03094 c 0.44181,47.57871 41.73909,63.4117 42.19661,63.61379 -0.34919,1.11663 -6.59862,22.56356 -21.75737,44.71672 -13.10408,19.15252 -26.70438,38.23488 -48.12887,38.63009 -21.05171,0.3879 -27.82101,-12.48371 -51.88907,-12.48371 -24.06079,0 -31.58179,12.08847 -51.509577,12.87161 C 62.035041,315.16201 46.287383,293.66852 33.074408,274.58616 6.0752932,235.55254 -14.557617,164.28633 13.147166,116.18047 26.910311,92.290905 51.506092,77.163036 78.202612,76.77511 98.509914,76.387746 117.67759,90.437185 130.0917,90.437185 c 12.40624,0 35.69905,-16.895682 60.18593,-14.414338 10.25104,0.426658 39.02588,4.140841 57.50299,31.186543 -1.4888,0.92294 -34.33427,20.04402 -33.97723,59.8216 M 174.23914,50.198703 C 185.21833,36.908832 192.60796,18.408102 190.59199,0 c -15.82568,0.63605022 -34.96248,10.545791 -46.31388,23.828351 -10.17304,11.762325 -19.08234,30.588677 -16.67845,48.632442 17.63957,1.36475 35.65975,-8.963767 46.63948,-22.262068\\\"       id=\\\"path4418\\\"       inkscape:connector-curvature=\\\"0\\\"       style=\\\"fill:#000000\\\" />    <path       style=\\\"fill:#a0a0a0;stroke:none;stroke-width:1.33474576\\\"       d=\\\"M 20.768867,118.19222 C 13.333362,110.55847 4.5949285,93.681698 2.2991031,82.521072 -2.4101515,59.628114 5.0336851,40.316452 21.156112,33.600034 c 0.875798,-0.364847 1.725313,-0.678799 2.564219,-0.941278 5.584189,-1.747194 10.698253,-1.213663 19.964689,1.770792 l 8.994288,2.896804 7.569815,-2.896804 c 13.712608,-5.247516 27.02579,-3.175662 35.083,5.459766 l 3.057926,3.277377 -3.269283,2.142117 c -1.798106,1.178164 -4.907015,5.489521 -6.908688,9.580793 -3.006394,6.144845 -3.504801,8.575199 -2.865521,13.972929 1.039339,8.775585 5.325217,16.765086 10.946704,20.406249 2.543055,1.647193 4.631169,3.811775 4.640259,4.810182 0.0287,3.155566 -8.21968,17.421399 -13.620548,23.557109 -6.580936,7.47634 -11.892311,8.73156 -22.167753,5.23883 -8.436845,-2.86777 -16.350355,-2.85154 -23.289386,0.0478 -9.214922,3.85024 -13.711667,2.84148 -21.086966,-4.73046 z\\\"       id=\\\"path4432\\\"       inkscape:connector-curvature=\\\"0\\\"       transform=\\\"scale(2.4952483)\\\"       sodipodi:nodetypes=\\\"csssscsscsssscssccc\\\" />    <path       style=\\\"fill:#a0a0a0;stroke:none;stroke-width:1.33474576\\\"       d=\\\"m 51.564449,25.933154 c 0,-9.229701 10.834756,-22.1655576 20.556824,-24.5432469 l 3.872502,-0.94708328 -0.772,5.75569228 C 73.874025,16.246745 65.228605,25.78433 55.235,28.247828 c -3.239821,0.798641 -3.670551,0.527019 -3.670551,-2.314674 z\\\"       id=\\\"path4434\\\"       inkscape:connector-curvature=\\\"0\\\"       transform=\\\"scale(2.4952483)\\\" />  </g></svg>\",\n" +
            "        \"type\": \"image/svg+xml\"\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";


    @Before
    public void init() throws NotSupportedException, SystemException {
        ObjectMapper mapper = jsonUtils.getMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        environment = processBuilder.environment();
        URL url = OrpheusDbJPAEntityManagerUtils.class.getResource("/config/config.properties");
        Properties properties = new Properties();
        String persistenceName = StringUtils.EMPTY;
        try {
            properties.load(url.openStream());
            Map<String, String> map = MapUtils.fromProperties(properties);
            if (!map.isEmpty()) {
                persistenceName = StringUtils.defaultIfBlank(map.get("PERSISTENCE_NAME"), environment.get("PERSISTENCE_NAME"));
                environment.putAll(map);
                log.info("Properties taken from config file '" + url.getFile().replace("file:" + File.separator, "") + "'");
            } else {
                log.info("Properties taken from system map...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        OrpheusDbJPAEntityManagerUtils.init(persistenceName, environment);
    }

    @Test
    public void testFindUsagesOf() {
        try {
            String modelName = "Firefox";
            Collection result = ModelsController.findUsagesOf(modelName);
            if (CollectionUtils.isNotEmpty(result)) {
                try {
                    log.debug(jsonUtils.toJSON(result));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                log.debug(jsonUtils.toJSON(result));
            } else {
                log.debug("Test done successfully due there is no model with name '" + modelName + "'");
            }
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

}

