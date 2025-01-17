package com.hxuanyu.jdolt.core.procedure;

import com.hxuanyu.jdolt.connection.DoltConnectionManager;
import com.hxuanyu.jdolt.constant.DoltSqlTemplate;
import com.hxuanyu.jdolt.core.DoltProcedure;
import com.hxuanyu.jdolt.core.DoltRepository;
import com.hxuanyu.jdolt.util.BranchNameValidator;

/**
 * DOLT_BRANCH()
 * <p>
 * 创建、删除和重命名分支。
 * <p>
 * 要列出分支，请使用 {@link dolt-system-tables.md#dolt_branches DOLT_BRANCHES 系统表}，而不是 DOLT_BRANCH() 存储过程。
 * <p>
 * 要查看当前分支，请使用 {@link dolt-sysvars.md#dbname_head_ref @@<dbname>_head_ref 系统变量}，
 * 或使用 `active_branch()` SQL 函数，如下方示例部分所示。
 * <p>
 * 警告：在多会话服务器环境中，如果某个分支正在其他会话中使用，Dolt将阻止您删除或重命名该分支。
 * 您可以通过传递 `--force` 选项强制重命名或删除，但需要注意的是，其他会话中正在使用该分支的客户端将无法再执行语句，并需要结束会话后重新连接。
 * <p>
 * 示例：
 * <pre>{@code
 * -- 从当前HEAD创建一个新分支
 * CALL DOLT_BRANCH('myNewBranch');
 *
 * -- 从feature1分支的起点创建一个新分支
 * CALL DOLT_BRANCH('myNewBranch', 'feature1');
 *
 * -- 通过复制现有分支创建一个新分支
 * -- 如果feature1分支已存在，将会失败
 * CALL DOLT_BRANCH('-c', 'main', 'feature1');
 *
 * -- 通过复制现有分支创建或替换分支
 * -- '-f'选项强制复制，即使feature1分支已存在
 * CALL DOLT_BRANCH('-c', '-f', 'main', 'feature1');
 *
 * -- 删除分支
 * CALL DOLT_BRANCH('-d', 'branchToDelete');
 *
 * -- 重命名分支
 * CALL DOLT_BRANCH('-m', 'currentBranchName', 'newBranchName');
 * }</pre>
 * <p>
 * 注意事项：
 * 分支名称有一些限制，与Git对分支名称的约束类似。Dolt的分支名称限制更严格，要求必须使用
 * {@link https://en.wikipedia.org/wiki/ASCII ASCII} 字符。规则如下：
 * <ul>
 * <li>所有字符必须是ASCII（7位）</li>
 * <li>不得以 '.'（句点）开头</li>
 * <li>不得包含 '..'（两个句点）</li>
 * <li>不得包含 '@{'</li>
 * <li>不得包含ASCII控制字符</li>
 * <li>不得包含以下字符：':', '?', '[', '\\', '^', '~', '*'</li>
 * <li>不得包含空格（空格、制表符、换行符）</li>
 * <li>不得以 '/' 结尾</li>
 * <li>不得以 '.lock' 结尾</li>
 * <li>不得为 HEAD（不区分大小写）</li>
 * <li>不得与提交哈希值无法区分（32个字符，且所有字符为0-9或a-z，区分大小写）</li>
 * </ul>
 * <p>
 * dolt_branch() 过程会隐式提交当前事务并开始一个新事务。
 * <p>
 * 选项：
 * <ul>
 * <li><b>-c, --copy</b>：复制分支。必须跟随要复制的源分支名称以及要创建的新分支名称。
 * 如果没有 `--force` 选项，当新分支已存在时复制将失败。</li>
 * <li><b>-m, --move</b>：移动/重命名分支。必须跟随现有分支的当前名称和该分支的新名称。
 * 如果没有 `--force` 选项，当分支正在其他服务器会话中使用时，重命名将失败。
 * 需要注意，强制重命名或删除正在其他会话中使用的分支将要求这些会话断开连接并重新连接后才能再次执行语句。</li>
 * <li><b>-d, --delete</b>：删除分支。必须跟随现有分支的名称。
 * 如果没有 `--force` 选项，当分支正在其他服务器会话中使用时，删除将失败。
 * 需要注意，强制重命名或删除正在其他会话中使用的分支将要求这些会话断开连接并重新连接后才能再次执行语句。</li>
 * <li><b>-f, --force</b>：与 `--copy` 选项一起使用时，允许从另一个分支重新创建分支，即使该分支已存在。
 * 与 `--move` 或 `--delete` 选项一起使用时，强制允许您重命名或删除正在其他活动服务器会话中使用的分支，
 * 但需要注意，这将要求其他会话断开连接并重新连接后才能再次执行语句。</li>
 * <li><b>-D</b>：`--delete --force` 的快捷方式。</li>
 * </ul>
 *
 * @author hanxuanyu
 * @version 1.0
 */
public class DoltBranch extends DoltRepository implements DoltProcedure {

    private static volatile DoltBranch instance;

    protected DoltBranch(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltBranch instance(DoltConnectionManager connectionManager) {
        if (instance == null) {
            synchronized (DoltBranch.class) {
                if (instance == null) {
                    instance = new DoltBranch(connectionManager);
                }
            }
        }
        return instance;
    }


    public boolean newBranch(String branchName) {
        checkBranchName(branchName);
        return call(branchName);
    }

    public boolean newBranch(String branchName, String parentBranch) {
        checkBranchName(branchName, parentBranch);
        return call(branchName, parentBranch);
    }

    public boolean copyBranch(String branchName, String originBranch) {
        checkBranchName(branchName, originBranch);
        return call("-c", branchName, originBranch);
    }

    public boolean copyBranchForced(String branchName, String originBranch) {
        checkBranchName(branchName, originBranch);
        return call("-c", "-f", branchName, originBranch);
    }

    public boolean moveBranch(String branchName, String newBranch) {
        checkBranchName(branchName, newBranch);
        return call("-m", branchName, newBranch);
    }

    public boolean moveBranchForced(String branchName, String newBranch) {
        checkBranchName(branchName, newBranch);
        return call("-m", "-f", branchName, newBranch);
    }

    public boolean renameBranch(String branchName, String newBranch) {
        checkBranchName(branchName, newBranch);
        return call("-m", branchName, newBranch);
    }

    public boolean renameBranchForced(String branchName, String newBranch) {
        checkBranchName(branchName, newBranch);
        return call("-m", "-f", branchName, newBranch);
    }

    public boolean deleteBranch(String branchName) {
        checkBranchName(branchName);
        return call("-d", branchName);
    }

    public boolean deleteBranchForced(String branchName) {
        checkBranchName(branchName);
        return call("-d", "-f", branchName);
    }


    @Override
    public String buildSql(String... params) {
        return DoltSqlTemplate.buildBranchSql(params);
    }
}
