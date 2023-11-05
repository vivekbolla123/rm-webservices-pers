package com.akasaair.rm_webservice.aws.controller;

import com.akasaair.rm_webservice.aws.service.AWSService;
import com.akasaair.rm_webservice.common.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/v1/aws", produces = "application/json")
@ControllerAdvice
public class AWSController {
    @Autowired
    AWSService awsService;

    @GetMapping("/scheduler")
    public ResponseEntity<?> getLambdaScheduler(Authentication authUser) {
        List<Map<String, Object>> outputValues = awsService.getLambdaScheduler();
        AuthUtils.getRoleList(authUser);
        return ResponseEntity.ok().body(outputValues);
    }
}
