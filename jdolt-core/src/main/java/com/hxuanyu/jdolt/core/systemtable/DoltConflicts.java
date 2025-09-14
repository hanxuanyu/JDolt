package com.hxuanyu.jdolt.core.systemtable;


import com.hxuanyu.jdolt.manager.DoltConnectionManager;

import java.util.concurrent.ConcurrentHashMap;


/**
 * ## {@code dolt_conflicts}
 *
 * {@code dolt_conflicts} 是一个系统表，其中为工作集中每个存在未解决合并冲突的表提供一行记录。
 *
 * <pre><code class="language-sql">
 * +---------------+-----------------+------+-----+---------+-------+
 * | Field         | Type            | Null | Key | Default | Extra |
 * +---------------+-----------------+------+-----+---------+-------+
 * | table         | text            | NO   | PRI |         |       |
 * | num_conflicts | bigint unsigned | NO   |     |         |       |
 * +---------------+-----------------+------+-----+---------+-------+
 * </code></pre>
 *
 * 在 SQL 会话中解决冲突时查询此表。有关在 SQL 中解决合并冲突的更多信息，请参阅
 * {@code dolt_conflicts_$TABLENAME} 表的文档。
 *
 * ## {@code dolt_conflicts_$TABLENAME}
 *
 * 对于每个在合并后发生冲突的表 {@code $TABLENAME}，都有一个对应的系统表 {@code dolt_conflicts_$TABLENAME}。
 * 每个此类表的模式会为实际表中的每一列生成三列，分别代表冲突行中的 ours、theirs 和 base 的值。
 *
 * 考虑一个模式如下的表 {@code mytable}：
 *
 * <pre><code class="language-sql">
 * +-------+------+------+-----+---------+-------+
 * | Field | Type | Null | Key | Default | Extra |
 * +-------+------+------+-----+---------+-------+
 * | a     | int  | NO   | PRI |         |       |
 * | b     | int  | YES  |     |         |       |
 * +-------+------+------+-----+---------+-------+
 * </code></pre>
 *
 * 如果我们尝试一次导致该表产生冲突的合并，动态生成的表会展示冲突的详细信息：
 *
 * <pre><code class="language-sql">
 * mydb&gt; select * from dolt_conflicts_mytable;
 * +----------------------------------+--------+--------+-------+-------+---------------+---------+---------+-----------------+------------------------+
 * | from_root_ish                    | base_a | base_b | our_a | our_b | our_diff_type | their_a | their_b | their_diff_type | dolt_conflict_id       |
 * +----------------------------------+--------+--------+-------+-------+---------------+---------+---------+-----------------+------------------------+
 * | gip4h957r8k07c9414lkp3sqe7rh9an6 | NULL   | NULL   | 3     | 3     | added         | 3       | 1       | added           | hWDLmYufTrm+eVjFSVzPWw |
 * | gip4h957r8k07c9414lkp3sqe7rh9an6 | NULL   | NULL   | 4     | 4     | added         | 4       | 2       | added           | gi2p1YbSwu8oUV/WRSpr3Q |
 * +----------------------------------+--------+--------+-------+-------+---------------+---------+---------+-----------------+------------------------+
 * </code></pre>
 *
 * 该表对于原表中每一条存在冲突的行都有一行记录。每个列会出现三次，分别代表 base、{@code ours} 和 {@code theirs} 的值。
 * 此外，{@code our_diff_type} 和 {@code their_diff_type} 列指示该行在相应分支中是“added”（新增）、“modified”（修改）还是“removed”（删除）。
 * {@code dolt_conflict_id} 列对每一行都是唯一的，可用于在编写自动冲突解决应用程序时标识特定冲突。
 * 最后，第一列 {@code from_root_ish} 是发生合并时数据库根的 ID。用户代码通常会忽略该列。
 *
 * 为简化操作，你可以仅查询感兴趣的列：
 *
 * <pre><code class="language-sql">
 * mydb&gt; select dolt_conflict_id, base_a, base_b, our_a, our_b, their_a, their_b from dolt_conflicts_mytable;
 * +------------------------+--------+--------+-------+-------+---------+---------+
 * | dolt_conflict_id       | base_a | base_b | our_a | our_b | their_a | their_b |
 * +------------------------+--------+--------+-------+-------+---------+---------+
 * | hWDLmYufTrm+eVjFSVzPWw | NULL   | NULL   | 3     | 3     | 3       | 1       |
 * | gi2p1YbSwu8oUV/WRSpr3Q | NULL   | NULL   | 4     | 4     | 4       | 2       |
 * +------------------------+--------+--------+-------+-------+---------+---------+
 * </code></pre>
 *
 * 要将冲突标记为已解决，从对应的表中删除它们即可。若要保留所有 {@code our} 的值，只需运行：
 *
 * <pre><code class="language-sql">
 * mydb&gt; delete from dolt_conflicts_mytable;
 * </code></pre>
 *
 * 如果我想保留所有 {@code their} 的值，我会先运行以下语句：
 *
 * <pre><code class="language-sql">
 * mydb&gt; replace into mytable (select their_a, their_b from dolt_conflicts_mytable);
 * </code></pre>
 *
 * 为方便起见，你也可以修改 {@code dolt_conflicts_mytable} 的 {@code our_} 列，从而更新 {@code mytable} 中对应的行。
 * 上述 replace 语句可改写为：
 *
 * <pre><code class="language-sql">
 * mydb&gt; update dolt_conflicts_mytable set our_a = their_a, our_b = their_b;
 * </code></pre>
 *
 * 当然，你也可以在这些语句中使用任意组合的 {@code ours}、{@code theirs} 和 {@code base} 行。
 *
 * 备注
 * - 对 {@code our_} 列所做的更新会使用主键（或无键表的哈希）应用到原始表。如果该行不存在，将被插入。然而，对 {@code our_} 列的更新永远不会删除行。
 * - {@code dolt_conflict_id} 是冲突的唯一标识符。在需要自动解决冲突的软件中尤为有用。
 * - {@code from_root_ish} 是合并“来源分支”的提交哈希。该哈希可用于识别是哪一次合并产生了冲突，因为冲突可能在多次合并中累积。
 */
public class DoltConflicts extends DoltSystemTableWithSuffix {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltConflicts> INSTANCES = new ConcurrentHashMap<>();

    protected DoltConflicts(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltConflicts getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltConflicts(connectionManager));
    }

    @Override
    public Params prepare() {
        return new Params(this).from("dolt_conflicts");
    }
}