package com.araguacaima.open_archi.web;

import com.araguacaima.open_archi.web.common.Commons;
import spark.RouteGroup;
import spark.route.HttpMethod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.araguacaima.open_archi.web.Server.engine;
import static com.araguacaima.open_archi.web.common.Commons.*;
import static com.araguacaima.open_archi.web.common.Security.setCORS;
import static java.net.HttpURLConnection.HTTP_OK;
import static spark.Spark.get;
import static spark.Spark.options;

public class Samples implements RouteGroup {

    public static final String PATH = "/samples";

    public static Collection<ExampleData> getExamples() {
        Collection<ExampleData> result = new ArrayList<>();
        result.add(new ExampleData(Server.basePath + "/diagrams/checkBoxes.html", "Features (checkbox)"));
        result.add(new ExampleData(Server.basePath + "/diagrams/columnResizing.html", "Adjusting sizes"));
        result.add(new ExampleData(Server.basePath + "/diagrams/comments.html", "Comments"));
        result.add(new ExampleData(Server.basePath + "/diagrams/dragCreating.html", "Agile origination"));
        result.add(new ExampleData(Server.basePath + "/diagrams/draggableLink.html", "Constraints"));
        result.add(new ExampleData(Server.basePath + "/diagrams/entityRelationship.html", "Entity Relationship"));
        result.add(new ExampleData(Server.basePath + "/diagrams/flowchart.html", "Workflow"));
        result.add(new ExampleData(Server.basePath + "/diagrams/gantt.html", "Gantt Diagram"));
        result.add(new ExampleData(Server.basePath + "/diagrams/grouping.html", "Expander"));
        result.add(new ExampleData(Server.basePath + "/diagrams/regrouping.html", "Re-grouping"));
        result.add(new ExampleData(Server.basePath + "/diagrams/guidedDragging.html", "Visual Guides"));
        result.add(new ExampleData(Server.basePath + "/diagrams/icons.html", "SVG Icons"));
        result.add(new ExampleData(Server.basePath + "/diagrams/kanban.html", "Kanban Board"));
        result.add(new ExampleData(Server.basePath + "/diagrams/logicCircuit.html", "Flow and Sequence 1"));
        result.add(new ExampleData(Server.basePath + "/diagrams/mindMap.html", "Strategic Maps"));
        result.add(new ExampleData(Server.basePath + "/diagrams/navigation.html", "Following the Flow"));
        result.add(new ExampleData(Server.basePath + "/diagrams/orgChartStatic.html", "Zooming"));
        result.add(new ExampleData(Server.basePath + "/diagrams/records.html", "Features Mapping"));
        result.add(new ExampleData(Server.basePath + "/diagrams/sequenceDiagram.html", "UML - Sequence"));
        result.add(new ExampleData(Server.basePath + "/diagrams/shopFloorMonitor.html", "Flow and Sequence 2"));
        result.add(new ExampleData(Server.basePath + "/diagrams/swimBands.html", "Release Planning"));
        result.add(new ExampleData(Server.basePath + "/diagrams/swimLanes.html", "Process Diagram"));
        result.add(new ExampleData(Server.basePath + "/diagrams/umlClass.html", "UML - Classes"));
        result.add(new ExampleData(Server.basePath + "/diagrams/updateDemo.html", "Realtime Update"));
        return result;
    }

    @Override
    public void addRoutes() {
        List<String> steps = new ArrayList<>();
        BeanBuilder bean = new BeanBuilder()
                .model(new Object())
                .source("basic")
                .mainTitle("Basic editor")
                .caption("Reading from OpenArchi!")
                .steps(steps);
        get("/basic", buildRoute(bean, "editor"), engine);

        get("/:page", (request, response) -> {
            try {
                String page = request.params(":page");
                String content = Commons.renderContent("public/diagrams/" + page);
                response.status(HTTP_OK);
                response.type(HTML_CONTENT_TYPE);
                return content;
            } catch (Exception ex) {
                return throwError(response, ex);
            }
        });
    }


}
