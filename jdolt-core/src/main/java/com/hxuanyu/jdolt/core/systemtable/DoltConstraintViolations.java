package com.hxuanyu.jdolt.core.systemtable;


import com.hxuanyu.jdolt.manager.DoltConnectionManager;

import java.util.concurrent.ConcurrentHashMap;


/**
 * ## {@code dolt_constraint_violations}
 *
 * 系统表 {@code dolt_constraint_violations} 对每一个因合并操作引入约束违规的表各包含一行。Dolt 在常规 SQL 操作中会强制执行约束（例如外键），但合并操作可能会使一个或多个表进入不再满足约束的状态。比如，在合并基（merge base）中被删除的一行，可能在合并提交中新增加的行里通过外键被引用。使用 {@code dolt_constraint_violations} 来发现此类违规。
 *
 * ### 模式（Schema）
 *
 * <pre><code class="language-sql">
 * +----------------+-----------------+------+-----+---------+-------+
 * | Field          | Type            | Null | Key | Default | Extra |
 * +----------------+-----------------+------+-----+---------+-------+
 * | table          | text            | NO   | PRI |         |       |
 * | num_violations | bigint unsigned | NO   |     |         |       |
 * +----------------+-----------------+------+-----+---------+-------+
 * </code></pre>
 *
 * ## {@code dolt_constraint_violations_$TABLENAME}
 *
 * 对于每个在合并后存在约束违规的表 {@code $TABLENAME}，都会有一个对应的系统表 {@code dolt_constraint_violations_$TABLENAME}。该表中的每一行代表一个必须通过 {@code INSERT}、{@code UPDATE} 或 {@code DELETE} 语句解决的约束违规。在提交引入这些违规的合并结果之前，需要解决每个约束违规。
 *
 * ### 模式（Schema）
 *
 * 假设存在如下模式的表 {@code a}：
 *
 * <pre><code class="language-sql">
 * +-------+------------+------+-----+---------+-------+
 * | Field | Type       | Null | Key | Default | Extra |
 * +-------+------------+------+-----+---------+-------+
 * | x     | bigint     | NO   | PRI |         |       |
 * | y     | varchar(1) | YES  |     |         |       |
 * +-------+------------+------+-----+---------+-------+
 * </code></pre>
 *
 * 则 {@code dolt_constraint_violations_a} 将具有如下模式：
 *
 * <pre><code class="language-sql">
 * +----------------+------------------------------------------------------------------+------+-----+---------+-------+
 * | Field          | Type                                                             | Null | Key | Default | Extra |
 * +----------------+------------------------------------------------------------------+------+-----+---------+-------+
 * | from_root_ish  | varchar(1023)                                                    | YES  |     |         |       |
 * | violation_type | enum('foreign key','unique index','check constraint','not null') | NO   | PRI |         |       |
 * | x              | bigint                                                           | NO   | PRI |         |       |
 * | y              | varchar(1)                                                       | YES  |     |         |       |
 * | violation_info | json                                                             | YES  |     |         |       |
 * +----------------+------------------------------------------------------------------+------+-----+---------+-------+
 * </code></pre>
 *
 * 该表中的每一行代表主表中处于一个或多个约束违规状态的行。{@code violation_info} 字段是描述违规的 JSON 负载。该负载会根据记录的约束违规类型而变化。
 *
 * 对于<strong>外键违规</strong>：
 *
 * <pre><code class="language-json">
 * {
 *   "ForeignKey": "key_name",
 *   "Table": "myTable",
 *   "Columns": ["col1", "col2"],
 *   "Index": "myIdx",
 *   "OnDelete": "RESTRICT",
 *   "OnUpdate": "RESTRICT",
 *   "ReferencedColumns": ["col3", "col4"],
 *   "ReferencedIndex": "myIdx2",
 *   "ReferencedTable": "refTable"
 * }
 * </code></pre>
 *
 * 对于<strong>唯一约束</strong>：
 *
 * <pre><code class="language-json">
 * {
 *   "Name": "constraint_name",
 *   "Columns": ["col1", "col2"]
 * }
 * </code></pre>
 *
 * 对于<strong>非空约束</strong>：
 *
 * <pre><code class="language-json">
 * {
 *   "Columns": ["col1", "col2"]
 * }
 * </code></pre>
 *
 * 对于<strong>检查约束</strong>：
 *
 * <pre><code class="language-json">
 * {
 *   "Name": "constraint_name",
 *   "Expression": "(col1 > 0)"
 * }
 * </code></pre>
 *
 * 与 {@code dolt_conflicts} 一样，从相应的 {@code dolt_constraint_violations} 表中删除行，以向 dolt 表明你已在提交之前解决了这些违规。
 */
public class DoltConstraintViolations extends DoltSystemTableWithSuffix {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltConstraintViolations> INSTANCES = new ConcurrentHashMap<>();

    protected DoltConstraintViolations(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltConstraintViolations getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltConstraintViolations(connectionManager));
    }

    @Override
    public Params prepare() {
        return new Params(this).from("dolt_constraint_violations");
    }
}