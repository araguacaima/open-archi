package com.araguacaima.open_archi.web.wrapper;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;

import java.io.IOException;

public abstract class RsqlJsonFilter {

    public static Object rsql(final String query, final Object json, String filter)
            throws IOException {
        Node rootNode = new RSQLParser().parse(query);
        String accept = rootNode.accept(new JsonPathRsqlVisitor(json, filter));
        return accept.equals("\"" + JsonParserSpecification.MARKED_FOR_DELETION + "\"" ) ? "{}" : accept;
    }
}