package com.hxuanyu.jdolt.core.procedure;

import com.hxuanyu.jdolt.connection.DoltConnectionManager;
import com.hxuanyu.jdolt.constant.DoltSqlTemplate;
import com.hxuanyu.jdolt.core.DoltProcedure;
import com.hxuanyu.jdolt.core.DoltRepository;
import com.hxuanyu.jdolt.util.BranchNameValidator;

/**
 * `DOLT_CHECKOUT()` 存储过程允许将当前会话切换到其他分支，或者将指定的表恢复到当前 HEAD 的内容。
 *
 * <p>注意：与 Git 命令行的行为不同，如果您有已修改的工作集，在执行 `DOLT_CHECKOUT()` 后，这些更改仍然保留在您修改过的分支上。
 * 未提交的工作集更改不会像命令行中那样转移到切换到的分支上。这种行为在 SQL 环境中被修改，
 * 以避免在多租户 SQL 环境中多个用户同时连接到同一分支时产生过多干扰。
 * 如果将其他分支的更改带入当前分支，可能会对环境造成破坏。</p>
 *
 * <p>使用示例：</p>
 * <pre>
 * {@code
 * CALL DOLT_CHECKOUT('-b', 'my-new-branch'); // 创建并切换到一个新分支
 * CALL DOLT_CHECKOUT('my-existing-branch');  // 切换到一个已存在的分支
 * CALL DOLT_CHECKOUT('my-table');           // 将指定表恢复到当前 HEAD
 * }
 * </pre>
 *
 * <h3>行为注意事项</h3>
 * <p>使用带分支参数的 `DOLT_CHECKOUT()` 会对会话状态产生两个副作用：</p>
 * <ol>
 *   <li>会话的当前数据库（通过 `SELECT DATABASE()` 返回）变为未加限定的数据库名称。</li>
 *   <li>在会话的剩余时间内，对该数据库未加限定名称的引用将解析为被检出的分支。</li>
 * </ol>
 *
 * <p>示例：</p>
 * <pre>
 * {@code
 * SET autocommit = ON;
 * USE mydb/branch1;               // 当前数据库现在是 `mydb/branch1`
 * INSERT INTO t1 VALUES (1);      // 修改 `branch1` 分支
 * CALL DOLT_CHECKOUT('branch2');  // 当前数据库现在是 `mydb`
 * INSERT INTO t1 VALUES (2);      // 修改 `branch2` 分支
 * USE mydb/branch3;               // 当前数据库现在是 `mydb/branch3`
 * INSERT INTO mydb.t1 VALUES (3); // 修改 `branch2` 分支
 * }
 * </pre>
 *
 * <h3>选项</h3>
 * <ul>
 *   <li><b>-b</b>：创建一个具有指定名称的新分支。</li>
 *   <li><b>-B</b>：类似于 `-b`，但如果分支已存在，则会移动分支。</li>
 *   <li><b>-t</b>：创建新分支时，设置“上游”配置。</li>
 * </ul>
 *
 * @author hanxuanyu
 * @version 1.0
 */
public class DoltCheckout extends DoltRepository implements DoltProcedure {

    private static volatile DoltCheckout instance;

    protected DoltCheckout(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltCheckout instance(DoltConnectionManager connectionManager) {
        if (instance == null) {
            synchronized (DoltBranch.class) {
                if (instance == null) {
                    instance = new DoltCheckout(connectionManager);
                }
            }
        }
        return instance;
    }

    public boolean createAndSwitch(String newBranchName) {
        return call("-b", newBranchName).isSuccess();
    }

    public boolean createAndSwitchForced(String newBranchName) {
        return call("-B", newBranchName).isSuccess();
    }

    public boolean traceBranch(String newBranchName) {
        return call("-t", newBranchName).isSuccess();
    }


    @Override
    public String buildSql(String... params) {
        return DoltSqlTemplate.buildCheckoutSql(params);
    }
}
