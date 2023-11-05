package com.akasaair.rm_webservice.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Constants {
    public static final String CONST_COLAN = ":";
    public static final String PERCENTAGE_SIGN = "%";
    public static final String UNDERSCORE = "_";
    public static final String SPACE = " ";
    public static final String COMMA = ",";
    public static final String ZERO = "0";
    public static final DateTimeFormatter DATE_OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    public static final DateTimeFormatter DATE_TIME_OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter TIME_OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("[HH:mm][H:mm]");
    public static final String DATE_TIME_FORMAT_WITH_DATE = "EEE, dd MMM yyyy HH:mm:ss";
    public static final String STRATEGY_FLAG_0 = "0";
    public static final String STRATEGY_FLAG_1 = "1";
    public static final char DIGIT_1 = '1';
    public static final char DIGIT_9 = '9';
    public static Map<String, String> STRATEGY_HEADER_VALUES = new LinkedHashMap<>();
    public static Map<String, String> RM_TABLE_NAMES = new LinkedHashMap<>();
    public static final int OFFSET_START_VALUE = -10;
    public static final int OFFSET_END_VALUE = 10;
    public static final int NUMBER_OF_DECISIONS = 3;
    public static final int NDO_SIZE = 366;
    public static final double LF_VALUE = 1.05;
    public static final int CURVES_FETCH_LIMIT = 366;
    public static final int DAYS_TO_ENTER_INTO_MARKET_LIST = 30;

    public static final List<String> ALLOCATION_SUMMARY_SUCCESS_LABELS = List.of(
            "Matched and allocated",
            "Matched and allocated - Backstop Hit",
            "Unable to Match Mkt - B2C allocation at extreme value",
            "Unable to Match Mkt - B2B allocation at extreme value",
            "Not Enough Levels available - taking the Max/Min Available"
    );

    public static final List<String> LAMBDA_SCHEDULER_RULES = List.of(
            "rm-day-schedule-rule-1",
            "rm-day-schedule-rule-2",
            "rm-day-schedule-rule-3",
            "rm-day-schedule-rule-4",
            "rm-day-schedule-rule-5"
    );

    public static final String INDIAN_REGION = "Asia/Kolkata";
    public static final String IST = "IST";
    public static final String START_INTERVAL = "start_interval";
    public static final String END_INTERVAL = "end_interval";
    public static final String BODY = "body";
    public static final String PARAMETER_RBD_VALUES = "RBD_VALUES";
    public static final String PERMISSION_MARKET_LIST_TILL_30_DAYS = "update_market_list_till_30_days";
    public static final String PERMISSION_MARKET_LIST_AFTER_30_DAYS = "update_market_list_after_30_days";


    //CSV Header File Index
    public static final int ROW_START_COUNT = 1;
    public static final int CSV_HEADER_ORIGIN = 0;
    public static final int CSV_HEADER_DESTINATION = 1;
    public static final int CSV_HEADER_FLIGHT_NO = 2;
    public static final int CSV_HEADER_PER_TYPE = 3;
    public static final int CSV_HEADER_START_DATE = 4;
    public static final int CSV_HEADER_END_DATE = 5;
    public static final int CSV_HEADER_DAY_OF_WEEK = 6;
    public static final int CSV_HEADER_STRATEGY_FLAG = 7;
    public static final int CSV_HEADER_START_TIME = 8;
    public static final int CSV_HEADER_END_TIME = 9;
    public static final int CSV_HEADER_CURVE_ID = 10;
    public static final int CSV_HEADER_CARR_EXCLUSION = 11;
    public static final int CSV_HEADER_FARE_ANCHOR = 12;
    public static final int CSV_HEADER_FARE_OFFSET = 13;
    public static final int CSV_HEADER_FIRST_ALLOCATION = 14;
    public static final int CSV_HEADER_OTHER_ALLOCATION = 15;
    public static final int CSV_HEADER_SKIPPING_FACTOR = 16;
    public static final int CSV_HEADER_B2B_BACKSTOP = 17;
    public static final int CSV_HEADER_B2C_BACKSTOP = 18;
    public static final int CSV_HEADER_B2B_FACTOR = 19;
    public static final int CSV_HEADER_ANALYST_NAME = 20;

    //CSV Headers
    public static final String CSV_HEADER_TIME = "Time";
    public static final String CSV_HEADER_CRITERIA = "Criteria";
    public static final String CSV_HEADER_OFFSET = "Offset";

    //Table Names
    public static final String TIME_RANGE_TABLE = "time_ranges";
    public static final String CRITERIA_TABLE = "criteria";
    public static final String NDO_BANDS_TABLE = "ndo_bands";
    public static final String DPLF_BANDS_TABLE = "dplf_bands";
    public static final String CURVES_TABLE_NAME = "Curves";
    public static final String STRATEGY_TABLE_NAME = "d1_d2_strategies";
    public static final String MARKET_LIST_TABLE_NAME = "market_list";
    public static final String MARKET_LIST_ADHOC_TABLE_NAME = "market_list_adhoc";
    public static final String AIRPORT_RANGE_TABLE_NAME = "allocation_acceptable_range_d1";
    public static final String QP_FARES_TABLE_NAME = "Fares";
    public static final String PLF_THRESHOLD_TABLE_NAME = "config_pfl_threshold";
    public static final String PARAMETERS_TABLE_NAME = "rm_parameter_values";
    public static final String USERS_TABLE_NAME = "config_users";
    public static final String STRATEGY_INPUT_NAME =  "strategy_input";
    public static final String RUN_SUMMARY_NAME =  "run_summary";
    //Column Names
    public static final String TIME_RANGE_COLUMN_NAME = "time_range";
    public static final String CRITERIA_COLUMN_NAME = "criteria";
    public static final String STRATEGY_COLUMN_NAME = "strategy";
    public static final String CURVES_COLUMN_NAME = "CurveID";
    public static final String NDO_BAND_COLUMN_NAME = "ndo_band";
    public static final String DPLF_BAND_COLUMN_NAME = "dplf_band";
    public static final String UUID_COLUMN_NAME = "UUID";
    public static final String RBD_COLUMN_NAME = "RBD";
    public static final String NDO_COLUMN_NAME = "NDO";
    public static final String LF_COLUMN_NAME = "LF";
    public static final String COUNT_COLUMN_NAME = "count";
    public static final String PARAMETER_VALUE_COLUMN_NAME = "parameterValue";
    public static final String USERNAME_COLUMN_NAME = "userName";
    public static final String SECTOR_COLUMN_NAME = "SECTOR";

    //Error Messages
    public static final String INVALID_DATA_ERROR_MESSAGE = "Invalid Data has been inserted";
    public static final String ALREADY_IN_PROGRESS_ERROR_MESSAGE = "Upload for this table is in progress!";
    public static final String SUCCESS_MESSAGE = "Values uploaded in DB";
    public static final String INVALID_NUMBER_OF_ROW_COLUMN_ERROR_MESSAGE = "Invalid Number of Rows or columns";
    public static final String DUPLICATE_RECORD_ERROR_MESSAGE = "Duplicate Record";
    public static final String SUCCESSFULLY_STARTED = "Adhoc Run has successfully started";
    public static final String INVALID_TABLE_NAME_ERROR_MESSAGE = "Invalid table Name";
    public static final String INVALID_CURVE_ID_ERROR_MESSAGE = "Invalid Curve Id shared";
    public static final String ADHOC_RUN_START_ERROR_MESSAGE = "Error in starting Adhoc Run";
    public static final String INVALID_ID_ERROR_MESSAGE = "Invalid Input Id";

    static {
        STRATEGY_HEADER_VALUES.put("Time", TIME_RANGE_COLUMN_NAME);
        STRATEGY_HEADER_VALUES.put("Criteria", CRITERIA_COLUMN_NAME);
        STRATEGY_HEADER_VALUES.put("Offset", "offset");
        STRATEGY_HEADER_VALUES.put("Strategy", STRATEGY_COLUMN_NAME);
        STRATEGY_HEADER_VALUES.put("NdoBand", NDO_BAND_COLUMN_NAME);
        STRATEGY_HEADER_VALUES.put("DplfBand", DPLF_BAND_COLUMN_NAME);

        RM_TABLE_NAMES.put("strategy", STRATEGY_TABLE_NAME);
        RM_TABLE_NAMES.put("airportRange", AIRPORT_RANGE_TABLE_NAME);
        RM_TABLE_NAMES.put("curves", CURVES_TABLE_NAME);
        RM_TABLE_NAMES.put("dlfBand", DPLF_BANDS_TABLE);
        RM_TABLE_NAMES.put("qpFares", QP_FARES_TABLE_NAME);
        RM_TABLE_NAMES.put("marketList", MARKET_LIST_TABLE_NAME);
        RM_TABLE_NAMES.put("marketListAdhoc", MARKET_LIST_ADHOC_TABLE_NAME);
        RM_TABLE_NAMES.put("ndoBand", NDO_BANDS_TABLE);
        RM_TABLE_NAMES.put("timeRange", TIME_RANGE_TABLE);
        RM_TABLE_NAMES.put("plfThreshold", PLF_THRESHOLD_TABLE_NAME);

    }

    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(DATE_TIME_OUTPUT_FORMATTER);
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

}
