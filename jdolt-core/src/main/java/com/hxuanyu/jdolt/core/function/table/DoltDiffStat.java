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
 * ## `DOLT_DIFF_STAT()`
 * <p>
 * *之前称为* *`dolt_diff_summary()`*
 * <p>
 * `DOLT_DIFF_STAT()` 表函数用于计算数据库中任意两个提交之间的数据差异统计。对于诸如创建没有行的新表或删除没有行的表等模式更改，
 * 将返回空结果。结果集中的每一行描述了单个表的差异统计，包括未修改、添加、删除和修改的行数，添加、删除和修改的单元格数，
 * 以及每次提交中表的总行数和单元格数。
 * <p>
 * `DOLT_DIFF_STAT()` 的功能类似于 {@code dolt diff --stat} 命令，但使用
 * `DOLT_DIFF_STAT()` 表函数需要提供两个提交，并且表名是可选的。对于无主键表，此表函数仅提供添加和删除的行数。
 * 对于没有数据更改的表，将返回空结果。
 * <p>
 * 请注意，`DOLT_DIFF_STAT()` 表函数目前要求参数值必须是字面值。
 * <p>
 * ### 权限
 * <p>
 * 如果未定义表名，`DOLT_DIFF_STAT()` 表函数需要对所有表具有 `SELECT` 权限；
 * 如果定义了表名，则仅需要对该表具有 `SELECT` 权限。
 * <p>
 * ### 选项
 *
 * <pre>{@code
 * DOLT_DIFF_STAT(<from_revision>, <to_revision>, <optional_tablename>)
 * DOLT_DIFF_STAT(<from_revision..to_revision>, <optional_tablename>)
 * DOLT_DIFF_STAT(<from_revision...to_revision>, <optional_tablename>)
 * }</pre>
 * <p>
 * `DOLT_DIFF_STAT()` 表函数接受三个参数：
 * <p>
 * - `from_revision` — 差异起点表数据的版本。此参数是必需的，可以是提交、标签、分支名称或其他版本指定符（例如 "main~"、"WORKING"、"STAGED"）。
 * - `to_revision` — 差异终点表数据的版本。此参数是必需的，可以是提交、标签、分支名称或其他版本指定符（例如 "main~"、"WORKING"、"STAGED"）。
 * - `from_revision..to_revision` — 获取两点差异统计，或 `from_revision` 和 `to_revision` 之间表数据的版本。
 * 这等同于 `DOLT_DIFF_STAT(<from_revision>, <to_revision>, <tablename>)`。
 * - `from_revision...to_revision` — 获取三点差异统计，或 `from_revision` 和 `to_revision` 之间表数据的版本，
 * *从最后一个共同提交开始*。
 * - `tablename` — 包含要比较数据的表的名称。此参数是可选的。如果未定义，将返回所有有数据差异的表。
 * <p>
 * ### 模式
 *
 * <pre>{@code
 * +-----------------+--------+
 * | field           | type   |
 * +-----------------+--------+
 * | table_name      | TEXT   |
 * | rows_unmodified | BIGINT |
 * | rows_added      | BIGINT |
 * | rows_deleted    | BIGINT |
 * | rows_modified   | BIGINT |
 * | cells_added     | BIGINT |
 * | cells_deleted   | BIGINT |
 * | cells_modified  | BIGINT |
 * | old_row_count   | BIGINT |
 * | new_row_count   | BIGINT |
 * | old_cell_count  | BIGINT |
 * | new_cell_count  | BIGINT |
 * +-----------------+--------+
 * }</pre>
 * <p>
 * ### 示例
 * <p>
 * 假设我们从 `main` 分支的数据库中的表 `inventory` 开始。当我们进行任何更改时，
 * 可以使用 `DOLT_DIFF_STAT()` 函数计算特定提交之间的表数据差异或所有有数据更改的表的差异。
 * <p>
 * 以下是 `main` 分支顶端的 `inventory` 表的模式：
 *
 * <pre>{@code
 * +----------+-------------+------+-----+---------+-------+
 * | Field    | Type        | Null | Key | Default | Extra |
 * +----------+-------------+------+-----+---------+-------+
 * | pk       | int         | NO   | PRI | NULL    |       |
 * | name     | varchar(50) | YES  |     | NULL    |       |
 * | quantity | int         | YES  |     | NULL    |       |
 * +----------+-------------+------+-----+---------+-------+
 * }</pre>
 * <p>
 * 以下是 `main` 分支顶端的 `inventory` 表的数据：
 *
 * <pre>{@code
 * +----+-------+----------+
 * | pk | name  | quantity |
 * +----+-------+----------+
 * | 1  | shirt | 15       |
 * | 2  | shoes | 10       |
 * +----+-------+----------+
 * }</pre>
 * <p>
 * 我们对 `inventory` 表进行了一些更改，并创建了一个新的无主键表：
 *
 * <pre>{@code
 * ALTER TABLE inventory ADD COLUMN color VARCHAR(10);
 * INSERT INTO inventory VALUES (3, 'hat', 6, 'red');
 * UPDATE inventory SET quantity=0 WHERE pk=1;
 * CREATE TABLE items (name varchar(50));
 * INSERT INTO items VALUES ('shirt'),('pants');
 * }</pre>
 * <p>
 * 以下是当前工作集中的 `inventory` 表：
 *
 * <pre>{@code
 * +----+-------+----------+-------+
 * | pk | name  | quantity | color |
 * +----+-------+----------+-------+
 * | 1  | shirt | 0        | NULL  |
 * | 2  | shoes | 10       | NULL  |
 * | 3  | hat   | 6        | red   |
 * +----+-------+----------+-------+
 * }</pre>
 * <p>
 * 为了计算差异并查看结果，我们运行以下查询：
 *
 * <pre>{@code
 * SELECT * FROM DOLT_DIFF_STAT('main', 'WORKING');
 * }</pre>
 * <p>
 * `DOLT_DIFF_STAT()` 的结果显示了从 `main` 顶端到当前工作集的数据更改情况：
 *
 * <pre>{@code
 * +------------+-----------------+------------+--------------+---------------+-------------+---------------+----------------+---------------+---------------+----------------+----------------+
 * | table_name | rows_unmodified | rows_added | rows_deleted | rows_modified | cells_added | cells_deleted | cells_modified | old_row_count | new_row_count | old_cell_count | new_cell_count |
 * +------------+-----------------+------------+--------------+---------------+-------------+---------------+----------------+---------------+---------------+----------------+----------------+
 * | inventory  | 1               | 1          | 0            | 1             | 6           | 0             | 1              | 2             | 3             | 6              | 12             |
 * | items      | NULL            | 2          | 0            | NULL          | NULL        | NULL          | NULL           | NULL          | NULL          | NULL           | NULL           |
 * +------------+-----------------+------------+--------------+---------------+-------------+---------------+----------------+---------------+---------------+----------------+----------------+
 * }</pre>
 * <p>
 * 为了获取从当前工作集到 `main` 顶端的特定表的更改，我们运行以下查询：
 *
 * <pre>{@code
 * SELECT * FROM DOLT_DIFF_STAT('WORKING', 'main', 'inventory');
 * }</pre>
 * <p>
 * 结果为单行：
 *
 * <pre>{@code
 * +------------+-----------------+------------+--------------+---------------+-------------+---------------+----------------+---------------+---------------+----------------+----------------+
 * | table_name | rows_unmodified | rows_added | rows_deleted | rows_modified | cells_added | cells_deleted | cells_modified | old_row_count | new_row_count | old_cell_count | new_cell_count |
 * +------------+-----------------+------------+--------------+---------------+-------------+---------------+----------------+---------------+---------------+----------------+----------------+
 * | inventory  | 1               | 0          | 1            | 1             | 0           | 6             | 1              | 3             | 2             | 12             | 6              |
 * +------------+-----------------+------------+--------------+---------------+-------------+---------------+----------------+---------------+---------------+----------------+----------------+
 * }</pre>
 */
@MethodInvokeRequiredGroup(value = {"withTable"})
public class DoltDiffStat extends DoltRepository implements DoltTableFunction<DoltDiffStat.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltDiffStat> INSTANCES = new ConcurrentHashMap<>();

    private DoltDiffStat(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltDiffStat getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltDiffStat(connectionManager));
    }

    public static class Params extends AbstractTableFunctionParamBuilder<Params> {

        protected Params(DoltTableFunction<Params> doltTableFunction) {
            super(Params.class, doltTableFunction);
        }

        @MethodMutexGroup({"withTable"})
        @MethodDependsOn({"toRevision", "twoDot", "threeDot"})
        public Params withTable(String table) {
            validator.checkAndMark("withTable");
            addFlags(table);
            return this;
        }

        @MethodMutexGroup({"fromRevision", "twoDot", "threeDot"})
        public Params fromRevision(String fromRevision) {
            validator.checkAndMark("fromRevision");
            addFlags(fromRevision);
            return this;
        }

        @MethodDependsOn({"fromRevision"})
        public Params toRevision(String toRevision) {
            validator.checkAndMark("toRevision");
            addFlags(toRevision);
            return this;
        }

        @MethodMutexGroup({"fromRevision", "twoDot", "threeDot"})
        public Params twoDot(String from, String to) {
            validator.checkAndMark("twoDot");
            addFlags(from + ".." + to);
            return this;
        }

        @MethodMutexGroup({"fromRevision", "twoDot", "threeDot"})
        public Params threeDot(String from, String to) {
            validator.checkAndMark("threeDot");
            addFlags(from + "..." + to);
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
                .fromFunction("dolt_diff_stat")
                .withParams(params)
                .build();
    }

}