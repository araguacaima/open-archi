package com.araguacaima.open_archi.web.routes;

import com.araguacaima.open_archi.persistence.capabilities.commons.Capability;
import com.araguacaima.open_archi.persistence.capabilities.commons.Dimension;
import com.araguacaima.open_archi.persistence.diagrams.meta.Account;
import com.araguacaima.open_archi.web.DBUtil;
import com.araguacaima.open_archi.web.MessagesWrapper;
import com.araguacaima.open_archi.web.common.Commons;
import com.araguacaima.orpheusdb.utils.OrpheusDbJPAEntityManagerUtils;
import org.pac4j.sparkjava.SparkWebContext;
import spark.RouteGroup;

import javax.persistence.EntityNotFoundException;
import java.util.*;

import static com.araguacaima.open_archi.web.common.Commons.*;
import static java.net.HttpURLConnection.*;
import static spark.Spark.*;

public class Capabilities implements RouteGroup {

    public static final String PATH = "/capabilities";

/*get post /capabilities/:capability-id/dimensions
get /dimensions/:dimension-id
get post /dimensions/:dimension-id/metrics
get /metrics/:metric-id
get put /metrics/:metric-id/base-thresholds
get /metrics/:metric-id/base-thresholds/:base-threshold-id
get put /metrics/:metric-id/thresholds
get /metrics/:metric-id/thresholds/:threshold-id*/

    @Override
    public void addRoutes() {
        get(Commons.EMPTY_PATH, (request, response) -> getList(request, response, Capability.GET_ALL_CAPABILITIES, null, Capability.class));
        post(Commons.EMPTY_PATH, (request, response) -> {
            try {
                Capability capability = jsonUtils.fromJSON(request.body(), Capability.class);
                if (capability == null) {
                    throw new Exception("Invalid kind for capability");
                }
                final SparkWebContext ctx = new SparkWebContext(request, response);
                Map<Object, Object> map = new HashMap<>();
                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                capability = capability.validateCreation(map);
                DBUtil.persist(capability);
                response.status(HTTP_CREATED);
                response.type(JSON_CONTENT_TYPE);
                response.header("Location", request.pathInfo() + Commons.SEPARATOR_PATH + capability.getId());
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/:cuuid/dimensions", (request, response) -> {
            Map<String, Object> params = new HashMap<>();
            params.put("id", request.params(":cuuid"));
            return getList(request, response, Capability.GET_ALL_DIMENSIONS, params, Collection.class);
        });
        put("/:cuuid/dimensions", (request, response) -> {
            try {
                String body = request.body();
                final Dimension dimension = jsonUtils.fromJSON(body, Dimension.class);
                if (dimension == null) {
                    throw new Exception("Invalid kind of dimension");
                }
                String capabilityId = request.params(":cuuid");
                Capability capability = OrpheusDbJPAEntityManagerUtils.find(Capability.class, capabilityId);
                if (capability == null) {
                    throw new EntityNotFoundException("There is no capability with identifer of '" + capabilityId + "' to which associate the provided dimension");
                } else {
                    Set<Dimension> dimensions = capability.getDimensions();
                    Optional<Dimension> dimensionOpt = dimensions.stream().filter(d -> d.getName().equals(dimension.getName())).findFirst();
                    if (dimensionOpt.isPresent()) {
                        Dimension dimensionToAdd = dimensionOpt.get();
                        dimensionToAdd.copyNonEmpty(dimension);
                        OrpheusDbJPAEntityManagerUtils.merge(dimensionToAdd);
                        response.status(HTTP_OK);
                        response.header("Location", "/dimensions" + Commons.SEPARATOR_PATH + dimensionToAdd.getId());
                    } else {
                        OrpheusDbJPAEntityManagerUtils.persist(dimension);
                        dimensions.add(dimension);
                        OrpheusDbJPAEntityManagerUtils.merge(capability);
                        response.status(HTTP_CREATED);
                        response.header("Location", "/dimensions" + Commons.SEPARATOR_PATH + dimension.getId());
                    }
                    response.type(JSON_CONTENT_TYPE);
                    return EMPTY_RESPONSE;
                }
            } catch (EntityNotFoundException enfe) {
                response.status(HTTP_NOT_FOUND);
                response.type(JSON_CONTENT_TYPE);
                return jsonUtils.toJSON(MessagesWrapper.fromExceptionToMessages(enfe, HTTP_NOT_FOUND));
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
    }
}
