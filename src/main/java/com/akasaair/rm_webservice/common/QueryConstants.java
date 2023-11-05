package com.akasaair.rm_webservice.common;

import org.springframework.stereotype.Component;

@Component
public class QueryConstants {

    public static String SELECT_MARKET_LIST(String tableName) {
        return "SELECT UUID FROM " + tableName + " where " +
                " Origin = ? and Destin = ? and analystName = ? ";
    }

    public static String INSERT_INTO_MARKET_LIST(String tableName) {
        return "INSERT INTO " + tableName +
                " (Origin, Destin, FlightNumber, PerType, PerStart, PerEnd, DOW, strategyFlag, TimeWindowStart, " +
                "TimeWindowEnd, CurveID, CarrExlusion, fareAnchor, fareOffset, FirstRBDAlloc, OtherRBDAlloc, " +
                "B2BBackstop, B2CBackstop, B2BFactor, SkippingFactor, analystName, UUID) " +
                "VALUES " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ;";
    }

    public static String GET_RUN_AUDIT_VALUES(String startDate, String endDate, String dtd, String userName, String type) {
        return GET_RUN_AUDIT_VALUES(startDate, endDate, dtd, userName, type, 30);
    }

    public static String GET_RUN_AUDIT_VALUES(String startDate, String endDate, String dtd, String userName, String type, int limit) {
        String orderBy = " order by start_time desc ";
        String groupBy = " GROUP BY run_id ";
        String condition = "";

        if (startDate != null && endDate != null) {
            condition += " and date(start_time) >= '" + startDate + "' " + " and date(end_time) <= '" + endDate + "' ";
        }
        if (dtd != null) {
            condition += " and dtd = '" + dtd + "'";
        }
        if (userName != null) {
            condition += " and user = '" + userName + "'";
        }
        if (type != null) {
            condition += " and type = '" + type + "'";
        }

        if (condition.isEmpty()) {
            condition = groupBy + orderBy + " LIMIT " + limit;
        } else {
            condition += groupBy + orderBy;
        }

        return " SELECT run_id, type, dtd, " +
                " ifnull(totalAllocationCount,'') as totalAllocationCount, start_time," +
                " ifnull(end_time,'') as end_time," +
                " user, runTime," +
                " ifnull((successfulAllocationCount/totalAllocationCount)*100,'') AS allocPer," +
                " ifnull((1-failures/total)*100,'') AS updatePer," +
                " Id_marketList, Id_Curves, Id_Strategy, Id_QpFares," +
                " ifnull(market_list_name,'') as market_list_name" +
                " FROM QP_RM_REPORTS.allocation_summary_report WHERE 1=1 " + condition;
    }

    public static String GET_RUN_SUMMARY() {
        return "SELECT HowDetermined,count(*) as count FROM run_summary where RunId = ? group by HowDetermined ";
    }

    public static String GET_RUN_OUTPUT_SUMMARY(String startDate, String endDate, String runId, String headers) {
        return GET_RUN_OUTPUT_SUMMARY(startDate, endDate, runId, headers, 100);
    }

    public static String GET_RUN_OUTPUT_SUMMARY(String startDate, String endDate, String runId, String headers, int limit) {
        String condition = " ORDER BY rs.CreatedAt desc LIMIT " + limit;
        if ((startDate == null || endDate == null) && runId == null) {
            condition = " ORDER BY rs.CreatedAt desc LIMIT " + limit;
        } else if (startDate != null && endDate != null) {
            condition = " ORDER BY rs.CreatedAt desc LIMIT " + limit;
        } else if (runId != null) {
            condition = " WHERE rs.RunId = '" + runId + "' ORDER BY rs.CreatedAt desc ";
        }
        return "SELECT " + headers + " FROM run_summary rs "
                + condition;
    }

    public static String GET_ROLE_PERMISSION_LIST(Object role) {
        return "SELECT " +
                " DISTINCT p.Permission AS permissions " +
                " FROM config_permissions p " +
                " JOIN ( " +
                " SELECT crp.Role, GROUP_CONCAT(permissions.permission ORDER BY permissions.position) AS concatenated_string " +
                " FROM config_role_permission crp " +
                " CROSS JOIN JSON_TABLE( " +
                " JSON_UNQUOTE(JSON_EXTRACT(crp.PermissionID, '$.permissions[*]')), " +
                " '$[*]' COLUMNS (permission INT PATH '$', position FOR ORDINALITY) " +
                " ) AS permissions " +
                " GROUP BY crp.Role " +
                " ) AS crp ON FIND_IN_SET(p.PermissionID, crp.concatenated_string) " +
                " WHERE crp.Role IN (" + role + ") ";
    }

    public static String GET_ROLE_NAME(Object role) {
        return "SELECT JSON_UNQUOTE(JSON_EXTRACT(PermissionID, '$.role')) " +
                "FROM config_role_permission " +
                "WHERE Role IN (" + role + ") " +
                "ORDER BY priority " +
                "LIMIT 1";
    }

    public static String GET_UPDATE_QUEUE_LIST(String tableName) {

        return " SELECT " +
                "    tableName, " +
                "    DATE_FORMAT(DATE(start_time), '%d %M %Y') AS date, " +
                "    TIME_FORMAT(TIME(CONVERT_TZ(start_time, 'UTC', 'ASIA/KOLKATA')), '%H:%m:%s') AS startTime, " +
                "    TIME_FORMAT(TIME(CONVERT_TZ(end_time, 'UTC', 'ASIA/KOLKATA')), '%H:%m:%s') AS endTime, " +
                "    case when end_time is null then 'Failed' else 'Success' end as Status, " +
                "    userName" +
                " FROM" +
                "    file_upload_audit" +
                " WHERE" +
                "    tableName = '" + tableName + "'" +
                " ORDER BY start_time DESC" +
                " LIMIT 10";
    }

    public static String GET_PARAMETER_VALUES(String value) {
        return "SELECT parameterValue FROM rm_parameter_values WHERE parameterKey = '" + value + "'";
    }

    public static String GET_COLUMN_HEADERS(String value) {
        return "SELECT columns FROM config_column_names WHERE tableName = '" + value + "'";
    }

    public static String GET_CURRENT_INPUT_STATUS() {
        return "SELECT * FROM inputs_status where name = ? and is_running = '1'";
    }

    public static String INSERT_FILE_UPLOAD() {
        return "INSERT INTO file_upload_audit (tableName, curr_version, start_time, userName) VALUE (?,?,?,?)";
    }

    public static String DELETE_TABLE(String tableName) {
        return "drop table " + tableName;
    }

    public static String GET_INPUT_STATUS_COUNT() {
        return "SELECT count(*) as count FROM inputs_status where name = ?";
    }

    public static String UPDATE_INPUT_STATUS(int value) {
        return "UPDATE inputs_status SET is_running = b'" + value + "' WHERE name = ? ";
    }

    public static String COPY_DATA_INTO_MAIN_TABLE(String tableName, String tempTableName) {
        return "insert into " + tableName + " select * from " + tempTableName;
    }

    public static String INSERT_INPUT_STATUS() {
        return "INSERT INTO inputs_status (is_running,name) VALUES (b'1',?)";
    }

    public static String UPDATE_FILE_UPLOAD() {
        return "UPDATE file_upload_audit SET end_time = ? WHERE curr_version = ? AND tableName = ?";
    }

    public static String GET_CURRENT_VERSION_COUNT() {
        return "SELECT count(*) as count FROM currentVersion where tableName = ?";
    }

    public static String UPDATE_CURRENT_VERSION() {
        return "UPDATE currentVersion SET curr_version = ? WHERE tableName = ?";
    }

    public static String INSERT_CURRENT_VERSION() {
        return "INSERT INTO currentVersion (curr_version,tableName) VALUES (?,?)";
    }

    public static String GET_STRATEGY_GRID_TIME(int limit) {
        StringBuilder query = new StringBuilder("SELECT " +
                " strategy AS Strategy, " +
                " 'Time' AS Decision, " +
                " case when s.ndo_band = n.ndo_band then n.ndo_band end as Band, ");
        for (int i = 0; i < limit; i++) {
            query.append(" MAX(IF(dplf_band = ").append(i).append(", time_range, NULL)) AS '").append(i).append("' ");
            if (i < limit - 1) {
                query.append(",");
            }
        }
        query.append(" FROM d1_d2_strategies s " + "INNER JOIN ndo_bands n ON n.ndo_band=s.ndo_band " + "GROUP BY strategy, Band ");
        return query.toString();
    }

    public static String GET_STRATEGY_GRID_CRITERIA(int limit) {
        StringBuilder query = new StringBuilder("SELECT " +
                " strategy AS Strategy, " +
                " 'Criteria' AS Decision, " +
                " case when s.ndo_band = n.ndo_band then n.ndo_band end as Band, ");
        for (int i = 0; i < limit; i++) {
            query.append(" MAX(IF(dplf_band = ").append(i).append(", criteria, NULL)) AS '").append(i).append("' ");
            if (i < limit - 1) {
                query.append(",");
            }
        }
        query.append(" FROM d1_d2_strategies s " + "INNER JOIN ndo_bands n ON n.ndo_band=s.ndo_band " + "GROUP BY strategy, Band ");
        return query.toString();
    }

    public static String GET_STRATEGY_GRID_OFFSET(int limit) {
        StringBuilder query = new StringBuilder("SELECT " +
                " strategy AS Strategy, " +
                " 'Offset' AS Decision, " +
                " case when s.ndo_band = n.ndo_band then n.ndo_band end as Band, ");
        for (int i = 0; i < limit; i++) {
            query.append(" MAX(IF(dplf_band = ").append(i).append(", offset, NULL)) AS '").append(i).append("' ");
            if (i < limit - 1) {
                query.append(",");
            }
        }
        query.append(" FROM d1_d2_strategies s " + "INNER JOIN ndo_bands n ON n.ndo_band=s.ndo_band " + "GROUP BY strategy, Band ");
        return query.toString();
    }

    public static String GET_STRATEGY_GRID(int limit) {
        return GET_STRATEGY_GRID_TIME(limit) +
                "UNION ALL " +
                GET_STRATEGY_GRID_CRITERIA(limit) +
                "UNION ALL " +
                GET_STRATEGY_GRID_OFFSET(limit) +
                "ORDER BY Strategy, Decision, Band ";
    }

}
