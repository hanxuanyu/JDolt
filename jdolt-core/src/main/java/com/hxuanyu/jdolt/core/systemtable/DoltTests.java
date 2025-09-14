package com.hxuanyu.jdolt.core.systemtable;


import com.hxuanyu.jdolt.manager.DoltConnectionManager;

import java.util.concurrent.ConcurrentHashMap;


/**
 * ## {@code dolt_tests}
 *
 * {@code dolt_tests} 存储可通过 {@link DOLT_TEST_RUN() 表函数} 执行的测试定义。测试通过对 SQL 查询的预期结果进行断言，提供验证数据库行为并捕获回归的一种方式。
 *
 * <h3>模式</h3>
 * <pre><code>
 * +----------------------+------+------+-----+
 * | Field                | Type | Null | Key |
 * +----------------------+------+------+-----+
 * | test_name            | text | NO   | PRI |
 * | test_group           | text | YES  |     |
 * | test_query           | text | NO   |     |
 * | assertion_type       | text | NO   |     |
 * | assertion_comparator | text | NO   |     |
 * | assertion_value      | text | NO   |     |
 * +----------------------+------+------+-----+
 * </code></pre>
 *
 * <h3>说明</h3>
 * 测试查询必须是只读的（不允许 {@code INSERT}、{@code UPDATE}、{@code DELETE} 或 DDL 语句），并且只能包含单个 SQL 语句。
 *
 * 提供三种断言类型：
 * <ul>
 *   <li>{@code expected_rows}：对查询返回的行数进行断言。{@code assertion_value} 应为整数。</li>
 *   <li>{@code expected_columns}：对查询返回的列数进行断言。{@code assertion_value} 应为整数。</li>
 *   <li>{@code expected_single_value}：对单个单元格的值进行断言。查询必须恰好返回一行一列。{@code assertion_value} 将与返回值进行比较。</li>
 * </ul>
 *
 * {@code assertion_comparator} 字段支持以下比较运算符：{@code ==}、{@code !=}、{@code <}、{@code >}、{@code <=}、{@code >=}。
 *
 * <h3>示例查询</h3>
 * 创建一个测试以验证表的行数是否符合预期：
 * <pre><code class="language-sql">
 * INSERT INTO dolt_tests VALUES
 * ('check_user_count', 'users', 'SELECT * FROM users', 'expected_rows', '==', '10');
 * </code></pre>
 *
 * 创建一个测试以验证特定的计算值：
 * <pre><code class="language-sql">
 * INSERT INTO dolt_tests VALUES
 * ('total_revenue', 'finance', 'SELECT SUM(amount) FROM sales', 'expected_single_value', '>=', '100000');
 * </code></pre>
 */
public class DoltTests extends DoltSystemTable {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltTests> INSTANCES = new ConcurrentHashMap<>();

    protected DoltTests(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltTests getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltTests(connectionManager));
    }

    @Override
    public Params prepare() {
        return new Params(this).from("dolt_tests");
    }
}