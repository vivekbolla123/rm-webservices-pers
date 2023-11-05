package com.akasaair.rm_webservice.common.config;

import com.azure.spring.cloud.autoconfigure.aad.AadResourceServerWebSecurityConfigurerAdapter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Configuration
    public static class ApiWebSecurityConfigurationAdapter extends AadResourceServerWebSecurityConfigurerAdapter {
        protected void configure(HttpSecurity http) throws Exception {
            super.configure(http);
            http
                    .cors()
                    .configurationSource(request -> {
                        var cors = new CorsConfiguration();
                        cors.setAllowedOrigins(List.of("*"));
                        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                        cors.setAllowedHeaders(List.of("*"));
                        return cors;
                    }).and()
                    .csrf().disable()
                    .authorizeRequests()
                    .antMatchers(
                            "/v1/auth/token"
                            , "/actuator/health"
                            , "/swagger-ui/**"
                            , "/v3/api-docs/**"
                            , "/swagger-ui.html"
                            , "/v1/**"
                    )
                    .permitAll()
                    .anyRequest().authenticated();
        }
    }
}