package com.hxuanyu.jdolt.util.builder;

import com.hxuanyu.jdolt.model.WhereCondition;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 灵活的SQL构造工具类，支持多种SQL操作：
 * - SELECT/INSERT/UPDATE/DELETE
 * - 表函数查询: SELECT * FROM function(param1, param2) WHERE xxx
 * - 存储过程调用: CALL procedure(param1, param2)
 * - 函数调用: SELECT function(param1, param2)
 * 生成带有占位符的SQL模板，方便与PreparedStatement一起使用
 */
public class SqlBuilder {

    // SQL操作类型  
    private enum SqlType {
        SELECT, INSERT, UPDATE, DELETE, CALL_PROCEDURE, SELECT_FUNCTION
    }

    private SqlType sqlType;
    private List<String> columns = new ArrayList<>();
    private String tableName;
    private String tableFunction; // 表函数名  
    private List<Object> tableFunctionParams = new ArrayList<>(); // 表函数参数  
    private String procedureName; // 存储过程名  
    private List<Object> procedureParams = new ArrayList<>(); // 存储过程参数  
    private String functionName; // 普通函数名  
    private List<Object> functionParams = new ArrayList<>(); // 普通函数参数  
    private String functionAlias; // 函数结果的别名  
    private Map<String, Object> conditions = new HashMap<>();
    private List<WhereCondition> whereConditions = new ArrayList<>(); // 新的WHERE条件列表
    private Map<String, Object> updateValues = new HashMap<>();
    private List<String> orderByColumns = new ArrayList<>();
    private boolean orderAsc = true;
    private Integer limit;
    private Integer offset;
    private List<Object> parameters = new ArrayList<>();

    /**
     * 私有构造函数，通过静态方法创建实例
     */
    private SqlBuilder() {
    }

    /**
     * 创建SELECT查询
     */
    public static SqlBuilder select(String... columns) {
        SqlBuilder builder = new SqlBuilder();
        builder.sqlType = SqlType.SELECT;
        if (columns != null && columns.length > 0) {
            for (String column : columns) {
                builder.columns.add(column);
            }
        }
        return builder;
    }

    /**
     * 创建调用函数的SELECT语句: SELECT function(param1, param2)
     */
    public static SqlBuilder selectFunction(String functionName) {
        SqlBuilder builder = new SqlBuilder();
        builder.sqlType = SqlType.SELECT_FUNCTION;
        builder.functionName = functionName;
        return builder;
    }

    /**
     * 创建INSERT语句
     */
    public static SqlBuilder insertInto(String tableName) {
        SqlBuilder builder = new SqlBuilder();
        builder.sqlType = SqlType.INSERT;
        builder.tableName = tableName;
        return builder;
    }

    /**
     * 创建UPDATE语句
     */
    public static SqlBuilder update(String tableName) {
        SqlBuilder builder = new SqlBuilder();
        builder.sqlType = SqlType.UPDATE;
        builder.tableName = tableName;
        return builder;
    }

    /**
     * 创建DELETE语句
     */
    public static SqlBuilder deleteFrom(String tableName) {
        SqlBuilder builder = new SqlBuilder();
        builder.sqlType = SqlType.DELETE;
        builder.tableName = tableName;
        return builder;
    }

    /**
     * 创建调用存储过程的语句: CALL procedure(param1, param2)
     */
    public static SqlBuilder callProcedure(String procedureName) {
        SqlBuilder builder = new SqlBuilder();
        builder.sqlType = SqlType.CALL_PROCEDURE;
        builder.procedureName = procedureName;
        return builder;
    }

    /**
     * 设置FROM子句的表名
     */
    public SqlBuilder from(String tableName) {
        this.tableName = tableName;
        this.tableFunction = null; // 清除表函数，因为使用了普通表  
        this.tableFunctionParams.clear();
        return this;
    }

    /**
     * 设置FROM子句为表函数
     *
     * @param functionName 函数名
     * @return SqlBuilder实例
     */
    public SqlBuilder fromFunction(String functionName) {
        this.tableFunction = functionName;
        this.tableName = null; // 清除表名，因为使用了表函数  
        this.tableFunctionParams.clear();
        return this;
    }

    /**
     * 向表函数添加参数
     *
     * @param param 函数参数
     * @return SqlBuilder实例
     */
    public SqlBuilder withParam(String param) {
        if (this.sqlType == SqlType.SELECT && this.tableFunction != null) {
            // 表函数参数  
            this.tableFunctionParams.add(param);
        } else if (this.sqlType == SqlType.CALL_PROCEDURE) {
            // 存储过程参数  
            this.procedureParams.add(param);
        } else if (this.sqlType == SqlType.SELECT_FUNCTION) {
            // 函数调用参数  
            this.functionParams.add(param);
        } else {
            throw new IllegalStateException("必须先调用fromFunction、callProcedure或selectFunction设置函数名");
        }
        return this;
    }

    /**
     * 向表函数添加参数列表
     *
     * @param params 函数参数
     * @return SqlBuilder实例
     */
    public SqlBuilder withParams(String... params) {
        if (this.sqlType == SqlType.SELECT && this.tableFunction != null) {
            // 表函数参数
            this.tableFunctionParams.addAll(List.of(params));
        } else if (this.sqlType == SqlType.CALL_PROCEDURE) {
            // 存储过程参数
            this.procedureParams.addAll(List.of(params));
        } else if (this.sqlType == SqlType.SELECT_FUNCTION) {
            // 函数调用参数
            this.functionParams.addAll(List.of(params));
        } else {
            throw new IllegalStateException("必须先调用fromFunction、callProcedure或selectFunction设置函数名");
        }
        return this;
    }

    /**
     * 设置函数调用结果的别名
     *
     * @param alias 别名
     * @return SqlBuilder实例
     */
    public SqlBuilder as(String alias) {
        if (this.sqlType == SqlType.SELECT_FUNCTION) {
            this.functionAlias = alias;
        } else {
            throw new IllegalStateException("as方法只能用于函数调用");
        }
        return this;
    }

    /**
     * 添加WHERE条件 (简单等于条件)
     */
    public SqlBuilder where(String column, Object value) {
        this.conditions.put(column, value);
        return this;
    }

    /**
     * 添加WHERE条件 (使用WhereCondition对象)
     */
    public SqlBuilder where(WhereCondition whereCondition) {
        this.whereConditions.add(whereCondition);
        return this;
    }

    /**
     * 设置更新的列和值
     */
    public SqlBuilder set(String column, Object value) {
        this.updateValues.put(column, value);
        return this;
    }

    /**
     * 添加要插入的列和值
     */
    public SqlBuilder column(String column, Object value) {
        this.columns.add(column);
        this.parameters.add(value);
        return this;
    }

    /**
     * 设置ORDER BY子句
     */
    public SqlBuilder orderBy(String... columns) {
        if (columns != null && columns.length > 0) {
            for (String column : columns) {
                this.orderByColumns.add(column);
            }
        }
        this.orderAsc = true;
        return this;
    }

    /**
     * 设置降序ORDER BY
     */
    public SqlBuilder orderByDesc(String... columns) {
        if (columns != null && columns.length > 0) {
            for (String column : columns) {
                this.orderByColumns.add(column);
            }
        }
        this.orderAsc = false;
        return this;
    }

    /**
     * 设置LIMIT子句
     */
    public SqlBuilder limit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * 设置OFFSET子句
     */
    public SqlBuilder offset(int offset) {
        this.offset = offset;
        return this;
    }

    /**
     * 构建SQL语句和参数
     *
     * @return SqlResult 包含SQL语句和对应的参数列表
     */
    public SqlTemplate build() {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        switch (sqlType) {
            case SELECT:
                buildSelectSql(sql, params);
                break;
            case SELECT_FUNCTION:
                buildSelectFunctionSql(sql, params);
                break;
            case INSERT:
                buildInsertSql(sql, params);
                break;
            case UPDATE:
                buildUpdateSql(sql, params);
                break;
            case DELETE:
                buildDeleteSql(sql, params);
                break;
            case CALL_PROCEDURE:
                buildCallProcedureSql(sql, params);
                break;
        }

        return new SqlTemplate(sql.toString(), params);
    }

    private void buildSelectSql(StringBuilder sql, List<Object> params) {
        sql.append("SELECT ");

        if (columns.isEmpty()) {
            sql.append("*");
        } else {
            sql.append(String.join(", ", columns));
        }

        sql.append(" FROM ");

        // 处理表函数或普通表名  
        if (tableFunction != null) {
            sql.append(tableFunction).append("(");

            // 添加表函数参数  
            for (int i = 0; i < tableFunctionParams.size(); i++) {
                if (i > 0) {
                    sql.append(", ");
                }
                sql.append("?");
                params.add(tableFunctionParams.get(i));
            }

            sql.append(")");
        } else {
            sql.append(tableName);
        }

        appendWhereClause(sql, params);
        appendOrderByClause(sql);
        appendLimitOffsetClause(sql, params); // 传递params参数
    }

    private void buildSelectFunctionSql(StringBuilder sql, List<Object> params) {
        sql.append("SELECT ").append(functionName).append("(");

        // 添加函数参数  
        for (int i = 0; i < functionParams.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append("?");
            params.add(functionParams.get(i));
        }

        sql.append(")");

        // 添加别名  
        if (functionAlias != null && !functionAlias.isEmpty()) {
            sql.append(" AS ").append(functionAlias);
        }

        appendWhereClause(sql, params);
        appendOrderByClause(sql);
        appendLimitOffsetClause(sql, params); // 传递params参数
    }

    private void buildCallProcedureSql(StringBuilder sql, List<Object> params) {
        sql.append("CALL ").append(procedureName).append("(");

        // 添加存储过程参数  
        for (int i = 0; i < procedureParams.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append("?");
            params.add(procedureParams.get(i));
        }

        sql.append(")");
    }

    private void buildInsertSql(StringBuilder sql, List<Object> params) {
        sql.append("INSERT INTO ").append(tableName).append(" (");
        sql.append(String.join(", ", columns));
        sql.append(") VALUES (");

        for (int i = 0; i < columns.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append("?");
            params.add(parameters.get(i));
        }

        sql.append(")");
    }

    private void buildUpdateSql(StringBuilder sql, List<Object> params) {
        sql.append("UPDATE ").append(tableName).append(" SET ");

        int i = 0;
        for (Map.Entry<String, Object> entry : updateValues.entrySet()) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append(entry.getKey()).append(" = ?");
            params.add(entry.getValue());
            i++;
        }

        appendWhereClause(sql, params);
    }

    private void buildDeleteSql(StringBuilder sql, List<Object> params) {
        sql.append("DELETE FROM ").append(tableName);
        appendWhereClause(sql, params);
    }

    private void appendWhereClause(StringBuilder sql, List<Object> params) {
        boolean hasConditions = !conditions.isEmpty() || !whereConditions.isEmpty();
        if (!hasConditions) {
            return;
        }

        sql.append(" WHERE ");
        int conditionCount = 0;

        // 处理旧的简单条件（等于条件）
        for (Map.Entry<String, Object> entry : conditions.entrySet()) {
            if (conditionCount > 0) {
                sql.append(" AND ");
            }
            sql.append(entry.getKey()).append(" = ?");
            params.add(entry.getValue());
            conditionCount++;
        }

        // 处理新的WhereCondition对象
        for (WhereCondition whereCondition : whereConditions) {
            if (conditionCount > 0) {
                sql.append(" AND ");
            }

            sql.append(whereCondition.getColumn()).append(" ").append(whereCondition.getOperator().getSymbol());

            // 某些操作符不需要参数（如IS NULL, IS NOT NULL）
            if (whereCondition.needsValue()) {
                sql.append(" ?");
                params.add(whereCondition.getValue());
            }

            conditionCount++;
        }
    }

    private void appendOrderByClause(StringBuilder sql) {
        if (!orderByColumns.isEmpty()) {
            sql.append(" ORDER BY ");
            sql.append(String.join(", ", orderByColumns));
            if (!orderAsc) {
                sql.append(" DESC");
            }
        }
    }

    private void appendLimitOffsetClause(StringBuilder sql, List<Object> params) {
        if (limit != null) {
            sql.append(" LIMIT ?");
            params.add(limit); // 添加LIMIT参数到参数列表
        }

        if (offset != null) {
            sql.append(" OFFSET ?");
            params.add(offset); // 添加OFFSET参数到参数列表
        }
    }

    /**
     * SQL结果类，包含SQL语句和对应的参数
     */
    public record SqlTemplate(String sql, List<Object> parameters) {

        /**
         * 将参数设置到PreparedStatement
         */
        public void setParameters(PreparedStatement pstmt) throws java.sql.SQLException {
            for (int i = 0; i < parameters.size(); i++) {
                pstmt.setObject(i + 1, parameters.get(i));
            }
        }
    }
}  