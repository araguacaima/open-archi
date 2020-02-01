package com.araguacaima.open_archi.web.common;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.sparkjava.SparkWebContext;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import static com.araguacaima.open_archi.web.common.Commons.JSON_CONTENT_TYPE;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.pac4j.core.context.HttpConstants.HTML_CONTENT_TYPE;

public class Security {
    public final static String JWT_SALT = "12345678901234567890123456789012";

    public static void setCORS(Request request, Response response) {
        response.status(HTTP_OK);
        response.header("Allow", "POST, GET, PUT, OPTIONS, HEAD");
        response.header("Content-Type", JSON_CONTENT_TYPE + ", " + HTML_CONTENT_TYPE);
        String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
        if (accessControlRequestHeaders != null) {
            response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
        }
        String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
        if (accessControlRequestMethod != null) {
            response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
        }
    }


    public static ModelAndView forceLogin(final Config config, final Request request, final Response response) {
        final SparkWebContext context = new SparkWebContext(request, response);
        final String clientName = context.getRequestParameter(Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER);
        final Client client = config.getClients().findClient(clientName);
        HttpAction action;
        try {
            action = client.redirect(context);
        } catch (final HttpAction e) {
            action = e;
        }
        config.getHttpActionAdapter().adapt(action.getCode(), context);
        return null;
    }

}
