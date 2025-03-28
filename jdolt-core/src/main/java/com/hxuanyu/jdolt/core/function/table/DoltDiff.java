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
 * `DOLT_DIFF()` 表函数用于计算数据库中任意两个提交点之间的表数据差异。
 * <br>结果集中的每一行描述了底层表中某一行在两个提交点之间的变化，
 * 包括该行在起始提交点和目标提交点的值以及变化类型（即 `added`、`modified` 或 `removed`）。
 * <br>`DOLT_DIFF()` 是 {@link dolt-system-tables.md#dolt_commit_diff_usdtablename dolt_commit_diff_$tablename} 系统表的替代方案。
 * <br>在可能的情况下，通常应优先使用系统表，因为它们的使用限制较少。
 * 然而，对于某些用例，例如查看包含模式更改的表数据差异或查看
 * <a href="https://www.dolthub.com/blog/2022-11-11-two-and-three-dot-diff-and-log/#three-dot-diff">三点差异</a>，使用 `DOLT_DIFF` 表函数可能更为直观。
 * <p>
 * `DOLT_DIFF()` 表函数与 `dolt_commit_diff_$tablename` 系统表的主要区别在于返回结果的模式。
 * <br>`dolt_commit_diff_$tablename` 根据当前检出的分支上的表模式生成结果模式，
 * 而 `DOLT_DIFF()` 会使用 `from_commit` 的模式生成 `from_` 列，并使用 `to_commit` 的模式生成 `to_` 列。
 * <br>这使得在底层表模式发生更改的情况下，更容易查看差异。
 *
 * <p>请注意，`DOLT_DIFF()` 表函数目前要求参数值必须是字面值。
 *
 * <p>### 参数选项
 *
 * <pre>{@code
 * DOLT_DIFF(<from_revision>, <to_revision>, <tablename>)
 * DOLT_DIFF(<from_revision..to_revision>, <tablename>)
 * DOLT_DIFF(<from_revision...to_revision>, <tablename>)
 * }</pre>
 * <p>
 * `DOLT_DIFF()` 表函数接受两个或三个必需参数：
 * <ul>
 *   <li>`from_revision` — 差异起始点的表数据修订版本。这可以是提交、标签、分支名称或其他修订标识符（例如 "main~"）。
 *   <li>`to_revision` — 差异终止点的表数据修订版本。这可以是提交、标签、分支名称或其他修订标识符（例如 "main~"）。
 *   <li>`from_revision..to_revision` — 获取两个点差异，即 `from_revision` 和 `to_revision` 之间的表数据修订差异。
 *       这等价于 `dolt_diff(<from_revision>, <to_revision>, <tablename>)`。
 *   <li>`from_revision...to_revision` — 获取三点差异，即从最后一个共同提交点开始的 `from_revision` 和 `to_revision` 之间的表数据修订差异。
 *   <li>`tablename` — 包含需要比较数据的表名称。
 * </ul>
 *
 * <p>### 模式
 *
 * <pre>{@code
 * +------------------+----------+
 * | field            | type     |
 * +------------------+----------+
 * | from_commit      | TEXT     |
 * | from_commit_date | DATETIME |
 * | to_commit        | TEXT     |
 * | to_commit_date   | DATETIME |
 * | diff_type        | TEXT     |
 * | other cols       |          |
 * +------------------+----------+
 * }</pre>
 * <p>
 * 其余列取决于用户表在 `from_commit` 和 `to_commit` 时的模式。
 * <br>对于 `from_commit` 修订版本中表的每一列 `X`，结果集中会有一个名为 `from_X` 的列。
 * 同样，对于 `to_commit` 修订版本中表的每一列 `Y`，结果集中会有一个名为 `to_Y` 的列。
 * 这是 `DOLT_DIFF()` 表函数与 `dolt_commit_diff_$tablename` 系统表的主要区别——
 * `DOLT_DIFF()` 使用 `to_commit` 和 `from_commit` 修订版本的两个模式来形成结果集中的 `to_` 和 `from_` 列，
 * 而 `dolt_commit_diff_$tablename` 仅使用当前检出分支的表模式来形成结果集中的 `to_` 和 `from_` 列。
 *
 * <p>### 示例
 * <p>
 * 假设数据库中有一个名为 `inventory` 的表，并且有两个分支：`main` 和 `feature_branch`。我们可以使用 `DOLT_DIFF()` 函数计算从 `main` 分支到 `feature_branch` 分支的表数据差异，以查看功能分支上的数据更改。
 * <p>
 * 以下是 `main` 分支顶端的 `inventory` 表模式：
 *
 * <pre>{@code
 * +----------+------+
 * | field    | type |
 * +----------+------+
 * | pk       | int  |
 * | name     | text |
 * | quantity | int  |
 * +----------+------+
 * }</pre>
 * <p>
 * 以下是 `feature_branch` 分支顶端的 `inventory` 表模式：
 *
 * <pre>{@code
 * +----------+------+
 * | field    | type |
 * +----------+------+
 * | pk       | int  |
 * | name     | text |
 * | color    | text |
 * | size     | int  |
 * +----------+------+
 * }</pre>
 * <p>
 * 基于上述两个版本的模式，`DOLT_DIFF()` 的结果模式将为：
 *
 * <pre>{@code
 * +------------------+----------+
 * | field            | type     |
 * +------------------+----------+
 * | from_pk          | int      |
 * | from_name        | text     |
 * | from_quantity    | int      |
 * | from_commit      | TEXT     |
 * | from_commit_date | DATETIME |
 * | to_pk            | int      |
 * | to_name          | text     |
 * | to_color         | text     |
 * | to_size          | int      |
 * | to_commit        | TEXT     |
 * | to_commit_date   | DATETIME |
 * | diff_type        | text     |
 * +------------------+----------+
 * }</pre>
 * <p>
 * 要计算差异并查看结果，我们可以运行以下查询：
 *
 * <pre>{@code
 * SELECT * FROM DOLT_DIFF("main", "feature_branch", "inventory")
 * }</pre>
 * <p>
 * `DOLT_DIFF()` 的结果显示了从 `main` 到 `feature_branch` 的数据更改：
 *
 * <pre>{@code
 * +---------+-------+---------+----------+----------------+-----------------------------------+-----------+---------+---------------+-------------+-----------------------------------+-----------+
 * | to_name | to_pk | to_size | to_color | to_commit      | to_commit_date                    | from_name | from_pk | from_quantity | from_commit | from_commit_date                  | diff_type |
 * +---------+-------+---------+----------+----------------+-----------------------------------+-----------+---------+---------------+-------------+-----------------------------------+-----------+
 * | shirt   | 1     | 15      | false    | feature_branch | 2022-03-23 18:57:38.476 +0000 UTC | shirt     | 1       | 70            | main        | 2022-03-23 18:51:48.333 +0000 UTC | modified  |
 * | shoes   | 2     | 9       | brown    | feature_branch | 2022-03-23 18:57:38.476 +0000 UTC | shoes     | 2       | 200           | main        | 2022-03-23 18:51:48.333 +0000 UTC | modified  |
 * | pants   | 3     | 30      | blue     | feature_branch | 2022-03-23 18:57:38.476 +0000 UTC | pants     | 3       | 150           | main        | 2022-03-23 18:51:48.333 +0000 UTC | modified  |
 * | hat     | 4     | 6       | grey     | feature_branch | 2022-03-23 18:57:38.476 +0000 UTC | NULL      | NULL    | NULL          | main        | 2022-03-23 18:51:48.333 +0000 UTC | added     |
 * +---------+-------+---------+----------+----------------+-----------------------------------+-----------+---------+---------------+-------------+-----------------------------------+-----------+
 * }</pre>
 *
 * <p>#### 三点差异 `DOLT_DIFF`
 * <p>
 * 假设上述数据库的提交图如下所示：
 *
 * <pre>{@code
 * A - B - C - D (main)
 *          \
 *           E - F (feature_branch)
 * }</pre>
 * <p>
 * 上例获取了两个点差异，即 `main` 和 `feature_branch` 两个修订版本之间的差异。
 * <br>`dolt_diff('main', 'feature_branch', 'inventory')`（等价于 `dolt_diff('main..feature_branch', 'inventory')`）
 * 输出从 F 到 D 的差异（即包含 E 和 F 的影响）。
 * <p>
 * 三点差异用于显示功能分支从主分支*分叉点*开始引入的差异。
 * 三点差异通常用于显示拉取请求的差异。
 * <p>
 * 因此，`dolt_diff('main...feature_branch')` 仅输出 `feature_branch` 中的差异（即 E 和 F）。
 *
 * <p>了解更多关于两点与三点差异的内容，请参考
 * <a href="https://www.dolthub.com/blog/2022-11-11-two-and-three-dot-diff-and-log">此处</a>。
 */
@MethodInvokeRequiredGroup(value = {"withTable"})
public class DoltDiff extends DoltRepository implements DoltTableFunction<DoltDiff.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltDiff> INSTANCES = new ConcurrentHashMap<>();

    private DoltDiff(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltDiff getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltDiff(connectionManager));
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
                .fromFunction("dolt_diff")
                .withParams(params)
                .build();
    }

}