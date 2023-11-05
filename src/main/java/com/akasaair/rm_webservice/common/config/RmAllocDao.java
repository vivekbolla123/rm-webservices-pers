package com.akasaair.rm_webservice.common.config;

import com.akasaair.rm_webservice.file_generation.dto.MarketListEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.akasaair.rm_webservice.common.Constants.*;
import static com.akasaair.rm_webservice.common.QueryConstants.*;

@Component
@Slf4j
public class RmAllocDao {

    @Autowired
    @Qualifier("rmAllocJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> searchMarketList(MarketListEntity marketListEntity, String tableName) {
        List<Map<String, Object>> listObj = null;
        try {
            listObj = jdbcTemplate.queryForList(SELECT_MARKET_LIST(tableName),
                    marketListEntity.getOrigin(),
                    marketListEntity.getDestination(),
                    marketListEntity.getAnalystName());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return listObj;
    }

    public List<Map<String, Object>> findAllRecordsFromTable(String tableName, List<String> headers) {
        return findAllRecordsFromTable(tableName, headers, 0);
    }

    public List<Map<String, Object>> getStrategyGrid() {
        int limit = getCountOfEntries(DPLF_BANDS_TABLE);
        List<Map<String, Object>> listObj = null;
        try {
            String query = GET_STRATEGY_GRID(limit);
            listObj = jdbcTemplate.queryForList(query);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return listObj;
    }

    public List<Map<String, Object>> findAllRecordsFromTable(String tableName, List<String> headers, int limit) {
        String limitCondition = "";
        if (limit != 0) {
            limitCondition = " LIMIT " + limit;
        }
        List<Map<String, Object>> listObj = null;
        try {
            StringBuilder headerValues = new StringBuilder();
            for (String header : headers) {
                headerValues.append(header).append(",");
            }
            String query = "SELECT " + headerValues.substring(0, headerValues.length() - 1) + " FROM " + tableName + limitCondition;
            listObj = jdbcTemplate.queryForList(query);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return listObj;
    }

    public List<Map<String, Object>> findCurveFilterData(String tableName, List<String> headers, String curveId) {
        List<Map<String, Object>> listObj = null;
        try {
            StringBuilder headerValues = new StringBuilder();
            for (String header : headers) {
                headerValues.append(header).append(",");
            }
            String query = "SELECT " + headerValues.substring(0, headerValues.length() - 1) + " FROM " + tableName + " WHERE CurveId = '" + curveId + "'";
            listObj = jdbcTemplate.queryForList(query);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return listObj;
    }

    public int findIfTableExists(String tableName) {
        List<Map<String, Object>> listObj = new ArrayList<>();
        try {
            String query = "SHOW TABLES LIKE '" + tableName + "'";
            listObj = jdbcTemplate.queryForList(query);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return listObj.size();
    }

    public List<Map<String, Object>> searchDistinctResults(String columnName, String tableName, String value) {
        List<Map<String, Object>> listObj = null;
        try {
            String query = "SELECT distinct " + columnName + " FROM " + tableName + " WHERE " + columnName + " = ?";
            listObj = jdbcTemplate.queryForList(query, value);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return listObj;
    }

    public String searchResults(String value) {
        String listObj = null;
        try {
            listObj = jdbcTemplate.queryForList(GET_PARAMETER_VALUES(value), String.class).get(0);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return listObj;
    }

    public String getColumnHeaders(String value) {
        String listObj = null;
        try {
            listObj = jdbcTemplate.queryForList(GET_COLUMN_HEADERS(value), String.class).get(0);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return listObj;
    }

    public List<Map<String, Object>> getInputFileQueue(String tableName) {
        List<Map<String, Object>> listObj = null;
        try {
            listObj = jdbcTemplate.queryForList(GET_UPDATE_QUEUE_LIST(tableName));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return listObj;
    }

    @Transactional
    public void insertStatement(List<Map<String, String>> data, List<String> columnHeaders, String tableName) {
        List<Object[]> listOfObject = new ArrayList<>();

        for (Map<String, String> dataMap : data) {
            Object[] entityValues = new Object[columnHeaders.size()];
            for (int i = 0; i < columnHeaders.size(); i++) {
                entityValues[i] = dataMap.get(columnHeaders.get(i));
            }
            listOfObject.add(entityValues);
        }
        StringBuilder columnNames = new StringBuilder();
        StringBuilder values = new StringBuilder();
        for (String name : columnHeaders) {
            columnNames.append(name).append(",");
            values.append("?,");
        }

        String insert = "INSERT INTO " + tableName + " (" + columnNames.substring(0, columnNames.length() - 1) + ") " +
                "VALUES (" + values.substring(0, values.length() - 1) + ")";

        jdbcTemplate.batchUpdate(insert, listOfObject, 1000, (PreparedStatement ps, Object[] product) -> {
            for (int i = 0; i < product.length; i++) {
                ps.setObject(i + 1, product[i]);
            }
        });
    }

    @Transactional
    public void copyFromTempToMainTable(String tableName, String tempTableName) {
        jdbcTemplate.batchUpdate(COPY_DATA_INTO_MAIN_TABLE(tableName, tempTableName));
    }

    public void deleteValues(Set<String> value, String tableName, String columnName) {
        if (!value.isEmpty()) {
            StringBuilder values = new StringBuilder("'");
            for (String name : value) {
                values.append(name).append("','");
            }
            String deleteQuery = "DELETE FROM " + tableName + " WHERE " + columnName + " IN (" + values.substring(0, values.length() - 2) + ")";
            jdbcTemplate.batchUpdate(deleteQuery);
        }
    }

    public void createTable(String tableName, String newTableName) {
        String createTable = "CREATE TABLE if not exists " + tableName + " like " + newTableName;
        jdbcTemplate.batchUpdate(createTable);
    }

    public int getCountOfEntries(String tableName) {
        List<Map<String, Object>> listObj = null;
        try {
            listObj = jdbcTemplate.queryForList("SELECT * FROM " + tableName);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return listObj.size();
    }

    public int getCurrentInputStatus(String tableName) {
        List<Map<String, Object>> listObj = null;
        try {
            listObj = jdbcTemplate.queryForList(GET_CURRENT_INPUT_STATUS(), tableName);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return listObj.size();
    }

    public void insertIntoMarketList(MarketListEntity marketListEntity, String tableName) {
        List<Object[]> listOfObject = new ArrayList<>();
        Object[] entityValues = {
                marketListEntity.getOrigin(),
                marketListEntity.getDestination(),
                marketListEntity.getFlightNo(),
                marketListEntity.getPerType(),
                marketListEntity.getStartDate(),
                marketListEntity.getEndDate(),
                new BigInteger(marketListEntity.getDayOfWeek()),
                marketListEntity.getStrategyFlag(),
                marketListEntity.getStartTime(),
                marketListEntity.getEndTime(),
                marketListEntity.getCurveId(),
                marketListEntity.getCarrExclusion(),
                marketListEntity.getFareAnchor(),
                marketListEntity.getFareOffset(),
                new BigInteger(marketListEntity.getFirstAllocation()),
                new BigInteger(marketListEntity.getOtherAllocation()),
                new BigInteger(marketListEntity.getB2bBackstop()),
                new BigInteger(marketListEntity.getB2cBackstop()),
                marketListEntity.getB2bFactor(),
                marketListEntity.getSkippingFactor(),
                marketListEntity.getAnalystName(),
                marketListEntity.getUuid()
        };
        listOfObject.add(entityValues);
        jdbcTemplate.batchUpdate(INSERT_INTO_MARKET_LIST(tableName), listOfObject);
    }

    public List<Map<String, Object>> findRunSummaryRecords(String startDate, String endDate, String dtd, String userName, String type) {
        List<Map<String, Object>> listObj = null;
        try {
            listObj = jdbcTemplate.queryForList(GET_RUN_AUDIT_VALUES(startDate, endDate, dtd, userName, type));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return listObj;
    }

    public List<Map<String, Object>> findAllocationPerCriteria(String runId) {
        List<Map<String, Object>> listObj = null;
        try {
            listObj = jdbcTemplate.queryForList(GET_RUN_SUMMARY(), runId);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return listObj;
    }


    public void insertAuditStatement(String tableName, String startTime, String userName, String uuid) {
        try {
            jdbcTemplate.update(INSERT_FILE_UPLOAD(), tableName, uuid, startTime, userName);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void deleteTable(String tableName) {
        try {
            jdbcTemplate.update(DELETE_TABLE(tableName));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void insertInputStatusTable(String tableName) {
        List<Map<String, Object>> listObj = new ArrayList<>();
        try {
            listObj = jdbcTemplate.queryForList(GET_INPUT_STATUS_COUNT(), tableName);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        if (Integer.parseInt(listObj.get(0).get(COUNT_COLUMN_NAME).toString()) == 1) {
            jdbcTemplate.update(UPDATE_INPUT_STATUS(1), tableName);
        } else {
            jdbcTemplate.update(INSERT_INPUT_STATUS(), tableName);
        }
    }

    public void updateAuditStatement(String tableName, String endTime, String uuid) {
        try {
            jdbcTemplate.update(UPDATE_FILE_UPLOAD(), endTime, uuid, tableName);
            List<Map<String, Object>> listObj = new ArrayList<>();
            try {
                listObj = jdbcTemplate.queryForList(GET_CURRENT_VERSION_COUNT(), tableName);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            if (Integer.parseInt(listObj.get(0).get(COUNT_COLUMN_NAME).toString()) == 1) {
                jdbcTemplate.update(UPDATE_CURRENT_VERSION(), uuid, tableName);
            } else {
                jdbcTemplate.update(INSERT_CURRENT_VERSION(), uuid, tableName);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void updateInputStatus(String tableName) {
        try {
            jdbcTemplate.update(UPDATE_INPUT_STATUS(0), tableName);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public List<Map<String, Object>> getRunSummaryOutput(String startDate, String endDate, String runId) {
        List<Map<String, Object>> listObj = null;
        try {
            listObj = jdbcTemplate.queryForList(GET_RUN_OUTPUT_SUMMARY(startDate, endDate, runId, getColumnHeaders(RUN_SUMMARY_NAME)));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return listObj;
    }

    public List<String> getRoleAccess(Object role) {
        role = role.toString().replaceAll("\\[", "")
                .replaceAll("]", "");
        return jdbcTemplate.queryForList(GET_ROLE_PERMISSION_LIST(role), String.class);
    }

    public List<String> getRoleName(Object role) {
        role = role.toString().replaceAll("\\[", "")
                .replaceAll("]", "");
        return jdbcTemplate.queryForList(GET_ROLE_NAME(role), String.class);
    }
}
