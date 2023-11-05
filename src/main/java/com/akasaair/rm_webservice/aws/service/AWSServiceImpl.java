package com.akasaair.rm_webservice.aws.service;

import com.akasaair.rm_webservice.common.aws.EventBridgeRules;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.akasaair.rm_webservice.common.Constants.*;

@Service
@Slf4j
public class AWSServiceImpl implements AWSService {

    @Autowired
    EventBridgeRules eventBridgeRules;

    @Override
    public List<Map<String, Object>> getLambdaScheduler() {
        List<Map<String, Object>> scheduler = new ArrayList<>();
        for (String rule : LAMBDA_SCHEDULER_RULES) {
            Map<String, Object> ruleMap = new LinkedHashMap<>();
            ruleMap.put("Rule", rule);

            String inputConstants = eventBridgeRules.getInputConstants(rule);
            List<Integer> intervalPeriod = extractJson(inputConstants);
            ruleMap.put("NDO Start", intervalPeriod.get(0));
            ruleMap.put("NDO End", intervalPeriod.get(1));

            String scheduleExpression = eventBridgeRules.getScheduler(rule);
            ruleMap.put("Cron", scheduleExpression);
            scheduleExpression = "* " + scheduleExpression
                    .replaceAll("cron", "")
                    .replaceAll("\\(", "")
                    .replaceAll("\\)", "");
            String time = getTime(scheduleExpression);
            ruleMap.put("Next Scheduler", time);

            scheduler.add(ruleMap);
        }
        return scheduler;
    }

    private String getTime(String cronExpression) {
        String currentDateTimeSchedule = "";
        try {
            CronExpression cron = new CronExpression(cronExpression);
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT_WITH_DATE);
            sdf.setTimeZone(TimeZone.getTimeZone(INDIAN_REGION));
            Date currentDateTime = new Date();
            currentDateTime = cron.getNextValidTimeAfter(currentDateTime);
            currentDateTimeSchedule = sdf.format(currentDateTime) + SPACE + IST;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return currentDateTimeSchedule;
    }

    private List<Integer> extractJson(String intervalPeriod) {
        List<Integer> interval = new ArrayList<>();
        JSONObject jsonData = new JSONObject(new JSONObject(intervalPeriod).getString(BODY));
        interval.add(jsonData.getInt(START_INTERVAL));
        interval.add(jsonData.getInt(END_INTERVAL));
        return interval;
    }
}