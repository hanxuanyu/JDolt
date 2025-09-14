package com.hxuanyu.jdolt.core.systemtable;


import com.hxuanyu.jdolt.manager.DoltConnectionManager;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ## `dolt_schemas`
 *
 * `dolt_schemas` 存储与 dolt 数据库一同进行版本管理的 SQL 模式片段。某些 DDL 语句会修改此表，而该表在某个 SQL 会话中的取值会影响该会话中存在的数据库实体。
 *
 * 此表中的值是与某些模式元素存储相关的实现细节。建议使用内置的 SQL 语句来查看和修改模式，而不是直接使用该表。
 *
 * ### 模式（Schema）
 *
 * <pre>
 * {@code
 * +----------+----------------------------------------+------+-----+---------+-------+
 * | Field    | Type                                   | Null | Key | Default | Extra |
 * +----------+----------------------------------------+------+-----+---------+-------+
 * | type     | varchar(64) COLLATE utf8mb4_0900_ai_ci | NO   | PRI | NULL    |       |
 * | name     | varchar(64) COLLATE utf8mb4_0900_ai_ci | NO   | PRI | NULL    |       |
 * | fragment | longtext                               | YES  |     | NULL    |       |
 * | extra    | json                                   | YES  |     | NULL    |       |
 * +----------+----------------------------------------+------+-----+---------+-------+
 * }
 * </pre>
 *
 * 目前，所有 `VIEW`、`TRIGGER` 和 `EVENT` 的定义都存储在 `dolt_schemas` 表中。`type` 列表示片段是 `view`、`trigger` 还是 `event`。`name` 列是 `CREATE` 语句中提供的片段名称。`fragment` 列存储该片段的 `CREATE` 语句。`json` 列为任何额外的重要信息，例如该片段的 `CreateAt` 字段。
 *
 * 此表中的值在一定程度上是与底层数据库对象实现相关的实现细节。
 *
 * ### 示例查询
 *
 * <pre>
 * {@code
 * CREATE VIEW four AS SELECT 2+2 FROM dual;
 * CREATE TABLE mytable (x INT PRIMARY KEY);
 * CREATE TRIGGER inc_insert BEFORE INSERT ON mytable FOR EACH ROW SET NEW.x = NEW.x + 1;
 * CREATE EVENT monthly_gc ON SCHEDULE EVERY 1 MONTH DO CALL DOLT_GC();
 * }
 * </pre>
 *
 * 然后你可以在 `dolt_schemas` 中查看它们： {% embed url="[https://www.dolthub.com/repositories/dolthub/docs_examples/embed/main?q=select+*+from+dolt_schemas%3B](https://www.dolthub.com/repositories/dolthub/docs_examples/embed/main?q=select+*+from+dolt_schemas%3B)" %}
 */
public class DoltSchemas extends DoltSystemTable {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltSchemas> INSTANCES = new ConcurrentHashMap<>();

    protected DoltSchemas(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltSchemas getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltSchemas(connectionManager));
    }

    @Override
    public Params prepare() {
        return new Params(this).from("dolt_schemas");
    }
}