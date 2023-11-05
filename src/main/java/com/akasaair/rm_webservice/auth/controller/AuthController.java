package com.akasaair.rm_webservice.auth.controller;

import com.akasaair.rm_webservice.auth.service.AuthService;
import com.akasaair.rm_webservice.common.APIResponse;
import com.akasaair.rm_webservice.common.AuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = "/v1/auth", produces = "application/json")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping(value = "/config")
    public APIResponse<?> getuserConfigDetails(Authentication authUser) {
        return new APIResponse<>(authService.getuserConfigDetails(AuthUtils.getRoleList(authUser)));
    }
}
