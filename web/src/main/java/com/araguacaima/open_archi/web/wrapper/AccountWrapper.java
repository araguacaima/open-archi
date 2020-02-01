package com.araguacaima.open_archi.web.wrapper;

import com.araguacaima.open_archi.persistence.diagrams.meta.Account;
import com.araguacaima.open_archi.persistence.diagrams.meta.Avatar;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oauth.profile.google2.Google2Profile;

public class AccountWrapper {
    public static Account toAccount(UserProfile profile) {
        Account account = null;
        if (profile != null) {
            account = new Account();
            account.setName(((Google2Profile) profile).getFirstName());
            account.setLastname(((Google2Profile) profile).getFamilyName());
            String displayName = ((Google2Profile) profile).getDisplayName();
            account.setLogin(displayName == null ? account.getName() : displayName);
            account.setEmail(((Google2Profile) profile).getEmail());
            String url = profile.getAttribute("image.url").toString();
            Avatar avatar = new Avatar();
            avatar.setUrl(url);
            account.setAvatar(avatar);
        }
        return account;
    }
}
