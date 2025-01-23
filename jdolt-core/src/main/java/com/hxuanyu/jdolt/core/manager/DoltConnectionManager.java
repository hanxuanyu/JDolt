package com.hxuanyu.jdolt.core.manager;

import com.hxuanyu.jdolt.exception.DoltConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Manages connections to a Dolt database using a specified {@link DataSource}.
 * This class is responsible for establishing and providing database connections
 * wrapped within the context of Dolt operations, while handling any exceptions
 * that occur during the connection process.
 */
public class DoltConnectionManager {
    private final DataSource dataSource;
    private final Logger logger = LoggerFactory.getLogger(DoltConnectionManager.class);

    public DoltConnectionManager(DataSource dataSource) {
        if (dataSource == null) {
            throw new IllegalArgumentException("DataSource 不能为空");
        }
        this.dataSource = dataSource;
    }

    public Connection getConnection() throws DoltConnectionException {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            logger.error("获取数据库连接失败", e);
            throw new DoltConnectionException("无法获取数据库连接", e);
        }
    }
}
