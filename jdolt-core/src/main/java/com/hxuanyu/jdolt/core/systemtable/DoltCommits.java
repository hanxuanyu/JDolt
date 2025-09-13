package com.hxuanyu.jdolt.core.systemtable;


import com.hxuanyu.jdolt.manager.DoltConnectionManager;

import java.util.concurrent.ConcurrentHashMap;
/**
 * dolt_commits
 *
 * dolt_commits 系统表显示 Dolt 数据库中的所有（ALL）提交。
 *
 * 这与 dolt_log 系统表 和 dolt log CLI 命令 类似但不同。dolt log 显示的是从当前已检出分支的 HEAD 可达的所有提交祖先的历史，而 dolt_commits 则显示整个数据库中的所有提交，无论当前检出的是哪个分支。
 *
 * 架构（Schema）
 *
 * <pre>
 * {@code
 * > describe dolt_commits;
 * +-------------+----------+------+-----+---------+-------+
 * | Field       | Type     | Null | Key | Default | Extra |
 * +-------------+----------+------+-----+---------+-------+
 * | commit_hash | text     | NO   | PRI |         |       |
 * | committer   | text     | NO   |     |         |       |
 * | email       | text     | NO   |     |         |       |
 * | date        | datetime | NO   |     |         |       |
 * | message     | text     | NO   |     |         |       |
 * +-------------+----------+------+-----+---------+-------+
 * }
 * </pre>
 */
public class DoltCommits extends DoltSystemTable {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltCommits> INSTANCES = new ConcurrentHashMap<>();

    protected DoltCommits(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltCommits getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltCommits(connectionManager));
    }

    @Override
    public Params prepare() {
        return new Params(this).from("dolt_commits");
    }
}