package com.araguacaima.open_archi.web;

import org.pac4j.core.authorization.authorizer.ProfileAuthorizer;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.CommonProfile;

import java.util.List;

public class Authorizer extends ProfileAuthorizer<CommonProfile> {

    @Override
    public boolean isAuthorized(final WebContext context, final List<CommonProfile> profiles) throws HttpAction {
        return isAnyAuthorized(context, profiles);
    }

    @Override
    public boolean isProfileAuthorized(final WebContext context, final CommonProfile profile) {
        return profile != null;
        // return StringUtils.startsWith(profile.getUsername(), "jle");
    }
}
