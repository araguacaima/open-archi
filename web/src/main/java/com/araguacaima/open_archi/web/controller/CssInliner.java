package com.araguacaima.open_archi.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.StringTokenizer;

public class CssInliner {

    private String concatenateProperties(String oldProp, String newProp) {
        newProp = StringUtils.defaultString(newProp, StringUtils.EMPTY);
        oldProp = oldProp.trim();
        if (!oldProp.endsWith(";"))
            oldProp += ";";
        return oldProp + newProp.replaceAll("\\s{2,}", " ").trim();
    }

    public String inlineCss(String html) {
        final String style = "style";
        Document doc = Jsoup.parse(html);
        Elements els = doc.select(style);// to get all the style elements
        for (Element e : els) {
            final String data = e.getAllElements().get(0).data();
            String styleRules = data.replaceAll("\n", "").trim();
            String delims = "{}";
            StringTokenizer st = new StringTokenizer(styleRules, delims);
            while (st.countTokens() > 1) {
                String selector = st.nextToken(), properties = st.nextToken().trim();
                selector = selector.trim();
                if (!selector.contains(":")) { // skip a:hover rules, etc.
                    if (!selector.startsWith("@")) {
                        Elements selectedElements = doc.select(selector);
                        for (Element selElem : selectedElements) {
                            String oldProperties = selElem.attr(style);
                            selElem.attr(style, oldProperties.length() > 0 ?
                                    concatenateProperties(oldProperties, properties) :
                                    properties);
                        }
                    } else {

                    }
                }
            }
            //e.remove();
        }
        return doc.toString();
    }
}