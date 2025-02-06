package com.hxuanyu.jdolt.repository;

import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.exception.DoltException;
import com.hxuanyu.jdolt.util.BranchNameValidator;
import com.hxuanyu.jdolt.util.ParamValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

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
     * @param sql    要执行的 SQL 模板
     * @param params 参数列表
     * @return 受影响的行数
     * @throws SQLException 如果执行失败
     */
    protected int executeUpdate(String sql, String... params) throws SQLException {
        try (
                Connection connection = connectionManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            setParameters(preparedStatement, params);
            return preparedStatement.executeUpdate();
        }
    }

    /**
     * 执行查询语句，并返回结果集。
     * 使用 SQL 模板和参数列表的方式，防止 SQL 注入。
     *
     * @param sql    要执行的查询 SQL 模板
     * @param params 参数列表
     * @return 查询结果集（ResultSet）
     * @throws SQLException 如果执行失败
     */
    protected ResultSet executeQuery(String sql, String... params) throws SQLException {
        Connection connection = connectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        setParameters(preparedStatement, params);
        return preparedStatement.executeQuery(); // 调用者需关闭 ResultSet 和 PreparedStatement
    }

    /**
     * 执行查询语句并返回封装的结果。
     * 使用 SQL 模板和参数列表的方式，防止 SQL 注入。
     *
     * @param sql    要执行的查询 SQL 模板
     * @param params 参数列表
     * @return 查询结果封装为 List<Map<String, Object>>
     * @throws SQLException 如果执行失败
     */
    public List<Map<String, Object>> executeQueryAsList(String sql, String... params) {
        long start = System.currentTimeMillis();
        logger.debug("executeQueryAsList start, sql: {} params: {}", sql, params);
        try (
                Connection connection = connectionManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            setParameters(preparedStatement, params);
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
                logger.debug("executeQueryAsList finish, sql: {} params: {}， result: {}, cost: {}ms", sql, params, results, (end - start));
                return results;
            }
        } catch (SQLException e) {
            DoltException doltException = new DoltException("dolt execute error, sql: " + sql + " params: " + Arrays.toString(params), e);
            logger.error("dolt execute error, sql: {} params: {}", sql, params, doltException);
            throw doltException;
        }
    }

    /**
     * 执行任意 SQL 语句。
     * 仅保留最通用的方式，适用于不需要参数的简单 SQL。
     *
     * @param sql 要执行的 SQL 语句
     * @return 查询结果封装为 List<Map<String, Object>>
     * @return 是否成功执行
     * @throws SQLException 如果执行失败
     */
    protected boolean execute(String sql, String... params) throws SQLException {
        logger.debug("execute start, sql: {} params: {}", sql, params);
        try (
                Connection connection = connectionManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            setParameters(preparedStatement, params);
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

    /**
     * 设置 PreparedStatement 的参数。
     *
     * @param preparedStatement PreparedStatement 对象
     * @param params            参数列表
     * @throws SQLException 如果设置参数失败
     */
    private void setParameters(PreparedStatement preparedStatement, String... params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setString(i + 1, params[i]);
            }
        }
    }

    public boolean commonDoltExecute(String sql, String... params) {
        try {
            ParamValidator.create(params)
                    .checkNotEmpty()
                    .checkNoDuplicates();
            logger.debug("start execute sql: [{}], params: [{}]", sql, Arrays.toString(params));
            boolean execResult = execute(sql, params);
            logger.debug("execute finished, result: [{}]", execResult);
            return execResult;
        } catch (SQLException e) {
            DoltException doltException = new DoltException("dolt execute error, sql: " + sql + " params: " + Arrays.toString(params), e);
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