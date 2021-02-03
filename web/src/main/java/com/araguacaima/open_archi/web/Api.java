package com.araguacaima.open_archi.web;

import com.araguacaima.open_archi.web.common.Commons;
import com.araguacaima.open_archi.web.routes.*;
import spark.RouteGroup;

import static com.araguacaima.open_archi.web.Server.engine;
import static com.araguacaima.open_archi.web.common.Commons.buildRoute;
import static spark.Spark.*;

public class Api implements RouteGroup {

    public static final String PATH = "/api";

    private final Diagrams diagrams = new Diagrams();
    private final Models models = new Models();
    private final Catalogs catalogs = new Catalogs();
    private final Consumers consumers = new Consumers();
    private final Palettes palettes = new Palettes();
    private final Capabilities capabilities = new Capabilities();

    @Override
    public void addRoutes() {
        //before(Commons.EMPTY_PATH, Commons.genericFilter);
        get(Commons.EMPTY_PATH, buildRoute(new BeanBuilder().title("Api"), "/apis"), engine);
        //before(Diagrams.PATH, OpenArchi.apiFilter);
        path(Diagrams.PATH, diagrams);
        path(Diagrams.PATH + Commons.SEPARATOR_PATH, diagrams);
        //before(Models.PATH, OpenArchi.apiFilter);
        path(Models.PATH, models);
        path(Models.PATH + Commons.SEPARATOR_PATH, models);
        //before(Catalogs.PATH, OpenArchi.apiFilter);
        path(Catalogs.PATH, catalogs);
        path(Catalogs.PATH + Commons.SEPARATOR_PATH, catalogs);
        //before(Consumers.PATH, OpenArchi.apiFilter);
        path(Consumers.PATH, consumers);
        path(Consumers.PATH + Commons.SEPARATOR_PATH, consumers);
        //before(Palettes.PATH, OpenArchi.apiFilter);
        path(Palettes.PATH, palettes);
        path(Palettes.PATH + Commons.SEPARATOR_PATH, palettes);
        //before(Capabilities.PATH, OpenArchi.apiFilter);
        path(Capabilities.PATH, capabilities);
        path(Capabilities.PATH + Commons.SEPARATOR_PATH, capabilities);
    }

}
