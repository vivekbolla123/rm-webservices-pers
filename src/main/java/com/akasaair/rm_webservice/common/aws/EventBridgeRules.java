package com.akasaair.rm_webservice.common.aws;

import com.amazonaws.services.cloudwatchevents.model.DescribeRuleRequest;
import com.amazonaws.services.cloudwatchevents.model.DescribeRuleResult;
import com.amazonaws.services.cloudwatchevents.model.ListTargetsByRuleRequest;
import com.amazonaws.services.cloudwatchevents.model.ListTargetsByRuleResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventBridgeRules {

    @Autowired
    AwsConfig awsConfig;

    public String getInputConstants(String ruleName) {
        ListTargetsByRuleRequest request = new ListTargetsByRuleRequest()
                .withRule(ruleName);
        ListTargetsByRuleResult result = awsConfig.getCredentials().listTargetsByRule(request);
        return result.getTargets().get(0).getInput();
    }

    public String getScheduler(String ruleName) {
        DescribeRuleRequest describeRuleRequest = new DescribeRuleRequest()
                .withName(ruleName);
        DescribeRuleResult describeRuleResult = awsConfig.getCredentials().describeRule(describeRuleRequest);
        return describeRuleResult.getScheduleExpression();
    }
}