package com.hxuanyu.jdolt.core.function.table;

import com.hxuanyu.jdolt.annotation.MethodDependsOn;
import com.hxuanyu.jdolt.annotation.MethodInvokeRequiredGroup;
import com.hxuanyu.jdolt.annotation.MethodMutexGroup;
import com.hxuanyu.jdolt.interfaces.DoltTableFunction;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.builder.AbstractTableFunctionParamBuilder;
import com.hxuanyu.jdolt.util.builder.SqlBuilder;

import java.util.concurrent.ConcurrentHashMap;

/**
 * {@code DOLT_SCHEMA_DIFF()} 表函数用于计算数据库中任意两个提交之间的模式差异。结果集中每一行描述了某张表在两个提交之间的更改情况，包括该表在起始和目标提交中的创建语句。
 * 
 * 请注意，{@code DOLT_SCHEMA_DIFF()} 表函数目前要求参数值必须为字面值。
 * 
 * <h3>权限要求</h3>
 * 
 * {@code DOLT_SCHEMA_DIFF()} 表函数需要对所有表具有 {@code SELECT} 权限（如果未指定具体表名），或者仅对指定的表具有 {@code SELECT} 权限。
 * 
 * <h3>参数选项</h3>
 * 
 * <pre>{@code
 * DOLT_SCHEMA_DIFF(<from_commit>, <to_commit>, <optional_tablename>)
 * DOLT_SCHEMA_DIFF(<from_revision..to_revision>, <optional_tablename>)
 * DOLT_SCHEMA_DIFF(<from_revision...to_revision>, <optional_tablename>)
 * }</pre>
 * 
 * {@code DOLT_SCHEMA_DIFF()} 表函数接受三个参数：
 * 
 * <ul>
 * <li>{@code from_revision} —— 表数据差异的起始版本。此参数为必填项，可以是提交、标签、分支名或其他版本标识符（例如："main~"、"WORKING"、"STAGED"）。</li>
 * <li>{@code to_revision} —— 表数据差异的目标版本。此参数为必填项，可以是提交、标签、分支名或其他版本标识符（例如："main~"、"WORKING"、"STAGED"）。</li>
 * <li>{@code from_revision..to_revision} —— 获取两个点的差异，或 {@code from_revision} 和 {@code to_revision} 之间的表模式变化。这与 {@code dolt_schema_diff(<from_revision>, <to_revision>, [<tablename>])} 等效。</li>
 * <li>{@code from_revision...to_revision} —— 获取三个点的差异，或 {@code from_revision} 和 {@code to_revision} 之间的表模式变化，<em>从最后一个共同提交开始</em>。</li>
 * <li>{@code tablename} —— 需要比较的表名。此参数为可选项。如果未指定，将返回所有有模式差异的表。</li>
 * </ul>
 * 
 * <h3>返回模式</h3>
 * 
 * <pre>{@code
 * +-----------------------+------+
 * | 字段                 | 类型 |
 * +-----------------------+------+
 * | from_table_name       | TEXT |
 * | to_table_name         | TEXT |
 * | from_create_statement | TEXT |
 * | to_create_statement   | TEXT |
 * +-----------------------+------+
 * }</pre>
 * 
 * <h3>示例</h3>
 * 
 * 以下示例中，我们将考虑两个分支 {@code main} 和 {@code feature_branch} 中的三张表：
 * 
 * 在 {@code main} 分支中的表为：{@code employees}、{@code inventory} 和 {@code vacations}。
 * 在 {@code feature_branch} 分支中的表为：{@code inventory}、{@code photos} 和 {@code trips}。
 * 
 * 为了查看这些表的变化，我们运行以下查询：
 * 
 * <pre>{@code
 * SELECT * FROM DOLT_SCHEMA_DIFF("main", "feature_branch")
 * }</pre>
 * 
 * {@code DOLT_SCHEMA_DIFF()} 的结果显示了从 {@code main} 分支的最新提交到 {@code feature_branch} 分支的最新提交之间，所有表的模式如何变化：
 * 
 * <pre>{@code
 * +-----------------+---------------+-------------------------------------------------------------------+-------------------------------------------------------------------+
 * | from_table_name | to_table_name | from_create_statement                                             | to_create_statement                                               |
 * +-----------------+---------------+-------------------------------------------------------------------+-------------------------------------------------------------------+
 * | employees       |               | CREATE TABLE `employees` (                                        |                                                                   |
 * |                 |               |   `pk` int NOT NULL,                                              |                                                                   |
 * |                 |               |   `name` varchar(50),                                             |                                                                   |
 * |                 |               |   PRIMARY KEY (`pk`)                                              |                                                                   |
 * |                 |               | ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin; |                                                                   |
 * | inventory       | inventory     | CREATE TABLE `inventory` (                                        | CREATE TABLE `inventory` (                                        |
 * |                 |               |   `pk` int NOT NULL,                                              |   `pk` int NOT NULL,                                              |
 * |                 |               |   `name` varchar(50),                                             |   `name` varchar(50),                                             |
 * |                 |               |   `quantity` int,                                                 |   `color` varchar(10),                                            |
 * |                 |               |   PRIMARY KEY (`pk`)                                              |   PRIMARY KEY (`pk`)                                              |
 * |                 |               | ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin; | ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin; |
 * |                 | photos        |                                                                   | CREATE TABLE `photos` (                                           |
 * |                 |               |                                                                   |   `pk` int NOT NULL,                                              |
 * |                 |               |                                                                   |   `name` varchar(50),                                             |
 * |                 |               |                                                                   |   `dt` datetime(6),                                               |
 * |                 |               |                                                                   |   PRIMARY KEY (`pk`)                                              |
 * |                 |               |                                                                   | ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin; |
 * | vacations       | trips         | CREATE TABLE `vacations` (                                        | CREATE TABLE `trips` (                                            |
 * |                 |               |   `pk` int NOT NULL,                                              |   `pk` int NOT NULL,                                              |
 * |                 |               |   `name` varchar(50),                                             |   `name` varchar(50),                                             |
 * |                 |               |   PRIMARY KEY (`pk`)                                              |   PRIMARY KEY (`pk`)                                              |
 * |                 |               | ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin; | ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin; |
 * +-----------------+---------------+-------------------------------------------------------------------+-------------------------------------------------------------------+
 * }</pre>
 */
@MethodInvokeRequiredGroup(value = {"fromRevision", "twoPointDiff", "threePointDiff"}, allRequired = false)
public class DoltSchemaDiff extends DoltRepository implements DoltTableFunction<DoltSchemaDiff.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltSchemaDiff> INSTANCES = new ConcurrentHashMap<>();

    private DoltSchemaDiff(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltSchemaDiff getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltSchemaDiff(connectionManager));
    }

    public static class Params extends AbstractTableFunctionParamBuilder<Params> {

        protected Params(DoltTableFunction<Params> doltTableFunction) {
            super(Params.class, doltTableFunction);
        }

        /**
         * 设置表数据差异的起始版本
         * 可以是提交、标签、分支名或其他版本标识符（例如："main~"、"WORKING"、"STAGED"）
         *
         * @param fromRevision 起始版本
         * @return 当前参数构建器实例
         */
        @MethodMutexGroup({"twoPointDiff", "threePointDiff"})
        public Params fromRevision(String fromRevision) {
            validator.checkAndMark("fromRevision");
            addFlags(fromRevision);
            return this;
        }

        /**
         * 设置表数据差异的目标版本
         * 可以是提交、标签、分支名或其他版本标识符（例如："main~"、"WORKING"、"STAGED"）
         *
         * @param toRevision 目标版本
         * @return 当前参数构建器实例
         */
        @MethodDependsOn({"fromRevision"})
        public Params toRevision(String toRevision) {
            validator.checkAndMark("toRevision");
            addFlags(toRevision);
            return this;
        }

        /**
         * 设置两个点的差异表达式，格式为 from_revision..to_revision
         * 获取 from_revision 和 to_revision 之间的表模式变化
         * 这与 fromRevision() 和 toRevision() 方法等效
         *
         * @param revisionRange 版本范围表达式，格式为 from_revision..to_revision
         * @return 当前参数构建器实例
         */
        @MethodMutexGroup({"fromRevision", "threePointDiff"})
        public Params twoPointDiff(String revisionRange) {
            validator.checkAndMark("twoPointDiff");
            addFlags(revisionRange);
            return this;
        }

        /**
         * 设置三个点的差异表达式，格式为 from_revision...to_revision
         * 获取 from_revision 和 to_revision 之间的表模式变化，从最后一个共同提交开始
         *
         * @param revisionRange 版本范围表达式，格式为 from_revision...to_revision
         * @return 当前参数构建器实例
         */
        @MethodMutexGroup({"fromRevision", "twoPointDiff"})
        public Params threePointDiff(String revisionRange) {
            validator.checkAndMark("threePointDiff");
            addFlags(revisionRange);
            return this;
        }

        /**
         * 设置需要比较的表名
         * 如果未指定，将返回所有有模式差异的表
         *
         * @param tableName 表名
         * @return 当前参数构建器实例
         */
        @MethodDependsOn({"fromRevision", "twoPointDiff", "threePointDiff"})
        public Params tableName(String tableName) {
            validator.checkAndMark("tableName");
            addFlags(tableName);
            return this;
        }
    }

    @Override
    public Params prepare() {
        return new Params(this);
    }

    @Override
    public SqlBuilder.SqlTemplate buildSqlTemplate(String... params) {
        return SqlBuilder.select()
                .fromFunction("dolt_schema_diff")
                .withParams(params)
                .build();
    }
}