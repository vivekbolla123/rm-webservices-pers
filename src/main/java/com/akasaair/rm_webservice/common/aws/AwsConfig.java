package com.akasaair.rm_webservice.common.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudwatchevents.AmazonCloudWatchEvents;
import com.amazonaws.services.cloudwatchevents.AmazonCloudWatchEventsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class AwsConfig {
    @Value("${spring.cloud.aws.credentials.accessKey}")
    private String accessKey;
    @Value("${spring.cloud.aws.credentials.secretKey}")
    private String secretKey;
    @Value("${spring.cloud.aws.bucketName}")
    private String bucketName;
    @Value("${aws.path}")
    public String startPath;

    @Bean
    public AmazonS3 s3Client() {
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(getAwsCredentials()))
                .withRegion(Regions.AP_SOUTH_1)
                .build();
    }

    @Bean
    public AmazonCloudWatchEvents getCredentials() {
        return AmazonCloudWatchEventsClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(getAwsCredentials()))
                .withRegion(Regions.AP_SOUTH_1)
                .build();
    }

    private AWSCredentials getAwsCredentials() {
        return new BasicAWSCredentials(
                accessKey,
                secretKey
        );
    }
}

