package com.akasaair.rm_webservice.run_alloc.controller;

import com.akasaair.rm_webservice.common.APIResponse;
import com.akasaair.rm_webservice.common.AuthUtils;
import com.akasaair.rm_webservice.common.Constants;
import com.akasaair.rm_webservice.common.exceptions.DataMismatchedException;
import com.akasaair.rm_webservice.run_alloc.dto.RunAllocDto;
import com.akasaair.rm_webservice.run_alloc.service.RunAllocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.util.annotation.Nullable;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/v1/runAlloc", produces = "application/json")
@ControllerAdvice
public class RunAllocController {
    @Autowired
    RunAllocService runAllocService;

    @GetMapping("/run/summary")
    public APIResponse<?> viewValues(@Nullable @RequestParam String startDate,
                                     @Nullable @RequestParam String endDate,
                                     @Nullable @RequestParam String dtd,
                                     @Nullable @RequestParam String userName,
                                     @Nullable @RequestParam String type,
                                     Authentication authUser) {
        List<RunAllocDto> outputValues = runAllocService.getRunSummary(startDate, endDate, dtd, userName, type);
        AuthUtils.getRoleList(authUser);
        return new APIResponse<>(outputValues);
    }

    @GetMapping("/runAdhoc")
    public APIResponse<?> runAdhoc(@RequestParam int startInterval,
                                   @RequestParam int endInterval,
                                   @RequestParam String name,
                                   Authentication authUser) throws DataMismatchedException {
        runAllocService.runAdhocMarketList(startInterval, endInterval, name);
        AuthUtils.getRoleList(authUser);
        return new APIResponse<>(Constants.SUCCESSFULLY_STARTED);
    }

    @GetMapping("/output/summary")
    public APIResponse<?> outputValues(@Nullable @RequestParam String startDate,
                                       @Nullable @RequestParam String endDate,
                                       @Nullable @RequestParam String runId,
                                       Authentication authUser) {
        List<Map<String, Object>> outputValues = runAllocService.getOutputValues(startDate, endDate, runId);
        AuthUtils.getRoleList(authUser);
        return new APIResponse<>(outputValues);
    }
}
