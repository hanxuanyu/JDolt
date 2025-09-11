package com.hxuanyu.jdolt.core.systemtable;


import com.hxuanyu.jdolt.manager.DoltConnectionManager;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ## `dolt_docs`
 *
 * `dolt_docs` 存储 Dolt 文档（`LICENSE.md`、`README.md`）的内容。
 *
 * 你可以通过 SQL 修改这些文件的内容，但不能保证这些更改会反映到磁盘上的文件中。
 *
 * ### 架构
 *
 * <pre>{@code
 * +----------+------+
 * | field    | type |
 * +----------+------+
 * | doc_name | text |
 * | doc_text | text |
 * +----------+------+
 * }</pre>
 *
 * ### 示例
 *
 * 获取所有文档。
 *
 * {% embed url="https://www.dolthub.com/repositories/dolthub/first-hour-db/embed/main?q=select+*+from+dolt_docs%3B" %}
 */
public class DoltDocs extends DoltSystemTable {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltDocs> INSTANCES = new ConcurrentHashMap<>();

    protected DoltDocs(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltDocs getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltDocs(connectionManager));
    }

    @Override
    public Params prepare() {
        return new Params(this).from("dolt_docs");
    }
}