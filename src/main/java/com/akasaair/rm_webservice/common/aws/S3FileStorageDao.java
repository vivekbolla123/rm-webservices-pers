package com.akasaair.rm_webservice.common.aws;

import com.akasaair.rm_webservice.common.Constants;
import com.akasaair.rm_webservice.common.exceptions.DataMismatchedException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class S3FileStorageDao {
    @Autowired
    private final AmazonS3 s3Client;
    @Autowired
    AwsConfig awsConfig;

    @Autowired
    public S3FileStorageDao(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    public void storeFile(ByteArrayInputStream file, String path, String fileName) throws IOException {
        String filePathName = generatePath(path, fileName);
        File convertedFile = new File(fileName);
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.readAllBytes());
        fos.close();
        PutObjectResult putObjectResponse = s3Client.putObject(
                awsConfig.getBucketName(),
                filePathName,
                convertedFile
        );
        StringUtils.isNotBlank(putObjectResponse.getETag());
    }

    private String generatePath(String path, String fileName) {
        // Example - test/rm-admin/d1_d2_strategies/3b908569-bc73-4745-9bcd-2c1453e218cc_d1_d2_strategies
        return awsConfig.startPath + path + "/" + fileName + "_" + path;
    }

    public List<String> retrieveDataFromS3(String path, String fileName) throws DataMismatchedException {
        String filePathName = generatePath(path, fileName);
        try {
            S3Object s3Object = s3Client.getObject(new GetObjectRequest(awsConfig.getBucketName(), filePathName));
            BufferedReader reader = new BufferedReader(new InputStreamReader(s3Object.getObjectContent()));
            List<String> data = new ArrayList<>();
            data.add(reader.readLine());
            String line;
            while ((line = reader.readLine()) != null) {
                data.add(line);
            }
            return data;
        } catch (AmazonServiceException | IOException e) {
            throw new DataMismatchedException(Collections.singletonList(Constants.INVALID_ID_ERROR_MESSAGE));
        }
    }

}