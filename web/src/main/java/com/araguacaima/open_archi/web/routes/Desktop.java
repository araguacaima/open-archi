package com.araguacaima.open_archi.web.routes;

import com.araguacaima.commons.utils.StringUtils;
import com.araguacaima.open_archi.persistence.diagrams.core.*;
import com.araguacaima.open_archi.web.Api;
import com.araguacaima.open_archi.web.BeanBuilder;
import com.araguacaima.open_archi.web.OpenArchi;
import com.araguacaima.open_archi.web.common.Commons;
import com.araguacaima.orpheusdb.utils.OrpheusDbJPAEntityManagerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.RouteGroup;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static com.araguacaima.open_archi.web.Samples.getExamples;
import static com.araguacaima.open_archi.web.Server.engine;
import static com.araguacaima.open_archi.web.common.Commons.buildModelAndView;
import static com.araguacaima.open_archi.web.common.Commons.enumsUtils;
import static spark.Spark.get;

public class Desktop implements RouteGroup {

    public static final String PATH = "/desktop";
    private static final Logger log = LoggerFactory.getLogger(Desktop.class);

    @Override
    public void addRoutes() {
        BeanBuilder bean = new BeanBuilder();
        bean.diagramTypes(new TreeSet<>(OpenArchi.getDiagramTypes()));
        get(Commons.EMPTY_PATH, (req, res) -> {
                    bean.title("OpenArchi | Desktop");
                    bean.examples(getExamples());
                    bean.prototyper(true);
                    bean.diagramType(new DiagramType());
                    bean.loadDesktopPrototyper(false);
                    bean.loadDesktopApi(false);
                    String kind = req.queryParams("kind");
                    String name = req.queryParams("name");
                    if (StringUtils.isNotBlank(kind) && StringUtils.isNotBlank(name)) {
                        try {
                            Map<String, Object> map = new HashMap<>();
                            Enum anEnum = (Enum) enumsUtils.getEnum(ElementKind.class, kind);
                            map.put("kind", anEnum);
                            map.put("name", name);
                            Taggable model = OrpheusDbJPAEntityManagerUtils.findByQuery(Item.class, Item.GET_PROTOTYPES_BY_NAME_AND_KIND, map);
                            if (model != null) {
                                model = model.validateRequest();
                                bean.model(model);
                            }
                        } catch (Throwable t) {
                            log.warn(t.getMessage());
                        }
                    } else {
                        log.debug("Loading empty prototyper due name and kind query parameters are both empty");
                    }
                    return buildModelAndView(req, res, bean, PATH + "/home");
                },
                engine);
        get(Prototyper.PATH, (req, res) -> {
                    bean.title("OpenArchi | Desktop");
                    String type = req.queryParams("type");
                    Map<String, Object> map = new HashMap<>();
                    DiagramType diagramType = null;
                    Set<ElementShape> elementTypes;
                    bean.palette(new DefaultPalette());
                    bean.elementTypes(new TreeSet<>());
                    if (StringUtils.isNotBlank(type)) {
                        map.put("type", enumsUtils.getEnum(ElementKind.class, type));
                        diagramType = OrpheusDbJPAEntityManagerUtils.findByQuery(DiagramType.class, DiagramType.GET_ACTIVE_DIAGRAM_TYPE_BY_TYPE, map);
                        bean.diagramType(diagramType);
                        elementTypes = diagramType.getElementShapes();
                        bean.elementTypes(elementTypes);
                    } else {
                        bean.diagramType(new DiagramType());
                    }
                    if (diagramType != null) {
                        switch (diagramType.getType()) {
                            case ARCHITECTURE_MODEL:
                                bean.palette(OpenArchi.getArchitecturePalette(Item.GET_ALL_PROTOTYPES_OF_TYPES, Palettes.ARCHITECTURE_TYPES));
                                map.put("type", ElementKind.ARCHITECTURE_MODEL);
                                break;
                            case COMPONENT_MODEL:
                                bean.palette(OpenArchi.getComponentPalette(Item.GET_ALL_PROTOTYPES_OF_TYPES, Palettes.COMPONENT_TYPES));
                                map.put("type", ElementKind.COMPONENT_MODEL);
                                break;
                            default:
                                break;
                        }
                        bean.elementTypes(diagramType.getElementShapes());
                    }
                    bean.diagramTypes(new TreeSet<>(OpenArchi.getDiagramTypes()));
                    bean.examples(getExamples());
                    bean.model(new Object());
                    if (req.queryParams("fullView") != null) {
                        bean.fullView(true);
                    } else {
                        bean.fullView(null);
                    }
                    bean.prototyper(true);
                    bean.loadDesktopPrototyper(true);
                    bean.loadDesktopApi(false);
                    String kind = req.queryParams("kind");
                    String name = req.queryParams("name");
                    if (StringUtils.isNotBlank(kind) && StringUtils.isNotBlank(name)) {
                        try {
                            Enum anEnum = (Enum) enumsUtils.getEnum(ElementKind.class, kind);
                            map.put("kind", anEnum);
                            map.put("name", name);
                            Taggable model = OrpheusDbJPAEntityManagerUtils.findByQuery(Item.class, Item.GET_PROTOTYPES_BY_NAME_AND_KIND, map);
                            if (model != null) {
                                model = model.validateRequest();
                                bean.model(model);
                            }
                        } catch (Throwable t) {
                            log.warn(t.getMessage());
                        }
                    } else {
                        log.debug("Loading empty prototyper due name and kind query parameters are both empty");
                    }
                    return buildModelAndView(req, res, bean, PATH + "/home");
                },
                engine);
        get(Prototyper.PATH + "/:uuid", (req, res) -> {
                    String id = req.params(":uuid");
                    bean.title("Desktop");
                    bean.palette(OpenArchi.getArchitecturePalette(Item.GET_ALL_PROTOTYPES_OF_TYPES, Palettes.ARCHITECTURE_TYPES));
                    bean.elementTypes(new TreeSet<>());
                    bean.diagramTypes(new TreeSet<>(OpenArchi.getDiagramTypes()));
                    bean.examples(getExamples());
                    bean.model(new Object());
                    if (req.queryParams("fullView") != null) {
                        bean.fullView(true);
                    } else {
                        bean.fullView(null);
                    }
                    bean.prototyper(true);
                    bean.loadDesktopPrototyper(true);
                    bean.prototypeId(id);
                    String kind = req.queryParams("kind");
                    String name = req.queryParams("name");
                    if (StringUtils.isNotBlank(id)) {
                        Taggable model = OrpheusDbJPAEntityManagerUtils.find(Taggable.class, id);
                        if (model != null) {
                            model = model.validateRequest();
                            bean.model(model);
                        }
                    } else if (StringUtils.isNotBlank(kind) && StringUtils.isNotBlank(name)) {
                        try {
                            Map<String, Object> map = new HashMap<>();
                            Enum anEnum = (Enum) enumsUtils.getEnum(ElementKind.class, kind);
                            map.put("kind", anEnum);
                            map.put("name", name);
                            Taggable model = OrpheusDbJPAEntityManagerUtils.findByQuery(Item.class, Item.GET_PROTOTYPES_BY_NAME_AND_KIND, map);
                            if (model != null) {
                                model = model.validateRequest();
                                bean.model(model);
                            }
                        } catch (Throwable t) {
                            log.warn(t.getMessage());
                        }
                    } else {
                        log.debug("Loading empty prototyper due name and kind query parameters are both empty");
                    }
                    return buildModelAndView(req, res, bean, PATH + "/home");
                },
                engine);
        get(Api.PATH, (req, res) -> {
                    bean.title("OpenArchi | Desktop");
                    bean.prototyper(false);
                    bean.loadDesktopPrototyper(false);
                    bean.loadDesktopApi(true);
                    return buildModelAndView(req, res, bean, PATH + "/home");
                },
                engine);
    }

}
