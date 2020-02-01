package com.araguacaima.open_archi.web.routes;

import com.araguacaima.open_archi.persistence.diagrams.meta.Account;
import com.araguacaima.open_archi.persistence.diagrams.meta.Role;
import com.araguacaima.open_archi.web.BeanBuilder;
import com.araguacaima.open_archi.web.filter.SessionFilter;
import com.araguacaima.orpheusdb.utils.OrpheusDbJPAEntityManagerUtils;
import com.araguacaima.open_archi.web.common.Commons;
import org.apache.commons.collections4.IterableUtils;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.sparkjava.SparkWebContext;
import spark.RouteGroup;

import java.util.*;

import static com.araguacaima.open_archi.web.Server.engine;
import static com.araguacaima.open_archi.web.common.Commons.*;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;
import static spark.Spark.*;

public class Admin implements RouteGroup {

    public static final String PATH = "/admin";

    @Override
    public void addRoutes() {
        //before(Commons.EMPTY_PATH, Commons.genericFilter, OpenArchi.adminApiFilter);
        //before("/*", OpenArchi.adminApiFilter);
        ArrayList<String> header = new ArrayList<>(Arrays.asList("Enabled", "Login", "Email"));
        header.addAll(Commons.ALL_ROLES);
        get(Commons.EMPTY_PATH, buildRoute(new BeanBuilder()
                .title("Open-Archi Admin")
                .accounts(OrpheusDbJPAEntityManagerUtils.executeQuery(Account.class, Account.GET_ALL_ACCOUNTS))
                .roles(Commons.ALL_ROLES)
                .header(header), Admin.PATH), engine);
        patch(Commons.EMPTY_PATH, (request, response) -> {
            try {
                Map requestInput = jsonUtils.fromJSON(request.body(), Map.class);
                String email = (String) requestInput.get("email");
                boolean approved = (Boolean) requestInput.get("approved");
                String role = (String) requestInput.get("role");
                Map<String, Object> params = new HashMap<>();
                params.put(Account.PARAM_EMAIL, email);
                Account account = OrpheusDbJPAEntityManagerUtils.findByQuery(Account.class, Account.FIND_BY_EMAIL_AND_ENABLED, params);
                if (account != null) {
                    Set<Role> roles = account.getRoles();
                    Map<String, Object> roleParams = new HashMap<>();
                    roleParams.put(Role.PARAM_NAME, role);
                    Role role_ = OrpheusDbJPAEntityManagerUtils.findByQuery(Role.class, Role.FIND_BY_NAME, roleParams);
                    if (role_ == null) {
                        throw halt("Role does not exists");
                    } else {
                        Role innerRole = IterableUtils.find(roles, role1 -> role1.getName().equals(role));
                        SparkWebContext context = new SparkWebContext(request, response);
                        CommonProfile profile = Commons.findAndFulfillProfile(context);
                        SessionFilter.SessionMap map = SessionFilter.map.get(email);
                        if (map != null) {
                            map.setActive(false);
                        }
                        if (approved) {
                            if (innerRole == null) {
                                roles.add(role_);
                                profile.addRole(role);
                            }
                        } else {
                            if (innerRole != null) {
                                roles.remove(innerRole);
                            }
                        }
                    }
                    OrpheusDbJPAEntityManagerUtils.merge(account);
                    response.status(HTTP_OK);
                    return EMPTY_RESPONSE;
                } else {
                    response.status(HTTP_BAD_REQUEST);
                    return EMPTY_RESPONSE;
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
                response.status(HTTP_BAD_REQUEST);
                response.type(JSON_CONTENT_TYPE);
                return EMPTY_RESPONSE;
            }
        });

    }

}
