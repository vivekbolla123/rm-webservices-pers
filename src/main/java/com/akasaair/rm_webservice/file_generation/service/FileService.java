package com.akasaair.rm_webservice.file_generation.service;

import com.akasaair.rm_webservice.common.exceptions.DataMismatchedException;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface FileService {
    String uploadMarketListFile(MultipartFile file, String tableName, String userName, Object role) throws DataMismatchedException, IOException;

    String uploadStrategyGridFile(MultipartFile file, String userName) throws DataMismatchedException, IOException;

    String uploadFile(MultipartFile file, String tableName, String userName) throws DataMismatchedException, IOException;

    ByteArrayInputStream createCSV(String tableName) throws DataMismatchedException;

    List<?> createListOfValues(String tableName, String curveId, String inputId) throws DataMismatchedException;

    List<Map<String, Object>> updateInputFiles(String tableName);

}
