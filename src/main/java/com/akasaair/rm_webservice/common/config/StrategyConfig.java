package com.akasaair.rm_webservice.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.akasaair.rm_webservice.common.Constants.*;

@Configuration
public class StrategyConfig {
    @Autowired
    RmAllocDao rmAllocDao;

    public List<String> getListOfTimeRange() {
        List<Map<String, Object>> listOfTimeRange = rmAllocDao.findAllRecordsFromTable(TIME_RANGE_TABLE, Collections.singletonList(TIME_RANGE_COLUMN_NAME));
        List<String> timeRangeValues = new ArrayList<>();
        for (Map<String, Object> timeRange : listOfTimeRange) {
            timeRangeValues.add(timeRange.get(TIME_RANGE_COLUMN_NAME).toString());
        }
        return timeRangeValues;
    }

    public List<String> getListOfCriteria() {
        List<Map<String, Object>> listOfCriteria = rmAllocDao.findAllRecordsFromTable(CRITERIA_TABLE, Collections.singletonList(CRITERIA_COLUMN_NAME));
        List<String> criteriaValues = new ArrayList<>();
        for (Map<String, Object> criteria : listOfCriteria) {
            criteriaValues.add(criteria.get(CRITERIA_COLUMN_NAME).toString());
        }
        return criteriaValues;
    }

    public List<Integer> getListOfOffset() {
        return IntStream
                .rangeClosed(OFFSET_START_VALUE, OFFSET_END_VALUE)
                .boxed()
                .sorted()
                .collect(Collectors.toList());
    }


}
