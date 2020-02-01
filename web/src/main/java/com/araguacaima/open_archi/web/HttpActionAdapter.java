package com.araguacaima.open_archi.web;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.sparkjava.DefaultHttpActionAdapter;
import org.pac4j.sparkjava.SparkWebContext;
import spark.Response;
import spark.TemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static com.araguacaima.open_archi.web.common.Commons.buildModelAndView;
import static org.pac4j.core.context.HttpConstants.*;

public class HttpActionAdapter extends DefaultHttpActionAdapter {

    private static final String JSON_CONTENT_TYPE = "application/json";
    private final TemplateEngine templateEngine;

    public HttpActionAdapter(final TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public Object adapt(int code, SparkWebContext context) {
        Map map = new HashMap();
        if (code == HttpConstants.UNAUTHORIZED) {
            map.put("title", HttpConstants.UNAUTHORIZED);
            map.put("message", "UNAUTHORIZED");
            stop(401, templateEngine.render(buildModelAndView(map, "error")));
        } else if (code == HttpConstants.FORBIDDEN) {
            map.put("title", HttpConstants.FORBIDDEN);
            map.put("message", "FORBIDDEN");
            stop(403, templateEngine.render(buildModelAndView(map, "error")));
        } else {
            Response response = context.getSparkResponse();
            response.header("Allow", "POST, GET, PUT, OPTIONS, HEAD");
            response.header("Content-Type", JSON_CONTENT_TYPE + ", " + HTML_CONTENT_TYPE);
            response.header(ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
            response.header(ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, "true");
            response.header(ACCESS_CONTROL_ALLOW_METHODS_HEADER, "*");
            response.header(ACCESS_CONTROL_ALLOW_HEADERS_HEADER, "*");
            return super.adapt(code, context);
        }
        return null;
    }
}
