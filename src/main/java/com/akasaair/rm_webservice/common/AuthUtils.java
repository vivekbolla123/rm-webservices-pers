package com.akasaair.rm_webservice.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

public class AuthUtils {

    public static String getAuthUserName(Authentication authUser) {
        try {
            Jwt user = (Jwt) authUser.getCredentials();
            return user.getClaims().get("name").toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static Object getRoleList(Authentication authUser) {
        try {
            Jwt user = (Jwt) authUser.getCredentials();
            return user.getClaims().get("roles");
        } catch (Exception e) {
            return null;
        }

    }

}
