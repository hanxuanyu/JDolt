package com.hxuanyu.jdolt.repository;

import com.hxuanyu.jdolt.exception.DoltException;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.util.builder.SqlBuilder;
import com.hxuanyu.jdolt.util.validator.BranchNameValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final Logger logger = LoggerFactory.getLogger(DoltRepository.class);

    private final DoltConnectionManager connectionManager;

    // 构造函数
    protected DoltRepository(DoltConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * 执行一个通用的 DML/DDL 命令（如 INSERT, UPDATE, DELETE, CREATE, DROP 等）。
     * 使用 SQL 模板和参数列表的方式，防止 SQL 注入。
     *
     * @param sqlTemplate    要执行的 SQL 模板
     * @return 受影响的行数
     * @throws SQLException 如果执行失败
     */
    protected int executeUpdate(SqlBuilder.SqlTemplate sqlTemplate) throws SQLException {
        String sql = sqlTemplate.sql();
        try (
                Connection connection = connectionManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            sqlTemplate.setParameters(preparedStatement);
            return preparedStatement.executeUpdate();
        }
    }

    /**
     * 执行查询语句，并返回结果集。
     * 使用 SQL 模板和参数列表的方式，防止 SQL 注入。
     *
     * @param sqlTemplate    要执行的查询 SQL 模板
     * @return 查询结果集（ResultSet）
     * @throws SQLException 如果执行失败
     */
    protected ResultSet executeQuery(SqlBuilder.SqlTemplate sqlTemplate) throws SQLException {
        String sql = sqlTemplate.sql();
        Connection connection = connectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        sqlTemplate.setParameters(preparedStatement);
        return preparedStatement.executeQuery(); // 调用者需关闭 ResultSet 和 PreparedStatement
    }

    /**
     * 执行查询语句并返回封装的结果。
     * 使用 SQL 模板和参数列表的方式，防止 SQL 注入。
     *
     * @param sqlTemplate    要执行的查询 SQL 模板
     * @return 查询结果封装为 List<Map<String, Object>>
     * @throws SQLException 如果执行失败
     */
    public List<Map<String, Object>> executeQueryAsList(SqlBuilder.SqlTemplate sqlTemplate) {
        String sql = sqlTemplate.sql();
        long start = System.currentTimeMillis();
        logger.debug("executeQueryAsList start, sql: {} params: {}", sqlTemplate, sqlTemplate.parameters());
        try (Connection connection = connectionManager.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                sqlTemplate.setParameters(preparedStatement);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
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
                    long end = System.currentTimeMillis();
                    logger.debug("executeQueryAsList finish, sql: {} params: {}， result: {}, cost: {}ms", sql, sqlTemplate.parameters(), results, (end - start));
                    return results;
                }
            }
        } catch (SQLException e) {
            DoltException doltException = new DoltException("dolt execute error, sql: " + sql + " params: " + sqlTemplate.parameters(), e);
            logger.error("dolt execute error, sql: {} params: {}", sql, sqlTemplate.parameters(), doltException);
            throw doltException;
        }
    }

    /**
     * 执行任意 SQL 语句。
     * 仅保留最通用的方式，适用于不需要参数的简单 SQL。
     *
     * @param sqlTemplate 要执行的 SQL 语句
     * @return 查询结果封装为 List<Map<String, Object>>
     * @return 是否成功执行
     * @throws SQLException 如果执行失败
     */
    protected boolean execute(SqlBuilder.SqlTemplate sqlTemplate) throws SQLException {
        String sql = sqlTemplate.sql();
        logger.debug("execute start, sql: {} params: {}", sql, sqlTemplate.parameters());
        try (
                Connection connection = connectionManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            sqlTemplate.setParameters(preparedStatement);
            return preparedStatement.execute();
        }
    }


    /**
     * 开始事务。
     *
     * @throws SQLException 如果设置失败
     */
    protected void beginTransaction() throws SQLException {
        Connection connection = connectionManager.getConnection();
        connection.setAutoCommit(false);
    }

    /**
     * 提交事务。
     *
     * @throws SQLException 如果提交失败
     */
    protected void commitTransaction() throws SQLException {
        Connection connection = connectionManager.getConnection();
        connection.commit();
        connection.setAutoCommit(true);
    }

    /**
     * 回滚事务。
     *
     * @throws SQLException 如果回滚失败
     */
    protected void rollbackTransaction() throws SQLException {
        Connection connection = connectionManager.getConnection();
        connection.rollback();
        connection.setAutoCommit(true);
    }


    public boolean commonDoltExecute(SqlBuilder.SqlTemplate sqlTemplate) {
        List<Object> params = sqlTemplate.parameters();
        String sql = sqlTemplate.sql();
        try {
            logger.debug("start execute sql: [{}], params: [{}]", sql, params);
            boolean execResult = execute(sqlTemplate);
            logger.debug("execute finished, result: [{}]", execResult);
            return execResult;
        } catch (SQLException e) {
            DoltException doltException = new DoltException("dolt execute error, sql: " + sql + " params: " + params, e);
            logger.error("dolt execute error, sql: {} params: {}", sql, params, doltException);
            throw doltException;
        }
    }


    protected void checkBranchName(String... branchNames) {
        if (branchNames == null) {
            throw new IllegalArgumentException("branchNames is null");
        }
        for (String param : branchNames) {
            if (!BranchNameValidator.isValidBranchName(param)) {
                throw new IllegalArgumentException("branchName is invalid, current: " + param);
            }
        }
    }
}