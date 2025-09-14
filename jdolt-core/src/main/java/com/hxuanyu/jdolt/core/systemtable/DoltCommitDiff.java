package com.hxuanyu.jdolt.core.systemtable;


import com.hxuanyu.jdolt.manager.DoltConnectionManager;

import java.util.concurrent.ConcurrentHashMap;


/**
 * ## {@code dolt_commit_diff_$TABLENAME}
 *
 * 对于每一个名为 {@code $TABLENAME} 的用户表，都会有一个只读的系统表 {@code dolt_commit_diff_$TABLENAME}，可用于查询数据库中任意两个提交（commit）之间该表的数据差异。比如，你可以通过这个系统表查看不同分支上两个提交之间的差异。该系统表返回的数据模式（schema）基于当前检出分支上底层用户表的模式。
 *
 * 在对该系统表的所有查询中，你必须提供 {@code from_commit} 和 {@code to_commit}，以指定需要对比的数据起点与终点。返回的每一行描述底层用户表中某一行从 {@code from_commit} 引用到 {@code to_commit} 引用发生的变化，展示旧值与新值。
 *
 * {@code dolt_commit_diff_$TABLENAME} 相当于 {@code dolt diff} 命令行（CLI）的同类。它表示所提供两个提交之间的<a href="https://git-scm.com/book/en/v2/Git-Tools-Revision-Selection#double_dot">双点差异（two-dot diff）</a>。{@code dolt_diff_$TABLENAME} 系统表也提供差异信息，但它不是二元对比，而是返回当前分支历史中相邻提交之间的逐次差异日志。换句话说，如果某一行在 10 次提交中被更改过，{@code dolt_diff_$TABLENAME} 将显示 10 行记录——每行对应一次增量变更。相反，{@code dolt_commit_diff_$TABLENAME} 只会显示一行，将所有单次提交的增量差异合并为一次整体对比。
 *
 * 当一个表在 {@code to} 和 {@code from} 提交之间发生了模式变更时，{@code DOLT_DIFF()} 表函数是 {@code dolt_commit_diff_$tablename} 系统表的替代方案。如果你需要分别查看这两个提交的各自模式，而不是使用当前检出分支的模式，请考虑使用 {@code DOLT_DIFF()} 表函数。
 *
 * <h3>模式（Schema）</h3>
 *
 * <pre><code class="text">
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
 * </code></pre>
 *
 * 其余列取决于当前检出分支上用户表的模式。对于当前检出分支中表的每一列 {@code X}，结果集中都会有名为 {@code from_X} 和 {@code to_X} 的列，类型与当前模式中的 {@code X} 相同。查询中必须同时指定 {@code from_commit} 和 {@code to_commit} 参数，否则会返回错误。
 *
 * <h3>示例模式</h3>
 *
 * 考虑一个只有一列的简单示例表：
 *
 * <pre><code class="text">
 * +--------------+
 * | field | type |
 * +--------------+
 * | x     | int  |
 * +--------------+
 * </code></pre>
 *
 * 基于上述表的模式，{@code dolt_commit_diff_$TABLENAME} 的模式将是：
 *
 * <pre><code class="text">
 * +------------------+----------+
 * | field            | type     |
 * +------------------+----------+
 * | to_x             | int      |
 * | to_commit        | longtext |
 * | to_commit_date   | datetime |
 * | from_x           | int      |
 * | from_commit      | longtext |
 * | from_commit_date | datetime |
 * | diff_type        | varchar  |
 * +------------------+----------+
 * </code></pre>
 *
 * <h3>查询细节</h3>
 *
 * 考虑如下分支结构：
 *
 * <pre><code class="text">
 *       A---B---C feature
 *      /
 * D---E---F---G main
 * </code></pre>
 *
 * 我们可以用上面的表来表示两种类型的差异：两点差异和三点差异。在两点差异中，我们希望查看点 C 与点 G 之间行数据的差异。
 *
 * {@code
 * https://www.dolthub.com/repositories/dolthub/docs_examples/embed/main?q=SELECT+*+from+dolt_commit_diff_mytable+where+to_commit%3DHASHOF%28%27feature%27%29+and+from_commit+%3D+HASHOF%28%27main%27%29%3B
 * }
 *
 * 我们也可以使用该表计算三点差异。在三点差异中，我们希望查看功能分支相对于共同祖先 E 的分歧，而不包含 main 上 F 和 G 的更改。
 *
 * {@code
 * https://www.dolthub.com/repositories/dolthub/docs_examples/embed/main?q=SELECT+*+from+dolt_commit_diff_mytable+where+to_commit%3DHASHOF%28%27feature%27%29+and+from_commit%3Ddolt_merge_base%28%27main%27%2C+%27feature%27%29%3B
 * }
 *
 * {@code dolt_merge_base} function 会计算 {@code main} 与 {@code feature} 之间最近的祖先 E。
 *
 * <h3>额外说明</h3>
 *
 * {@code to_commit} 有一个特殊的取值 {@code WORKING}，可用于查看工作集（working set）中尚未提交到 HEAD 的更改。通常可以使用 {@code HASHOF()} 来获取某个分支或其祖先提交的提交哈希。上述表要求同时填充 {@code from_commit} 和 {@code to_commit}。
 */
public class DoltCommitDiff extends DoltSystemTableWithSuffix{
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltCommitDiff> INSTANCES = new ConcurrentHashMap<>();

    protected DoltCommitDiff(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltCommitDiff getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltCommitDiff(connectionManager));
    }

    @Override
    public Params prepare() {
        return new Params(this).from("dolt_commit_diff");
    }
}