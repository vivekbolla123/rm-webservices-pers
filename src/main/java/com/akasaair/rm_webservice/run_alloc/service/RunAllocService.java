package com.akasaair.rm_webservice.run_alloc.service;

import com.akasaair.rm_webservice.common.exceptions.DataMismatchedException;
import com.akasaair.rm_webservice.run_alloc.dto.RunAllocDto;

import java.util.List;
import java.util.Map;

public interface RunAllocService {
    List<RunAllocDto> getRunSummary(String startDate, String endDate, String dtd, String userName, String type);

    void runAdhocMarketList(int startInterval, int endInterval, String name) throws DataMismatchedException;

    List<Map<String, Object>> getOutputValues(String startDate, String endDate, String runId);
}
