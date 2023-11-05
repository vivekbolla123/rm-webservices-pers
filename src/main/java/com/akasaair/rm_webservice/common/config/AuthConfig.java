package com.akasaair.rm_webservice.common.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@NoArgsConstructor
@AllArgsConstructor
public class AuthConfig {
    @Value("${auth.client-id}")
    private String appClientId;

    @Value("${auth.tenant-id}")
    private String appTenantId;

    @Value("${auth.scope}")
    private String authScope;

    @Value("${auth.grant-type}")
    private String grantType;

    @Value("${auth.login-base-url}")
    private String loginBaseUrl;

    @Value("${auth.token-uri}")
    private String tokenUri;

    public String getLoginUrl() {
        return loginBaseUrl + "/" + appTenantId + "/" + tokenUri;
    }
}
