package com.araguacaima.open_archi.web.routes;

import com.araguacaima.commons.utils.ReflectionUtils;
import com.araguacaima.open_archi.persistence.commons.IdName;
import com.araguacaima.open_archi.persistence.commons.exceptions.EntityDeletionError;
import com.araguacaima.open_archi.persistence.commons.exceptions.EntityError;
import com.araguacaima.open_archi.persistence.commons.exceptions.EntityInsertionError;
import com.araguacaima.open_archi.persistence.diagrams.architectural.ArchitecturalModel;
import com.araguacaima.open_archi.persistence.diagrams.core.*;
import com.araguacaima.open_archi.persistence.diagrams.meta.Account;
import com.araguacaima.open_archi.web.DBUtil;
import com.araguacaima.open_archi.web.MessagesWrapper;
import com.araguacaima.open_archi.web.OpenArchi;
import com.araguacaima.open_archi.web.common.Commons;
import com.araguacaima.open_archi.web.controller.EmailService;
import com.araguacaima.open_archi.web.controller.ModelsController;
import com.araguacaima.open_archi.web.controller.SendEmailSetup;
import com.araguacaima.orpheusdb.utils.OrpheusDbJPAEntityManagerUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang.StringUtils;
import org.pac4j.sparkjava.SparkWebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.RouteGroup;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.Field;
import java.util.*;

import static com.araguacaima.open_archi.persistence.diagrams.core.Item.itemComparator;
import static com.araguacaima.open_archi.web.common.Commons.*;
import static java.net.HttpURLConnection.*;
import static spark.Spark.*;

public class Models implements RouteGroup {


    public static final String PATH = "/models";
    private static Logger log = LoggerFactory.getLogger(Models.class);
    private EmailService emailService = new EmailService();

    @SuppressWarnings("unchecked")
    @Override
    public void addRoutes() {

        post("/validate", (request, response) -> {
            try {
                String body = request.body();
                Map<String, Object> incomingModel = (Map<String, Object>) jsonUtils.fromJSON(body, Map.class);
                Object kind = incomingModel.get("kind");
                Taggable model = extractTaggable(body, kind);

                Map<String, String> queryMap = new HashMap<>();
                Set<String> queryParams = request.queryParams();
                if (CollectionUtils.isNotEmpty(queryParams)) {
                    String rulesPath = request.queryParams("rulesPath");
                    String rulesRepositoryStrategy = request.queryParams("rulesRepositoryStrategy");
                    String urlResourceStrategy = request.queryParams("urlResourceStrategy");
                    if (StringUtils.isNotBlank(rulesPath) && StringUtils.isNotBlank(rulesRepositoryStrategy) && StringUtils.isNotBlank(urlResourceStrategy)) {
                        queryMap.put("rulesPath", rulesPath);
                        queryMap.put("rulesRepositoryStrategy", rulesRepositoryStrategy);
                        queryMap.put("urlResourceStrategy", urlResourceStrategy);
                    }

                }
                Collection<Object> messages_ = validate(model, queryMap);

                Collection<String> recipients = new ArrayList<>();
                Collection<String> cc = new ArrayList<>();
                Collection<String> bcc = new ArrayList<>();

                if (CollectionUtils.isNotEmpty(queryParams)) {

                    String recipientsParam = request.queryParams("recipients");
                    if (StringUtils.isNotBlank(recipientsParam)) {
                        recipients.addAll(Arrays.asList(StringUtils.split(recipientsParam)));
                    }
                }

                final SparkWebContext ctx = new SparkWebContext(request, response);
                Map<Object, Object> map = new HashMap<>();
                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);


                Collection<Object> messages = ModelsController.extractRulesMessages(messages_);

                Collection<Object> sendEmailSetupList = ModelsController.extractSendEmailSetup(messages_);
                if (CollectionUtils.isNotEmpty(sendEmailSetupList)) {
                    sendEmailSetupList.forEach(sendEmailSetup -> {
                        SendEmailSetup sendEmailSetup_ = (SendEmailSetup) sendEmailSetup;
                        recipients.addAll(sendEmailSetup_.getRecipients());
                        cc.addAll(sendEmailSetup_.getCc());
                        bcc.addAll(sendEmailSetup_.getBcc());
                    });
                }

                String from = null;
                if (account != null && StringUtils.isNotBlank(account.getEmail())) {
                    from = account.getEmail();
                } else {
                    SendEmailSetup setup = (SendEmailSetup) IterableUtils.find(sendEmailSetupList, sendEmailSetup -> {
                        SendEmailSetup sendEmailSetup_ = (SendEmailSetup) sendEmailSetup;
                        return StringUtils.isNotEmpty(sendEmailSetup_.getFrom());
                    });
                    if (setup != null) {
                        from = setup.getFrom();
                    }
                }

                if (CollectionUtils.isNotEmpty(messages)) {
                    Collection<Object> notifiableMessages = ModelsController.extractNotifiableMessages(messages);
                    if (CollectionUtils.isEmpty(notifiableMessages)) {
                        notifiableMessages = messages;
                    }
                    if (CollectionUtils.isNotEmpty(notifiableMessages) || CollectionUtils.isNotEmpty(sendEmailSetupList)) {
                        try {
                            EmailService.send(recipients.toArray(new String[]{}), cc.toArray(new String[]{}), bcc.toArray(new String[]{}), from, "OpenArchi Validation Report", notifiableMessages);
                        } catch (Throwable t) {
                            log.error(t.getMessage());
                        }
                    }
                }

                response.status(HTTP_ACCEPTED);
                response.type(JSON_CONTENT_TYPE);
                return jsonUtils.toJSON(messages);
            } catch (Throwable ex) {
                ex.printStackTrace();
                return throwError(response, ex);
            }
        });
        post(Commons.EMPTY_PATH, (request, response) -> {
            try {
                String body = request.body();
                Map<String, Object> incomingModel = (Map<String, Object>) jsonUtils.fromJSON(body, Map.class);
                Object kind = incomingModel.get("kind");
                Taggable model = extractTaggable(body, kind);
                final SparkWebContext ctx = new SparkWebContext(request, response);
                Map<Object, Object> map = new HashMap<>();
                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                try {
                    model = model.validateCreation(map);
                } catch (EntityError e) {
                    response.status(HTTP_CONFLICT);
                    response.type(JSON_CONTENT_TYPE);
                    return jsonUtils.toJSON(MessagesWrapper.fromSpecificationMapToMessages(map));
                }
                Object found = OrpheusDbJPAEntityManagerUtils.find(model);
                if (found != null) {
                    response.status(HTTP_CONFLICT);
                    return EMPTY_RESPONSE;
                }
                DBUtil.persist(model);
                response.status(HTTP_CREATED);
                response.type(JSON_CONTENT_TYPE);
                response.header("Location", request.pathInfo() + "/" + model.getId());
                Taggable storedModel = OrpheusDbJPAEntityManagerUtils.find(model);
                return jsonUtils.toJSON(storedModel);
            } catch (Throwable ex) {
                ex.printStackTrace();
                if (EntityInsertionError.class.isAssignableFrom(ex.getClass())) {
                    return throwError(response, HTTP_CONFLICT, ex);
                } else {
                    return throwError(response, ex);
                }
            }
        });
        get(Commons.EMPTY_PATH, (request, response) -> {
            Set<String> queryParams = request.queryParams();
            String query = request.queryParams("query");
            if (CollectionUtils.isEmpty(queryParams) || StringUtils.isNotBlank(query)) {
                return getList(request, response, Item.GET_ALL_MODELS, null, null);
            }
            String name = request.queryParams("name");
            String kind = request.queryParams("kind");
            Map<String, Object> params = new HashMap<>();
            params.put("name", name);
            params.put("kind", enumsUtils.getEnum(ElementKind.class, kind));
            List<Item> storedModel = OrpheusDbJPAEntityManagerUtils.executeQuery(Item.class, Item.GET_ITEMS_BY_NAME_AND_KIND, params);
            response.status(HTTP_OK);
            response.type(JSON_CONTENT_TYPE);
            return jsonUtils.toJSON(storedModel);
        });
        get("/:uuid", (request, response) -> {
            try {
                String id = request.params(":uuid");
                Taggable model = OrpheusDbJPAEntityManagerUtils.find(Taggable.class, id);
                if (model != null) {
                    model = model.validateRequest();
                }
                response.status(HTTP_OK);
                response.type(JSON_CONTENT_TYPE);
                return jsonUtils.toJSON(model);
            } catch (Exception ex) {
                return throwError(response, ex);
            }
        });
        put("/:uuid", (request, response) -> {
            try {
                String body = request.body();
                Map<String, Object> incomingModel = (Map<String, Object>) jsonUtils.fromJSON(body, Map.class);
                Object kind = incomingModel.get("kind");
                String id = request.params(":uuid");
                ArchitecturalModel model = extractTaggable(body, kind);
                model.setId(id);
                Map<String, Object> params = new HashMap<>();
                params.put("name", model.getName());
                params.put("kind", model.getKind());
                Map<Object, Object> map = new HashMap<>();

                final SparkWebContext ctx = new SparkWebContext(request, response);
                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);

                Item storedModel = OrpheusDbJPAEntityManagerUtils.find(model);
                if (storedModel == null) {
                    storedModel = OrpheusDbJPAEntityManagerUtils.findByQuery(Item.class, Item.GET_ITEMS_BY_NAME_AND_KIND, params);
                }

                if (storedModel == null) {
                    map.put("Parent", model);
                    model = model.validateCreation(map);
                    DBUtil.persist(model);
                    response.status(HTTP_CREATED);
                    return EMPTY_RESPONSE;
                } else {
                    ArchitecturalModel storedModel_ = (ArchitecturalModel) storedModel;
                    map.put("Parent", storedModel_);
                    model = model.validateReplacement(map);
                    storedModel_.override(model);
                    DBUtil.update(storedModel_);
                    response.status(HTTP_OK);
                    return EMPTY_RESPONSE;
                }

            } catch (EntityNotFoundException ex) {
                response.status(HTTP_NOT_FOUND);
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                if (EntityInsertionError.class.isAssignableFrom(ex.getClass())) {
                    return throwError(response, HTTP_CONFLICT, ex);
                } else {
                    ex.printStackTrace();
                    return throwError(response, ex);
                }
            }
        });
        delete("/:uuid", (request, response) -> {
            try {
                String id = request.params(":uuid");
                Taggable entity = OrpheusDbJPAEntityManagerUtils.find(Taggable.class, id);
                DBUtil.delete(entity);
                response.status(HTTP_OK);
                return EMPTY_RESPONSE;
            } catch (EntityNotFoundException | EntityDeletionError ex) {
                response.status(HTTP_NOT_FOUND);
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/:uuid/clone", (request, response) -> {
            try {
                String id = request.params(":uuid");
                String suffix = StringUtils.defaultIfEmpty(request.queryParams("suffix"), StringUtils.EMPTY);
                Taggable model = OrpheusDbJPAEntityManagerUtils.find(Taggable.class, id);
                if (model != null) {
                    model = model.validateRequest();
                } else {
                    throw new Exception("Model with id of '" + id + "' not found");
                }
                Object clonedModel = model.getClass().newInstance();
                CompositeElement clonedFrom = ((Item) model).buildCompositeElement();
                if (DiagramableElement.class.isAssignableFrom(clonedModel.getClass()) || BaseEntity.class.isAssignableFrom(clonedModel.getClass())) {
                    Object[] overrideArgs = new Object[5];
                    overrideArgs[0] = model;
                    overrideArgs[1] = false;
                    overrideArgs[2] = suffix;
                    overrideArgs[3] = clonedFrom;
                    overrideArgs[4] = itemComparator;
                    reflectionUtils.invokeMethod(clonedModel, "override", overrideArgs);
                } else {
                    throw new Exception("Invalid model");
                }
                Item clonedModelItem = (Item) clonedModel;
                clonedModelItem.setClonedFrom(clonedFrom);
                OpenArchi.fixCompositeFromItem(clonedModelItem);
                String name = clonedModelItem.getName();
                Map<String, Object> map = new HashMap<>();
                map.put("type", model.getClass());
                map.put("name", name);
                List<IdName> modelNames = OrpheusDbJPAEntityManagerUtils.executeQuery(IdName.class, Item.GET_MODEL_NAMES_BY_NAME_AND_TYPE, map);
                if (CollectionUtils.isNotEmpty(modelNames)) {
                    Collections.sort(modelNames);
                    IdName lastFoundName = IterableUtils.get(modelNames, modelNames.size() - 1);
                    name = lastFoundName.getName() + " (1)";
                }
                clonedModelItem.setName(name);
                response.status(HTTP_OK);
                response.type(JSON_CONTENT_TYPE);
                return jsonUtils.toJSON(clonedModel);
            } catch (Exception ex) {
                return throwError(response, ex);
            }
        });
        put("/:uuid/image", (request, response) -> {
            try {
                Image image = jsonUtils.fromJSON(request.body(), Image.class);
                if (image == null) {
                    throw new Exception("Invalid kind of relationship");
                }
                final SparkWebContext ctx = new SparkWebContext(request, response);
                Map<Object, Object> map = new HashMap<>();
                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                image = image.validateReplacement(map);
                DBUtil.persist(image);
                response.status(HTTP_CREATED);
                response.type(JSON_CONTENT_TYPE);
                response.header("Location", request.pathInfo() + image.getId());
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        patch("/:uuid/status/:sid", (request, response) -> {
            try {
                Taggable model = jsonUtils.fromJSON(request.body(), Taggable.class);
                if (model == null) {
                    throw new Exception("Invalid kind of model");
                }
                String id = request.params(":uuid");
                model.setId(id);
                String sid = request.params(":sid");
                model.setStatus((Status) enumsUtils.getEnum(Status.class, sid));
                final SparkWebContext ctx = new SparkWebContext(request, response);
                Map<Object, Object> map = new HashMap<>();
                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                model = model.validateReplacement(map);
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
        put("/:uuid/children", (request, response) -> {
            response.status(HTTP_NOT_IMPLEMENTED);
            response.type(JSON_CONTENT_TYPE);
            return EMPTY_RESPONSE;
        });
        get("/:uuid/children", (request, response) -> {
            Map<String, Object> params = new HashMap<>();
            params.put("id", request.params(":uuid"));
            return getList(request, response, Item.GET_ALL_CHILDREN, params, Collection.class);
        });
        put("/:uuid/children", (request, response) -> {
            response.status(HTTP_NOT_IMPLEMENTED);
            response.type(JSON_CONTENT_TYPE);
            return EMPTY_RESPONSE;
        });
        get("/:uuid/parent", (request, response) -> {
            response.status(HTTP_NOT_IMPLEMENTED);
            response.type(JSON_CONTENT_TYPE);
            return EMPTY_RESPONSE;
        });
        post("/:uuid/parent", (request, response) -> {
            try {
                CompositeElement model;
                try {
                    model = jsonUtils.fromJSON(request.body(), CompositeElement.class);
                } catch (Throwable t) {
                    throw new Exception("Invalid kind of parent model info due: '" + t.getMessage() + "'");
                }
                DBUtil.persistComposite(model);
                response.status(HTTP_CREATED);
                response.header("Location", request.pathInfo() + model.getId());
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/:uuid/meta-data", (request, response) -> {
            Map<String, Object> params = new HashMap<>();
            params.put("id", request.params(":uuid"));
            return getElement(request, response, Item.GET_META_DATA, params, MetaData.class);
        });
        post("/:uuid/meta-data", (request, response) -> {
            try {
                MetaData metaData = jsonUtils.fromJSON(request.body(), MetaData.class);
                if (metaData == null) {
                    throw new Exception("Invalid metadata");
                }
                final SparkWebContext ctx = new SparkWebContext(request, response);
                Map<Object, Object> map = new HashMap<>();
                Account account = (Account) ctx.getRequest().getSession().getAttribute("account");
                map.put("account", account);
                metaData = metaData.validateCreation(map);
                DBUtil.persist(metaData);
                response.status(HTTP_CREATED);
                response.header("Location", request.pathInfo() + request.params(":uuid") + "/meta-data");
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/:uuid/features", (request, response) -> {
            Map<String, Object> params = new HashMap<>();
            params.put("id", request.params(":uuid"));
            return getList(request, response, Element.GET_ALL_FEATURES, params, Collection.class);
        });
        put("/:uuid/features", (request, response) -> {
            try {

                CompositeElement feature = jsonUtils.fromJSON(request.body(), CompositeElement.class);
                if (feature == null) {
                    throw new Exception("Invalid kind of feature");
                }
                DBUtil.persistComposite(feature);
                response.status(HTTP_CREATED);
                response.header("Location", request.pathInfo() + feature.getId());
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
    }

    private void deleteFromCollection(Object entity, Set<Item> includedInModels) {
        if (CollectionUtils.isNotEmpty(includedInModels)) {
            includedInModels.forEach(model -> {
                Collection<Field> fields = reflectionUtils.getAllFieldsIncludingParents(model.getClass());
                CollectionUtils.filter(fields, field -> Collection.class.isAssignableFrom(field.getType()));
                fields.forEach(field -> {
                    try {
                        field.setAccessible(true);
                        try {
                            Collection collection = (Collection) field.get(model);
                            collection.remove(entity);
                        } catch (ClassCastException | IllegalArgumentException ignored) {
                        } catch (Throwable t) {//TODO ignore
                            t.printStackTrace();
                        }
                    } catch (Throwable t) {
                        //TODO ignore
                        t.printStackTrace();
                    }
                });
            });
        }
    }

}
