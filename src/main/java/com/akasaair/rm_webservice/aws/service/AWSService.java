package com.akasaair.rm_webservice.aws.service;

import java.util.List;
import java.util.Map;

public interface AWSService {
    List<Map<String, Object>> getLambdaScheduler();
}
