package com.araguacaima.open_archi.web.common;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.CommonProfile;

import java.util.List;
import java.util.Set;

public class FilterAllRolesAuthorizer<U extends CommonProfile> extends AbstractFilterAllAuthorizer<String, U> {

    public FilterAllRolesAuthorizer() {
    }

    public FilterAllRolesAuthorizer(final String... roles) {
        setElements(roles);
    }

    public FilterAllRolesAuthorizer(final List<String> roles) {
        setElements(roles);
    }

    public FilterAllRolesAuthorizer(final Set<String> roles) {
        setElements(roles);
    }

    @Override
    protected boolean check(final WebContext context, final U profile, final String element) throws HttpAction {
        final Set<String> profileRoles = profile.getRoles();
        return profileRoles.contains(element);
    }
}
