package com.hxuanyu.jdolt.core;

import com.hxuanyu.jdolt.connection.DoltConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a repository for managing Dolt commands execution against a database.
 * This class encapsulates the logic to interact with a Dolt database using SQL commands,
 * abstracting the connection management and command execution process.
 */
public class DoltRepository {
    private final DoltConnectionManager connectionManager;


    // 构造函数
    protected DoltRepository(DoltConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * 执行一个通用的 DML/DDL 命令（如 INSERT, UPDATE, DELETE, CREATE, DROP 等）。
     * @param sql 要执行的 SQL 语句
     * @return 受影响的行数
     * @throws SQLException 如果执行失败
     */
    protected int executeUpdate(String sql) throws SQLException {
        try (
                Connection connection = connectionManager.getConnection();
                Statement statement = connection.createStatement()
        ) {
            return statement.executeUpdate(sql);
        }
    }

    /**
     * 执行查询语句，并返回结果集。
     * @param sql 要执行的查询 SQL 语句
     * @return 查询结果集（ResultSet）
     * @throws SQLException 如果执行失败
     */
    protected ResultSet executeQuery(String sql) throws SQLException {
        Connection connection = connectionManager.getConnection();
        Statement statement = connection.createStatement();
        return statement.executeQuery(sql); // 调用者需关闭 ResultSet 和 Statement
    }

    /**
     * 执行查询语句并返回封装的结果。
     * @param sql 要执行的查询 SQL 语句
     * @return 查询结果封装为 List<Map<String, Object>>
     * @throws SQLException 如果执行失败
     */
    protected List<Map<String, Object>> executeQueryAsList(String sql) throws SQLException {
        try (
                Connection connection = connectionManager.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)
        ) {
            List<Map<String, Object>> results = new ArrayList<>();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), resultSet.getObject(i));
                }
                results.add(row);
            }

            return results;
        }
    }

    /**
     * 执行任意 SQL 语句。
     * @param sql 要执行的 SQL 语句
     * @return 是否成功执行
     * @throws SQLException 如果执行失败
     */
    protected boolean execute(String sql) throws SQLException {
        try (
                Connection connection = connectionManager.getConnection();
                Statement statement = connection.createStatement()
        ) {
            return statement.execute(sql);
        }
    }

    /**
     * 开始事务。
     * @throws SQLException 如果设置失败
     */
    protected void beginTransaction() throws SQLException {
        Connection connection = connectionManager.getConnection();
        connection.setAutoCommit(false);
    }

    /**
     * 提交事务。
     * @throws SQLException 如果提交失败
     */
    protected void commitTransaction() throws SQLException {
        Connection connection = connectionManager.getConnection();
        connection.commit();
        connection.setAutoCommit(true);
    }

    /**
     * 回滚事务。
     * @throws SQLException 如果回滚失败
     */
    protected void rollbackTransaction() throws SQLException {
        Connection connection = connectionManager.getConnection();
        connection.rollback();
        connection.setAutoCommit(true);
    }
}
