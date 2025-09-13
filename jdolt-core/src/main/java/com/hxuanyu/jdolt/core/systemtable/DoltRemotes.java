package com.hxuanyu.jdolt.core.systemtable;


import com.hxuanyu.jdolt.manager.DoltConnectionManager;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ## {@code dolt_remotes}
 *
 * {@code dolt_remotes} 返回 {@code repo_state.json} 中 remotes 的内容，类似于在命令行运行 {@code dolt remote -v}。
 *
 * {@code dolt_remotes} 表当前为只读。请使用 CLI 的 {@code dolt remote} 功能或
 * {@link dolt_remote() 过程来添加、更新或删除 remotes}。
 *
 * <h3>模式（Schema）</h3>
 *
 * <pre>{@code
 * +-------------+------+------+-----+---------+-------+
 * | Field       | Type | Null | Key | Default | Extra |
 * +-------------+------+------+-----+---------+-------+
 * | name        | text | NO   | PRI |         |       |
 * | url         | text | NO   |     |         |       |
 * | fetch_specs | json | YES  |     |         |       |
 * | params      | json | YES  |     |         |       |
 * +-------------+------+------+-----+---------+-------+
 * }</pre>
 *
 * <h3>示例查询</h3>
 *
 * <pre>{@code
 * SELECT *
 * FROM dolt_remotes
 * WHERE name = 'origin';
 * }</pre>
 *
 * <pre>{@code
 * +--------+-----------------------------------------+--------------------------------------+--------+
 * | name   | url                                     | fetch_specs                          | params |
 * +--------+-----------------------------------------+--------------------------------------+--------+
 * | origin | file:///go/github.com/dolthub/dolt/rem1 | [refs/heads/*:refs/remotes/origin/*] | map[]  |
 * +--------+-----------------------------------------+--------------------------------------+--------+
 * }</pre>
 */
public class DoltRemotes extends DoltSystemTable {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltRemotes> INSTANCES = new ConcurrentHashMap<>();

    protected DoltRemotes(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltRemotes getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltRemotes(connectionManager));
    }

    @Override
    public Params prepare() {
        return new Params(this).from("dolt_remotes");
    }
}