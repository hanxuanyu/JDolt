package com.hxuanyu.jdolt.core;

import com.hxuanyu.jdolt.connection.DoltConnectionManager;
import com.hxuanyu.jdolt.exception.DoltException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 封装版本管理相关操作
 */
public class DoltVersionControl extends DoltRepository {
    private final Logger logger = LoggerFactory.getLogger(DoltVersionControl.class);

    public DoltVersionControl(DoltConnectionManager connectionManager) {
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
}
