package com.hxuanyu.jdolt.core.systemtable;


import com.hxuanyu.jdolt.manager.DoltConnectionManager;

import java.util.concurrent.ConcurrentHashMap;
/**
 * ## {@code dolt_tags}
 *
 * {@code dolt_tags} 显示当前数据库中所有活动标签的信息。
 *
 * 可以使用 {@link DOLT_TAG()} 存储过程在 {@code dolt_tags} 表上执行 INSERT 和 DELETE 操作以管理标签。
 *
 * ### 模式（Schema）
 *
 * <pre>
 * +----------+----------+------+-----+---------+-------+
 * | Field    | Type     | Null | Key | Default | Extra |
 * +----------+----------+------+-----+---------+-------+
 * | tag_name | text     | NO   | PRI | NULL    |       |
 * | tag_hash | text     | NO   | PRI | NULL    |       |
 * | tagger   | text     | NO   |     | NULL    |       |
 * | email    | text     | NO   |     | NULL    |       |
 * | date     | datetime | NO   |     | NULL    |       |
 * | message  | text     | NO   |     | NULL    |       |
 * +----------+----------+------+-----+---------+-------+
 * </pre>
 *
 * ### 示例查询
 *
 * 使用 dolt_tag() 存储过程创建一个标签。
 *
 * <pre><code class="language-sql">
 * CALL DOLT_TAG('_migrationtest','head','-m','savepoint for migration testing');
 * </code></pre>
 *
 * <pre>
 * +--------+
 * | status |
 * +--------+
 * | 0      |
 * +--------+
 * </pre>
 *
 * 获取所有标签。
 *
 * <pre>
 * {% embed url="[https://www.dolthub.com/repositories/dolthub/first-hour-db/embed/main?q=SELECT+*+FROM+dolt_tags%3B](https://www.dolthub.com/repositories/dolthub/first-hour-db/embed/main?q=SELECT+*+FROM+dolt_tags%3B)" %}
 * </pre>
 */
public class DoltTags extends DoltSystemTable {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltTags> INSTANCES = new ConcurrentHashMap<>();

    protected DoltTags(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltTags getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltTags(connectionManager));
    }

    @Override
    public Params prepare() {
        return new Params(this).from("dolt_tags");
    }
}