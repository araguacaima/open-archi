package com.araguacaima.open_archi.web.routes;

import com.araguacaima.open_archi.persistence.commons.exceptions.EntityError;
import com.araguacaima.open_archi.persistence.commons.exceptions.EntityInsertionError;
import com.araguacaima.open_archi.persistence.diagrams.architectural.System;
import com.araguacaima.open_archi.persistence.diagrams.architectural.*;
import com.araguacaima.open_archi.persistence.diagrams.bpm.BpmModel;
import com.araguacaima.open_archi.persistence.diagrams.classes.ClassesModel;
import com.araguacaima.open_archi.persistence.diagrams.core.ElementKind;
import com.araguacaima.open_archi.persistence.diagrams.core.Item;
import com.araguacaima.open_archi.persistence.diagrams.core.Taggable;
import com.araguacaima.open_archi.persistence.diagrams.er.ErModel;
import com.araguacaima.open_archi.persistence.diagrams.flowchart.FlowchartModel;
import com.araguacaima.open_archi.persistence.diagrams.gantt.GanttModel;
import com.araguacaima.open_archi.persistence.diagrams.sequence.SequenceModel;
import com.araguacaima.open_archi.persistence.diagrams.meta.Account;
import com.araguacaima.open_archi.web.DBUtil;
import com.araguacaima.open_archi.web.MessagesWrapper;
import com.araguacaima.open_archi.web.common.Commons;
import com.araguacaima.orpheusdb.utils.OrpheusDbJPAEntityManagerUtils;
import org.pac4j.sparkjava.SparkWebContext;
import spark.RouteGroup;
import spark.route.HttpMethod;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.araguacaima.open_archi.web.common.Commons.*;
import static com.araguacaima.open_archi.web.common.Security.setCORS;
import static java.net.HttpURLConnection.*;
import static spark.Spark.*;

public class Diagrams implements RouteGroup {

    public static final String PATH = "/diagrams";

    @Override
    public void addRoutes() {
        get("/architectures", (request, response) -> {
            Map<String, Object> params = new HashMap<>();
            params.put("modelType", ArchitecturalModel.class);
            response.type(JSON_CONTENT_TYPE);
            return getList(request, response, Item.GET_MODELS_BY_TYPE, params, null);
        });
        post("/architectures", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                ArchitecturalModel model = jsonUtils.fromJSON(request.body(), ArchitecturalModel.class);
                if (model == null) {
                    throw new Exception("Invalid model");
                }
                if (model.getKind() != ElementKind.ARCHITECTURE_MODEL) {
                    throw new Exception("Invalid kind of model '" + model.getKind() + "'. It should be '" + ElementKind.ARCHITECTURE_MODEL + "'");
                }
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                model = model.validateCreation(map);
                DBUtil.persist(model);
                response.status(HTTP_CREATED);
                response.type(JSON_CONTENT_TYPE);
                response.header("Location", request.pathInfo() + Commons.SEPARATOR_PATH + model.getId());
                Taggable storedModel = OrpheusDbJPAEntityManagerUtils.find(model);
                return jsonUtils.toJSON(storedModel);
            } catch (Throwable ex) {
                ex.printStackTrace();
                return throwError(response, ex);
            }
        });
        patch("/architectures/:uuid", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                ArchitecturalModel model = jsonUtils.fromJSON(request.body(), ArchitecturalModel.class);
                if (model == null) {
                    throw new Exception("Invalid kind of model");
                }
                String id = request.params(":uuid");
                model.setId(id);
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                model = model.validateModification(map);
                DBUtil.update(model);
                response.status(HTTP_OK);
                return EMPTY_RESPONSE;
            } catch (EntityNotFoundException ex) {
                response.status(HTTP_NOT_FOUND);
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/architectures/:uuid/relationships", (request, response) -> {
            Map<String, Object> params = new HashMap<>();
            params.put("id", request.params(":uuid"));
            return getList(request, response, ArchitecturalModel.GET_ALL_RELATIONSHIPS, params, Collection.class);
        });
        put("/architectures/:uuid/relationships", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                com.araguacaima.open_archi.persistence.diagrams.architectural.ArchitecturalRelationship relationship = jsonUtils.fromJSON(request.body(), com.araguacaima.open_archi.persistence.diagrams.architectural.ArchitecturalRelationship.class);
                if (relationship == null) {
                    throw new Exception("Invalid kind of relationship");
                }
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                relationship = relationship.validateReplacement(map);
                DBUtil.persist(relationship);
                response.status(HTTP_CREATED);
                response.type(JSON_CONTENT_TYPE);
                response.header("Location", request.pathInfo() + Commons.SEPARATOR_PATH + relationship.getId());
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/architectures/:uuid/consumers", (request, response) -> {
            Map<String, Object> params = new HashMap<>();
            params.put("id", request.params(":uuid"));
            return getList(request, response, ArchitecturalModel.GET_ALL_CONSUMERS_FOR_MODEL, params, Collection.class);
        });
        post("/architectures/:uuid/consumers", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                Consumer consumer = jsonUtils.fromJSON(request.body(), Consumer.class);
                if (consumer == null) {
                    throw new Exception("Invalid kind of relationship");
                }
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                consumer = consumer.validateCreation(map);
                DBUtil.persist(consumer);
                response.status(HTTP_CREATED);
                response.type(JSON_CONTENT_TYPE);
                response.header("Location", request.pathInfo() + Commons.SEPARATOR_PATH + consumer.getId());
                Taggable storedConsumer = OrpheusDbJPAEntityManagerUtils.find(consumer);
                return jsonUtils.toJSON(storedConsumer);
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/architectures/:uuid/consumers/:cuuid", (request, response) -> {
            Map<String, Object> params = new HashMap<>();
            String id = request.params(":uuid");
            String cid = request.params(":cuuid");
            params.put("id", id);
            params.put("cid", cid);
            response.type(JSON_CONTENT_TYPE);
            return getList(request, response, ArchitecturalModel.GET_CONSUMER_FOR_MODEL, params, null);
        });
        put("/architectures/:uuid/consumers/:cuuid", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                Consumer consumer = jsonUtils.fromJSON(request.body(), Consumer.class);
                if (consumer == null) {
                    throw new Exception("Invalid kind of model");
                }
                String id = request.params(":cuuid");
                consumer.setId(id);
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);

                Item storedConsumer = OrpheusDbJPAEntityManagerUtils.find(consumer);
                if (storedConsumer == null) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("name", consumer.getName());
                    params.put("kind", consumer.getKind());
                    storedConsumer = OrpheusDbJPAEntityManagerUtils.findByQuery(Item.class, Item.GET_ITEMS_BY_NAME_AND_KIND, params);
                }
                if (storedConsumer == null) {
                    map.put("Parent", consumer);
                    consumer = consumer.validateCreation(map);
                    DBUtil.persist(consumer);
                    response.status(HTTP_CREATED);
                    return EMPTY_RESPONSE;
                } else {
                    Consumer storedConsumer_ = (Consumer) storedConsumer;
                    map.put("Parent", storedConsumer_);
                    consumer = consumer.validateReplacement(map);
                    storedConsumer_.override(consumer);
                    DBUtil.update(storedConsumer_);
                    response.status(HTTP_OK);
                    return EMPTY_RESPONSE;
                }
            } catch (EntityNotFoundException ex) {
                response.status(HTTP_NOT_FOUND);
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        patch("/architectures/:uuid/consumers/:cuuid", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                Consumer consumer = jsonUtils.fromJSON(request.body(), Consumer.class);
                if (consumer == null) {
                    throw new Exception("Invalid kind of consumer");
                }
                String id = request.params(":cuuid");
                consumer.setId(id);
                final SparkWebContext ctx = new SparkWebContext(request, response);

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
        get("/architectures/:uuid/layers", (request, response) -> {
            Map<String, Object> params = new HashMap<>();
            params.put("id", request.params(":uuid"));
            return getList(request, response, ArchitecturalModel.GET_ALL_LAYERS_FROM_MODEL, params, Collection.class);
        });
        post("/architectures/:uuid/layers", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                String body = request.body();
                Map<String, Object> incomingModel = (Map<String, Object>) jsonUtils.fromJSON(body, Map.class);
                Object kind = incomingModel.get("kind");
                Layer layer;
                try {
                    layer = extractTaggable(body, kind);
                } catch (Throwable t) {
                    throw new Exception("Invalid kind of layer");
                }
                String modelId = request.params(":uuid");
                ArchitecturalModel model = OrpheusDbJPAEntityManagerUtils.find(ArchitecturalModel.class, modelId);
                if (model == null) {
                    throw new EntityNotFoundException("There is no model with identifer of '" + modelId + "' to which associate the incoming layer");
                } else {
                    map.put("OriginType", model.getKind());
                    map.put("DestinationType", layer.getKind());
                    layer = layer.validateAsociation(map);
                }

                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);

                Map<String, Object> params = new HashMap<>();
                params.put("name", layer.getName());
                params.put("kind", layer.getKind());
                Item storedLayer = OrpheusDbJPAEntityManagerUtils.findByQuery(Item.class, Item.GET_ITEMS_BY_NAME_AND_KIND, params);

                Set<Layer> modelLayers = model.getLayers();
                if (storedLayer == null) {
                    map.put("Parent", model);
                    layer = layer.validateCreation(map);
                    DBUtil.persist(layer);
                    modelLayers.add(layer);
                    OrpheusDbJPAEntityManagerUtils.merge(model);
                } else {
                    if (!Layer.class.isAssignableFrom(storedLayer.getClass())) {
                        throw new EntityError("Provided body does not corresponds with a layer");
                    } else {
                        storedLayer.override(layer);
                        layer = layer.validateReplacement(map);
                        DBUtil.persist(layer);
                    }
                }
                response.status(HTTP_CREATED);
                response.type(JSON_CONTENT_TYPE);
                response.header("Location", request.pathInfo() + Commons.SEPARATOR_PATH + layer.getId());
                Taggable storedModel = OrpheusDbJPAEntityManagerUtils.find(model);
                return jsonUtils.toJSON(storedModel);
            } catch (EntityNotFoundException enfe) {
                response.status(HTTP_NOT_FOUND);
                response.type(JSON_CONTENT_TYPE);
                return jsonUtils.toJSON(MessagesWrapper.fromExceptionToMessages(enfe, HTTP_NOT_FOUND));
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/architectures/layers", (request, response) -> getList(request, response, Layer.GET_ALL_LAYERS, null, null));
        post("/architectures/layers", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                String body = request.body();
                Map<String, Object> incomingModel = (Map<String, Object>) jsonUtils.fromJSON(body, Map.class);
                Object kind = incomingModel.get("kind");
                Layer layer;
                try {
                    layer = extractTaggable(body, kind);
                } catch (Throwable t) {
                    throw new Exception("Invalid kind of layer");
                }
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                layer = layer.validateCreation(map);
                DBUtil.persist(layer);
                response.status(HTTP_CREATED);
                response.type(JSON_CONTENT_TYPE);
                response.header("Location", request.pathInfo() + Commons.SEPARATOR_PATH + layer.getId());
                Taggable storedLayer = OrpheusDbJPAEntityManagerUtils.find(layer);
                return jsonUtils.toJSON(storedLayer);
            } catch (Throwable ex) {
                if (EntityInsertionError.class.isAssignableFrom(ex.getClass())) {
                    return throwError(response, HTTP_CONFLICT, ex);
                }
                return throwError(response, ex);
            }
        });
        get("/architectures/layers/:luuid", (request, response) -> {
            try {
                String id = request.params(":luuid");
                Layer layer = OrpheusDbJPAEntityManagerUtils.find(Layer.class, id);
                if (layer != null) {
                    layer = layer.validateRequest();
                }
                response.status(HTTP_OK);
                response.type(JSON_CONTENT_TYPE);
                return jsonUtils.toJSON(layer);
            } catch (Exception ex) {
                return throwError(response, ex);
            }
        });
        patch("/architectures/layers/:luuid", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                Layer layer = jsonUtils.fromJSON(request.body(), Layer.class);
                if (layer == null) {
                    throw new Exception("Invalid kind of layer");
                }
                String id = request.params(":luuid");
                layer.setId(id);
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                layer = layer.validateModification(map);
                DBUtil.update(layer);
                response.status(HTTP_OK);
                return EMPTY_RESPONSE;
            } catch (EntityNotFoundException ex) {
                response.status(HTTP_NOT_FOUND);
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        put("/architectures/layers/:luuid", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                Layer layer = jsonUtils.fromJSON(request.body(), Layer.class);
                if (layer == null) {
                    throw new Exception("Invalid kind of layer");
                }
                String id = request.params(":luuid");
                layer.setId(id);
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);

                Item storedLayer = OrpheusDbJPAEntityManagerUtils.find(layer);
                if (storedLayer == null) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("name", layer.getName());
                    params.put("kind", layer.getKind());
                    storedLayer = OrpheusDbJPAEntityManagerUtils.findByQuery(Item.class, Item.GET_ITEMS_BY_NAME_AND_KIND, params);
                }
                if (storedLayer == null) {
                    map.put("Parent", layer);
                    layer = layer.validateCreation(map);
                    DBUtil.persist(layer);
                    response.status(HTTP_CREATED);
                    return EMPTY_RESPONSE;
                } else {
                    Layer storedLayer_ = (Layer) storedLayer;
                    map.put("Parent", storedLayer_);
                    layer = layer.validateReplacement(map);
                    storedLayer_.override(layer);
                    DBUtil.update(storedLayer_);
                    response.status(HTTP_OK);
                    return EMPTY_RESPONSE;
                }
                
                
            } catch (EntityNotFoundException ex) {
                response.status(HTTP_NOT_FOUND);
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/architectures/layers/:luuid/systems", (request, response) -> {
            Map<String, Object> params = new HashMap<>();
            params.put("id", request.params(":luuid"));
            return getList(request, response, Layer.GET_ALL_SYSTEMS_FROM_LAYER, params, Collection.class);
        });
        post("/architectures/layers/:luuid/systems", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                System system = jsonUtils.fromJSON(request.body(), System.class);
                if (system == null) {
                    throw new Exception("Invalid kind of system");
                }
                String id = request.params(":luuid");
                String systemId = system.getId();
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                system = system.validateCreation(map);
                Layer layer = OrpheusDbJPAEntityManagerUtils.find(Layer.class, id);
                DBUtil.persist(system/*, systemId == null*/);
                layer.getSystems().add(system);
                DBUtil.update(layer);
                response.status(HTTP_CREATED);
                response.type(JSON_CONTENT_TYPE);
                response.header("Location", request.pathInfo() + Commons.SEPARATOR_PATH + system.getId());
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        delete("/architectures/layers/:luuid/systems/:suuid", (request, response) -> {
            try {
                String luuid = request.params(":luuid");
                String suuid = request.params(":suuid");
                Layer layer = OrpheusDbJPAEntityManagerUtils.find(Layer.class, luuid);
                if (layer == null) {
                    throw new EntityNotFoundException("Layer with id of '" + luuid + "' does nos exists");
                }
                Set<System> systems = layer.getSystems();
                System system = null;
                for (System system_ : systems) {
                    if (system_.getId().equals(suuid)) {
                        system = system_;
                    }
                }
                if (system == null) {
                    throw new EntityNotFoundException("System with id of '" + suuid + "' is not found on Layer[" + luuid + "]");
                }
                systems.remove(system);
                DBUtil.update(layer);
                response.status(HTTP_OK);
                return EMPTY_RESPONSE;
            } catch (EntityNotFoundException ex) {
                int status = HTTP_NOT_FOUND;
                response.status(status);
                response.type(JSON_CONTENT_TYPE);
                return jsonUtils.toJSON(MessagesWrapper.fromExceptionToMessages(ex, status));
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/architectures/layers/:suuid/containers", (request, response) -> {
            Map<String, Object> params = new HashMap<>();
            params.put("id", request.params(":suuid"));
            return getList(request, response, Layer.GET_ALL_CONTAINERS_FROM_LAYER, params, Collection.class);
        });
        post("/architectures/layers/:suuid/containers", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                Container container = jsonUtils.fromJSON(request.body(), Container.class);
                if (container == null) {
                    throw new Exception("Invalid kind of container");
                }
                String id = request.params(":suuid");
                String containerId = container.getId();
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                container = container.validateCreation(map);
                Layer layer = OrpheusDbJPAEntityManagerUtils.find(Layer.class, id);
                DBUtil.persist(container/*, containerId == null*/);
                layer.getContainers().add(container);
                DBUtil.update(layer);
                response.status(HTTP_CREATED);
                response.type(JSON_CONTENT_TYPE);
                response.header("Location", request.pathInfo() + Commons.SEPARATOR_PATH + container.getId());
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        delete("/architectures/layers/:luuid/containers/:cuuid", (request, response) -> {
            try {
                String luuid = request.params(":luuid");
                String cuuid = request.params(":cuuid");
                Layer layer = OrpheusDbJPAEntityManagerUtils.find(Layer.class, luuid);
                if (layer == null) {
                    throw new EntityNotFoundException("Layer with id of '" + luuid + "' does nos exists");
                }
                Set<Container> containers = layer.getContainers();
                Container container = null;
                for (Container container_ : containers) {
                    if (container_.getId().equals(cuuid)) {
                        container = container_;
                        break;
                    }
                }
                if (container == null) {
                    throw new EntityNotFoundException("Container with id of '" + cuuid + "' is not found on Layer[" + luuid + "]");
                }
                containers.remove(container);
                OrpheusDbJPAEntityManagerUtils.merge(layer);
                response.status(HTTP_OK);
                return EMPTY_RESPONSE;
            } catch (EntityNotFoundException ex) {
                int status = HTTP_NOT_FOUND;
                response.status(status);
                response.type(JSON_CONTENT_TYPE);
                return jsonUtils.toJSON(MessagesWrapper.fromExceptionToMessages(ex, status));
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/architectures/layers/:suuid/components", (request, response) -> {
            Map<String, Object> params = new HashMap<>();
            params.put("id", request.params(":suuid"));
            return getList(request, response, Layer.GET_ALL_COMPONENTS_FROM_LAYER, params, Collection.class);
        });
        post("/architectures/layers/:suuid/components", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                Component component = jsonUtils.fromJSON(request.body(), Component.class);
                if (component == null) {
                    throw new Exception("Invalid kind of component");
                }
                String id = request.params(":suuid");
                String componentId = component.getId();
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                component = component.validateCreation(map);
                Layer layer = OrpheusDbJPAEntityManagerUtils.find(Layer.class, id);
                DBUtil.persist(component/*, componentId == null*/);
                layer.getComponents().add(component);
                DBUtil.update(layer);
                response.status(HTTP_CREATED);
                response.type(JSON_CONTENT_TYPE);
                response.header("Location", request.pathInfo() + Commons.SEPARATOR_PATH + component.getId());
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        delete("/architectures/layers/:luuid/components/:suuid", (request, response) -> {
            try {
                String luuid = request.params(":luuid");
                String suuid = request.params(":suuid");
                Layer layer = OrpheusDbJPAEntityManagerUtils.find(Layer.class, luuid);
                if (layer == null) {
                    throw new EntityNotFoundException("Layer with id of '" + luuid + "' does nos exists");
                }
                Set<Component> components = layer.getComponents();
                Component component = null;
                for (Component component_ : components) {
                    if (component_.getId().equals(suuid)) {
                        component = component_;
                    }
                }
                if (component == null) {
                    throw new EntityNotFoundException("Component with id of '" + suuid + "' is not found on Layer[" + luuid + "]");
                }
                components.remove(component);
                DBUtil.update(layer);
                response.status(HTTP_OK);
                return EMPTY_RESPONSE;
            } catch (EntityNotFoundException ex) {
                int status = HTTP_NOT_FOUND;
                response.status(status);
                response.type(JSON_CONTENT_TYPE);
                return jsonUtils.toJSON(MessagesWrapper.fromExceptionToMessages(ex, status));
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/architectures/:uuid/systems", (request, response) -> {
            Map<String, Object> params = new HashMap<>();
            params.put("id", request.params(":uuid"));
            return getList(request, response, ArchitecturalModel.GET_ALL_SYSTEMS_FROM_MODEL, params, Collection.class);
        });
        get("/architectures/systems", (request, response) -> getList(request, response, System.GET_ALL_SYSTEMS, null, null));
        post("/architectures/systems", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                System system = jsonUtils.fromJSON(request.body(), System.class);
                if (system == null) {
                    throw new Exception("Invalid kind of system");
                }
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                system = system.validateCreation(map);
                DBUtil.persist(system);
                response.status(HTTP_CREATED);
                response.type(JSON_CONTENT_TYPE);
                response.header("Location", request.pathInfo() + Commons.SEPARATOR_PATH + system.getId());
                Taggable storedModel = OrpheusDbJPAEntityManagerUtils.find(system);
                return jsonUtils.toJSON(storedModel);
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/architectures/systems/:suuid", (request, response) -> {
            try {
                String id = request.params(":suuid");
                System system = OrpheusDbJPAEntityManagerUtils.find(System.class, id);
                if (system != null) {
                    system = system.validateRequest();
                }
                response.status(HTTP_OK);
                response.type(JSON_CONTENT_TYPE);
                return jsonUtils.toJSON(system);
            } catch (Exception ex) {
                return throwError(response, ex);
            }
        });
        patch("/architectures/systems/:suuid", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                System system = jsonUtils.fromJSON(request.body(), System.class);
                if (system == null) {
                    throw new Exception("Invalid kind of system");
                }
                String id = request.params(":suuid");
                system.setId(id);
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                system = system.validateModification(map);
                DBUtil.update(system);
                response.status(HTTP_OK);
                return EMPTY_RESPONSE;
            } catch (EntityNotFoundException ex) {
                response.status(HTTP_NOT_FOUND);
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        put("/architectures/systems/:suuid", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                System system = jsonUtils.fromJSON(request.body(), System.class);
                if (system == null) {
                    throw new Exception("Invalid kind of system");
                }
                String id = request.params(":suuid");
                system.setId(id);
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);

                Item storedSystem = OrpheusDbJPAEntityManagerUtils.find(system);
                if (storedSystem == null) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("name", system.getName());
                    params.put("kind", system.getKind());
                    storedSystem = OrpheusDbJPAEntityManagerUtils.findByQuery(Item.class, Item.GET_ITEMS_BY_NAME_AND_KIND, params);
                }
                if (storedSystem == null) {
                    map.put("Parent", system);
                    system = system.validateCreation(map);
                    DBUtil.persist(system);
                    response.status(HTTP_CREATED);
                    return EMPTY_RESPONSE;
                } else {
                    System storedSystem_ = (System) storedSystem;
                    map.put("Parent", storedSystem_);
                    system = system.validateReplacement(map);
                    storedSystem_.override(system);
                    DBUtil.update(storedSystem_);
                    response.status(HTTP_OK);
                    return EMPTY_RESPONSE;
                }
            } catch (EntityNotFoundException ex) {
                response.status(HTTP_NOT_FOUND);
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/architectures/systems/:suuid/systems", (request, response) -> {
            Map<String, Object> params = new HashMap<>();
            params.put("id", request.params(":suuid"));
            return getList(request, response, System.GET_ALL_SYSTEMS_FROM_SYSTEM, params, Collection.class);
        });
        post("/architectures/systems/:suuid/systems", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                System system = jsonUtils.fromJSON(request.body(), System.class);
                if (system == null) {
                    throw new Exception("Invalid kind of system");
                }
                String id = request.params(":suuid");
                String systemId = system.getId();
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                system = system.validateCreation(map);
                System system_ = OrpheusDbJPAEntityManagerUtils.find(System.class, id);
                DBUtil.persist(system/*, systemId == null*/);
                system_.getSystems().add(system);
                DBUtil.update(system);
                response.status(HTTP_CREATED);
                response.type(JSON_CONTENT_TYPE);
                response.header("Location", request.pathInfo() + Commons.SEPARATOR_PATH + system.getId());
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/architectures/systems/:suuid/containers", (request, response) -> {
            Map<String, Object> params = new HashMap<>();
            params.put("id", request.params(":suuid"));
            return getList(request, response, System.GET_ALL_CONTAINERS_FROM_SYSTEM, params, Collection.class);
        });
        post("/architectures/systems/:suuid/containers", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                Container container = jsonUtils.fromJSON(request.body(), Container.class);
                if (container == null) {
                    throw new Exception("Invalid kind of container");
                }
                String id = request.params(":suuid");
                String containerId = container.getId();
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                container = container.validateCreation(map);
                System system = OrpheusDbJPAEntityManagerUtils.find(System.class, id);
                DBUtil.persist(container/*, containerId == null*/);
                system.getContainers().add(container);
                DBUtil.update(system);
                response.status(HTTP_CREATED);
                response.type(JSON_CONTENT_TYPE);
                response.header("Location", request.pathInfo() + Commons.SEPARATOR_PATH + container.getId());
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/architectures/systems/:suuid/components", (request, response) -> {
            Map<String, Object> params = new HashMap<>();
            params.put("id", request.params(":suuid"));
            return getList(request, response, System.GET_ALL_COMPONENTS_FROM_SYSTEM, params, Collection.class);
        });
        post("/architectures/systems/:suuid/components", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                Component component = jsonUtils.fromJSON(request.body(), Component.class);
                if (component == null) {
                    throw new Exception("Invalid kind of component");
                }
                String id = request.params(":suuid");
                String componentId = component.getId();
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                component = component.validateCreation(map);
                System system = OrpheusDbJPAEntityManagerUtils.find(System.class, id);
                DBUtil.persist(component/*, componentId == null*/);
                system.getComponents().add(component);
                DBUtil.update(system);
                response.status(HTTP_CREATED);
                response.type(JSON_CONTENT_TYPE);
                response.header("Location", request.pathInfo() + Commons.SEPARATOR_PATH + component.getId());
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/architectures/containers", (request, response) -> getList(request, response, Container.GET_ALL_CONTAINERS, null, null));
        post("/architectures/containers", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                Container container = jsonUtils.fromJSON(request.body(), Container.class);
                if (container == null) {
                    throw new Exception("Invalid kind of container");
                }
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                container = container.validateCreation(map);
                DBUtil.persist(container);
                response.status(HTTP_CREATED);
                response.type(JSON_CONTENT_TYPE);
                response.header("Location", request.pathInfo() + Commons.SEPARATOR_PATH + container.getId());
                Taggable storedModel = OrpheusDbJPAEntityManagerUtils.find(container);
                return jsonUtils.toJSON(storedModel);
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/architectures/containers/:cuuid", (request, response) -> {
            try {
                String id = request.params(":cuuid");
                Container container = OrpheusDbJPAEntityManagerUtils.find(Container.class, id);
                if (container != null) {
                    container = container.validateRequest();
                }
                response.status(HTTP_OK);
                response.type(JSON_CONTENT_TYPE);
                return jsonUtils.toJSON(container);
            } catch (Exception ex) {
                return throwError(response, ex);
            }
        });
        patch("/architectures/containers/:cuuid", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                Container container = jsonUtils.fromJSON(request.body(), Container.class);
                if (container == null) {
                    throw new Exception("Invalid kind of container");
                }
                String id = request.params(":cuuid");
                container.setId(id);
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                container = container.validateModification(map);
                DBUtil.update(container);
                response.status(HTTP_OK);
                return EMPTY_RESPONSE;
            } catch (EntityNotFoundException ex) {
                response.status(HTTP_NOT_FOUND);
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        put("/architectures/containers/:cuuid", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                Container container = jsonUtils.fromJSON(request.body(), Container.class);
                if (container == null) {
                    throw new Exception("Invalid kind of container");
                }
                String id = request.params(":cuuid");
                container.setId(id);
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);

                Item storedContainer = OrpheusDbJPAEntityManagerUtils.find(container);
                if (storedContainer == null) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("name", container.getName());
                    params.put("kind", container.getKind());
                    storedContainer = OrpheusDbJPAEntityManagerUtils.findByQuery(Item.class, Item.GET_ITEMS_BY_NAME_AND_KIND, params);
                }
                if (storedContainer == null) {
                    map.put("Parent", container);
                    container = container.validateCreation(map);
                    DBUtil.persist(container);
                    response.status(HTTP_CREATED);
                    return EMPTY_RESPONSE;
                } else {
                    Container storedContainer_ = (Container) storedContainer;
                    map.put("Parent", storedContainer_);
                    container = container.validateReplacement(map);
                    storedContainer_.override(container);
                    DBUtil.update(storedContainer_);
                    response.status(HTTP_OK);
                    return EMPTY_RESPONSE;
                }
            } catch (EntityNotFoundException ex) {
                response.status(HTTP_NOT_FOUND);
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/architectures/containers/:cuuid/components", (request, response) -> {
            Map<String, Object> params = new HashMap<>();
            params.put("id", request.params(":cuuid"));
            return getList(request, response, Container.GET_ALL_COMPONENTS_FROM_CONTAINER, params, Collection.class);
        });
        post("/architectures/containers/:cuuid/components", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                Component component = jsonUtils.fromJSON(request.body(), Component.class);
                if (component == null) {
                    throw new Exception("Invalid kind of component");
                }
                String id = request.params(":cuuid");

                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                component = component.validateCreation(map);
                Container container = OrpheusDbJPAEntityManagerUtils.find(Container.class, id);
                DBUtil.persist(component);
                container.getComponents().add(component);
                DBUtil.update(container);
                response.status(HTTP_CREATED);
                response.type(JSON_CONTENT_TYPE);
                response.header("Location", request.pathInfo() + Commons.SEPARATOR_PATH + component.getId());
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/architectures/components", (request, response) -> getList(request, response, Component.GET_ALL_COMPONENTS, null, null));
        post("/architectures/components", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                Component component = jsonUtils.fromJSON(request.body(), Component.class);
                if (component == null) {
                    throw new Exception("Invalid kind of relationship");
                }
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                component = component.validateCreation(map);
                DBUtil.persist(component);
                response.status(HTTP_CREATED);
                response.type(JSON_CONTENT_TYPE);
                response.header("Location", request.pathInfo() + Commons.SEPARATOR_PATH + component.getId());
                Taggable storedModel = OrpheusDbJPAEntityManagerUtils.find(component);
                return jsonUtils.toJSON(storedModel);
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/architectures/components/:cuuid", (request, response) -> {
            try {
                String id = request.params(":cuuid");
                Component component = OrpheusDbJPAEntityManagerUtils.find(Component.class, id);
                if (component != null) {
                    component = component.validateRequest();
                }
                response.status(HTTP_OK);
                response.type(JSON_CONTENT_TYPE);
                return jsonUtils.toJSON(component);
            } catch (Exception ex) {
                return throwError(response, ex);
            }
        });
        patch("/architectures/components/:cuuid", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                Component component = jsonUtils.fromJSON(request.body(), Component.class);
                if (component == null) {
                    throw new Exception("Invalid kind of component");
                }
                String id = request.params(":cuuid");
                component.setId(id);
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                component = component.validateModification(map);
                DBUtil.update(component);
                response.status(HTTP_OK);
                return EMPTY_RESPONSE;
            } catch (EntityNotFoundException ex) {
                response.status(HTTP_NOT_FOUND);
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        put("/architectures/components/:cuuid", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                Component component = jsonUtils.fromJSON(request.body(), Component.class);
                if (component == null) {
                    throw new Exception("Invalid kind of container");
                }
                String id = request.params(":cuuid");
                component.setId(id);
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);

                Item storedComponent = OrpheusDbJPAEntityManagerUtils.find(component);
                if (storedComponent == null) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("name", component.getName());
                    params.put("kind", component.getKind());
                    storedComponent = OrpheusDbJPAEntityManagerUtils.findByQuery(Item.class, Item.GET_ITEMS_BY_NAME_AND_KIND, params);
                }
                if (storedComponent == null) {
                    map.put("Parent", component);
                    component = component.validateCreation(map);
                    DBUtil.persist(component);
                    response.status(HTTP_CREATED);
                    return EMPTY_RESPONSE;
                } else {
                    Component storedComponent_ = (Component) storedComponent;
                    map.put("Parent", storedComponent_);
                    component = component.validateReplacement(map);
                    storedComponent_.override(component);
                    DBUtil.update(storedComponent_);
                    response.status(HTTP_OK);
                    return EMPTY_RESPONSE;
                }
            } catch (EntityNotFoundException ex) {
                response.status(HTTP_NOT_FOUND);
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        post("/architectures/:uuid/systems", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                System system = jsonUtils.fromJSON(request.body(), System.class);
                if (system == null) {
                    throw new Exception("Invalid kind of container");
                }
                String id = request.params(":uuid");
                String systemId = system.getId();
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                system = system.validateCreation(map);
                ArchitecturalModel model = OrpheusDbJPAEntityManagerUtils.find(ArchitecturalModel.class, id);
                DBUtil.persist(system/*, systemId == null*/);
                model.getSystems().add(system);
                DBUtil.update(model);
                response.status(HTTP_CREATED);
                response.type(JSON_CONTENT_TYPE);
                response.header("Location", request.pathInfo() + Commons.SEPARATOR_PATH + system.getId());
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/architectures/:uuid/systems/:suuid", (request, response) -> {
            Map<String, Object> params = new HashMap<>();
            String id = request.params(":uuid");
            String sid = request.params(":suuid");
            params.put("id", id);
            params.put("sid", sid);
            response.type(JSON_CONTENT_TYPE);
            return getList(request, response, ArchitecturalModel.GET_SYSTEM, params, null);
        });
        put("/architectures/:uuid/systems/:suuid", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                System system = jsonUtils.fromJSON(request.body(), System.class);
                if (system == null) {
                    throw new Exception("Invalid kind of container");
                }
                String id = request.params(":cuuid");
                system.setId(id);
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);

                Item storedSystem = OrpheusDbJPAEntityManagerUtils.find(system);
                if (storedSystem == null) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("name", system.getName());
                    params.put("kind", system.getKind());
                    storedSystem = OrpheusDbJPAEntityManagerUtils.findByQuery(Item.class, Item.GET_ITEMS_BY_NAME_AND_KIND, params);
                }
                if (storedSystem == null) {
                    map.put("Parent", system);
                    system = system.validateCreation(map);
                    DBUtil.persist(system);
                    response.status(HTTP_CREATED);
                    return EMPTY_RESPONSE;
                } else {
                    System storedSystem_ = (System) storedSystem;
                    map.put("Parent", storedSystem_);
                    system = system.validateReplacement(map);
                    storedSystem_.override(system);
                    DBUtil.update(storedSystem_);
                    response.status(HTTP_OK);
                    return EMPTY_RESPONSE;
                }
            } catch (EntityNotFoundException ex) {
                response.status(HTTP_NOT_FOUND);
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        patch("/architectures/:uuid/systems/:suuid", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                Container container = jsonUtils.fromJSON(request.body(), Container.class);
                if (container == null) {
                    throw new Exception("Invalid kind of container");
                }
                String id = request.params(":cuuid");
                container.setId(id);
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                container = container.validateModification(map);
                DBUtil.update(container);
                response.status(HTTP_OK);
                return EMPTY_RESPONSE;
            } catch (EntityNotFoundException ex) {
                response.status(HTTP_NOT_FOUND);
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/architectures/:uuid/systems/:suuid/containers", (request, response) -> {
            Map<String, Object> params = new HashMap<>();
            params.put("id", request.params(":suuid"));
            return getList(request, response, System.GET_ALL_CONTAINERS_FROM_SYSTEM, params, Collection.class);
        });
        post("/architectures/:uuid/systems/:suuid/containers", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                Container container = jsonUtils.fromJSON(request.body(), Container.class);
                if (container == null) {
                    throw new Exception("Invalid kind of container");
                }
                String id = request.params(":suuid");
                String containerId = container.getId();
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                container = container.validateCreation(map);
                System system = OrpheusDbJPAEntityManagerUtils.find(System.class, id);
                DBUtil.persist(container/*, containerId == null*/);
                system.getContainers().add(container);
                DBUtil.update(system);
                response.status(HTTP_CREATED);
                response.type(JSON_CONTENT_TYPE);
                response.header("Location", request.pathInfo() + Commons.SEPARATOR_PATH + container.getId());
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/architectures/:uuid/systems/:suuid/containers/:cuuid", (request, response) -> {
            Map<String, Object> params = new HashMap<>();
            String id = request.params(":suuid");
            String cid = request.params(":cuuid");
            params.put("id", id);
            params.put("cid", cid);
            response.type(JSON_CONTENT_TYPE);
            return getList(request, response, System.GET_CONTAINER, params, null);
        });
        put("/architectures/:uuid/systems/:suuid/containers/:cuuid", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                Container container = jsonUtils.fromJSON(request.body(), Container.class);
                if (container == null) {
                    throw new Exception("Invalid kind of container");
                }
                String id = request.params(":cuuid");
                container.setId(id);
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);

                Item storedContainer = OrpheusDbJPAEntityManagerUtils.find(container);
                if (storedContainer == null) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("name", container.getName());
                    params.put("kind", container.getKind());
                    storedContainer = OrpheusDbJPAEntityManagerUtils.findByQuery(Item.class, Item.GET_ITEMS_BY_NAME_AND_KIND, params);
                }
                if (storedContainer == null) {
                    map.put("Parent", container);
                    container = container.validateCreation(map);
                    DBUtil.persist(container);
                    response.status(HTTP_CREATED);
                    return EMPTY_RESPONSE;
                } else {
                    Container storedContainer_ = (Container) storedContainer;
                    map.put("Parent", storedContainer_);
                    container = container.validateReplacement(map);
                    storedContainer_.override(container);
                    DBUtil.update(storedContainer_);
                    response.status(HTTP_OK);
                    return EMPTY_RESPONSE;
                }
            } catch (EntityNotFoundException ex) {
                response.status(HTTP_NOT_FOUND);
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        patch("/architectures/:uuid/systems/:suuid/containers/:cuuid", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                Container container = jsonUtils.fromJSON(request.body(), Container.class);
                if (container == null) {
                    throw new Exception("Invalid kind of container");
                }
                String id = request.params(":cuuid");
                container.setId(id);
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                container = container.validateModification(map);
                DBUtil.update(container);
                response.status(HTTP_OK);
                return EMPTY_RESPONSE;
            } catch (EntityNotFoundException ex) {
                response.status(HTTP_NOT_FOUND);
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/bpms", (request, response) -> {
            Map<String, Object> params = new HashMap<>();
            params.put("modelType", BpmModel.class);
            response.type(JSON_CONTENT_TYPE);
            return getList(request, response, Item.GET_MODELS_BY_TYPE, params, null);
        });
        post("/bpms", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                BpmModel model = jsonUtils.fromJSON(request.body(), BpmModel.class);
                if (model == null) {
                    throw new Exception("Invalid model");
                }
                if (model.getKind() != ElementKind.BPM_MODEL) {
                    throw new Exception("Invalid kind of model '" + model.getKind() + "'. It should be '" + ElementKind.BPM_MODEL + "'");
                }
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                model = model.validateCreation(map);
                DBUtil.persist(model);
                response.status(HTTP_CREATED);
                response.type(JSON_CONTENT_TYPE);
                response.header("Location", request.pathInfo() + Commons.SEPARATOR_PATH + model.getId());
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/ers", (request, response) -> {
            Map<String, Object> params = new HashMap<>();
            params.put("modelType", ErModel.class);
            response.type(JSON_CONTENT_TYPE);
            return getList(request, response, Item.GET_MODELS_BY_TYPE, params, null);
        });
        post("/ers", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                ErModel model = jsonUtils.fromJSON(request.body(), ErModel.class);
                if (model == null) {
                    throw new Exception("Invalid model");
                }
                if (model.getKind() != ElementKind.ENTITY_RELATIONSHIP_MODEL) {
                    throw new Exception("Invalid kind of model '" + model.getKind() + "'. It should be '" + ElementKind.ENTITY_RELATIONSHIP_MODEL + "'");
                }
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                model = model.validateCreation(map);
                DBUtil.persist(model);
                response.status(HTTP_CREATED);
                response.type(JSON_CONTENT_TYPE);
                response.header("Location", request.pathInfo() + Commons.SEPARATOR_PATH + model.getId());
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/flowcharts", (request, response) -> {
            Map<String, Object> params = new HashMap<>();
            params.put("modelType", FlowchartModel.class);
            response.type(JSON_CONTENT_TYPE);
            return getList(request, response, Item.GET_MODELS_BY_TYPE, params, null);
        });
        post("/flowcharts", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                FlowchartModel model = jsonUtils.fromJSON(request.body(), FlowchartModel.class);
                if (model == null) {
                    throw new Exception("Invalid model");
                }
                if (model.getKind() != ElementKind.FLOWCHART_MODEL) {
                    throw new Exception("Invalid kind of model '" + model.getKind() + "'. It should be '" + ElementKind.FLOWCHART_MODEL + "'");
                }
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                model = model.validateCreation(map);
                DBUtil.persist(model);
                response.status(HTTP_CREATED);
                response.type(JSON_CONTENT_TYPE);
                response.header("Location", request.pathInfo() + Commons.SEPARATOR_PATH + model.getId());
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/gantts", (request, response) -> {
            Map<String, Object> params = new HashMap<>();
            params.put("modelType", GanttModel.class);
            response.type(JSON_CONTENT_TYPE);
            return getList(request, response, Item.GET_MODELS_BY_TYPE, params, null);
        });
        post("/gantts", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                GanttModel model = jsonUtils.fromJSON(request.body(), GanttModel.class);
                if (model == null) {
                    throw new Exception("Invalid model");
                }
                if (model.getKind() != ElementKind.GANTT_MODEL) {
                    throw new Exception("Invalid kind of model '" + model.getKind() + "'. It should be '" + ElementKind.GANTT_MODEL + "'");
                }
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                model = model.validateCreation(map);
                DBUtil.persist(model);
                response.status(HTTP_CREATED);
                response.type(JSON_CONTENT_TYPE);
                response.header("Location", request.pathInfo() + Commons.SEPARATOR_PATH + model.getId());
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/sequences", (request, response) -> {
            Map<String, Object> params = new HashMap<>();
            params.put("modelType", SequenceModel.class);
            response.type(JSON_CONTENT_TYPE);
            return getList(request, response, Item.GET_MODELS_BY_TYPE, params, null);
        });
        post("/sequences", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                SequenceModel model = jsonUtils.fromJSON(request.body(), SequenceModel.class);
                if (model == null) {
                    throw new Exception("Invalid model");
                }
                if (model.getKind() != ElementKind.SEQUENCE_MODEL) {
                    throw new Exception("Invalid kind of model '" + model.getKind() + "'. It should be '" + ElementKind.SEQUENCE_MODEL + "'");
                }
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                model = model.validateCreation(map);
                DBUtil.persist(model);
                response.status(HTTP_CREATED);
                response.type(JSON_CONTENT_TYPE);
                response.header("Location", request.pathInfo() + Commons.SEPARATOR_PATH + model.getId());
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/classes", (request, response) -> {
            Map<String, Object> params = new HashMap<>();
            params.put("modelType", ClassesModel.class);
            response.type(JSON_CONTENT_TYPE);
            return getList(request, response, Item.GET_MODELS_BY_TYPE, params, null);
        });
        post("/classes", (request, response) -> {
            try {
                Map<Object, Object> map = new HashMap<>();
                ClassesModel model = jsonUtils.fromJSON(request.body(), ClassesModel.class);
                if (model == null) {
                    throw new Exception("Invalid model");
                }
                if (model.getKind() != ElementKind.UML_CLASS_MODEL) {
                    throw new Exception("Invalid kind of model '" + model.getKind() + "'. It should be '" + ElementKind.UML_CLASS_MODEL + "'");
                }
                final SparkWebContext ctx = new SparkWebContext(request, response);

                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                model = model.validateCreation(map);
                DBUtil.persist(model);
                response.status(HTTP_CREATED);
                response.type(JSON_CONTENT_TYPE);
                response.header("Location", request.pathInfo() + Commons.SEPARATOR_PATH + model.getId());
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
    }

}
