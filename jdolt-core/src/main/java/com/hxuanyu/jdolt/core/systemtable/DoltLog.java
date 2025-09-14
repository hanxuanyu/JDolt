package com.hxuanyu.jdolt.core.systemtable;


import com.hxuanyu.jdolt.manager.DoltConnectionManager;

import java.util.concurrent.ConcurrentHashMap;
/**
 * dolt_log
 *
 * dolt_log 系统表包含从当前 HEAD 可达的所有提交的提交日志。其数据与 dolt log CLI 命令返回的数据相同。
 *
 * <h3>架构（Schema）</h3>
 *
 * <pre>
 * +--------------+----------+
 * | field        | type     |
 * +--------------+--------- +
 * | commit_hash  | text     |
 * | committer    | text     |
 * | email        | text     |
 * | date         | datetime |
 * | message      | text     |
 * | commit_order | int      |
 * +--------------+--------- +
 * </pre>
 *
 * commit_order 字段是一个整数值，表示从 HEAD 开始按降序排列的提交顺序。注意，对于提交图的不同拓扑排序层级，commit_order 值可能会重复。
 *
 * <h3>示例查询</h3>
 *
 * 以下查询显示自 2022 年 4 月以来，由用户 jennifersp 创建且从当前检出 head 可达的提交：
 *
 * @see <a href="https://www.dolthub.com/repositories/dolthub/first-hour-db/embed/main?q=SELECT+*%0AFROM+dolt_log%0AWHERE+committer+%3D+%22jennifersp%22+and+date+%3E+%222022-04-01%22%0AORDER+BY+date%3B">示例查询链接</a>
 */
public class DoltLog extends DoltSystemTable {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltLog> INSTANCES = new ConcurrentHashMap<>();

    protected DoltLog(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltLog getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltLog(connectionManager));
    }

    @Override
    public Params prepare() {
        return new Params(this).from("dolt_log");
    }
}