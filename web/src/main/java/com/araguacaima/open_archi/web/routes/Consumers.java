package com.araguacaima.open_archi.web.routes;

import com.araguacaima.open_archi.persistence.diagrams.architectural.Consumer;
import com.araguacaima.open_archi.persistence.diagrams.core.Item;
import com.araguacaima.open_archi.persistence.diagrams.meta.Account;
import com.araguacaima.orpheusdb.utils.OrpheusDbJPAEntityManagerUtils;
import com.araguacaima.open_archi.web.DBUtil;
import com.araguacaima.open_archi.web.common.Commons;
import org.pac4j.sparkjava.SparkWebContext;
import spark.RouteGroup;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;

import static com.araguacaima.open_archi.web.common.Commons.*;
import static java.net.HttpURLConnection.*;
import static spark.Spark.*;

public class Consumers implements RouteGroup {

    public static final String PATH ="/consumers";

    @Override
    public void addRoutes() {
        get(Commons.EMPTY_PATH, (request, response) -> getList(request, response, Item.GET_ALL_CONSUMERS, null, null));
        post(Commons.EMPTY_PATH, (request, response) -> {
            try {
                Consumer consumer = jsonUtils.fromJSON(request.body(), Consumer.class);
                if (consumer == null) {
                    throw new Exception("Invalid kind for consumer");
                }
                final SparkWebContext ctx = new SparkWebContext(request, response);
                Map<Object, Object> map = new HashMap<>();
                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                consumer = consumer.validateCreation(map);
                DBUtil.persist(consumer);
                response.status(HTTP_CREATED);
                response.type(JSON_CONTENT_TYPE);
                response.header("Location", request.pathInfo() + Commons.SEPARATOR_PATH + consumer.getId());
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/:uuid", (request, response) -> {
            try {
                String id = request.params(":uuid");
                Consumer consumer = OrpheusDbJPAEntityManagerUtils.find(Consumer.class, id);
                if (consumer != null) {
                    consumer = consumer.validateRequest();
                }
                response.status(HTTP_OK);
                response.type(JSON_CONTENT_TYPE);
                return jsonUtils.toJSON(consumer);
            } catch (Exception ex) {
                return throwError(response, ex);
            }
        });
        put("/:uuid", (request, response) -> {
            try {
                Consumer consumer = jsonUtils.fromJSON(request.body(), Consumer.class);
                if (consumer == null) {
                    throw new Exception("Invalid kind of consumer");
                }
                String id = request.params(":uuid");
                consumer.setId(id);
                final SparkWebContext ctx = new SparkWebContext(request, response);
                Map<Object, Object> map = new HashMap<>();
                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                consumer = consumer.validateReplacement(map);
                DBUtil.persist(consumer);
                response.status(HTTP_OK);
                return EMPTY_RESPONSE;
            } catch (EntityNotFoundException ex) {
                response.status(HTTP_NOT_FOUND);
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        patch("/:uuid", (request, response) -> {
            try {
                Consumer consumer = jsonUtils.fromJSON(request.body(), Consumer.class);
                if (consumer == null) {
                    throw new Exception("Invalid kind of consumer");
                }
                String id = request.params(":uuid");
                consumer.setId(id);
                final SparkWebContext ctx = new SparkWebContext(request, response);
                Map<Object, Object> map = new HashMap<>();
                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                consumer = consumer.validateModification(map);
                DBUtil.update(consumer);
                response.status(HTTP_OK);
                return EMPTY_RESPONSE;
            } catch (EntityNotFoundException ex) {
                response.status(HTTP_NOT_FOUND);
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
    }
}
