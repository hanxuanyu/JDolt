package com.hxuanyu.jdolt.core.systemtable;

import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import java.util.concurrent.ConcurrentHashMap;


/**
 * dolt_branches
 *
 * dolt_branches 包含数据库已知的分支信息。
 *
 * 由于分支信息对所有客户端（而不仅仅是你的会话）都是全局的，dolt_branches 系统表是只读的。可以使用
 * DOLT_BRANCH() 存储过程创建或删除分支。
 *
 * 架构
 *
 * <pre>
 * +------------------------+----------+
 * | Field                  | Type     |
 * +------------------------+----------+
 * | name                   | TEXT     |
 * | hash                   | TEXT     |
 * | latest_committer       | TEXT     |
 * | latest_committer_email | TEXT     |
 * | latest_commit_date     | DATETIME |
 * | latest_commit_message  | TEXT     |
 * | remote                 | TEXT     |
 * | branch                 | TEXT     |
 * | dirty                  | BOOLEAN  |
 * +------------------------+----------+
 * </pre>
 *
 * 查询示例
 *
 * 获取所有分支。
 *
 * remote 和 branch 显示每个分支所跟踪的远程主机和分支。
 *
 * 当分支上存在未提交的更改时，dirty 为 TRUE。
 *
 * 要查找当前活动分支，请使用 {@code select active_branch()}。
 *
 * dolt_branches 仅包含关于本地分支的信息。对于你已获取的远程上的分支，请参见 dolt_remote_branches。
 */
public class DoltBranches extends DoltSystemTable {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltBranches> INSTANCES = new ConcurrentHashMap<>();

    protected DoltBranches(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltBranches getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltBranches(connectionManager));
    }

    @Override
    public DoltSystemTable.Params prepare() {
        return new DoltSystemTable.Params(this).from("dolt_branches");
    }
}