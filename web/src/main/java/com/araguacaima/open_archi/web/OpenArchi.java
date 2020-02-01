package com.araguacaima.open_archi.web;

import com.araguacaima.open_archi.persistence.commons.IdName;
import com.araguacaima.open_archi.persistence.diagrams.core.*;
import com.araguacaima.open_archi.web.common.Commons;
import com.araguacaima.open_archi.web.routes.Models;
import com.araguacaima.open_archi.web.routes.Root;
import com.araguacaima.orpheusdb.utils.OrpheusDbJPAEntityManagerUtils;
import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.config.Config;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.araguacaima.open_archi.web.Server.deployedServer;
import static com.araguacaima.open_archi.web.Server.engine;
import static com.araguacaima.open_archi.web.common.Commons.*;
import static com.araguacaima.open_archi.web.common.Security.JWT_SALT;

public class OpenArchi {

    public static final String PATH = "/";
    final static Config config = new ConfigFactory(JWT_SALT, engine).build(deployedServer, PATH, clients);
    //final static CallbackRoute callback = new CallbackRoute(config, null, true);
    public static Root root = new Root();
    //static Filter strongSecurityFilter = Authentication.buildStrongSecurityFilter(config);
    //static Filter adminApiFilter = new AdminAPIFilter(config, clients, "adminAuthorizer,custom," + DefaultAuthorizers.ALLOW_AJAX_REQUESTS + "," + DefaultAuthorizers.IS_AUTHENTICATED);
    //static Filter apiFilter = new APIFilter(config, clients, "checkHttpMethodAuthorizer,requireAnyRoleAuthorizer,custom," + DefaultAuthorizers.ALLOW_AJAX_REQUESTS + "," + DefaultAuthorizers.IS_AUTHENTICATED);
    //static Filter scopesFilter = new ScopesFilter(config, clients, "filterAllRolesAuthorizer");

    public static void fixCompositeFromItem(Item object) {
        Set<Item> items = reflectionUtils.extractByType(object, Item.class);
        Set<CompositeElement> composites = reflectionUtils.extractByType(object, CompositeElement.class);
        items.forEach(item -> {
            String id = item.getId();
            String key = item.getKey();
            composites.forEach(composite -> {
                if (composite.getId().equals(key)) {
                    composite.setId(id);
                    String link = composite.getLink();
                    if (StringUtils.isNotBlank(link)) {
                        link = link.replaceAll(key, id);
                    } else {
                        link = Models.PATH + Commons.SEPARATOR_PATH + id;
                    }
                    composite.setLink(link);
                }
            });
        });
    }

    static public <T extends Palettable> Palettable getPalette(Class<T> clazz, String query, List<ElementKind> types) {
        try {
            T palette = clazz.newInstance();
            List<Item> models;
            Map<String, Object> map = new HashMap<>();
            map.put("kindList", types);
            models = OrpheusDbJPAEntityManagerUtils.executeQuery(Item.class, query, map);
            int rank = palette.getBasicElements().size() + 1;
            if (models != null) {
                for (Item model : models) {
                    palette.addGeneralElement(buildPalette(rank, model));
                    rank++;
                }
            }
            return palette;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    static public Palettable getArchitecturePalette(String query, List<ElementKind> types) {
        return getPalette(com.araguacaima.open_archi.persistence.diagrams.architectural.Palette.class, query, types);
    }

    static public Palettable getBpmPalette(String query, List<ElementKind> types) {
        return getPalette(com.araguacaima.open_archi.persistence.diagrams.bpm.Palette.class, query, types);
    }

    static public Palettable getFlowchartPalette(String query, List<ElementKind> types) {
        return getPalette(com.araguacaima.open_archi.persistence.diagrams.flowchart.Palette.class, query, types);
    }

    static public Palettable getGanttPalette(String query, List<ElementKind> types) {
        return getPalette(com.araguacaima.open_archi.persistence.diagrams.gantt.Palette.class, query, types);
    }

    static public Palettable getSequencePalette(String query, List<ElementKind> types) {
        return getPalette(com.araguacaima.open_archi.persistence.diagrams.sequence.Palette.class, query, types);
    }

    static public Palettable getEntityRelationshipPalette(String query, List<ElementKind> types) {
        return getPalette(com.araguacaima.open_archi.persistence.diagrams.er.Palette.class, query, types);
    }

    static public Palettable getUmlClassPalette(String query, List<ElementKind> types) {
        return getPalette(com.araguacaima.open_archi.persistence.diagrams.classes.Palette.class, query, types);
    }

    static public Palettable getComponentPalette(String query, List<ElementKind> types) {
        return getPalette(com.araguacaima.open_archi.persistence.diagrams.component.Palette.class, query, types);
    }

    static public PaletteItem buildPalette(int rank, Item model) {
        PaletteItem item = new PaletteItem();
        item.setId(model.getId());
        item.setRank(rank);
        item.setKind(model.getKind());
        item.setName(model.getName());
        Shape shape = model.getShape();
        item.setShape(shape);
        item.setPrototype(model.isPrototype());
        return item;
    }

    static public List<ElementShape> getElementTypes() {
        return OrpheusDbJPAEntityManagerUtils.executeQuery(ElementShape.class, ElementShape.GET_ALL_ELEMENT_SHAPES);
    }

    static public Object getItemNames(Request request, Response response, String query) throws IOException, URISyntaxException {
        return getItemNames(request, response, query, null);
    }

    static public Object getItemNames(Request request, Response response, String query, String contentType) throws IOException, URISyntaxException {
        String diagramNames = (String) getList(request, response, query, null, IdName.class);
        List diagramNamesList = getListIdName(diagramNames);
        return getList(request, response, diagramNamesList, contentType);
    }

    static public List<DiagramType> getDiagramTypes() {
        return OrpheusDbJPAEntityManagerUtils.executeQuery(DiagramType.class, DiagramType.GET_ALL_ACTIVE_DIAGRAM_TYPES);
    }
}
