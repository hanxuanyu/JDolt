package com.hxuanyu.jdolt.core.systemtable;


import com.hxuanyu.jdolt.manager.DoltConnectionManager;

import java.util.concurrent.ConcurrentHashMap;


/**
 * ## {@code dolt_query_catalog}
 *
 * {@code dolt_query_catalog} 系统表用于存储数据库的命名查询。与 Dolt 中存储的所有数据一样，这些命名查询会与数据一起进行版本管理，因此在你创建、修改或删除命名查询之后，需要提交（commit）该变更才能保存。你可以使用 Dolt CLI 保存和执行命名查询，也可以直接通过 {@code dolt_query_catalog} 系统表来新增、修改或删除命名查询。所有命名查询都会显示在你数据库的 {@link https://www.dolthub.com/ DoltHub} 的 Queries 标签页中。
 *
 * <h3>模式（Schema）</h3>
 *
 * <pre>
 * +---------------+-----------------+------+-----+---------+-------+
 * | Field         | Type            | Null | Key | Default | Extra |
 * +---------------+-----------------+------+-----+---------+-------+
 * | id            | varchar(16383)  | NO   | PRI |         |       |
 * | display_order | bigint unsigned | NO   |     |         |       |
 * | name          | varchar(16383)  | YES  |     |         |       |
 * | query         | varchar(16383)  | YES  |     |         |       |
 * | description   | varchar(16383)  | YES  |     |         |       |
 * +---------------+-----------------+------+-----+---------+-------+
 * </pre>
 *
 * <h3>示例查询</h3>
 *
 * 以 DoltHub 上的 {@code dolthub/docs_examples} 仓库为例，你可以通过 CLI 创建一个命名查询，或直接向 {@code dolt_query_catalog} 表插入一条记录。
 *
 * <pre><code class="language-shell">
 * &gt; dolt sql -q "select * from tablename" -s "select all" -m "Query to select all records from tablename"
 * </code></pre>
 *
 * 创建命名查询后，可以在 {@code dolt_query_catalog} 表中查看：
 *
 * {@code https://www.dolthub.com/repositories/dolthub/docs_examples/embed/main?q=select+*+from+dolt_query_catalog%3B}
 *
 * 然后你可以使用 dolt CLI 来执行它：
 *
 * <pre><code class="language-shell">
 * &gt; dolt sql -x "Large Irises"
 * Executing saved query 'Large Irises':
 * select distinct(class) from classified_measurements where petal_length_cm &gt; 5
 * +------------+
 * | class)     |
 * +------------+
 * | versicolor |
 * | virginica  |
 * +------------+
 * </code></pre>
 *
 * 最后，如果你希望持久化该命名查询，请务必提交对 {@code dolt_query_catalog} 表的修改。
 *
 * <pre><code class="language-shell">
 * dolt add dolt_query_catalog
 * dolt commit -m "Adding new named query"
 * </code></pre>
 */
public class DoltQueryCatalog extends DoltSystemTable {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltQueryCatalog> INSTANCES = new ConcurrentHashMap<>();

    protected DoltQueryCatalog(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltQueryCatalog getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltQueryCatalog(connectionManager));
    }

    @Override
    public Params prepare() {
        return new Params(this).from("dolt_query_catalog");
    }
}