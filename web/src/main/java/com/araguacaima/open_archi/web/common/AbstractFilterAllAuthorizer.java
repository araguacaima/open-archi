package com.araguacaima.open_archi.web.common;

import org.pac4j.core.authorization.authorizer.AbstractRequireElementAuthorizer;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.CommonProfile;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class AbstractFilterAllAuthorizer<E, U extends CommonProfile>
        extends AbstractRequireElementAuthorizer<E, U> {

    @Override
    protected boolean isProfileAuthorized(final WebContext context, final U profile) throws HttpAction {
        if (elements == null || elements.isEmpty()) {
            return true;
        }
        for (final E element : elements) {
            boolean check = check(context, profile, element);
            if (!check) {
                Set<E> rejectedScopes = (Set<E>) profile.getAuthenticationAttribute(Commons.REJECTED_SCOPES);
                if (rejectedScopes == null) {
                    rejectedScopes = new LinkedHashSet<>();
                }
                rejectedScopes.add(element);
                profile.addAuthenticationAttribute(Commons.REJECTED_SCOPES, rejectedScopes);
            }
        }
        return true;
    }
}
