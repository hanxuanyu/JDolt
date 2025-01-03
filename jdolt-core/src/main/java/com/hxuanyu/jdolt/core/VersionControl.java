package com.hxuanyu.jdolt.core;

import com.hxuanyu.jdolt.connection.DoltConnectionManager;
import com.hxuanyu.jdolt.constant.DoltSqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 封装版本管理相关操作
 */
public class VersionControl extends DoltRepository {
    private final Logger logger = LoggerFactory.getLogger(VersionControl.class);

    public VersionControl(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public List<String> showDatabases() {
        String sql = "SHOW DATABASES;";
        try {
            List<Map<String, Object>> mapList = executeQueryAsList(sql);
            if (!mapList.isEmpty()) {
                List<String> result = new ArrayList<>(mapList.size());
                for (Map<String, Object> stringObjectMap : mapList) {
                    result.add(stringObjectMap.get("Database").toString());
                }
                return result;
            }
        } catch (SQLException e) {
            logger.error("execute query [{}] failed", sql, e);
        }
        return null;
    }

    public boolean doltAdd(String... params) {
        if (params == null) {
            throw new IllegalArgumentException("The 'params' argument cannot be null. Please provide a valid parameter.");
        } else if (params.length == 1) {
            if (!("-A".equals(params[0]) || ".".equals(params[0]))) {
                throw new IllegalArgumentException("When the length of 'params' is 1, its value must be either '-A' or '.'.");
            }
        }


        try {
            return execute(DoltSqlTemplate.SQL_DOLT_ADD, formatParams(params));
        } catch (SQLException e) {
            logger.error("execute [{}] failed, params: {}", DoltSqlTemplate.SQL_DOLT_ADD, params);
        }

        return false;
    }

    public static String formatParams(String[] params) {
        if (params == null || params.length == 0) {
            throw new IllegalArgumentException("The 'params' list cannot be null or empty.");
        }
        return Arrays.stream(params)
                .map(param -> "'" + param + "'")
                .collect(Collectors.joining(", "));
    }
}
