package com.hxuanyu.jdolt.core.systemtable;


import com.hxuanyu.jdolt.manager.DoltConnectionManager;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ## {@code dolt_diff}
 *
 * {@code dolt_diff} 系统表显示从当前活动分支的 HEAD 可达的每一次提交中，当前数据库里哪些表发生了更改。若单次提交更改了多个表，则 {@code dolt_diff} 系统表中会为每个表各有一行记录，这些行的提交哈希相同。工作集中任何已暂存或未暂存的更改也会包含在内，其 {@code commit_hash} 值为 {@code WORKING}。确认某次提交中发生更改的表后，可以使用对应的 {@code dolt_diff_$TABLENAME} 系统表来确定各表的数据更改内容。
 *
 * <h3>模式（Schema）</h3>
 *
 * {@code DOLT_DIFF} 系统表包含以下列：
 *
 * <pre><code class="language-text">
 * +---------------+----------+
 * | field         | Type     |
 * +---------------+----------+
 * | commit_hash   | text     |
 * | table_name    | text     |
 * | committer     | text     |
 * | email         | text     |
 * | date          | datetime |
 * | message       | text     |
 * | data_change   | boolean  |
 * | schema_change | boolean  |
 * +---------------+----------+
 * </code></pre>
 *
 * <h3>查询细节</h3>
 *
 * {@code dolt_diff} 展示自当前分支 HEAD 起的更改，包括任何工作集中的更改。如果某次提交没有对表做任何更改（例如空提交），则不会出现在 {@code dolt_diff} 的结果中。
 *
 * <h3>示例查询</h3>
 *
 * 以来自 <a href="https://www.dolthub.com/">DoltHub</a> 的
 * <a href="https://www.dolthub.com/repositories/dolthub/first-hour-db">{@code dolthub/first-hour-db}</a>
 * 数据库为例，下面的查询使用 {@code dolt_diff} 系统表来查找 2022 年 4 月份期间所有的提交及其更改的表。
 *
 * <pre><code class="language-text">
 * {% embed url="[https://www.dolthub.com/repositories/dolthub/first-hour-db/embed/main?q=SELECT+commit_hash%2C+table_name%2C+schema_change%0AFROM+++dolt_diff%0AWHERE++date+BETWEEN+%222022-04-01%22+AND+%222022-04-30%22%3B%0A](https://www.dolthub.com/repositories/dolthub/first-hour-db/embed/main?q=SELECT+commit_hash%2C+table_name%2C+schema_change%0AFROM+++dolt_diff%0AWHERE++date+BETWEEN+%222022-04-01%22+AND+%222022-04-30%22%3B%0A)" %}
 * </code></pre>
 *
 * 从这些结果中，我们可以看到在 2022 年 4 月对该数据库有四次提交。提交 {@code 	224helo} 只更改了 {@code dolt_schemas} 表，提交 {@code 7jrvg1a} 更改了 {@code dolt_docs} 表，而提交 {@code 5jpgb0f} 更改了两个表。我们还可以看到这些表中哪些发生了模式更改，哪些只是数据更改。
 *
 * 为了更深入地查看这些更改，我们可以查询每个已更改表对应的 {@code dolt_diff_$TABLE} 系统表，如下所示：
 *
 * <pre><code class="language-text">
 * {% embed url="[https://www.dolthub.com/repositories/dolthub/first-hour-db/embed/main?q=SELECT+count%28*%29+as+total_rows_changed%0AFROM+++dolt_diff_dolt_schemas%0AWHERE++to_commit%3D%27224helolb2bg6iqrf9b7befrflehqgnb%27%3B%0A](https://www.dolthub.com/repositories/dolthub/first-hour-db/embed/main?q=SELECT+count%28*%29+as+total_rows_changed%0AFROM+++dolt_diff_dolt_schemas%0AWHERE++to_commit%3D%27224helolb2bg6iqrf9b7befrflehqgnb%27%3B%0A)" %}
 * </code></pre>
 */
public class DoltDiff extends DoltSystemTableWithSuffix {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltDiff> INSTANCES = new ConcurrentHashMap<>();

    protected DoltDiff(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltDiff getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltDiff(connectionManager));
    }

    @Override
    public Params prepare() {
        return new Params(this).from("dolt_diff");
    }
}