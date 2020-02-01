package com.araguacaima.open_archi.web.routes;

import com.araguacaima.open_archi.persistence.commons.IdName;
import com.araguacaima.open_archi.persistence.diagrams.core.*;
import com.araguacaima.open_archi.web.DBUtil;
import com.araguacaima.open_archi.web.OpenArchi;
import com.araguacaima.open_archi.web.common.Commons;
import com.araguacaima.orpheusdb.utils.OrpheusDbJPAEntityManagerUtils;
import org.apache.commons.collections4.IterableUtils;
import spark.RouteGroup;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.araguacaima.open_archi.web.common.Commons.*;
import static java.net.HttpURLConnection.*;
import static spark.Spark.*;

public class Catalogs implements RouteGroup {

    public static final String PATH = "/catalogs";

    @Override
    public void addRoutes() {
        get("/element-types", (request, response) -> getList(request, response, ElementShape.GET_ALL_ELEMENT_SHAPES, null, null));
        get("/element-types/:elementTypeId/shape", (request, response) -> {
            Map<String, Object> params = new HashMap<>();
            ElementKind type = (ElementKind) enumsUtils.getEnum(ElementKind.class, request.params(":elementTypeId"));
            params.put("type", type);
            ElementShape elementShape = OrpheusDbJPAEntityManagerUtils.findByQuery(ElementShape.class, ElementShape.GET_ELEMENT_SHAPE_BY_TYPE, params);
            return jsonUtils.toJSON(elementShape);
        });
        put("/element-types/:elementTypeId/shape", (request, response) -> {
            try {
                ElementShape elementShape = jsonUtils.fromJSON(request.body(), ElementShape.class);
                if (elementShape == null) {
                    throw new Exception("Invalid kind of element shape");
                }
                Map<String, Object> params = new HashMap<>();
                ElementKind type = (ElementKind) enumsUtils.getEnum(ElementKind.class, request.params(":elementTypeId"));
                params.put("type", type);
                ElementShape elementShape1 = OrpheusDbJPAEntityManagerUtils.findByQuery(ElementShape.class, ElementShape.GET_ELEMENT_SHAPE_BY_TYPE, params);
                if (elementShape1 == null) {
                    elementShape.setType(type);
                    DBUtil.persist(elementShape);
                } else {
                    elementShape1.override(elementShape);
                    DBUtil.update(elementShape1);
                }
                response.status(HTTP_CREATED);
                response.header("Location", request.pathInfo() + Commons.SEPARATOR_PATH + elementShape.getType());
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/color-schemes/:colorSchemeId", (request, response) -> {
            String colorSchemeId = request.params(":colorSchemeId");
            ColorScheme colorScheme = OrpheusDbJPAEntityManagerUtils.find(ColorScheme.class, colorSchemeId);
            if (colorScheme == null) {
                response.status(HTTP_NOT_FOUND);
                return throwError(response, new EntityNotFoundException());
            }
            response.status(HTTP_OK);
            return jsonUtils.toJSON(colorScheme);
        });
        patch("/color-schemes/:colorSchemeId", (request, response) -> {
            String colorSchemeId = request.params(":colorSchemeId");
            ColorScheme colorScheme = OrpheusDbJPAEntityManagerUtils.find(ColorScheme.class, colorSchemeId);
            if (colorScheme == null) {
                response.status(HTTP_NOT_FOUND);
                return throwError(response, new EntityNotFoundException());
            }
            try {
                Map<Object, Object> map = new HashMap<>();
                colorScheme = colorScheme.validateReplacement(map);
                ColorScheme newColorScheme = jsonUtils.fromJSON(request.body(), ColorScheme.class);
                newColorScheme.setMeta(colorScheme.getMeta());
                colorScheme.copyNonEmpty(newColorScheme, true);
                DBUtil.update(colorScheme);
                response.status(HTTP_OK);
                return EMPTY_RESPONSE;
            } catch (EntityNotFoundException ex) {
                response.status(HTTP_NOT_FOUND);
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/element-types/:elementTypeId/shape/color-schemes", (request, response) -> {
            Map<String, Object> params = new HashMap<>();
            String elemenTypeId = request.params(":elementTypeId");
            String colorSchemeTypeName = request.queryParams("colorSchemeType");
            ElementKind type = (ElementKind) enumsUtils.getEnum(ElementKind.class, elemenTypeId);
            ColorSchemeOption colorSchemeType = (ColorSchemeOption) enumsUtils.getEnum(ColorSchemeOption.class, colorSchemeTypeName);
            params.put("type", type);
            ElementShape elementShape = OrpheusDbJPAEntityManagerUtils.findByQuery(ElementShape.class, ElementShape.GET_ELEMENT_SHAPE_BY_TYPE, params);
            ColorScheme colorScheme = IterableUtils.find(elementShape.getColorSchemes(), colorScheme1 -> colorScheme1.getName().equals(colorSchemeType));
            return jsonUtils.toJSON(colorScheme);
        });
        put("/element-types/:elementTypeId/shape/color-schemes", (request, response) -> {
            try {
                ColorScheme newColorScheme = jsonUtils.fromJSON(request.body(), ColorScheme.class);
                if (newColorScheme == null) {
                    throw new Exception("Invalid kind of color scheme");
                }
                Map<String, Object> params = new HashMap<>();
                ElementKind type = (ElementKind) enumsUtils.getEnum(ElementKind.class, request.params(":elementTypeId"));
                params.put("type", type);
                ElementShape elementShape = OrpheusDbJPAEntityManagerUtils.findByQuery(ElementShape.class, ElementShape.GET_ELEMENT_SHAPE_BY_TYPE, params);
                if (elementShape == null) {
                    response.status(HTTP_NOT_FOUND);
                    return throwError(response, new EntityNotFoundException());
                } else {
                    ColorScheme colorScheme = IterableUtils.find(elementShape.getColorSchemes(), colorScheme1 -> colorScheme1.equals(newColorScheme));
                    if (colorScheme == null) {
                        DBUtil.persist(newColorScheme);
                        elementShape.getColorSchemes().add(newColorScheme);
                        response.status(HTTP_CREATED);
                        response.header("Location", "/color-schemes" + Commons.SEPARATOR_PATH + newColorScheme.getId());
                    } else {
                        colorScheme.override(newColorScheme);
                        DBUtil.update(colorScheme);
                        response.status(HTTP_ACCEPTED);
                        response.header("Location", "/color-schemes" + Commons.SEPARATOR_PATH + newColorScheme.getId());
                    }
                    DBUtil.update(elementShape);
                }
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        post("/diagram-types", (request, response) -> {
            response.status(HTTP_NOT_IMPLEMENTED);
            response.type(JSON_CONTENT_TYPE);
            return EMPTY_RESPONSE;
        });
        get("/diagram-types", (request, response) -> getList(request, response, DiagramType.GET_ALL_ACTIVE_DIAGRAM_TYPES, null, null));
        get("/diagram-types/:diagramTypeId/element-types", (request, response) -> {
            Map<String, Object> params = new HashMap<>();
            String diagramTypeId = request.params(":diagramTypeId");
            ElementKind type = (ElementKind) enumsUtils.getEnum(ElementKind.class, diagramTypeId);
            params.put("type", type);
            DiagramType diagramType = OrpheusDbJPAEntityManagerUtils.findByQuery(DiagramType.class, DiagramType.GET_DIAGRAM_TYPE_BY_TYPE, params);
            if (diagramType == null) {
                response.status(HTTP_NOT_FOUND);
                return throwError(response, new Exception("Diagram type not found with type of '" + diagramTypeId + "'"));
            } else {
                return jsonUtils.toJSON(diagramType.getElementShapes());
            }
        });
        put("/diagram-types/:diagramTypeId/shape/color-schemes", (request, response) -> {
            try {
                ColorScheme newColorScheme = jsonUtils.fromJSON(request.body(), ColorScheme.class);
                if (newColorScheme == null) {
                    throw new Exception("Invalid kind of color scheme");
                }
                Map<String, Object> params = new HashMap<>();
                ElementKind type = (ElementKind) enumsUtils.getEnum(ElementKind.class, request.params(":diagramTypeId"));
                params.put("type", type);
                DiagramType diagramType = OrpheusDbJPAEntityManagerUtils.findByQuery(DiagramType.class, DiagramType.GET_DIAGRAM_TYPE_BY_TYPE, params);
                if (diagramType == null) {
                    response.status(HTTP_NOT_FOUND);
                    return throwError(response, new EntityNotFoundException());
                } else {
                    ColorScheme colorScheme = IterableUtils.find(diagramType.getColorSchemes(), colorScheme1 -> colorScheme1.equals(newColorScheme));
                    if (colorScheme == null) {
                        DBUtil.persist(newColorScheme);
                        diagramType.getColorSchemes().add(newColorScheme);
                        response.status(HTTP_CREATED);
                        response.header("Location", "/color-schemes" + Commons.SEPARATOR_PATH + newColorScheme.getId());
                    } else {
                        colorScheme.override(newColorScheme);
                        DBUtil.update(colorScheme);
                        response.status(HTTP_ACCEPTED);
                        response.header("Location", "/color-schemes" + Commons.SEPARATOR_PATH + newColorScheme.getId());
                    }
                    //DBUtil.update(diagramType);
                }
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });

        post("/diagram-names", (request, response) -> {
            response.status(HTTP_NOT_IMPLEMENTED);
            response.type(JSON_CONTENT_TYPE);
            return EMPTY_RESPONSE;
        });
        get("/diagram-names", (request, response) -> OpenArchi.getItemNames(request, response, Item.GET_ALL_DIAGRAM_NAMES));
        post("/prototype-names", (request, response) -> {
            response.status(HTTP_NOT_IMPLEMENTED);
            response.type(JSON_CONTENT_TYPE);
            return EMPTY_RESPONSE;
        });
        get("/prototype-names", (request, response) -> OpenArchi.getItemNames(request, response, Item.GET_ALL_PROTOTYPE_NAMES));
        post("/consumer-names", (request, response) -> {
            response.status(HTTP_NOT_IMPLEMENTED);
            response.type(JSON_CONTENT_TYPE);
            return EMPTY_RESPONSE;
        });
        get("/consumer-names", (request, response) -> {
            String diagramNames = (String) getList(request, response, Item.GET_ALL_CONSUMER_NAMES, null, IdName.class);
            List diagramNamesList = getListIdName(diagramNames);
            return getList(request, response, diagramNamesList);
        });
        post("/element-roles", (request, response) -> {
            try {
                String body = request.body();
                ElementRole model;
                try {
                    model = jsonUtils.fromJSON(body, ElementRole.class);
                } catch (Throwable ignored) {
                    throw new Exception("Invalid element role of '" + body + "'");
                }
                DBUtil.persist(model);
                response.status(HTTP_OK);
                response.type(JSON_CONTENT_TYPE);
                response.header("Location", request.pathInfo() + Commons.SEPARATOR_PATH + model.getId());
                return EMPTY_RESPONSE;
            } catch (Throwable ex) {
                return throwError(response, ex);
            }
        });
        get("/element-roles", (request, response) -> {
            String roleNames = (String) getList(request, response, ElementRole.GET_ALL_ROLES, null, ElementRole.class);
            List diagramNamesList = getListIdName(roleNames);
            return getList(request, response, diagramNamesList);

        });
        get("/json-schema", (request, response) -> {
            String modelType = request.queryParams("modelType");
            Map definition = Commons.getOpenArchiDefinition(modelType);
            response.status(HTTP_OK);
            response.type(JSON_CONTENT_TYPE);
            return jsonUtils.toJSON(definition);
        });
    }
}
