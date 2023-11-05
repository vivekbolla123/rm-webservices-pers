package com.akasaair.rm_webservice.file_generation.service;

import com.akasaair.rm_webservice.common.Validators.CurvesValidator;
import com.akasaair.rm_webservice.common.Validators.FaresValidator;
import com.akasaair.rm_webservice.common.Validators.MarketListValidator;
import com.akasaair.rm_webservice.common.Validators.Validator;
import com.akasaair.rm_webservice.common.aws.S3FileStorageDao;
import com.akasaair.rm_webservice.common.config.RmAllocDao;
import com.akasaair.rm_webservice.common.config.StrategyConfig;
import com.akasaair.rm_webservice.common.exceptions.DataMismatchedException;
import com.akasaair.rm_webservice.file_generation.dto.FileUploadResponse;
import com.akasaair.rm_webservice.file_generation.dto.MarketListEntity;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.ICSVParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

import static com.akasaair.rm_webservice.common.Constants.*;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Autowired
    RmAllocDao rmAllocDao;
    @Autowired
    StrategyConfig strategyConfig;
    @Autowired
    S3FileStorageDao AWSFileStorageDao;

    @Override
    public String uploadMarketListFile(MultipartFile file, String tableName, String userName, Object role) throws DataMismatchedException, IOException {
        boolean flag = true;
        try {
            List<String> permissions = rmAllocDao.getRoleAccess(role);
            if (tableName.startsWith(MARKET_LIST_ADHOC_TABLE_NAME)) {
                rmAllocDao.createTable(tableName, MARKET_LIST_TABLE_NAME);
            }
            if (rmAllocDao.getCurrentInputStatus(tableName) != 0) {
                flag = false;
                throw new DataMismatchedException(Collections.singletonList(ALREADY_IN_PROGRESS_ERROR_MESSAGE));
            }
            String uuid = generateUUID();
            rmAllocDao.insertAuditStatement(tableName, getCurrentDateTime(), userName, uuid);
            rmAllocDao.insertInputStatusTable(tableName);
            List<FileUploadResponse> fileUploadResponseList = new ArrayList<>();
            List<MarketListEntity> marketListEntityList = new ArrayList<>();
            int rowCount = ROW_START_COUNT;

            List<List<String>> csvData = getDataFromCSVFile(file);

            for (int i = 1; i < csvData.size(); i++) {
                String uuidForEntries = generateUUID();
                validationForMarketListFile(fileUploadResponseList, csvData.get(i), rowCount, tableName, permissions);
                createMarketListEntity(csvData.get(i), marketListEntityList, uuidForEntries);
                rowCount++;
            }

            if (!fileUploadResponseList.isEmpty()) {
                rmAllocDao.updateInputStatus(tableName);
                throw new DataMismatchedException(fileUploadResponseList);
            }
            if (tableName.startsWith(MARKET_LIST_ADHOC_TABLE_NAME)) {
                rmAllocDao.deleteValues(Collections.singleton(ZERO), tableName, ZERO);
            } else {
                deleteRecords(marketListEntityList, tableName);
            }
            upsertMarketListEntity(marketListEntityList, tableName);
            AWSFileStorageDao.storeFile(createCSV(tableName), tableName, uuid);
            rmAllocDao.updateAuditStatement(tableName, getCurrentDateTime(), uuid);
            rmAllocDao.updateInputStatus(tableName);
        } finally {
            if (flag)
                rmAllocDao.updateInputStatus(tableName);
        }
        return SUCCESS_MESSAGE;
    }

    @Override
    public String uploadStrategyGridFile(MultipartFile file, String userName) throws DataMismatchedException, IOException {
        if (rmAllocDao.getCurrentInputStatus(STRATEGY_TABLE_NAME) != 0) {
            throw new DataMismatchedException(Collections.singletonList(ALREADY_IN_PROGRESS_ERROR_MESSAGE));
        }
        String uuid = generateUUID();
        try {
            rmAllocDao.insertAuditStatement(STRATEGY_TABLE_NAME, getCurrentDateTime(), userName, uuid);
            rmAllocDao.insertInputStatusTable(STRATEGY_TABLE_NAME);
            List<List<String>> csvData = getDataFromCSVFile(file);
            Map<String, Map<String, String>> strategyMap = getStrategyGrid(csvData, STRATEGY_HEADER_VALUES);
            Set<String> strategy = getUniqueStrategies(strategyMap);
            rmAllocDao.deleteValues(strategy, STRATEGY_TABLE_NAME, STRATEGY_COLUMN_NAME);
            List<String> headers = Arrays.asList(rmAllocDao.getColumnHeaders(STRATEGY_INPUT_NAME).split(COMMA));
            rmAllocDao.insertStatement(strategyMap.values().stream().toList(), headers, STRATEGY_TABLE_NAME);
            AWSFileStorageDao.storeFile(createCSV(STRATEGY_TABLE_NAME), STRATEGY_TABLE_NAME, uuid);
            rmAllocDao.updateAuditStatement(STRATEGY_TABLE_NAME, getCurrentDateTime(), uuid);
            rmAllocDao.updateInputStatus(STRATEGY_TABLE_NAME);
        } finally {
            rmAllocDao.updateInputStatus(STRATEGY_TABLE_NAME);
        }
        return SUCCESS_MESSAGE;
    }

    @Override
    public String uploadFile(MultipartFile file, String tableName, String userName) throws DataMismatchedException, IOException {
        String uuid = generateUUID();
        if (rmAllocDao.getCurrentInputStatus(tableName) != 0) {
            throw new DataMismatchedException(Collections.singletonList(ALREADY_IN_PROGRESS_ERROR_MESSAGE));
        }
        try {
            String tempTableName = tableName + "_temp";
            List<String> headers = Arrays.asList(rmAllocDao.getColumnHeaders(tableName).split(COMMA));
            rmAllocDao.insertAuditStatement(tableName, getCurrentDateTime(), userName, uuid);
            rmAllocDao.createTable(tempTableName, tableName);
            List<List<String>> csvData = getDataFromCSVFile(file);
            List<Map<String, String>> data = createListOfMap(csvData, headers);
            Validator validate = getValidator(tableName);
            String val = null;
            if (tableName.equalsIgnoreCase(QP_FARES_TABLE_NAME)) {
                val = rmAllocDao.searchResults(PARAMETER_RBD_VALUES);
            }
            if (validate != null && !validate.isValid(data, val)) {
                rmAllocDao.updateInputStatus(tableName);
                throw new DataMismatchedException(Collections.singletonList(INVALID_DATA_ERROR_MESSAGE));
            }
            rmAllocDao.insertStatement(data, headers, tempTableName);
            rmAllocDao.insertInputStatusTable(tableName);
            if (tableName.equalsIgnoreCase(CURVES_TABLE_NAME)) {
                Set<String> curves = getUniqueValues(data, CURVES_COLUMN_NAME);
                rmAllocDao.deleteValues(curves, tableName, CURVES_COLUMN_NAME);
            } else if (tableName.equalsIgnoreCase(QP_FARES_TABLE_NAME)) {
                Set<String> fares = getUniqueValues(data, SECTOR_COLUMN_NAME);
                rmAllocDao.deleteValues(fares, tableName, SECTOR_COLUMN_NAME);
            } else {
                rmAllocDao.deleteValues(Collections.singleton(ZERO), tableName, ZERO);
            }
            rmAllocDao.copyFromTempToMainTable(tableName, tempTableName);
            rmAllocDao.updateInputStatus(tableName);
            AWSFileStorageDao.storeFile(createCSV(tableName), tableName, uuid);
            rmAllocDao.updateAuditStatement(tableName, getCurrentDateTime(), uuid);
            rmAllocDao.deleteTable(tempTableName);
        } finally {
            rmAllocDao.updateInputStatus(tableName);
        }
        return SUCCESS_MESSAGE;
    }

    private static Validator getValidator(String tableName) {
        Validator validate = null;
        if (tableName.equalsIgnoreCase(CURVES_TABLE_NAME)) {
            validate = new CurvesValidator();
        }
        if (tableName.equalsIgnoreCase(QP_FARES_TABLE_NAME)) {
            validate = new FaresValidator();
        }
        return validate;
    }

    private List<Map<String, String>> createListOfMap(List<List<String>> csvData, List<String> headers) {
        csvData.remove(0);
        List<Map<String, String>> data = new ArrayList<>();
        for (List<String> singleCsvData : csvData) {
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < headers.size(); i++) {
                map.put(headers.get(i), singleCsvData.get(i));
            }
            data.add(map);
        }
        return data;
    }

    private Set<String> getUniqueValues(List<Map<String, String>> data, String columnName) {
        Set<String> uniqueValues = new HashSet<>();
        for (Map<String, String> singleData : data) {
            uniqueValues.add(singleData.get(columnName));
        }
        return uniqueValues;
    }

    @Override
    public ByteArrayInputStream createCSV(String tableName) throws DataMismatchedException {
        if (rmAllocDao.findIfTableExists(tableName) == 0) {
            throw new DataMismatchedException(Collections.singletonList(INVALID_TABLE_NAME_ERROR_MESSAGE));
        }
        String tableNameForHeader = tableName;
        if (tableNameForHeader.startsWith(MARKET_LIST_ADHOC_TABLE_NAME))
            tableNameForHeader = MARKET_LIST_TABLE_NAME;
        List<String> headers = Arrays.asList(rmAllocDao.getColumnHeaders(tableNameForHeader).split(COMMA));
        List<Map<String, Object>> values = rmAllocDao.findAllRecordsFromTable(tableName, headers);
        if (tableName.equalsIgnoreCase(STRATEGY_TABLE_NAME)) {
            values = rmAllocDao.getStrategyGrid();
        }
        return convertToCSV(values, headers);
    }

    @Override
    public List<?> createListOfValues(String tableName, String curveId, String inputId) throws DataMismatchedException {
        String tableNameForHeader = tableName;
        if (tableNameForHeader.startsWith(MARKET_LIST_ADHOC_TABLE_NAME))
            tableNameForHeader = MARKET_LIST_TABLE_NAME;
        List<String> headers = Arrays.asList(rmAllocDao.getColumnHeaders(tableNameForHeader).split(COMMA));
        if (inputId != null) {
            List<String> data = AWSFileStorageDao.retrieveDataFromS3(tableName, inputId);
            if(data.isEmpty()){
                throw new DataMismatchedException(Collections.singletonList(INVALID_ID_ERROR_MESSAGE));
            }
            return parseListToCreateListMap(data);
        }
        if (rmAllocDao.findIfTableExists(tableName) == 0) {
            throw new DataMismatchedException(Collections.singletonList(INVALID_TABLE_NAME_ERROR_MESSAGE));
        }
        if (tableName.equalsIgnoreCase(CURVES_TABLE_NAME)) {
            if (curveId == null) {
                return rmAllocDao.findAllRecordsFromTable(tableName, headers, CURVES_FETCH_LIMIT);
            } else {
                List<Map<String, Object>> curveData = rmAllocDao.findCurveFilterData(tableName, headers, curveId);
                if (curveData.isEmpty()) {
                    throw new DataMismatchedException(Collections.singletonList(INVALID_CURVE_ID_ERROR_MESSAGE));
                }
                return curveData;
            }
        }
        if (tableName.equalsIgnoreCase(STRATEGY_TABLE_NAME)) {
            return getFinalStrategyGrid();
        }
        return rmAllocDao.findAllRecordsFromTable(tableName, headers);
    }

    private List<Map<String, List<Map<String, Object>>>> getFinalStrategyGrid() {
        List<Map<String, Object>> dplfBands = rmAllocDao.findAllRecordsFromTable("dplf_bands", Collections.singletonList("dplf_band,start,end"), 0);
        List<Map<String, Object>> ndoBands = rmAllocDao.findAllRecordsFromTable("ndo_bands", Collections.singletonList("ndo_band,start,end"), 0);
        List<Map<String, Object>> strategyGrid = rmAllocDao.getStrategyGrid();

        Map<String, Object> dplf = new HashMap<>();
        dplfBands.forEach(map -> {
            int dplf_band = (int) map.get("dplf_band");
            int start = (int) map.get("start");
            int end = (int) map.get("end");
            dplf.put(Integer.toString(dplf_band), start + " to " + end);
        });

        Map<String, Object> ndo = new HashMap<>();
        ndoBands.forEach(map -> {
            int dplf_band = (int) map.get("ndo_band");
            int start = (int) map.get("start");
            int end = (int) map.get("end");
            ndo.put(Integer.toString(dplf_band), start + " to " + end);
        });

        Map<String, List<Map<String, Object>>> mapOutput = new HashMap<>();
        mapOutput.put("Strategy", strategyGrid);
        mapOutput.put("dplfBands", Collections.singletonList(dplf));
        mapOutput.put("ndoBands", Collections.singletonList(ndo));

        return Collections.singletonList(mapOutput);
    }


    @Override
    public List<Map<String, Object>> updateInputFiles(String tableName) {
        return rmAllocDao.getInputFileQueue(tableName);
    }

    private List<Map<String, Object>> parseListToCreateListMap(List<String> csvDataList) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        String[] headers = csvDataList.get(0).split(String.valueOf(ICSVParser.DEFAULT_SEPARATOR));
        for (int i = 1; i < csvDataList.size(); i++) {
            String[] values = csvDataList.get(i).split(String.valueOf(ICSVParser.DEFAULT_SEPARATOR));
            Map<String, Object> dataMap = new HashMap<>();
            for (int j = 0; j < headers.length; j++) {
                dataMap.put(headers[j].replace("\"", ""), values[j].replace("\"", ""));
            }
            dataList.add(dataMap);
        }
        return dataList;
    }

    public static ByteArrayInputStream convertToCSV(List<Map<String, Object>> values, List<String> columnHeader) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVWriter csvWriter = new CSVWriter(new PrintWriter(out))) {
            csvWriter.writeNext(columnHeader.toArray(new String[0]));
            for (Map<String, Object> value : values) {
                List<String> row = new ArrayList<>();
                columnHeader.forEach(header -> {
                    if (value.get(header) != null) {
                        row.add(value.get(header).toString());
                    } else {
                        row.add("");
                    }
                });
                String[] stringArray = row.toArray(new String[0]);
                List<String[]> listOfStringArray = new ArrayList<>();
                listOfStringArray.add(stringArray);
                csvWriter.writeAll(listOfStringArray);
            }
            csvWriter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Set<String> getUniqueStrategies(Map<String, Map<String, String>> strategyMap) {
        Set<String> strategy = new HashSet<>();
        for (Map<String, String> strategies : strategyMap.values()) {
            strategy.add(strategies.get(STRATEGY_COLUMN_NAME));
        }
        return strategy;
    }

    private Map<String, Map<String, String>> getStrategyGrid(List<List<String>> csvData, Map<String, String> headerColumnMap) throws DataMismatchedException {
        Map<String, Map<String, String>> strategyMap = new LinkedHashMap<>();
        int startColumn = 3;
        int strategyGridHeader = 0;
        int decisionGridHeader = 1;
        int ndoBandHeader = 2;
        Set<String> strategyCounter = new HashSet<>();
        int countTotalValues = 0;
        for (int i = 1; i < csvData.size(); i++) {
            List<String> inputRow = csvData.get(i);
            String strategyKey = csvData.get(i).get(strategyGridHeader);
            strategyCounter.add(strategyKey);

            String decision = csvData.get(i).get(decisionGridHeader);
            String ndoBandKey = csvData.get(i).get(ndoBandHeader);
            for (int j = startColumn; j < inputRow.size(); j++) {
                String value = csvData.get(i).get(j);
                String dlfBand = csvData.get(0).get(j);

                String keyVal = strategyKey + ndoBandKey + dlfBand;

                if (!strategyMap.containsKey(keyVal)) {
                    Map<String, String> map = new HashMap<>();
                    map.put(STRATEGY_COLUMN_NAME, strategyKey);
                    map.put(NDO_BAND_COLUMN_NAME, ndoBandKey);
                    map.put(DPLF_BAND_COLUMN_NAME, dlfBand);
                    strategyMap.put(keyVal, map);
                }
                Map<String, String> map = strategyMap.get(keyVal);

                if (!validationForStrategyGrid(decision, value)) {
                    rmAllocDao.updateInputStatus(STRATEGY_TABLE_NAME);
                    throw new DataMismatchedException(Collections.singletonList(INVALID_DATA_ERROR_MESSAGE
                            + " for " + strategyKey + " with decision :" + decision + " having value as " + value));
                }
                map.put(headerColumnMap.get(decision), value);
                countTotalValues++;
            }
        }
        findActualCountBetweenRowsAndColumns(strategyCounter, countTotalValues);
        return strategyMap;
    }

    private void findActualCountBetweenRowsAndColumns(Set<String> strategyCounter, int countTotalValues) throws DataMismatchedException {
        int dplfBand = rmAllocDao.getCountOfEntries(DPLF_BANDS_TABLE);
        int ndoBand = rmAllocDao.getCountOfEntries(NDO_BANDS_TABLE);
        int expectedValueCount = strategyCounter.size() * dplfBand * ndoBand * NUMBER_OF_DECISIONS;
        if (expectedValueCount != countTotalValues) {
            rmAllocDao.updateInputStatus(STRATEGY_TABLE_NAME);
            throw new DataMismatchedException(Collections.singletonList(INVALID_NUMBER_OF_ROW_COLUMN_ERROR_MESSAGE));
        }
    }

    private boolean validationForStrategyGrid(String decision, String value) {
        if (decision.equalsIgnoreCase(CSV_HEADER_TIME)) {
            return strategyConfig.getListOfTimeRange().contains(value);
        }
        if (decision.equalsIgnoreCase(CSV_HEADER_CRITERIA)) {
            return strategyConfig.getListOfCriteria().contains(value);
        }
        if (decision.equalsIgnoreCase(CSV_HEADER_OFFSET)) {
            return strategyConfig.getListOfOffset().contains(Integer.parseInt(value));
        }
        return false;
    }

    private List<List<String>> getDataFromCSVFile(MultipartFile csvFile) {
        List<List<String>> csvData = new ArrayList<>();
        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(csvFile.getInputStream())).build()) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                List<String> row = new ArrayList<>();
                Collections.addAll(row, nextLine);
                csvData.add(row);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return csvData;
    }

    private void validationForMarketListFile(List<FileUploadResponse> fileUploadResponseList, List<String> record, int rowCount, String tableName, List<String> permissions) {
        List<Map<String, Object>> listOfCurves = rmAllocDao.searchDistinctResults(CURVES_COLUMN_NAME, CURVES_TABLE_NAME, record.get(CSV_HEADER_CURVE_ID));
        List<Map<String, Object>> listOfStrategies = rmAllocDao.searchDistinctResults(STRATEGY_COLUMN_NAME, STRATEGY_TABLE_NAME, record.get(CSV_HEADER_FARE_ANCHOR));
        List<Map<String, Object>> listOfCriteria = rmAllocDao.searchDistinctResults(CRITERIA_COLUMN_NAME, CRITERIA_TABLE, record.get(CSV_HEADER_FARE_ANCHOR));
        List<Map<String, Object>> listOfUsers = rmAllocDao.searchDistinctResults(USERNAME_COLUMN_NAME, USERS_TABLE_NAME, record.get(CSV_HEADER_ANALYST_NAME).toUpperCase());

        if (!MarketListValidator.isLengthEqualTo(record.get(CSV_HEADER_ORIGIN), 3)) {
            fileUploadResponseList.add(FileUploadResponse.builder()
                    .line(rowCount)
                    .error("Origin Station is not equal to 3 characters")
                    .marketListEntity(record)
                    .build());
        }
        if (!MarketListValidator.isLengthEqualTo(record.get(CSV_HEADER_DESTINATION), 3)) {
            fileUploadResponseList.add(FileUploadResponse.builder()
                    .line(rowCount)
                    .error("Destination Station is not equal to 3 characters")
                    .marketListEntity(record)
                    .build());
        }
        if (!MarketListValidator.isNumeric(record.get(CSV_HEADER_FLIGHT_NO))) {
            fileUploadResponseList.add(FileUploadResponse.builder()
                    .line(rowCount)
                    .error("Flight Number is not a Number")
                    .marketListEntity(record)
                    .build());
        }
        if (tableName.equalsIgnoreCase(MARKET_LIST_TABLE_NAME)) {
            String startDate = record.get(CSV_HEADER_START_DATE);
            String endDate = record.get(CSV_HEADER_END_DATE);

            LocalDate daysFromToday = LocalDate.now().plusDays(DAYS_TO_ENTER_INTO_MARKET_LIST);

            LocalDate formattedStartDate = LocalDate.parse(startDate, DATE_OUTPUT_FORMATTER);
            LocalDate formattedEndDate = LocalDate.parse(endDate, DATE_OUTPUT_FORMATTER);

            if (permissions.contains(PERMISSION_MARKET_LIST_TILL_30_DAYS)) {
                if (daysFromToday.isBefore(formattedEndDate)) {
                    fileUploadResponseList.add(FileUploadResponse.builder()
                            .line(rowCount)
                            .error("End date is above 30 days")
                            .marketListEntity(record)
                            .build());
                }
                if(formattedStartDate.isAfter(daysFromToday)){
                    fileUploadResponseList.add(FileUploadResponse.builder()
                            .line(rowCount)
                            .error("Start date is above 30 days")
                            .marketListEntity(record)
                            .build());
                }
            }

            if (permissions.contains(PERMISSION_MARKET_LIST_AFTER_30_DAYS)) {
                if (formattedStartDate.isBefore(daysFromToday)) {
                    fileUploadResponseList.add(FileUploadResponse.builder()
                            .line(rowCount)
                            .error("Start date is below 30 days")
                            .marketListEntity(record)
                            .build());
                }
                if (daysFromToday.isAfter(formattedEndDate)) {
                    fileUploadResponseList.add(FileUploadResponse.builder()
                            .line(rowCount)
                            .error("End date is below 30 days")
                            .marketListEntity(record)
                            .build());
                }
            }
        }
        if (!MarketListValidator.isValidDate(record.get(CSV_HEADER_END_DATE))) {
            fileUploadResponseList.add(FileUploadResponse.builder()
                    .line(rowCount)
                    .error("End date is not a valid date")
                    .marketListEntity(record)
                    .build());
        }
        if (!(MarketListValidator.isLengthEqualTo(record.get(CSV_HEADER_DAY_OF_WEEK), 7)
                && MarketListValidator.isNumeric(record.get(CSV_HEADER_DAY_OF_WEEK))
                && MarketListValidator.checkDOWDigits(record.get(CSV_HEADER_DAY_OF_WEEK)))) {
            fileUploadResponseList.add(FileUploadResponse.builder()
                    .line(rowCount)
                    .error("DOW is not 7 digits or has value other than '1' or '9'")
                    .marketListEntity(record)
                    .build());
        }
        if (!MarketListValidator.checkStrategyFlag(record.get(CSV_HEADER_STRATEGY_FLAG))) {
            fileUploadResponseList.add(FileUploadResponse.builder()
                    .line(rowCount)
                    .error("Strategy Flag has value other than '1' or '0'")
                    .marketListEntity(record)
                    .build());
        }
        if (!(MarketListValidator.isValidTime(record.get(CSV_HEADER_START_TIME)))) {
            fileUploadResponseList.add(FileUploadResponse.builder()
                    .line(rowCount)
                    .error("Start Time is not invalid time format")
                    .marketListEntity(record)
                    .build());
        }
        if (!(MarketListValidator.isValidTime(record.get(CSV_HEADER_END_TIME)))) {
            fileUploadResponseList.add(FileUploadResponse.builder()
                    .line(rowCount)
                    .error("End Time is not 4 digits or has an invalid input")
                    .marketListEntity(record)
                    .build());
        }
        if (listOfCurves.isEmpty()) {
            fileUploadResponseList.add(FileUploadResponse.builder()
                    .line(rowCount)
                    .error("Curve Id is not valid")
                    .marketListEntity(record)
                    .build());
        }
        if (!MarketListValidator.isLengthEqualTo(record.get(CSV_HEADER_CARR_EXCLUSION), 2)) {
            fileUploadResponseList.add(FileUploadResponse.builder()
                    .line(rowCount)
                    .error("Carrier Exclusion is not 2 digits")
                    .marketListEntity(record)
                    .build());
        }
        if (record.get(CSV_HEADER_STRATEGY_FLAG).equalsIgnoreCase(STRATEGY_FLAG_1)) {
            if (listOfStrategies.isEmpty()) {
                fileUploadResponseList.add(FileUploadResponse.builder()
                        .line(rowCount)
                        .error("Strategy has an invalid value")
                        .marketListEntity(record)
                        .build());
            }
            if (!record.get(CSV_HEADER_FARE_OFFSET).isBlank()) {
                fileUploadResponseList.add(FileUploadResponse.builder()
                        .line(rowCount)
                        .error("Fare Offset should be empty")
                        .marketListEntity(record)
                        .build());
            }
        } else {
            if (listOfCriteria.isEmpty()) {
                fileUploadResponseList.add(FileUploadResponse.builder()
                        .line(rowCount)
                        .error("Fare Anchor has an invalid value")
                        .marketListEntity(record)
                        .build());
            }
            if (!MarketListValidator.isNumeric(record.get(CSV_HEADER_FARE_OFFSET))) {
                fileUploadResponseList.add(FileUploadResponse.builder()
                        .line(rowCount)
                        .error("Fare Offset is not a number")
                        .marketListEntity(record)
                        .build());
            }
        }

        if (!MarketListValidator.isNumeric(record.get(CSV_HEADER_FIRST_ALLOCATION))) {
            fileUploadResponseList.add(FileUploadResponse.builder()
                    .line(rowCount)
                    .error("First Allocation is not a number")
                    .marketListEntity(record)
                    .build());
        }
        if (!(MarketListValidator.isNumeric(record.get(CSV_HEADER_OTHER_ALLOCATION))
                && MarketListValidator.isPositive(record.get(CSV_HEADER_OTHER_ALLOCATION)))) {
            fileUploadResponseList.add(FileUploadResponse.builder()
                    .line(rowCount)
                    .error("Other Allocation is not a positive number")
                    .marketListEntity(record)
                    .build());
        }
        if (!(MarketListValidator.isNumeric(record.get(CSV_HEADER_B2B_BACKSTOP))
                && MarketListValidator.isPositive(record.get(CSV_HEADER_B2B_BACKSTOP)))) {
            fileUploadResponseList.add(FileUploadResponse.builder()
                    .line(rowCount)
                    .error("B2B Backstop is not a positive number")
                    .marketListEntity(record)
                    .build());
        }
        if (!(MarketListValidator.isNumeric(record.get(CSV_HEADER_B2C_BACKSTOP))
                && MarketListValidator.isPositive(record.get(CSV_HEADER_B2C_BACKSTOP)))) {
            fileUploadResponseList.add(FileUploadResponse.builder()
                    .line(rowCount)
                    .error("B2C Backstop is not a positive number")
                    .marketListEntity(record)
                    .build());
        }
        if (!(MarketListValidator.isDouble(record.get(CSV_HEADER_B2B_FACTOR)))) {
            fileUploadResponseList.add(FileUploadResponse.builder()
                    .line(rowCount)
                    .error("B2B Factor is not a valid float")
                    .marketListEntity(record)
                    .build());
        }
        if (!(MarketListValidator.isNumeric(record.get(CSV_HEADER_SKIPPING_FACTOR)))) {
            fileUploadResponseList.add(FileUploadResponse.builder()
                    .line(rowCount)
                    .error("Skipping Factor is not a number")
                    .marketListEntity(record)
                    .build());
        }
        if (listOfUsers.isEmpty()) {
            fileUploadResponseList.add(FileUploadResponse.builder()
                    .line(rowCount)
                    .error("Analyst Name is not valid")
                    .marketListEntity(record)
                    .build());
        }
    }

    private static void createMarketListEntity(List<String> record, List<MarketListEntity> marketListEntityList, String uuid) {
        marketListEntityList.add(MarketListEntity.builder()
                .uuid(uuid)
                .origin(record.get(CSV_HEADER_ORIGIN))
                .destination(record.get(CSV_HEADER_DESTINATION))
                .flightNo(record.get(CSV_HEADER_FLIGHT_NO))
                .perType(record.get(CSV_HEADER_PER_TYPE))
                .startDate(record.get(CSV_HEADER_START_DATE))
                .endDate(record.get(CSV_HEADER_END_DATE))
                .dayOfWeek(record.get(CSV_HEADER_DAY_OF_WEEK))
                .strategyFlag(record.get(CSV_HEADER_STRATEGY_FLAG))
                .startTime(record.get(CSV_HEADER_START_TIME))
                .endTime(record.get(CSV_HEADER_END_TIME))
                .curveId(record.get(CSV_HEADER_CURVE_ID))
                .carrExclusion(record.get(CSV_HEADER_CARR_EXCLUSION))
                .fareAnchor(record.get(CSV_HEADER_FARE_ANCHOR))
                .fareOffset(record.get(CSV_HEADER_FARE_OFFSET))
                .firstAllocation(record.get(CSV_HEADER_FIRST_ALLOCATION))
                .otherAllocation(record.get(CSV_HEADER_OTHER_ALLOCATION))
                .b2bBackstop(record.get(CSV_HEADER_B2B_BACKSTOP))
                .b2cBackstop(record.get(CSV_HEADER_B2C_BACKSTOP))
                .b2bFactor(record.get(CSV_HEADER_B2B_FACTOR))
                .skippingFactor(record.get(CSV_HEADER_SKIPPING_FACTOR))
                .analystName(record.get(CSV_HEADER_ANALYST_NAME))
                .build());
    }

    private void upsertMarketListEntity(List<MarketListEntity> marketListEntityList, String tableName) {
        for (MarketListEntity marketEntity : marketListEntityList) {
            rmAllocDao.insertIntoMarketList(marketEntity, tableName);
        }
    }

    private void deleteRecords(List<MarketListEntity> marketListEntityList, String tableName) {
        Set<String> uuid = new HashSet<>();
        for (MarketListEntity marketEntity : marketListEntityList) {
            List<Map<String, Object>> listOfResult = rmAllocDao.searchMarketList(marketEntity, tableName);
            for (Map<String, Object> mapOfResult : listOfResult) {
                uuid.add(mapOfResult.get(UUID_COLUMN_NAME).toString());
            }
        }
        rmAllocDao.deleteValues(uuid, tableName, UUID_COLUMN_NAME);
    }

}
