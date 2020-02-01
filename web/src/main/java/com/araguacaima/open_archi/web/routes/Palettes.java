package com.araguacaima.open_archi.web.routes;

import com.araguacaima.open_archi.persistence.diagrams.core.ElementKind;
import com.araguacaima.open_archi.persistence.diagrams.core.Item;
import com.araguacaima.open_archi.persistence.diagrams.core.Palettable;
import com.araguacaima.open_archi.web.OpenArchi;
import spark.RouteGroup;

import java.util.ArrayList;
import java.util.List;

import static com.araguacaima.open_archi.web.common.Commons.*;
import static java.net.HttpURLConnection.HTTP_NOT_IMPLEMENTED;
import static java.net.HttpURLConnection.HTTP_OK;
import static spark.Spark.get;
import static spark.Spark.post;

public class Palettes implements RouteGroup {

    public static final String PATH = "/palette";

    public static List<ElementKind> ARCHITECTURE_TYPES = new ArrayList<ElementKind>() {
        {
            add(ElementKind.ARCHITECTURE_MODEL);
            add(ElementKind.LAYER);
            add(ElementKind.SYSTEM);
            add(ElementKind.CONTAINER);
            add(ElementKind.COMPONENT);
        }
    };

    public static List<ElementKind> BPM_TYPES = new ArrayList<ElementKind>() {
        {
            add(ElementKind.BPM_MODEL);
            add(ElementKind.POOL);
            add(ElementKind.LANE);
            add(ElementKind.ACTIVITY);
        }
    };

    public static List<ElementKind> FLOWCHART_TYPES = new ArrayList<ElementKind>() {
        {
            add(ElementKind.FLOWCHART_MODEL);
            add(ElementKind.FLOWCHART_INITIATOR);
            add(ElementKind.FLOWCHART_FINISHER);
            add(ElementKind.FLOWCHART_CONDITION);
        }
    };

    public static List<ElementKind> GANTT_TYPES = new ArrayList<ElementKind>() {
        {
            add(ElementKind.GANTT_MODEL);
            add(ElementKind.GANTT);
        }
    };

    public static List<ElementKind> SEQUENCE_TYPES = new ArrayList<ElementKind>() {
        {
            add(ElementKind.SEQUENCE_MODEL);
            add(ElementKind.SEQUENCE);
        }
    };

    public static List<ElementKind> ENTITY_RELATIONSHIP_TYPES = new ArrayList<ElementKind>() {
        {
            add(ElementKind.ENTITY_RELATIONSHIP_MODEL);
            add(ElementKind.ENTITY);
            add(ElementKind.ATTRIBUTE);
        }
    };

    public static List<ElementKind> UML_CLASS_TYPES = new ArrayList<ElementKind>() {
        {
            add(ElementKind.UML_CLASS_MODEL);
            add(ElementKind.CLASS);
            add(ElementKind.METHOD);
            add(ElementKind.FIELD);
            add(ElementKind.PARAMETER);
        }
    };


    public static List<ElementKind> COMPONENT_TYPES = new ArrayList<ElementKind>() {
        {
            add(ElementKind.COMPONENT_MODEL);
            add(ElementKind.GROUP);
            add(ElementKind.ELEMENT);
        }
    };

    @Override
    public void addRoutes() {
        get("/architectures", (request, response) -> {
            try {
                Palettable palette = OpenArchi.getArchitecturePalette(Item.GET_ALL_PROTOTYPES_OF_TYPES, ARCHITECTURE_TYPES);
                response.status(HTTP_OK);
                response.type(JSON_CONTENT_TYPE);
                return jsonUtils.toJSON(palette);
            } catch (Exception ex) {
                return throwError(response, ex);
            }
        });
        post("/architectures", (request, response) -> {
            response.status(HTTP_NOT_IMPLEMENTED);
            return EMPTY_RESPONSE;
        });
        get("/bpms", (request, response) -> {
            try {
                Palettable palette = OpenArchi.getBpmPalette(Item.GET_ALL_PROTOTYPES_OF_TYPES, BPM_TYPES);
                response.status(HTTP_OK);
                response.type(JSON_CONTENT_TYPE);
                return jsonUtils.toJSON(palette);
            } catch (Exception ex) {
                return throwError(response, ex);
            }
        });
        post("/bpms", (request, response) -> {
            response.status(HTTP_NOT_IMPLEMENTED);
            return EMPTY_RESPONSE;
        });
        get("/flowcharts", (request, response) -> {
            try {
                Palettable palette = OpenArchi.getFlowchartPalette(Item.GET_ALL_PROTOTYPES_OF_TYPES, FLOWCHART_TYPES);
                response.status(HTTP_OK);
                response.type(JSON_CONTENT_TYPE);
                return jsonUtils.toJSON(palette);
            } catch (Exception ex) {
                return throwError(response, ex);
            }
        });
        post("/flowcharts", (request, response) -> {
            response.status(HTTP_NOT_IMPLEMENTED);
            return EMPTY_RESPONSE;
        });
        get("/gantts", (request, response) -> {
            try {
                Palettable palette = OpenArchi.getGanttPalette(Item.GET_ALL_PROTOTYPES_OF_TYPES, GANTT_TYPES);
                response.status(HTTP_OK);
                response.type(JSON_CONTENT_TYPE);
                return jsonUtils.toJSON(palette);
            } catch (Exception ex) {
                return throwError(response, ex);
            }
        });
        post("/gantts", (request, response) -> {
            response.status(HTTP_NOT_IMPLEMENTED);
            return EMPTY_RESPONSE;
        });
        get("/sequences", (request, response) -> {
            try {
                Palettable palette = OpenArchi.getSequencePalette(Item.GET_ALL_PROTOTYPES_OF_TYPES, SEQUENCE_TYPES);
                response.status(HTTP_OK);
                response.type(JSON_CONTENT_TYPE);
                return jsonUtils.toJSON(palette);
            } catch (Exception ex) {
                return throwError(response, ex);
            }
        });
        post("/sequences", (request, response) -> {
            response.status(HTTP_NOT_IMPLEMENTED);
            return EMPTY_RESPONSE;
        });

        get("/entity-relationships", (request, response) -> {
            try {
                Palettable palette = OpenArchi.getEntityRelationshipPalette(Item.GET_ALL_PROTOTYPES_OF_TYPES, ENTITY_RELATIONSHIP_TYPES);
                response.status(HTTP_OK);
                response.type(JSON_CONTENT_TYPE);
                return jsonUtils.toJSON(palette);
            } catch (Exception ex) {
                return throwError(response, ex);
            }
        });
        post("/entity-relationships", (request, response) -> {
            response.status(HTTP_NOT_IMPLEMENTED);
            return EMPTY_RESPONSE;
        });
        get("/uml-classes", (request, response) -> {
            try {
                Palettable palette = OpenArchi.getUmlClassPalette(Item.GET_ALL_PROTOTYPES_OF_TYPES, UML_CLASS_TYPES);
                response.status(HTTP_OK);
                response.type(JSON_CONTENT_TYPE);
                return jsonUtils.toJSON(palette);
            } catch (Exception ex) {
                return throwError(response, ex);
            }
        });
        post("/uml-classes", (request, response) -> {
            response.status(HTTP_NOT_IMPLEMENTED);
            return EMPTY_RESPONSE;
        });
        get("/components", (request, response) -> {
            try {
                Palettable palette = OpenArchi.getComponentPalette(Item.GET_ALL_PROTOTYPES_OF_TYPES, COMPONENT_TYPES);
                response.status(HTTP_OK);
                response.type(JSON_CONTENT_TYPE);
                return jsonUtils.toJSON(palette);
            } catch (Exception ex) {
                return throwError(response, ex);
            }
        });
        post("/components", (request, response) -> {
            response.status(HTTP_NOT_IMPLEMENTED);
            return EMPTY_RESPONSE;
        });


    }
}
