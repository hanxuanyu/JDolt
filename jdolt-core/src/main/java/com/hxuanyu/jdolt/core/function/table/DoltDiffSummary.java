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
 * <h2>DOLT_DIFF_SUMMARY()</h2>
 *
 * <p><em>之前版本的<code>dolt_diff_summary</code>已重命名为<code>dolt_diff_stat</code>。</em></p>
 *
 * <p>
 * <code>DOLT_DIFF_SUMMARY()</code> 表函数用于总结在数据库中任意两个提交之间发生变化的表及其变化方式。
 * 结果中只会列出发生变化的表，并显示差异类型（“新增”、“删除”、“修改”、“重命名”）以及是否存在数据和模式的变化。
 * </p>
 *
 * <p>
 * <code>DOLT_DIFF_SUMMARY()</code> 的功能类似于
 * <a href="../../cli/cli.md#dolt-diff">CLI dolt diff --summary 命令</a>，
 * 但使用 <code>DOLT_DIFF_SUMMARY()</code> 表函数时需要指定两个提交，表名则为可选项。
 * 如果没有任何表发生变化，将返回空结果。
 * </p>
 *
 * <p>
 * 请注意，<code>DOLT_DIFF()</code> 表函数当前要求参数值必须是字面值。
 * </p>
 *
 * <h3>权限</h3>
 * <p>
 * 如果未定义表名，<code>DOLT_DIFF_SUMMARY()</code> 表函数需要对所有表具有 <code>SELECT</code> 权限；
 * 如果定义了表名，则仅需对该表具有 <code>SELECT</code> 权限。
 * </p>
 *
 * <h3>使用方法</h3>
 *
 * <pre>{@code
 * DOLT_DIFF_SUMMARY(<from_revision>, <to_revision>, <optional_tablename>)
 * DOLT_DIFF_SUMMARY(<from_revision..to_revision>, <optional_tablename>)
 * DOLT_DIFF_SUMMARY(<from_revision...to_revision>, <optional_tablename>)
 * }</pre>
 *
 * <p>
 * <code>DOLT_DIFF_SUMMARY()</code> 表函数接受三个参数：
 * </p>
 * <ul>
 *   <li><code>from_revision</code> — 差异开始的表数据修订版本。此参数为必填项，可以是提交、标签、分支名或其他修订版本说明符（例如 "main~"，"WORKING"，"STAGED"）。</li>
 *   <li><code>to_revision</code> — 差异结束的表数据修订版本。此参数为必填项，可以是提交、标签、分支名或其他修订版本说明符（例如 "main~"，"WORKING"，"STAGED"）。</li>
 *   <li><code>from_revision..to_revision</code> — 获取两个点的差异摘要，即 <code>from_revision</code> 和 <code>to_revision</code> 之间的表数据修订版本。这等同于 <code>dolt_diff_summary(&lt;from_revision&gt;, &lt;to_revision&gt;, &lt;tablename&gt;)</code>。</li>
 *   <li><code>from_revision...to_revision</code> — 获取三个点的差异摘要，即 <code>from_revision</code> 和 <code>to_revision</code> 之间的表数据修订版本，<em>从最后一个共同提交开始</em>。</li>
 *   <li><code>tablename</code> — 需要比较数据差异的表名。此参数为可选项。如果未定义，将返回所有有数据差异的表。</li>
 * </ul>
 *
 * <h3>数据结构</h3>
 *
 * <pre>{@code
 * +-----------------+---------+
 * | 字段            | 类型    |
 * +-----------------+---------+
 * | from_table_name | TEXT    |
 * | to_table_name   | TEXT    |
 * | diff_type       | TEXT    |
 * | data_change     | BOOLEAN |
 * | schema_change   | BOOLEAN |
 * +-----------------+---------+
 * }</pre>
 *
 * <h3>示例</h3>
 *
 * <p>
 * 假设我们从 <code>main</code> 分支的数据库中有一个名为 <code>inventory</code> 的表开始。
 * 当我们进行任何更改时，可以使用 <code>DOLT_DIFF_SUMMARY()</code> 函数计算表数据的差异，
 * 或者查看特定提交之间所有发生数据变化的表。
 * </p>
 * <p>
 * 以下是 <code>main</code> 分支末端 <code>inventory</code> 表的结构：
 * </p>
 * <pre>{@code
 * +----------+-------------+------+-----+---------+-------+
 * | 字段     | 类型        | 空值 | 键  | 默认值  | 额外  |
 * +----------+-------------+------+-----+---------+-------+
 * | pk       | int         | NO   | PRI | NULL    |       |
 * | name     | varchar(50) | YES  |     | NULL    |       |
 * | quantity | int         | YES  |     | NULL    |       |
 * +----------+-------------+------+-----+---------+-------+
 * }</pre>
 *
 * <p>
 * 以下是 <code>main</code> 分支末端 <code>inventory</code> 表的数据：
 * </p>
 * <pre>{@code
 * +----+-------+----------+
 * | pk | name  | quantity |
 * +----+-------+----------+
 * | 1  | shirt | 15       |
 * | 2  | shoes | 10       |
 * +----+-------+----------+
 * }</pre>
 *
 * <p>
 * 我们对 <code>inventory</code> 表进行了一些更改，并创建了一个新的无主键表：
 * </p>
 * <pre>{@code
 * ALTER TABLE inventory ADD COLUMN color VARCHAR(10);
 * INSERT INTO inventory VALUES (3, 'hat', 6, 'red');
 * UPDATE inventory SET quantity=0 WHERE pk=1;
 * CREATE TABLE items (name varchar(50));
 * }</pre>
 *
 * <p>
 * 以下是当前工作集中的 <code>inventory</code> 表数据：
 * </p>
 * <pre>{@code
 * +----+-------+----------+-------+
 * | pk | name  | quantity | color |
 * +----+-------+----------+-------+
 * | 1  | shirt | 0        | NULL  |
 * | 2  | shoes | 10       | NULL  |
 * | 3  | hat   | 6        | red   |
 * +----+-------+----------+-------+
 * }</pre>
 *
 * <p>
 * 要计算差异并查看结果，我们运行以下查询：
 * </p>
 * <pre>{@code
 * SELECT * FROM DOLT_DIFF_SUMMARY('main', 'WORKING');
 * }</pre>
 *
 * <p>
 * <code>DOLT_DIFF_SUMMARY()</code> 的结果显示了从 <code>main</code> 分支末端到当前工作集中数据的变化：
 * </p>
 * <pre>{@code
 * +-----------------+---------------+-----------+-------------+---------------+
 * | from_table_name | to_table_name | diff_type | data_change | schema_change |
 * +-----------------+---------------+-----------+-------------+---------------+
 * | inventory       | inventory     | modified  | true        | true          |
 * | items           | items         | added     | false       | true          |
 * +-----------------+---------------+-----------+-------------+---------------+
 * }</pre>
 *
 * <p>
 * 要查看从当前工作集到 <code>main</code> 分支末端特定表的变化，我们运行以下查询：
 * </p>
 * <pre>{@code
 * SELECT * FROM DOLT_DIFF_SUMMARY('WORKING', 'main', 'inventory');
 * }</pre>
 *
 * <p>
 * 结果仅包含一行：
 * </p>
 * <pre>{@code
 * +-----------------+---------------+-----------+-------------+---------------+
 * | from_table_name | to_table_name | diff_type | data_change | schema_change |
 * +-----------------+---------------+-----------+-------------+---------------+
 * | inventory       | inventory     | modified  | true        | true          |
 * +-----------------+---------------+-----------+-------------+---------------+
 * }</pre>
 */
@MethodInvokeRequiredGroup(value = {"withTable"})
public class DoltDiffSummary extends DoltRepository implements DoltTableFunction<DoltDiffSummary.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltDiffSummary> INSTANCES = new ConcurrentHashMap<>();

    private DoltDiffSummary(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltDiffSummary getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltDiffSummary(connectionManager));
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
                .fromFunction("DOLT_DIFF_SUMMARY")
                .withParams(params)
                .build();
    }

}
