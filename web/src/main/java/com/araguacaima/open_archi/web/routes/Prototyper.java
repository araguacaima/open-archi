package com.araguacaima.open_archi.web.routes;

import com.araguacaima.commons.utils.EnumsUtils;
import com.araguacaima.commons.utils.StringUtils;
import com.araguacaima.open_archi.persistence.diagrams.core.ElementKind;
import com.araguacaima.open_archi.persistence.diagrams.core.Item;
import com.araguacaima.open_archi.persistence.diagrams.core.Taggable;
import com.araguacaima.open_archi.web.BeanBuilder;
import com.araguacaima.open_archi.web.Editor;
import com.araguacaima.open_archi.web.OpenArchi;
import com.araguacaima.open_archi.web.common.Commons;
import com.araguacaima.orpheusdb.utils.OrpheusDbJPAEntityManagerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.RouteGroup;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import static com.araguacaima.open_archi.web.Samples.getExamples;
import static com.araguacaima.open_archi.web.Server.engine;
import static com.araguacaima.open_archi.web.common.Commons.buildModelAndView;
import static spark.Spark.get;

public class Prototyper implements RouteGroup {

    public static final String PATH = "/prototyper";
    private static final EnumsUtils enumsUtils = EnumsUtils.getInstance();
    private static final Logger log = LoggerFactory.getLogger(Prototyper.class);

    @Override
    public void addRoutes() {
        //before(Commons.EMPTY_PATH, Commons.genericFilter);
        //before("/*", OpenArchi.strongSecurityFilter);
        BeanBuilder bean = new BeanBuilder();
        bean.diagramTypes(new TreeSet<>(OpenArchi.getDiagramTypes()));
        get(Commons.EMPTY_PATH, (req, res) -> {
            bean.title("Prototyper");
            bean.palette(OpenArchi.getArchitecturePalette(Item.GET_ALL_PROTOTYPES_OF_TYPES, Palettes.ARCHITECTURE_TYPES));
            bean.elementTypes(new TreeSet<>(OpenArchi.getElementTypes()));
            bean.diagramTypes(new TreeSet<>(OpenArchi.getDiagramTypes()));
            bean.source("basic");
            bean.examples(getExamples());
            bean.model(new Object());
            if (req.queryParams("fullView") != null) {
                bean.fullView(true);
            } else {
                bean.fullView(null);
            }
            bean.prototyper(true);
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
            return buildModelAndView(req, res, bean, Editor.PATH);
        }, engine);
        get("/:uuid", (req, res) -> {
            try {
                String id = req.params(":uuid");
                Taggable model = OrpheusDbJPAEntityManagerUtils.find(Taggable.class, id);
                if (model != null) {
                    model = model.validateRequest();
                    bean.model(model);
                }
                bean.title("Editor");
                bean.palette(OpenArchi.getArchitecturePalette(Item.GET_ALL_PROTOTYPES_OF_TYPES, Palettes.ARCHITECTURE_TYPES));
                bean.elementTypes(new TreeSet<>(OpenArchi.getElementTypes()));
                bean.source("basic");
                bean.examples(getExamples());
                if (req.queryParams("fullView") != null) {
                    bean.fullView(true);
                } else {
                    bean.fullView(null);
                }
                bean.prototyper(false);
                return buildModelAndView(req, res, bean, Editor.PATH);
            } catch (Exception ex) {
                bean.title("Error");
                bean.message(ex.getMessage());
                bean.stack(ex.getStackTrace());
                return buildModelAndView(bean, "/error");
            }
        }, engine);
    }
}
