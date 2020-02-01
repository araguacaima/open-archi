package com.araguacaima.open_archi.web;

import com.araguacaima.commons.utils.ClassLoaderUtils;
import com.araguacaima.commons.utils.MapUtils;
import com.araguacaima.open_archi.web.common.Commons;
import com.araguacaima.open_archi.web.routes.Desktop;
import com.araguacaima.orpheusdb.utils.OrpheusDbJPAEntityManagerUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.neuland.jade4j.JadeConfiguration;
import de.neuland.jade4j.template.TemplateLoader;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.ogm.cfg.OgmProperties;
import org.hibernate.ogm.datastore.mongodb.MongoDB;
import org.hibernate.ogm.datastore.mongodb.MongoDBProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.route.HttpMethod;
import spark.template.jade.JadeTemplateEngine;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.araguacaima.open_archi.web.common.Commons.*;
import static com.araguacaima.open_archi.web.common.Security.setCORS;
import static spark.Spark.*;

public class Server {
    public static JadeConfiguration config = new JadeConfiguration();
    public static JadeTemplateEngine engine = new JadeTemplateEngine(config);
    private static TemplateLoader templateLoader = new Loader("web/views");
    public static int assignedPort;
    private static Logger log = LoggerFactory.getLogger(Server.class);
    private static Map<String, String> environment;
    public static String deployedServer;
    public static String basePath;
    private static ProcessBuilder processBuilder = new ProcessBuilder();

    static {
        environment = new HashMap<>(processBuilder.environment());
        URL url = Server.class.getResource("/config/config.properties");
        Properties properties = new Properties();
        String persistenceName = null;
        try {
            properties.load(url.openStream());
            Map<String, String> map = MapUtils.fromProperties(properties);
            if (!map.isEmpty()) {
                String dbUrl = StringUtils.defaultIfBlank(map.get("JDBC_DATABASE_URL"), environment.get("JDBC_DATABASE_URL"));
                String dbUsername = StringUtils.defaultIfBlank(map.get("JDBC_DATABASE_USERNAME"), environment.get("JDBC_DATABASE_USERNAME"));
                String dbPassword = StringUtils.defaultIfBlank(map.get("JDBC_DATABASE_PASSWORD"), environment.get("JDBC_DATABASE_PASSWORD"));
                persistenceName = StringUtils.defaultIfBlank(map.get("PERSISTENCE_NAME"), environment.get("PERSISTENCE_NAME"));
                if (StringUtils.isBlank(dbUrl)) {
                    String db = StringUtils.defaultIfBlank(map.get("MONGODB_DATABASE"), environment.get("MONGODB_DATABASE"));
                    String dbHost = StringUtils.defaultIfBlank(map.get("MONGODB_HOST"), environment.get("MONGODB_HOST"));
                    String dbPort = StringUtils.defaultIfBlank(map.get("MONGODB_PORT"), environment.get("MONGODB_PORT"));
                    dbUsername = StringUtils.defaultIfBlank(map.get("MONGODB_DATABASE_USERNAME"), environment.get("MONGODB_DATABASE_USERNAME"));
                    dbPassword = StringUtils.defaultIfBlank(map.get("MONGODB_DATABASE_PASSWORD"), environment.get("MONGODB_DATABASE_PASSWORD"));
                    dbUrl = StringUtils.defaultIfBlank(map.get("MONGODB_URI"), environment.get("MONGODB_URI"));
                    log.trace("MONGODB_URI=" + dbUrl);
                    map.put(MongoDBProperties.MONGO_DRIVER_SETTINGS_PREFIX + ".sslEnabled", "true");
                    map.put(MongoDBProperties.MONGO_DRIVER_SETTINGS_PREFIX + ".sslInvalidHostNameAllowed", "true");
                    map.put(OgmProperties.GRID_DIALECT, "org.hibernate.ogm.datastore.mongodb.MongoDBDialect");
                    map.put(OgmProperties.DATABASE, db);
                    map.put(OgmProperties.DATASTORE_PROVIDER, MongoDB.DATASTORE_PROVIDER_NAME);
                    map.put(OgmProperties.HOST, dbHost + (StringUtils.isBlank(dbPort) ? "" : (":" + dbPort)));
                    map.put(OgmProperties.CREATE_DATABASE, "true");
                    map.put(OgmProperties.USERNAME, dbUsername);
                    map.put(OgmProperties.PASSWORD, dbPassword);
                } else {
                    log.trace("JDBC_DATABASE_URL=" + dbUrl);
                    map.put("hibernate.connection.provider_class", "org.hibernate.c3p0.internal.C3P0ConnectionProvider");
                    map.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL95Dialect");
                    map.put("hibernate.connection.driver_class", "org.postgresql.Driver");
                    map.put("hibernate.connection.url", dbUrl);
                    map.put("hibernate.connection.username", dbUsername);
                    map.put("hibernate.connection.password", dbPassword);
                    map.put("hibernate.default_schema", "Diagrams");
                    map.put("hibernate.hbm2ddl.auto", "update");
                    map.put("hibernate.show_sql", log.isDebugEnabled() ? "true" : "false");
                    //map.put("hibernate.show_sql", "true");
                }
                map.put("hibernate.archive.autodetection", "class");
//                map.put("hibernate.transaction.jta.platform", "JBossTS");
//                map.put("hibernate.transaction.coordinator_class", "jta");
                map.put("hibernate.flushMode", "FLUSH_AUTO");
                map.put("packagesToScan", "com.araguacaima.open_archi.persistence");
                map.put("hibernate.c3p0.min_size", "8");
                map.put("hibernate.c3p0.max_size", "20");
                map.put("hibernate.c3p0.timeout", "300");
                map.put("hibernate.c3p0.max_statements", "50");
                map.put("hibernate.c3p0.idle_test_period", "3000");
                //map.put("orpheus.db.versionable.packages", "com.araguacaima.open_archi.persistence.diagrams.architectural");
                //map.put("orpheus.db.versionable.classes", "{fill with comma separated fully qualified classes names}");
                environment.putAll(map);
                log.info("Properties taken from config file '" + url.getFile().replace("file:" + File.separator, "") + "'");
            } else {
                log.info("Properties taken from system map...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.trace("Properties: " + environment);
        try {
            OrpheusDbJPAEntityManagerUtils.init(persistenceName, environment);
            config.setTemplateLoader(templateLoader);
            ObjectMapper mapper = jsonUtils.getMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

            assignedPort = getAssignedPort();
            deployedServer = environment.get("DEPLOYED_SERVER");
            basePath = environment.get("BASE_PATH");
            config.setBasePath(basePath);
            config.getSharedVariables().put("basePath", basePath);
            config.setPrettyPrint(true);

            DBUtil.Initialize.process();
        } catch (SystemException | NotSupportedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static int getAssignedPort() {
        if (environment.get("PORT") != null) {
            return Integer.parseInt(environment.get("PORT"));
        }
        return 4567;
    }

    public static void main(String[] args) throws GeneralSecurityException {
        exception(Exception.class, exceptionHandler);
        port(assignedPort);
        //secure("deploy/keystore.jks", "password", null, null);
        log.info("Server listen on port '" + assignedPort + "'");
        staticFiles.location("/web/public");
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Request-Method", "*");
            response.header("Access-Control-Allow-Headers", "*");
            setCORS(request, response);
            String body = request.body();
            if (StringUtils.isNotBlank(body)) {
                body = jsonUtils.toJSON(jsonUtils.fromJSON(body, Object.class));
                if (log.isDebugEnabled()) {
                    log.info("Request for : " + request.requestMethod() + " " + request.uri() + "\n" + body);
                } else {
                    log.info("Request for : " + request.requestMethod() + " " + request.uri());
                }
            } else {
                log.info("Request for : " + request.requestMethod() + " " + request.uri());
            }
        });
        path(OpenArchi.PATH, OpenArchi.root);
        path(Desktop.PATH, new Desktop());
    }
}

