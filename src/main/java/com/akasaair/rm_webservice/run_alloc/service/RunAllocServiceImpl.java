package com.akasaair.rm_webservice.run_alloc.service;

import com.akasaair.rm_webservice.common.config.RmAllocDao;
import com.akasaair.rm_webservice.common.exceptions.DataMismatchedException;
import com.akasaair.rm_webservice.common.helper.HttpHelper;
import com.akasaair.rm_webservice.run_alloc.dto.RunAllocDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.akasaair.rm_webservice.common.Constants.*;

@Service
@Slf4j
public class RunAllocServiceImpl implements RunAllocService {

    @Autowired
    RmAllocDao rmAllocDao;
    @Autowired
    HttpHelper httpHelper;
    @Autowired
    ObjectMapper objectMapper;

    @Override
    public List<RunAllocDto> getRunSummary(String startDate, String endDate, String dtd, String userName, String type) {
        List<RunAllocDto> runDtoList = new ArrayList<>();
        List<Map<String, Object>> val = rmAllocDao.findRunSummaryRecords(startDate, endDate, dtd, userName, type);
        if (val != null) {
            for (Map<String, Object> mapVal : val) {
                runDtoList.add(
                        RunAllocDto.builder()
                                .runId(mapVal.get("run_id").toString())
                                .type(mapVal.get("type").toString())
                                .dtd(mapVal.get("dtd").toString())
                                .count(mapVal.get("totalAllocationCount").toString())
                                .start_time(mapVal.get("start_time").toString())
                                .end_time(mapVal.get("end_time").toString())
                                .user(mapVal.get("user").toString())
                                .runTime(mapVal.get("runTime").toString())
                                .curvesId(mapVal.get("Id_Curves").toString())
                                .marketListId(mapVal.get("Id_marketList").toString())
                                .qpFaresId(mapVal.get("Id_QpFares").toString())
                                .strategyId(mapVal.get("Id_Strategy").toString())
                                .tableName(mapVal.get("market_list_name").toString())
                                .allocationPer(mapVal.get("allocPer").toString())
                                .updatePer(mapVal.get("updatePer").toString())
                                .build()
                );
            }
        }
        return runDtoList;
    }

    @Async
    @Override
    public void runAdhocMarketList(int startInterval, int endInterval, String name) throws DataMismatchedException {
        try {
            ObjectNode adhocRunJson = objectMapper.createObjectNode();
            adhocRunJson.put("start_interval", startInterval);
            adhocRunJson.put("end_interval", endInterval);
            adhocRunJson.put("market_list_table_name", MARKET_LIST_ADHOC_TABLE_NAME + UNDERSCORE + name);
            adhocRunJson.put("username", name);
            httpHelper.makeLambdaAPICall(adhocRunJson);

        } catch (Exception e) {
            throw new DataMismatchedException(Collections.singletonList(ADHOC_RUN_START_ERROR_MESSAGE));
        }
    }

    @Override
    public List<Map<String, Object>> getOutputValues(String startDate, String endDate, String runId) {
        return rmAllocDao.getRunSummaryOutput(startDate, endDate, runId);
    }
}